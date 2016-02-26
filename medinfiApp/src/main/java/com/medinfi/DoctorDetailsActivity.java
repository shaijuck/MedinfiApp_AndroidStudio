package com.medinfi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.SherlockActivity;
import com.bumptech.glide.Glide;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.adapters.HospitalsAdapter;
import com.medinfi.adapters.ReviewAdapter;
import com.medinfi.datainfo.DoctorInfo;
import com.medinfi.datainfo.ExperienceInfo;
import com.medinfi.datainfo.HospitalInfo;
import com.medinfi.datainfo.QualificationInfo;
import com.medinfi.datainfo.ReviewsInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CircleTransform;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.DynamicListViewHeight;
import com.medinfi.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class DoctorDetailsActivity extends SherlockActivity implements OnClickListener {

    // Initialize buttons
    private TextView rateDoctorButton;
    private TextView shareButton;
    private TextView favrate;

    // Initialize arraylist for
    // doctors,reviews,hospitals,experiences,qualification
    private ArrayList<DoctorInfo> doctorArrayList;
    private ArrayList<ReviewsInfo> reviewArrayList;
    private ArrayList<HospitalInfo> hospitalArrayList;
    private ArrayList<ExperienceInfo> experienceArrayList;
    private ArrayList<QualificationInfo> qualificationArrayList;

    // Initialize listview for reviews,hospitals and adapters
    private ListView hospitalListView;
   // private ListView reviewListView;
    private HospitalsAdapter hospitalAdapter;
    private ReviewAdapter reviewAdapter;

    // views for doctor name,image,designation
    private ImageView doctvorImageView;
    private TextView doctorNameTextView;
    private TextView doctorDesignationTextView;
    private TextView doctorFeeTextView;

    // views for ratings fees waittime attitude

    private TextView ratedTextView;
    private TextView ratedTextViewValue;

    private TextView feesTextView;
    private TextView feesTextViewValue;

    private TextView waittimeTextView;
    private TextView waittimeTextViewValue;

    private TextView attitudeTextView;
    private TextView attitudeTextViewValue;

    // custom fonts
    private Typeface tfBold, tfRegular, tfLite;

    // image loader image caching
    private ImageLoader imageLoader;
    DisplayImageOptions options;

    private ScrollView scrollView;

    // layouts for awards membership experience
    private RelativeLayout associatedRelativeLayout;
    private View associatedView;

    private LinearLayout awardsLinearLayout;
    private ImageView backLinearLayout;
    private LinearLayout membershipLinearLayout;
    private LinearLayout awards_layout;
    private LinearLayout member_layout;

    private LinearLayout experienceLinearLayout;
    private LinearLayout experience_layout;

    private LinearLayout qualificationLinearLayout;
    private LinearLayout qualification_layout;

    private ArrayList<String> awardsArrayList;
    private ArrayList<String> memberShipArrayList;

    private RelativeLayout reviewRelativeLayout;
    private View reviewView;

    private TextView readAllTextView;
    private TextView docTypeTextView;
    private TextView doctorExperiance;
    // private TextView viewAllTextView;
    private TextView associatedtext;
    private Boolean isViewAll = false;
    private Boolean isReadAll = false;

    private String doctorName, doctorPic, doctorAddress;
    private String docType;
    private String hospitalLocality;
    private String setHospitalCity;

    private LinearLayout feesLinearLayout;
    private LinearLayout waitTimeLinearLayout;
    private LinearLayout ratedLinearLayout;
    private LinearLayout rateLayoutMain;
    private LinearLayout attitudeLinearLayout;

    private TextView associatedText;
    private TextView reviewText;
    private TextView awardText;
    private TextView expText;
    private TextView memText;
    private TextView qualificationText;

    private TextView memberHospital, memberYear, memberFees, memberType;
    private EasyTracker easyTracker = null;

    private GetDoctorList getDoctorList;
    private boolean isTrue = false;
    private ProgressDialog pdia;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private String callingScreen;
    private boolean isFavSaved = false;
    
    private String preServerTimeSpend="";
    private String serverTimeSpend="";
    private String postServerTimepend="";
    private String totalScreenTimeSpend="";
    private LinearLayout specializationDegreelayout;
    private TextView txtReview;
    private TextView txtRecomended;
    private static DoctorDetailsActivity doctorDetailsScreen;
    private ImageButton btn_call_hospital;
    String hospitalphone = null;
    
    // initialize id's for xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.doctorsdetails);
	
	doctorDetailsScreen = this;
	if (AppConstants.LogLevel == 1) {
	    Utils.startPreServerCallTime = System.currentTimeMillis();
	    Utils.startTotalScreenTime = System.currentTimeMillis();
	}
	easyTracker = EasyTracker.getInstance(DoctorDetailsActivity.this);
	if(getIntent().getStringExtra("CALLING_SCREEN")!=null){
	    callingScreen = getIntent().getStringExtra("CALLING_SCREEN");
	}
	scrollView = (ScrollView) findViewById(R.id.sv);
	
	RegisterActivity.isRegisteredScreen = false;
	associatedText = (TextView) findViewById(R.id.associatedtext);
	reviewText = (TextView) findViewById(R.id.reviewtext);
	reviewText.setOnClickListener(this);
	awardText = (TextView) findViewById(R.id.awardtext);

	expText = (TextView) findViewById(R.id.exptext);
	memText = (TextView) findViewById(R.id.membertext);

	memberHospital = (TextView) findViewById(R.id.member_hospital);
	memberYear = (TextView) findViewById(R.id.member_year);
	memberFees = (TextView) findViewById(R.id.member_fees);
	memberType = (TextView) findViewById(R.id.member_type);

	qualificationText = (TextView) findViewById(R.id.qualificationtext);

	feesLinearLayout = (LinearLayout) findViewById(R.id.fee_layout);
	waitTimeLinearLayout = (LinearLayout) findViewById(R.id.waittime_layout);
	ratedLinearLayout = (LinearLayout) findViewById(R.id.rate_layout);
	rateLayoutMain = (LinearLayout) findViewById(R.id.rateLayoutMain);
	attitudeLinearLayout = (LinearLayout) findViewById(R.id.attitude_layout);
	btn_call_hospital=(ImageButton) findViewById(R.id.btn_call_hospital);

	docType = ApplicationSettings.getPref(AppConstants.DOCTOR_SPECIALITY, "");
	tfBold = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoBold.ttf");
	tfLite = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoLight.ttf");
	tfRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	reviewText.setTypeface(tfRegular);
	associatedText.setTypeface(tfRegular);
	awardText.setTypeface(tfRegular);

	memberHospital.setTypeface(tfRegular);
	memberYear.setTypeface(tfRegular);
	memberFees.setTypeface(tfRegular);
	memberType.setTypeface(tfRegular);

	expText.setTypeface(tfRegular);
	memText.setTypeface(tfRegular);
	qualificationText.setTypeface(tfRegular);

	backLinearLayout = (ImageView) findViewById(R.id.close_doc_detail);
	backLinearLayout.setOnClickListener(this);
	associatedRelativeLayout = (RelativeLayout) findViewById(R.id.associated_layout);
	associatedView = findViewById(R.id.associated_layout_view);
	member_layout = (LinearLayout) findViewById(R.id.member_layout);
	reviewRelativeLayout = (RelativeLayout) findViewById(R.id.review_layout);
	reviewView = findViewById(R.id.review_layout_view);

	readAllTextView = (TextView) findViewById(R.id.readall_tv);
	readAllTextView.setOnClickListener(this);
	readAllTextView.setTypeface(tfRegular);
	associatedtext = (TextView) findViewById(R.id.associatedtext);
	associatedtext.setTypeface(tfRegular);
	associatedtext.setOnClickListener(this);
	awards_layout = (LinearLayout) findViewById(R.id.awards_layout);

	experienceLinearLayout = (LinearLayout) findViewById(R.id.experience_layout);
	experience_layout = (LinearLayout) findViewById(R.id.experience);
	experienceLinearLayout.setVisibility(View.GONE);
	qualificationLinearLayout = (LinearLayout) findViewById(R.id.qualification_layout);
	qualification_layout = (LinearLayout) findViewById(R.id.qualification);

	rateDoctorButton = (TextView) findViewById(R.id.rate_the_doctor_button);
	rateDoctorButton.setTypeface(tfRegular);
	rateDoctorButton.setOnClickListener(this);
	shareButton = (TextView) findViewById(R.id.share_btn);
	shareButton.setTypeface(tfRegular);
	shareButton.setOnClickListener(this);

	favrate = (TextView) findViewById(R.id.favrate);
	favrate.setTypeface(tfRegular);
	favrate.setOnClickListener(this);

	hospitalListView = (ListView) findViewById(R.id.doctor_listview);
	//reviewListView = (ListView) findViewById(R.id.reviewsListview);
	doctvorImageView = (ImageView) findViewById(R.id.iv_doctorimage);
	doctorNameTextView = (TextView) findViewById(R.id.doctorname);
	doctorNameTextView.setTypeface(tfRegular);
	doctorDesignationTextView = (TextView) findViewById(R.id.doctor_designation);
	doctorDesignationTextView.setTypeface(tfRegular);
	awardsLinearLayout = (LinearLayout) findViewById(R.id.awards_recognisation);
	membershipLinearLayout = (LinearLayout) findViewById(R.id.membership);
	ratedTextView = (TextView) findViewById(R.id.rated_text);
	ratedTextView.setTypeface(tfRegular);
	ratedTextViewValue = (TextView) findViewById(R.id.rated_text_value);
	ratedTextViewValue.setTypeface(tfLite);
	docTypeTextView = (TextView) findViewById(R.id.doctor_specialisation);
	docTypeTextView.setTypeface(tfLite);
	docTypeTextView.setVisibility(View.GONE);
	doctorExperiance = (TextView) findViewById(R.id.doctor_experiance);
	doctorExperiance.setTypeface(tfLite);
	doctorExperiance.setVisibility(View.GONE);
	doctorFeeTextView = (TextView) findViewById(R.id.doctor_fee);
	doctorFeeTextView.setTypeface(tfLite);
	doctorFeeTextView.setVisibility(View.GONE);
	
	feesTextView = (TextView) findViewById(R.id.fees_text);
	feesTextView.setTypeface(tfRegular);
	feesTextViewValue = (TextView) findViewById(R.id.fees_text_value);
	feesTextViewValue.setTypeface(tfLite);
	waittimeTextView = (TextView) findViewById(R.id.waittime_text);
	waittimeTextView.setTypeface(tfRegular);
	waittimeTextViewValue = (TextView) findViewById(R.id.waittime_text_value);
	waittimeTextViewValue.setTypeface(tfLite);
	attitudeTextView = (TextView) findViewById(R.id.attitude_text);
	attitudeTextView.setTypeface(tfRegular);
	attitudeTextViewValue = (TextView) findViewById(R.id.attitude_text_value);
	attitudeTextViewValue.setTypeface(tfLite);
	isHomePressed = false;
	isEventPressed = false;
	isFavSaved = false;
	
	specializationDegreelayout = (LinearLayout)findViewById(R.id.specializationDegreelayout);
	txtReview = (TextView)findViewById(R.id.txtReview);
	txtReview.setOnClickListener(this);
	txtRecomended = (TextView)findViewById(R.id.txtRecomended);
	
	initialiseloadImage();
	loadDoctorsInfo();
	touchEvents();
	clickEvent();
	//stopReviewListViewScroll();
    }

    // events called when listview is scrolled
    public void touchEvents() {
	hospitalListView.setOnTouchListener(new ListView.OnTouchListener() {
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		    // Disallow ScrollView to intercept touch events.
		    v.getParent().requestDisallowInterceptTouchEvent(true);
		    break;

		case MotionEvent.ACTION_UP:
		    // Allow ScrollView to intercept touch events.
		    v.getParent().requestDisallowInterceptTouchEvent(false);
		    break;
		}

		// Handle ListView touch events.
		v.onTouchEvent(event);
		return true;
	    }
	});

	/*reviewListView.setOnTouchListener(new ListView.OnTouchListener() {
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		    // Disallow ScrollView to intercept touch events.
		    v.getParent().requestDisallowInterceptTouchEvent(true);
		    break;

		case MotionEvent.ACTION_UP:
		    // Allow ScrollView to intercept touch events.
		    v.getParent().requestDisallowInterceptTouchEvent(false);
		    break;
		}

		// Handle ListView touch events.
		v.onTouchEvent(event);
		return true;
	    }
	});*/

	// touch events inside scrollview
	scrollView.setOnTouchListener(new OnTouchListener() {

	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		    // Disallow ScrollView to intercept touch events.
		    v.getParent().requestDisallowInterceptTouchEvent(false);
		    break;

		case MotionEvent.ACTION_UP:
		    // Allow ScrollView to intercept touch events.
		    v.getParent().requestDisallowInterceptTouchEvent(false);
		    break;
		}

		// Handle ListView touch events.
		v.onTouchEvent(event);
		return true;
	    }
	});
    }

    /* click event for the xml views */
    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.rate_the_doctor_button:

	    easyTracker.send(MapBuilder.createEvent("Doctors Details", "Rate & Review", docType + ":" + doctorName, null).build());

	    if(ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false) && ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0){
		 startActivity(new Intent(DoctorDetailsActivity.this, RateTheDoctorActivity.class).putExtra("doctorname", doctorName)
			    .putExtra("doctorpic", doctorPic).putExtra("doctype", docType));
		
	    }else{
		Intent intent = new Intent(DoctorDetailsActivity.this,RegisterActivity.class);
		intent.putExtra("CALLING_SCREEN", "DoctorDetailsRated");
		intent.putExtra("doctorname", doctorName);
		intent.putExtra("doctorpic", doctorPic);
		intent.putExtra("doctype", docType);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	    }
	    
	    break;
	case R.id.share_btn:

	    easyTracker.send(MapBuilder.createEvent("Doctors Details", "Share", docType + ":" + doctorName, null).build());

	    WhatsAppPost();
	    break;

	case R.id.readall_tv:

	    easyTracker.send(MapBuilder.createEvent("Doctors Details", "View Reviews", docType, null).build());
	    toggleReviews();
	    break;
	case R.id.associatedtext:

	    easyTracker.send(MapBuilder.createEvent("Doctors Details", "Associated Hospitals", "Associated Hospitals", null)
		    .build());
	    // toggleReview();
	    break;
	case R.id.close_doc_detail:
	    easyTracker.send(MapBuilder.createEvent("Doctors Details", "Back", "Back", null).build());
	    backActivity();
	    break;

	case R.id.favrate:
	    
	    if (isFavSaved) {
		easyTracker.send(MapBuilder.createEvent("Doctors Details", "Un Save", docType + ":" + doctorName, null).build());
		if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
			&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
		    new DeleteFavouriteAsyncTask().execute("Doctor", ApplicationSettings.getPref(AppConstants.DOCTOR_ID, ""));
		} else {
		    Intent intent = new Intent(DoctorDetailsActivity.this, RegisterActivity.class);
		    intent.putExtra("CALLING_SCREEN", "DoctorDetails");
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
		}
	    } else {
		easyTracker.send(MapBuilder.createEvent("Doctors Details", "Save", docType + ":" + doctorName, null).build());
		if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
			&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
		    new AddFavouriteAsyncTask().execute("Doctor", ApplicationSettings.getPref(AppConstants.DOCTOR_ID, ""));
		} else {
		    Intent intent = new Intent(DoctorDetailsActivity.this, RegisterActivity.class);
		    intent.putExtra("CALLING_SCREEN", "DoctorDetails");
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
		}
	    }
	    break;
	case R.id.txtReview:
	    scrollView.scrollTo(0, scrollView.getBottom());
	break;
	/*case R.id.reviewtext:
	    scrollView.scrollTo(0, scrollView.getTop());
	    break;*/
	default:
	    break;
	}
    }

    // invoke whatsapp using package name
    public void WhatsAppPost() {

	PackageManager pm = getPackageManager();
	try {

	    Intent waIntent = new Intent(Intent.ACTION_SEND);
	    waIntent.setType("text/plain");
	    String text = "Hey. I found ";
	    if (hospitalLocality != null && hospitalLocality.length() > 1 && !hospitalLocality.equalsIgnoreCase("null") && setHospitalCity != null
		    && setHospitalCity.length() > 1 && !setHospitalCity.equalsIgnoreCase("null")) {
		text = text + doctorName + ", " + docType + ", " + hospitalLocality + ", " + setHospitalCity
			+ " using Medinfi Android Mobile App. You may also try and install it here:" + "\n\n"
			+ "https://play.google.com/store/apps/details?id=com.medinfi";

	    } else {
		text = text + doctorName + ", " + docType + " using Medinfi Android Mobile App. You may also try and install it here:" + "\n\n"
			+ "https://play.google.com/store/apps/details?id=com.medinfi";
	    }

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

    // API call for doctor details and associated views related to doctor
    public void loadDoctorDetails(final String doctorID) {
    }

    public class GetDoctorList extends AsyncTask<Void, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if (AppConstants.LogLevel == 1) {
		Utils.startServerTime = System.currentTimeMillis();
		preServerTimeSpend = Utils.preServerTimeSpend();
	    }
	    pdia = new ProgressDialog(DoctorDetailsActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.screenLoading)));
	    pdia.show();
	}

	@Override
	protected String doInBackground(Void... arg0) {
	    isTrue = true;
	    String result = null;
	    try {
		JSONParser jparser = new JSONParser();
		result = jparser.getDoctorDetails(ApplicationSettings.getPref(AppConstants.DOCTOR_ID, ""));
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
		    doctorArrayList = new ArrayList<DoctorInfo>();
		    JSONArray jsonArray = new JSONArray(result.toString());
		    DoctorInfo doctorInfo = new DoctorInfo();
		    for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject doctorObject = jsonObject.getJSONObject("doctor");
			if (AppConstants.LogLevel == 1) {
			    try {
				ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			}
			if (doctorObject != null) {
			    String docId = doctorObject.getString("id");
			    String fullName = null;
			    String firstName = doctorObject.getString("firstname");
			    String middlename = doctorObject.getString("middlename");

			    String lastname = doctorObject.getString("lastname");

			    String totalExperience = doctorObject.getString("totalExperience").trim();
			    
			    
			    String consultFee = doctorObject.getString("consultation_fee").trim();
			   // String consultFee="";
			    if (consultFee != null && !consultFee.equalsIgnoreCase("null") && !consultFee.equalsIgnoreCase("")) {
					doctorInfo.setDoctorConsultFee(consultFee);
				    }
			    
			    if (totalExperience != null && !totalExperience.equalsIgnoreCase("")) {
				doctorInfo.setDoctorExperience(totalExperience);
			    }

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

			    String qualification = doctorObject.getString("qualification");
			    String doctorType = doctorObject.getString("docType");
			    String doctorAddress = doctorObject.getString("address");
			    String phone = null;
			    String email = null;
			    if (doctorObject.has("phone") && doctorObject.getString("phone") != null
				    && !doctorObject.getString("phone").equalsIgnoreCase("")
				    && !doctorObject.getString("phone").equalsIgnoreCase(null)) {
				phone = doctorObject.getString("phone");
				doctorInfo.setDoctorPhone(phone);

			    }

			    if (doctorObject.has("email") && doctorObject.getString("email") != null
				    && !doctorObject.getString("email").equalsIgnoreCase("")
				    && !doctorObject.getString("email").equalsIgnoreCase(null)) {
				email = doctorObject.getString("email");
				doctorInfo.setDoctorEmail(email);
			    }
			    if (doctorObject.has("isUserFavorite") && doctorObject.getString("isUserFavorite") != null
				    && !doctorObject.getString("isUserFavorite").equalsIgnoreCase("")
				    && !doctorObject.getString("isUserFavorite").equalsIgnoreCase(null)) {
				doctorInfo.setDoctorSaved(doctorObject.getString("isUserFavorite"));
			    }
			    if (doctorObject.has("isRated") && doctorObject.getString("isRated") != null
					&& !doctorObject.getString("isRated").equalsIgnoreCase("")
					&& !doctorObject.getString("isRated").equalsIgnoreCase(null)) {
				doctorInfo.setDoctorRated(doctorObject.getString("isRated"));
				}
			    String pic = null;
			    String awardRecog = null;
			    if (doctorObject.has("pic") && doctorObject.getString("pic") != null
				    && !doctorObject.getString("pic").trim().equalsIgnoreCase("") && !doctorObject.getString("pic").trim().equalsIgnoreCase(null)) {
				pic = doctorObject.getString("pic").trim();
			    }
			    if (doctorObject.has("second_image") && doctorObject.getString("second_image") != null
				    && !doctorObject.getString("second_image").trim().equalsIgnoreCase("") && !doctorObject.getString("second_image").trim().equalsIgnoreCase(null)) {
				doctorInfo.setDoctorSecondImage(doctorObject.getString("second_image").trim());
			    }
			    if (doctorObject.has("awardRecog") && doctorObject.getString("awardRecog") != null
				    && !doctorObject.getString("awardRecog").equalsIgnoreCase("")) {
				awardRecog = doctorObject.getString("awardRecog");
			    }
			    String avgrate = null;
			    if (doctorObject.has("avgrate") && doctorObject.getString("avgrate") != null
				    && !doctorObject.getString("avgrate").equalsIgnoreCase("")) {
				avgrate = doctorObject.getString("avgrate");
			    }
			    String fees = null;
			    if (doctorObject.has("fees") && doctorObject.getString("fees") != null
				    && !doctorObject.getString("fees").equalsIgnoreCase("") && !doctorObject.getString("fees").equalsIgnoreCase("0")) {
				fees = doctorObject.getString("fees");
			    }
			    String waitTime = null;
			    if (doctorObject.has("waitTime") && doctorObject.getString("waitTime") != null
				    && !doctorObject.getString("waitTime").equalsIgnoreCase("")) {
				waitTime = doctorObject.getString("waitTime");
			    }
			    String attitude = null;
			    if (doctorObject.has("attitude") && doctorObject.getString("attitude") != null
				    && !doctorObject.getString("attitude").equalsIgnoreCase("")) {
				attitude = doctorObject.getString("attitude");
			    }
			    String satisfaction = null;
			    if (doctorObject.has("satisfaction") && doctorObject.getString("satisfaction") != null
				    && !doctorObject.getString("satisfaction").equalsIgnoreCase("")) {
				satisfaction = doctorObject.getString("satisfaction");
			    }
			    if (doctorObject.has("specialization_degree") && doctorObject.getString("specialization_degree") != null
				    && !doctorObject.getString("specialization_degree").equalsIgnoreCase("")
				    && !doctorObject.getString("specialization_degree").equalsIgnoreCase(null)) {
				doctorInfo.setDoctorSpecialistDegree(doctorObject.getString("specialization_degree"));
			    }
			    JSONArray awardsArray = null;
			    if (doctorObject.has("awrec") && doctorObject.getString("awrec") != null
				    && !doctorObject.getString("awrec").equalsIgnoreCase("")
				    && !doctorObject.getString("awrec").equalsIgnoreCase(null)) {
				awardsArray = new JSONArray(doctorObject.getString("awrec"));
			    }
			    JSONArray memberShipbArray = null;
			    if (doctorObject.has("membership") && doctorObject.getString("membership") != null
				    && !doctorObject.getString("membership").equalsIgnoreCase("")
				    && !doctorObject.getString("membership").equalsIgnoreCase("")
				    && !doctorObject.getString("membership").equalsIgnoreCase("[]")
				    && !doctorObject.getString("membership").equalsIgnoreCase(null)
				    && !doctorObject.getString("membership").equalsIgnoreCase("null")) {
				memberShipbArray = new JSONArray(doctorObject.getString("membership"));
			    }
			    
			    if (doctorObject.has("recommendCount") && doctorObject.getString("recommendCount") != null
				    && !doctorObject.getString("recommendCount").equalsIgnoreCase("")) {
				doctorInfo.setDoctorRecommendCount(doctorObject.getString("recommendCount"));
			    }
			    
			    if (doctorObject.has("nonRecommendCount") && doctorObject.getString("nonRecommendCount") != null
				    && !doctorObject.getString("nonRecommendCount").equalsIgnoreCase("")) {
				doctorInfo.setDoctorNonRecommendCount(doctorObject.getString("nonRecommendCount"));
			    }
			    
			    awardsArrayList = new ArrayList<String>();
			    memberShipArrayList = new ArrayList<String>();

			    if (awardsArray != null && awardsArray.length() > 0 && !awardsArray.equals("null")) {
				for (int j = 0; j < awardsArray.length(); j++) {
				    if (awardsArray.getString(j) != null && !awardsArray.getString(j).equals("")) {
					awardsArrayList.add(awardsArray.getString(j).trim());
				    }
				}
			    }

			    if (memberShipbArray != null && memberShipbArray.length() > 0 && !memberShipbArray.equals("null")
				    && !memberShipbArray.equals("[]") && !memberShipbArray.equals("")) {
				for (int j = 0; j < memberShipbArray.length(); j++) {
				    if (memberShipbArray.getString(j) != null && !memberShipbArray.getString(j).equals("")
					    && !memberShipbArray.getString(j).equals(null) && !memberShipbArray.getString(j).equals("null")
					    && !memberShipbArray.getString(j).equals("[]")) {

					memberShipArrayList.add(memberShipbArray.getString(j).trim());
				    }
				}
			    }
			    doctorInfo.setAwardsArrayList(awardsArrayList);
			    doctorInfo.setMemberShipArrayList(memberShipArrayList);
			    doctorInfo.setDoctorID(docId);
			    doctorInfo.setDoctorName(fullName.replace("?", ""));
			    doctorInfo.setDoctorQualification(qualification);
			    doctorInfo.setDoctorType(doctorType);
			    doctorInfo.setDoctorAddress(doctorAddress);

			    doctorInfo.setDoctorPic(pic);
			    doctorInfo.setDoctorAwards(awardRecog);
			    doctorInfo.setDoctorAverage(avgrate);

			    doctorInfo.setDoctorAttitude(attitude);
			    doctorInfo.setDoctorFees(fees);
			    doctorInfo.setDoctorWaittime(waitTime);
			    doctorInfo.setDoctorSatisfaction(satisfaction);

			}
			if (jsonObject.has("reviews") && jsonObject.getJSONArray("reviews") != null
				&& !jsonObject.getJSONArray("reviews").equals(null)) {
			    JSONArray reviewArray = jsonObject.getJSONArray("reviews");
			    if (reviewArray != null && reviewArray.length() > 0) {
				reviewArrayList = new ArrayList<ReviewsInfo>();
				for (int j = 0; j < reviewArray.length(); j++) {

				    ReviewsInfo reviewsInfo = new ReviewsInfo();

				    if (reviewArray.getJSONObject(j).has("id") && reviewArray.getJSONObject(j).getString("id") != null) {
					String reviewId = reviewArray.getJSONObject(j).getString("id");
					reviewsInfo.setReviewId(reviewId);

				    }
				    if (reviewArray.getJSONObject(j).has("doctorId") && reviewArray.getJSONObject(j).getString("doctorId") != null) {
					String doctorId = reviewArray.getJSONObject(j).getString("doctorId");
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
				    if (reviewArray.getJSONObject(j).has("waitTime") && reviewArray.getJSONObject(j).getString("waitTime") != null) {
					String waitTime = reviewArray.getJSONObject(j).getString("waitTime");

					reviewsInfo.setWaittime(waitTime);

				    }

				    if (reviewArray.getJSONObject(j).has("attitude") && reviewArray.getJSONObject(j).getString("attitude") != null) {
					String attitude = reviewArray.getJSONObject(j).getString("attitude");
					reviewsInfo.setAttitude(attitude);
				    }
				    if (reviewArray.getJSONObject(j).has("satisfaction")
					    && reviewArray.getJSONObject(j).getString("satisfaction") != null) {
					String satisfaction = reviewArray.getJSONObject(j).getString("satisfaction");
					reviewsInfo.setSatisfaction(satisfaction);

				    }

				    if (reviewArray.getJSONObject(j).has("recommend") && reviewArray.getJSONObject(j).getString("recommend") != null) {
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
			    doctorInfo.setReviewArrayList(reviewArrayList);
			}

			// experience data
			if (jsonObject.has("experience") && jsonObject.getJSONArray("experience") != null
				&& !jsonObject.getJSONArray("experience").equals(null)) {
			    JSONArray expArray = jsonObject.getJSONArray("experience");
			    if (expArray != null && expArray.length() > 0) {
				experienceArrayList = new ArrayList<ExperienceInfo>();
				for (int l = 0; l < expArray.length(); l++) {

				    ExperienceInfo experienceInfo = new ExperienceInfo();

				    if (expArray.getJSONObject(l).has("id") && expArray.getJSONObject(l).getString("id") != null
					    && !expArray.getJSONObject(l).getString("id").equalsIgnoreCase(""))
					experienceInfo.setId(expArray.getJSONObject(l).getString("id"));
				    if (expArray.getJSONObject(l).has("doctorId") && expArray.getJSONObject(l).getString("doctorId") != null
					    && !expArray.getJSONObject(l).getString("doctorId").equalsIgnoreCase(""))
					experienceInfo.setDoctorId(expArray.getJSONObject(l).getString("doctorId"));
				    if (expArray.getJSONObject(l).has("hospitalName") && expArray.getJSONObject(l).getString("hospitalName") != null
					    && !expArray.getJSONObject(l).getString("hospitalName").equalsIgnoreCase(""))
					experienceInfo.setHospitalName(expArray.getJSONObject(l).getString("hospitalName"));
				    if (expArray.getJSONObject(l).has("employmentType")
					    && expArray.getJSONObject(l).getString("employmentType") != null
					    && !expArray.getJSONObject(l).getString("employmentType").equalsIgnoreCase(""))
					experienceInfo.setEmploymentType(expArray.getJSONObject(l).getString("employmentType"));
				    if (expArray.getJSONObject(l).has("fee") && expArray.getJSONObject(l).getString("fee") != null
					    && !expArray.getJSONObject(l).getString("fee").equalsIgnoreCase(""))
					experienceInfo.setFee(expArray.getJSONObject(l).getString("fee"));
				    if (expArray.getJSONObject(l).has("year") && expArray.getJSONObject(l).getString("year") != null
					    && !expArray.getJSONObject(l).getString("year").equalsIgnoreCase(""))
					experienceInfo.setYear(expArray.getJSONObject(l).getString("year"));
				    experienceArrayList.add(experienceInfo);
				}
			    }
			    doctorInfo.setExperienceArrayList(experienceArrayList);
			}
			// End Exp data

			// Qualification
			if (jsonObject.has("qualification") && jsonObject.getJSONArray("qualification") != null
				&& !jsonObject.getJSONArray("qualification").equals(null)) {
			    JSONArray qualificationArray = jsonObject.getJSONArray("qualification");
			    if (qualificationArray != null && qualificationArray.length() > 0) {
				qualificationArrayList = new ArrayList<QualificationInfo>();
				for (int o = 0; o < qualificationArray.length(); o++) {

				    QualificationInfo qualificationInfo = new QualificationInfo();
				    if (qualificationArray.getJSONObject(o).has("id") && qualificationArray.getJSONObject(o).getString("id") != null
					    && !qualificationArray.getJSONObject(o).getString("id").equalsIgnoreCase(""))
					qualificationInfo.setId(qualificationArray.getJSONObject(o).getString("id"));
				    if (qualificationArray.getJSONObject(o).has("doctorId")
					    && qualificationArray.getJSONObject(o).getString("doctorId") != null
					    && !qualificationArray.getJSONObject(o).getString("doctorId").equalsIgnoreCase(""))
					qualificationInfo.setDoctorId(qualificationArray.getJSONObject(o).getString("doctorId"));
				    if (qualificationArray.getJSONObject(o).has("course")
					    && qualificationArray.getJSONObject(o).getString("course") != null
					    && !qualificationArray.getJSONObject(o).getString("course").equalsIgnoreCase(""))
					qualificationInfo.setCourse(qualificationArray.getJSONObject(o).getString("course"));
				    if (qualificationArray.getJSONObject(o).has("college")
					    && qualificationArray.getJSONObject(o).getString("college") != null
					    && !qualificationArray.getJSONObject(o).getString("college").equalsIgnoreCase(""))
					qualificationInfo.setCollege(qualificationArray.getJSONObject(o).getString("college"));

				    qualificationArrayList.add(qualificationInfo);
				}
			    }
			    doctorInfo.setQualificationArrayList(qualificationArrayList);
			}
			// End Qualification

			if (jsonObject.has("hospital")) {
			    JSONArray hospitalArray = jsonObject.getJSONArray("hospital");

			    if (hospitalArray != null && hospitalArray.length() > 0) {
				hospitalArrayList = new ArrayList<HospitalInfo>();
				for (int j = 0; j < hospitalArray.length(); j++) {

				    String hospitalID = hospitalArray.getJSONObject(j).getString("id");
				    String hospitalName = hospitalArray.getJSONObject(j).getString("name").replace("?", "");

				    if (hospitalName.contains(",")) {

					hospitalName = hospitalName.toString().split(",")[0];
				    } else {
					hospitalName = hospitalName.toString();
				    }

				    
				    if (hospitalArray.getJSONObject(j).getString("primaryphone") != null
					    && !hospitalArray.getJSONObject(j).getString("primaryphone").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("primaryphone").equalsIgnoreCase(null)) {
					hospitalphone = hospitalArray.getJSONObject(j).getString("primaryphone");
				    }
				    String hospitalAddress = null;
				    if (hospitalArray.getJSONObject(j).getString("address") != null
					    && !hospitalArray.getJSONObject(j).getString("address").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("address").equalsIgnoreCase(null))
					hospitalAddress = hospitalArray.getJSONObject(j).getString("address").replace("?", "");
				    String hospitalEmail = null;
				    if (hospitalArray.getJSONObject(j).getString("email") != null
					    && !hospitalArray.getJSONObject(j).getString("email").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("email").equalsIgnoreCase(null))
					hospitalEmail = hospitalArray.getJSONObject(j).getString("email");
				    String hospitalSpeciality = hospitalArray.getJSONObject(j).getString("speciality");
				    String hospitalavgrate = null;
				    if (hospitalArray.getJSONObject(j).getString("avgrate") != null
					    && !hospitalArray.getJSONObject(j).getString("avgrate").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("avgrate").equalsIgnoreCase(null))
					hospitalavgrate = hospitalArray.getJSONObject(j).getString("avgrate");

				    if (hospitalArray.getJSONObject(j).getString("locality") != null
					    && !hospitalArray.getJSONObject(j).getString("locality").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("locality").equalsIgnoreCase(null)) {
					hospitalLocality = hospitalArray.getJSONObject(j).getString("locality");

				    }
				    if (hospitalArray.getJSONObject(j).getString("city") != null
					    && !hospitalArray.getJSONObject(j).getString("city").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("city").equalsIgnoreCase(null)) {
					setHospitalCity = hospitalArray.getJSONObject(j).getString("city");

				    }
				    HospitalInfo hospitalInfo = new HospitalInfo();
				    hospitalInfo.setHospitalID(hospitalID);
				    hospitalInfo.setHospitalName(hospitalName);
				    hospitalInfo.setHospitalLocality(hospitalLocality);
				    hospitalInfo.setHospitalCity(setHospitalCity);
				    if (hospitalArray.getJSONObject(j).has("pic") && hospitalArray.getJSONObject(j).getString("pic") != null
					    && !hospitalArray.getJSONObject(j).getString("pic").trim().equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("pic").trim().equalsIgnoreCase("null"))
					hospitalInfo.setHospitalImage(hospitalArray.getJSONObject(j).getString("pic").trim());

				    if (hospitalphone != null && !hospitalphone.equalsIgnoreCase("") && !hospitalphone.equalsIgnoreCase(null))
					hospitalInfo.setHospitalPhoneNumber(hospitalphone);
				    hospitalInfo.setHospitalAddress(hospitalAddress.replace("?", ""));

				    if (hospitalEmail != null && !hospitalEmail.equalsIgnoreCase("") && !hospitalEmail.equalsIgnoreCase(null))
					hospitalInfo.setHospitalEmail(hospitalEmail);

				    if (hospitalSpeciality != null && !hospitalSpeciality.equalsIgnoreCase("")
					    && !hospitalSpeciality.equalsIgnoreCase(null))
					hospitalInfo.setHospitalSpeciality(hospitalSpeciality);
				    if (hospitalavgrate != null && !hospitalavgrate.equalsIgnoreCase("") && !hospitalavgrate.equalsIgnoreCase(null)
					    && !hospitalavgrate.equalsIgnoreCase("null"))
					hospitalInfo.setHospitalRating(hospitalavgrate);
				    
				    if (hospitalArray.getJSONObject(j).getString("lat") != null
					    && !hospitalArray.getJSONObject(j).getString("lat").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("lat").equalsIgnoreCase(null)){
					hospitalInfo.setHospitalLat(hospitalArray.getJSONObject(j).getString("lat"));
				    }
					
				    if (hospitalArray.getJSONObject(j).getString("lon") != null
					    && !hospitalArray.getJSONObject(j).getString("lon").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("lon").equalsIgnoreCase(null)){
					hospitalInfo.setHospitalLong(hospitalArray.getJSONObject(j).getString("lon"));
				    }
				    if (hospitalArray.getJSONObject(j).getString("distance") != null
					    && !hospitalArray.getJSONObject(j).getString("distance").equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("distance").equalsIgnoreCase(null)){
					hospitalInfo.setHospitalDistance(hospitalArray.getJSONObject(j).getString("distance"));
				    }
				    if (hospitalArray.getJSONObject(j).has("emergencyPic") && hospitalArray.getJSONObject(j).getString("emergencyPic") != null
					    && !hospitalArray.getJSONObject(j).getString("emergencyPic").trim().equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("emergencyPic").trim().equalsIgnoreCase("null")){
					hospitalInfo.setHospitalEmergencyPic(hospitalArray.getJSONObject(j).getString("emergencyPic").trim());
				    }
				    if (hospitalArray.getJSONObject(j).has("leftPic") && hospitalArray.getJSONObject(j).getString("leftPic") != null
					    && !hospitalArray.getJSONObject(j).getString("leftPic").trim().equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("leftPic").trim().equalsIgnoreCase("null")){
					hospitalInfo.setHospitalLeftPic(hospitalArray.getJSONObject(j).getString("leftPic").trim());
				    }
				    if (hospitalArray.getJSONObject(j).has("rightPic") && hospitalArray.getJSONObject(j).getString("rightPic") != null
					    && !hospitalArray.getJSONObject(j).getString("rightPic").trim().equalsIgnoreCase("")
					    && !hospitalArray.getJSONObject(j).getString("rightPic").trim().equalsIgnoreCase("null")){
					hospitalInfo.setHospitalRightPic(hospitalArray.getJSONObject(j).getString("rightPic").trim());
				    }
				    
				    hospitalArrayList.add(hospitalInfo);
				}
			    }
			    doctorInfo.setHospitalArrayList(hospitalArrayList);

			}
		    }
		    doctorArrayList.add(doctorInfo);
		    if (doctorArrayList != null && doctorArrayList.size() > 0) {

			for (int i = 0; i < doctorArrayList.size(); i++) {

			    if (doctorArrayList.get(i).getDoctorPic() != null && !doctorArrayList.get(i).getDoctorPic().equalsIgnoreCase("")
				    && !doctorArrayList.get(i).getDoctorPic().equalsIgnoreCase(null)){
			//	dimageLoader.displayImage((doctorArrayList.get(i).getDoctorPic()), doctvorImageView, options);
			    	doctvorImageView.setVisibility(View.VISIBLE);
			    Glide.with(getApplicationContext()).load(doctorArrayList.get(i).getDoctorPic()).placeholder(R.drawable.ic_action_person).transform(new CircleTransform(getApplicationContext())).into(doctvorImageView);

			    }
			    else{
			    	doctvorImageView.setVisibility(View.GONE);
				doctvorImageView.setImageResource(R.drawable.ic_action_person);
			    }
			    
			    doctorName = doctorArrayList.get(i).getDoctorName().replace("?", "");
			    doctorPic = doctorArrayList.get(i).getDoctorPic();
			    // doctorAddress =
			    // doctorArrayList.get(i).getDoctorQualification().replace("?",
			    // "");
			    doctorNameTextView.setText(getResources().getString(R.string.dr) + " " + doctorArrayList.get(i).getDoctorName());

			    String designation = doctorArrayList.get(i).getDoctorQualification().replace("?", "").trim().toString();

			    if (designation != null && !designation.equalsIgnoreCase("") && !designation.equalsIgnoreCase("null")
				    && !designation.equalsIgnoreCase(null)) {
				doctorDesignationTextView.setVisibility(View.VISIBLE);
				doctorDesignationTextView.setText(doctorArrayList.get(i).getDoctorQualification().replace("?", "").trim().toString());
			    } else {
				doctorDesignationTextView.setVisibility(View.GONE);
			    }
			    if(doctorArrayList.get(i).getDoctorType()!=null){
				docTypeTextView.setVisibility(View.VISIBLE);
				docTypeTextView.setText(doctorArrayList.get(i).getDoctorType());
			    }
			    
			    if(doctorArrayList.get(i).getDoctorConsultFee()!=null){
				 doctorFeeTextView.setVisibility(View.VISIBLE);
				 doctorFeeTextView.setText("Fees("+getResources().getString(R.string.rs_symbol)+"): "+doctorArrayList.get(i).getDoctorConsultFee());
				    }
			    
			    String doctExperiance = doctorArrayList.get(i).getDoctorExperience().trim();
			    if(doctExperiance!=null
				    && !doctExperiance.equalsIgnoreCase("")
				    && !doctExperiance.equalsIgnoreCase("null")
				    && !doctExperiance.equalsIgnoreCase(null) 
				    && doctExperiance.equalsIgnoreCase("1")
				    || doctExperiance.equalsIgnoreCase("01")
				    || doctExperiance.equalsIgnoreCase("001")
				    && !doctExperiance.equalsIgnoreCase("0")){
				 doctorExperiance.setVisibility(View.VISIBLE);
				 doctorExperiance.setText(doctExperiance+" "+getString(R.string.doctorExp));
			    }else if(doctExperiance!=null 
				    && !doctExperiance.equalsIgnoreCase("")
				    && !doctExperiance.equalsIgnoreCase("null")
				    && !doctExperiance.equalsIgnoreCase(null) 
				    && !doctExperiance.equalsIgnoreCase("1")
				    && !doctExperiance.equalsIgnoreCase("0") 
				    && !doctExperiance.equalsIgnoreCase("00") 
				    && !doctExperiance.equalsIgnoreCase("000")) {
				boolean isTrue = true;
				try {
				    int iValue = Integer.parseInt(doctExperiance);
				} catch (NumberFormatException e) {
				    isTrue = false;
				}
				if (isTrue) {
				    doctorExperiance.setVisibility(View.VISIBLE);
				    doctorExperiance.setText(doctExperiance + " " + getString(R.string.doctorExps));
				}else{
				    doctorExperiance.setVisibility(View.GONE);
				}

			    }else{
				doctorExperiance.setVisibility(View.GONE);
			    }
			    
			    docType = doctorArrayList.get(i).getDoctorType();
			    doctorAddress = doctorArrayList.get(i).getDoctorAddress();
			    doctorName = doctorNameTextView.getText().toString();
			    if (doctorArrayList.get(i).getDoctorAverage() != null && !doctorArrayList.get(i).getDoctorAverage().equalsIgnoreCase("")
				    && !doctorArrayList.get(i).getDoctorAverage().equalsIgnoreCase("null")
				    && !doctorArrayList.get(i).getDoctorAverage().equalsIgnoreCase(null)
				    && !doctorArrayList.get(i).getDoctorAverage().equalsIgnoreCase("0.0")) {
				long lValue = Math.round(Double.valueOf(doctorArrayList.get(i).getDoctorAverage()));
				((RatingBar)findViewById(R.id.overAllRating)).setRating(lValue);
				((RatingBar)findViewById(R.id.overAllRating)).setIsIndicator(true);
				
				((LinearLayout) findViewById(R.id.rate_layout_top)).setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.plus)).setVisibility(View.VISIBLE);
				((RatingBar) findViewById(R.id.overAllRatingTop)).setRating(lValue);
				((RatingBar) findViewById(R.id.overAllRatingTop)).setIsIndicator(true);
				ratedTextViewValue.setText(Utils.getRatingHints(lValue));
				rateLayoutMain.setVisibility(View.VISIBLE);
				ratedLinearLayout.setVisibility(View.VISIBLE);
			    } else
				ratedLinearLayout.setVisibility(View.GONE);

			    if (doctorArrayList.get(i).getDoctorFees() != null && !doctorArrayList.get(i).getDoctorFees().equalsIgnoreCase("")
				    && !doctorArrayList.get(i).getDoctorFees().equalsIgnoreCase(null)
				    && !doctorArrayList.get(i).getDoctorFees().equalsIgnoreCase("null")) {
				long lValue = Math.round(Double.valueOf(doctorArrayList.get(i).getDoctorFees()));
				((RatingBar)findViewById(R.id.feesRating)).setRating(lValue);
				((RatingBar)findViewById(R.id.feesRating)).setIsIndicator(true);
				feesTextViewValue.setText(Utils.getRatingHints(lValue));
				rateLayoutMain.setVisibility(View.VISIBLE);
				feesLinearLayout.setVisibility(View.VISIBLE);
			    } else
				feesLinearLayout.setVisibility(View.GONE);

			    if (doctorArrayList.get(i).getDoctorWaittime() != null
				    && !doctorArrayList.get(i).getDoctorWaittime().equalsIgnoreCase("")
				    && !doctorArrayList.get(i).getDoctorWaittime().equalsIgnoreCase(null)
				    && !doctorArrayList.get(i).getDoctorWaittime().equalsIgnoreCase("null")) {
				long lValue = Math.round(Double.valueOf(doctorArrayList.get(i).getDoctorWaittime()));
				((RatingBar)findViewById(R.id.waitingRating)).setRating(lValue);
				((RatingBar)findViewById(R.id.waitingRating)).setIsIndicator(true);
				waittimeTextViewValue.setText(Utils.getRatingHints(lValue));
				rateLayoutMain.setVisibility(View.VISIBLE);
				waitTimeLinearLayout.setVisibility(View.VISIBLE);
			    } else
				waitTimeLinearLayout.setVisibility(View.GONE);

			    if (doctorArrayList.get(i).getDoctorAttitude() != null
				    && !doctorArrayList.get(i).getDoctorAttitude().equalsIgnoreCase("")
				    && !doctorArrayList.get(i).getDoctorAttitude().equalsIgnoreCase(null)
				    && !doctorArrayList.get(i).getDoctorAttitude().equalsIgnoreCase("null")) {
				long lValue = Math.round(Double.valueOf(doctorArrayList.get(i).getDoctorAttitude()));
				((RatingBar)findViewById(R.id.attitudeRating)).setRating(lValue);
				((RatingBar)findViewById(R.id.attitudeRating)).setIsIndicator(true);
				attitudeTextViewValue.setText(Utils.getRatingHints(lValue));
				rateLayoutMain.setVisibility(View.VISIBLE);
				attitudeLinearLayout.setVisibility(View.VISIBLE);
			    } else
				attitudeLinearLayout.setVisibility(View.GONE);

			    if (doctorArrayList.get(i).getHospitalArrayList() != null && doctorArrayList.get(i).getHospitalArrayList().size() > 0) {
				hospitalAdapter = new HospitalsAdapter(DoctorDetailsActivity.this, R.layout.doctordetail_hospital_list_item, doctorArrayList
					.get(i).getHospitalArrayList(),"HospitalActivity");
				hospitalListView.setAdapter(hospitalAdapter);
				DynamicListViewHeight
				.setListViewHeightBasedOnChildren(hospitalListView);

				associatedRelativeLayout.setVisibility(View.VISIBLE);
				associatedView.setVisibility(View.VISIBLE);
				hospitalListView.setVisibility(View.VISIBLE);
				
			    } else {
				associatedRelativeLayout.setVisibility(View.GONE);
				associatedView.setVisibility(View.GONE);
				hospitalListView.setVisibility(View.GONE);
			    }

			    if (doctorArrayList.get(i).getReviewArrayList() != null && doctorArrayList.get(i).getReviewArrayList().size() > 0) {
				
				/*reviewAdapter = new ReviewAdapter(DoctorDetailsActivity.this, R.layout.review_list_item, doctorArrayList.get(i)
					.getReviewArrayList());
				reviewListView.setAdapter(reviewAdapter);*/
				
				setReviewsViewDynamically(doctorArrayList.get(i).getReviewArrayList());
				
				reviewRelativeLayout.setVisibility(View.VISIBLE);
				reviewView.setVisibility(View.VISIBLE);
				//reviewListView.setVisibility(View.VISIBLE);
				int iSize = doctorArrayList.get(i).getReviewArrayList().size(); 
				txtReview.setVisibility(View.VISIBLE);
				if(iSize == 1){
				    txtReview.setText(""+iSize+" "+"Review");
				    reviewText.setText(""+iSize+" "+"REVIEW");
				}else{
				    txtReview.setText(""+iSize+" "+getString(R.string.reviewsSmall));
				    reviewText.setText(""+iSize+" "+getString(R.string.review));
				}
				
			    } else {
				new Handler().postDelayed(new Runnable() {

				    @Override
				    public void run() {
					viewAllAssociateHospitals();
				    }
				}, 50);
				txtReview.setVisibility(View.GONE);
				reviewRelativeLayout.setVisibility(View.GONE);
				reviewView.setVisibility(View.GONE);
				//reviewListView.setVisibility(View.GONE);
			    }

			    if(doctorArrayList.get(i).getDoctorRecommendCount() != null && !doctorArrayList.get(i).getDoctorRecommendCount().trim().equalsIgnoreCase("")
					   && !doctorArrayList.get(i).getDoctorRecommendCount().trim().equalsIgnoreCase("null")
				    && !doctorArrayList.get(i).getDoctorRecommendCount().trim().equalsIgnoreCase(null)) {
				String values = doctorArrayList.get(i).getDoctorRecommendCount();

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
			    if(txtRecomended.getVisibility() == View.GONE && txtReview.getVisibility() == View.GONE){
                        	((RelativeLayout)findViewById(R.id.recommendedLayout)).setVisibility(View.GONE);
                            }
			    if (doctorArrayList.get(i).getDoctorSaved() != null && doctorArrayList.get(i).getDoctorSaved().equalsIgnoreCase("1")) {
				//favrate.setClickable(false);
				isFavSaved = true;
				favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_saved, 0, 0);
				favrate.setText(getString(R.string.favrateSaved));
			    } else {
				//favrate.setClickable(true);
				isFavSaved = false;
				favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_save, 0, 0);
				favrate.setText(getString(R.string.favrate));
			    }

			    if(doctorArrayList.get(i).getDoctorRated() != null 
				    && !doctorArrayList.get(i).getDoctorRated().equalsIgnoreCase("")
				    && !doctorArrayList.get(i).getDoctorRated().equalsIgnoreCase("null")
				    && !doctorArrayList.get(i).getDoctorRated().equalsIgnoreCase(null)
				    && doctorArrayList.get(i).getDoctorRated().equalsIgnoreCase("1")) {
				rateDoctorButton.setClickable(false);
				rateDoctorButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_rated, 0, 0);
				rateDoctorButton.setText(getString(R.string.DocHospRated));
			    }else{
				rateDoctorButton.setClickable(true);
				rateDoctorButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_rate, 0, 0);
				rateDoctorButton.setText(getString(R.string.rate));
			    }
			    
			    if (doctorArrayList.get(i).getDoctorSpecialistDegree() != null && !doctorArrayList.get(i).getDoctorSpecialistDegree().trim().equalsIgnoreCase("null")) {
				specializationDegreelayout.setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.specializationDegreeTxt)).setText(doctorArrayList.get(i).getDoctorSpecialistDegree());
			    } else {
				specializationDegreelayout.setVisibility(View.GONE);
			    }
			    
			    if (doctorArrayList.get(i).getAwardsArrayList() != null && doctorArrayList.get(i).getAwardsArrayList().size() > 0) {
				showAwards(doctorArrayList.get(i).getAwardsArrayList());
			    } else {
				awards_layout.setVisibility(View.GONE);
			    }
			    if (doctorArrayList.get(i).getMemberShipArrayList() != null && !doctorArrayList.get(i).getMemberShipArrayList().isEmpty()
				    && !doctorArrayList.get(i).getMemberShipArrayList().equals("")
				    && !doctorArrayList.get(i).getMemberShipArrayList().equals("null")
				    && !doctorArrayList.get(i).getMemberShipArrayList().equals(null)
				    && doctorArrayList.get(i).getMemberShipArrayList().size() > 0) {
				showMemberShip(doctorArrayList.get(i).getMemberShipArrayList());
			    } else {
				membershipLinearLayout.setVisibility(View.GONE);
				member_layout.setVisibility(View.GONE);

			    }

			   /* if (doctorArrayList.get(i).getExperienceArrayList() != null && doctorArrayList.get(i).getExperienceArrayList().size() > 0) {

				showExperience(doctorArrayList.get(i).getExperienceArrayList());
				// showMemberShip(doctorArrayList.get(i).getMemberShipArrayList());
				// member_layout.setVisibility(View.VISIBLE);
			    } else {
				experienceLinearLayout.setVisibility(View.GONE);
				// member_layout.setVisibility(View.GONE);
			    }*/

			    // show doctor qualifications
			    /*
			     * if(doctorArrayList.get(i).
			     * getQualificationArrayList()!=null &&
			     * doctorArrayList
			     * .get(i).getQualificationArrayList().size()>0) {
			     * //vv showQualification(doctorArrayList.get(i
			     * ).getQualificationArrayList());
			     * qualificationLinearLayout
			     * .setVisibility(View.VISIBLE); //showMemberShip
			     * (doctorArrayList.get(i).getMemberShipArrayList
			     * ()); //member_layout.setVisibility(View.VISIBLE);
			     * } else {
			     * 
			     * qualificationLinearLayout.setVisibility(View.GONE
			     * ); //member_layout.setVisibility(View.GONE); }
			     */

			}

			/*
			 * profileAdapter = new ProfileAdapter(
			 * DoctorDetailsActivity.this,
			 * R.layout.profile_list_item, doctorArrayList.);
			 * physiciansListView.setAdapter(profileAdapter);
			 */
		    }
		    if (AppConstants.LogLevel == 1) {
			try {
			    postServerTimepend = Utils.postServerTimeSpend();
			    totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			    Utils.callPerfomanceTestingApi("Doctor Details list", "DoctorDetailsActivity", "DoctorDetail", preServerTimeSpend,
				    serverTimeSpend, postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(DoctorDetailsActivity.this),
				    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""), AppConstants.perfTestCode);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}
		scrollView.scrollTo(0, scrollView.getTop());
		/*new Handler().postDelayed(new Runnable() {

		    @Override
		    public void run() {
			 scrollView.scrollTo(0, scrollView.getTop());
		    }
		}, 50);*/
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    
	}
    }

    /* check network and call doctor details API */
    private void loadDoctorsInfo() {
	try {
	    if (isNetworkAvailable()) {
	        hospitalListView.setVisibility(View.VISIBLE);
	      //  reviewListView.setVisibility(View.VISIBLE);
	        
	        ((RelativeLayout)findViewById(R.id.profiLayout)).setVisibility(View.VISIBLE);
	        ((LinearLayout)findViewById(R.id.rateLayoutMain)).setVisibility(View.VISIBLE);
	        ((LinearLayout)findViewById(R.id.lineLayout)).setVisibility(View.VISIBLE);
	        ((LinearLayout)findViewById(R.id.favRateLayout)).setVisibility(View.VISIBLE);
	      //  ((LinearLayout)findViewById(R.id.lineLayoutBelowFav)).setVisibility(View.VISIBLE);
	        
	        ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	        String doctorID = ApplicationSettings.getPref(AppConstants.DOCTOR_ID, "");
	        if (doctorID != null && !doctorID.equals("")) {
	    	getDoctorList = new GetDoctorList();
	    	getDoctorList.execute();

	        }
	    } else {
	        hospitalListView.setVisibility(View.GONE);
	     //   reviewListView.setVisibility(View.GONE);
	        
	        ((RelativeLayout)findViewById(R.id.profiLayout)).setVisibility(View.GONE);
	        ((LinearLayout)findViewById(R.id.rateLayoutMain)).setVisibility(View.GONE);
	        ((LinearLayout)findViewById(R.id.lineLayout)).setVisibility(View.GONE);
	        ((LinearLayout)findViewById(R.id.favRateLayout)).setVisibility(View.GONE);
	    //    ((LinearLayout)findViewById(R.id.lineLayoutBelowFav)).setVisibility(View.GONE);
	        
	        ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	        ((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tfRegular);
	        ((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tfRegular);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    // check for network connection
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

    // initialize image caching
    public void initialiseloadImage() {
	imageLoader = ImageLoader.getInstance();
	options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.ic_action_person).showImageOnFail(R.drawable.ic_action_person)
		.showImageOnLoading(R.drawable.ic_action_person).build();
    }

    /* method to show Awards based on size we add dynamic views */
    private void showAwards(ArrayList<String> awaArrayList) {

	TextView[] tv = new TextView[awaArrayList.size()];
	TextView[] tv2 = new TextView[awaArrayList.size()];

	View[] view = new View[awaArrayList.size()];
	Boolean isValid = false;
	for (int i = 0; i < awaArrayList.size(); i++) {

	    LinearLayout ll = new LinearLayout(this);
	    tv[i] = new TextView(DoctorDetailsActivity.this);
	    //tv[i].setText("*  : ");
	    tv[i].setPadding(0, 5, 0, 0);
	    tv[i].setTypeface(tfRegular);
	    tv[i].setTextColor(Color.parseColor("#000000"));

	    tv2[i] = new TextView(DoctorDetailsActivity.this);
	    tv2[i].setText(awaArrayList.get(i).toString());
	    tv2[i].setPadding(0, 5, 0, 0);
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
		view[i] = new View(DoctorDetailsActivity.this);
		view[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
		view[i].setBackgroundColor(Color.rgb(51, 51, 51));
		ll9.addView(view[i]);

		awardsLinearLayout.addView(ll);
		//awardsLinearLayout.addView(ll9);
		awardsLinearLayout.setVisibility(View.VISIBLE);
		awards_layout.setVisibility(View.VISIBLE);
	    } else {
		awardsLinearLayout.setVisibility(View.GONE);
		awards_layout.setVisibility(View.GONE);
	    }

	}

    }

    /* method to show MemberShip based on size we add dynamic views */
    private void showMemberShip(ArrayList<String> memberShipArrayList) {

	TextView[] tv = new TextView[memberShipArrayList.size()];
	TextView[] tv2 = new TextView[memberShipArrayList.size()];
	View[] view = new View[memberShipArrayList.size()];
	Boolean isValid = false;
	for (int i = 0; i < memberShipArrayList.size(); i++) {

	    LinearLayout ll = new LinearLayout(this);
	    tv[i] = new TextView(DoctorDetailsActivity.this);
	    //tv[i].setText("*  : ");
	    tv[i].setPadding(0, 5, 0, 0);
	    tv[i].setTextColor(Color.parseColor("#000000"));
	    tv[i].setTypeface(tfRegular);

	    tv2[i] = new TextView(DoctorDetailsActivity.this);
	    tv2[i].setText(memberShipArrayList.get(i).toString());
	    tv2[i].setPadding(0, 5, 0, 0);
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
		view[i] = new View(DoctorDetailsActivity.this);
		view[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
		view[i].setBackgroundColor(Color.rgb(51, 51, 51));
		ll9.addView(view[i]);

		membershipLinearLayout.addView(ll);
		//membershipLinearLayout.addView(ll9);
		membershipLinearLayout.setVisibility(View.VISIBLE);
		member_layout.setVisibility(View.VISIBLE);
	    } else {

		membershipLinearLayout.setVisibility(View.GONE);
		member_layout.setVisibility(View.GONE);
	    }

	}

    }

    /* method to show Experience based on size we add dynamic views */
    private void showExperience(ArrayList<ExperienceInfo> expArrayList) {

	TextView[] tv = new TextView[expArrayList.size()];
	TextView[] tv2 = new TextView[expArrayList.size()];
	TextView[] tv3 = new TextView[expArrayList.size()];
	TextView[] tv4 = new TextView[expArrayList.size()];
	LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	lp1.weight = 1;
	Boolean isValid = false;
	View[] view = new View[expArrayList.size()];
	for (int i = 0; i < expArrayList.size(); i++) {

	    LinearLayout ll = new LinearLayout(this);
	    tv[i] = new TextView(DoctorDetailsActivity.this);
	    if (expArrayList.get(i).getHospitalName() != null)
		tv[i].setText(expArrayList.get(i).getHospitalName());
	    else
		tv[i].setText("");

	    tv[i].setPadding(5, 5, 0, 0);
	    tv[i].setTextColor(Color.parseColor("#000000"));
	    tv[i].setLayoutParams(lp1);
	    tv[i].setTypeface(tfRegular);

	    tv[i].setGravity(Gravity.CENTER_VERTICAL);
	    tv2[i] = new TextView(DoctorDetailsActivity.this);
	    if (expArrayList.get(i).getYear() != null)
		tv2[i].setText(expArrayList.get(i).getYear());
	    else
		tv2[i].setText("");
	    tv2[i].setPadding(5, 5, 0, 0);
	    tv2[i].setTypeface(tfRegular);
	    tv2[i].setTextColor(Color.parseColor("#000000"));
	    tv2[i].setLayoutParams(lp1);
	    tv2[i].setGravity(Gravity.CENTER_VERTICAL);

	    tv3[i] = new TextView(DoctorDetailsActivity.this);
	    if (expArrayList.get(i).getEmploymentType() != null)
		tv3[i].setText(expArrayList.get(i).getEmploymentType());
	    else
		tv3[i].setText("");

	    tv3[i].setTypeface(tfRegular);
	    tv3[i].setPadding(5, 5, 0, 0);
	    tv3[i].setTextColor(Color.parseColor("#000000"));
	    tv3[i].setGravity(Gravity.CENTER_VERTICAL);
	    tv3[i].setLayoutParams(lp1);

	    tv4[i] = new TextView(DoctorDetailsActivity.this);
	    if (expArrayList.get(i).getEmploymentType() != null)
		tv4[i].setText(expArrayList.get(i).getFee());
	    else
		tv4[i].setText("");
	    tv4[i].setTypeface(tfRegular);
	    tv4[i].setPadding(5, 5, 0, 0);
	    tv4[i].setTextColor(Color.parseColor("#000000"));
	    tv4[i].setGravity(Gravity.CENTER_VERTICAL);
	    tv4[i].setLayoutParams(lp1);
	    ll.setOrientation(LinearLayout.HORIZONTAL);
	    ll.setLayoutParams(lp1);
	    ll.setGravity(Gravity.LEFT);
	    ll.setWeightSum(4f);
	    boolean isTrue = true;
	    if (tv[i].getText().toString() != null && !tv[i].getText().toString().equalsIgnoreCase(null)
		    && !tv[i].getText().toString().equalsIgnoreCase("") && !tv[i].getText().toString().equalsIgnoreCase("null")) {
		ll.addView(tv[i]);
		isValid = true;
	    } else {
		isTrue = false;
	    }

	    if (tv2[i].getText().toString() != null && !tv2[i].getText().toString().equalsIgnoreCase(null)
		    && !tv2[i].getText().toString().equalsIgnoreCase("") && !tv2[i].getText().toString().equalsIgnoreCase("null")) {
		ll.addView(tv2[i]);
		isValid = true;
	    } else {
		isTrue = false;
	    }

	    if (tv3[i].getText().toString() != null && !tv3[i].getText().toString().equalsIgnoreCase(null)
		    && !tv3[i].getText().toString().equalsIgnoreCase("") && !tv3[i].getText().toString().equalsIgnoreCase("null")) {
		ll.addView(tv3[i]);
		isValid = true;
	    } else {
		isTrue = false;
	    }

	    if (tv4[i].getText().toString() != null && !tv4[i].getText().toString().equalsIgnoreCase(null)
		    && !tv4[i].getText().toString().equalsIgnoreCase("") && !tv4[i].getText().toString().equalsIgnoreCase("null")) {
		ll.addView(tv4[i]);
		isValid = true;
	    } else {
		isTrue = false;
	    }
	    if (isValid && (!isTrue || isTrue)) {
		LinearLayout ll9 = new LinearLayout(this);
		view[i] = new View(DoctorDetailsActivity.this);
		view[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
		view[i].setBackgroundColor(Color.rgb(51, 51, 51));
		ll9.addView(view[i]);

		experience_layout.addView(ll);
		experience_layout.addView(ll9);
		experience_layout.setVisibility(View.VISIBLE);
		experienceLinearLayout.setVisibility(View.VISIBLE);

	    } else {
		experience_layout.setVisibility(View.GONE);
		experienceLinearLayout.setVisibility(View.GONE);

	    }

	}

    }

    /*
     * method to show qualification based on size we add dynamic views code is
     * present we need to call the method if we want to show qualification
     */
    private void showQualification(ArrayList<QualificationInfo> qualificationArrayList) {

	TextView[] tv = new TextView[qualificationArrayList.size()];
	TextView[] tv2 = new TextView[qualificationArrayList.size()];
	View[] view = new View[qualificationArrayList.size()];
	for (int i = 0; i < qualificationArrayList.size(); i++) {

	    LinearLayout ll = new LinearLayout(this);
	    tv[i] = new TextView(DoctorDetailsActivity.this);
	    if (qualificationArrayList.get(i).getCourse() != null && !qualificationArrayList.get(i).getCourse().equalsIgnoreCase("")
		    && !qualificationArrayList.get(i).getCourse().equalsIgnoreCase(null)
		    && !qualificationArrayList.get(i).getCourse().equalsIgnoreCase("null"))
		tv[i].setText("Graduation Degree : " + qualificationArrayList.get(i).getCourse());
	    else
		tv[i].setVisibility(View.GONE);

	    tv[i].setPadding(5, 5, 0, 0);
	    tv[i].setTypeface(tfRegular);
	    tv[i].setTextColor(Color.parseColor("#000000"));
	    tv[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
	    tv2[i] = new TextView(DoctorDetailsActivity.this);
	    if (qualificationArrayList.get(i).getCollege() != null && !qualificationArrayList.get(i).getCollege().equalsIgnoreCase("")
		    && !qualificationArrayList.get(i).getCollege().equalsIgnoreCase(null)
		    && !qualificationArrayList.get(i).getCollege().equalsIgnoreCase("null"))
		tv2[i].setText("College : " + qualificationArrayList.get(i).getCollege());
	    else
		tv2[i].setVisibility(View.GONE);
	    tv2[i].setPadding(5, 5, 0, 0);
	    tv2[i].setTypeface(tfRegular);
	    tv2[i].setTextColor(Color.parseColor("#000000"));
	    tv2[i].setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));

	    ll.setOrientation(LinearLayout.VERTICAL);
	    ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    ll.setGravity(Gravity.LEFT);
	    ll.addView(tv[i]);
	    ll.addView(tv2[i]);
	    LinearLayout ll9 = new LinearLayout(this);
	    view[i] = new View(DoctorDetailsActivity.this);
	    view[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
	    view[i].setBackgroundColor(Color.rgb(51, 51, 51));
	    ll9.addView(view[i]);

	    qualification_layout.addView(ll);
	    qualification_layout.addView(ll9);
	    qualification_layout.setVisibility(View.VISIBLE);
	}

    }
    
    // toggle method for view ALL for associated hospitals
    public void toggleAssociateHospital() {
	if (!isReadAll) {
	    viewAllAssociateHospitals();
	} else {
	    associatedtext.setTextColor(getResources().getColor(R.color.black_color));
	    isReadAll = false;
	    LinearLayout.LayoutParams lp = (LayoutParams) hospitalListView.getLayoutParams();
	    lp.height = lp.WRAP_CONTENT;
	    hospitalListView.setLayoutParams(lp);

	    hospitalListView.setOnTouchListener(new ListView.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		    int action = event.getAction();
		    switch (action) {
		    case MotionEvent.ACTION_DOWN:
			// Disallow ScrollView to intercept touch events.
			v.getParent().requestDisallowInterceptTouchEvent(true);
			break;

		    case MotionEvent.ACTION_UP:
			// Allow ScrollView to intercept touch events.
			v.getParent().requestDisallowInterceptTouchEvent(true);
			break;
		    }

		    // Handle ListView touch events.
		    v.onTouchEvent(event);
		    return true;
		}
	    });
	}

    }

    private void viewAllAssociateHospitals() {
	isReadAll = true;
	// associatedtext.setTextColor(getResources().getColor(R.color.pink_color));
	Utils.setListViewHeightBasedOnChildren(hospitalListView);
	hospitalListView.setOnTouchListener(new ListView.OnTouchListener() {
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		    // Disallow ScrollView to intercept touch events.
		    v.getParent().requestDisallowInterceptTouchEvent(false);
		    break;

		case MotionEvent.ACTION_UP:
		    // Allow ScrollView to intercept touch events.
		    v.getParent().requestDisallowInterceptTouchEvent(false);
		    break;
		}

		// Handle ListView touch events.
		v.onTouchEvent(event);
		return true;
	    }
	});

    }

    // toggle method for read ALL for Reviews
    public void toggleReviews() {

	if (!isViewAll) {
	    isViewAll = true;
	    //readAllTextView.setTextColor(getResources().getColor(R.color.pink_color));
	  //  Utils.setListViewHeightBasedOnChildren(reviewListView);
	   /* LayoutParams lp = (LayoutParams) reviewListView.getLayoutParams();
	    lp.height = lp.MATCH_PARENT;
	    reviewListView.setLayoutParams(lp);*/
	   /* reviewListView.setOnTouchListener(new ListView.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		    int action = event.getAction();
		    switch (action) {
		    case MotionEvent.ACTION_DOWN:
			// Disallow ScrollView to intercept touch events.
			v.getParent().requestDisallowInterceptTouchEvent(false);
			break;

		    case MotionEvent.ACTION_UP:
			// Allow ScrollView to intercept touch events.
			v.getParent().requestDisallowInterceptTouchEvent(false);
			break;
		    }

		    // Handle ListView touch events.
		    v.onTouchEvent(event);
		    return true;
		}
	    });*/
	} else {
	    readAllTextView.setTextColor(getResources().getColor(R.color.black_color));
	    isViewAll = false;
	   /* LayoutParams lp = (LayoutParams) reviewListView.getLayoutParams();
	    lp.height = lp.MATCH_PARENT;
	    reviewListView.setLayoutParams(lp);*/

	    /*reviewListView.setOnTouchListener(new ListView.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
		    int action = event.getAction();
		    switch (action) {
		    case MotionEvent.ACTION_DOWN:
			// Disallow ScrollView to intercept touch events.
			v.getParent().requestDisallowInterceptTouchEvent(true);
			break;

		    case MotionEvent.ACTION_UP:
			// Allow ScrollView to intercept touch events.
			v.getParent().requestDisallowInterceptTouchEvent(false);
			break;
		    }

		    // Handle ListView touch events.
		    v.onTouchEvent(event);
		    return true;
		}
	    });*/
	}

    }

    // click event on associated hospital click
    public void clickEvent() {
	hospitalListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HospitalInfo entry = (HospitalInfo) parent.getItemAtPosition(position);
		ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getHospitalID());
		// ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY,
		// "HOSPITAL DETAILS");

		easyTracker.send(MapBuilder.createEvent("Doctors Details", "View Hospital", entry.getHospitalName(), null).build());
		isEventPressed = true;
		Intent intent = new Intent(DoctorDetailsActivity.this, HospitalDetailsActivity.class);
		/*if(callingScreen!=null && callingScreen.equalsIgnoreCase("Favourite")){
		    intent.putExtra("CALLING_SCREEN", "Favourite");
		}
		if(callingScreen!=null && callingScreen.equalsIgnoreCase("ContactSyncUp")){
		    intent.putExtra("CALLING_SCREEN", "ContactSyncUp");
		}
		if(callingScreen!=null && callingScreen.equalsIgnoreCase("ReviewsMenuList")){
		    intent.putExtra("CALLING_SCREEN", "ReviewsMenuList");
		}*/
		if(HospitalDetailsActivity.getInstance()!=null){
		    HospitalDetailsActivity.getInstance().finish();
		}
		startActivity(intent);
		//finish();
	    }
	});
	
	btn_call_hospital.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			new calDocAsyncTask().execute("Doctor", ApplicationSettings.getPref(AppConstants.DOCTOR_ID, ""));
			if (hospitalphone != null
				    && !hospitalphone.equalsIgnoreCase("null")
				    && !hospitalphone.equalsIgnoreCase(null)
				    && !hospitalphone.equalsIgnoreCase("6855")) {

				Intent intent = new Intent(Intent.ACTION_DIAL);

				String phoneno = hospitalphone.toString().trim();
				if (phoneno.startsWith("0")) {
				    phoneno = hospitalphone.toString().trim();
				} else if (phoneno.startsWith("91")) {
				    phoneno =hospitalphone.toString().trim();
				} else if (phoneno.startsWith("+91")) {
				    phoneno = hospitalphone.toString().trim();
				} else {
				    phoneno = "0" + hospitalphone.toString().trim();
				}

				easyTracker.send(MapBuilder.createEvent("Doctor List", "Call Doctor", getResources().getString(R.string.dr) + " " +doctorName, null).build());

				intent.setData(Uri.parse("tel:" + phoneno));
				startActivity(intent);
			    } else {
				Toast.makeText(getApplicationContext(), "Phone number does not exist", 1000).show();
			    }
		}
	});
    }

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
	        }});
	   // if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	} catch (Exception e1) {
	    e1.printStackTrace();
	}
	/*new Handler().postDelayed(new Runnable() {

	    @Override
	    public void run() {
		viewAllAssociateHospitals();
	    }
	}, 50);*/
	if(callingScreen!=null && callingScreen.equalsIgnoreCase("Favourite")){
	    //favrate.setClickable(false);
	    if (!isFavSaved) {
		//isFavSaved = true;
		favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_save, 0, 0);
		favrate.setText(getString(R.string.favrate));
	    } else {
		favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_saved, 0, 0);
		favrate.setText(getString(R.string.favrateSaved));
	    }
	    if (ApplicationSettings.getPref(AppConstants.SHOW_THANKS_MESSAGE, false)) {
		Utils.customThankYouDialog(this,getString(R.string.reviewSubmitted),getString(R.string.reviewThankyou));
		rateDoctorButton.setClickable(false);
		rateDoctorButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_rated, 0, 0);
		rateDoctorButton.setText(getString(R.string.DocHospRated));
	    }
	} else {
	    if (ApplicationSettings.getPref(AppConstants.SHOW_THANKS_MESSAGE, false)) {
		Utils.customThankYouDialog(this,getString(R.string.reviewSubmitted),getString(R.string.reviewThankyou));
		rateDoctorButton.setClickable(false);
		rateDoctorButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_rated, 0, 0);
		rateDoctorButton.setText(getString(R.string.DocHospRated));
	    }

	    if (RegisterActivity.isRegisteredScreen) {
		//favrate.setClickable(false);
		isFavSaved = true;
		favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_saved, 0, 0);
		favrate.setText(getString(R.string.favrateSaved));
	    }
	}
	
	try {
	    if (!isEventPressed) {
		if (isHomePressed) {
		    easyTracker.send(MapBuilder.createEvent("Doctors Details", "Device Home", "Device Home", null).build());
		    new CreateSessionIdAsyncTask().execute("0", "");
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
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("DoctorDetails Activity");
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
	isEventPressed = true;

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

	    DoctorDetailsActivity.this.finish();
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
	    easyTracker.send(MapBuilder.createEvent("Doctors Details", "Device Back", "Device Back", null).build());
	   
	    backActivity();

	}

	return super.onKeyDown(keyCode, event);
    }

    private void startFavActivity() {
	if (AppConstants.isSaveUnSaveClicked) {
	    if (FavouriteActivity.getInstance() != null) {
		FavouriteActivity.getInstance().finish();
	    }
	    Intent intent = new Intent(DoctorDetailsActivity.this, FavouriteActivity.class);
	    startActivity(intent);
	    //DoctorDetailsActivity.this.finish();
	}

    }
    private void startContactSyncUpActivity() {
  	if (AppConstants.isSaveUnSaveClicked) {
  	    if (ContactSyncUpActivity.getInstance() != null) {
  		ContactSyncUpActivity.getInstance().finish();
  	    }
  	    Intent intent = new Intent(DoctorDetailsActivity.this, ContactSyncUpActivity.class);
  	    startActivity(intent);
  	   // DoctorDetailsActivity.this.finish();
  	}

      }
    public class AddFavouriteAsyncTask extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pdia = new ProgressDialog(DoctorDetailsActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.favouriteSaving)));
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
			    AppConstants.isSaveUnSaveClicked = true;
			    isFavSaved = true;
			    favrate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_saved, 0, 0);
			    favrate.setText(getString(R.string.favrateSaved));
			} else {
			    Toast.makeText(DoctorDetailsActivity.this,  Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)), Toast.LENGTH_LONG).show();
			}

		    }
		} catch (Exception e) {
		    Toast.makeText(DoctorDetailsActivity.this,  Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)), Toast.LENGTH_LONG).show();
		}
	    } else {
		Toast.makeText(DoctorDetailsActivity.this,  Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)), Toast.LENGTH_LONG).show();
	    }
          if(pdia!=null){
              pdia.dismiss();
          }
	}

    }
    public class DeleteFavouriteAsyncTask extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pdia = new ProgressDialog(DoctorDetailsActivity.this);
	    pdia.setCancelable(true);
	   // pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.favouriteDeleting)));
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
			    Toast.makeText(DoctorDetailsActivity.this,  Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
				    Toast.LENGTH_LONG).show();
			}

		    }
		} catch (Exception e) {
		    Toast.makeText(DoctorDetailsActivity.this,  Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
			    Toast.LENGTH_LONG).show();
		}
	    } else {
		 Toast.makeText(DoctorDetailsActivity.this,  Utils.getCustomeFontStyle(DoctorDetailsActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
			    Toast.LENGTH_LONG).show();
	    }
	    if (pdia != null) {
		pdia.dismiss();
	    }
	}

    }
    
    public class calDocAsyncTask extends AsyncTask<String, Void, String> {

    	@Override
    	protected void onPreExecute() {
    	    super.onPreExecute();
    	}

    	@Override
    	protected String doInBackground(String... params) {
    	    JSONParser jparser = new JSONParser();
    	    String deviceId=Secure.getString(DoctorDetailsActivity.this.getContentResolver(),
                    Secure.ANDROID_ID);
    	    String result = jparser.callDoctor(params[0], params[1],deviceId);

    	    return result;
    	}

    	@Override
    	protected void onPostExecute(String result) {
    	    super.onPostExecute(result);
    	   
    	}

        }
    
    
    public void onImageZoom(View view){
	if (doctorArrayList.get(0).getDoctorPic() != null
		&& !doctorArrayList.get(0).getDoctorPic().trim().equalsIgnoreCase("null")) {
	    new EnlargeImageDialog(this, 0).show();
	}
	
       }
    class EnlargeImageDialog extends Dialog implements android.view.View.OnClickListener {
	Context mContext;
	private int iPosition;
	RelativeLayout layoutSecondImage;
	DisplayImageOptions optionsEnlarg;
	private ArrayList<String> multiplaeImageList = new ArrayList<String>();
	int iCunt;

	ViewPager viewPager;
	MyPagerAdapter myPagerAdapter;
	String[] imageArray;

	public EnlargeImageDialog(Context mContext, int iPosition) {
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

	    if (doctorArrayList.get(iPosition).getDoctorPic() != null) {
		    multiplaeImageList.add(doctorArrayList.get(iPosition).getDoctorPic());
		}
		if (doctorArrayList.get(iPosition).getDoctorSecondImage() != null) {
		    multiplaeImageList.add(doctorArrayList.get(iPosition).getDoctorSecondImage());
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
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
    private void setReviewsViewDynamically(ArrayList<ReviewsInfo> reviewList) {
	try {
	    LinearLayout layoutMain = (LinearLayout) findViewById(R.id.reviewsLayout);
	    LinearLayout reviewsLayout = null;
	    LinearLayout reviewsLayoutDesc = null;
	    LinearLayout reviewsDivider = null;
	    LinearLayout reviewsRating = null;

	    for (int k = 0; k < reviewList.size(); k++) {

		reviewsLayout = new LinearLayout(DoctorDetailsActivity.this);
		reviewsLayoutDesc = new LinearLayout(DoctorDetailsActivity.this);
		reviewsDivider = new LinearLayout(DoctorDetailsActivity.this);
		reviewsRating = new LinearLayout(DoctorDetailsActivity.this);

		View to_add =  getLayoutInflater().inflate(R.layout.ratingbardetail,
				reviewsRating,false);		 
        RatingBar ratingBar=(RatingBar)to_add.findViewById(R.id.ratingBarDetail);
        ratingBar.setRating(Integer.parseInt(reviewList.get(k).getRate()));
        ratingBar.setIsIndicator(true);
		
		TextView txtEmail = new TextView(DoctorDetailsActivity.this);
		txtEmail.setTypeface(tfRegular);
		txtEmail.setText(" "+reviewList.get(k).getUserName()+" ");
	//	txtEmail.setText(reviewList.get(k).getActualUsername());
		txtEmail.setTextColor(getResources().getColor(R.color.screen_header));

		TextView txtCreatedDate = new TextView(DoctorDetailsActivity.this);
		txtCreatedDate.setTypeface(tfLite);
		txtCreatedDate.setText(" - "+reviewList.get(k).getCreatedDate());
		txtCreatedDate.setPadding(5, 0, 5, 0);

		TextView txtReviewDescription = new TextView(DoctorDetailsActivity.this);
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

		//reviewsLayout.addView(to_add);
		reviewsLayout.addView(txtEmail);
		reviewsLayout.addView(txtCreatedDate);
		reviewsRating.addView(to_add);
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
	DoctorDetailsActivity.this.finish();
    }
    private void startReviewsMenuActivity() {
  	if (AppConstants.isSaveUnSaveClicked) {
  	    if (ReviewsActivity.getInstance() != null) {
  		ReviewsActivity.getInstance().finish();
  	    }
  	    Intent intent = new Intent(DoctorDetailsActivity.this, ReviewsActivity.class);
  	    startActivity(intent);
  	    //DoctorDetailsActivity.this.finish();
  	}
      }
    public static DoctorDetailsActivity getInstance() {
   	return doctorDetailsScreen;
       }
}
