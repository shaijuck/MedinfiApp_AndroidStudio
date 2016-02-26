package com.medinfi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.bumptech.glide.Glide;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.adapters.DoctorDetailsAdapter;
import com.medinfi.adapters.ReviewAdapter;
import com.medinfi.datainfo.DoctorDetailInfo;
import com.medinfi.datainfo.HospitalInfo;
import com.medinfi.datainfo.ReviewsInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CircleTransform;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class HospitalDetailsActivity extends SherlockActivity implements OnClickListener{

    // initialize views
    private TextView rateDoctorButton;
    private ImageButton callButton;
    private TextView shareButton;
    private TextView favrate;
    private ArrayList<HospitalInfo> hospitalArrayList;
    private ArrayList<ReviewsInfo> reviewArrayList;
    private ArrayList<DoctorDetailInfo> doctorArrayList;
  //  private ListView doctorsListView;
    //private ListView reviewListView;
    private DoctorDetailsAdapter doctorAdapter;
    private ReviewAdapter reviewAdapter;
    private ImageView closeHosDetail;
    
   // private SliderLayout mDemoSlider;//New Layout

    private ImageView hospitalImageView;
    private TextView hospitalNameTextView;
    private TextView hospitalDesignationTextView;

    private TextView ratedTextView;
    

    private TextView feesTextView;

    private TextView waittimeTextView;

    private TextView attitudeTextView;

    private TextView readAllTextView;
    private TextView associated_with;
    private TextView reviewTextView;
    
    private Typeface  tfRegular, tfLite;
    private ImageLoader imageLoader;
    DisplayImageOptions options;
    DisplayImageOptions optionsMultipleImage;

    private RelativeLayout associatedRelativeLayout;
    private View associatedView;
    private LinearLayout backLinearLayout;
    private LinearLayout awardsLinearLayout;
    private LinearLayout membershipLinearLayout;
    private LinearLayout awards_layout;
    private LinearLayout member_layout;
    private ScrollView scrollView;

    private ArrayList<String> awardsArrayList;
    private ArrayList<String> memberShipArrayList;

    private RelativeLayout reviewRelativeLayout;
    private View reviewView;

    private TextView hospitaltxtTextView;
    private Boolean isViewAll = false;
    private Boolean isReadAll = false;
    public int i;
    private String doctorName, doctorPic;
    private String docType;
    private String hospitalName, hospitalLocality, hospitalCity;

    private LinearLayout feesLinearLayout;
    private LinearLayout waitTimeLinearLayout;
    private LinearLayout ratedLinearLayout;
    private LinearLayout ratedLinearLayoutTop;
    private LinearLayout rateLayoutMain;
    private LinearLayout attitudeLinearLayout;
    private TextView websiteTopTextView;

    private TextView awardText;
    private TextView memText;

    private LinearLayout hospitalSpecialityLayout;
    private TextView hospitalSpecialityText;
    private LinearLayout hospitalSpecialityMainLayout;

    private LinearLayout hospitalDescriptionLayout;
    private TextView hospitalDescriptionText;
    private LinearLayout hospitalDescriptionMainLayout;

    private LinearLayout hospitalWebsiteLayout;
    private TextView hospitalWebsiteText;
    private LinearLayout hospitalWebsiteMainLayout;

    private LinearLayout hospitalEmaiIdMainLayout;
    private LinearLayout hospitalEmaiIdLayout;
    private TextView hospitalEmaiIdText;

    private LinearLayout hospitalLandMarkMainLayout;
    private LinearLayout hospitalLandMarkLayout;
    private TextView hospitalLandMarkText;

    int pos = 0;
    private EasyTracker easyTracker = null;

    private GetDoctorList getDoctorList;
    private boolean isTrue = false;
    private ProgressDialog pdia;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private String hospitalAddress;
    private String websiteTop;
    private String callingScreen;
    private boolean isFavSaved = false;

    private String preServerTimeSpend = "";
    private String serverTimeSpend = "";
    private String postServerTimepend = "";
    private String totalScreenTimeSpend = "";
    private TextView viewAllDoctor;
    private TextView txtReview;
    private TextView localityLandMarkValue;
    private TextView bedsValues, emergencyServices, icuServices, nicuServices, ambulanceServices, hospitalParking;
    private LinearLayout facilityLayout;
    //private TextView txtdistance, txtKm;
    private TextView txtRecomended;
    private int doctorListCount = 0;
    ArrayList<DoctorDetailInfo> hospitalList;
    private LinearLayout multipleImageLayout;
    private ImageView imgHospEmergrncyGate,imgHospLeft,imgHospRight;
    private static HospitalDetailsActivity hospitalDetailsScreen;
    private HashMap<String,String> url_maps;
    ArrayList<String> url_img_maps=new ArrayList<String>();
    private ImageView hospitalImageView2;
    boolean showFlag=false;
    MyPagerAdapter myPagerAdapter;
    /* declare ids for xml */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	// WindowManager.LayoutParams.FLAG_FULLSCREEN);
	Log.i("On Create Top","Inside on create at top");
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.hospitaldetails);

	hospitalDetailsScreen = this;
	if (AppConstants.LogLevel == 1) {
	    Utils.startPreServerCallTime = System.currentTimeMillis();
	    Utils.startTotalScreenTime = System.currentTimeMillis();
	}
	easyTracker = EasyTracker.getInstance(HospitalDetailsActivity.this);

	if (getIntent().getStringExtra("CALLING_SCREEN") != null) {
	    callingScreen = getIntent().getStringExtra("CALLING_SCREEN");
	}
	RegisterActivity.isRegisteredScreen = false;
	hospitaltxtTextView = (TextView) findViewById(R.id.hospital_text);
	feesLinearLayout = (LinearLayout) findViewById(R.id.fee_layout);
	waitTimeLinearLayout = (LinearLayout) findViewById(R.id.waittime_layout);
	ratedLinearLayout = (LinearLayout) findViewById(R.id.rate_layout);
	ratedLinearLayoutTop = (LinearLayout) findViewById(R.id.rate_layout_top);
	rateLayoutMain = (LinearLayout) findViewById(R.id.rateLayoutMain);
	attitudeLinearLayout = (LinearLayout) findViewById(R.id.attitude_layout);
	hospitalImageView2 = (ImageView) findViewById(R.id.iv_doctorimage_hospitals2);
    
	//mDemoSlider = (SliderLayout)findViewById(R.id.slider);//new layout
	
	docType = ApplicationSettings.getPref(AppConstants.DOCTOR_SPECIALITY, "");
	tfLite = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoLight.ttf");
	tfRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");

	hospitalDescriptionLayout = (LinearLayout) findViewById(R.id.hospital_description_layout);
	hospitalDescriptionText = (TextView) findViewById(R.id.hospitaldescriptiontext);
	hospitalDescriptionText.setTypeface(tfRegular);
	hospitalDescriptionMainLayout = (LinearLayout) findViewById(R.id.hospital_description_lyt);

	hospitalSpecialityLayout = (LinearLayout) findViewById(R.id.hospital_speciality_layout);
	hospitalSpecialityText = (TextView) findViewById(R.id.hospitalspecialitytext);
	hospitalSpecialityText.setTypeface(tfRegular);
	hospitalSpecialityMainLayout = (LinearLayout) findViewById(R.id.hospital_speciality_lyt);

	hospitalWebsiteLayout = (LinearLayout) findViewById(R.id.hospital_website_layout);
	hospitalWebsiteText = (TextView) findViewById(R.id.hospitalwebsitetext);
	hospitalWebsiteText.setTypeface(tfRegular);
	hospitalWebsiteMainLayout = (LinearLayout) findViewById(R.id.hospital_website_lyt);

	hospitaltxtTextView.setTypeface(tfLite);

	hospitalEmaiIdMainLayout = (LinearLayout) findViewById(R.id.hospitalEmailIdMainLt);
	hospitalEmaiIdLayout = (LinearLayout) findViewById(R.id.hospitalEmailIdlyt);
	hospitalEmaiIdText = (TextView) findViewById(R.id.hospitalEmailIdtext);
	hospitalEmaiIdText.setTypeface(tfRegular);

	hospitalLandMarkMainLayout = (LinearLayout) findViewById(R.id.hospitalLandMarkMainLt);
	hospitalLandMarkLayout = (LinearLayout) findViewById(R.id.hospitalLandMarklyt);
	hospitalLandMarkText = (TextView) findViewById(R.id.hospitalLandMarktext);
	hospitalLandMarkText.setTypeface(tfRegular);

	scrollView = (ScrollView) findViewById(R.id.sv);
	
	// scrollView.requestFocus();
	associatedRelativeLayout = (RelativeLayout) findViewById(R.id.associated_layout);
	associatedView = findViewById(R.id.associated_layout_view);
	member_layout = (LinearLayout) findViewById(R.id.member_layout);
	reviewRelativeLayout = (RelativeLayout) findViewById(R.id.review_layout);
	reviewView = findViewById(R.id.review_layout_view);
	awardsLinearLayout = (LinearLayout) findViewById(R.id.awards_recognisation);
	readAllTextView = (TextView) findViewById(R.id.readall_tv);
	readAllTextView.setOnClickListener(this);

	readAllTextView.setTypeface(tfRegular);
	associated_with = (TextView) findViewById(R.id.associated_with);
	associated_with.setOnClickListener(this);
	associated_with.setTypeface(tfRegular);
	awards_layout = (LinearLayout) findViewById(R.id.awards_layout);
