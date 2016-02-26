package com.medinfi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.TypefaceSpan;
import com.medinfi.utils.Utils;

public class UploadPrescriptionActivity extends SherlockActivity implements OnClickListener {

    /* declare xml views */
    private ImageView uploadPrescription;
    // private CheckBox checkBox;
    private Button submitButton;
    String encodedImage;
    public String fees, waitTime, attitude, overall, recommend, details, rate, doctorID;
    private JSONObject jsonObject;
    // private TextView agreeTextView;
    private TextView termsTextView;

    private LinearLayout layoutBack;
    private Button closeButton, closeButtonImage;
    private Typeface tfBold, tfRegular, tfLite;
    private TextView uploadPresTV, uploadHelpTextone;
    private File file;
    private Bitmap bitmap;
    private String picturePath;
    int rotate = 0;
    private EasyTracker easyTracker = null;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private TextView txtErrorMesg;
    private Bitmap largeImageBitmap;
    private Dialog selectImageDialog;

    /* declare xml ids */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	setContentView(R.layout.upload_precription);
	easyTracker = EasyTracker.getInstance(UploadPrescriptionActivity.this);

	tfBold = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoBold.ttf");
	tfLite = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoLight.ttf");
	tfRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	uploadPresTV = (TextView) findViewById(R.id.upload_prescription_text);
	uploadPresTV.setTypeface(tfRegular);
	uploadHelpTextone = (TextView) findViewById(R.id.upload_prescription_helptext_one);
	uploadHelpTextone.setTypeface(tfLite);
	txtErrorMesg = (TextView) findViewById(R.id.txtErrorMesg);
	txtErrorMesg.setTypeface(tfRegular);
	txtErrorMesg.setVisibility(View.GONE);
	layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
	closeButton = (Button) findViewById(R.id.close_button_hs);
	closeButtonImage = (Button) findViewById(R.id.close_button_imageview);
	layoutBack.setOnClickListener(this);
	closeButton.setOnClickListener(this);
	closeButtonImage.setOnClickListener(this);
	ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, false);
	// agreeTextView = (TextView) findViewById(R.id.agree_tv);
	termsTextView = (TextView) findViewById(R.id.termsandconditions);
	// agreeTextView.setTypeface(tfRegular);
	termsTextView.setTypeface(tfRegular);
	//termsTextView.setOnClickListener(this);
	uploadPrescription = (ImageView) findViewById(R.id.iv_upload_prescription);
	uploadPrescription.setOnClickListener(this);
	submitButton = (Button) findViewById(R.id.submit_button_prescription);
	submitButton.setOnClickListener(this);
	submitButton.setTypeface(tfRegular);
	// checkBox = (CheckBox) findViewById(R.id.check_submit_prescription);
	/*
	 * agreeTextView.setMovementMethod(LinkMovementMethod.getInstance());
	 * agreeTextView
	 * .setText(Html.fromHtml(getResources().getString(R.string.mlink)));
	 */
	// stripUnderlines(termsTextView);
	// showLocation();
	isHomePressed = false;
	isEventPressed = false;

	getValues();
	if (isNetworkAvailable()) {
	    ((LinearLayout) findViewById(R.id.layoutMain)).setVisibility(View.VISIBLE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	} else {
	    ((LinearLayout) findViewById(R.id.layoutMain)).setVisibility(View.GONE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tfRegular);
	    ((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tfRegular);
	}
	openTermsOfUse();
    }

    private void openTermsOfUse() {
	try {
	    Spannable wordtoSpan = new SpannableString(getString(R.string.uploadPrescriptionTermsOfUse));
	    ClickableSpan clickableSpanTerms = new ClickableSpan() {
		@Override
		public void onClick(View textView) {
		    isEventPressed = true;
		    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Terms And Conditions", "Terms And Conditions", null).build());
		    startActivity(new Intent(UploadPrescriptionActivity.this, LoadUrlActivity.class).putExtra("key", AppConstants.TERMS_BASE_URL)
			    .putExtra("title", "TOU"));
		}
	    };
	    wordtoSpan.setSpan(clickableSpanTerms, 54, 66, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    termsTextView.setText(wordtoSpan);
	    termsTextView.setLinkTextColor(getResources().getColor(R.color.screen_header));
	    termsTextView.setMovementMethod(LinkMovementMethod.getInstance());

	} catch (NotFoundException e) {
	    e.printStackTrace();
	}
    }
    /* get values from rate the DoctorsDetails page */
    public void getValues() {

	fees = getIntent().getExtras().getString("fees");
	waitTime = getIntent().getExtras().getString("waittime");
	attitude = getIntent().getExtras().getString("attitude");
	overall = getIntent().getExtras().getString("overall");
	recommend = getIntent().getExtras().getString("recommend");
	details = getIntent().getExtras().getString("details");
	Float calulateRate = (Float.parseFloat(fees) + Float.parseFloat(waitTime) + Float.parseFloat(attitude) + Float.parseFloat(overall)) / 4;
	rate = String.valueOf(calulateRate);
	doctorID = ApplicationSettings.getPref(AppConstants.DOCTOR_ID, "");
	jsonObject = new JSONObject();
	try {
	    jsonObject.put("doctorId", doctorID);
	    jsonObject.put("userId", ApplicationSettings.getPref(AppConstants.USER_ID, ""));
	    jsonObject.put("fees", fees);
	    jsonObject.put("attitude", attitude);
	    jsonObject.put("waitTime", waitTime);
	    jsonObject.put("satisfaction", overall);
	    jsonObject.put("recommend", recommend);
	    jsonObject.put("rate", rate);
	    jsonObject.put("review", details);
	    jsonObject.put("email", ApplicationSettings.getPref(AppConstants.EMAIL_ID, ""));
	    jsonObject.put("deviceId", Secure.getString(UploadPrescriptionActivity.this.getContentResolver(),
                Secure.ANDROID_ID));

	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /* API call for PostUploadPrescriptionDetails */
    public void PostUploadPrescriptionDetails() {
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

	    // 200793653151
	    @Override
	    protected String doInBackground(Void... arg0) {

		JSONParser jparser = new JSONParser();
		String result = null;
		try {
		    result = jparser.postDoctorDetails(jsonObject.toString());
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
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

    // click events
    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.submit_button_prescription:
	    // if (checkBox.isChecked()) {
	    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Submit Prescription", "Submit Prescription", null).build());
	    Boolean isTrue = ApplicationSettings.getPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, false);
	    if (isTrue) {
		try {
		    if (isNetworkAvailable()) {
			txtErrorMesg.setVisibility(View.GONE);
			easyTracker.send(MapBuilder.createEvent("Upload Prescription", " Add Photo", encodedImage, null).build());
			isEventPressed = true;
			jsonObject.putOpt("image", encodedImage);
			ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, true);
			PostUploadPrescriptionDetails();
			if (RateTheDoctorActivity.getInstance() != null) {
			    RateTheDoctorActivity.getInstance().finish();
			}
			finish();

		    } else {
			txtErrorMesg.setVisibility(View.VISIBLE);
			txtErrorMesg.setText("Network not Available");
			// Toast.makeText(UploadPrescriptionActivity.this,
			// "Network not Available", Toast.LENGTH_SHORT).show();
		    }
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		}
	    } else {
		txtErrorMesg.setVisibility(View.VISIBLE);
		txtErrorMesg.setText(getString(R.string.upload_pres_image));
		// Toast.makeText(UploadPrescriptionActivity.this,
		// getResources().getString(R.string.upload_pres_image),
		// Toast.LENGTH_SHORT).show();
	    }
	    /*
	     * } else { txtErrorMesg.setVisibility(View.VISIBLE);
	     * txtErrorMesg.setText(getString(R.string.agree_message));
	     * //Toast.makeText(UploadPrescriptionActivity.this,
	     * getResources().getString(R.string.agree_message),
	     * Toast.LENGTH_SHORT).show();
	     * 
	     * }
	     */
	    break;
	case R.id.iv_upload_prescription:
	    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Add Photo", "Add Photo", null).build());
	    txtErrorMesg.setVisibility(View.GONE);
	    if(ApplicationSettings.getPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, false)){
		enlargImageDialog();
	    }else{
		selectImage();
	    }
	    break;

	case R.id.layoutBack:
	    isEventPressed = true;
	    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Back", "Back", null).build());
	    UploadPrescriptionActivity.this.finish();
	    break;

	case R.id.close_button_hs:
	    isEventPressed = true;
	    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Close", "Close", null).build());
	    ApplicationSettings.putPref(AppConstants.CLOSE_BTN, true);
	    UploadPrescriptionActivity.this.finish();
	    break;
	case R.id.close_button_imageview:

	    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Close Image", "Close Image", null).build());
	    ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, false);
	    uploadPrescription.setLayoutParams(new LinearLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics())));
	    uploadPrescription.setImageResource(R.drawable.ic_action_camera);
	    closeButtonImage.setVisibility(View.GONE);
	    break;

	default:
	    break;
	}

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

    /* select image from camera /phone */
    private void selectImage() {
	try {
	    if(selectImageDialog!=null)
	       selectImageDialog.dismiss();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	selectImageDialog = new Dialog(this);
	SpannableString s = new SpannableString("Add Photo!");
	s.setSpan(new TypefaceSpan(this, "RobotoRegular.ttf"), 0, s.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
		    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Camera", "Camera", null).build());
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
		    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Add Photo", "Choose from Gallery", null).build());
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
		    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Add Photo", "Cancel", null).build());
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
	    if(selectImageDialog!=null)
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
			// TODO Auto-generated catch block
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

  

    private class URLSpanNoUnderline extends URLSpan {
	public URLSpanNoUnderline(String url) {
	    super(url);
	}

	@Override
	public void updateDrawState(TextPaint ds) {
	    super.updateDrawState(ds);
	    ds.setUnderlineText(false);
	}
    }
    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, false);
    }

   

    public void loadBitmap(final String path, final int orientation, final int targetWidth, final int targetHeight) {

	new AsyncTask<Void, Void, Bitmap>() {

	    ProgressDialog dialog;

	    protected void onPreExecute() {
		dialog = new ProgressDialog(UploadPrescriptionActivity.this);
		dialog.setCancelable(false);
		dialog.setMessage(Utils.getCustomeFontStyle(UploadPrescriptionActivity.this, getString(R.string.screenProcessing)));
		dialog.show();
	    };

	    @Override
	    protected Bitmap doInBackground(Void... params) {
		try {
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = false;
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
			if (dialog != null && dialog.isShowing()){
			    dialog.dismiss();
			}
		    } catch (Exception e1) {
			e1.printStackTrace();
		    }
		    if (result != null) {
			try {
			    if(selectImageDialog!=null)
			      selectImageDialog.dismiss();
			} catch (Exception e) {
			    e.printStackTrace();
			}
			closeButtonImage.setVisibility(View.VISIBLE);
			ApplicationSettings.putPref(AppConstants.UPLOAD_IMAGE_PRESCRIPTION, true);
			largeImageBitmap = result;
			uploadPrescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			uploadPrescription.setImageBitmap(largeImageBitmap);
		    } else {
			txtErrorMesg.setVisibility(View.VISIBLE);
			txtErrorMesg.setText(getString(R.string.errorMesg_PrecriptionFormat));
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

    @Override
    protected void onResume() {
	super.onResume();
	try {
	    if (!isEventPressed) {
		if (isHomePressed) {
		    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Device Back", "Device Back", null).build());
		    
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
	 FlurryAgent.logEvent("UploadPrescription Activity");
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
	    isEventPressed = true;
	    easyTracker.send(MapBuilder.createEvent("Upload Prescription", "Device Back", "Device Back", null).build());
	    UploadPrescriptionActivity.this.finish();
	}
	return super.onKeyDown(keyCode, event);
    }
    private void enlargImageDialog() {
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

   	if(largeImageBitmap!=null){
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
}
