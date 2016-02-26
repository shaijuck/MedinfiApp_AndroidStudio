package com.medinfi.utils;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.medinfi.DoctorDetailsActivity;
import com.medinfi.HospitalDetailsActivity;
import com.medinfi.HospitalsActivity;
import com.medinfi.ProfileActivity;
import com.medinfi.R;
import com.medinfi.SplashActivity;
import com.medinfi.main.AppConstants;
public class GCMNotificationIntentService  extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
				//sendNotification(extras);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "+ extras.toString());
				//sendNotification(extras);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				sendNotification(""+extras.get(Config.MESSAGE_KEY));
				//sendNotification(extras);
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String extras) {
		//Log.d(TAG, "Preparing to send notification...: " + msg);
		String msg="";
		String title="";
		String event="";
		String docId="";
		String hosId="";
		String spec="";
		try{
		JSONObject obj=new JSONObject(extras);
		
		 msg=""+obj.get(Config.MESSAGE);
		 title=""+obj.get(Config.TITLE);
		 event=""+obj.get(Config.EVENT);
		 docId=""+obj.get(Config.DOCTORID);
		 hosId=""+obj.get(Config.HOSPITALID);
		 spec=""+obj.get(Config.SPEC);
		}
		catch(Exception e){}
		
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent contentIntent = null;
		if(event.equalsIgnoreCase("rate_doc") || event.equalsIgnoreCase("call_doctor"))
		{
			ApplicationSettings.putPref(AppConstants.DOCTOR_ID,docId);
			ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY,spec.toUpperCase());
			contentIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, DoctorDetailsActivity.class), 0);
		}
		else if(event.equalsIgnoreCase("rate_hos") || event.equalsIgnoreCase("suggestion_approved")
				|| event.equalsIgnoreCase("call_hospital"))
		{
			ApplicationSettings.putPref(AppConstants.HOSPITAL_ID,hosId);
		    ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY, spec.toUpperCase());
		    contentIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, HospitalDetailsActivity.class), 0);
		}
		else if(event.equalsIgnoreCase("feedback"))
		{
		    contentIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, HospitalsActivity.class), 0);
		}
		else if(event.equalsIgnoreCase("rate_app"))
		{
			Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
			try {
			contentIntent = PendingIntent.getActivity(this, 0,
					new Intent(Intent.ACTION_VIEW, uri), 0);
			} catch (ActivityNotFoundException e) {
				contentIntent = PendingIntent.getActivity(this, 0,
						new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())), 0);
			}
		}
		else{
		 contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SplashActivity.class), 0);
		}
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.app_logo)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		
		mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS);
		//mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}
}
