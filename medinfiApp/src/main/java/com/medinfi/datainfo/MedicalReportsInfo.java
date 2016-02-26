package com.medinfi.datainfo;

import java.io.Serializable;
import java.util.ArrayList;

public class MedicalReportsInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    
    String medRecordId;
    String medRecordDoctorName;
    String medRecordHospitalName;
    String medRecordCreatedDate;
    String medRecordRecordDate;
    String medRecordRecordType;
    String medRecordRelationType;
    String medRecordImageFirst;
    String medRecordImageSecond;
   // String medRecordPic;
    ArrayList<String> medRecordMultiplePic;
    
    public String getMedRecordId() {
        return medRecordId;
    }
    public void setMedRecordId(String medRecordId) {
        this.medRecordId = medRecordId;
    }
    public String getMedRecordDoctorName() {
        return medRecordDoctorName;
    }
    public void setMedRecordDoctorName(String medRecordDoctorName) {
        this.medRecordDoctorName = medRecordDoctorName;
    }
    public String getMedRecordHospitalName() {
        return medRecordHospitalName;
    }
    public void setMedRecordHospitalName(String medRecordHospitalName) {
        this.medRecordHospitalName = medRecordHospitalName;
    }
    public String getMedRecordCreatedDate() {
        return medRecordCreatedDate;
    }
    public void setMedRecordCreatedDate(String medRecordCreatedDate) {
        this.medRecordCreatedDate = medRecordCreatedDate;
    }
   /* public String getMedRecordPic() {
        return medRecordPic;
    }
    public void setMedRecordPic(String medRecordPic) {
        this.medRecordPic = medRecordPic;
    }*/
    public String getMedRecordRecordDate() {
        return medRecordRecordDate;
    }
    public void setMedRecordRecordDate(String medRecordRecordDate) {
        this.medRecordRecordDate = medRecordRecordDate;
    }
    public String getMedRecordRecordType() {
        return medRecordRecordType;
    }
    public void setMedRecordRecordType(String medRecordRecordType) {
        this.medRecordRecordType = medRecordRecordType;
    }
    public String getMedRecordRelationType() {
        return medRecordRelationType;
    }
    public void setMedRecordRelationType(String medRecordRelationType) {
        this.medRecordRelationType = medRecordRelationType;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public ArrayList<String> getMedRecordMultiplePic() {
        return medRecordMultiplePic;
    }
    public void setMedRecordMultiplePic(ArrayList<String> medRecordMultiplePic) {
        this.medRecordMultiplePic = medRecordMultiplePic;
    }
    public String getMedRecordImageFirst() {
        return medRecordImageFirst;
    }
    public void setMedRecordImageFirst(String medRecordImageFirst) {
        this.medRecordImageFirst = medRecordImageFirst;
    }
    public String getMedRecordImageSecond() {
        return medRecordImageSecond;
    }
    public void setMedRecordImageSecond(String medRecordImageSecond) {
        this.medRecordImageSecond = medRecordImageSecond;
    }
   
}
