package com.medinfi.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.medinfi.R;
import com.medinfi.datainfo.DoctorInfo;
import com.medinfi.datainfo.ProfileInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class EnlargeImageDialog extends Dialog implements android.view.View.OnClickListener {
    Context mContext;
    private int iPosition;
    RelativeLayout layoutSecondImage;
    DisplayImageOptions optionsEnlarg;
    private ArrayList<String> multiplaeImageList = new ArrayList<String>();
    int iCunt;

    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    String[] imageArray;
    ArrayList<ProfileInfo> profileArrayList;

    public EnlargeImageDialog(Context mContext, int iPosition, ArrayList<ProfileInfo> profileArrayList) {
	super(mContext);
	this.mContext = mContext;
	this.iPosition = iPosition;
	this.profileArrayList = profileArrayList;
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
	//layout.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	 layout.setBackgroundColor(Color.WHITE);
	layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
	// layout.setMinimumHeight((int) (displayRectangle.height() *
	// 0.9f));

	//optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

	setContentView(layout);
	Button btnCancel = (Button) findViewById(R.id.close_button_hs);
	btnCancel.setOnClickListener(this);

	if (profileArrayList.get(iPosition).getProfileImage() != null) {
	    multiplaeImageList.add(profileArrayList.get(iPosition).getProfileImage());
	}
	if (profileArrayList.get(iPosition).getSecondImage() != null) {
	    multiplaeImageList.add(profileArrayList.get(iPosition).getSecondImage());
	}

	imageArray = new String[multiplaeImageList.size()];

	for (int i = 0; i < multiplaeImageList.size(); i++) {
	    imageArray[i] = multiplaeImageList.get(i);
	}

	viewPager = (ViewPager) findViewById(R.id.myviewpager);
	myPagerAdapter = new MyPagerAdapter(mContext, imageArray);
	viewPager.setAdapter(myPagerAdapter);

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
	    //optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
	    optionsEnlarg = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().imageScaleType(ImageScaleType.NONE).bitmapConfig(Bitmap.Config.ARGB_8888).build();
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
	//    layout.setBackgroundColor(Color.parseColor("#80000000"));
	 //   layout.setBackgroundColor(Color.WHITE);
//	    layout.setAlpha(130);
//	    layout.setStyle(android.R.style.Theme_Holo_Light_Dialog);
//	    layout.getD
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
		System.out.println("imageUrl"+imageUrl);

//		 Glide.with(context).load(imageUrl).placeholder(R.drawable.hospital_icon_normal).into(new GlideDrawableImageViewTarget(imgProfilePicDisplay) {
//				
//				
//				@Override
//			public void onStart() {
//				super.onStart();
//				 progressBar.setVisibility(View.VISIBLE);
//				    textView.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onStop() {
//				super.onStop();
//			}
//
//				@Override
//				public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
//				    super.onResourceReady(drawable, anim);
//				   // holder.progressBar.setVisibility(View.GONE);
//				    progressBar.setVisibility(View.GONE);
//				}
//				
//				@Override
//				public void onLoadFailed(Exception e, Drawable errorDrawable) {
//				    super.onLoadFailed(e, errorDrawable);
//				   // holder.progressBar.setVisibility(View.GONE);
//				    progressBar.setVisibility(View.GONE);
//				}
//			    });
		
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
