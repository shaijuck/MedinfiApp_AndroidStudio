package com.medinfi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.SpecialistDoctorsActivity.GetGlobalSearchList;
import com.medinfi.adapters.AutoCompleteAdapter;
import com.medinfi.adapters.HospitalsAdapter;
import com.medinfi.datainfo.AutoCompleteInfo;
import com.medinfi.datainfo.HospitalInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.pulltorefresh.library.PullToRefreshBase;
import com.medinfi.pulltorefresh.library.PullToRefreshBase.Mode;
import com.medinfi.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.medinfi.pulltorefresh.library.PullToRefreshListView;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;

public class HospitalsActivity extends Activity {

	
    /* intialize views used in xml */
	boolean doubleBackToExitPressedOnce=false;
	 private ListView globalListView;
    private PullToRefreshListView hospitalListView;
    private HospitalsAdapter hospitalAdapter;
    private ArrayList<HospitalInfo> hospitalList;
    private com.actionbarsherlock.widget.SearchView mSearchView;
    // private TextView nearmeTextView;
    private TextView gpTextView;
    // private TextView topratedTextView;
    private Typeface tf;
    // private View nearbyView;
    // private View topratedView;
    private AutoCompleteTextView searchText;
    private Toast toast = null;
    private ArrayList<AutoCompleteInfo> autocompleteList;
    private AutoCompleteAdapter autoCompleteAdapter;
    private TextView actionbarTitle, actionbarSubtitle;
    public boolean isTrueToast = false;
    private LinearLayout searchLayout;
    private LinearLayout titleLayout;
    private ImageView searchImageView;

    private EasyTracker easyTracker = null;
    private boolean isTrue = false;
    private ProgressDialog pdia;
    private GetHospitalList getHospitalList;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
   // private LinearLayout layoutBack;
    private EditText searchEditText;
    private String strSearchText;
    private boolean isAllDataAvailable = false;
    private static HospitalsActivity hospitalScreen;
    private TextView txtNoResultFound;
    private String preServerTimeSpend = "";
    private String serverTimeSpend = "";
    private String postServerTimepend = "";
    private String totalScreenTimeSpend = "";
    private boolean isPullToRefreshFromEnd = false;
    private ArrayList<AutoCompleteInfo> globalSearchList;
    //private ProgressBar globalprogressBar;
    private GetGlobalSearchList asyncTask;  
	//declare ids from xml
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.hospitals_screen);
	easyTracker = EasyTracker.getInstance(HospitalsActivity.this);
	ApplicationSettings.putPref(AppConstants.LAST_COUNT, "0");
	hospitalScreen = this;
	tf = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	// customActionbar();
	actionbarTitle = (TextView) findViewById(R.id.title);
	actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
