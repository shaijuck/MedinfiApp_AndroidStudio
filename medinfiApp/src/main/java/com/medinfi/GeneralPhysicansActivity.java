package com.medinfi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.adapters.AutoCompleteAdapter;
import com.medinfi.adapters.ProfileAdapter;
import com.medinfi.datainfo.AutoCompleteInfo;
import com.medinfi.datainfo.ProfileInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.pulltorefresh.library.PullToRefreshBase;
import com.medinfi.pulltorefresh.library.PullToRefreshBase.Mode;
import com.medinfi.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.medinfi.pulltorefresh.library.PullToRefreshListView;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;

public class GeneralPhysicansActivity extends Activity {

    // intialize views used in xml
    private PullToRefreshListView physiciansListView;
    private ProfileAdapter profileAdapter;
    private ArrayList<ProfileInfo> physiciansList;
    private ArrayList<AutoCompleteInfo> autocompleteList;
    private AutoCompleteAdapter autoCompleteAdapter;
    // private TextView nearmeTextView;
    private TextView gpTextView;
    // private TextView topratedTextView;
    private Typeface tf;
    // private View nearbyView;
    // private View topratedView;
    public static Toast toast = null;
    private AutoCompleteTextView searchText;
    private EasyTracker easyTracker = null;
    private TextView actionbarTitle, actionbarSubtitle;
    private LinearLayout searchLayout;
    private LinearLayout titleLayout;
    private ImageView searchImageView;

    public boolean isTrueToast = false;
    private boolean isTrue = false;
    private ProgressDialog pdia;
    private GetPhysicianList getPhysicianList;
    private LinearLayout layoutBack;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private EditText searchEditText;
    private String strSearchText;
    private boolean isAllDataAvailable = false;
    private static GeneralPhysicansActivity generalPhysicansScreen;
    private String preServerTimeSpend = "";
    private String serverTimeSpend = "";
    private String postServerTimepend = "";
    private String totalScreenTimeSpend = "";
    private TextView txtNoResultFound;
    private boolean isPullToRefreshFromEnd = false;
	@SuppressLint("InlinedApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	/*
	 * this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
	 */
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.profilescreen);

	easyTracker = EasyTracker.getInstance(GeneralPhysicansActivity.this);
	isHomePressed = false;
	isEventPressed = false;
	generalPhysicansScreen = this;
	/*
	 * disable logo in the action bar if user wants to enable remove the
	 * code 3 lines from below code
	 */
	/*
	 * getSupportActionBar().setIcon(null);
	 * getSupportActionBar().setDisplayUseLogoEnabled(false);
	 * getSupportActionBar().setHomeButtonEnabled(false);
	 * getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	 */

