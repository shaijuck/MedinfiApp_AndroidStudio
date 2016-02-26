package com.medinfi;

/**
 * 
 * @author Lalbabu
 * handle favourite Doctors and Hospitals
 */
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.adapters.FavouriteAdapter;
import com.medinfi.adapters.MenuItemListAdapter;
import com.medinfi.datainfo.FavouriteInfo;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.SliderMenuClickListener;
import com.medinfi.utils.Utils;

public class FavouriteActivity extends Activity {
    private ListView favouriteListView;
    private EasyTracker easyTracker = null;
    private ProgressDialog pdia;
    private ArrayList<FavouriteInfo> favouriteList;
    private FavouriteAdapter favouriteAdapter;
    private TextView actionbarTitle, actionbarSubtitle;
    private static boolean isEventPressed = false;
    private boolean isHomePressed = false;
    private static FavouriteActivity favouriteScreen;
    
    private ImageView imgBack;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<MenuItemInfo> menuItemInfo;
    private MenuItemListAdapter menuItemListAdapter;
    boolean isMenuOpened = false;
    
    private String preServerTimeSpend="";
    private String serverTimeSpend="";
    private String postServerTimepend="";
    private String totalScreenTimeSpend="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.favourite_layout);
	easyTracker = EasyTracker.getInstance(FavouriteActivity.this);
	favouriteScreen = this;
	isEventPressed = false;
	isHomePressed = false;
	if (AppConstants.LogLevel == 1) {
	    Utils.startTotalScreenTime = System.currentTimeMillis();
	    Utils.startPreServerCallTime = System.currentTimeMillis();
	}
	initialize();
	showLocation();

	chechNetworkConnectivity();

