package com.medinfi.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.R;
import com.medinfi.RegisterActivity;
import com.medinfi.SuggestDoctorHospitalActivity;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.main.UserUpdateService;
import com.medinfi.parser.JSONParser;

public class Utils {
    public Context context;
    public static long startPreServerCallTime = 0;
    public static long startServerTime = 0;
    public static long startPostServerCallTime = 0;
    public static long startTotalScreenTime = 0;
    public static long startTotalScreenTime20 = 0;
    public static boolean isGPRSON = false;
    public static boolean isMedDadSelected = false;

    public Utils(Context context) {
	// TODO Auto-generated constructor stub
	this.context = context;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
	ListAdapter listAdapter = listView.getAdapter();
	if (listAdapter == null) {
	    // pre-condition
	    return;
	}

	int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
	for (int i = 0; i < listAdapter.getCount(); i++) {
	    View listItem = listAdapter.getView(i, null, listView);
	    if (listItem instanceof ViewGroup) {
		listItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    }
	    listItem.measure(0, 0);
	    totalHeight += listItem.getMeasuredHeight();
	}

	ViewGroup.LayoutParams params = listView.getLayoutParams();
	params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	listView.setLayoutParams(params);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	// Raw height and width of image
	final int height = options.outHeight;
	final int width = options.outWidth;
	int inSampleSize = 1;

	if (height > reqHeight || width > reqWidth) {

	    final int halfHeight = height / 2;
	    final int halfWidth = width / 2;

	    // Calculate the largest inSampleSize value that is a power of 2 and
	    // keeps both
	    // height and width larger than the requested height and width.
	    while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
		inSampleSize *= 2;
	    }
	}

	return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

	// First decode with inJustDecodeBounds=true to check dimensions
	final BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeResource(res, resId, options);

