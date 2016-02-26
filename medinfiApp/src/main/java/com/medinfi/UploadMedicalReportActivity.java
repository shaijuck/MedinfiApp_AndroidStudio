package com.medinfi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.SuggestDoctorHospitalActivity.GetCityList;
import com.medinfi.adapters.CitySearchAdapter;
import com.medinfi.datainfo.SearchInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.MedinfiEnums.RelationsType;
import com.medinfi.utils.MedinfiEnums.ReportsType;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.TypefaceSpan;
import com.medinfi.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UploadMedicalReportActivity extends Activity {

    private EasyTracker easyTracker = null;
    private Bitmap firstbitmap,secondBitmap;
    String [] encodedImage = new String[2];
    private Dialog selectImageDialog;
    private File file;
    private String picturePath;
    int rotate = 0;
    private Bitmap firstLargeImageBitmap,secondLargeImageBitmap;
    private ImageView imgFirstMedicalRecord,imgSecondMedicalRecord;
    private Button imgFirstMedicalRecordClose,imgSecondMedicalRecordClose;
    private LinearLayout takePicLayout, reportTypeLayout, infoLayout, feildLayout, momLayout, dadLayout;
    private LinearLayout prescriptionLayout, reportLayout, billsLayout;
    private TextView txtPrescription, txtReport, txtBill;
    private TextView txtMom, txtDad;
    private String relationTypeValues, reportTypeValues, createdDate = "";
    private ListView cityListView;
    private EditText edtCityName, edtHosName, edtDocName, edtSpecialist;
    private TextView txtErrorMessage, txtDateErrorMessage;
    private TextView txtReportDate;

    private int year;
    private int month;
    private int day;
    private static final int DATE_PICKER_ID = 1111;
    private ProgressDialog waitDialog;
    private ArrayList<SearchInfo> citySearchList;
    private CitySearchAdapter citySearchAdapter;
    private int infoLayoutHeight = 0;
    private String searchCityText;
    private boolean isCityListPapulated = false;
    ArrayList<SearchInfo> arrListFilter;
    private JSONObject jsonObject;
    private static UploadMedicalReportActivity uploadMedicalReportScreen;
    private boolean isCitySelected = false;
    private RelativeLayout firstPicLayout,secondPicLayout;
    private int imageSelected = 0;
    private static boolean isEventPressed = false;
    private boolean isHomePressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.upload_medical_report_layout);
	easyTracker = EasyTracker.getInstance(UploadMedicalReportActivity.this);
	initialize();
	setSpecialistVisible(edtDocName);
	cityClickEvent();

	final Calendar c = Calendar.getInstance();
	year = c.get(Calendar.YEAR);
	month = c.get(Calendar.MONTH);
	day = c.get(Calendar.DAY_OF_MONTH);
	uploadMedicalReportScreen = this;
	// getLayoutHeight();
	searchCity(edtCityName);
	isHomePressed = false;
	isEventPressed = false;

    }

    private void initialize() {
	try {
	    Typeface tf2 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoLight.ttf");
	    Typeface fontRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	    imgFirstMedicalRecord = (ImageView) findViewById(R.id.imgFirstMedicalRecord);
	    imgSecondMedicalRecord = (ImageView) findViewById(R.id.imgSecondMedicalRecord);
	    imgFirstMedicalRecordClose = (Button) findViewById(R.id.imgFirstMedicalRecordClose);
	    imgSecondMedicalRecordClose = (Button) findViewById(R.id.imgSecondMedicalRecordClose);
	    takePicLayout = (LinearLayout) findViewById(R.id.takePicLayout);
	    reportTypeLayout = (LinearLayout) findViewById(R.id.reportTypeLayout);
	    infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
	    feildLayout = (LinearLayout) findViewById(R.id.feildLayout);
	    momLayout = (LinearLayout) findViewById(R.id.momLayout);
	    dadLayout = (LinearLayout) findViewById(R.id.dadLayout);
	    txtMom = (TextView) findViewById(R.id.txtMom);
	    txtMom.setTypeface(fontRegular);
	    txtDad = (TextView) findViewById(R.id.txtDad);
	    txtDad.setTypeface(fontRegular);

	    prescriptionLayout = (LinearLayout) findViewById(R.id.prescriptionLayout);
	    reportLayout = (LinearLayout) findViewById(R.id.reportLayout);
	    billsLayout = (LinearLayout) findViewById(R.id.billsLayout);
	    txtPrescription = (TextView) findViewById(R.id.txtPrescription);
	    txtPrescription.setTypeface(fontRegular);
	    txtReport = (TextView) findViewById(R.id.txtReport);
	    txtReport.setTypeface(fontRegular);
	    txtBill = (TextView) findViewById(R.id.txtBill);
	    txtBill.setTypeface(fontRegular);

	    cityListView = (ListView) findViewById(R.id.cityListView);
	    cityListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	    cityListView.setVisibility(View.GONE);

	    edtCityName = (EditText) findViewById(R.id.edtCityName);
	    edtCityName.setTypeface(tf2);

	    // edtCityName.setText(ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION,
	    // ""));

	    edtDocName = (EditText) findViewById(R.id.edtDocName);
	    edtDocName.setTypeface(tf2);
	    edtHosName = (EditText) findViewById(R.id.edtHosName);
	    edtHosName.setTypeface(tf2);
	    edtSpecialist = (EditText) findViewById(R.id.edtSpecialist);
	    edtSpecialist.setTypeface(tf2);
	    txtErrorMessage = (TextView) findViewById(R.id.txtErrorMessage);
	    txtDateErrorMessage = (TextView) findViewById(R.id.txtDateErrorMessage);
	    txtReportDate = (TextView) findViewById(R.id.txtReportDate);
	    txtReportDate.setTypeface(fontRegular);
	    ((Button) findViewById(R.id.submitButton)).setTypeface(fontRegular);

	    ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_FIRST, false);
	    ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_SECOND, false);
	    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
	    
	    firstPicLayout = (RelativeLayout)findViewById(R.id.firstPicLayout);
	    secondPicLayout = (RelativeLayout)findViewById(R.id.secondPicLayout);

	    isCitySelected = false;

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void onMomSelected(View view) {
	Utils.isMedDadSelected = false;
	takePicLayout.setVisibility(View.VISIBLE);
	dadLayout.setBackgroundResource(R.drawable.rectangle_bg);
	txtDad.setTextColor(getResources().getColor(R.color.black));
	momLayout.setBackgroundResource(R.drawable.rectangle_bg_selected);
	txtMom.setTextColor(getResources().getColor(R.color.screen_header));
	relationTypeValues = RelationsType.MOM.toString();
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Relation Type", relationTypeValues, null).build());
    }

    public void onDadSelected(View view) {
	Utils.isMedDadSelected = true;
	takePicLayout.setVisibility(View.VISIBLE);
	momLayout.setBackgroundResource(R.drawable.rectangle_bg);
	txtMom.setTextColor(getResources().getColor(R.color.black));
	dadLayout.setBackgroundResource(R.drawable.rectangle_bg_selected);
	txtDad.setTextColor(getResources().getColor(R.color.screen_header));
	relationTypeValues = RelationsType.DAD.toString();
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Relation Type", relationTypeValues, null).build());
    }

    public void onClickFirstMedicalRecord(View view) {
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Take Photo", "Take Photo1", null).build());
	if (ApplicationSettings.getPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_FIRST, false)) {
	    enlargImageDialog(firstLargeImageBitmap);
	} else {
	    imageSelected = 1;
	    selectImage();
	}
    }
    public void onClickSecondMedicalRecord(View view) {
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Take Photo", "Take Photo2", null).build());
	if (ApplicationSettings.getPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_SECOND, false)) {
	    enlargImageDialog(secondLargeImageBitmap);
	} else {
	    imageSelected = 2;
	    selectImage();
	}
    }

    public void onCloseFirstMedicalRecord(View view) {
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Close Photo", "Close Photo1", null).build());
	ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_FIRST, false);
	imgFirstMedicalRecord.setLayoutParams(new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60,
		getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources()
		.getDisplayMetrics())));
	imgFirstMedicalRecord.setImageResource(R.drawable.ic_action_camera);
	imgFirstMedicalRecordClose.setVisibility(View.GONE);
	infoLayout.setVisibility(View.GONE);
	reportTypeLayout.setVisibility(View.GONE);
	secondPicLayout.setVisibility(View.GONE);
	reportTypeValues = "";
	encodedImage[0] = "";
	closeSecondImageIfFirstClosed();
    }
    public void onCloseSecondMedicalRecord(View view) {
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Close Photo", "Close Photo2", null).build());
	closeSecondImageIfFirstClosed();
    }

    public void onClickPrescriptionReport(View view) {
	infoLayout.setVisibility(View.VISIBLE);
	prescriptionLayout.setBackgroundResource(R.drawable.rectangle_bg_selected);
	txtPrescription.setTextColor(getResources().getColor(R.color.screen_header));
	reportLayout.setBackgroundResource(R.drawable.rectangle_bg);
	txtReport.setTextColor(getResources().getColor(R.color.black));
	billsLayout.setBackgroundResource(R.drawable.rectangle_bg);
	txtBill.setTextColor(getResources().getColor(R.color.black));
	reportTypeValues = ReportsType.PRESCRIPTION.toString();
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Report Type", reportTypeValues, null).build());
	// setInfoFocus();
    }

    private void closeSecondImageIfFirstClosed() {
	ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_SECOND, false);
	imgSecondMedicalRecord.setLayoutParams(new LinearLayout.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60,
		getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources()
		.getDisplayMetrics())));
	imgSecondMedicalRecord.setImageResource(R.drawable.ic_action_camera);
	imgSecondMedicalRecordClose.setVisibility(View.GONE);
	encodedImage[1] = "";
    }
    public void onClickReportType(View view) {
	infoLayout.setVisibility(View.VISIBLE);
	prescriptionLayout.setBackgroundResource(R.drawable.rectangle_bg);
	txtPrescription.setTextColor(getResources().getColor(R.color.black));
	reportLayout.setBackgroundResource(R.drawable.rectangle_bg_selected);
	txtReport.setTextColor(getResources().getColor(R.color.screen_header));
	billsLayout.setBackgroundResource(R.drawable.rectangle_bg);
	txtBill.setTextColor(getResources().getColor(R.color.black));
	reportTypeValues = ReportsType.REPORTS.toString();
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Report Type", reportTypeValues, null).build());
	// setInfoFocus();
    }

    public void onClickBillsType(View view) {
	infoLayout.setVisibility(View.VISIBLE);
	prescriptionLayout.setBackgroundResource(R.drawable.rectangle_bg);
	txtPrescription.setTextColor(getResources().getColor(R.color.black));
	reportLayout.setBackgroundResource(R.drawable.rectangle_bg);
	txtReport.setTextColor(getResources().getColor(R.color.black));
	billsLayout.setBackgroundResource(R.drawable.rectangle_bg_selected);
	txtBill.setTextColor(getResources().getColor(R.color.screen_header));
	reportTypeValues = ReportsType.BILLS.toString();
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Report Type", reportTypeValues, null).build());
	// setInfoFocus();
    }

    /*
     * public void onClickSearchCity(View view) { if
     * (Utils.isNetworkAvailable(UploadMedicalReportActivity.this)) { //
     * ((LinearLayout)
     * findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE); if
     * (!isCityListPapulated) { cityListView.setVisibility(View.VISIBLE);
     * feildLayout.setVisibility(View.GONE); new GetCityList().execute();
     * hideKeyboard();
     * 
     * }
     * 
     * 
     * } else { // ((LinearLayout)
     * findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
     * cityListView.setVisibility(View.GONE);
     * 
     * } }
     */
    public void onSubmit(View view) {
	if (validate()) {
	    setMedicalRecordDetails();

	    Boolean isTrueFirst = ApplicationSettings.getPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_FIRST, false);
	    Boolean isTrueSecond = ApplicationSettings.getPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_SECOND, false);
	    if (isTrueFirst || isTrueSecond) {
		try {
		    if (Utils.isNetworkAvailable(this)) {
			JSONObject jGroup =  new JSONObject();;
			txtErrorMessage.setVisibility(View.GONE);
			for(int i = 0; i < encodedImage.length; i++){
			    if(encodedImage[i]!=null && !encodedImage[i].trim().equalsIgnoreCase("")){
			        jGroup.put(""+i, encodedImage[i]);
			    }
			}
			if(jGroup!=null){
			    jsonObject.putOpt("image", jGroup);
			}
			easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Submit", "City: "+edtCityName.getText().toString().trim()+
				"Hospital: "+edtHosName.getText().toString().trim()+
				"Doctor: "+edtDocName.getText().toString().trim(), null).build());
			PostUploadMedicalRecordsDetails();
		    } else {
			Toast.makeText(UploadMedicalReportActivity.this, "Network not Available", Toast.LENGTH_SHORT).show();
		    }
		} catch (JSONException e) {
		    e.printStackTrace();
		}
	    } else {
		txtErrorMessage.setVisibility(View.VISIBLE);
		txtErrorMessage.setText(getString(R.string.upload_pres_image));
	    }

	}
    }

    public void onCancelScreen(View view) {
	isEventPressed = true;
	easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Close", "Close", null).build());
	UploadMedicalReportActivity.this.finish();
    }

    private void setMedicalRecordDetails() {
	jsonObject = new JSONObject();
	try {
	    jsonObject.put("doctor_name", edtDocName.getText().toString().trim());
	    jsonObject.put("user_id", ApplicationSettings.getPref(AppConstants.USER_ID, ""));
	    jsonObject.put("hospital_name", edtHosName.getText().toString().trim());
	    jsonObject.put("city_name", edtCityName.getText().toString().trim());
	    jsonObject.put("speciality", edtSpecialist.getText().toString().trim());
	    jsonObject.put("relation_type", relationTypeValues);
	    jsonObject.put("record_type", reportTypeValues);
	    jsonObject.put("record_date", createdDate);

	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }

    public void onCreatedDate(View view) {
	showDialog(DATE_PICKER_ID);
    }
    private void editTextFocus() {
	edtCityName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
		    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		    /*
		     * if (searchCity != null &&
		     * searchCity.getText().toString().trim().length() > 1) {
		     * viewVisibility("city"); edtCityName.setText(""); }
		     */
		}
	    }
	});
    }

    private boolean validate() {
	boolean isValid = true;
	try {
	   
	    String strMesg = "Please enter";
	    if (edtCityName.getText().toString().trim().equals("")) {
		strMesg = strMesg + " " + getString(R.string.medicalRecordFieldCityName);
		isValid = false;
	    }
	    if (edtDocName.getText().toString().trim().equals("") && edtHosName.getText().toString().trim().equals("")) {
		if (!isValid) {
		    strMesg = strMesg + ", " + getString(R.string.medicalRecordFieldValidation);
		} else {
		    strMesg = strMesg + " " + getString(R.string.medicalRecordFieldValidation);
		}
		isValid = false;
	    }
	    if (!strMesg.equalsIgnoreCase("Please enter")) {
		txtErrorMessage.setVisibility(View.VISIBLE);
		txtErrorMessage.setText(strMesg + ".");
	    } else {
		txtErrorMessage.setVisibility(View.GONE);
	    }
	    if (createdDate.trim().equals("")) {
		txtDateErrorMessage.setVisibility(View.VISIBLE);
		txtDateErrorMessage.setText(getString(R.string.createdDate));
		isValid = false;
	    }

	} catch (Exception e) {
	    isValid = true;
	}
	return isValid;
    }

    // City click events
    public void cityClickEvent() {
	cityListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		/*
		 * if (AppConstants.LogLevel == 1) { Utils.startServerTime =
		 * System.currentTimeMillis(); }
		 */
		SearchInfo entry = (SearchInfo) parent.getItemAtPosition(position);
		// easyTracker.send(MapBuilder.createEvent("Suggest Doctor/Hospital",
		// "Select City", entry.getSearchName(), null).build());
		isCitySelected = true;
		edtCityName.setVisibility(View.VISIBLE);
		edtCityName.setText(entry.getSearchName());
		cityListView.setVisibility(View.GONE);
		feildLayout.setVisibility(View.VISIBLE);
		clearFeilds();
	    }
	});
    }

    public void setSpecialistVisible(final EditText editsearch) {
	editsearch.addTextChangedListener(new TextWatcher() {
	    @Override
	    public void afterTextChanged(Editable arg0) {
		String searchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		if (searchText != null && searchText.trim().length() > 0) {
		  //  edtDocName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		    edtSpecialist.setVisibility(View.GONE);
		} else {
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
	searchCityText = "";
	edtDocName.setText("");
	edtHosName.setText("");
	edtSpecialist.setText("");
    }

    @Override
    protected void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("UploadMedicalReport Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onResume() {
	super.onResume();
	if (!isEventPressed) {
	    if (isHomePressed) {
		 easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Device Home", "Device Home", null).build());
	    }
	  //  if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
			ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
			
			new CreateSessionIdAsyncTask().execute("0", "");
			
	//	}
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Device Back", "Device Back", null).build());
	    if (cityListView.getVisibility() == View.VISIBLE) {
		cityListView.setVisibility(View.GONE);
		feildLayout.setVisibility(View.VISIBLE);
	    } else {
		isEventPressed = true;
		finish();
	    }
	    // return true;
	}
	return false;

    }

    public void loadBitmap(final String path, final int orientation, final int targetWidth, final int targetHeight,final int imagePositionId) {

	new AsyncTask<Void, Void, Bitmap>() {

	    ProgressDialog dialog;

	    protected void onPreExecute() {
		dialog = new ProgressDialog(UploadMedicalReportActivity.this);
		dialog.setCancelable(false);
		dialog.setMessage(Utils.getCustomeFontStyle(UploadMedicalReportActivity.this, getString(R.string.screenProcessing)));
		dialog.show();
	    };
	    Bitmap bitmap;
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

		    if (imageSelected == 1) {
			bitmap = firstbitmap;
		    } else if (imageSelected == 2) {
			bitmap = secondBitmap;
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
			if (imageSelected == 1) {
			    encodedImage[0] = Utils.convertBase64(bitmap);
			} else if (imageSelected == 2) {
			    encodedImage[1] = Utils.convertBase64(bitmap);
			}

		    }
		} catch (Exception e) {
		    e.printStackTrace();
		} catch (OutOfMemoryError e) {
		    e.printStackTrace();
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

			if (imageSelected == 1) {
			    imgFirstMedicalRecordClose.setVisibility(View.VISIBLE);
			    ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_FIRST, true);
			    firstLargeImageBitmap = result;
			    imgFirstMedicalRecord.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				    LinearLayout.LayoutParams.MATCH_PARENT));
			    imgFirstMedicalRecord.setImageBitmap(firstLargeImageBitmap);
			    reportTypeLayout.setVisibility(View.VISIBLE);
			    secondPicLayout.setVisibility(View.VISIBLE);
			} else if (imageSelected == 2) {
			    imgSecondMedicalRecordClose.setVisibility(View.VISIBLE);
			    ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION_SECOND, true);
			    secondLargeImageBitmap = result;
			    imgSecondMedicalRecord.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				    LinearLayout.LayoutParams.MATCH_PARENT));
			    imgSecondMedicalRecord.setImageBitmap(secondLargeImageBitmap);
			    reportTypeLayout.setVisibility(View.VISIBLE);
			    secondPicLayout.setVisibility(View.VISIBLE);
			}

		    } else {
			// txtErrorMesg.setVisibility(View.VISIBLE);
			// txtErrorMesg.setText(getString(R.string.errorMesg_PrecriptionFormat));
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}catch (OutOfMemoryError e) {
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
		    loadBitmap(file.getAbsolutePath(), rotate, 600, 600,imageSelected);
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

		    loadBitmap(picturePath, rotate, 600, 600,imageSelected);
		} catch (Exception e) {
		    e.printStackTrace();

		}

	    }
	}
    }

    @SuppressLint("NewApi")
    @Override
    protected Dialog onCreateDialog(int id) {
	switch (id) {
	case DATE_PICKER_ID:
	   // return new DatePickerDialog(this, pickerListener, year, month, day);
	    // set date picker as current date
	    //start changes...
	        DatePickerDialog dialog = new DatePickerDialog(this, pickerListener, year, month, day);
	        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
	        return dialog;
        }
        return null;
	
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
	// when dialog box is closed, below method will be called.
	@SuppressLint("NewApi")
	@Override
	public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

	    year = selectedYear;
	    month = selectedMonth+1;
	    day = selectedDay;

	    long dateTime = view.getCalendarView().getDate();
	    Date date = new Date(dateTime);
	    DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
	    String formateDate = dateFormat.format(date);
	    createdDate = year + "-" + month + "-" + day;
	    /*
	     * Date d = new Date(); SimpleDateFormat dateFormatter = new
	     * SimpleDateFormat("d MMM yyyy"); String strDate =
	     * dateFormatter.format(d);
	     */
            if(formateDate!=null && formateDate.trim().length() > 2){
        	txtDateErrorMessage.setVisibility(View.GONE);
            }
	    txtReportDate.setText(getString(R.string.medicalRecordsReportDate) + "\n" + formateDate);
	    easyTracker.send(MapBuilder.createEvent("Add Parent Records", "Report Date", "Report Date", null).build());

	    /*
	     * txtReportDate.setText(Utils.getDate(new
	     * StringBuilder().append(month + 1)
	     * .append(" ").append(day).append(" ").append(year)
	     * .append(" ").toString()));
	     */

	}
    };

    // API Call for city
    final class GetCityList extends AsyncTask<String, Void, String> {
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    /*
	     * if (AppConstants.LogLevel == 1) { Utils.startServerTime =
	     * System.currentTimeMillis(); }
	     */
	    waitDialog = new ProgressDialog(UploadMedicalReportActivity.this);
	    waitDialog.setCancelable(true);
	    waitDialog.setMessage(Utils.getCustomeFontStyle(UploadMedicalReportActivity.this, getString(R.string.screenLoading)));
	    // waitDialog.setOnCancelListener(cancelListener);
	    // waitDialog.show();
	}

	@Override
	protected String doInBackground(String... arg0) {
	    JSONParser jparser = new JSONParser();
	    String result = jparser.getSearchCityList(arg0[0]);

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
		    if (jsonObject.has("Status")) {
			cityListView.setVisibility(View.GONE);
			feildLayout.setVisibility(View.VISIBLE);
		    } else {
			JSONArray jsonarray = new JSONArray(jsonObject.getString("city"));
			for (int i = 0; i < jsonarray.length(); i++) {
			    JSONObject obj = jsonarray.getJSONObject(i);
			    SearchInfo searchInfo = new SearchInfo();
			    searchInfo.setSearchName(obj.getString("name"));
			    searchInfo.setSearchId(obj.getString("id"));
			    citySearchList.add(searchInfo);
			}
		    }
		    if (citySearchList != null && citySearchList.size() > 0) {
			isCityListPapulated = true;
			cityListView.setVisibility(View.VISIBLE);
			setCityAdapter(citySearchList);
			feildLayout.setVisibility(View.GONE);
		    } else {
			cityListView.setVisibility(View.GONE);
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
    public void searchCity(final EditText editsearch) {
	editsearch.addTextChangedListener(new TextWatcher() {
	    @Override
	    public void afterTextChanged(Editable arg0) {
		searchCityText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		if (Utils.isNetworkAvailable(UploadMedicalReportActivity.this)) {
		    if (searchCityText != null && searchCityText.trim().length() == 3) {
			new GetCityList().execute(searchCityText);
		    }
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

    private void searchText(ArrayList<SearchInfo> arrListFilteredItems) {
	try {
	    arrListFilter = new ArrayList<SearchInfo>();
	    if (!searchCityText.equals("")) {
		for (SearchInfo wp : arrListFilteredItems) {
		    if ((wp.getSearchName().toLowerCase(Locale.getDefault()).startsWith(searchCityText))) {
			arrListFilter.add(wp);
		    }
		}
		for (SearchInfo wp : arrListFilteredItems) {
		    if ((wp.getSearchName().toLowerCase(Locale.getDefault()).contains(searchCityText))) {
			arrListFilter.add(wp);
		    }
		}

		Object[] st = arrListFilter.toArray();
		for (Object s : st) {
		    if (arrListFilter.indexOf(s) != arrListFilter.lastIndexOf(s)) {
			arrListFilter.remove(arrListFilter.lastIndexOf(s));
		    }
		}
		setCityAdapter(arrListFilter);
	    } else {
		setCityAdapter(arrListFilteredItems);
	    }
	} catch (Exception e) {
	    e.getStackTrace();
	}
    }

    private void setCityAdapter(ArrayList<SearchInfo> citySearchList) {
	citySearchAdapter = new CitySearchAdapter(UploadMedicalReportActivity.this, R.layout.search_list_item, citySearchList);
	cityListView.setAdapter(citySearchAdapter);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
	outState.putParcelable("photopath", firstLargeImageBitmap);
	outState.putParcelable("photopath", secondLargeImageBitmap);
	super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	if (savedInstanceState != null) {
	    if (savedInstanceState.containsKey("photopath")) {
		firstLargeImageBitmap = savedInstanceState.getParcelable("photopath");
		secondLargeImageBitmap = savedInstanceState.getParcelable("photopath");
	    }
	}
	super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * API call for PostUploadMedicalRecordsDetails
     * 
     **/
    public void PostUploadMedicalRecordsDetails() {
	final class PostData extends AsyncTask<Void, Void, String> {
	    ProgressDialog pdia;

	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();

		pdia = new ProgressDialog(UploadMedicalReportActivity.this);
		pdia.setCancelable(false);
		pdia.setMessage(Utils.getCustomeFontStyle(UploadMedicalReportActivity.this, getString(R.string.favouriteDeleting)));
		if (pdia != null)
		    pdia.show();
	    }

	    @Override
	    protected String doInBackground(Void... arg0) {

		JSONParser jparser = new JSONParser();
		String result = null;
		try {
		    result = jparser.postAddMedicalRecordDetails(jsonObject.toString());
		} catch (JSONException e) {
		    e.printStackTrace();
		}
		return result.toString();
	    }

	    @Override
	    protected void onPostExecute(String result) {
		if (pdia != null && pdia.isShowing())
		    pdia.dismiss();
		if (result != null) {
		    try {
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			if (status.equalsIgnoreCase("Success")) {
			    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, true);
			    if (MedicalReportsActivity.getInstance() != null) {
				MedicalReportsActivity.getInstance().finish();
			    }
			    Intent intent = new Intent(UploadMedicalReportActivity.this,MedicalReportsActivity.class);
			    intent.putExtra("CALLING_SCREEN", "UploadMedicalRecord");
			    startActivity(intent);
			    UploadMedicalReportActivity.this.finish();
			} else {
			}
		    } catch (JSONException e) {
			e.printStackTrace();
		    }
		}

	    }

	}
	new PostData().execute();
    }

    private void enlargImageDialog(Bitmap largeImageBitmap) {
	final Dialog dialog = new Dialog(this);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	Rect displayRectangle = new Rect();
	Window window = getWindow();
	window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.profile_pic_display, null);
	layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
	// layout.setMinimumHeight((int) (displayRectangle.height() * 0.9f));

	dialog.setContentView(layout);
	dialog.setCancelable(true);
	dialog.show();
	ImageView imgProfilePicDisplay = (ImageView) dialog.findViewById(R.id.imgProfilePicDisplay);

	if (largeImageBitmap != null) {
	    imgProfilePicDisplay.setImageBitmap(largeImageBitmap);
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

    public static UploadMedicalReportActivity getInstance() {
	return uploadMedicalReportScreen;
    }
    
}
