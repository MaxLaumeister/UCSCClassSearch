package com.cmps121.ucsccoursebrowser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.cmps121.ucsccoursebrowser.SearchParameter.FieldType;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	protected static final String resultsPagePath = "index.php";
	
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
				final HashMap<String, String> listItem = ((HashMap<String, String>) adapter.getItemAtPosition(position));
				final String title = listItem.get("First Line");
				final SearchParameter param = PisaHTMLModel.SEARCH_PARAMETERS.get(title);
				
				if (param.type == FieldType.MULT_CHOICE) {
					
					// Type juggling
					HashMap<String, String> optionsList = param.options;
					final String[] optionsTitlesArray = new String[optionsList.size()];
					int i = 0;
					for (Map.Entry<String, String> entry : optionsList.entrySet()) {
						optionsTitlesArray[i] = entry.getKey();
						i++;
					}
					
					new AlertDialog.Builder(v.getContext()).setTitle(title)
					.setTitle(title)
					.setItems(optionsTitlesArray, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							listItem.put("Second Line", optionsTitlesArray[whichButton]);
							listAdapter.notifyDataSetChanged();
						}
					})
					.show();
				} else if (param.type == FieldType.TEXT_ENTRY) {
					// TODO: Show a text entry dialog
				}
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
				HTMLParser.parseSearchPage(result); // TODO: Parse this in the Async thread instead of the UI thread
				HTTPProgress.dismiss();
				listViewSearch.setVisibility(View.VISIBLE);
				// TODO: Populate ListView using the newly updated "options" field of the search parameters
				
				assert(listData.size() == PisaHTMLModel.SEARCH_PARAMETERS.size());
				int i = 0;
				for (Map.Entry<String, SearchParameter> entry : PisaHTMLModel.SEARCH_PARAMETERS.entrySet()) {
					SearchParameter param = entry.getValue();
					Map<String, String> listItem = listData.get(i);
					
					// The text in the ListView should match the key in the HashMap,
					// since that's what the HashMap is keyed on.
					assert(listItem.get("First Line") == entry.getKey());
					
					String defaultOption;
					if (param.type == FieldType.MULT_CHOICE) {
						// Multiple choice search parameters should show their default option in the ListView.
						// TODO: Mark the default option instead of assuming it's at position 0 in the options LinkedHashMap
						String HTMLString = param.options.entrySet().iterator().next().getKey(); // Position 0 in the LinkedHashMap
						defaultOption = Html.fromHtml(HTMLString).toString(); // Properly display any HTML entities
					} else {
						// Text entry search parameters should not show anything by default in the ListView.
						defaultOption = "";
					}
					listItem.put("Second Line", defaultOption); // Replace line in ListView data
					i++;
				}
				listAdapter.notifyDataSetChanged();
			}
		}).execute(new HttpPost(baseURL));
	}
	
	private void initListViewSearch() {
		final int rows = PisaHTMLModel.SEARCH_PARAMETERS.size();
		listData.clear();
		listData.ensureCapacity(rows);
		
		for (Map.Entry<String, SearchParameter> entry : PisaHTMLModel.SEARCH_PARAMETERS.entrySet()) {
			String parameter_label = entry.getKey();
			Map<String, String> el = new HashMap<String, String>();
			el.put("First Line", parameter_label);
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
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(listData.size());
		
		// Get name-value pairs for the post request
		
		nameValuePairs.add(new BasicNameValuePair("action", "results"));
		for (Map<String, String> listItem : listData) {
			String search_parameter_title = listItem.get("First Line");
			String selected_option_title = listItem.get("Second Line");
			
			SearchParameter param = PisaHTMLModel.SEARCH_PARAMETERS.get(search_parameter_title);
			String select_name = param.html_name;
			if (param.type == FieldType.MULT_CHOICE) {
				String selected_option_value = param.options.get(selected_option_title);
				nameValuePairs.add(new BasicNameValuePair(select_name, selected_option_value));
			} else if (param.type == FieldType.TEXT_ENTRY) {
				nameValuePairs.add(new BasicNameValuePair(select_name, selected_option_title));
			}
		}
		
		// Construct the HTTP Post request
		
		String postURL = baseURL + resultsPagePath;
		HttpPost post = new HttpPost(postURL);
		post.setHeader("Content-type", "application/x-www-form-urlencoded");
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		// Send the HTTP Post request
		
		(new HTMLGetter(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				// TODO: Parse this in the Async thread instead of the UI thread
				List<Course> resultsList = HTMLParser.parseResultsPage(result);
				Log.d(LOG_TAG, resultsList.toString());
			}
		}).execute(post);
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