	// customActionbar();
	tf = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	ApplicationSettings.putPref(AppConstants.LAST_COUNT, "0");
	actionbarTitle = (TextView) findViewById(R.id.title);
	actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
	layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
	searchEditText = (EditText) findViewById(R.id.searchedittext);
	searchEditText.setTypeface(tf);
	showLocation();
	//nearbyView = findViewById(R.id.nearme_view);
	//nearbyView.setVisibility(View.VISIBLE);
	//topratedView = findViewById(R.id.toprated_view);
	gpTextView = (TextView) findViewById(R.id.gp_text);
	gpTextView.setTypeface(tf);
	//nearmeTextView = (TextView) findViewById(R.id.nearme_textview);
	//nearmeTextView.setTypeface(tf);
	//topratedTextView = (TextView) findViewById(R.id.toprated_textview);
	/*
	 * String styledText =
	 * "<u><font color=\"#ed207b\">TOP RATED</font></u>.";
	 * topratedTextView.setText(Html.fromHtml(styledText),
	 * TextView.BufferType.SPANNABLE);
	 */
	//topratedTextView.setTypeface(tf);
	physiciansListView = (PullToRefreshListView)findViewById(R.id.profilelistview);
	txtNoResultFound = (TextView)findViewById(R.id.txtNoResultFound);
	txtNoResultFound.setVisibility(View.GONE);
	//physiciansListView = (ListView)findViewById(R.id.physiciansListView);
	isAllDataAvailable = false;
	if (AppConstants.LogLevel == 1) {
	    Utils.startPreServerCallTime = System.currentTimeMillis();
	    Utils.startTotalScreenTime = System.currentTimeMillis();
	    Utils.startTotalScreenTime20 = System.currentTimeMillis();
	}
	if (isNetworkAvailable()) {
	    physiciansListView.setVisibility(View.VISIBLE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	    physiciansList = new ArrayList<ProfileInfo>();
	    physiciansDetails(AppConstants.OFFSET_DEFAULT_VALUE);
	    getPhysicianList = new GetPhysicianList();
	    getPhysicianList.execute();
	    physiciansDetailsLoadAllData("-1");
	} else {
	    // Toast.makeText(GeneralPhysicansActivity.this,
	    // "Network not Available", Toast.LENGTH_SHORT).show();
	    physiciansListView.setVisibility(View.GONE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tf);
	    ((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tf);
	}
	searchGeneralPhysician(searchEditText);
	clickEvent();
//	backClickEvent();
//	locationClickEvent();
	tabClickEvent();
	Utils.startSuggestionActivity(this,txtNoResultFound,"GeneralPhysicansActivity",easyTracker);
    }

    private void backClickEvent() {
	layoutBack.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Doctor List", "Back", "Back", null).build());
		GeneralPhysicansActivity.this.finish();

	    }
	});
    }
    private void locationClickEvent(){
	 LinearLayout layoutLocation = (LinearLayout)findViewById(R.id.layoutLocation);
	 layoutLocation.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Doctor List", "Update Location", "Update Location", null).build());
		startLocationUpdate();
	    }
	});
	 
    }
    private void startLocationUpdate() {
	Intent intent = new Intent(GeneralPhysicansActivity.this,LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
    }
	// Api calling for general physicians
	public void physiciansDetails(final String offset) {
	}

    public class GetPhysicianList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
		preServerTimeSpend = Utils.preServerTimeSpend();
	    }
	    pdia = new ProgressDialog(GeneralPhysicansActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(GeneralPhysicansActivity.this, getString(R.string.screenLoading)));
	    pdia.show();
	}

	// 200793653151
	@Override
	protected String doInBackground(Void... arg0) {
	    isTrue = true;
	    String result = null;
	    try {
		JSONParser jparser = new JSONParser();

		String latitude = ApplicationSettings.getPref(AppConstants.LATITUDE, "");
		String longitude = ApplicationSettings.getPref(AppConstants.LONGITUDE, "");
		result = jparser.getGeneralPhysicians(latitude, longitude, AppConstants.OFFSET_DEFAULT_VALUE);
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
	    isTrue = false;
	    try {
		if (result != null) {
		    physiciansList.clear();
		    JSONArray jsonarray = new JSONArray(result.toString());

		    if (jsonarray != null && jsonarray.length() > 0) {
			for (int i = 0; i < jsonarray.length(); i++) {
			    JSONObject obj = jsonarray.getJSONObject(i);
			    ProfileInfo profileInfo = new ProfileInfo();
			    if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {
				profileInfo.setProfileId(obj.getString("id"));
				String fullName = null;
				String firstName = obj.getString("firstname");
				String middlename = obj.getString("middlename");

				String lastname = obj.getString("lastname");

				if (firstName != null && !firstName.equalsIgnoreCase("") && !firstName.equalsIgnoreCase(null)
					&& !firstName.equalsIgnoreCase("null")) {
				    fullName = firstName.trim();
				    profileInfo.setFirstName(firstName);
				}
				if (middlename != null && !middlename.equalsIgnoreCase("") && !middlename.equalsIgnoreCase(null)
					&& !middlename.equalsIgnoreCase("null")) {
				    fullName = fullName + " " + middlename.trim();
				    profileInfo.setMiddleName(middlename);
				}
				if (lastname != null && !lastname.equalsIgnoreCase("") && !lastname.equalsIgnoreCase("null")) {
				    fullName = fullName + " " + lastname.trim();
				    profileInfo.setLastName(lastname);
				}
				profileInfo.setProfileName(fullName.replace("?", ""));
				if (obj.getString("qualification") != null && !obj.getString("qualification").equalsIgnoreCase("")
					&& !obj.getString("qualification").equalsIgnoreCase("null"))
				    profileInfo.setProfileDesignation(obj.getString("qualification"));
				if (obj.getString("phone") != null && !obj.getString("phone").equalsIgnoreCase("")
					&& !obj.getString("phone").equalsIgnoreCase("null"))
				    profileInfo.setProfilePhoneNumber(obj.getString("phone"));
				if (obj.getString("email") != null && !obj.getString("email").equalsIgnoreCase("")
					&& !obj.getString("email").equalsIgnoreCase("null"))
				    profileInfo.setProfileEmail(obj.getString("email"));
				if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").trim().equalsIgnoreCase("")
					&& !obj.getString("pic").trim().equalsIgnoreCase("null"))
				    profileInfo.setProfileImage(obj.getString("pic").trim());
				
				if (obj.has("second_image") && obj.getString("second_image") != null
					&& !obj.getString("second_image").trim().equalsIgnoreCase("")
					&& !obj.getString("second_image").trim().equalsIgnoreCase(null)) {
				    profileInfo.setSecondImage(obj.getString("second_image").trim());
				}
				
				if (obj.has("distance") && obj.getString("distance") != null && !obj.getString("distance").equalsIgnoreCase("")
					&& !obj.getString("distance").equalsIgnoreCase("99999"))
				    profileInfo.setProfileDistance(obj.getString("distance"));
				if (obj.has("awardRecog") && obj.getString("awardRecog") != null && !obj.getString("awardRecog").equalsIgnoreCase("")
					&& !obj.getString("awardRecog").equalsIgnoreCase(null))
				    profileInfo.setProfileAwards(obj.getString("awardRecog"));
				if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
					&& !obj.getString("avgrate").equalsIgnoreCase(null))
				    profileInfo.setProfileRating(obj.getString("avgrate"));

				if (obj.has("hospitalName") && obj.getString("hospitalName") != null
					&& !obj.getString("hospitalName").equalsIgnoreCase("")
					&& !obj.getString("hospitalName").equalsIgnoreCase(null)) {
				    String hospitalName;
				    if (obj.getString("hospitalName").toString().contains(",")) {

					hospitalName = obj.getString("hospitalName").toString().split(",")[0];
				    } else {
					hospitalName = obj.getString("hospitalName").toString();
				    }
				    profileInfo.setHospitalName(hospitalName.replace("?", ""));
				}
				if (AppConstants.LogLevel == 1) {
				    try {
					if (obj.has("server_processtime") && obj.getString("server_processtime") != null
					    && !obj.getString("server_processtime").equalsIgnoreCase("")
					    && !obj.getString("server_processtime").equalsIgnoreCase(null)) {
					 ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, obj.getString("server_processtime"));
					}
				    } catch (Exception e) {
					e.printStackTrace();
				    }
				}
				physiciansList.add(profileInfo);
			    }
			}

			if (physiciansList != null && physiciansList.size() > 0) {

			    //ApplicationSettings.putPref(AppConstants.LAST_COUNT, "" + physiciansList.size());
			    profileAdapter = new ProfileAdapter(GeneralPhysicansActivity.this, R.layout.profile_list_item, physiciansList);
			    physiciansListView.setAdapter(profileAdapter);
			    if (!isAllDataAvailable) {
				physiciansListView.setMode(Mode.PULL_FROM_END);
			    } else {
				physiciansListView.setMode(Mode.DISABLED);
			    }
			}

		    } else {
			physiciansListView.setMode(Mode.DISABLED);
			Toast.makeText(GeneralPhysicansActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
		    }
		} else {
		    
		    Toast.makeText(GeneralPhysicansActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
		}
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    if (AppConstants.LogLevel == 1) {
		try {
		    postServerTimepend = Utils.postServerTimeSpend();
		    String totalScreenTimeSpend20 = Utils.totalScreenTimeSpend20();
		    Utils.callPerfomanceTestingApi("GeneralPhysicans 20 List", "GeneralPhysicansActivity", "listgenphysician", preServerTimeSpend, serverTimeSpend,
		    	postServerTimepend, totalScreenTimeSpend20, Utils.getNetworkType(GeneralPhysicansActivity.this),
		    	ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}

    }

	// Api calling for general physicians for load more
    public void physiciansDetailsLoadAllData(final String offset) {
	final class GetPhysicianList extends AsyncTask<Void, Void, String> {
	    // 200793653151
	    @Override
	    protected String doInBackground(Void... arg0) {
		if (AppConstants.LogLevel == 1) {
		    Utils.startServerTime = System.currentTimeMillis();
		}
		JSONParser jparser = new JSONParser();
		String latitude = ApplicationSettings.getPref(AppConstants.LATITUDE, "");
		String longitude = ApplicationSettings.getPref(AppConstants.LONGITUDE, "");
		String result = jparser.getGeneralPhysicians(latitude, longitude, offset);
		if (AppConstants.LogLevel == 1) {
		    serverTimeSpend = Utils.serverTimeSpend();
		    Utils.startPostServerCallTime = System.currentTimeMillis();
		} 
		return result.toString();
	    }

	    @Override
	    protected void onPostExecute(String result) {
		physiciansListView.onRefreshComplete();
		try {
		    if (result != null) {
			physiciansList.clear();
			JSONArray jsonarray = new JSONArray(result.toString());
			if (jsonarray != null && jsonarray.length() > 0) {

			    for (int i = 0; i < jsonarray.length(); i++) {

				JSONObject obj = jsonarray.getJSONObject(i);
				ProfileInfo profileInfo = new ProfileInfo();
				if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {
				    profileInfo.setProfileId(obj.getString("id"));
				    String fullName = null;
				    String firstName = obj.getString("firstname");
				    String middlename = obj.getString("middlename");

				    String lastname = obj.getString("lastname");

				    if (firstName != null && !firstName.equalsIgnoreCase("") && !firstName.equalsIgnoreCase(null)
					    && !firstName.equalsIgnoreCase("null")) {
					fullName = firstName.trim();
					profileInfo.setFirstName(firstName);
				    }
				    if (middlename != null && !middlename.equalsIgnoreCase("") && !middlename.equalsIgnoreCase(null)
					    && !middlename.equalsIgnoreCase("null")) {
					fullName = fullName + " " + middlename.trim();
					profileInfo.setMiddleName(middlename);
				    }
				    if (lastname != null && !lastname.equalsIgnoreCase("") && !lastname.equalsIgnoreCase("null")) {
					fullName = fullName + " " + lastname.trim();
					profileInfo.setLastName(lastname);
				    }
				    profileInfo.setProfileName(fullName.replace("?", ""));
				    if (obj.getString("qualification") != null && !obj.getString("qualification").equalsIgnoreCase("")
					    && !obj.getString("qualification").equalsIgnoreCase("null"))
					profileInfo.setProfileDesignation(obj.getString("qualification"));
				    if (obj.getString("phone") != null && !obj.getString("phone").equalsIgnoreCase("")
					    && !obj.getString("phone").equalsIgnoreCase("null"))
					profileInfo.setProfilePhoneNumber(obj.getString("phone"));
				    if (obj.getString("email") != null && !obj.getString("email").equalsIgnoreCase("")
					    && !obj.getString("email").equalsIgnoreCase("null"))
					profileInfo.setProfileEmail(obj.getString("email"));
				    if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").trim().equalsIgnoreCase("")
					    && !obj.getString("pic").trim().equalsIgnoreCase("null"))
					profileInfo.setProfileImage(obj.getString("pic").trim());
				    
				    if (obj.has("second_image") && obj.getString("second_image") != null
						&& !obj.getString("second_image").trim().equalsIgnoreCase("")
					    && !obj.getString("second_image").trim().equalsIgnoreCase(null)) {
					profileInfo.setSecondImage(obj.getString("second_image").trim());
				    }
				    
				    if (obj.has("distance") && obj.getString("distance") != null && !obj.getString("distance").equalsIgnoreCase("")
					    && !obj.getString("distance").equalsIgnoreCase("99999"))
					profileInfo.setProfileDistance(obj.getString("distance"));
				    if (obj.has("awardRecog") && obj.getString("awardRecog") != null
					    && !obj.getString("awardRecog").equalsIgnoreCase("")
					    && !obj.getString("awardRecog").equalsIgnoreCase(null))
					profileInfo.setProfileAwards(obj.getString("awardRecog"));
				    if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
					    && !obj.getString("avgrate").equalsIgnoreCase(null) && !obj.getString("avgrate").equalsIgnoreCase("null"))
					profileInfo.setProfileRating(obj.getString("avgrate"));

				    if (obj.has("hospitalName") && obj.getString("hospitalName") != null
					    && !obj.getString("hospitalName").equalsIgnoreCase("")
					    && !obj.getString("hospitalName").equalsIgnoreCase(null)) {
					String hospitalName;
					if (obj.getString("hospitalName").toString().contains(",")) {

					    hospitalName = obj.getString("hospitalName").toString().split(",")[0];
					} else {
					    hospitalName = obj.getString("hospitalName").toString();
					}
					profileInfo.setHospitalName(hospitalName.replace("?", ""));
				    }
				    if (AppConstants.LogLevel == 1) {
					try {
					    if (obj.has("server_processtime") && obj.getString("server_processtime") != null
					    	&& !obj.getString("server_processtime").equalsIgnoreCase("")
					    	&& !obj.getString("server_processtime").equalsIgnoreCase(null)) {
					        ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, obj.getString("server_processtime"));
					    }
					} catch (Exception e) {
					    e.printStackTrace();
					}
				    }
				    physiciansList.add(profileInfo);
				}
			    }

			    if (physiciansList != null && physiciansList.size() > 0) {

				if(isPullToRefreshFromEnd){
				    isPullToRefreshFromEnd = false;
				    physiciansListView.setRefreshing();
				}
				physiciansListView.onRefreshComplete();
				physiciansListView.setMode(Mode.DISABLED);
				profileAdapter = new ProfileAdapter(GeneralPhysicansActivity.this, R.layout.profile_list_item, physiciansList);
				physiciansListView.setAdapter(profileAdapter);
                                searchText(physiciansList);
				isAllDataAvailable = true;

			    }

			} else {
			    
			     Toast.makeText(GeneralPhysicansActivity.this,"No Records Found", Toast.LENGTH_SHORT) .show();
			     
			}
		    } else {
			 Toast.makeText(GeneralPhysicansActivity.this,"No Records Found", Toast.LENGTH_SHORT) .show();
		    }
		} catch (JSONException e) {
		    e.printStackTrace();
		}
		if (AppConstants.LogLevel == 1) {
		    postServerTimepend = Utils.postServerTimeSpend();
		    totalScreenTimeSpend = Utils.totalScreenTimeSpend();
		    Utils.callPerfomanceTestingApi("GeneralPhysicans All List", "GeneralPhysicansActivity", "listgenphysician", preServerTimeSpend,
			    serverTimeSpend, postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(GeneralPhysicansActivity.this),
			    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
		}
	    }

	}
	new GetPhysicianList().execute();

    }

	// method for top rated API calling
    public void TopRatedphysiciansDetails() {
	final class GetTopRatedPhysicianList extends AsyncTask<Void, Void, String> {

	    ProgressDialog pdia;

	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		pdia = new ProgressDialog(GeneralPhysicansActivity.this);
		pdia.setCancelable(false);
		pdia.setMessage(Utils.getCustomeFontStyle(GeneralPhysicansActivity.this, getString(R.string.screenLoading)));
		pdia.show();
	    }

	    // 200793653151
	    @Override
	    protected String doInBackground(Void... arg0) {

		JSONParser jparser = new JSONParser();

		String latitude = ApplicationSettings.getPref(AppConstants.LATITUDE, "");
		String longitude = ApplicationSettings.getPref(AppConstants.LONGITUDE, "");
		String result = jparser.getGeneralPhysiciansTopRated(latitude, longitude);
		return result.toString();
	    }

	    @Override
	    protected void onPostExecute(String result) {
		pdia.dismiss();
		try {
		    if (result != null) {
			physiciansList = new ArrayList<ProfileInfo>();
			JSONArray jsonarray = new JSONArray(result.toString());

			if (jsonarray != null && jsonarray.length() > 0) {
			    for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject obj = jsonarray.getJSONObject(i);
				ProfileInfo profileInfo = new ProfileInfo();
				profileInfo.setProfileId(obj.getString("id"));
				String fullName = null;
				String firstName = obj.getString("firstname");
				String middlename = obj.getString("middlename");

				String lastname = obj.getString("lastname");

				if (firstName != null && !firstName.equalsIgnoreCase("") && !firstName.equalsIgnoreCase(null)
					&& !firstName.equalsIgnoreCase("null")) {
				    fullName = firstName;
				}
				if (middlename != null && !middlename.equalsIgnoreCase("") && !middlename.equalsIgnoreCase(null)
					&& !middlename.equalsIgnoreCase("null")) {
				    fullName = fullName + " " + middlename;
				}
				if (lastname != null && !lastname.equalsIgnoreCase("") && !lastname.equalsIgnoreCase("null")) {
				    fullName = fullName + " " + lastname;
				}
				profileInfo.setProfileName(fullName.toString().replace("?", ""));
				if (obj.getString("qualification") != null && !obj.getString("qualification").equalsIgnoreCase("")
					&& !obj.getString("qualification").equalsIgnoreCase("null"))
				    profileInfo.setProfileDesignation(obj.getString("qualification"));
				if (obj.getString("phone") != null && !obj.getString("phone").equalsIgnoreCase("")
					&& !obj.getString("phone").equalsIgnoreCase("null"))
				    profileInfo.setProfilePhoneNumber(obj.getString("phone"));
				if (obj.getString("email") != null && !obj.getString("email").equalsIgnoreCase("")
					&& !obj.getString("email").equalsIgnoreCase("null"))
				    profileInfo.setProfileEmail(obj.getString("email"));
				if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").equalsIgnoreCase("")
					&& !obj.getString("pic").equalsIgnoreCase("null"))
				    profileInfo.setProfileImage(obj.getString("pic"));
				if (obj.has("distance") && obj.getString("distance") != null && !obj.getString("distance").equalsIgnoreCase("")
					&& !obj.getString("distance").equalsIgnoreCase("99999"))
				    profileInfo.setProfileDistance(obj.getString("distance"));
				if (obj.has("awardRecog") && obj.getString("awardRecog") != null && !obj.getString("awardRecog").equalsIgnoreCase("")
					&& !obj.getString("awardRecog").equalsIgnoreCase(null))
				    profileInfo.setProfileAwards(obj.getString("awardRecog"));
				if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
					&& !obj.getString("avgrate").equalsIgnoreCase(null))
				    profileInfo.setProfileRating(obj.getString("avgrate"));

				if (obj.has("hospitalName") && obj.getString("hospitalName") != null
					&& !obj.getString("hospitalName").equalsIgnoreCase("")
					&& !obj.getString("hospitalName").equalsIgnoreCase(null)) {
				    String hospitalName;
				    if (obj.getString("hospitalName").toString().contains(",")) {

					hospitalName = obj.getString("hospitalName").toString().split(",")[0];
				    } else {
					hospitalName = obj.getString("hospitalName").toString();
				    }
				    profileInfo.setHospitalName(hospitalName.replace("?", ""));

				}
				if (AppConstants.LogLevel == 1) {
				    if (obj.has("server_processtime") && obj.getString("server_processtime") != null
					    && !obj.getString("server_processtime").equalsIgnoreCase("")
					    && !obj.getString("server_processtime").equalsIgnoreCase(null)) {
					try {
					    ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, obj.getString("server_processtime"));

					} catch (Exception e) {
					    e.printStackTrace();
					}
				    }
				}
				
				physiciansList.add(profileInfo);
			    }

			    if (physiciansList != null && physiciansList.size() > 0) {
				profileAdapter = new ProfileAdapter(GeneralPhysicansActivity.this, R.layout.profile_list_item, physiciansList);
				physiciansListView.setAdapter(profileAdapter);

			    }

			} else {
			    Toast.makeText(GeneralPhysicansActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
			}
		    } else {
			Toast.makeText(GeneralPhysicansActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
		    }
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }

	}
	new GetTopRatedPhysicianList().execute();

    }

	// click event for general physicians list
    public void clickEvent() {

	physiciansListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ProfileInfo entry = (ProfileInfo) parent.getItemAtPosition(position);
		if (parent != null && entry != null) {
		    ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getProfileId());
		    ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY, gpTextView.getText().toString());
		    easyTracker.send(MapBuilder.createEvent("Doctor List", "View Doctor",
			    gpTextView.getText().toString() + ":" + entry.getProfileName(), null).build());

		    isEventPressed = true;
		    startActivity(new Intent(GeneralPhysicansActivity.this, DoctorDetailsActivity.class));
		}
	    }
	});

	// call pull to refresh
	physiciansListView.setMode(Mode.DISABLED);
	physiciansListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
	    @Override
	    public void onRefresh(PullToRefreshBase<ListView> refreshView) {

		if (!isAllDataAvailable) {
		    if (isNetworkAvailable()) {
			physiciansListView.setMode(Mode.PULL_FROM_END);
			isPullToRefreshFromEnd = true;
			easyTracker.send(MapBuilder.createEvent("Lazy Loading", "Lazy Loading Doctor List", gpTextView.getText().toString(),
				null).build());

			//physiciansDetailsLoadMore("-1", true);
			ApplicationSettings.putPref(AppConstants.OFFSET_PREVIOUS_VALUE, physiciansList.size());

		    } else {
			physiciansListView.onRefreshComplete();
			physiciansListView.setMode(Mode.DISABLED);
			Toast.makeText(GeneralPhysicansActivity.this, "Network not Available", Toast.LENGTH_SHORT).show();
		    }
		} else {
		    physiciansListView.setMode(Mode.DISABLED);
		}
	    }
	});
    }

	// check network connection
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

	/*// click event for near me view and top rated view
	public void clickTextViews() {
		nearmeTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//easyTracker.send(MapBuilder.createEvent(gpTextView.getText().toString(), "NEAR ME", "Empty", null).build());
				//nearbyView.setVisibility(View.VISIBLE);
				//topratedView.setVisibility(View.INVISIBLE);

				if (isNetworkAvailable()) {
					// physiciansDetails(AppConstants.OFFSET_DEFAULT_VALUE);
					getPhysicianList = new GetPhysicianList();
					getPhysicianList.execute();
				} else {
					Toast.makeText(GeneralPhysicansActivity.this, "Network not Available", Toast.LENGTH_SHORT).show();
				}
			}
		});
		topratedTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//easyTracker.send(MapBuilder.createEvent(gpTextView.getText().toString(), "TOP RATED", "Empty", null).build());
				//nearbyView.setVisibility(View.INVISIBLE);
				//topratedView.setVisibility(View.VISIBLE);
				if (isNetworkAvailable()) {
					TopRatedphysiciansDetails();
				} else {
					Toast.makeText(GeneralPhysicansActivity.this, "Network not Available", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}*/

	/*// click event for home arrow in action bar
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 && item.getTitleCondensed() != null) {
			item.setTitleCondensed(item.getTitleCondensed().toString());
		}

		switch (item.getItemId()) {

		case android.R.id.home:
			googleAnalyticsForNoSelection("Back");
			GeneralPhysicansActivity.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

	// search query called here
	public void searchPhysicians(final AutoCompleteTextView editsearch) {
		// Capture Text in EditText
		editsearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
				if (!text.startsWith("com.medinfi.datainfo")) {
					isTrueToast = false;
					if (text != null && text.length() > 2) {
						callSearchAPI(text);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}
		});
	}

	/*
	 * // enable and disable pulltorefresh from the adapter once the query
	 * results // populated public static void CheckEnabling() { int countValue
	 * = Integer.parseInt(ApplicationSettings.getPref( AppConstants.LAST_COUNT,
	 * "")); if (countValue > 19) {
	 * physiciansListView.setMode(Mode.PULL_FROM_END); } else {
	 * physiciansListView.setMode(Mode.DISABLED); } }
	 */
    @Override
    protected void onResume() {
	super.onResume();
	if (!isEventPressed) {
	    if (isHomePressed) {
		easyTracker.send(MapBuilder.createEvent("Doctor List", "Device Home", "Device Home", null).build());
		
	    }
	   // if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	}
    }

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
		FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
		 FlurryAgent.logEvent("GeneralPhysicans Activity");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		  FlurryAgent.onEndSession(this);
	isHomePressed = true;
		EasyTracker.getInstance(this).activityStop(this);
	}

    public void callSearchAPI(final String keyword) {

	new AsyncTask<Void, Void, String>() {

	    @Override
	    protected String doInBackground(Void... params) {

		String result = null;
		try {
		    JSONParser jparser = new JSONParser();

		    String latitude = ApplicationSettings.getPref(AppConstants.LATITUDE, "");
		    String longitude = ApplicationSettings.getPref(AppConstants.LONGITUDE, "");
		    if (keyword != null && keyword.length() > 2) {
			result = jparser.getAllSearch(keyword, latitude, longitude,
				ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, ""), "1");
		    }

		} catch (Exception e) {
		    // TODO Auto-generated catch block
		}
		return result.toString();
	    }

	    protected void onPostExecute(String result) {
		if (result != null) {
		    hideKeyboard();
		    /* {"status":"Fail","message":"No Results Found!"} */

		    JSONObject jObject = null;
		    try {
			jObject = new JSONObject(result);
		    } catch (JSONException e1) {
			e1.printStackTrace();
		    }
		    try {
			if (jObject != null && jObject.has("status") && jObject.getString("status") != null
				&& jObject.getString("status").equalsIgnoreCase("Fail")) {

			    if (!isTrueToast) {

				easyTracker.send(MapBuilder.createEvent("Doctor List", " Search Doctor",
					gpTextView.getText().toString() + " No search result ", null).build());

				toast = Toast.makeText(GeneralPhysicansActivity.this, "", Toast.LENGTH_SHORT);
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
					AutoCompleteInfo autoCompleteInfo = new AutoCompleteInfo();
					autoCompleteInfo.setId(jsonObject.getString("id"));
					autoCompleteInfo.setName("Dr " + jsonObject.getString("name").replace("?", ""));

					searchList.add(jsonObject.getString("name"));
					autocompleteList.add(autoCompleteInfo);
				    }
				    if (autocompleteList != null && autocompleteList.size() > 0) {

					// new AsyncTask<Void, Void,
					// String>(){}.execute();

					isTrueToast = false;
					autoCompleteAdapter = new AutoCompleteAdapter(GeneralPhysicansActivity.this, R.layout.autocompletesearch,
						autocompleteList,"");

					searchText.setAdapter(autoCompleteAdapter);
					autoCompleteAdapter.notifyDataSetChanged();

					searchText.setOnItemClickListener(new OnItemClickListener() {

					    @Override
					    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if (toast != null) {
						    toast.cancel();
						    toast = null;
						}

						searchText.setText("");
						searchLayout.setVisibility(View.GONE);
						searchImageView.setVisibility(View.VISIBLE);
						titleLayout.setVisibility(View.VISIBLE);
						AutoCompleteInfo entry = (AutoCompleteInfo) parent.getItemAtPosition(position);
						if (parent != null && entry != null) {
						    easyTracker.send(MapBuilder.createEvent("Doctor List", " View Doctor from Search Result",
							    gpTextView.getText().toString() + "," + entry.getName(), null).build());
						    ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getId());
						    ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY, gpTextView.getText().toString());

						    startActivity(new Intent(GeneralPhysicansActivity.this, DoctorDetailsActivity.class));
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

		searchPhysicians(searchText);
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
				if(searchText!= null && searchText.getText().toString().length() > 0){
					easyTracker.send(MapBuilder.createEvent("Doctor List","Close" , searchText.getText().toString(), null).build());
				}else{
					easyTracker.send(MapBuilder.createEvent("Doctor List","Close" , "Close", null).build());
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
				if(searchText!= null && searchText.getText().toString().length() > 0){
					easyTracker.send(MapBuilder.createEvent("Doctor List","Back" , searchText.getText().toString(), null).build());
				}else{
					easyTracker.send(MapBuilder.createEvent("Doctor List","Back" , "Back", null).build());
				}
				GeneralPhysicansActivity.this.finish();

			}
		});
		actionbarTitle = (TextView) mCustomView.findViewById(R.id.title);
		actionbarSubtitle = (TextView) mCustomView.findViewById(R.id.subtitle);

		ImageView imageButton = (ImageView) mCustomView.findViewById(R.id.imageButton);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				GeneralPhysicansActivity.this.finish();
			}
		});

		getSupportActionBar().setCustomView(mCustomView);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

	}*/

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (toast != null) {
			toast.cancel();
			toast = null;
			isTrueToast = true;

		}
	}

	/* handle back button event */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (isTrue) {

			pdia.setCancelable(true);
			getPhysicianList.cancel(true);
			finish();
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isTrue = false;
		if (pdia != null && pdia.isShowing())
			pdia.dismiss();
	}

	/* listener for dialog cancel in thread */
	OnCancelListener cancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface arg0) {
			isTrue = true;

			GeneralPhysicansActivity.this.finish();
		}
	};

	/* handle back button event */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
	    isEventPressed = true;
			if (getPhysicianList != null
					&& getPhysicianList.getStatus() == Status.RUNNING)
				getPhysicianList.cancel(true);
			if (pdia != null && pdia.isShowing())
				pdia.dismiss();
			
			easyTracker.send(MapBuilder.createEvent("Doctor List", "Device Back", "Device Back", null).build());
			GeneralPhysicansActivity.this.finish();

		}

		return super.onKeyDown(keyCode, event);
	}

	/*
	 * public void sortData(String charText) { for (AutoCompleteInfo wp :
	 * autocompleteList) { if (
	 * (wp.getName().toLowerCase(Locale.getDefault()).startsWith(charText)) ) {
	 * autocompleteList.add(wp); } } for (AutoCompleteInfo wp :
	 * autocompleteList) { if (
	 * (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) ) {
	 * autocompleteList.add(wp); } }
	 * 
	 * Object[] st = autocompleteList.toArray(); for (Object s : st) { if
	 * (autocompleteList.indexOf(s) != autocompleteList.lastIndexOf(s)) {
	 * autocompleteList.remove(autocompleteList.lastIndexOf(s)); } }
	 * 
	 * 
	 * }
	 */
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
	public void searchGeneralPhysician(final EditText editsearch){
	// Capture Text in EditText
	editsearch.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		strSearchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		if(strSearchText!=null && strSearchText.length() == 3 ){
		    easyTracker.send(MapBuilder.createEvent("Doctor List", "Search Doctor", strSearchText, null).build());
		}
		if (physiciansList != null) {
		   /* if (profileAdapter != null){
			profileAdapter.filter(strSearchText);*/
		    searchText(physiciansList);
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

    private void searchText(ArrayList<ProfileInfo> arrListFilteredItems) {
	try {
	    ArrayList<ProfileInfo> arrListFilter = new ArrayList<ProfileInfo>();
	    if (!strSearchText.equals("")) {
		//Search for First Name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {

		    if ((arrListFilteredItems.get(i).getFirstName() != null && arrListFilteredItems.get(i).getFirstName()
			    .toLowerCase(Locale.getDefault()).trim().startsWith(strSearchText)) 
			    || arrListFilteredItems.get(i).getProfileName() != null && arrListFilteredItems.get(i).getProfileName()
				    .toLowerCase(Locale.getDefault()).startsWith(strSearchText)) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for Middle Name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getMiddleName() != null && arrListFilteredItems.get(i).getMiddleName()
			    .toLowerCase(Locale.getDefault()).trim().startsWith(strSearchText)
			    || arrListFilteredItems.get(i).getProfileName() != null && arrListFilteredItems.get(i).getProfileName()
			    .toLowerCase(Locale.getDefault()).startsWith(strSearchText))) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}

		    }

		}
		//Search for Last name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getLastName() != null && arrListFilteredItems.get(i).getLastName()
			    .toLowerCase(Locale.getDefault()).trim().startsWith(strSearchText)
			    || arrListFilteredItems.get(i).getProfileName() != null && arrListFilteredItems.get(i).getProfileName()
			    .toLowerCase(Locale.getDefault()).startsWith(strSearchText))) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for First name + Middle name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getFirstName() != null && arrListFilteredItems.get(i).getMiddleName() != null && (arrListFilteredItems
			    .get(i).getFirstName().toLowerCase(Locale.getDefault()).trim()
			    + " " + arrListFilteredItems.get(i).getMiddleName().toLowerCase(Locale.getDefault()).trim()).startsWith(strSearchText))) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for Middle name + Last name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getMiddleName() != null && arrListFilteredItems.get(i).getLastName() != null && (arrListFilteredItems
			    .get(i).getMiddleName().toLowerCase(Locale.getDefault()).trim()
			    + " " + arrListFilteredItems.get(i).getLastName().toLowerCase(Locale.getDefault()).trim()).startsWith(strSearchText))) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for First name + Last Name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getFirstName() != null && arrListFilteredItems.get(i).getLastName() != null && (arrListFilteredItems
			    .get(i).getFirstName().toLowerCase(Locale.getDefault()).trim()
			    + " " + arrListFilteredItems.get(i).getLastName().toLowerCase(Locale.getDefault()).trim()).startsWith(strSearchText))) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for Last name + Middle name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getLastName() != null && arrListFilteredItems.get(i).getMiddleName() != null
			    && (arrListFilteredItems.get(i).getLastName()
			    .toLowerCase(Locale.getDefault()).trim()+" "+arrListFilteredItems.get(i).getMiddleName().toLowerCase(Locale.getDefault()).trim()).startsWith(strSearchText)
			    )) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for Last name + First Name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getLastName() != null && arrListFilteredItems.get(i).getFirstName() != null && (arrListFilteredItems
			    .get(i).getLastName().toLowerCase(Locale.getDefault()).trim()
			    + " " + arrListFilteredItems.get(i).getFirstName().toLowerCase(Locale.getDefault()).trim()).startsWith(strSearchText))) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for Middle name + First Name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getMiddleName() != null && arrListFilteredItems.get(i).getFirstName() != null && (arrListFilteredItems
			    .get(i).getMiddleName().toLowerCase(Locale.getDefault()).trim()
			    + " " + arrListFilteredItems.get(i).getFirstName().toLowerCase(Locale.getDefault()).trim()).startsWith(strSearchText))) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for Last name + Middle Name + First Name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getLastName() != null && arrListFilteredItems.get(i).getMiddleName() != null && arrListFilteredItems.get(i).getFirstName() != null
			    &&(arrListFilteredItems.get(i).getLastName().toLowerCase(Locale.getDefault()).trim()
			    + " " + arrListFilteredItems.get(i).getMiddleName().toLowerCase(Locale.getDefault()).trim()
			    +" "+arrListFilteredItems.get(i).getFirstName().toLowerCase(Locale.getDefault()).trim()).startsWith(strSearchText))) {
			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		//Search for Hospital name
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getHospitalName() != null && arrListFilteredItems.get(i).getHospitalName()
			    .toLowerCase(Locale.getDefault()).trim().startsWith(strSearchText))) {
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
    private void setAdapter(ArrayList<ProfileInfo> physiciansList){
	if(physiciansList.size() > 0){
	    txtNoResultFound.setVisibility(View.GONE);
	    physiciansListView.setVisibility(View.VISIBLE);
	    profileAdapter = new ProfileAdapter(GeneralPhysicansActivity.this, R.layout.profile_list_item, physiciansList);
	    physiciansListView.setAdapter(profileAdapter);
	}else{
	    physiciansListView.setVisibility(View.GONE);
	    txtNoResultFound.setVisibility(View.VISIBLE);
	}
	
    }
    public static GeneralPhysicansActivity getInstance() {
	return generalPhysicansScreen;
    }
    
    private void tabClickEvent(){
    	RelativeLayout hospital,doctor,profile;
    	ImageView doctor_iv;
    	TextView doctor_tv;
    	View doctor_view;
    	hospital= (RelativeLayout)findViewById(R.id.hospital);
    	doctor= (RelativeLayout)findViewById(R.id.doctor);
    	profile= (RelativeLayout)findViewById(R.id.profile);
    	doctor_iv= (ImageView)findViewById(R.id.doctor_iv);
    	doctor_tv= (TextView)findViewById(R.id.doctor_tv);
    	doctor_view=findViewById(R.id.doctor_view);
    	
    	doctor_view.setVisibility(View.VISIBLE);
    	doctor_iv.setBackgroundResource(R.drawable.doctor_on);
    	doctor_tv.setTextColor(getResources().getColor(R.color.screen_header));
    	doctor_tv.setTypeface(Typeface.DEFAULT_BOLD);
    			
    	RelativeLayout layoutLocation = (RelativeLayout)findViewById(R.id.layoutLocation);
	 layoutLocation.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Doctor List", "Update Location", "Update Location", null).build());
		startLocationUpdate(); 
	    }
	});
	 
	 hospital.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			 easyTracker.send(MapBuilder.createEvent("Doctor List", "Select Hospitals", "Select Hospitals", null).build());
			    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
			    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
			    // hospitalsButton.setBackgroundResource(R.drawable.hospital_icon_selected);
			    if (HospitalsActivity.getInstance() != null) {
				HospitalsActivity.getInstance().finish();
			    }
			    startActivity(new Intent(GeneralPhysicansActivity.this, HospitalsActivity.class));
		}
	});
	
	 profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
	 
   }
    
}
