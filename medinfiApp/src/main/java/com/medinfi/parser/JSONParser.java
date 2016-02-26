package com.medinfi.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.medinfi.main.AppConstants;
import com.medinfi.utils.ApplicationSettings;
import com.medinfi.utils.Utils;

/**
 * 
 * @author AppsOnClick
 * 
 */
public class JSONParser {

	String result = "";
	static JSONObject jObj = null;

	static String json = "";

	JSONObject data = new JSONObject();

	public JSONParser() {

	}

	public String getGeneralPhysicians(String latitude, String longitude,String offset) {
		try {
			String url = AppConstants.BASE_URL + "api/listgenphysician";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("lat", latitude));
			postparams.add(new BasicNameValuePair("lon", longitude));
			
			String city = ApplicationSettings.getPref(
					AppConstants.CURRENT_USER_LOCATION, "");
			if (city.equalsIgnoreCase("Bengaluru"))
			{
				postparams.add(new BasicNameValuePair("city", "Bangalore"));
			}
			else
			{
				postparams.add(new BasicNameValuePair("city", city));
			}
			
			
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			postparams.add(new BasicNameValuePair(AppConstants.OFFSET, offset));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

	public String getGeneralPhysiciansTopRated(String latitude, String longitude) {
		try {
			String url = AppConstants.BASE_URL + "api/listgenphysician";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("lat", latitude));
			postparams.add(new BasicNameValuePair("lon", longitude));
			postparams.add(new BasicNameValuePair("sort", "rate"));
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

	public String getCityList() {

		try {
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String url = AppConstants.BASE_URL + "api/listcity";
			
			
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}
		return json;
	}

	public String getHospitalList(String latitude, String longitude,String offset) {
		try {
			String url = AppConstants.BASE_URL + "api/ListHospital";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("lat", latitude));
			postparams.add(new BasicNameValuePair("lon", longitude));
			postparams.add(new BasicNameValuePair(AppConstants.OFFSET, offset.trim().toString()));
			
			String city = ApplicationSettings.getPref(
					AppConstants.CURRENT_USER_LOCATION, "");
			if (city.equalsIgnoreCase("Bengaluru"))
			{
				postparams.add(new BasicNameValuePair("city", "Bangalore"));
			}
			else
			{
				postparams.add(new BasicNameValuePair("city", city));
			}
			
			
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

	public String getTopRatedHospitalList(String latitude, String longitude) {
		try {
			String url = AppConstants.BASE_URL + "api/ListHospital";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("lat", latitude));
			postparams.add(new BasicNameValuePair("lon", longitude));
			postparams.add(new BasicNameValuePair("sort", "rate"));
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

	public String getLocations(String cityID) {
		try {
			String url = AppConstants.BASE_URL + "api/listlocation";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("cityId", cityID));
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

	public String getDoctorDetails(String doctorID) {
		try {
			String url = AppConstants.BASE_URL + "api/DoctorDetail";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("doctorId", doctorID));
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			postparams.add(new BasicNameValuePair("userId", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
			postparams.add(new BasicNameValuePair("lat", ApplicationSettings.getPref(AppConstants.LATITUDE, "")));
			postparams.add(new BasicNameValuePair("lon", ApplicationSettings.getPref(AppConstants.LONGITUDE, "")));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

	public String getHospitalDetails(String hospitalID) {
		try {
			String url = AppConstants.BASE_URL + "api/HospitalDetail";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("hospitalId", hospitalID));
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			postparams.add(new BasicNameValuePair("userId", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
			postparams.add(new BasicNameValuePair("lat", ApplicationSettings.getPref(AppConstants.LATITUDE, "")));
			postparams.add(new BasicNameValuePair("lon", ApplicationSettings.getPref(AppConstants.LONGITUDE, "")));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

	public String postDoctorDetails(String jsonObject) throws JSONException {

		try {
			String urlPath = AppConstants.BASE_URL + "api/AddRate";
			
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			urlPath += "?" + paramString;
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(urlPath);

			StringEntity entity = new StringEntity(jsonObject);

			entity.setContentType("application/json");
			httpPost.setEntity(entity);

			HttpResponse responsePOST = httpClient.execute(httpPost);

			HttpEntity httpEntity = responsePOST.getEntity();
			if (httpEntity != null) {
				result = EntityUtils.toString(httpEntity);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		try {
			jObj = new JSONObject(json);

		} catch (JSONException e) {
		}
		// return JSON Object
		return json;

	}

	public String postHospitalDetails(String jsonObject) throws JSONException {

		try {
			String urlPath = AppConstants.BASE_URL + "api/AddHospitalRate";
			
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			urlPath += "?" + paramString;
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(urlPath);

			StringEntity entity = new StringEntity(jsonObject);

			entity.setContentType("application/json");
			httpPost.setEntity(entity);

			HttpResponse responsePOST = httpClient.execute(httpPost);

			HttpEntity httpEntity = responsePOST.getEntity();
			if (httpEntity != null) {
				result = EntityUtils.toString(httpEntity);

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		try {
			jObj = new JSONObject(json);

		} catch (JSONException e) {
		}
		return json;

	}

	public String postMedicineDelivery(String jsonObject) throws JSONException {

		try {
			String urlPath = AppConstants.BASE_URL + "api/MedicineDelivery";
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(urlPath);

			StringEntity entity = new StringEntity(jsonObject);

			entity.setContentType("application/json");
			httpPost.setEntity(entity);

			HttpResponse responsePOST = httpClient.execute(httpPost);

			HttpEntity httpEntity = responsePOST.getEntity();
			if (httpEntity != null) {
				result = EntityUtils.toString(httpEntity);

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		try {
			jObj = new JSONObject(json);

		} catch (JSONException e) {
		}
		System.out
				.println("json valuejson valuejson valuejson valuejson valuejson valuejson valuejson valuejson valuejson valuejson "
						+ json);
		// return JSON Object
		return json;

	}

	public String getSpecialistList() {

		try {
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String url = AppConstants.BASE_URL + "api/ListSpecial";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
	        postparams.add(new BasicNameValuePair("city", ApplicationSettings.getPref(
		    AppConstants.CURRENT_USER_LOCATION, "")));
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}
		return json;
	}

	public String getSpecialistsbyID(String specialistID, String latitude,
			String longitude,String offset) {
		try {
			String url = AppConstants.BASE_URL + "api/ListSplDoctor";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("splId", specialistID));
			postparams.add(new BasicNameValuePair("lat", latitude));
			postparams.add(new BasicNameValuePair("lon", longitude));
			postparams.add(new BasicNameValuePair(AppConstants.OFFSET, offset));
			
			
			String city = ApplicationSettings.getPref(
					AppConstants.CURRENT_USER_LOCATION, "");
			if (city.equalsIgnoreCase("Bengaluru"))
			{
				postparams.add(new BasicNameValuePair("city", "Bangalore"));
			}
			else
			{
				postparams.add(new BasicNameValuePair("city", city));
			}
			
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

	public String getSpecialistsTopRated(String specialistID, String latitude,
			String longitude) {
		try {
			String url = AppConstants.BASE_URL + "api/ListSplDoctor";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("splId", specialistID));
			postparams.add(new BasicNameValuePair("lat", latitude));
			postparams.add(new BasicNameValuePair("lon", longitude));
			postparams.add(new BasicNameValuePair("sort", "rate"));
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}

    public String Register(String emailId, String password, String deviceManufacture, String deviceModel,
 int deviceApiLevel, String deviceISO, String deviceIMEI, boolean isNewUser) {
	try {
	    String url;
	    if (!isNewUser) {
		url = AppConstants.BASE_URL + "api/SignIn";
	    } else {
		url = AppConstants.BASE_URL + "api/Register";
	    }
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("email", emailId));
	    postparams.add(new BasicNameValuePair("password", password));

	    if (isNewUser) {
		postparams.add(new BasicNameValuePair("ManufactureName", deviceManufacture));
		postparams.add(new BasicNameValuePair("Model", deviceModel));
		postparams.add(new BasicNameValuePair("APILevel", String.valueOf(deviceApiLevel)));
		postparams.add(new BasicNameValuePair("ISO", deviceISO));
		postparams.add(new BasicNameValuePair("DeviceIMEI", deviceIMEI));
	    }

	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	// return JSON String
	return json;

    }

	public String Register(String phoneno, String email) {
		try {
			String url = AppConstants.BASE_URL + "api/Register";
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("phone", phoneno);
			jsonObj.put("email", email);

			StringEntity entity = new StringEntity(jsonObj.toString());

			entity.setContentType("application/json");
			httpPost.setEntity(entity);

			HttpResponse responsePOST = httpClient.execute(httpPost);

			HttpEntity httpEntity = responsePOST.getEntity();
			if (httpEntity != null) {
				result = EntityUtils.toString(httpEntity);

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {

			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		try {
			jObj = new JSONObject(json);

		} catch (JSONException e) {
		}

		return json;

	}

	public String postUserComments(String userID, String message,String email,String deviceId) {
		try {

			String url = AppConstants.BASE_URL + "api/CreateUserFeedback";
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("userId", userID));
			postparams.add(new BasicNameValuePair("feedback", message));
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			postparams.add(new BasicNameValuePair("email", email));
			postparams.add(new BasicNameValuePair("deviceId", deviceId));
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;

	}
	public String getAddress(String latitude, String longitude) {
		try {
			
			String url="http://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=true";
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}

		// return JSON String
		return json;
	}
	
	
	public String getAllSearch(String keyword,String latitude, String longitude,String city,String searchType) 
	{
		String url=null;
		try {	
				if(searchType.equalsIgnoreCase("0"))
				{
					url = AppConstants.BASE_URL + "api/SearchHospital";
				}
				else
				{
					 url = AppConstants.BASE_URL + "api/SearchDoctor";
				}
				
			
			List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			postparams.add(new BasicNameValuePair("city", city));
			postparams.add(new BasicNameValuePair("lat", latitude));
			postparams.add(new BasicNameValuePair("lon", longitude));
			postparams.add(new BasicNameValuePair("term", keyword));
			postparams.add(new BasicNameValuePair("type", searchType));
			postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
			
			DefaultHttpClient httpClient1 = new DefaultHttpClient();
			String paramString = URLEncodedUtils.format(postparams, "utf-8");

			url += "?" + paramString;
			
			Log.e("Print Values:::::::::::::::::::::::::::::::",url);
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient1.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] bytess = new byte[16384];

			while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
				buffer.write(bytess, 0, nRead);
			}

			buffer.flush();

			result = buffer.toString();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(result);
			json = sb.toString();
		} catch (Exception e) {
		}
		return json;

	}
    public String getSessionId(String userId, String session_id, String tokenId, String lat, String lon,
	    String gps_status, String identified_location, String identified_city, String location_detected,String autoDetection,
	    String os_details,String device_type,String device_id
	    ) {//,String user_type,
	try {
	    String url = AppConstants.BASE_URL + "api/CreateUserSession";
	    
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    
	    postparams.add(new BasicNameValuePair("userId", userId));
	    postparams.add(new BasicNameValuePair("session_id", session_id));
	    postparams.add(new BasicNameValuePair("token", tokenId));
	    
	    postparams.add(new BasicNameValuePair("identified_location", identified_location));
	    postparams.add(new BasicNameValuePair("identified_city", identified_city));
	    
	    postparams.add(new BasicNameValuePair("lat", lat));
	    postparams.add(new BasicNameValuePair("lon", lon));
	    postparams.add(new BasicNameValuePair("gps_status", gps_status));
	    if(identified_location.length() > 0){
		 postparams.add(new BasicNameValuePair("location_detected", "1"));
	    }else{
		 postparams.add(new BasicNameValuePair("location_detected", location_detected));
	    }
	    postparams.add(new BasicNameValuePair("os_details", os_details));
	    postparams.add(new BasicNameValuePair("device_type", device_type));
	    postparams.add(new BasicNameValuePair("device_id", device_id));
	    

	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	// return JSON String
	return json;

    }
    public String updateSessionId(String userId, String session_id, String tokenId, String selected_location,
	    String selected_city) {
	try {
	    String url = AppConstants.BASE_URL + "api/UpdateUserSessionLog";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("userId", userId));
	    postparams.add(new BasicNameValuePair("session_id", session_id));
	    postparams.add(new BasicNameValuePair("token", tokenId));
	    postparams.add(new BasicNameValuePair("selected_location", selected_location));
	    postparams.add(new BasicNameValuePair("selected_city", selected_city));

	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	// return JSON String
	return json;

    }
    
    public String getCurrentSessionId(String userId) {
	try {
	    String url = AppConstants.BASE_URL + "api/GenerateToken/";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("userId", userId));
	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	// return JSON String
	return json;

    }
    
    public String userDeviceUpdate(String userID, String deviceIMEI) {
	try {

	    String url = AppConstants.BASE_URL + "api/KeepAliveUserUpdate";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("userId", userID));
	    postparams.add(new BasicNameValuePair("DeviceIMEI", deviceIMEI));
	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	// return JSON String
	return json;

    }
    public String getAutoDetectedCityId(String detectedCityName) {
	try {

	    String url = AppConstants.BASE_URL + "api/getCityId";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("userId", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
	    postparams.add(new BasicNameValuePair("city", detectedCityName));
	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);
	    
	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}
	return json;

    }
    public String getCurrentLocationGeoCoderAPI(double latitute, double longitute) {
	try {
	    String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitute + ","+ longitute + "&sensor=false";
	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    
	    HttpParams params = httpClient1.getParams();
	    HttpConnectionParams.setConnectionTimeout(params, 10000);
	    HttpConnectionParams.setSoTimeout(params, 10000);
	    
	    HttpGet httpGet = new HttpGet(url);
	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}
	return json;

    }

    public String forgotPassword(String emailId) {
	try {
	    String url = AppConstants.BASE_URL + "api/PasswordRequest";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("email", emailId));

	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	// return JSON String
	return json;

    }
    public String getFavouriteList() {
	try {
	    String url = AppConstants.BASE_URL + "api/ListUserFavourites";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
	    postparams.add(new BasicNameValuePair("lat", ApplicationSettings.getPref(AppConstants.LATITUDE, "")));
		postparams.add(new BasicNameValuePair("lon", ApplicationSettings.getPref(AppConstants.LONGITUDE, "")));
		
	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	// return JSON String
	return json;
    }

    public String addFavourite(String type, String docHospitalId) {
	String url = AppConstants.BASE_URL + "api/AddUserFavourites";
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost(url);
	try {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
	    nameValuePairs.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
	    nameValuePairs.add(new BasicNameValuePair("type", type));
	    nameValuePairs.add(new BasicNameValuePair("doctor_hospital_id", docHospitalId));
	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    // Execute HTTP Post Request
	    HttpResponse response = httpclient.execute(httppost);
	    HttpEntity httpEntity = response.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }
	    buffer.flush();
	    result = buffer.toString();

	} catch (ClientProtocolException e) {
	} catch (IOException e) {
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}
	// return JSON String
	return json;
    }

    public String postContactsDetails(String jsonObject) throws JSONException {
	String url = AppConstants.BASE_URL + "api/SyncUserContacts";
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost(url);
	try {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
	    nameValuePairs.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
	    nameValuePairs.add(new BasicNameValuePair("contactlist", jsonObject));
	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    // Execute HTTP Post Request
	    HttpResponse response = httpclient.execute(httppost);
	    HttpEntity httpEntity = response.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }
	    buffer.flush();
	    result = buffer.toString();

	} catch (ClientProtocolException e) {
	} catch (IOException e) {
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}
	// return JSON String
	return json;
    }
    public String addAllFavourite(String docHospitalTypeId) {
   	String url = AppConstants.BASE_URL + "api/AddUserAllFavourites";
   	HttpClient httpclient = new DefaultHttpClient();
   	HttpPost httppost = new HttpPost(url);
   	try {
   	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
   	    nameValuePairs.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
   	    nameValuePairs.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
   	    nameValuePairs.add(new BasicNameValuePair("docHospitalSaveList", docHospitalTypeId));
   	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
   	    // Execute HTTP Post Request
   	    HttpResponse response = httpclient.execute(httppost);
   	    HttpEntity httpEntity = response.getEntity();
   	    InputStream is = httpEntity.getContent();

   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

   	    int nRead;
   	    byte[] bytess = new byte[16384];

   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
   		buffer.write(bytess, 0, nRead);
   	    }
   	    buffer.flush();
   	    result = buffer.toString();

   	} catch (ClientProtocolException e) {
   	} catch (IOException e) {
   	}
   	try {
   	    StringBuilder sb = new StringBuilder();
   	    sb.append(result);
   	    json = sb.toString();
   	} catch (Exception e) {
   	}
   	// return JSON String
   	return json;
       }
    public String deleteFavourite(String type, String docHospitalId) {
   	String url = AppConstants.BASE_URL + "api/DeleteUserFavourites";
   	HttpClient httpclient = new DefaultHttpClient();
   	HttpPost httppost = new HttpPost(url);
   	try {
   	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
   	    nameValuePairs.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
   	    nameValuePairs.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
   	    nameValuePairs.add(new BasicNameValuePair("type", type));
   	    nameValuePairs.add(new BasicNameValuePair("doctor_hospital_id", docHospitalId));
   	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
   	    // Execute HTTP Post Request
   	    HttpResponse response = httpclient.execute(httppost);
   	    HttpEntity httpEntity = response.getEntity();
   	    InputStream is = httpEntity.getContent();

   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

   	    int nRead;
   	    byte[] bytess = new byte[16384];

   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
   		buffer.write(bytess, 0, nRead);
   	    }
   	    buffer.flush();
   	    result = buffer.toString();

   	} catch (ClientProtocolException e) {
   	} catch (IOException e) {
   	}
   	try {
   	    StringBuilder sb = new StringBuilder();
   	    sb.append(result);
   	    json = sb.toString();
   	} catch (Exception e) {
   	}
   	// return JSON String
   	return json;
       }
    
    public String deleteAllFavourite(String docHospitalTypeId) {
   	String url = AppConstants.BASE_URL + "api/DeleteUserAllFavourites";
   	HttpClient httpclient = new DefaultHttpClient();
   	HttpPost httppost = new HttpPost(url);
   	try {
   	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
   	    nameValuePairs.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
   	    nameValuePairs.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
   	    nameValuePairs.add(new BasicNameValuePair("docHospitalSaveList", docHospitalTypeId));
   	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
   	    // Execute HTTP Post Request
   	    HttpResponse response = httpclient.execute(httppost);
   	    HttpEntity httpEntity = response.getEntity();
   	    InputStream is = httpEntity.getContent();

   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

   	    int nRead;
   	    byte[] bytess = new byte[16384];

   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
   		buffer.write(bytess, 0, nRead);
   	    }
   	    buffer.flush();
   	    result = buffer.toString();

   	} catch (ClientProtocolException e) {
   	} catch (IOException e) {
   	}
   	try {
   	    StringBuilder sb = new StringBuilder();
   	    sb.append(result);
   	    json = sb.toString();
   	} catch (Exception e) {
   	}
   	// return JSON String
   	return json;
       }
    public String getGlobalSearchList(String searchKey,String offset,String latitude, String longitude) {
   	try {
   	    String url = AppConstants.BASE_URL + "api/ListUserGlobalSearch";
   	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
   	    postparams.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
   	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
   	    postparams.add(new BasicNameValuePair("searchkeyword", searchKey));
   	    postparams.add(new BasicNameValuePair("offset", offset));
   	    postparams.add(new BasicNameValuePair("lat", latitude));
   	    postparams.add(new BasicNameValuePair("lon", longitude));
   	    postparams.add(new BasicNameValuePair("city", ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, "")));

   	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
   	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

   	    url += "?" + paramString;
   	    HttpGet httpGet = new HttpGet(url);

   	    HttpResponse httpResponse = httpClient1.execute(httpGet);
   	    HttpEntity httpEntity = httpResponse.getEntity();
   	    InputStream is = httpEntity.getContent();

   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

   	    int nRead;
   	    byte[] bytess = new byte[16384];

   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
   		buffer.write(bytess, 0, nRead);
   	    }

   	    buffer.flush();

   	    result = buffer.toString();

   	} catch (UnsupportedEncodingException e) {
   	    e.printStackTrace();
   	} catch (ClientProtocolException e) {
   	    e.printStackTrace();
   	} catch (IOException e) {
   	    e.printStackTrace();
   	}
   	try {
   	    StringBuilder sb = new StringBuilder();
   	    sb.append(result);
   	    json = sb.toString();
   	} catch (Exception e) {
   	}

   	// return JSON String
   	return json;
       }
    public String getGlobalHospitalSearchList(String searchKey,String offset,String latitude, String longitude) {
       	try {
       	    String url = AppConstants.BASE_URL + "api/ListHospitalGlobalSearch";
       	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
       	    postparams.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
       	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
       	    postparams.add(new BasicNameValuePair("searchkeyword", searchKey));
       	    postparams.add(new BasicNameValuePair("offset", offset));
       	    postparams.add(new BasicNameValuePair("lat", latitude));
       	    postparams.add(new BasicNameValuePair("lon", longitude));
       	    postparams.add(new BasicNameValuePair("city", ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, "")));

       	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
       	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

       	    url += "?" + paramString;
       	    HttpGet httpGet = new HttpGet(url);

       	    HttpResponse httpResponse = httpClient1.execute(httpGet);
       	    HttpEntity httpEntity = httpResponse.getEntity();
       	    InputStream is = httpEntity.getContent();

       	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

       	    int nRead;
       	    byte[] bytess = new byte[16384];

       	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
       		buffer.write(bytess, 0, nRead);
       	    }

       	    buffer.flush();

       	    result = buffer.toString();

       	} catch (UnsupportedEncodingException e) {
       	    e.printStackTrace();
       	} catch (ClientProtocolException e) {
       	    e.printStackTrace();
       	} catch (IOException e) {
       	    e.printStackTrace();
       	}
       	try {
       	    StringBuilder sb = new StringBuilder();
       	    sb.append(result);
       	    json = sb.toString();
       	} catch (Exception e) {
       	}

       	// return JSON String
       	return json;
           }
    
    public String getGlobalDoctorSearchList(String searchKey,String offset,String latitude, String longitude,String specialization) {
       	try {
       	    String url = AppConstants.BASE_URL + "api/ListDoctorGlobalSearch";
       	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
       	    postparams.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
       	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
       	    postparams.add(new BasicNameValuePair("searchkeyword", searchKey));
       	    postparams.add(new BasicNameValuePair("offset", offset));
       	    postparams.add(new BasicNameValuePair("lat", latitude));
       	    postparams.add(new BasicNameValuePair("lon", longitude));
       	    postparams.add(new BasicNameValuePair("city", ApplicationSettings.getPref(AppConstants.CURRENT_USER_LOCATION, "")));
       	    postparams.add(new BasicNameValuePair("specialization", specialization));
       	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
       	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

       	    url += "?" + paramString;
       	    HttpGet httpGet = new HttpGet(url);

       	    HttpResponse httpResponse = httpClient1.execute(httpGet);
       	    HttpEntity httpEntity = httpResponse.getEntity();
       	    InputStream is = httpEntity.getContent();

       	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

       	    int nRead;
       	    byte[] bytess = new byte[16384];

       	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
       		buffer.write(bytess, 0, nRead);
       	    }

       	    buffer.flush();

       	    result = buffer.toString();

       	} catch (UnsupportedEncodingException e) {
       	    e.printStackTrace();
       	} catch (ClientProtocolException e) {
       	    e.printStackTrace();
       	} catch (IOException e) {
       	    e.printStackTrace();
       	}
       	try {
       	    StringBuilder sb = new StringBuilder();
       	    sb.append(result);
       	    json = sb.toString();
       	} catch (Exception e) {
       	}

       	// return JSON String
       	return json;
           }
    
    public String postSuggetedDocHospital(String usrEmail,String doctorName,String hospitalName,String cityId,String specialist,String jsonObject,String deviceId) throws JSONException {
	
	try {
	    String urlPath = AppConstants.BASE_URL + "api/AddUserSuggestions";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
	    postparams.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
	    postparams.add(new BasicNameValuePair("doctorname", doctorName));
	    postparams.add(new BasicNameValuePair("hospitalname", hospitalName));
	    postparams.add(new BasicNameValuePair("cityid", cityId));
	    postparams.add(new BasicNameValuePair("speciality", specialist));
	    postparams.add(new BasicNameValuePair("emailid", usrEmail));
	    postparams.add(new BasicNameValuePair("deviceId", deviceId));
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    urlPath += "?" + paramString;
	    
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(urlPath);

	    StringEntity entity = new StringEntity(jsonObject);

	    entity.setContentType("application/json");
	    httpPost.setEntity(entity);

	    HttpResponse responsePOST = httpClient.execute(httpPost);

	    HttpEntity httpEntity = responsePOST.getEntity();
	    if (httpEntity != null) {
		result = EntityUtils.toString(httpEntity);
	    }

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	try {
	    jObj = new JSONObject(json);

	} catch (JSONException e) {
	}
	// return JSON Object
	return json;
	
    }
    //For Performance Testing
    public String postTimeSpend(String message,String screenName,String serverApiName,String preServerTime,
	    String serverTotalTime,String postServerTime,String totalScreenTime,String connectivityType,
	    String server_processtime,String uniquePerfCode) {
   	String url = AppConstants.BASE_URL + "api/PerformanceTestData";
   	HttpClient httpclient = new DefaultHttpClient();
   	HttpPost httppost = new HttpPost(url);
   	try {
   	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
   	    nameValuePairs.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
   	    nameValuePairs.add(new BasicNameValuePair("useCase", message));
   	    nameValuePairs.add(new BasicNameValuePair("activityName", screenName));
   	    nameValuePairs.add(new BasicNameValuePair("preServerCallTime", preServerTime));
   	    nameValuePairs.add(new BasicNameValuePair("serverAPIName", serverApiName));
   	    nameValuePairs.add(new BasicNameValuePair("serverLatency", serverTotalTime));
   	    nameValuePairs.add(new BasicNameValuePair("postServerCallTime", postServerTime));
   	    nameValuePairs.add(new BasicNameValuePair("totalTime", totalScreenTime));
   	    nameValuePairs.add(new BasicNameValuePair("deviceModel", Utils.getDeviceManufacture()+"-"+Utils.getDeviceModel()));
   	    nameValuePairs.add(new BasicNameValuePair("connectivityType", connectivityType));
   	    nameValuePairs.add(new BasicNameValuePair("server_processtime", server_processtime));
   	    nameValuePairs.add(new BasicNameValuePair("uniquePerfCode", uniquePerfCode));
   	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
   	    // Execute HTTP Post Request
   	    HttpResponse response = httpclient.execute(httppost);
   	    HttpEntity httpEntity = response.getEntity();
   	    InputStream is = httpEntity.getContent();

   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

   	    int nRead;
   	    byte[] bytess = new byte[16384];

   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
   		buffer.write(bytess, 0, nRead);
   	    }
   	    buffer.flush();
   	    result = buffer.toString();

   	} catch (ClientProtocolException e) {
   	} catch (IOException e) {
   	}
   	try {
   	    StringBuilder sb = new StringBuilder();
   	    sb.append(result);
   	    json = sb.toString();
   	} catch (Exception e) {
   	}
   	// return JSON String
   	return json;
       }
    public String getReviewMenuList() {
	try {
	    String url = AppConstants.BASE_URL + "api/ListUserReviews";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));

	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	// return JSON String
	return json;
    }
    public String getMedicalRecordsList() {
   	try {
   	    String url = AppConstants.BASE_URL + "api/ListUserMedicalRecords";
   	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
   	    postparams.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
   	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));

   	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
   	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

   	    url += "?" + paramString;
   	    HttpGet httpGet = new HttpGet(url);

   	    HttpResponse httpResponse = httpClient1.execute(httpGet);
   	    HttpEntity httpEntity = httpResponse.getEntity();
   	    InputStream is = httpEntity.getContent();

   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

   	    int nRead;
   	    byte[] bytess = new byte[16384];

   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
   		buffer.write(bytess, 0, nRead);
   	    }

   	    buffer.flush();

   	    result = buffer.toString();

   	} catch (UnsupportedEncodingException e) {
   	    e.printStackTrace();
   	} catch (ClientProtocolException e) {
   	    e.printStackTrace();
   	} catch (IOException e) {
   	    e.printStackTrace();
   	}
   	try {
   	    StringBuilder sb = new StringBuilder();
   	    sb.append(result);
   	    json = sb.toString();
   	} catch (Exception e) {
   	}

   	// return JSON String
   	return json;
       }

    public String postAddMedicalRecordDetails(String jsonObject) throws JSONException {

	try {
	    String urlPath = AppConstants.BASE_URL + "api/AddMedicalRecord";
	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    urlPath += "?" + paramString;

	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(urlPath);

	    StringEntity entity = new StringEntity(jsonObject);

	    entity.setContentType("application/json");
	    httpPost.setEntity(entity);

	    HttpResponse responsePOST = httpClient.execute(httpPost);

	    HttpEntity httpEntity = responsePOST.getEntity();
	    if (httpEntity != null) {
		result = EntityUtils.toString(httpEntity);
	    }

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}

	try {
	    jObj = new JSONObject(json);

	} catch (JSONException e) {
	}
	// return JSON Object
	return json;
    }

    public String getSearchCityList(String searhCityKeyword) {

	try {
	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
	    String url = AppConstants.BASE_URL + "api/SearchCity";

	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
	    postparams.add(new BasicNameValuePair("city", searhCityKeyword));
	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

	    url += "?" + paramString;
	    HttpGet httpGet = new HttpGet(url);

	    HttpResponse httpResponse = httpClient1.execute(httpGet);
	    HttpEntity httpEntity = httpResponse.getEntity();
	    InputStream is = httpEntity.getContent();

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	    int nRead;
	    byte[] bytess = new byte[16384];

	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		buffer.write(bytess, 0, nRead);
	    }

	    buffer.flush();

	    result = buffer.toString();

	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(result);
	    json = sb.toString();
	} catch (Exception e) {
	}
	return json;
    }
    
    public String getCityListValidation() {

   	try {
   	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
   	    String url = AppConstants.BASE_URL + "api/ListAppCities";
   	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
   	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
   	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

   	    url += "?" + paramString;
   	    HttpGet httpGet = new HttpGet(url);
   	    

   	    HttpResponse httpResponse = httpClient1.execute(httpGet);
   	    HttpEntity httpEntity = httpResponse.getEntity();
   	    InputStream is = httpEntity.getContent();

   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

   	    int nRead;
   	    byte[] bytess = new byte[16384];

   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
   		buffer.write(bytess, 0, nRead);
   	    }

   	    buffer.flush();

   	    result = buffer.toString();

   	} catch (UnsupportedEncodingException e) {
   	    e.printStackTrace();
   	} catch (ClientProtocolException e) {
   	    e.printStackTrace();
   	} catch (IOException e) {
   	    e.printStackTrace();
   	}
   	try {
   	    StringBuilder sb = new StringBuilder();
   	    sb.append(result);
   	    json = sb.toString();
   	} catch (Exception e) {
   	}
   	return json;
       }
    public String getCityAfterValidation(String detectedCity) {

       	try {
       	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
       	    String url = AppConstants.BASE_URL + "api/GetActualCity";
       	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
       	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
       	    postparams.add(new BasicNameValuePair("detectedCity", detectedCity));
       	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

       	    url += "?" + paramString;
       	    HttpGet httpGet = new HttpGet(url);
       	    

       	    HttpResponse httpResponse = httpClient1.execute(httpGet);
       	    HttpEntity httpEntity = httpResponse.getEntity();
       	    InputStream is = httpEntity.getContent();

       	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

       	    int nRead;
       	    byte[] bytess = new byte[16384];

       	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
       		buffer.write(bytess, 0, nRead);
       	    }

       	    buffer.flush();

       	    result = buffer.toString();

       	} catch (UnsupportedEncodingException e) {
       	    e.printStackTrace();
       	} catch (ClientProtocolException e) {
       	    e.printStackTrace();
       	} catch (IOException e) {
       	    e.printStackTrace();
       	}
       	try {
       	    StringBuilder sb = new StringBuilder();
       	    sb.append(result);
       	    json = sb.toString();
       	} catch (Exception e) {
       	}
       	return json;
    }
    public String deleteMedicalRecordsList(String recordId) {
   	try {
   	    String url = AppConstants.BASE_URL + "api/DeleteMedicalRecord";
   	    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
   	    postparams.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
   	    postparams.add(new BasicNameValuePair("record_id", recordId));
   	    postparams.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));

   	    DefaultHttpClient httpClient1 = new DefaultHttpClient();
   	    String paramString = URLEncodedUtils.format(postparams, "utf-8");

   	    url += "?" + paramString;
   	    HttpGet httpGet = new HttpGet(url);

   	    HttpResponse httpResponse = httpClient1.execute(httpGet);
   	    HttpEntity httpEntity = httpResponse.getEntity();
   	    InputStream is = httpEntity.getContent();

   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

   	    int nRead;
   	    byte[] bytess = new byte[16384];

   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
   		buffer.write(bytess, 0, nRead);
   	    }

   	    buffer.flush();

   	    result = buffer.toString();

   	} catch (UnsupportedEncodingException e) {
   	    e.printStackTrace();
   	} catch (ClientProtocolException e) {
   	    e.printStackTrace();
   	} catch (IOException e) {
   	    e.printStackTrace();
   	}
   	try {
   	    StringBuilder sb = new StringBuilder();
   	    sb.append(result);
   	    json = sb.toString();
   	} catch (Exception e) {
   	}

   	// return JSON String
   	return json;
       }
    
    public String getDistance(String latitude,String longitude) {
		try {
			String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=12.9259,77.6229&destinations=12.9700,77.7500&mode=driving&language=en-EN&sensor=false";
			DefaultHttpClient httpClient = new DefaultHttpClient();
			 HttpGet httpGet = new HttpGet(url);

		   	    HttpResponse httpResponse = httpClient.execute(httpGet);
		   	    HttpEntity httpEntity = httpResponse.getEntity();
		   	    InputStream is = httpEntity.getContent();

		   	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		   	    int nRead;
		   	    byte[] bytess = new byte[16384];

		   	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
		   		buffer.write(bytess, 0, nRead);
		   	    }

		   	    buffer.flush();

		   	    result = buffer.toString();

		   	} catch (UnsupportedEncodingException e) {
		   	    e.printStackTrace();
		   	} catch (ClientProtocolException e) {
		   	    e.printStackTrace();
		   	} catch (IOException e) {
		   	    e.printStackTrace();
		   	}
		   	try {
		   	    StringBuilder sb = new StringBuilder();
		   	    sb.append(result);
		   	    json = sb.toString();
		   	} catch (Exception e) {
		   	}

		   	// return JSON String
		   	return json;

	}
    
