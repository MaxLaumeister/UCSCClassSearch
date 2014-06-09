package com.github.ucscclasssearch;

import java.util.ArrayList;
import java.util.List; 

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;



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
		List<Course> bookmarks = gson.fromJson(json, new TypeToken<List<Course>>(){}.getType());
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