//	backLinearLayout = (LinearLayout) findViewById(R.id.back_layout);
//	backLinearLayout.setOnClickListener(this);
	closeHosDetail =(ImageView) findViewById(R.id.close_hos_detail);
	 closeHosDetail.setOnClickListener(this);

	callButton = (ImageButton) findViewById(R.id.btn_call_hospital);
	callButton.setOnClickListener(this);
	rateDoctorButton = (TextView) findViewById(R.id.rate_the_doctor_button);
	rateDoctorButton.setOnClickListener(this);
	shareButton = (TextView) findViewById(R.id.share_btn);
	shareButton.setOnClickListener(this);
	shareButton.setTypeface(tfRegular);

	favrate = (TextView) findViewById(R.id.favrate);
	favrate.setOnClickListener(this);
	favrate.setTypeface(tfRegular);

	rateDoctorButton.setTypeface(tfRegular);
	//doctorsListView = (ListView) findViewById(R.id.doctor_listview);
	//reviewListView = (ListView) findViewById(R.id.review_listview);
	hospitalImageView = (ImageView) findViewById(R.id.iv_doctorimage_hospitals);
	hospitalNameTextView = (TextView) findViewById(R.id.hospitalname);
	hospitalDesignationTextView = (TextView) findViewById(R.id.hospital_designation);
	websiteTopTextView=(TextView) findViewById(R.id.website_top);
	hospitalNameTextView.setTypeface(tfRegular);
	hospitalDesignationTextView.setTypeface(tfRegular);
	websiteTopTextView.setTypeface(tfRegular);
	

	awardText = (TextView) findViewById(R.id.awardtext);
	memText = (TextView) findViewById(R.id.membertext);
	awardText.setTypeface(tfRegular);
	memText.setTypeface(tfRegular);
	reviewTextView = (TextView) findViewById(R.id.reviews);
	reviewTextView.setTypeface(tfRegular);
	ratedTextView = (TextView) findViewById(R.id.rated_text);
	ratedTextView.setTypeface(tfRegular);
	feesTextView = (TextView) findViewById(R.id.fees_text);
	feesTextView.setTypeface(tfRegular);
	waittimeTextView = (TextView) findViewById(R.id.waittime_text);
	waittimeTextView.setTypeface(tfRegular);
	attitudeTextView = (TextView) findViewById(R.id.attitude_text);
	attitudeTextView.setTypeface(tfRegular);
	viewAllDoctor = (TextView) findViewById(R.id.viewAllDoctor);
		
	viewAllDoctor.setOnClickListener(this);
	
	websiteTopTextView.setOnClickListener(this);
	isHomePressed = false;
	isEventPressed = false;
	isFavSaved = false;
	txtReview = (TextView) findViewById(R.id.txtReview);
	txtReview.setOnClickListener(this);
	localityLandMarkValue = (TextView) findViewById(R.id.localityLandMarkValue);

	bedsValues = (TextView) findViewById(R.id.bedsValues);
	emergencyServices = (TextView) findViewById(R.id.emergencyServices);
	icuServices = (TextView) findViewById(R.id.icuServices);
	nicuServices = (TextView) findViewById(R.id.nicuServices);
	ambulanceServices = (TextView) findViewById(R.id.ambulanceServices);
	hospitalParking = (TextView) findViewById(R.id.hospitalParking);
	facilityLayout = (LinearLayout) findViewById(R.id.facilityLayout);
	//txtdistance = (TextView) findViewById(R.id.txtdistance);
	//txtKm = (TextView) findViewById(R.id.txtKm);
	txtRecomended = (TextView) findViewById(R.id.txtRecomended);
	multipleImageLayout = (LinearLayout) findViewById(R.id.multipleImageLayout);
	multipleImageLayout.setVisibility(View.GONE);
	imgHospEmergrncyGate = (ImageView) findViewById(R.id.imgHospEmergrncyGate);
	imgHospLeft = (ImageView) findViewById(R.id.imgHospLeft);
	imgHospRight = (ImageView) findViewById(R.id.imgHospRight);
	url_maps = new HashMap<String, String>();
	
	initialiseloadImage();
	loadHospitalInfo();
	//touchEvents();
	clickEvent();
	//stopReviewListViewScroll();

    }
    
    

    // click events
    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.rate_the_doctor_button:
	    isEventPressed = true;
	    easyTracker.send(MapBuilder.createEvent("Hospital Details", "Rate & Review", hospitalName, null).build());

	    if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
		    && ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
		startActivity(new Intent(HospitalDetailsActivity.this, RateTheHospitalActivity.class).putExtra("doctorname", doctorName)
			.putExtra("doctorpic", doctorPic).putExtra("hospitaladdress", hospitalAddress));

	    } else {
		Intent intent = new Intent(HospitalDetailsActivity.this, RegisterActivity.class);
		intent.putExtra("CALLING_SCREEN", "HospitalDetailsRated");
		intent.putExtra("doctorname", doctorName);
		intent.putExtra("doctorpic", doctorPic);
		intent.putExtra("hospitaladdress", hospitalAddress);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	    }

	    break;
	case R.id.share_btn:
	    easyTracker.send(MapBuilder.createEvent("Hospital Details", "Share Hospital", hospitalName, null).build());
	    isEventPressed = true;
	    WhatsAppPost();
	    break;
	
	case R.id.close_hos_detail:
	    isEventPressed = true;
	    easyTracker.send(MapBuilder.createEvent("Hospital Details", "Back", "Back", null).build());
	    backActivity();
	    break;
	case R.id.favrate:
	    isEventPressed = true;
	    if (isFavSaved) {
		easyTracker.send(MapBuilder.createEvent("Hospital Details", "Un Save", "Un Save", null).build());
		if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
			&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
		    new DeleteFavouriteAsyncTask().execute("Hospital", ApplicationSettings.getPref(AppConstants.HOSPITAL_ID, ""));
		} else {
		    Intent intent = new Intent(HospitalDetailsActivity.this, RegisterActivity.class);
		    intent.putExtra("CALLING_SCREEN", "HospitalDetails");
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
		}
	    } else {
		easyTracker.send(MapBuilder.createEvent("Hospital Details", "Save", "Save", null).build());
		if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
			&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
		    new AddFavouriteAsyncTask().execute("Hospital", ApplicationSettings.getPref(AppConstants.HOSPITAL_ID, ""));
		} else {
		    Intent intent = new Intent(HospitalDetailsActivity.this, RegisterActivity.class);
		    intent.putExtra("CALLING_SCREEN", "HospitalDetails");
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
		}
	    }

	    break;
	case R.id.viewAllDoctor:
	    scrollView.scrollTo(0, scrollView.getBottom());
	    toggleReview();

	    break;
	case R.id.txtReview:
	    scrollView.scrollTo(0, scrollView.getBottom());
	    break;
	case R.id.website_top:
		String url=websiteTopTextView.getText().toString();
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			   url = "http://" + url;
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
		break;
	default:
	    break;
	}
    }

    // call whats app using package name
    public void WhatsAppPost() {

	PackageManager pm = getPackageManager();
	try {

	    Intent waIntent = new Intent(Intent.ACTION_SEND);
	    waIntent.setType("text/plain");

	    /*
	     * "Hi. It would be great if you can share your opinion on Dr Jagdish Rohira, General Physician, Apollo Hospital,Bangalore"
	     */
	    // Hey. I found Dr. XYZ, 'Main Area of Expertise' in<Locality> using
	    // Medinfi Android Mobile App. You may also try and install it here:
	    // URL"
	    String text = "Hey. I found " + hospitalName + ", " + hospitalLocality + ", " + hospitalCity
		    + " using Medinfi Android Mobile App. You may also try and install it here:" + "\n\n"
		    + "https://play.google.com/store/apps/details?id=com.medinfi";

	    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
	    // Check if package exists or not. If not then code
	    // in catch block will be called
	    waIntent.setPackage("com.whatsapp");

	    waIntent.putExtra(Intent.EXTRA_TEXT, text);
	    startActivity(Intent.createChooser(waIntent, "Share with"));

	} catch (NameNotFoundException e) {
	    Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
	}

    }

    /* call hospital details API */
    public void loadHospitalDetails(final String hospitalID) {
    }

    public class calHosAsyncTask extends AsyncTask<String, Void, String> {

    	@Override
    	protected void onPreExecute() {
    	    super.onPreExecute();
    	}

    	@Override
    	protected String doInBackground(String... params) {
    	    JSONParser jparser = new JSONParser();
    	    String deviceId=Secure.getString(HospitalDetailsActivity.this.getContentResolver(),
                    Secure.ANDROID_ID);
    	    String result = jparser.callHospital(params[0], params[1],deviceId);

    	    return result;
    	}

    	@Override
    	protected void onPostExecute(String result) {
    	    super.onPostExecute(result);
    	   
    	}

        }
    
    public class GetDoctorList extends AsyncTask<Void, Void, String> {

	ProgressDialog pdia;

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
		preServerTimeSpend = Utils.preServerTimeSpend();
	    }
	    pdia = new ProgressDialog(HospitalDetailsActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(HospitalDetailsActivity.this, getString(R.string.screenLoading)));
	    pdia.show();
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		
	    String result = null;
	    try {
		JSONParser jparser = new JSONParser();
		result = jparser.getHospitalDetails(ApplicationSettings.getPref(AppConstants.HOSPITAL_ID, ""));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (AppConstants.LogLevel == 1) {
		serverTimeSpend = Utils.serverTimeSpend();
		Utils.startPostServerCallTime = System.currentTimeMillis();
	    }
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		
	    pdia.dismiss();
	    Log.i("Hosp Info","Inside Post Execute");
	    try {
		if (result != null) {

		    hospitalArrayList = new ArrayList<HospitalInfo>();
		    JSONArray jsonArray = new JSONArray(result.toString());
		    HospitalInfo hospitalInfo = new HospitalInfo();
		    for (i = 0; i < jsonArray.length(); i++) {
			try {
			    JSONObject jsonObject = jsonArray.getJSONObject(i);
			    JSONObject hospitalObject = jsonObject.getJSONObject("hospital");
			    if (AppConstants.LogLevel == 1) {
				try {
				    ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
				} catch (Exception e) {
				    e.printStackTrace();
				}
			    }

			    if (hospitalObject != null) {
				String docId = hospitalObject.getString("id");
				String fullname = hospitalObject.getString("name");

				if (fullname.toString().contains(",")) {

				    fullname = fullname.toString().split(",")[0];
				} else {
				    fullname = fullname.toString();
				}
				String qualification = hospitalObject.getString("speciality");

				String phone = null;
				String email = null;
				if (hospitalObject.has("primaryphone") && hospitalObject.getString("primaryphone") != null
					&& !hospitalObject.getString("primaryphone").equalsIgnoreCase("")
					&& !hospitalObject.getString("primaryphone").equalsIgnoreCase(null)) {
				    phone = hospitalObject.getString("primaryphone");
				    hospitalInfo.setHospitalPhoneNumber(phone);
				}

				if (hospitalObject.has("email") && hospitalObject.getString("email") != null
					&& !hospitalObject.getString("email").equalsIgnoreCase("")
					&& !hospitalObject.getString("email").equalsIgnoreCase(null)) {
				    email = hospitalObject.getString("email");
				    hospitalInfo.setHospitalEmail(email);
				}

				String pic = "";
				String awardRecog = "";
				if (hospitalObject.has("pic") && hospitalObject.getString("pic") != null
					&& !hospitalObject.getString("pic").trim().equalsIgnoreCase("")
					&& !hospitalObject.getString("pic").trim().equalsIgnoreCase(null)) {
				    pic = hospitalObject.getString("pic");
				    //Log.i("Hosp Info","Pic"+pic);
				}

				if (hospitalObject.has("awardRecog") && hospitalObject.getString("awardRecog") != null
					&& !hospitalObject.getString("awardRecog").equalsIgnoreCase("")) {
				    awardRecog = hospitalObject.getString("awardRecog");
				}
				String avgrate = "";
				if (hospitalObject.has("avgrate") && hospitalObject.getString("avgrate") != null
					&& !hospitalObject.getString("avgrate").equalsIgnoreCase("")
					&& !hospitalObject.getString("avgrate").equalsIgnoreCase("null")) {
				    avgrate = hospitalObject.getString("avgrate");
				}

				if (hospitalObject.has("recommendCount") && hospitalObject.getString("recommendCount") != null
					&& !hospitalObject.getString("recommendCount").equalsIgnoreCase("")) {
				    hospitalInfo.setHospitalRecommendCount(hospitalObject.getString("recommendCount"));
				}

				if (hospitalObject.has("nonRecommendCount") && hospitalObject.getString("nonRecommendCount") != null
					&& !hospitalObject.getString("nonRecommendCount").equalsIgnoreCase("")) {
				    hospitalInfo.setHospitalNonRecommendCount(hospitalObject.getString("nonRecommendCount"));
				}

				String address = hospitalObject.getString("address");
				String websiteURL =hospitalObject.getString("website");
				if (avgrate != null && !avgrate.equalsIgnoreCase("") && !avgrate.equalsIgnoreCase("null"))
				    hospitalInfo.setHospitalRating(avgrate);
				String fees = "";
				String waitTime = "";
				String attitude = "";
				String satisfaction = "";
				if (hospitalObject.has("fees") && hospitalObject.getString("fees") != null
					&& !hospitalObject.getString("fees").equalsIgnoreCase("")) {
				    fees = hospitalObject.getString("fees");
				    hospitalInfo.setHospitalFees(fees);
				}
				if (hospitalObject.has("waitTime") && hospitalObject.getString("waitTime") != null
					&& !hospitalObject.getString("waitTime").equalsIgnoreCase("")) {
				    waitTime = hospitalObject.getString("waitTime");
				    hospitalInfo.setHospitalWaittime(waitTime);
				}
				if (hospitalObject.has("attitude") && hospitalObject.getString("attitude") != null
					&& !hospitalObject.getString("attitude").equalsIgnoreCase("")) {
				    attitude = hospitalObject.getString("attitude");
				    hospitalInfo.setHospitalAttitude(attitude);
				}

				if (hospitalObject.has("satisfaction") && hospitalObject.getString("satisfaction") != null
					&& !hospitalObject.getString("satisfaction").equalsIgnoreCase("")) {
				    satisfaction = hospitalObject.getString("satisfaction");
				    hospitalInfo.setHospitalSatisfaction(satisfaction);
				}

				if (hospitalObject.has("landmark") && hospitalObject.getString("landmark") != null
					&& !hospitalObject.getString("landmark").equalsIgnoreCase("")
					&& !hospitalObject.getString("landmark").equalsIgnoreCase(null)
					&& !hospitalObject.getString("landmark").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalLandmark(hospitalObject.getString("landmark"));
				}

				if (hospitalObject.has("city") && hospitalObject.getString("city") != null
					&& !hospitalObject.getString("city").equalsIgnoreCase("")
					&& !hospitalObject.getString("city").equalsIgnoreCase(null)
					&& !hospitalObject.getString("city").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalCity(hospitalObject.getString("city"));
				}

				if (hospitalObject.has("state") && hospitalObject.getString("state") != null
					&& !hospitalObject.getString("state").equalsIgnoreCase("")
					&& !hospitalObject.getString("state").equalsIgnoreCase(null)
					&& !hospitalObject.getString("state").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalState(hospitalObject.getString("state"));
				}
				if (hospitalObject.has("locality") && hospitalObject.getString("locality") != null
					&& !hospitalObject.getString("locality").equalsIgnoreCase("")
					&& !hospitalObject.getString("locality").equalsIgnoreCase(null)
					&& !hospitalObject.getString("locality").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalLocality(hospitalObject.getString("locality"));
				}

				if (hospitalObject.has("description") && hospitalObject.getString("description") != null
					&& !hospitalObject.getString("description").equalsIgnoreCase("")
					&& !hospitalObject.getString("description").equalsIgnoreCase(null)
					&& !hospitalObject.getString("description").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalDescription(hospitalObject.getString("description"));
				}

				if (hospitalObject.has("alternateEmail") && hospitalObject.getString("alternateEmail") != null
					&& !hospitalObject.getString("alternateEmail").equalsIgnoreCase("")
					&& !hospitalObject.getString("alternateEmail").equalsIgnoreCase(null)
					&& !hospitalObject.getString("alternateEmail").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalAlternateEmail(hospitalObject.getString("alternateEmail"));
				}
				if (hospitalObject.has("ext1") && hospitalObject.getString("ext1") != null
					&& !hospitalObject.getString("ext1").equalsIgnoreCase("")
					&& !hospitalObject.getString("ext1").equalsIgnoreCase(null)
					&& !hospitalObject.getString("ext1").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalext1(hospitalObject.getString("ext1"));
				}

				if (hospitalObject.has("ext2") && hospitalObject.getString("ext2") != null
					&& !hospitalObject.getString("ext2").equalsIgnoreCase("")
					&& !hospitalObject.getString("ext2").equalsIgnoreCase(null)
					&& !hospitalObject.getString("ext2").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalext2(hospitalObject.getString("ext2"));
				}

				if (hospitalObject.has("alternatephone2") && hospitalObject.getString("alternatephone2") != null
					&& !hospitalObject.getString("alternatephone2").equalsIgnoreCase("")
					&& !hospitalObject.getString("alternatephone2").equalsIgnoreCase(null)
					&& !hospitalObject.getString("alternatephone2").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalAlternatephone2(hospitalObject.getString("alternatephone2"));
				}

				if (hospitalObject.has("alternatephone1") && hospitalObject.getString("alternatephone1") != null
					&& !hospitalObject.getString("alternatephone1").equalsIgnoreCase("")
					&& !hospitalObject.getString("alternatephone1").equalsIgnoreCase(null)
					&& !hospitalObject.getString("alternatephone1").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalAlternatephone1(hospitalObject.getString("alternatephone1"));
				}

				if (hospitalObject.has("fax") && hospitalObject.getString("fax") != null
					&& !hospitalObject.getString("fax").equalsIgnoreCase("")
					&& !hospitalObject.getString("fax").equalsIgnoreCase(null)
					&& !hospitalObject.getString("fax").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalFax(hospitalObject.getString("fax"));
				}

				if (hospitalObject.has("website") && hospitalObject.getString("website") != null
					&& !hospitalObject.getString("website").equalsIgnoreCase("")
					&& !hospitalObject.getString("website").equalsIgnoreCase(null)
					&& !hospitalObject.getString("website").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalWebsite(hospitalObject.getString("website"));
				}

				if (hospitalObject.has("isUserFavorite") && hospitalObject.getString("isUserFavorite") != null
					&& !hospitalObject.getString("isUserFavorite").equalsIgnoreCase("")
					&& !hospitalObject.getString("isUserFavorite").equalsIgnoreCase(null)) {
				    hospitalInfo.setHospitalSaved(hospitalObject.getString("isUserFavorite"));
				}
				if (hospitalObject.has("isRated") && hospitalObject.getString("isRated") != null
					&& !hospitalObject.getString("isRated").equalsIgnoreCase("")
					&& !hospitalObject.getString("isRated").equalsIgnoreCase(null)) {
				    hospitalInfo.setHospitalRated(hospitalObject.getString("isRated"));
				}

				if (hospitalObject.has("num_beds") && hospitalObject.getString("num_beds") != null
					&& !hospitalObject.getString("num_beds").equalsIgnoreCase("")
					&& !hospitalObject.getString("num_beds").equalsIgnoreCase(null)
					&& !hospitalObject.getString("num_beds").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalBedsNumber(hospitalObject.getString("num_beds"));
				}
				if (hospitalObject.has("emergency_service_available")
					&& hospitalObject.getString("emergency_service_available") != null
					&& !hospitalObject.getString("emergency_service_available").equalsIgnoreCase("")
					&& !hospitalObject.getString("emergency_service_available").equalsIgnoreCase(null)
					&& !hospitalObject.getString("emergency_service_available").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalEmergencyServices(hospitalObject.getString("emergency_service_available"));
				}
				if (hospitalObject.has("ICU_available") && hospitalObject.getString("ICU_available") != null
					&& !hospitalObject.getString("ICU_available").equalsIgnoreCase("")
					&& !hospitalObject.getString("ICU_available").equalsIgnoreCase(null)
					&& !hospitalObject.getString("ICU_available").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalICUServices(hospitalObject.getString("ICU_available"));
				}
				if (hospitalObject.has("NICU_available") && hospitalObject.getString("NICU_available") != null
					&& !hospitalObject.getString("NICU_available").equalsIgnoreCase("")
					&& !hospitalObject.getString("NICU_available").equalsIgnoreCase(null)
					&& !hospitalObject.getString("NICU_available").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalNICUServices(hospitalObject.getString("NICU_available"));
				}
				if (hospitalObject.has("num_ambulance") && hospitalObject.getString("num_ambulance") != null
					&& !hospitalObject.getString("num_ambulance").equalsIgnoreCase("")
					&& !hospitalObject.getString("num_ambulance").equalsIgnoreCase(null)
					&& !hospitalObject.getString("num_ambulance").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalAmbulanceNumber(hospitalObject.getString("num_ambulance"));
				}
				if (hospitalObject.has("hospital_parking_available")
					&& hospitalObject.getString("hospital_parking_available") != null
					&& !hospitalObject.getString("hospital_parking_available").equalsIgnoreCase("")
					&& !hospitalObject.getString("hospital_parking_available").equalsIgnoreCase(null)
					&& !hospitalObject.getString("hospital_parking_available").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalParking(hospitalObject.getString("hospital_parking_available"));
				}

				if (hospitalObject.has("distance") && hospitalObject.getString("distance") != null
					&& !hospitalObject.getString("distance").equalsIgnoreCase("")
					&& !hospitalObject.getString("distance").equalsIgnoreCase(null)
					&& !hospitalObject.getString("distance").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalDistance(hospitalObject.getString("distance"));
				}
				if (hospitalObject.has("lat") && hospitalObject.getString("lat") != null
					&& !hospitalObject.getString("lat").equalsIgnoreCase("")
					&& !hospitalObject.getString("lat").equalsIgnoreCase(null)
					&& !hospitalObject.getString("lat").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalLat(hospitalObject.getString("lat"));
				}
				if (hospitalObject.has("lon") && hospitalObject.getString("lon") != null
					&& !hospitalObject.getString("lon").equalsIgnoreCase("")
					&& !hospitalObject.getString("lon").equalsIgnoreCase(null)
					&& !hospitalObject.getString("lon").equalsIgnoreCase("null")) {
				    hospitalInfo.setHospitalLong(hospitalObject.getString("lon"));
				}

				if (hospitalObject.has("awrec") && hospitalObject.getString("awrec") != null
					&& !hospitalObject.getString("awrec").equalsIgnoreCase("")
					&& !hospitalObject.getString("awrec").equalsIgnoreCase("null")
					&& !hospitalObject.getString("awrec").equalsIgnoreCase(null)) {
				    JSONArray awardsArray = new JSONArray(hospitalObject.getString("awrec"));
				    awardsArrayList = new ArrayList<String>();
				    if (awardsArray != null && awardsArray.length() > 0 && !awardsArray.equals("null")) {
					for (int j = 0; j < awardsArray.length(); j++) {
					    if (awardsArray.getString(j) != null && !awardsArray.getString(j).equals("")) {
						awardsArrayList.add(awardsArray.getString(j));
					    }
					}
				    }
				}

				if (hospitalObject.has("membership") && hospitalObject.getString("membership") != null
					&& !hospitalObject.getString("membership").equalsIgnoreCase("")
					&& !hospitalObject.getString("membership").equalsIgnoreCase("null")
					&& !hospitalObject.getString("membership").equalsIgnoreCase(null)) {
				    JSONArray memberShipbArray = new JSONArray(hospitalObject.getString("membership"));
				    memberShipArrayList = new ArrayList<String>();

				    if (memberShipbArray != null && memberShipbArray.length() > 0 && !memberShipbArray.equals("null")) {
					for (int j = 0; j < memberShipbArray.length(); j++) {
					    if (memberShipbArray.getString(j) != null && !memberShipbArray.getString(j).equals("")) {
						memberShipArrayList.add(memberShipbArray.getString(j));
					    }
					}
				    }
				}

				if (hospitalObject.has("emergencyPic") && hospitalObject.getString("emergencyPic") != null
					&& !hospitalObject.getString("emergencyPic").trim().equalsIgnoreCase("")
					&& !hospitalObject.getString("emergencyPic").trim().equalsIgnoreCase(null)) {
				    hospitalInfo.setHospitalEmergencyPic(hospitalObject.getString("emergencyPic").trim());
				    Log.i("Hosp Info","Emer Pic"+hospitalInfo.getHospitalEmergencyPic());
				}
				if (hospitalObject.has("leftPic") && hospitalObject.getString("leftPic") != null
					&& !hospitalObject.getString("leftPic").trim().equalsIgnoreCase("")
					&& !hospitalObject.getString("leftPic").trim().equalsIgnoreCase(null)) {
				    hospitalInfo.setHospitalLeftPic(hospitalObject.getString("leftPic").trim());
				    //Log.i("Hosp Info","Left Pic"+hospitalInfo.getHospitalLeftPic());
				}
				if (hospitalObject.has("rightPic") && hospitalObject.getString("rightPic") != null
					&& !hospitalObject.getString("rightPic").trim().equalsIgnoreCase("")
					&& !hospitalObject.getString("rightPic").trim().equalsIgnoreCase(null)) {
				    hospitalInfo.setHospitalRightPic(hospitalObject.getString("rightPic").trim());
				    //Log.i("Hosp Info","Right Pic"+hospitalInfo.getHospitalRightPic());
				    //See the kind of value returned for each of the images and if they are URL then add them to the url hash map and initialize the slider
				}
				
				hospitalInfo.setAwardsArrayList(awardsArrayList);
				hospitalInfo.setMemberShipArrayList(memberShipArrayList);

				if (address != null && !address.equalsIgnoreCase(""))
				    hospitalInfo.setHospitalAddress(address.replace("?", ""));
				if(websiteURL!=null && !websiteURL.equalsIgnoreCase(""))
					hospitalInfo.setHospitalWebsite(websiteURL.toLowerCase());
				
				hospitalInfo.setHospitalID(docId);
				hospitalInfo.setHospitalName(fullname.replace("?", ""));
				hospitalInfo.setHospitalSpeciality(qualification);
				if (phone != null && !phone.equalsIgnoreCase("") && !phone.equalsIgnoreCase(null)) {
				    hospitalInfo.setHospitalPhoneNumber(phone);
				}
				if (email != null && !email.equalsIgnoreCase("") && !email.equalsIgnoreCase(null)) {
				    hospitalInfo.setHospitalEmail(email);
				}
				if (pic != null && !pic.equalsIgnoreCase("") && !pic.equalsIgnoreCase(null)) {
				    hospitalInfo.setHospitalImage(pic);
				}
			    }
			    if (jsonObject.has("reviews") && jsonObject.getString("reviews") != null
				    && !jsonObject.getString("reviews").equalsIgnoreCase("")
				    && !jsonObject.getString("reviews").equalsIgnoreCase("null")
				    && !jsonObject.getString("reviews").equalsIgnoreCase(null)) {
				JSONArray reviewArray = jsonObject.getJSONArray("reviews");

				if (reviewArray != null && reviewArray.length() > 0) {
				    reviewArrayList = new ArrayList<ReviewsInfo>();
				    for (int j = 0; j < reviewArray.length(); j++) {

					ReviewsInfo reviewsInfo = new ReviewsInfo();

					if (reviewArray.getJSONObject(j).has("id") && reviewArray.getJSONObject(j).getString("id") != null) {
					    String reviewId = reviewArray.getJSONObject(j).getString("id");
					    reviewsInfo.setReviewId(reviewId);

					}
					if (reviewArray.getJSONObject(j).has("hospitalId")
						&& reviewArray.getJSONObject(j).getString("hospitalId") != null) {
					    String doctorId = reviewArray.getJSONObject(j).getString("hospitalId");
					    reviewsInfo.setDoctorId(doctorId);

					}
					if (reviewArray.getJSONObject(j).has("userId") && reviewArray.getJSONObject(j).getString("userId") != null) {
					    String userId = reviewArray.getJSONObject(j).getString("userId");
					    reviewsInfo.setUserId(userId);

					}
					if (reviewArray.getJSONObject(j).has("fees") && reviewArray.getJSONObject(j).getString("fees") != null) {
					    String fees = reviewArray.getJSONObject(j).getString("fees");

					    reviewsInfo.setFees(fees);

					}
					if (reviewArray.getJSONObject(j).has("waitTime")
						&& reviewArray.getJSONObject(j).getString("waitTime") != null) {
					    String waitTime = reviewArray.getJSONObject(j).getString("waitTime");

					    reviewsInfo.setWaittime(waitTime);

					}

					if (reviewArray.getJSONObject(j).has("attitude")
						&& reviewArray.getJSONObject(j).getString("attitude") != null) {
					    String attitude = reviewArray.getJSONObject(j).getString("attitude");
					    reviewsInfo.setAttitude(attitude);
					}
					if (reviewArray.getJSONObject(j).has("satisfaction")
						&& reviewArray.getJSONObject(j).getString("satisfaction") != null) {
					    String satisfaction = reviewArray.getJSONObject(j).getString("satisfaction");
					    reviewsInfo.setSatisfaction(satisfaction);

					}

					if (reviewArray.getJSONObject(j).has("recommend")
						&& reviewArray.getJSONObject(j).getString("recommend") != null) {
					    String recommend = reviewArray.getJSONObject(j).getString("recommend");
					    reviewsInfo.setRecommend(recommend);

					}

					if (reviewArray.getJSONObject(j).has("rate") && reviewArray.getJSONObject(j).getString("rate") != null) {
					    String rate = reviewArray.getJSONObject(j).getString("rate");
					    reviewsInfo.setRate(rate);

					}
					if (reviewArray.getJSONObject(j).has("review") && reviewArray.getJSONObject(j).getString("review") != null) {
					    String review = reviewArray.getJSONObject(j).getString("review");
					    reviewsInfo.setReview(review);

					}

					if (reviewArray.getJSONObject(j).has("prescriptionImage")
						&& reviewArray.getJSONObject(j).getString("prescriptionImage") != null) {
					    String prescriptionImage = reviewArray.getJSONObject(j).getString("prescriptionImage");

					    reviewsInfo.setPrescriptionImage(prescriptionImage);

					}

					if (reviewArray.getJSONObject(j).has("status") && reviewArray.getJSONObject(j).getString("status") != null) {
					    String status = reviewArray.getJSONObject(j).getString("status");
					    reviewsInfo.setStatus(status);
					}
					if (reviewArray.getJSONObject(j).has("created_at")
						&& reviewArray.getJSONObject(j).getString("created_at") != null) {
					    String date = Utils.getDate(reviewArray.getJSONObject(j).getString("created_at"));
					    reviewsInfo.setCreatedDate(date);
					}

					if (reviewArray.getJSONObject(j).has("name") && reviewArray.getJSONObject(j).getString("name") != null) {

					    String name = reviewArray.getJSONObject(j).getString("name");
					    reviewsInfo.setUserName(name);

					}
					if (reviewArray.getJSONObject(j).has("username") && reviewArray.getJSONObject(j).getString("username") != null) {

					    String actualUsername = reviewArray.getJSONObject(j).getString("username");
					    reviewsInfo.setActualUsername(actualUsername);

					}
					reviewArrayList.add(reviewsInfo);
				    }
				}
				hospitalInfo.setReviewArrayList(reviewArrayList);
			    }

			    if (jsonObject.has("doctor") && jsonObject.getString("doctor") != null
				    && !jsonObject.getString("doctor").equalsIgnoreCase("")) {
				JSONArray doctorArray = jsonObject.getJSONArray("doctor");

				if (doctorArray != null && doctorArray.length() > 0) {
				    doctorArrayList = new ArrayList<DoctorDetailInfo>();

				    for (int j = 0; j < doctorArray.length(); j++) {

					String doctorID = doctorArray.getJSONObject(j).getString("id");
					String doctorName = null;
					String firstName = doctorArray.getJSONObject(j).getString("firstname");
					String middlename = doctorArray.getJSONObject(j).getString("middlename");

					String lastname = doctorArray.getJSONObject(j).getString("lastname");

					if (firstName != null && !firstName.equalsIgnoreCase("") && !firstName.equalsIgnoreCase(null)
						&& !firstName.equalsIgnoreCase("null")) {
					    doctorName = firstName;
					}
					if (middlename != null && !middlename.equalsIgnoreCase("") && !middlename.equalsIgnoreCase(null)
						&& !middlename.equalsIgnoreCase("null")) {
					    doctorName = doctorName + " " + middlename;
					}
					if (lastname != null && !lastname.equalsIgnoreCase("") && !lastname.equalsIgnoreCase("null")) {
					    doctorName = doctorName + " " + lastname;
					}
					String doctorphone = doctorArray.getJSONObject(j).getString("phone");

					/*
					 * String doctorAddress = doctorArray
					 * .getJSONObject(i).getString(
					 * "address");
					 */
					String doctorEmail = doctorArray.getJSONObject(j).getString("email");
					String doctorDesignation = doctorArray.getJSONObject(j).getString("qualification");
					String doctoravgrate = doctorArray.getJSONObject(j).getString("avgrate");
					String doctorType = doctorArray.getJSONObject(j).getString("docType");
					String doctorAddress = doctorArray.getJSONObject(j).getString("address");
					DoctorDetailInfo doctorInfo = new DoctorDetailInfo();
					doctorInfo.setDoctorID(doctorID);
					doctorInfo.setDoctorName(doctorName.replace("?", ""));
					doctorInfo.setDoctorType(doctorType.trim());
					doctorInfo.setDoctorAddress(doctorAddress);

					if (doctorArray.getJSONObject(j).has("pic") && doctorArray.getJSONObject(j).getString("pic") != null
						&& !doctorArray.getJSONObject(j).getString("pic").trim().equalsIgnoreCase("")
						&& !doctorArray.getJSONObject(j).getString("pic").trim().equalsIgnoreCase("null"))
					    doctorInfo.setDoctorPic(doctorArray.getJSONObject(j).getString("pic").trim());
					
					 if (doctorArray.getJSONObject(j).has("second_image") && doctorArray.getJSONObject(j).getString("second_image").trim() != null
						&& !doctorArray.getJSONObject(j).getString("second_image").trim().equalsIgnoreCase("")
						&& !doctorArray.getJSONObject(j).getString("second_image").trim().equalsIgnoreCase("null")
						&& !doctorArray.getJSONObject(j).getString("second_image").trim().equalsIgnoreCase(null)) {
					    doctorInfo.setDoctorSecondImage(doctorArray.getJSONObject(j).getString("second_image").trim());
					}
					if (doctorphone != null && !doctorphone.equalsIgnoreCase("") && !doctorphone.equalsIgnoreCase(null))
					    doctorInfo.setDoctorPhone(doctorphone);
					if (doctorEmail != null && !doctorEmail.equalsIgnoreCase("") && !doctorEmail.equalsIgnoreCase(null))
					    doctorInfo.setDoctorEmail(doctorEmail);

					if (doctorDesignation != null && !doctorDesignation.equalsIgnoreCase("")
						&& !doctorDesignation.equalsIgnoreCase(null) && !doctorDesignation.equalsIgnoreCase("null"))
					    doctorInfo.setDoctorQualification(doctorDesignation);
					if (doctoravgrate != null && !doctoravgrate.equalsIgnoreCase("") && !doctoravgrate.equalsIgnoreCase(null)
						&& !doctoravgrate.equalsIgnoreCase("0.0"))
					    doctorInfo.setDoctorAverage(doctoravgrate);

					if (doctorArray.getJSONObject(j).has("totalExperience")
						&& doctorArray.getJSONObject(j).getString("totalExperience") != null) {
					    doctorInfo.setDoctorTotalExperience(doctorArray.getJSONObject(j).getString("totalExperience"));
					}

					doctorArrayList.add(doctorInfo);
				    }
				}
				hospitalInfo.setDoctorArrayList(doctorArrayList);

			    }
			} catch (NumberFormatException e) {
			     e.printStackTrace();
			}
		    }
		    hospitalArrayList.add(hospitalInfo);
		    Log.i("Hosp Info","About to add image");
		    if (hospitalArrayList != null && hospitalArrayList.size() > 0) {

			for (int i = 0; i < hospitalArrayList.size(); i++) {

				if (hospitalArrayList.get(i).getHospitalImage() != null
				        && !hospitalArrayList.get(i).getHospitalImage().equalsIgnoreCase("")
				        && !hospitalArrayList.get(i).getHospitalImage().equalsIgnoreCase(null)) {
				 //   imageLoader.displayImage((hospitalArrayList.get(i).getHospitalImage()), hospitalImageView2, options);
				    Log.i("Hosp Info","Main Pic"+(hospitalArrayList.get(i).getHospitalImage()));
				    url_maps.put("1", hospitalArrayList.get(i).getHospitalImage());
				    url_img_maps.add(hospitalArrayList.get(i).getHospitalImage());
//				    hospitalImageView2.setVisibility(View.VISIBLE);
//				    mDemoSlider.setVisibility(View.GONE);
				    //hospitalImageView2.setImageResource(R.drawable.hospital_icon_normal);
				    
				       } else {
//				    hospitalImageView.setImageResource(R.drawable.hospital_icon_normal);
//				    mDemoSlider.setVisibility(View.GONE);
				       }
				       
				       if(hospitalArrayList.get(i).getHospitalEmergencyPic() != null
				        && !hospitalArrayList.get(i).getHospitalEmergencyPic().trim().equalsIgnoreCase("")
				        && !hospitalArrayList.get(i).getHospitalEmergencyPic().trim().equalsIgnoreCase(null)){
				    multipleImageLayout.setVisibility(View.GONE);
				   
				    
				    imgHospEmergrncyGate.setVisibility(View.VISIBLE);
				    imageLoader.displayImage((hospitalArrayList.get(i).getHospitalEmergencyPic()), imgHospEmergrncyGate, optionsMultipleImage);
				    Log.i("Hosp Info","Emer Pic"+(hospitalArrayList.get(i).getHospitalEmergencyPic()));
				    url_maps.put("2", hospitalArrayList.get(i).getHospitalEmergencyPic());
				    url_img_maps.add(hospitalArrayList.get(i).getHospitalEmergencyPic());
				       }
				       if(hospitalArrayList.get(i).getHospitalLeftPic() != null
				        && !hospitalArrayList.get(i).getHospitalLeftPic().trim().equalsIgnoreCase("")
				        && !hospitalArrayList.get(i).getHospitalLeftPic().trim().equalsIgnoreCase(null)){
				    multipleImageLayout.setVisibility(View.GONE);
				    imgHospLeft.setVisibility(View.VISIBLE);
				    imageLoader.displayImage((hospitalArrayList.get(i).getHospitalLeftPic()), imgHospLeft, optionsMultipleImage);
				    Log.i("Hosp Info","Left Pic"+(hospitalArrayList.get(i).getHospitalLeftPic()));
				    url_maps.put("3", hospitalArrayList.get(i).getHospitalLeftPic());
				    url_img_maps.add(hospitalArrayList.get(i).getHospitalLeftPic());
				    showFlag=true;
				       }
				       
				       if(hospitalArrayList.get(i).getHospitalRightPic() != null
				        && !hospitalArrayList.get(i).getHospitalRightPic().trim().equalsIgnoreCase("")
				        && !hospitalArrayList.get(i).getHospitalRightPic().trim().equalsIgnoreCase(null)){
				    multipleImageLayout.setVisibility(View.GONE);
				    imgHospRight.setVisibility(View.VISIBLE);
				    imageLoader.displayImage((hospitalArrayList.get(i).getHospitalRightPic()), imgHospRight, optionsMultipleImage);
				    Log.i("Hosp Info","Right Pic"+(hospitalArrayList.get(i).getHospitalRightPic()));
				    url_maps.put("4", hospitalArrayList.get(i).getHospitalRightPic());
				    url_img_maps.add(hospitalArrayList.get(i).getHospitalRightPic());
				    showFlag=true;    
			    }
			
				       ViewPager viewPager;
				       viewPager = (ViewPager) findViewById(R.id.myviewpager);
				       if(url_img_maps.size()>0){
				    	   ((RelativeLayout) findViewById(R.id.myImgSlider)).setVisibility(View.VISIBLE);
					    myPagerAdapter = new MyPagerAdapter(HospitalDetailsActivity.this, url_img_maps);
					    viewPager.setAdapter(myPagerAdapter);
					  
					    if(url_img_maps.size()==1){
					    ((ImageView) findViewById(R.id.dot1)).setVisibility(View.VISIBLE);	
					    ((ImageView) findViewById(R.id.dot1)).setBackgroundResource(R.drawable.dot_on);
					    ((ImageView) findViewById(R.id.dot2)).setVisibility(View.GONE);
					    ((ImageView) findViewById(R.id.dot3)).setVisibility(View.GONE);
					    ((ImageView) findViewById(R.id.dot4)).setVisibility(View.GONE);
					    }else if(url_img_maps.size()==2){
					    	((ImageView) findViewById(R.id.dot1)).setVisibility(View.VISIBLE);	
					    	 ((ImageView) findViewById(R.id.dot1)).setBackgroundResource(R.drawable.dot_on);
						    ((ImageView) findViewById(R.id.dot2)).setVisibility(View.VISIBLE);
						    ((ImageView) findViewById(R.id.dot3)).setVisibility(View.GONE);
						    ((ImageView) findViewById(R.id.dot4)).setVisibility(View.GONE);
						    }else if(url_img_maps.size()==3){
						    	((ImageView) findViewById(R.id.dot1)).setVisibility(View.VISIBLE);
						    	 ((ImageView) findViewById(R.id.dot1)).setBackgroundResource(R.drawable.dot_on);
							    ((ImageView) findViewById(R.id.dot2)).setVisibility(View.VISIBLE);
							    ((ImageView) findViewById(R.id.dot3)).setVisibility(View.VISIBLE);
							    ((ImageView) findViewById(R.id.dot4)).setVisibility(View.GONE);
						    }else if(url_img_maps.size()>4){
						    	((ImageView) findViewById(R.id.dot1)).setVisibility(View.VISIBLE);	
						    	 ((ImageView) findViewById(R.id.dot1)).setBackgroundResource(R.drawable.dot_on);
							    ((ImageView) findViewById(R.id.dot2)).setVisibility(View.VISIBLE);
							    ((ImageView) findViewById(R.id.dot3)).setVisibility(View.VISIBLE);
							    ((ImageView) findViewById(R.id.dot4)).setVisibility(View.VISIBLE);
						    }
					    
					    viewPager.setOnPageChangeListener(new OnPageChangeListener() {
							
							@Override
							public void onPageSelected(int arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onPageScrolled(int arg0, float arg1, int arg2) {
							
								 if((arg0+1)==1){
									    ((ImageView) findViewById(R.id.dot1)).setBackgroundResource(R.drawable.dot_on);
									    ((ImageView) findViewById(R.id.dot2)).setBackgroundResource(R.drawable.dot_off);
									    ((ImageView) findViewById(R.id.dot3)).setBackgroundResource(R.drawable.dot_off);
									    ((ImageView) findViewById(R.id.dot4)).setBackgroundResource(R.drawable.dot_off);
									    }else if((arg0+1)==2){
									    	((ImageView) findViewById(R.id.dot1)).setBackgroundResource(R.drawable.dot_off);
										    ((ImageView) findViewById(R.id.dot2)).setBackgroundResource(R.drawable.dot_on);
										    ((ImageView) findViewById(R.id.dot3)).setBackgroundResource(R.drawable.dot_off);
										    ((ImageView) findViewById(R.id.dot4)).setBackgroundResource(R.drawable.dot_off);
										    }else if((arg0+1)==3){
										    	((ImageView) findViewById(R.id.dot1)).setBackgroundResource(R.drawable.dot_off);
											    ((ImageView) findViewById(R.id.dot2)).setBackgroundResource(R.drawable.dot_off);
											    ((ImageView) findViewById(R.id.dot3)).setBackgroundResource(R.drawable.dot_on);
											    ((ImageView) findViewById(R.id.dot4)).setBackgroundResource(R.drawable.dot_off);
										    }else if((arg0+1)>=4){
										    	((ImageView) findViewById(R.id.dot1)).setBackgroundResource(R.drawable.dot_off);
											    ((ImageView) findViewById(R.id.dot2)).setBackgroundResource(R.drawable.dot_off);
											    ((ImageView) findViewById(R.id.dot3)).setBackgroundResource(R.drawable.dot_off);
											    ((ImageView) findViewById(R.id.dot4)).setBackgroundResource(R.drawable.dot_on);
										    }
								
							}
							
							@Override
							public void onPageScrollStateChanged(int arg0) {	
							}
						});
					    
				       }
				       else
				       {
                         ((RelativeLayout) findViewById(R.id.myImgSlider)).setVisibility(View.GONE);
				    	   viewPager.setVisibility(View.GONE);
				       }
				       
//				       for(int j=0;j<url_img_maps.size();j++)
//				       {
//				    	   DefaultSliderView textSliderView = new DefaultSliderView(HospitalDetailsActivity.this);
//					        // initialize a SliderLayout
//					        textSliderView
//					                .description("")
//					                .image(url_img_maps.get(j))
//					                .setScaleType(BaseSliderView.ScaleType.CenterInside)
//					                .setOnSliderClickListener(HospitalDetailsActivity.this);
//
//					        //add your extra information
//					        textSliderView.getBundle()
//					                .putString("extra","");
//
//					       mDemoSlider.addSlider(textSliderView);  
//				       }
//			    
//			    mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
//			    mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//			    mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//			    mDemoSlider.stopAutoCycle();
		  
			   // mDemoSlider.setPresetTransformer("DepthPage");
//			    if(showFlag){
//			    	 hospitalImageView2.setVisibility(View.GONE);
//			    	 mDemoSlider.setVisibility(View.VISIBLE);
//			    }
			        
			    
			    
			    hospitalNameTextView.setText(hospitalArrayList.get(i).getHospitalName().replace("?", ""));
			    hospitalName = hospitalArrayList.get(i).getHospitalName().replace("?", "");
			    hospitalLocality = hospitalArrayList.get(i).getHospitalLocality().replace("?", "");
			    hospitalCity = hospitalArrayList.get(i).getHospitalCity().replace("?", "");
			    doctorName = hospitalArrayList.get(i).getHospitalName().replace("?", "");
			    doctorPic = hospitalArrayList.get(i).getHospitalImage();
			    hospitalAddress = hospitalArrayList.get(i).getHospitalAddress().replace("?", "");
			    hospitalDesignationTextView.setText(hospitalAddress);
			    websiteTop= hospitalArrayList.get(i).getHospitalWebsite();
			    
			    
			    String hospitalCity=hospitalArrayList.get(i).getHospitalCity();
			    ((RelativeLayout)findViewById(R.id.layoutLandMark)).setVisibility(View.VISIBLE);
			    ((LinearLayout) findViewById(R.id.lineAboveFacilityLaout)).setVisibility(View.VISIBLE);

				if(websiteTop!=null && websiteTop.trim().length() > 0 && !websiteTop.equalsIgnoreCase("null"))
					websiteTopTextView.setText(websiteTop);
				else
					websiteTopTextView.setVisibility(View.GONE);
			    if (hospitalArrayList.get(i).getHospitalLandmark()!=null && hospitalArrayList.get(i).getHospitalLandmark().replace("?", "") != null) {
				String hospitalLandMark = hospitalArrayList.get(i).getHospitalLandmark().replace("?", "");
				
				if (hospitalLandMark.trim().length() > 0 && !hospitalLandMark.equalsIgnoreCase("null")) {
				    //localityLandMarkValue.setText(hospitalLocality + "," + hospitalCity+ '\n'+hospitalLandMark);
					localityLandMarkValue.setText(hospitalLandMark);
				} 
//				else {
//				    localityLandMarkValue.setText( hospitalCity+ ".");
//				}
			    }
//			    else{
//				 localityLandMarkValue.setText( hospitalCity+ ".");
//			    }

			    if (hospitalArrayList.get(i).getHospitalDistance() != null
				    && !hospitalArrayList.get(i).getHospitalDistance().trim().equalsIgnoreCase("")) {
				try {
				    double rounded = (double) Math.round(Double.valueOf(hospitalInfo.getHospitalDistance()) * 100) / 100;
				    DecimalFormat df2 = new DecimalFormat("#####.#");
				    //txtdistance.setVisibility(View.VISIBLE);
				    //txtKm.setVisibility(View.VISIBLE);
				    //txtdistance.setText("" + Double.valueOf(df2.format(rounded)));

				} catch (NumberFormatException e) {
				    e.printStackTrace();
				}

			    } else {
				//txtdistance.setVisibility(View.GONE);
				//txtKm.setVisibility(View.GONE);
			    }
			    if (hospitalArrayList.get(i).getHospitalRecommendCount() != null
				    && !hospitalArrayList.get(i).getHospitalRecommendCount().trim().equalsIgnoreCase("")
				    && !hospitalArrayList.get(i).getHospitalRecommendCount().trim().equalsIgnoreCase("null")
				    && !hospitalArrayList.get(i).getHospitalRecommendCount().trim().equalsIgnoreCase(null)) {
				String values = hospitalArrayList.get(i).getHospitalRecommendCount();

				if (values.equalsIgnoreCase("1")) {
				    txtRecomended.setVisibility(View.VISIBLE);
				    txtRecomended.setText(getString(R.string.recommendedBy) + " " + values + " " + getString(R.string.patient));
				} else if (!values.equalsIgnoreCase("0") && !values.equalsIgnoreCase("1")) {
				    txtRecomended.setVisibility(View.VISIBLE);
				    txtRecomended.setText(getString(R.string.recommendedBy) + " " + values + " " + getString(R.string.patients));
				}
			    } else {
				txtRecomended.setVisibility(View.GONE);
			    }
			    if (hospitalArrayList.get(i).getHospitalRating() != null && !hospitalArrayList.get(i).getHospitalRating().equals("0.0")
				    && !hospitalArrayList.get(i).getHospitalRating().equals("0")
				    && !hospitalArrayList.get(i).getHospitalRating().equals(0)
				    && !hospitalArrayList.get(i).getHospitalRating().equals(0.0)) {
				long lValue = Math.round(Double.valueOf(hospitalArrayList.get(i).getHospitalRating()));
				((RatingBar) findViewById(R.id.overAllRating)).setRating(lValue);
				((RatingBar) findViewById(R.id.overAllRating)).setIsIndicator(true);
				((RatingBar) findViewById(R.id.overAllRatingTop)).setRating(lValue);
				((RatingBar) findViewById(R.id.overAllRatingTop)).setIsIndicator(true);
				((RelativeLayout) findViewById(R.id.recommendedLayout)).setVisibility(View.VISIBLE);
				rateLayoutMain.setVisibility(View.VISIBLE);
				ratedLinearLayout.setVisibility(View.VISIBLE);
				ratedLinearLayoutTop.setVisibility(View.VISIBLE);
			    } else{
				ratedLinearLayout.setVisibility(View.GONE);
				ratedLinearLayoutTop.setVisibility(View.GONE);
			    }

			    if (hospitalArrayList.get(i).getHospitalFees() != null
				    && !hospitalArrayList.get(i).getHospitalFees().equalsIgnoreCase("")
				    && !hospitalArrayList.get(i).getHospitalFees().equalsIgnoreCase("null")
				    && !hospitalArrayList.get(i).getHospitalFees().equalsIgnoreCase(null)) {
				long lValue = Math.round(Double.valueOf(hospitalArrayList.get(i).getHospitalFees()));
				((RatingBar) findViewById(R.id.feesRating)).setRating(lValue);
				((RatingBar) findViewById(R.id.feesRating)).setIsIndicator(true);
				rateLayoutMain.setVisibility(View.VISIBLE);
				feesLinearLayout.setVisibility(View.VISIBLE);
			    } else
				feesLinearLayout.setVisibility(View.GONE);

			    if (hospitalArrayList.get(i).getHospitalWaittime() != null
				    && !hospitalArrayList.get(i).getHospitalWaittime().equalsIgnoreCase("")
				    && !hospitalArrayList.get(i).getHospitalWaittime().equalsIgnoreCase("null")
				    && !hospitalArrayList.get(i).getHospitalWaittime().equalsIgnoreCase(null)) {
				long lValue = Math.round(Double.valueOf(hospitalArrayList.get(i).getHospitalWaittime()));
				((RatingBar) findViewById(R.id.waitingRating)).setRating(lValue);
				((RatingBar) findViewById(R.id.waitingRating)).setIsIndicator(true);
				rateLayoutMain.setVisibility(View.VISIBLE);
				waitTimeLinearLayout.setVisibility(View.VISIBLE);
			    } else
				waitTimeLinearLayout.setVisibility(View.GONE);

			    if (hospitalArrayList.get(i).getHospitalAttitude() != null
				    && !hospitalArrayList.get(i).getHospitalAttitude().equalsIgnoreCase("")
				    && !hospitalArrayList.get(i).getHospitalAttitude().equalsIgnoreCase("null")
				    && !hospitalArrayList.get(i).getHospitalAttitude().equalsIgnoreCase(null)) {
				long lValue = Math.round(Double.valueOf(hospitalArrayList.get(i).getHospitalAttitude()));
				((RatingBar) findViewById(R.id.attitudeRating)).setRating(lValue);
				((RatingBar) findViewById(R.id.attitudeRating)).setIsIndicator(true);
				rateLayoutMain.setVisibility(View.VISIBLE);
				attitudeLinearLayout.setVisibility(View.VISIBLE);
			    } else {
				attitudeLinearLayout.setVisibility(View.GONE);
			    }
			    if (hospitalArrayList.get(i).getHospitalSaved() != null
				    && !hospitalArrayList.get(i).getHospitalSaved().equalsIgnoreCase("")
				    && !hospitalArrayList.get(i).getHospitalSaved().equalsIgnoreCase("null")
				    && !hospitalArrayList.get(i).getHospitalSaved().equalsIgnoreCase(null)
				    && hospitalArrayList.get(i).getHospitalSaved().equalsIgnoreCase("1")) {
				isFavSaved = true;
				favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_saved, 0, 0);
				favrate.setText(getString(R.string.favrateSaved));
			    } else {
				isFavSaved = false;
				favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_save, 0, 0);
				favrate.setText(getString(R.string.favrate));
			    }
			    if (hospitalArrayList.get(i).getHospitalRated() != null
				    && !hospitalArrayList.get(i).getHospitalRated().equalsIgnoreCase("")
				    && !hospitalArrayList.get(i).getHospitalRated().equalsIgnoreCase("null")
				    && !hospitalArrayList.get(i).getHospitalRated().equalsIgnoreCase(null)
				    && hospitalArrayList.get(i).getHospitalRated().equalsIgnoreCase("1")) {
				rateDoctorButton.setClickable(false);
				rateDoctorButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_rated, 0, 0);
				rateDoctorButton.setText(getString(R.string.DocHospRated));
			    } else {
				rateDoctorButton.setClickable(true);
				rateDoctorButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_rate, 0, 0);
				rateDoctorButton.setText(getString(R.string.rate));
			    }
			    setFacilitiesValues(hospitalArrayList);
			    pos = i;
			    callButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new calHosAsyncTask().execute("Hospital", ApplicationSettings.getPref(AppConstants.HOSPITAL_ID, ""));
				    if (hospitalArrayList.get(pos).getHospitalPhoneNumber() != null
					    && !hospitalArrayList.get(pos).getHospitalPhoneNumber().equalsIgnoreCase("null")
					    && !hospitalArrayList.get(pos).getHospitalPhoneNumber().equalsIgnoreCase(null)
					    && !hospitalArrayList.get(pos).getHospitalPhoneNumber().equalsIgnoreCase("6855")) {

					Intent intent = new Intent(Intent.ACTION_DIAL);
					String phoneno = hospitalArrayList.get(pos).getHospitalPhoneNumber().toString().trim();
					if (phoneno.startsWith("0")) {
					    phoneno = hospitalArrayList.get(pos).getHospitalPhoneNumber().toString().trim();
					} else if (phoneno.startsWith("91")) {
					    phoneno = hospitalArrayList.get(pos).getHospitalPhoneNumber().toString().trim();
					} else if (phoneno.startsWith("+91")) {
					    phoneno = hospitalArrayList.get(pos).getHospitalPhoneNumber().toString().trim();
					} else {
					    phoneno = "0" + hospitalArrayList.get(pos).getHospitalPhoneNumber().toString().trim();
					}

					easyTracker.send(MapBuilder.createEvent("Hospital Details", "Call Hospital", hospitalName, null).build());
					intent.setData(Uri.parse("tel:" + phoneno));
					startActivity(intent);
				    } else {
					Toast.makeText(HospitalDetailsActivity.this, "Phone number does not exist", 1000).show();
				    }

				}
			    });
			    if (hospitalArrayList.get(i).getDoctorArrayList() != null && hospitalArrayList.get(i).getDoctorArrayList().size() > 0) {
				doctorListCount = hospitalArrayList.get(i).getDoctorArrayList().size();
				//Addition of the # of doctor to the Heading for the new template
				if(doctorListCount<2)
					associated_with.setText(doctorListCount+"  DOCTOR");
				else
					associated_with.setText(doctorListCount+"  "+associated_with.getText().toString());
				// hospitalMainList.addAll(hospitalArrayList.get(i).getDoctorArrayList());
				// setListViewHeight();
				hospitalList = new ArrayList<DoctorDetailInfo>();
				if (doctorListCount > 3) {
				    for (int j = 0; j < 3; j++) {
					hospitalList.add(hospitalArrayList.get(i).getDoctorArrayList().get(j));
					
				    }
				    setAssociateDoctorDynamic(hospitalList);
				} else {
				    setAssociateDoctorDynamic(hospitalArrayList.get(i).getDoctorArrayList());
				}

				associatedRelativeLayout.setVisibility(View.VISIBLE);
				//associatedView.setVisibility(View.VISIBLE); OLD
				associatedView.setVisibility(View.GONE);//NEW
				//doctorsListView.setVisibility(View.VISIBLE);
				if (doctorListCount > 3) {
				    viewAllDoctor.setVisibility(View.VISIBLE);
				    viewAllDoctor.setText(Html.fromHtml(getString(R.string.viewAll) + " " + "<font color=\"#ed207b\">"
					    + doctorListCount + " Doctors" + "</font>"));
				} else {
				    viewAllDoctor.setVisibility(View.GONE);
				}
				/*new Handler().postDelayed(new Runnable() {

				    @Override
				    public void run() {
					viewAll(doctorsListView);
				    }
				}, 50);*/
			    } else {
				associatedRelativeLayout.setVisibility(View.GONE);
				associatedView.setVisibility(View.GONE);
				//doctorsListView.setVisibility(View.GONE);
				viewAllDoctor.setVisibility(View.GONE);
			    }

			    if (hospitalArrayList.get(i).getReviewArrayList() != null && hospitalArrayList.get(i).getReviewArrayList().size() > 0) {
				int iSize = hospitalArrayList.get(i).getReviewArrayList().size();

				reviewRelativeLayout.setVisibility(View.VISIBLE);
				reviewView.setVisibility(View.VISIBLE);
				//reviewListView.setVisibility(View.VISIBLE);
				txtReview.setVisibility(View.VISIBLE);
				
				/*reviewAdapter = new ReviewAdapter(HospitalDetailsActivity.this, R.layout.review_list_item, hospitalArrayList.get(i)
					.getReviewArrayList());
				reviewListView.setAdapter(reviewAdapter);*/
				
				setReviewsViewDynamically(hospitalArrayList.get(i).getReviewArrayList());
				

				if (iSize == 1) {
				    txtReview.setText("" + iSize + " " + "Review");
				    reviewTextView.setText("" + iSize + " " + "REVIEW");
				} else {
				    txtReview.setText("" + iSize + " " + getString(R.string.reviewsSmall));
				    reviewTextView.setText("" + iSize + " " + getString(R.string.review));
				}

				
			    } else {

				txtReview.setVisibility(View.GONE);
				reviewRelativeLayout.setVisibility(View.GONE);
				reviewView.setVisibility(View.GONE);
				//reviewListView.setVisibility(View.GONE);
			    }
			    if (txtRecomended.getVisibility() == View.GONE && txtReview.getVisibility() == View.GONE) {
				((RelativeLayout) findViewById(R.id.recommendedLayout)).setVisibility(View.GONE);
			    }
			    if (hospitalArrayList.get(i).getAwardsArrayList() != null && hospitalArrayList.get(i).getAwardsArrayList().size() > 0) {
				// showAwards(hospitalArrayList.get(i).getAwardsArrayList());
				awards_layout.setVisibility(View.GONE);
			    } else {
				awards_layout.setVisibility(View.GONE);
			    }
			    if (hospitalArrayList.get(i).getMemberShipArrayList() != null
				    && !hospitalArrayList.get(i).getMemberShipArrayList().equals("")
				    && !hospitalArrayList.get(i).getMemberShipArrayList().equals("null")
				    && !hospitalArrayList.get(i).getMemberShipArrayList().equals(null)
				    && hospitalArrayList.get(i).getMemberShipArrayList().size() > 0) {
				member_layout.setVisibility(View.GONE);
				// showMemberShip(hospitalArrayList.get(i).getMemberShipArrayList());
			    } else {
				member_layout.setVisibility(View.GONE);
			    }

			    showOtherInfo(hospitalArrayList.get(i).getHospitalEmail(), hospitalArrayList.get(i).getHospitalLandmark(),
				    hospitalArrayList.get(i).getHospitalAlternateEmail(), hospitalArrayList.get(i).getHospitalext1(),
				    hospitalArrayList.get(i).getHospitalext2(), hospitalArrayList.get(i).getHospitalAlternatephone1(),
				    hospitalArrayList.get(i).getHospitalAlternatephone2(), hospitalArrayList.get(i).getHospitalFax(),
				    hospitalArrayList.get(i).getHospitalWebsite(), hospitalArrayList.get(i).getHospitalCity(),
				    hospitalArrayList.get(i).getHospitalState(), hospitalArrayList.get(i).getHospitalDescription(), hospitalArrayList
					    .get(i).getHospitalSpeciality());
			}
		    }

		}
		if (AppConstants.LogLevel == 1) {
		    try {
			postServerTimepend = Utils.postServerTimeSpend();
			totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			Utils.callPerfomanceTestingApi("Hospital Details list", "HospitalDetailsActivity", "HospitalDetail", preServerTimeSpend,
				serverTimeSpend, postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(HospitalDetailsActivity.this),
				ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""), AppConstants.perfTestCode);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}

	    } catch (JSONException e) {
		e.printStackTrace();
	    }

	}

    }

    /* check network and call hospital details API */
    private void loadHospitalInfo() {
	try {
	    if (isNetworkAvailable()) {
		//doctorsListView.setVisibility(View.VISIBLE);
		//reviewListView.setVisibility(View.VISIBLE);
		((RelativeLayout) findViewById(R.id.profiLayout)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.rateLayoutMain)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.lineLayout)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.favRateLayout)).setVisibility(View.GONE);//Set to gone for new layout
		((LinearLayout) findViewById(R.id.lineLayoutBelowFav)).setVisibility(View.GONE);//Set to gone for new layout

		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
		String hospitalID = ApplicationSettings.getPref(AppConstants.HOSPITAL_ID, "");
		if (hospitalID != null && !hospitalID.equals("")) {
		    getDoctorList = new GetDoctorList();
		    getDoctorList.execute();
		}
	    } else {
	    	
		//doctorsListView.setVisibility(View.GONE);
		//reviewListView.setVisibility(View.GONE);
		((RelativeLayout) findViewById(R.id.profiLayout)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.rateLayoutMain)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.lineLayout)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.favRateLayout)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.lineLayoutBelowFav)).setVisibility(View.GONE);

		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tfRegular);
		((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tfRegular);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /* check for network connection */
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

    /* initialize image caching */
    public void initialiseloadImage() {
	try {
	    imageLoader = ImageLoader.getInstance();
	    options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		    .showImageForEmptyUri(R.drawable.hospital_icon_normal).showImageOnFail(R.drawable.hospital_icon_normal)
		    .showImageOnLoading(R.drawable.hospital_icon_normal).build();
	    
	    optionsMultipleImage = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /* toggle method for view ALL for doctors */
    public void toggleReview() {
	if (!isReadAll) {
	    isReadAll = true;
	    setAssociateDoctorDynamic(doctorArrayList);
	   // viewAll(doctorsListView);
	    viewAllDoctor.setText(Html.fromHtml(getString(R.string.collapseAll) + " " + "<font color=\"#ed207b\">" + doctorListCount + " Doctors"
		    + "</font>"));
	} else {
	    if (doctorListCount > 3) {
		setAssociateDoctorDynamic(hospitalList);
		//viewAll(doctorsListView);
	    }
	    viewAllDoctor.setText(Html.fromHtml(getString(R.string.viewAll) + " " + "<font color=\"#ed207b\">" + doctorListCount + " Doctors"
		    + "</font>"));
	    isReadAll = false;
	   
	}

    }
    /* toggle method for read ALL for Reviews */
    public void toggleRead() {

	if (!isViewAll) {
	    isViewAll = true;
	 
	} else {
	    readAllTextView.setTextColor(getResources().getColor(R.color.black_color));
	    isViewAll = false;
	 
	}

    }

    private void showOtherInfo(String email, String landmark, String AlternateEmail, String ext1, String ext2, String altPhone1, String altPhone2,
	    String fax, String webSite, String city, String state, String description, String speciality) {

	/*
	 * showSpeciality(speciality); showDescription(description);
	 * showWebsite(webSite); showHospitalEmailId(email);
	 * showHospitalLandMark(landmark);
	 */
    }

    // show awards for hospitals
    private void showAwards(ArrayList<String> awaArrayList) {

	TextView[] tv = new TextView[awaArrayList.size()];
	TextView[] tv2 = new TextView[awaArrayList.size()];

	View[] view = new View[awaArrayList.size()];
	Boolean isValid = false;
	for (int i = 0; i < awaArrayList.size(); i++) {

	    LinearLayout ll = new LinearLayout(this);
	    tv[i] = new TextView(HospitalDetailsActivity.this);
	    // tv[i].setText("*  : ");
	    tv[i].setPadding(5, 5, 0, 0);
	    tv[i].setTypeface(tfRegular);
	    tv[i].setTextColor(Color.parseColor("#000000"));

	    tv2[i] = new TextView(HospitalDetailsActivity.this);
	    tv2[i].setText(awaArrayList.get(i).toString());
	    tv2[i].setPadding(5, 5, 0, 0);
	    tv2[i].setTypeface(tfRegular);
	    tv2[i].setTextColor(Color.parseColor("#000000"));
	    ll.setOrientation(LinearLayout.HORIZONTAL);
	    ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    ll.setGravity(Gravity.LEFT);
	    Boolean isTrue = true;

	    if (tv2[i].getText().toString()!= null && !tv2[i].getText().toString().trim().equalsIgnoreCase(null)
		    && !tv2[i].getText().toString().trim().equalsIgnoreCase("[]") && !tv2[i].getText().toString().trim().equalsIgnoreCase("")
		    && !tv2[i].getText().toString().trim().equalsIgnoreCase("null")) {
		ll.addView(tv[i]);
		ll.addView(tv2[i]);
		isValid = true;
	    } else {
		isTrue = false;
	    }

	    if (isValid && (!isTrue || isTrue)) {
		LinearLayout ll9 = new LinearLayout(this);
		view[i] = new View(HospitalDetailsActivity.this);
		view[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
		view[i].setBackgroundColor(Color.rgb(51, 51, 51));
		ll9.addView(view[i]);

		awardsLinearLayout.addView(ll);
		awardsLinearLayout.addView(ll9);
		awardsLinearLayout.setVisibility(View.VISIBLE);
		awards_layout.setVisibility(View.VISIBLE);
	    } else {
		awardsLinearLayout.setVisibility(View.GONE);
		awards_layout.setVisibility(View.GONE);
	    }
	}

    }

    // show speciality
    private void showSpeciality(String speciality) {

	TextView tv = new TextView(HospitalDetailsActivity.this);
	TextView specialityTextView = new TextView(HospitalDetailsActivity.this);

	View view = new View(HospitalDetailsActivity.this);
	LinearLayout ll = new LinearLayout(this);
	tv = new TextView(HospitalDetailsActivity.this);
	// tv.setText("*  : ");
	tv.setPadding(10, 5, 0, 0);
	tv.setTypeface(tfRegular);
	tv.setTextColor(Color.parseColor("#000000"));

	specialityTextView = new TextView(HospitalDetailsActivity.this);
	specialityTextView.setText(speciality);
	specialityTextView.setPadding(5, 5, 0, 0);
	specialityTextView.setTypeface(tfRegular);
	specialityTextView.setTextColor(Color.parseColor("#000000"));

	Boolean isTrue = true;
	ll.addView(tv);
	if (specialityTextView.getText().toString() != null && !specialityTextView.getText().toString().equalsIgnoreCase("")
		&& !specialityTextView.getText().toString().equalsIgnoreCase("null")) {
	    ll.addView(specialityTextView);

	} else {
	    isTrue = false;
	}
	ll.setOrientation(LinearLayout.HORIZONTAL);
	ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	ll.setGravity(Gravity.LEFT);
	if (isTrue) {
	    LinearLayout ll9 = new LinearLayout(this);
	    ll9.setPadding(10, 0, 0, 0);
	    view = new View(HospitalDetailsActivity.this);
	    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
	    view.setBackgroundColor(Color.rgb(51, 51, 51));
	    ll9.addView(view);

	    hospitalSpecialityLayout.addView(ll);
	    hospitalSpecialityLayout.addView(ll9);
	    hospitalSpecialityLayout.setVisibility(View.VISIBLE);
	    hospitalSpecialityMainLayout.setVisibility(View.VISIBLE);
	} else {
	    hospitalSpecialityLayout.setVisibility(View.GONE);
	    hospitalSpecialityMainLayout.setVisibility(View.GONE);
	}

    }

    // show hospital details
    private void showDescription(String description) {

	TextView tv = new TextView(HospitalDetailsActivity.this);
	TextView specialityTextView = new TextView(HospitalDetailsActivity.this);

	View view = new View(HospitalDetailsActivity.this);
	LinearLayout ll = new LinearLayout(this);
	tv = new TextView(HospitalDetailsActivity.this);
	// tv.setText("*  : ");
	tv.setPadding(10, 5, 0, 0);
	tv.setTypeface(tfRegular);
	tv.setTextColor(Color.parseColor("#000000"));

	specialityTextView = new TextView(HospitalDetailsActivity.this);
	specialityTextView.setText(description);
	specialityTextView.setPadding(5, 5, 0, 0);
	specialityTextView.setTypeface(tfRegular);
	specialityTextView.setTextColor(Color.parseColor("#000000"));
	ll.setOrientation(LinearLayout.HORIZONTAL);
	ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	ll.setGravity(Gravity.LEFT);
	Boolean isTrue = true;
	ll.addView(tv);
	if (specialityTextView.getText().toString() != null && !specialityTextView.getText().toString().equalsIgnoreCase("")
		&& !specialityTextView.getText().toString().equalsIgnoreCase("null")) {
	    ll.addView(specialityTextView);

	} else {
	    isTrue = false;
	}

	if (isTrue) {
	    LinearLayout ll9 = new LinearLayout(this);
	    ll9.setPadding(10, 0, 0, 0);
	    view = new View(HospitalDetailsActivity.this);
	    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
	    view.setBackgroundColor(Color.rgb(51, 51, 51));
	    ll9.addView(view);

	    hospitalDescriptionLayout.addView(ll);
	    hospitalDescriptionLayout.addView(ll9);
	    hospitalDescriptionLayout.setVisibility(View.VISIBLE);
	    hospitalDescriptionMainLayout.setVisibility(View.VISIBLE);
	} else {
	    hospitalDescriptionLayout.setVisibility(View.GONE);
	    hospitalDescriptionMainLayout.setVisibility(View.GONE);
	}

    }

    // show hospital details
    private void showWebsite(String website) {

	TextView tv = new TextView(HospitalDetailsActivity.this);
	TextView hospitalTextView = new TextView(HospitalDetailsActivity.this);

	View view = new View(HospitalDetailsActivity.this);
	LinearLayout ll = new LinearLayout(this);
	tv = new TextView(HospitalDetailsActivity.this);
	// tv.setText("*  : ");
	tv.setPadding(10, 5, 0, 0);
	tv.setTypeface(tfRegular);
	tv.setTextColor(Color.parseColor("#000000"));

	hospitalTextView = new TextView(HospitalDetailsActivity.this);
	hospitalTextView.setText(website);
	hospitalTextView.setPadding(17, 5, 0, 0);
	hospitalTextView.setTypeface(tfRegular);
	hospitalTextView.setTextColor(Color.parseColor("#000000"));

	Boolean isTrue = true;
	ll.addView(tv);
	if (hospitalTextView.getText().toString()!= null && !hospitalTextView.getText().toString().trim().equalsIgnoreCase("")
		&& !hospitalTextView.getText().toString().trim().equalsIgnoreCase(null)
		&& !hospitalTextView.getText().toString().trim().equalsIgnoreCase("null")) {
	    ll.addView(hospitalTextView);

	} else {
	    isTrue = false;
	}
	ll.setOrientation(LinearLayout.HORIZONTAL);
	ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	ll.setGravity(Gravity.LEFT);
	if (isTrue) {
	    LinearLayout ll9 = new LinearLayout(this);
	    ll9.setPadding(10, 0, 0, 0);
	    view = new View(HospitalDetailsActivity.this);
	    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
	    view.setBackgroundColor(Color.rgb(51, 51, 51));
	    ll9.addView(view);

	    hospitalWebsiteLayout.addView(ll);
	    hospitalWebsiteLayout.addView(ll9);
	    hospitalWebsiteLayout.setVisibility(View.VISIBLE);
	    hospitalWebsiteMainLayout.setVisibility(View.VISIBLE);
	} else {
	    hospitalWebsiteLayout.setVisibility(View.GONE);//Gone
	    hospitalWebsiteMainLayout.setVisibility(View.GONE);//Gone
	}
    }

    // show membership details
    private void showMemberShip(ArrayList<String> memberShipArrayList) {

	TextView[] tv = new TextView[memberShipArrayList.size()];
	TextView[] tv2 = new TextView[memberShipArrayList.size()];
	View[] view = new View[memberShipArrayList.size()];
	Boolean isValid = false;
	for (int i = 0; i < memberShipArrayList.size(); i++) {

	    LinearLayout ll = new LinearLayout(this);
	    tv[i] = new TextView(HospitalDetailsActivity.this);
	    // tv[i].setText("*  : ");
	    tv[i].setPadding(5, 5, 0, 0);
	    tv[i].setTextColor(Color.parseColor("#000000"));
	    tv[i].setTypeface(tfRegular);

	    tv2[i] = new TextView(HospitalDetailsActivity.this);
	    tv2[i].setText(memberShipArrayList.get(i).toString());
	    tv2[i].setPadding(5, 5, 0, 0);
	    tv2[i].setTextColor(Color.parseColor("#000000"));
	    tv2[i].setTypeface(tfRegular);
	    Boolean isTrue = true;

	    if (tv2[i].getText().toString()!= null && !tv2[i].getText().toString().trim().equalsIgnoreCase(null)
		    && !tv2[i].getText().toString().trim().equalsIgnoreCase("[]") && !tv2[i].getText().toString().trim().equalsIgnoreCase("")
		    && !tv2[i].getText().toString().trim().equalsIgnoreCase("null")) {
		ll.addView(tv[i]);
		ll.addView(tv2[i]);
		isValid = true;
	    } else {
		isTrue = false;
	    }
	    ll.setOrientation(LinearLayout.HORIZONTAL);
	    ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    ll.setGravity(Gravity.LEFT);
	    if (isValid && (!isTrue || isTrue)) {
		LinearLayout ll9 = new LinearLayout(this);
		view[i] = new View(HospitalDetailsActivity.this);
		view[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
		view[i].setBackgroundColor(Color.rgb(51, 51, 51));
		ll9.addView(view[i]);

		membershipLinearLayout.addView(ll);
		membershipLinearLayout.addView(ll9);
		membershipLinearLayout.setVisibility(View.VISIBLE);
		member_layout.setVisibility(View.VISIBLE);
	    } else {

		membershipLinearLayout.setVisibility(View.GONE);
		member_layout.setVisibility(View.GONE);
	    }

	}

    }

    // show hospital email id
    private void showHospitalEmailId(String hospitalEmailId) {

	TextView tv = new TextView(HospitalDetailsActivity.this);
	TextView hospitalTextView = new TextView(HospitalDetailsActivity.this);

	View view = new View(HospitalDetailsActivity.this);
	LinearLayout ll = new LinearLayout(this);
	tv = new TextView(HospitalDetailsActivity.this);
	// tv.setText("*  : ");
	tv.setPadding(10, 5, 0, 0);
	tv.setTypeface(tfRegular);
	tv.setTextColor(Color.parseColor("#000000"));

	hospitalTextView = new TextView(HospitalDetailsActivity.this);
	hospitalTextView.setText(hospitalEmailId);
	hospitalTextView.setPadding(5, 5, 0, 0);
	hospitalTextView.setTypeface(tfRegular);
	hospitalTextView.setTextColor(Color.parseColor("#000000"));

	Boolean isTrue = true;
	ll.addView(tv);
	if (hospitalTextView.getText().toString()!= null && !hospitalTextView.getText().toString().trim().equalsIgnoreCase("")
		&& !hospitalTextView.getText().toString().trim().equalsIgnoreCase(null)
		&& !hospitalTextView.getText().toString().trim().equalsIgnoreCase("null")) {
	    ll.addView(hospitalTextView);

	} else {
	    isTrue = false;
	}
	ll.setOrientation(LinearLayout.HORIZONTAL);
	ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	ll.setGravity(Gravity.LEFT);
	if (isTrue) {
	    LinearLayout ll9 = new LinearLayout(this);
	    ll9.setPadding(10, 0, 0, 0);
	    view = new View(HospitalDetailsActivity.this);
	    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
	    view.setBackgroundColor(Color.rgb(51, 51, 51));
	    ll9.addView(view);

	    hospitalEmaiIdLayout.addView(ll);
	    hospitalEmaiIdLayout.addView(ll9);
	    hospitalEmaiIdLayout.setVisibility(View.VISIBLE);
	    hospitalEmaiIdMainLayout.setVisibility(View.VISIBLE);
	} else {
	    hospitalEmaiIdLayout.setVisibility(View.GONE);
	    hospitalEmaiIdMainLayout.setVisibility(View.GONE);
	}
    }

    // show hospital email id
    private void showHospitalLandMark(String hospitalLandMark) {

	TextView tv = new TextView(HospitalDetailsActivity.this);
	TextView hospitalTextView = new TextView(HospitalDetailsActivity.this);

	View view = new View(HospitalDetailsActivity.this);
	LinearLayout ll = new LinearLayout(this);
	tv = new TextView(HospitalDetailsActivity.this);
	// tv.setText("*  : ");
	tv.setPadding(10, 5, 0, 0);
	tv.setTypeface(tfRegular);
	tv.setTextColor(Color.parseColor("#000000"));

	hospitalTextView = new TextView(HospitalDetailsActivity.this);
	hospitalTextView.setText(hospitalLandMark);
	hospitalTextView.setPadding(5, 5, 0, 0);
	hospitalTextView.setTypeface(tfRegular);
	hospitalTextView.setTextColor(Color.parseColor("#000000"));

	Boolean isTrue = true;
	ll.addView(tv);
	if (hospitalTextView.getText().toString() != null && !hospitalTextView.getText().toString().trim().equalsIgnoreCase("")
		&& !hospitalTextView.getText().toString().trim().equalsIgnoreCase(null)
		&& !hospitalTextView.getText().toString().trim().equalsIgnoreCase("null")) {
	    ll.addView(hospitalTextView);

	} else {
	    isTrue = false;
	}
	ll.setOrientation(LinearLayout.HORIZONTAL);
	ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	ll.setGravity(Gravity.LEFT);
	if (isTrue) {
	    LinearLayout ll9 = new LinearLayout(this);
	    ll9.setPadding(10, 0, 0, 0);
	    view = new View(HospitalDetailsActivity.this);
	    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
	    view.setBackgroundColor(Color.rgb(51, 51, 51));
	    ll9.addView(view);

	    hospitalLandMarkLayout.addView(ll);
	    hospitalLandMarkLayout.addView(ll9);
	    hospitalLandMarkLayout.setVisibility(View.VISIBLE);
	    hospitalLandMarkMainLayout.setVisibility(View.VISIBLE);
	} else {
	    hospitalLandMarkLayout.setVisibility(View.GONE);
	    hospitalLandMarkMainLayout.setVisibility(View.GONE);
	}

    }

    // click event on associated doctors click
    public void clickEvent() {/*
	doctorsListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		DoctorDetailInfo entry = (DoctorDetailInfo) parent.getItemAtPosition(position);
		easyTracker.send(MapBuilder.createEvent("Hospital Details", " View Doctor", docType + ", " + entry.getDoctorName(), null).build());
		ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getDoctorID());
		// ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY,
		// "DOCTOR DETAILS");
		Intent intent = new Intent(HospitalDetailsActivity.this, DoctorDetailsActivity.class);
		if (callingScreen != null && callingScreen.equalsIgnoreCase("Favourite")) {
		    intent.putExtra("CALLING_SCREEN", "Favourite");
		}
		if (callingScreen != null && callingScreen.equalsIgnoreCase("ContactSyncUp")) {
		    intent.putExtra("CALLING_SCREEN", "ContactSyncUp");
		}
		startActivity(intent);
		finish();
	    }
	});
    */}

    // check the boolean for close button in upload prescription for doctors
    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	
	try {
	    scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	        @Override
	        public void onGlobalLayout() {
	    	scrollView.post(new Runnable() {
	    	    public void run() {
	    		scrollView.fullScroll(View.FOCUS_UP);
	    	    }
	    	});
	        }
	    });
	   // if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	} catch (Exception e1) {
	    e1.printStackTrace();
	}
	
	if (callingScreen != null && callingScreen.equalsIgnoreCase("Favourite")) {
	    // favrate.setClickable(false);
	    if (!isFavSaved) {
		// isFavSaved = true;
		favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_save, 0, 0);
		favrate.setText(getString(R.string.favrate));
	    } else {
		favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_saved, 0, 0);
		favrate.setText(getString(R.string.favrateSaved));
	    }
	    if (ApplicationSettings.getPref(AppConstants.SHOW_THANKS_MESSAGE, false)) {
		Utils.customThankYouDialog(this,getString(R.string.reviewSubmitted), getString(R.string.reviewThankyou));
		rateDoctorButton.setClickable(false);
		rateDoctorButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_rated, 0, 0);
		rateDoctorButton.setText(getString(R.string.DocHospRated));
	    }
	} else {
	    if (ApplicationSettings.getPref(AppConstants.SHOW_THANKS_MESSAGE, false)) {
		Utils.customThankYouDialog(this,getString(R.string.reviewSubmitted), getString(R.string.reviewThankyou));
		rateDoctorButton.setClickable(false);
		rateDoctorButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_rated, 0, 0);
		rateDoctorButton.setText(getString(R.string.DocHospRated));
	    }

	    if (RegisterActivity.isRegisteredScreen) {
		// favrate.setClickable(false);
		isFavSaved = true;
		;
		favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_saved, 0, 0);
		favrate.setText(getString(R.string.favrateSaved));
	    }
	}
	try {
	    if (!isEventPressed) {
		if (isHomePressed) {
		    easyTracker.send(MapBuilder.createEvent("Hospital Details", "Device Home", "Device Home", null).build());
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	Boolean isTrue = ApplicationSettings.getPref(AppConstants.CLOSE_BTN, false);
	if (isTrue) {
	    ApplicationSettings.putPref(AppConstants.CLOSE_BTN, false);
	}
    }

    @Override
    public void onStart() {
	super.onStart();
	Log.i("onstart","Inside On Create");
	
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("HospitalDetails Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
	super.onStop();
	//mDemoSlider.stopAutoCycle();
	  FlurryAgent.onEndSession(this);
	isHomePressed = true;
	EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	if (isTrue) {

	    pdia.setCancelable(true);
	    getDoctorList.cancel(true);
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

	    HospitalDetailsActivity.this.finish();
	}
    };

    /* handle back button event */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    isEventPressed = true;
	    if (getDoctorList != null && getDoctorList.getStatus() == Status.RUNNING)
		getDoctorList.cancel(true);
	    if (pdia != null && pdia.isShowing())
		pdia.dismiss();

	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    easyTracker.send(MapBuilder.createEvent("Hospital Details", "Device Back", "Device Back", null).build());

	    backActivity();

	}

	return super.onKeyDown(keyCode, event);
    }

    private void startFavActivity() {
	if (AppConstants.isSaveUnSaveClicked) {
	    if (FavouriteActivity.getInstance() != null) {
		FavouriteActivity.getInstance().finish();
	    }
	    Intent intent = new Intent(HospitalDetailsActivity.this, FavouriteActivity.class);
	    startActivity(intent);
	 //   HospitalDetailsActivity.this.finish();
	}

    }

    private void startContactSyncUpActivity() {
	if (AppConstants.isSaveUnSaveClicked) {
	    if (ContactSyncUpActivity.getInstance() != null) {
		ContactSyncUpActivity.getInstance().finish();
	    }
	    Intent intent = new Intent(HospitalDetailsActivity.this, ContactSyncUpActivity.class);
	    startActivity(intent);
	    //HospitalDetailsActivity.this.finish();
	}

    }

    public class AddFavouriteAsyncTask extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pdia = new ProgressDialog(HospitalDetailsActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(HospitalDetailsActivity.this, getString(R.string.favouriteSaving)));
	    pdia.show();
	}

	@Override
	protected String doInBackground(String... params) {

	    JSONParser jparser = new JSONParser();
	    String result = jparser.addFavourite(params[0], params[1]);

	    return result;
	}

	@Override
	protected void onPostExecute(String result) {
	    super.onPostExecute(result);

	    if (result != null) {
		try {
		    JSONObject jobj = new JSONObject(result);
		    if (jobj != null) {
			String message = jobj.getString("status");
			if (message.equalsIgnoreCase("Success")) {
			    isFavSaved = true;
			    AppConstants.isSaveUnSaveClicked = true;
			    favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_saved, 0, 0);
			    favrate.setText(getString(R.string.favrateSaved));

			} else {
			    Toast.makeText(HospitalDetailsActivity.this, Utils.getCustomeFontStyle(HospitalDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)), Toast.LENGTH_LONG)
				    .show();
			}

		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    } else {
		Toast.makeText(HospitalDetailsActivity.this, Utils.getCustomeFontStyle(HospitalDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)), Toast.LENGTH_LONG)
		    .show();
	    }
	    if (pdia != null) {
		pdia.dismiss();
	    }
	}

    }

    public class DeleteFavouriteAsyncTask extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pdia = new ProgressDialog(HospitalDetailsActivity.this);
	    pdia.setCancelable(true);
	    // pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(HospitalDetailsActivity.this, getString(R.string.favouriteDeleting)));
	    pdia.show();
	}

	@Override
	protected String doInBackground(String... params) {

	    JSONParser jparser = new JSONParser();
	    String result = jparser.deleteFavourite(params[0], params[1]);

	    return result;
	}

	@Override
	protected void onPostExecute(String result) {
	    super.onPostExecute(result);

	    if (result != null) {
		try {
		    JSONObject jobj = new JSONObject(result);
		    if (jobj != null) {
			String message = jobj.getString("status");
			if (message.equalsIgnoreCase("Success")) {
			    isFavSaved = false;
			    AppConstants.isSaveUnSaveClicked = true;
			    favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_save, 0, 0);
			    favrate.setText(getString(R.string.favrate));

			} else {
			    Toast.makeText(HospitalDetailsActivity.this, " Hospital " + Utils.getCustomeFontStyle(HospitalDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
				    Toast.LENGTH_LONG).show();
			}

		    }
		} catch (Exception e) {
		    Toast.makeText(HospitalDetailsActivity.this, " Hospital " + Utils.getCustomeFontStyle(HospitalDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
			    Toast.LENGTH_LONG).show();
		}
	    } else {
		 Toast.makeText(HospitalDetailsActivity.this, " Hospital " + Utils.getCustomeFontStyle(HospitalDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
			    Toast.LENGTH_LONG).show();
	    }
	    if (pdia != null) {
		pdia.dismiss();
	    }
	}

    }

    public void onImageZoom(View view) {
	if(hospitalArrayList.get(0).getHospitalImage() != null 
		&& !hospitalArrayList.get(0).getHospitalImage().trim().equalsIgnoreCase("null")){
	   // new EnlargeImageDialog(this, 0).show();
	}
	
    }
    
    private class MyPagerAdapter extends PagerAdapter {

	    Context context;
	    ImageLoader imageLoader;
	    DisplayImageOptions optionsEnlarg;
	    private Typeface tfRegular;
	    ArrayList<String> imageArray=new ArrayList<String>();
	    MyPagerAdapter(Context context, ArrayList<String> imageArray) {
		this.context = context;
		this.imageArray=imageArray;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
		tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	    }

	   // int NumberOfPages = imageArray.size();

	    @Override
	    public int getCount() {
	    	
		return imageArray.size();
	    }

	    @Override
	    public boolean isViewFromObject(View view, Object object) {
		return view == object;
	    }

	    @Override
	    public Object instantiateItem(ViewGroup container, int position) { 
	    	
//		TextView textView = new TextView(context);
//		textView.setTextColor(Color.BLACK);
//		textView.setTextSize(14);
//		textView.setPadding(5, 7, 0, 0);
//		textView.setTypeface(tfRegular);
//		textView.setText(String.valueOf(position + 1) + " of " + imageArray.size());
//		textView.setVisibility(View.GONE);

		ImageView imageView = new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setPadding(0, 0, 0, 0);
		ProgressBar progresBar = new ProgressBar(context);
		progresBar.setIndeterminate(true);

		LayoutParams layoutParamsProgresBar = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progresBar.setLayoutParams(layoutParamsProgresBar);

		LinearLayout linearLayoutPBar = new LinearLayout(context);
		linearLayoutPBar.setOrientation(LinearLayout.VERTICAL);
		linearLayoutPBar.setGravity(Gravity.CENTER);
		LayoutParams layoutParamsLinearpBar = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		linearLayoutPBar.setLayoutParams(layoutParamsLinearpBar);
		linearLayoutPBar.addView(progresBar);

		setImageEnLargLoading(imageArray.get(position), imageView, optionsEnlarg, progresBar);
		LayoutParams imageParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(imageParams);

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);
		LayoutParams layoutParamsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		linearLayout.setLayoutParams(layoutParamsLinear);

		//linearLayout.addView(textView);
		linearLayout.addView(imageView);

		RelativeLayout layout = new RelativeLayout(context);

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

	    private void setImageEnLargLoading(String imageUrl, ImageView imgProfilePicDisplay, DisplayImageOptions options,
		    final ProgressBar progressBar) {
		imageLoader.displayImage(imageUrl, imgProfilePicDisplay, options, new ImageLoadingListener() {

		    @Override
		    public void onLoadingStarted(String arg0, View arg1) {
			progressBar.setVisibility(View.VISIBLE);
			//textView.setVisibility(View.GONE);

		    }

		    @Override
		    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			progressBar.setVisibility(View.GONE);
		    }

		    @Override
		    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			progressBar.setVisibility(View.GONE);
		//	textView.setVisibility(View.VISIBLE);
		    }

		    @Override
		    public void onLoadingCancelled(String arg0, View arg1) {

		    }
		});
	    }

	}

