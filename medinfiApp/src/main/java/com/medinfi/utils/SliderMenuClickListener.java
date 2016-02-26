package com.medinfi.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.ContactUs;
import com.medinfi.FavouriteActivity;
import com.medinfi.HomeScreen;
import com.medinfi.HospitalsActivity;
import com.medinfi.LoadUrlActivity;
import com.medinfi.MedicalReportsActivity;
import com.medinfi.R;
import com.medinfi.RegisterActivity;
import com.medinfi.ReviewsActivity;
import com.medinfi.SplashActivity;
import com.medinfi.SuggestDoctorHospitalActivity;
import com.medinfi.adapters.MenuItemListAdapter;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.main.AppConstants;

public class SliderMenuClickListener implements ListView.OnItemClickListener {
    private Context context;
    private DrawerLayout drawableLayout;
    private ListView mDrawerList;
    private ArrayList<MenuItemInfo> menuItemInfo;
    private MenuItemListAdapter menuItemListAdapter;
    private ImageView imgBack;
    private String callingScreen;
    private EasyTracker easyTracker = null;

    public SliderMenuClickListener(Context context, DrawerLayout drawableLayout, ListView mDrawerList, ArrayList<MenuItemInfo> menuItemInfo,
	    MenuItemListAdapter menuItemListAdapter, ImageView imgBack, String callingScreen) {
	this.context = context;
	this.drawableLayout = drawableLayout;
	this.mDrawerList = mDrawerList;
	this.menuItemInfo = menuItemInfo;
	this.menuItemListAdapter = menuItemListAdapter;
	this.imgBack = imgBack;
	this.callingScreen = callingScreen;
	easyTracker = EasyTracker.getInstance(context);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	drawableLayout.closeDrawer(mDrawerList);
	imgBack.setImageResource(R.drawable.menu_icon_closed);
	final MenuItemInfo menuInfo = (MenuItemInfo) parent.getItemAtPosition(position);

	new Handler().postDelayed(new Runnable() {
	    @Override
	    public void run() {

		if (menuInfo.getItemName().equalsIgnoreCase("Home")) {
		    if (callingScreen != null
			    && (callingScreen.equalsIgnoreCase("SuggestionMenu") || callingScreen.equalsIgnoreCase("FavouriteMenu")
				    || callingScreen.equalsIgnoreCase("FeedBackMenu") || callingScreen.equalsIgnoreCase("MedicalRecordMenu") || callingScreen
					.equalsIgnoreCase("ReviewsMenu"))) {
			finishActivities();
			//HomeScreen.getInstance().setHomeScreenVisible();
			easyTracker.send(MapBuilder.createEvent("Menu", callingScreen,callingScreen, null).build());
		    } else if (callingScreen != null && callingScreen.equalsIgnoreCase("MenuList")) {
			//HomeScreen.getInstance().setHomeScreenVisible();
			easyTracker.send(MapBuilder.createEvent("Home", "Menu", "Menu", null).build());
		    }
		} else if ((callingScreen != null && !callingScreen.equalsIgnoreCase("FavouriteMenu"))
			&& menuInfo.getItemName().equalsIgnoreCase("Saves")) {
		    startSavesScreen();
		} else if ((callingScreen != null && !callingScreen.equalsIgnoreCase("SuggestionMenu"))
			&& menuInfo.getItemName().equalsIgnoreCase("Suggest doctor/hospital")) {
		    startSuggetionActivity();
		} else if (menuInfo.getItemName().equalsIgnoreCase("Sign In") || menuInfo.getItemName().equalsIgnoreCase("Sign Out")) {
		    if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
			    && ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
			easyTracker.send(MapBuilder.createEvent("Menu", "Sign Out","Sign Out", null).build());
			ApplicationSettings.putPref(AppConstants.REGISTERED_USER, false);
			ApplicationSettings.putPref(AppConstants.USER_ID, "");
			menuItemListAdapter = new MenuItemListAdapter(context, menuItemInfo);
			mDrawerList.setAdapter(menuItemListAdapter);
			if (callingScreen != null && callingScreen.equalsIgnoreCase("MenuList")) {
			   // HomeScreen.getInstance().setHomeScreenVisible();
			} else {
			    finishActivities();
			  //  HomeScreen.getInstance().setHomeScreenVisible();
			}

		    } else {
			easyTracker.send(MapBuilder.createEvent("Menu", "Sign In","Sign In", null).build());
			menuItemListAdapter = new MenuItemListAdapter(context, menuItemInfo);
			mDrawerList.setAdapter(menuItemListAdapter);
			Intent intent = new Intent(context, RegisterActivity.class);
			intent.putExtra("CALLING_SCREEN", callingScreen);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
		    }
		} else if (menuInfo.getItemName().equalsIgnoreCase("Rate the App")) {
		    rateTheApp();
		} else if (menuInfo.getItemName().equalsIgnoreCase("Terms of Use")) {
		    easyTracker.send(MapBuilder.createEvent("Menu", "Terms of Use","Terms of Use", null).build());
		    context.startActivity(new Intent(context, LoadUrlActivity.class).putExtra("key", AppConstants.TERMS_BASE_URL).putExtra("title",
			    "TOU"));
		} else if (menuInfo.getItemName().equalsIgnoreCase("Privacy Policy")) {
		    easyTracker.send(MapBuilder.createEvent("Menu", "Privacy Policy","Privacy Policy", null).build());
		    context.startActivity(new Intent(context, LoadUrlActivity.class).putExtra("key", AppConstants.PRIVACY_BASE_URL).putExtra("title",
			    "PP"));
		} else if ((callingScreen != null && !callingScreen.equalsIgnoreCase("FeedBackMenu"))
			&& menuInfo.getItemName().equalsIgnoreCase("Feedback")) {
		    startFeedBackActivity();
		} else if ((callingScreen != null && !callingScreen.equalsIgnoreCase("MedicalRecordMenu"))
			&& menuInfo.getItemName().equalsIgnoreCase("Parents Reports")) {
		    startMedicalRecordsActivity();
		} else if ((callingScreen != null && !callingScreen.equalsIgnoreCase("ReviewsMenu"))
			&& menuInfo.getItemName().equalsIgnoreCase("Reviews")) {
		    startReviewsScreen();
		}
	    }
	}, 100);

    }

    private void startFeedBackActivity() {
	easyTracker.send(MapBuilder.createEvent("Menu", "Feedback","Feedback", null).build());
	if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
		&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
	    if (ContactUs.getInstance() != null) {
		ContactUs.getInstance().finish();
	    }
	    Intent intent = new Intent(context, ContactUs.class);
	    context.startActivity(intent);
	} else {
	    Intent intent = new Intent(context, RegisterActivity.class);
	    intent.putExtra("CALLING_SCREEN", AppConstants.CALLING_SCREEN_SUBMIT_FEEDBACK);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    context.startActivity(intent);
	}

    }

    private void startSavesScreen() {
	easyTracker.send(MapBuilder.createEvent("Menu", "Save","Save", null).build());
	if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
		&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
	    if (FavouriteActivity.getInstance() != null) {
		FavouriteActivity.getInstance().finish();
	    }
	    Intent intent = new Intent(context, FavouriteActivity.class);
	    context.startActivity(intent);
	} else {
	    Intent intent = new Intent(context, RegisterActivity.class);
	    intent.putExtra("CALLING_SCREEN", "SaveMenu");
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    context.startActivity(intent);
	}
    }

    private void startHomeScreen() {	
	ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE, false);
    ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
    if (HospitalsActivity.getInstance() != null) {
	HospitalsActivity.getInstance().finish();
    }
    context.startActivity(new Intent(context, HospitalsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
    
    

    private void rateTheApp() {
	easyTracker.send(MapBuilder.createEvent("Menu", "Rate the App","Rate the App", null).build());
	Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
	Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
	try {
	    context.startActivity(goToMarket);
	} catch (ActivityNotFoundException e) {
	    context.startActivity(new Intent(Intent.ACTION_VIEW,
		    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
	}
    }

    private void startSuggetionActivity() {
	easyTracker.send(MapBuilder.createEvent("Menu", "Suggest Doctor/Hospital","Suggest Doctor/Hospital", null).build());
	if (SuggestDoctorHospitalActivity.getInstance() != null) {
	    SuggestDoctorHospitalActivity.getInstance().finish();
	}
	Intent intent = new Intent(context, SuggestDoctorHospitalActivity.class);
	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	context.startActivity(intent);
    }

    private void startMedicalRecordsActivity() {
	Utils.isMedDadSelected = false;
	easyTracker.send(MapBuilder.createEvent("Menu", "Parent Reports","Parent Reports", null).build());
	if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
		&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
	    if (MedicalReportsActivity.getInstance() != null) {
		MedicalReportsActivity.getInstance().finish();
	    }
	    Intent intent = new Intent(context, MedicalReportsActivity.class);
	    context.startActivity(intent);
	} else {
	    Intent intent = new Intent(context, RegisterActivity.class);
	    intent.putExtra("CALLING_SCREEN", "MedicalRecordMenu");
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    context.startActivity(intent);
	}
	
    }

    private void startReviewsScreen() {
	easyTracker.send(MapBuilder.createEvent("Menu", "Reviews","Reviews", null).build());
	if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
		&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
	    if (ReviewsActivity.getInstance() != null) {
		ReviewsActivity.getInstance().finish();
	    }
	    Intent intent = new Intent(context, ReviewsActivity.class);
	    context.startActivity(intent);
	} else {
	    Intent intent = new Intent(context, RegisterActivity.class);
	    intent.putExtra("CALLING_SCREEN", "ReviewsMenu");
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    context.startActivity(intent);
	}
    }

    private void finishActivities() {
	if (SuggestDoctorHospitalActivity.getInstance() != null) {
	    SuggestDoctorHospitalActivity.getInstance().finish();
	}
	if (FavouriteActivity.getInstance() != null) {
	    FavouriteActivity.getInstance().finish();
	}
	if (ContactUs.getInstance() != null) {
	    ContactUs.getInstance().finish();
	}
	if (MedicalReportsActivity.getInstance() != null) {
	    MedicalReportsActivity.getInstance().finish();
	}
	if (ReviewsActivity.getInstance() != null) {
	    ReviewsActivity.getInstance().finish();
	}
    }
}