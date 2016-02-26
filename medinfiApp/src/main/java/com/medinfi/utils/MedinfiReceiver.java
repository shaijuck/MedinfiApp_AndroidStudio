package com.medinfi.utils;

import com.appflood.AFReferralReceiver;
import com.mobileapptracker.Tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MedinfiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Tracker tracker = new Tracker();
		tracker.onReceive(context, intent);
		
		AFReferralReceiver appFloodReceiver = new AFReferralReceiver();
		appFloodReceiver.onReceive(context, intent);
		
		

	}

}
