package com.medinfi.main;

import java.util.HashMap;

import android.app.Application;
import android.util.Log;

import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MedInfiApplication extends Application {
	private static MedInfiApplication appInstance;
	@Override
	public void onCreate() {
		appInstance = this;
		super.onCreate();
		this.initializeInstance();
		// UNIVERSAL IMAGE LOADER SETUP
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true).cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.discCacheSize(100 * 1024 * 1024).build();

		ImageLoader.getInstance().init(config);
		// END - UNIVERSAL IMAGE LOADER SETUP
	}

	protected void initializeInstance() {
		Log.e("Inside Application", "MedinfiApplication");

	}

	public static MedInfiApplication getAppInstance() {
		if (appInstance == null)
			throw new NullPointerException("Application is not initialized");
		return appInstance;
	}

	public String getAppName() {
		return "Medichek";
	}
	
}
