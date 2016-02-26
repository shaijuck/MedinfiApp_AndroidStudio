package com.medinfi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.medinfi.SplashActivity.GetAutoDetectedCityId;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;
import com.mobileapptracker.MobileAppTracker;

public class AutoDetection extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    // declare xml views
   
    private ProgressBar progressBar;
    // initialize gpstracker

    private EasyTracker easyTracker = null;
    /*
     * double latitude; double longitude; String location;
     */
    static AutoDetection splashActivity;
    public static boolean isEventPressed = false;
    private boolean isHomePressed = false;

    MobileAppTracker mobileAppTracker = null;
    private String[] testerName = { "AN", "SH", "RA", "RG", "LB" };
    private ArrayList<String> cityValidationList;

    // GoogleApiClient
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 500; 
    private static int FATEST_INTERVAL = 500; 
    private static int DISPLACEMENT = 0; 
    private static int attempts=0;
    private static String sub_loc="";
    private static String loc="";
    private static String fullAddress="";
    private boolean isDefaultCalled=false;
    private boolean isFetchComplete=false;
    
    Timer timer;
    TimerTask timerTask;
    // we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();
    CountDownTimer cutOffTimer;
    private static long CUTOFF_TIMER = 5000; 
    private static long CUTOFF_TIMER_INT = 5000; 
   // private Boolean CUTOFF_TIMER_FLAG = false; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.splashscreen);

	/*
	 * Initializing MAT - Tune
	 */
	mobileAppTracker = MobileAppTracker.init(getApplicationContext(), "184144", "69fffbccc3a47c4df157ea828b1355e2");
	mobileAppTracker.setAndroidId(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
	String deviceId = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	mobileAppTracker.setDeviceId(deviceId);
	
	ApplicationSettings.putPref(AppConstants.TOKEN, Utils.createMD5AsToken(AppConstants.MD5_AS_TOKEN));
	// new CurrentSessionId().execute();
	easyTracker = EasyTracker.getInstance(AutoDetection.this);
	splashActivity = this;
	initialization();
	 ((LinearLayout)findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	// First we need to check availability of play services
	if (Utils.isNetworkAvailable(this)) {
	   
	    if (checkPlayServices()) {
	    	
		buildGoogleApiClient();
		
		createLocationRequest();
		
//		cutOffTimer=new CountDownTimer(CUTOFF_TIMER, CUTOFF_TIMER_INT) {
//
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//            	Toast.makeText(AutoDetection.this, getString(R.string.locationError), Toast.LENGTH_LONG).show();
//            }
//        }.start();
	    }
	} else {
	    Toast.makeText(this, getString(R.string.netWorkError), Toast.LENGTH_LONG).show();
           // ((LinearLayout)findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	}

	isEventPressed = false;
	isHomePressed = false;
	Utils.isGPRSON = false;

	// For performance testing
	try {
	    String[] words = Utils.readFileFromSdCard().split(",");
	    if (words.length > 1) {
		for (int i = 0; i < testerName.length; i++) {
		    if (words[1].contains(testerName[i])) {
			AppConstants.LogLevel = Integer.parseInt(words[0]);
			AppConstants.perfTestCode = words[1];
			// System.out.println("LogLevel  "+AppConstants.LogLevel+" perfTestCode "+AppConstants.perfTestCode);
			break;
		    }
		}

	    } else {
		AppConstants.LogLevel = 0;
	    }

	} catch (NumberFormatException e) {
	    AppConstants.LogLevel = 0;
	} catch (Exception e) {
	    AppConstants.LogLevel = 0;
	}
	
    }

    private void initialization() {
	try {
	    cityValidationList = new ArrayList<String>();
	    //new GetCityListValidation().execute();
	    progressBar = (ProgressBar) findViewById(R.id.progressBar);
	    progressBar.setVisibility(View.VISIBLE);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
    	
	mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
		.addApi(LocationServices.API).build();
	
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
	mLocationRequest = new LocationRequest();
	mLocationRequest.setInterval(UPDATE_INTERVAL);
	mLocationRequest.setFastestInterval(FATEST_INTERVAL);
	mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	if (resultCode != ConnectionResult.SUCCESS) {
	    if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
		GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
	    } else {
		Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
		finish();
	    }
	    return false;
	}
	return true;
    }

    /* back button called */
 	public boolean onKeyDown(int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
 		Log.i("Back","Back Back pressed");
					if (keyCode == KeyEvent.KEYCODE_BACK) {
					    try {
					    	Log.i("Back","Back Back pressed");
						finishRuningActivity();
						moveTaskToBack(true);
					    } catch (Exception e) {
						e.printStackTrace();
					    }
					}
					
				    
		return super.onKeyDown(keyCode, event);
	}
    
    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

	try {
		
	    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	    
	} catch (Exception e) {
		
	    e.printStackTrace();
	}

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
	LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onResume() {
	super.onResume();
	/*
	 * MAT changes
	 */
	mobileAppTracker.setReferralSources(this);
	mobileAppTracker.measureSession();
	progressBar.setVisibility(View.VISIBLE);
	
	if (Utils.isNetworkAvailable(this)) {
		
	    checkPlayServices();
	    startLocationUpdates();
	   
	} else {
	    startTimer();
	}
	
	/*
	 * if (Utils.isGPRSON) { dialog.setVisibility(View.VISIBLE); new
	 * GPSTracker(SplashActivity.this); if (Utils.isGPRSON) { final
	 * GPSTracker gpsTrakerInnner = new GPSTracker(SplashActivity.this);
	 * runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() {
	 * 
	 * Thread thread = new Thread(new Runnable() {
	 * 
	 * @Override public void run() { while (isLatLongPresent) { try {
	 * 
	 * latitude = gpsTrakerInnner.getLatitude(); longitude =
	 * gpsTrakerInnner.getLongitude(); if (latitude != 0.0 && longitude !=
	 * 0.0) { //
	 * System.out.println("SplashActivity.onResume() isLatLongPresent ");
	 * isLatLongPresent = false; new LocationGeoCoderAPITask().execute(); }
	 * else { dialog.setVisibility(View.VISIBLE); //
	 * System.out.println("SplashActivity.onResume() isLatLongPresent else "
	 * ); } Thread.sleep(1000);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } } } });
	 * thread.start();
	 * 
	 * } }); } }
	 */
	if (!isEventPressed) {
	    if (isHomePressed) {
		easyTracker.send(MapBuilder.createEvent("Auto Detection", "Home Button", "Home", null).build());
	    }
	    if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		}
	}
    }

     public void startTimer() {
   	timer = new Timer();
   	initializeTimerTask();
   	timer.schedule(timerTask, 0, 60000); 
       }

       public void stoptimertask() {
   	try {
   	    if (timer != null) {
   	        timer.cancel();
   	        timer = null;
   	    }
   	} catch (Exception e) {
   	    e.printStackTrace();
   	}
       }

       public void initializeTimerTask() {

	timerTask = new TimerTask() {
	    public void run() {
		// use a handler to run a toast that shows the current timestamp
		handler.post(new Runnable() {
		    public void run() {
			Toast.makeText(AutoDetection.this, getString(R.string.netWorkError), Toast.LENGTH_LONG).show();
		    }
		});
	    }
	};
       }
    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    @Override
    public void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("AutoDetection Activity");
	// crash();//for crash analytics testing
	try {
		
	    if (mGoogleApiClient != null) {
	    	
	        mGoogleApiClient.connect();
	    }
	    else{
	    	
	    }
	} catch (Exception e) {
		
	    e.printStackTrace();
	}
	EasyTracker.getInstance(this).activityStart(this);
    }
    
    void crash()
    {
    	try{
    	String[] title = {
    	        "test 1",
    	        "test 2" };
    	
    	String name=title[2];
    	}
    	catch(Exception e){
    		FlurryAgent.onError("Crash 1", e.getMessage(), e.getClass().getName()) ;
    	}
    	try{
    	int a=1/0;
    	}
    	catch(Exception e){
    		FlurryAgent.onError("Crash 2", e.getMessage(), e.getClass().getName()) ;
    	}
    }

    @Override
    public void onStop() {
	super.onStop();
	Log.i("Back","Back Stopping Auto");
	  FlurryAgent.onEndSession(this);
	isHomePressed = true;
	stoptimertask();
	try {
	    if (mGoogleApiClient.isConnected()) {
	        mGoogleApiClient.disconnect();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onPause() {
	super.onPause();
	try {
	    stopLocationUpdates();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    class CurrentSessionId extends AsyncTask<Void, Void, String> {

	@Override
	protected String doInBackground(Void... params) {
	    JSONParser jparser = new JSONParser();
	    String result = jparser.getCurrentSessionId(ApplicationSettings.getPref(AppConstants.USER_ID, ""));
	    return result;
	}

	@Override
	protected void onPostExecute(String result) {
	    super.onPostExecute(result);
	    if (result != null) {
		try {
		    JSONObject jobj = new JSONObject(result);
		    if (jobj != null) {
			String message = jobj.getString("token");
			ApplicationSettings.putPref(AppConstants.TOKEN, message);
		    }

		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }
	}
    }

    public static AutoDetection getInstance() {
	return splashActivity;
    }


private String validateCity(String detectedCity) {	
		
		JSONParser jparser = new JSONParser();
	    String result = jparser.getCityAfterValidation(detectedCity);
	    String cityName="";
	    
	    if (result != null) {
			try {
			     JSONObject jsonObject = new JSONObject(result.toString());
			    
			     cityName=jsonObject.getString("ActualCity");

			    
			}
			catch (JSONException e) {
			}

		}
	    
	    
		return cityName;
	}

    /* API Call for Locality */

    final class GetAutoDetectedCityId extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();

	}

	@Override
	protected String doInBackground(Void... arg0) {
	    JSONParser jparser = new JSONParser();
	    String result = jparser.getAutoDetectedCityId(ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, ""));
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	    if (result != null) {
		try {
		    JSONObject jobj = new JSONObject(result);
		    if (jobj != null) {
			String message = jobj.getString("status");
			if (message.equalsIgnoreCase("Success")) {
			    ApplicationSettings.putPref(AppConstants.CITY_SEARCH_ID, jobj.getString("cityid"));
			} else {
			    ApplicationSettings.putPref(AppConstants.CITY_SEARCH_ID, "CityIDNotFound");
			}
		    }

		} catch (JSONException e) {
		    ApplicationSettings.putPref(AppConstants.CITY_SEARCH_ID, "CityIDNotFound");
		}

	    } else {
		ApplicationSettings.putPref(AppConstants.CITY_SEARCH_ID, "CityIDNotFound");
	    }
	}

    }

    private void finishRuningActivity() {
	try {Log.i("Back","Back FinishRunning");
	    if (HomeScreen.getInstance() != null) {
	    	Log.i("Back","Back Home");
		HomeScreen.getInstance().finish();
	    }
	    if (LocationUpdateActivity.getInstance() != null) {
	    	Log.i("Back","Back LocUp");
		LocationUpdateActivity.getInstance().finish();
	    }
	    if (AutoDetection.getInstance() != null) {
	    	Log.i("Back","Back Auto");
		AutoDetection.getInstance().finish();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    final class GetCityListValidation extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();

	}

	@Override
	protected String doInBackground(Void... arg0) {
	    JSONParser jparser = new JSONParser();
	    String result = jparser.getCityListValidation();
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	    if (result != null) {
		try {
		    cityValidationList.clear();
		    JSONObject jsonObject = new JSONObject(result.toString());

		    JSONArray jsonarray = new JSONArray(jsonObject.getString("AppCities"));
		    for (int i = 0; i < jsonarray.length(); i++) {
			cityValidationList.add(jsonarray.getString(i));
		    }
		} catch (JSONException e) {
		}

	    } else {
	    }
	}

    }
    public String getCurrentAddressIfGeoCoderFails(double latitute, double longitute) {
    	String responseBody = "";
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitute + ","
    		+ longitute + "&key="+AppConstants.GOOGLE_API_KEY);
    	
    	
    	try {
    	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	    ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	    responseBody = httpclient.execute(httppost, responseHandler);
    	    //System.out.println("ADD "+responseBody);
    	    

    	} catch (ConnectTimeoutException e) {
    	    e.getMessage();
    	} catch (Exception e) {
    	    e.getMessage();
    	}
    	return responseBody;

        }
  /*  private String parserCurrentAddressIfGeoCoderFails(String result) {
    	//ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION, addresses.get(0).getLocality().toString());
	    //ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY,
		  //  addresses.get(0).getAddressLine(1).substring(addresses.get(0).getAddressLine(1).lastIndexOf(",") + 1).trim().toString());
	    //ApplicationSettings.putPref(AppConstants.GPRS_STATUS, "1");
	    //ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, addresses.get(0).getLocality().toString());
	    //ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY_AUTO, addresses.get(0).getSubLocality().toString());
	    //ApplicationSettings.putPref(AppConstants.LOCALITY_ADDRESS, addressLine.replace("null,", "").replace(",null", ""));
    	String fullAddress="";
    	try {
    		
    	    if (result.length() > 0) {
    		try {
    		    JSONObject jsonObject = new JSONObject(result.toString());
    		    JSONArray jsonarray = new JSONArray(jsonObject.getString("results"));

    		    for (int i = 0; i < jsonarray.length(); i++) {
    			JSONObject obj = jsonarray.getJSONObject(i);
    			String long_name = null;
    			JSONArray address_components = new JSONArray(obj.getString("address_components"));
    			for (int j = 0; j < address_components.length(); j++) {
    			    JSONObject objaddress_components = address_components.getJSONObject(j);
    			    long_name = objaddress_components.getString("long_name");
    			    //String short_name = objaddress_components.getString("short_name");
    			    String typeResult = objaddress_components.getString("types");
    			    String types = typeResult.replace("[", " ").replace("]", " ").replace("\"", " ");
    			    String[] types3 = types.split(",");
    			
    			    for (int k = 0; k < types3.length; k++) {
    				if (types3[k].trim().equalsIgnoreCase("sublocality_level_1")) {
    				    ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY,long_name);
    				    ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY_AUTO, long_name);
    					Log.i("Address","Add : sublocality_level_1 : "+long_name);
    				    //strRresult.append("sublocality_level_2 : "+long_name + "  ");

    				} else if (types3[k].trim().equalsIgnoreCase("locality")) {
    				     ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION,long_name);
    				     ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, long_name);
    					Log.i("Address","Add : locality : "+long_name);
    				    //strRresult.append("locality : "+long_name + "  ");
    				}else if (types3[k].trim().equalsIgnoreCase("sublocality_level_2")) {
    					ApplicationSettings.putPref(AppConstants.GPRS_STATUS, "1");
    					ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION,long_name);
   				     	ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, long_name);
    					Log.i("Address","Add : sublocality_level_2 : "+long_name );
    				   // strRresult.append("sublocality_level_1 : "+long_name + "  ");
    				}

    			    }
    			   
    			}
    			
    			fullAddress = obj.getString("formatted_address");
    			ApplicationSettings.putPref(AppConstants.LOCALITY_ADDRESS,fullAddress);
    			Log.i("Address","Add : formatted_address : "+fullAddress);
    			//strRresult.append("formatted_address : "+fullAddress+" \n ");
    			
    			if (fullAddress != null) {
    			    break;
    			}

    		    }
    		} catch (JSONException e) {
    		    e.printStackTrace();
    		}
    	    } else {
    	    	return "";
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	return fullAddress;
        }*/
 private String parserCurrentAddressIfGeoCoderFails(String result) {
    	
    	fullAddress="";
    	try {
    		
    	    if (result.length() > 0) {
    		try {
    		    JSONObject jsonObject = new JSONObject(result.toString());
    		    JSONArray jsonarray = new JSONArray(jsonObject.getString("results"));

    		    for (int i = 0; i < jsonarray.length(); i++) {
    			JSONObject obj = jsonarray.getJSONObject(i);
    			String long_name = null;
    			JSONArray address_components = new JSONArray(obj.getString("address_components"));
    			for (int j = 0; j < address_components.length(); j++) {
    			    JSONObject objaddress_components = address_components.getJSONObject(j);
    			    long_name = objaddress_components.getString("long_name");
    			    //String short_name = objaddress_components.getString("short_name");
    			    String typeResult = objaddress_components.getString("types");
    			    String types = typeResult.replace("[", " ").replace("]", " ").replace("\"", " ");
    			    String[] types3 = types.split(",");
    			
    			    for (int k = 0; k < types3.length; k++) {
    				if (types3[k].trim().equalsIgnoreCase("sublocality_level_1")) {
    					sub_loc=long_name;
    				   // ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY,long_name);
    				   // ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY_AUTO, long_name);
    					//Log.i("Address","AddSplash : sublocality_level_1 : "+long_name);
    				    //strRresult.append("sublocality_level_2 : "+long_name + "  ");

    				} else if (types3[k].trim().equalsIgnoreCase("locality")) {
    					loc=long_name;
    				    // ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION,long_name);
    				    // ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, long_name);
    					//Log.i("Address","AddAplash : locality : "+long_name);
    				    //strRresult.append("locality : "+long_name + "  ");
    				}else if (types3[k].trim().equalsIgnoreCase("sublocality_level_2")) {
    					loc=long_name;
    					//ApplicationSettings.putPref(AppConstants.GPRS_STATUS, "1");
    					//ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION,long_name);
   				     	//ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, long_name);
    					//Log.i("Address","AddSplash : sublocality_level_2 : "+long_name );
    				   // strRresult.append("sublocality_level_1 : "+long_name + "  ");
    				}

    			    }
    			   
    			}
    			
    			fullAddress = obj.getString("formatted_address");
    			//fullAddress=fullAddress+"#"+loc+"#"+sub_loc;
    			//ApplicationSettings.putPref(AppConstants.LOCALITY_ADDRESS,fullAddress);
    			//Log.i("Address","AddSplash : formatted_address : "+fullAddress);
    			//strRresult.append("formatted_address : "+fullAddress+" \n ");
    			
    			if (fullAddress != null) {
    			    break;
    			}

    		    }
    		} catch (JSONException e) {
    		    e.printStackTrace();
    		}
    	    } else {
    	    	return "";
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	
    	return fullAddress;
        }
    public String getAddressLine(double lat, double lon) {
	String addressLine = "";
	
	//List<Address> addresses = getGeocoderAddress(lat, lon);
	if(attempts<=5){
		
	String result = getCurrentAddressIfGeoCoderFails(lat, lon);
	
	addressLine=parserCurrentAddressIfGeoCoderFails(result);
	
	}
	if (addressLine != "") {
	    /*try {
		try {
		    addressLine = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1) + ","
			    + addresses.get(0).getAddressLine(2) + "," + addresses.get(0).getAddressLine(3);
		} catch (Exception e) {
		    addressLine = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1) + ","
			    + addresses.get(0).getAddressLine(2);
		}
		
		  /*System.out.println("getAddressLine(0) "+addresses.get(0).getAddressLine(0));
		  System.out.println("getAddressLine(1) "+addresses.get(0).getAddressLine(1));
		  System.out.println("getAddressLine(2) "+addresses.get(0).getAddressLine(2));
		  System.out.println("getAddressLine(3) "+addresses.get(0).getAddressLine(3));
		  System.out.println("Locality "+addresses.get(0).getLocality().toString());
		  System.out.println("addressLine "+addressLine);
		 
		if (addresses.get(0).getSubLocality() != null && addresses.get(0).getLocality() != null) {
		    ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION, addresses.get(0).getLocality().toString());
		    ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY,
			    addresses.get(0).getAddressLine(1).substring(addresses.get(0).getAddressLine(1).lastIndexOf(",") + 1).trim().toString());
		    ApplicationSettings.putPref(AppConstants.GPRS_STATUS, "1");
		    ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, addresses.get(0).getLocality().toString());
		    ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY_AUTO, addresses.get(0).getSubLocality().toString());
		    ApplicationSettings.putPref(AppConstants.LOCALITY_ADDRESS, addressLine.replace("null,", "").replace(",null", ""));
		    // System.out.println("GPSTracker.getAddressLine() addressLine "+addressLine);
		}
		// for testing
		StringBuffer strRresult = new StringBuffer();
		 strRresult.append("latitude : "+lat+" , longitude : "+lon+"\naddressLine 0 : "+addresses.get(0).getAddressLine(0) + "\naddressLine 1 : "+addresses.get(0).getAddressLine(1)+""
			    	+ " \naddressLine 2 : "+addresses.get(0).getAddressLine(2)+"\naddressLine 3 : "+addresses.get(0).getAddressLine(3)
			    	+ "\nSub Locality : "+addresses.get(0).getSubLocality()+"\n"
		                + "\nLocality : "+addresses.get(0).getLocality()+"\n");
			    strRresult.append("full Address : "+addressLine.replace("null,", "").replace(",null", "")+"\n\n");
		Utils.writeFileToSdCard("MedinfAddress",strRresult.toString());	   			    
	    } catch (Exception e) {
		e.printStackTrace();
	    }*/
		attempts=0;
	    return addressLine;
	} else {
		attempts++;
	    return null;
	}
    }

    List<Address> getGeocoderAddress(double latitude, double longitude) {
	// if (location != null) {
	Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
	try {
	    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
	    return addresses;
	} catch (IOException e) {
	    return null;
	}
	// }
	// return null;
    }
    
    private void setDefault() {
    	isDefaultCalled=true;
    	if(!isFetchComplete){
    	ApplicationSettings.putPref(AppConstants.CITY_SEARCH_ID,"1");
    	ApplicationSettings.putPref(AppConstants.LATITUDE,"12.933221");
	    ApplicationSettings.putPref(AppConstants.LONGITUDE, "77.632195");
	    ApplicationSettings.putPref(AppConstants.LOCALITY_ADDRESS,"155, 2nd Main Road, S.T. Bed, Cauvery Colony, Koramangala, Bengaluru, Karnataka 560095, India");
	    ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION, "Bengaluru");
	    ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY,"Koramangala");
	    
//	    startActivity(new Intent(AutoDetection.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//		AutoDetection.this.finish();
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
	    if (HospitalsActivity.getInstance() != null) {
		HospitalsActivity.getInstance().finish();
	    }
	    //if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	    ApplicationSettings.putPref(AppConstants.MANUAL_DETECT_WORKED,"no");
	    startActivity(new Intent(AutoDetection.this, HospitalsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	    AutoDetection.this.finish();
    }
    }

    /**
     * Create custom dialog
     */
    private void customDialog() {
	final Dialog dialog = new Dialog(AutoDetection.this);
	Typeface tfRegular = Typeface.createFromAsset(getAssets(), "fonts/RobotoRegular.ttf");
	Typeface tfRobotoLight = Typeface.createFromAsset(getAssets(), "fonts/RobotoLight.ttf");
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

	// retrieve display dimensions
	Rect displayRectangle = new Rect();
	Window window = getWindow();
	window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	// inflate and adjust layout
	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.gprs_alert_dialog, null);
	layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
	// layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

	// dialog.setView(layout);
	Utils.isGPRSON = false;
	dialog.setContentView(layout);
	// dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
	// LayoutParams.WRAP_CONTENT);
	dialog.setCancelable(false);
	dialog.show();
	Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
	btnOk.setTypeface(tfRegular);
	((TextView) dialog.findViewById(R.id.textDialog)).setTypeface(tfRegular);
	((TextView) dialog.findViewById(R.id.textDialogMsg)).setTypeface(tfRegular);
	btnOk.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Auto Detection", "Turn On GPS", "Turn On GPS", null).build());
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
		Utils.isGPRSON = false;
		isEventPressed = true;
		dialog.dismiss();

	    }
	});
	Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
	btnCancel.setTypeface(tfRegular);
	btnCancel.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		ApplicationSettings.putPref(AppConstants.LOCATION_SELECTED_MANUAL, true);
		ApplicationSettings.putPref(AppConstants.LOCATION_SELECTED_MANUAL_GPRS, true);
		Intent intent = new Intent(AutoDetection.this, LocationUpdateActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		easyTracker.send(MapBuilder.createEvent("Auto Detection", "Cancel GPS", "Cancel GPS", null).build());
		if (AutoDetection.getInstance() != null) {
		    AutoDetection.getInstance().finish();
		}
		Utils.isGPRSON = false;
		isEventPressed = true;
		dialog.dismiss();
	    }
	});
	dialog.setOnKeyListener(new OnKeyListener() {

	    @Override
	    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		    AutoDetection.getInstance().finish();
		    // dialog.dismiss();
		}
		return true;
	    }
	});
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle arg0) {
	// Once connected with google api, get the location
	System.out.println("SplashActivity.onConnected()");
	startLocationUpdates();
	try {
	    if (!Utils.isGPRSON && isGPRSOff()) {
		customDialog();
	    }
	    else{
	    	cutOffTimer=new CountDownTimer(CUTOFF_TIMER, CUTOFF_TIMER_INT) {

	            public void onTick(long millisUntilFinished) {
	            }

	            public void onFinish() {
	            	Toast.makeText(AutoDetection.this, getString(R.string.locationError), Toast.LENGTH_LONG).show();
	            }
	        }.start();
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	// displayLocation();

	/*
	 * if (mRequestingLocationUpdates) { startLocationUpdates(); }
	 */

    }

    @Override
    public void onConnectionSuspended(int arg0) {
	mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
	mLastLocation = location;
	
	Thread thread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
			
		    displayLocation();
		    
		    Thread.sleep(2000);
		} catch (InterruptedException e) {
			
		    e.printStackTrace();
		}		
	    }
	});
	thread.start();
    }
    // use for start home screen one time only
    boolean isLocationTrue = false; 
    private void displayLocation() {
    	
    if(attempts<=5){
    		
	mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
	
	if (mLastLocation != null) {
		
		
	    double latitude = mLastLocation.getLatitude();
	    double longitude = mLastLocation.getLongitude();
	    
	    if (getAddressLine(latitude, longitude) != null && !isLocationTrue) {
		//if (cityValidationList != null && cityValidationList.size() > 1) {
		    isLocationTrue = true;
		    String city="";
		    
		    if(!(city=validateCity(loc)).equals("")){
		    	cutOffTimer.cancel();
		    	
		    	if(!isDefaultCalled){
		    		isFetchComplete=true;
		    		
		    	ApplicationSettings.putPref(AppConstants.LATITUDE, String.valueOf(latitude));
			    ApplicationSettings.putPref(AppConstants.LONGITUDE, String.valueOf(longitude));
			    ApplicationSettings.putPref(AppConstants.LATITUDE_AUTO, String.valueOf(latitude));
			    ApplicationSettings.putPref(AppConstants.LONGITUDE_AUTO, String.valueOf(longitude));
			    ApplicationSettings.putPref(AppConstants.LOCALITY_ADDRESS,fullAddress);
			    if(!loc.equals(city)){
		   			ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY,loc);
					ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY_AUTO,loc);
					ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION,city);
					ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, city);
			    }
			    else{
			    	ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY,sub_loc);
					ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY_AUTO,sub_loc);
					ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION,loc);
					ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, loc);
			    	
			    }
	   			ApplicationSettings.putPref(AppConstants.GPRS_STATUS, "1");
			    
				new GetAutoDetectedCityId().execute();
				ApplicationSettings.putPref(AppConstants.LOCATION_SELECTED_AUTO, true);
	
				ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
			    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
			
			    if (HospitalsActivity.getInstance() != null) {
				HospitalsActivity.getInstance().finish();
			    }
			    
					
					ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
					
					new CreateSessionIdAsyncTask().execute("0", "");
					
				
			    
			    startActivity(new Intent(AutoDetection.this, HospitalsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			    AutoDetection.this.finish();
		    	}
		    } else {
		    	cutOffTimer.cancel();
		    	setDefault();

		    }
		
	      }
		}
    }
    	else{
    		//Toast.makeText(this, getString(R.string.cannot_get_address), Toast.LENGTH_LONG).show();
    		attempts=0;
    		setDefault();
    	}
    }

    private boolean isGPRSOff() {
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean isResult = false;
	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	if (!isGPSEnabled && !isNetworkEnabled) {
	    isResult = true;
	} else {
	    isResult = false;
	}
	return isResult;
    }
}