//	layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
	searchEditText = (EditText) findViewById(R.id.searchedittext);
	searchEditText.setTypeface(tf);
	
	showLocation();
	// nearbyView = findViewById(R.id.nearme_view);
	// nearbyView.setVisibility(View.VISIBLE);
	// topratedView = findViewById(R.id.toprated_view);
	gpTextView = (TextView) findViewById(R.id.gp_text);
	gpTextView.setTypeface(tf);
	// nearmeTextView = (TextView) findViewById(R.id.nearme_textview);
	// nearmeTextView.setTypeface(tf);
	// topratedTextView = (TextView) findViewById(R.id.toprated_textview);

	// topratedTextView.setTypeface(tf);
	globalSearchList = new ArrayList<AutoCompleteInfo>();
	globalListView = (ListView) findViewById(R.id.globalListView);
	hospitalListView = (PullToRefreshListView)findViewById(R.id.hospitalListView);
	txtNoResultFound = (TextView)findViewById(R.id.txtNoResultFound);
	txtNoResultFound.setVisibility(View.GONE);
	isAllDataAvailable = false;
	isHomePressed = false;
	isEventPressed = false;

	hospitalListView.setMode(Mode.DISABLED);
	hospitalListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

	    @Override
	    public void onRefresh(PullToRefreshBase<ListView> refreshView) {

		if (!isAllDataAvailable) {
		    if (isNetworkAvailable()) {
			hospitalListView.setMode(Mode.PULL_FROM_END);
			isPullToRefreshFromEnd = true;
	//		System.out.println(" Mode.PULL_FROM_END ");
			ApplicationSettings.putPref(AppConstants.OFFSET_PREVIOUS_VALUE, hospitalList.size());
//			hospitalDetailsLoadMore("-1");
			hospitalDetailsLoadALLData(ApplicationSettings.getPref(AppConstants.LAST_COUNT,""+hospitalList.size()));
		    } else {
			hospitalListView.onRefreshComplete();
			hospitalListView.setMode(Mode.DISABLED);
			Toast.makeText(HospitalsActivity.this, "Network not Available", Toast.LENGTH_SHORT).show();
		    }
		} else {

		    hospitalListView.setMode(Mode.DISABLED);
		}

	    }
	});
	// clickTextViews();
	if (AppConstants.LogLevel == 1) {
	    Utils.startPreServerCallTime = System.currentTimeMillis();
	    Utils.startTotalScreenTime = System.currentTimeMillis();
	    Utils.startTotalScreenTime20 = System.currentTimeMillis();
	}
	if (isNetworkAvailable()) {
	    // hospitalDetails(AppConstants.OFFSET_DEFAULT_VALUE);
	    hospitalListView.setVisibility(View.VISIBLE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	    hospitalList = new ArrayList<HospitalInfo>();
	   
	  //checking data in cache if available load the data or call data from server
	    if(!ApplicationSettings.getPref(AppConstants.HOSPITAL_CACHE, "").equals(""))
		{
	    	
		    loadData(ApplicationSettings.getPref(AppConstants.HOSPITAL_CACHE, ""));
		}
	    else
	    {
	    	getHospitalList = new GetHospitalList();
		    getHospitalList.execute();
	    }
	  
	   // hospitalDetailsLoadALLData("-1");
	} else {
	    hospitalListView.setVisibility(View.GONE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tf);
	    ((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tf);
	}
 //	searchHospital(searchEditText);
	globalSearch(searchEditText);
	globalSearchListViewClickEvent();
	clickEvent();
//	backClickEvent();
//	locationClickEvent();
	tabClickEvent();
	if(ApplicationSettings.getPref(AppConstants.MANUAL_DETECT_WORKED, "").equals("no")){
		ApplicationSettings.putPref(AppConstants.MANUAL_DETECT_WORKED,"yes");
	Toast.makeText(this, getString(R.string.cannot_get_address), Toast.LENGTH_LONG).show();
	}
	Utils.startSuggestionActivity(this,txtNoResultFound,"HospitalActivity",easyTracker);
	//the below mentioned code is for enabling the Popup to rate the app
//	if(Integer.parseInt(ApplicationSettings.getPref(AppConstants.APP_VISIT_COUNT,"0"))==2)
//	customDialog();
    }
	private void customDialog() {
		final Dialog dialog = new Dialog(HospitalsActivity.this);
		Typeface tfRegular = Typeface.createFromAsset(getAssets(), "fonts/RobotoRegular.ttf");
		Typeface tfRobotoLight = Typeface.createFromAsset(getAssets(), "fonts/RobotoLight.ttf");
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// retrieve display dimensions
		Rect displayRectangle = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		// inflate and adjust layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.rate_the_app, null);
		layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
		// layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

		// dialog.setView(layout);
		dialog.setContentView(layout);
		// dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT);
		dialog.setCancelable(false);
		dialog.show();
		Button rate = (Button) dialog.findViewById(R.id.rate);
		rate.setTypeface(tfRegular);
		((TextView) dialog.findViewById(R.id.textDialog)).setTypeface(tfRegular);
		((TextView) dialog.findViewById(R.id.textDialogMsg)).setTypeface(tfRegular);
		rate.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    rateTheApp();
			dialog.dismiss();

		    }
		});
		Button noThanks = (Button) dialog.findViewById(R.id.noThanks);
		noThanks.setTypeface(tfRegular);
		noThanks.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
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
	private void rateTheApp() {
		easyTracker.send(MapBuilder.createEvent("Rate the App Prompt", "Rate the App","Rate the App", null).build());
		Uri uri = Uri.parse("market://details?id=" + HospitalsActivity.this.getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
		    startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
		    startActivity(new Intent(Intent.ACTION_VIEW,
			    Uri.parse("http://play.google.com/store/apps/details?id=" + HospitalsActivity.this.getPackageName())));
		}
	    }
    private void backClickEvent() {
//	layoutBack.setOnClickListener(new OnClickListener() {
//
//	    @Override
//	    public void onClick(View v) {
//		easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Back", "Back", null).build());
//		HospitalsActivity.this.finish();
//
//	    }
//	});
    }
    private void locationClickEvent(){
    	RelativeLayout layoutLocation = (RelativeLayout)findViewById(R.id.layoutLocation);
	 layoutLocation.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Update Location", "Update Location", null).build());
		startLocationUpdate(); 
	    }
	});
   }
    private void startLocationUpdate() {
	Intent intent = new Intent(HospitalsActivity.this,LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
    }
	//call hospital API
	public void hospitalDetails(final String offset) {}
	
	
	

    public class GetHospitalList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
		preServerTimeSpend = Utils.preServerTimeSpend();
	    }
	    pdia = new ProgressDialog(HospitalsActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(HospitalsActivity.this, getString(R.string.screenLoading)));
	    pdia.show();
	}

	@Override
	protected String doInBackground(Void... arg0) {
	    isTrue = true;
	    String result = null;
	    
	    JSONParser jparser = new JSONParser();
	    try {
	    	
		String latitude = ApplicationSettings.getPref(AppConstants.LATITUDE, "");
		String longitude = ApplicationSettings.getPref(AppConstants.LONGITUDE, "");
		
		result = jparser.getHospitalList(latitude, longitude, AppConstants.OFFSET_DEFAULT_VALUE);

		if (AppConstants.LogLevel == 1) {
		    serverTimeSpend = Utils.serverTimeSpend();
		    Utils.startPostServerCallTime = System.currentTimeMillis();
		}

	    } catch (Exception e) {
		// TODO Auto-generated catch block
	    }
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		
		if(ApplicationSettings.getPref(AppConstants.HOSPITAL_CACHE, "").equals(""))
		{
			ApplicationSettings.putPref(AppConstants.HOSPITAL_CACHE,result);
		}
	    if (pdia != null && pdia.isShowing())
		pdia.dismiss();
	    
	    loadData(result);
//	    try {
//		if (result != null) {
//		    hospitalList.clear();
//		    JSONObject jsonObject = new JSONObject(result.toString());
//		    if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("fail")) {
//			 hospitalListView.setMode(Mode.DISABLED);
//			Toast.makeText(HospitalsActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
//		    } else {
//			JSONArray jsonarray = new JSONArray(jsonObject.getString("hospital"));
//			if (AppConstants.LogLevel == 1) {
//			    try {
//				ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
//			    } catch (Exception e) {
//				e.printStackTrace();
//			    }
//			}
//			
//			for (int i = 0; i < jsonarray.length(); i++) {
//			    JSONObject obj = jsonarray.getJSONObject(i);
//			    HospitalInfo hospitalInfo = new HospitalInfo();
//			    if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {
//				hospitalInfo.setHospitalID(obj.getString("id"));
//				String hospitalName;
//				if (obj.getString("name").toString().contains(",")) {
//
//				    hospitalName = obj.getString("name").toString().split(",")[0];
//				} else {
//				    hospitalName = obj.getString("name").toString();
//				}
//				hospitalInfo.setHospitalName(hospitalName.replace("?", ""));
//				if (obj.getString("phone") != null && !obj.getString("phone").equalsIgnoreCase("")
//					&& !obj.getString("phone").equalsIgnoreCase("null"))
//				    hospitalInfo.setHospitalPhoneNumber(obj.getString("phone"));
//				if (obj.getString("email") != null && !obj.getString("email").equalsIgnoreCase("")
//					&& !obj.getString("email").equalsIgnoreCase("null"))
//				    hospitalInfo.setHospitalEmail(obj.getString("email"));
//				if (obj.has("distance") && obj.getString("distance") != null && !obj.getString("distance").equalsIgnoreCase("")
//					&& !obj.getString("distance").equalsIgnoreCase("99999"))
//				    hospitalInfo.setHospitalDistance(obj.getString("distance"));
//				
//				if (obj.has("locality") && obj.getString("locality") != null && !obj.getString("locality").equalsIgnoreCase("")
//					&& !obj.getString("locality").equalsIgnoreCase(null)){
//				    hospitalInfo.setHospitalLocality(obj.getString("locality").replace("?", ""));
//				}
//				if (obj.has("address") && obj.getString("address") != null && !obj.getString("address").equalsIgnoreCase("")
//					&& !obj.getString("address").equalsIgnoreCase(null))
//				    hospitalInfo.setHospitalAddress(obj.getString("address").replace("?", ""));
//				if (obj.has("speciality") && obj.getString("speciality") != null && !obj.getString("speciality").equalsIgnoreCase("")
//					&& !obj.getString("speciality").equalsIgnoreCase(null))
//				    hospitalInfo.setHospitalSpeciality(obj.getString("speciality"));
//				if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
//					&& !obj.getString("avgrate").equalsIgnoreCase(null) && !obj.getString("avgrate").equalsIgnoreCase("null"))
//				    hospitalInfo.setHospitalRating(obj.getString("avgrate"));
//				if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").trim().equalsIgnoreCase("")
//					&& !obj.getString("pic").trim().equalsIgnoreCase("null")){
//				    hospitalInfo.setHospitalImage(obj.getString("pic").trim());
//				}
//				if (obj.has("emergencyPic") && obj.getString("emergencyPic") != null
//					&& !obj.getString("emergencyPic").trim().equalsIgnoreCase("")
//					&& !obj.getString("emergencyPic").trim().equalsIgnoreCase(null)) {
//				    hospitalInfo.setHospitalEmergencyPic(obj.getString("emergencyPic").trim());
//				}
//				if (obj.has("leftPic") && obj.getString("leftPic") != null
//					&& !obj.getString("leftPic").trim().equalsIgnoreCase("")
//					&& !obj.getString("leftPic").trim().equalsIgnoreCase(null)) {
//				    hospitalInfo.setHospitalLeftPic(obj.getString("leftPic").trim());
//				}
//				if (obj.has("rightPic") && obj.getString("rightPic") != null
//					&& !obj.getString("rightPic").trim().equalsIgnoreCase("")
//					&& !obj.getString("rightPic").trim().equalsIgnoreCase(null)) {
//				    hospitalInfo.setHospitalRightPic(obj.getString("rightPic").trim());
//				}
//				    
//				hospitalList.add(hospitalInfo);
//			    }
//			}
//
//			if (hospitalList != null && hospitalList.size() > 0) {
//
//			    ApplicationSettings.putPref(AppConstants.LAST_COUNT, "" + hospitalList.size());
//			    hospitalAdapter = new HospitalsAdapter(HospitalsActivity.this, R.layout.hospital_list_item, hospitalList,"HospitalActivity");
//			    hospitalListView.setAdapter(hospitalAdapter);
//
//			    
//			    if (!isAllDataAvailable) {
//				hospitalListView.setMode(Mode.PULL_FROM_END);
//			    } else {
//				hospitalListView.setMode(Mode.DISABLED);
//			    }
//			} else {
//			    hospitalListView.setMode(Mode.DISABLED);
//			    Toast.makeText(HospitalsActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
//			}
//
//		    }
//		}
//		if (AppConstants.LogLevel == 1) {
//		    try {
//			postServerTimepend = Utils.postServerTimeSpend();
//			String totalScreenTimeSpend20 = Utils.totalScreenTimeSpend20();
//			Utils.callPerfomanceTestingApi("Hospitals 20 List", "HospitalsActivity", "ListHospital", preServerTimeSpend, serverTimeSpend,
//			    postServerTimepend, totalScreenTimeSpend20, Utils.getNetworkType(HospitalsActivity.this),
//			    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
//		    } catch (Exception e) {
//			e.printStackTrace();
//		    }
//		}
//
//	    } catch (JSONException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    }

	}

    }
    
    public void loadData(String result)
    {
    	
	    isTrue = false;
    	 try {
    			if (result != null) {
    			    hospitalList.clear();
    			    JSONObject jsonObject = new JSONObject(result.toString());
    			    if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("fail")) {
    				 hospitalListView.setMode(Mode.DISABLED);
    				Toast.makeText(HospitalsActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
    			    } else {
    				JSONArray jsonarray = new JSONArray(jsonObject.getString("hospital"));
    				if (AppConstants.LogLevel == 1) {
    				    try {
    					ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
    				    } catch (Exception e) {
    					e.printStackTrace();
    				    }
    				}
    				
    				for (int i = 0; i < jsonarray.length(); i++) {
    				    JSONObject obj = jsonarray.getJSONObject(i);
    				    HospitalInfo hospitalInfo = new HospitalInfo();
    				    if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {
    					hospitalInfo.setHospitalID(obj.getString("id"));
    					String hospitalName;
    					if (obj.getString("name").toString().contains(",")) {

    					    hospitalName = obj.getString("name").toString().split(",")[0];
    					} else {
    					    hospitalName = obj.getString("name").toString();
    					}
    					hospitalInfo.setHospitalName(hospitalName.replace("?", ""));
    					if (obj.getString("phone") != null && !obj.getString("phone").equalsIgnoreCase("")
    						&& !obj.getString("phone").equalsIgnoreCase("null"))
    					    hospitalInfo.setHospitalPhoneNumber(obj.getString("phone"));
    					if (obj.getString("email") != null && !obj.getString("email").equalsIgnoreCase("")
    						&& !obj.getString("email").equalsIgnoreCase("null"))
    					    hospitalInfo.setHospitalEmail(obj.getString("email"));
    					if (obj.has("distance") && obj.getString("distance") != null && !obj.getString("distance").equalsIgnoreCase("")
    						&& !obj.getString("distance").equalsIgnoreCase("99999"))
    					    hospitalInfo.setHospitalDistance(obj.getString("distance"));
    					
    					if (obj.has("locality") && obj.getString("locality") != null && !obj.getString("locality").equalsIgnoreCase("")
    						&& !obj.getString("locality").equalsIgnoreCase(null)){
    					    hospitalInfo.setHospitalLocality(obj.getString("locality").replace("?", ""));
    					}
    					if (obj.has("address") && obj.getString("address") != null && !obj.getString("address").equalsIgnoreCase("")
    						&& !obj.getString("address").equalsIgnoreCase(null))
    					    hospitalInfo.setHospitalAddress(obj.getString("address").replace("?", ""));
    					if (obj.has("speciality") && obj.getString("speciality") != null && !obj.getString("speciality").equalsIgnoreCase("")
    						&& !obj.getString("speciality").equalsIgnoreCase(null))
    					    hospitalInfo.setHospitalSpeciality(obj.getString("speciality"));
    					if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
    						&& !obj.getString("avgrate").equalsIgnoreCase(null) && !obj.getString("avgrate").equalsIgnoreCase("null"))
    					    hospitalInfo.setHospitalRating(obj.getString("avgrate"));
    					if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").trim().equalsIgnoreCase("")
    						&& !obj.getString("pic").trim().equalsIgnoreCase("null")){
    					    hospitalInfo.setHospitalImage(obj.getString("pic").trim());
    					}
    					if (obj.has("emergencyPic") && obj.getString("emergencyPic") != null
    						&& !obj.getString("emergencyPic").trim().equalsIgnoreCase("")
    						&& !obj.getString("emergencyPic").trim().equalsIgnoreCase(null)) {
    					    hospitalInfo.setHospitalEmergencyPic(obj.getString("emergencyPic").trim());
    					}
    					if (obj.has("leftPic") && obj.getString("leftPic") != null
    						&& !obj.getString("leftPic").trim().equalsIgnoreCase("")
    						&& !obj.getString("leftPic").trim().equalsIgnoreCase(null)) {
    					    hospitalInfo.setHospitalLeftPic(obj.getString("leftPic").trim());
    					}
    					if (obj.has("rightPic") && obj.getString("rightPic") != null
    						&& !obj.getString("rightPic").trim().equalsIgnoreCase("")
    						&& !obj.getString("rightPic").trim().equalsIgnoreCase(null)) {
    					    hospitalInfo.setHospitalRightPic(obj.getString("rightPic").trim());
    					}
    					    
    					hospitalList.add(hospitalInfo);
    				    }
    				}

    				if (hospitalList != null && hospitalList.size() > 0) {

    				    ApplicationSettings.putPref(AppConstants.LAST_COUNT, "" + hospitalList.size());
    				    hospitalAdapter = new HospitalsAdapter(HospitalsActivity.this, R.layout.hospital_list_item, hospitalList,"HospitalActivity");
    				    hospitalListView.setAdapter(hospitalAdapter);

    				    
    				    if (!isAllDataAvailable) {
    					hospitalListView.setMode(Mode.PULL_FROM_END);
    				    } else {
    					hospitalListView.setMode(Mode.DISABLED);
    				    }
    				} else {
    				    hospitalListView.setMode(Mode.DISABLED);
    				    Toast.makeText(HospitalsActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
    				}

    			    }
    			}
    			if (AppConstants.LogLevel == 1) {
    			    try {
    				postServerTimepend = Utils.postServerTimeSpend();
    				String totalScreenTimeSpend20 = Utils.totalScreenTimeSpend20();
    				Utils.callPerfomanceTestingApi("Hospitals 20 List", "HospitalsActivity", "ListHospital", preServerTimeSpend, serverTimeSpend,
    				    postServerTimepend, totalScreenTimeSpend20, Utils.getNetworkType(HospitalsActivity.this),
    				    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
    			    } catch (Exception e) {
    				e.printStackTrace();
    			    }
    			}

    		    } catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		    }
    }

  /**
   * Load All Hospital data in background.
   */
    public void hospitalDetailsLoadALLData(final String offset) {
	final class GetHospitalList extends AsyncTask<Void, Void, String> {

	    @Override
	    protected String doInBackground(Void... arg0) {
		if (AppConstants.LogLevel == 1) {
		    Utils.startServerTime = System.currentTimeMillis();
		}
		JSONParser jparser = new JSONParser();
		String latitude = ApplicationSettings.getPref(AppConstants.LATITUDE, "");
		String longitude = ApplicationSettings.getPref(AppConstants.LONGITUDE, "");
		String result = jparser.getHospitalList(latitude, longitude, offset);
		if (AppConstants.LogLevel == 1) {
		    serverTimeSpend = Utils.serverTimeSpend();
		    Utils.startPostServerCallTime = System.currentTimeMillis();
		} 
		return result.toString();
	    }

	    @Override
	    protected void onPostExecute(String result) {

		hospitalListView.onRefreshComplete();
		try {
		    if (result != null) {
		//	hospitalList.clear();
			JSONObject jsonObject = new JSONObject(result.toString());
			if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("fail")) {

			} else {
			    JSONArray jsonarray = new JSONArray(jsonObject.getString("hospital"));
			    if (AppConstants.LogLevel == 1) {
				try {
				    ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
				} catch (Exception e) {
				    e.printStackTrace();
				}
			    }
			    
			    for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject obj = jsonarray.getJSONObject(i);
				HospitalInfo hospitalInfo = new HospitalInfo();
				if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalID(obj.getString("id"));

				    String hospitalName;
				    if (obj.getString("name").toString().contains(",")) {

					hospitalName = obj.getString("name").toString().split(",")[0];
				    } else {
					hospitalName = obj.getString("name").toString();
				    }

				    hospitalInfo.setHospitalName(hospitalName.replace("?", ""));
				    if (obj.getString("phone") != null && !obj.getString("phone").equalsIgnoreCase("")
					    && !obj.getString("phone").equalsIgnoreCase("null"))
					hospitalInfo.setHospitalPhoneNumber(obj.getString("phone"));
				    if (obj.getString("email") != null && !obj.getString("email").equalsIgnoreCase("")
					    && !obj.getString("email").equalsIgnoreCase("null"))
					hospitalInfo.setHospitalEmail(obj.getString("email"));
				    if (obj.has("distance") && obj.getString("distance") != null && !obj.getString("distance").equalsIgnoreCase("")
					    && !obj.getString("distance").equalsIgnoreCase("99999"))
					hospitalInfo.setHospitalDistance(obj.getString("distance"));
				    if (obj.has("locality") && obj.getString("locality") != null && !obj.getString("locality").equalsIgnoreCase("")
						&& !obj.getString("locality").equalsIgnoreCase(null)){
					    hospitalInfo.setHospitalLocality(obj.getString("locality").replace("?", ""));
					}
				    if (obj.has("address") && obj.getString("address") != null && !obj.getString("address").equalsIgnoreCase("")
					    && !obj.getString("address").equalsIgnoreCase(null))
					hospitalInfo.setHospitalAddress(obj.getString("address").replace("?", ""));
				    if (obj.has("speciality") && obj.getString("speciality") != null
					    && !obj.getString("speciality").equalsIgnoreCase("")
					    && !obj.getString("speciality").equalsIgnoreCase(null))
					hospitalInfo.setHospitalSpeciality(obj.getString("speciality"));
				    if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
					    && !obj.getString("avgrate").equalsIgnoreCase(null) && !obj.getString("avgrate").equalsIgnoreCase("null"))
					hospitalInfo.setHospitalRating(obj.getString("avgrate"));
				    if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").trim().equalsIgnoreCase("")
					    && !obj.getString("pic").trim().equalsIgnoreCase("null")){
					hospitalInfo.setHospitalImage(obj.getString("pic").trim());
				    }
				    
				    if (obj.has("emergencyPic") && obj.getString("emergencyPic") != null
						&& !obj.getString("emergencyPic").trim().equalsIgnoreCase("")
						&& !obj.getString("emergencyPic").trim().equalsIgnoreCase(null)) {
					    hospitalInfo.setHospitalEmergencyPic(obj.getString("emergencyPic").trim());
					}
					if (obj.has("leftPic") && obj.getString("leftPic") != null
						&& !obj.getString("leftPic").trim().equalsIgnoreCase("")
						&& !obj.getString("leftPic").trim().equalsIgnoreCase(null)) {
					    hospitalInfo.setHospitalLeftPic(obj.getString("leftPic").trim());
					}
					if (obj.has("rightPic") && obj.getString("rightPic") != null
						&& !obj.getString("rightPic").trim().equalsIgnoreCase("")
						&& !obj.getString("rightPic").trim().equalsIgnoreCase(null)) {
					    hospitalInfo.setHospitalRightPic(obj.getString("rightPic").trim());
					}
				    
				    hospitalList.add(hospitalInfo);
				}
			    }

			    if (hospitalList != null && hospitalList.size() > 0) {

				ApplicationSettings.putPref(AppConstants.LAST_COUNT, "" + hospitalList.size());
				//searchHospital(searchEditText);
				if(isPullToRefreshFromEnd){
				    isPullToRefreshFromEnd = false;
				  //  hospitalListView.setRefreshing();
				}
				
				hospitalListView.onRefreshComplete();
				hospitalListView.setMode(Mode.PULL_FROM_END);
				hospitalAdapter = new HospitalsAdapter(HospitalsActivity.this, R.layout.hospital_list_item, hospitalList,"HospitalActivity");
			//	hospitalListView.setAdapter(hospitalAdapter);
				hospitalAdapter.notifyDataSetChanged();
				searchText(hospitalList);
				//isAllDataAvailable = true;
			    }

			}

		    }
		    if (AppConstants.LogLevel == 1) {
			try {
			    postServerTimepend = Utils.postServerTimeSpend();
			    totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			    Utils.callPerfomanceTestingApi("Hospitals All List", "HospitalsActivity", "ListHospital", preServerTimeSpend,
			    	serverTimeSpend, postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(HospitalsActivity.this),
			    	ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		} catch (JSONException e) {
			isAllDataAvailable = true;
			hospitalListView.setMode(Mode.DISABLED);
		    e.printStackTrace();
		}
		
	    }

	}
	new GetHospitalList().execute();

	}

	//click events for views
    public void clickEvent() {
	hospitalListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HospitalInfo entry = (HospitalInfo) parent.getItemAtPosition(position);
		if (parent != null && entry != null) {
		    ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getHospitalID());
		    ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY, gpTextView.getText().toString());
		    easyTracker.send(MapBuilder.createEvent("Hospital Tab", "View Hospital",
			    gpTextView.getText().toString() + ":" + entry.getHospitalName(), null).build());
		    isEventPressed = true;
		    startActivity(new Intent(HospitalsActivity.this, HospitalDetailsActivity.class));
		}
	    }
	});

    }

	public boolean isNetworkAvailable() {
		boolean isConnectedWifi = false;
		boolean isConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equals(ni.getTypeName()))
				if (ni.isConnected())
					isConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase(ni.getTypeName()))
				if (ni.isConnected())
					isConnectedMobile = true;
		}
		return isConnectedWifi || isConnectedMobile;
	}
	/*get the data from preferences use spannable string to show location in action bar add custom color, fonts to it*/
	public void showLocation() {
		if (ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, "") != null
			&& ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, "").length() > 1
			&& ApplicationSettings.getPref(AppConstants.CURRENT_USER_SUB_LOCALITY, "") != null
			&& ApplicationSettings.getPref(AppConstants.CURRENT_USER_SUB_LOCALITY, "").length() > 1) {

			SpannableString s = new SpannableString(Html.fromHtml("<font color=\"#ed207b\"><b>"
					+ ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, "") + "</b></font>"));
			s.setSpan(new com.medinfi.utils.TypefaceSpan(this, "RobotoRegular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionbarTitle.setText(s);
			SpannableString s1 = new SpannableString(Html.fromHtml("<font color=\"#ed207b\">"
					+ ApplicationSettings.getPref(AppConstants.CURRENT_USER_SUB_LOCALITY, "") + "</font>"));
			s1.setSpan(new com.medinfi.utils.TypefaceSpan(this, "RobotoLight.ttf"), 0, s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			actionbarSubtitle.setText(s1);
			
		} else {
			
		    startLocationUpdate();
		}
	}
	/*click event for near me view and top rated view
	public void clickTextViews() {
		nearmeTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//easyTracker.send(MapBuilder.createEvent(gpTextView.getText().toString(), "NEAR ME", "Empty", null).build());
				nearbyView.setVisibility(View.VISIBLE);
				topratedView.setVisibility(View.INVISIBLE);
				if (isNetworkAvailable()) {
					hospitalDetails(AppConstants.OFFSET_DEFAULT_VALUE);
					getHospitalList = new GetHospitalList();
					getHospitalList.execute();
				} else {
					Toast.makeText(HospitalsActivity.this, "Network not Available", Toast.LENGTH_SHORT).show();
				}

			}
		});
		topratedTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//easyTracker.send(MapBuilder.createEvent(gpTextView.getText().toString(), "TOP RATED", "Empty", null).build());
				nearbyView.setVisibility(View.INVISIBLE);
				topratedView.setVisibility(View.VISIBLE);
				if (isNetworkAvailable()) {
					TopRatedHospitalDetails();
				} else {
					Toast.makeText(HospitalsActivity.this, "Network not Available", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}*/
	/*search query called here*/
	public void searchHospital(final AutoCompleteTextView editsearch) {
		// Capture Text in EditText
		editsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = editsearch.getText().toString()
						.toLowerCase(Locale.getDefault());
				//easyTracker.send(MapBuilder.createEvent("Hospital Tab","Search Hospital", text, null).build());
				/*if (hospitalList != null) {
					if (hospitalAdapter != null)
						hospitalAdapter.filter(text);
				}*/
				
				if(!text.startsWith("com.medinfi.datainfo"))
				{
				isTrueToast=false;
				Log.e("Calling Feature::::::::::::::::::::::;", "True");
				if (text != null && text.length() > 2) {
					
					callSearchAPI(text);
				}
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
		});
	}
	/*//enable and disable pulltorefresh from the adapter once the query results populated
	public static void CheckEnabling() {
		int countValue = Integer.parseInt(ApplicationSettings.getPref(
				AppConstants.LAST_COUNT, ""));
		if (countValue > 19) {
			hospitalListView.setMode(Mode.PULL_FROM_END);
		} else {
			hospitalListView.setMode(Mode.DISABLED);
		}
	}*/
	
	public void callSearchAPI(final String keyword)
 {
	new AsyncTask<Void, Void, String>() {

	    @Override
	    protected String doInBackground(Void... params) {

		String result = null;
		try {
		    JSONParser jparser = new JSONParser();

		    String latitude = ApplicationSettings.getPref(AppConstants.LATITUDE, "");
		    String longitude = ApplicationSettings.getPref(AppConstants.LONGITUDE, "");
		    result = jparser.getAllSearch(keyword, latitude, longitude,
			    ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, ""), "0");
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		}
		return result.toString();
	    }

	    protected void onPostExecute(String result) {
		if (result != null) {
		    hideKeyboard();
		    Log.e("Results JSON::::::::::::::::::::::;", "True" + result);
		    /* {"status":"Fail","message":"No Results Found!"} */

		    JSONObject jObject = null;
		    try {
			jObject = new JSONObject(result);
		    } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		    try {
			if (jObject != null && jObject.has("status") && jObject.getString("status") != null
				&& jObject.getString("status").equalsIgnoreCase("Fail")) {

			    if (!isTrueToast) {
				easyTracker.send(MapBuilder.createEvent("Hospital Tab", " Search Select ", "No Results Found", null).build());
				toast = Toast.makeText(HospitalsActivity.this, "", Toast.LENGTH_SHORT);
				toast.setText("No Results Found");
				toast.show();
				isTrueToast = true;
			    }
			} else {
			    try {
				JSONArray array = new JSONArray(result);

				if (array != null && array.length() > 0) {
				    ArrayList<String> searchList = new ArrayList<String>();
				    autocompleteList = new ArrayList<AutoCompleteInfo>();
				    for (int i = 0; i < array.length(); i++) {

					JSONObject jsonObject = array.getJSONObject(i);
					Log.e("Calling Feature::::::::::::::::::::::;", "True" + jsonObject.getString("name"));
					AutoCompleteInfo autoCompleteInfo = new AutoCompleteInfo();
					autoCompleteInfo.setId(jsonObject.getString("id"));
					autoCompleteInfo.setName(jsonObject.getString("name").toString().replace("?", ""));

					searchList.add(jsonObject.getString("name"));
					autocompleteList.add(autoCompleteInfo);
				    }
				    if (autocompleteList != null && autocompleteList.size() > 0) {
					isTrueToast = false;
					autoCompleteAdapter = new AutoCompleteAdapter(HospitalsActivity.this, R.layout.autocompletesearch,
						autocompleteList,"");

					searchText.setAdapter(autoCompleteAdapter);
					autoCompleteAdapter.notifyDataSetChanged();

					

					// easyTracker.send(MapBuilder.createEvent("Hospital Tab",
					// " Search Select", "Keyword",
					// null).build());

					searchText.setOnItemClickListener(new OnItemClickListener() {

					    @Override
					    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method
						// stub

						if (toast != null) {
						    toast.cancel();
						    toast = null;
						}
						// TODO Auto-generated
						// method
						// stub
						/*
						 * searchText.setText("");
						 * hideKeyboard();
						 */
						searchText.setText("");
						searchLayout.setVisibility(View.GONE);
						searchImageView.setVisibility(View.VISIBLE);
						titleLayout.setVisibility(View.VISIBLE);
						AutoCompleteInfo entry = (AutoCompleteInfo) parent.getItemAtPosition(position);
						if (parent != null && entry != null) {
						    easyTracker.send(MapBuilder.createEvent("Hospital Tab", " Search Select ",
							    gpTextView.getText().toString() + ", " + entry.getName(), null).build());
						    ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getId());
						    ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY, gpTextView.getText().toString());
						    if (toast != null)
							toast.cancel();
						    startActivity(new Intent(HospitalsActivity.this, HospitalDetailsActivity.class));
						}

					    }
					});
				    }
				} else {
				    Log.e("No data", "No data");
				}
			    } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			}
		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }

		}
	    };
	}.execute();
    }
	
	/*public void customActionbar() {

		getSupportActionBar().show();
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.searchbar, null);

		LinearLayout backLayout = (LinearLayout) mCustomView.findViewById(R.id.back_layout);
		searchLayout = (LinearLayout) mCustomView.findViewById(R.id.search_lyt);

		titleLayout = (LinearLayout) mCustomView.findViewById(R.id.ab_titles_lyt);
		searchText = (AutoCompleteTextView) mCustomView.findViewById(R.id.search_input_et);
		searchImageView = (ImageView) mCustomView.findViewById(R.id.searchbtn);
		searchText = (AutoCompleteTextView) mCustomView.findViewById(R.id.search_input_et);

		searchHospital(searchText);
		ImageView cancelImageView = (ImageView) mCustomView.findViewById(R.id.closebtn);

		searchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				searchLayout.setVisibility(View.VISIBLE);
				searchText.setHint(getResources().getString(R.string.gp_hint_search));
				searchText.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
				searchImageView.setVisibility(View.GONE);
				titleLayout.setVisibility(View.GONE);

			}
		});

		cancelImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (searchText != null && searchText.getText().toString().length() > 0) {
					easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Close", searchText.getText().toString(), null).build());
				}else{
					easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Close", "Close", null).build());
				}
				searchText.setText("");
				hideKeyboard();
				searchLayout.setVisibility(View.GONE);
				searchImageView.setVisibility(View.VISIBLE);
				titleLayout.setVisibility(View.VISIBLE);
			}
		});
		backLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchText != null && searchText.getText().toString().length() > 0) {
					easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Back", searchText.getText().toString(), null).build());
				}else{
					easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Back", "Back", null).build());
				}
				HospitalsActivity.this.finish();
			}
		});
		actionbarTitle = (TextView) mCustomView.findViewById(R.id.title);
		actionbarSubtitle = (TextView) mCustomView.findViewById(R.id.subtitle);

		ImageView imageButton = (ImageView) mCustomView.findViewById(R.id.imageButton);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				HospitalsActivity.this.finish();
			}
		});

		getSupportActionBar().setCustomView(mCustomView);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

	}*/
	private void hideKeyboard() {   
	    // Check if no view has focus:
	    View view = this.getCurrentFocus();
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (toast != null)
		{
			toast.cancel();
			toast=null;
			isTrueToast=true;
			
		}
	}
	
	/*handle back button event*/
	@Override
	public void onBackPressed() {

		if (isTrue) {
		
			pdia.setCancelable(true);
			getHospitalList.cancel(true);
			HospitalsActivity.this.finish();
		}
		super.onBackPressed();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isTrue = false;
		if(pdia!=null && pdia.isShowing())
			pdia.dismiss();
	}
	/*listener for dialog cancel in thread*/
	OnCancelListener cancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface arg0) {
			isTrue = true;
			//HospitalsActivity.this.finish();
		}
	};

	/*handle back button event*/
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    isEventPressed = true;
	    if (getHospitalList != null && getHospitalList.getStatus() == Status.RUNNING)
		getHospitalList.cancel(true);
	    if (pdia != null && pdia.isShowing())
		pdia.dismiss();
	    easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Device Back", "Device Back", null).build());
	    HospitalsActivity.this.finish();

	}

	return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
	super.onResume();
	try {
	    if (!isEventPressed) {
		if (isHomePressed) {
		    easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Device Home", "Device Home", null).build());
		    
		}
		   
			//if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
				
				ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
				
				new CreateSessionIdAsyncTask().execute("0", "");
				
			//}
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
		FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
		 FlurryAgent.logEvent("Hospitals Activity");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		 FlurryAgent.onEndSession(this);
	        isHomePressed = true;
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	/*hide keyboard on tap of screen*/
	@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
	boolean ret = false;
	try {
	    View v = getCurrentFocus();
	    ret = super.dispatchTouchEvent(event);

	    if (v instanceof EditText) {
		View w = getCurrentFocus();
		int scrcoords[] = new int[2];
		w.getLocationOnScreen(scrcoords);
		float x = event.getRawX() + w.getLeft() - scrcoords[0];
		float y = event.getRawY() + w.getTop() - scrcoords[1];

		if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {

		    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return ret;
    }
	
	
	
	/*searchSpecialist based on key */
    public void searchHospital(final EditText editsearch) {
	editsearch.addTextChangedListener(new TextWatcher() {
	    @Override
	    public void afterTextChanged(Editable arg0) {
		strSearchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		if(strSearchText!=null && strSearchText.trim().length() == 3){
		    easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Search Hospital", strSearchText, null).build());
		}
		if (hospitalList != null) {
		    /*if (hospitalAdapter != null)
			hospitalAdapter.filter(strSearchText);*/
		    searchText(hospitalList);
		}
	    }

	    @Override
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	    }

	    @Override
	    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	    }
	});
    }

    private void searchText(ArrayList<HospitalInfo> arrListFilteredItems) {
	try {
	    ArrayList<HospitalInfo> arrListFilter = new ArrayList<HospitalInfo>();
	    if (!strSearchText.equals("")) {
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if (arrListFilteredItems.get(i).getHospitalName().toLowerCase(Locale.getDefault()).startsWith(strSearchText)) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if (arrListFilteredItems.get(i).getHospitalName().toLowerCase(Locale.getDefault()).contains(strSearchText)) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		    arrListFilteredItems.get(i).setAddedToFilteredList(false);
		}
		setAdapter(arrListFilter);
	    } else {
		setAdapter(arrListFilteredItems);
	    }
	} catch (Exception e) {
	    e.getStackTrace();
	}
    }

    private void setAdapter(ArrayList<HospitalInfo> arrListFilteredItems) {
	if(arrListFilteredItems.size() > 0){
	    txtNoResultFound.setVisibility(View.GONE);
	    hospitalListView.setVisibility(View.VISIBLE);
	    hospitalAdapter = new HospitalsAdapter(HospitalsActivity.this, R.layout.hospital_list_item, arrListFilteredItems,"HospitalActivity");
	    hospitalListView.setAdapter(hospitalAdapter);
	}else{
	    hospitalListView.setVisibility(View.GONE);
	    txtNoResultFound.setVisibility(View.VISIBLE);
	}
	
    }
    public static HospitalsActivity getInstance(){
	return hospitalScreen;
	
    }
    
    private void tabClickEvent(){
    	RelativeLayout hospital,doctor,profile;
    	ImageView hospital_iv;
    	TextView hospital_tv;
    	View hospital_view;
    	hospital= (RelativeLayout)findViewById(R.id.hospital);
    	doctor= (RelativeLayout)findViewById(R.id.doctor);
    	profile= (RelativeLayout)findViewById(R.id.profile);  
    	hospital_iv= (ImageView)findViewById(R.id.hospital_iv);
    	hospital_tv= (TextView)findViewById(R.id.hospital_tv);
    	hospital_view=findViewById(R.id.hospital_view);
    	
    	hospital_iv.setBackgroundResource(R.drawable.hospital_on);
    	hospital_tv.setTextColor(getResources().getColor(R.color.screen_header));
    	hospital_view.setVisibility(View.VISIBLE);
    	hospital_tv.setTypeface(Typeface.DEFAULT_BOLD);
    			
    	RelativeLayout layoutLocation = (RelativeLayout)findViewById(R.id.layoutLocation);
	 layoutLocation.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Update Location", "Update Location", null).build());
		startLocationUpdate(); 
	    }
	});
	 
	 doctor.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {    
			    easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Doctor Tab", "Doctor Tab", null).build());
				
			    Intent intent=new Intent(HospitalsActivity.this, SpecialistDoctorsActivity.class).putExtra("key_specialistID",
						"-999").putExtra("key_specialist", "");
			    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			    finish();
			    
		}
	});
	 profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
