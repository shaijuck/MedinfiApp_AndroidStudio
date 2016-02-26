package com.medinfi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.medinfi.adapters.AutoCompleteAdapter;
import com.medinfi.adapters.MenuItemListAdapter;
import com.medinfi.datainfo.AutoCompleteInfo;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.SliderMenuClickListener;
import com.medinfi.utils.Utils;

public class HomeScreen extends Activity implements OnClickListener {

    // initialize views
    private ImageView generalPhysiciansButton;
    private ImageView specialistsButton;
    private ImageView hospitalsButton;
    private TextView gpTextView, spTextView, hospitalTextView;

    // private Typeface tfLight;
    private Typeface tfRegular;

    private LinearLayout gpLayout, spLayout, hospitalLayout;

    private TextView actionbarTitle, actionbarSubtitle;

    private EasyTracker easyTracker = null;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private ImageView imgBack;
    private static HomeScreen homeScreen;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
  //  private String[] menuTitles;
    private ArrayList<MenuItemInfo> menuItemInfo;
    private MenuItemListAdapter menuItemListAdapter;
    boolean isMenuOpened = false;
   /* private Integer[] menuItemIcon = { R.drawable.home_icon, R.drawable.favrate_icon, R.drawable.star_unselected, R.drawable.add_doc_hospital,

    };*/

    private EditText globalSearchEdtText;

    private ArrayList<AutoCompleteInfo> globalSearchList;
    private ListView globalListView;
    private AutoCompleteAdapter autoCompleteAdapter;
    private ScrollView scrollLayout;
    private LinearLayout hs_footer, layoutNoNetWork;
    private TextView txtNoResultFound;
    private String preServerTimeSpend = "";
    private String serverTimeSpend = "";
    private String postServerTimepend = "";
    private String totalScreenTimeSpend = "";
    //private InterstitialAd interstitial;
    private ImageView imgBannerAds;
   // private AdView adView;
    static boolean isTrue;
    Timer timer;
    TimerTask timerTask;

    // we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.homescreen);
	easyTracker = EasyTracker.getInstance(HomeScreen.this);
	isHomePressed = false;
	isEventPressed = false;
	homeScreen = this;

	// tfLight = Typeface.createFromAsset(this.getAssets(),
	// "fonts/RobotoLight.ttf");
	tfRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");

	actionbarTitle = (TextView) findViewById(R.id.title);
	actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
	imgBack = (ImageView) findViewById(R.id.imgBack);
	imgBack.setPadding(0, 0, 10, 0);
	imgBack.setImageResource(R.drawable.menu_icon_closed);

	showLocation();
	gpLayout = (LinearLayout) findViewById(R.id.gp_layout);
	spLayout = (LinearLayout) findViewById(R.id.sp_layout);
	hospitalLayout = (LinearLayout) findViewById(R.id.hospital_layout);
	gpLayout.setOnClickListener(this);
	spLayout.setOnClickListener(this);
	hospitalLayout.setOnClickListener(this);
	gpTextView = (TextView) findViewById(R.id.gp_tv);
	spTextView = (TextView) findViewById(R.id.sp_tv);
	hospitalTextView = (TextView) findViewById(R.id.hospital_tv);
	gpTextView.setTypeface(tfRegular);
	hospitalTextView.setTypeface(tfRegular);
	spTextView.setTypeface(tfRegular);

	globalSearchEdtText = (EditText) findViewById(R.id.globalSearchEdtText);
	globalSearchEdtText.setTypeface(tfRegular);
	globalListView = (ListView) findViewById(R.id.globalListView);
	globalSearchList = new ArrayList<AutoCompleteInfo>();
	((TextView) findViewById(R.id.txtNoComission)).setTypeface(tfRegular);

	generalPhysiciansButton = (ImageView) findViewById(R.id.btn_general_physicians);
	specialistsButton = (ImageView) findViewById(R.id.btn_specialists);
	hospitalsButton = (ImageView) findViewById(R.id.btn_hospitals);
	generalPhysiciansButton.setOnClickListener(this);
	specialistsButton.setOnClickListener(this);
	hospitalsButton.setOnClickListener(this);
	scrollLayout = (ScrollView) findViewById(R.id.scrollLayout);
	hs_footer = (LinearLayout) findViewById(R.id.hs_footer);
	layoutNoNetWork = (LinearLayout) findViewById(R.id.layoutNoNetWork);
	txtNoResultFound = (TextView) findViewById(R.id.txtNoResultFound);
	imgBannerAds = (ImageView) findViewById(R.id.imgBannerAds);
	//adView = (AdView) this.findViewById(R.id.adView);
         
