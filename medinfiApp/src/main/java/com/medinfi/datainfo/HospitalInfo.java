package com.medinfi.datainfo;

import java.io.Serializable;
import java.util.ArrayList;

public class HospitalInfo implements Serializable {
	
	private String hospitalID;
	private String hospitalName;
	private String hospitalPhoneNumber;
	private String hospitalEmail;
	private String hospitalAddress;
	private String hospitalSpeciality;
	private String hospitalRating;
	private String hospitalDistance;
	private String hospitalImage;
	
	private String hospitalLandmark;
	private String hospitalAlternateEmail;
	private String hospitalext1;
	private String hospitalext2;
	private String hospitalAlternatephone1;
	private String hospitalAlternatephone2;
	private String hospitalFax;
	private String hospitalWebsite;
	private String hospitalDescription;

	private String hospitalCity;
	private String hospitalState;
	private String hospitalPincode;
	
	private ArrayList<String> awardsArrayList;
	private ArrayList<String> memberShipArrayList;
	
	private String hospitalFees;
	private String hospitalWaittime;
	private String hospitalAttitude;
	private String hospitalSatisfaction;
	private String hospitalLocality;
	private String hospitalSaved;
	private String hospitalRated;
	
	private String hospitalBedsNumber;
	private String hospitalEmergencyServices;
	private String hospitalICUServices;
	private String hospitalNICUServices;
	private String hospitalAmbulanceNumber;
	private String hospitalParking;
	
	private String hospitalLat;
	private String hospitalLong;
	private String hospitalRecommendCount;
	private String hospitalNonRecommendCount;
	private String hospitalEmergencyPic;
	private String hospitalLeftPic;
	private String hospitalRightPic;
	
	
	private ArrayList<ReviewsInfo> reviewArrayList;
	private ArrayList<DoctorDetailInfo> doctorArrayList;
	private boolean addedToFilteredList=false;
	
	public HospitalInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public String getHospitalDescription() {
		return hospitalDescription;
	}



	public void setHospitalDescription(String hospitalDescription) {
		this.hospitalDescription = hospitalDescription;
	}



	public String getHospitalCity() {
		return hospitalCity;
	}



	public void setHospitalCity(String hospitalCity) {
		this.hospitalCity = hospitalCity;
	}



	public String getHospitalState() {
		return hospitalState;
	}



	public void setHospitalState(String hospitalState) {
		this.hospitalState = hospitalState;
	}



	public String getHospitalPincode() {
		return hospitalPincode;
	}



	public void setHospitalPincode(String hospitalPincode) {
		this.hospitalPincode = hospitalPincode;
	}



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


