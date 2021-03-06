package com.github.ucscclasssearch;

// An extended version of Course, to be used for display in the Course Detail Activity

public class CourseDetail extends Course {
	
	final int wait_list_capacity; 
	final int wait_list_total; 
	final String genEds; 
	final String description;
	final String credits;
	final String room;
	
	public CourseDetail(String title, String time, String instructor, String status,
			int capacity, int enrollment_total, int available_seats, String detail_url, String type,
			int wait_list_capacity, int wait_list_total, String genEds, String description, String credits, String room) {
		
		super(title, time, instructor, status,
				capacity, enrollment_total, available_seats, type, detail_url);
		
		this.wait_list_capacity = wait_list_capacity;
		this.wait_list_total = wait_list_total;
		this.genEds = genEds;
		this.description = description;
		this.credits = credits;
		this.room = room;
	}
	
	public boolean waitlistFull(){
		//this function will check to see if the waitlist capacity is at or beyond capacity.
		return (wait_list_total >= wait_list_capacity); 
	}
}
