package com.medinfi.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.medinfi.R;
import com.medinfi.datainfo.SearchInfo;

public class LocalitySearchAdapter extends ArrayAdapter<SearchInfo> {

    Context context;
    int layoutResourceId;
    public ArrayList<SearchInfo> searchList;
    private ArrayList<SearchInfo> arraylist;

    public LocalitySearchAdapter(Context context, int layoutResourceId, ArrayList<SearchInfo> searchList) {
	super(context, layoutResourceId, searchList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.searchList = searchList;
	this.arraylist = new ArrayList<SearchInfo>();
	this.arraylist.addAll(searchList);
    }

    /*
     * add the view based on the count of arraylist set the values using getter
     * setter methods
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
	View row = convertView;
	SearchHolder holder = null;

	if (row == null) {
	    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    row = inflater.inflate(layoutResourceId, parent, false);

	    holder = new SearchHolder();
	    holder.txtSearchName = (CheckedTextView) row.findViewById(R.id.searchitemid);
	    Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoLight.ttf");
	    holder.txtSearchName.setTypeface(tf);

	    row.setTag(holder);

	} else {
	    holder = (SearchHolder) row.getTag();
	}

	SearchInfo searchInfo = searchList.get(position);
	holder.txtSearchName.setText(searchInfo.getSearchName());
	return row;
    }

    static class SearchHolder {
	CheckedTextView txtSearchName;

    }

    // Filter Class
    public void filter(String charText) {
	charText = charText.toLowerCase(Locale.getDefault());
	searchList.clear();
	if (charText.length() == 0) {
	    searchList.addAll(arraylist);
	} else {
	    for (SearchInfo wp : arraylist) {
		if ((wp.getSearchName().toLowerCase(Locale.getDefault()).startsWith(charText))) {
		    searchList.add(wp);
		}
	    }
	    for (SearchInfo wp : arraylist) {
		if ((wp.getSearchName().toLowerCase(Locale.getDefault()).contains(charText))) {
		    searchList.add(wp);
		}
	    }

	    Object[] st = searchList.toArray();
	    for (Object s : st) {
		if (searchList.indexOf(s) != searchList.lastIndexOf(s)) {
		    searchList.remove(searchList.lastIndexOf(s));
		}
	    }

	}
	notifyDataSetChanged();
    }

}
