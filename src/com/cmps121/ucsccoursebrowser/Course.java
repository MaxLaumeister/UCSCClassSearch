package com.cmps121.ucsccoursebrowser;

//this is the class that will be used to store all the information about a particular course 


public class Course {

	//first thing is to declare the variable for each class. 
	
	String title; 
	String time; 
	String professor; 
	String status; 
	String avalible; 
	String capacity; 
	String taken; 
	String wlCapacity; 
	String wlTotal; 
	String credits; 
	String genEds; 
	String description; 
	
	
	public boolean isFull(){
		//this function will check to see if the class is full. 
		//if the class is full it will display an icon indicating the class is full. 
		if(taken.compareTo(capacity)>0){
			return true; 
		}
		return false; 
	}
	
	public boolean wlFull(){
		//this function will check to see if the waitlist capacity is at or beyond capacity.
		if(wlTotal.compareTo(wlCapacity) > 0 ){
			return true; 
		}
		return false; 
	}
	
}
