package com.medinfi.adapters;

import java.util.ArrayList;

import com.medinfi.R;
import com.medinfi.datainfo.MenuItemInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.utils.ApplicationSettings;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuItemListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MenuItemInfo> menuItemInfo;

    public MenuItemListAdapter(Context context, ArrayList<MenuItemInfo> menuItemInfo) {
	this.context = context;
	this.menuItemInfo = menuItemInfo;
    }

    @Override
    public int getCount() {
	return menuItemInfo.size();
    }

    @Override
    public Object getItem(int position) {
	return menuItemInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	if (convertView == null) {
	    LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	    convertView = mInflater.inflate(R.layout.menu_item_list, null);
	}

	LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.layoutId);
	TextView txtMenuItem = (TextView) convertView.findViewById(R.id.txtMenuItem);
	ImageView menu_item_icon = (ImageView) convertView.findViewById(R.id.menu_item_icon);
	ImageView imgLineDivider = (ImageView) convertView.findViewById(R.id.imgLineDivider);
	ImageView imgLineDividerBelow = (ImageView) convertView.findViewById(R.id.imgLineDividerBelow);
	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	if(menuItemInfo.get(position).getItemName().equalsIgnoreCase("Sign Out")
		||menuItemInfo.get(position).getItemName().equalsIgnoreCase("Sign In")){
	    if (ApplicationSettings.getPref(AppConstants.REGISTERED_USER, false)
			&& ApplicationSettings.getPref(AppConstants.USER_ID, "").trim().length() > 0) {
		 txtMenuItem.setText("Sign Out");
	    }else{
		 txtMenuItem.setText("Sign In");
	    }
	    imgLineDivider.setVisibility(View.VISIBLE);
	    imgLineDividerBelow.setVisibility(View.VISIBLE);
	}else{
	    txtMenuItem.setText(menuItemInfo.get(position).getItemName());
	    imgLineDivider.setVisibility(View.GONE);
	    imgLineDividerBelow.setVisibility(View.GONE); 
	}

	if(position < 5){
	    menu_item_icon.setVisibility(View.VISIBLE);
	    menu_item_icon.setImageResource(menuItemInfo.get(position).getMenuIcon());  
	}else{
	    menu_item_icon.setVisibility(View.GONE);
	    menu_item_icon.setImageResource(android.R.color.transparent);  
	}
	if(position > 5){
	    txtMenuItem.setTextSize(16);
	    if(menuItemInfo.get(position).getItemName().equalsIgnoreCase("Rate the App")){
		layoutParams.setMargins(0, 27, 0, 17); 
	    }else{
		layoutParams.setMargins(0, 17, 0, 17); 
	    }
	    linearLayout.setLayoutParams(layoutParams);
	}else{
	    layoutParams.setMargins(0, 27, 0, 27); 
	    linearLayout.setLayoutParams(layoutParams);  
	}

	return convertView;
    }

}
