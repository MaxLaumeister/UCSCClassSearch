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
		ArrayList<String> titles = new ArrayList<String>();
		Elements t_rows = results_tbody.getElementsByTag("tr");
		for (Element t_row : t_rows) {
			Element td_title = t_row.getElementsByTag("td").get(1); // Title is the second column in the table
			titles.add(td_title.html());
		}
		Log.d(LOG_TAG, titles.toString());
		
		return null; // TODO: Implementation
	}
	
	public static CourseDetail parseDetailPage(String html_data) {
		return null; // TODO: Implementation
	}
}
