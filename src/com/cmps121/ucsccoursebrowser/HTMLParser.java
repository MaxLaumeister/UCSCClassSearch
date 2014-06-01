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
			
			Element results_table = doc.getElementById("results_table");
			Element results_thead = results_table.getElementsByTag("thead").first();
			Element results_tbody = results_table.getElementsByTag("tbody").first();
			Elements t_rows = results_tbody.getElementsByTag("tr");
			
			if (t_rows.get(0).getElementsByTag("td").get(0).html().equals("")) {
				// No results found
				return result; // Empty
			}
			
			final int column_number = results_thead.getElementById("class_nbr").elementSiblingIndex();
			final int column_id = results_thead.getElementById("class_id").elementSiblingIndex();
			final int column_title = results_thead.getElementById("class_title").elementSiblingIndex();
			final int column_type = results_thead.getElementById("type").elementSiblingIndex();
			final int column_times = results_thead.getElementById("times").elementSiblingIndex();
			final int column_days = results_thead.getElementById("days").elementSiblingIndex();
			final int column_instructor = results_thead.getElementById("instr_name").elementSiblingIndex();
			final int column_status = results_thead.getElementById("status").elementSiblingIndex();
			final int column_capacity = results_thead.getElementById("enrl_cap").elementSiblingIndex();
			final int column_enrollment_total = results_thead.getElementById("enrl_tot").elementSiblingIndex();
			final int column_available_seats = results_thead.getElementById("seats_avail").elementSiblingIndex();
			
			for (Element t_row : t_rows) {
				Elements tds = t_row.getElementsByTag("td");
				
				// Time for some dom traversal
				
				final String classTitleNumber = StringEscapeUtils.unescapeHtml4(tds.get(column_id).html().split(" - ")[0]);
				final String classTitleName = StringEscapeUtils.unescapeHtml4(tds.get(column_title).getElementsByTag("a").get(0).html());
				
				final String title = classTitleNumber + " - " + classTitleName;
				final String time = StringEscapeUtils.unescapeHtml4(tds.get(column_times).html() + " " + tds.get(column_days).html());
				final String instructor = StringEscapeUtils.unescapeHtml4(tds.get(column_instructor).html()).replace("<br />", ", ").replace(",", ", ");
				final String status = StringEscapeUtils.unescapeHtml4(tds.get(column_status).getElementsByTag("img").get(0).attr("alt"));
				final int capacity = Integer.parseInt(tds.get(column_capacity).html());
				final int enrollment_total = Integer.parseInt(tds.get(column_enrollment_total).html()); 
				final int available_seats = Integer.parseInt(tds.get(column_available_seats).html()); 
				final String type = StringEscapeUtils.unescapeHtml4(tds.get(column_type).html());
				final String detail_url = tds.get(column_number).getElementsByTag("a").get(0).attr("href");
				
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
					.ownText(); //   ¯\_(ツ)_/¯
			
			String title = StringEscapeUtils.unescapeHtml4(escapedTitle);
			
			Element meetingInformationTbody = doc.getElementsContainingOwnText("Meeting Information").select("b")
					.get(0).parent().parent().parent();
			Element meetingInformationHeaderRow = meetingInformationTbody.child(1);
			Element meetingInformationRow = meetingInformationTbody.child(2);
			
			final int column_time = meetingInformationHeaderRow.getElementsContainingOwnText("Days & Times").get(0).parent().elementSiblingIndex();
			final int column_room = meetingInformationHeaderRow.getElementsContainingOwnText("Room").get(0).parent().elementSiblingIndex();
			final int column_instructor = meetingInformationHeaderRow.getElementsContainingOwnText("Instructor").get(0).parent().elementSiblingIndex();
			
			String time = StringEscapeUtils.unescapeHtml4(meetingInformationRow.child(column_time).html());
			String room = StringEscapeUtils.unescapeHtml4(meetingInformationRow.child(column_room).html());
			String instructor = StringEscapeUtils.unescapeHtml4(meetingInformationRow.child(column_instructor).html().replace(",", ", "));
			
			final Element classDetailsTbody = doc.getElementsContainingOwnText("Class Details").select("b")
					.get(0).parent().parent().parent();
			
			// Position in the Class Details table
			class Position {
				int row;
				int col;
				
				Position(String key) {
					init(key);
				}
				
				Position(String key, String alt_key) {
					Elements tds = classDetailsTbody.getElementsContainingOwnText(key);
					if (!tds.isEmpty()) init(key);
					else init(alt_key);
				}
				
				void init(String key_name) {
					Log.d(LOG_TAG, classDetailsTbody.outerHtml());
					Log.d(LOG_TAG, key_name);
					Element td  = classDetailsTbody.getElementsContainingOwnText(key_name).get(0).parent();
					col = td.elementSiblingIndex() + 1; // Move one to the right
					row = td.parent().elementSiblingIndex();
				}
				
				Element getElement() {
					return classDetailsTbody.child(row).child(col);
				}
			}
			
			Position pos_status = new Position("Status");
			Position pos_capacity = new Position("Enrollment Capacity", "Combined Section Capacity");
			Position pos_type = new Position("Type");
			Position pos_enrollment_total = new Position("Enrolled");
			Position pos_available_seats = new Position("Available Seats");
			Position pos_credits = new Position("Credits");
			Position pos_wait_list_capacity = new Position("Wait List Capacity");
			Position pos_genEds = new Position("General Education");
			
			String status = StringEscapeUtils.unescapeHtml4(pos_status.getElement().child(0).attr("alt"));
			int capacity = Integer.parseInt(pos_capacity.getElement().html());
			String type = StringEscapeUtils.unescapeHtml4(pos_type.getElement().html());
			int enrollment_total = Integer.parseInt(pos_enrollment_total.getElement().html());
			int available_seats = Integer.parseInt(pos_available_seats.getElement().html());
			String detail_url = null; // Don't need this anymore
			String credits = StringEscapeUtils.unescapeHtml4(pos_credits.getElement().html());
			int wait_list_capacity = Integer.parseInt(pos_wait_list_capacity.getElement().html());
			String genEds = StringEscapeUtils.unescapeHtml4(pos_genEds.getElement().html());
			
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
