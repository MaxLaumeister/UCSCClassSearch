package com.cmps121.ucsccoursebrowser;

import java.util.ArrayList;

// The SearchParameter class describes a single search parameter, for example "Term", "Subject", or "Instructor".
// The complete, global list of these gets initialized by and stored in PisaHTMLModel before run time. When the app is run,
// the HTMLGetter class immediately retrieves the Pisa search page and populates the 'value' and 'options' fields
// of this global list.

public class SearchParameter {
	public enum FieldType {
		MULT_CHOICE,
		TEXT_ENTRY
	}
	
	// Whether for this parameter the user chooses from a multiple choice list (such as for subject)
	// or whether they enter a phrase into a text entry box.
	public final FieldType type;
	
	// The id attribute of the HTML tag for this parameter in the class search page source.
	public final String html_id;
	
	// The value attribute of the HTML option tag, e.g. for label "Computer Science",
	// in the Pisa search page HTML source, the value would be "CMPS"
	// This starts as null and gets populated when the HTMLGetter retrieves the search page
	public String value;
	
	// The options for each search parameter - e.g. for "Term" the options would be
	// ["2014 Fall Quarter", "2014 Summer Quarter", "2014 Spring Quarter", etc.]
	// This starts as null and gets populated when the HTMLGetter retrieves the search page
	public ArrayList<String> options;
	
	public SearchParameter(FieldType t, String id) {
		type = t;
		html_id = id;
	}
}
