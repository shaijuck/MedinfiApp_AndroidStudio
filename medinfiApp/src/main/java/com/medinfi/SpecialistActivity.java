package com.medinfi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.adapters.SpecialistAdapter;
import com.medinfi.datainfo.ProfileInfo;
import com.medinfi.datainfo.SpecialistInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;

public class SpecialistActivity extends Activity {

    /* declare xml views */
    private ListView specialistListView;
    private SpecialistAdapter specialistAdapter;
    private ArrayList<SpecialistInfo> data;
    private Typeface tfRegular;
    private TextView specialistTextView;
    private EditText searchEditText;
    private EasyTracker easyTracker = null;

    private GetSpecialistList getSpecialistList;
    private boolean isTrue = false;
    private ProgressDialog pdia;
    private String searchText;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private TextView actionbarTitle, actionbarSubtitle;
    private ImageView layoutBack;
    private static SpecialistActivity specialistScreen;

    private String preServerTimeSpend="";
    private String serverTimeSpend="";
    private String postServerTimepend="";
    private String totalScreenTimeSpend="";
    /* declare id for xml views */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.specialists);
	easyTracker = EasyTracker.getInstance(SpecialistActivity.this);
	specialistScreen = this;

	/*
	 * getSupportActionBar().show(); getSupportActionBar().setIcon(null);
	 * getSupportActionBar().setDisplayUseLogoEnabled(false);
	 * getSupportActionBar().setHomeButtonEnabled(false);
	 * getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	 */
	actionbarTitle = (TextView) findViewById(R.id.title);
	actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
	layoutBack = (ImageView) findViewById(R.id.imgCancel);
	showLocation();
	tfRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	specialistTextView = (TextView) findViewById(R.id.specialists_tv);
	specialistTextView.setTypeface(tfRegular);
	specialistListView = (ListView) findViewById(R.id.specialistslistview);
	searchEditText = (EditText) findViewById(R.id.searchedittext);
	searchEditText.setTypeface(tfRegular);
	if (AppConstants.LogLevel == 1) {
	    Utils.startPreServerCallTime = System.currentTimeMillis();
	    Utils.startTotalScreenTime = System.currentTimeMillis();
	}
	if (isNetworkAvailable()) {
	    // specialistDetails();
	    specialistListView.setVisibility(View.VISIBLE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	   
	    getSpecialistList = new GetSpecialistList();
	    getSpecialistList.execute();
	} else {
	    specialistListView.setVisibility(View.GONE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tfRegular);
	    ((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tfRegular);
	}
	isHomePressed = false;
	isEventPressed = false;
	clickEvent();
	searchSpecialist(searchEditText);
	backClickEvent();
	locationClickEvent();
    }

    private void backClickEvent() {
	layoutBack.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		easyTracker.send(MapBuilder.createEvent("Specialist List", "Back", "Back", null).build());
		SpecialistActivity.this.finish();

	    }
	});
    }
    private void locationClickEvent(){
	 LinearLayout layoutLocation = (LinearLayout)findViewById(R.id.layoutLocation);
	 layoutLocation.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		 easyTracker.send(MapBuilder.createEvent("Specialist List", "Update Location", "Update Location", null).build());
		startLocationUpdate();
	    }
	});
	 
   }
    private void startLocationUpdate() {
	Intent intent = new Intent(SpecialistActivity.this,LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
       }
    // check network
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

    // click event for specialist list item
    public void clickEvent() {
	specialistListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SpecialistInfo entry = (SpecialistInfo) parent.getItemAtPosition(position);

		easyTracker.send(MapBuilder.createEvent("Specialist List", "View Specialization", entry.getSpecialistName(), null).build());
		// entry.getSpecialistID();
		isEventPressed = true;
		if (AppConstants.LogLevel == 1) {
		    preServerTimeSpend = Utils.preServerTimeSpend();
		}
		
		Intent intent=new Intent(SpecialistActivity.this, SpecialistDoctorsActivity.class).putExtra("key_specialistID",
				entry.getSpecialistID()).putExtra("key_specialist", entry.getSpecialistName().toUpperCase().toString());
		Log.i("SpecialAct","Entry : "+entry);
		Log.i("SpecialAct","ID : "+entry.getSpecialistID());
		Log.i("SpecialAct","Name: "+entry.getSpecialistName().toUpperCase().toString());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		finish();
	    }
	});
    }

    // call api for individual specialist
    public void specialistDetails() {
    }

    public class GetSpecialistList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
		preServerTimeSpend = Utils.preServerTimeSpend();
	    }
	    pdia = new ProgressDialog(SpecialistActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(SpecialistActivity.this, getString(R.string.screenLoading)));
	    pdia.show();
	}

	// 200793653151
	@Override
	protected String doInBackground(Void... arg0) {
	    isTrue = true;
	    JSONParser jparser = new JSONParser();

	    String result = jparser.getSpecialistList();
	    if (AppConstants.LogLevel == 1) {
		serverTimeSpend = Utils.serverTimeSpend();
		Utils.startPostServerCallTime = System.currentTimeMillis();
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
		    
		    data = new ArrayList<SpecialistInfo>();

		    JSONArray jsonarray = new JSONArray(result.toString());
		    for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = jsonarray.getJSONObject(i);

			if (obj.getString("speciality") != null && !obj.getString("speciality").equalsIgnoreCase("")
				&& !obj.getString("speciality").equalsIgnoreCase("null") && !obj.getString("speciality").equalsIgnoreCase(null)
				&& obj.getString("detail") != null && !obj.getString("detail").equalsIgnoreCase("")
				&& !obj.getString("detail").equalsIgnoreCase("null") && !obj.getString("detail").equalsIgnoreCase(null)) {
			    SpecialistInfo specialistInfo = new SpecialistInfo();
			    specialistInfo.setSpecialistID(obj.getString("id"));
			    specialistInfo.setSpecialistName(obj.getString("speciality"));
			    specialistInfo.setSpecialistDescription(obj.getString("detail"));
			    if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").equalsIgnoreCase("")
				    && !obj.getString("pic").equalsIgnoreCase("null"))
				specialistInfo.setSpecialistImage(obj.getString("pic"));
			    
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
			    data.add(specialistInfo);
			}
		    }
		    if (data != null && data.size() > 0) {
			specialistAdapter = new SpecialistAdapter(SpecialistActivity.this, R.layout.specialist_list_item, data);
			specialistListView.setAdapter(specialistAdapter);
			
		    }
		    if (AppConstants.LogLevel == 1) {
			try {
			    postServerTimepend = Utils.postServerTimeSpend();
			    totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			    Utils.callPerfomanceTestingApi("Get Specialist List", "SpecialistActivity", "ListSpecial", preServerTimeSpend,
				    serverTimeSpend, postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(SpecialistActivity.this),
				    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""), AppConstants.perfTestCode);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}

    }

    /* searchSpecialist based on key */
    public void searchSpecialist(final EditText editsearch) {
	// Capture Text in EditText
	editsearch.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		searchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		if(searchText!=null && searchText.trim().length() == 3){
		    easyTracker.send(MapBuilder.createEvent("Specialist List", "Search Specialist", searchText, null).build());
		}
		if (data != null) {
		    /*
		     * if (specialistAdapter != null)
		     * specialistAdapter.filter(searchText);
		     */
		    searchText(data);
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

    @Override
    protected void onResume() {
	super.onResume();
	try {
	    if (!isEventPressed) {
		if (isHomePressed) {
		    easyTracker.send(MapBuilder.createEvent("Specialist List", "Device Back", "Device Back", null).build());
		    
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
	 FlurryAgent.logEvent("Specialist Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
	super.onStop();
	FlurryAgent.onEndSession(this);
	isHomePressed = true;
	EasyTracker.getInstance(this).activityStop(this);
    }

    /* handle back button event */
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub

	if (isTrue) {

	    pdia.setCancelable(true);
	    getSpecialistList.cancel(true);
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

	    SpecialistActivity.this.finish();
	}
    };

    /* handle back button event */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    isEventPressed = true;
	    if (getSpecialistList != null && getSpecialistList.getStatus() == Status.RUNNING)
		getSpecialistList.cancel(true);
	    if (pdia != null && pdia.isShowing())
		pdia.dismiss();
	    easyTracker.send(MapBuilder.createEvent("Specialist List", "Device Back", "Device Back", null).build());
	    SpecialistActivity.this.finish();

	}

	return super.onKeyDown(keyCode, event);
    }

    private void searchText(ArrayList<SpecialistInfo> arrListFilteredItems) {
	try {
	    ArrayList<SpecialistInfo> arrListFilter = new ArrayList<SpecialistInfo>();
	    if (!searchText.equals("")) {
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getSpecialistName() != null && arrListFilteredItems.get(i).getSpecialistName().toLowerCase(Locale.getDefault()).startsWith(searchText))
			    || (arrListFilteredItems.get(i).getSpecialistDescription() != null && arrListFilteredItems.get(i).getSpecialistDescription().toLowerCase(Locale.getDefault()).startsWith(searchText))) {

			if (!arrListFilteredItems.get(i).isAddedToFilteredList()) {
			    arrListFilteredItems.get(i).setAddedToFilteredList(true);
			    arrListFilter.add(arrListFilteredItems.get(i));
			}
		    }
		}
		for (int i = 0; i < arrListFilteredItems.size(); i++) {
		    if ((arrListFilteredItems.get(i).getSpecialistName() != null && arrListFilteredItems.get(i).getSpecialistName().toLowerCase(Locale.getDefault()).contains(searchText))
			    || (arrListFilteredItems.get(i).getSpecialistDescription() != null && arrListFilteredItems.get(i).getSpecialistDescription().toLowerCase(Locale.getDefault()).contains(searchText))) {

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

    private void setAdapter(ArrayList<SpecialistInfo> arrListFilteredItems) {
	specialistAdapter = new SpecialistAdapter(SpecialistActivity.this, R.layout.specialist_list_item, arrListFilteredItems);
	specialistListView.setAdapter(specialistAdapter);
    }
    public static SpecialistActivity getInstance(){
	return specialistScreen;
    }
}
