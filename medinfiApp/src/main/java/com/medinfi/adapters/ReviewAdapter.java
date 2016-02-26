package com.medinfi.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.medinfi.R;
import com.medinfi.datainfo.ReviewsInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ReviewAdapter extends ArrayAdapter<ReviewsInfo> {
	Context context;
	int layoutResourceId;
	private List<ReviewsInfo> reviewList = null;
	private ArrayList<ReviewsInfo> arraylist;
	private ImageLoader imageLoader;
	DisplayImageOptions options;
	private Typeface tfBold, tfRegular, tfLite,tfCondensed;
	public ReviewAdapter(Context context, int layoutResourceId,
			ArrayList<ReviewsInfo> reviewList) {
		super(context, layoutResourceId, reviewList);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.reviewList = reviewList;
		this.arraylist = new ArrayList<ReviewsInfo>();
		this.arraylist.addAll(reviewList);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.ic_action_person)
				.showImageOnFail(R.drawable.ic_action_person)
				.showImageOnLoading(R.drawable.ic_action_person).build();
		tfBold = Typeface.createFromAsset(context.getAssets(),
				"fonts/RobotoBold.ttf");
		tfLite = Typeface.createFromAsset(context.getAssets(),
				"fonts/RobotoLight.ttf");
		tfRegular = Typeface.createFromAsset(context.getAssets(),
				"fonts/RobotoRegular.ttf");
		tfCondensed= Typeface.createFromAsset(context.getAssets(),
				"fonts/RobotoCondensed.ttf");
	}

	@Override
	public ReviewsInfo getItem(int position) {
		return reviewList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	/*add the view based on the count of arraylist set the values using getter setter methods*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RwviewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RwviewHolder();
			holder.doctorName = (TextView) row
					.findViewById(R.id.review_name);
			holder.reviewDescription = (TextView) row
					.findViewById(R.id.review_description);
			holder.reviewDate = (TextView) row
					.findViewById(R.id.review_date);
			
			holder.doctorName.setTypeface(tfRegular);
			
			holder.reviewDescription.setTypeface(tfLite);
			
			holder.reviewDate.setTypeface(tfLite);
			row.setTag(holder);
		} else {
			holder = (RwviewHolder) row.getTag();
		}

		ReviewsInfo reviewInfo = reviewList.get(position);

		
		holder.doctorName.setText(reviewInfo.getUserName());
		holder.reviewDescription.setText(reviewInfo.getReview());
		holder.reviewDate.setText(reviewInfo.getCreatedDate());
		return row;
	}

	static class RwviewHolder {
		TextView doctorName;
		TextView reviewDescription;
		TextView reviewDate;
		
	}

	// Filter for search
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		reviewList.clear();
		if (charText.length() == 0) {
			reviewList.addAll(arraylist);
		} else {
			for (ReviewsInfo wp : arraylist) {
				if (wp.getReview().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					reviewList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	}

}