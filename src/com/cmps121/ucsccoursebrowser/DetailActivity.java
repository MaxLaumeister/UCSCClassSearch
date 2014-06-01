package com.cmps121.ucsccoursebrowser;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.os.Build;

public class DetailActivity extends ActionBarActivity {
	
	private static final String LOG_TAG = MainActivity.LOG_TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		final ScrollView scroll_view = (ScrollView) findViewById(R.id.scroll_view);
		
		// Retrieve url from extras
		
		Intent intent = getIntent();
		String relative_url = (String) intent.getStringExtra("com.cmps121.ucsccoursebrowser.detail_url");
		String detail_url = PisaHTMLModel.baseURL + relative_url;
		
		// Enable up navigation
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Block the screen with a ProgressDialog, which gets dismissed
		// after the HTML request for the class search page returns.
		
		// TODO: Make it cancellable and go back to the results activity when cancelled.
		
		final ProgressDialog HTTPProgress = ProgressDialog.show(DetailActivity.this,
				"Searching ...", "Please wait ...", true, false);
		scroll_view.setVisibility(View.INVISIBLE);
		
		// Send the HTTP request
		
		(new HTMLGetter(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				// TODO: Parse this in the Async thread instead of the UI thread
				CourseDetail course = HTMLParser.parseDetailPage(result);
				HTTPProgress.dismiss();
				if (course == null) return;
				scroll_view.setVisibility(View.VISIBLE);
				
				// Fill views with course data
				
				((TextView) findViewById(R.id.textViewCourseName)).setText(course.title);
				((TextView) findViewById(R.id.textViewCourseTime)).setText(course.time);
				((TextView) findViewById(R.id.textViewCourseRoom)).setText(course.room);
				((TextView) findViewById(R.id.textViewInstructor)).setText(course.instructor);
				((TextView) findViewById(R.id.textViewStatusValue)).setText(course.status);
				((TextView) findViewById(R.id.textViewEnrolledValue)).setText(Integer.toString(course.enrollment_total) + "/" + Integer.toString(course.capacity));
				((TextView) findViewById(R.id.textViewTypeValue)).setText(course.type);
				((TextView) findViewById(R.id.textViewCreditsValue)).setText(course.credits);
				((TextView) findViewById(R.id.textViewGEValue)).setText(course.genEds);
				((TextView) findViewById(R.id.textViewDescription)).setText(course.description);
				
				if (course.status.equals("Open")) {
					((ImageView) findViewById(R.id.green_circle)).setVisibility(View.VISIBLE);
				} else if (course.status.equals("Closed")) {
					((ImageView) findViewById(R.id.blue_square)).setVisibility(View.VISIBLE);
				}
			}
		}).execute(new HttpPost(detail_url));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    // Respond to the action bar's Up/Home button
		    case android.R.id.home:
		        finish();
		        return true;
		    case R.id.action_settings:
		    	return true;
		    }
		return super.onOptionsItemSelected(item);
	}

}
