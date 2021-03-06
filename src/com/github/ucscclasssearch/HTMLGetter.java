package com.github.ucscclasssearch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class HTMLGetter extends AsyncTask<HttpUriRequest, Void, String>{
	
	private static final String LOG_TAG = MainActivity.LOG_TAG;
	
	private static DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
	
	protected Context ctx;
	
	public HTMLGetter(Context context) {
		ctx = context;
	}
	
	// doInBackground(HttpUriRequest request):
	// Returns the String representing the HTML retrieved in response to the request.
	
	@Override
	protected String doInBackground(HttpUriRequest... httppostArr) {
		assert(httppostArr.length == 1);
		HttpUriRequest httppost = httppostArr[0];
		httppost.setHeader("Content-type", "application/x-www-form-urlencoded");
		
		Log.d(LOG_TAG, "HTTP REQUEST");

		InputStream inputStream = null;
		String result = null;
		try {
		    HttpResponse response;
		    if (httppost instanceof HttpPost) {
		    	response = httpclient.execute(httppost);
		    } else {
		    	response = (new DefaultHttpClient(new BasicHttpParams())).execute(httppost);
		    }
		    HttpEntity entity = response.getEntity();

		    inputStream = entity.getContent();
		    // json is UTF-8 by default
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    while ((line = reader.readLine()) != null)
		    {
		        sb.append(line + "\n");
		    }
		    result = sb.toString();
		    return result;
		} catch (Exception e) { 
		    Log.e("HTTP Error", "exception", e);
		}
		finally {
		    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
		}
		return "";
	}
}
