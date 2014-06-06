package com.github.ucscclasssearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.ucscclasssearch.R;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView; 

public class BookmarkActivity extends ActionBarActivity {
	
	private static final String LOG_TAG = MainActivity.LOG_TAG;
	
	private ListView listViewSaves; //the list view 
	private List<Course> listData;
	private CourseAdapter listAdapter; 
	private View footerView; 
	
	private boolean flag_items_loading; 
	private boolean last_saves_page; 
	private final int SAVES_PER_PAGE = 25; 
	
	@SuppressWarnings("unchecked")

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		
		flag_items_loading = true; 
		last_saves_page = false; 
		listViewSaves = (ListView) findViewById(R.id.listViewSaves);
		listData = new ArrayList<Course>(); 
		listViewSaves.setAdapter(listAdapter);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable up navigation
		
		listAdapter = new CourseAdapter(this, (List<? extends Map<String, ?>>) listData,
				R.layout.search_result_listview_item, 
				new String[] {"First Line", "Second Line" }, 
				new int[] {android.R.id.text1, android.R.id.text2 },
				false);
		
		listViewSaves.setAdapter(listAdapter);
		
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
		    }
		return super.onOptionsItemSelected(item);
	}

}
