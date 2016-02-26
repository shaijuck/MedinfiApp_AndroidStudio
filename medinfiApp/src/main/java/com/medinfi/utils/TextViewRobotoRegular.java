package com.medinfi.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Handle Custom Font  
 * 
 * @author LalBabu
 * 
 */
public class TextViewRobotoRegular extends TextView {

    public TextViewRobotoRegular(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }

    public TextViewRobotoRegular(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public TextViewRobotoRegular(Context context) {
	super(context);
	init();
    }

    private void init() {
	if (!isInEditMode()) {
	    Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoRegular.ttf");
	    setTypeface(tf);
	}
    }
}