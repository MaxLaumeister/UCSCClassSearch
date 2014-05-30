package com.cmps121.ucsccoursebrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmps121.ucsccoursebrowser.SearchParameter.FieldType;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
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
		
		// Initialize the ListView, set its onItemClick to bring up a dialog for user data entry
		
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
		
		// Block the screen with a ProgressDialog, which gets dismissed
		// after the HTML request for the class search page returns.
		
		// TODO: Make it cancellable but with a retry button behind it,
		// so if user loses connection it's possible for them to restart
		// the HTML request.
		
		final ProgressDialog HTTPProgress = ProgressDialog.show(MainActivity.this, "Loading Course Data ...", "Please wait ...", true, false);
		listViewSearch.setVisibility(View.INVISIBLE);
		
		(new HTMLGetter(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				HTMLParser.parseSearchPage(result);
				Log.d(LOG_TAG, PisaHTMLModel.SEARCH_PARAMETERS.toString());
				HTTPProgress.dismiss();
				listViewSearch.setVisibility(View.VISIBLE);
				// TODO: Populate ListView using the newly updated "options" field of the search parameters
				
				assert(listData.size() == PisaHTMLModel.SEARCH_PARAMETERS.size());
				for (int i = 0; i < listData.size(); i++) {
					SearchParameter param = PisaHTMLModel.SEARCH_PARAMETERS.get(i);
					Map<String, String> listItem = listData.get(i);
					
					String defaultOption;
					if (param.type == FieldType.MULT_CHOICE) {
						// Multiple choice search parameters should show their default option in the ListView.
						// TODO: Mark the default option instead of assuming it's at position 0 in the options list
						String HTMLString = PisaHTMLModel.SEARCH_PARAMETERS.get(i).options.get(0);
						defaultOption = Html.fromHtml(HTMLString).toString(); // Properly display any HTML entities
					} else {
						// Text entry search parameters should not show anything by default in the ListView.
						defaultOption = "";
					}
					listItem.put("Second Line", defaultOption); // Replace line in ListView data
				}
				listAdapter.notifyDataSetChanged();
			}
		}).execute(baseURL);
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
		// TODO: This should use data from the ListView and from PisaHTMLModel
		// to do an HTML post to the class search page, performing the search.
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
