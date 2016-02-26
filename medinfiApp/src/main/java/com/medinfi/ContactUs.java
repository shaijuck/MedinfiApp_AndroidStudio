package com.medinfi;

import java.util.ArrayList;





import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.adapters.MenuItemListAdapter;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.SliderMenuClickListener;
import com.medinfi.utils.Utils;

public class ContactUs extends Activity implements OnClickListener {

    // private TextView feedbackTextView;
    private Button submitButton;
    private EditText feedbackEditText;
    private Typeface tf1, tf2, tf3;
    private JSONObject jsonObject;

    private EasyTracker easyTracker = null;
    private LinearLayout back_layout;
    private static ContactUs instance;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;

    // For performance testing
    private String preServerTimeSpend = "";
    private String serverTimeSpend = "";
    private String postServerTimepend = "";
    private String totalScreenTimeSpend = "";
    private TextView actionbarTitle, actionbarSubtitle;
    private ImageView imgBack;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<MenuItemInfo> menuItemInfo;
    private MenuItemListAdapter menuItemListAdapter;
    boolean isMenuOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.contactus);
	instance = this;
	easyTracker = EasyTracker.getInstance(ContactUs.this);

	isEventPressed = false;
	isHomePressed = false;
	initialize();
	showLocation();
	backClickEvent();
	chechNetworkConnectivity();
	locationClickEvent();

    }

    private void initialize() {
	try {
	    actionbarTitle = (TextView) findViewById(R.id.title);
	    actionbarSubtitle = (TextView) findViewById(R.id.subtitle);

//	    imgBack = (ImageView) findViewById(R.id.imgBack);
//	    imgBack.setPadding(0, 0, 10, 0);
//	    imgBack.setImageResource(R.drawable.menu_icon_closed);

	    feedbackEditText = (EditText) findViewById(R.id.feedback_edittext);
	    submitButton = (Button) findViewById(R.id.feedback_submit);
	    submitButton.setOnClickListener(this);
	    back_layout = (LinearLayout) findViewById(R.id.back_layout);

	    // typeface for custom fonts
	    tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoBold.ttf");
	    tf2 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoLight.ttf");
	    tf3 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	    feedbackEditText.setTypeface(tf3);
	    submitButton.setTypeface(tf3);
	    ((TextView) findViewById(R.id.textFeedBack)).setTypeface(tf3);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void chechNetworkConnectivity() {
	if (isNetworkAvailable()) {
	    ((LinearLayout) findViewById(R.id.layoutFeedback)).setVisibility(View.VISIBLE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	} else {
	    ((LinearLayout) findViewById(R.id.layoutFeedback)).setVisibility(View.GONE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tf3);
	    ((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tf3);
	}
    }

    private void backClickEvent() {
	LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
	layoutBack.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		hideKeyboard();
		easyTracker.send(MapBuilder.createEvent("Feedback", "Back", "Back", null).build());
		ContactUs.this.finish();
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

    private void locationClickEvent() {
	LinearLayout layoutLocation = (LinearLayout) findViewById(R.id.layoutLocation);
	layoutLocation.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		hideKeyboard();
		isEventPressed = true;
		startLocationUpdate();
	    }
	});
    }

    // validations for that commentbox
    private boolean checkValidation() {
	boolean isValid = true;
	try {
	    if (feedbackEditText.getText().toString().trim().equals("")) {
		((TextView) findViewById(R.id.txtErrorMessage)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.txtErrorMessage)).setText(getString(R.string.feedbackValidation));
		isValid = false;
	    } else {
		((TextView) findViewById(R.id.txtErrorMessage)).setVisibility(View.GONE);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return isValid;
    }

    /* method to check the network */
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

    /*
     * implement on click method for the id's of xml
     */@Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.feedback_submit:

	    if (checkValidation()) {
		if (isNetworkAvailable()) {
		    try {
			easyTracker.send(MapBuilder.createEvent("Feedback", "Submit Feedback", "Submit Feedback", null).build());
			PostComments();
		    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }

		} else {
		    Toast.makeText(ContactUs.this, "Network not Available", Toast.LENGTH_SHORT).show();
		}
	    }
	    break;

	default:
	    break;
	}
    }

    /* post comments API call method */
    public void PostComments() {
	final class PostUserComments extends AsyncTask<Void, Void, String> {

	    ProgressDialog pdia;

	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		pdia = new ProgressDialog(ContactUs.this);
		pdia.setCancelable(false);
		pdia.setMessage(Utils.getCustomeFontStyle(ContactUs.this, "Posting Feedback..."));
		pdia.show();
	    }

	    // 200793653151
	    @Override
	    protected String doInBackground(Void... arg0) {
		if (AppConstants.LogLevel == 1) {
		    Utils.startServerTime = System.currentTimeMillis();
		}
		/* create object for JSONPARSER */
		JSONParser jparser = new JSONParser();

		// get userid from preferences
		String userID = ApplicationSettings.getPref(AppConstants.USER_ID, "");
		String emailID =ApplicationSettings.getPref(AppConstants.EMAIL_ID, "");
		String deviceId= Secure.getString(ContactUs.this.getContentResolver(),
	                Secure.ANDROID_ID);
		// call post user methods
		String result = jparser.postUserComments(userID, feedbackEditText.getText().toString(),emailID,deviceId);
		if (AppConstants.LogLevel == 1) {
		    serverTimeSpend = Utils.serverTimeSpend();
		}
		return result.toString();
	    }

	    @Override
	    protected void onPostExecute(String result) {
		if (pdia != null && pdia.isShowing())
		    pdia.dismiss();
		try {
		    if (result != null) {
			isEventPressed = true;
			if (AppConstants.LogLevel == 1) {
			    try {
				Utils.callPerfomanceTestingApi("Submit feedback", "ContactUs", "CreateUserFeedback", preServerTimeSpend,
					serverTimeSpend, postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(ContactUs.this),
					ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""), AppConstants.perfTestCode);
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}
		    }
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		finish();
	    }

	}
	// thread is executed here
	new PostUserComments().execute();

    }

    /*
     * method to show location we are using SpannableString for the custom color
     * and custom font
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

    @Override
    public void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("ContactUs Activity");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    isEventPressed = true;
	    easyTracker.send(MapBuilder.createEvent("Feedback", "Device Back", "Device Back", null).build());
	    finish();
	}
	return super.onKeyDown(keyCode, event);
    }

    public static ContactUs getInstance() {

	return instance;
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
		easyTracker.send(MapBuilder.createEvent("Feedback", "Device Home", "Device Home", null).build());
	    }
	   // if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	}
    }

    private void startLocationUpdate() {
	Intent intent = new Intent(ContactUs.this, LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
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
		"FeedBackMenu"));
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

    private void hideKeyboard() {
	try {
	    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

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