    public String callDoctor(String type, String docHospitalId,String deviceId) {
    	String url = AppConstants.BASE_URL + "api/CallDoctor";
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost(url);
    	try {
    	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    	    nameValuePairs.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
    	   // nameValuePairs.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
    	    nameValuePairs.add(new BasicNameValuePair("deviceId", deviceId));
    	    nameValuePairs.add(new BasicNameValuePair("doctorId", docHospitalId));
    	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	    // Execute HTTP Post Request
    	    HttpResponse response = httpclient.execute(httppost);
    	    HttpEntity httpEntity = response.getEntity();
    	    InputStream is = httpEntity.getContent();

    	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    	    int nRead;
    	    byte[] bytess = new byte[16384];

    	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
    		buffer.write(bytess, 0, nRead);
    	    }
    	    buffer.flush();
    	    result = buffer.toString();

    	} catch (ClientProtocolException e) {
    	} catch (IOException e) {
    	}
    	try {
    	    StringBuilder sb = new StringBuilder();
    	    sb.append(result);
    	    json = sb.toString();
    	} catch (Exception e) {
    	}
    	// return JSON String
    	return json;
        }
    
    public String callHospital(String type, String docHospitalId,String deviceId) {
    	String url = AppConstants.BASE_URL + "api/CallHospital";
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost(url);
    	try {
    	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    	    nameValuePairs.add(new BasicNameValuePair("token", ApplicationSettings.getPref(AppConstants.TOKEN, "")));
    	   // nameValuePairs.add(new BasicNameValuePair("userid", ApplicationSettings.getPref(AppConstants.USER_ID, "")));
    	    nameValuePairs.add(new BasicNameValuePair("deviceId", deviceId));
    	    nameValuePairs.add(new BasicNameValuePair("hospitalId", docHospitalId));
    	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	    // Execute HTTP Post Request
    	    HttpResponse response = httpclient.execute(httppost);
    	    HttpEntity httpEntity = response.getEntity();
    	    InputStream is = httpEntity.getContent();

    	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    	    int nRead;
    	    byte[] bytess = new byte[16384];

    	    while ((nRead = is.read(bytess, 0, bytess.length)) != -1) {
    		buffer.write(bytess, 0, nRead);
    	    }
    	    buffer.flush();
    	    result = buffer.toString();

    	} catch (ClientProtocolException e) {
    	} catch (IOException e) {
    	}
    	try {
    	    StringBuilder sb = new StringBuilder();
    	    sb.append(result);
    	    json = sb.toString();
    	} catch (Exception e) {
    	}
    	// return JSON String
    	return json;
        }
    
}
