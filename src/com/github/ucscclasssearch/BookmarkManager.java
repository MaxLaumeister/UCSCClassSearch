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

	//having a lot of trouble with this line. 
	static SharedPreferences mPrefs = getApplication.Context().getSharedPreferences(Context.MODE_PRIVATE);
	
	public List<Course> courses = new ArrayList<Course>() ;

	
   public static  void addBookmark(Course course){
	   //the function to actually add the bookmarks to the ArrayList. 
	   List<Course> courses = getBookmarks();
	   courses.add(course);
	   setBookmarks(courses);
	   
   }
	
	public static void setBookmarks(List<Course> courses){
		//this is the function to load the bookmarks to the list
		Editor prefsEditor = mPrefs.edit();
		Gson gson = new Gson();
		String json = gson.toJson(courses);
		//prefsEditor.putString(courses.course.detail_url,json);
		prefsEditor.commit(); 
		
		
	}
	
	public static List<Course> getBookmarks(){
		//function to fetch the bookmarks.  
		Gson gson = new Gson();
		String json = mPrefs.getString("bookmarks","");
		List<Course> bookmarks = new ArrayList<Course>();
		//bookmarks = gson.fromJson(json, List<Course>);
		
		return bookmarks; 
		
	}
	
	
	
	
	
	
}
