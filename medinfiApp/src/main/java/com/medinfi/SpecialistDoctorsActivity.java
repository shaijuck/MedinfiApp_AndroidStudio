package com.medinfi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.HospitalsActivity.GetGlobalSearchList;
import com.medinfi.adapters.AutoCompleteAdapter;
import com.medinfi.adapters.DoctorsAdapter;
import com.medinfi.adapters.SpecialistAdapter;
import com.medinfi.datainfo.AutoCompleteInfo;
import com.medinfi.datainfo.ProfileInfo;
import com.medinfi.datainfo.SpecialistInfo;
import com.medinfi.main.AppConstants;
import com.medinfi.parser.JSONParser;
import com.medinfi.pulltorefresh.library.PullToRefreshBase;
import com.medinfi.pulltorefresh.library.PullToRefreshBase.Mode;
import com.medinfi.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.medinfi.pulltorefresh.library.PullToRefreshListView;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.CreateSessionIdAsyncTask;
import com.medinfi.utils.Utils;

public class SpecialistDoctorsActivity extends Activity {

	/* declare xml views */
	 private ListView globalListView;
	private PullToRefreshListView doctorsListView;
	private DoctorsAdapter doctorAdapter;
	private ArrayList<ProfileInfo> doctorList;
	// private TextView nearmeTextView;
	private TextView gpTextView;
	// private TextView topratedTextView;
	private Typeface tf;
	// private View nearbyView;
	// private View topratedView;
	private com.actionbarsherlock.widget.SearchView mSearchView;
	private AutoCompleteTextView searchText;
	private EasyTracker easyTracker = null;
	private Toast toast = null;

	private ArrayList<AutoCompleteInfo> autocompleteList;
	private AutoCompleteAdapter autoCompleteAdapter;
	private TextView actionbarTitle, actionbarSubtitle;
	public boolean isTrueToast = false;
	private LinearLayout searchLayout;
	private LinearLayout titleLayout;
	private ImageView searchImageView;

	private GetSpecialistDoctorsList getSpecialistDoctorsList;
	private boolean isTrue = false;
	private ProgressDialog pdia;
	private boolean isHomePressed = false;
	private static boolean isEventPressed = false;
	// private LinearLayout layoutBack;
	private String strSearchText;
	private EditText searchEditText;
	private boolean isAllDataAvailable = false;
	private static SpecialistDoctorsActivity specialistDoctorsScreen;
	private TextView txtNoResultFound;
	private String preServerTimeSpend = "";
	private String serverTimeSpend = "";
	private String postServerTimepend = "";
	private String totalScreenTimeSpend = "";
	private String specialistName;
	private boolean isPullToRefreshFromEnd = false;
	private ArrayList<AutoCompleteInfo> globalSearchList;
	private GetGlobalSearchList asyncTask;  
	
