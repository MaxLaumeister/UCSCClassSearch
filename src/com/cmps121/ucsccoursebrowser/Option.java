package com.cmps121.ucsccoursebrowser;

public class Option {
	// The title of the option, e.g. "2014 Summer Quarter" for Term, or "Anthropology" for Subject
	String title;
	
	// The value attribute of the HTML option tag, e.g. for label "Computer Science",
	// in the Pisa search page HTML source, the value would be "CMPS"
	// This starts as null and gets populated when the HTMLGetter retrieves the search page
	String value;
	
	public Option (String t, String v) {
		title = t;
		value = v;
	}
}