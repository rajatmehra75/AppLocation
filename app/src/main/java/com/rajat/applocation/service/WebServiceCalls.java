package com.rajat.applocation.service;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.List;

public class WebServiceCalls {
	
	private static WebServiceCalls webServiceCalls;
	private static final String TAG = WebServiceCalls.class.getSimpleName();
	public static String csrfToken;
	private static Context mContext;
	public static DefaultHttpClient mHttpclient;
	
	private static final int CONNECTION_TIME_OUT = 15 * 1000;
	private static final int SOCKET_TIME_OUT_AVG = 1 * 60 * 1000;
	
	private static char[] chunkBuffer = new char[1024];
	
	static String Exception = "There is some problem in fetching data";
	static String REQUEST_TIMEOUT = "Request Timed out, Please try again";
	static String SERVER_DOWN = "Server is Down, Please try later";
	static String UPLOAD_Exception = "There is some problem on uploading data";
	
	
	public static WebServiceCalls getInstance(Context context){
		mContext = context;
		if(webServiceCalls==null){
			webServiceCalls = new WebServiceCalls();
		}
		return webServiceCalls;
	}
	
	private WebServiceCalls(){
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIME_OUT);
		mHttpclient = new DefaultHttpClient(httpParameters);
	}
	
	public String sendHttpGetRequest(String url) {
		Log.d(TAG, "requested url is "+ url);
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("crsfParam", csrfToken);
//			httpGet.setHeader("Referer", refererValue);
			mHttpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIME_OUT_AVG);
			HttpResponse response = mHttpclient.execute(httpGet);
			Log.d("Request Handler", "response code="+response.getStatusLine().getStatusCode());
			manageCookies();
			HttpEntity entity = response.getEntity();
			InputStreamReader isr = new InputStreamReader(entity.getContent());
			String msg = readData(isr);
			isr.close();
			entity.consumeContent();

			Log.d("Request Handler", "response="+msg);
			return msg;

		} catch (ClientProtocolException e) {
			Log.e("log_tag", e.toString());
			e.printStackTrace();
				return Exception;
		} catch (ConnectException e) {
			e.printStackTrace();
				return Exception;
		} catch (Exception e) {
			e.printStackTrace();

				return Exception;
		} finally {
			mHttpclient.getConnectionManager().closeExpiredConnections();
		}
	}
	
	synchronized public String sendHttpPosttRequest(String url, String jsonString) {
		try {
			Log.d(TAG, "requested url is " + url);
			System.out.println("JSON TO SEND====="+jsonString);
			HttpPost HttpPost = new HttpPost( url);
			 HttpPost.setHeader("crsfParam", csrfToken);
//			 HttpPost.setHeader("Referer", refererValue);
			HttpPost.addHeader("Content-Type", "application/json");
			HttpPost.setEntity(new StringEntity(jsonString));
			mHttpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIME_OUT_AVG);
			HttpResponse response = mHttpclient.execute(HttpPost);

			manageCookies();

			int statusCode = response.getStatusLine().getStatusCode();
			Log.d(TAG, "statusCode===" + statusCode);
			System.out.println("status code for site upload for test point===" + statusCode);
			HttpEntity entity = response.getEntity();

			InputStreamReader isr = new InputStreamReader(entity.getContent());
			String msg = readData(isr);
			System.out.println("msg for site upload for test point==="+msg);
			isr.close();
			entity.consumeContent();
			if (HttpStatus.SC_OK == statusCode || HttpStatus.SC_NO_CONTENT == statusCode) {
					return msg;
			}
		} catch (ClientProtocolException e) {
			Log.e("log_tag", e.toString());
			e.printStackTrace();
				return UPLOAD_Exception;
		} catch (ConnectException e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
				return SERVER_DOWN;
		} catch (Exception e) {
			Log.e("log_tag", e.toString());
			e.printStackTrace();
				return REQUEST_TIMEOUT;
		} finally {
			mHttpclient.getConnectionManager().closeExpiredConnections();
		}
		return UPLOAD_Exception;
	}
	
	private void manageCookies() {
		// -------------------cookie management----------------------------
		CookieSyncManager.createInstance(mContext).sync();
		List<Cookie> cookies = mHttpclient.getCookieStore().getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {

				if ("csrfToken".equals(cookie.getName())) {
					csrfToken = cookie.getValue();
				}
				String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
				CookieManager.getInstance().setCookie(cookie.getDomain(), cookieString);
			}
		}
	}
	
	public synchronized static String readData(InputStreamReader rd) {
		try {
			StringBuffer sb = new StringBuffer();
			while (true) {
				int read = rd.read(chunkBuffer, 0, chunkBuffer.length);
				if (read == -1) {
					break;
				}
				sb.append(chunkBuffer, 0, read);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				rd.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

}