	/* declare ids for xml views */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.specialist_doctors_screen);
		specialistDoctorsScreen = this;
		easyTracker = EasyTracker.getInstance(SpecialistDoctorsActivity.this);
		ApplicationSettings.putPref(AppConstants.LAST_COUNT, "0");
		tf = Typeface.createFromAsset(this.getAssets(),
				"fonts/RobotoRegular.ttf");
		/*
		 * getSupportActionBar().show(); getSupportActionBar().setIcon(null);
		 * getSupportActionBar().setDisplayUseLogoEnabled(false);
		 * getSupportActionBar().setHomeButtonEnabled(false);
		 * getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		 */
		// customActionbar();
		actionbarTitle = (TextView) findViewById(R.id.title);
		actionbarSubtitle = (TextView) findViewById(R.id.subtitle);
		// layoutBack = (LinearLayout) findViewById(R.id.layoutBack);
		searchEditText = (EditText) findViewById(R.id.searchedittext);
		searchEditText.setTypeface(tf);
		showLocation();
		// nearbyView = findViewById(R.id.nearme_view_specialistd);
		// nearbyView.setVisibility(View.VISIBLE);
		// topratedView = findViewById(R.id.toprated_view_specialistd);
		gpTextView = (TextView) findViewById(R.id.gp_text_specialistd);
		gpTextView.setTypeface(tf);
		if(!getIntent().getExtras().getString("key_specialist").equals(""))
		gpTextView.setText(getIntent().getExtras().getString("key_specialist"));
		else
		gpTextView.setVisibility(View.GONE);
		
		//searchEditText.setHint(Html.fromHtml(getString(R.string.html)));
		
		// nearmeTextView = (TextView)
		// findViewById(R.id.nearme_textview_specialistd);
		// nearmeTextView.setTypeface(tf);
		// topratedTextView = (TextView)
		// findViewById(R.id.toprated_textview_specialistd);

		// topratedTextView.setTypeface(tf);
		globalSearchList = new ArrayList<AutoCompleteInfo>();
		globalListView = (ListView) findViewById(R.id.globalListView);
		doctorsListView = (PullToRefreshListView) findViewById(R.id.doctorsListView);
		txtNoResultFound = (TextView) findViewById(R.id.txtNoResultFound);
		txtNoResultFound.setVisibility(View.GONE);
		isAllDataAvailable = false;
		// clickTextViews();
		if (AppConstants.LogLevel == 1) {
			Utils.startPreServerCallTime = System.currentTimeMillis();
			Utils.startTotalScreenTime = System.currentTimeMillis();
			Utils.startTotalScreenTime20 = System.currentTimeMillis();
		}
		if (getIntent().getExtras().getString("key_specialist") != null) {
			specialistName = getIntent().getExtras()
					.getString("key_specialist");
		}
		
		if (isNetworkAvailable()) {
			doctorsListView.setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.layoutNoNetWork))
					.setVisibility(View.GONE);
			if (getIntent().getExtras().getString("key_specialistID") != null
					&& !getIntent().getExtras().getString("key_specialistID")
							.equalsIgnoreCase("")) {
				doctorList = new ArrayList<ProfileInfo>();
				
				//Log.i("SpecialID","ID : "+getIntent().getExtras().getString("key_specialistID"));	
				//checking data in cache if available load the data or call data from server
				if(!ApplicationSettings.getPref(AppConstants.DOCTOR_CACHE, "").equals("") &&
						getIntent().getExtras().getString("key_specialistID").equals("-999"))
				{	
					// Log.i("DocActiv","Cached Data");
					 loadData(ApplicationSettings.getPref(AppConstants.DOCTOR_CACHE, ""));
				}
				else{
					getSpecialistDoctorsList = new GetSpecialistDoctorsList();
					getSpecialistDoctorsList.execute();
				}
				 
				
//				specialistDoctorDetailsLoadMore(getIntent().getExtras()
//						.getString("key_specialistID"), "-1");
			}

			/*
			 * specialistDoctorDetails(
			 * getIntent().getExtras().getString("key_specialistID"),
			 * AppConstants.OFFSET_DEFAULT_VALUE);
			 */

		} else {
			// Toast.makeText(SpecialistDoctorsActivity.this,
			// "Network not Available", Toast.LENGTH_SHORT).show();
			doctorsListView.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutNoNetWork))
					.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.txtNoNetwork)).setTypeface(tf);
			((TextView) findViewById(R.id.txtNoNetworkDesc)).setTypeface(tf);
		}
		isHomePressed = false;
		isEventPressed = false;

		//searchSpecialistDoctor(searchEditText);
		globalSearch(searchEditText);
		globalSearchListViewClickEvent();
		clickEvent();
		// backClickEvent();
		// locationClickEvent();
		tabClickEvent();
		Utils.startSuggestionActivity(this, txtNoResultFound,
				"Doctor Tab", easyTracker);
	}

	// private void backClickEvent() {
	// layoutBack.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// easyTracker.send(MapBuilder.createEvent("Doctor Tab", "Back",
	// "Back", null).build());
	// SpecialistDoctorsActivity.this.finish();
	//
	// }
	// });
	// }
	private void locationClickEvent() {
		LinearLayout layoutLocation = (LinearLayout) findViewById(R.id.layoutLocation);
		layoutLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startLocationUpdate();
			}
		});
	}

	private void startLocationUpdate() {
		Intent intent = new Intent(SpecialistDoctorsActivity.this,
				LocationUpdateActivity.class);
		intent.putExtra(AppConstants.CALLING_SCREEN_KEY,
				AppConstants.CALLING_SCREEN_VALUE);
		startActivity(intent);
	}

	/* API call for specialist doctors */

	public void specialistDoctorDetails(final String specialistID,
			final String offest) {
	}

	final class GetSpecialistDoctorsList extends AsyncTask<Void, Void, String> {

		ProgressDialog pdia;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (AppConstants.LogLevel == 1) {
				Utils.startServerTime = System.currentTimeMillis();
				preServerTimeSpend = Utils.preServerTimeSpend();
			}
			pdia = new ProgressDialog(SpecialistDoctorsActivity.this);
			pdia.setCancelable(true);
			pdia.setOnCancelListener(cancelListener);
			pdia.setMessage(Utils.getCustomeFontStyle(
					SpecialistDoctorsActivity.this,
					getString(R.string.screenLoading)));
			pdia.show();
		}

		// 200793653151
		@Override
		protected String doInBackground(Void... arg0) {
			isTrue = true;
			String result = null;
			try {
				JSONParser jparser = new JSONParser();
				String latitude = ApplicationSettings.getPref(
						AppConstants.LATITUDE, "");
				String longitude = ApplicationSettings.getPref(
						AppConstants.LONGITUDE, "");
				Log.i("DocActivity","ServerCall : "+getIntent().getExtras().getString("key_specialistID"));
				result = jparser.getSpecialistsbyID(getIntent().getExtras()
						.getString("key_specialistID"), latitude, longitude,
						AppConstants.OFFSET_DEFAULT_VALUE);
				if (AppConstants.LogLevel == 1) {
					serverTimeSpend = Utils.serverTimeSpend();
					Utils.startPostServerCallTime = System.currentTimeMillis();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			return result.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			if(ApplicationSettings.getPref(AppConstants.DOCTOR_CACHE, "").equals(""))
			{
				ApplicationSettings.putPref(AppConstants.DOCTOR_CACHE,result);
			}
			if (pdia != null && pdia.isShowing())
				pdia.dismiss();
			  loadData(result);
//			try {
//				if (result != null) {
//					doctorList.clear();
//					JSONArray jsonarray = new JSONArray(result.toString());
//					if (jsonarray != null && jsonarray.length() > 0) {
//						for (int i = 0; i < jsonarray.length(); i++) {
//							JSONObject obj = jsonarray.getJSONObject(i);
//							ProfileInfo profileInfo = new ProfileInfo();
//							if (obj.getString("id") != null
//									&& !obj.getString("id").equalsIgnoreCase(
//											"null")) {
//								profileInfo.setProfileId(obj.getString("id"));
//								String fullName = null;
//								String firstName = obj.getString("firstname");
//								String middlename = obj.getString("middlename");
//
//								String lastname = obj.getString("lastname");
//
//								if (firstName != null
//										&& !firstName.equalsIgnoreCase("")
//										&& !firstName.equalsIgnoreCase(null)
//										&& !firstName.equalsIgnoreCase("null")) {
//									fullName = firstName.trim();
//									profileInfo.setFirstName(firstName);
//								}
//								if (middlename != null
//										&& !middlename.equalsIgnoreCase("")
//										&& !middlename.equalsIgnoreCase(null)
//										&& !middlename.equalsIgnoreCase("null")) {
//									fullName = fullName + " "
//											+ middlename.trim();
//									profileInfo.setMiddleName(middlename);
//								}
//								if (lastname != null
//										&& !lastname.equalsIgnoreCase("")
//										&& !lastname.equalsIgnoreCase("null")) {
//									fullName = fullName + " " + lastname.trim();
//									profileInfo.setLastName(lastname);
//								}
//								profileInfo.setProfileName(fullName.toString()
//										.replace("?", ""));
//
//								if (obj.has("specialityName") && obj.getString("specialityName") != null && !obj.getString("specialityName").equalsIgnoreCase("")
//										&& !obj.getString("specialityName").equalsIgnoreCase(null)) {
//									profileInfo.setSpecialityName(obj.getString("specialityName").replace("?", ""));
//									}
//								
//								if (obj.has("qualification")
//										&& obj.getString("qualification") != null
//										&& !obj.getString("qualification")
//												.equalsIgnoreCase("")
//										&& !obj.getString("qualification")
//												.equalsIgnoreCase(null))
//									profileInfo.setProfileDesignation(obj
//											.getString("qualification"));
//
//								if (obj.has("phone")
//										&& obj.getString("phone") != null
//										&& !obj.getString("phone")
//												.equalsIgnoreCase("")
//										&& !obj.getString("phone")
//												.equalsIgnoreCase(null))
//									profileInfo.setProfilePhoneNumber(obj
//											.getString("phone"));
//
//								if (obj.has("email")
//										&& obj.getString("email") != null
//										&& !obj.getString("email")
//												.equalsIgnoreCase("")
//										&& !obj.getString("email")
//												.equalsIgnoreCase(null))
//									profileInfo.setProfileEmail(obj
//											.getString("email"));
//								if (obj.has("distance")
//										&& obj.getString("distance") != null
//										&& !obj.getString("distance")
//												.equalsIgnoreCase("")
//										&& !obj.getString("distance")
//												.equalsIgnoreCase(null)
//										&& !obj.getString("distance")
//												.equalsIgnoreCase("9999")) {
//
//									profileInfo.setProfileDistance(obj
//											.getString("distance"));
//								}
//								if (obj.has("pic")
//										&& obj.getString("pic") != null
//										&& !obj.getString("pic").trim()
//												.equalsIgnoreCase("")
//										&& !obj.getString("pic").trim()
//												.equalsIgnoreCase(null))
//									profileInfo.setProfileImage(obj.getString(
//											"pic").trim());
//
//								if (obj.has("second_image")
//										&& obj.getString("second_image") != null
//										&& !obj.getString("second_image")
//												.trim().equalsIgnoreCase("")
//										&& !obj.getString("second_image")
//												.trim().equalsIgnoreCase(null)) {
//									profileInfo.setSecondImage(obj.getString(
//											"second_image").trim());
//								}
//
//								if (obj.has("awardRecog")
//										&& obj.getString("awardRecog") != null
//										&& !obj.getString("awardRecog")
//												.equalsIgnoreCase("")
//										&& !obj.getString("awardRecog")
//												.equalsIgnoreCase(null))
//									profileInfo.setProfileAwards(obj
//											.getString("awardRecog"));
//								if (obj.has("avgrate")
//										&& obj.getString("avgrate") != null
//										&& !obj.getString("avgrate")
//												.equalsIgnoreCase("")
//										&& !obj.getString("avgrate")
//												.equalsIgnoreCase(null))
//									profileInfo.setProfileRating(obj
//											.getString("avgrate"));
//								if (obj.has("hospitalName")
//										&& obj.getString("hospitalName") != null
//										&& !obj.getString("hospitalName")
//												.equalsIgnoreCase("")
//										&& !obj.getString("hospitalName")
//												.equalsIgnoreCase(null)) {
//									String hospitalName;
//									if (obj.getString("hospitalName")
//											.toString().contains(",")) {
//
//										hospitalName = obj
//												.getString("hospitalName")
//												.toString().split(",")[0];
//									} else {
//										hospitalName = obj.getString(
//												"hospitalName").toString();
//									}
//									profileInfo.setHospitalName(hospitalName
//											.replace("?", ""));
//								}
//								if (AppConstants.LogLevel == 1) {
//									try {
//										if (obj.has("server_processtime")
//												&& obj.getString("server_processtime") != null
//												&& !obj.getString(
//														"server_processtime")
//														.equalsIgnoreCase("")
//												&& !obj.getString(
//														"server_processtime")
//														.equalsIgnoreCase(null)) {
//											ApplicationSettings
//													.putPref(
//															AppConstants.SERVER_PROCESSTIME,
//															obj.getString("server_processtime"));
//										}
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//
//								doctorList.add(profileInfo);
//							}
//						}
//
//						if (doctorList != null && doctorList.size() > 0) {
//
//							ApplicationSettings.putPref(
//									AppConstants.LAST_COUNT,
//									"" + doctorList.size());
//							doctorAdapter = new DoctorsAdapter(
//									SpecialistDoctorsActivity.this,
//									R.layout.profile_list_item, doctorList,getIntent().getExtras().getString("key_specialistID"));
//							doctorsListView.setAdapter(doctorAdapter);
//
//							if (!isAllDataAvailable) {
//								doctorsListView.setMode(Mode.PULL_FROM_END);
//							} else {
//								doctorsListView.setMode(Mode.DISABLED);
//							}
//						}
//
//					} else {
//
//						doctorsListView.setMode(Mode.DISABLED);
//						Toast.makeText(
//								SpecialistDoctorsActivity.this,
//								Utils.getCustomeFontStyle(
//										SpecialistDoctorsActivity.this,
//										getString(R.string.recordNotFound)),
//								Toast.LENGTH_SHORT).show();
//					}
//					if (AppConstants.LogLevel == 1) {
//						try {
//							postServerTimepend = Utils.postServerTimeSpend();
//							String totalScreenTimeSpend20 = Utils
//									.totalScreenTimeSpend20();
//							Utils.callPerfomanceTestingApi(
//									"Special Doctors 20 List",
//									"SpecialistDoctorsActivity_"
//											+ specialistName,
//									"ListSplDoctor",
//									preServerTimeSpend,
//									serverTimeSpend,
//									postServerTimepend,
//									totalScreenTimeSpend20,
//									Utils.getNetworkType(SpecialistDoctorsActivity.this),
//									ApplicationSettings
//											.getPref(
//													AppConstants.SERVER_PROCESSTIME,
//													""),
//									AppConstants.perfTestCode);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//
//				else {
//					doctorsListView.setMode(Mode.DISABLED);
//					Toast.makeText(
//							SpecialistDoctorsActivity.this,
//							Utils.getCustomeFontStyle(
//									SpecialistDoctorsActivity.this,
//									getString(R.string.recordNotFound)),
//							Toast.LENGTH_SHORT).show();
//				}
//
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		}

	}
	
	public void loadData(String result)
	{
		isTrue = false;
		try {
			if (result != null) {
				doctorList.clear();
				JSONArray jsonarray = new JSONArray(result.toString());
				if (jsonarray != null && jsonarray.length() > 0) {
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject obj = jsonarray.getJSONObject(i);
						ProfileInfo profileInfo = new ProfileInfo();
						if (obj.getString("id") != null
								&& !obj.getString("id").equalsIgnoreCase(
										"null")) {
							profileInfo.setProfileId(obj.getString("id"));
							String fullName = null;
							String firstName = obj.getString("firstname");
							String middlename = obj.getString("middlename");

							String lastname = obj.getString("lastname");

							if (firstName != null
									&& !firstName.equalsIgnoreCase("")
									&& !firstName.equalsIgnoreCase(null)
									&& !firstName.equalsIgnoreCase("null")) {
								fullName = firstName.trim();
								profileInfo.setFirstName(firstName);
							}
							if (middlename != null
									&& !middlename.equalsIgnoreCase("")
									&& !middlename.equalsIgnoreCase(null)
									&& !middlename.equalsIgnoreCase("null")) {
								fullName = fullName + " "
										+ middlename.trim();
								profileInfo.setMiddleName(middlename);
							}
							if (lastname != null
									&& !lastname.equalsIgnoreCase("")
									&& !lastname.equalsIgnoreCase("null")) {
								fullName = fullName + " " + lastname.trim();
								profileInfo.setLastName(lastname);
							}
							profileInfo.setProfileName(fullName.toString()
									.replace("?", ""));

							if (obj.has("specialityName") && obj.getString("specialityName") != null && !obj.getString("specialityName").equalsIgnoreCase("")
									&& !obj.getString("specialityName").equalsIgnoreCase(null)) {
								profileInfo.setSpecialityName(obj.getString("specialityName").replace("?", ""));
								}
							
							if (obj.has("qualification")
									&& obj.getString("qualification") != null
									&& !obj.getString("qualification")
											.equalsIgnoreCase("")
									&& !obj.getString("qualification")
											.equalsIgnoreCase(null))
								profileInfo.setProfileDesignation(obj
										.getString("qualification"));

							if (obj.has("phone")
									&& obj.getString("phone") != null
									&& !obj.getString("phone")
											.equalsIgnoreCase("")
									&& !obj.getString("phone")
											.equalsIgnoreCase(null))
								profileInfo.setProfilePhoneNumber(obj
										.getString("phone"));

							if (obj.has("email")
									&& obj.getString("email") != null
									&& !obj.getString("email")
											.equalsIgnoreCase("")
									&& !obj.getString("email")
											.equalsIgnoreCase(null))
								profileInfo.setProfileEmail(obj
										.getString("email"));
							if (obj.has("distance")
									&& obj.getString("distance") != null
									&& !obj.getString("distance")
											.equalsIgnoreCase("")
									&& !obj.getString("distance")
											.equalsIgnoreCase(null)
									&& !obj.getString("distance")
											.equalsIgnoreCase("9999")) {

								profileInfo.setProfileDistance(obj
										.getString("distance"));
							}
							if (obj.has("pic")
									&& obj.getString("pic") != null
									&& !obj.getString("pic").trim()
											.equalsIgnoreCase("")
									&& !obj.getString("pic").trim()
											.equalsIgnoreCase(null))
								profileInfo.setProfileImage(obj.getString(
										"pic").trim());

							if (obj.has("second_image")
									&& obj.getString("second_image") != null
									&& !obj.getString("second_image")
											.trim().equalsIgnoreCase("")
									&& !obj.getString("second_image")
											.trim().equalsIgnoreCase(null)) {
								profileInfo.setSecondImage(obj.getString(
										"second_image").trim());
							}

							if (obj.has("awardRecog")
									&& obj.getString("awardRecog") != null
									&& !obj.getString("awardRecog")
											.equalsIgnoreCase("")
									&& !obj.getString("awardRecog")
											.equalsIgnoreCase(null))
								profileInfo.setProfileAwards(obj
										.getString("awardRecog"));
							if (obj.has("avgrate")
									&& obj.getString("avgrate") != null
									&& !obj.getString("avgrate")
											.equalsIgnoreCase("")
									&& !obj.getString("avgrate")
											.equalsIgnoreCase(null))
								profileInfo.setProfileRating(obj
										.getString("avgrate"));
							if (obj.has("hospitalName")
									&& obj.getString("hospitalName") != null
									&& !obj.getString("hospitalName")
											.equalsIgnoreCase("")
									&& !obj.getString("hospitalName")
											.equalsIgnoreCase(null)) {
								String hospitalName;
								if (obj.getString("hospitalName")
										.toString().contains(",")) {

									hospitalName = obj
											.getString("hospitalName")
											.toString().split(",")[0];
								} else {
									hospitalName = obj.getString(
											"hospitalName").toString();
								}
								profileInfo.setHospitalName(hospitalName
										.replace("?", ""));
							}
							if (AppConstants.LogLevel == 1) {
								try {
									if (obj.has("server_processtime")
											&& obj.getString("server_processtime") != null
											&& !obj.getString(
													"server_processtime")
													.equalsIgnoreCase("")
											&& !obj.getString(
													"server_processtime")
													.equalsIgnoreCase(null)) {
										ApplicationSettings
												.putPref(
														AppConstants.SERVER_PROCESSTIME,
														obj.getString("server_processtime"));
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							doctorList.add(profileInfo);
						}
					}

					if (doctorList != null && doctorList.size() > 0) {

						ApplicationSettings.putPref(
								AppConstants.LAST_COUNT,
								"" + doctorList.size());
						doctorAdapter = new DoctorsAdapter(
								SpecialistDoctorsActivity.this,
								R.layout.profile_list_item, doctorList,getIntent().getExtras().getString("key_specialistID"));
						doctorsListView.setAdapter(doctorAdapter);

						
						if (!isAllDataAvailable) {
							System.out.println("PULL_FROM_END");
							doctorsListView.setMode(Mode.PULL_FROM_END);
						} else {
							System.out.println("DISABLED");
							doctorsListView.setMode(Mode.DISABLED);
						}
					}

				} else {
					System.out.println("DISABLED 2");
					doctorsListView.setMode(Mode.DISABLED);
					Toast.makeText(
							SpecialistDoctorsActivity.this,
							Utils.getCustomeFontStyle(
									SpecialistDoctorsActivity.this,
									getString(R.string.recordNotFound)),
							Toast.LENGTH_SHORT).show();
				}
				if (AppConstants.LogLevel == 1) {
					try {
						postServerTimepend = Utils.postServerTimeSpend();
						String totalScreenTimeSpend20 = Utils
								.totalScreenTimeSpend20();
						Utils.callPerfomanceTestingApi(
								"Special Doctors 20 List",
								"SpecialistDoctorsActivity_"
										+ specialistName,
								"ListSplDoctor",
								preServerTimeSpend,
								serverTimeSpend,
								postServerTimepend,
								totalScreenTimeSpend20,
								Utils.getNetworkType(SpecialistDoctorsActivity.this),
								ApplicationSettings
										.getPref(
												AppConstants.SERVER_PROCESSTIME,
												""),
								AppConstants.perfTestCode);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			else {
				doctorsListView.setMode(Mode.DISABLED);
				Toast.makeText(
						SpecialistDoctorsActivity.this,
						Utils.getCustomeFontStyle(
								SpecialistDoctorsActivity.this,
								getString(R.string.recordNotFound)),
						Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* api call for load more */
	public void specialistDoctorDetailsLoadMore(final String specialistID,
			final String offest) {
		final class GetSpecialistDoctorsList extends
				AsyncTask<Void, Void, String> {

			@Override
			protected String doInBackground(Void... arg0) {
				if (AppConstants.LogLevel == 1) {
					Utils.startServerTime = System.currentTimeMillis();
				}
				JSONParser jparser = new JSONParser();
				String latitude = ApplicationSettings.getPref(
						AppConstants.LATITUDE, "");
				String longitude = ApplicationSettings.getPref(
						AppConstants.LONGITUDE, "");
				String result = jparser.getSpecialistsbyID(specialistID,
						latitude, longitude, offest);
				if (AppConstants.LogLevel == 1) {
					serverTimeSpend = Utils.serverTimeSpend();
					Utils.startPostServerCallTime = System.currentTimeMillis();
				}
				return result.toString();
			}

			@Override
			protected void onPostExecute(String result) {

				doctorsListView.onRefreshComplete();
				try {
					if (result != null) {
						//doctorList.clear();
						JSONArray jsonarray = new JSONArray(result.toString());
						if (jsonarray != null && jsonarray.length() > 0) {
							for (int i = 0; i < jsonarray.length(); i++) {
								JSONObject obj = jsonarray.getJSONObject(i);
								ProfileInfo profileInfo = new ProfileInfo();
								if (obj.getString("id") != null
										&& !obj.getString("id")
												.equalsIgnoreCase("null")) {

									profileInfo.setProfileId(obj
											.getString("id"));
									String fullName = null;
									String firstName = obj
											.getString("firstname");
									String middlename = obj
											.getString("middlename");

									String lastname = obj.getString("lastname");

									if (firstName != null
											&& !firstName.equalsIgnoreCase("")
											&& !firstName
													.equalsIgnoreCase(null)
											&& !firstName
													.equalsIgnoreCase("null")) {
										fullName = firstName.trim();
										profileInfo.setFirstName(firstName);
									}
									if (middlename != null
											&& !middlename.equalsIgnoreCase("")
											&& !middlename
													.equalsIgnoreCase(null)
											&& !middlename
													.equalsIgnoreCase("null")) {
										fullName = fullName + " "
												+ middlename.trim();
										profileInfo.setMiddleName(middlename);
									}
									if (lastname != null
											&& !lastname.equalsIgnoreCase("")
											&& !lastname
													.equalsIgnoreCase("null")) {
										fullName = fullName + " "
												+ lastname.trim();
										profileInfo.setLastName(lastname);
									}
									profileInfo.setProfileName(fullName
											.replace("?", ""));

									if (obj.has("specialityName") && obj.getString("specialityName") != null && !obj.getString("specialityName").equalsIgnoreCase("")
											&& !obj.getString("specialityName").equalsIgnoreCase(null)) {
										profileInfo.setSpecialityName(obj.getString("specialityName").replace("?", ""));
										}
									
									if (obj.has("qualification")
											&& obj.getString("qualification") != null
											&& !obj.getString("qualification")
													.equalsIgnoreCase("")
											&& !obj.getString("qualification")
													.equalsIgnoreCase(null))
										profileInfo.setProfileDesignation(obj
												.getString("qualification"));

									if (obj.has("phone")
											&& obj.getString("phone") != null
											&& !obj.getString("phone")
													.equalsIgnoreCase("")
											&& !obj.getString("phone")
													.equalsIgnoreCase(null))
										profileInfo.setProfilePhoneNumber(obj
												.getString("phone"));

									if (obj.has("email")
											&& obj.getString("email") != null
											&& !obj.getString("email")
													.equalsIgnoreCase("")
											&& !obj.getString("email")
													.equalsIgnoreCase(null))
										profileInfo.setProfileEmail(obj
												.getString("email"));
									if (obj.has("distance")
											&& obj.getString("distance") != null
											&& !obj.getString("distance")
													.equalsIgnoreCase("")
											&& !obj.getString("distance")
													.equalsIgnoreCase(null)
											&& !obj.getString("distance")
													.equalsIgnoreCase("9999")) {

										profileInfo.setProfileDistance(obj
												.getString("distance"));
									}
									if (obj.has("pic")
											&& obj.getString("pic") != null
											&& !obj.getString("pic").trim()
													.equalsIgnoreCase("")
											&& !obj.getString("pic").trim()
													.equalsIgnoreCase(null))
										profileInfo.setProfileImage(obj
												.getString("pic").trim());

									if (obj.has("second_image")
											&& obj.getString("second_image") != null
											&& !obj.getString("second_image")
													.trim()
													.equalsIgnoreCase("")
											&& !obj.getString("second_image")
													.trim()
													.equalsIgnoreCase(null)) {
										profileInfo.setSecondImage(obj
												.getString("second_image")
												.trim());
									}

									if (obj.has("awardRecog")
											&& obj.getString("awardRecog") != null
											&& !obj.getString("awardRecog")
													.equalsIgnoreCase("")
											&& !obj.getString("awardRecog")
													.equalsIgnoreCase(null))
										profileInfo.setProfileAwards(obj
												.getString("awardRecog"));
									if (obj.has("avgrate")
											&& obj.getString("avgrate") != null
											&& !obj.getString("avgrate")
													.equalsIgnoreCase("")
											&& !obj.getString("avgrate")
													.equalsIgnoreCase(null)
											&& !obj.getString("avgrate")
													.equalsIgnoreCase("null"))
										profileInfo.setProfileRating(obj
												.getString("avgrate"));
									if (obj.has("hospitalName")
											&& obj.getString("hospitalName") != null
											&& !obj.getString("hospitalName")
													.equalsIgnoreCase("")
											&& !obj.getString("hospitalName")
													.equalsIgnoreCase(null)) {
										String hospitalName;
										if (obj.getString("hospitalName")
												.toString().contains(",")) {

											hospitalName = obj
													.getString("hospitalName")
													.toString().split(",")[0];
										} else {
											hospitalName = obj.getString(
													"hospitalName").toString();
										}
										profileInfo
												.setHospitalName(hospitalName
														.replace("?", ""));
									}

									if (AppConstants.LogLevel == 1) {
										try {
											if (obj.has("server_processtime")
													&& obj.getString("server_processtime") != null
													&& !obj.getString(
															"server_processtime")
															.equalsIgnoreCase(
																	"")
													&& !obj.getString(
															"server_processtime")
															.equalsIgnoreCase(
																	null)) {
												ApplicationSettings
														.putPref(
																AppConstants.SERVER_PROCESSTIME,
																obj.getString("server_processtime"));
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									doctorList.add(profileInfo);
								}
							}

							if (doctorList != null && doctorList.size() > 0) {

								 ApplicationSettings.putPref(AppConstants.LAST_COUNT,
								 "" + doctorList.size());
								 
								if (isPullToRefreshFromEnd) {
									isPullToRefreshFromEnd = false;
									//doctorsListView.setRefreshing();
								}
								doctorsListView.onRefreshComplete();
							//	doctorsListView.setMode(Mode.DISABLED);
								doctorsListView.setMode(Mode.PULL_FROM_END);
								doctorAdapter = new DoctorsAdapter(
										SpecialistDoctorsActivity.this,
										R.layout.profile_list_item, doctorList,getIntent().getExtras().getString("key_specialistID"));
								//doctorsListView.setAdapter(doctorAdapter);
								doctorAdapter.notifyDataSetChanged();
								searchText(doctorList);
							//	isAllDataAvailable = true;
							}

						} else {
							isAllDataAvailable = true;
							doctorsListView.setMode(Mode.DISABLED);
							// Toast.makeText(SpecialistDoctorsActivity.this,
							// "No Records Found", Toast.LENGTH_SHORT).show();
						}

						if (AppConstants.LogLevel == 1) {
							postServerTimepend = Utils.postServerTimeSpend();
							totalScreenTimeSpend = Utils.totalScreenTimeSpend();
							Utils.callPerfomanceTestingApi(
									"Special Doctors All List",
									"SpecialistDoctorsActivity_"
											+ specialistName,
									"ListSplDoctor",
									preServerTimeSpend,
									serverTimeSpend,
									postServerTimepend,
									totalScreenTimeSpend,
									Utils.getNetworkType(SpecialistDoctorsActivity.this),
									ApplicationSettings
											.getPref(
													AppConstants.SERVER_PROCESSTIME,
													""),
									AppConstants.perfTestCode);
						}
					}

					else {
						isAllDataAvailable = true;
						doctorsListView.setMode(Mode.DISABLED);
						// Toast.makeText(SpecialistDoctorsActivity.this,
						// "No Records Found", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					isAllDataAvailable = true;
					doctorsListView.setMode(Mode.DISABLED);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		new GetSpecialistDoctorsList().execute();

	}

	/* APi call for top specialists */
	public void TopspecialistDoctorDetails(final String specialistID) {
		final class GetTopRatedSpecialistDoctorsList extends
				AsyncTask<Void, Void, String> {

			ProgressDialog pdia;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pdia = new ProgressDialog(SpecialistDoctorsActivity.this);
				pdia.setCancelable(false);
				pdia.setMessage(Utils.getCustomeFontStyle(
						SpecialistDoctorsActivity.this,
						getString(R.string.screenLoading)));
				pdia.show();
			}

			// 200793653151
			@Override
			protected String doInBackground(Void... arg0) {

				JSONParser jparser = new JSONParser();
				String latitude = ApplicationSettings.getPref(
						AppConstants.LATITUDE, "");
				String longitude = ApplicationSettings.getPref(
						AppConstants.LONGITUDE, "");
				String result = jparser.getSpecialistsTopRated(specialistID,
						latitude, longitude);
				return result.toString();
			}

			@Override
			protected void onPostExecute(String result) {
				pdia.dismiss();
				try {
					if (result != null) {
						doctorList = new ArrayList<ProfileInfo>();

						JSONArray jsonarray = new JSONArray(result.toString());
						if (jsonarray != null && jsonarray.length() > 0) {
							for (int i = 0; i < jsonarray.length(); i++) {
								JSONObject obj = jsonarray.getJSONObject(i);
								ProfileInfo profileInfo = new ProfileInfo();
								profileInfo.setProfileId(obj.getString("id"));
								String fullName = null;
								String firstName = obj.getString("firstname");
								String middlename = obj.getString("middlename");

								String lastname = obj.getString("lastname");

								if (firstName != null
										&& !firstName.equalsIgnoreCase("")
										&& !firstName.equalsIgnoreCase(null)
										&& !firstName.equalsIgnoreCase("null")) {
									fullName = firstName;
								}
								if (middlename != null
										&& !middlename.equalsIgnoreCase("")
										&& !middlename.equalsIgnoreCase(null)
										&& !middlename.equalsIgnoreCase("null")) {
									fullName = fullName + " " + middlename;
								}
								if (lastname != null
										&& !lastname.equalsIgnoreCase("")
										&& !lastname.equalsIgnoreCase("null")) {
									fullName = fullName + " " + lastname;
								}
								profileInfo.setProfileName(fullName);
								
								if (obj.has("specialityName") && obj.getString("specialityName") != null && !obj.getString("specialityName").equalsIgnoreCase("")
										&& !obj.getString("specialityName").equalsIgnoreCase(null)) {
									profileInfo.setSpecialityName(obj.getString("specialityName").replace("?", ""));
									}

								if (obj.has("qualification")
										&& obj.getString("qualification") != null
										&& !obj.getString("qualification")
												.equalsIgnoreCase("")
										&& !obj.getString("qualification")
												.equalsIgnoreCase(null))
									profileInfo.setProfileDesignation(obj
											.getString("qualification"));

								if (obj.has("phone")
										&& obj.getString("phone") != null
										&& !obj.getString("phone")
												.equalsIgnoreCase("")
										&& !obj.getString("phone")
												.equalsIgnoreCase(null))
									profileInfo.setProfilePhoneNumber(obj
											.getString("phone"));

								if (obj.has("email")
										&& obj.getString("email") != null
										&& !obj.getString("email")
												.equalsIgnoreCase("")
										&& !obj.getString("email")
												.equalsIgnoreCase(null))
									profileInfo.setProfileEmail(obj
											.getString("email"));
								if (obj.has("distance")
										&& obj.getString("distance") != null
										&& !obj.getString("distance")
												.equalsIgnoreCase("")
										&& !obj.getString("distance")
												.equalsIgnoreCase(null)
										&& !obj.getString("distance")
												.equalsIgnoreCase("9999"))
									profileInfo.setProfileDistance(obj
											.getString("distance"));
								if (obj.has("pic")
										&& obj.getString("pic") != null
										&& !obj.getString("pic")
												.equalsIgnoreCase("")
										&& !obj.getString("pic")
												.equalsIgnoreCase(null))
									profileInfo.setProfileImage(obj
											.getString("pic"));
								if (obj.has("awardRecog")
										&& obj.getString("awardRecog") != null
										&& !obj.getString("awardRecog")
												.equalsIgnoreCase("")
										&& !obj.getString("awardRecog")
												.equalsIgnoreCase(null))
									profileInfo.setProfileAwards(obj
											.getString("awardRecog"));
								if (obj.has("avgrate")
										&& obj.getString("avgrate") != null
										&& !obj.getString("avgrate")
												.equalsIgnoreCase("")
										&& !obj.getString("avgrate")
												.equalsIgnoreCase(null))
									profileInfo.setProfileRating(obj
											.getString("avgrate"));

								if (obj.has("hospitalName")
										&& obj.getString("hospitalName") != null
										&& !obj.getString("hospitalName")
												.equalsIgnoreCase("")
										&& !obj.getString("hospitalName")
												.equalsIgnoreCase(null))
									profileInfo.setHospitalName(obj
											.getString("hospitalName"));

								doctorList.add(profileInfo);
							}

							if (doctorList != null && doctorList.size() > 0) {
								doctorAdapter = new DoctorsAdapter(
										SpecialistDoctorsActivity.this,
										R.layout.profile_list_item, doctorList,getIntent().getExtras().getString("key_specialistID"));
								doctorsListView.setAdapter(doctorAdapter);
							}

						} else {
							Toast.makeText(
									SpecialistDoctorsActivity.this,
									Utils.getCustomeFontStyle(
											SpecialistDoctorsActivity.this,
											getString(R.string.recordNotFound)),
									Toast.LENGTH_SHORT).show();
						}
					}

					else {
						Toast.makeText(
								SpecialistDoctorsActivity.this,
								Utils.getCustomeFontStyle(
										SpecialistDoctorsActivity.this,
										getString(R.string.recordNotFound)),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		new GetTopRatedSpecialistDoctorsList().execute();

	}

	// click event for specialist doctors and pull to refresh event
	public void clickEvent() {
		doctorsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ProfileInfo entry = (ProfileInfo) parent
						.getItemAtPosition(position);
				if (parent != null && entry != null) {

					easyTracker.send(MapBuilder.createEvent(
							"Doctor Tab", "View Specialization",
							entry.getProfileName(), null).build());

					ApplicationSettings.putPref(AppConstants.DOCTOR_ID,
							entry.getProfileId());
					ApplicationSettings.putPref(AppConstants.DOCTOR_SPECIALITY,
							gpTextView.getText().toString());
					startActivity(new Intent(SpecialistDoctorsActivity.this,
							DoctorDetailsActivity.class));
				}
			}
		});

	//	doctorsListView.setMode(Mode.DISABLED);
		doctorsListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

				if (!isAllDataAvailable) {
					if (isNetworkAvailable()) {

						doctorsListView.setMode(Mode.PULL_FROM_END);
						isPullToRefreshFromEnd = true;
						
						//pull from end
						  ApplicationSettings.putPref(AppConstants.
						  OFFSET_PREVIOUS_VALUE, doctorList.size());
						  specialistDoctorDetailsLoadMore
						  (getIntent().getExtras(
						  ).getString("key_specialistID"),
						  ApplicationSettings.getPref(AppConstants.LAST_COUNT,
						  "" + doctorList.size()));
						 
					} else {
						doctorsListView.onRefreshComplete();
						doctorsListView.setMode(Mode.DISABLED);
						Toast.makeText(
								SpecialistDoctorsActivity.this,
								Utils.getCustomeFontStyle(
										SpecialistDoctorsActivity.this,
										getString(R.string.notWorkNotAvailable)),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					doctorsListView.setMode(Mode.DISABLED);
				}
			}
		});
	}

	// check network
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

	/*
	 * get the data from preferences use spannable string to show location in
	 * action bar add custom color, fonts to it
	 */
	public void showLocation() {
		if (ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, "") != null
				&& ApplicationSettings
						.getPref(AppConstants.CURRENT_USER_LOCATION, "").trim()
						.length() > 1
				&& ApplicationSettings.getPref(
						AppConstants.CURRENT_USER_SUB_LOCALITY, "") != null
				&& ApplicationSettings
						.getPref(AppConstants.CURRENT_USER_SUB_LOCALITY, "")
						.trim().length() > 1) {

			SpannableString s = new SpannableString(
					Html.fromHtml("<font color=\"#ed207b\"><b>"
							+ ApplicationSettings.getPref(
									AppConstants.CURRENT_USER_LOCATION, "")
							+ "</b></font>"));
			s.setSpan(new com.medinfi.utils.TypefaceSpan(this,
					"RobotoRegular.ttf"), 0, s.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			actionbarTitle.setText(s);

			SpannableString s1 = new SpannableString(
					Html.fromHtml("<font color=\"#ed207b\">"
							+ ApplicationSettings.getPref(
									AppConstants.CURRENT_USER_SUB_LOCALITY, "")
							+ "</font>"));
			s1.setSpan(new com.medinfi.utils.TypefaceSpan(this,
					"RobotoLight.ttf"), 0, s1.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			actionbarSubtitle.setText(s1);
		} else {
			startLocationUpdate();
		}
	}

	/*
	 * // click event for near me view and top rated view public void
	 * clickTextViews() { nearmeTextView.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub nearbyView.setVisibility(View.VISIBLE);
	 * topratedView.setVisibility(View.INVISIBLE);
	 * 
	 * //easyTracker.send(MapBuilder.createEvent(gpTextView.getText().toString(),
	 * "NEAR ME", "Empty", null).build());
	 * 
	 * if (isNetworkAvailable()) { if
	 * (getIntent().getExtras().getString("key_specialistID") != null &&
	 * !getIntent
	 * ().getExtras().getString("key_specialistID").equalsIgnoreCase(""))
	 * 
	 * specialistDoctorDetails(getIntent().getExtras()
	 * .getString("key_specialistID"), AppConstants.OFFSET_DEFAULT_VALUE);
	 * 
	 * getSpecialistDoctorsList = new GetSpecialistDoctorsList();
	 * getSpecialistDoctorsList.execute();
	 * 
	 * } else { Toast.makeText(SpecialistDoctorsActivity.this,
	 * "Network not Available", Toast.LENGTH_SHORT).show(); } } });
	 * topratedTextView.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub nearbyView.setVisibility(View.INVISIBLE);
	 * topratedView.setVisibility(View.VISIBLE);
	 * 
	 * //easyTracker.send(MapBuilder.createEvent(gpTextView.getText().toString(),
	 * "TOP RATED", "Empty", null).build()); if (isNetworkAvailable()) { if
	 * (getIntent().getExtras().getString("key_specialistID") != null &&
	 * !getIntent
	 * ().getExtras().getString("key_specialistID").equalsIgnoreCase(""))
	 * TopspecialistDoctorDetails
	 * (getIntent().getExtras().getString("key_specialistID"));
	 * 
	 * } else { Toast.makeText(SpecialistDoctorsActivity.this,
	 * "Network not Available", Toast.LENGTH_SHORT).show(); } } }); }
	 */

	/* search key user input */
	public void searchPhysicians(final AutoCompleteTextView editsearch) {
		// Capture Text in EditText
		editsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = editsearch.getText().toString()
						.toLowerCase(Locale.getDefault());
				/*
				 * if (doctorList != null) { if (doctorAdapter != null)
				 * doctorAdapter.filter(text); }
				 */

				if (!text.startsWith("com.medinfi.datainfo")) {
					isTrueToast = false;
					Log.e("Calling Feature::::::::::::::::::::::;", "True");
					if (text != null && text.length() > 2) {
						callSearchAPI(text);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
		});
	}

	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	public boolean onClose() {
		return false;
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

	/*
	 * //enable and disable pulltorefresh from the adapter once the query
	 * results populated public static void CheckEnabling() { int countValue =
	 * Integer.parseInt(ApplicationSettings .getPref(AppConstants.LAST_COUNT,
	 * "")); if (countValue > 19) { doctorsListView.setMode(Mode.PULL_FROM_END);
	 * } else { doctorsListView.setMode(Mode.DISABLED); } }
	 */
	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (!isEventPressed) {
				if (isHomePressed) {
					
				}
				//if(AppConstants.LAST_TRACK_REQ_TIME.equals("0")||((Calendar.getInstance().getTimeInMillis()/1000)-AppConstants.REQ_INTERVAL)>Long.parseLong(ApplicationSettings.getPref(AppConstants.LAST_TRACK_REQ_TIME, ""))){
					
					ApplicationSettings.putPref(AppConstants.LAST_TRACK_REQ_TIME, (Calendar.getInstance().getTimeInMillis()/1000)+"");
					
					new CreateSessionIdAsyncTask().execute("0", "");
					
				//}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(this, getResources().getString(R.string.FlurryAPIKey));
		FlurryAgent.onStartSession(this,
				getResources().getString(R.string.FlurryAPIKey));
		FlurryAgent.logEvent("SpecialistDoctors Activity");
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
		isHomePressed = true;
		EasyTracker.getInstance(this).activityStop(this);
	}

	public void callSearchAPI(final String keyword) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				try {
					JSONParser jparser = new JSONParser();
					String latitude = ApplicationSettings.getPref(
							AppConstants.LATITUDE, "");
					String longitude = ApplicationSettings.getPref(
							AppConstants.LONGITUDE, "");
					result = jparser.getAllSearch(
							keyword,
							latitude,
							longitude,
							ApplicationSettings.getPref(
									AppConstants.CURRENT_USER_LOCATION, ""),
							getIntent().getExtras().getString(
									"key_specialistID"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				return result.toString();
			}

			protected void onPostExecute(String result) {
				if (result != null) {
					hideKeyboard();
					/* {"status":"Fail","message":"No Results Found!"} */

					JSONObject jObject = null;
					try {
						jObject = new JSONObject(result);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						if (jObject != null
								&& jObject.has("status")
								&& jObject.getString("status") != null
								&& jObject.getString("status")
										.equalsIgnoreCase("Fail")) {
							if (!isTrueToast) {
								toast = Toast.makeText(
										SpecialistDoctorsActivity.this, "",
										Toast.LENGTH_SHORT);
								toast.setText(Utils.getCustomeFontStyle(
										SpecialistDoctorsActivity.this,
										getString(R.string.recordNotFound)));
								toast.show();
								isTrueToast = true;
							}
						} else {
							try {
								JSONArray array = new JSONArray(result);

								if (array != null && array.length() > 0) {
									ArrayList<String> searchList = new ArrayList<String>();
									autocompleteList = new ArrayList<AutoCompleteInfo>();
									for (int i = 0; i < array.length(); i++) {

										JSONObject jsonObject = array
												.getJSONObject(i);
										AutoCompleteInfo autoCompleteInfo = new AutoCompleteInfo();
										autoCompleteInfo.setId(jsonObject
												.getString("id"));
										autoCompleteInfo.setName("Dr "
												+ jsonObject.getString("name")
														.replace("?", ""));

										searchList.add(jsonObject
												.getString("name"));
										autocompleteList.add(autoCompleteInfo);
									}
									if (autocompleteList != null
											&& autocompleteList.size() > 0) {
										isTrueToast = false;
										autoCompleteAdapter = new AutoCompleteAdapter(
												SpecialistDoctorsActivity.this,
												R.layout.autocompletesearch,
												autocompleteList,getIntent().getExtras().getString("key_specialistID"));

										searchText
												.setAdapter(autoCompleteAdapter);
										autoCompleteAdapter
												.notifyDataSetChanged();
										searchText
												.setOnItemClickListener(new OnItemClickListener() {

													@Override
													public void onItemClick(
															AdapterView<?> parent,
															View view,
															int position,
															long id) {
														// TODO Auto-generated
														// method
														// stub
														if (toast != null) {
															toast.cancel();
															toast = null;
														}
														// TODO Auto-generated
														// method
														// stub
														/*
														 * searchText.setText("")
														 * ; hideKeyboard();
														 */
														searchText.setText("");
														searchLayout
																.setVisibility(View.GONE);
														searchImageView
																.setVisibility(View.VISIBLE);
														titleLayout
																.setVisibility(View.VISIBLE);
														AutoCompleteInfo entry = (AutoCompleteInfo) parent
																.getItemAtPosition(position);
														if (parent != null
																&& entry != null) {
															ApplicationSettings
																	.putPref(
																			AppConstants.DOCTOR_ID,
																			entry.getId());
															ApplicationSettings
																	.putPref(
																			AppConstants.DOCTOR_SPECIALITY,
																			gpTextView
																					.getText()
																					.toString());
															if (toast != null)
																toast.cancel();
															startActivity(new Intent(
																	SpecialistDoctorsActivity.this,
																	DoctorDetailsActivity.class));
														}

													}
												});
									}
								} else {
									Log.e("No data", "No data");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};
		}.execute();
	}

	/*
	 * public void customActionbar() {
	 * 
	 * getSupportActionBar().show();
	 * getSupportActionBar().setDisplayShowHomeEnabled(false);
	 * getSupportActionBar().setDisplayShowTitleEnabled(false); LayoutInflater
	 * mInflater = LayoutInflater.from(this);
	 * 
	 * View mCustomView = mInflater.inflate(R.layout.searchbar, null);
	 * 
	 * LinearLayout backLayout = (LinearLayout) mCustomView
	 * .findViewById(R.id.back_layout); searchLayout = (LinearLayout)
	 * mCustomView.findViewById(R.id.search_lyt);
	 * 
	 * titleLayout = (LinearLayout) mCustomView
	 * .findViewById(R.id.ab_titles_lyt); searchText = (AutoCompleteTextView)
	 * mCustomView .findViewById(R.id.search_input_et); searchImageView =
	 * (ImageView) mCustomView.findViewById(R.id.searchbtn); searchText =
	 * (AutoCompleteTextView) mCustomView .findViewById(R.id.search_input_et);
	 * searchText = (AutoCompleteTextView) mCustomView
	 * .findViewById(R.id.search_input_et);
	 * 
	 * searchPhysicians(searchText); ImageView cancelImageView = (ImageView)
	 * mCustomView .findViewById(R.id.closebtn);
	 * 
	 * searchImageView.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub searchLayout.setVisibility(View.VISIBLE);
	 * searchText.setHint(getResources().getString( R.string.gp_hint_search));
	 * searchText.requestFocus(); InputMethodManager imm = (InputMethodManager)
	 * getSystemService(Context.INPUT_METHOD_SERVICE);
	 * imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
	 * searchImageView.setVisibility(View.GONE);
	 * titleLayout.setVisibility(View.GONE);
	 * 
	 * } });
	 * 
	 * cancelImageView.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub searchText.setText(""); hideKeyboard();
	 * searchLayout.setVisibility(View.GONE);
	 * searchImageView.setVisibility(View.VISIBLE);
	 * titleLayout.setVisibility(View.VISIBLE); } });
	 * backLayout.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { isEventPressed = true;
	 * SpecialistDoctorsActivity.this.finish();
	 * Log.e("Print Inside back button", ":::::::::::::::::::::::::::;"); } });
	 * actionbarTitle = (TextView) mCustomView.findViewById(R.id.title);
	 * actionbarSubtitle = (TextView) mCustomView.findViewById(R.id.subtitle);
	 * 
	 * ImageView imageButton = (ImageView) mCustomView
	 * .findViewById(R.id.imageButton); imageButton.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View view) {
	 * SpecialistDoctorsActivity.this.finish(); } });
	 * 
	 * getSupportActionBar().setCustomView(mCustomView);
	 * getSupportActionBar().setDisplayShowCustomEnabled(true);
	 * 
	 * }
	 */
	private void hideKeyboard() {
		// Check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (toast != null) {
			toast.cancel();
			toast = null;
			isTrueToast = true;

		}
	}

	/* handle back button event */
	@Override
	public void onBackPressed() {

		if (isTrue) {
			isEventPressed = true;
			pdia.setCancelable(true);
			getSpecialistDoctorsList.cancel(true);
			finish();
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isTrue = false;
		if (pdia != null && pdia.isShowing())
			pdia.dismiss();
	}

	/* listener for dialog cancel in thread */
	OnCancelListener cancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface arg0) {
			isTrue = true;
			//SpecialistDoctorsActivity.this.finish();
		}
	};

	/* handle back button event */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isEventPressed = true;
			if (getSpecialistDoctorsList != null
					&& getSpecialistDoctorsList.getStatus() == Status.RUNNING)
				getSpecialistDoctorsList.cancel(true);
			if (pdia != null && pdia.isShowing())
				pdia.dismiss();
			SpecialistDoctorsActivity.this.finish();

		}

		return super.onKeyDown(keyCode, event);
	}

	/* searchSpecialist based on key */
	public void searchSpecialistDoctor(final EditText editsearch) {
		// Capture Text in EditText
		editsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				strSearchText = editsearch.getText().toString()
						.toLowerCase(Locale.getDefault());
				if (doctorList != null) {
					/*
					 * if (doctorAdapter != null)
					 * doctorAdapter.filter(strSearchText);
					 */
					searchText(doctorList);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
		});
	}

	/* hide keyboard on tap of screen */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		boolean ret = false;
		try {
			View v = getCurrentFocus();
			ret = super.dispatchTouchEvent(event);

			if (v instanceof EditText) {
				View w = getCurrentFocus();
				int scrcoords[] = new int[2];
				w.getLocationOnScreen(scrcoords);
				float x = event.getRawX() + w.getLeft() - scrcoords[0];
				float y = event.getRawY() + w.getTop() - scrcoords[1];

				if (event.getAction() == MotionEvent.ACTION_UP
						&& (x < w.getLeft() || x >= w.getRight()
								|| y < w.getTop() || y > w.getBottom())) {

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
							.getWindowToken(), 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private void searchText(ArrayList<ProfileInfo> arrListFilteredItems) {
		try {
			ArrayList<ProfileInfo> arrListFilter = new ArrayList<ProfileInfo>();
			if (!strSearchText.equals("")) {
				// Search for First name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getFirstName() != null && arrListFilteredItems
							.get(i).getFirstName()
							.toLowerCase(Locale.getDefault()).trim()
							.startsWith(strSearchText))
							|| arrListFilteredItems.get(i).getProfileName() != null
							&& arrListFilteredItems.get(i).getProfileName()
									.toLowerCase(Locale.getDefault())
									.startsWith(strSearchText)) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for Middle name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getMiddleName() != null && arrListFilteredItems
							.get(i).getMiddleName()
							.toLowerCase(Locale.getDefault()).trim()
							.startsWith(strSearchText))
							|| arrListFilteredItems.get(i).getProfileName() != null
							&& arrListFilteredItems.get(i).getProfileName()
									.toLowerCase(Locale.getDefault())
									.startsWith(strSearchText)) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}

					}
				}
				// Search Last Name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getLastName() != null && arrListFilteredItems
							.get(i).getLastName()
							.toLowerCase(Locale.getDefault()).trim()
							.startsWith(strSearchText))
							|| arrListFilteredItems.get(i).getProfileName() != null
							&& arrListFilteredItems.get(i).getProfileName()
									.toLowerCase(Locale.getDefault())
									.startsWith(strSearchText)) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for First name + Middle name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getFirstName() != null
							&& arrListFilteredItems.get(i).getMiddleName() != null && (arrListFilteredItems
							.get(i).getFirstName()
							.toLowerCase(Locale.getDefault()).trim()
							+ " " + arrListFilteredItems.get(i).getMiddleName()
							.toLowerCase(Locale.getDefault()).trim())
							.startsWith(strSearchText))) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for Middle name + Last name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getMiddleName() != null
							&& arrListFilteredItems.get(i).getLastName() != null && (arrListFilteredItems
							.get(i).getMiddleName()
							.toLowerCase(Locale.getDefault()).trim()
							+ " " + arrListFilteredItems.get(i).getLastName()
							.toLowerCase(Locale.getDefault()).trim())
							.startsWith(strSearchText))) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for First name + Last Name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getFirstName() != null
							&& arrListFilteredItems.get(i).getLastName() != null && (arrListFilteredItems
							.get(i).getFirstName()
							.toLowerCase(Locale.getDefault()).trim()
							+ " " + arrListFilteredItems.get(i).getLastName()
							.toLowerCase(Locale.getDefault()).trim())
							.startsWith(strSearchText))) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for Last name + Middle name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getLastName() != null
							&& arrListFilteredItems.get(i).getMiddleName() != null && (arrListFilteredItems
							.get(i).getLastName()
							.toLowerCase(Locale.getDefault()).trim()
							+ " " + arrListFilteredItems.get(i).getMiddleName()
							.toLowerCase(Locale.getDefault()).trim())
							.startsWith(strSearchText))) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for Last name + First Name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getLastName() != null
							&& arrListFilteredItems.get(i).getFirstName() != null && (arrListFilteredItems
							.get(i).getLastName()
							.toLowerCase(Locale.getDefault()).trim()
							+ " " + arrListFilteredItems.get(i).getFirstName()
							.toLowerCase(Locale.getDefault()).trim())
							.startsWith(strSearchText))) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for Middle name + First Name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getMiddleName() != null
							&& arrListFilteredItems.get(i).getFirstName() != null && (arrListFilteredItems
							.get(i).getMiddleName()
							.toLowerCase(Locale.getDefault()).trim()
							+ " " + arrListFilteredItems.get(i).getFirstName()
							.toLowerCase(Locale.getDefault()).trim())
							.startsWith(strSearchText))) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for Last name + Middle Name + First Name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getLastName() != null
							&& arrListFilteredItems.get(i).getMiddleName() != null
							&& arrListFilteredItems.get(i).getFirstName() != null && (arrListFilteredItems
							.get(i).getLastName()
							.toLowerCase(Locale.getDefault()).trim()
							+ " "
							+ arrListFilteredItems.get(i).getMiddleName()
									.toLowerCase(Locale.getDefault()).trim()
							+ " " + arrListFilteredItems.get(i).getFirstName()
							.toLowerCase(Locale.getDefault()).trim())
							.startsWith(strSearchText))) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}
					}
				}
				// Search for Hospital name
				for (int i = 0; i < arrListFilteredItems.size(); i++) {
					if ((arrListFilteredItems.get(i).getHospitalName() != null && arrListFilteredItems
							.get(i).getHospitalName()
							.toLowerCase(Locale.getDefault()).trim()
							.startsWith(strSearchText))) {
						if (!arrListFilteredItems.get(i)
								.isAddedToFilteredList()) {
							arrListFilteredItems.get(i).setAddedToFilteredList(
									true);
							arrListFilter.add(arrListFilteredItems.get(i));
						}

					}
					arrListFilteredItems.get(i).setAddedToFilteredList(false);
				}
				setAdapter(arrListFilter);
			} else {
				setAdapter(arrListFilteredItems);
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	private void setAdapter(ArrayList<ProfileInfo> arrListFilteredItems) {
		if (arrListFilteredItems.size() > 0) {
			txtNoResultFound.setVisibility(View.GONE);
			doctorsListView.setVisibility(View.VISIBLE);
			doctorAdapter = new DoctorsAdapter(SpecialistDoctorsActivity.this,
					R.layout.profile_list_item, arrListFilteredItems,getIntent().getExtras().getString("key_specialistID"));
			doctorsListView.setAdapter(doctorAdapter);
		} else {
			doctorsListView.setVisibility(View.GONE);
			txtNoResultFound.setVisibility(View.VISIBLE);
		}

	}

	public static SpecialistDoctorsActivity getInstance() {
		return specialistDoctorsScreen;
	}

	private void tabClickEvent() {
		RelativeLayout hospital, doctor, profile;
		ImageView doctor_iv;
		TextView doctor_tv;
		View doctor_view;
		hospital = (RelativeLayout) findViewById(R.id.hospital);
		doctor = (RelativeLayout) findViewById(R.id.doctor);
		profile = (RelativeLayout) findViewById(R.id.profile);
		doctor_iv = (ImageView) findViewById(R.id.doctor_iv);
		doctor_tv = (TextView) findViewById(R.id.doctor_tv);
		doctor_view = findViewById(R.id.doctor_view);

		doctor_view.setVisibility(View.VISIBLE);
		doctor_iv.setBackgroundResource(R.drawable.doctor_on);
		doctor_tv.setTextColor(getResources().getColor(R.color.screen_header));
		doctor_tv.setTypeface(Typeface.DEFAULT_BOLD);

		RelativeLayout layoutLocation = (RelativeLayout) findViewById(R.id.layoutLocation);
		layoutLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				easyTracker.send(MapBuilder.createEvent("Doctor Tab",
						"Update Location", "Update Location", null).build());
				startLocationUpdate();
			}
		});

		hospital.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				easyTracker.send(MapBuilder.createEvent("Doctor Tab",
						"Hospital Tab", "Hospital Tab", null).build());
				ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE,
						false);
				ApplicationSettings.putPref(
						AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
				// hospitalsButton.setBackgroundResource(R.drawable.hospital_icon_selected);
				if (HospitalsActivity.getInstance() != null) {
					HospitalsActivity.getInstance().finish();
				}
				Intent intent = new Intent(SpecialistDoctorsActivity.this,
						HospitalsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				finish();
			}
		});

		profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				easyTracker.send(MapBuilder.createEvent("Doctor Tab",
						"Profile Tab", "Profile Tab", null).build());
				Intent intent = new Intent(SpecialistDoctorsActivity.this,
						ProfileActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				finish();
			}
		});

		((TextView) findViewById(R.id.filter)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				easyTracker.send(MapBuilder.createEvent("Doctor Tab",
						"Select Specialist Doctors",
						"Select Specialist Doctors", null).build());
				ApplicationSettings.putPref(AppConstants.SHOW_THANKS_MESSAGE,
						false);
				ApplicationSettings.putPref(
						AppConstants.SHOW_THANKS_MESSAGE_HOSPITAL, false);
				// specialistsButton.setBackgroundResource(R.drawable.specialist_icon_selected);
				// spTextView.setTextColor(getResources().getColor(R.color.pink_color));
				if (SpecialistActivity.getInstance() != null) {
					SpecialistActivity.getInstance().finish();
				}
				startActivity(new Intent(SpecialistDoctorsActivity.this,
						SpecialistActivity.class));
			}
		});

	}
	
	  /**
     * Quick search for Doctor and Hospital Author LalBabu
     * 
     * @param editsearch
     */
    public void globalSearch(final EditText editsearch) {
	editsearch.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void afterTextChanged(Editable arg0) {
		String globalSearchText;
		globalSearchText = editsearch.getText().toString().toLowerCase(Locale.getDefault());
		
		if (globalSearchText != null && globalSearchText.length() < 1) {
//			while(asyncTask.getStatus()==AsyncTask.Status.RUNNING){
//				Log.i("here","here");
				//asyncTask.cancel(true);
//				if(asyncTask.isCancelled()){
//					((ProgressBar) findViewById(R.id.globalprogressBar)).setVisibility(View.GONE);
//					break;
//						}
				
			globalListView.setVisibility(View.GONE);
			txtNoResultFound.setVisibility(View.GONE);
			doctorsListView.setVisibility(View.VISIBLE);
			globalListView.setAdapter(null);
			//if(!asyncTask.isCancelled())
				
		    easyTracker.send(MapBuilder.createEvent("Global Search", "Global Search", "Global Search", null).build());
		}
		if (Utils.isNetworkAvailable(SpecialistDoctorsActivity.this)) {
		    // globalSearchList.clear();
		    if (globalSearchText != null && globalSearchText.length() > 2) {
		    	doctorsListView.setVisibility(View.GONE);
				txtNoResultFound.setVisibility(View.GONE);
				globalListView.setVisibility(View.VISIBLE);
			    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
			 if (AppConstants.LogLevel == 1) {
				Utils.startTotalScreenTime = System.currentTimeMillis();
				Utils.startPreServerCallTime = System.currentTimeMillis();
			    }
			asyncTask= (GetGlobalSearchList) new GetGlobalSearchList();
			asyncTask.execute(globalSearchText);
		    }
		} else {
			
			doctorsListView.setVisibility(View.GONE);
		    globalListView.setVisibility(View.GONE);
		    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
		}
	    }

	    @Override
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	    }

	    @Override
	    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	    }
	});
    }
    
    public class GetGlobalSearchList extends AsyncTask<String, Void, String> {
    	private ProgressDialog pdia;

    	@Override
    	protected void onPreExecute() {
    	    super.onPreExecute();
    	    if (AppConstants.LogLevel == 1) {
    		Utils.startServerTime = System.currentTimeMillis();
    		preServerTimeSpend = Utils.preServerTimeSpend();
    	    }
    	   /* pdia = new ProgressDialog(HomeScreen.this);
    	    pdia.setCancelable(true);
    	    pdia.setOnCancelListener(cancelListener);
    	    pdia.setMessage("Loading...");*/
    	    // pdia.show();
    	    ((ProgressBar) findViewById(R.id.globalprogressBar)).setVisibility(View.VISIBLE);
    	}

    	@Override
    	protected String doInBackground(String... arg0) {
    	    String result = null;
    	    JSONParser jparser = new JSONParser();
    	    try {

    	    	String specialization="ALL";
    	    	if(getIntent().getExtras().getString("key_specialist").equalsIgnoreCase(""))
    	    		specialization="ALL";
    	    	else
    	    		specialization=getIntent().getExtras().getString("key_specialist");
    	    	
    		result = jparser.getGlobalDoctorSearchList(arg0[0], "20", ApplicationSettings.getPref(AppConstants.LATITUDE, ""),
    			ApplicationSettings.getPref(AppConstants.LONGITUDE, ""),specialization);
	    		if (AppConstants.LogLevel == 1) {
	    		    serverTimeSpend = Utils.serverTimeSpend();
	    			}

    	    	
    	    } catch (Exception e) {
    		e.printStackTrace();
    	    }
    	    return result.toString();
    	}

    	@Override
    	protected void onPostExecute(String result) {
    	    try {
    	    	((ProgressBar) findViewById(R.id.globalprogressBar)).setVisibility(View.GONE);
    		/*
    		 * if (pdia != null && pdia.isShowing()) { pdia.dismiss(); }
    		 */
    		if (result != null) {
    		    if (AppConstants.LogLevel == 1) {
    			Utils.startPostServerCallTime = System.currentTimeMillis();
    		    }
    		    // hideKeyboard();
    		    globalSearchList.clear();
    		    JSONObject jsonObject = new JSONObject(result.toString());

    		    if (jsonObject.has("Status")) {
    		    	
    			globalListView.setVisibility(View.GONE);
    			
    			((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
    			//to prevent async task which end late from setting the wrong view
    			if(((EditText)findViewById(R.id.searchedittext)).getText().toString().length()!=0){
    				doctorsListView.setVisibility(View.GONE);
    				txtNoResultFound.setVisibility(View.VISIBLE);
    			}
    			// ((TextView)
    			// findViewById(R.id.txtNoNetwork)).setText("");
    			// ((TextView)
    			// findViewById(R.id.txtNoNetworkDesc)).setText("No Result Found");
    			// ((TextView)
    			// findViewById(R.id.txtNoNetworkDesc)).setTextColor(Color.BLACK);;
    		    } else {
    			JSONArray jsonarray = new JSONArray(jsonObject.getString("doctor"));
    			if (AppConstants.LogLevel == 1) {
    			    try {
    				ApplicationSettings.putPref(AppConstants.SERVER_PROCESSTIME, jsonObject.getString("server_processtime"));
    			    } catch (Exception e) {
    				e.printStackTrace();
    			    }
    			}
    			for (int i = 0; i < jsonarray.length(); i++) {
    			    JSONObject obj = jsonarray.getJSONObject(i);
    			    AutoCompleteInfo autoCompleteInfo = new AutoCompleteInfo();
    			    if (obj.getString("id") != null && !obj.getString("id").equalsIgnoreCase("null")) {

    				autoCompleteInfo.setId(obj.getString("id"));
    				String profileName;
    				if (obj.getString("name").toString().contains(",")) {

    				    profileName = obj.getString("name").toString().split(",")[0];
    				} else {
    				    profileName = obj.getString("name").toString();
    				}
    				autoCompleteInfo.setName(profileName.replace("?", ""));
    				if (obj.getString("address") != null && !obj.getString("address").equalsIgnoreCase("")
    					&& !obj.getString("address").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setAddress(obj.getString("address"));
    				}

    				if (obj.getString("primaryphone") != null && !obj.getString("primaryphone").equalsIgnoreCase("")
    					&& !obj.getString("primaryphone").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setPrimaryphone(obj.getString("primaryphone"));
    				}

    				if (obj.has("locality") && obj.getString("locality") != null && !obj.getString("locality").equalsIgnoreCase("")
    					&& !obj.getString("locality").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setLocality(obj.getString("locality").replace("?", ""));
    				}

    				if (obj.has("type") && obj.getString("type") != null && !obj.getString("type").equalsIgnoreCase("")
    					&& !obj.getString("type").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setType(obj.getString("type").replace("?", ""));
    				}
    				if (obj.has("lat") && obj.getString("lat") != null && !obj.getString("lat").equalsIgnoreCase("")
    					&& !obj.getString("lat").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setLatitude(obj.getString("lat").replace("?", ""));
    				}
    				if (obj.has("lon") && obj.getString("lon") != null && !obj.getString("lon").equalsIgnoreCase("")
    					&& !obj.getString("lon").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setLongitude(obj.getString("lon").replace("?", ""));
    				}
    				if (obj.has("doctype") && obj.getString("doctype") != null && !obj.getString("doctype").equalsIgnoreCase("")
    					&& !obj.getString("doctype").equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setDoctype(obj.getString("doctype").replace("?", ""));
    				}
    				if (obj.has("avgrate") && obj.getString("avgrate") != null && !obj.getString("avgrate").equalsIgnoreCase("")
    					&& !obj.getString("avgrate").equalsIgnoreCase(null) && !obj.getString("avgrate").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setRating(obj.getString("avgrate"));
    				}
    				if (obj.has("distance") && obj.getString("distance") != null && !obj.getString("distance").equalsIgnoreCase("")
    					&& !obj.getString("distance").equalsIgnoreCase(null) && !obj.getString("distance").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setDistance(obj.getString("distance"));
    				}
    				if (obj.has("pic") && obj.getString("pic") != null && !obj.getString("pic").equalsIgnoreCase("")
    					&& !obj.getString("pic").equalsIgnoreCase(null) && !obj.getString("pic").equalsIgnoreCase("null")) {
    				    autoCompleteInfo.setPic(obj.getString("pic"));
    				}

    				if (obj.has("second_image") && obj.getString("second_image") != null
    					&& !obj.getString("second_image").trim().equalsIgnoreCase("")
    					&& !obj.getString("second_image").trim().equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setDocSecondImage(obj.getString("second_image").trim());
    				}

    				if (obj.has("emergencyPic") && obj.getString("emergencyPic") != null
    					&& !obj.getString("emergencyPic").trim().equalsIgnoreCase("")
    					&& !obj.getString("emergencyPic").trim().equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setHospitalEmergencyPic(obj.getString("emergencyPic").trim());
    				}
    				if (obj.has("leftPic") && obj.getString("leftPic") != null && !obj.getString("leftPic").trim().equalsIgnoreCase("")
    					&& !obj.getString("leftPic").trim().equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setHospitalLeftPic(obj.getString("leftPic").trim());
    				}
    				if (obj.has("rightPic") && obj.getString("rightPic") != null && !obj.getString("rightPic").trim().equalsIgnoreCase("")
    					&& !obj.getString("rightPic").trim().equalsIgnoreCase(null)) {
    				    autoCompleteInfo.setHospitalRightPic(obj.getString("rightPic").trim());
    				}
    				if (obj.has("hospitalName") && obj.getString("hospitalName") != null && !obj.getString("hospitalName").trim().equalsIgnoreCase("")
        					&& !obj.getString("hospitalName").trim().equalsIgnoreCase(null)) {
        				    autoCompleteInfo.setHospitalName(obj.getString("hospitalName").trim());
        				}

    				globalSearchList.add(autoCompleteInfo);
    			    }
    			}

    			if (globalSearchList != null && globalSearchList.size() > 0) {
    			    //globalListView.setVisibility(View.VISIBLE);
    				if( globalListView.getVisibility()==View.VISIBLE)
    					doctorsListView.setVisibility(View.GONE);
    			    txtNoResultFound.setVisibility(View.GONE);
    			    autoCompleteAdapter = new AutoCompleteAdapter(SpecialistDoctorsActivity.this, R.layout.autocompletesearch,globalSearchList,getIntent().getExtras().getString("key_specialistID"));
    			    globalListView.setAdapter(autoCompleteAdapter);

    			} else {  			    
    				
    			    	globalListView.setVisibility(View.GONE);
    			    	//if(((EditText)findViewById(R.id.searchedittext)).getText().toString().length()!=0){
	    					doctorsListView.setVisibility(View.GONE);
	    					txtNoResultFound.setVisibility(View.VISIBLE);
    			    	
    			    ((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.GONE);
    			    
    			}

    		    }

    		}
    		if (AppConstants.LogLevel == 1) {
    		    try {
    			postServerTimepend = Utils.postServerTimeSpend();
    			totalScreenTimeSpend = Utils.totalScreenTimeSpend();
    			Utils.callPerfomanceTestingApi("Doctor Search","HomeScreen","ListUserGlobalSearch",preServerTimeSpend,serverTimeSpend,
    			    postServerTimepend,totalScreenTimeSpend,Utils.getNetworkType(SpecialistDoctorsActivity.this),
    			    ApplicationSettings.getPref(AppConstants.SERVER_PROCESSTIME, ""),AppConstants.perfTestCode);
    		    } catch (Exception e) {
    			e.printStackTrace();
    		    }
    		}
    	    } catch (JSONException e) {
    		globalListView.setVisibility(View.GONE);
    		doctorsListView.setVisibility(View.GONE);
    		((LinearLayout) findViewById(R.id.layoutNoNetWork)).setVisibility(View.VISIBLE);
    		txtNoResultFound.setVisibility(View.GONE);
    	    }

    	}

        }
    
    private void globalSearchListViewClickEvent() {
    	globalListView.setOnItemClickListener(new OnItemClickListener() {
    	    @Override
    	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		AutoCompleteInfo entry = (AutoCompleteInfo) parent.getItemAtPosition(position);
    		if (parent != null && entry != null) {
    		    isEventPressed = true;
    		    if (entry.getType().equalsIgnoreCase("Doctor")) {
    			easyTracker.send(MapBuilder.createEvent("Doctor Tab", " View Doctor", entry.getName(), null).build());
    			ApplicationSettings.putPref(AppConstants.DOCTOR_ID, entry.getId());
    			Intent intent = new Intent(SpecialistDoctorsActivity.this, DoctorDetailsActivity.class);
    			intent.putExtra("CALLING_SCREEN", "HomeScreen");
    			startActivity(intent);
    		    } else {
    			easyTracker.send(MapBuilder.createEvent("Doctor Tab", "View Hospital", entry.getName(), null).build());
    			ApplicationSettings.putPref(AppConstants.HOSPITAL_ID, entry.getId());
    			Intent intent = new Intent(SpecialistDoctorsActivity.this, HospitalDetailsActivity.class);
    			intent.putExtra("CALLING_SCREEN", "HomeScreen");
    			startActivity(intent);
    		    }

    		}
    	    }
    	});
        }

}
