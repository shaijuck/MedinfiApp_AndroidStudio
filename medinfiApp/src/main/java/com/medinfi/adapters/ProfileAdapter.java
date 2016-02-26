package com.medinfi.adapters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.R;
import com.medinfi.datainfo.ProfileInfo;
import com.medinfi.utils.EnlargeImageDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileAdapter extends ArrayAdapter<ProfileInfo> {
    Context context;
    int layoutResourceId;
    private ArrayList<ProfileInfo> profileList;
    private ArrayList<ProfileInfo> arraylist;
    private ImageLoader imageLoader;
    DisplayImageOptions options;
    private Typeface tfBold, tfRegular, tfLite, tfCondensed;
    private EasyTracker easyTracker = null;
    ProfileInfo profileInfo;

    public ProfileAdapter(Context context, int layoutResourceId, ArrayList<ProfileInfo> profileList) {
	super(context, layoutResourceId, profileList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.profileList = profileList;
	this.arraylist = new ArrayList<ProfileInfo>();
	this.arraylist.addAll(profileList);
	easyTracker = EasyTracker.getInstance(context);
	imageLoader = ImageLoader.getInstance();
	options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.ic_action_person).showImageOnFail(R.drawable.ic_action_person)
		.showImageOnLoading(R.drawable.ic_action_person).build();
	tfBold = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoBold.ttf");
	tfLite = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoLight.ttf");
	tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	tfCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed.ttf");
    }

    @Override
    public ProfileInfo getItem(int position) {
	return profileList.get(position);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
	View row = convertView;
	ProfileHolder holder = null;

	if (row == null) {
	    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    row = inflater.inflate(layoutResourceId, parent, false);

	    holder = new ProfileHolder();
	    holder.txtProfileName = (TextView) row.findViewById(R.id.tv_profilename);

	    holder.txtProfileName.setTypeface(tfRegular);
	    holder.txtProfileDesignation = (TextView) row.findViewById(R.id.tv_profiledesignation);
	    holder.txtProfileDesignation.setTypeface(tfLite);
	    holder.txtProfileDistance = (TextView) row.findViewById(R.id.tv_profiledistance);
	    holder.txtProfileDistance.setTypeface(tfRegular);
	    holder.txtProfileKM = (TextView) row.findViewById(R.id.km_tv);
	    holder.txtProfileKM.setTypeface(tfRegular);
	    holder.ratingLayout = (LinearLayout)row.findViewById(R.id.ratingLayout);
	    holder.overAllRating = (RatingBar)row.findViewById(R.id.overAllRating);
	    
	    holder.profileImageView = (ImageView) row.findViewById(R.id.iv_profile);
	    holder.txtVerified = (TextView)row.findViewById(R.id.txtVerified);
	    holder.callButton = (Button) row.findViewById(R.id.btn_call);
	    holder.callButton.setOnClickListener(callButtonClickListener);
	    row.setTag(holder);

	} else {
	    holder = (ProfileHolder) row.getTag();
	}

	profileInfo = profileList.get(position);
	holder.callButton.setTag(position);

	holder.txtProfileName.setText(context.getResources().getString(R.string.dr) + " " +profileInfo.getProfileName());
	if (profileInfo.getHospitalName() != null && !profileInfo.getHospitalName().equalsIgnoreCase("")
		&& !profileInfo.getHospitalName().equalsIgnoreCase("null"))
	    holder.txtProfileDesignation.setText(profileInfo.getHospitalName());
	else
	    holder.txtProfileDesignation.setText("");
	if (profileInfo.getProfileRating() != null && !profileInfo.getProfileRating().equalsIgnoreCase("")
		&& !profileInfo.getProfileRating().equalsIgnoreCase("null") && !profileInfo.getProfileRating().equalsIgnoreCase("0.0")
		&& !profileInfo.getProfileRating().equalsIgnoreCase(null)) {
	    holder.ratingLayout.setVisibility(View.VISIBLE);
	    long lValue = Math.round(Double.valueOf(profileInfo.getProfileRating()));
	    holder.overAllRating.setRating(lValue);
	    holder.overAllRating.setIsIndicator(true);
	} else {
	    holder.ratingLayout.setVisibility(View.GONE);
	}
	if (profileInfo.getProfileDistance() != null && !profileInfo.getProfileDistance().equalsIgnoreCase("99999")
		&& !profileInfo.getProfileDistance().equalsIgnoreCase("0") && !profileInfo.getProfileDistance().equalsIgnoreCase("0.0")) {
	    try {
		double rounded = (double) Math.round(Double.valueOf(profileInfo.getProfileDistance()) * 100) / 100;

		DecimalFormat df2 = new DecimalFormat("#####.#");
		holder.txtProfileDistance.setText("" + Double.valueOf(df2.format(rounded)));
		//getDistance(holder);
		holder.txtProfileDistance.setVisibility(View.VISIBLE);
		holder.txtProfileKM.setVisibility(View.VISIBLE);
	    } catch (NumberFormatException e) {
		// TODO Auto-generated catch block
	    }
	} else {
	    holder.txtProfileKM.setVisibility(View.GONE);
	    holder.txtProfileDistance.setVisibility(View.GONE);
	}
	// holder.callButton.setTag(profileInfo);
	if (profileInfo.getProfileImage() != null && !profileInfo.getProfileImage().trim().equalsIgnoreCase("")
		&& !profileInfo.getProfileImage().trim().equalsIgnoreCase(null) && !profileInfo.getProfileImage().trim().equalsIgnoreCase("null")){
	   // imageLoader.displayImage(profileInfo.getProfileImage(), holder.profileImageView, options);
	    Glide.with(context).load(profileInfo.getProfileImage()).placeholder(R.drawable.ic_action_person).into(holder.profileImageView); 
	    holder.txtVerified.setText(context.getString(R.string.msgVisited));  
	}
	else{
		 Glide.with(context).load(R.drawable.ic_action_person).into(holder.profileImageView); 
		   // holder.profileImageView.setImageResource(R.drawable.ic_action_person);
	    holder.txtVerified.setText(context.getString(R.string.msgVerified));
	}
	   

	holder.profileImageView.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (profileList.get(position).getProfileImage() != null) {
		  //  enlargImageDialog(position);
		    new EnlargeImageDialog(context, position,profileList).show();
		}
	    }
	});
	return row;
    }

    static class ProfileHolder {
	TextView txtProfileName;
	TextView txtProfileDesignation;
	TextView txtProfileDistance;
	//TextView txtProfileRatings;
	TextView txtProfileKM;
	//TextView txtProfileRated;
	ImageView profileImageView;
	Button callButton;
	RatingBar overAllRating;
	LinearLayout ratingLayout;
	TextView txtVerified;
    }

    /*
     * // Filter Class public void filter(String charText) { charText =
     * charText.toLowerCase(Locale.getDefault()); profileList.clear(); if
     * (charText.length() == 0) { profileList.addAll(arraylist);
     * ApplicationSettings.putPref(AppConstants.LAST_COUNT,
     * ""+arraylist.size()); } else {
     * 
     * 
     * for (ProfileInfo wp : arraylist) { if
     * (wp.getProfileName().toLowerCase(Locale.getDefault())
     * .contains(charText)) { profileList.add(wp); }
     * 
     * ApplicationSettings.putPref(AppConstants.LAST_COUNT,
     * ""+profileList.size()); } } notifyDataSetChanged();
     * GeneralPhysicansActivity.CheckEnabling(); }
     */

    private OnClickListener callButtonClickListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
	    int position = (Integer) v.getTag();
	    if (profileList.get(position).getProfilePhoneNumber() != null
		    && !profileList.get(position).getProfilePhoneNumber().equalsIgnoreCase("null")
		    && !profileList.get(position).getProfilePhoneNumber().equalsIgnoreCase(null)
		    && !profileList.get(position).getProfilePhoneNumber().equalsIgnoreCase("6855")) {

		Intent intent = new Intent(Intent.ACTION_DIAL);

		String phoneno = profileList.get(position).getProfilePhoneNumber().toString().trim();
		if (phoneno.startsWith("0")) {
		    phoneno = profileList.get(position).getProfilePhoneNumber().toString().trim();
		} else if (phoneno.startsWith("91")) {
		    phoneno = profileList.get(position).getProfilePhoneNumber().toString().trim();
		} else if (phoneno.startsWith("+91")) {
		    phoneno = profileList.get(position).getProfilePhoneNumber().toString().trim();
		} else {
		    phoneno = "0" + profileList.get(position).getProfilePhoneNumber().toString().trim();
		}

		/*
		 * easyTracker.send(MapBuilder.createEvent(
		 * profileList.get(position).getProfileName(),
		 * "Call Button Clicked",
		 * profileList.get(position).getProfilePhoneNumber(), null)
		 * .build());
		 */

		easyTracker.send(MapBuilder.createEvent("Doctor List", "Call Doctor",
			"GENERAL PHYSICIANS" + ", " + profileList.get(position).getProfileName(), null).build());
		intent.setData(Uri.parse("tel:" + phoneno));
		context.startActivity(intent);
	    } else {
		Toast.makeText(context, "Phone number does not exist", 1000).show();
	    }
	}

    };
    
    public void getDistance(ProfileHolder holder) {
    	String result = "";
    	 String json = "";
    	 String distKms="";
    	 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
         StrictMode.setThreadPolicy(policy);
    	try {
    	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
    	    String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=54.406505,18.67708&destinations=54.446251,18.570993&mode=driving&language=en-EN&sensor=false";

//    	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
//    	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
//    	    postparams.add(new BasicNameValuePair("city", searhCityKeyword));
//    	    String paramString = URLEncodedUtils.format(postparams, "utf-8");
//
//    	    url += "?" + paramString;
    	    HttpGet httpGet = new HttpGet(url);

    	    HttpResponse httpResponse = httpClient1.execute(httpGet);
    	    HttpEntity httpEntity = httpResponse.getEntity();
    	    InputStream is = httpEntity.getContent();

    	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    	    int nRead;
    	    byte[] bytess = new byte[16384];

    	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
    		buffer.write(bytess, 0, nRead);
    	    }

    	    buffer.flush();

    	    result = buffer.toString();

    	} catch (UnsupportedEncodingException e) {
    	    e.printStackTrace();
    	} catch (ClientProtocolException e) {
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    	try {
    	    StringBuilder sb = new StringBuilder();
    	    sb.append(result);
    	    json = sb.toString();
    	} catch (Exception e) {
    	}
    //	return json;
    	try{
    	 JSONObject jobj=new JSONObject(json);
    	 JSONArray jArr=jobj.getJSONArray("rows");
    	 JSONObject jobj2=jArr.getJSONObject(0);
    	 JSONArray jArr2=jobj2.getJSONArray("elements");
    	 JSONObject jobj3=jArr2.getJSONObject(0);
    	 JSONObject jobj4=jobj3.getJSONObject("distance");
    	 distKms=jobj4.getString("text");
    	 holder.txtProfileDistance.setText("" + distKms);
    	}
    	catch(Exception e){}
        }
    

    
}