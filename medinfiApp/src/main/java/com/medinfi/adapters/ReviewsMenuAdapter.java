package com.medinfi.adapters;

import java.util.ArrayList;




import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
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
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.medinfi.R;
import com.medinfi.adapters.FavouriteAdapter.EnlargeImageDialogDoctor;
import com.medinfi.adapters.FavouriteAdapter.EnlargeMultipleImageDialog;
import com.medinfi.datainfo.ReviewsMenuInfo;
import com.medinfi.utils.CircleTransform;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ReviewsMenuAdapter extends ArrayAdapter<ReviewsMenuInfo> {

    /* Apdater used to load the Hospitals information in the listview */
    Context context;
    int layoutResourceId;
    public ArrayList<ReviewsMenuInfo> reviewsMenuList;
    private ArrayList<ReviewsMenuInfo> arraylist;
    private ImageLoader imageLoader;
    DisplayImageOptions optionsDoctor, optionHospital;
    private Typeface tfBold, tfRegular, tfLite, tfCondensed;
    private EasyTracker easyTracker = null;

    public ReviewsMenuAdapter(Context context, int layoutResourceId, ArrayList<ReviewsMenuInfo> reviewsMenuList) {
	super(context, layoutResourceId, reviewsMenuList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.reviewsMenuList = reviewsMenuList;
	this.arraylist = new ArrayList<ReviewsMenuInfo>();
	this.arraylist.addAll(reviewsMenuList);
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

	    // holder.txtFavRatedValue = (TextView)
	    // row.findViewById(R.id.profileRating);
	    // holder.txtFavRatedValue.setTypeface(tfRegular);
	    holder.txtProfileKM = (TextView) row.findViewById(R.id.km_tv);
	    holder.txtProfileKM.setTypeface(tfRegular);
	    // holder.txtProfileRated = (TextView)
	    // row.findViewById(R.id.rated_tv);

	    holder.ratingLayout = (LinearLayout) row.findViewById(R.id.rateLayoutMain);
	    holder.overAllRating = (RatingBar) row.findViewById(R.id.overAllRating);
	    holder.feesRating = (RatingBar) row.findViewById(R.id.feesRating);
	    holder.waitingRating = (RatingBar) row.findViewById(R.id.waitingRating);
	    holder.attitudeRating = (RatingBar) row.findViewById(R.id.attitudeRating);

	    holder.txtMailId = (TextView) row.findViewById(R.id.txtMailId);
	    holder.txtData = (TextView) row.findViewById(R.id.txtData);
	    holder.txtReviewsDetails = (TextView) row.findViewById(R.id.txtReviewsDetails);
	    holder.txtReviewsDetails = (TextView) row.findViewById(R.id.txtReviewsDetails);
	    holder.reviewsDetailsLayout = (LinearLayout) row.findViewById(R.id.reviewsDetailsLayout);

	    row.setTag(holder);
	} else {
	    holder = (FavouriteHolder) row.getTag();
	}

	ReviewsMenuInfo reviewsMenuInfo = reviewsMenuList.get(position);
	holder.txtProfileName.setText(reviewsMenuInfo.getReviewsName());

	if (reviewsMenuInfo.getReviewsType() != null && reviewsMenuInfo.getReviewsType().equalsIgnoreCase("Doctor")) {
	    holder.imgProfilePic.setImageResource(R.drawable.ic_action_person);
	    if (reviewsMenuInfo.getReviewsType() != null && !reviewsMenuInfo.getReviewsType().equalsIgnoreCase("")
		    && !reviewsMenuInfo.getReviewsType().equalsIgnoreCase("null")) {
		holder.txtProfiledesignation.setVisibility(View.VISIBLE);
		holder.txtProfiledesignation.setText(reviewsMenuInfo.getReviewsDoctype());
	    } else {
		holder.txtProfiledesignation.setVisibility(View.GONE);
	    }

	    if (reviewsMenuInfo.getReviewsPic() != null && !reviewsMenuInfo.getReviewsPic().equalsIgnoreCase("")
		    && !reviewsMenuInfo.getReviewsPic().equalsIgnoreCase(null) && !reviewsMenuInfo.getReviewsPic().equalsIgnoreCase("null")) {
		//imageLoader.displayImage(reviewsMenuInfo.getReviewsPic(), holder.imgProfilePic, optionsDoctor);
		Glide.with(context).load(reviewsMenuInfo.getReviewsPic()).placeholder(R.drawable.ic_action_person).transform(new CircleTransform(context)).into(holder.imgProfilePic);
	    } else {
			Glide.with(context).load(R.drawable.ic_action_person).into(holder.imgProfilePic);
		//holder.imgProfilePic.setImageResource(R.drawable.ic_action_person);
	    }

	} else {
		Glide.with(context).load(R.drawable.hospital_icon_normal).into(holder.imgProfilePic);
	  //  holder.imgProfilePic.setImageResource(R.drawable.hospital_icon_normal);
	    if (reviewsMenuInfo.getReviewsLocality() != null && !reviewsMenuInfo.getReviewsLocality().equalsIgnoreCase("")
		    && !reviewsMenuInfo.getReviewsLocality().equalsIgnoreCase("null")) {
		holder.txtProfiledesignation.setVisibility(View.VISIBLE);
		holder.txtProfiledesignation.setText(reviewsMenuInfo.getReviewsLocality());
	    } else {
		holder.txtProfiledesignation.setVisibility(View.GONE);
	    }
	    if (reviewsMenuInfo.getReviewsPic() != null && !reviewsMenuInfo.getReviewsPic().equalsIgnoreCase("")
		    && !reviewsMenuInfo.getReviewsPic().equalsIgnoreCase(null) && !reviewsMenuInfo.getReviewsPic().equalsIgnoreCase("null")) {
		//imageLoader.displayImage(reviewsMenuInfo.getReviewsPic(), holder.imgProfilePic, optionHospital);
		Glide.with(context).load(reviewsMenuInfo.getReviewsPic()).placeholder(R.drawable.hospital_icon_normal).into(holder.imgProfilePic);
	    } else {
			Glide.with(context).load(R.drawable.hospital_icon_normal).into(holder.imgProfilePic);
		//holder.imgProfilePic.setImageResource(R.drawable.hospital_icon_normal);
	    }
	}
	if (reviewsMenuInfo.getReviewsAvgrate() != null && !reviewsMenuInfo.getReviewsAvgrate().equals(null)
		&& !reviewsMenuInfo.getReviewsAvgrate().equals("0.0") && !reviewsMenuInfo.getReviewsAvgrate().equals("null")
		&& !reviewsMenuInfo.getReviewsAvgrate().equals(0.0)) {
	    try {
		holder.ratingLayout.setVisibility(View.VISIBLE);
		long lAvgValue = Math.round(Double.valueOf(reviewsMenuInfo.getReviewsRating()));
		holder.overAllRating.setRating(lAvgValue);
		holder.overAllRating.setIsIndicator(true);

		long lFeeValue = Math.round(Double.valueOf(reviewsMenuInfo.getReviewsFees()));
		holder.feesRating.setRating(lFeeValue);
		holder.feesRating.setIsIndicator(true);

		long lWaitValue = Math.round(Double.valueOf(reviewsMenuInfo.getReviewsWaitTime()));
		holder.waitingRating.setRating(lWaitValue);
		holder.waitingRating.setIsIndicator(true);

		long lAttitudeValue = Math.round(Double.valueOf(reviewsMenuInfo.getReviewsAttitude()));
		holder.attitudeRating.setRating(lAttitudeValue);
		holder.attitudeRating.setIsIndicator(true);

	    } catch (Exception e) {
		holder.ratingLayout.setVisibility(View.GONE);
	    }
	} else {
	    holder.ratingLayout.setVisibility(View.GONE);
	}
	if (reviewsMenuInfo.getReviewsMailId() != null && !reviewsMenuInfo.getReviewsMailId().equalsIgnoreCase("")
		&& !reviewsMenuInfo.getReviewsMailId().equalsIgnoreCase("null") 
		&& !reviewsMenuInfo.getReviewsMailId().equalsIgnoreCase(null)
		&& reviewsMenuInfo.getReviewsMailId().length() > 2) {
	    holder.reviewsDetailsLayout.setVisibility(View.VISIBLE); 
	   // holder.txtMailId.setText(reviewsMenuInfo.getReviewsMailId());

	    if (reviewsMenuInfo.getReviewsCreateddate() != null && !reviewsMenuInfo.getReviewsCreateddate().equalsIgnoreCase("")
		    && !reviewsMenuInfo.getReviewsCreateddate().equalsIgnoreCase("null")
		    && !reviewsMenuInfo.getReviewsCreateddate().equalsIgnoreCase(null)) {
		holder.txtData.setText(reviewsMenuInfo.getReviewsCreateddate());
	    }
	    if (reviewsMenuInfo.getReviewsDescription() != null && !reviewsMenuInfo.getReviewsDescription().equalsIgnoreCase("")
		    && !reviewsMenuInfo.getReviewsDescription().equalsIgnoreCase("null")
		    && !reviewsMenuInfo.getReviewsDescription().equalsIgnoreCase(null)) {
		holder.txtReviewsDetails.setVisibility(View.VISIBLE);
		holder.txtReviewsDetails.setText(reviewsMenuInfo.getReviewsDescription());
	    }
	}else{
	    holder.reviewsDetailsLayout.setVisibility(View.GONE); 
	}
//	holder.imgProfilePic.setOnClickListener(new OnClickListener() {
//
//	    @Override
//	    public void onClick(View v) {
//		 if (reviewsMenuList.get(position).getReviewsPic() != null && !reviewsMenuList.get(position).getReviewsPic().trim().equalsIgnoreCase("")
//			    && !reviewsMenuList.get(position).getReviewsPic().trim().equalsIgnoreCase(null) && !reviewsMenuList.get(position).getReviewsPic().trim().equalsIgnoreCase("null")) {
//		     new EnlargeMultipleImageDialog(context, position).show();
//		 }
//	    }
//	});

	return row;
    }

    /* holder which holds the view associated to the adapter */
    static class FavouriteHolder {
	TextView txtProfileName;
	TextView txtProfiledesignation;
	TextView txtDistance;
	ImageView imgProfilePic;
	TextView txtProfileKM;
	LinearLayout ratingLayout;
	RatingBar overAllRating;
	RatingBar feesRating;
	RatingBar waitingRating;
	RatingBar attitudeRating;
	TextView txtMailId;
	TextView txtData;
	TextView txtReviewsDetails;
	LinearLayout reviewsDetailsLayout;

    }
    
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

	    if (reviewsMenuList.get(iPosition).getReviewsType() != null && reviewsMenuList.get(iPosition).getReviewsType().equalsIgnoreCase("Doctor")) {
		if (reviewsMenuList.get(iPosition).getReviewsPic() != null) {
		    multiplaeImageList.add(reviewsMenuList.get(iPosition).getReviewsPic());
		}
		if (reviewsMenuList.get(iPosition).getReviewsSecondImage() != null) {
		    multiplaeImageList.add(reviewsMenuList.get(iPosition).getReviewsSecondImage());
		}

	    } else {
		if (reviewsMenuList.get(iPosition).getReviewsPic() != null) {
		    multiplaeImageList.add(reviewsMenuList.get(iPosition).getReviewsPic());
		}
		if (reviewsMenuList.get(iPosition).getHospitalEmergencyPic() != null) {
		    multiplaeImageList.add(reviewsMenuList.get(iPosition).getHospitalEmergencyPic());
		}
		if (reviewsMenuList.get(iPosition).getHospitalLeftPic() != null) {
		    multiplaeImageList.add(reviewsMenuList.get(iPosition).getHospitalLeftPic());
		}
		if (reviewsMenuList.get(iPosition).getHospitalRightPic() != null) {
		    multiplaeImageList.add(reviewsMenuList.get(iPosition).getHospitalRightPic());
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

		if (reviewsMenuList.get(position).getReviewsPic() != null) {
		    iCount = 1;
		    txtCount.setVisibility(View.VISIBLE);
		    txtCount.setText("1 of " + iCount);
		    setImageEnLargLoading(reviewsMenuList.get(position).getReviewsPic(), imageViewFirst, optionsEnlarg);
		}
		if (reviewsMenuList.get(position).getReviewsSecondImage() != null) {
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

			if (reviewsMenuList.get(position).getReviewsSecondImage() != null) {
			    layoutSecondImage.setVisibility(View.VISIBLE);
			    txtCount.setText("2 of " + iCount);
			    setImageEnLargLoading(reviewsMenuList.get(position).getReviewsSecondImage(), imageViewSecond, optionsEnlarg);
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