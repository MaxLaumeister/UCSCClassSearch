package com.github.ucscclasssearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List; 

import org.apache.http.client.methods.HttpPost;

import com.github.ucscclasssearch.R;
import com.github.ucscclasssearch.SearchParameter.FieldType;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context; 
import android.app.Application;



public class BookmarkManager {
	
	// The string that we will use as a key to save/load the course list from shared preferences
	private static final String PREFS_ID = "BookmarksList";
	

	// Saves the list of courses to shared preferences (overwrites existing list)
	public static void setBookmarks(List<Course> courses, Context ctx){
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		Editor prefsEditor = mPrefs.edit();
		Gson gson = new Gson();
		String json = gson.toJson(courses);
		prefsEditor.putString(PREFS_ID, json);
		prefsEditor.commit(); 
	}
	
	// Fetches the list of courses from shared preferences
	public static List<Course> getBookmarks(Context ctx){
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		Gson gson = new Gson();
		String json = mPrefs.getString(PREFS_ID, "");
		@SuppressWarnings("unchecked")
		List<Course> bookmarks = gson.fromJson(json, List.class);
		return bookmarks; 
	}
	
	// Saves a single course to the bookmarks list in shared preferences
	public static  void addBookmark(Course course, Context ctx){
		List<Course> courses = getBookmarks(ctx);
		if (courses == null) courses = new ArrayList<Course>();
		courses.add(course);
		setBookmarks(courses, ctx);
	}
	
}
