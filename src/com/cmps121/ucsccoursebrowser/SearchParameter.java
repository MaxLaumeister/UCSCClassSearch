package com.cmps121.ucsccoursebrowser;

public class SearchParameter {
	public enum FieldType {
		MULT_CHOICE,
		TEXT_ENTRY
	}
	
	// The name of this search parameter as presented in the Main Search Activity
	public final String label;
	
	// Whether for this parameter the user chooses from a multiple choice list (such as for subject)
	// or whether they enter a phrase into a text entry box.
	public final FieldType type;
	
	// The id attribute of the HTML tag for this parameter in the class search page source.
	public final String html_id;
	
	public SearchParameter(String lbl, FieldType t, String id) {
		label = lbl;
		type = t;
		html_id = id;
	}
}
