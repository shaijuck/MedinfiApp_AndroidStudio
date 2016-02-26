package com.medinfi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.LocationUpdateActivity.GetLocalityList;
import com.medinfi.adapters.CitySearchAdapter;
import com.medinfi.adapters.MenuItemListAdapter;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.datainfo.SearchInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.SliderMenuClickListener;
import com.medinfi.utils.TypefaceSpan;
import com.medinfi.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SuggestDoctorHospitalActivity extends Activity {
    private EasyTracker easyTracker = null;
    EditText edtCityName, edtDocName, edtHosName, edtSpecialist,edtEmail;
    TextView txtErrorMessage;
    private TextView actionbarTitle, actionbarSubtitle;
    String selectedCityName;
    ProgressDialog waitDialog;
    private ListView cityListView;
    private String cityID = null;
    private ArrayList<SearchInfo> citySearchList;
    private CitySearchAdapter citySearchAdapter;
    private static boolean isEventPressed = false;
    private static SuggestDoctorHospitalActivity suggestDoctorHospitalActivity;
   
    private ImageView imgBack;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<MenuItemInfo> menuItemInfo;
    private MenuItemListAdapter menuItemListAdapter;
    boolean isMenuOpened = false;
    
    //For performance testing
    private String preServerTimeSpend="";
    private String serverTimeSpend="";
    private String postServerTimepend="";
    private String totalScreenTimeSpend="";
    private boolean isHomePressed = false;
    
    private Bitmap bitmap;
    String encodedImage = "";
    private Dialog selectImageDialog;
    private File file;
    private String picturePath;
    int rotate = 0;
    private Bitmap largeImageBitmap;
    private ImageView imgSuggetion;
    private Button imgClose;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.suggestion_layout);
	easyTracker = EasyTracker.getInstance(SuggestDoctorHospitalActivity.this);
	suggestDoctorHospitalActivity = this;
	initialize();
	showLocation();
	cityClickEvent();
	locationClickEvent();
	backClickEvent();
	setSpecialistVisible(edtDocName);
	isHomePressed = false;
	isEventPressed = false;
    }

    private void initialize() {
	try {
	   
	    Typeface tf2 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoLight.ttf");
	    Typeface fontRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	    actionbarTitle = (TextView) findViewById(R.id.title);
	    actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
//	    imgBack = (ImageView) findViewById(R.id.imgBack);
//	    imgBack.setPadding(0, 0, 10, 0);
//	    imgBack.setImageResource(R.drawable.menu_icon_closed);

	    cityListView = (ListView) findViewById(R.id.cityListView);
	    cityListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    cityListView.setVisibility(View.GONE);
	    edtCityName = (EditText) findViewById(R.id.edtCityName);
	    edtCityName.setTypeface(tf2);

	    edtCityName.setText(ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, ""));

	    edtDocName = (EditText) findViewById(R.id.edtDocName);
	    edtDocName.setTypeface(tf2);
	    edtHosName = (EditText) findViewById(R.id.edtHosName);
	    edtHosName.setTypeface(tf2);
	    edtSpecialist = (EditText) findViewById(R.id.edtSpecialist);
	    edtSpecialist.setTypeface(tf2);
	    edtEmail= (EditText) findViewById(R.id.edtEmail);
	    edtEmail.setTypeface(tf2);
//	    if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
//				&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
//	    edtEmail.setText(ApplicationSettings.getPref(AppConstants.EMAIL_ID, ""));
//	    }
	    txtErrorMessage = (TextView) findViewById(R.id.txtErrorMessage);
	    imgSuggetion = (ImageView) findViewById(R.id.imgSuggetion);
	    imgClose = (Button) findViewById(R.id.imgClose);
	    ((Button)findViewById(R.id.submitButton)).setTypeface(fontRegular);
	    ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, false);
	    jsonObject = new JSONObject();
	} catch (Exception e) {
             e.printStackTrace();
	}
    }

    public void onClickSearchCity(View view) {
	if (Utils.isNetworkAvailable(SuggestDoctorHospitalActivity.this)) {
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	    ((ScrollView) findViewById(R.id.scrollView)).setVisibility(View.VISIBLE);
	    cityListView.setVisibility(View.VISIBLE);
	    ((LinearLayout) findViewById(R.id.feildLayout)).setVisibility(View.GONE);
	    
	    new GetCityList().execute();
	} else {
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    ((ScrollView) findViewById(R.id.scrollView)).setVisibility(View.GONE);
	    cityListView.setVisibility(View.GONE);

	}

    }

    // City click events
    public void cityClickEvent() {
	cityListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    if (AppConstants.LogLevel == 1) {
	    	Utils.startServerTime = System.currentTimeMillis();
	    }
		
		SearchInfo entry = (SearchInfo) parent.getItemAtPosition(position);
	        easyTracker.send(MapBuilder.createEvent("Suggest Doctor/Hospital", "Select City", entry.getSearchName(), null).build());
		edtCityName.setVisibility(View.VISIBLE);
		cityID = entry.getSearchId();
		edtCityName.setText(entry.getSearchName());
		cityListView.setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.feildLayout)).setVisibility(View.VISIBLE);
		clearFeilds();
	    }
	});
    }
    public void setSpecialistVisible(final EditText editsearch) {
	editsearch.addTextChangedListener(new TextWatcher() {
	    @Override
	    public void afterTextChanged(Editable arg0) {
		String searchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		if(searchText!=null && searchText.trim().length() > 0){
		   // edtDocName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		    edtSpecialist.setVisibility(View.GONE);
		}else{
		   // edtDocName.setImeOptions(EditorInfo.IME_ACTION_DONE);
		    edtSpecialist.setVisibility(View.GONE); 
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
    private void clearFeilds() {
	edtDocName.setText("");
	edtHosName.setText("");
	edtSpecialist.setText("");
	encodedImage = "";
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

    private void startLocationUpdate() {
	Intent intent = new Intent(SuggestDoctorHospitalActivity.this, LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
    }

    public void onSubmit(View view) {
	if (validate()) {
	    isEventPressed = true;
	    if (Utils.isNetworkAvailable(this)) {
		String strCityId;
		if (cityID != null && cityID.trim().length() > 0) {
		    strCityId = cityID;
		} else {
		    strCityId = ApplicationSettings.getPref(AppConstants.CITY_SEARCH_ID, "");
		}
		if (AppConstants.LogLevel == 1) {
		    Utils.startPreServerCallTime = System.currentTimeMillis();
		    Utils.startTotalScreenTime = System.currentTimeMillis();
		}
		easyTracker.send(MapBuilder.createEvent("Suggest Doctor/Hospital", "Submit", "Submit", null).build());
		try {
		    jsonObject.putOpt("image", encodedImage);
		} catch (JSONException e) {
		    e.printStackTrace();
		}
		new PostSuggestedDoctorHospital().execute(edtEmail.getText().toString().trim(),edtDocName.getText().toString().trim(), edtHosName.getText().toString().trim(), strCityId,
			edtSpecialist.getText().toString().trim());
	    } else {
		Toast.makeText(SuggestDoctorHospitalActivity.this, Utils.getCustomeFontStyle(SuggestDoctorHospitalActivity.this, getString(R.string.notWorkNotAvailable)), Toast.LENGTH_SHORT).show();
	    }

	}
    }

    private boolean validate() {
	boolean isValid = true;
	try {
	    String strMesg = "";
	    if (edtCityName.getText().toString().trim().equals("")) {
		strMesg = getString(R.string.suggest_cityValidate);
		isValid = false;
	    }
	    if (edtDocName.getText().toString().trim().equals("") && edtHosName.getText().toString().trim().equals("")) {
		if (!isValid) {
		    strMesg = strMesg + ", " + getString(R.string.suggest_docHosName);
		} else {
		    strMesg = strMesg + getString(R.string.suggest_docHosName);
		}
		isValid = false;
	    }
	    if (!edtEmail.getText().toString().trim().equals("")){ if (!isValidEmail(edtEmail.getText().toString().trim())) {
	    	if (!isValid) 
	    	strMesg =strMesg +", "+ getString(R.string.msgRegMailIDBlanck);
	    	else
	    	strMesg = strMesg + getString(R.string.msgRegMailIDBlanck);
			isValid = false;
	    }
		    }
	    if (!strMesg.equalsIgnoreCase("")) {
		txtErrorMessage.setVisibility(View.VISIBLE);
		txtErrorMessage.setText(strMesg + ".");
	    } else {
		txtErrorMessage.setVisibility(View.GONE);
	    }
	} catch (Exception e) {
	    isValid = true;
	}
	return isValid;
    }
    
    public boolean isValidEmail(CharSequence target) {
    	if (target == null) {
    	    return false;
    	} else {
    	    return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    	}
        }

    private void locationClickEvent() {
	LinearLayout layoutLocation = (LinearLayout) findViewById(R.id.layoutLocation);
	layoutLocation.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		isEventPressed = true;
		easyTracker.send(MapBuilder.createEvent("Suggest Doctor/Hospital", "Update Location", "Update Location", null).build());
		startLocationUpdate();
	    }
	});
    }

    private void backClickEvent() {
	LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
	layoutBack.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Suggest Doctor/Hospital", "Back", "Back", null).build());
		SuggestDoctorHospitalActivity.this.finish();
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

    class PostSuggestedDoctorHospital extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();

	    waitDialog = new ProgressDialog(SuggestDoctorHospitalActivity.this);
	    waitDialog.setMessage(Utils.getCustomeFontStyle(SuggestDoctorHospitalActivity.this, getString(R.string.favouriteDeleting)));
	    waitDialog.setCancelable(false);
	    waitDialog.show();
	}

	@Override
	protected String doInBackground(String... arg0) {
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
		preServerTimeSpend = Utils.preServerTimeSpend();
	    }
	    JSONParser jparser = new JSONParser();
	    String result = null;
	    try {
	    	 String deviceId = Secure.getString(SuggestDoctorHospitalActivity.this.getContentResolver(),
                     Secure.ANDROID_ID);
		result = jparser.postSuggetedDocHospital(arg0[0], arg0[1], arg0[2], arg0[3], arg0[4],jsonObject.toString(),deviceId);
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	    if (AppConstants.LogLevel == 1) {
		serverTimeSpend = Utils.serverTimeSpend();
	    }
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {

	    if (waitDialog != null && waitDialog.isShowing()) {
		waitDialog.dismiss();
	    }
	    if (result != null) {
		try {
		    JSONObject jobj = new JSONObject(result);
		    if (jobj != null) {
			String message = jobj.getString("status");
			
			if (AppConstants.LogLevel == 1) {
			    try {
				Utils.startPostServerCallTime = System.currentTimeMillis();
				ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jobj.getString("server_processtime"));
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}
			
			if (message.equalsIgnoreCase("Success")) {
			    customThankYouDialog(getString(R.string.suggestedThankMesg), getString(R.string.suggestedThankMesgDesc));

			} else if (message.equalsIgnoreCase("Fail")) {
			    
			    customThankYouDialog(getString(R.string.suggestedThankMesgFail), jobj.getString("desc"));
			    /*
			     * String strError = jobj.getString("error_code");
			     * if (strError.equalsIgnoreCase("ERR_3")) {
			     * customThankYouDialog
			     * (getString(R.string.reviewSubmitted
			     * ),getString(R.string.reviewThankyou)); } else if
			     * (strError.equalsIgnoreCase("ERR_4")) {
			     * customThankYouDialog
			     * (getString(R.string.reviewSubmitted
			     * ),getString(R.string.reviewThankyou)); }
			     */

			} else {
			}
		    }

		} catch (JSONException e) {
		    e.printStackTrace();
		}
		if (AppConstants.LogLevel == 1) {
		    try {
			postServerTimepend = Utils.postServerTimeSpend();
			totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			Utils.callPerfomanceTestingApi("Submit Suggestion", "SuggestDoctorHospitalActivity", "AddUserSuggestions", preServerTimeSpend, serverTimeSpend,
				postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(SuggestDoctorHospitalActivity.this),
				ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""), AppConstants.perfTestCode);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	}

    }

    // API Call for city
    final class GetCityList extends AsyncTask<Void, Void, String> {
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
	    }
	    waitDialog = new ProgressDialog(SuggestDoctorHospitalActivity.this);
	    waitDialog.setCancelable(true);
	    waitDialog.setMessage(Utils.getCustomeFontStyle(SuggestDoctorHospitalActivity.this, getString(R.string.screenLoading)));
	    // waitDialog.setOnCancelListener(cancelListener);
	    waitDialog.show();
	}

	@Override
	protected String doInBackground(Void... arg0) {
	    JSONParser jparser = new JSONParser();
	    String result = jparser.getCityList();
	    
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	    try {
		if (waitDialog != null && waitDialog.isShowing())
		    waitDialog.dismiss();
	    } catch (Exception e1) {
		e1.printStackTrace();
	    }
	    try {
		if (result != null) {
		    citySearchList = new ArrayList<SearchInfo>();

		    JSONObject jsonObject = new JSONObject(result.toString());
		    JSONArray jsonarray = new JSONArray(jsonObject.getString("city"));
		    for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = jsonarray.getJSONObject(i);
			SearchInfo searchInfo = new SearchInfo();
			searchInfo.setSearchName(obj.getString("name"));
			searchInfo.setSearchId(obj.getString("id"));
			citySearchList.add(searchInfo);
		    }

		    if (citySearchList != null && citySearchList.size() > 0) {
			citySearchAdapter = new CitySearchAdapter(SuggestDoctorHospitalActivity.this, R.layout.search_list_item, citySearchList);
			cityListView.setAdapter(citySearchAdapter);
			edtCityName.setVisibility(View.GONE);
		    }
		} else {
		    cityListView.setVisibility(View.GONE);
		    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
		    ((LinearLayout) findViewById(R.id.mainLayout)).setVisibility(View.GONE);
		}
		
	    } catch (JSONException e) {
		cityListView.setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.mainLayout)).setVisibility(View.GONE);
	    }

	}

    }

    /**
     * Create custom dialog
     */
    private void customThankYouDialog(String strTitle, String strMassag) {
	final Dialog dialog = new Dialog(this);
	Typeface tfRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	Typeface tfRobotoLight = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoLight.ttf");
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.review_thankyou_layout, null);
	dialog.setContentView(layout);
	dialog.setCancelable(false);
	WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
	wmlp.gravity = Gravity.TOP | Gravity.LEFT;
	wmlp.x = 60;
	wmlp.y = 60;
	dialog.show();
	TextView title = (TextView) dialog.findViewById(R.id.textDialog);
	title.setTypeface(tfRegular);
	TextView messageDesc = (TextView) dialog.findViewById(R.id.textDialogMsg);
	messageDesc.setTypeface(tfRegular);
	title.setText(strTitle);
	messageDesc.setText(strMassag);
	ImageView btnCancel = (ImageView) dialog.findViewById(R.id.imgCancel);
	btnCancel.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
		clearFeilds();
		closeSelectedImage();
		dialog.dismiss();
	    }
	});

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
	    if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
	    }
	}
	return ret;
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
		"SuggestionMenu"));
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

    @Override
    protected void onResume() {
	super.onResume();
//	try {
//	   // menuItems();
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}
	if (!isEventPressed) {
	    if (isHomePressed) {
		 easyTracker.send(MapBuilder.createEvent("Suggest Doctor/Hospital", "Device Home", "Device Home", null).build());
	    }
	  //  if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	}
	 if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
				&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
	    edtEmail.setText(ApplicationSettings.getPref(AppConstants.EMAIL_ID, ""));
	    }
    }

    @Override
    protected void onPause() {
	super.onPause();
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    public static SuggestDoctorHospitalActivity getInstance() {
	return suggestDoctorHospitalActivity;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    easyTracker.send(MapBuilder.createEvent("Suggest Doctor/Hospital", "Back", "Back", null).build());
	    isEventPressed = true;
	    if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerList)) {
		isMenuOpened = false;
		mDrawerLayout.closeDrawer(mDrawerList);
		imgBack.setImageResource(R.drawable.menu_icon_closed);
	    } else {
		finish();
	    }
	}
	return false;
    }
    @Override
    public void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("SuggestDoctorHospital Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
	super.onStop();
	 FlurryAgent.onEndSession(this);
	isHomePressed = true;
	EasyTracker.getInstance(this).activityStop(this);
    }
    public void loadBitmap(final String path, final int orientation, final int targetWidth, final int targetHeight) {

  	new AsyncTask<Void, Void, Bitmap>() {

  	    ProgressDialog dialog;

  	    protected void onPreExecute() {
  		dialog = new ProgressDialog(SuggestDoctorHospitalActivity.this);
  		dialog.setCancelable(false);
  		dialog.setMessage(Utils.getCustomeFontStyle(SuggestDoctorHospitalActivity.this, getString(R.string.screenProcessing)));
  		dialog.show();
  	    };

  	    @Override
  	    protected Bitmap doInBackground(Void... params) {
  		try {
  		    // First decode with inJustDecodeBounds=true to check
  		    // dimensions
  		    final BitmapFactory.Options options = new BitmapFactory.Options();
  		    options.inJustDecodeBounds = false;
  		    // set sample size
  		    options.inSampleSize = Utils.calculateInSampleSize(options, 500, 500);
  		    BitmapFactory.decodeFile(path, options);

  		    // Adjust extents
  		    int sourceWidth, sourceHeight;
  		    if (orientation == 90 || orientation == 270) {
  			sourceWidth = options.outHeight;
  			sourceHeight = options.outWidth;
  		    } else {
  			sourceWidth = options.outWidth;
  			sourceHeight = options.outHeight;
  		    }

  		    // Calculate the maximum required scaling ratio if required
  		    // and load the bitmap
  		    if (sourceWidth > targetWidth || sourceHeight > targetHeight) {
  			float widthRatio = (float) sourceWidth / (float) targetWidth;
  			float heightRatio = (float) sourceHeight / (float) targetHeight;
  			float maxRatio = Math.max(widthRatio, heightRatio);
  			options.inJustDecodeBounds = false;
  			options.inSampleSize = 5;
  			// this options allow android to claim the bitmap memory
  			// if it runs low on memory
  			options.inPurgeable = true;
  			options.inInputShareable = true;
  			options.inTempStorage = new byte[16 * 1024];
  			bitmap = BitmapFactory.decodeFile(path, options);
  		    } else {
  			bitmap = BitmapFactory.decodeFile(path);
  		    }

  		    // Rotate the bitmap if required
  		    if (orientation > 0) {
  			Matrix matrix = new Matrix();
  			matrix.postRotate(orientation);
  			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
  		    }

  		    // Re-scale the bitmap if necessary
  		    sourceWidth = bitmap.getWidth();
  		    sourceHeight = bitmap.getHeight();
  		    if (sourceWidth != targetWidth || sourceHeight != targetHeight) {
  			float widthRatio = (float) sourceWidth / (float) targetWidth;
  			float heightRatio = (float) sourceHeight / (float) targetHeight;
  			float maxRatio = Math.max(widthRatio, heightRatio);
  			sourceWidth = (int) ((float) sourceWidth / maxRatio);
  			sourceHeight = (int) ((float) sourceHeight / maxRatio);
  			bitmap = Bitmap.createScaledBitmap(bitmap, sourceWidth, sourceHeight, true);
  			encodedImage = Utils.convertBase64(bitmap);
  		    }
  		} catch (Exception e) {
  		}
  		return bitmap;
  	    }

  	    protected void onPostExecute(Bitmap result) {
  		try {
  		    try {
  			if (dialog != null && dialog.isShowing()) {
  			    dialog.dismiss();
  			}
  		    } catch (Exception e1) {
  			e1.printStackTrace();
  		    }
  		    if (result != null) {
  			try {
  			    if (selectImageDialog != null)
  				selectImageDialog.dismiss();
  			} catch (Exception e) {
  			    e.printStackTrace();
  			}
  			imgClose.setVisibility(View.VISIBLE);
  			ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, true);
  			largeImageBitmap = result;
  			imgSuggetion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
  				LinearLayout.LayoutParams.MATCH_PARENT));
  			imgSuggetion.setImageBitmap(largeImageBitmap);
  		    } else {
  			// txtErrorMesg.setVisibility(View.VISIBLE);
  			// txtErrorMesg.setText(getString(R.string.errorMesg_PrecriptionFormat));
  		    }
  		} catch (Exception e) {
  		    e.printStackTrace();
  		}
  	    };

  	    @Override
  	    protected void onCancelled() {
  		// TODO Auto-generated method stub
  		super.onCancelled();
  		if (dialog != null && dialog.isShowing()) {
  		    dialog.dismiss();
  		    dialog = null;
  		}
  	    }
  	}.execute();

      }

    public void onSuggetionImageClick(View view) {
	if (ApplicationSettings.getPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, false)) {
	    Utils.enlargImageDialog(this,largeImageBitmap);
	} else {
	    selectImage();
	}
    }
    public void onCloseImage(View view) {
	closeSelectedImage();
       }

    private void closeSelectedImage() {
	ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, false);
	imgSuggetion.setLayoutParams(new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources()
		.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics())));
	imgSuggetion.setImageResource(R.drawable.ic_action_camera);
	imgClose.setVisibility(View.GONE);
    }
      /* select image from camera /phone */
      private void selectImage() {
  	try {
  	    if (selectImageDialog != null)
  		selectImageDialog.dismiss();
  	} catch (Exception e) {
  	    e.printStackTrace();
  	}
  	selectImageDialog = new Dialog(this);
  	SpannableString s = new SpannableString("Add Photo!");
  	s.setSpan(new TypefaceSpan(this, "RobotoRegular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
  	selectImageDialog = new Dialog(this);
  	selectImageDialog.setTitle(s);
  	Rect displayRectangle = new Rect();
  	Window window = getWindow();
  	window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
  	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  	View layout = inflater.inflate(R.layout.image_selector_layout, null);
  	layout.setMinimumWidth((int) (displayRectangle.width() * 0.60f));
  	// layout.setMinimumHeight((int) (displayRectangle.height() * 0.9f));
  	selectImageDialog.setContentView(layout);
  	selectImageDialog.setCancelable(true);
  	selectImageDialog.setOnCancelListener(cancelListener);
  	selectImageDialog.show();

  	final String defaultCamera = getDefaultCamera();
  	LinearLayout takePhotoLayout = (LinearLayout) selectImageDialog.findViewById(R.id.takePhotoLayout);
  	takePhotoLayout.setOnClickListener(new OnClickListener() {
  	    @Override
  	    public void onClick(View v) {
  		try {
  		    // easyTracker.send(MapBuilder.createEvent("Upload Prescription",
  		    // "Camera", "Camera", null).build());
  		    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  		    if (defaultCamera != null) {
  			intent.setPackage(defaultCamera);
  		    }
  		    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
  		    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
  		    startActivityForResult(intent, 1);
  		} catch (Exception e) {
  		    e.printStackTrace();
  		}
  	    }
  	});

  	LinearLayout galleryLayout = (LinearLayout) selectImageDialog.findViewById(R.id.galleryLayout);
  	galleryLayout.setOnClickListener(new OnClickListener() {
  	    @Override
  	    public void onClick(View v) {
  		try {
  		    // easyTracker.send(MapBuilder.createEvent("Upload Prescription",
  		    // "Add Photo", "Choose from Gallery", null).build());
  		    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
  		    startActivityForResult(intent, 2);
  		} catch (Exception e) {
  		    e.printStackTrace();
  		}
  	    }
  	});

  	LinearLayout cancelLayout = (LinearLayout) selectImageDialog.findViewById(R.id.cancelLayout);
  	cancelLayout.setOnClickListener(new OnClickListener() {
  	    @Override
  	    public void onClick(View v) {
  		try {
  		    // easyTracker.send(MapBuilder.createEvent("Upload Prescription",
  		    // "Add Photo", "Cancel", null).build());
  		    selectImageDialog.dismiss();
  		} catch (Exception e) {
  		    e.printStackTrace();
  		}
  	    }
  	});
      }

      private String getDefaultCamera() {
  	String defaultCameraPackage = null;
  	try {
  	    List<ApplicationInfo> list = getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
  	    for (int n = 0; n < list.size(); n++) {
  		if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
  		    if (list.get(n).loadLabel(getPackageManager()).toString().equalsIgnoreCase("Camera")) {
  			defaultCameraPackage = list.get(n).packageName;
  			break;
  		    }
  		}
  	    }
  	} catch (Exception e) {
  	    e.printStackTrace();
  	}
  	return defaultCameraPackage;

      }

      /* return once the photo is clicked or selected */
      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  	super.onActivityResult(requestCode, resultCode, data);
  	try {
  	    if (selectImageDialog != null)
  		selectImageDialog.dismiss();
  	} catch (Exception e) {
  	    e.printStackTrace();
  	}
  	if (resultCode == RESULT_OK) {
  	    if (requestCode == 1) {
  		try {
  		    file = new File(Environment.getExternalStorageDirectory().toString());
  		    for (File temp : file.listFiles()) {
  			if (temp.getName().equals("temp.jpg")) {
  			    file = temp;
  			    break;
  			}
  		    }

  		    try {
  			ExifInterface exif = new ExifInterface(file.getAbsolutePath());
  			rotate = 0;
  			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

  			switch (orientation) {
  			case ExifInterface.ORIENTATION_ROTATE_270:
  			    rotate -= 90 - 360;
  			    break;
  			case ExifInterface.ORIENTATION_ROTATE_180:
  			    rotate -= 90 - 180;
  			    break;
  			case ExifInterface.ORIENTATION_ROTATE_90:
  			    rotate -= 90 - 180;
  			    break;
  			}

  		    } catch (IOException e) {
  			e.printStackTrace();
  		    }
  		    loadBitmap(file.getAbsolutePath(), rotate, 600, 600);
  		} catch (Exception e) {
  		    e.printStackTrace();
  		}

  	    } else if (requestCode == 2) {

  		try {
  		    Uri selectedImage = data.getData();
  		    String[] filePath = { MediaStore.Images.Media.DATA };
  		    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
  		    c.moveToFirst();
  		    int columnIndex = c.getColumnIndex(filePath[0]);
  		    picturePath = c.getString(columnIndex);
  		    c.close();

  		    try {
  			ExifInterface exif = new ExifInterface(picturePath);
  			rotate = 0;
  			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

  			switch (orientation) {
  			case ExifInterface.ORIENTATION_ROTATE_270:
  			    rotate -= 90 - 360;
  			    break;
  			case ExifInterface.ORIENTATION_ROTATE_180:
  			    rotate -= 90 - 180;
  			    break;
  			case ExifInterface.ORIENTATION_ROTATE_90:
  			    rotate -= 90 - 180;
  			    break;
  			}

  		    } catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		    }

  		    loadBitmap(picturePath, rotate, 600, 600);
  		} catch (Exception e) {
  		    e.printStackTrace();

  		}

  	    }
  	}
      }
      @Override
      public void onSaveInstanceState(Bundle outState) {
  	outState.putParcelable("photopath", largeImageBitmap);
  	super.onSaveInstanceState(outState);
      }

      @Override
      protected void onRestoreInstanceState(Bundle savedInstanceState) {
  	if (savedInstanceState != null) {
  	    if (savedInstanceState.containsKey("photopath")) {
  		largeImageBitmap = savedInstanceState.getParcelable("photopath");
  	    }
  	}
  	super.onRestoreInstanceState(savedInstanceState);
      }
      /* listener for dialog cancel in thread */
      OnCancelListener cancelListener = new OnCancelListener() {
  	@Override
  	public void onCancel(DialogInterface arg0) {
  	    try {
  		selectImageDialog.dismiss();
  	    } catch (Exception e) {
  		e.printStackTrace();
  	    }
  	}
      };
     
}
