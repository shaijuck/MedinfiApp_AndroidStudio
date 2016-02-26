package com.medinfi.adapters;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.medinfi.MedicalReportsActivity;
import com.medinfi.R;
import com.medinfi.adapters.FavouriteAdapter.FavouriteHolder;
import com.medinfi.datainfo.DoctorDetailInfo;
import com.medinfi.datainfo.FavouriteInfo;
import com.medinfi.datainfo.MedicalReportsInfo;
import com.medinfi.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MedicalReportsAdapter extends ArrayAdapter<MedicalReportsInfo> {

    /* Apdater used to load the Hospitals information in the listview */
    Context context;
    int layoutResourceId;
    public ArrayList<MedicalReportsInfo> medicalRecordsList;
    private ArrayList<MedicalReportsInfo> arraylist;
    private ImageLoader imageLoader;
    DisplayImageOptions options;
    private Typeface tfBold, tfRegular, tfLite, tfCondensed;
    private EasyTracker easyTracker = null;
    private int iSize = 0;

    public MedicalReportsAdapter(Context context, int layoutResourceId, ArrayList<MedicalReportsInfo> medicalRecordsList) {
	super(context, layoutResourceId, medicalRecordsList);
	this.layoutResourceId = layoutResourceId;
	this.context = context;
	this.medicalRecordsList = medicalRecordsList;
	this.arraylist = new ArrayList<MedicalReportsInfo>();
	this.arraylist.addAll(medicalRecordsList);
	easyTracker = EasyTracker.getInstance(context);
	imageLoader = ImageLoader.getInstance();

	options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.prescription_icon).showImageOnFail(R.drawable.prescription_icon)
		.showImageOnLoading(R.drawable.prescription_icon).build();

	tfBold = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoBold.ttf");
	tfLite = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoLight.ttf");
	tfRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoRegular.ttf");
	tfCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed.ttf");
    }
    
    // get the object based on array position
    @Override
    public MedicalReportsInfo getItem(int position) {
	return medicalRecordsList.get(position);
    }

    /* get the id based on array position */
    @Override
    public long getItemId(int position) {
	return position;
    }


    /*
     * add the view based on the count of arraylist set the values using getter
     * setter methods
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
	View row = convertView;
	MedicalRecordHolder holder = null;

	if (row == null) {
	    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    row = inflater.inflate(layoutResourceId, parent, false);

	    holder = new MedicalRecordHolder();

	    holder.imgRecords = (ImageView) row.findViewById(R.id.imgRecords);
	 //   holder.imgSecondPic = (ImageView) row.findViewById(R.id.imgSecondPic);
	    holder.txtDoctorName = (TextView) row.findViewById(R.id.txtDoctorName);
	    holder.txtHospitalName = (TextView) row.findViewById(R.id.txtHospitalName);
	    holder.txtCreatedDate = (TextView) row.findViewById(R.id.txtCreatedDate);
	    holder.imgDownload = (ImageView) row.findViewById(R.id.imgDownload);
	    holder.imgRecordsType = (ImageView) row.findViewById(R.id.imgRecordsType);
	    holder.imgRelationType = (ImageView) row.findViewById(R.id.imgRelationType);

	    holder.txtDoctorName.setVisibility(View.GONE);
	    holder.txtHospitalName.setVisibility(View.GONE);

	    row.setTag(holder);
	} else {
	    holder = (MedicalRecordHolder) row.getTag();
	}

	MedicalReportsInfo medicalRecordsInfo = medicalRecordsList.get(position);
	if (medicalRecordsInfo.getMedRecordMultiplePic() != null && medicalRecordsInfo.getMedRecordMultiplePic().size() > 0) {
	     iSize = medicalRecordsInfo.getMedRecordMultiplePic().size();
	 //   for (int i = 0; i < iSize; i++) {
		if (iSize == 1) {
		    imageLoader.displayImage(medicalRecordsInfo.getMedRecordMultiplePic().get(0), holder.imgRecords, options);
		    holder.imgSecondPic.setVisibility(View.GONE);
		}
		if (iSize == 2) {
		    holder.imgSecondPic.setVisibility(View.VISIBLE);
		    imageLoader.displayImage(medicalRecordsInfo.getMedRecordMultiplePic().get(0), holder.imgRecords, options);
		    imageLoader.displayImage(medicalRecordsInfo.getMedRecordMultiplePic().get(1), holder.imgSecondPic, options);
		}

	//    }
	    
	    if (medicalRecordsInfo.getMedRecordRecordType() != null && medicalRecordsInfo.getMedRecordRecordType().equalsIgnoreCase("Prescription")) {
		holder.imgRecordsType.setImageResource(R.drawable.prescription_icon);
	    } else if (medicalRecordsInfo.getMedRecordRecordType() != null && medicalRecordsInfo.getMedRecordRecordType().equalsIgnoreCase("Reports")) {
		holder.imgRecordsType.setImageResource(R.drawable.reports_icon);
	    } else if (medicalRecordsInfo.getMedRecordRecordType() != null && medicalRecordsInfo.getMedRecordRecordType().equalsIgnoreCase("Bills")) {
		holder.imgRecordsType.setImageResource(R.drawable.bill_icon);
	    }

	    if (medicalRecordsInfo.getMedRecordRelationType() != null && medicalRecordsInfo.getMedRecordRelationType().equalsIgnoreCase("MOM")) {
		holder.imgRelationType.setImageResource(R.drawable.mom_icon);
	    } else if (medicalRecordsInfo.getMedRecordRelationType() != null && medicalRecordsInfo.getMedRecordRelationType().equalsIgnoreCase("DAD")) {
		holder.imgRelationType.setImageResource(R.drawable.dad_icon);
	    }

	} else {
	    holder.imgRecords.setImageResource(R.drawable.prescription_icon);
	}
	if (medicalRecordsInfo.getMedRecordDoctorName() != null && !medicalRecordsInfo.getMedRecordDoctorName().equalsIgnoreCase("")
		&& !medicalRecordsInfo.getMedRecordDoctorName().equalsIgnoreCase(null)
		&& !medicalRecordsInfo.getMedRecordDoctorName().equalsIgnoreCase("null")) {
	    holder.txtDoctorName.setVisibility(View.VISIBLE);
	    holder.txtDoctorName.setText(medicalRecordsInfo.getMedRecordDoctorName());
	}else{
	    holder.txtDoctorName.setVisibility(View.GONE);
	}
	if (medicalRecordsInfo.getMedRecordHospitalName() != null && !medicalRecordsInfo.getMedRecordHospitalName().equalsIgnoreCase("")
		&& !medicalRecordsInfo.getMedRecordHospitalName().equalsIgnoreCase(null)
		&& !medicalRecordsInfo.getMedRecordHospitalName().equalsIgnoreCase("null")) {
	    holder.txtHospitalName.setVisibility(View.VISIBLE);
	    holder.txtHospitalName.setText(medicalRecordsInfo.getMedRecordHospitalName());
	}else{
	    holder.txtHospitalName.setVisibility(View.GONE);
	}
	if (medicalRecordsInfo.getMedRecordRecordDate() != null && !medicalRecordsInfo.getMedRecordRecordDate().equalsIgnoreCase("")
		&& !medicalRecordsInfo.getMedRecordRecordDate().equalsIgnoreCase(null)
		&& !medicalRecordsInfo.getMedRecordRecordDate().equalsIgnoreCase("null")) {
	    holder.txtCreatedDate.setText(medicalRecordsInfo.getMedRecordRecordDate());
	}
	holder.imgRecords.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (medicalRecordsList.get(position).getMedRecordMultiplePic() != null) {
		    enlargImageDialog(position);
		}
	    }
	});
	holder.imgDownload.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (medicalRecordsList.get(position).getMedRecordMultiplePic() != null) {
		    try {
			if (medicalRecordsList.get(position).getMedRecordDoctorName() != null
				&& !medicalRecordsList.get(position).getMedRecordDoctorName().equalsIgnoreCase("")
				&& !medicalRecordsList.get(position).getMedRecordDoctorName().equalsIgnoreCase(null)
				&& !medicalRecordsList.get(position).getMedRecordDoctorName().equalsIgnoreCase("null")) {
			   
			    if (checkDownloadedFileExist(medicalRecordsList.get(position).getMedRecordDoctorName(), medicalRecordsList.get(position)
				    .getMedRecordCreatedDate())) {
				String fileName = Environment.getExternalStorageDirectory().getPath() + "/MedRecords/";
				Toast.makeText(context, Utils.getCustomeFontStyle(context, context.getString(R.string.reportDownloadedAlready)+" "+fileName), Toast.LENGTH_LONG).show();
			    } else {
				 if(iSize == 1){
				     new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordMultiplePic().get(0), medicalRecordsList.get(position)
						.getMedRecordDoctorName(), medicalRecordsList.get(position).getMedRecordCreatedDate());
				    }else if(iSize == 2){
					new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordMultiplePic().get(0), medicalRecordsList.get(position)
						.getMedRecordDoctorName(), medicalRecordsList.get(position).getMedRecordCreatedDate());
					new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordMultiplePic().get(1), medicalRecordsList.get(position)
						.getMedRecordDoctorName(), medicalRecordsList.get(position).getMedRecordCreatedDate());
				    }
				
			    }

			} else if (medicalRecordsList.get(position).getMedRecordHospitalName() != null
				&& !medicalRecordsList.get(position).getMedRecordHospitalName().equalsIgnoreCase("")
				&& !medicalRecordsList.get(position).getMedRecordHospitalName().equalsIgnoreCase(null)
				&& !medicalRecordsList.get(position).getMedRecordHospitalName().equalsIgnoreCase("null")) {
			    if (checkDownloadedFileExist(medicalRecordsList.get(position).getMedRecordHospitalName(), medicalRecordsList
				    .get(position).getMedRecordCreatedDate())) {
				String fileName = Environment.getExternalStorageDirectory().getPath() + "/MedRecords/";
				Toast.makeText(context, Utils.getCustomeFontStyle(context, context.getString(R.string.reportDownloadedAlready)+" "+fileName), Toast.LENGTH_LONG).show();
			    } else {
				 if(iSize == 1){
				     new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordMultiplePic().get(0), medicalRecordsList.get(position)
						.getMedRecordHospitalName(), medicalRecordsList.get(position).getMedRecordCreatedDate());
				 }else if(iSize == 2){
				     new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordMultiplePic().get(0), medicalRecordsList.get(position)
						.getMedRecordHospitalName(), medicalRecordsList.get(position).getMedRecordCreatedDate());
				     new DownloadFile().execute(medicalRecordsList.get(position).getMedRecordMultiplePic().get(1), medicalRecordsList.get(position)
						.getMedRecordHospitalName(), medicalRecordsList.get(position).getMedRecordCreatedDate());
				 }
				
			    }

			}

		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	});
	return row;
    }

    /* holder which holds the view associated to the adapter */
    static class MedicalRecordHolder {
	ImageView imgRecords;
	ImageView imgSecondPic;
	ImageView imgRecordsType;
	ImageView imgRelationType;
	ImageView imgDownload;
	TextView txtDoctorName;
	TextView txtHospitalName;
	TextView txtCreatedDate;

    }

    private void enlargImageDialog(int iPosition) {
	final Dialog dialog = new Dialog(context);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	Rect displayRectangle = new Rect();
	Window window = ((Activity) context).getWindow();
	window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.profile_pic_display, null);
	layout.setMinimumWidth((int) (displayRectangle.width() * 0.75f));
	// layout.setMinimumHeight((int) (displayRectangle.height() * 0.9f));

	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.ic_action_person).showImageOnFail(R.drawable.ic_action_person).build();

	dialog.setContentView(layout);
	dialog.setCancelable(true);
	dialog.show();
	ImageView imgProfilePicDisplay = (ImageView) dialog.findViewById(R.id.imgProfilePicDisplay);

	if (medicalRecordsList.get(iPosition).getMedRecordMultiplePic() != null) {
	    imageLoader.displayImage(medicalRecordsList.get(iPosition).getMedRecordMultiplePic().get(0), imgProfilePicDisplay, options, new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);

		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.GONE);
		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		    ((ProgressBar) dialog.findViewById(R.id.progressBar)).setVisibility(View.GONE);
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {

		}
	    });
	} else {
	    imgProfilePicDisplay.setImageResource(R.drawable.ic_action_person);
	}

	Button btnCancel = (Button) dialog.findViewById(R.id.close_button_hs);
	btnCancel.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		try {
		    dialog.dismiss();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    File fileRecord;

    class DownloadFile extends AsyncTask<String, String, String> {
	ProgressDialog mProgressDialog = new ProgressDialog(context);
	String strFolderName;

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    mProgressDialog.setMessage(Utils.getCustomeFontStyle(context, context.getString(R.string.reportDownloading)));
	    /*
	     * mProgressDialog.setIndeterminate(false);
	     * mProgressDialog.setMax(100); mProgressDialog.setCancelable(true);
	     * mProgressDialog
	     * .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	     */
	    mProgressDialog.setCancelable(true);
	    mProgressDialog.show();
	}

	@Override
	protected String doInBackground(String... aurl) {
	    int count;
	    try {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    URL url = new URL(aurl[0]);
		    URLConnection conection = url.openConnection();
		    conection.connect();
		    // getting file length
		    int lenghtOfFile = conection.getContentLength();
		    InputStream input = new BufferedInputStream(url.openStream(), 8192);

		    String strDirPath = Environment.getExternalStorageDirectory().getPath() + "/MedRecords/";
		    File dirLog = new File(strDirPath);
		    if (!dirLog.isDirectory() || !dirLog.exists()) {
			dirLog.mkdirs();
		    }
		    String strLogFile = "Med" + "_" + aurl[1] + "_" + aurl[2] + ".png";
		    fileRecord = new File(dirLog, strLogFile);
		    if (!fileRecord.exists()) {
			fileRecord.createNewFile();
		    }
		    long total = 0;
		    OutputStream output = new FileOutputStream(fileRecord);
		    byte data[] = new byte[1024];
		    while ((count = input.read(data)) != -1) {
			total += count;
			// publishing the progress....
			// After this onProgressUpdate will be called
			// publishProgress(""+(int)((total*100)/lenghtOfFile));

			// writing data to file
			output.write(data, 0, count);
		    }

		    // flushing output
		    output.flush();

		    // closing streams
		    output.close();
		    input.close();
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    return null;
	}

	/*
	 * protected void onProgressUpdate(Integer... progress) {
	 * mProgressDialog.setProgress(progress[0]);
	 * if(mProgressDialog.getProgress()==mProgressDialog.getMax()){
	 * mProgressDialog.dismiss(); Toast.makeText(context,
	 * "Downloaded in -> "+fileRecord, Toast.LENGTH_LONG).show(); } }
	 */
	protected void onPostExecute(String result) {
	    if (mProgressDialog != null && mProgressDialog.isShowing()) {
		mProgressDialog.dismiss();
		Toast.makeText(context, Utils.getCustomeFontStyle(context, context.getString(R.string.reportDownloadInto)+" "+fileRecord), Toast.LENGTH_LONG).show();
		
	    }

	}
    }

    private boolean checkDownloadedFileExist(String doctorName, String hospitalName) {
	boolean isExist = false;
	try {
	    String strDirPath = Environment.getExternalStorageDirectory().getPath() + "/MedRecords/";
	    File dirLog = new File(strDirPath);

	    String strLogFile = "Med" + "_" + doctorName + "_" + hospitalName + ".png";
	    File fileRecord = new File(dirLog, strLogFile);
	    if (fileRecord.exists()) {
		isExist = true;
	    } else {
		isExist = false;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return isExist;
    }
}