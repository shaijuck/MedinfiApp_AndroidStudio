package com.medinfi;

import java.util.ArrayList;




import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.datainfo.FavouriteInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ContactSyncUpActivity extends Activity {

    private EasyTracker easyTracker = null;
    private TextView syncUpWithPhoneDesc;
    private ProgressDialog pdia;
    private ListView matchedContactsListView;
    private ArrayList<FavouriteInfo> contactSyncUpList;
    private ContactSyncUpAdapter contactSyncUpAdapter;
    boolean isAllSaved = false;
    private boolean isHomePressed = false;
    private static boolean isEventPressed = false;
    private static ContactSyncUpActivity contactSyncUp;

    // Performance testing
    private String preServerTimeSpend = "";
    private String serverTimeSpend = "";
    private String postServerTimepend = "";
    private String totalScreenTimeSpend = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.contact_syncup_layout);
	easyTracker = EasyTracker.getInstance(ContactSyncUpActivity.this);
	contactSyncUp = this;
	initialize();
	isHomePressed = false;
	isEventPressed = false;
	chechNetworkConnectivity();
	listViewClickEvent();

    }

    private void initialize() {
	try {
	    if (AppConstants.LogLevel == 1) {
		Utils.startPreServerCallTime = System.currentTimeMillis();
		Utils.startTotalScreenTime = System.currentTimeMillis();
	    }
	    syncUpWithPhoneDesc = (TextView) findViewById(R.id.syncUpWithPhoneDesc);
	    matchedContactsListView = (ListView) findViewById(R.id.matchedContactsListView);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void chechNetworkConnectivity() {
	try {
	    if (Utils.isNetworkAvailable(this)) {
		matchedContactsListView.setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
		contactSyncUpList = new ArrayList<FavouriteInfo>();
		getContactsDetails(this.getContentResolver());
	    } else {
		matchedContactsListView.setVisibility(View.GONE);
		syncUpWithPhoneDesc.setText(getString(R.string.syncWithPhoneNotFound));
		((LinearLayout) findViewById(R.id.layoutSaveAll)).setVisibility(View.GONE);
		((ImageView) findViewById(R.id.imgLineDivider)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void getContactsDetails(ContentResolver cr) {
	StringBuffer stringBuffer = new StringBuffer();
	boolean isPhoneNumberAdded = false;
	try {
	    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
	    while (phones.moveToNext()) {
		// String name =
		// phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		String resPhoneNumber;
		isPhoneNumberAdded = false;
		if (phoneNumber.contains("-")) {
		    isPhoneNumberAdded = true;
		    resPhoneNumber = phoneNumber.replace("-", "");
		    resPhoneNumber = resPhoneNumber.replaceAll(" ", "");
		    resPhoneNumber = resPhoneNumber.substring(resPhoneNumber.length() - 10);
		    stringBuffer.append("'");
		    stringBuffer.append(resPhoneNumber);
		    stringBuffer.append("'");
		    stringBuffer.append(",");
		}
		if (phoneNumber.contains("+") && phoneNumber.length() == 13) {
		    isPhoneNumberAdded = true;
		    resPhoneNumber = phoneNumber.replaceAll(" ", "");
		    resPhoneNumber = resPhoneNumber.substring(resPhoneNumber.length() - 10);
		    stringBuffer.append("'");
		    stringBuffer.append(resPhoneNumber);
		    stringBuffer.append("'");
		    stringBuffer.append(",");
		} else if (phoneNumber.startsWith("0") && phoneNumber.length() == 11) {
		    isPhoneNumberAdded = true;
		    resPhoneNumber = phoneNumber.replaceAll(" ", "");
		    resPhoneNumber = resPhoneNumber.substring(resPhoneNumber.length() - 10);
		    stringBuffer.append("'");
		    stringBuffer.append(resPhoneNumber);
		    stringBuffer.append("'");
		    stringBuffer.append(",");
		} else if (phoneNumber.length() >= 10) {
		    try {
			if (!isPhoneNumberAdded) {
			    resPhoneNumber = phoneNumber.replaceAll(" ", "");
			    resPhoneNumber = resPhoneNumber.substring(resPhoneNumber.length() - 10);
			    stringBuffer.append("'");
			    stringBuffer.append(resPhoneNumber);
			    stringBuffer.append("'");
			    stringBuffer.append(",");
			}

		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}

	    }
	    phones.close();
	    new GetMatchedContactList().execute(removeLastChar(stringBuffer.toString()));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static String removeLastChar(String str) {
	String strResult = "";
	try {
	    strResult = str.substring(0, str.length() - 1);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return strResult;
    }

    public void onClickCancel(View view) {
	easyTracker.send(MapBuilder.createEvent("Sync-Up", "Close", "Close", null).build());
	startFavActivity();

    }

    private void listViewClickEvent() {

	matchedContactsListView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FavouriteInfo entry = (FavouriteInfo) parent.getItemAtPosition(position);
		if (parent != null && entry != null) {
		    if (entry.getFavType().equalsIgnoreCase("Doctor")) {
			ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getFavId());
			Intent intent = new Intent(ContactSyncUpActivity.this, DoctorDetailsActivity.class);
			intent.putExtra("CALLING_SCREEN", "ContactSyncUp");
			startActivity(intent);
		    } else {
			ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getFavId());
			Intent intent = new Intent(ContactSyncUpActivity.this, HospitalDetailsActivity.class);
			intent.putExtra("CALLING_SCREEN", "ContactSyncUp");
			startActivity(intent);
		    }
		    easyTracker.send(MapBuilder.createEvent("Sync-Up", "View Details", entry.getFavName(), null).build());

		}
	    }
	});

    }

    @Override
    protected void onStart() {
	super.onStart();
	FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
	FlurryAgent.logEvent("ContactSyncUp Activity");
	EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onResume() {
	super.onResume();
	if (!isEventPressed) {
	    if (isHomePressed) {
		easyTracker.send(MapBuilder.createEvent("Sync-Up", "Device Home", "Device Home", null).build());
	    }
	   // if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
			
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
	EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
    }

    public class GetMatchedContactList extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pdia = new ProgressDialog(ContactSyncUpActivity.this);
	    pdia.setCancelable(true);
	    pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.screenLoading)));
	    pdia.show();
	}

	@Override
	protected String doInBackground(String... params) {
	    String result = null;
	    JSONParser jparser = new JSONParser();
	    if (AppConstants.LogLevel == 1) {
		preServerTimeSpend = Utils.preServerTimeSpend();
		Utils.startServerTime = System.currentTimeMillis();
	    }
	    try {
		result = jparser.postContactsDetails(params[0]);
	    } catch (Exception e) {
	    }
	    if (AppConstants.LogLevel == 1) {
		serverTimeSpend = Utils.serverTimeSpend();
	    }
	    return result.toString();
	}

	@Override
	protected void onPostExecute(String result) {
	    if (pdia != null && pdia.isShowing())
		pdia.dismiss();
	    try {
		if (result != null) {
		    if (AppConstants.LogLevel == 1) {
			Utils.startPostServerCallTime = System.currentTimeMillis();
		    }
		    contactSyncUpList.clear();
		    JSONObject jsonObject = new JSONObject(result.toString());
		    JSONArray jsonarray = new JSONArray(jsonObject.getString("ContactSync"));
		    if (AppConstants.LogLevel == 1) {
			try {
			    ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		    for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject obj = jsonarray.getJSONObject(i);
			FavouriteInfo favouriteInfo = new FavouriteInfo();
			if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {

			    favouriteInfo.setFavId(obj.getString("id"));
			    String favouriteName;
			    if (obj.getString("name").toString().contains(",")) {

				favouriteName = obj.getString("name").toString().split(",")[0];
			    } else {
				favouriteName = obj.getString("name").toString();
			    }
			    favouriteInfo.setFavName(favouriteName.replace("?", ""));
			    if (obj.getString("address") != null && !obj.getString("address").equalsIgnoreCase("")
				    && !obj.getString("address").equalsIgnoreCase("null")) {
				favouriteInfo.setFavAddress(obj.getString("address"));
			    }

			    if (obj.getString("primaryphone") != null && !obj.getString("primaryphone").equalsIgnoreCase("")
				    && !obj.getString("primaryphone").equalsIgnoreCase("null")) {
				favouriteInfo.setFavPrimaryphone(obj.getString("primaryphone"));
			    }

			    if (obj.has("locality") && obj.getString("locality") != null && !obj.getString("locality").equalsIgnoreCase("")
				    && !obj.getString("locality").equalsIgnoreCase(null)) {
				favouriteInfo.setFavLocality(obj.getString("locality").replace("?", ""));
			    }

			    if (obj.has("type") && obj.getString("type") != null && !obj.getString("type").equalsIgnoreCase("")
				    && !obj.getString("type").equalsIgnoreCase(null)) {
				favouriteInfo.setFavType(obj.getString("type").replace("?", ""));
			    }
			    if (obj.has("lat") && obj.getString("lat") != null && !obj.getString("lat").equalsIgnoreCase("")
				    && !obj.getString("lat").equalsIgnoreCase(null)) {
				favouriteInfo.setFavLatitude(obj.getString("lat").replace("?", ""));
			    }
			    if (obj.has("lon") && obj.getString("lon") != null && !obj.getString("lon").equalsIgnoreCase("")
				    && !obj.getString("lon").equalsIgnoreCase(null)) {
				favouriteInfo.setFavLongitude(obj.getString("lon").replace("?", ""));
			    }
			    if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
				    && !obj.getString("avgrate").equalsIgnoreCase(null)) {
				favouriteInfo.setFavRating(obj.getString("avgrate").replace("?", ""));
			    }
			    if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").trim().equalsIgnoreCase("")
				    && !obj.getString("pic").trim().equalsIgnoreCase(null)) {
				favouriteInfo.setFavPic(obj.getString("pic").replace("?", "").trim());
			    }

			    if (obj.has("second_image") && obj.getString("second_image") != null
				    && !obj.getString("second_image").trim().equalsIgnoreCase("")
				    && !obj.getString("second_image").trim().equalsIgnoreCase(null)) {
				favouriteInfo.setFavSecondPic(obj.getString("second_image").trim());
			    }

			    if (obj.has("emergencyPic") && obj.getString("emergencyPic") != null
				    && !obj.getString("emergencyPic").trim().equalsIgnoreCase("") && !obj.getString("emergencyPic").trim().equalsIgnoreCase(null)) {
				favouriteInfo.setHospitalEmergencyPic(obj.getString("emergencyPic").trim());
			    }
			    if (obj.has("leftPic") && obj.getString("leftPic") != null && !obj.getString("leftPic").trim().equalsIgnoreCase("")
				    && !obj.getString("leftPic").trim().equalsIgnoreCase(null)) {
				favouriteInfo.setHospitalLeftPic(obj.getString("leftPic").trim());
			    }
			    if (obj.has("rightPic") && obj.getString("rightPic") != null && !obj.getString("rightPic").trim().equalsIgnoreCase("")
				    && !obj.getString("rightPic").trim().equalsIgnoreCase(null)) {
				favouriteInfo.setHospitalRightPic(obj.getString("rightPic").trim());
			    }

			    if (obj.has("doctype") && obj.getString("doctype") != null && !obj.getString("doctype").equalsIgnoreCase("")
				    && !obj.getString("doctype").equalsIgnoreCase(null)) {
				favouriteInfo.setFavDoctype(obj.getString("doctype").replace("?", ""));
			    }
			    if (obj.has("isUserFavorite") && obj.getString("isUserFavorite") != null
				    && !obj.getString("isUserFavorite").equalsIgnoreCase("")
				    && !obj.getString("isUserFavorite").equalsIgnoreCase(null)) {
				favouriteInfo.setFavSaved(obj.getString("isUserFavorite"));
			    }

			    contactSyncUpList.add(favouriteInfo);
			}
		    }

		    if (contactSyncUpList != null && contactSyncUpList.size() > 0) {

			setContactSyncAdapter(contactSyncUpList, "UnSaveAll");
			for (int i = 0; i < contactSyncUpList.size(); i++) {
			    if (contactSyncUpList.get(i).getFavSaved().equalsIgnoreCase("1")) {
				isAllSaved = true;
			    } else {
				isAllSaved = false;
				break;
			    }
			}
			if (isAllSaved) {
			    ((ImageView) findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favourite_icon_sel);
			}
			((LinearLayout) findViewById(R.id.layoutSaveAll)).setVisibility(View.VISIBLE);
		    } else {
			((TextView) findViewById(R.id.syncUpWithPhoneDesc)).setText(getString(R.string.syncWithPhoneNotFound));
			((LinearLayout) findViewById(R.id.layoutSaveAll)).setVisibility(View.GONE);
			((ImageView) findViewById(R.id.imgLineDivider)).setVisibility(View.GONE);
		    }
		    if (AppConstants.LogLevel == 1) {
			try {
			    postServerTimepend = Utils.postServerTimeSpend();
			    totalScreenTimeSpend = Utils.totalScreenTimeSpend();
			    Utils.callPerfomanceTestingApi("Get Contact matched data", "ContactSyncUpActivity", "SyncUserContacts",
				    preServerTimeSpend, serverTimeSpend, postServerTimepend, totalScreenTimeSpend,
				    Utils.getNetworkType(ContactSyncUpActivity.this),
				    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""), AppConstants.perfTestCode);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}

	    } catch (JSONException e) {
		((TextView) findViewById(R.id.syncUpWithPhoneDesc)).setText(getString(R.string.syncWithPhoneNotFound));
		((LinearLayout) findViewById(R.id.layoutSaveAll)).setVisibility(View.GONE);
		((ImageView) findViewById(R.id.imgLineDivider)).setVisibility(View.GONE);
	    }

	}

    }

    OnCancelListener cancelListener = new OnCancelListener() {
	@Override
	public void onCancel(DialogInterface arg0) {
	    ContactSyncUpActivity.this.finish();
	}
    };

    private void setContactSyncAdapter(ArrayList<FavouriteInfo> contactSyncUpList, String savedAll) {
	contactSyncUpAdapter = new ContactSyncUpAdapter(ContactSyncUpActivity.this, R.layout.contact_syncup_list_data, contactSyncUpList, savedAll);
	matchedContactsListView.setAdapter(contactSyncUpAdapter);
    }

    public void saveAllClickEvent(View view) {
	StringBuffer stringBuffer = new StringBuffer();
	if (isAllSaved) {
	    for (int i = 0; i < contactSyncUpList.size(); i++) {
		// if
		// (contactSyncUpList.get(i).getFavSaved().equalsIgnoreCase("0"))
		// {
		stringBuffer.append(contactSyncUpList.get(i).getFavId());
		stringBuffer.append(":");
		stringBuffer.append(contactSyncUpList.get(i).getFavType());
		stringBuffer.append(",");
		// }
	    }
	    easyTracker.send(MapBuilder.createEvent("Sync-Up", "Un Save All", "Un Save All", null).build());
	    new DeleteAllFavouriteAsyncTask().execute(removeLastChar(stringBuffer.toString()));

	} else {
	    for (int i = 0; i < contactSyncUpList.size(); i++) {
		if (contactSyncUpList.get(i).getFavSaved().equalsIgnoreCase("0")) {
		    stringBuffer.append(contactSyncUpList.get(i).getFavId());
		    stringBuffer.append(":");
		    stringBuffer.append(contactSyncUpList.get(i).getFavType());
		    stringBuffer.append(",");
		}

	    }
	    easyTracker.send(MapBuilder.createEvent("Sync-Up", "Save All", "Save All", null).build());
	    new AddAllFavouriteAsyncTask().execute(removeLastChar(stringBuffer.toString()));

	}

    }

    public class AddAllFavouriteAsyncTask extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pdia = new ProgressDialog(ContactSyncUpActivity.this);
	    pdia.setCancelable(true);
	    // pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favouriteSaving)));
	    pdia.show();

	}

	@Override
	protected String doInBackground(String... params) {

	    JSONParser jparser = new JSONParser();
	    String result = jparser.addAllFavourite(params[0]);

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

			    for (int i = 0; i < contactSyncUpList.size(); i++) {
				contactSyncUpList.get(i).setFavSaved("1");
			    }

			    setContactSyncAdapter(contactSyncUpList, "SaveAll");
			    isAllSaved = true;
			    ((ImageView) findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favourite_icon_sel);

			} else {
			    ((ImageView) findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favrate_icon);
			    Toast.makeText(ContactSyncUpActivity.this,
				    Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
				    Toast.LENGTH_LONG).show();
			}

		    }
		} catch (Exception e) {
		    // ((ImageView)
		    // findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favrate_icon);
		    Toast.makeText(ContactSyncUpActivity.this,
			    Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
			    Toast.LENGTH_LONG).show();
		}
	    } else {
		// ((ImageView)
		// findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favrate_icon);
		Toast.makeText(ContactSyncUpActivity.this,
			Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)), Toast.LENGTH_LONG)
			.show();
	    }
	    if (pdia != null) {
		pdia.dismiss();
	    }
	}

    }

    public class DeleteAllFavouriteAsyncTask extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    pdia = new ProgressDialog(ContactSyncUpActivity.this);
	    pdia.setCancelable(true);
	    // pdia.setOnCancelListener(cancelListener);
	    pdia.setMessage(Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favouriteDeleting)));
	    pdia.show();

	}

	@Override
	protected String doInBackground(String... params) {

	    JSONParser jparser = new JSONParser();
	    String result = jparser.deleteAllFavourite(params[0]);

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
			    for (int i = 0; i < contactSyncUpList.size(); i++) {
				contactSyncUpList.get(i).setFavSaved("0");
			    }
			    setContactSyncAdapter(contactSyncUpList, "UnSaveAll");
			    isAllSaved = false;
			    ((ImageView) findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favrate_icon);

			} else {
			    // ((ImageView)
			    // findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favourite_icon_sel);
			    Toast.makeText(ContactSyncUpActivity.this,
				    Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
				    Toast.LENGTH_LONG).show();
			}

		    }
		} catch (Exception e) {
		    // ((ImageView)
		    // findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favourite_icon_sel);
		    Toast.makeText(ContactSyncUpActivity.this,
			    Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
			    Toast.LENGTH_LONG).show();
		}
	    } else {
		Toast.makeText(ContactSyncUpActivity.this,
			Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)), Toast.LENGTH_LONG)
			.show();
	    }
	    if (pdia != null) {
		pdia.dismiss();
	    }
	}

    }

    /* back button called */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    easyTracker.send(MapBuilder.createEvent("Sync-Up", "Device Back", "Device Back", null).build());
	    startFavActivity();
	    return true;
	}
	return false;
    }

    private void startFavActivity() {
	isEventPressed = true;
	if (FavouriteActivity.getInstance() != null) {
	    FavouriteActivity.getInstance().finish();
	}
	Intent intent = new Intent(ContactSyncUpActivity.this, FavouriteActivity.class);
	startActivity(intent);
	ContactSyncUpActivity.this.finish();

    }

    public static ContactSyncUpActivity getInstance() {
	return contactSyncUp;
    }

    class ContactSyncUpAdapter extends ArrayAdapter<FavouriteInfo> {

	/* Apdater used to load the Hospitals information in the listview */
	Context context;
	int layoutResourceId;
	public ArrayList<FavouriteInfo> favouriteList;
	private ArrayList<FavouriteInfo> arraylist;
	private ImageLoader imageLoader;
	DisplayImageOptions optionsDoctor, optionHospital;
	private Typeface tfBold, tfRegular, tfLite, tfCondensed;
	private ProgressDialog pdia;
	FavouriteHolder holder = null;

	public ContactSyncUpAdapter(Context context, int layoutResourceId, ArrayList<FavouriteInfo> favouriteList, String favSavedAll) {
	    super(context, layoutResourceId, favouriteList);
	    this.layoutResourceId = layoutResourceId;
	    this.context = context;
	    this.favouriteList = favouriteList;
	    this.arraylist = new ArrayList<FavouriteInfo>();
	    this.arraylist.addAll(favouriteList);
	    imageLoader = ImageLoader.getInstance();

	    optionsDoctor = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		    .showImageForEmptyUri(R.drawable.ic_action_person).showImageOnFail(R.drawable.ic_action_person)
		    .showImageOnLoading(R.drawable.ic_action_person).build();

	    optionHospital = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		    .showImageForEmptyUri(R.drawable.hospital_icon_normal).showImageOnFail(R.drawable.hospital_icon_normal)
		    .showImageOnLoading(R.drawable.hospital_icon_normal).build();

	    tfBold = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoBold.ttf");
	    tfLite = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoLight.ttf");
	    tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	    tfCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed.ttf");
	}

	/*
	 * add the view based on the count of arraylist set the values using
	 * getter setter methods
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    View row = convertView;

	    if (row == null) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new FavouriteHolder();
		holder.txtProfileName = (TextView) row.findViewById(R.id.txtProfileName);
		holder.txtProfileName.setTypeface(tfRegular);

		holder.txtProfiledesignation = (TextView) row.findViewById(R.id.txtProfiledesignation);
		holder.txtProfiledesignation.setTypeface(tfLite);
		holder.imgProfilePic = (ImageView) row.findViewById(R.id.imgProfilePic);

		holder.saveButton = (ImageView) row.findViewById(R.id.btnSave);
		holder.saveButton.setOnClickListener(callButtonClickListener);
		row.setTag(holder);
	    } else {
		holder = (FavouriteHolder) row.getTag();
	    }
	    FavouriteInfo favouriteInfo = favouriteList.get(position);
	    holder.saveButton.setTag(position);
	    holder.txtProfileName.setText(favouriteInfo.getFavName());

	    if (favouriteInfo.getFavType() != null && favouriteInfo.getFavType().equalsIgnoreCase("Doctor")) {
		 holder.imgProfilePic.setImageResource(R.drawable.ic_action_person);
		if (favouriteInfo.getFavDoctype() != null && !favouriteInfo.getFavDoctype().equalsIgnoreCase("")
			&& !favouriteInfo.getFavDoctype().equalsIgnoreCase("null")) {
		    holder.txtProfiledesignation.setVisibility(View.VISIBLE);
		    holder.txtProfiledesignation.setText(favouriteInfo.getFavDoctype());
		} else {
		    holder.txtProfiledesignation.setVisibility(View.GONE);
		}

		if (favouriteInfo.getFavPic() != null && !favouriteInfo.getFavPic().equalsIgnoreCase("")
			&& !favouriteInfo.getFavPic().equalsIgnoreCase(null) && !favouriteInfo.getFavPic().equalsIgnoreCase("null")) {
		   // imageLoader.displayImage(favouriteInfo.getFavPic(), holder.imgProfilePic, optionsDoctor);
		    Glide.with(context).load(favouriteInfo.getFavPic()).placeholder(R.drawable.ic_action_person).into(holder.imgProfilePic); 
		} else {
		    holder.imgProfilePic.setImageResource(R.drawable.ic_action_person);
		}
	    } else {
		holder.imgProfilePic.setImageResource(R.drawable.hospital_icon_normal);
		if (favouriteInfo.getFavLocality() != null && !favouriteInfo.getFavLocality().equalsIgnoreCase("")
			&& !favouriteInfo.getFavLocality().equalsIgnoreCase("null")) {
		    holder.txtProfiledesignation.setVisibility(View.VISIBLE);
		    holder.txtProfiledesignation.setText(favouriteInfo.getFavLocality());
		} else {
		    holder.txtProfiledesignation.setVisibility(View.GONE);
		}
		if (favouriteInfo.getFavPic() != null && !favouriteInfo.getFavPic().equalsIgnoreCase("")
			&& !favouriteInfo.getFavPic().equalsIgnoreCase(null) && !favouriteInfo.getFavPic().equalsIgnoreCase("null")) {
		  //  imageLoader.displayImage(favouriteInfo.getFavPic(), holder.imgProfilePic, optionHospital);
		    Glide.with(context).load(favouriteInfo.getFavPic()).placeholder(R.drawable.hospital_icon_normal).into(holder.imgProfilePic);
		} else {
		    holder.imgProfilePic.setImageResource(R.drawable.hospital_icon_normal);
		}
	    }
	    if (favouriteInfo.getFavSaved() != null && favouriteInfo.getFavSaved().equalsIgnoreCase("1")) {
		holder.saveButton.setBackgroundResource(R.drawable.favourite_icon_sel);
	    }
	    if (favouriteInfo.getFavSaved() != null && favouriteInfo.getFavSaved().equalsIgnoreCase("0")) {
		holder.saveButton.setBackgroundResource(R.drawable.favrate_icon);
	    }
	    for (int i = 0; i < favouriteList.size(); i++) {
		if (favouriteList.get(i).getFavSaved().equalsIgnoreCase("1")) {
		    isAllSaved = true;
		} else {
		    isAllSaved = false;
		    break;
		}
	    }
	    if (isAllSaved) {
		((ImageView) findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favourite_icon_sel);
	    }

	    holder.imgProfilePic.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    if (favouriteList.get(position).getFavPic() != null && !favouriteList.get(position).getFavPic().trim().equalsIgnoreCase("")
				&& !favouriteList.get(position).getFavPic().trim().equalsIgnoreCase(null) && !favouriteList.get(position).getFavPic().trim().equalsIgnoreCase("null")) {
			new EnlargeMultipleImageDialog(context, position).show();
		    }
		    
		}
	    });

	    return row;
	}

	/* holder which holds the view associated to the adapter */
	class FavouriteHolder {
	    TextView txtProfileName;
	    TextView txtProfiledesignation;
	    TextView txtDistance;
	    TextView txtRatings;
	    ImageView imgProfilePic;
	    TextView txtProfileKM;
	    TextView txtProfileRated;
	    ImageView saveButton;
	}

	int position = -1;
	// click listener called when user clicks on the list item call button
	private OnClickListener callButtonClickListener = new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		position = (Integer) v.getTag();
		favouriteList.get(position).setFavSavedIcon(R.drawable.favourite_icon_sel);
		if (favouriteList.get(position).getFavSaved() != null && favouriteList.get(position).getFavSaved().equalsIgnoreCase("1")) {
		    if (favouriteList.get(position).getFavType() != null && favouriteList.get(position).getFavType().equalsIgnoreCase("Doctor")) {
			new DeleteFavouriteAsyncTask().execute("Doctor", favouriteList.get(position).getFavId());
		    } else {
			new DeleteFavouriteAsyncTask().execute("Hospital", favouriteList.get(position).getFavId());
		    }
		    easyTracker.send(MapBuilder.createEvent("Sync-Up", "Un Save", favouriteList.get(position).getFavName(), null).build());
		} else {
		    if (favouriteList.get(position).getFavType() != null && favouriteList.get(position).getFavType().equalsIgnoreCase("Doctor")) {
			new AddFavouriteAsyncTask().execute("Doctor", favouriteList.get(position).getFavId());
		    } else {
			new AddFavouriteAsyncTask().execute("Hospital", favouriteList.get(position).getFavId());
		    }
		    easyTracker.send(MapBuilder.createEvent("Sync-Up", "Save", favouriteList.get(position).getFavName(), null).build());
		}
	    }

	};
	String strType;

	public class AddFavouriteAsyncTask extends AsyncTask<String, Void, String> {

	    @Override
	    protected void onPreExecute() {
		super.onPreExecute();
		pdia = new ProgressDialog(context);
		pdia.setCancelable(true);
		// pdia.setOnCancelListener(cancelListener);
		pdia.setMessage(Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favouriteSaving)));
		pdia.show();

	    }

	    @Override
	    protected String doInBackground(String... params) {

		JSONParser jparser = new JSONParser();
		strType = params[0];
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
				favouriteList.get(position).setFavSaved("1");
				holder.saveButton.setBackgroundResource(favouriteList.get(position).getFavSavedIcon());
				notifyDataSetChanged();
			    } else {
				favouriteList.get(position).setFavSaved("0");
				Toast.makeText(context,
					Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
					Toast.LENGTH_LONG).show();
			    }

			}
		    } catch (Exception e) {
			Toast.makeText(context,
				Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
				Toast.LENGTH_LONG).show();
		    }
		} else {
		    Toast.makeText(context,
			    Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
			    Toast.LENGTH_LONG).show();
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
		pdia = new ProgressDialog(context);
		pdia.setCancelable(true);
		// pdia.setOnCancelListener(cancelListener);
		pdia.setMessage(Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favouriteDeleting)));
		pdia.show();
	    }

	    @Override
	    protected String doInBackground(String... params) {

		JSONParser jparser = new JSONParser();
		strType = params[0];
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
				// isFavSaved = false;
				favouriteList.get(position).setFavSaved("0");
				isAllSaved = false;
				((ImageView) findViewById(R.id.imgSaveAll)).setBackgroundResource(R.drawable.favrate_icon);
				holder.saveButton.setBackgroundResource(R.drawable.favrate_icon);
				notifyDataSetChanged();
			    } else {
				Toast.makeText(context,
					Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
					Toast.LENGTH_LONG).show();
			    }

			}
		    } catch (Exception e) {
			Toast.makeText(context,
				Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
				Toast.LENGTH_LONG).show();
		    }
		} else {
		    Toast.makeText(context,
			    Utils.getCustomeFontStyle(ContactSyncUpActivity.this, getString(R.string.favAddedFailureHospitalDeleting)),
			    Toast.LENGTH_LONG).show();
		}
		if (pdia != null) {
		    pdia.dismiss();
		}
	    }
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

		    if (favouriteList.get(iPosition).getFavType() != null && favouriteList.get(iPosition).getFavType().equalsIgnoreCase("Doctor")) {
			if (favouriteList.get(iPosition).getFavPic() != null) {
			    multiplaeImageList.add(favouriteList.get(iPosition).getFavPic());
			}
			if (favouriteList.get(iPosition).getFavSecondPic() != null) {
			    multiplaeImageList.add(favouriteList.get(iPosition).getFavSecondPic());
			}

		    } else {
			if (favouriteList.get(iPosition).getFavPic() != null) {
			    multiplaeImageList.add(favouriteList.get(iPosition).getFavPic());
			}
			if (favouriteList.get(iPosition).getHospitalEmergencyPic() != null) {
			    multiplaeImageList.add(favouriteList.get(iPosition).getHospitalEmergencyPic());
			}
			if (favouriteList.get(iPosition).getHospitalLeftPic() != null) {
			    multiplaeImageList.add(favouriteList.get(iPosition).getHospitalLeftPic());
			}
			if (favouriteList.get(iPosition).getHospitalRightPic() != null) {
			    multiplaeImageList.add(favouriteList.get(iPosition).getHospitalRightPic());
			}
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

	class EnlargeImageDialogDoctor extends Dialog implements android.view.View.OnClickListener {
	    Context context;
	    private ViewFlipper viewFlipper;
	    private float lastX;
	    private int position;
	    RelativeLayout layoutSecondImage;
	    private ImageView imageViewFirst, imageViewSecond;
	    private TextView txtCount;
	    private int iCount = 0;
	    DisplayImageOptions optionsEnlarg;
	    private ImageLoader imageLoader;

	    // ArrayList<DoctorDetailInfo> associateDocList;

	    public EnlargeImageDialogDoctor(Context context, int position) {
		super(context);
		imageLoader = ImageLoader.getInstance();
		this.context = context;
		this.position = position;
		// this.associateDocList = associateDocList;
	    }

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Rect displayRectangle = new Rect();
		Window window = ((Activity) context).getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.image_switcher_layout, null);
		layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
		// layout.setMinimumHeight((int) (displayRectangle.height() *
		// 0.9f));

		optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

		setContentView(layout);

		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

		layoutSecondImage = (RelativeLayout) findViewById(R.id.layoutSecondImage);
		imageViewFirst = (ImageView) findViewById(R.id.imageViewFirst);
		imageViewSecond = (ImageView) findViewById(R.id.imageViewSecond);
		txtCount = (TextView) findViewById(R.id.txtCount);

		if (favouriteList.get(position).getFavPic() != null) {
		    iCount = 1;
		    txtCount.setVisibility(View.VISIBLE);
		    txtCount.setText("1 of " + iCount);
		    setImageEnLargLoading(favouriteList.get(position).getFavPic(), imageViewFirst, optionsEnlarg);
		}
		if (favouriteList.get(position).getFavSecondPic() != null) {
		    iCount = 2;
		    txtCount.setVisibility(View.VISIBLE);
		    txtCount.setText("1 of " + iCount);
		}
		if (iCount == 0) {
		    txtCount.setVisibility(View.GONE);
		    ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
		    imageViewFirst.setImageResource(R.drawable.ic_action_person);
		}

		Button btnCancel = (Button) findViewById(R.id.close_button_hs);
		btnCancel.setOnClickListener(this);

	    }

	    public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {

		case MotionEvent.ACTION_DOWN:
		    lastX = touchevent.getX();
		    break;
		case MotionEvent.ACTION_UP:
		    float currentX = touchevent.getX();

		    // Handling left to right screen swap.
		    if (lastX < currentX) {

			// If there aren't any other children, just break.
			if (viewFlipper.getDisplayedChild() == 0)
			    break;

			txtCount.setText("1 of " + iCount);
			viewFlipper.setInAnimation(context, R.anim.slide_in_from_left);
			viewFlipper.setOutAnimation(context, R.anim.slide_out_to_right);

			viewFlipper.showNext();
		    }

		    // Handling right to left screen swap.
		    if (lastX > currentX) {

			if (viewFlipper.getDisplayedChild() == 1)
			    break;

			if (favouriteList.get(position).getFavSecondPic() != null) {
			    layoutSecondImage.setVisibility(View.VISIBLE);
			    txtCount.setText("2 of " + iCount);
			    setImageEnLargLoading(favouriteList.get(position).getFavSecondPic(), imageViewSecond, optionsEnlarg);
			    viewFlipper.setInAnimation(context, R.anim.slide_in_from_right);
			    viewFlipper.setOutAnimation(context, R.anim.slide_out_to_left);
			    viewFlipper.showPrevious();
			}

		    }
		    break;
		}
		return false;
	    }

	    private void setImageEnLargLoading(String imageUrl, ImageView imgProfilePicDisplay, DisplayImageOptions options) {
		imageLoader.displayImage(imageUrl, imgProfilePicDisplay, options, new ImageLoadingListener() {

		    @Override
		    public void onLoadingStarted(String arg0, View arg1) {
			((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);

		    }

		    @Override
		    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
		    }

		    @Override
		    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
		    }

		    @Override
		    public void onLoadingCancelled(String arg0, View arg1) {

		    }
		});
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
    }

}
