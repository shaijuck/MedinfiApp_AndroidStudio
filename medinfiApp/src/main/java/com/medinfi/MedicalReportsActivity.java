package com.medinfi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher.ViewFactory;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.adapters.MenuItemListAdapter;
import com.medinfi.datainfo.MedicalReportsInfo;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.SliderMenuClickListener;
import com.medinfi.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MedicalReportsActivity extends Activity {
    private EasyTracker easyTracker = null;
    private static MedicalReportsActivity medicalRecordsScreen;
    private ListView medicalRecordListView;
    private ProgressDialog pdia;
    private ArrayList<MedicalReportsInfo> medicalRecordList;
    private ArrayList<MedicalReportsInfo> medicalRecordMomList;
    private ArrayList<MedicalReportsInfo> medicalRecordDadList;
    private ArrayList<String> medicalMultileImagRecordList;
    private MedicalReportsAdapter medicalRecordsAdapter;
    private TextView actionbarTitle, actionbarSubtitle;
    private static boolean isEventPressed = false;
    private boolean isHomePressed = false;

    private ImageView imgBack;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<MenuItemInfo> menuItemInfo;
    private MenuItemListAdapter menuItemListAdapter;
    boolean isMenuOpened = false;
    private String callingScreen = "callingScreen";
    private boolean isListPopulated = false;
    private TextView txtDad;
    private TextView txtMom;
    private LinearLayout dadLayout;
    private LinearLayout momLayout;
    private boolean isDelete = false;
    private TextView txtNoRecordFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.medical_report_layout);
	easyTracker = EasyTracker.getInstance(MedicalReportsActivity.this);
	medicalRecordsScreen = this;

	initialize();
	if (getIntent().getStringExtra("CALLING_SCREEN") != null) {
	    callingScreen = getIntent().getStringExtra("CALLING_SCREEN");
	}
	showLocation();
	chechNetworkConnectivity();
	locationClickEvent();
	// listViewClickEvent();
	backClickEvent();
	isHomePressed = false;
	isEventPressed = false;
    }

    private void chechNetworkConnectivity() {
	try {
	    if (Utils.isNetworkAvailable(this)) {
		medicalRecordListView.setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
		medicalRecordList = new ArrayList<MedicalReportsInfo>();
		medicalRecordMomList = new ArrayList<MedicalReportsInfo>();
		medicalRecordDadList = new ArrayList<MedicalReportsInfo>();
		medicalMultileImagRecordList = new ArrayList<String>();
		new GetMedicalRecordsList().execute();
	    } else {
		medicalRecordListView.setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void initialize() {
	try {
	    medicalRecordListView = (ListView) findViewById(R.id.medicalRecordListView);
	    actionbarTitle = (TextView) findViewById(R.id.title);
	    actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
	    
	    txtDad = (TextView) findViewById(R.id.txtDad);
	    txtMom = (TextView) findViewById(R.id.txtMom);
	    txtNoRecordFound = (TextView) findViewById(R.id.txtNoRecordFound);
	    momLayout = (LinearLayout)findViewById(R.id.momLayout);
	    dadLayout = (LinearLayout)findViewById(R.id.dadLayout);

//	    imgBack = (ImageView) findViewById(R.id.imgBack);
//	    imgBack.setPadding(0, 0, 10, 0);
//	    imgBack.setImageResource(R.drawable.menu_icon_closed);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void onClickUploadRecords(View view) {
	isEventPressed = true;
	easyTracker.send(MapBuilder.createEvent("Parent Record", "Click & Upload","Click & Upload", null).build());
	Intent intent = new Intent(MedicalReportsActivity.this, UploadMedicalReportActivity.class);
	startActivity(intent);
	// finish();
    }

    private void backClickEvent() {
	easyTracker.send(MapBuilder.createEvent("Parent Record", "Menu","Menu", null).build());
	LinearLayout layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
	layoutBack.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
	    	easyTracker.send(MapBuilder.createEvent("Parent Record", "Back", "Back", null).build());
			MedicalReportsActivity.this.finish();
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
		easyTracker.send(MapBuilder.createEvent("Parent Record", "Update Location", "Update Location", null).build());
		startLocationUpdate();
	    }
	});
    }

    private void startLocationUpdate() {
	Intent intent = new Intent(MedicalReportsActivity.this, LocationUpdateActivity.class);
	intent.putExtra(AppConstants.CALLING_SCREEN_KEY, AppConstants.CALLING_SCREEN_VALUE);
	startActivity(intent);
    }
   
    public void onMomRecordTypeClicked(View view) {
	Utils.isMedDadSelected = false;
	setSelectedTypeMom(true);
	if (medicalRecordMomList != null && medicalRecordMomList.size() > 0) {
	    setAdapter(medicalRecordMomList);
	} else {
	    medicalRecordListView.setVisibility(View.GONE);
	    txtNoRecordFound.setVisibility(View.VISIBLE);
	    txtNoRecordFound.setText(getString(R.string.medicalMomRecordsNo_RecordFound));
	}
    }

    public void onDadRecordTypeClicked(View view) {
	Utils.isMedDadSelected = true;
	setSelectedTypeMom(false);
	if (medicalRecordDadList != null && medicalRecordDadList.size() > 0) {
	    setAdapter(medicalRecordDadList);
	} else {
	    medicalRecordListView.setVisibility(View.GONE);
	    txtNoRecordFound.setVisibility(View.VISIBLE);
	    txtNoRecordFound.setText(getString(R.string.medicalDadRecordsNo_RecordFound));
	}
    }
    public class GetMedicalRecordsList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (!isDelete) {
		pdia = new ProgressDialog(MedicalReportsActivity.this);
		pdia.setCancelable(true);
		pdia.setOnCancelListener(cancelListener);
		pdia.setMessage(Utils.getCustomeFontStyle(MedicalReportsActivity.this, getString(R.string.screenLoading)));
		pdia.show();
	    }
	      
	}

	@Override
	protected String doInBackground(Void... arg0) {
	    String result = null;
	    JSONParser jparser = new JSONParser();
	    try {

		result = jparser.getMedicalRecordsList();
		/*
		 * if (AppConstants.LogLevel == 1) { serverTimeSpend =
		 * Utils.serverTimeSpend(); Utils.startPostServerCallTime =
		 * System.currentTimeMillis(); }
		 */

	    } catch (Exception e) {
		// TODO Auto-generated catch block
	    }
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	    if (pdia != null && pdia.isShowing()){
		pdia.dismiss();
		isDelete = false;
	    }
		
	    try {
		if (result != null) {

		    medicalRecordList.clear();
		    medicalRecordMomList.clear();
		    medicalRecordDadList.clear();
		    medicalMultileImagRecordList.clear();
		    JSONObject jsonObject = new JSONObject(result.toString());

		    JSONArray jsonarray = new JSONArray(jsonObject.getString("MedicalRecords"));
		    /*
		     * if (AppConstants.LogLevel == 1) { try {
		     * ApplicationSettings
		     * .putPref(AppConstants.SERVER_PROCESSTIME,
		     * jsonObject.getString("server_processtime")); } catch
		     * (Exception e) { e.printStackTrace(); } }
		     */
		    for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = jsonarray.getJSONObject(i);
			MedicalReportsInfo medicalInfo = new MedicalReportsInfo();
			if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {
			    medicalInfo.setMedRecordId(obj.getString("id"));
			    if (obj.has("hospital_name") && obj.getString("hospital_name") != null
				    && !obj.getString("hospital_name").equalsIgnoreCase("") && !obj.getString("hospital_name").equalsIgnoreCase(null)) {
				medicalInfo.setMedRecordHospitalName(obj.getString("hospital_name").replace("?", ""));
			    }
			    if (obj.has("doctor_name") && obj.getString("doctor_name") != null && !obj.getString("doctor_name").equalsIgnoreCase("")
				    && !obj.getString("doctor_name").equalsIgnoreCase(null)) {
				medicalInfo.setMedRecordDoctorName(obj.getString("doctor_name").replace("?", ""));
			    }
			    if (obj.has("created_at") && obj.getString("created_at") != null && !obj.getString("created_at").equalsIgnoreCase("")
				    && !obj.getString("created_at").equalsIgnoreCase(null)) {
				medicalInfo.setMedRecordCreatedDate(Utils.getDate(obj.getString("created_at").replace("?", "")));
			    }

			    if (obj.has("record_date") && obj.getString("record_date") != null && !obj.getString("record_date").equalsIgnoreCase("")
				    && !obj.getString("record_date").equalsIgnoreCase(null)) {
				medicalInfo.setMedRecordRecordDate(Utils.getDate(obj.getString("record_date").replace("?", "")));
			    }
			    if (obj.has("record_type") && obj.getString("record_type") != null && !obj.getString("record_type").equalsIgnoreCase("")
				    && !obj.getString("record_type").equalsIgnoreCase(null)) {
				medicalInfo.setMedRecordRecordType(obj.getString("record_type").replace("?", ""));
			    }
			    if (obj.has("relation_type") && obj.getString("relation_type") != null
				    && !obj.getString("relation_type").equalsIgnoreCase("") && !obj.getString("relation_type").equalsIgnoreCase(null)) {
				medicalInfo.setMedRecordRelationType(obj.getString("relation_type").replace("?", ""));
			    }

			    JSONArray recordImagesArray = null;
			    if (obj.has("record_images") && obj.getString("record_images") != null
				    && !obj.getString("record_images").equalsIgnoreCase("") && !obj.getString("record_images").equalsIgnoreCase(null)) {
				recordImagesArray = new JSONArray(obj.getString("record_images"));
			    }

			    if (recordImagesArray != null && recordImagesArray.length() > 0 && !recordImagesArray.equals("null")) {
				for (int j = 0; j < recordImagesArray.length(); j++) {
				    if (recordImagesArray.getString(j) != null && !recordImagesArray.getString(j).equals("")) {
					medicalMultileImagRecordList.add(recordImagesArray.getString(j).trim());
					if (recordImagesArray.length() == 1) {
					    medicalInfo.setMedRecordImageFirst(recordImagesArray.getString(0).trim());
					} else if (recordImagesArray.length() == 2) {
					    medicalInfo.setMedRecordImageFirst(recordImagesArray.getString(0).trim());
					    medicalInfo.setMedRecordImageSecond(recordImagesArray.getString(1).trim());
					}

				    }
				}

			    }
			    medicalInfo.setMedRecordMultiplePic(medicalMultileImagRecordList);
			   // medicalRecordList.add(medicalInfo);
			    if(medicalInfo.getMedRecordRelationType().equalsIgnoreCase("MOM")){
				 medicalRecordMomList.add(medicalInfo);
			    }else{
				 medicalRecordDadList.add(medicalInfo);
			    }
			}
		    }
		    if (Utils.isMedDadSelected) {
			if (medicalRecordDadList != null && medicalRecordDadList.size() > 0) {
			    setSelectedTypeMom(false);
			    setAdapter(medicalRecordDadList);
			}else if(medicalRecordMomList != null && medicalRecordMomList.size() > 0){
			    setSelectedTypeMom(true);
			    setAdapter(medicalRecordMomList);
			}else {
			    medicalRecordListView.setVisibility(View.GONE);
			    txtNoRecordFound.setVisibility(View.VISIBLE);
			}
		    } else {
			if (medicalRecordMomList != null && medicalRecordMomList.size() > 0) {
			    setSelectedTypeMom(true);
			    setAdapter(medicalRecordMomList);

			} else if (medicalRecordDadList != null && medicalRecordDadList.size() > 0) {
			    setSelectedTypeMom(false);
			    setAdapter(medicalRecordDadList);
			} else {
			    medicalRecordListView.setVisibility(View.GONE);
			    txtNoRecordFound.setVisibility(View.VISIBLE);
			}
		    }
		    
		    if (ApplicationSettings.getPref(AppConstants.SHOW_THANKS_MESSAGE, false)) {
			Utils.customThankYouDialog(MedicalReportsActivity.this, getString(R.string.medicalRecordSubmitted),
				getString(R.string.medicalReportThankYou));
		    }

		}

	    } catch (JSONException e) {
		medicalRecordListView.setVisibility(View.GONE);
		txtNoRecordFound.setVisibility(View.VISIBLE);
	    }
	   
	}

    }

    private void setSelectedTypeMom(boolean isMom) {
	if (isMom) {
	    dadLayout.setBackgroundResource(R.drawable.rectangle_bg);
	    momLayout.setBackgroundResource(R.drawable.rectangle_bg_selected);
	    txtDad.setTextColor(getResources().getColor(R.color.black));
	    txtMom.setTextColor(getResources().getColor(R.color.screen_header));
	} else {
	    dadLayout.setBackgroundResource(R.drawable.rectangle_bg_selected);
	    momLayout.setBackgroundResource(R.drawable.rectangle_bg);
	    txtDad.setTextColor(getResources().getColor(R.color.screen_header));
	    txtMom.setTextColor(getResources().getColor(R.color.black));
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
		"MedicalRecordMenu"));
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
	    MedicalReportsActivity.this.finish();
	}
    };

    @Override
    protected void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("MedicalReports Activity");
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
		 easyTracker.send(MapBuilder.createEvent("Parent Record", "Device Home", "Device Home", null).build());
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
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    /* back button called */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    isEventPressed = true;
	    easyTracker.send(MapBuilder.createEvent("Parent Record", "Device Back","Device Back", null).build());
	    if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerList)) {
		isMenuOpened = false;
		mDrawerLayout.closeDrawer(mDrawerList);
		imgBack.setImageResource(R.drawable.menu_icon_closed);
	    } else {
		MedicalReportsActivity.this.finish();
	    }
	    return true;
	}
	return false;
    }

    public static MedicalReportsActivity getInstance() {
	return medicalRecordsScreen;
    }

    private void setAdapter(ArrayList<MedicalReportsInfo> medicalRecordList) {
	medicalRecordListView.setVisibility(View.VISIBLE);
	medicalRecordsAdapter = new MedicalReportsAdapter(MedicalReportsActivity.this, R.layout.medical_report_items, medicalRecordList);
	medicalRecordListView.setAdapter(medicalRecordsAdapter);
    }

    /**
     * Create custom dialog
     */
    private ViewFlipper viewFlipper;
    private float lastX;

    class MedicalReportsAdapter extends ArrayAdapter<MedicalReportsInfo> {

	/* Apdater used to load the Hospitals information in the listview */
	Context context;
	int layoutResourceId;
	public ArrayList<MedicalReportsInfo> medicalRecordsList;
	private ArrayList<MedicalReportsInfo> arraylist;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	private Typeface tfBold, tfRegular, tfLite, tfCondensed;
	private boolean isFirstImagesDownloaded = false;
	private boolean isSecondImagesDownloaded = false;

	public MedicalReportsAdapter(Context context, int layoutResourceId, ArrayList<MedicalReportsInfo> medicalRecordsList) {
	    super(context, layoutResourceId, medicalRecordsList);
	    this.layoutResourceId = layoutResourceId;
	    this.context = context;
	    this.medicalRecordsList = medicalRecordsList;
	    this.arraylist = new ArrayList<MedicalReportsInfo>();
	    this.arraylist.addAll(medicalRecordsList);
	   
	    imageLoader = ImageLoader.getInstance();

	    options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		    .showImageForEmptyUri(R.drawable.prescription_icon).showImageOnFail(R.drawable.prescription_icon)
		    .showImageOnLoading(R.drawable.prescription_icon).build();

	    tfBold = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoBold.ttf");
	    tfLite = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoLight.ttf");
	    tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	    tfCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed.ttf");
	}

	// get the object based on array position
	@Override
	public MedicalReportsInfo getItem(int position) {
	    return medicalRecordsList.get(position);
	}

	/* get the id based on array position */
	@Override
	public long getItemId(int position) {
	    return position;
	}

	/*
	 * add the view based on the count of arraylist set the values using
	 * getter setter methods
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    View row = convertView;
	    MedicalRecordHolder holder = null;

	    if (row == null) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new MedicalRecordHolder();

		holder.imgRecords = (ImageView) row.findViewById(R.id.imgRecords);
		holder.txtDoctorName = (TextView) row.findViewById(R.id.txtDoctorName);
		holder.txtHospitalName = (TextView) row.findViewById(R.id.txtHospitalName);
		holder.txtCreatedDate = (TextView) row.findViewById(R.id.txtCreatedDate);
		holder.imgDownload = (ImageView) row.findViewById(R.id.imgDownload);
		holder.imgRecordsType = (ImageView) row.findViewById(R.id.imgRecordsType);
		holder.imgRelationType = (ImageView) row.findViewById(R.id.imgRelationType);
		holder.deletMedRecord = (ImageView) row.findViewById(R.id.deletMedRecord);

		holder.txtDoctorName.setVisibility(View.GONE);
		holder.txtHospitalName.setVisibility(View.GONE);

		row.setTag(holder);
	    } else {
		holder = (MedicalRecordHolder) row.getTag();
	    }

	    MedicalReportsInfo medicalRecordsInfo = medicalRecordsList.get(position);
	    if (medicalRecordsList.get(position).getMedRecordImageFirst() != null) {
		// for (int i = 0; i < iSize; i++) {
		imageLoader.displayImage(medicalRecordsList.get(position).getMedRecordImageFirst(), holder.imgRecords, options);

		// }

		if (medicalRecordsInfo.getMedRecordRecordType() != null
			&& medicalRecordsInfo.getMedRecordRecordType().equalsIgnoreCase("Prescription")) {
		    holder.imgRecordsType.setImageResource(R.drawable.prescription_icon);
		} else if (medicalRecordsInfo.getMedRecordRecordType() != null
			&& medicalRecordsInfo.getMedRecordRecordType().equalsIgnoreCase("Reports")) {
		    holder.imgRecordsType.setImageResource(R.drawable.reports_icon);
		} else if (medicalRecordsInfo.getMedRecordRecordType() != null
			&& medicalRecordsInfo.getMedRecordRecordType().equalsIgnoreCase("Bills")) {
		    holder.imgRecordsType.setImageResource(R.drawable.bill_icon);
		}

		/*if (medicalRecordsInfo.getMedRecordRelationType() != null && medicalRecordsInfo.getMedRecordRelationType().equalsIgnoreCase("MOM")) {
		    holder.imgRelationType.setImageResource(R.drawable.mom_icon);
		} else if (medicalRecordsInfo.getMedRecordRelationType() != null
			&& medicalRecordsInfo.getMedRecordRelationType().equalsIgnoreCase("DAD")) {
		    holder.imgRelationType.setImageResource(R.drawable.dad_icon);
		}*/

	    } else {
		holder.imgRecords.setImageResource(R.drawable.prescription_icon);
	    }
	    if (medicalRecordsInfo.getMedRecordDoctorName() != null && !medicalRecordsInfo.getMedRecordDoctorName().equalsIgnoreCase("")
		    && !medicalRecordsInfo.getMedRecordDoctorName().equalsIgnoreCase(null)
		    && !medicalRecordsInfo.getMedRecordDoctorName().equalsIgnoreCase("null")) {
		holder.txtDoctorName.setVisibility(View.VISIBLE);
		holder.txtDoctorName.setText(medicalRecordsInfo.getMedRecordDoctorName());
	    } else {
		holder.txtDoctorName.setVisibility(View.GONE);
	    }
	    if (medicalRecordsInfo.getMedRecordHospitalName() != null && !medicalRecordsInfo.getMedRecordHospitalName().equalsIgnoreCase("")
		    && !medicalRecordsInfo.getMedRecordHospitalName().equalsIgnoreCase(null)
		    && !medicalRecordsInfo.getMedRecordHospitalName().equalsIgnoreCase("null")) {
		holder.txtHospitalName.setVisibility(View.VISIBLE);
		holder.txtHospitalName.setText(medicalRecordsInfo.getMedRecordHospitalName());
	    } else {
		holder.txtHospitalName.setVisibility(View.GONE);
	    }
	    if (medicalRecordsInfo.getMedRecordRecordDate() != null && !medicalRecordsInfo.getMedRecordRecordDate().equalsIgnoreCase("")
		    && !medicalRecordsInfo.getMedRecordRecordDate().equalsIgnoreCase(null)
		    && !medicalRecordsInfo.getMedRecordRecordDate().equalsIgnoreCase("null")) {
		holder.txtCreatedDate.setText(medicalRecordsInfo.getMedRecordRecordDate());
	    }
	    holder.imgRecords.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    if (medicalRecordsList.get(position).getMedRecordImageFirst() != null) {
			easyTracker.send(MapBuilder.createEvent("Parent Record", "ImageView", "ImageView", null).build());
			new EnlargeMultipleImageDialog(context, position).show();
		    }
		}
	    });
	    holder.imgDownload.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    if (Utils.isNetworkAvailable(context)) {
			easyTracker.send(MapBuilder.createEvent("Parent Record", "Download", "Download", null).build());
			if (medicalRecordsList.get(position).getMedRecordImageFirst() != null) {
			    try {
				if (medicalRecordsList.get(position).getMedRecordDoctorName() != null
					&& !medicalRecordsList.get(position).getMedRecordDoctorName().equalsIgnoreCase("")
					&& !medicalRecordsList.get(position).getMedRecordDoctorName().equalsIgnoreCase(null)
					&& !medicalRecordsList.get(position).getMedRecordDoctorName().equalsIgnoreCase("null")) {

				    if (medicalRecordsList.get(position).getMedRecordImageFirst() != null) {
					if (checkDownloadedFileExist(medicalRecordsList.get(position).getMedRecordDoctorName(), medicalRecordsList
						.get(position).getMedRecordCreatedDate(), "1")) {
					    isFirstImagesDownloaded = true;
					   
					} else {
					    isFirstImagesDownloaded = false;
					    new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordImageFirst(), medicalRecordsList
						    .get(position).getMedRecordDoctorName(), "1", medicalRecordsList.get(position)
						    .getMedRecordCreatedDate());
					}
				    }
				    if (medicalRecordsList.get(position).getMedRecordImageSecond() != null) {
					if (checkDownloadedFileExist(medicalRecordsList.get(position).getMedRecordDoctorName(), medicalRecordsList
						.get(position).getMedRecordCreatedDate(), "2")) {
					    isSecondImagesDownloaded = true;
					    
					} else {
					    isSecondImagesDownloaded = false;
					    new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordImageSecond(), medicalRecordsList
						    .get(position).getMedRecordDoctorName(), "2", medicalRecordsList.get(position)
						    .getMedRecordCreatedDate());
					}
				    }
				    
				} else if (medicalRecordsList.get(position).getMedRecordHospitalName() != null
					&& !medicalRecordsList.get(position).getMedRecordHospitalName().equalsIgnoreCase("")
					&& !medicalRecordsList.get(position).getMedRecordHospitalName().equalsIgnoreCase(null)
					&& !medicalRecordsList.get(position).getMedRecordHospitalName().equalsIgnoreCase("null")) {

				    if (medicalRecordsList.get(position).getMedRecordImageFirst() != null) {
					if (checkDownloadedFileExist(medicalRecordsList.get(position).getMedRecordHospitalName(), medicalRecordsList
						.get(position).getMedRecordCreatedDate(), "1")) {
					    isFirstImagesDownloaded = true;
					   
					} else {
					    isFirstImagesDownloaded = false;
					    new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordImageFirst(), medicalRecordsList
						    .get(position).getMedRecordHospitalName(), "1", medicalRecordsList.get(position)
						    .getMedRecordCreatedDate());

					}
				    }
				    if (medicalRecordsList.get(position).getMedRecordImageSecond() != null) {
					if (checkDownloadedFileExist(medicalRecordsList.get(position).getMedRecordHospitalName(), medicalRecordsList
						.get(position).getMedRecordCreatedDate(), "2")) {
					    isSecondImagesDownloaded = true;
					  
					} else {
					    isSecondImagesDownloaded = false;
					    new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordImageSecond(), medicalRecordsList
						    .get(position).getMedRecordHospitalName(), "2", medicalRecordsList.get(position)
						    .getMedRecordCreatedDate());

					}
				    }

				}
                                if(isFirstImagesDownloaded && isSecondImagesDownloaded){
                                    Toast.makeText(
					    context,
					    Utils.getCustomeFontStyle(context, context.getString(R.string.reportDownloadedAlready)
						    + " Medinfi folder"), Toast.LENGTH_LONG).show();
                                }
				
				
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}
		    } else {
			Toast.makeText(context, Utils.getCustomeFontStyle(context,context.getString(R.string.notWorkNotAvailable)),
				    Toast.LENGTH_LONG).show();
		    }
		}
	    });
	    
	    holder.deletMedRecord.setOnClickListener(new OnClickListener() {
	        
	        @Override
	        public void onClick(View v) {
	            customDialog(position);
	        }
	    });
	    return row;
	}

	/* holder which holds the view associated to the adapter */
	class MedicalRecordHolder {
	    ImageView imgRecords;
	    ImageView imgRecordsType;
	    ImageView imgRelationType;
	    ImageView imgDownload;
	    TextView txtDoctorName;
	    TextView txtHospitalName;
	    TextView txtCreatedDate;
	    ImageView deletMedRecord;

	}

	class EnlargeMultipleImageDialog extends Dialog implements android.view.View.OnClickListener {
		Context mContext;
		private int iPosition;
		RelativeLayout layoutSecondImage;
		DisplayImageOptions optionsEnlarg;
		private ArrayList<String> multiplaeImageList = new ArrayList<String>();
		int iCunt;

		ViewPager viewPager;
		MyPagerAdapter myPagerAdapter;
		String[] imageArray;

		public EnlargeMultipleImageDialog(Context mContext, int iPosition) {
		    super(mContext);
		    this.mContext = mContext;
		    this.iPosition = iPosition;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    requestWindowFeature(Window.FEATURE_NO_TITLE);
		    Rect displayRectangle = new Rect();
		    Window window = ((Activity) mContext).getWindow();
		    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

		    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View layout = inflater.inflate(R.layout.multiple_image_filiper, null);
		    layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
		    // layout.setMinimumHeight((int) (displayRectangle.height() *
		    // 0.9f));

		    optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

		    setContentView(layout);
		    Button btnCancel = (Button) findViewById(R.id.close_button_hs);
		    btnCancel.setOnClickListener(this);

		    if (medicalRecordsList.get(iPosition).getMedRecordImageFirst() != null
			    &&! medicalRecordsList.get(iPosition).getMedRecordImageFirst().trim().equalsIgnoreCase("null")) {
			    multiplaeImageList.add(medicalRecordsList.get(iPosition).getMedRecordImageFirst());
			}
			if (medicalRecordsList.get(iPosition).getMedRecordImageSecond() != null
				&&! medicalRecordsList.get(iPosition).getMedRecordImageSecond().trim().equalsIgnoreCase("null")) {
			    multiplaeImageList.add(medicalRecordsList.get(iPosition).getMedRecordImageSecond());
			}

		    imageArray = new String[multiplaeImageList.size()];

		    for (int i = 0; i < multiplaeImageList.size(); i++) {
			imageArray[i] = multiplaeImageList.get(i);
		    }

		    viewPager = (ViewPager) findViewById(R.id.myviewpager);
		    myPagerAdapter = new MyPagerAdapter(mContext, imageArray);
		    viewPager.setAdapter(myPagerAdapter);

		    // txtCountMultip.setVisibility(View.VISIBLE);

		}

		private class MyPagerAdapter extends PagerAdapter {

		    Context context;
		    ImageLoader imageLoader;
		    DisplayImageOptions optionsEnlarg;
		    private Typeface tfRegular;

		    MyPagerAdapter(Context context, String[] imageArray) {
			this.context = context;
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
			optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
			tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
		    }

		    int NumberOfPages = imageArray.length;

		    @Override
		    public int getCount() {
			return NumberOfPages;
		    }

		    @Override
		    public boolean isViewFromObject(View view, Object object) {
			return view == object;
		    }

		    @Override
		    public Object instantiateItem(ViewGroup container, int position) {

			TextView textView = new TextView(mContext);
			textView.setTextColor(Color.BLACK);
			textView.setTextSize(14);
			textView.setPadding(5, 7, 0, 0);
			textView.setTypeface(tfRegular);
			textView.setText(String.valueOf(position + 1) + " of " + NumberOfPages);
			textView.setVisibility(View.GONE);

			ImageView imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setPadding(0, 10, 0, 0);
			ProgressBar progresBar = new ProgressBar(mContext);
			progresBar.setIndeterminate(true);

			LayoutParams layoutParamsProgresBar = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			progresBar.setLayoutParams(layoutParamsProgresBar);

			LinearLayout linearLayoutPBar = new LinearLayout(mContext);
			linearLayoutPBar.setOrientation(LinearLayout.VERTICAL);
			linearLayoutPBar.setGravity(Gravity.CENTER);
			LayoutParams layoutParamsLinearpBar = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			linearLayoutPBar.setLayoutParams(layoutParamsLinearpBar);
			linearLayoutPBar.addView(progresBar);

			setImageEnLargLoading(imageArray[position], imageView, optionsEnlarg, textView, progresBar);
			LayoutParams imageParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			imageView.setLayoutParams(imageParams);

			LinearLayout linearLayout = new LinearLayout(mContext);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.setGravity(Gravity.CENTER);
			LayoutParams layoutParamsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			linearLayout.setLayoutParams(layoutParamsLinear);

			linearLayout.addView(textView);
			linearLayout.addView(imageView);

			RelativeLayout layout = new RelativeLayout(mContext);

			layout.setGravity(Gravity.CENTER);
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 300);
			layout.setBackgroundColor(Color.WHITE);
			layout.setLayoutParams(layoutParams);

			layout.addView(linearLayoutPBar);
			layout.addView(linearLayout);

			container.addView(layout);
			return layout;
		    }

		    @Override
		    public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((RelativeLayout) object);
		    }

		    private void setImageEnLargLoading(String imageUrl, ImageView imgProfilePicDisplay, DisplayImageOptions options, final TextView textView,
			    final ProgressBar progressBar) {
			imageLoader.displayImage(imageUrl, imgProfilePicDisplay, options, new ImageLoadingListener() {

			    @Override
			    public void onLoadingStarted(String arg0, View arg1) {
				progressBar.setVisibility(View.VISIBLE);
				textView.setVisibility(View.GONE);

			    }

			    @Override
			    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				progressBar.setVisibility(View.GONE);
			    }

			    @Override
			    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				progressBar.setVisibility(View.GONE);
				textView.setVisibility(View.VISIBLE);
			    }

			    @Override
			    public void onLoadingCancelled(String arg0, View arg1) {

			    }
			});
		    }

		}

		@Override
		public void onClick(View v) {
		    int id = v.getId();
		    switch (id) {
		    case R.id.close_button_hs:
			try {
			    dismiss();
			} catch (Exception e) {
			    e.printStackTrace();
			}
			break;
		    default:
			break;
		    }

		}
	    }
	    public class DeleteMedicalRecordsList extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
		    super.onPreExecute();
		    pdia = new ProgressDialog(MedicalReportsActivity.this);
			pdia.setCancelable(true);
			pdia.setOnCancelListener(cancelListener);
			pdia.setMessage(Utils.getCustomeFontStyle(MedicalReportsActivity.this, getString(R.string.favouriteDeleting)));
			pdia.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
		    String result = null;
		    JSONParser jparser = new JSONParser();
		    try {
			result = jparser.deleteMedicalRecordsList(arg0[0]);

		    } catch (Exception e) {
			e.printStackTrace();
		    }
		    return result.toString();
		}

		@Override
		protected void onPostExecute(String result) {
		    /*if (pdia != null && pdia.isShowing())
			pdia.dismiss();*/
		    try {
			
			if (result != null) {
			    JSONObject jobj = new JSONObject(result);
			if (jobj != null) {
			    String message = jobj.getString("status");
			    if (message.equalsIgnoreCase("Success")) {
				isDelete = true;
				new GetMedicalRecordsList().execute();
			    } else {
				Toast.makeText(context,
					Utils.getCustomeFontStyle(MedicalReportsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
					Toast.LENGTH_LONG).show();
			    }

			}
			    
			}

		    } catch (JSONException e) {
			e.printStackTrace();
		    }
		   
		}

	    }
	File fileRecord;

	class DownloadFile extends AsyncTask<String, String, String> {
	    ProgressDialog mProgressDialog = new ProgressDialog(context);
	    String strFolderName;

	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog.setMessage(Utils.getCustomeFontStyle(context, context.getString(R.string.reportDownloading)));
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	    }

	    @Override
	    protected String doInBackground(String... aurl) {
		int count;
		try {
		    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			URL url = new URL(aurl[0]);
			URLConnection conection = url.openConnection();
			conection.connect();
			// getting file length
			int lenghtOfFile = conection.getContentLength();
			InputStream input = new BufferedInputStream(url.openStream(), 8192);

			String strDirPath = Environment.getExternalStorageDirectory().getPath() + "/Medinfi/";
			File dirLog = new File(strDirPath);
			if (!dirLog.isDirectory() || !dirLog.exists()) {
			    dirLog.mkdirs();
			}
			String strLogFile = "Med" + "_" + aurl[1] + "_" + aurl[2] + "_" + aurl[3] + ".png";
			fileRecord = new File(dirLog, strLogFile);
			if (!fileRecord.exists()) {
			    fileRecord.createNewFile();
			}
			long total = 0;
			OutputStream output = new FileOutputStream(fileRecord);
			byte data[] = new byte[1024];
			while ((count = input.read(data)) != -1) {
			    total += count;
			    // publishing the progress....
			    // After this onProgressUpdate will be called
			    // publishProgress(""+(int)((total*100)/lenghtOfFile));

			    // writing data to file
			    output.write(data, 0, count);
			}

			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}

		return null;
	    }

	    /*
	     * protected void onProgressUpdate(Integer... progress) {
	     * mProgressDialog.setProgress(progress[0]);
	     * if(mProgressDialog.getProgress()==mProgressDialog.getMax()){
	     * mProgressDialog.dismiss(); Toast.makeText(context,
	     * "Downloaded in -> "+fileRecord, Toast.LENGTH_LONG).show(); } }
	     */
	    protected void onPostExecute(String result) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
		    mProgressDialog.dismiss();
		    Toast.makeText(context, Utils.getCustomeFontStyle(context, context.getString(R.string.reportDownloadInto) + " Medinfi folder"),
			    Toast.LENGTH_LONG).show();

		}

	    }
	}

	private boolean checkDownloadedFileExist(String doctorName, String createDate, String imageType) {
	    boolean isExist = false;
	    try {
		String strDirPath = Environment.getExternalStorageDirectory().getPath() + "/Medinfi/";
		File dirLog = new File(strDirPath);

		String strLogFile = "Med" + "_" + doctorName +"_" + imageType + "_" + createDate +  ".png";
		File fileRecord = new File(dirLog, strLogFile);
		if (fileRecord.exists()) {
		    isExist = true;
		} else {
		    isExist = false;
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    return isExist;
	}
	
	/**
	     * Create custom dialog
	     */
	    private void customDialog(final int possitionId){
	        final Dialog dialog = new Dialog(context);
	        Typeface tfRegular = Typeface.createFromAsset(getAssets(), "fonts/RobotoRegular.ttf");
	        Typeface tfRobotoLight = Typeface.createFromAsset(getAssets(), "fonts/RobotoLight.ttf");
	        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	        Rect displayRectangle = new Rect();
	        Window window = getWindow();
	        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View layout = inflater.inflate(R.layout.gprs_alert_dialog, null);
	        layout.setMinimumWidth((int)(displayRectangle.width() * 0.75f));
	        //layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

	        dialog.setContentView(layout);
	        dialog.setCancelable(false);
	        dialog.show();
	        
	        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
	        btnOk.setTypeface(tfRegular);
	        btnOk.setText(getString(R.string.yes));
	        TextView textDialog = (TextView)dialog.findViewById(R.id.textDialog);
	        TextView textDialogMsg = (TextView)dialog.findViewById(R.id.textDialogMsg);
	        textDialog.setTypeface(tfRegular);
	        textDialog.setText(getString(R.string.deleteMedRecord));
	        textDialogMsg.setTypeface(tfRegular);
	        textDialogMsg.setVisibility(View.GONE);
	        
	        
	        btnOk.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	        	if (Utils.isNetworkAvailable(context)) {
	        	    new DeleteMedicalRecordsList().execute(medicalRecordsList.get(possitionId).getMedRecordId());
	        	}else{
	        	    Toast.makeText(context, Utils.getCustomeFontStyle(context,context.getString(R.string.notWorkNotAvailable)),
				    Toast.LENGTH_LONG).show();
	        	}
	        	
	                dialog.dismiss();
	        	
	            }
	        });
	        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
	        btnCancel.setTypeface(tfRegular);
	        btnCancel.setText(getString(R.string.no));
	        btnCancel.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	        	
	                dialog.dismiss();
	            }
	        });
	        dialog.setOnKeyListener(new OnKeyListener() {
		    
		    @Override
		    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
	                    dialog.dismiss();
	                }
	                return true;
		    }
		});
	    }
    }

}