	locationClickEvent();
	backClickEvent();
	globalSearch(globalSearchEdtText);
	onClickGlobalSearch();
	globalSearchListViewClickEvent();
	Utils.startSuggestionActivity(this, txtNoResultFound, "HomeScreen", easyTracker);

	//Appirater.appLaunched(HomeScreen.this);
    }

   /* private void googlAds() {
	interstitial = new InterstitialAd(HomeScreen.this);
	interstitial.setAdUnitId(getString(R.string.googleAdsId));
	AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("").build();
	adView.loadAd(adRequest);
	interstitial.loadAd(adRequest);
	interstitial.setAdListener(new AdListener() {
	    public void onAdLoaded() {
		displayInterstitial();
	    }
	});
    }

    public void displayInterstitial() {
	if (interstitial.isLoaded()) {
	    interstitial.show();
	}
    }*/

    private boolean validateCity() {
	boolean istrue = false;
	String city = ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, "");
	if (city.equalsIgnoreCase("Delhi") || city.equalsIgnoreCase("New Delhi") ||city.equalsIgnoreCase("Greater Noida")
		|| city.equalsIgnoreCase("Noida") || city.equalsIgnoreCase("Faridabad") || city.equalsIgnoreCase("Gurgaon")
		|| city.equalsIgnoreCase("Delhi NCR")) {
	    istrue = true;
	}
	return istrue;
    }

  /*  private void showAdsBasedOnCity() {
	if (validateCity()) {
	    startTimer();
	} else {
	  //  imgBannerAds.setVisibility(View.GONE);
	    adView.setVisibility(View.VISIBLE);
	    googlAds();
	}
	
	 adView.setVisibility(View.VISIBLE);
	 googlAds();
    }*/

   /* public void startTimer() {
	timer = new Timer();
	initializeTimerTask();
	timer.schedule(timerTask, 0, 30000); 
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
			// get the current timeStamp
			if (isTrue) {
			    isTrue = false;
			    adView.setVisibility(View.GONE);
			 //   imgBannerAds.setVisibility(View.VISIBLE);
			} else {
			    isTrue = true;
			 //   imgBannerAds.setVisibility(View.GONE);
			    adView.setVisibility(View.VISIBLE);
			    googlAds();
			}
		    }
		});
	    }
	};
    }*/
    public void onClickBannerAds(View view){
	easyTracker.send(MapBuilder.createEvent("Home", "One MG", "One MG", null).build());
	String url = AppConstants.ONEMG_BANNER_ADS;
	Intent i = new Intent(Intent.ACTION_VIEW);
	i.setData(Uri.parse(url));
	startActivity(i);
    }
    private void menuItems() {
	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
	
	int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
	DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mDrawerList.getLayoutParams();
	params.width = width;
	mDrawerList.setLayoutParams(params);
	
	menuItemInfo = Utils.getMenuListItem(this);
	
	mDrawerList.setOnItemClickListener(new SliderMenuClickListener(this, mDrawerLayout, mDrawerList, menuItemInfo, menuItemListAdapter, imgBack,
		"MenuList"));
	menuItemListAdapter = new MenuItemListAdapter(getApplicationContext(), menuItemInfo);
	mDrawerList.setAdapter(menuItemListAdapter);

	ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.app_logo, R.string.app_name,
		R.string.app_name) {
	    public void onDrawerClosed(View view) {
		// calling onPrepareOptionsMenu() to show action bar icons
		isMenuOpened = false;
		imgBack.setImageResource(R.drawable.menu_icon_closed);
	    }

	    public void onDrawerOpened(View drawerView) {
		// calling onPrepareOptionsMenu() to hide action bar icons
		isMenuOpened = true;
		imgBack.setImageResource(R.drawable.menu_icon_open);
	    }
	};
	mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void backClickEvent() {
	LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
	layoutBack.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Home", "Menu", "Menu", null).build());
		if (!isMenuOpened) {
		    isMenuOpened = true;
		    mDrawerLayout.openDrawer(mDrawerList);
		    imgBack.setImageResource(R.drawable.menu_icon_open);
		} else {
		    isMenuOpened = false;
		    mDrawerLayout.closeDrawer(mDrawerList);
		    imgBack.setImageResource(R.drawable.menu_icon_closed);
		}

	    }
	});
    }

    /* click events for buttons and views */
    @Override
    public void onClick(View v) {
	isEventPressed = true;
	switch (v.getId()) {
	case R.id.btn_general_physicians:

	    easyTracker.send(MapBuilder.createEvent("Home", "Select General Physician", "Select General Physician", null).build());

	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
	    // generalPhysiciansButton.setBackgroundResource(R.drawable.gp_icon_selected);
	    gpTextView.setTextColor(getResources().getColor(R.color.pink_color));
	    if (GeneralPhysicansActivity.getInstance() != null) {
		GeneralPhysicansActivity.getInstance().finish();
	    }
	    startActivity(new Intent(HomeScreen.this, GeneralPhysicansActivity.class));
	    break;

	case R.id.btn_specialists:

	    easyTracker.send(MapBuilder.createEvent("Home", "Select Specialist Doctors", "Select Specialist Doctors", null).build());
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
	    // specialistsButton.setBackgroundResource(R.drawable.specialist_icon_selected);
	    spTextView.setTextColor(getResources().getColor(R.color.pink_color));
	    if (SpecialistActivity.getInstance() != null) {
		SpecialistActivity.getInstance().finish();
	    }
	    startActivity(new Intent(HomeScreen.this, SpecialistActivity.class));
	    break;
	case R.id.btn_hospitals:
	    easyTracker.send(MapBuilder.createEvent("Home", "Select Hospitals", "Select Hospitals", null).build());
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
	    // hospitalsButton.setBackgroundResource(R.drawable.hospital_icon_selected);
	    hospitalTextView.setTextColor(getResources().getColor(R.color.pink_color));
	    if (HospitalsActivity.getInstance() != null) {
		HospitalsActivity.getInstance().finish();
	    }
	    startActivity(new Intent(HomeScreen.this, HospitalsActivity.class));
	    break;
	case R.id.gp_layout:

	    easyTracker.send(MapBuilder.createEvent("Home", "Select General Physician", "Select General Physician", null).build());

	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
	    // generalPhysiciansButton.setBackgroundResource(R.drawable.gp_icon_selected);
	    gpTextView.setTextColor(getResources().getColor(R.color.pink_color));
	    if (GeneralPhysicansActivity.getInstance() != null) {
		GeneralPhysicansActivity.getInstance().finish();
	    }
	    startActivity(new Intent(HomeScreen.this, GeneralPhysicansActivity.class));
	    break;

	case R.id.sp_layout:
	    easyTracker.send(MapBuilder.createEvent("Home", "Select Specialist Doctors", "Select Specialist Doctors", null).build());
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
	    // specialistsButton.setBackgroundResource(R.drawable.specialist_icon_selected);
	    spTextView.setTextColor(getResources().getColor(R.color.pink_color));
	    if (SpecialistActivity.getInstance() != null) {
		SpecialistActivity.getInstance().finish();
	    }
	    startActivity(new Intent(HomeScreen.this, SpecialistActivity.class));
	    break;
	case R.id.hospital_layout:

	    easyTracker.send(MapBuilder.createEvent("Home", "Select Hospitals", "Select Hospitals", null).build());
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
	    // hospitalsButton.setBackgroundResource(R.drawable.hospital_icon_selected);
	    hospitalTextView.setTextColor(getResources().getColor(R.color.pink_color));
	    if (HospitalsActivity.getInstance() != null) {
		HospitalsActivity.getInstance().finish();
	    }
	    startActivity(new Intent(HomeScreen.this, HospitalsActivity.class));
	    break;
	default:
	    break;
	}
    }

    /*
     * get the data from preferences use spannable string to show location in
     * action bar add custom color, fonts to it
     */
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

    private void locationClickEvent() {
	LinearLayout layoutLocation = (LinearLayout) findViewById(R.id.layoutLocation);
	layoutLocation.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Home", "Update Location", "Update Location", null).build());
		startLocationUpdate();
	    }
	});

    }

    private void startLocationUpdate() {
	Intent intent = new Intent(HomeScreen.this, LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
    }

    @Override
    protected void onResume() {
	super.onResume();
	menuItems();
	/*if(validateCity()){
            imgBannerAds.setVisibility(View.VISIBLE);
        }else{
            imgBannerAds.setVisibility(View.GONE);
        }*/
	//showAdsBasedOnCity();
	/*
	 * if (scrollLayout.getVisibility() == View.GONE) {
	 * setHomeScreenVisible(); }
	 */
	// mDrawerLayout.closeDrawer(mDrawerList);
	if (!isEventPressed) {
	    if (isHomePressed) {
		easyTracker.send(MapBuilder.createEvent("Home", "Device Home", "Device Home", null).build());
		
	    }
	  //  if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	}
	// generalPhysiciansButton.setBackgroundResource(R.drawable.gp_icon_normal);
	gpTextView.setTextColor(getResources().getColor(android.R.color.black));
	// specialistsButton.setBackgroundResource(R.drawable.specialist_icon_normal);
	spTextView.setTextColor(getResources().getColor(android.R.color.black));
	// hospitalsButton.setBackgroundResource(R.drawable.hospital_icon_normal);
	hospitalTextView.setTextColor(getResources().getColor(android.R.color.black));
    }

    @Override
    public void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("HomeScreen Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onPause() {
	super.onPause();
    }

    @Override
    public void onStop() {
	super.onStop();
	 FlurryAgent.onEndSession(this);
	/*
	 * if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerList))
	 * { isMenuOpened = false; mDrawerLayout.closeDrawer(mDrawerList);
	 * imgBack.setImageResource(R.drawable.menu_icon_closed); }
	 */
	isHomePressed = true;
	EasyTracker.getInstance(this).activityStop(this);
	//stoptimertask();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    easyTracker.send(MapBuilder.createEvent("Home", "Device Back", "Device Back", null).build());

	    isEventPressed = true;

	    if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerList)) {
		isMenuOpened = false;
		mDrawerLayout.closeDrawer(mDrawerList);
		imgBack.setImageResource(R.drawable.menu_icon_closed);
	    } else if (scrollLayout.getVisibility() == View.GONE) {
		setHomeScreenVisible();
	    } else {
		try {
		    if (SplashActivity.getInstance() != null) {
		        SplashActivity.getInstance().finish();
		    }if (HomeScreen.getInstance() != null) {
		        HomeScreen.getInstance().finish();
		    }
		    finish();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	}
	return false;
    }

    public static HomeScreen getInstance() {
	return homeScreen;
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
		hs_footer.setVisibility(View.GONE);
		scrollLayout.setVisibility(View.GONE);
		txtNoResultFound.setVisibility(View.GONE);
		if (globalSearchText != null && globalSearchText.length() < 2) {
		    easyTracker.send(MapBuilder.createEvent("Global Search", "Global Search", "Global Search", null).build());
		}
		if (Utils.isNetworkAvailable(HomeScreen.this)) {
		    // globalSearchList.clear();
		    globalListView.setVisibility(View.VISIBLE);
		    layoutNoNetWork.setVisibility(View.GONE);
		    if (globalSearchText != null && globalSearchText.length() > 2) {
			 if (AppConstants.LogLevel == 1) {
				Utils.startTotalScreenTime = System.currentTimeMillis();
				Utils.startPreServerCallTime = System.currentTimeMillis();
			    }
			new GetGlobalSearchList().execute(globalSearchText);
		    }
		} else {
		    globalListView.setVisibility(View.GONE);
		    layoutNoNetWork.setVisibility(View.VISIBLE);
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

    private void onClickGlobalSearch() {
	globalSearchEdtText.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		try {
		    //easyTracker.send(MapBuilder.createEvent("Home", "GlobalSearch", "GlobalSearch", null).build());
		    hs_footer.setVisibility(View.GONE);
		    scrollLayout.setVisibility(View.GONE);
		    txtNoResultFound.setVisibility(View.GONE);
		    if (Utils.isNetworkAvailable(HomeScreen.this)) {
			globalListView.setVisibility(View.VISIBLE);
			layoutNoNetWork.setVisibility(View.GONE);
		    } else {
			globalListView.setVisibility(View.GONE);
			layoutNoNetWork.setVisibility(View.VISIBLE);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}

	    }
	});
    }

    private void hideKeyboard() {
	try {
	    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	} catch (Exception e) {
	    e.printStackTrace();
	}
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

	}

	@Override
	protected String doInBackground(String... arg0) {
	    String result = null;
	    JSONParser jparser = new JSONParser();
	    try {
		result = jparser.getGlobalSearchList(arg0[0], "20", ApplicationSettings.getPref(AppConstants.LATITUDE, ""),
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
			hs_footer.setVisibility(View.GONE);
			scrollLayout.setVisibility(View.GONE);
			layoutNoNetWork.setVisibility(View.GONE);
			txtNoResultFound.setVisibility(View.VISIBLE);
			// ((TextView)
			// findViewById(R.id.txtNoNetwork)).setText("");
			// ((TextView)
			// findViewById(R.id.txtNoNetworkDesc)).setText("No Result Found");
			// ((TextView)
			// findViewById(R.id.txtNoNetworkDesc)).setTextColor(Color.BLACK);;
		    } else {
			JSONArray jsonarray = new JSONArray(jsonObject.getString("GlobalSearch"));
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
			    globalListView.setVisibility(View.VISIBLE);
			    hs_footer.setVisibility(View.GONE);
			    scrollLayout.setVisibility(View.GONE);
			    txtNoResultFound.setVisibility(View.GONE);
			    autoCompleteAdapter = new AutoCompleteAdapter(HomeScreen.this, R.layout.autocompletesearch, globalSearchList,"");
			    globalListView.setAdapter(autoCompleteAdapter);

			} else {
			    globalListView.setVisibility(View.GONE);
			    hs_footer.setVisibility(View.GONE);
			    scrollLayout.setVisibility(View.GONE);
			    layoutNoNetWork.setVisibility(View.GONE);
			    txtNoResultFound.setVisibility(View.VISIBLE);
			}

		    }

		}
		if (AppConstants.LogLevel == 1) {
		    try {
			postServerTimepend = Utils.postServerTimeSpend();
			totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			Utils.callPerfomanceTestingApi("GlobalSearch","HomeScreen","ListUserGlobalSearch",preServerTimeSpend,serverTimeSpend,
			    postServerTimepend,totalScreenTimeSpend,Utils.getNetworkType(HomeScreen.this),
			    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    } catch (JSONException e) {
		globalListView.setVisibility(View.GONE);
		hs_footer.setVisibility(View.GONE);
		scrollLayout.setVisibility(View.GONE);
		layoutNoNetWork.setVisibility(View.VISIBLE);
		txtNoResultFound.setVisibility(View.GONE);
	    }

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
			easyTracker.send(MapBuilder.createEvent("Global Search", " View Doctor", entry.getName(), null).build());
			ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getId());
			Intent intent = new Intent(HomeScreen.this, DoctorDetailsActivity.class);
			intent.putExtra("CALLING_SCREEN", "HomeScreen");
			startActivity(intent);
		    } else {
			easyTracker.send(MapBuilder.createEvent("GlobalSearch", "View Hospital", entry.getName(), null).build());
			ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getId());
			Intent intent = new Intent(HomeScreen.this, HospitalDetailsActivity.class);
			intent.putExtra("CALLING_SCREEN", "HomeScreen");
			startActivity(intent);
		    }

		}
	    }
	});
    }

    public void setHomeScreenVisible() {
	try {
	    hideKeyboard();
	    globalSearchEdtText.setText("");
	    globalListView.setVisibility(View.GONE);
	    layoutNoNetWork.setVisibility(View.GONE);
	    txtNoResultFound.setVisibility(View.GONE);
	    scrollLayout.setVisibility(View.VISIBLE);
	    hs_footer.setVisibility(View.VISIBLE);
	    globalSearchList.clear();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    OnCancelListener cancelListener = new OnCancelListener() {
	@Override
	public void onCancel(DialogInterface arg0) {
	    // globalSearchEdtText.setText("");
	    /*
	     * System.out.println("cancelListener ..........."); if
	     * (globalSearchList != null && globalSearchList.size() > 0) {
	     * System
	     * .out.println("cancelListener ...........if "+globalSearchList
	     * .size()); } globalListView.setVisibility(View.GONE);
	     * ((ScrollView)
	     * findViewById(R.id.scrollLayout)).setVisibility(View.VISIBLE);
	     * ((LinearLayout)
	     * findViewById(R.id.hs_footer)).setVisibility(View.VISIBLE);
	     * ((LinearLayout)
	     * findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	     */
	}
    };

    /* hide keyboard on tap of screen */
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
}
