package com.medinfi.locationservice;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.LocationUpdateActivity;
import com.medinfi.R;
import com.medinfi.SplashActivity;
import com.medinfi.main.AppConstants;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.Utils;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location; 
	double latitude; 
	double longitude; 
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	protected LocationManager locationManager;
	private EasyTracker easyTracker = null;
	public GPSTracker(Context context) {
		this.mContext = context;
		getLocation();
		easyTracker = EasyTracker.getInstance(mContext);
	}

    public Location getLocation() {
	try {
	    locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
	    isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	    if (!isGPSEnabled && !isNetworkEnabled) {
		// no network provider is enabled
		customDialog();
		ApplicationSettings.putPref(AppConstants.GPRS_STATUS, "0");
		Utils.isGPRSON = false;
	    } else {
		/*try {
		    turnGPSOff(mContext);
		    turnGPSOn(mContext);
		} catch (Exception e) {
		    e.printStackTrace();
		}*/
		
		ApplicationSettings.putPref(AppConstants.GPRS_STATUS, "1");
		this.canGetLocation = true;
		if (isNetworkEnabled) {
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
			    this);
		    if (locationManager != null) {
			//location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			 location = getLastKnownLocation();
			if (location != null) {
			    latitude = location.getLatitude();
			    longitude = location.getLongitude();
			}
		    }
		}
		// if GPS Enabled get lat/long using GPS Services
		if (isGPSEnabled) {
		    if (location == null) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
				this);
			if (locationManager != null) {
			    //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			    location = getLastKnownLocation();
			    if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			    }
			}
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return location;
    }
    
    public void turnGPSOn(Context context) {
	try {
	    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if (!provider.contains("gps")) { // if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3"));
	        context.sendBroadcast(poke);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
     
    public void turnGPSOff(Context context) {
	try {
	    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if (provider.contains("gps")) {
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3"));
	        context.sendBroadcast(poke);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }    
    
    private Location getLastKnownLocation() {
	    List<String> providers = locationManager.getProviders(true);
	    Location bestLocation = null;
	    for (String provider : providers) {
	        Location l = locationManager.getLastKnownLocation(provider);
	        if (l == null) {
	            continue;
	        }
	        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
	            // Found best last known location: %s", l);
	            bestLocation = l;
	        }
	    }
	    return bestLocation;
	}
	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * *//*
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("GPS is settings");
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}*/
	
	 /**
	     * Create custom dialog
	     */
	    private void customDialog(){
	        final Dialog dialog = new Dialog(mContext);
	        Typeface tfRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoRegular.ttf");
	        Typeface tfRobotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoLight.ttf");
	        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	        
	     // retrieve display dimensions
	        Rect displayRectangle = new Rect();
	        Window window = ((Activity) mContext).getWindow();
	        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	     // inflate and adjust layout
	        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View layout = inflater.inflate(R.layout.gprs_alert_dialog, null);
	        layout.setMinimumWidth((int)(displayRectangle.width() * 0.75f));
	        //layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

	       // dialog.setView(layout);
	        Utils.isGPRSON = false;
	        dialog.setContentView(layout);
	       // dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	        dialog.setCancelable(false);
	        dialog.show();
	        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
	        btnOk.setTypeface(tfRegular);
	        ((TextView)dialog.findViewById(R.id.textDialog)).setTypeface(tfRegular);
	        ((TextView)dialog.findViewById(R.id.textDialogMsg)).setTypeface(tfRegular);
	        btnOk.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	        	easyTracker.send(MapBuilder.createEvent("Splash", "Turn On GPS", "Turn On GPS", null).build());
	        	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			mContext.startActivity(intent);
			Utils.isGPRSON = true;
			SplashActivity.isEventPressed = true;
	                dialog.dismiss();
	        	
	            }
	        });
	        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
	        btnCancel.setTypeface(tfRegular);
	        btnCancel.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	        	ApplicationSettings.putPref(AppConstants.LOCATION_SELECTED_MANUAL, true);
			ApplicationSettings.putPref(AppConstants.LOCATION_SELECTED_MANUAL_GPRS, true);
			Intent intent = new Intent(mContext, LocationUpdateActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
			easyTracker.send(MapBuilder.createEvent("Splash", "Cancel GPS", "Cancel GPS", null).build());
			if(SplashActivity.getInstance()!=null){
			    SplashActivity.getInstance().finish();
			}
			Utils.isGPRSON = false;
			SplashActivity.isEventPressed = true;
	                dialog.dismiss();
	            }
	        });
	        dialog.setOnKeyListener(new OnKeyListener() {
		    
		    @Override
		    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
			    SplashActivity.getInstance().finish();
	                  //  dialog.dismiss();
	                }
	                return true;
		    }
		});
	    }

	@Override
	public void onLocationChanged(Location location) {
	    this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

    public String getAddressLine(Context context) {
	String addressLine = null;
	List<Address> addresses = getGeocoderAddress(context);
	if (addresses != null) {
	    try {
		try {
		    addressLine = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1) + "," 
		                  + addresses.get(0).getAddressLine(2)+","+addresses.get(0).getAddressLine(3);
		} catch (Exception e) {
		    addressLine = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1) + "," 
		                  + addresses.get(0).getAddressLine(2);
		}
		if (addresses.get(0).getSubLocality() != null && addresses.get(0).getLocality() != null) {
		    ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION, addresses.get(0).getLocality().toString());
		    ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY, addresses.get(0).getSubLocality().toString());
		    ApplicationSettings.putPref(AppConstants.GPRS_STATUS, "1");
		    ApplicationSettings.putPref(AppConstants.CURRENT_USER_LOCATION_AUTO, addresses.get(0).getLocality().toString());
		    ApplicationSettings.putPref(AppConstants.CURRENT_USER_SUB_LOCALITY_AUTO, addresses.get(0).getSubLocality().toString());
		    ApplicationSettings.putPref(AppConstants.LOCALITY_ADDRESS, addressLine.replace("null,", ""));
		    // System.out.println("GPSTracker.getAddressLine() addressLine "+addressLine);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    return addressLine;
	} else {
	    return null;
	}
    }

    List<Address> getGeocoderAddress(Context context) {
	if (location != null) {
	    Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
	    try {
		List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
		return addresses;
	    } catch (IOException e) {
		return null;
	    }
	}
	return null;
    }
}
