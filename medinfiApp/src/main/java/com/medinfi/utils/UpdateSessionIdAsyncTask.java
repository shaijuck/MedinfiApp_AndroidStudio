package com.medinfi.utils;

import android.os.AsyncTask;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;

/**
 * Update session id
 * 
 * @author LalBabu
 * 
 */
public class UpdateSessionIdAsyncTask extends AsyncTask<Void, Void, String> {

    @Override
    protected void onPreExecute() {
	super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {

	JSONParser jparser = new JSONParser();
	String result = jparser.updateSessionId(ApplicationSettings.getPref(AppConstants.USER_ID, ""),
		ApplicationSettings.getPref(AppConstants.USER_SESSION_ID, ""), ApplicationSettings.getPref(AppConstants.TOKEN, ""),
		ApplicationSettings.getPref(AppConstants.CURRENT_USER_SUB_LOCALITY, ""),
		ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, ""));
	return result;
    }

    @Override
    protected void onPostExecute(String result) {
	super.onPostExecute(result);

    }

}