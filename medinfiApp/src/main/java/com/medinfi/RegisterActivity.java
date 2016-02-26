package com.medinfi;

import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;

public class RegisterActivity extends SherlockActivity implements OnClickListener {

    /* initialize views from xml */
    public Button saveButton;
    private EditText edtMailId;
    private EditText edtPassword;
    private EditText edtConformPassword;
    private ProgressDialog waitDialog;
    private Typeface tf1, tf2, tf3;
    private TextView txtHeaderDesc;
  //  private TextView checkDoctorsTextView;
    private TextView txtHeader;
    private TextView txtForgotPassword;
    private TextView txtExistingUser;
    private TextView txtSignIn;
    private TextView txtRegDesc;
    private LinearLayout layoutForgotPassword;

    private EasyTracker easyTracker = null;
    private String callingScreen = null;
    private String callingScreenJsonData = null;
    public static boolean isRegisteredScreen = false;
    public boolean isProgeressDialog = false;
    
    private String preServerTimeSpend="";
    private String serverTimeSpend="";
    private String postServerTimepend="";
    private String totalScreenTimeSpend="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setContentView(R.layout.register);
	easyTracker = EasyTracker.getInstance(RegisterActivity.this);
	initialization();
    }

    // check for registration if already registered goto splash
    public void initialization() {

	tf1 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoBold.ttf");
	tf2 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoLight.ttf");
	tf3 = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	txtHeader = (TextView) findViewById(R.id.txtHeader);
	txtHeader.setTypeface(tf3);

	if(getIntent().getStringExtra("CALLING_SCREEN")!=null){
	    callingScreen = getIntent().getStringExtra("CALLING_SCREEN");
	}
	if(getIntent().getStringExtra("JSONDATA")!=null){
	    callingScreenJsonData = getIntent().getStringExtra("JSONDATA");
	}
	isRegisteredScreen = false;
	txtHeaderDesc = (TextView) findViewById(R.id.txtHeaderDesc);
	txtHeaderDesc.setTypeface(tf3);
	// txtHeaderDesc.setPadding(0, Utils.getDipFromPx(this, 30), 0, 0);
	txtRegDesc = (TextView) findViewById(R.id.txtRegDesc);
	txtRegDesc.setTypeface(tf3);
	saveButton = (Button) findViewById(R.id.saveButton);
	edtMailId = (EditText) findViewById(R.id.edtMailId);
	edtMailId.setTypeface(tf2);
	edtPassword = (EditText) findViewById(R.id.edtPassword);
	edtPassword.setTypeface(tf2);
	edtConformPassword = (EditText) findViewById(R.id.edtConformPassword);
	edtConformPassword.setTypeface(tf2);
	//checkDoctorsTextView = (TextView) findViewById(R.id.tv_one);
	//checkDoctorsTextView.setTypeface(tf1);

	txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
	txtForgotPassword.setTypeface(tf2);

	layoutForgotPassword = (LinearLayout) findViewById(R.id.layoutForgotPassword);

	txtExistingUser = (TextView) findViewById(R.id.txtExistingUser);
	txtExistingUser.setTypeface(tf2);
	txtSignIn = (TextView) findViewById(R.id.txtSignIn);
	txtSignIn.setTypeface(tf3);
	saveButton.setTypeface(tf2);
	saveButton.setOnClickListener(this);
	signInView();
	openPrivacyPolicyTermCondotion();
	passWordText(edtPassword);
    }

    private void openPrivacyPolicyTermCondotion() {
	final SpannableString ss = new SpannableString(getString(R.string.msgRegDesc));
	ClickableSpan clickableSpanPrivacy = new ClickableSpan() {
	    @Override
	    public void onClick(View textView) {
		startActivity(new Intent(RegisterActivity.this, LoadUrlActivity.class).putExtra("key", AppConstants.PRIVACY_BASE_URL).putExtra(
			"title", "PP"));
		if(isNewUser){
		    easyTracker.send(MapBuilder.createEvent("Sign Up", "Privacy Policy", "Privacy Policy", null).build());
		}else{
		    easyTracker.send(MapBuilder.createEvent("Sign In", "Privacy Policy", "Privacy Policy", null).build());
		}
	    }
	};
	ClickableSpan clickableSpanTerms = new ClickableSpan() {
	    @Override
	    public void onClick(View textView) {
		startActivity(new Intent(RegisterActivity.this, LoadUrlActivity.class).putExtra("key", AppConstants.TERMS_BASE_URL).putExtra("title",
			"TOU"));
		
		if(isNewUser){
		    easyTracker.send(MapBuilder.createEvent("Sign Up", "Terms of Use", "Terms of Use", null).build());
		}else{
		    easyTracker.send(MapBuilder.createEvent("Sign In", "Terms of Use", "Terms of Use", null).build());
		}
	    }
	};
	ss.setSpan(clickableSpanPrivacy, 102, 116, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	ss.setSpan(clickableSpanTerms, 121, 133, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	txtRegDesc.setText(ss);
	txtRegDesc.setLinkTextColor(getResources().getColor(R.color.screen_header));
	txtRegDesc.setMovementMethod(LinkMovementMethod.getInstance());
    }

    boolean isNewUser = false;

    public void switchUserClickEvent(View view) {
	try {
	    edtMailId.setText("");
	    edtPassword.setText("");
	    edtConformPassword.setText("");
	    txtHeaderDesc.setVisibility(View.GONE);
	    if (!isNewUser) {
		isNewUser = true;
		signUPView();

	    } else {
		isNewUser = false;
		signInView();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
    private void signInView(){
	edtConformPassword.setVisibility(View.GONE);
	layoutForgotPassword.setVisibility(View.VISIBLE);
	txtHeader.setText(getString(R.string.msgSignInCap));
	saveButton.setText(getString(R.string.msgSignInCap));
	txtExistingUser.setText(getString(R.string.msgNewUser));
	txtSignIn.setText(getString(R.string.msgNewUserSignIn));
	edtPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);
	easyTracker.send(MapBuilder.createEvent("Sign Up", "Sign In", "Sign In", null).build());
    }
    private void signUPView(){
	edtConformPassword.setVisibility(View.VISIBLE);
	layoutForgotPassword.setVisibility(View.GONE);
	txtHeader.setText(getString(R.string.create_account));
	saveButton.setText(getString(R.string.create_account));
	txtExistingUser.setText(getString(R.string.msgExistUser));
	txtSignIn.setText(getString(R.string.msgSignIn));
	edtPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
	edtConformPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);
	easyTracker.send(MapBuilder.createEvent("Sign In", "Sign Up", "Sign Up", null).build());
    }
    public void onForgotClickEnent(View view) {
	easyTracker.send(MapBuilder.createEvent("Sign In", "Forgot Password", "email", null).build());
	if (!edtMailId.getText().toString().trim().equals("") && isValidEmail(edtMailId.getText().toString().trim())) {
	    txtHeaderDesc.setVisibility(View.GONE);
	    forgotPassword(edtMailId.getText().toString());
	} else {
	    txtHeaderDesc.setVisibility(View.VISIBLE);
	    txtHeaderDesc.setText(getString(R.string.msgRegMailIDBlanck));
	}

    }

    // check network connection
    private boolean isNetworkAvailable() {
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

    // click events
    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.saveButton:
	    if(isNewUser){
		easyTracker.send(MapBuilder.createEvent("Sign Up", "Sign Up", "email", null).build());
	    }else{
		easyTracker.send(MapBuilder.createEvent("Sign In", "Sign In", "email", null).build());
	    }
	    if (AppConstants.LogLevel == 1) {
		Utils.startTotalScreenTime = System.currentTimeMillis();
	    }
	    submitForm(edtMailId.getText().toString().trim(), edtPassword.getText().toString().trim(), isNewUser);
	    break;

	default:
	    break;
	}

    }

    private boolean validate() {
	boolean isValid = true;
	try {
	    txtHeaderDesc.setVisibility(View.VISIBLE);
	    String strMesg = "";

	    if (edtMailId.getText().toString().trim().equals("")) {
		strMesg = getString(R.string.msgRegMailIDBlanck);
		isValid = false;
	    }
	    if (!edtMailId.getText().toString().trim().equals("") && !isValidEmail(edtMailId.getText().toString().trim())) {
		strMesg = getString(R.string.msgRegMailIDBlanck);
		isValid = false;
	    }
	    if (edtPassword.getText().toString().trim().equals("")) {
		if (!isValid) {
		    strMesg = strMesg + ", " + getString(R.string.msgRegPassBlanck);
		} else {
		    strMesg = strMesg + getString(R.string.msgRegPassFullBlanck);
		}
		isValid = false;
	    }
	    if (isNewUser) {
		if (edtConformPassword.getText().toString().trim().equals("")) {
		    if (!isValid) {
			strMesg = strMesg + ", " + getString(R.string.msgRegConfPassBlanck);
		    } else {
			strMesg = strMesg + getString(R.string.msgRegConfPassFullBlanck);
		    }
		    isValid = false;
		}
		if (!edtConformPassword.getText().toString().trim().equals("")
			&& !(edtPassword.getText().toString().equalsIgnoreCase(edtConformPassword.getText().toString().trim()))) {
		    if (!isValid) {
			strMesg = strMesg + ", " + getString(R.string.msgRegPassMismatchBlanck);
		    } else {
			strMesg = strMesg + getString(R.string.msgRegPassMismatchBlanck);
		    }
		    isValid = false;
		}
	    }
	    if (!strMesg.equalsIgnoreCase("")) {
		txtHeaderDesc.setText(strMesg + ".");
		// txtHeaderDesc.setTextColor(getResources().getColor(R.color.screen_header));
	    } else {
		txtHeaderDesc.setVisibility(View.GONE);
		// txtHeaderDesc.setTextColor(getResources().getColor(R.color.black));
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

    // call api for registration
    public void submitForm(String emailId, String password, boolean isNewUser) {
	if (validate()) {
	    if (isNetworkAvailable()) {

		registeruser(emailId, password, isNewUser);

	    } else {
		Toast.makeText(RegisterActivity.this, Utils.getCustomeFontStyle(RegisterActivity.this, "Network Not Available..."), 1000).show();
	    }

	}
    }
    String userType;
    public void registeruser(final String emailId, final String password, final boolean isNewUser) {
	final class registerNewUser extends AsyncTask<Void, Void, String> {

	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		if (AppConstants.LogLevel == 1) {
		    Utils.startServerTime = System.currentTimeMillis();
		}
		waitDialog = new ProgressDialog(RegisterActivity.this);
		if (isNewUser) {
		    userType = "Register";
		    waitDialog.setMessage(Utils.getCustomeFontStyle(RegisterActivity.this, getString(R.string.msgRegCreatingUser)));
		} else {
		    userType = "SignIn";
		    waitDialog.setMessage(Utils.getCustomeFontStyle(RegisterActivity.this, getString(R.string.msgRegSignInUser)));
		}
		waitDialog.setCancelable(false);
		waitDialog.setOwnerActivity(RegisterActivity.this);
		if (waitDialog != null)
		    waitDialog.show();
	    }

	    @Override
	    protected String doInBackground(Void... arg0) {

		JSONParser jparser = new JSONParser();
		String result = jparser.Register(emailId, password, Utils.getDeviceManufacture(), Utils.getDeviceModel(), Utils.getDeviceApiLevel(),
			Utils.getDeviceISO(RegisterActivity.this), Utils.getDeviceIMEI(RegisterActivity.this), isNewUser);
		if (AppConstants.LogLevel == 1) {
		    serverTimeSpend = Utils.serverTimeSpend();
		    Utils.startPostServerCallTime = System.currentTimeMillis();
		}
		return result;
	    }

	    @Override
	    protected void onPostExecute(String result) {
		if (result != null) {
		    try {
			JSONObject jobj = new JSONObject(result);
			if (jobj != null) {

			    String message = jobj.getString("status");
			    if (AppConstants.LogLevel == 1) {
				try {
				    ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jobj.getString("server_processtime"));
				} catch (Exception e) {
				    e.printStackTrace();
				}
			    }
			   
			    if (message.equalsIgnoreCase("Success")) {
				ApplicationSettings.putPref(AppConstants.REGISTERED_USER, true);
				ApplicationSettings.putPref(AppConstants.USER_ID, jobj.getString("id"));
				ApplicationSettings.putPref(AppConstants.EMAIL_ID, jobj.getString("email"));
				ApplicationSettings.putPref(AppConstants.TOKEN, jobj.getString("token"));

				if (callingScreen!=null && callingScreen.equalsIgnoreCase("SaveMenu")) {
				    startActivity(new Intent(RegisterActivity.this, FavouriteActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				    RegisterActivity.this.finish();
				}
				else if (callingScreen!=null && callingScreen.equalsIgnoreCase(AppConstants.CALLING_SCREEN_SUBMIT_FEEDBACK)) {
				    startActivity(new Intent(RegisterActivity.this, ContactUs.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				    RegisterActivity.this.finish();
				}
				else if (callingScreen!=null && callingScreen.equalsIgnoreCase("DoctorDetailsRated")) {
				    isProgeressDialog = true;
				    Intent intent = new Intent(RegisterActivity.this, RateTheDoctorActivity.class);
				    intent.putExtra("doctorname", getIntent().getStringExtra("doctorname"));
				    intent.putExtra("doctorpic", getIntent().getStringExtra("doctorpic"));
				    intent.putExtra("doctype", getIntent().getStringExtra("doctype"));
				    startActivity(intent);
				    RegisterActivity.this.finish();

				}else if (callingScreen!=null && callingScreen.equalsIgnoreCase("HospitalDetailsRated")) {
				    isProgeressDialog = true;
				    Intent intent = new Intent(RegisterActivity.this, RateTheHospitalActivity.class);
				    intent.putExtra("doctorname", getIntent().getStringExtra("doctorname"));
				    intent.putExtra("doctorpic", getIntent().getStringExtra("doctorpic"));
				    intent.putExtra("hospitaladdress", getIntent().getStringExtra("hospitaladdress"));
				    startActivity(intent);
				    RegisterActivity.this.finish();

				}
				else if (callingScreen!=null && callingScreen.equalsIgnoreCase("HospitalDetails")) {
				    isProgeressDialog = true;
				    new AddFavouriteAsyncTask().execute("Hospital", ApplicationSettings.getPref(AppConstants.HOSPITAL_ID, ""));
				} else if (callingScreen!=null && callingScreen.equalsIgnoreCase("DoctorDetails")) {
				    isProgeressDialog = true;
				    new AddFavouriteAsyncTask().execute("Doctor", ApplicationSettings.getPref(AppConstants.DOCTOR_ID, ""));
				}else if(callingScreen!=null && (callingScreen.equalsIgnoreCase("MenuList")
					||callingScreen.equalsIgnoreCase("SuggestionMenu") || callingScreen.equalsIgnoreCase("FavouriteMenu"))){
				    RegisterActivity.this.finish();
				}else if(callingScreen!=null && callingScreen.equalsIgnoreCase("ReviewsMenu")){
				    startActivity(new Intent(RegisterActivity.this, ReviewsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				    RegisterActivity.this.finish();
				}else if (callingScreen!=null && callingScreen.equalsIgnoreCase("MedicalRecordMenu")) {
				   startActivity(new Intent(RegisterActivity.this, MedicalReportsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				    RegisterActivity.this.finish();
				}else if (callingScreen!=null && callingScreen.equalsIgnoreCase("ProfileActivity")){
					 RegisterActivity.this.finish();
				}
		
			    } else if (message.equalsIgnoreCase("Fail")) {
				txtHeaderDesc.setVisibility(View.VISIBLE);
				String strError = jobj.getString("error_code");
				if (strError.equalsIgnoreCase("ERR_1")) {
				    txtHeaderDesc.setText(getString(R.string.msgReg_EmailAlreadyReg));
				} else if (strError.equalsIgnoreCase("ERR_4")) {
				    txtHeaderDesc.setText(getString(R.string.msgReg_Either_Mail_Pwd));
				}

			    } else {
				txtHeaderDesc.setVisibility(View.VISIBLE);
				txtHeaderDesc.setText(getString(R.string.msgReg_Failed));
				// Toast.makeText(getApplicationContext(),
				// "Registration Failed",
				// Toast.LENGTH_SHORT).show();
			    }
			}

		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }

		} else {
		    txtHeaderDesc.setVisibility(View.VISIBLE);
		    txtHeaderDesc.setText(getString(R.string.msgReg_Failed));
		}
		if(!isProgeressDialog){
		    if (waitDialog != null && waitDialog.isShowing()) {
			    waitDialog.dismiss();
			}
		}
		if (AppConstants.LogLevel == 1) {
		    try {
			postServerTimepend = Utils.postServerTimeSpend();
			totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			Utils.callPerfomanceTestingApi("Registering/sign in user", "RegisterActivity", userType, preServerTimeSpend, serverTimeSpend,
				postServerTimepend, totalScreenTimeSpend, Utils.getNetworkType(RegisterActivity.this),
				ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""), AppConstants.perfTestCode);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }

	}
	new registerNewUser().execute();

    }

    // hide keyboard on tap of screen
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
	boolean ret = false;
	try {
	    View view = getCurrentFocus();
	    ret = super.dispatchTouchEvent(event);
	    if (view instanceof EditText) {
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

    @Override
    public void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	 FlurryAgent.logEvent("Register Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onPause() {
	super.onPause();
    }

    @Override
    public void onStop() {
	super.onStop();
	  FlurryAgent.onEndSession(this);
	EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onResume() {
	super.onResume();
	//if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
		
		ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
		
		new CreateSessionIdAsyncTask().execute("0", "");
		
	//}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    googleAnalyticsForLeavePage();
	    RegisterActivity.this.finish();
	    return true;
	}
	return super.onKeyDown(keyCode, event);
    }

    private void googleAnalyticsForLeavePage() {
	String labal = "";
	if (edtMailId.getText().toString().trim().length() > 0) {
	    labal = labal + "Email Id";
	}
	if (edtPassword.getText().toString().trim().length() > 0) {
	    labal = labal + ": Password";
	}

	easyTracker.send(MapBuilder.createEvent("Registration", "Leave Registration", labal, null).build());
    }

    public void passWordText(final EditText editsearch) {
	// Capture Text in EditText
	editsearch.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		String strSearchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		if (!isNewUser) {
		    if (strSearchText.trim().length() > 0) {
			layoutForgotPassword.setVisibility(View.GONE);
		    } else {
			layoutForgotPassword.setVisibility(View.VISIBLE);
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

    public void forgotPassword(final String emailId) {
	final class registerNewUser extends AsyncTask<Void, Void, String> {

	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();

		waitDialog = new ProgressDialog(RegisterActivity.this);
		waitDialog.setMessage(Utils.getCustomeFontStyle(RegisterActivity.this, getString(R.string.msgResetingUser)));
		waitDialog.setCancelable(false);
		waitDialog.setOwnerActivity(RegisterActivity.this);
		if (waitDialog != null)
		    waitDialog.show();
	    }

	    @Override
	    protected String doInBackground(Void... arg0) {

		JSONParser jparser = new JSONParser();
		String result = jparser.forgotPassword(emailId);
		return result;
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
			    if (message.equalsIgnoreCase("Success")) {
				forgotCustomDialog(getString(R.string.msgReg_ResetPwd), getString(R.string.msgReg_ResetPwdDecs), emailId,
					getString(R.string.msgReg_ResetPwdDecs1));

			    } else if (message.equalsIgnoreCase("Fail")) {
				String strError = jobj.getString("error_code");
				if (strError.equalsIgnoreCase("ERR_3")) {
				    forgotCustomDialog(getString(R.string.msgTitlRegInvalidPwd), emailId, getString(R.string.msgReg_NoRecordFound),
					    "");
				} else if (strError.equalsIgnoreCase("ERR_4")) {
				    forgotCustomDialog(getString(R.string.msgTitlRegInvalidPwd), emailId, getString(R.string.msgReg_NoRecordFound),
					    "");
				}

			    } else {
				txtHeaderDesc.setText(getString(R.string.msgReg_Failed));
			    }
			}

		    } catch (JSONException e) {
			e.printStackTrace();
		    }

		}
	    }

	}
	new registerNewUser().execute();

    }

    /**
     * Create custom dialog
     */
    private void forgotCustomDialog(String titileMesg, String mesg, String mesgEmail, String mesgDesc) {
	final Dialog dialog = new Dialog(this);
	Typeface tfRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

	Rect displayRectangle = new Rect();
	Window window = ((Activity) this).getWindow();
	window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.forgot_alert_dialog, null);
	layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
	// layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

	dialog.setContentView(layout);
	// dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
	// LayoutParams.WRAP_CONTENT);
	dialog.setCancelable(false);
	dialog.show();
	TextView textDialogMsg = (TextView) dialog.findViewById(R.id.textDialogMsg);
	TextView textDialog = (TextView) dialog.findViewById(R.id.textDialog);
	textDialog.setText(titileMesg);
	textDialogMsg.setText(mesg + " " + mesgEmail + " " + mesgDesc);
	Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
	btnOk.setTypeface(tfRegular);
	((TextView) dialog.findViewById(R.id.textDialog)).setTypeface(tfRegular);
	btnOk.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		dialog.dismiss();

	    }
	});

    }

    public class AddFavouriteAsyncTask extends AsyncTask<String, Void, String> {
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();

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

	    if (waitDialog != null && waitDialog.isShowing()) {
		isProgeressDialog = false;
		waitDialog.dismiss();
	    }
	    if (result != null) {
		try {
		    JSONObject jobj = new JSONObject(result);
		    if (jobj != null) {
			String message = jobj.getString("status");
			if (message.equalsIgnoreCase("Success")) {
			    isRegisteredScreen = true;
			} else {
			    isRegisteredScreen = false;
			}
			RegisterActivity.this.finish();
		    }
		} catch (Exception e) {
		    isRegisteredScreen = false;
		    RegisterActivity.this.finish();
		    e.printStackTrace();
		}
	    } else {
		isRegisteredScreen = false;
		RegisterActivity.this.finish();
	    }

	}

    }
    /*API call for PostUploadPrescriptionDetails*/ 
    public void PostUploadPrescriptionDetails(final JSONObject jsonObject) {
	final class PostData extends AsyncTask<Void, Void, String> {
	    // ProgressDialog pdia;
	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		/*
		 * pdia = new ProgressDialog(UploadPrescriptionActivity.this);
		 * pdia.setCancelable(false); pdia.setMessage("Loading...");
		 * if(pdia!=null) pdia.show();
		 */
	    }
	    @Override
	    protected String doInBackground(Void... arg0) {
		JSONParser jparser = new JSONParser();
		String result = null;
		try {
		    result = jparser.postDoctorDetails(jsonObject.toString());
		} catch (JSONException e) {
		    e.printStackTrace();
		}
		return result.toString();
	    }
	    @Override
	    protected void onPostExecute(String result) {
		/*
		 * if(pdia!=null && pdia.isShowing()) pdia.dismiss();
		 */
		if (result != null) {
		    try {
			if (waitDialog != null && waitDialog.isShowing()) {
			    waitDialog.dismiss();
			}
		    } catch (Exception e1) {
			e1.printStackTrace();
		    }
		    try {
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			if (status.equalsIgnoreCase("Success")) {
			    /*
			     * ApplicationSettings.putPref(AppConstants.
			     * SHOW_THANKS_MESSAGE, true); startActivity(new
			     * Intent
			     * (UploadPrescriptionActivity.this,HomeScreen.
			     * class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			     */
			} else {
			    /*
			     * ApplicationSettings.putPref(AppConstants.
			     * SHOW_THANKS_MESSAGE, false);
			     */
			}
		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }
	}
	new PostData().execute();
    }
 // post PostUploadPrescriptionDetails API call
    public void PostUploadHospitalPrescriptionDetails(final JSONObject jsonObject) {
	final class PostData extends AsyncTask<Void, Void, String> {
	    // ProgressDialog pdia;
	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		/*
		 * pdia = new ProgressDialog(UploadHospitalPrescription.this);
		 * pdia.setCancelable(false); pdia.setMessage("Loading..."); if
		 * (pdia != null) pdia.show();
		 */
	    }

	    // 200793653151
	    @Override
	    protected String doInBackground(Void... arg0) {

		JSONParser jparser = new JSONParser();

		String result = null;
		try {

		    result = jparser.postHospitalDetails(jsonObject.toString());
		} catch (JSONException e) {
		    e.printStackTrace();
		}
		return result.toString();
	    }

	    @Override
	    protected void onPostExecute(String result) {
		/*
		 * if (pdia != null && pdia.isShowing()) pdia.dismiss();
		 */
		if (result != null) {
		    try {
			if (waitDialog != null && waitDialog.isShowing()) {
			    waitDialog.dismiss();
			}
		    } catch (Exception e1) {
			e1.printStackTrace();
		    }
		    try {
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			if (status.equalsIgnoreCase("Success")) {
			    /*
			     * ApplicationSettings.putPref(
			     * AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL,true);
			     */
			    /*
			     * startActivity(new Intent(
			     * UploadHospitalPrescription.this,
			     * HomeScreen.class)
			     * .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			     */
			} else {
			    /*
			     * ApplicationSettings.putPref(
			     * AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL,
			     * false);
			     */
			}
		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }
	}
	new PostData().execute();
    }
}
