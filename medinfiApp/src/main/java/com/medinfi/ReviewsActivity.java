package com.medinfi;

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
import com.medinfi.adapters.MenuItemListAdapter;
import com.medinfi.adapters.ReviewsMenuAdapter;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.datainfo.ReviewsInfo;
import com.medinfi.datainfo.ReviewsMenuInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.SliderMenuClickListener;
import com.medinfi.utils.Utils;

public class ReviewsActivity extends Activity {
    private ListView reviewsListView;
    private EasyTracker easyTracker = null;
    private ProgressDialog pdia;
    private ArrayList<ReviewsMenuInfo> reviewsMenuList;
    private ArrayList<ReviewsInfo> reviewArrayList;
    private ReviewsMenuAdapter reviewsMenuAdapter;
    private TextView actionbarTitle, actionbarSubtitle;
    private static boolean isEventPressed = false;
    private boolean isHomePressed = false;
    private static ReviewsActivity reviewsScreen;

    private ImageView imgBack;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<MenuItemInfo> menuItemInfo;
    private MenuItemListAdapter menuItemListAdapter;
    boolean isMenuOpened = false;

    private String preServerTimeSpend = "";
    private String serverTimeSpend = "";
    private String postServerTimepend = "";
    private String totalScreenTimeSpend = "";
    private TextView txtReviewsHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.reviews_layout);
	easyTracker = EasyTracker.getInstance(ReviewsActivity.this);
	reviewsScreen = this;
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

    private void initialize() {
	try {
	    reviewsListView = (ListView) findViewById(R.id.reviewsListView);
	    actionbarTitle = (TextView) findViewById(R.id.title);
	    actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
	    txtReviewsHeader = (TextView) findViewById(R.id.txtReviewsHeader);

//	    imgBack = (ImageView) findViewById(R.id.imgBack);
//	    imgBack.setPadding(0, 0, 10, 0);
//	    imgBack.setImageResource(R.drawable.menu_icon_closed);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void chechNetworkConnectivity() {
	try {
	    if (Utils.isNetworkAvailable(this)) {
		reviewsListView.setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
		reviewsMenuList = new ArrayList<ReviewsMenuInfo>();
		 new GetReviewsList().execute();
	    } else {
		reviewsListView.setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void listViewClickEvent() {
	reviewsListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ReviewsMenuInfo entry = (ReviewsMenuInfo) parent.getItemAtPosition(position);
		if (parent != null && entry != null) {
		    isEventPressed = true;
		    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
		    if (entry.getReviewsType().equalsIgnoreCase("Doctor")) {
			// easyTracker.send(MapBuilder.createEvent("Saves",
			// "View Doctor", entry.getFavName(), null).build());
			ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getReviewsId());
			Intent intent = new Intent(ReviewsActivity.this, DoctorDetailsActivity.class);
			intent.putExtra("CALLING_SCREEN", "ReviewsMenuList");
			startActivity(intent);
		    } else {
			// easyTracker.send(MapBuilder.createEvent("Saves",
			// "View Hospital", entry.getFavName(), null).build());
			ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getReviewsId());
			Intent intent = new Intent(ReviewsActivity.this, HospitalDetailsActivity.class);
			intent.putExtra("CALLING_SCREEN", "ReviewsMenuList");
			startActivity(intent);
		    }

		}
	    }
	});
    }

    private void backClickEvent() {
	LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
	layoutBack.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
	    	easyTracker.send(MapBuilder.createEvent("Reviews", "Back", "Back", null).build());
			ReviewsActivity.this.finish();
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
		// easyTracker.send(MapBuilder.createEvent("Saves",
		// "Update Location", "Update Location", null).build());
		startLocationUpdate();
	    }
	});
    }

    private void startLocationUpdate() {
	Intent intent = new Intent(ReviewsActivity.this, LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
    }

    public class GetReviewsList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
		preServerTimeSpend = Utils.preServerTimeSpend();
	    }
	    pdia = new ProgressDialog(ReviewsActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(ReviewsActivity.this, getString(R.string.screenLoading)));
	    pdia.show();
	}

	@Override
	protected String doInBackground(Void... arg0) {
	    String result = null;
	    JSONParser jparser = new JSONParser();
	    try {

		result = jparser.getReviewMenuList();
		if (AppConstants.LogLevel == 1) {
		    serverTimeSpend = Utils.serverTimeSpend();
		    Utils.startPostServerCallTime = System.currentTimeMillis();
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	    if (pdia != null && pdia.isShowing())
		pdia.dismiss();
	    try {
		if (result != null) {

		    reviewsMenuList.clear();
		    JSONObject jsonObject = new JSONObject(result.toString());

		    JSONArray jsonarray = new JSONArray(jsonObject.getString("Reviews"));
		    if (AppConstants.LogLevel == 1) {
			try {
			    ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		    ReviewsMenuInfo reviewsMenuInfo = null;
		    for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = jsonarray.getJSONObject(i);
			reviewsMenuInfo = new ReviewsMenuInfo();
			if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {

			    reviewsMenuInfo.setReviewsId(obj.getString("id"));
			    String favouriteName;
			    if (obj.getString("name").toString().contains(",")) {

				favouriteName = obj.getString("name").toString().split(",")[0];
			    } else {
				favouriteName = obj.getString("name").toString();
			    }
			    reviewsMenuInfo.setReviewsName(favouriteName.replace("?", ""));
			    if (obj.getString("address") != null && !obj.getString("address").equalsIgnoreCase("")
				    && !obj.getString("address").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsAddress(obj.getString("address"));
			    }

			    if (obj.getString("primaryphone") != null && !obj.getString("primaryphone").equalsIgnoreCase("")
				    && !obj.getString("primaryphone").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsPrimaryphone(obj.getString("primaryphone"));
			    }

			    if (obj.has("locality") && obj.getString("locality") != null && !obj.getString("locality").equalsIgnoreCase("")
				    && !obj.getString("locality").equalsIgnoreCase(null)) {
				reviewsMenuInfo.setReviewsLocality(obj.getString("locality").replace("?", ""));
			    }

			    if (obj.has("type") && obj.getString("type") != null && !obj.getString("type").equalsIgnoreCase("")
				    && !obj.getString("type").equalsIgnoreCase(null)) {
				reviewsMenuInfo.setReviewsType(obj.getString("type").replace("?", ""));
			    }
			    if (obj.has("lat") && obj.getString("lat") != null && !obj.getString("lat").equalsIgnoreCase("")
				    && !obj.getString("lat").equalsIgnoreCase(null)) {
				reviewsMenuInfo.setReviewsLatitude(obj.getString("lat").replace("?", ""));
			    }
			    if (obj.has("lon") && obj.getString("lon") != null && !obj.getString("lon").equalsIgnoreCase("")
				    && !obj.getString("lon").equalsIgnoreCase(null)) {
				reviewsMenuInfo.setReviewsLongitude(obj.getString("lon").replace("?", ""));
			    }
			    if (obj.has("doctype") && obj.getString("doctype") != null && !obj.getString("doctype").equalsIgnoreCase("")
				    && !obj.getString("doctype").equalsIgnoreCase(null)) {
				reviewsMenuInfo.setReviewsDoctype(obj.getString("doctype").replace("?", ""));
			    }
			    if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
				    && !obj.getString("avgrate").equalsIgnoreCase(null) && !obj.getString("avgrate").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsAvgrate(obj.getString("avgrate"));
			    }
			    if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").equalsIgnoreCase("")
				    && !obj.getString("pic").equalsIgnoreCase(null) && !obj.getString("pic").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsPic(obj.getString("pic"));
			    }
			    if (obj.has("second_image") && obj.getString("second_image") != null
				    && !obj.getString("second_image").trim().equalsIgnoreCase("") && !obj.getString("second_image").trim().equalsIgnoreCase(null)) {
				reviewsMenuInfo.setReviewsSecondImage(obj.getString("second_image").trim());
			    }
			    
			    if (obj.has("emergencyPic") && obj.getString("emergencyPic") != null
				    && !obj.getString("emergencyPic").trim().equalsIgnoreCase("")
				    && !obj.getString("emergencyPic").trim().equalsIgnoreCase(null)) {
				reviewsMenuInfo.setHospitalEmergencyPic(obj.getString("emergencyPic").trim());
			    }
			    if (obj.has("leftPic") && obj.getString("leftPic") != null
				    && !obj.getString("leftPic").trim().equalsIgnoreCase("")
				    && !obj.getString("leftPic").trim().equalsIgnoreCase(null)) {
				reviewsMenuInfo.setHospitalLeftPic(obj.getString("leftPic").trim());
			    }
			    if (obj.has("rightPic") && obj.getString("rightPic") != null
				    && !obj.getString("rightPic").trim().equalsIgnoreCase("")
				    && !obj.getString("rightPic").trim().equalsIgnoreCase(null)) {
				reviewsMenuInfo.setHospitalRightPic(obj.getString("rightPic").trim());
			    }
			    
			    if (obj.has("rate") && obj.getString("rate") != null && !obj.getString("rate").equalsIgnoreCase("")
				    && !obj.getString("rate").equalsIgnoreCase(null) && !obj.getString("rate").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsRating(obj.getString("rate"));
			    }
			    if (obj.has("recommend") && obj.getString("recommend") != null && !obj.getString("recommend").equalsIgnoreCase("")
				    && !obj.getString("recommend").equalsIgnoreCase(null) && !obj.getString("recommend").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsRecommend(obj.getString("recommend"));
			    }
			    if (obj.has("fees") && obj.getString("fees") != null && !obj.getString("fees").equalsIgnoreCase("")
				    && !obj.getString("fees").equalsIgnoreCase(null) && !obj.getString("fees").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsFees(obj.getString("fees"));
			    }
			    if (obj.has("waitTime") && obj.getString("waitTime") != null && !obj.getString("waitTime").equalsIgnoreCase("")
				    && !obj.getString("waitTime").equalsIgnoreCase(null) && !obj.getString("waitTime").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsWaitTime(obj.getString("waitTime"));
			    }
			    if (obj.has("attitude") && obj.getString("attitude") != null && !obj.getString("attitude").equalsIgnoreCase("")
				    && !obj.getString("attitude").equalsIgnoreCase(null) && !obj.getString("attitude").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsAttitude(obj.getString("attitude"));
			    }
			    if (obj.has("satisfaction") && obj.getString("satisfaction") != null
				    && !obj.getString("satisfaction").equalsIgnoreCase("") && !obj.getString("satisfaction").equalsIgnoreCase(null)
				    && !obj.getString("satisfaction").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsSatisfaction(obj.getString("satisfaction"));
			    }
			    if (obj.has("review") && obj.getString("review") != null
				    && !obj.getString("review").equalsIgnoreCase("") && !obj.getString("review").equalsIgnoreCase(null)
				    && !obj.getString("review").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsDescription(obj.getString("review"));
			    }
			    if (obj.has("created_at") && obj.getString("created_at") != null
				    && !obj.getString("created_at").equalsIgnoreCase("") && !obj.getString("created_at").equalsIgnoreCase(null)
				    && !obj.getString("created_at").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsCreateddate(Utils.getDate(obj.getString("created_at")));
			    }
			    if (obj.has("email") && obj.getString("email") != null
				    && !obj.getString("email").equalsIgnoreCase("") && !obj.getString("email").equalsIgnoreCase(null)
				    && !obj.getString("email").equalsIgnoreCase("null")) {
				reviewsMenuInfo.setReviewsMailId(obj.getString("email"));
			    }
			}
			reviewsMenuList.add(reviewsMenuInfo);
		    }
		   
		    if (reviewsMenuList != null && reviewsMenuList.size() > 0) {

			txtReviewsHeader.setText(getString(R.string.reviewsHeader)+" - "+reviewsMenuList.size());
			reviewsMenuAdapter = new ReviewsMenuAdapter(ReviewsActivity.this, R.layout.reviews_menu_items, reviewsMenuList);
			reviewsListView.setAdapter(reviewsMenuAdapter);

		    } else {
			reviewsListView.setVisibility(View.GONE);
			((TextView) findViewById(R.id.txtNoRecordFound)).setVisibility(View.VISIBLE);
		    }

		}

	    } catch (JSONException e) {
		reviewsListView.setVisibility(View.GONE);
		((TextView) findViewById(R.id.txtNoRecordFound)).setVisibility(View.VISIBLE);
	    }
	    if (AppConstants.LogLevel == 1) {
		try {
		    postServerTimepend = Utils.postServerTimeSpend();
		    totalScreenTimeSpend = Utils.totalScreenTimeSpend();
		    Utils.callPerfomanceTestingApi("Saved List", "FavouriteActivity", "ListUserFavourites", preServerTimeSpend, serverTimeSpend,
			    postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(ReviewsActivity.this),
			    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""), AppConstants.perfTestCode);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}

    }

    @Override
    protected void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("Reviews Activity");
    }

    @Override
    protected void onResume() {
	super.onResume();
	//if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
		
		ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
		
		new CreateSessionIdAsyncTask().execute("0", "");
		
	//}
//	try {
//	    menuItems();
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}
    }

    @Override
    protected void onPause() {
	super.onPause();
    }

    @Override
    protected void onStop() {
	super.onStop();
	  FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	return super.onKeyDown(keyCode, event);
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
		"ReviewsMenu"));
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
	    ReviewsActivity.this.finish();
	}
    };

    public static ReviewsActivity getInstance() {
	return reviewsScreen;
    }
}