	public String getHospitalID() {
		return hospitalID;
	}
	public void setHospitalID(String hospitalID) {
		this.hospitalID = hospitalID;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	
	public String getHospitalRating() {
		return hospitalRating;
	}
	public void setHospitalRating(String hospitalRating) {
		this.hospitalRating = hospitalRating;
	}
	public String getHospitalDistance() {
		return hospitalDistance;
	}
	public void setHospitalDistance(String hospitalDistance) {
		this.hospitalDistance = hospitalDistance;
	}
	public String getHospitalPhoneNumber() {
		return hospitalPhoneNumber;
	}
	public void setHospitalPhoneNumber(String hospitalPhoneNumber) {
		this.hospitalPhoneNumber = hospitalPhoneNumber;
	}
	public String getHospitalImage() {
		return hospitalImage;
	}
	public void setHospitalImage(String hospitalImage) {
		this.hospitalImage = hospitalImage;
	}
	public String getHospitalEmail() {
		return hospitalEmail;
	}
	public void setHospitalEmail(String hospitalEmail) {
		this.hospitalEmail = hospitalEmail;
	}
	public String getHospitalAddress() {
		return hospitalAddress;
	}
	public void setHospitalAddress(String hospitalAddress) {
		this.hospitalAddress = hospitalAddress;
	}
	public String getHospitalSpeciality() {
		return hospitalSpeciality;
	}
	public void setHospitalSpeciality(String hospitalSpeciality) {
		this.hospitalSpeciality = hospitalSpeciality;
	}
	public String getHospitalFees() {
		return hospitalFees;
	}


	public void setHospitalFees(String hospitalFees) {
		this.hospitalFees = hospitalFees;
	}


	public String getHospitalWaittime() {
		return hospitalWaittime;
	}


	public void setHospitalWaittime(String hospitalWaittime) {
		this.hospitalWaittime = hospitalWaittime;
	}


	public String getHospitalAttitude() {
		return hospitalAttitude;
	}


	public void setHospitalAttitude(String hospitalAttitude) {
		this.hospitalAttitude = hospitalAttitude;
	}


	public String getHospitalSatisfaction() {
		return hospitalSatisfaction;
	}


	public void setHospitalSatisfaction(String hospitalSatisfaction) {
		this.hospitalSatisfaction = hospitalSatisfaction;
	}


	public ArrayList<ReviewsInfo> getReviewArrayList() {
		return reviewArrayList;
	}


	public void setReviewArrayList(ArrayList<ReviewsInfo> reviewArrayList) {
		this.reviewArrayList = reviewArrayList;
	}


	public ArrayList<DoctorDetailInfo> getDoctorArrayList() {
		return doctorArrayList;
	}


	public void setDoctorArrayList(ArrayList<DoctorDetailInfo> doctorArrayList) {
		this.doctorArrayList = doctorArrayList;
	}


	public String getHospitalLandmark() {
		return hospitalLandmark;
	}


	public void setHospitalLandmark(String hospitalLandmark) {
		this.hospitalLandmark = hospitalLandmark;
	}


	public String getHospitalAlternateEmail() {
		return hospitalAlternateEmail;
	}


	public void setHospitalAlternateEmail(String hospitalAlternateEmail) {
		this.hospitalAlternateEmail = hospitalAlternateEmail;
	}


	public String getHospitalext1() {
		return hospitalext1;
	}


	public void setHospitalext1(String hospitalext1) {
		this.hospitalext1 = hospitalext1;
	}


	public String getHospitalext2() {
		return hospitalext2;
	}


	public void setHospitalext2(String hospitalext2) {
		this.hospitalext2 = hospitalext2;
	}


	public String getHospitalAlternatephone1() {
		return hospitalAlternatephone1;
	}


	public void setHospitalAlternatephone1(String hospitalAlternatephone1) {
		this.hospitalAlternatephone1 = hospitalAlternatephone1;
	}


	public String getHospitalAlternatephone2() {
		return hospitalAlternatephone2;
	}


	public void setHospitalAlternatephone2(String hospitalAlternatephone2) {
		this.hospitalAlternatephone2 = hospitalAlternatephone2;
	}


	public String getHospitalFax() {
		return hospitalFax;
	}


	public void setHospitalFax(String hospitalFax) {
		this.hospitalFax = hospitalFax;
	}


	public String getHospitalWebsite() {
		return hospitalWebsite;
	}


	public void setHospitalWebsite(String hospitalWebsite) {
		this.hospitalWebsite = hospitalWebsite;
	}



	public String getHospitalLocality() {
	    return hospitalLocality;
	}



	public void setHospitalLocality(String hospitalLocality) {
	    this.hospitalLocality = hospitalLocality;
	}



	public String getHospitalSaved() {
	    return hospitalSaved;
	}



	public void setHospitalSaved(String hospitalSaved) {
	    this.hospitalSaved = hospitalSaved;
	}



	public String getHospitalRated() {
	    return hospitalRated;
	}



	public void setHospitalRated(String hospitalRated) {
	    this.hospitalRated = hospitalRated;
	}



	public boolean isAddedToFilteredList() {
	    return addedToFilteredList;
	}



	public void setAddedToFilteredList(boolean addedToFilteredList) {
	    this.addedToFilteredList = addedToFilteredList;
	}

	public String getHospitalBedsNumber() {
	    return hospitalBedsNumber;
	}
	
	public void setHospitalBedsNumber(String hospitalBedsNumber) {
	    this.hospitalBedsNumber = hospitalBedsNumber;
	}
	
	public String getHospitalEmergencyServices() {
	    return hospitalEmergencyServices;
	}
	
	public void setHospitalEmergencyServices(String hospitalEmergencyServices) {
	    this.hospitalEmergencyServices = hospitalEmergencyServices;
	}
	
	public String getHospitalICUServices() {
	    return hospitalICUServices;
	}
	
	public void setHospitalICUServices(String hospitalICUServices) {
	    this.hospitalICUServices = hospitalICUServices;
	}
	public String getHospitalNICUServices() {
	    return hospitalNICUServices;
	}
	
	public void setHospitalNICUServices(String hospitalNICUServices) {
	    this.hospitalNICUServices = hospitalNICUServices;
	}
	
	public String getHospitalAmbulanceNumber() {
	    return hospitalAmbulanceNumber;
	}
	
	public void setHospitalAmbulanceNumber(String hospitalAmbulanceNumber) {
	    this.hospitalAmbulanceNumber = hospitalAmbulanceNumber;
	}
	
	public String getHospitalParking() {
	    return hospitalParking;
	}

	public void setHospitalParking(String hospitalParking) {
	    this.hospitalParking = hospitalParking;
	}



	public String getHospitalLat() {
	    return hospitalLat;
	}



	public void setHospitalLat(String hospitalLat) {
	    this.hospitalLat = hospitalLat;
	}
	
	public String getHospitalLong() {
	    return hospitalLong;
	}

	public void setHospitalLong(String hospitalLong) {
	    this.hospitalLong = hospitalLong;
	}



	public String getHospitalRecommendCount() {
	    return hospitalRecommendCount;
	}



	public void setHospitalRecommendCount(String hospitalRecommendCount) {
	    this.hospitalRecommendCount = hospitalRecommendCount;
	}



	public String getHospitalNonRecommendCount() {
	    return hospitalNonRecommendCount;
	}



	public void setHospitalNonRecommendCount(String hospitalNonRecommendCount) {
	    this.hospitalNonRecommendCount = hospitalNonRecommendCount;
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
	
	
}
