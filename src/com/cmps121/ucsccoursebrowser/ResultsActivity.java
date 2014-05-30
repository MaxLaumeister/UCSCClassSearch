package com.cmps121.ucsccoursebrowser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.cmps121.ucsccoursebrowser.SearchParameter.FieldType;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;

public class ResultsActivity extends ActionBarActivity {

	private static final String LOG_TAG = MainActivity.LOG_TAG;
	
	ListView listViewResults; // The ListView containing the search results
	ArrayList<Course> listData = new ArrayList<Course>(); // The underlying list for the above ListView
	ResultsAdapter listAdapter; // The adapter that links listData to ListViewSearch
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
		// Initialize
		
		listViewResults = (ListView) findViewById(R.id.listViewResults);
		listData = new ArrayList<Course>();
		listViewResults.setAdapter(listAdapter);
		
		listAdapter = new ResultsAdapter(this, (List<? extends Map<String, ?>>) listData,
				R.layout.search_result_listview_item, 
				new String[] {"First Line", "Second Line" }, 
				new int[] {android.R.id.text1, android.R.id.text2 });
		
		// Send the post request that will populate the listView
		
		sendSearchPost();
	}
	
	private void sendSearchPost() {
		// Get intent extras data
		
		Intent intent = getIntent();
		@SuppressWarnings("unchecked")
		ArrayList<Map<String, String>> searchQueryList = (ArrayList<Map<String, String>>) intent.getSerializableExtra("com.cmps121.ucsccoursebrowser.listData");
		
		// Create an HTTP post to populate this activity
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(searchQueryList.size());
		
		// Get name-value pairs for the post request
		
		nameValuePairs.add(new BasicNameValuePair("action", "results"));
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
				//Log.d(LOG_TAG, resultsList.toString());
			}
		}).execute(post);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
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
	
	public class ResultsAdapter extends SimpleAdapter {

		public ResultsAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
		    if (convertView == null) {
		    	LayoutInflater vi = (LayoutInflater) getApplicationContext()
		    			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	convertView = vi.inflate(R.layout.search_result_listview_item, null);
		    }
		    
		    Course course = listData.get(position);
		    
		    TextView text1 = (TextView) convertView.findViewById(R.id.textView1);
		    if (text1 != null) {
		    	text1.setText(course.title);
		    }
		    
		    TextView text2 = (TextView) convertView.findViewById(R.id.textView2);
		    if (text2 != null) {
		    	text2.setText(course.time);
		    }
		    
		    TextView text3 = (TextView) convertView.findViewById(R.id.textView3);
		    if (text3 != null) {
		    	text3.setText(course.instructor);
		    }

		    return v;
		}
		
	}

}
