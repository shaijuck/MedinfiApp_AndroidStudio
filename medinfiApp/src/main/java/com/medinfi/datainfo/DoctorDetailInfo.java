package com.medinfi.datainfo;

import java.io.Serializable;
import java.util.ArrayList;

public class DoctorDetailInfo implements Serializable{
	
	private  String doctorID;
	private String doctorName;
	private String doctorQualification;
	private String doctorPhone;
	private String doctorEmail;
	private String doctorType;
	private String doctorPic;
	private String doctorAwards;
	private ArrayList<String> awardsArrayList;
	
	private ArrayList<String> memberShipArrayList;
	private String doctorAverage;
	private String doctorCreatedAt;
	private String doctorUpdatedAt;
	
	private float doctorFees;
	private float doctorWaittime;
	private float doctorAttitude;
	private float doctorSatisfaction;
	private String doctorAddress;
	private String doctorTotalExperience;
	private String doctorSecondImage;
	
	
	private ArrayList<ReviewsInfo> reviewArrayList;
	private ArrayList<HospitalInfo> hospitalArrayList;
	
	
	public ArrayList<String> getAwardsArrayList() {
		return awardsArrayList;
	}


	public void setAwardsArrayList(ArrayList<String> awardsArrayList) {
		this.awardsArrayList = awardsArrayList;
	}


	public ArrayList<String> getMemberShipArrayList() {
		return memberShipArrayList;
	}


	public void setMemberShipArrayList(ArrayList<String> memberShipArrayList) {
		this.memberShipArrayList = memberShipArrayList;
	}


	public DoctorDetailInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	

	public float getDoctorFees() {
		return doctorFees;
	}


	public void setDoctorFees(float doctorFees) {
		this.doctorFees = doctorFees;
	}


	public float getDoctorWaittime() {
		return doctorWaittime;
	}


	public void setDoctorWaittime(float doctorWaittime) {
		this.doctorWaittime = doctorWaittime;
	}


	public float getDoctorAttitude() {
		return doctorAttitude;
	}


	public void setDoctorAttitude(float doctorAttitude) {
		this.doctorAttitude = doctorAttitude;
	}


	public float getDoctorSatisfaction() {
		return doctorSatisfaction;
	}


	public void setDoctorSatisfaction(float doctorSatisfaction) {
		this.doctorSatisfaction = doctorSatisfaction;
	}


	public String getDoctorID() {
		return doctorID;
	}
	public void setDoctorID(String doctorID) {
		this.doctorID = doctorID;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	public String getDoctorQualification() {
		return doctorQualification;
	}
	public void setDoctorQualification(String doctorQualification) {
		this.doctorQualification = doctorQualification;
	}
	public String getDoctorPhone() {
		return doctorPhone;
	}
	public void setDoctorPhone(String doctorPhone) {
		this.doctorPhone = doctorPhone;
	}
	public String getDoctorEmail() {
		return doctorEmail;
	}
	public void setDoctorEmail(String doctorEmail) {
		this.doctorEmail = doctorEmail;
	}
	public String getDoctorType() {
		return doctorType;
	}
	public void setDoctorType(String doctorType) {
		this.doctorType = doctorType;
	}
	public String getDoctorPic() {
		return doctorPic;
	}
	public void setDoctorPic(String doctorPic) {
		this.doctorPic = doctorPic;
	}
	public String getDoctorAwards() {
		return doctorAwards;
	}
	public void setDoctorAwards(String doctorAwards) {
		this.doctorAwards = doctorAwards;
	}
	public String getDoctorAverage() {
		return doctorAverage;
	}
	public void setDoctorAverage(String doctorAverage) {
		this.doctorAverage = doctorAverage;
	}
	public String getDoctorCreatedAt() {
		return doctorCreatedAt;
	}
	public void setDoctorCreatedAt(String doctorCreatedAt) {
		this.doctorCreatedAt = doctorCreatedAt;
	}
	public String getDoctorUpdatedAt() {
		return doctorUpdatedAt;
	}
	public void setDoctorUpdatedAt(String doctorUpdatedAt) {
		this.doctorUpdatedAt = doctorUpdatedAt;
	}
	
	
	public ArrayList<ReviewsInfo> getReviewArrayList() {
		return reviewArrayList;
	}
	public void setReviewArrayList(ArrayList<ReviewsInfo> reviewArrayList) {
		this.reviewArrayList = reviewArrayList;
	}
	public ArrayList<HospitalInfo> getHospitalArrayList() {
		return hospitalArrayList;
	}
	public void setHospitalArrayList(ArrayList<HospitalInfo> hospitalArrayList) {
		this.hospitalArrayList = hospitalArrayList;
	}


	public String getDoctorAddress() {
	    return doctorAddress;
	}


	public void setDoctorAddress(String doctorAddress) {
	    this.doctorAddress = doctorAddress;
	}


	public String getDoctorTotalExperience() {
	    return doctorTotalExperience;
	}


	public void setDoctorTotalExperience(String doctorTotalExperience) {
	    this.doctorTotalExperience = doctorTotalExperience;
	}


	public String getDoctorSecondImage() {
	    return doctorSecondImage;
	}

	public void setDoctorSecondImage(String doctorSecondImage) {
	    this.doctorSecondImage = doctorSecondImage;
	}
	
}
