package com.cmps121.ucsccoursebrowser;

// The PisaHTMLModel class acts as a global data structure, a kind of "database" for storing
// each search parameter (which each include a label, info about how to display it to the user,
// and the HTML ID that the HTMLGetter needs to look for when it parses the Pisa search page.

import java.util.LinkedHashMap;

import com.cmps121.ucsccoursebrowser.SearchParameter.FieldType;

public class PisaHTMLModel {
	
	// We can iterate over SEARCH_PARAMETERS in MainActivity
	// to populate the ListView items with search parameters.
	// This data structure will also be used by the search page parser
	// to determine which ID's it needs to look for in the HTML.
	
	// This list of search parameters is hard-coded because they have been hand-selected
	// as the most useful search parameters from the Pisa class search page.
	// They are hard-coded because it would be more complicated to parse them from 
	// the class search page, and such parsing would not be robust to future changes
	// in the search page HTML.
	
	public static final LinkedHashMap<String, SearchParameter> SEARCH_PARAMETERS = new LinkedHashMap<String, SearchParameter>();
	static {
		LinkedHashMap<String, SearchParameter> s = SEARCH_PARAMETERS;
		//    #label                                      #type                  #html_id
		s.put("Term",                 new SearchParameter(FieldType.MULT_CHOICE, "term_dropdown"));
		s.put("Status",               new SearchParameter(FieldType.MULT_CHOICE, "reg_status"));
		s.put("Subject",              new SearchParameter(FieldType.MULT_CHOICE, "subject"));
		s.put("Course Title Keyword", new SearchParameter(FieldType.TEXT_ENTRY,  "title"));
		s.put("Instructor Last Name", new SearchParameter(FieldType.TEXT_ENTRY,  "instructor"));
		s.put("General Education",    new SearchParameter(FieldType.MULT_CHOICE, "ge"));
		s.put("Course Units",         new SearchParameter(FieldType.TEXT_ENTRY,  "crse_units_exact"));
		s.put("Meeting Days",         new SearchParameter(FieldType.MULT_CHOICE, "Days"));
		s.put("Meeting Times",        new SearchParameter(FieldType.MULT_CHOICE, "Times"));
	}
	
	public static final String baseURL = "https://pisa.ucsc.edu/class_search/";
	public static final String resultsPagePath = "index.php";
}