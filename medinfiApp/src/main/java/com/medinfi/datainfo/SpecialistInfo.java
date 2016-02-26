package com.medinfi.datainfo;


public class SpecialistInfo {

	private String specialistName;
	private String specialistID;
	private String specialistDescription;
	private String specialistImage;
	private boolean addedToFilteredList=false;

	// Empty Constructor
	public SpecialistInfo() {

	}

	/*// Constructor
	public SpecialistInfo(String title,String specialistDescription, Drawable image) {
		super();
		this.specialistName = title;
		this.specialistDescription = specialistDescription;
		this.specialistImage = image;
	}*/

	public String getSpecialistID() {
		return specialistID;
	}

	public void setSpecialistID(String specialistID) {
		this.specialistID = specialistID;
	}

	public String getSpecialistName() {
		return specialistName;
	}

	public void setSpecialistName(String specialistName) {
		this.specialistName = specialistName;
	}

	public String getSpecialistDescription() {
		return specialistDescription;
	}

	public void setSpecialistDescription(String specialistDescription) {
		this.specialistDescription = specialistDescription;
	}

	public String getSpecialistImage() {
		return specialistImage;
	}

	public void setSpecialistImage(String specialistImage) {
		this.specialistImage = specialistImage;
	}

	public boolean isAddedToFilteredList() {
	    return addedToFilteredList;
	}

	public void setAddedToFilteredList(boolean addedToFilteredList) {
	    this.addedToFilteredList = addedToFilteredList;
	}

	
}
