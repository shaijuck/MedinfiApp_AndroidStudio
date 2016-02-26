package com.medinfi.datainfo;

import java.io.Serializable;

public class AutoCompleteInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String address;
    private String primaryphone;
    private  String locality;
    private String type;
    private String latitude;
    private String longitude;
    private String doctype;
    private String pic;
    private String rating;
    private String saved;
    private String distance;
    private String docSecondImage;
    private String hospitalEmergencyPic;
    private String hospitalLeftPic;
    private String hospitalRightPic;
    private String hospitalName;

    public AutoCompleteInfo() {
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrimaryphone() {
        return primaryphone;
    }

    public void setPrimaryphone(String primaryphone) {
        this.primaryphone = primaryphone;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDocSecondImage() {
        return docSecondImage;
    }

    public void setDocSecondImage(String docSecondImage) {
        this.docSecondImage = docSecondImage;
    }

    public String getHospitalEmergencyPic() {
        return hospitalEmergencyPic;
    }

    public void setHospitalEmergencyPic(String hospitalEmergencyPic) {
        this.hospitalEmergencyPic = hospitalEmergencyPic;
    }

    public String getHospitalLeftPic() {
        return hospitalLeftPic;
    }

    public void setHospitalLeftPic(String hospitalLeftPic) {
        this.hospitalLeftPic = hospitalLeftPic;
    }

    public String getHospitalRightPic() {
        return hospitalRightPic;
    }

    public void setHospitalRightPic(String hospitalRightPic) {
        this.hospitalRightPic = hospitalRightPic;
    }

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

}
