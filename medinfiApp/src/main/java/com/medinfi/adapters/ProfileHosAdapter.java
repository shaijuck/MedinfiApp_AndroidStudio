package com.medinfi.adapters;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.R;
import com.medinfi.datainfo.FavouriteInfo;
import com.medinfi.utils.CircleTransform;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ProfileHosAdapter extends ArrayAdapter<FavouriteInfo> {

    /* Apdater used to load the Hospitals information in the listview */
    Context context;
    int layoutResourceId;
    public ArrayList<FavouriteInfo> favouriteList;
    private ArrayList<FavouriteInfo> arraylist;
    private ImageLoader imageLoader;
    DisplayImageOptions optionsDoctor,optionHospital;
    private Typeface tfBold, tfRegular, tfLite, tfCondensed;
    private EasyTracker easyTracker = null;

    public ProfileHosAdapter(Context context, int layoutResourceId, ArrayList<FavouriteInfo> favouriteList) {
	super(context, layoutResourceId, favouriteList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.favouriteList = favouriteList;
	this.arraylist = new ArrayList<FavouriteInfo>();
	this.arraylist.addAll(favouriteList);
	easyTracker = EasyTracker.getInstance(context);
	imageLoader = ImageLoader.getInstance();

	optionsDoctor = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.ic_action_person).showImageOnFail(R.drawable.ic_action_person)
		.showImageOnLoading(R.drawable.ic_action_person).build();

	optionHospital = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.hospital_icon_normal).showImageOnFail(R.drawable.hospital_icon_normal)
		.showImageOnLoading(R.drawable.hospital_icon_normal).build();

	tfBold = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoBold.ttf");
	tfLite = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoLight.ttf");
	tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	tfCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed.ttf");
    }
    // get the object based on array position
    @Override
    public FavouriteInfo getItem(int position) {
	return favouriteList.get(position);
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
	FavouriteHolder holder = null;

	if (row == null) {
	    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    row = inflater.inflate(layoutResourceId, parent, false);

	    holder = new FavouriteHolder();
	    holder.txtProfileName = (TextView) row.findViewById(R.id.txtProfileName);
	    holder.txtProfileName.setTypeface(tfRegular);

	    holder.txtProfiledesignation = (TextView) row.findViewById(R.id.txtProfiledesignation);
	    holder.txtProfiledesignation.setTypeface(tfLite);
	    holder.imgProfilePic = (ImageView) row.findViewById(R.id.imgProfilePic);
	    
	   // holder.txtFavRatedValue = (TextView) row.findViewById(R.id.profileRating);
	   // holder.txtFavRatedValue.setTypeface(tfRegular);
	    holder.txtProfileKM = (TextView) row.findViewById(R.id.km_tv);
	    holder.txtProfileKM.setTypeface(tfRegular);
	    holder.txtDistance = (TextView) row.findViewById(R.id.txtDistance);
	    holder.txtDistance.setTypeface(tfRegular);
	 //   holder.txtProfileRated = (TextView) row.findViewById(R.id.rated_tv);
	  //  holder.txtProfileRated.setTypeface(tfCondensed);
	    holder.ratingLayout = (LinearLayout)row.findViewById(R.id.ratingLayout);
	    holder.overAllRating = (RatingBar)row.findViewById(R.id.overAllRating);

	    holder.callButton = (Button) row.findViewById(R.id.btnCall);
	    holder.txtVerified = (TextView)row.findViewById(R.id.txtVerified);
	    holder.imgVerifiedLog=(ImageView) row.findViewById(R.id.imgVerifiedLog);
	    holder.callButton.setOnClickListener(callButtonClickListener);
	    row.setTag(holder);
	} else {
	    holder = (FavouriteHolder) row.getTag();
	}

	FavouriteInfo favouriteInfo = favouriteList.get(position);
	holder.callButton.setTag(position);
	holder.txtProfileName.setText(favouriteInfo.getFavName());
	
	if (favouriteInfo.getFavType()!=null && favouriteInfo.getFavType().equalsIgnoreCase("Doctor")) {
	    holder.imgProfilePic.setImageResource(R.drawable.ic_action_person);
	    if (favouriteInfo.getFavDoctype() != null && !favouriteInfo.getFavDoctype().equalsIgnoreCase("")
		    && !favouriteInfo.getFavDoctype().equalsIgnoreCase("null")) {
		holder.txtProfiledesignation.setVisibility(View.VISIBLE);
		holder.txtProfiledesignation.setText(favouriteInfo.getFavDoctype());
	    } else {
		holder.txtProfiledesignation.setVisibility(View.GONE);
	    }

	    if (favouriteInfo.getFavPic() != null && !favouriteInfo.getFavPic().equalsIgnoreCase("")
			&& !favouriteInfo.getFavPic().equalsIgnoreCase(null) && !favouriteInfo.getFavPic().equalsIgnoreCase("null")) {
		   // imageLoader.displayImage(favouriteInfo.getFavPic(), holder.imgProfilePic, optionsDoctor);
		    Glide.with(context).load(favouriteInfo.getFavPic()).placeholder(R.drawable.ic_action_person).transform(new CircleTransform(context)).into(holder.imgProfilePic); 
		    holder.txtVerified.setText(context.getString(R.string.msgVisited)); 
		    holder.imgVerifiedLog.setBackgroundResource(R.drawable.two_tick);
	    } else {
		    Glide.with(context).load(R.drawable.ic_action_person).into(holder.imgProfilePic); 
		   // holder.imgProfilePic.setImageResource(R.drawable.ic_action_person);
		    holder.txtVerified.setText(context.getString(R.string.msgVerified));
		    holder.imgVerifiedLog.setBackgroundResource(R.drawable.one_tick);
		}
	   

	} else {
		 Glide.with(context).load(R.drawable.hospital_icon_normal).into(holder.imgProfilePic); 
	   // holder.imgProfilePic.setImageResource(R.drawable.hospital_icon_normal);
	    if (favouriteInfo.getFavLocality() != null && !favouriteInfo.getFavLocality().equalsIgnoreCase("")
			&& !favouriteInfo.getFavLocality().equalsIgnoreCase("null")) {
		    holder.txtProfiledesignation.setVisibility(View.VISIBLE);
		    holder.txtProfiledesignation.setText(favouriteInfo.getFavLocality());
		} else {
		    holder.txtProfiledesignation.setVisibility(View.GONE);
		}
	    if (favouriteInfo.getFavPic() != null && !favouriteInfo.getFavPic().equalsIgnoreCase("")
			&& !favouriteInfo.getFavPic().equalsIgnoreCase(null) && !favouriteInfo.getFavPic().equalsIgnoreCase("null")) {
		   // imageLoader.displayImage(favouriteInfo.getFavPic(), holder.imgProfilePic, optionHospital);
		    Glide.with(context).load(favouriteInfo.getFavPic()).placeholder(R.drawable.hospital_icon_normal).into(holder.imgProfilePic); 
		    holder.txtVerified.setText(context.getString(R.string.msgVisited)); 
		    holder.imgVerifiedLog.setBackgroundResource(R.drawable.two_tick);   
		} else {
		    Glide.with(context).load(R.drawable.hospital_icon_normal).into(holder.imgProfilePic); 
		  // holder.imgProfilePic.setImageResource(R.drawable.hospital_icon_normal);
		   holder.txtVerified.setText(context.getString(R.string.msgVerified));
		   holder.imgVerifiedLog.setBackgroundResource(R.drawable.one_tick);
		}
	}
	if (favouriteInfo.getFavRating() != null &&  !favouriteInfo.getFavRating().equals(null) &&  !favouriteInfo.getFavRating().equals("0.0") && !favouriteInfo.getFavRating().equals("null") && !favouriteInfo.getFavRating().equals(0.0)){
	    try {
		holder.ratingLayout.setVisibility(View.VISIBLE);
		long lValue = Math.round(Double.valueOf(favouriteInfo.getFavRating()));
		holder.overAllRating.setRating(lValue);
		holder.overAllRating.setIsIndicator(true);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
	    }
	} else {
	    holder.ratingLayout.setVisibility(View.GONE);
	}
	
	if (favouriteInfo.getDistance() != null && !favouriteInfo.getDistance().equalsIgnoreCase("")) {

	    try {
		double rounded = (double) Math.round(Double.valueOf(favouriteInfo.getDistance()) * 100) / 100;
		DecimalFormat df2 = new DecimalFormat("#####.#");
		
		   holder.txtDistance.setText("" + Double.valueOf(df2.format(rounded)));
		    holder.txtDistance.setVisibility(View.VISIBLE);
		    holder.txtProfileKM.setVisibility(View.VISIBLE);
		
		
	    } catch (NumberFormatException e) {
		// TODO Auto-generated catch block
	    }

	} else {
	    holder.txtDistance.setVisibility(View.GONE);
	    holder.txtProfileKM.setVisibility(View.GONE);
	}
	
//	holder.imgProfilePic.setOnClickListener(new OnClickListener() {
//
//	    @Override
//	    public void onClick(View v) {
//		if (favouriteList.get(position).getFavPic() != null) {
//		    new EnlargeMultipleImageDialog(context, position).show();
//		}
//	    }
//	});
	
	return row;
    }

    /* holder which holds the view associated to the adapter */
    static class FavouriteHolder {
	TextView txtProfileName;
	TextView txtProfiledesignation;
	TextView txtDistance;
	//TextView txtRatings;
	ImageView imgProfilePic;
	TextView txtProfileKM;
	//TextView txtFavRatedValue;
	//TextView txtProfileRated;
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
	    if (favouriteList.get(position).getFavPrimaryphone() != null
		    && !favouriteList.get(position).getFavPrimaryphone().equalsIgnoreCase("null")
		    && !favouriteList.get(position).getFavPrimaryphone().equalsIgnoreCase(null)
		    && !favouriteList.get(position).getFavPrimaryphone().equalsIgnoreCase("6855")) {

		String phoneno = favouriteList.get(position).getFavPrimaryphone().toString().trim();
		if (phoneno.startsWith("0")) {
		    phoneno = favouriteList.get(position).getFavPrimaryphone().toString().trim();
		} else if (phoneno.startsWith("91")) {
		    phoneno = favouriteList.get(position).getFavPrimaryphone().toString().trim();
		} else if (phoneno.startsWith("+91")) {
		    phoneno = favouriteList.get(position).getFavPrimaryphone().toString().trim();
		} else {
		    phoneno = "0" + favouriteList.get(position).getFavPrimaryphone().toString().trim();
		}
		if(favouriteList.get(position).getFavType()!=null && favouriteList.get(position).getFavType().equalsIgnoreCase("Doctor")){
		    easyTracker.send(MapBuilder.createEvent("Saves", "Call Doctor", favouriteList.get(position).getFavDoctype()+":"+favouriteList.get(position).getFavName(), null).build());
		}else{
		    easyTracker.send(MapBuilder.createEvent("Saves", "Call Hospital", favouriteList.get(position).getFavLocality()+":"+favouriteList.get(position).getFavName(), null).build());
		}

		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phoneno));
		context.startActivity(intent);
	    } else {
		Toast.makeText(context, "Phone number does not exist", Toast.LENGTH_LONG).show();
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

	    if (favouriteList.get(iPosition).getFavType() != null && favouriteList.get(iPosition).getFavType().equalsIgnoreCase("Doctor")) {
		if (favouriteList.get(iPosition).getFavPic() != null) {
		    multiplaeImageList.add(favouriteList.get(iPosition).getFavPic());
		}
		if (favouriteList.get(iPosition).getFavSecondPic() != null) {
		    multiplaeImageList.add(favouriteList.get(iPosition).getFavSecondPic());
		}

	    } else {
		if (favouriteList.get(iPosition).getFavPic() != null) {
		    multiplaeImageList.add(favouriteList.get(iPosition).getFavPic());
		}
		if (favouriteList.get(iPosition).getHospitalEmergencyPic() != null) {
		    multiplaeImageList.add(favouriteList.get(iPosition).getHospitalEmergencyPic());
		}
		if (favouriteList.get(iPosition).getHospitalLeftPic() != null) {
		    multiplaeImageList.add(favouriteList.get(iPosition).getHospitalLeftPic());
		}
		if (favouriteList.get(iPosition).getHospitalRightPic() != null) {
		    multiplaeImageList.add(favouriteList.get(iPosition).getHospitalRightPic());
		}
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
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
    class EnlargeImageDialogDoctor extends Dialog implements android.view.View.OnClickListener {
	    Context context;
	    private ViewFlipper viewFlipper;
	    private float lastX;
	    private int position;
	    RelativeLayout layoutSecondImage;
	    private ImageView imageViewFirst, imageViewSecond;
	    private TextView txtCount;
	    private int iCount = 0;
	    DisplayImageOptions optionsEnlarg;
	    private ImageLoader imageLoader;
	 //   ArrayList<DoctorDetailInfo> associateDocList;

	    public EnlargeImageDialogDoctor(Context context, int position) {
		super(context);
		imageLoader = ImageLoader.getInstance();
		this.context = context;
		this.position = position;
		//this.associateDocList = associateDocList;
	    }
	   

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Rect displayRectangle = new Rect();
		Window window = ((Activity) context).getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.image_switcher_layout, null);
		layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
		// layout.setMinimumHeight((int) (displayRectangle.height() *
		// 0.9f));

	        optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

		setContentView(layout);

		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

		layoutSecondImage = (RelativeLayout) findViewById(R.id.layoutSecondImage);
		imageViewFirst = (ImageView) findViewById(R.id.imageViewFirst);
		imageViewSecond = (ImageView) findViewById(R.id.imageViewSecond);
		txtCount = (TextView) findViewById(R.id.txtCount);

		if (favouriteList.get(position).getFavPic() != null) {
		    iCount = 1;
		    txtCount.setVisibility(View.VISIBLE);
		    txtCount.setText("1 of " + iCount);
		    setImageEnLargLoading(favouriteList.get(position).getFavPic(), imageViewFirst, optionsEnlarg);
		}
		if (favouriteList.get(position).getFavSecondPic() != null) {
		    iCount = 2;
		    txtCount.setVisibility(View.VISIBLE);
		    txtCount.setText("1 of " + iCount);
		}if(iCount == 0){
		    txtCount.setVisibility(View.GONE);
		    ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
		    imageViewFirst.setImageResource(R.drawable.ic_action_person); 
		}
		
		Button btnCancel = (Button) findViewById(R.id.close_button_hs);
		btnCancel.setOnClickListener(this);

	    }

	    public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {

		case MotionEvent.ACTION_DOWN:
		    lastX = touchevent.getX();
		    break;
		case MotionEvent.ACTION_UP:
		    float currentX = touchevent.getX();

		    // Handling left to right screen swap.
		    if (lastX < currentX) {

			// If there aren't any other children, just break.
			if (viewFlipper.getDisplayedChild() == 0)
			    break;

			txtCount.setText("1 of " + iCount);
			viewFlipper.setInAnimation(context, R.anim.slide_in_from_left);
			viewFlipper.setOutAnimation(context, R.anim.slide_out_to_right);

			viewFlipper.showNext();
		    }

		    // Handling right to left screen swap.
		    if (lastX > currentX) {

			if (viewFlipper.getDisplayedChild() == 1)
			    break;

			if (favouriteList.get(position).getFavSecondPic() != null) {
			    layoutSecondImage.setVisibility(View.VISIBLE);
			    txtCount.setText("2 of " + iCount);
			    setImageEnLargLoading(favouriteList.get(position).getFavSecondPic(), imageViewSecond, optionsEnlarg);
			    viewFlipper.setInAnimation(context, R.anim.slide_in_from_right);
			    viewFlipper.setOutAnimation(context, R.anim.slide_out_to_left);
			    viewFlipper.showPrevious();
			}

		    }
		    break;
		}
		return false;
	    }

	    private void setImageEnLargLoading(String imageUrl, ImageView imgProfilePicDisplay, DisplayImageOptions options) {
		imageLoader.displayImage(imageUrl, imgProfilePicDisplay, options, new ImageLoadingListener() {

		    @Override
		    public void onLoadingStarted(String arg0, View arg1) {
			((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);

		    }

		    @Override
		    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
		    }

		    @Override
		    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
		    }

		    @Override
		    public void onLoadingCancelled(String arg0, View arg1) {

		    }
		});
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
}