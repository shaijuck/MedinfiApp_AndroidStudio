package com.medinfi.datainfo;

import java.io.Serializable;

public class QualificationInfo implements Serializable
{
/*	"id": "382",
    "doctorId": "161",
    "course": "MBBS",
    "college": ""
    
    Graduation Degree        Graduation College  
          Post Graduation Degree        Post Graduation Specialization   
               Post Graduation College        Specialization Degree      
                 Specialization Area        Specialization College
            
            Super Specialization Degree        Super Specialization Area  
                  Super Specialization College
    */
	
	private String id;
	private String doctorId;
	private String course;
	private String college;
	
	private String pgDegree;
	private String pgSpecialization;
	private String pgCollege;
	
	
	private String specializationDegree;
	private String specializationArea;
	private String specializationCollege;
	
	
	
	
	public String getPgDegree() {
		return pgDegree;
	}
	public void setPgDegree(String pgDegree) {
		this.pgDegree = pgDegree;
	}
	public String getPgSpecialization() {
		return pgSpecialization;
	}
	public void setPgSpecialization(String pgSpecialization) {
		this.pgSpecialization = pgSpecialization;
	}
	public String getPgCollege() {
		return pgCollege;
	}
	public void setPgCollege(String pgCollege) {
		this.pgCollege = pgCollege;
	}
	public String getSpecializationDegree() {
		return specializationDegree;
	}
	public void setSpecializationDegree(String specializationDegree) {
		this.specializationDegree = specializationDegree;
	}
	public String getSpecializationArea() {
		return specializationArea;
	}
	public void setSpecializationArea(String specializationArea) {
		this.specializationArea = specializationArea;
	}
	public String getSpecializationCollege() {
		return specializationCollege;
	}
	public void setSpecializationCollege(String specializationCollege) {
		this.specializationCollege = specializationCollege;
	}
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
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
public QualificationInfo() {
	// TODO Auto-generated constructor stub
}
}
