package com.cmps121.ucsccoursebrowser;

import java.util.ArrayList;

// The PisaHTMLModel class acts as a global data structure, a kind of "database" for storing
// each search parameter (which each include a label, info about how to display it to the user,
// and the HTML ID that the HTMLGetter needs to look for when it parses the Pisa search page.

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
	
	public static final ArrayList<SearchParameter> SEARCH_PARAMETERS = new ArrayList<SearchParameter>();
	static {
		ArrayList<SearchParameter> s = SEARCH_PARAMETERS;
		//                        #label                  #type                  #html_id
		s.add(new SearchParameter("Term",                 FieldType.MULT_CHOICE, "term_dropdown"));
		s.add(new SearchParameter("Status",               FieldType.MULT_CHOICE, "reg_status"));
		s.add(new SearchParameter("Subject",              FieldType.MULT_CHOICE, "subject"));
		s.add(new SearchParameter("Course Title Keyword", FieldType.TEXT_ENTRY,  "title"));
		s.add(new SearchParameter("Instructor Last Name", FieldType.TEXT_ENTRY,  "instructor"));
		s.add(new SearchParameter("General Education",    FieldType.MULT_CHOICE, "ge"));
		s.add(new SearchParameter("Course Units",         FieldType.TEXT_ENTRY,  "crse_units_exact"));
		s.add(new SearchParameter("Meeting Days",         FieldType.MULT_CHOICE, "Days"));
		s.add(new SearchParameter("Meeting Times",        FieldType.MULT_CHOICE, "Times"));
	}
}