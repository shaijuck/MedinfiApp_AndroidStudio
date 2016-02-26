package com.medinfi.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.R;
import com.medinfi.datainfo.DoctorDetailInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/*Apdater used to load the doctor information in the listview */
public class DoctorDetailsAdapter extends ArrayAdapter<DoctorDetailInfo> {
    Context context;
    int layoutResourceId;
    private List<DoctorDetailInfo> doctorList = null;
    private ArrayList<DoctorDetailInfo> arraylist;
    private ImageLoader imageLoader;
    DisplayImageOptions options;
    public DoctorDetailInfo docInfo;
    String strHospitalLocality;
    String strHospitalCity;
    private Typeface tfBold, tfRegular, tfLite, tfCondensed;
    private EasyTracker easyTracker = null;

    public DoctorDetailsAdapter(Context context, int layoutResourceId, ArrayList<DoctorDetailInfo> doctorList, String hospitalLocality,
	    String hospitalCity) {
	super(context, layoutResourceId, doctorList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.doctorList = doctorList;
	strHospitalLocality = hospitalLocality;
	strHospitalCity = hospitalCity;
	this.arraylist = new ArrayList<DoctorDetailInfo>();
	this.arraylist.addAll(doctorList);
	imageLoader = ImageLoader.getInstance();
	easyTracker = EasyTracker.getInstance(context);
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
    public DoctorDetailInfo getItem(int position) {
	return doctorList.get(position);
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
	DoctorHolder holder = null;

	if (row == null) {
	    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    row = inflater.inflate(layoutResourceId, parent, false);

	    holder = new DoctorHolder();
	    holder.txtDoctorName = (TextView) row.findViewById(R.id.tv_profilename);

	    holder.txtDoctorName.setTypeface(tfRegular);
	    holder.txtDoctorDesignation = (TextView) row.findViewById(R.id.tv_profiledesignation);

	    holder.txtDoctorDesignation.setTypeface(tfLite);
	    holder.txtTotalExperiance = (TextView) row.findViewById(R.id.txtTotalExperiance);
	    holder.txtTotalExperiance.setTypeface(tfLite);
	    holder.txtDoctorDistance = (TextView) row.findViewById(R.id.tv_profiledistance);

	    holder.txtDoctorDistance.setTypeface(tfRegular);

	    // holder.txtDoctorRatings = (TextView)
	    // row.findViewById(R.id.profile_rating);
	    // holder.txtDoctorRatings.setTypeface(tfCondensed);

	    holder.DoctorImageView = (ImageView) row.findViewById(R.id.iv_profile);
	    holder.callButton = (Button) row.findViewById(R.id.btn_call);
	    // holder.rateLinearLayout= (LinearLayout)
	    // row.findViewById(R.id.doc_rate_layout);

	    holder.ratingLayout = (LinearLayout) row.findViewById(R.id.ratingLayout);
	    holder.overAllRating = (RatingBar) row.findViewById(R.id.overAllRating);

	    row.setTag(holder);

	} else {
	    holder = (DoctorHolder) row.getTag();
	}

	docInfo = doctorList.get(position);

	holder.txtDoctorName.setText(context.getResources().getString(R.string.dr) + " " + docInfo.getDoctorName());
	try {
	    String strValue = "";boolean isTrue = true;
	    if(docInfo.getDoctorType().trim()!=null && !docInfo.getDoctorType().trim().equalsIgnoreCase("")
	    	    && !docInfo.getDoctorType().trim().equalsIgnoreCase("null")
	    	    && !docInfo.getDoctorType().trim().equalsIgnoreCase(null)){
		isTrue = false;
		strValue = docInfo.getDoctorType().trim();
	    }
	    if (docInfo.getDoctorQualification().trim()!= null && !docInfo.getDoctorQualification().trim().equalsIgnoreCase("")
	    	&& !docInfo.getDoctorQualification().trim().equalsIgnoreCase("null")
	    	&& !docInfo.getDoctorQualification().trim().equalsIgnoreCase(null)){
		if(!isTrue){
		    strValue = strValue+" - "+docInfo.getDoctorQualification().trim();
		}else{
		    strValue = docInfo.getDoctorQualification().trim();
		}
		isTrue = false;
	    }if(!isTrue){
		holder.txtDoctorDesignation.setVisibility(View.VISIBLE);
		holder.txtDoctorDesignation.setText(strValue);
	    }else{
		holder.txtDoctorDesignation.setVisibility(View.GONE);
	    }
	    
	} catch (Exception e1) {
	    e1.printStackTrace();
	}
	   
	if (docInfo.getDoctorAverage() != null && !docInfo.getDoctorAverage().equalsIgnoreCase("")
		&& !docInfo.getDoctorAverage().equalsIgnoreCase("null") && !docInfo.getDoctorAverage().equalsIgnoreCase(null)
		&& !docInfo.getDoctorAverage().equalsIgnoreCase("0.0")) {
	    holder.ratingLayout.setVisibility(View.GONE);
	    long lValue = Math.round(Double.valueOf(docInfo.getDoctorAverage()));
	    holder.overAllRating.setRating(lValue);
	    holder.overAllRating.setIsIndicator(true);
	} else {
	    holder.ratingLayout.setVisibility(View.GONE);
	}
	
	    String doctExperiance = docInfo.getDoctorTotalExperience().trim();
	    if(doctExperiance!=null
		    && !doctExperiance.equalsIgnoreCase("")
		    && !doctExperiance.equalsIgnoreCase("null")
		    && !doctExperiance.equalsIgnoreCase(null) 
		    && doctExperiance.equalsIgnoreCase("1")
		    || doctExperiance.equalsIgnoreCase("01")
		    || doctExperiance.equalsIgnoreCase("001")
		    && !doctExperiance.equalsIgnoreCase("0")){
		holder.txtTotalExperiance.setVisibility(View.VISIBLE);
		holder.txtTotalExperiance.setText(doctExperiance+" "+context.getString(R.string.doctorExp));
	    }else if(doctExperiance!=null 
		    && !doctExperiance.equalsIgnoreCase("")
		    && !doctExperiance.equalsIgnoreCase("null")
		    && !doctExperiance.equalsIgnoreCase(null) 
		    && !doctExperiance.equalsIgnoreCase("1")
		    && !doctExperiance.equalsIgnoreCase("0") 
		    && !doctExperiance.equalsIgnoreCase("00") 
		    && !doctExperiance.equalsIgnoreCase("000")) {
		boolean isTrue = true;
		try {
		    int iValue = Integer.parseInt(doctExperiance);
		} catch (NumberFormatException e) {
		    isTrue = false;
		}
		if (isTrue) {
		    holder.txtTotalExperiance.setVisibility(View.VISIBLE);
		    holder.txtTotalExperiance.setText(doctExperiance + " " +context.getString(R.string.doctorExps));
		}else{
		    holder.txtTotalExperiance.setVisibility(View.GONE);
		}

	    }else{
		holder.txtTotalExperiance.setVisibility(View.GONE);
	    }
	
	/*
	 * double rounded = (double) Math.round(Double.valueOf(profileInfo.getD)
	 * * 100) / 100; holder.txtProfileDistance.setText(""+rounded);
	 */
	holder.callButton.setTag(docInfo);

	/*
	 * imageLoader.displayImage(docInfo.getDoctorPic(),
	 * holder.DoctorImageView, options);
	 */
	try {
	    if (docInfo.getDoctorPic() != null && !docInfo.getDoctorPic().equalsIgnoreCase("") && !docInfo.getDoctorPic().equalsIgnoreCase(null)
		    && !docInfo.getDoctorPic().equalsIgnoreCase("null")) {
		imageLoader.displayImage(docInfo.getDoctorPic(), holder.DoctorImageView, options);
	    } else {
		holder.DoctorImageView.setImageResource(R.drawable.ic_action_person);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	holder.callButton.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		String docName = context.getResources().getString(R.string.dr) + " " + doctorList.get(position).getDoctorName();
		easyTracker.send(MapBuilder.createEvent("Hospital Details", "Share Doctor", docName, null).build());
		WhatsAppPost(docName, doctorList.get(position).getDoctorType(), strHospitalLocality, strHospitalCity);
	    }
	});

	holder.DoctorImageView.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (doctorList.get(position).getDoctorPic() != null) {
		    enlargImageDialog(position);
		}
	    }
	});
	return row;
    }

    // holder which holds the view associated to the adapter
    static class DoctorHolder {

	TextView txtDoctorName;
	TextView txtDoctorDesignation;
	TextView txtDoctorDistance;
	// TextView txtDoctorRatings;
	ImageView DoctorImageView;
	// LinearLayout rateLinearLayout;
	Button callButton;
	RatingBar overAllRating;
	LinearLayout ratingLayout;
	TextView txtTotalExperiance;
    }

    // Filter for search
    public void filter(String charText) {
	charText = charText.toLowerCase(Locale.getDefault());
	doctorList.clear();
	if (charText.length() == 0) {
	    doctorList.addAll(arraylist);
	} else {
	    for (DoctorDetailInfo wp : arraylist) {
		if (wp.getDoctorName().toLowerCase(Locale.getDefault()).contains(charText)) {
		    doctorList.add(wp);
		}
	    }
	}
	notifyDataSetChanged();
    }

    public void WhatsAppPost(String doctorName, String docType, String hospitalLocality, String strHospitalCity) {

	PackageManager pm = context.getPackageManager();
	try {

	    Intent waIntent = new Intent(Intent.ACTION_SEND);
	    waIntent.setType("text/plain");

	    /*
	     * "Hi. It would be great if you can share your opinion on Dr Jagdish Rohira, General Physician, Apollo Hospital,Bangalore"
	     */
	    String text = "Hey. I found ";
	    if (hospitalLocality != null && hospitalLocality.length() > 1 && !hospitalLocality.equalsIgnoreCase("null") && strHospitalCity != null
		    && strHospitalCity.length() > 1 && !strHospitalCity.equalsIgnoreCase("null")) {
		text = text + doctorName + ", " + docType + ", " + hospitalLocality + ", " + strHospitalCity
			+ " using Medinfi Android Mobile App. You may also try and install it here:" + "\n\n"
			+ "https://play.google.com/store/apps/details?id=com.medinfi";
	    } else {
		text = text + doctorName + ", " + docType + " using Medinfi Android Mobile App. You may also try and install it here:" + "\n\n"
			+ "https://play.google.com/store/apps/details?id=com.medinfi";
	    }
	    PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
	    // Check if package exists or not. If not then code
	    // in catch block will be called
	    waIntent.setPackage("com.whatsapp");

	    waIntent.putExtra(Intent.EXTRA_TEXT, text);
	    context.startActivity(Intent.createChooser(waIntent, "Share with"));

	} catch (NameNotFoundException e) {
	    Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
	}

    }

    private void enlargImageDialog(int iPosition) {
	final Dialog dialog = new Dialog(context);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	Rect displayRectangle = new Rect();
	Window window = ((Activity) context).getWindow();
	window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.profile_pic_display, null);
	layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
	// layout.setMinimumHeight((int) (displayRectangle.height() * 0.9f));

	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.ic_action_person).showImageOnFail(R.drawable.ic_action_person).build();

	dialog.setContentView(layout);
	dialog.setCancelable(true);
	dialog.show();
	ImageView imgProfilePicDisplay = (ImageView) dialog.findViewById(R.id.imgProfilePicDisplay);

	if (doctorList.get(iPosition).getDoctorPic() != null) {
	    imageLoader.displayImage((doctorList.get(iPosition).getDoctorPic()), imgProfilePicDisplay, options, new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);

		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.GONE);
		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.GONE);
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {

		}
	    });
	} else {
	    imgProfilePicDisplay.setImageResource(R.drawable.ic_action_person);
	}

	Button btnCancel = (Button) dialog.findViewById(R.id.close_button_hs);
	btnCancel.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		try {
		    dialog.dismiss();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }
}