//    class EnlargeImageDialog extends Dialog implements android.view.View.OnClickListener {
//	Context mContext;
//	private int iPosition;
//	RelativeLayout layoutSecondImage;
//	DisplayImageOptions optionsEnlarg;
//	private ArrayList<String> multiplaeImageList = new ArrayList<String>();
//
//	ViewPager viewPager;
//	MyPagerAdapter myPagerAdapter;
//	ArrayList<String> imageArray;
//
//	public EnlargeImageDialog(Context mContext, int iPosition) {
//	    super(mContext);
//	    this.mContext = mContext;
//	    this.iPosition = iPosition;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//	    super.onCreate(savedInstanceState);
//	    requestWindowFeature(Window.FEATURE_NO_TITLE);
//	    Rect displayRectangle = new Rect();
//	    Window window = ((Activity) mContext).getWindow();
//	    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//	    Log.i("OnCreate Middle","Inside On Create in middle");
//	    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    View layout = inflater.inflate(R.layout.multiple_image_filiper, null);
//	    layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
//	    // layout.setMinimumHeight((int) (displayRectangle.height() *
//	    // 0.9f));
//
//	    optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
//
//	    
//	    setContentView(layout);
//	    Button btnCancel = (Button) findViewById(R.id.close_button_hs);
//	    btnCancel.setOnClickListener(this);
//
//	    if (hospitalArrayList.get(iPosition).getHospitalImage() != null) {
//		    multiplaeImageList.add(hospitalArrayList.get(iPosition).getHospitalImage());
//		}
//		if (hospitalArrayList.get(iPosition).getHospitalEmergencyPic() != null) {
//		    multiplaeImageList.add(hospitalArrayList.get(iPosition).getHospitalEmergencyPic());
//		}
//		if (hospitalArrayList.get(iPosition).getHospitalLeftPic() != null) {
//		    multiplaeImageList.add(hospitalArrayList.get(iPosition).getHospitalLeftPic());
//		}
//		if (hospitalArrayList.get(iPosition).getHospitalRightPic() != null) {
//		    multiplaeImageList.add(hospitalArrayList.get(iPosition).getHospitalRightPic());
//		}
//
//	  //  imageArray = new String[multiplaeImageList.size()];
//
//	    for (int i = 0; i < multiplaeImageList.size(); i++) {
//		imageArray.add(multiplaeImageList.get(i));
//	    }
//
//	    viewPager = (ViewPager) findViewById(R.id.myviewpager);
//	    myPagerAdapter = new MyPagerAdapter(mContext, imageArray);
//	    viewPager.setAdapter(myPagerAdapter);
//
//	    // txtCountMultip.setVisibility(View.VISIBLE);
//
//	}
//
////	private class MyPagerAdapter extends PagerAdapter {
////
////	    Context context;
////	    ImageLoader imageLoader;
////	    DisplayImageOptions optionsEnlarg;
////	    private Typeface tfRegular;
////
////	    MyPagerAdapter(Context context, ArrayList<String> imageArray) {
////		this.context = context;
////		imageLoader = ImageLoader.getInstance();
////		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
////		optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
////		tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
////	    }
////
////	    int NumberOfPages = imageArray.size();
////
////	    @Override
////	    public int getCount() {
////		return NumberOfPages;
////	    }
////
////	    @Override
////	    public boolean isViewFromObject(View view, Object object) {
////		return view == object;
////	    }
////
////	    @Override
////	    public Object instantiateItem(ViewGroup container, int position) {
////
////		TextView textView = new TextView(mContext);
////		textView.setTextColor(Color.BLACK);
////		textView.setTextSize(14);
////		textView.setPadding(5, 7, 0, 0);
////		textView.setTypeface(tfRegular);
////		textView.setText(String.valueOf(position + 1) + " of " + NumberOfPages);
////		textView.setVisibility(View.GONE);
////
////		ImageView imageView = new ImageView(mContext);
////		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
////		imageView.setPadding(0, 10, 0, 0);
////		ProgressBar progresBar = new ProgressBar(mContext);
////		progresBar.setIndeterminate(true);
////
////		LayoutParams layoutParamsProgresBar = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
////		progresBar.setLayoutParams(layoutParamsProgresBar);
////
////		LinearLayout linearLayoutPBar = new LinearLayout(mContext);
////		linearLayoutPBar.setOrientation(LinearLayout.VERTICAL);
////		linearLayoutPBar.setGravity(Gravity.CENTER);
////		LayoutParams layoutParamsLinearpBar = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
////		linearLayoutPBar.setLayoutParams(layoutParamsLinearpBar);
////		linearLayoutPBar.addView(progresBar);
////
////		setImageEnLargLoading(imageArray.get(position), imageView, optionsEnlarg, textView, progresBar);
////		LayoutParams imageParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
////		imageView.setLayoutParams(imageParams);
////
////		LinearLayout linearLayout = new LinearLayout(mContext);
////		linearLayout.setOrientation(LinearLayout.VERTICAL);
////		linearLayout.setGravity(Gravity.CENTER);
////		LayoutParams layoutParamsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
////		linearLayout.setLayoutParams(layoutParamsLinear);
////
////		linearLayout.addView(textView);
////		linearLayout.addView(imageView);
////
////		RelativeLayout layout = new RelativeLayout(mContext);
////
////		layout.setGravity(Gravity.CENTER);
////		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 300);
////		layout.setBackgroundColor(Color.WHITE);
////		layout.setLayoutParams(layoutParams);
////
////		layout.addView(linearLayoutPBar);
////		layout.addView(linearLayout);
////
////		container.addView(layout);
////		return layout;
////	    }
////
////	    @Override
////	    public void destroyItem(ViewGroup container, int position, Object object) {
////		container.removeView((RelativeLayout) object);
////	    }
////
////	    private void setImageEnLargLoading(String imageUrl, ImageView imgProfilePicDisplay, DisplayImageOptions options, final TextView textView,
////		    final ProgressBar progressBar) {
////		imageLoader.displayImage(imageUrl, imgProfilePicDisplay, options, new ImageLoadingListener() {
////
////		    @Override
////		    public void onLoadingStarted(String arg0, View arg1) {
////			progressBar.setVisibility(View.VISIBLE);
////			textView.setVisibility(View.GONE);
////
////		    }
////
////		    @Override
////		    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
////			progressBar.setVisibility(View.GONE);
////		    }
////
////		    @Override
////		    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
////			progressBar.setVisibility(View.GONE);
////			textView.setVisibility(View.VISIBLE);
////		    }
////
////		    @Override
////		    public void onLoadingCancelled(String arg0, View arg1) {
////
////		    }
////		});
////	    }
////
////	}
//
//	@Override
//	public void onClick(View v) {
//	    int id = v.getId();
//	    switch (id) {
//	    case R.id.close_button_hs:
//		try {
//		    dismiss();
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//		break;
//	    default:
//		break;
//	    }
//
//	}
//    }
    
    public void setFacilitiesValues(ArrayList<HospitalInfo> hospitalArrayList) {
	for (int i = 0; i < hospitalArrayList.size(); i++) {
	    if (hospitalArrayList.get(i).getHospitalBedsNumber() != null && !hospitalArrayList.get(i).getHospitalBedsNumber().equalsIgnoreCase("")
		    && !hospitalArrayList.get(i).getHospitalBedsNumber().equalsIgnoreCase("null")
		    && !hospitalArrayList.get(i).getHospitalBedsNumber().equalsIgnoreCase(null)) {
		bedsValues.setVisibility(View.VISIBLE);
		bedsValues.setText(getString(R.string.beds) + " " + hospitalArrayList.get(i).getHospitalBedsNumber());
	    } else {
		bedsValues.setVisibility(View.GONE);
	    }
	    if (hospitalArrayList.get(i).getHospitalEmergencyServices() != null
		    && !hospitalArrayList.get(i).getHospitalEmergencyServices().equalsIgnoreCase("")
		    && !hospitalArrayList.get(i).getHospitalEmergencyServices().equalsIgnoreCase("null")
		    && !hospitalArrayList.get(i).getHospitalEmergencyServices().equalsIgnoreCase(null)) {
		emergencyServices.setVisibility(View.VISIBLE);
		emergencyServices.setText(getString(R.string.emergencyServices) + " " + hospitalArrayList.get(i).getHospitalEmergencyServices());
	    } else {
		emergencyServices.setVisibility(View.GONE);
	    }
	    if (hospitalArrayList.get(i).getHospitalICUServices() != null && !hospitalArrayList.get(i).getHospitalICUServices().equalsIgnoreCase("")
		    && !hospitalArrayList.get(i).getHospitalICUServices().equalsIgnoreCase("null")
		    && !hospitalArrayList.get(i).getHospitalICUServices().equalsIgnoreCase(null)) {
		icuServices.setVisibility(View.VISIBLE);
		icuServices.setText(getString(R.string.ICUIntesiveCareUnit) + " " + hospitalArrayList.get(i).getHospitalICUServices());
	    } else {
		icuServices.setVisibility(View.GONE);
	    }
	    if (hospitalArrayList.get(i).getHospitalNICUServices() != null
		    && !hospitalArrayList.get(i).getHospitalNICUServices().equalsIgnoreCase("")
		    && !hospitalArrayList.get(i).getHospitalNICUServices().equalsIgnoreCase("null")
		    && !hospitalArrayList.get(i).getHospitalNICUServices().equalsIgnoreCase(null)) {
		nicuServices.setVisibility(View.VISIBLE);
		nicuServices.setText(getString(R.string.NICUNeoNatalIntensiveCareUnit) + " " + hospitalArrayList.get(i).getHospitalNICUServices());
	    } else {
		nicuServices.setVisibility(View.GONE);
	    }
	    if (hospitalArrayList.get(i).getHospitalAmbulanceNumber() != null
		    && !hospitalArrayList.get(i).getHospitalAmbulanceNumber().equalsIgnoreCase("")
		    && !hospitalArrayList.get(i).getHospitalAmbulanceNumber().equalsIgnoreCase("null")
		    && !hospitalArrayList.get(i).getHospitalAmbulanceNumber().equalsIgnoreCase(null)) {
		ambulanceServices.setVisibility(View.VISIBLE);
		ambulanceServices.setText(getString(R.string.ambulancesService) + " " + hospitalArrayList.get(i).getHospitalAmbulanceNumber());
	    } else {
		ambulanceServices.setVisibility(View.GONE);
	    }
	    if (hospitalArrayList.get(i).getHospitalParking() != null && !hospitalArrayList.get(i).getHospitalParking().equalsIgnoreCase("")
		    && !hospitalArrayList.get(i).getHospitalParking().equalsIgnoreCase("null")
		    && !hospitalArrayList.get(i).getHospitalParking().equalsIgnoreCase(null)) {
		hospitalParking.setVisibility(View.VISIBLE);
		hospitalParking.setText(getString(R.string.HospitalParking) + " " + hospitalArrayList.get(i).getHospitalParking());
	    } else {
		hospitalParking.setVisibility(View.GONE);
	    }
	}
	if (bedsValues.getVisibility() == View.GONE && emergencyServices.getVisibility() == View.GONE && icuServices.getVisibility() == View.GONE
		&& nicuServices.getVisibility() == View.GONE && ambulanceServices.getVisibility() == View.GONE
		&& hospitalParking.getVisibility() == View.GONE) {
	    facilityLayout.setVisibility(View.GONE);
	    ((LinearLayout) findViewById(R.id.facilityBelowLine)).setVisibility(View.GONE);
	} else {
	    facilityLayout.setVisibility(View.VISIBLE);
	    ((LinearLayout) findViewById(R.id.facilityBelowLine)).setVisibility(View.VISIBLE);
	    
	}

    }

    public void onMapClicked(View view) {
	try {
	    easyTracker.send(MapBuilder.createEvent("Hospital Details", "Map View", "Map View", null).build());
	    for (int i = 0; i < hospitalArrayList.size(); i++) {
		Utils.routeDirection(this, Double.valueOf(ApplicationSettings.getPref(AppConstants.LATITUDE, "")),
			Double.valueOf(ApplicationSettings.getPref(AppConstants.LONGITUDE, "")),
			Double.valueOf(hospitalArrayList.get(i).getHospitalLat()), Double.valueOf(hospitalArrayList.get(i).getHospitalLong()));
	    }
	} catch (NumberFormatException e) {
	    e.printStackTrace();
	}
    }
    
    private void setAssociateDoctorDynamic(final ArrayList<DoctorDetailInfo> hospitalList) {

	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.ic_action_person).showImageOnFail(R.drawable.ic_action_person)
		.showImageOnLoading(R.drawable.ic_action_person).build();
	
	
	LinearLayout associateDoctorLayout = (LinearLayout) findViewById(R.id.associateDoctorLayout);
	//RatingBar associatDocRating  = (RatingBar)associateDoctorLayout.findViewById(R.id.associatDocRating);

	if(((LinearLayout) associateDoctorLayout).getChildCount() > 0){
	    ((LinearLayout) associateDoctorLayout).removeAllViews(); 
	}
	
	LinearLayout subMainLayout = null;
	LinearLayout imgLayout = null;
	LinearLayout ratingLayout = null;
	LinearLayout divider = null;
	LinearLayout layoutMain = null;
	
	int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
	int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
	
	for (int k = 0; k < hospitalList.size(); k++) {

	    divider = new LinearLayout(HospitalDetailsActivity.this);
	    layoutMain = new LinearLayout(HospitalDetailsActivity.this);
	    layoutMain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    layoutMain.setOrientation(LinearLayout.HORIZONTAL);
	    layoutMain.setGravity(Gravity.CENTER_VERTICAL);
	    //Commented out for Dec layout for not showing div lines b/w multiple doctors
	   /* divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    divider.setOrientation(LinearLayout.HORIZONTAL);
	    divider.setBackgroundResource(R.drawable.div_line);*/
	   
	    imgLayout = new LinearLayout(HospitalDetailsActivity.this);
	    imgLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    imgLayout.setId(k);
	    
	    ratingLayout = new LinearLayout(HospitalDetailsActivity.this);
	    ratingLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    ratingLayout.setOrientation(LinearLayout.HORIZONTAL);

	    ImageView imgProfile = new ImageView(HospitalDetailsActivity.this);
	    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
	    imgProfile.setLayoutParams(parms);
	    imgProfile.setScaleType(ScaleType.FIT_XY); 
	    imgProfile.setPadding(0, 10, 10, 10);
	    
	    subMainLayout = new LinearLayout(HospitalDetailsActivity.this);
	    subMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    subMainLayout.setOrientation(LinearLayout.VERTICAL);
	    subMainLayout.setGravity(Gravity.CENTER|Gravity.LEFT);
	    subMainLayout.setId(k);
	    
	    LinearLayout verifiedLayout = new LinearLayout(HospitalDetailsActivity.this);
	    verifiedLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    verifiedLayout.setOrientation(LinearLayout.HORIZONTAL);
	    verifiedLayout.setGravity(Gravity.LEFT);
	    
	    
	    ImageView verifiedImage = new ImageView(HospitalDetailsActivity.this);
	 //   LinearLayout.LayoutParams verifiedImageparms = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	  //  verifiedImage.setLayoutParams(verifiedImageparms);
	    verifiedImage.setPadding(0, 0, 2, 0);
	    verifiedImage.setImageResource(R.drawable.screen_logo_verified);
	    verifiedImage.setVisibility(View.GONE);//Set invisible for the new layout
	    verifiedLayout.addView(verifiedImage);
	    
	    
	    TextView txtverified = new TextView(HospitalDetailsActivity.this);
	    txtverified.setTypeface(tfLite);
	    txtverified.setTextSize(14);
	    txtverified.setTextColor(getResources().getColor(R.color.black));
	    txtverified.setVisibility(View.GONE);//Set invisible for the new layout

	    try {
		if (hospitalList.get(k).getDoctorPic() != null && !hospitalList.get(k).getDoctorPic().equalsIgnoreCase("")
			&& !hospitalList.get(k).getDoctorPic().equalsIgnoreCase(null) && !hospitalList.get(k).getDoctorPic().equalsIgnoreCase("null")) {
		  //  imageLoader.displayImage(hospitalList.get(k).getDoctorPic(), imgProfile, options);
			//Circle Transform has been used to make the DP of the doc appear as circular
		    Glide.with(this).load(hospitalList.get(k).getDoctorPic()).error(R.drawable.ic_action_person).transform(new CircleTransform(this)).into(imgProfile);
		     
		    txtverified.setText(getString(R.string.msgVisited));
		} else {
		    imgProfile.setImageResource(R.drawable.ic_action_person);
		    txtverified.setText(getString(R.string.msgVerified));
		}
		imgLayout.addView(imgProfile);
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	   
	    verifiedLayout.addView(txtverified);

	    TextView txtProfileName = new TextView(HospitalDetailsActivity.this);
	    txtProfileName.setTypeface(tfRegular);
	    txtProfileName.setTextSize(15);
	    txtProfileName.setText(getResources().getString(R.string.dr) + " " + hospitalList.get(k).getDoctorName());
	    txtProfileName.setTextColor(getResources().getColor(R.color.black));
	    subMainLayout.addView(txtProfileName);

	    TextView txtProfileDesignation = new TextView(HospitalDetailsActivity.this);
	    txtProfileDesignation.setTypeface(tfLite);
	    txtProfileDesignation.setTextSize(14);
	    txtProfileDesignation.setTextColor(getResources().getColor(R.color.black));

	    try {
		String strValue = "";
		boolean isTrue = true;
		if (hospitalList.get(k).getDoctorType()!= null && !hospitalList.get(k).getDoctorType().trim().equalsIgnoreCase("")
			&& !hospitalList.get(k).getDoctorType().trim().equalsIgnoreCase("null")
			&& !hospitalList.get(k).getDoctorType().trim().equalsIgnoreCase(null)) {
		    isTrue = false;
		    strValue = hospitalList.get(k).getDoctorType().trim();
		}
		if (hospitalList.get(k).getDoctorQualification()!= null
			&& !hospitalList.get(k).getDoctorQualification().trim().equalsIgnoreCase("")
			&& !hospitalList.get(k).getDoctorQualification().trim().equalsIgnoreCase("null")
			&& !hospitalList.get(k).getDoctorQualification().trim().equalsIgnoreCase(null)) {
		    if (!isTrue) {
			strValue = strValue + " - " + hospitalList.get(k).getDoctorQualification().trim();
		    } else {
			strValue = hospitalList.get(k).getDoctorQualification().trim();
		    }
		    isTrue = false;
		}
		if (!isTrue) {
		    txtProfileDesignation.setVisibility(View.VISIBLE);
		    txtProfileDesignation.setText(strValue);
		    subMainLayout.addView(txtProfileDesignation);
		} else {
		    txtProfileDesignation.setVisibility(View.GONE);
		}

	    } catch (Exception e1) {
		e1.printStackTrace();
	    }

	    TextView txtTotalExperiance = new TextView(HospitalDetailsActivity.this);
	    txtTotalExperiance.setTypeface(tfLite);
	    txtTotalExperiance.setTextSize(14);
	    txtTotalExperiance.setTextColor(getResources().getColor(R.color.black));

	    try {
		String doctExperiance = hospitalList.get(k).getDoctorTotalExperience().trim();
		if (doctExperiance != null && !doctExperiance.equalsIgnoreCase("") && !doctExperiance.equalsIgnoreCase("null")
			&& !doctExperiance.equalsIgnoreCase(null) && doctExperiance.equalsIgnoreCase("1") || doctExperiance.equalsIgnoreCase("01")
			|| doctExperiance.equalsIgnoreCase("001") && !doctExperiance.equalsIgnoreCase("0")) {
		    txtTotalExperiance.setVisibility(View.VISIBLE);
		    txtTotalExperiance.setText(doctExperiance + " " + getString(R.string.doctorExp));
		    subMainLayout.addView(txtTotalExperiance);
		} else if (doctExperiance != null && !doctExperiance.equalsIgnoreCase("") && !doctExperiance.equalsIgnoreCase("null")
			&& !doctExperiance.equalsIgnoreCase(null) && !doctExperiance.equalsIgnoreCase("1") && !doctExperiance.equalsIgnoreCase("0")
			&& !doctExperiance.equalsIgnoreCase("00") && !doctExperiance.equalsIgnoreCase("000")) {
		    boolean isTrue = true;
		    try {
			int iValue = Integer.parseInt(doctExperiance);
		    } catch (NumberFormatException e) {
			isTrue = false;
		    }
		    if (isTrue) {
			txtTotalExperiance.setVisibility(View.VISIBLE);
			txtTotalExperiance.setText(doctExperiance + " " + getString(R.string.doctorExps));
			subMainLayout.addView(txtTotalExperiance);
		    } else {
			txtTotalExperiance.setVisibility(View.GONE);
		    }

		} else {
		    txtTotalExperiance.setVisibility(View.GONE);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    try {
		if (hospitalList.get(k).getDoctorAverage() != null && !hospitalList.get(k).getDoctorAverage().equalsIgnoreCase("")
			&& !hospitalList.get(k).getDoctorAverage().equalsIgnoreCase("null")
			&& !hospitalList.get(k).getDoctorAverage().equalsIgnoreCase(null)
			&& !hospitalList.get(k).getDoctorAverage().equalsIgnoreCase("0.0")) {
		    RatingBar associatDocRating = new RatingBar(new ContextThemeWrapper(HospitalDetailsActivity.this, R.style.ratingBar), null, 0);
		    associatDocRating.setNumStars(5);
		    long lValue = Math.round(Double.valueOf(hospitalList.get(k).getDoctorAverage()));
		    associatDocRating.setRating(lValue);
		    associatDocRating.setIsIndicator(true);
		    ratingLayout.addView(associatDocRating);
		    subMainLayout.addView(ratingLayout);
		}
	    } catch (NumberFormatException e) {
		e.printStackTrace();
	    }catch (Exception e) {
		e.printStackTrace();
	    }
	    subMainLayout.addView(verifiedLayout);
	    
	    layoutMain.addView(imgLayout);
	    layoutMain.addView(subMainLayout);
	    associateDoctorLayout.addView(layoutMain);
	    associateDoctorLayout.addView(divider);
	    
	    subMainLayout.setOnClickListener(new OnClickListener() {
	        
	        @Override
	        public void onClick(View v) {
	            int id = v.getId();
			easyTracker.send(MapBuilder.createEvent("Hospital Details", " View Doctor", docType + ", " + hospitalList.get(id).getDoctorName(), null).build());
			ApplicationSettings.putPref(AppConstants.DOCTOR_ID, hospitalList.get(id).getDoctorID());
			Intent intent = new Intent(HospitalDetailsActivity.this, DoctorDetailsActivity.class);
			/*if(callingScreen!=null && callingScreen.equalsIgnoreCase("Favourite")){
			    intent.putExtra("CALLING_SCREEN", "Favourite");
			}
			if(callingScreen!=null && callingScreen.equalsIgnoreCase("ContactSyncUp")){
			    intent.putExtra("CALLING_SCREEN", "ContactSyncUp");
			}
			if(callingScreen!=null && callingScreen.equalsIgnoreCase("ReviewsMenuList")){
			    intent.putExtra("CALLING_SCREEN", "ReviewsMenuList");
			}*/
			if(DoctorDetailsActivity.getInstance()!=null){
			    DoctorDetailsActivity.getInstance().finish();
			}
			startActivity(intent);
			//finish();
	    	
	        }
	    });
	    imgLayout.setOnClickListener(new OnClickListener() {
	        
	        @Override
	        public void onClick(View v) {
	            int id = v.getId();
//	            if (hospitalList.get(id).getDoctorPic() != null
//	        	    && !hospitalList.get(id).getDoctorPic().trim().equalsIgnoreCase("null")) {
//	        	 new EnlargeImageDialogAssociateDoctor(HospitalDetailsActivity.this, id,hospitalList).show();
//	            }
				easyTracker.send(MapBuilder.createEvent("Hospital Details", " View Doctor", docType + ", " + hospitalList.get(id).getDoctorName(), null).build());
				ApplicationSettings.putPref(AppConstants.DOCTOR_ID, hospitalList.get(id).getDoctorID());
				Intent intent = new Intent(HospitalDetailsActivity.this, DoctorDetailsActivity.class);
				if(DoctorDetailsActivity.getInstance()!=null){
				    DoctorDetailsActivity.getInstance().finish();
				}
				startActivity(intent);
	           
	        }
	    });
	}

    }

    private void setReviewsViewDynamically(ArrayList<ReviewsInfo> reviewList) {
	try {
	    
	    LinearLayout layoutMain = (LinearLayout) findViewById(R.id.reviewsLayout);
	    LinearLayout reviewsLayout = null;
	    LinearLayout reviewsLayoutDesc = null;
	    LinearLayout reviewsDivider = null;
	    LinearLayout reviewsRating = null;
	    
	    for (int k = 0; k < reviewList.size(); k++) {

		reviewsLayout = new LinearLayout(HospitalDetailsActivity.this);
		reviewsLayoutDesc = new LinearLayout(HospitalDetailsActivity.this);
		reviewsDivider = new LinearLayout(HospitalDetailsActivity.this);
		reviewsRating = new LinearLayout(HospitalDetailsActivity.this);
		
		
		 View to_add =  getLayoutInflater().inflate(R.layout.ratingbardetail,
				 reviewsRating,false);		 
		 RatingBar ratingBar=(RatingBar)to_add.findViewById(R.id.ratingBarDetail);
		 ratingBar.setRating(Integer.parseInt(reviewList.get(k).getRate()));
		 ratingBar.setIsIndicator(true);
		 
		 
		
		TextView txtEmail = new TextView(HospitalDetailsActivity.this);
		txtEmail.setTypeface(tfRegular);
		txtEmail.setText(" "+reviewList.get(k).getUserName()+" "); 
		//txtEmail.setText(reviewList.get(k).getActualUsername());
		txtEmail.setTextColor(getResources().getColor(R.color.screen_header));

		TextView txtCreatedDate = new TextView(HospitalDetailsActivity.this);
		txtCreatedDate.setTypeface(tfLite);
		txtCreatedDate.setText(reviewList.get(k).getCreatedDate());
		txtCreatedDate.setPadding(5, 0, 5, 0);

		TextView txtReviewDescription = new TextView(HospitalDetailsActivity.this);
		txtReviewDescription.setTypeface(tfLite);
		txtReviewDescription.setText(reviewList.get(k).getReview());
		txtReviewDescription.setPadding(5, 5, 5, 5);

		reviewsRating.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		reviewsRating.setOrientation(LinearLayout.HORIZONTAL);
		
		reviewsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		reviewsLayout.setOrientation(LinearLayout.HORIZONTAL);

		reviewsLayoutDesc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		reviewsLayoutDesc.setOrientation(LinearLayout.HORIZONTAL);

		reviewsDivider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		reviewsDivider.setOrientation(LinearLayout.HORIZONTAL);
		reviewsDivider.setBackgroundResource(R.drawable.div_line);

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutMain.getLayoutParams();
		params.setMargins(10, 10, 10, 10);
		layoutMain.setLayoutParams(params);
		
		reviewsRating.addView(to_add);
		reviewsLayout.addView(txtEmail);
		reviewsLayout.addView(txtCreatedDate);
		reviewsLayoutDesc.addView(txtReviewDescription);
		layoutMain.addView(reviewsLayout);
		layoutMain.addView(reviewsRating);
		layoutMain.addView(reviewsLayoutDesc);
		layoutMain.addView(reviewsDivider);
	    }
	} catch (NotFoundException e) {
	    e.printStackTrace();
	}

    }
    
//    class EnlargeImageDialogAssociateDoctor extends Dialog implements android.view.View.OnClickListener {
//	Context mContext;
//	private float lastX;
//	RelativeLayout layoutSecondImage;
//	private ImageView imageViewFirst, imageViewSecond;
//	private TextView txtCount;
//	private int iCount = 0;
//	DisplayImageOptions optionsEnlarg;
//	private ImageLoader imageLoader;
//	ArrayList<DoctorDetailInfo> associateDocList;
//
//	private int iPosition;
//	private ArrayList<String> multiplaeImageList = new ArrayList<String>();
//
//	ViewPager viewPager;
//	MyPagerAdapter myPagerAdapter;
//	String[] imageArray;
//
//	public EnlargeImageDialogAssociateDoctor(Context mContext, int iPosition, ArrayList<DoctorDetailInfo> associateDocList) {
//	    super(mContext);
//	    imageLoader = ImageLoader.getInstance();
//	    this.mContext = mContext;
//	    this.iPosition = iPosition;
//	    this.associateDocList = associateDocList;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//	    super.onCreate(savedInstanceState);
//	    requestWindowFeature(Window.FEATURE_NO_TITLE);
//	    Rect displayRectangle = new Rect();
//	    Window window = ((Activity) mContext).getWindow();
//	    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//	    Log.i("On Create Bottom","Inside on create at bottom");
//	    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    View layout = inflater.inflate(R.layout.multiple_image_filiper, null);
//	    layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
//	    // layout.setMinimumHeight((int) (displayRectangle.height() *
//	    // 0.9f));
//
//	    optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
//
//	    setContentView(layout);
//	    Button btnCancel = (Button) findViewById(R.id.close_button_hs);
//	    btnCancel.setOnClickListener(this);
//
//	    if (associateDocList.get(iPosition).getDoctorPic() != null) {
//		multiplaeImageList.add(associateDocList.get(iPosition).getDoctorPic());
//	    }
//	    if (associateDocList.get(iPosition).getDoctorSecondImage() != null) {
//		multiplaeImageList.add(associateDocList.get(iPosition).getDoctorSecondImage());
//	    }
//	    imageArray = new String[multiplaeImageList.size()];
//
//	    for (int i = 0; i < multiplaeImageList.size(); i++) {
//		imageArray[i] = multiplaeImageList.get(i);
//	    }
//
//	    viewPager = (ViewPager) findViewById(R.id.myviewpager);
//	    myPagerAdapter = new MyPagerAdapter(mContext, imageArray);
//	    viewPager.setAdapter(myPagerAdapter);
//
//	    // txtCountMultip.setVisibility(View.VISIBLE);
//
//	}
//
//	private class MyPagerAdapter extends PagerAdapter {
//
//	    Context context;
//	    ImageLoader imageLoader;
//	    DisplayImageOptions optionsEnlarg;
//	    private Typeface tfRegular;
//
//	    MyPagerAdapter(Context context, String[] imageArray) {
//		this.context = context;
//		imageLoader = ImageLoader.getInstance();
//		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
//		optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
//		tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
//	    }
//
//	    int NumberOfPages = imageArray.length;
//
//	    @Override
//	    public int getCount() {
//		return NumberOfPages;
//	    }
//
//	    @Override
//	    public boolean isViewFromObject(View view, Object object) {
//		return view == object;
//	    }
//
//	    @Override
//	    public Object instantiateItem(ViewGroup container, int position) {
//
//		TextView textView = new TextView(mContext);
//		textView.setTextColor(Color.BLACK);
//		textView.setTextSize(14);
//		textView.setPadding(5, 7, 0, 0);
//		textView.setTypeface(tfRegular);
//		textView.setText(String.valueOf(position + 1) + " of " + NumberOfPages);
//		textView.setVisibility(View.GONE);
//
//		ImageView imageView = new ImageView(mContext);
//		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//		imageView.setPadding(0, 10, 0, 0);
//		ProgressBar progresBar = new ProgressBar(mContext);
//		progresBar.setIndeterminate(true);
//
//		LayoutParams layoutParamsProgresBar = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		progresBar.setLayoutParams(layoutParamsProgresBar);
//
//		LinearLayout linearLayoutPBar = new LinearLayout(mContext);
//		linearLayoutPBar.setOrientation(LinearLayout.VERTICAL);
//		linearLayoutPBar.setGravity(Gravity.CENTER);
//		LayoutParams layoutParamsLinearpBar = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		linearLayoutPBar.setLayoutParams(layoutParamsLinearpBar);
//		linearLayoutPBar.addView(progresBar);
//
//		setImageEnLargLoading(imageArray[position], imageView, optionsEnlarg, textView, progresBar);
//		LayoutParams imageParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		imageView.setLayoutParams(imageParams);
//
//		LinearLayout linearLayout = new LinearLayout(mContext);
//		linearLayout.setOrientation(LinearLayout.VERTICAL);
//		linearLayout.setGravity(Gravity.CENTER);
//		LayoutParams layoutParamsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		linearLayout.setLayoutParams(layoutParamsLinear);
//
//		linearLayout.addView(textView);
//		linearLayout.addView(imageView);
//
//		RelativeLayout layout = new RelativeLayout(mContext);
//
//		layout.setGravity(Gravity.CENTER);
//		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 300);
//		layout.setBackgroundColor(Color.WHITE);
//		layout.setLayoutParams(layoutParams);
//
//		layout.addView(linearLayoutPBar);
//		layout.addView(linearLayout);
//
//		container.addView(layout);
//		return layout;
//	    }
//
//	    @Override
//	    public void destroyItem(ViewGroup container, int position, Object object) {
//		container.removeView((RelativeLayout) object);
//	    }
//
//	    private void setImageEnLargLoading(String imageUrl, ImageView imgProfilePicDisplay, DisplayImageOptions options, final TextView textView,
//		    final ProgressBar progressBar) {
//		imageLoader.displayImage(imageUrl, imgProfilePicDisplay, options, new ImageLoadingListener() {
//
//		    @Override
//		    public void onLoadingStarted(String arg0, View arg1) {
//			progressBar.setVisibility(View.VISIBLE);
//			textView.setVisibility(View.GONE);
//
//		    }
//
//		    @Override
//		    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//			progressBar.setVisibility(View.GONE);
//		    }
//
//		    @Override
//		    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//			progressBar.setVisibility(View.GONE);
//			textView.setVisibility(View.VISIBLE);
//		    }
//
//		    @Override
//		    public void onLoadingCancelled(String arg0, View arg1) {
//
//		    }
//		});
//	    }
//
//	}
//
//	@Override
//	public void onClick(View v) {
//	    int id = v.getId();
//	    switch (id) {
//	    case R.id.close_button_hs:
//		try {
//		    dismiss();
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//		break;
//	    default:
//		break;
//	    }
//
//	}
//    }

    private void backActivity() {
	if (callingScreen != null && callingScreen.equalsIgnoreCase("Favourite")) {
	    startFavActivity();
	}
	if (callingScreen != null && callingScreen.equalsIgnoreCase("ContactSyncUp")) {
	    startContactSyncUpActivity();
	}
	if (callingScreen != null && callingScreen.equalsIgnoreCase("ReviewsMenuList")) {
	    startReviewsMenuActivity();
	}
	HospitalDetailsActivity.this.finish();
    }
    private void startReviewsMenuActivity() {
  	if (AppConstants.isSaveUnSaveClicked) {
  	    if (ReviewsActivity.getInstance() != null) {
  		ReviewsActivity.getInstance().finish();
  	    }
  	    Intent intent = new Intent(HospitalDetailsActivity.this, ReviewsActivity.class);
  	    startActivity(intent);
  	   // HospitalDetailsActivity.this.finish();
  	}

      }

    public void onEmergencyImage(View view) {
	//enlargImageDialog(1);
	//new EnlargeImageDialog(this, 1).show();
    }

    public void onLeftImage(View view) {
	//enlargImageDialog(2);
	//new EnlargeImageDialog(this, 2).show();
    }

    public void onRightImage(View view) {
	//enlargImageDialog(3);
	//new EnlargeImageDialog(this, 3).show();
    }
    public static HospitalDetailsActivity getInstance() {
   	return hospitalDetailsScreen;
       }

//	@Override
//	public void onSliderClick(BaseSliderView slider) {
//		// TODO Auto-generated method stub
//		
//	}
}