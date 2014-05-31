package com.cmps121.ucsccoursebrowser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.Html;
import android.util.Log;

import com.cmps121.ucsccoursebrowser.SearchParameter.FieldType;

// Maybe this class should be merged with PisaHTMLModel...

public class HTMLParser {
	
	private static final String LOG_TAG = MainActivity.LOG_TAG;
	
	// Takes the HTML of the Class Search Page, and updates the SearchParameters in PisaHTMLModel
	// to reflect the options available on the search page.
	
	public static boolean parseSearchPage(String html_data) {
		
		Document doc = Jsoup.parse(html_data);
		
		for (Map.Entry<String, SearchParameter> entry : PisaHTMLModel.SEARCH_PARAMETERS.entrySet()) {
			SearchParameter param = entry.getValue();
			Element select = doc.getElementById(param.html_id);
			param.html_name = select.attr("name");

			if (param.type == FieldType.MULT_CHOICE) {
				Elements options = select.getElementsByTag("option");
				LinkedHashMap<String, String> optionsObjects = new LinkedHashMap<String, String>();
				for (Element option : options) {
					String title = Html.fromHtml(option.html()).toString();
					String value = option.attr("value");
					optionsObjects.put(title, value);
				}
				// Save the results externally
				param.options = optionsObjects;
			}
		}
		
		return true; // Operation success
	}
	
	// Takes the HTML of the Search Results Page
	// Returns a List of Courses to be displayed as search results to the user
	
	public static List<Course> parseResultsPage(String html_data) {
		
		Document doc = Jsoup.parse(html_data);
		
		Element results_tbody = doc.getElementById("results_table").getElementsByTag("tbody").first();
		Elements t_rows = results_tbody.getElementsByTag("tr");
		
		if (t_rows.get(0).getElementsByTag("td").get(0).html().equals("")) {
			// No results found
			return new ArrayList<Course>();
		}
		
		List<Course> result = new ArrayList<Course>();
		
		for (Element t_row : t_rows) {
			Elements tds = t_row.getElementsByTag("td");
			
			// Time for some dom traversal
			
			final String title = tds.get(1).html();
			final String time = tds.get(4).html() + " " + tds.get(5).html();
			final String instructor = tds.get(6).html();
			final String status = tds.get(7).getElementsByTag("img").get(0).attr("alt");
			final int capacity = Integer.parseInt(tds.get(8).html());
			final int enrollment_total = Integer.parseInt(tds.get(9).html()); 
			final int available_seats = Integer.parseInt(tds.get(10).html()); 
			final String type = tds.get(3).html();
			
			// The moment you've all been waiting for...
			
			Course course = new Course(title, time, instructor, status, capacity, enrollment_total, available_seats, type);
			
			// And add it into the list
			
			result.add(course);
		}
		
		return result;
	}
	
	public static CourseDetail parseDetailPage(String html_data) {
		return null; // TODO: Implementation
	}
}
