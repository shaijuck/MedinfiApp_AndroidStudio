package com.medinfi.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medinfi.R;
import com.medinfi.datainfo.SpecialistInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
public class SpecialistAdapter extends ArrayAdapter<SpecialistInfo>
{
    Context mContext;
    int resourceId;
    ArrayList<SpecialistInfo> data = new ArrayList<SpecialistInfo>();
    private ArrayList<SpecialistInfo> arraylist;
   private ImageLoader imageLoader;
	DisplayImageOptions options;
	ViewHolder holder = null;
    public SpecialistAdapter(Context context, int layoutResourceId, ArrayList<SpecialistInfo> data)
    {
        super(context, layoutResourceId, data);
        this.mContext = context;
        this.resourceId = layoutResourceId;
        this.data = data;
        this.arraylist = new ArrayList<SpecialistInfo>();
		this.arraylist.addAll(data);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.build();
		
		/*.showImageForEmptyUri(R.drawable.specialist_icon_normal)
		.showImageOnFail(R.drawable.specialist_icon_normal)
		.showImageOnLoading(R.drawable.specialist_icon_normal)*/
    }
    /*add the view based on the count of arraylist set the values using getter setter methods*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View itemView = convertView;
        

        if (itemView == null)
        {
            final LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(resourceId, parent, false);
            holder = new ViewHolder();
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(),
		            "fonts/RobotoRegular.ttf");
            Typeface tfLite = Typeface.createFromAsset(mContext.getAssets(),
		            "fonts/RobotoLight.ttf");
           // holder.imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            holder.txtItem = (TextView) itemView.findViewById(R.id.txtprofile);
            holder.txtItem.setTypeface(tf);
            holder.txtdescription = (TextView) itemView.findViewById(R.id.txtdescription);
            holder.txtdescription.setTypeface(tfLite);
            holder.tick=(ImageView) itemView.findViewById(R.id.tick);
            itemView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) itemView.getTag();
        }

        SpecialistInfo item = getItem(position);
      //  holder.imgItem.setImageDrawable(item.getSpecialistImage());
        holder.txtItem.setText(item.getSpecialistName().toUpperCase().toString());
        
        if(item.getSpecialistDescription()!=null && !item.getSpecialistDescription().equalsIgnoreCase("") && !item.getSpecialistDescription().equalsIgnoreCase(null)  && !item.getSpecialistDescription().equalsIgnoreCase("null"))
        {
        	holder.txtdescription.setText(item.getSpecialistDescription());
        	holder.txtdescription.setVisibility(View.VISIBLE);
        }
        else
        {
        	holder.txtdescription.setVisibility(View.INVISIBLE);
        }
        
        
        /*if(item.getSpecialistImage()!=null && !item.getSpecialistImage().equalsIgnoreCase("") && !item.getSpecialistImage().equalsIgnoreCase(null)  && !item.getSpecialistImage().equalsIgnoreCase("null"))
        	{
        		imageLoader.displayImage(item.getSpecialistImage(), holder.imgItem, options);
        		holder.imgItem.setVisibility(View.VISIBLE);
        	}
			else
			{
				holder.imgItem.setImageResource(R.drawable.specialist_icon_normal);
				holder.imgItem.setVisibility(View.INVISIBLE);
			}
        
        holder.tick.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				holder.tick.setAlpha(1);
			}
		});*/
        
        return itemView;
    }

    static class ViewHolder
    {
        //ImageView imgItem;
        TextView txtItem;
        TextView txtdescription;
        ImageView tick;
    }
 // Filter Class
    public void filter(String charText) {
	charText = charText.toLowerCase(Locale.getDefault());
	data.clear();
	if (charText.length() == 0) {
	    data.addAll(arraylist);
	} else {
	    for (SpecialistInfo wp : arraylist) {
		if ((wp.getSpecialistName()!=null && wp.getSpecialistName().toLowerCase(Locale.getDefault()).startsWith(charText))
			||(wp.getSpecialistDescription()!=null && wp.getSpecialistDescription().toLowerCase(Locale.getDefault()).contains(charText))) {
		    data.add(wp);
		}
	    }
	}
	notifyDataSetChanged();
    }

}