	// Calculate inSampleSize
	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	// Decode bitmap with inSampleSize set
	options.inJustDecodeBounds = false;
	return BitmapFactory.decodeResource(res, resId, options);
    }

    public static String getDeviceIMEI(Context context) {
	String deviceIMEI = "";
	try {
	    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    deviceIMEI = telephonyManager.getDeviceId();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return deviceIMEI;
    }

    public static int getDeviceApiLevel() {
	return android.os.Build.VERSION.SDK_INT;
    }

    public static String getDeviceManufacture() {
	return android.os.Build.MANUFACTURER;
    }

    public static String getDeviceModel() {
	return android.os.Build.MODEL;
    }

    public static String getDeviceISO(Context context) {
	TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	return telephonyManager.getSimCountryIso();
    }

    public static String preServerTimeSpend() {
	String strResult = "0";
	try {
	    if (startPreServerCallTime != 0) {
		float difference = 0;
		long lCurrentTimeMillis = System.currentTimeMillis();
		difference = (lCurrentTimeMillis - startPreServerCallTime) / (1000f);
		// System.out.println(msg + " millisecond " +
		// (lCurrentTimeMillis - startTotalScreenTime));
		long lResult = lCurrentTimeMillis - startPreServerCallTime;
		strResult = Long.toString(lResult);
		startPreServerCallTime = 0;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return strResult;
    }

    public static String serverTimeSpend() {
	String strResult = "0";
	try {
	    if (startServerTime != 0) {
		long lCurrentTimeMillis = System.currentTimeMillis();
		long lResult = lCurrentTimeMillis - startServerTime;
		strResult = Long.toString(lResult);
		startServerTime = 0;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return strResult;
    }

    public static String postServerTimeSpend() {
	String strResult = "0";
	try {
	    if (startPostServerCallTime != 0) {
		long lCurrentTimeMillis = System.currentTimeMillis();
		long lResult = lCurrentTimeMillis - startPostServerCallTime;
		strResult = Long.toString(lResult);
		startPostServerCallTime = 0;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return strResult;
    }

    public static String totalScreenTimeSpend() {
	String strResult = "0";
	try {
	    if (startTotalScreenTime != 0) {
		long lCurrentTimeMillis = System.currentTimeMillis();
		long lResult = lCurrentTimeMillis - startTotalScreenTime;
		strResult = Long.toString(lResult);
		startTotalScreenTime = 0;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return strResult;
    }

    public static String totalScreenTimeSpend20() {
	String strResult = "0";
	try {
	    if (startTotalScreenTime20 != 0) {
		long lCurrentTimeMillis = System.currentTimeMillis();
		long lResult = lCurrentTimeMillis - startTotalScreenTime20;
		strResult = Long.toString(lResult);
		startTotalScreenTime20 = 0;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return strResult;
    }

    public static boolean isNetworkAvailable(Context context) {
	boolean isConnectedWifi = false;
	boolean isConnectedMobile = false;

	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static void setKeepAliveUserUpdate(Context context) {
	try {
	    Intent myIntent = new Intent(context, UserUpdateService.class);
	    PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);
	    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(System.currentTimeMillis());
	    calendar.set(Calendar.DAY_OF_WEEK, 6);
	    calendar.set(Calendar.HOUR_OF_DAY, 12);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

	    // alarmManager.set(AlarmManager.RTC_WAKEUP,
	    // calendar.getTimeInMillis(), pendingIntent);
	    // alarmManager.set(AlarmManager.RTC_WAKEUP,
	    // System.currentTimeMillis() + (1000 * 60 * 5),
	    // pendingIntent);//for every 10 mints

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static final String createMD5AsToken(final String s) {
	final String MD5 = "MD5";
	try {
	    // Create MD5 Hash
	    MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
	    digest.update(s.getBytes());
	    byte messageDigest[] = digest.digest();

	    // Create Hex String
	    StringBuilder hexString = new StringBuilder();
	    for (byte aMessageDigest : messageDigest) {
		String h = Integer.toHexString(0xFF & aMessageDigest);
		while (h.length() < 2)
		    h = "0" + h;
		hexString.append(h);
	    }
	    return hexString.toString();

	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}
	return "";
    }

    public static ArrayList<MenuItemInfo> getMenuListItem(Context context) {
	Integer[] menuItemIcon = { R.drawable.home_icon, R.drawable.favrate_icon, R.drawable.star_unselected, R.drawable.add_doc_hospital,
		R.drawable.star_unselected, R.drawable.menu_records_icon

	};
	String[] menuTitles = context.getResources().getStringArray(R.array.menu_items);
	ArrayList<MenuItemInfo> menuItemInfo = new ArrayList<MenuItemInfo>();
	menuItemInfo.add(new MenuItemInfo(menuTitles[0], menuItemIcon[0]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[1], menuItemIcon[1]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[2], menuItemIcon[2]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[3], menuItemIcon[3]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[4], menuItemIcon[5]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[5]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[6]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[7]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[8]));
	menuItemInfo.add(new MenuItemInfo(menuTitles[9]));
	return menuItemInfo;

    }

    public static String getNetworkType(Context context) {
	try {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo info = cm.getActiveNetworkInfo();
	    if (info == null || !info.isConnected())
		return "Not Connected"; // not connected
	    if (info.getType() == ConnectivityManager.TYPE_WIFI)
		return "WIFI";
	    if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
		int networkType = info.getSubtype();
		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
							 // 11
		    return "2G";
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by
							   // 14
		case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by
							  // 12
		case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by
							  // 15
		    return "3G";
		case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by
							// 13
		    return "4G";
		default:
		    return "UnKnown";
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return "UnKnown";
    }

    // For Performance testing
    public static void callPerfomanceTestingApi(final String message, final String screenName, final String serverApiName,
	    final String preServerTime, final String serverTotalTime, final String postServerTime, final String totalScreenTime,
	    final String connectivityType, final String server_processtime, final String uniquePerfCode) {
	new Thread(new Runnable() {

	    @Override
	    public void run() {
		JSONParser jparser = new JSONParser();
		jparser.postTimeSpend(message, screenName, serverApiName, preServerTime, serverTotalTime, postServerTime, totalScreenTime,
			connectivityType, server_processtime, uniquePerfCode);

	    }
	}).start();
    }

    public static String readFileFromSdCard() {
	File sdcard = Environment.getExternalStorageDirectory();
	StringBuilder text = new StringBuilder();
	try {
	    File file = new File(sdcard, "med_perf_properties.txt");
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    String line;
	    while ((line = br.readLine()) != null) {
		text.append(line);
	    }
	    br.close();
	} catch (IOException e) {
	    return "0";
	} catch (Exception e) {
	    return "0";
	}
	return text.toString();

    }

    public static void startSuggestionActivity(final Context context, TextView txtNoResultFound, final String callingScreen,
	    final EasyTracker easyTracker) {
	try {
	    Spannable wordtoSpan = new SpannableString(context.getString(R.string.noResultFoundSuggest));
	    ClickableSpan clickableSpanTerms = new ClickableSpan() {
		@Override
		public void onClick(View textView) {
		    sendGADetails(easyTracker, callingScreen);
		    if (SuggestDoctorHospitalActivity.getInstance() != null) {
			SuggestDoctorHospitalActivity.getInstance().finish();
		    }
		    Intent intent = new Intent(context, SuggestDoctorHospitalActivity.class);
		    context.startActivity(intent);
		}
	    };
	    wordtoSpan.setSpan(new com.medinfi.utils.TypefaceSpan(context, "RobotoRegular.ttf"), 0, wordtoSpan.length(),
		    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    wordtoSpan.setSpan(clickableSpanTerms, 50, 73, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    txtNoResultFound.setText(wordtoSpan);
	    txtNoResultFound.setLinkTextColor(context.getResources().getColor(R.color.screen_header));
	    txtNoResultFound.setMovementMethod(LinkMovementMethod.getInstance());

	} catch (NotFoundException e) {
	    e.printStackTrace();
	}
    }

    public static void routeDirection(Context context, double currentLatitude, double currentLongitude, double destLatitude, double destLongitude) {
	try {
	    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?   saddr=" + currentLatitude + ","
		    + currentLongitude + "&daddr=" + destLatitude + "," + destLongitude));
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);
	    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
	    context.startActivity(intent);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static String getRatingHints(long iValues) {
	String result = "";
	if (iValues == 1) {
	    result = "Poor";
	} else if (iValues == 2) {
	    result = "Not Bad";
	} else if (iValues == 3) {
	    result = "Good";
	} else if (iValues == 4) {
	    result = "Very Good";
	} else if (iValues == 5) {
	    result = "Excellent";
	}
	return result;

    }

    /* display date in format */
    public static String getDate(String createdDate) {
	SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

	SimpleDateFormat desiredFormat = new SimpleDateFormat("dd'%s' MMM yyyy");
	try {
	    Date date = isoFormat.parse(createdDate);
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    createdDate = String.format(desiredFormat.format(date), dateSuffix(calendar));
	} catch (java.text.ParseException e) {
	    e.printStackTrace();
	}
	return createdDate;
    }

    // add suffix for date
    public static String dateSuffix(final Calendar cal) {
	final int date = cal.get(Calendar.DATE);
	switch (date % 10) {
	case 1:
	    if (date != 11) {
		return "st";
	    }
	    break;

	case 2:
	    if (date != 12) {
		return "nd";
	    }
	    break;

	case 3:
	    if (date != 13) {
		return "rd";
	    }
	    break;
	}
	return "th";
    }

    private static void sendGADetails(EasyTracker easyTracker, String callingScreen) {
	try {
	    if (callingScreen != null) {
		if (callingScreen.equalsIgnoreCase("HospitalActivity")) {
		    easyTracker.send(MapBuilder.createEvent("Hospital List", "Suggest Doctor/Hospita", "Suggest Doctor/Hospita", null).build());
		} else if (callingScreen.equalsIgnoreCase("GeneralPhysicansActivity")) {
		    easyTracker.send(MapBuilder.createEvent("Hospital List", "Suggest Doctor/Hospita", "Suggest Doctor/Hospita", null).build());
		} else if (callingScreen.equalsIgnoreCase("HomeScreen")) {
		    easyTracker.send(MapBuilder.createEvent("Global Search", "Suggest Doctor/Hospita", "Suggest Doctor/Hospita", null).build());
		} else if (callingScreen.equalsIgnoreCase("SpecialistDoctorsActivity")) {
		    easyTracker.send(MapBuilder.createEvent("Doctor List", "Suggest Doctor/Hospita", "Suggest Doctor/Hospita", null).build());
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /* convert bitmap to base64 string */
    public static String convertBase64(Bitmap bitmap) {
	// Bitmap bm = uploadPrescription.getDrawingCache();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
	byte[] b = baos.toByteArray();
	return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * Create custom dialog
     */
    public static void customThankYouDialog(Context context, String strTitle, String strMassag) {
	final Dialog dialog = new Dialog(context);
	Typeface tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	// Typeface tfRobotoLight =
	// Typeface.createFromAsset(context.getAssets(),
	// "fonts/RobotoLight.ttf");
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.review_thankyou_layout, null);
	dialog.setContentView(layout);
	dialog.setCancelable(false);
	WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
	wmlp.gravity = Gravity.TOP | Gravity.LEFT;
	wmlp.x = 60;
	wmlp.y = 60;
	dialog.show();
	((TextView) dialog.findViewById(R.id.textDialog)).setTypeface(tfRegular);
	((TextView) dialog.findViewById(R.id.textDialogMsg)).setTypeface(tfRegular);
	((TextView) dialog.findViewById(R.id.textDialog)).setText(strTitle);
	((TextView) dialog.findViewById(R.id.textDialogMsg)).setText(strMassag);
	ImageView btnCancel = (ImageView) dialog.findViewById(R.id.imgCancel);
	btnCancel.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
		RegisterActivity.isRegisteredScreen = false;
		dialog.dismiss();
	    }
	});
    }

    public static void enlargImageDialog(Context context, Bitmap largeImageBitmap) {
	final Dialog dialog = new Dialog(context);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	Rect displayRectangle = new Rect();
	Window window = ((Activity) context).getWindow();
	window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public static CharSequence getCustomeFontStyle(Context context, String message) {
	Spannable wordtoSpan = new SpannableString(message);
	try {
	    wordtoSpan.setSpan(new com.medinfi.utils.TypefaceSpan(context, "RobotoRegular.ttf"), 0, wordtoSpan.length(),
		    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	} catch (NotFoundException e) {
	    e.printStackTrace();
	}

	return wordtoSpan;

    }

    public static void writeFileToSdCard(String fname, String fcontent) {
	try {
	    String fpath = "/sdcard/" + fname + ".txt";
	    File file = new File(fpath);
	    // If file does not exists, then create it
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    FileWriter fw = new FileWriter(file.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write(fcontent);
	    bw.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
