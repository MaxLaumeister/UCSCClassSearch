package com.github.ucscclasssearch;

import org.apache.http.client.methods.HttpGet;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ucscclasssearch.R;

public class DetailActivity extends ActionBarActivity {
	
	CourseDetail course;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		final ScrollView scroll_view = (ScrollView) findViewById(R.id.scroll_view);
		
		// Retrieve url from extras
		
		Intent intent = getIntent();
		final String detail_url = intent.getStringExtra("com.github.ucscclasssearch.detail_url");
		
		// Enable up navigation
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Block the screen with a ProgressDialog, which gets dismissed
		// after the HTML request for the class search page returns.
		
		// TODO: Make it cancellable and go back to the results activity when cancelled.
		
		final ProgressDialog HTTPProgress = ProgressDialog.show(DetailActivity.this,
				"Loading ...", "Please wait ...", true, false);
		scroll_view.setVisibility(View.INVISIBLE);
		
		// Send the HTTP request
		
		(new HTMLGetter(getApplicationContext()) {
			@Override
			protected void onPostExecute(String result) {
				// TODO: Parse this in the Async thread instead of the UI thread
				 //CourseDetail course = HTMLParser.parseDetailPage(result);
				course = HTMLParser.parseDetailPage(detail_url, result);
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
					((ImageView) findViewById(R.id.green_circle_detail)).setVisibility(View.VISIBLE);
				} else if (course.status.equals("Closed")) {
					((ImageView) findViewById(R.id.blue_square_detail)).setVisibility(View.VISIBLE);
				}
			}
		}).execute(new HttpGet(detail_url));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		
		final Boolean show_bookmark_button = getIntent().getBooleanExtra("com.github.ucscclasssearch.show_bookmark_button", true);
		if (!show_bookmark_button) {
			MenuItem item = menu.findItem(R.id.action_addbookmark);
			item.setVisible(false);
			this.invalidateOptionsMenu();
		}
		
		return true;
	}
	
	//rewritten to 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		int id = item.getItemId();
		
		if (id == android.R.id.home){
			finish();
			return true; 
		}
		
		if(id == R.id.action_addbookmark){
			BookmarkManager.addBookmark(course, getApplicationContext());
			Toast.makeText(this, "Added to Bookmarks", Toast.LENGTH_SHORT).show();
		}
		
		return super.onOptionsItemSelected(item);
	}

}
