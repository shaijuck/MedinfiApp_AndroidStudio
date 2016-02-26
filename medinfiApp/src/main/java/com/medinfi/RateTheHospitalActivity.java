package com.medinfi;

/*Functionality take the ratings like fees waittime overall satisfication message to upload hospital prescription class*/

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.main.AppConstants;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class RateTheHospitalActivity extends SherlockActivity implements OnClickListener {

    private RadioGroup radioGroup;
    private RadioButton no, mayBe, yes;
    private Button nextButton;
    private String fees, waitTime, attitude, overallSatisfaction, recommend;
    private EditText detailsEditText;
    private ImageView doctorImageView;
    private ImageLoader imageLoader;
    DisplayImageOptions options;
    private TextView profileName, profileField;
    private Typeface tf1Regular;
    private TextView recommendText;
    private boolean isFees = false;
    private boolean iswaitTime = false;
    private boolean isattitude = false;
    private boolean isoverall = false;
    private boolean isRecommend = false;
    private EasyTracker easyTracker = null;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private Button feeBtnOne, feeBtnTwo, feeBtnThree, feeBtnFour, feeBtnFive;
    private Button waitTimeoneButton, waitTimetwoButton, waitTimethreeButton, waitTimefourButton, waitTimefiveButton;
    private Button attitudeoneButton, attitudetwoButton, attitudethreeButton, attitudefourButton, attitudefiveButton;
    private Button overalloneButton, overalltwoButton, overallthreeButton, overallfourButton, overallfiveButton;
    private static RateTheHospitalActivity rateTheHospitalScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.rating_layout);
	easyTracker = EasyTracker.getInstance(RateTheHospitalActivity.this);
	rateTheHospitalScreen = this;
	initialize();
	isHomePressed = false;
	isEventPressed = false;
	radioBtnEvents();
	initialiseloadImage();
	getValues();
    }

    /* get doc name from previous screen using intents */
    public void getValues() {
	profileName.setText(getIntent().getExtras().getString("doctorname"));
	profileField.setText(getIntent().getExtras().getString("hospitaladdress"));
	imageLoader.displayImage(getIntent().getExtras().getString("doctorpic"), doctorImageView, options);
    }

    private void initialize() {
	try {
	    tf1Regular = Typeface.createFromAsset(getAssets(), "fonts/RobotoRegular.ttf");
	    detailsEditText = (EditText) findViewById(R.id.more_details_tv);
	    detailsEditText.setTypeface(tf1Regular);
	    radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
	    no = (RadioButton) findViewById(R.id.no_rb);
	    mayBe = (RadioButton) findViewById(R.id.maybe_rb);
	    yes = (RadioButton) findViewById(R.id.yes_rb);
	    // ((TextView)findViewById(R.id.rate_doc_textview)).setText(getString(R.string.rate_the_hospital));
	    feeBtnOne = (Button) findViewById(R.id.feeBtnOne);
	    feeBtnOne.setOnClickListener(this);
	    feeBtnTwo = (Button) findViewById(R.id.feeBtnTwo);
	    feeBtnTwo.setOnClickListener(this);
	    feeBtnThree = (Button) findViewById(R.id.feeBtnThree);
	    feeBtnThree.setOnClickListener(this);
	    feeBtnFour = (Button) findViewById(R.id.feeBtnFour);
	    feeBtnFour.setOnClickListener(this);
	    feeBtnFive = (Button) findViewById(R.id.feeBtnFive);
	    feeBtnFive.setOnClickListener(this);

	    waitTimeoneButton = (Button) findViewById(R.id.WaitBtnOne);
	    waitTimeoneButton.setOnClickListener(this);
	    waitTimetwoButton = (Button) findViewById(R.id.WaitBtnTwo);
	    waitTimetwoButton.setOnClickListener(this);
	    waitTimethreeButton = (Button) findViewById(R.id.WaitBtnThree);
	    waitTimethreeButton.setOnClickListener(this);
	    waitTimefourButton = (Button) findViewById(R.id.WaitBtnFour);
	    waitTimefourButton.setOnClickListener(this);
	    waitTimefiveButton = (Button) findViewById(R.id.WaitBtnFive);
	    waitTimefiveButton.setOnClickListener(this);

	    attitudeoneButton = (Button) findViewById(R.id.AttitudeBtnOne);
	    attitudeoneButton.setOnClickListener(this);
	    attitudetwoButton = (Button) findViewById(R.id.AttitudeBtnTwo);
	    attitudetwoButton.setOnClickListener(this);
	    attitudethreeButton = (Button) findViewById(R.id.AttitudeBtnThree);
	    attitudethreeButton.setOnClickListener(this);
	    attitudefourButton = (Button) findViewById(R.id.AttitudeBtnFour);
	    attitudefourButton.setOnClickListener(this);
	    attitudefiveButton = (Button) findViewById(R.id.AttitudeBtnFive);
	    attitudefiveButton.setOnClickListener(this);

	    overalloneButton = (Button) findViewById(R.id.SatisfactionBtnOne);
	    overalloneButton.setOnClickListener(this);
	    overalltwoButton = (Button) findViewById(R.id.SatisfactionBtnTwo);
	    overalltwoButton.setOnClickListener(this);
	    overallthreeButton = (Button) findViewById(R.id.SatisfactionBtnThree);
	    overallthreeButton.setOnClickListener(this);
	    overallfourButton = (Button) findViewById(R.id.SatisfactionBtnFour);
	    overallfourButton.setOnClickListener(this);
	    overallfiveButton = (Button) findViewById(R.id.SatisfactionBtnFive);
	    overallfiveButton.setOnClickListener(this);

	    recommendText = (TextView) findViewById(R.id.recommendtext);
	    recommendText.setTypeface(tf1Regular);
	    recommendText.setText(getString(R.string.i_recommend_hospital));
	    no.setTypeface(tf1Regular);
	    yes.setTypeface(tf1Regular);
	    mayBe.setTypeface(tf1Regular);

	    doctorImageView = (ImageView) findViewById(R.id.rate_iv_profile);
	    profileName = (TextView) findViewById(R.id.profileName);
	    profileField = (TextView) findViewById(R.id.profileField);

	    nextButton = (Button) findViewById(R.id.next_button);
	    nextButton.setOnClickListener(this);
	    nextButton.setTypeface(tf1Regular);
	} catch (Exception e) {

	}
    }

    /* click events for radio buttons */
    public void radioBtnEvents() {
	radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(RadioGroup group, int checkedId) {
		// find which radio button is selected
		if (checkedId == R.id.no_rb) {
		    recommend = "NO";
		    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Recommend", recommend, null).build());
		    isRecommend = true;
		} else if (checkedId == R.id.maybe_rb) {
		    recommend = "MAY-BE";
		    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Recommend", recommend, null).build());
		    isRecommend = true;
		} else {
		    recommend = "YES";
		    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Recommend", recommend, null).build());
		    isRecommend = true;
		}
	    }

	});
    }

    // image caching
    public void initialiseloadImage() {
	imageLoader = ImageLoader.getInstance();
	options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.hospital_icon_normal).showImageOnFail(R.drawable.hospital_icon_normal)
		.showImageOnLoading(R.drawable.hospital_icon_normal).build();
    }

    /* validation for fees waittime attitude satisfaction */
    private boolean checkValidation() {
	boolean ret = true;

	if (!isFees)
	    ret = false;
	if (!iswaitTime)
	    ret = false;
	if (!isattitude)
	    ret = false;
	if (!isoverall)
	    ret = false;

	if (!isRecommend)
	    ret = false;
	return ret;
    }

    /* click events */
    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {

	case R.id.feeBtnOne:

	    fees = "1";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Fees", fees, null).build());
	    isFees = true;
	    feeBtnOne.setBackgroundResource(R.drawable.star_selected);
	    feeBtnTwo.setBackgroundResource(R.drawable.star_unselected);
	    feeBtnThree.setBackgroundResource(R.drawable.star_unselected);
	    feeBtnFour.setBackgroundResource(R.drawable.star_unselected);
	    feeBtnFive.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.fees)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.fees)).setText("Very high");
	    break;
	case R.id.feeBtnTwo:
	    fees = "2";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Fees", fees, null).build());
	    isFees = true;
	    feeBtnOne.setBackgroundResource(R.drawable.star_selected);
	    feeBtnTwo.setBackgroundResource(R.drawable.star_selected);
	    feeBtnThree.setBackgroundResource(R.drawable.star_unselected);
	    feeBtnFour.setBackgroundResource(R.drawable.star_unselected);
	    feeBtnFive.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.fees)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.fees)).setText("High");
	    break;

	case R.id.feeBtnThree:
	    fees = "3";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Fees", fees, null).build());
	    isFees = true;
	    feeBtnOne.setBackgroundResource(R.drawable.star_selected);
	    feeBtnTwo.setBackgroundResource(R.drawable.star_selected);
	    feeBtnThree.setBackgroundResource(R.drawable.star_selected);
	    feeBtnFour.setBackgroundResource(R.drawable.star_unselected);
	    feeBtnFive.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.fees)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.fees)).setText("Moderate");
	    break;

	case R.id.feeBtnFour:
	    fees = "4";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Fees", fees, null).build());
	    isFees = true;
	    feeBtnOne.setBackgroundResource(R.drawable.star_selected);
	    feeBtnTwo.setBackgroundResource(R.drawable.star_selected);
	    feeBtnThree.setBackgroundResource(R.drawable.star_selected);
	    feeBtnFour.setBackgroundResource(R.drawable.star_selected);
	    feeBtnFive.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.fees)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.fees)).setText("Low");
	    break;

	case R.id.feeBtnFive:
	    isFees = true;
	    fees = "5";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Fees", fees, null).build());
	    feeBtnOne.setBackgroundResource(R.drawable.star_selected);
	    feeBtnTwo.setBackgroundResource(R.drawable.star_selected);
	    feeBtnThree.setBackgroundResource(R.drawable.star_selected);
	    feeBtnFour.setBackgroundResource(R.drawable.star_selected);
	    feeBtnFive.setBackgroundResource(R.drawable.star_selected);
	    ((TextView) findViewById(R.id.fees)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.fees)).setText("Very low");
	    break;

	case R.id.WaitBtnOne:
	    waitTime = "1";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Wait Time", waitTime, null).build());
	    iswaitTime = true;
	    waitTimeoneButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimetwoButton.setBackgroundResource(R.drawable.star_unselected);
	    waitTimethreeButton.setBackgroundResource(R.drawable.star_unselected);
	    waitTimefourButton.setBackgroundResource(R.drawable.star_unselected);
	    waitTimefiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.time)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.time)).setText("Very high");
	    break;
	case R.id.WaitBtnTwo:
	    waitTime = "2";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Wait Time", waitTime, null).build());
	    iswaitTime = true;
	    waitTimetwoButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimeoneButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimethreeButton.setBackgroundResource(R.drawable.star_unselected);
	    waitTimefourButton.setBackgroundResource(R.drawable.star_unselected);
	    waitTimefiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.time)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.time)).setText("High");
	    break;

	case R.id.WaitBtnThree:
	    waitTime = "3";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Wait Time", waitTime, null).build());
	    iswaitTime = true;
	    waitTimetwoButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimeoneButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimethreeButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimefourButton.setBackgroundResource(R.drawable.star_unselected);
	    waitTimefiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.time)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.time)).setText("Moderate");
	    break;

	case R.id.WaitBtnFour:
	    waitTime = "4";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Wait Time", waitTime, null).build());
	    iswaitTime = true;
	    waitTimefourButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimetwoButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimeoneButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimethreeButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimefiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.time)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.time)).setText("Less");
	    break;

	case R.id.WaitBtnFive:
	    waitTime = "5";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Wait Time", waitTime, null).build());
	    iswaitTime = true;
	    waitTimefiveButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimetwoButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimeoneButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimethreeButton.setBackgroundResource(R.drawable.star_selected);
	    waitTimefourButton.setBackgroundResource(R.drawable.star_selected);
	    ((TextView) findViewById(R.id.time)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.time)).setText("Very less");
	    break;
	case R.id.AttitudeBtnOne:
	    isattitude = true;
	    attitude = "1";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Attitude", attitude, null).build());
	    attitudeoneButton.setBackgroundResource(R.drawable.star_selected);
	    attitudetwoButton.setBackgroundResource(R.drawable.star_unselected);
	    attitudethreeButton.setBackgroundResource(R.drawable.star_unselected);
	    attitudefourButton.setBackgroundResource(R.drawable.star_unselected);
	    attitudefiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.attitude)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.attitude)).setText("Terrible");
	    break;
	case R.id.AttitudeBtnTwo:
	    isattitude = true;
	    attitude = "2";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Attitude", attitude, null).build());
	    attitudetwoButton.setBackgroundResource(R.drawable.star_selected);
	    attitudeoneButton.setBackgroundResource(R.drawable.star_selected);
	    attitudethreeButton.setBackgroundResource(R.drawable.star_unselected);
	    attitudefourButton.setBackgroundResource(R.drawable.star_unselected);
	    attitudefiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.attitude)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.attitude)).setText("Poor");
	    break;

	case R.id.AttitudeBtnThree:
	    attitude = "3";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Attitude", attitude, null).build());
	    isattitude = true;
	    attitudetwoButton.setBackgroundResource(R.drawable.star_selected);
	    attitudeoneButton.setBackgroundResource(R.drawable.star_selected);
	    attitudethreeButton.setBackgroundResource(R.drawable.star_selected);
	    attitudefourButton.setBackgroundResource(R.drawable.star_unselected);
	    attitudefiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.attitude)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.attitude)).setText("Average");
	    break;

	case R.id.AttitudeBtnFour:
	    attitude = "4";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Attitude", attitude, null).build());
	    isattitude = true;
	    attitudefourButton.setBackgroundResource(R.drawable.star_selected);
	    attitudetwoButton.setBackgroundResource(R.drawable.star_selected);
	    attitudeoneButton.setBackgroundResource(R.drawable.star_selected);
	    attitudethreeButton.setBackgroundResource(R.drawable.star_selected);
	    attitudefiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.attitude)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.attitude)).setText("Very Good");
	    break;

	case R.id.AttitudeBtnFive:
	    attitude = "5";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Attitude", attitude, null).build());
	    isattitude = true;
	    attitudefiveButton.setBackgroundResource(R.drawable.star_selected);
	    attitudetwoButton.setBackgroundResource(R.drawable.star_selected);
	    attitudeoneButton.setBackgroundResource(R.drawable.star_selected);
	    attitudethreeButton.setBackgroundResource(R.drawable.star_selected);
	    attitudefourButton.setBackgroundResource(R.drawable.star_selected);
	    ((TextView) findViewById(R.id.attitude)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.attitude)).setText("Excellent");
	    break;

	case R.id.SatisfactionBtnOne:
	    overallSatisfaction = "1";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Overall", overallSatisfaction, null).build());
	    isoverall = true;
	    overalloneButton.setBackgroundResource(R.drawable.star_selected);
	    overalltwoButton.setBackgroundResource(R.drawable.star_unselected);
	    overallthreeButton.setBackgroundResource(R.drawable.star_unselected);
	    overallfourButton.setBackgroundResource(R.drawable.star_unselected);
	    overallfiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.satisfaction)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.satisfaction)).setText("Terrible");
	    break;
	case R.id.SatisfactionBtnTwo:
	    isoverall = true;
	    overallSatisfaction = "2";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Overall", overallSatisfaction, null).build());
	    overalltwoButton.setBackgroundResource(R.drawable.star_selected);
	    overalloneButton.setBackgroundResource(R.drawable.star_selected);
	    overallthreeButton.setBackgroundResource(R.drawable.star_unselected);
	    overallfourButton.setBackgroundResource(R.drawable.star_unselected);
	    overallfiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.satisfaction)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.satisfaction)).setText("Poor");
	    break;

	case R.id.SatisfactionBtnThree:
	    isoverall = true;
	    overallSatisfaction = "3";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Overall", overallSatisfaction, null).build());
	    overalltwoButton.setBackgroundResource(R.drawable.star_selected);
	    overalloneButton.setBackgroundResource(R.drawable.star_selected);
	    overallthreeButton.setBackgroundResource(R.drawable.star_selected);
	    overallfourButton.setBackgroundResource(R.drawable.star_unselected);
	    overallfiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.satisfaction)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.satisfaction)).setText("Average");
	    break;

	case R.id.SatisfactionBtnFour:
	    isoverall = true;
	    overallSatisfaction = "4";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Overall", overallSatisfaction, null).build());
	    overallfourButton.setBackgroundResource(R.drawable.star_selected);
	    overalltwoButton.setBackgroundResource(R.drawable.star_selected);
	    overalloneButton.setBackgroundResource(R.drawable.star_selected);
	    overallthreeButton.setBackgroundResource(R.drawable.star_selected);
	    overallfiveButton.setBackgroundResource(R.drawable.star_unselected);
	    ((TextView) findViewById(R.id.satisfaction)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.satisfaction)).setText("Very Good");
	    break;

	case R.id.SatisfactionBtnFive:
	    isoverall = true;
	    overallSatisfaction = "5";
	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Overall", overallSatisfaction, null).build());
	    overallfiveButton.setBackgroundResource(R.drawable.star_selected);
	    overalltwoButton.setBackgroundResource(R.drawable.star_selected);
	    overalloneButton.setBackgroundResource(R.drawable.star_selected);
	    overallthreeButton.setBackgroundResource(R.drawable.star_selected);
	    overallfourButton.setBackgroundResource(R.drawable.star_selected);
	    ((TextView) findViewById(R.id.satisfaction)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.satisfaction)).setText("Excellent");
	    break;
	case R.id.next_button:
	    if (checkValidation()) {

		if (detailsEditText.getText().toString().trim().length() > 0) {
		    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Review", "Review ", null).build());
		} else {
		    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Next", "Next ", null).build());
		}
		startActivity(new Intent(RateTheHospitalActivity.this, UploadHospitalPrescription.class).putExtra("fees", fees)
			.putExtra("waittime", waitTime).putExtra("attitude", attitude).putExtra("overall", overallSatisfaction)
			.putExtra("recommend", recommend).putExtra("details", detailsEditText.getText().toString()));
	    } else {
		if (!isFees) {
		    Toast.makeText(getApplicationContext(), Utils.getCustomeFontStyle(this, getString(R.string.msgRateFeeBlanck)), Toast.LENGTH_SHORT).show();
		} else if (!iswaitTime) {
		    Toast.makeText(getApplicationContext(), Utils.getCustomeFontStyle(this, getString(R.string.msgRateWaitTimeBlanck)), Toast.LENGTH_SHORT).show();
		} else if (!isattitude) {
		    Toast.makeText(getApplicationContext(), Utils.getCustomeFontStyle(this, getString(R.string.msgRateAttitudeBlanck)), Toast.LENGTH_SHORT).show();
		} else if (!isoverall) {
		    Toast.makeText(getApplicationContext(), Utils.getCustomeFontStyle(this, getString(R.string.msgRateOverallBlanck)), Toast.LENGTH_SHORT).show();
		} else if (!isRecommend) {
		    Toast.makeText(getApplicationContext(), Utils.getCustomeFontStyle(this, getString(R.string.msgRateRecommendBlanck)), Toast.LENGTH_SHORT).show();
		}
	    }
	    break;

	default:
	    break;
	}
    }

    public void onCancel(View view) {
	easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Close", "Close", null).build());
	isEventPressed = true;
	RateTheHospitalActivity.this.finish();
    }

    /* check the boolean for close button in upload prescription for Hospitals */
    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	try {
	    if (!isEventPressed) {
		if (isHomePressed) {
		    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Device Home", "Device Home", null).build());
		    
		}
	//	if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
		//}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	Boolean isTrue = ApplicationSettings.getPref(AppConstants.CLOSE_BTN, false);
	if (isTrue) {
	    RateTheHospitalActivity.this.finish();
	}
	
	/*try {
	    final ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
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
	} catch (Exception e1) {
	    e1.printStackTrace();
	}*/
    }

    @Override
    public void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("RateTheHospital Activity");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {

	    easyTracker.send(MapBuilder.createEvent("Rate Hospital", "Device Back", "Device Back", null).build());
	    RateTheHospitalActivity.this.finish();

	}

	return super.onKeyDown(keyCode, event);
    }

    public static RateTheHospitalActivity getInstance() {
	return rateTheHospitalScreen;
    }
    public void onImageZoom(View view){
	if(getIntent().getExtras().getString("doctorpic") != null){
	    enlargImageDialog(0);
	}
       }

    private void enlargImageDialog(int iPosition) {
	final Dialog dialog = new Dialog(this);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	Rect displayRectangle = new Rect();
	Window window = getWindow();
	window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.profile_pic_display, null);
	layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
	// layout.setMinimumHeight((int) (displayRectangle.height() * 0.9f));

	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.hospital_icon_normal).showImageOnFail(R.drawable.hospital_icon_normal).build();

	dialog.setContentView(layout);
	dialog.setCancelable(true);
	dialog.show();
	ImageView imgProfilePicDisplay = (ImageView) dialog.findViewById(R.id.imgProfilePicDisplay);
	if (getIntent().getExtras().getString("doctorpic") != null) {
	    imageLoader.displayImage(getIntent().getExtras().getString("doctorpic"), imgProfilePicDisplay, options, new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);

		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.GONE);
		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.GONE);
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {

		}
	    });
	} else {
	    imgProfilePicDisplay.setImageResource(R.drawable.hospital_icon_normal);
	}

	Button btnCancel = (Button) dialog.findViewById(R.id.close_button_hs);
	btnCancel.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		try {
		    dialog.dismiss();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }
}
