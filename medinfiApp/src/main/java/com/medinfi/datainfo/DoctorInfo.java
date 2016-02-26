package com.medinfi.datainfo;

import java.io.Serializable;
import java.util.ArrayList;

public class DoctorInfo implements Serializable{
	
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
	
	private String doctorFees;
	private String doctorWaittime;
	private String doctorAttitude;
	private String doctorSatisfaction;
	private String doctorExperience;
	private String doctorAddress;
	private String doctorSaved;
	private String doctorRated;
	private String doctorSpecialistDegree;
	private String doctorRecommendCount;
	private String doctorNonRecommendCount;
	private String doctorConsultFee;
	
	private ArrayList<ReviewsInfo> reviewArrayList;
	private ArrayList<HospitalInfo> hospitalArrayList;
	
	private ArrayList<ExperienceInfo> experienceArrayList;
	
	private ArrayList<QualificationInfo> qualificationArrayList;
	private String doctorSecondImage;
	
	
	
	
	public ArrayList<QualificationInfo> getQualificationArrayList() {
		return qualificationArrayList;
	}


	public void setQualificationArrayList(
			ArrayList<QualificationInfo> qualificationArrayList) {
		this.qualificationArrayList = qualificationArrayList;
	}


	public ArrayList<String> getAwardsArrayList() {
		return awardsArrayList;
	}


	public void setAwardsArrayList(ArrayList<String> awardsArrayList) {
		this.awardsArrayList = awardsArrayList;
	}

	

	public ArrayList<ExperienceInfo> getExperienceArrayList() {
		return experienceArrayList;
	}


	public void setExperienceArrayList(ArrayList<ExperienceInfo> experienceArrayList) {
		this.experienceArrayList = experienceArrayList;
	}


	public String getDoctorExperience() {
		return doctorExperience;
	}


	public void setDoctorExperience(String doctorExperience) {
		this.doctorExperience = doctorExperience;
	}


	public ArrayList<String> getMemberShipArrayList() {
		return memberShipArrayList;
	}


	public void setMemberShipArrayList(ArrayList<String> memberShipArrayList) {
		this.memberShipArrayList = memberShipArrayList;
	}


	public DoctorInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	public String getDoctorFees() {
		return doctorFees;
	}


	public void setDoctorFees(String doctorFees) {
		this.doctorFees = doctorFees;
	}


	public String getDoctorWaittime() {
		return doctorWaittime;
	}


	public void setDoctorWaittime(String doctorWaittime) {
		this.doctorWaittime = doctorWaittime;
	}


	public String getDoctorAttitude() {
		return doctorAttitude;
	}


	public void setDoctorAttitude(String doctorAttitude) {
		this.doctorAttitude = doctorAttitude;
	}


	public String getDoctorSatisfaction() {
		return doctorSatisfaction;
	}


	public void setDoctorSatisfaction(String doctorSatisfaction) {
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


	public String getDoctorSaved() {
	    return doctorSaved;
	}


	public void setDoctorSaved(String doctorSaved) {
	    this.doctorSaved = doctorSaved;
	}


	public String getDoctorRated() {
	    return doctorRated;
	}


	public void setDoctorRated(String doctorRated) {
	    this.doctorRated = doctorRated;
	}


	public String getDoctorSpecialistDegree() {
	    return doctorSpecialistDegree;
	}
	public void setDoctorSpecialistDegree(String doctorSpecialistDegree) {
	    this.doctorSpecialistDegree = doctorSpecialistDegree;
	}


	public String getDoctorRecommendCount() {
	    return doctorRecommendCount;
	}


	public void setDoctorRecommendCount(String doctorRecommendCount) {
	    this.doctorRecommendCount = doctorRecommendCount;
	}


	public String getDoctorNonRecommendCount() {
	    return doctorNonRecommendCount;
	}


	public void setDoctorNonRecommendCount(String doctorNonRecommendCount) {
	    this.doctorNonRecommendCount = doctorNonRecommendCount;
	}

	public String getDoctorSecondImage() {
	    return doctorSecondImage;
	}
	
	public void setDoctorSecondImage(String doctorSecondImage) {
	    this.doctorSecondImage = doctorSecondImage;
	}


	public String getDoctorConsultFee() {
		return doctorConsultFee;
	}


	public void setDoctorConsultFee(String doctorConsultFee) {
		this.doctorConsultFee = doctorConsultFee;
	}
	
	
	
}