easyTracker.send(MapBuilder.createEvent("Hospital Tab", "Profile Tab", "Profile Tab", null).build());
				
			    Intent intent=new Intent(HospitalsActivity.this, ProfileActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			    finish();
			}
		});
	 
   }
    
    
    /**
     * Quick search for Doctor and Hospital Author LalBabu
     * 
     * @param editsearch
     */
    public void globalSearch(final EditText editsearch) {
	editsearch.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void afterTextChanged(Editable arg0) {
		String globalSearchText;
		globalSearchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		
		if (globalSearchText != null && globalSearchText.length() < 1) {
			asyncTask.cancel(true);
			globalListView.setVisibility(View.GONE);
			txtNoResultFound.setVisibility(View.GONE);
			hospitalListView.setVisibility(View.VISIBLE);
			globalListView.setAdapter(null);
		    easyTracker.send(MapBuilder.createEvent("Global Search", "Global Search", "Global Search", null).build());
		}
		if (Utils.isNetworkAvailable(HospitalsActivity.this)) {
		    // globalSearchList.clear();
		    if (globalSearchText != null && globalSearchText.length() > 2) {
		    	hospitalListView.setVisibility(View.GONE);
				txtNoResultFound.setVisibility(View.GONE);
				globalListView.setVisibility(View.VISIBLE);
			    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
			 if (AppConstants.LogLevel == 1) {
				Utils.startTotalScreenTime = System.currentTimeMillis();
				Utils.startPreServerCallTime = System.currentTimeMillis();
			    }
			
			asyncTask= (GetGlobalSearchList) new GetGlobalSearchList();
			asyncTask.execute(globalSearchText);
		    }
		} else {
			hospitalListView.setVisibility(View.GONE);
		    globalListView.setVisibility(View.GONE);
		    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
		}
	    }

	    @Override
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	    }

	    @Override
	    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	    }
	});
    }
    
    public class GetGlobalSearchList extends AsyncTask<String, Void, String> {
    	private ProgressDialog pdia;

    	@Override
    	protected void onPreExecute() {
    	    super.onPreExecute();
    	    if (AppConstants.LogLevel == 1) {
    		Utils.startServerTime = System.currentTimeMillis();
    		preServerTimeSpend = Utils.preServerTimeSpend();
    	    }
    	   /* pdia = new ProgressDialog(HomeScreen.this);
    	    pdia.setCancelable(true);
    	    pdia.setOnCancelListener(cancelListener);
    	    pdia.setMessage("Loading...");*/
    	    // pdia.show();
    	    ((ProgressBar) findViewById(R.id.globalprogressBar)).setVisibility(View.VISIBLE);

    	}

    	@Override
    	protected String doInBackground(String... arg0) {
    	    String result = null;
    	    JSONParser jparser = new JSONParser();
    	    try {
    		result = jparser.getGlobalHospitalSearchList(arg0[0], "20", ApplicationSettings.getPref(AppConstants.LATITUDE, ""),
    			ApplicationSettings.getPref(AppConstants.LONGITUDE, ""));
    		if (AppConstants.LogLevel == 1) {
    		    serverTimeSpend = Utils.serverTimeSpend();
    		}
    	    } catch (Exception e) {
    		e.printStackTrace();
    	    }
    	    return result.toString();
    	}

    	@Override
    	protected void onPostExecute(String result) {
    	    try {
    	    	 ((ProgressBar) findViewById(R.id.globalprogressBar)).setVisibility(View.GONE);
    		/*
    		 * if (pdia != null && pdia.isShowing()) { pdia.dismiss(); }
    		 */
    		if (result != null) {
    		    if (AppConstants.LogLevel == 1) {
    			Utils.startPostServerCallTime = System.currentTimeMillis();
    		    }
    		    // hideKeyboard();
    		    globalSearchList.clear();
    		    JSONObject jsonObject = new JSONObject(result.toString());

    		    if (jsonObject.has("Status")) {
    			globalListView.setVisibility(View.GONE);
    			
    			((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
    			if(((EditText)findViewById(R.id.searchedittext)).getText().toString().length()!=0){
    				hospitalListView.setVisibility(View.GONE);
    				txtNoResultFound.setVisibility(View.VISIBLE);
    			}
    			
    			// ((TextView)
    			// findViewById(R.id.txtNoNetwork)).setText("");
    			// ((TextView)
    			// findViewById(R.id.txtNoNetworkDesc)).setText("No Result Found");
    			// ((TextView)
    			// findViewById(R.id.txtNoNetworkDesc)).setTextColor(Color.BLACK);;
    		    } else {
    			JSONArray jsonarray = new JSONArray(jsonObject.getString("hospital"));
    			if (AppConstants.LogLevel == 1) {
    			    try {
    				ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
    			    } catch (Exception e) {
    				e.printStackTrace();
    			    }
    			}
    			for (int i = 0; i < jsonarray.length(); i++) {
    			    JSONObject obj = jsonarray.getJSONObject(i);
    			    AutoCompleteInfo autoCompleteInfo = new AutoCompleteInfo();
    			    if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {

    				autoCompleteInfo.setId(obj.getString("id"));
    				String profileName;
    				if (obj.getString("name").toString().contains(",")) {

    				    profileName = obj.getString("name").toString().split(",")[0];
    				} else {
    				    profileName = obj.getString("name").toString();
    				}
    				autoCompleteInfo.setName(profileName.replace("?", ""));
    				if (obj.getString("address") != null && !obj.getString("address").equalsIgnoreCase("")
    					&& !obj.getString("address").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setAddress(obj.getString("address"));
    				}

    				if (obj.getString("primaryphone") != null && !obj.getString("primaryphone").equalsIgnoreCase("")
    					&& !obj.getString("primaryphone").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setPrimaryphone(obj.getString("primaryphone"));
    				}

    				if (obj.has("locality") && obj.getString("locality") != null && !obj.getString("locality").equalsIgnoreCase("")
    					&& !obj.getString("locality").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setLocality(obj.getString("locality").replace("?", ""));
    				}

    				if (obj.has("type") && obj.getString("type") != null && !obj.getString("type").equalsIgnoreCase("")
    					&& !obj.getString("type").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setType(obj.getString("type").replace("?", ""));
    				}
    				if (obj.has("lat") && obj.getString("lat") != null && !obj.getString("lat").equalsIgnoreCase("")
    					&& !obj.getString("lat").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setLatitude(obj.getString("lat").replace("?", ""));
    				}
    				if (obj.has("lon") && obj.getString("lon") != null && !obj.getString("lon").equalsIgnoreCase("")
    					&& !obj.getString("lon").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setLongitude(obj.getString("lon").replace("?", ""));
    				}
    				if (obj.has("doctype") && obj.getString("doctype") != null && !obj.getString("doctype").equalsIgnoreCase("")
    					&& !obj.getString("doctype").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setDoctype(obj.getString("doctype").replace("?", ""));
    				}
    				if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
    					&& !obj.getString("avgrate").equalsIgnoreCase(null) && !obj.getString("avgrate").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setRating(obj.getString("avgrate"));
    				}
    				if (obj.has("distance") && obj.getString("distance") != null && !obj.getString("distance").equalsIgnoreCase("")
    					&& !obj.getString("distance").equalsIgnoreCase(null) && !obj.getString("distance").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setDistance(obj.getString("distance"));
    				}
    				if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").equalsIgnoreCase("")
    					&& !obj.getString("pic").equalsIgnoreCase(null) && !obj.getString("pic").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setPic(obj.getString("pic"));
    				}

    				if (obj.has("second_image") && obj.getString("second_image") != null
    					&& !obj.getString("second_image").trim().equalsIgnoreCase("")
    					&& !obj.getString("second_image").trim().equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setDocSecondImage(obj.getString("second_image").trim());
    				}

    				if (obj.has("emergencyPic") && obj.getString("emergencyPic") != null
    					&& !obj.getString("emergencyPic").trim().equalsIgnoreCase("")
    					&& !obj.getString("emergencyPic").trim().equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setHospitalEmergencyPic(obj.getString("emergencyPic").trim());
    				}
    				if (obj.has("leftPic") && obj.getString("leftPic") != null && !obj.getString("leftPic").trim().equalsIgnoreCase("")
    					&& !obj.getString("leftPic").trim().equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setHospitalLeftPic(obj.getString("leftPic").trim());
    				}
    				if (obj.has("rightPic") && obj.getString("rightPic") != null && !obj.getString("rightPic").trim().equalsIgnoreCase("")
    					&& !obj.getString("rightPic").trim().equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setHospitalRightPic(obj.getString("rightPic").trim());
    				}

    				globalSearchList.add(autoCompleteInfo);
    			    }
    			}

    			if (globalSearchList != null && globalSearchList.size() > 0) {
    			    //globalListView.setVisibility(View.VISIBLE);
    				if(globalListView.getVisibility()==View.VISIBLE)
    					hospitalListView.setVisibility(View.GONE);
    			    txtNoResultFound.setVisibility(View.GONE);
    			    autoCompleteAdapter = new AutoCompleteAdapter(HospitalsActivity.this, R.layout.autocompletesearch, globalSearchList,"");
    			    globalListView.setAdapter(autoCompleteAdapter);

    			} else {
    			    globalListView.setVisibility(View.GONE);
    			    hospitalListView.setVisibility(View.GONE);
    			    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
    			    txtNoResultFound.setVisibility(View.VISIBLE);
    			}

    		    }

    		}
    		if (AppConstants.LogLevel == 1) {
    		    try {
    			postServerTimepend = Utils.postServerTimeSpend();
    			totalScreenTimeSpend = Utils.totalScreenTimeSpend();
    			Utils.callPerfomanceTestingApi("Hospital Search","HomeScreen","ListUserGlobalSearch",preServerTimeSpend,serverTimeSpend,
    			    postServerTimepend,totalScreenTimeSpend,Utils.getNetworkType(HospitalsActivity.this),
    			    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
    		    } catch (Exception e) {
    			e.printStackTrace();
    		    }
    		}
    	    } catch (JSONException e) {
    		globalListView.setVisibility(View.GONE);
    		hospitalListView.setVisibility(View.GONE);
    		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
    		txtNoResultFound.setVisibility(View.GONE);
    	    }
    	    
    	    	//ApplicationSettings.putPref(AppConstants.HOSP_ACT_CALLED, "no");
    	}

        }
    
    private void globalSearchListViewClickEvent() {
    	globalListView.setOnItemClickListener(new OnItemClickListener() {
    	    @Override
    	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		AutoCompleteInfo entry = (AutoCompleteInfo) parent.getItemAtPosition(position);
    		if (parent != null && entry != null) {
    		    isEventPressed = true;
    		    if (entry.getType().equalsIgnoreCase("Doctor")) {
    			easyTracker.send(MapBuilder.createEvent("Hospital Tab", " View Doctor", entry.getName(), null).build());
    			ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getId());
    			Intent intent = new Intent(HospitalsActivity.this, DoctorDetailsActivity.class);
    			intent.putExtra("CALLING_SCREEN", "HomeScreen");
    			startActivity(intent);
    		    } else {
    			easyTracker.send(MapBuilder.createEvent("Hospital Tab", "View Hospital", entry.getName(), null).build());
    			ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getId());
    			Intent intent = new Intent(HospitalsActivity.this, HospitalDetailsActivity.class);
    			intent.putExtra("CALLING_SCREEN", "HomeScreen");
    			startActivity(intent);
    		    }

    		}
    	    }
    	});
        }
    
}
