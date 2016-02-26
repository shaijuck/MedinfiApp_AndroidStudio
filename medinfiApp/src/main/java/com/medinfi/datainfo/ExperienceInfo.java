package com.medinfi.datainfo;

import java.io.Serializable;

public class ExperienceInfo implements Serializable {

	// {"id":"41","doctorId":"1","hospitalName":"hospital1","year":"2001","employmentType":"permanent","fee":"100"}
	private String id;
	private String doctorId;
	private String hospitalName;
	private String year;
	private String employmentType;
	private String fee;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getEmploymentType() {
		return employmentType;
	}
	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}

	public ExperienceInfo() {
		// TODO Auto-generated constructor stub
	}
}
