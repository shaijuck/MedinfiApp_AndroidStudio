package com.medinfi.utils;

import com.medinfi.main.AppConstants;

public interface Config {

	// used to share GCM regId with application server - using php app server
	//static final String APP_SERVER_URL = "http://192.168.1.17/gcm/gcm.php?shareRegId=1";
	static final String APP_SERVER_URL =AppConstants.BASE_URL + "api/SaveAppDeviceDetails";
	// GCM server using java
	// static final String APP_SERVER_URL =
	// "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "1042658319446";
	static final String MESSAGE_KEY = "msg";
	static final String MESSAGE = "m";
	static final String EVENT = "event";
	static final String TITLE = "title";
	static final String DOCTORID = "dId";
	static final String HOSPITALID = "hId";
	static final String SPEC = "spec";

}
