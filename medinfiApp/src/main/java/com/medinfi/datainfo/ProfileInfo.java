package com.medinfi.datainfo;

import java.io.Serializable;

public class ProfileInfo implements Serializable {
	
	private String profileId;
	private String profileName;
	private String profileDesignation;
	private String profileRating;
	private String profileDistance;
	private String profilePhoneNumber;
	private String profileImage;
	private String profileEmail;
	private String profileAwards;
	private String profileExperience;
	private String hospitalName;
	private String firstName;
	private String lastName;
	private String middleName;
	private boolean addedToFilteredList=false;
	private String secondImage;
	private String specialityName;
	public ProfileInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public String getHospitalName() {
		return hospitalName;
	}



	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}



	public String getProfileExperience() {
		return profileExperience;
	}



	public void setProfileExperience(String profileExperience) {
		this.profileExperience = profileExperience;
	}



	public String getProfileAwards() {
		return profileAwards;
	}



	public void setProfileAwards(String profileAwards) {
		this.profileAwards = profileAwards;
	}



	public String getProfileEmail() {
		return profileEmail;
	}


	public void setProfileEmail(String profileEmail) {
		this.profileEmail = profileEmail;
	}


	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProfileDesignation() {
		return profileDesignation;
	}
	public void setProfileDesignation(String profileDesignation) {
		this.profileDesignation = profileDesignation;
	}
	public String getProfileRating() {
		return profileRating;
	}
	public void setProfileRating(String profileRating) {
		this.profileRating = profileRating;
	}
	public String getProfileDistance() {
		return profileDistance;
	}
	public void setProfileDistance(String profileDistance) {
		this.profileDistance = profileDistance;
	}
	public String getProfilePhoneNumber() {
		return profilePhoneNumber;
	}
	public void setProfilePhoneNumber(String profilePhoneNumber) {
		this.profilePhoneNumber = profilePhoneNumber;
	}
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public String getFirstName() {
	    return firstName;
	}
	public void setFirstName(String firstName) {
	    this.firstName = firstName;
	}
	public String getLastName() {
	    return lastName;
	}
	public void setLastName(String lastName) {
	    this.lastName = lastName;
	}
	public String getMiddleName() {
	    return middleName;
	}
	public void setMiddleName(String middleName) {
	    this.middleName = middleName;
	}
	
	public boolean isAddedToFilteredList() {
	    return addedToFilteredList;
	}



	public void setAddedToFilteredList(boolean addedToFilteredList) {
	    this.addedToFilteredList = addedToFilteredList;
	}

	public String getSecondImage() {
	    return secondImage;
	}

	public void setSecondImage(String secondImage) {
	    this.secondImage = secondImage;
	}



	public String getSpecialityName() {
		return specialityName;
	}



	public void setSpecialityName(String specialityName) {
		this.specialityName = specialityName;
	}
	
}
