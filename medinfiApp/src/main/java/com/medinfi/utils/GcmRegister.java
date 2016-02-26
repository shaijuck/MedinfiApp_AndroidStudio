package com.medinfi.utils;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.medinfi.SplashActivity;
import com.medinfi.main.AppConstants;

public class GcmRegister {
	
	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";
	
	public GcmRegister(Context context) {
	this.context=context;
	}
	
	public void GCMRegister()
	{
		//check the PUSH_REG flag already register in server
		int count=Integer.parseInt(ApplicationSettings.getPref(AppConstants.APP_VISIT_COUNT, "0"));
		ApplicationSettings.putPref(AppConstants.APP_VISIT_COUNT, (++count)+"");
		
		if(!ApplicationSettings.getPref(AppConstants.PUSH_REG, false)){
			System.out.println("Inside reg");
		if (TextUtils.isEmpty(regId)) {
			regId = registerGCM();
			Log.d("RegisterActivity", "GCM RegId: " + regId);
		} 
		}
		
	}
	
	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(context);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
//			Toast.makeText(context,
//					"RegId already available. RegId: " + regId,
//					Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = context.getSharedPreferences(
				SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>()  {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg)  {
//				Toast.makeText(context,
//						"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
//						.show();
				System.out.println("Id msg"+msg.toString());
				registerInServer();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = context.getSharedPreferences(
				SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}
	
	ShareExternalServer appUtil;
	AsyncTask<Void, Void, String> shareRegidTask;
	
	private void registerInServer() {
		shareRegidTask = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				System.out.println("flow 1");
				appUtil = new ShareExternalServer();
				String result = appUtil.shareRegIdWithAppServer(context, regId);
				return result;
			}

			@Override
			protected  void onPostExecute(String result) {
				System.out.println("flow 3"+result);
				
				//change the status of PUSH_REG on result
				if(result.equalsIgnoreCase("true"))
				{
				ApplicationSettings.putPref(AppConstants.PUSH_REG, true);
				}
				else{
				ApplicationSettings.putPref(AppConstants.PUSH_REG, false);
				}
				shareRegidTask = null;
//				Toast.makeText(context,
//						"Result" + result, Toast.LENGTH_LONG)
//						.show();
			}

			

		};
		shareRegidTask.execute(null, null, null);
	}

}
