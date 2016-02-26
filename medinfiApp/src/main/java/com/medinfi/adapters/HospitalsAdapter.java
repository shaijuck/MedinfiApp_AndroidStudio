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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.R;
import com.medinfi.datainfo.HospitalInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class HospitalsAdapter extends ArrayAdapter<HospitalInfo> {

    /* Apdater used to load the Hospitals information in the listview */
    Context context;
    int layoutResourceId;
    public ArrayList<HospitalInfo> hospitalList;
    private ArrayList<HospitalInfo> arraylist;
    private ImageLoader imageLoader;
    DisplayImageOptions options;
    DisplayImageOptions optionsMultiple;
    private Typeface tfBold, tfRegular, tfLite, tfCondensed;
    private EasyTracker easyTracker = null;
    private HospitalInfo hospitalInfo;
    private String callingSceen;

    public HospitalsAdapter(Context context, int layoutResourceId, ArrayList<HospitalInfo> hospitalList, String callingSceen) {
	super(context, layoutResourceId, hospitalList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.hospitalList = hospitalList;
	this.arraylist = new ArrayList<HospitalInfo>();
	this.arraylist.addAll(hospitalList);
	this.callingSceen = callingSceen;
	easyTracker = EasyTracker.getInstance(context);
	imageLoader = ImageLoader.getInstance();
	options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.hospital_icon_normal).showImageOnFail(R.drawable.hospital_icon_normal)
		.showImageOnLoading(R.drawable.hospital_icon_normal).build();
	
	optionsMultiple = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

	tfBold = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoBold.ttf");
	tfLite = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoLight.ttf");
	tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	tfCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed.ttf");
    }
    // get the object based on array position
    @Override
    public HospitalInfo getItem(int position) {
	return hospitalList.get(position);
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
    HospitalHolder holder = null;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
	View row = convertView;
	

	if (row == null) {
	    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    row = inflater.inflate(layoutResourceId, parent, false);

	    holder = new HospitalHolder();
	    holder.txtHospitalName = (TextView) row.findViewById(R.id.tv_profilename_hospital);

	    holder.txtHospitalName.setTypeface(tfRegular);
	    holder.txtHospitalSpeciality = (TextView) row.findViewById(R.id.tv_profiledesignation_hospital);

	    holder.txtHospitalSpeciality.setTypeface(tfLite);
	    holder.txtHospitalDistance = (TextView) row.findViewById(R.id.tv_profiledistance_hospital);

	    holder.txtHospitalDistance.setTypeface(tfRegular);
	    holder.HospitalImageView = (ImageView) row.findViewById(R.id.iv_profile_hospital);
	    holder.callButton = (Button) row.findViewById(R.id.btn_call_hospital);
	   // holder.txtHospitalRatings = (TextView) row.findViewById(R.id.profile_rating_hospital);
	    holder.ratingLayout = (LinearLayout)row.findViewById(R.id.ratingLayout);
	    holder.overAllRating = (RatingBar)row.findViewById(R.id.overAllRating);
	   // holder.txtHospitalRatings.setTypeface(tfRegular);
	    holder.txtProfileKM = (TextView) row.findViewById(R.id.km_tv);
	   
	    holder.txtProfileKM.setTypeface(tfRegular);
	    
	    holder.imgDirection = (ImageView)row.findViewById(R.id.imgDirection);
	    
	    holder.layoutDirection = (LinearLayout)row.findViewById(R.id.layoutDirection);
	    holder.layoutDistance = (LinearLayout)row.findViewById(R.id.layoutDistance);
	    holder.multipleImageLayout = (LinearLayout)row.findViewById(R.id.multipleImageLayout);
	    holder.txtDirectionDistance = (TextView) row.findViewById(R.id.txtDirectionDistance);
	    holder.multipleImageLayout.setVisibility(View.GONE);
	    
	    holder.imgHospEmergrncyGate = (ImageView)row.findViewById(R.id.imgHospEmergrncyGate);
	    holder.imgHospLeft = (ImageView)row.findViewById(R.id.imgHospLeft);
	    holder.imgHospRight = (ImageView)row.findViewById(R.id.imgHospRight);
	    holder.txtVerified = (TextView)row.findViewById(R.id.txtVerified);
	    holder.imgVerifiedLog=(ImageView) row.findViewById(R.id.imgVerifiedLog);
	    holder.verifiedLayout = (LinearLayout)row.findViewById(R.id.verifiedLayout);
	    holder.progressBar = (ProgressBar)row.findViewById(R.id.progressBar);
	    
	    holder.callButton.setOnClickListener(callButtonClickListener);
	    
	    if(callingSceen!=null && callingSceen.equalsIgnoreCase("DoctorDetails")){
		holder.layoutDirection.setVisibility(View.VISIBLE);
		holder.layoutDistance .setVisibility(View.GONE);
		holder.imgDirection.setVisibility(View.VISIBLE);
		holder.verifiedLayout.setVisibility(View.VISIBLE);
		holder.layoutDirection.setOnClickListener(routeDirectionClickListener);
	    }else{
		holder.layoutDirection.setVisibility(View.GONE);
		holder.layoutDistance .setVisibility(View.VISIBLE);
		holder.verifiedLayout.setVisibility(View.VISIBLE);
	    }
	    
	    row.setTag(holder);
	} else {
	    holder = (HospitalHolder) row.getTag();
	}

	hospitalInfo = hospitalList.get(position);
	holder.callButton.setTag(position);
	holder.layoutDirection.setTag(position);
	holder.txtHospitalName.setText(hospitalInfo.getHospitalName());
	/*
	 * if (hospitalInfo.getHospitalSpeciality() != null &&
	 * !hospitalInfo.getHospitalSpeciality().equalsIgnoreCase("") &&
	 * !hospitalInfo.getHospitalSpeciality().equalsIgnoreCase( "null"))
	 * holder
	 * .txtHospitalSpeciality.setText(hospitalInfo.getHospitalSpeciality());
	 * else holder.txtHospitalSpeciality.setText("");
	 */

	if (hospitalInfo.getHospitalLocality() != null && !hospitalInfo.getHospitalLocality().equalsIgnoreCase("")
		&& !hospitalInfo.getHospitalLocality().equalsIgnoreCase("null")) {
	    holder.txtHospitalSpeciality.setVisibility(View.VISIBLE);
	    holder.txtHospitalSpeciality.setText(hospitalInfo.getHospitalLocality());
	} else {
	    holder.txtHospitalSpeciality.setVisibility(View.GONE);
	}
	
	
	if (hospitalInfo.getHospitalDistance() != null && !hospitalInfo.getHospitalDistance().equalsIgnoreCase("")) {

	    try {
		double rounded = (double) Math.round(Double.valueOf(hospitalInfo.getHospitalDistance()) * 100) / 100;
		DecimalFormat df2 = new DecimalFormat("#####.#");
		if(callingSceen!=null && callingSceen.equalsIgnoreCase("DoctorDetails")){
		   // holder.txtDirectionDistance.setText("" + Double.valueOf(df2.format(rounded)));
		} else {
		   holder.txtHospitalDistance.setText("" + Double.valueOf(df2.format(rounded)));
		    holder.txtHospitalDistance.setVisibility(View.VISIBLE);
		    holder.txtProfileKM.setVisibility(View.VISIBLE);
		   // getDistance();
		}
		
	    } catch (NumberFormatException e) {
		// TODO Auto-generated catch block
	    }

	} else {
	    holder.txtHospitalDistance.setVisibility(View.GONE);
	    holder.txtProfileKM.setVisibility(View.GONE);
	}
	
	
    
	if (hospitalInfo.getHospitalRating() != null && !hospitalInfo.getHospitalRating().equals(null)
		&& !hospitalInfo.getHospitalRating().equals("0.0") && !hospitalInfo.getHospitalRating().equals("null")
		&& !hospitalInfo.getHospitalRating().equals(0.0)) {
	    try {
		holder.ratingLayout.setVisibility(View.VISIBLE);
		long lValue = Math.round(Double.valueOf(hospitalInfo.getHospitalRating()));
		holder.overAllRating.setRating(lValue);
		holder.overAllRating.setIsIndicator(true);
		//holder.txtHospitalRatings.setText("" + hospitalInfo.getHospitalRating() + "/5 ");
		//holder.txtProfileRated.setVisibility(View.VISIBLE);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} else {
	    holder.ratingLayout.setVisibility(View.GONE);
	}
        boolean isVisited = false;
	if (hospitalInfo.getHospitalImage() != null && !hospitalInfo.getHospitalImage().trim().equalsIgnoreCase("")
		&& !hospitalInfo.getHospitalImage().trim().equalsIgnoreCase(null) && !hospitalInfo.getHospitalImage().trim().equalsIgnoreCase("null")) {
	  //  imageLoader.displayImage(hospitalInfo.getHospitalImage(), holder.HospitalImageView, options);
	  // Glide.with(context).load(hospitalInfo.getHospitalImage()).error(R.drawable.hospital_icon_normal).into(holder.HospitalImageView); 
	    holder.progressBar.setVisibility(View.GONE);
	    try{
	    Glide.with(context).load(hospitalInfo.getHospitalImage()).placeholder(R.drawable.hospital_icon_normal).into(new GlideDrawableImageViewTarget(holder.HospitalImageView) {
		
		
		@Override
		public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
		    super.onResourceReady(drawable, anim);
		    holder.progressBar.setVisibility(View.GONE);
		}
		@Override
		public void onLoadFailed(Exception e, Drawable errorDrawable) {
		    super.onLoadFailed(e, errorDrawable);
		    holder.progressBar.setVisibility(View.GONE);
		}
	    });
	    }catch(Exception e){}
	    isVisited = true;
	    
	} else {
		// holder.HospitalImageView.setImageResource(R.drawable.hospital_icon_normal);
		try{
	    Glide.with(context).load(R.drawable.hospital_icon_normal).into(holder.HospitalImageView);
	    }catch(Exception e){}
	}

	if (hospitalInfo.getHospitalEmergencyPic() != null && !hospitalInfo.getHospitalEmergencyPic().trim().equalsIgnoreCase("")
		&& !hospitalInfo.getHospitalEmergencyPic().trim().equalsIgnoreCase(null) && !hospitalInfo.getHospitalEmergencyPic().trim().equalsIgnoreCase("null")) {
	    holder.multipleImageLayout.setVisibility(View.GONE);
	    holder.imgHospEmergrncyGate.setVisibility(View.VISIBLE);
	    isVisited = true;
	   // imageLoader.displayImage(hospitalInfo.getHospitalEmergencyPic(), holder.imgHospEmergrncyGate, optionsMultiple);
	    Glide.with(context).load(hospitalInfo.getHospitalEmergencyPic()).into(holder.imgHospEmergrncyGate); 
	}
	if (hospitalInfo.getHospitalLeftPic() != null && !hospitalInfo.getHospitalLeftPic().trim().equalsIgnoreCase("")
		&& !hospitalInfo.getHospitalLeftPic().trim().equalsIgnoreCase(null) && !hospitalInfo.getHospitalLeftPic().trim().equalsIgnoreCase("null")) {
	    holder.multipleImageLayout.setVisibility(View.GONE);
	    holder.imgHospLeft.setVisibility(View.VISIBLE);
	    isVisited = true;
	   // imageLoader.displayImage(hospitalInfo.getHospitalLeftPic(), holder.imgHospLeft, optionsMultiple);
	    Glide.with(context).load(hospitalInfo.getHospitalLeftPic()).into(holder.imgHospLeft); 
	}
	if (hospitalInfo.getHospitalRightPic() != null && !hospitalInfo.getHospitalRightPic().trim().equalsIgnoreCase("")
		&& !hospitalInfo.getHospitalRightPic().trim().equalsIgnoreCase(null) && !hospitalInfo.getHospitalRightPic().trim().equalsIgnoreCase("null")) {
	    holder.multipleImageLayout.setVisibility(View.GONE);
	    holder.imgHospRight.setVisibility(View.VISIBLE);
	    isVisited = true;
	    //imageLoader.displayImage(hospitalInfo.getHospitalRightPic(), holder.imgHospRight, optionsMultiple);
	    Glide.with(context).load(hospitalInfo.getHospitalRightPic()).into(holder.imgHospRight); 
	}
	if(isVisited){
	    holder.txtVerified.setText(context.getString(R.string.msgVisited)); 
	    holder.imgVerifiedLog.setBackgroundResource(R.drawable.two_tick);
	}else{
	    holder.txtVerified.setText(context.getString(R.string.msgVerified));
	    holder.imgVerifiedLog.setBackgroundResource(R.drawable.one_tick);
	}
	
//	holder.HospitalImageView.setOnClickListener(new OnClickListener() {
//
//	    @Override
//	    public void onClick(View v) {
//		if (hospitalList.get(position).getHospitalImage() != null) {
//		    new EnlargeMultipleImageDialog(context, position).show();
//		}
//	    }
//	});
	holder.imgHospEmergrncyGate.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (hospitalList.get(position).getHospitalEmergencyPic() != null) {
		    //enlargImageDialog(position,1);
		}
	    }
	});
	holder.imgHospLeft.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (hospitalList.get(position).getHospitalLeftPic() != null) {
		   // enlargImageDialog(position,2);
		}
	    }
	});
	holder.imgHospRight.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (hospitalList.get(position).getHospitalRightPic() != null) {
		   // enlargImageDialog(position,3);
		}
	    }
	});
	return row;
	
    }
    

    /* holder which holds the view associated to the adapter */
    static class HospitalHolder {
	TextView txtHospitalName;
	TextView txtHospitalSpeciality;
	TextView txtHospitalDistance;
	//TextView txtHospitalRatings;
	ImageView HospitalImageView;
	TextView txtProfileKM;
	//TextView txtProfileRated;
	Button callButton;
	RatingBar overAllRating;
	LinearLayout ratingLayout;
	ImageView imgDirection;
	LinearLayout layoutDirection;
	LinearLayout layoutDistance;
	LinearLayout multipleImageLayout;
	TextView txtDirectionDistance;
	ImageView imgHospEmergrncyGate;
	ImageView imgHospLeft;
	ImageView imgHospRight;
	ImageView imgVerifiedLog;
	TextView txtVerified;
	LinearLayout verifiedLayout;
	ProgressBar progressBar;
    }

    
    // click listener called when user clicks on the list item call button
    private OnClickListener callButtonClickListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
	    int position = (Integer) v.getTag();
	    if (hospitalList.get(position).getHospitalPhoneNumber() != null
		    && !hospitalList.get(position).getHospitalPhoneNumber().equalsIgnoreCase("null")
		    && !hospitalList.get(position).getHospitalPhoneNumber().equalsIgnoreCase(null)
		    && !hospitalList.get(position).getHospitalPhoneNumber().equalsIgnoreCase("6855")) {

		String phoneno = hospitalList.get(position).getHospitalPhoneNumber().toString().trim();
		if (phoneno.startsWith("0")) {
		    phoneno = hospitalList.get(position).getHospitalPhoneNumber().toString().trim();
		} else if (phoneno.startsWith("91")) {
		    phoneno = hospitalList.get(position).getHospitalPhoneNumber().toString().trim();
		} else if (phoneno.startsWith("+91")) {
		    phoneno = hospitalList.get(position).getHospitalPhoneNumber().toString().trim();
		} else {
		    phoneno = "0" + hospitalList.get(position).getHospitalPhoneNumber().toString().trim();
		}

		easyTracker.send(MapBuilder.createEvent("Doctor Details", "Call Hospital", hospitalList.get(position).getHospitalName(), null)
			.build());

		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phoneno));
		context.startActivity(intent);
	    } else {
		Toast.makeText(context, "Phone number does not exist", 1000).show();
	    }
	}

    };
    
    class EnlargeMultipleImageDialog extends Dialog implements android.view.View.OnClickListener {
	Context mContext;
	private int iPosition;
	RelativeLayout layoutSecondImage;
	DisplayImageOptions optionsEnlarg;
	private ArrayList<String> multiplaeImageList = new ArrayList<String>();
	int iCunt;

	ViewPager viewPager;
	MyPagerAdapter myPagerAdapter;
	String[] imageArray;

	public EnlargeMultipleImageDialog(Context mContext, int iPosition) {
	    super(mContext);
	    this.mContext = mContext;
	    this.iPosition = iPosition;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    Rect displayRectangle = new Rect();
	    Window window = ((Activity) mContext).getWindow();
	    window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

	    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.multiple_image_filiper, null);
	    layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
	    // layout.setMinimumHeight((int) (displayRectangle.height() *
	    // 0.9f));

	    optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

	    setContentView(layout);
	    Button btnCancel = (Button) findViewById(R.id.close_button_hs);
	    btnCancel.setOnClickListener(this);

	    if (hospitalList.get(iPosition).getHospitalImage() != null) {
		multiplaeImageList.add(hospitalList.get(iPosition).getHospitalImage());
	    }
	    if (hospitalList.get(iPosition).getHospitalEmergencyPic() != null) {
		multiplaeImageList.add(hospitalList.get(iPosition).getHospitalEmergencyPic());
	    }
	    if (hospitalList.get(iPosition).getHospitalLeftPic() != null) {
		multiplaeImageList.add(hospitalList.get(iPosition).getHospitalLeftPic());
	    }
	    if (hospitalList.get(iPosition).getHospitalRightPic() != null) {
		multiplaeImageList.add(hospitalList.get(iPosition).getHospitalRightPic());
	    }

	    imageArray = new String[multiplaeImageList.size()];

	    for (int i = 0; i < multiplaeImageList.size(); i++) {
		imageArray[i] = multiplaeImageList.get(i);
	    }

	    viewPager = (ViewPager) findViewById(R.id.myviewpager);
	    myPagerAdapter = new MyPagerAdapter(mContext, imageArray);
	    viewPager.setAdapter(myPagerAdapter);

	    // txtCountMultip.setVisibility(View.VISIBLE);

	}

	private class MyPagerAdapter extends PagerAdapter {

	    Context context;
	    ImageLoader imageLoader;
	    DisplayImageOptions optionsEnlarg;
	    private Typeface tfRegular;

	    MyPagerAdapter(Context context, String[] imageArray) {
		this.context = context;
	         imageLoader = ImageLoader.getInstance();
		 imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		 optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
		tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	    }

	    int NumberOfPages = imageArray.length;

	    @Override
	    public int getCount() {
		return NumberOfPages;
	    }

	    @Override
	    public boolean isViewFromObject(View view, Object object) {
		return view == object;
	    }

	    @Override
	    public Object instantiateItem(ViewGroup container, int position) {

		TextView textView = new TextView(mContext);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(14);
		textView.setPadding(5, 7, 0, 0);
		textView.setTypeface(tfRegular);
		textView.setText(String.valueOf(position + 1) + " of " + NumberOfPages);
		textView.setVisibility(View.GONE);

		ImageView imageView = new ImageView(mContext);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setPadding(0, 10, 0, 0);
		ProgressBar progresBar = new ProgressBar(mContext);
		progresBar.setIndeterminate(true);

		LayoutParams layoutParamsProgresBar = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progresBar.setLayoutParams(layoutParamsProgresBar);

		LinearLayout linearLayoutPBar = new LinearLayout(mContext);
		linearLayoutPBar.setOrientation(LinearLayout.VERTICAL);
		linearLayoutPBar.setGravity(Gravity.CENTER);
		LayoutParams layoutParamsLinearpBar = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		linearLayoutPBar.setLayoutParams(layoutParamsLinearpBar);
		linearLayoutPBar.addView(progresBar);

		setImageEnLargLoading(imageArray[position], imageView, optionsEnlarg, textView, progresBar);
		LayoutParams imageParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(imageParams);

		LinearLayout linearLayout = new LinearLayout(mContext);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);
		LayoutParams layoutParamsLinear = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		linearLayout.setLayoutParams(layoutParamsLinear);

		linearLayout.addView(textView);
		linearLayout.addView(imageView);

		RelativeLayout layout = new RelativeLayout(mContext);

		layout.setGravity(Gravity.CENTER);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 300);
		layout.setBackgroundColor(Color.WHITE);
		layout.setLayoutParams(layoutParams);

		layout.addView(linearLayoutPBar);
		layout.addView(linearLayout);

		container.addView(layout);
		return layout;
	    }

	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((RelativeLayout) object);
	    }

	    private void setImageEnLargLoading(String imageUrl, ImageView imgProfilePicDisplay, DisplayImageOptions options, final TextView textView,
		    final ProgressBar progressBar) {
		imageLoader.displayImage(imageUrl, imgProfilePicDisplay, options, new ImageLoadingListener() {

		    @Override
		    public void onLoadingStarted(String arg0, View arg1) {
			progressBar.setVisibility(View.VISIBLE);
			textView.setVisibility(View.GONE);

		    }

		    @Override
		    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			progressBar.setVisibility(View.GONE);
		    }

		    @Override
		    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			progressBar.setVisibility(View.GONE);
			textView.setVisibility(View.VISIBLE);
		    }

		    @Override
		    public void onLoadingCancelled(String arg0, View arg1) {

		    }
		});
	    }

	}

	@Override
	public void onClick(View v) {
	    int id = v.getId();
	    switch (id) {
	    case R.id.close_button_hs:
		try {
		    dismiss();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		break;
	    default:
		break;
	    }

	}
    }
    
    private OnClickListener routeDirectionClickListener = new OnClickListener() {

	@Override
	public void onClick(View v) {
	    int position = (Integer) v.getTag();
	    easyTracker.send(MapBuilder.createEvent("Doctor Details", "Map View", "Map View", null).build());
	    Utils.routeDirection(context,Double.valueOf(ApplicationSettings.getPref(AppConstants.LATITUDE, "")),Double.valueOf(ApplicationSettings.getPref(AppConstants.LONGITUDE, "")),
	    	    Double.valueOf(hospitalList.get(position).getHospitalLat()),Double.valueOf(hospitalList.get(position).getHospitalLong()));
	}

    };
    
    String dist="";
    public void getDistance() {
    	final class GetHospitalList extends AsyncTask<Void, Void, String> {

    	    @Override
    	    protected String doInBackground(Void... arg0) {
    		
    		JSONParser jparser = new JSONParser();
    		String latitude = ApplicationSettings.getPref(AppConstants.LATITUDE, "");
    		String longitude = ApplicationSettings.getPref(AppConstants.LONGITUDE, "");
    		String result = jparser.getDistance(latitude, longitude);
    		return result.toString();
    	    }

    	    @Override
    	    protected void onPostExecute(String result) {

    	    	dist="";
    		
    		try {
				JSONObject jObj=new JSONObject(result);
				JSONArray jary=jObj.getJSONArray("rows");
				JSONObject jObj2=jary.getJSONObject(0);
				JSONArray jary2=jObj2.getJSONArray("elements");
				JSONObject jObj3=jary2.getJSONObject(0);
				JSONObject jObj4=jObj3.getJSONObject("distance");
				System.out.println("result"+jObj4.getString("text"));
				dist=jObj4.getString("text");
				messageHandler.sendEmptyMessage(0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	    }

    	}
    	new GetHospitalList().execute();

    	}
    
    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	holder.txtHospitalDistance.setText("" +dist);
        }
    };
    
}