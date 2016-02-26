package com.medinfi.datainfo;

import java.io.Serializable;

public class SearchInfo implements Serializable {

	private String searchName;
	private String searchId;
	private String latitude;
	private String longitude;
	private String localityAddress;
	

	public SearchInfo() {
		// TODO Auto-generated constructor stub
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

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getLocalityAddress() {
	    return localityAddress;
	}

	public void setLocalityAddress(String localityAddress) {
	    this.localityAddress = localityAddress;
	}
	
}
