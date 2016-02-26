package com.medinfi.adapters;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.R;
import com.medinfi.SpecialistDoctorsActivity;
import com.medinfi.datainfo.ProfileInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CircleTransform;
import com.medinfi.utils.EnlargeImageDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/*Apdater used to load the doctor information in the listview */
public class DoctorsAdapter extends ArrayAdapter<ProfileInfo> {
    Context context;
    int layoutResourceId;
    private ArrayList<ProfileInfo> profileList = null;
    private ArrayList<ProfileInfo> arraylist;
    private ImageLoader imageLoader;
    DisplayImageOptions options;
    private Typeface tfBold, tfRegular, tfLite, tfCondensed;
    private EasyTracker easyTracker = null;
    private ProfileInfo profileInfo;
    String SpecializationId;
    public DoctorsAdapter(Context context, int layoutResourceId, ArrayList<ProfileInfo> profileList,String Specialization) {
	super(context, layoutResourceId, profileList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.profileList = profileList;
	this.arraylist = new ArrayList<ProfileInfo>();
	this.arraylist.addAll(profileList);
	this.SpecializationId=Specialization;
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

    // get the object based on array position
    @Override
    public ProfileInfo getItem(int position) {
	return profileList.get(position);
    }

    /* get the id based on array position */
    @Override
    public long getItemId(int position) {
	return position;
    }

    /*
     * add the view based on the count of arraylist set the values using getter
     * setter methods
     */
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

	    holder.txtProfileKM = (TextView) row.findViewById(R.id.km_tv);
	    holder.txtProfileDistance.setTypeface(tfRegular);
	    holder.txtProfileKM.setTypeface(tfRegular);
	    holder.ratingLayout = (LinearLayout)row.findViewById(R.id.ratingLayout);
	    holder.overAllRating = (RatingBar)row.findViewById(R.id.overAllRating);

	    holder.profileImageView = (ImageView) row.findViewById(R.id.iv_profile);
	    holder.txtVerified = (TextView)row.findViewById(R.id.txtVerified);
	    holder.callButton = (Button) row.findViewById(R.id.btn_call);
	    holder.callButton.setOnClickListener(callButtonClickListener);
	    holder.imgVerifiedLog=(ImageView) row.findViewById(R.id.imgVerifiedLog);
	    row.setTag(holder);

	} else {
	    holder = (ProfileHolder) row.getTag();
	}

	profileInfo = profileList.get(position);
	holder.callButton.setTag(position);
	holder.txtProfileName.setText(context.getResources().getString(R.string.dr) + " " + profileInfo.getProfileName());
	
	if(!SpecializationId.equalsIgnoreCase("-999"))
	{
	if (profileInfo.getHospitalName() != null && !profileInfo.getHospitalName().equalsIgnoreCase("")
		&& !profileInfo.getHospitalName().equalsIgnoreCase("null"))
	    holder.txtProfileDesignation.setText(profileInfo.getHospitalName());
	else
	    holder.txtProfileDesignation.setText("");
	}
	else
	{
		if (profileInfo.getSpecialityName() != null && !profileInfo.getSpecialityName().equalsIgnoreCase("")
				&& !profileInfo.getSpecialityName().equalsIgnoreCase("null"))
			    holder.txtProfileDesignation.setText(profileInfo.getSpecialityName());
			else
			    holder.txtProfileDesignation.setText("");
	}
	
	if (profileInfo.getProfileRating() != null && !profileInfo.getProfileRating().equalsIgnoreCase("")
		&& !profileInfo.getProfileRating().equalsIgnoreCase("null") && !profileInfo.getProfileRating().equalsIgnoreCase(null)
		&& !profileInfo.getProfileRating().equalsIgnoreCase("0.0")) {
	    holder.ratingLayout.setVisibility(View.VISIBLE);
	    long lValue = Math.round(Double.valueOf(profileInfo.getProfileRating()));
	    holder.overAllRating.setRating(lValue);
	    holder.overAllRating.setIsIndicator(true);

	} else {
	    holder.ratingLayout.setVisibility(View.GONE);
	}
	if (profileInfo.getProfileDistance() != null && !profileInfo.getProfileDistance().equalsIgnoreCase("9999")) {
	    try {
		double rounded = (double) Math.round(Double.valueOf(profileInfo.getProfileDistance()) * 100) / 100;
		DecimalFormat df2 = new DecimalFormat("#####.#");
		holder.txtProfileDistance.setText("" + Double.valueOf(df2.format(rounded)));
		holder.txtProfileDistance.setVisibility(View.VISIBLE);
		holder.txtProfileKM.setVisibility(View.VISIBLE);
	    } catch (NumberFormatException e) {
		// TODO Auto-generated catch block
	    }
	} else {
	    holder.txtProfileKM.setVisibility(View.GONE);
	    holder.txtProfileDistance.setVisibility(View.GONE);
	}
	if (profileInfo.getProfileImage() != null && !profileInfo.getProfileImage().trim().equalsIgnoreCase("")
		&& !profileInfo.getProfileImage().trim().equalsIgnoreCase("null")
		&& !profileInfo.getProfileImage().equalsIgnoreCase(null)){
	    //imageLoader.displayImage(profileInfo.getProfileImage(), holder.profileImageView, options);
	   // Glide.with(context).load(profileInfo.getProfileImage()).placeholder(R.drawable.ic_action_person).into(holder.profileImageView);
	    holder.txtVerified.setText(context.getString(R.string.msgVisited));  
	    holder.imgVerifiedLog.setBackgroundResource(R.drawable.two_tick);
	    
	    Glide.with(context).load(profileInfo.getProfileImage()).placeholder(R.drawable.ic_action_person).transform(new CircleTransform(context)).into(holder.profileImageView);
	}
	else{
		  Glide.with(context).load(R.drawable.ic_action_person).into(holder.profileImageView);
		   // holder.profileImageView.setImageResource(R.drawable.ic_action_person);
		    holder.txtVerified.setText(context.getString(R.string.msgVerified));
		    holder.imgVerifiedLog.setBackgroundResource(R.drawable.one_tick);
	}
	   

//	holder.profileImageView.setOnClickListener(new OnClickListener() {
//
//	    @Override
//	    public void onClick(View v) {
//		if (profileList.get(position).getProfileImage() != null) {
//		   // enlargImageDialog(position);
//		    new EnlargeImageDialog(context, position,profileList).show();
//		}
//	    }
//	});
	return row;
    }

    // holder which holds the view associated to the adapter
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
	ImageView imgVerifiedLog;
    }

    // click listener called when user clicks on the list item call button
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

		easyTracker.send(MapBuilder.createEvent("Doctor List", "Call Doctor", profileList.get(position).getProfileName(), null).build());

		intent.setData(Uri.parse("tel:" + phoneno));
		context.startActivity(intent);
	    } else {
		Toast.makeText(context, "Phone number does not exist", 1000).show();
	    }
	}

    };
}

