package com.medinfi.datainfo;

import java.io.Serializable;

public class ReviewsInfo implements Serializable {

	private String reviewId;
	private String review;
	private String userName;
	private String doctorId;
	private String userId;
	private String fees;
	private String waittime;
	private String attitude;
	private String satisfaction;
	private String recommend;
	private String rate;
	private String prescriptionImage;
	private String status;
	private String createdDate;
	private String actualUsername;
	public ReviewsInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getReviewId() {
		return reviewId;
	}
	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFees() {
		return fees;
	}
	public void setFees(String fees) {
		this.fees = fees;
	}
	public String getWaittime() {
		return waittime;
	}
	public void setWaittime(String waittime) {
		this.waittime = waittime;
	}
	public String getAttitude() {
		return attitude;
	}
	public void setAttitude(String attitude) {
		this.attitude = attitude;
	}
	public String getSatisfaction() {
		return satisfaction;
	}
	public void setSatisfaction(String satisfaction) {
		this.satisfaction = satisfaction;
	}
	public String getRecommend() {
		return recommend;
	}
	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getPrescriptionImage() {
		return prescriptionImage;
	}
	public void setPrescriptionImage(String prescriptionImage) {
		this.prescriptionImage = prescriptionImage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getActualUsername() {
		return actualUsername;
	}

	public void setActualUsername(String actualUsername) {
		this.actualUsername = actualUsername;
	}
	
}
