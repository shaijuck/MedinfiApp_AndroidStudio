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
public class TextViewRobotoCondensed extends TextView {

    public TextViewRobotoCondensed(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }

    public TextViewRobotoCondensed(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public TextViewRobotoCondensed(Context context) {
	super(context);
	init();
    }

    private void init() {
	if (!isInEditMode()) {
	    Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoCondensed.ttf");
	    setTypeface(tf);
	}
    }
}