	locationClickEvent();
	listViewClickEvent();
	backClickEvent();
    }

    private void chechNetworkConnectivity() {
	try {
	    if (Utils.isNetworkAvailable(this)) {
		favouriteListView.setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
		favouriteList = new ArrayList<FavouriteInfo>();
		new GetFavouriteList().execute();
	    } else {
		favouriteListView.setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void initialize() {
	try {
	    favouriteListView = (ListView) findViewById(R.id.favouriteListView);
	    actionbarTitle = (TextView) findViewById(R.id.title);
	    actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
	    
//	    imgBack = (ImageView) findViewById(R.id.imgBack);
//	    imgBack.setPadding(0, 0, 10, 0);
//	    imgBack.setImageResource(R.drawable.menu_icon_closed);
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    public void onContactSyncupClickEvent(View view){
	isEventPressed = true;
	easyTracker.send(MapBuilder.createEvent("Saves", "Sync-Up", "Sync-Up", null).build());
	Intent intent = new Intent(FavouriteActivity.this,ContactSyncUpActivity.class);
	startActivity(intent);
	finish();
    }
    private void listViewClickEvent() {
	favouriteListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FavouriteInfo entry = (FavouriteInfo) parent.getItemAtPosition(position);
		if (parent != null && entry != null) {
		    isEventPressed = true; 
		    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
		    AppConstants.isSaveUnSaveClicked = false;
		    if (entry.getFavType().equalsIgnoreCase("Doctor")) {
			easyTracker.send(MapBuilder.createEvent("Saves", "View Doctor", entry.getFavName(), null).build());
			ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getFavId());
			Intent intent = new Intent(FavouriteActivity.this, DoctorDetailsActivity.class);
			intent.putExtra("CALLING_SCREEN", "Favourite");
			startActivity(intent);
		    } else {
			easyTracker.send(MapBuilder.createEvent("Saves", "View Hospital", entry.getFavName(), null).build());
			ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getFavId());
			Intent intent = new Intent(FavouriteActivity.this, HospitalDetailsActivity.class);
			intent.putExtra("CALLING_SCREEN", "Favourite");
			startActivity(intent);
		    }

		}
	    }
	});
    }
    private void backClickEvent(){
         LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
         layoutBack.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
	    	easyTracker.send(MapBuilder.createEvent("Saves", "Back", "Back", null).build());
			FavouriteActivity.this.finish();
//		if (!isMenuOpened) {
//		    isMenuOpened = true;
//		    mDrawerLayout.openDrawer(mDrawerList);
//		    imgBack.setImageResource(R.drawable.menu_icon_open);
//		} else {
//		    isMenuOpened = false;
//		    mDrawerLayout.closeDrawer(mDrawerList);
//		    imgBack.setImageResource(R.drawable.menu_icon_closed);
//		}
	    }
	});
    }
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
		isEventPressed = true;
		easyTracker.send(MapBuilder.createEvent("Saves", "Update Location", "Update Location", null).build());
		startLocationUpdate();
	    }
	});
    }

    private void startLocationUpdate() {
	Intent intent = new Intent(FavouriteActivity.this, LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
    }

    @Override
    protected void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("Favourite Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onResume() {
	super.onResume();
//	try {
//	    menuItems();
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}
	if (!isEventPressed) {
	    if (isHomePressed) {
		easyTracker.send(MapBuilder.createEvent("Saves", "Device Home", "Device Home", null).build());
	    }
	  //  if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	}
    }

    @Override
    protected void onPause() {
	super.onPause();
    }

    @Override
    protected void onStop() {
	super.onStop();
	  FlurryAgent.onEndSession(this);
	isHomePressed = true;
	EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    /* back button called */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    isEventPressed = true;
	    easyTracker.send(MapBuilder.createEvent("Saves", "Device Back", "Device Back", null).build());
	    if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerList)) {
		isMenuOpened = false;
		mDrawerLayout.closeDrawer(mDrawerList);
		imgBack.setImageResource(R.drawable.menu_icon_closed);
	    } else {
		 FavouriteActivity.this.finish();
	    }
	    return true;
	}
	return false;
    }

    public class GetFavouriteList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
		preServerTimeSpend = Utils.preServerTimeSpend();
	    }
	    pdia = new ProgressDialog(FavouriteActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(FavouriteActivity.this, getString(R.string.screenLoading)));
	    pdia.show();
	}

	@Override
	protected String doInBackground(Void... arg0) {
	    String result = null;
	    JSONParser jparser = new JSONParser();
	    try {

	        result = jparser.getFavouriteList();
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
	    if (pdia != null && pdia.isShowing())
		pdia.dismiss();
	    try {
		if (result != null) {
		   
		    favouriteList.clear();
		    JSONObject jsonObject = new JSONObject(result.toString());

		    JSONArray jsonarray = new JSONArray(jsonObject.getString("Favourites"));
		    if (AppConstants.LogLevel == 1) {
			try {
			    ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		    for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = jsonarray.getJSONObject(i);
			FavouriteInfo favouriteInfo = new FavouriteInfo();
			if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {

			    favouriteInfo.setFavId(obj.getString("id"));
			    String favouriteName;
			    if (obj.getString("name").toString().contains(",")) {

				favouriteName = obj.getString("name").toString().split(",")[0];
			    } else {
				favouriteName = obj.getString("name").toString();
			    }
			    favouriteInfo.setFavName(favouriteName.replace("?", ""));
			    if (obj.getString("address") != null && !obj.getString("address").equalsIgnoreCase("")
				    && !obj.getString("address").equalsIgnoreCase("null")) {
				favouriteInfo.setFavAddress(obj.getString("address"));
			    }

			    if (obj.getString("primaryphone") != null && !obj.getString("primaryphone").equalsIgnoreCase("")
				    && !obj.getString("primaryphone").equalsIgnoreCase("null")) {
				favouriteInfo.setFavPrimaryphone(obj.getString("primaryphone"));
			    }

			    if (obj.has("locality") && obj.getString("locality") != null && !obj.getString("locality").equalsIgnoreCase("")
				    && !obj.getString("locality").equalsIgnoreCase(null)) {
				favouriteInfo.setFavLocality(obj.getString("locality").replace("?", ""));
			    }

			    if (obj.has("type") && obj.getString("type") != null && !obj.getString("type").equalsIgnoreCase("")
				    && !obj.getString("type").equalsIgnoreCase(null)) {
				favouriteInfo.setFavType(obj.getString("type").replace("?", ""));
			    }
			    if (obj.has("lat") && obj.getString("lat") != null && !obj.getString("lat").equalsIgnoreCase("")
				    && !obj.getString("lat").equalsIgnoreCase(null)) {
				favouriteInfo.setFavLatitude(obj.getString("lat").replace("?", ""));
			    }
			    if (obj.has("lon") && obj.getString("lon") != null && !obj.getString("lon").equalsIgnoreCase("")
				    && !obj.getString("lon").equalsIgnoreCase(null)) {
				favouriteInfo.setFavLongitude(obj.getString("lon").replace("?", ""));
			    }
			    if (obj.has("doctype") && obj.getString("doctype") != null && !obj.getString("doctype").equalsIgnoreCase("")
				    && !obj.getString("doctype").equalsIgnoreCase(null)) {
				favouriteInfo.setFavDoctype(obj.getString("doctype").replace("?", ""));
			    }
			    if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
				    && !obj.getString("avgrate").equalsIgnoreCase(null) && !obj.getString("avgrate").equalsIgnoreCase("null")) {
				favouriteInfo.setFavRating(obj.getString("avgrate"));
			    }
			    if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").trim().equalsIgnoreCase("")
				    && !obj.getString("pic").trim().equalsIgnoreCase(null) && !obj.getString("pic").trim().equalsIgnoreCase("null")) {
				favouriteInfo.setFavPic(obj.getString("pic").trim());
			    }
			    if (obj.has("second_image") && obj.getString("second_image") != null
				    && !obj.getString("second_image").trim().equalsIgnoreCase("")
				    && !obj.getString("second_image").trim().equalsIgnoreCase(null)) {
				favouriteInfo.setFavSecondPic(obj.getString("second_image").trim());
			    }

			    if (obj.has("emergencyPic") && obj.getString("emergencyPic") != null
				    && !obj.getString("emergencyPic").equalsIgnoreCase("")
				    && !obj.getString("emergencyPic").equalsIgnoreCase(null)) {
				favouriteInfo.setHospitalEmergencyPic(obj.getString("emergencyPic"));
			    }
			    if (obj.has("leftPic") && obj.getString("leftPic") != null
				    && !obj.getString("leftPic").equalsIgnoreCase("")
				    && !obj.getString("leftPic").equalsIgnoreCase(null)) {
				favouriteInfo.setHospitalLeftPic(obj.getString("leftPic"));
			    }
			    if (obj.has("rightPic") && obj.getString("rightPic") != null
				    && !obj.getString("rightPic").equalsIgnoreCase("")
				    && !obj.getString("rightPic").equalsIgnoreCase(null)) {
				favouriteInfo.setHospitalRightPic(obj.getString("rightPic"));
			    }
			    if (obj.has("distance") && obj.getString("distance") != null
					    && !obj.getString("distance").equalsIgnoreCase("")
					    && !obj.getString("distance").equalsIgnoreCase(null)) {
					favouriteInfo.setDistance(obj.getString("distance"));
				    }
			    
			    favouriteList.add(favouriteInfo);
			}
		    }

		    if (favouriteList != null && favouriteList.size() > 0) {

			favouriteAdapter = new FavouriteAdapter(FavouriteActivity.this, R.layout.favourite_list_data, favouriteList);
			favouriteListView.setAdapter(favouriteAdapter);

		    } else {
			favouriteListView.setVisibility(View.GONE);
			((TextView)findViewById(R.id.txtNoRecordFound)).setVisibility(View.VISIBLE);
		    }

		}

	    } catch (JSONException e) {
		favouriteListView.setVisibility(View.GONE);
		((TextView)findViewById(R.id.txtNoRecordFound)).setVisibility(View.VISIBLE);
	    }
	    if (AppConstants.LogLevel == 1) {
		try {
		    postServerTimepend = Utils.postServerTimeSpend();
		    totalScreenTimeSpend = Utils.totalScreenTimeSpend();
		    Utils.callPerfomanceTestingApi("Saved List", "FavouriteActivity", "ListUserFavourites", preServerTimeSpend, serverTimeSpend,
		    	postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(FavouriteActivity.this),
		    	ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}

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
   		"FavouriteMenu"));
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
    OnCancelListener cancelListener = new OnCancelListener() {
	@Override
	public void onCancel(DialogInterface arg0) {
	    FavouriteActivity.this.finish();
	}
    };
    
    public static FavouriteActivity getInstance(){
	return favouriteScreen;
    }
}
