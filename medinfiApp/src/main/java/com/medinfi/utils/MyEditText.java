package com.medinfi.utils;

import com.medinfi.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MyEditText extends EditText {
    private static final int MAX_LENGTH = 100;

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //this.setBackgroundResource(android.R.drawable.edit_text);
        }

        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public synchronized boolean onEditorAction(TextView v,
                    int actionId, KeyEvent event) {
                /*if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;*/
            	 if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
            	        return false;
            	    } else if (actionId == EditorInfo.IME_ACTION_SEARCH
            	        || event == null
            	        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            	    	 return true;
            	    }
				return false;
            }
        });

        String value = "";
        final String viewMode = "editing";
        final String viewSide = "right";
        final Drawable x = getResources().getDrawable(android.R.drawable.ic_input_delete);

        // The height will be set the same with [X] icon
        setHeight(x.getBounds().height());

        x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
        final Drawable x2 = viewMode.equals("never") ? null : viewMode
                .equals("always") ? x : viewMode.equals("editing") ? (value
                .equals("") ? null : x)
                : viewMode.equals("unlessEditing") ? (value.equals("") ? x
                        : null) : null;
        // Display search icon in text field
        final Drawable searchIcon = getResources().getDrawable(
                R.drawable.search);
        searchIcon.setBounds(0, 0, x.getIntrinsicWidth()/2,
                x.getIntrinsicHeight()/2);

        setCompoundDrawables(searchIcon, null, viewSide.equals("right") ? x2
                : null, null);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getCompoundDrawables()[viewSide.equals("left") ? 0 : 2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                // x pressed
                if ((viewSide.equals("left") && event.getX() < getPaddingLeft()
                        + x.getIntrinsicWidth())
                        || (viewSide.equals("right") && event.getX() > getWidth()
                                - getPaddingRight() - x.getIntrinsicWidth())) {
                    Drawable x3 = viewMode.equals("never") ? null : viewMode
                            .equals("always") ? x
                            : viewMode.equals("editing") ? null : viewMode
                                    .equals("unlessEditing") ? x : null;
                    setText("");
                    setCompoundDrawables(searchIcon, null,
                            viewSide.equals("right") ? x3 : null, null);
                }
                return false;
            }
        });
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            	System.out.println("char"+s);
            //	setCompoundDrawables(null, null,null, null);
            	if(s.length()>0)
                	setCompoundDrawables(null, null,null, null);
            	else
            		setCompoundDrawables(searchIcon, null, viewSide.equals("right") ? x2
                            : null, null);
            		
                Drawable x4 = viewMode.equals("never") ? null : viewMode
                        .equals("always") ? x
                        : viewMode.equals("editing") ? (getText().toString()
                                .equals("") ? null : x) : viewMode
                                .equals("unlessEditing") ? (getText()
                                .toString().equals("") ? x : null) : null;
//                setCompoundDrawables(searchIcon, null,
//                        viewSide.equals("right") ? x4 : null, null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > MAX_LENGTH) {
                    setText(s.subSequence(0, MAX_LENGTH));
                    setSelection(MAX_LENGTH);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }
        });
    }
}