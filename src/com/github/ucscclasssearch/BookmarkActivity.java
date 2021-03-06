package com.github.ucscclasssearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.ucscclasssearch.R;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView; 
import android.widget.AdapterView.OnItemClickListener;

public class BookmarkActivity extends ActionBarActivity {
	
	private ListView listViewSaves; //the list view 
	private List<Course> listData;
	private CourseAdapter listAdapter; 
	
	@SuppressWarnings("unchecked")

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		
		listViewSaves = (ListView) findViewById(R.id.listViewSaves);
		listData = new ArrayList<Course>(); 
		listViewSaves.setAdapter(listAdapter);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable up navigation
		
		listAdapter = new CourseAdapter(this, (List<? extends Map<String, ?>>) listData,
				R.layout.bookmark_listview_item, 
				new String[] {"First Line", "Second Line" }, 
				new int[] {android.R.id.text1, android.R.id.text2 },
				false);
		
		listViewSaves.setAdapter(listAdapter);
		
		listViewSaves.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Course course = listData.get(position);
				Intent intent = new Intent(BookmarkActivity.this, DetailActivity.class);
				intent.putExtra("com.github.ucscclasssearch.detail_url", course.detail_url);
				intent.putExtra("com.github.ucscclasssearch.show_bookmark_button", false);
				startActivity(intent);
			}
			
		});
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		List<Course> bookmarks = BookmarkManager.getBookmarks(getApplicationContext());
		listData.clear();
		if (bookmarks != null) listData.addAll(bookmarks);
		listAdapter.notifyDataSetChanged();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bookmark, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		    	finish();
		        return true;
		    
		    // Clear all bookmarks
		    case R.id.action_bookmark_clear:
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("Delete all bookmarks?");
				adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						Editor prefsEditor = mPrefs.edit();
						prefsEditor.putString("BookmarksList", "");
						prefsEditor.commit();
						listData.clear();
						listAdapter.notifyDataSetChanged();
					}
				});
				adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});
				adb.show();
		}
		
		return super.onOptionsItemSelected(item);
	}

}
