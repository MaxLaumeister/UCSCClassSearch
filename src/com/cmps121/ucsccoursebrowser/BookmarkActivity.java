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
import android.support.v4.app.NavUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;



import android.R.string; 

public class BookmarkActivity extends ActionBarActivity {
	
	private static final String LOG_TAG = MainActivity.LOG_TAG;
	
	private ListView listViewSaves; //the list view 
	private List<Course> listData;
	private ResultsAdapter listAdapter; 
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
		
		listAdapter = new ResultsAdapter(this, (List<? extends Map<String, ?>>) listData,
				R.layout.search_result_listview_item, 
				new String[] {"First Line", "Second Line" }, 
				new int[] {android.R.id.text1, android.R.id.text2 });
		
		listViewSaves.setAdapter(listAdapter);
		
		//sendSearchPost(); 
		
		footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.search_result_listview_spinner, null, false);
		listViewSaves.addFooterView(footerView);
		
	}

	private void notifyEndOfResults() {
		last_saves_page = true;
		listViewSaves.removeFooterView(footerView);
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
		    if (v == null) {
		    	LayoutInflater vi = (LayoutInflater) parent.getContext()
		    			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    	v = vi.inflate(R.layout.search_result_listview_item, null);
		    }
		    
		    Course course = listData.get(position);
		    
		    TextView text1 = (TextView) v.findViewById(R.id.textView1);
		    if (text1 != null) {
		    	text1.setText(course.title);
		    }
		    
		    TextView text2 = (TextView) v.findViewById(R.id.textView2);
		    if (text2 != null) {
		    	text2.setText(course.time);
		    }
		    
		    TextView text3 = (TextView) v.findViewById(R.id.textView3);
		    if (text3 != null) {
		    	text3.setText(course.instructor);
		    }
		    
		    ImageView green_circle = (ImageView) v.findViewById(R.id.green_circle);
		    ImageView blue_square = (ImageView) v.findViewById(R.id.blue_square);
		    
		    green_circle.setVisibility(View.GONE);
		    blue_square.setVisibility(View.GONE);
		    
		    if (course.status.equals("Open")) {
		    	green_circle.setVisibility(View.VISIBLE);
		    } else if (course.status.equals("Closed")) {
		    	blue_square.setVisibility(View.VISIBLE);
		    }

		    return v;
		}
		
	}

}
