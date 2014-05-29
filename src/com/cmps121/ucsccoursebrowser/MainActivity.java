package com.cmps121.ucsccoursebrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ActionBarActivity {

	ListView listViewSearch; // The ListView containing the search parameters
	ArrayList<Map<String, String>> listData = new ArrayList<Map<String, String>>(); // The underlying list for the above ListView
	SimpleAdapter listAdapter; // The adapter that links listData to ListViewSearch
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listViewSearch = (ListView) findViewById(R.id.listViewSearch);
		initListViewSearch();
	}
	
	private void initListViewSearch() {
		final int rows = 9;
		listData.clear();
		listData.ensureCapacity(rows);
		// Initialize list
		for (int i = 0; i < rows; i++) {
			listData.add(new HashMap<String, String>());
		}
		
		// List Data (this gets shoved into the listview at the end of onCreate())
		// None of these are actually "Loading" yet until I get the netcode in. ~Max
		
		listData.get(0).put("First Line", "Term ");
		listData.get(0).put("Second Line","Loading... ");
		
		listData.get(1).put("First Line", "Status ");
		listData.get(1).put("Second Line","Loading... ");
		
		listData.get(2).put("First Line", "Subject ");
		listData.get(2).put("Second Line","Loading... ");
		
		listData.get(3).put("First Line", "Course Title Keyword ");
		listData.get(3).put("Second Line","Loading... ");
		
		listData.get(4).put("First Line", "Instructor Last Name ");
		listData.get(4).put("Second Line","Loading... ");
		
		listData.get(5).put("First Line", "General Education ");
		listData.get(5).put("Second Line","Loading... ");
		
		listData.get(6).put("First Line", "Course Units ");
		listData.get(6).put("Second Line","Loading... ");
		
		listData.get(7).put("First Line", "Meeting Days ");
		listData.get(7).put("Second Line","Loading... ");
		
		listData.get(8).put("First Line", "Meeting Times ");
		listData.get(8).put("Second Line","Loading... ");
		
		listAdapter = new SimpleAdapter(this, listData,
				android.R.layout.simple_list_item_2, 
				new String[] {"First Line", "Second Line" }, 
				new int[] {android.R.id.text1, android.R.id.text2 });
		listViewSearch.setAdapter(listAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
