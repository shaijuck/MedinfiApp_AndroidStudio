package com.medinfi.datainfo;

import java.io.Serializable;

public class FavouriteInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    
    String favId;
    String favName;
    String favAddress;
    String favPrimaryphone;
    String favLocality;
    String favType;
    String favLatitude;
    String favLongitude;
    String favDoctype;
    String favPic;
    String favRating;
    String favSaved;
    int favSavedIcon;
    String favSecondPic;
    private String hospitalEmergencyPic;
    private String hospitalLeftPic;
    private String hospitalRightPic;
    String distance;
    
    public String getFavId() {
        return favId;
    }
    public void setFavId(String favId) {
        this.favId = favId;
    }
    public String getFavName() {
        return favName;
    }
    public void setFavName(String favName) {
        this.favName = favName;
    }
    public String getFavAddress() {
        return favAddress;
    }
    public void setFavAddress(String favAddress) {
        this.favAddress = favAddress;
    }
    public String getFavPrimaryphone() {
        return favPrimaryphone;
    }
    public void setFavPrimaryphone(String favPrimaryphone) {
        this.favPrimaryphone = favPrimaryphone;
    }
    public String getFavLocality() {
        return favLocality;
    }
    public void setFavLocality(String favLocality) {
        this.favLocality = favLocality;
    }
    public String getFavType() {
        return favType;
    }
    public void setFavType(String favType) {
        this.favType = favType;
    }
    public String getFavLatitude() {
        return favLatitude;
    }
    public void setFavLatitude(String favLatitude) {
        this.favLatitude = favLatitude;
    }
    public String getFavLongitude() {
        return favLongitude;
    }
    public void setFavLongitude(String favLongitude) {
        this.favLongitude = favLongitude;
    }
    public String getFavDoctype() {
        return favDoctype;
    }
    public void setFavDoctype(String favDoctype) {
        this.favDoctype = favDoctype;
    }
    public String getFavPic() {
        return favPic;
    }
    public void setFavPic(String favPic) {
        this.favPic = favPic;
    }
    public String getFavRating() {
        return favRating;
    }
    public void setFavRating(String favRating) {
        this.favRating = favRating;
    }
    public String getFavSaved() {
        return favSaved;
    }
    public void setFavSaved(String favSaved) {
        this.favSaved = favSaved;
    }
    public int getFavSavedIcon() {
        return favSavedIcon;
    }
    public void setFavSavedIcon(int favSavedIcon) {
        this.favSavedIcon = favSavedIcon;
    }
    public String getFavSecondPic() {
        return favSecondPic;
    }
    public void setFavSecondPic(String favSecondPic) {
        this.favSecondPic = favSecondPic;
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
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
    
    
}
