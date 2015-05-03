package com.ihsinformatics.xpertsmsweb.shared.model;

// default package
// Generated Dec 21, 2010 3:45:59 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;

/**
 * Encounter generated by hbm2java
 */
public class Encounter implements java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 4709944064176167388L;
    private EncounterId id;
    private String encounterType;
    private String locationId;
    private Date dateEncounterStart;
    private Date dateEncounterEnd;
    private Date dateEncounterEntered;
    private String details;

    public Encounter() {
	// Not implemented
    }

    public Encounter(EncounterId id, String encounterType,
	    Date dateEncounterStart, Date dateEncounterEnd,
	    Date dateEncounterEntered) {
	this.id = id;
	this.encounterType = encounterType;
	this.dateEncounterStart = dateEncounterStart;
	this.dateEncounterEnd = dateEncounterEnd;
	this.dateEncounterEntered = dateEncounterEntered;
    }

    public Encounter(EncounterId id, String encounterType, String locationId,
	    Date dateEncounterStart, Date dateEncounterEnd,
	    Date dateEncounterEntered, String details) {
	this.id = id;
	this.encounterType = encounterType;
	this.locationId = locationId;
	this.dateEncounterStart = dateEncounterStart;
	this.dateEncounterEnd = dateEncounterEnd;
	this.dateEncounterEntered = dateEncounterEntered;
	this.details = details;
    }

    public EncounterId getId() {
	return this.id;
    }

    public void setId(EncounterId id) {
	this.id = id;
    }

    public String getEncounterType() {
	return this.encounterType;
    }

    public void setEncounterType(String encounterType) {
	this.encounterType = encounterType;
    }

    public String getLocationId() {
	return this.locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public Date getDateEncounterStart() {
	return this.dateEncounterStart;
    }

    public void setDateEncounterStart(Date dateEncounterStart) {
	this.dateEncounterStart = dateEncounterStart;
    }

    public Date getDateEncounterEnd() {
	return this.dateEncounterEnd;
    }

    public void setDateEncounterEnd(Date dateEncounterEnd) {
	this.dateEncounterEnd = dateEncounterEnd;
    }

    /**
     * @return the dateEncounterEntered
     */
    public Date getDateEncounterEntered() {
	return this.dateEncounterEntered;
    }

    /**
     * @param dateEncounterEntered
     *            the dateEncounterEntered to set
     */
    public void setDateEncounterEntered(Date dateEncounterEntered) {
	this.dateEncounterEntered = dateEncounterEntered;
    }

    public String getDetails() {
	return this.details;
    }

    public void setDetails(String details) {
	this.details = details;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return this.id + ", " + this.encounterType + ", " + this.locationId
		+ ", " + this.dateEncounterStart + ", " + this.dateEncounterEnd
		+ ", " + this.dateEncounterEntered + ", " + this.details;
    }

}