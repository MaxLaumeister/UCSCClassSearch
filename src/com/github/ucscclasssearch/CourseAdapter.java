package com.github.ucscclasssearch;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.github.ucscclasssearch.R;

public class CourseAdapter extends SimpleAdapter {
	
	// showClassStatus determines whether the listview will display an icon next
	// to the class name showing whether it's open (green circle) or closed
	// (blue square). For the results activity this should be initialized as
	// true, but for the bookmark activity it should initialized to false.
	// This is because the results activity pulls new data in every time,
	// whereas the bookmark activity displays cached data, and the class status
	// is very time sensitive data (a class can be open right now, but closed
	// in an hour from now). The title, professor, and location aren't as time
	// sensitive so we can pull them from the cache.
	final boolean showClassStatus;

	public CourseAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource,
			String[] from, int[] to, boolean showClassStatus) {
		super(context, data, resource, from, to);
		this.showClassStatus = showClassStatus;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
	    if (v == null) {
	    	LayoutInflater vi = (LayoutInflater) parent.getContext()
	    			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	v = vi.inflate(R.layout.search_result_listview_item, null);
	    }
	    
	    Course course = (Course) getItem(position);
	    
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
	    
	    if (showClassStatus) {
		    if (course.status.equals("Open")) {
		    	green_circle.setVisibility(View.VISIBLE);
		    } else if (course.status.equals("Closed")) {
		    	blue_square.setVisibility(View.VISIBLE);
		    }
	    }

	    return v;
	}
	
}
