package com.github.ucscclasssearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

public class MainActivity extends ActionBarActivity {

	public static final String LOG_TAG = "com.github.ucscclasssearch";
	//public List<Course> courses = new ArrayList<Course>() ;
	
	private ListView listViewSearch; // The ListView containing the search parameters
	private ArrayList<Map<String, String>> listData = new ArrayList<Map<String, String>>(); // The underlying list for the above ListView
	private SimpleAdapter listAdapter; // The adapter that links listData to ListViewSearch

	
	
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
					
					// Show single choice dialog
					
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
					
					// Show text entry dialog
					
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle(title);

					// Set up the input
					final EditText input = new EditText(MainActivity.this);
					input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
					input.setText(listItem.get("Second Line"));
					builder.setView(input);

					// Set up the buttons
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
						@Override
						public void onClick(DialogInterface dialog, int which) {
							listItem.put("Second Line", input.getText().toString());
							listAdapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					
					AlertDialog dialog = builder.create();
					// Show keyboard
			        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
					dialog.show();
				}
			}
		});
		listViewSearch.setItemsCanFocus(true);
		
		loadSearchParameters();
	}
	
	private void loadSearchParameters() {
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
				
				// Because the list view data (initialized in initListViewSearch()) should be the 
				// same size and in the same order as the LinkedHashMap of search parameters,
				// we can walk up both of them in tandem to update the list view, instead of doing
				// a bunch of key queries on the hash map.
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
		}).execute(new HttpPost(PisaHTMLModel.baseURL));
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
		Intent intent = new Intent(this, ResultsActivity.class);
		intent.putExtra("com.github.ucscclasssearch.listData", listData);
		startActivity(intent);
	}

	//public void onClickBookmarkButton(View v) {
		//Intent intent = new Intent(this, BookmarkActivity.class);
		//intent.putExtra("com.github.ucscclasssearch.listData", listData);
		//startActivity(intent);
	//}
	
	
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
		if (id == R.id.action_refresh) {
			loadSearchParameters();
			return true;
		}else if(id == R.id.action_bookmark_list){
			Intent intent = new Intent(this, BookmarkActivity.class);
			intent.putExtra("com.github.ucscclasssearch.listData", listData);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
