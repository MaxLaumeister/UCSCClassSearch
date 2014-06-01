package com.cmps121.ucsccoursebrowser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
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
		
		try { 
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
		} catch (Exception e) {
			Log.e("Error Parsing HTML", "exception", e);
			return false;
		}
		return true; // Operation success
	}
	
	// Takes the HTML of the Search Results Page
	// Returns a List of Courses to be displayed as search results to the user
	
	public static List<Course> parseResultsPage(String html_data) {
		List<Course> result = new ArrayList<Course>();
		try { 
			Document doc = Jsoup.parse(html_data);
			
			Element results_tbody = doc.getElementById("results_table").getElementsByTag("tbody").first();
			Elements t_rows = results_tbody.getElementsByTag("tr");
			
			if (t_rows.get(0).getElementsByTag("td").get(0).html().equals("")) {
				// No results found
				return result; // Empty
			}
			
			for (Element t_row : t_rows) {
				Elements tds = t_row.getElementsByTag("td");
				
				// Time for some dom traversal
				
				final String classTitleNumber = StringEscapeUtils.unescapeHtml4(tds.get(1).html().split(" - ")[0]);
				final String classTitleName = StringEscapeUtils.unescapeHtml4(tds.get(2).getElementsByTag("a").get(0).html());
				
				final String title = classTitleNumber + " - " + classTitleName;
				final String time = StringEscapeUtils.unescapeHtml4(tds.get(4).html() + " " + tds.get(5).html());
				final String instructor = StringEscapeUtils.unescapeHtml4(tds.get(6).html()).replace("<br />", ", ").replace(",", ", ");
				final String status = StringEscapeUtils.unescapeHtml4(tds.get(7).getElementsByTag("img").get(0).attr("alt"));
				final int capacity = Integer.parseInt(tds.get(8).html());
				final int enrollment_total = Integer.parseInt(tds.get(9).html()); 
				final int available_seats = Integer.parseInt(tds.get(10).html()); 
				final String type = StringEscapeUtils.unescapeHtml4(tds.get(3).html());
				final String detail_url = tds.get(0).getElementsByTag("a").get(0).attr("href");
				
				// The moment you've all been waiting for...
				
				Course course = new Course(title, time, instructor, status, capacity,
						enrollment_total, available_seats, type, detail_url);
				
				// And add it into the list
				
				result.add(course);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error Parsing HTML", e);
			return new ArrayList<Course>();
		}
		return result;
	}
	
	public static CourseDetail parseDetailPage(String html_data) {
		try {
			Document doc = Jsoup.parse(html_data);
			
			String escapedTitle = doc.getElementsContainingOwnText("Class Detail").select("h4")
					.get(0).parent().parent().parent().child(1).child(0)
					.html().split("<")[0].replace("&nbsp;&nbsp; ", "\n"); //   ¯\_(ツ)_/¯
			
			String title = StringEscapeUtils.unescapeHtml4(escapedTitle);
			
			Element meetingInformationRow = doc.getElementsContainingOwnText("Meeting Information").select("b")
					.get(0).parent().parent().parent().child(2);
			
			String time = meetingInformationRow.child(0).html();
			String room = meetingInformationRow.child(1).html();
			String instructor = meetingInformationRow.child(2).html().replace(",", ", ");
			
			Element classDetailsTbody = doc.getElementsContainingOwnText("Class Details").select("b")
					.get(0).parent().parent().parent();
			
			String status = StringEscapeUtils.unescapeHtml4(classDetailsTbody.child(1).child(4).child(0).attr("alt"));
			int capacity = Integer.parseInt(classDetailsTbody.child(3).child(4).html());
			String type = StringEscapeUtils.unescapeHtml4(classDetailsTbody.child(4).child(1).html());
			int enrollment_total = Integer.parseInt(classDetailsTbody.child(4).child(4).html());
			int available_seats = Integer.parseInt(classDetailsTbody.child(4).child(4).html());
			String detail_url = null; // Don't need this anymore
			String credits = StringEscapeUtils.unescapeHtml4(classDetailsTbody.child(5).child(1).html());
			int wait_list_capacity = Integer.parseInt(classDetailsTbody.child(5).child(4).html());
			String genEds = StringEscapeUtils.unescapeHtml4(classDetailsTbody.child(6).child(1).html());
			
			if (genEds.equals("")) {
				genEds = "(None)";
			}
			
			int wait_list_total = Integer.parseInt(classDetailsTbody.child(6).child(4).html());
			String description = StringEscapeUtils.unescapeHtml4(doc.getElementsContainingOwnText("Description")
					.select("b").get(0).parent().parent().parent().child(1).child(0).html().replaceAll("<[^>]*>", ""));
			// The regex strips out any remaining HTML tags
			
			return new CourseDetail(title, time, instructor, status, capacity, enrollment_total,
					available_seats, detail_url, type, wait_list_capacity, wait_list_total,
					genEds, description, credits, room);
			
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error Parsing HTML", e);
			return null;
		}
	}
}
