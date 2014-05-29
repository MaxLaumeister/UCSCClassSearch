package com.cmps121.ucsccoursebrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	public static final String LOG_TAG = "com.cmps121.ucsccoursebrowser";
	protected static final String baseURL = "https://pisa.ucsc.edu/class_search/";
	
	ListView listViewSearch; // The ListView containing the search parameters
	ArrayList<Map<String, String>> listData = new ArrayList<Map<String, String>>(); // The underlying list for the above ListView
	SimpleAdapter listAdapter; // The adapter that links listData to ListViewSearch
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listViewSearch = (ListView) findViewById(R.id.listViewSearch);
		initListViewSearch();
		listViewSearch.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
				@SuppressWarnings("unchecked")
				String value = ((HashMap<String, String>) adapter.getItemAtPosition(position)).get("First Line");
				new AlertDialog.Builder(v.getContext()).setTitle(value).show();
			}
		});
		listViewSearch.setItemsCanFocus(true);
	}
	
	private void initListViewSearch() {
		final int rows = PisaHTMLModel.SEARCH_PARAMETERS.size();
		listData.clear();
		listData.ensureCapacity(rows);
		
		// List Data (this gets shoved into the listview at the end of onCreate())
		// None of these are actually "Loading" yet until I get the netcode in. ~Max
		
		for (SearchParameter parameter : PisaHTMLModel.SEARCH_PARAMETERS) {
			Map<String, String> el = new HashMap<String, String>();
			el.put("First Line", parameter.label);
			el.put("Second Line", "Loading...");
			listData.add(el);
		}
		
		listAdapter = new SimpleAdapter(this, listData,
				android.R.layout.simple_list_item_2, 
				new String[] {"First Line", "Second Line" }, 
				new int[] {android.R.id.text1, android.R.id.text2 });
		listViewSearch.setAdapter(listAdapter);
	}
	
	public void onClickSearchButton(View v) {
		// TODO: After finishing the HTML parser, move this HTML get call into OnCreate
		// so that it populates the ListView when the app is loaded, instead of when
		// the search button is pressed.
		(new HTMLGetter(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				Toast.makeText(ctx, "Done", Toast.LENGTH_SHORT).show();
				HashMap<String, List<String>> parameterOptions = HTMLParser.parseSearchPage(result);
				Log.d(LOG_TAG, parameterOptions.toString());
				// TODO: Populate ListView using the contents of parameterOptions
				// (see HTMLParser.parseSearchPage() for an idea of how these contents are formatted)
			}
		}).execute(baseURL);
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
