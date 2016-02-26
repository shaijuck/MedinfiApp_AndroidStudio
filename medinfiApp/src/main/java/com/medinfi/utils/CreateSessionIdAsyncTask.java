package com.medinfi.utils;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;

/**
 * Create session id
 * 
 * @author LalBabu
 * 
 */
public class CreateSessionIdAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
	super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

	JSONParser jparser = new JSONParser();
	Log.i("Usersession","user_session"+ApplicationSettings.getPref(AppConstants.USER_ID, "0"));
	String result = jparser.getSessionId(ApplicationSettings.getPref(AppConstants.USER_ID, "0"),
		ApplicationSettings.getPref(AppConstants.USER_SESSION_ID, "0"), ApplicationSettings.getPref(AppConstants.TOKEN, ""),
		ApplicationSettings.getPref(AppConstants.LATITUDE_AUTO, ""), ApplicationSettings.getPref(AppConstants.LONGITUDE_AUTO, ""),
		ApplicationSettings.getPref(AppConstants.GPRS_STATUS, "0"), ApplicationSettings.getPref(AppConstants.CURRENT_USER_SUB_LOCALITY_AUTO, ""),
		ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION_AUTO, ""),params[0],params[1],
		ApplicationSettings.getPref(AppConstants.OS,"Android"),
		ApplicationSettings.getPref(AppConstants.DEVICE_TYPE,"Phone"),
		ApplicationSettings.getPref(AppConstants.DEVICE_ID,""));
	//Log.i("DEVICEID","DEVID"+ApplicationSettings.getPref(AppConstants.DEVICE_ID,""));
	
	
	return result;
    }

    @Override
    protected void onPostExecute(String result) {
	super.onPostExecute(result);

	if (result != null) {
	    try {
		JSONObject jobj = new JSONObject(result);
		if (jobj != null) {
		    String message = jobj.getString("session_id");
		    // isPageView = false;
		    ApplicationSettings.putPref(AppConstants.USER_SESSION_ID, message);
		}
	    } catch (Exception e) {

	    }
	}
    }

}