package com.medinfi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.utils.Utils;

public class LoadUrlActivity extends Activity {
 
    private WebView webView;
    private boolean isTrue=false;
    
	private EasyTracker easyTracker = null;
    private Typeface tfRegular;
    private LinearLayout backLayout;

    public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.show_web_view);
	easyTracker = EasyTracker.getInstance(LoadUrlActivity.this);
	/* show ActionBar in the screen */
	/*getSupportActionBar().show();
	getSupportActionBar().setIcon(null);
	getSupportActionBar().setDisplayUseLogoEnabled(false);
	getSupportActionBar().setHomeButtonEnabled(false);
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
	// Get webview
	webView = (WebView) findViewById(R.id.webView1);
	tfRegular = Typeface.createFromAsset(this.getAssets(), "fonts/RobotoRegular.ttf");
	showLocation();
	if (isNetworkAvailable()) {
	    webView.setVisibility(View.VISIBLE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
	    startWebView(getIntent().getExtras().getString("key"));
	} else {
	    webView.setVisibility(View.GONE);
	    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
	    ((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tfRegular);
	    ((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tfRegular);
	}
	backClickEvent();
    }

    private void backClickEvent() {
	backLayout = (LinearLayout) findViewById(R.id.backLayout);
	backLayout.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (webView.canGoBack()) {
		    webView.goBack();
		} else {
		    // Let the system handle the back button
		    LoadUrlActivity.this.finish();
		}

	    }
	});
    }
    private void startWebView(String url) {
         
        //Create new webview Client to show progress dialog
        //When opening a url or click on link
         
        webView.setWebViewClient(new WebViewClient() {      
            ProgressDialog progressDialog;
          
            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {              
                view.loadUrl(url);
                return true;
            }
            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
            	if(!isTrue)
            	{
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(LoadUrlActivity.this);
                    progressDialog.setMessage(Utils.getCustomeFontStyle(LoadUrlActivity.this, getString(R.string.screenLoading)));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    isTrue=true;
                }
            	}
            }
            public void onPageFinished(WebView view, String url) {
                try{
                	if (isTrue) {
                		if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }		
					}
                
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }
             
        }); 
          
         // Javascript inabled on webview  
        webView.getSettings().setJavaScriptEnabled(true); 
         
      
        
        webView.loadUrl(url);
          
          
    }
     
   /* @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item)
 {
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 && item.getTitleCondensed() != null) {
	    item.setTitleCondensed(item.getTitleCondensed().toString());
	}

	switch (item.getItemId()) {

	case android.R.id.home:
	    // ApplicationSettings.putPref(AppConstants.IS_SHOW_LOCATION, true);
	    // startActivity(new
	    // Intent(GeneralPhysicansActivity.this,LocationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

	    if (webView.canGoBack()) {
		webView.goBack();
	    } else {
		// Let the system handle the back button
		LoadUrlActivity.this.finish();
	    }
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }*/
    // Open previous opened link from history on webview when back button pressed
     
    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }
    
	/*get the data from preferences use spannable string to show location in action bar add custom color, fonts to it*/
    public void showLocation() {

	if (getIntent().getExtras().getString("title").equalsIgnoreCase("TOU")) {

	    SpannableString s = new SpannableString(Html.fromHtml("<font color=\"#000000\"><b>" + getResources().getString(R.string.terms_of_use)
		    + "</b></font>"));
	    s.setSpan(new com.medinfi.utils.TypefaceSpan(this, "RobotoRegular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	   // getSupportActionBar().setTitle(s);
	    ((TextView)findViewById(R.id.textView)).setText(s);
	} else {

	    SpannableString s = new SpannableString(Html.fromHtml("<font color=\"#000000\"><b>" + getResources().getString(R.string.privacy_policy)
		    + "</b></font>"));
	    s.setSpan(new com.medinfi.utils.TypefaceSpan(this, "RobotoRegular.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

	   // getSupportActionBar().setTitle(s);
	    ((TextView)findViewById(R.id.textView)).setText(s);
	}

    }
	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(this,  getResources().getString(R.string.FlurryAPIKey));
		FlurryAgent.onStartSession(this, getResources().getString(R.string.FlurryAPIKey));
		 FlurryAgent.logEvent("LoadUrl Activity");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
		EasyTracker.getInstance(this).activityStop(this);
	}
	/*method to check the network*/
	public boolean isNetworkAvailable() {
		boolean isConnectedWifi = false;
		boolean isConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equals(ni.getTypeName()))
				if (ni.isConnected())
					isConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase(ni.getTypeName()))
				if (ni.isConnected())
					isConnectedMobile = true;
		}
		return isConnectedWifi || isConnectedMobile;
	}
 }
