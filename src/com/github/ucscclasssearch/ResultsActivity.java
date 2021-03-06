package com.github.ucscclasssearch;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.github.ucscclasssearch.R;
import com.github.ucscclasssearch.SearchParameter.FieldType;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView; 

public class ResultsActivity extends ActionBarActivity {
	
	private ListView listViewResults; // The ListView containing the search results
	private List<Course> listData; // The underlying list for the above ListView
	private CourseAdapter listAdapter; // The adapter that links listData to ListViewResults
	private View footerView; // The loading text and spinner at the bottom of ListViewResults
	
	private boolean flag_items_loading; // True when an HTML post is in-progress
	private boolean last_results_page; // True when there are no more pages of results to load
	private final int RESULTS_PER_PAGE = 25;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
		// Initialize
		
		flag_items_loading = true;
		last_results_page = false;
		listViewResults = (ListView) findViewById(R.id.listViewResults);
		listData = new ArrayList<Course>();
		listViewResults.setAdapter(listAdapter);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable up navigation
		
		listAdapter = new CourseAdapter(this, (List<? extends Map<String, ?>>) listData,
				R.layout.search_result_listview_item, 
				new String[] {"First Line", "Second Line" }, 
				new int[] {android.R.id.text1, android.R.id.text2 },
				true );
		
		listViewResults.setAdapter(listAdapter);
		
		// Send the post request that will populate the ListView
		
		sendSearchPost();
		
		// Add a loading spinner at the bottom of the ListView that
		// signifies that more results are being loaded.
		
		footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.search_result_listview_spinner, null, false);
		listViewResults.addFooterView(footerView);
	}
	
	private void sendSearchPost() {
		
		// Get intent extras data
		
		Intent intent = getIntent();
		@SuppressWarnings("unchecked")
		ArrayList<Map<String, String>> searchQueryList = (ArrayList<Map<String, String>>)
			intent.getSerializableExtra("com.github.ucscclasssearch.listData");
		
		// Create an HTTP post to populate this activity
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(searchQueryList.size());
		
		// Get name-value pairs for the post request
		
		nameValuePairs.add(new BasicNameValuePair("action", "results"));
		nameValuePairs.add(new BasicNameValuePair("binds[:instr_name_op]", "contains"));
		for (Map<String, String> listItem : searchQueryList) {
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
		
		String postURL = PisaHTMLModel.baseURL + PisaHTMLModel.resultsPagePath;
		final HttpPost post = new HttpPost(postURL);
		post.setHeader("Content-type", "application/x-www-form-urlencoded");
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		// Block the screen with a ProgressDialog, which gets dismissed
		// after the HTML request for the class search page returns.
		
		// TODO: Make it cancellable and go back to the search activity when cancelled.
		
		final ProgressDialog HTTPProgress = ProgressDialog.show(ResultsActivity.this,
				"Searching ...", "Please wait ...", true, false);
		listViewResults.setVisibility(View.INVISIBLE);
		
		// Send the HTTP Post request
		
		(new HTMLGetter(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				// TODO: Parse this in the Async thread instead of the UI thread
				List<Course> resultList = HTMLParser.parseResultsPage(result);
				if (resultList.isEmpty()) {
					// No courses found
					TextView noMatches = (TextView) findViewById(R.id.noMatches);
					noMatches.setVisibility(View.VISIBLE);
				} else {
					// Add courses to listview and show it
					listData.addAll(resultList);
					listAdapter.notifyDataSetChanged();
					listViewResults.setVisibility(View.VISIBLE);
				}
				HTTPProgress.dismiss();
				
				if (resultList.size() < RESULTS_PER_PAGE) notifyEndOfResults();
				flag_items_loading = false;
			}
		}).execute(post);
		
		// Set a listener and post request for when the next page of results needs to be loaded
		
		final HttpPost nextPagePost = new HttpPost(postURL);
		List<NameValuePair> nextPageNameValuePairs = new ArrayList<NameValuePair>();
		nextPageNameValuePairs.add(new BasicNameValuePair("action", "next"));
		 // How many more results we want
		nextPageNameValuePairs.add(new BasicNameValuePair("Rec_Dur", Integer.toString(RESULTS_PER_PAGE)));
		
		nextPagePost.setHeader("Origin", "https://pisa.ucsc.edu");
		nextPagePost.setHeader("Referer", "https://pisa.ucsc.edu/class_search/index.php");
		
		try {
			nextPagePost.setEntity(new UrlEncodedFormEntity(nextPageNameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		listViewResults.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
								int visibleItemCount, int totalItemCount) {
				
				if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0)
				{
					if(flag_items_loading == false && last_results_page == false)
					{
						flag_items_loading = true;
						(new HTMLGetter(getApplicationContext()) {
							@Override
							protected void onPostExecute(String result) {
								// TODO: Parse this in the Async thread instead of the UI thread
								List<Course> resultList = HTMLParser.parseResultsPage(result);
								listData.addAll(resultList);
								listAdapter.notifyDataSetChanged();
								
								flag_items_loading = false;
								if (resultList.size() < RESULTS_PER_PAGE) notifyEndOfResults();
							}
						}).execute(nextPagePost);
					}
				}
			}
		});
		
		listViewResults.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Course course = listData.get(position);
				Intent intent = new Intent(ResultsActivity.this, DetailActivity.class);
				intent.putExtra("com.github.ucscclasssearch.detail_url", course.detail_url);
				startActivity(intent);
			}
			
		});
	}
	
	private void notifyEndOfResults() {
		last_results_page = true;
		listViewResults.setAdapter(listAdapter);
		listViewResults.removeFooterView(footerView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
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
