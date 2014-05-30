package com.cmps121.ucsccoursebrowser;

import java.util.LinkedHashMap;

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
	
	// The name attribute of the HTML tag for this parameter in the class search page source.
	// This starts out null and gets populated when the HTMLGetter retrieves the search page.
	public String html_name;
	
	// The options for each search parameter. The key is the title, the value is the 
	// HTML "value" attribute of the option tag, e.g. for "Term" the options would be
	// [["2014 Fall Quarter", "2148"], ["2014 Summer Quarter", "2144"], ...]
	// This starts out empty and gets populated when the HTMLGetter retrieves the search page.
	public LinkedHashMap<String, String> options;
	
	public SearchParameter(FieldType t, String id) {
		type = t;
		html_id = id;
		html_name = null;
	}
}
