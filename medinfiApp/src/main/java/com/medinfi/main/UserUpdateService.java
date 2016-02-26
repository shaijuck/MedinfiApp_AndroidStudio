package com.medinfi.main;

import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UserUpdateService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }
    @Override
    public void onCreate() {
	userUpdation();
    }

    private void userUpdation(){
	new Thread(new Runnable() {
	    
	    @Override
	    public void run() {
		JSONParser jparser = new JSONParser();
		String result = jparser.userDeviceUpdate(ApplicationSettings.getPref(AppConstants.USER_ID, ""),Utils.getDeviceIMEI(UserUpdateService.this));
		//System.out.println("userDeviceUpdate  result "+result);
	    }
	}).start();
	
    }
    @Override
    public void onDestroy() {
	super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
	super.onStart(intent, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
	return super.onUnbind(intent);
    }
}
