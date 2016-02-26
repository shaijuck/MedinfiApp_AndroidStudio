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
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.R;
import com.medinfi.datainfo.AutoCompleteInfo;
import com.medinfi.utils.CircleTransform;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class AutoCompleteAdapter extends ArrayAdapter<AutoCompleteInfo> {

    Context context;
    int layoutResourceId;
    public ArrayList<AutoCompleteInfo> globalSearchList;
    private ArrayList<AutoCompleteInfo> arraylist;
    private ImageLoader imageLoader;
    DisplayImageOptions optionsDoctor, optionHospital;
    private Typeface tfBold, tfRegular, tfLite, tfCondensed;
    private EasyTracker easyTracker = null;
    String SpecializationId;
    public AutoCompleteAdapter(Context context, int layoutResourceId, ArrayList<AutoCompleteInfo> globalSearchList,String Specialization) {
	super(context, layoutResourceId, globalSearchList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.globalSearchList = globalSearchList;
	this.arraylist = new ArrayList<AutoCompleteInfo>();
	this.arraylist.addAll(globalSearchList);
	this.SpecializationId=Specialization;
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

	try {
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
		holder.txtDistance = (TextView) row.findViewById(R.id.txtDistance);
		holder.txtDistance.setTypeface(tfRegular);

		holder.txtProfileKM = (TextView) row.findViewById(R.id.km_tv);
		holder.txtProfileKM.setTypeface(tfRegular);
		// holder.txtProfileRated = (TextView)
		// row.findViewById(R.id.rated_tv);
		// holder.txtProfileRated.setTypeface(tfCondensed);
		holder.txtVerified = (TextView) row.findViewById(R.id.txtVerified);
		holder.ratingLayout = (LinearLayout) row.findViewById(R.id.ratingLayout);
		holder.overAllRating = (RatingBar) row.findViewById(R.id.overAllRating);
		holder.imgVerifiedLog=(ImageView) row.findViewById(R.id.imgVerifiedLog);
		holder.callButton = (Button) row.findViewById(R.id.btnCall);
		holder.callButton.setOnClickListener(callButtonClickListener);
		row.setTag(holder);
	    } else {
		holder = (FavouriteHolder) row.getTag();
	    }

	    AutoCompleteInfo autoCompleteInfo = globalSearchList.get(position);
	    holder.callButton.setTag(position);
	    holder.txtProfileName.setText(autoCompleteInfo.getName());

	    if (autoCompleteInfo.getType() != null && autoCompleteInfo.getType().equalsIgnoreCase("Doctor")) {
		 holder.imgProfilePic.setImageResource(R.drawable.ic_action_person);
		 
		 if(SpecializationId.equalsIgnoreCase("-999"))
			{
		if (autoCompleteInfo.getDoctype() != null && !autoCompleteInfo.getDoctype().equalsIgnoreCase("")
			&& !autoCompleteInfo.getDoctype().equalsIgnoreCase("null")) {
		    holder.txtProfiledesignation.setVisibility(View.VISIBLE);
		    holder.txtProfiledesignation.setText(autoCompleteInfo.getDoctype());
		} else {
		    holder.txtProfiledesignation.setVisibility(View.GONE);
		}
			}
			else
			{
		if (autoCompleteInfo.getHospitalName() != null && !autoCompleteInfo.getHospitalName().equalsIgnoreCase("")
						&& !autoCompleteInfo.getHospitalName().equalsIgnoreCase("null")) {
					    holder.txtProfiledesignation.setVisibility(View.VISIBLE);
					    holder.txtProfiledesignation.setText(autoCompleteInfo.getHospitalName());
					} else {
					    holder.txtProfiledesignation.setVisibility(View.GONE);
					}
			}

		if (autoCompleteInfo.getPic() != null && !autoCompleteInfo.getPic().equalsIgnoreCase("")
			&& !autoCompleteInfo.getPic().equalsIgnoreCase(null) && !autoCompleteInfo.getPic().equalsIgnoreCase("null")) {
		   // imageLoader.displayImage(autoCompleteInfo.getPic(), holder.imgProfilePic, optionsDoctor);
		    Glide.with(context).load(autoCompleteInfo.getPic()).placeholder(R.drawable.ic_action_person).transform(new CircleTransform(context)).into(holder.imgProfilePic);
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
		if (autoCompleteInfo.getLocality() != null && !autoCompleteInfo.getLocality().equalsIgnoreCase("")
			&& !autoCompleteInfo.getLocality().equalsIgnoreCase("null")) {
		    holder.txtProfiledesignation.setVisibility(View.VISIBLE);
		    holder.txtProfiledesignation.setText(autoCompleteInfo.getLocality());
		} else {
		    holder.txtProfiledesignation.setVisibility(View.GONE);
		}
		if (autoCompleteInfo.getPic() != null && !autoCompleteInfo.getPic().equalsIgnoreCase("")
			&& !autoCompleteInfo.getPic().equalsIgnoreCase(null) && !autoCompleteInfo.getPic().equalsIgnoreCase("null")) {
		   // imageLoader.displayImage(autoCompleteInfo.getPic(), holder.imgProfilePic, optionHospital);
		    Glide.with(context).load(autoCompleteInfo.getPic()).placeholder(R.drawable.hospital_icon_normal).into(holder.imgProfilePic);
		    holder.txtVerified.setText(context.getString(R.string.msgVisited));
		    holder.imgVerifiedLog.setBackgroundResource(R.drawable.two_tick);
		} else {
		    Glide.with(context).load(R.drawable.hospital_icon_normal).into(holder.imgProfilePic);
		   // holder.imgProfilePic.setImageResource(R.drawable.hospital_icon_normal);
		    holder.txtVerified.setText(context.getString(R.string.msgVerified));
		    holder.imgVerifiedLog.setBackgroundResource(R.drawable.one_tick);
		}
	    }
	    if (autoCompleteInfo.getDistance() != null && !autoCompleteInfo.getDistance().equalsIgnoreCase("")) {

		try {
		    holder.txtDistance.setVisibility(View.VISIBLE);
		    holder.txtProfileKM.setVisibility(View.VISIBLE);
		    double rounded = (double) Math.round(Double.valueOf(autoCompleteInfo.getDistance()) * 100) / 100;
		    DecimalFormat df2 = new DecimalFormat("#####.#");
		    holder.txtDistance.setText("" + Double.valueOf(df2.format(rounded)));

		} catch (NumberFormatException e) {
		    // TODO Auto-generated catch block
		}

	    } else {
		holder.txtDistance.setVisibility(View.GONE);
		holder.txtProfileKM.setVisibility(View.GONE);
	    }
	    if (autoCompleteInfo.getRating() != null && !autoCompleteInfo.getRating().equals(null) && !autoCompleteInfo.getRating().equals("0.0")
		    && !autoCompleteInfo.getRating().equals("null") && !autoCompleteInfo.getRating().equals(0.0)) {
		try {
		    holder.ratingLayout.setVisibility(View.VISIBLE);
		    long lValue = Math.round(Double.valueOf(autoCompleteInfo.getRating()));
		    holder.overAllRating.setRating(lValue);
		    holder.overAllRating.setIsIndicator(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    } else {
		holder.ratingLayout.setVisibility(View.GONE);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
//	holder.imgProfilePic.setOnClickListener(new OnClickListener() {
//
//	    @Override
//	    public void onClick(View v) {
//		if (globalSearchList.get(position).getPic() != null && !globalSearchList.get(position).getPic().trim().equalsIgnoreCase("")
//			&& !globalSearchList.get(position).getPic().trim().equalsIgnoreCase(null)
//			&& !globalSearchList.get(position).getPic().trim().equalsIgnoreCase("null")) {
//		    new EnlargeMultipleImageDialog(context, position).show();
//		}
//
//	    }
//	});
	return row;
    }

    /* holder which holds the view associated to the adapter */
    static class FavouriteHolder {
	TextView txtProfileName;
	TextView txtProfiledesignation;
	TextView txtDistance;
	// TextView txtRatings;
	ImageView imgProfilePic;
	TextView txtProfileKM;
	TextView txtFavRatedValue;
	// TextView txtProfileRated;
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
	    if (globalSearchList.get(position).getPrimaryphone() != null
		    && !globalSearchList.get(position).getPrimaryphone().equalsIgnoreCase("null")
		    && !globalSearchList.get(position).getPrimaryphone().equalsIgnoreCase(null)
		    && !globalSearchList.get(position).getPrimaryphone().equalsIgnoreCase("6855")) {

		String phoneno = globalSearchList.get(position).getPrimaryphone().toString().trim();
		if (phoneno.startsWith("0")) {
		    phoneno = globalSearchList.get(position).getPrimaryphone().toString().trim();
		} else if (phoneno.startsWith("91")) {
		    phoneno = globalSearchList.get(position).getPrimaryphone().toString().trim();
		} else if (phoneno.startsWith("+91")) {
		    phoneno = globalSearchList.get(position).getPrimaryphone().toString().trim();
		} else {
		    phoneno = "0" + globalSearchList.get(position).getPrimaryphone().toString().trim();
		}
		if (globalSearchList.get(position).getType() != null && globalSearchList.get(position).getType().equalsIgnoreCase("Doctor")) {
		    easyTracker.send(MapBuilder.createEvent("Saves", "Call Doctor",
			    globalSearchList.get(position).getDoctype() + ":" + globalSearchList.get(position).getName(), null).build());
		} else {
		    easyTracker.send(MapBuilder.createEvent("Saves", "Call Hospital",
			    globalSearchList.get(position).getLocality() + ":" + globalSearchList.get(position).getName(), null).build());
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

	    if (globalSearchList.get(iPosition).getType() != null && globalSearchList.get(iPosition).getType().equalsIgnoreCase("Doctor")) {
		if (globalSearchList.get(iPosition).getPic() != null) {
		    multiplaeImageList.add(globalSearchList.get(iPosition).getPic());
		}
		if (globalSearchList.get(iPosition).getDocSecondImage() != null) {
		    multiplaeImageList.add(globalSearchList.get(iPosition).getDocSecondImage());
		}

	    } else {
		if (globalSearchList.get(iPosition).getPic() != null) {
		    multiplaeImageList.add(globalSearchList.get(iPosition).getPic());
		}
		if (globalSearchList.get(iPosition).getHospitalEmergencyPic() != null) {
		    multiplaeImageList.add(globalSearchList.get(iPosition).getHospitalEmergencyPic());
		}
		if (globalSearchList.get(iPosition).getHospitalLeftPic() != null) {
		    multiplaeImageList.add(globalSearchList.get(iPosition).getHospitalLeftPic());
		}
		if (globalSearchList.get(iPosition).getHospitalRightPic() != null) {
		    multiplaeImageList.add(globalSearchList.get(iPosition).getHospitalRightPic());
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

	// ArrayList<DoctorDetailInfo> associateDocList;

	public EnlargeImageDialogDoctor(Context context, int position) {
	    super(context);
	    imageLoader = ImageLoader.getInstance();
	    this.context = context;
	    this.position = position;
	    // this.associateDocList = associateDocList;
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

	    if (globalSearchList.get(position).getPic() != null) {
		iCount = 1;
		txtCount.setVisibility(View.VISIBLE);
		txtCount.setText("1 of " + iCount);
		setImageEnLargLoading(globalSearchList.get(position).getPic(), imageViewFirst, optionsEnlarg);
	    }
	    if (globalSearchList.get(position).getDocSecondImage() != null) {
		iCount = 2;
		txtCount.setVisibility(View.VISIBLE);
		txtCount.setText("1 of " + iCount);
	    }
	    if (iCount == 0) {
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

		    if (globalSearchList.get(position).getDocSecondImage() != null) {
			layoutSecondImage.setVisibility(View.VISIBLE);
			txtCount.setText("2 of " + iCount);
			setImageEnLargLoading(globalSearchList.get(position).getDocSecondImage(), imageViewSecond, optionsEnlarg);
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
