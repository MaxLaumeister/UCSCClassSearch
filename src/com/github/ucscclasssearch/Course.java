package com.github.ucscclasssearch;

// The Course object contains course information as displayed on the Search Results page.
// When the user taps on a course in the Search Results Activity, an HTML request will be made
// for the Pisa Class Detail page. Then a CourseDetail object will be constructed using
// information that's only available on the Class Detail page.
// This CourseDetail instance will then be used to populate the view in DetailActivity.

public class Course {

	//first thing is to declare the variable for each class. 
	
	final String title; 
	final String time; 
	final String instructor; 
	final String status; 
	final int capacity; 
	final int enrollment_total; 
	final int available_seats; 
	final String type; // LEC, LAB, SEM, etc.
	final String detail_url; // The url that points to the detail page for the class
	
	public Course(String title, String time, String instructor, String status,
					int capacity, int enrollment_total, int available_seats, String type, String detail_url) {
		
		this.title = title;
		this.time = time;
		this.instructor = instructor;
		this.status = status;
		this.capacity = capacity;
		this.enrollment_total = enrollment_total;
		this.available_seats = available_seats;
		this.type = type;
		this.detail_url = detail_url;
		
	}
	
	public boolean isFull(){
		//this function will check to see if the class is full. 
		//if the class is full it will display an icon indicating the class is full. 
		return (available_seats <= 0);
	}
	
}
