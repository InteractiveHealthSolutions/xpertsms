// default package
// Generated Dec 30, 2010 12:44:19 PM by Hibernate Tools 3.4.0.Beta1

package com.ihsinformatics.xpertsmsweb.shared.model;

/**
 * Location generated by hbm2java
 */
public class Location implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 961228589044922151L;

	private String locationId;
	private String locationName;
	private String locationType;
	private String addressHouse;
	private String addressStreet;
	private String addressSector;
	private String addressColony;
	private String addressTown;
	private String cityId;
	private Integer countryId;
	private Float addressLocationLat;
	private Float addressLocationLon;
	private String phone;
	private String mobile;
	private String fax;
	private String email;
	private String secondaryPhone;
	private String secondaryMobile;
	private String secondaryFax;
	private String secondaryEmail;
	private String detail;
	private String addressLandMark;

	public Location() {
		// Not implemented
	}

	public Location(String locationId, String locationName, String locationType) {
		this.locationId = locationId;
		this.locationName = locationName;
		this.locationType = locationType;
	}

	public Location(String locationId, String locationName,
			String locationType, String addressHouse, String addressStreet,
			String addressSector, String addressColony, String addressTown,
			String cityId, Integer countryId, Float addressLocationLat,
			Float addressLocationLon, String phone, String mobile, String fax,
			String email, String secondaryPhone, String secondaryMobile,
			String secondaryFax, String secondaryEmail, String detail,
			String addressLandMark) {
		this.locationId = locationId;
		this.locationName = locationName;
		this.locationType = locationType;
		this.addressHouse = addressHouse;
		this.addressStreet = addressStreet;
		this.addressSector = addressSector;
		this.addressColony = addressColony;
		this.addressTown = addressTown;
		this.cityId = cityId;
		this.countryId = countryId;
		this.addressLocationLat = addressLocationLat;
		this.addressLocationLon = addressLocationLon;
		this.phone = phone;
		this.mobile = mobile;
		this.fax = fax;
		this.email = email;
		this.secondaryPhone = secondaryPhone;
		this.secondaryMobile = secondaryMobile;
		this.secondaryFax = secondaryFax;
		this.secondaryEmail = secondaryEmail;
		this.detail = detail;
		this.addressLandMark = addressLandMark;
	}

	public String getLocationId() {
		return this.locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationType() {
		return this.locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getAddressHouse() {
		return this.addressHouse;
	}

	public void setAddressHouse(String addressHouse) {
		this.addressHouse = addressHouse;
	}

	public String getAddressStreet() {
		return this.addressStreet;
	}

	public void setAddressStreet(String addressStreet) {
		this.addressStreet = addressStreet;
	}

	public String getAddressSector() {
		return this.addressSector;
	}

	public void setAddressSector(String addressSector) {
		this.addressSector = addressSector;
	}

	public String getAddressColony() {
		return this.addressColony;
	}

	public void setAddressColony(String addressColony) {
		this.addressColony = addressColony;
	}

	public String getAddressTown() {
		return this.addressTown;
	}

	public void setAddressTown(String addressTown) {
		this.addressTown = addressTown;
	}

	public String getCityId() {
		return this.cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public Integer getCountryId() {
		return this.countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Float getAddressLocationLat() {
		return this.addressLocationLat;
	}

	public void setAddressLocationLat(Float addressLocationLat) {
		this.addressLocationLat = addressLocationLat;
	}

	public Float getAddressLocationLon() {
		return this.addressLocationLon;
	}

	public void setAddressLocationLon(Float addressLocationLon) {
		this.addressLocationLon = addressLocationLon;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSecondaryPhone() {
		return this.secondaryPhone;
	}

	public void setSecondaryPhone(String secondaryPhone) {
		this.secondaryPhone = secondaryPhone;
	}

	public String getSecondaryMobile() {
		return this.secondaryMobile;
	}

	public void setSecondaryMobile(String secondaryMobile) {
		this.secondaryMobile = secondaryMobile;
	}

	public String getSecondaryFax() {
		return this.secondaryFax;
	}

	public void setSecondaryFax(String secondaryFax) {
		this.secondaryFax = secondaryFax;
	}

	public String getSecondaryEmail() {
		return this.secondaryEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAddressLandMark() {
		return this.addressLandMark;
	}

	public void setAddressLandMark(String addressLandMark) {
		this.addressLandMark = addressLandMark;
	}

	@Override
	public String toString() {
		return locationId + ", " + locationName + ", " + locationType + ", "
				+ addressHouse + ", " + addressStreet + ", " + addressSector
				+ ", " + addressColony + ", " + addressTown + ", " + cityId
				+ ", " + countryId + ", " + addressLocationLat + ", "
				+ addressLocationLon + ", " + phone + ", " + mobile + ", "
				+ fax + ", " + email + ", " + secondaryPhone + ", "
				+ secondaryMobile + ", " + secondaryFax + ", " + secondaryEmail
				+ ", " + detail + ", " + addressLandMark;
	}
}
