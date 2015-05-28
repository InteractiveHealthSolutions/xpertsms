// default package
// Generated Dec 30, 2010 12:44:19 PM by Hibernate Tools 3.4.0.Beta1

package com.ihsinformatics.xpertsmsweb.shared.model;

/**
 * Contact generated by hbm2java
 */
public class OtherMessageSetting implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8166994625522457654L;

	private String id;
	private String name;
	private String districtId;
	private String healthFacilityId;
	private String cellNumber;

	public OtherMessageSetting() {
		// Not implemented
	}

	public OtherMessageSetting(String id) {
		this.id = id;
	}

	public OtherMessageSetting(String id, String name, String districtId,
			String healthFacilityId, String cellNumber) {
		this.id = id;
		this.name = name;
		this.districtId = districtId;
		this.healthFacilityId = healthFacilityId;
		this.cellNumber = cellNumber;
	}

	public String getId() {
		return this.id;
	}

	public void setPid(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistrictId() {
		return this.districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public String getHealthFacilityId() {
		return this.healthFacilityId;
	}

	public void setHealthFacilityId(String healthFacilityId) {
		this.healthFacilityId = healthFacilityId;
	}

	public String getCellNumber() {
		return this.cellNumber;
	}

	public void setCellNumber(String cellNumber) {
		this.cellNumber = cellNumber;
	}

	@Override
	public String toString() {
		return id + ", " + name + ", " + districtId + ", " + healthFacilityId
				+ ", " + cellNumber;
	}

}
