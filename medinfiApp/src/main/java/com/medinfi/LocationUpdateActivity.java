package com.medinfi;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.medinfi.adapters.CitySearchAdapter;
import com.medinfi.adapters.LocalitySearchAdapter;
import com.medinfi.adapters.SpecialistAdapter;
import com.medinfi.datainfo.SearchInfo;
import com.medinfi.datainfo.SpecialistInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.UpdateSessionIdAsyncTask;
import com.medinfi.utils.Utils;

/**
 * Handle City and Locality
 * 
 * @author LalBabu
 * 
 */
public class LocationUpdateActivity extends SherlockActivity {

    // initialize xml views
    private ListView cityListView;
    private EditText searchCity;
    private CitySearchAdapter citySearchAdapter;
    private ArrayList<SearchInfo> citySearchList;
    private ProgressDialog pdia;
    private GetCityList getCityList;

    // initialize for Locality
    private ListView localityListView;
    private EditText searchLocality;
    private String cityID;
    private LocalitySearchAdapter localitySearchAdapter;
    private ArrayList<SearchInfo> localitySearchList;
    // end of initialize for Locality

    private boolean updateLocation = false;
    private boolean isTrue = false;
    private EasyTracker easyTracker = null;
    private String searchCityText;
    private String searchLocalityText;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private Button btnUpdate;
    private TextView txtLocality;
    private TextView txtCity;
    private TextView txtAutoDetect;
    private TextView txtAddress;
    private ImageView imgCancel;
    private boolean isLocalityListPapulated = false;
    private static LocationUpdateActivity locationUpdateScreen;
    //For performance testing
    private String preServerTimeSpend="";
    private String serverTimeSpend="";
    private String postServerTimepend="";
    private String totalScreenTimeSpend="";
    private InterstitialAd interstitial;
    //private AdView adView;
    private LinearLayout layoutBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.location_update);
	initialize();
	
    }

    private void initialize() {
	try {
	    if (AppConstants.LogLevel == 1) {
		Utils.startTotalScreenTime = System.currentTimeMillis();
		Utils.startPreServerCallTime = System.currentTimeMillis();
	    }
	    //adView = (AdView)findViewById(R.id.adView);
	    localitySearchList = new ArrayList<SearchInfo>();
	    easyTracker = EasyTracker.getInstance(LocationUpdateActivity.this);
	    Typeface tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	    cityListView = (ListView) findViewById(R.id.cityListView);
	    cityListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    searchCity = (EditText) findViewById(R.id.searchCity);
	    searchCity.setTypeface(tf1);

	    localityListView = (ListView) findViewById(R.id.localityListView);
	    localityListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    searchLocality = (EditText) findViewById(R.id.searchLocality);
	    searchLocality.setTypeface(tf1);

	    btnUpdate = (Button) findViewById(R.id.btnUpdate);
	    btnUpdate.setTypeface(tf1);
	    txtLocality = (TextView) findViewById(R.id.txtLocality);
	    txtCity = (TextView) findViewById(R.id.txtCity);
	    txtAutoDetect = (TextView) findViewById(R.id.txtAutoDetect);
	    txtAddress = (TextView) findViewById(R.id.txtAddress);
	    txtAddress.setVisibility(View.GONE);
	    imgCancel = (ImageView)findViewById(R.id.imgCancel);
	    imgCancel.setVisibility(View.GONE);
	    layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);

	    locationAddressUpdate();
	    if (Utils.isNetworkAvailable(LocationUpdateActivity.this)) {
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	//	((ScrollView) findViewById(R.id.scrollviewlayoutMiddle)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutMiddle)).setVisibility(View.VISIBLE);
		getCityList = new GetCityList();
		getCityList.execute();
	    } else {
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	//	((ScrollView) findViewById(R.id.scrollviewlayoutMiddle)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutMiddle)).setVisibility(View.GONE);

	    }
	    isHomePressed = false;
	    isEventPressed = false;
	    locationUpdateScreen = this;
	    cityClickEvent();
	    searchCity(searchCity);

	    localityClickEvent();
	    searchLocality(searchLocality);
	    onAutoDetectClick();

	    editTextFocus();

	    
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    private void checkKeyBoardVisible(){
	if(isSoftKeyboardShown(searchLocality) || isSoftKeyboardShown(searchCity)){
		System.out.println("LocationUpdateActivity.initialize()");
	    }else{
		System.out.println("LocationUpdateActivity.initialize() else ");
	    }
    }
    /*private void googlAds() {
	interstitial = new InterstitialAd(LocationUpdateActivity.this);
   	interstitial.setAdUnitId(getString(R.string.googleAdsId));
   	AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("").build();
   	adView.loadAd(adRequest);
   	interstitial.loadAd(adRequest);
	interstitial.setAdListener(new AdListener() {
	    public void onAdLoaded() {
		displayInterstitial();
	    }
	    @Override
	    public void onAdOpened() {
		easyTracker.send(MapBuilder.createEvent("Current Location", "Admob", "Admob", null).build());
	    }
	});
       }*/

       public void displayInterstitial() {
   	if (interstitial.isLoaded()) {
   	    interstitial.show();
   	}
       }
    private void editTextFocus() {
	searchCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
		    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		    if (searchCity != null && searchCity.getText().toString().trim().length() > 1) {
			viewVisibility("city");
			searchCity.setText("");
		    }
		}
	    }
	});
    }

    private void viewVisibility(String msg) {
	if (msg.equalsIgnoreCase("city")) {
	    cityListView.setVisibility(View.VISIBLE);
	    txtCity.setVisibility(View.VISIBLE);
	    searchCity.setVisibility(View.GONE);
	    txtLocality.setVisibility(View.GONE);
	    searchLocality.setVisibility(View.GONE);
	    btnUpdate.setVisibility(View.GONE);
	    localityListView.setVisibility(View.GONE);
	    layoutBottom.setVisibility(View.GONE);

	} else if (msg.equalsIgnoreCase("locality")) {
	    localityListView.setVisibility(View.VISIBLE);
	    txtLocality.setVisibility(View.VISIBLE);
	    searchLocality.setVisibility(View.VISIBLE);
	    searchCity.setVisibility(View.GONE);
	    cityListView.setVisibility(View.GONE);
	    txtCity.setVisibility(View.GONE);
	    btnUpdate.setVisibility(View.GONE);
	    layoutBottom.setVisibility(View.GONE);
	} else {
	    hideKeyboard();
	    txtLocality.setVisibility(View.VISIBLE);
	    searchLocality.setVisibility(View.VISIBLE);
	    cityListView.setVisibility(View.GONE);
	    localityListView.setVisibility(View.GONE);
	    searchCity.setVisibility(View.VISIBLE);
	    txtCity.setVisibility(View.VISIBLE);
	    btnUpdate.setVisibility(View.VISIBLE);
	    layoutBottom.setVisibility(View.VISIBLE);
	  
	}
    }
    public boolean isSoftKeyboardShown(View v) {
	    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	    IMMResult result = new IMMResult();
	    int res;
	    imm.showSoftInput(v, 0, result);
	    // if keyboard doesn't change, handle the keypress
	    res = result.getResult();
	    if (res == InputMethodManager.RESULT_UNCHANGED_SHOWN ||
	            res == InputMethodManager.RESULT_UNCHANGED_HIDDEN) {
	        return true;
	    }
	    else{
		 return false;
	    }
	}
    /**
     * To capture the result of IMM hide/show soft keyboard
     */
    private class IMMResult extends ResultReceiver {
        public int result = -1;
        public IMMResult() {
            super(null);
        }
        
        @Override 
        public void onReceiveResult(int r, Bundle data) {
            result = r;
        }
        
        // poll result value for up to 500 milliseconds
        public int getResult() {
            try {
                int sleep = 0;
                while (result == -1 && sleep < 500) {
                    Thread.sleep(100);
                    sleep += 100;
                }
            } catch (InterruptedException e) {
                Log.e("IMMResult", e.getMessage());
            }
            return result;
        }
    }
    public void onAutoDetectClick() {
	txtAutoDetect.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	    	 //Clear the hospital and doctor Cache
	   	 ApplicationSettings.putPref(AppConstants.HOSPITAL_CACHE,"");
	   	 ApplicationSettings.putPref(AppConstants.DOCTOR_CACHE,"");
	   	 
		easyTracker.send(MapBuilder.createEvent("Current Location", "Auto Detection", "Auto Detection", null).build());
		startActivity(new Intent(LocationUpdateActivity.this, AutoDetection.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	    }
	});

    }

    public void onClickSearchCity(View view) {
	viewVisibility("city");
	if (searchCity != null && searchCity.length() > 0) {
	    searchCity.setText("");
	}
    }

    public void onClickSearchLocality(View view) {
	if (searchCity != null && searchCity.length() > 0) {
	    viewVisibility("locality");
	    searchLocality.setText("");
	    if (getIntent().getStringExtra(AppConstants.CALLING_SCREEN_KEY) != null
			&& getIntent().getStringExtra(AppConstants.CALLING_SCREEN_KEY).equalsIgnoreCase(AppConstants.CALLING_SCREEN_VALUE)) {
		if(!isLocalityListPapulated){
		    new GetLocalityList().execute();
		    hideKeyboard();
		  //  layoutBottom.setVisibility(View.VISIBLE);
		}
	    }
	    
	} else {
	    Toast.makeText(LocationUpdateActivity.this, "Please select City", Toast.LENGTH_LONG).show();
	}

    }

    public void onBtnUpdate(View view) {

    	 //Clear the hospital and doctor Cache
   	 ApplicationSettings.putPref(AppConstants.HOSPITAL_CACHE,"");
   	 ApplicationSettings.putPref(AppConstants.DOCTOR_CACHE,"");
    	
	if (isValidate()) {
	    ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION, searchCity.getText().toString().trim());
	    ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY, searchLocality.getText().toString().trim());
	    System.out.println("AppConstants.CURRENT_USER_LOCATION "+searchCity.getText().toString().trim());
	    System.out.println("AppConstants.CURRENT_USER_SUB_LOCALITY "+searchLocality.getText().toString().trim());
	    if(searchCity.getText().toString().trim().length() > 1 && searchLocality.getText().toString().trim().length() > 1){
		finishActivites();
		easyTracker.send(MapBuilder.createEvent("Current Location", "Update", searchCity.getText().toString().trim()+":"+searchLocality.getText().toString().trim(), null).build());
//		startActivity(new Intent(LocationUpdateActivity.this, HomeScreen.class));
//		 finish();
		 
		 ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
		    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
		    if (HospitalsActivity.getInstance() != null) {
			HospitalsActivity.getInstance().finish();
		    }
		    startActivity(new Intent(LocationUpdateActivity.this, HospitalsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
		    finish();
		 
	    }else{
		Toast.makeText(LocationUpdateActivity.this, "Seems like City and Locality is empty. ", Toast.LENGTH_LONG).show();
	    }
	   
	}
    }
    private void finishActivites(){
	try {
	    if(HomeScreen.getInstance()!=null){
	        HomeScreen.getInstance().finish();
	    }
	    if(GeneralPhysicansActivity.getInstance()!=null){
		GeneralPhysicansActivity.getInstance().finish();
	    }
	    if(SpecialistActivity.getInstance()!=null){
		SpecialistActivity.getInstance().finish();
	    }
	    if(SpecialistDoctorsActivity.getInstance()!=null){
		SpecialistDoctorsActivity.getInstance().finish();
	    }
	    if(HospitalsActivity.getInstance()!=null){
		HospitalsActivity.getInstance().finish();
	    }
	    if(FavouriteActivity.getInstance()!=null){
		FavouriteActivity.getInstance().finish();
	    }
	    if(SuggestDoctorHospitalActivity.getInstance()!=null){
		SuggestDoctorHospitalActivity.getInstance().finish();
	    }
	    if(ContactUs.getInstance()!=null){
		ContactUs.getInstance().finish();
	    }
	    if(MedicalReportsActivity.getInstance()!=null){
		MedicalReportsActivity.getInstance().finish();
	    }
		 if(ReviewsActivity.getInstance()!=null){
		ReviewsActivity.getInstance().finish();
	    }
		 if(ProfileActivity.getInstance()!=null){
			 ProfileActivity.getInstance().finish();
			    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    private void locationAddressUpdate() {
	if (getIntent().getStringExtra(AppConstants.CALLING_SCREEN_KEY) != null
		&& getIntent().getStringExtra(AppConstants.CALLING_SCREEN_KEY).equalsIgnoreCase(AppConstants.CALLING_SCREEN_VALUE)) {
	    if(ApplicationSettings.getPref(AppConstants.LOCATION_SELECTED_AUTO, false) 
		    && ApplicationSettings.getPref(AppConstants.CITY_SEARCH_ID, "").equalsIgnoreCase("CityIDNotFound")){
		viewVisibility("city");
	    }else{
		 viewVisibility("");
	    }
	    hideKeyboard();
	  //  layoutBottom.setVisibility(View.VISIBLE);
	    isLocalityListPapulated = false;
	    if (ApplicationSettings.getPref(AppConstants.LOCALITY_ADDRESS, "").length() > 1 
		    && !ApplicationSettings.getPref(AppConstants.LOCALITY_ADDRESS, "").equalsIgnoreCase("null")) {
		txtAddress.setVisibility(View.VISIBLE);
		txtAddress.setText(ApplicationSettings.getPref(AppConstants.LOCALITY_ADDRESS, ""));
	    }else{
		txtAddress.setVisibility(View.GONE);
	    }
	    searchCity.setText(ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, ""));
	    searchLocality.setText(ApplicationSettings.getPref(AppConstants.CURRENT_USER_SUB_LOCALITY, ""));
	    imgCancel.setVisibility(View.VISIBLE);
	} else {
	    viewVisibility("city");
	}
    }
    public void onClickCancel(View view){
	easyTracker.send(MapBuilder.createEvent("Current Location", "Close", "Close", null).build());
	LocationUpdateActivity.this.finish();
    }
    private boolean isValidate() {
	boolean isSelected = false;
	try {
	    isSelected = true;

	    if (searchCity.getText().toString().trim().equals("")) {
		((TextView) findViewById(R.id.txtErrorMessage)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.txtErrorMessage)).setText(getString(R.string.cityValidation));
		return false;
	    }
	    else if (searchLocality.getText().toString().trim().equals("")) {
		((TextView) findViewById(R.id.txtErrorMessage)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.txtErrorMessage)).setText(getString(R.string.localityValidation));
		return false;

	    }else{
		((TextView) findViewById(R.id.txtErrorMessage)).setVisibility(View.GONE);
	    }
	} catch (Exception e) {
	    return false;
	}
	return isSelected;

    }

    // City click events
    public void cityClickEvent() {
	cityListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		isEventPressed = true;
		Utils.startServerTime = System.currentTimeMillis();

		SearchInfo entry = (SearchInfo) parent.getItemAtPosition(position);
		//ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION,entry.getSearchName());
		easyTracker.send(MapBuilder.createEvent("Current Location", "Select City", entry.getSearchName(), null).build());
		cityID = entry.getSearchId();
		ApplicationSettings.putPref(AppConstants.CITY_SEARCH_ID,entry.getSearchId());
		System.out.println("AppConstants.CITY_SEARCH_ID"+entry.getSearchId());
		localitySearchList.clear();
		setLocalityAdapter(localitySearchList);
		if (Utils.isNetworkAvailable(LocationUpdateActivity.this)) {
		    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
		 //   ((ScrollView) findViewById(R.id.scrollviewlayoutMiddle)).setVisibility(View.VISIBLE);
		    ((LinearLayout) findViewById(R.id.layoutMiddle)).setVisibility(View.VISIBLE);
		    new GetLocalityList().execute();
		} else {
		    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
		 //   ((ScrollView) findViewById(R.id.scrollviewlayoutMiddle)).setVisibility(View.GONE);
		    ((LinearLayout) findViewById(R.id.layoutMiddle)).setVisibility(View.GONE);

		}
		
		searchCity.setText(entry.getSearchName());
		searchLocality.setText("");
		cityListView.setVisibility(View.GONE);
		ApplicationSettings.putPref(AppConstants.LOCATION_SELECTED_AUTO,false);
		viewVisibility("locality");
		// startActivity(new Intent(SelectCityActivity.this,
		// SelectLocationActivity.class).putExtra("key",
		// entry.getSearchName()).putExtra("city_key",
		// entry.getSearchId()));

	    }
	});
    }

    // search for city
    public void searchCity(final EditText editsearch) {
	editsearch.addTextChangedListener(new TextWatcher() {
	    @Override
	    public void afterTextChanged(Editable arg0) {
		searchCityText = editsearch.getText().toString().toLowerCase(Locale.getDefault());

		if (citySearchList != null) {
		    if (citySearchAdapter != null) {
			viewVisibility("city");
			citySearchAdapter.filter(searchCityText);
		    }

		}
	    }

	    @Override
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	    }

	    @Override
	    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	    }
	});
    }

    // Locality click event for listview items
    public void localityClickEvent() {
	localityListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		SearchInfo entry = (SearchInfo) parent.getItemAtPosition(position);

		if (entry.getLatitude() != null && !entry.getLatitude().equals("") && !entry.getLatitude().equals(null)
			&& !entry.getLatitude().equals("null") && entry.getLongitude() != null && !entry.getLongitude().equals("")
			&& !entry.getLongitude().equals(null) && !entry.getLongitude().equals("null")) {

		  //  ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY,entry.getSearchName());
		    ApplicationSettings.putPref(AppConstants.LATITUDE, entry.getLatitude());
		    ApplicationSettings.putPref(AppConstants.LONGITUDE, entry.getLongitude());
		    ApplicationSettings.putPref(AppConstants.LOCALITY_ADDRESS, entry.getLocalityAddress());
		    System.out.println("AppConstants.LATITUDE "+entry.getLatitude());
		    System.out.println("AppConstants.LONGITUDE "+entry.getLongitude());
		    System.out.println("AppConstants.LOCALITY_ADDRESS "+entry.getLocalityAddress());
		    isEventPressed = true;
		    new UpdateSessionIdAsyncTask().execute();
		    
		    easyTracker.send(MapBuilder.createEvent("Current Location", "Select Locality", searchCity.getText().toString().trim()+":"+entry.getSearchName(), null).build());
		    
		    searchLocality.setText(entry.getSearchName());
		    viewVisibility("");
		    ((TextView) findViewById(R.id.txtErrorMessage)).setVisibility(View.GONE);
		    if (getIntent().getStringExtra(AppConstants.CALLING_SCREEN_KEY) != null
				&& getIntent().getStringExtra(AppConstants.CALLING_SCREEN_KEY).equalsIgnoreCase(AppConstants.CALLING_SCREEN_VALUE)) {
			if(entry.getLocalityAddress().trim()!=null && !entry.getLocalityAddress().trim().equalsIgnoreCase("null")){
			    txtAddress.setVisibility(View.VISIBLE);
			    txtAddress.setText(entry.getLocalityAddress());
			}else{
			    txtAddress.setVisibility(View.GONE);
			    txtAddress.setText(entry.getLocalityAddress());
			}
			
		    }
		} else {
		    Toast.makeText(LocationUpdateActivity.this, "No Lat long available for selected location", Toast.LENGTH_SHORT).show();
		}

	    }
	});
    }

    // .search lcoation based on key
    public void searchLocality(final EditText editsearch) {
	// Capture Text in EditText
	editsearch.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void afterTextChanged(Editable arg0) {
		searchLocalityText = editsearch.getText().toString().toLowerCase(Locale.getDefault());

		 if (searchLocalityText != null && searchLocalityText.length() == 3) {
			easyTracker.send(MapBuilder.createEvent("Current Location", "Search Locality", searchLocalityText, null).build());
		    } 
		if (localitySearchList != null) {
		   /* if (localitySearchAdapter != null){
			viewVisibility("locality");
			localitySearchAdapter.filter(searchLocalityText);
		    }*/
		    viewVisibility("locality");
		    searchText(localitySearchList);
		}
	    }

	    @Override
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	    }

	    @Override
	    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	    }
	});
    }

    /* hide keyboard on tap of screen */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

	View v = getCurrentFocus();
	boolean ret = super.dispatchTouchEvent(event);

	if (v instanceof EditText) {
	    View w = getCurrentFocus();
	    int scrcoords[] = new int[2];
	    w.getLocationOnScreen(scrcoords);
	    float x = event.getRawX() + w.getLeft() - scrcoords[0];
	    float y = event.getRawY() + w.getTop() - scrcoords[1];

	    Log.d("Activity",
		    "Touch event " + event.getRawX() + "," + event.getRawY() + " " + x + "," + y + " rect " + w.getLeft() + "," + w.getTop() + ","
			    + w.getRight() + "," + w.getBottom() + " coords " + scrcoords[0] + "," + scrcoords[1]);
	    if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
	    }
	}
	return ret;
    }

    // API Call for city
    final class GetCityList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (AppConstants.LogLevel == 1) {
		preServerTimeSpend = Utils.preServerTimeSpend();
		Utils.startServerTime = System.currentTimeMillis();
	    }
	    pdia = new ProgressDialog(LocationUpdateActivity.this);
	    pdia.setCancelable(true);
	    pdia.setMessage(Utils.getCustomeFontStyle(LocationUpdateActivity.this, getString(R.string.screenLoading)));
	    pdia.setOnCancelListener(cancelListener);
	    pdia.show();
	}

	/*
	 * @Override protected void onCancelled() { // TODO Auto-generated
	 * method stub if(pdia!=null && pdia.isShowing()) pdia.dismiss();
	 * super.onCancelled();
	 * 
	 * }
	 */
	// 200793653151
	@Override
	protected String doInBackground(Void... arg0) {
	    isTrue = true;
	    JSONParser jparser = new JSONParser();
	    String result = jparser.getCityList();
	    if (AppConstants.LogLevel == 1) {
		serverTimeSpend = Utils.serverTimeSpend();
	    }
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	    try {
		if (pdia != null && pdia.isShowing())
		pdia.dismiss();
	    } catch (Exception e1) {
		e1.printStackTrace();
	    }

	    isTrue = false;
	    try {
		if (result != null) {
		    if (AppConstants.LogLevel == 1) {
			Utils.startPostServerCallTime = System.currentTimeMillis();
		    }
		    citySearchList = new ArrayList<SearchInfo>();

		    JSONObject jsonObject = new JSONObject(result.toString());
		    if (AppConstants.LogLevel == 1) {
			try {
			    ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		    JSONArray jsonarray = new JSONArray(jsonObject.getString("city"));
		    for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = jsonarray.getJSONObject(i);
			SearchInfo searchInfo = new SearchInfo();
			searchInfo.setSearchName(obj.getString("name"));
			searchInfo.setSearchId(obj.getString("id"));
			citySearchList.add(searchInfo);
		    }

		    if (citySearchList != null && citySearchList.size() > 0) {
			citySearchAdapter = new CitySearchAdapter(LocationUpdateActivity.this, R.layout.search_list_item, citySearchList);
			cityListView.setAdapter(citySearchAdapter);
		    }
		} else {
		    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
		 //   ((ScrollView) findViewById(R.id.scrollviewlayoutMiddle)).setVisibility(View.GONE);
		    ((LinearLayout) findViewById(R.id.layoutMiddle)).setVisibility(View.GONE);
		    ((TextView) findViewById(R.id.txtNoNetwork)).setText(getString(R.string.errorMesg_CityCancel));
		    ((TextView) findViewById(R.id.txtNoNetworkDesc)).setText(getString(R.string.errorMesg_CityCancelPlease));
		}
		if (AppConstants.LogLevel == 1) {
		    try {
			postServerTimepend = Utils.postServerTimeSpend();
			totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			Utils.callPerfomanceTestingApi("Get City List Data","LocationUpdateActivity","listcity",preServerTimeSpend,serverTimeSpend,
			    postServerTimepend,totalScreenTimeSpend,Utils.getNetworkType(LocationUpdateActivity.this),
			    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    } catch (JSONException e) {
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	//	((ScrollView) findViewById(R.id.scrollviewlayoutMiddle)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutMiddle)).setVisibility(View.GONE);
		((TextView) findViewById(R.id.txtNoNetwork)).setText(getString(R.string.errorMesg_CityCancel));
		((TextView) findViewById(R.id.txtNoNetworkDesc)).setText(getString(R.string.errorMesg_CityCancelPlease));
	    }

	}

    }

    /* listener for dialog cancel in thread */
    OnCancelListener cancelListener = new OnCancelListener() {
	@Override
	public void onCancel(DialogInterface arg0) {
	    isTrue = true;
	    localitySearchList.clear();
	    if (searchCity.getText().toString().trim().length() > 1) {
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	//	((ScrollView) findViewById(R.id.scrollviewlayoutMiddle)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutMiddle)).setVisibility(View.VISIBLE);
		viewVisibility("");
	    }else{
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	//	((ScrollView) findViewById(R.id.scrollviewlayoutMiddle)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutMiddle)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.txtNoNetwork)).setText(getString(R.string.errorMesg_CityCancel));
		((TextView)findViewById(R.id.txtNoNetworkDesc)).setText(getString(R.string.errorMesg_CityCancelPlease));
	    }
	}
    };

    /* API Call for Locality */

    final class GetLocalityList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
	    }
	    pdia = new ProgressDialog(LocationUpdateActivity.this);
	    pdia.setCancelable(true);
	    pdia.setMessage(Utils.getCustomeFontStyle(LocationUpdateActivity.this, getString(R.string.screenLoading)));
	    pdia.setOnCancelListener(cancelListener);
	    pdia.show();
	}

	// 200793653151
	@Override
	protected String doInBackground(Void... arg0) {
	    isTrue = true;
	    //ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY, "nodata");
	    JSONParser jparser = new JSONParser();
	    String result = jparser.getLocations(ApplicationSettings.getPref(AppConstants.CITY_SEARCH_ID, ""));
	    if (AppConstants.LogLevel == 1) {
		serverTimeSpend = Utils.serverTimeSpend();
	    }
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	    if (pdia != null && pdia.isShowing())
		pdia.dismiss();
	    isTrue = false;
	    try {
		if (result != null) {
		    localitySearchList.clear();
		    JSONObject jsonObject = new JSONObject(result.toString());
		    if (jsonObject.has("status")) {
			Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
			//finish();
		    } else {
			if (AppConstants.LogLevel == 1) {
			    try {
				ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}
			JSONArray jsonarray = new JSONArray(jsonObject.getString("location"));
			for (int i = 0; i < jsonarray.length(); i++) {
			    JSONObject obj = jsonarray.getJSONObject(i);
			    SearchInfo searchInfo = new SearchInfo();

			    if (obj.getString("lat").toString().trim() != null && !obj.getString("lat").toString().trim().equalsIgnoreCase("")
				    && !obj.getString("lat").toString().trim().equalsIgnoreCase("null")
				    && !obj.getString("lat").toString().trim().equalsIgnoreCase(null)
				    && obj.getString("lon").toString().trim() != null && !obj.getString("lon").toString().trim().equalsIgnoreCase("")
				    && !obj.getString("lon").toString().trim().equalsIgnoreCase("null")
				    && !obj.getString("lon").toString().trim().equalsIgnoreCase(null)) {
				searchInfo.setSearchName(obj.getString("name"));
				searchInfo.setSearchId(obj.getString("id"));
				searchInfo.setLatitude(obj.getString("lat"));
				searchInfo.setLongitude(obj.getString("lon"));
				searchInfo.setLocalityAddress(obj.getString("address").trim());
				localitySearchList.add(searchInfo);
			    }

			}

			if (localitySearchList != null && localitySearchList.size() > 0) {
			    isLocalityListPapulated = true;
			    setLocalityAdapter(localitySearchList);
			}
		    }
		    if (AppConstants.LogLevel == 1) {
			try {
			    Utils.callPerfomanceTestingApi("Get Locality List Data", "LocationUpdateActivity", "listlocation", preServerTimeSpend,
			    	serverTimeSpend, postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(LocationUpdateActivity.this),
			    	ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}
	    } catch (JSONException e) {
		e.printStackTrace();
	    }

	}

    }
    private void setLocalityAdapter(ArrayList<SearchInfo> localityArrayList){
	localitySearchAdapter = new LocalitySearchAdapter(LocationUpdateActivity.this, R.layout.search_locality_list_item,localityArrayList);
	localityListView.setAdapter(localitySearchAdapter);
    }
    private boolean keyBoardVisible() {
	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	if (imm.isAcceptingText()) {
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
	//googlAds();
	if (!isEventPressed) {
	    if (isHomePressed) {
		 easyTracker.send(MapBuilder.createEvent("Current Location", "Device Home", "Device Home", null).build());
		new CreateSessionIdAsyncTask().execute("0", "");
	    }
	}
    }

    @Override
    public void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("LocationUpdate Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
	super.onStop();
	  FlurryAgent.onEndSession(this);
	isHomePressed = true;
	EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    easyTracker.send(MapBuilder.createEvent("Current Location", "Device Back", "Device Back", null).build());
	    isEventPressed = true;

	    if (searchCity.getVisibility() == View.VISIBLE && searchLocality.getVisibility() == View.VISIBLE) {
	    	//finishActivites();
		LocationUpdateActivity.this.finish();
	    } else if (cityListView.getVisibility() == View.VISIBLE) {
	    	//finishActivites();
		LocationUpdateActivity.this.finish();
	    } else {
	    	searchLocality.setText("");
			viewVisibility("");
	    }
	    return true;
	}
	return false;

    }

    private void searchText(ArrayList<SearchInfo> arrListFilteredItems) {
	try {
	    ArrayList<SearchInfo> arrListFilter = new ArrayList<SearchInfo>();
	    if (!searchLocalityText.equals("")) {
		for (SearchInfo wp : arrListFilteredItems) {
		    if ((wp.getSearchName().toLowerCase(Locale.getDefault()).startsWith(searchLocalityText))) {
			arrListFilter.add(wp);
		    }
		}
		for (SearchInfo wp : arrListFilteredItems) {
		    if ((wp.getSearchName().toLowerCase(Locale.getDefault()).contains(searchLocalityText))) {
			arrListFilter.add(wp);
		    }
		}

		Object[] st = arrListFilter.toArray();
		for (Object s : st) {
		    if (arrListFilter.indexOf(s) != arrListFilter.lastIndexOf(s)) {
			arrListFilter.remove(arrListFilter.lastIndexOf(s));
		    }
		}
		setLocalityAdapter(arrListFilter);
	    } else {
		setLocalityAdapter(arrListFilteredItems);
	    }
	} catch (Exception e) {
	    e.getStackTrace();
	}
    }
    private void hideKeyboard() {
	// Check if no view has focus:
	try {
	    View view = this.getCurrentFocus();
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
 public static LocationUpdateActivity getInstance(){
    return locationUpdateScreen;
     
 }
}
