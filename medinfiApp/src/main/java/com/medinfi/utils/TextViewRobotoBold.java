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
public class TextViewRobotoBold extends TextView {

    public TextViewRobotoBold(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }

    public TextViewRobotoBold(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public TextViewRobotoBold(Context context) {
	super(context);
	init();
    }

    private void init() {
	if (!isInEditMode()) {
	    Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoBold.ttf");
	    setTypeface(tf);
	}
    }
}