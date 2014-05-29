package com.cmps121.ucsccoursebrowser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class HTMLGetter extends AsyncTask<String, Void, String>{
	
	private static final String LOG_TAG = MainActivity.LOG_TAG;
	
	protected Context ctx;
	
	public HTMLGetter(Context context) {
		ctx = context;
	}
	
	// This takes one URL, and returns the String representing the HTML retrieved.
	@Override
	protected String doInBackground(String... urls) {
		if (urls.length != 1) return ""; // TODO: Make this an exception
		Log.i(LOG_TAG, "Getting HTML");
		DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpPost httppost = new HttpPost(urls[0]);
		// Depends on your web service
		httppost.setHeader("Content-type", "application/json");

		InputStream inputStream = null;
		String result = null;
		try {
		    HttpResponse response = httpclient.execute(httppost);           
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
