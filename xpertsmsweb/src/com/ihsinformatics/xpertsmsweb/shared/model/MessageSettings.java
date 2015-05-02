package com.ihsinformatics.xpertsmsweb.shared.model;

// default package
// Generated Dec 21, 2010 3:45:59 PM by Hibernate Tools 3.4.0.Beta1

/**
 * Incentive generated by hbm2java
 */
public class MessageSettings implements java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -335445919499188798L;
    /**
	 * 
	 */

    private Integer settingsId;
    private String patientText;
    private String providerText;
    private String programText;
    private String otherText;
    private Boolean sendToPatient;
    private Boolean sendToProvider;
    private Boolean sendToProgram;
    private Boolean sendToOther;
    private String programNumber;
    private String otherNumber;

    /*
     * private String tbrServerAddress; private String tbrServerPort;
     */

    public MessageSettings() {
	// Not implemented
	patientText = null;
	providerText = null;
	programText = null;
	otherText = null;
    }

    /**
     * @return the settingsId
     */
    public Integer getSettingsId() {
	return settingsId;
    }

    /**
     * @param settingsId
     *            the settingsId to set
     */
    public void setSettingsId(Integer settingsId) {
	this.settingsId = settingsId;
    }

    /**
     * @return the patientText
     */
    public String getPatientText() {
	return patientText;
    }

    /**
     * @param patientText
     *            the patientText to set
     */
    public void setPatientText(String patientText) {
	this.patientText = patientText;
    }

    /**
     * @return the providerText
     */
    public String getProviderText() {
	return providerText;
    }

    /**
     * @param providerText
     *            the providerText to set
     */
    public void setProviderText(String providerText) {
	this.providerText = providerText;
    }

    /**
     * @return the programText
     */
    public String getProgramText() {
	return programText;
    }

    /**
     * @param programText
     *            the programText to set
     */
    public void setProgramText(String programText) {
	this.programText = programText;
    }

    /**
     * @return the otherText
     */
    public String getOtherText() {
	return otherText;
    }

    /**
     * @param otherText
     *            the otherText to set
     */
    public void setOtherText(String otherText) {
	this.otherText = otherText;
    }

    /**
     * @return the sendToPatient
     */
    public Boolean getSendToPatient() {
	return sendToPatient;
    }

    /**
     * @param sendToPatient
     *            the sendToPatient to set
     */
    public void setSendToPatient(Boolean sendToPatient) {
	this.sendToPatient = sendToPatient;
    }

    /**
     * @return the sendToProvider
     */
    public Boolean getSendToProvider() {
	return sendToProvider;
    }

    /**
     * @param sendToProvider
     *            the sendToProvider to set
     */
    public void setSendToProvider(Boolean sendToProvider) {
	this.sendToProvider = sendToProvider;
    }

    /**
     * @return the sendToProgram
     */
    public Boolean getSendToProgram() {
	return sendToProgram;
    }

    /**
     * @param sendToProgram
     *            the sendToProgram to set
     */
    public void setSendToProgram(Boolean sendToProgram) {
	this.sendToProgram = sendToProgram;
    }

    /**
     * @return the sendToOther
     */
    public Boolean getSendToOther() {
	return sendToOther;
    }

    /**
     * @param sendToOther
     *            the sendToOther to set
     */
    public void setSendToOther(Boolean sendToOther) {
	this.sendToOther = sendToOther;
    }

    /**
     * @return the programNumber
     */
    public String getProgramNumber() {
	return programNumber;
    }

    /**
     * @param programNumber
     *            the programNumber to set
     */
    public void setProgramNumber(String programNumber) {
	this.programNumber = programNumber;
    }

    /**
     * @return the otherNumber
     */
    public String getOtherNumber() {
	return otherNumber;
    }

    /**
     * @param otherNumber
     *            the otherNumber to set
     */
    public void setOtherNumber(String otherNumber) {
	this.otherNumber = otherNumber;
    }

    /**
     * @return the tbrServerAddress
     */
    /*
     * public String getTbrServerAddress() { return tbrServerAddress; }
     *//**
     * @param tbrServerAddress
     *            the tbrServerAddress to set
     */
    /*
     * public void setTbrServerAddress(String tbrServerAddress) {
     * this.tbrServerAddress = tbrServerAddress; }
     *//**
     * @return the tbrServerPort
     */
    /*
     * public String getTbrServerPort() { return tbrServerPort; }
     *//**
     * @param tbrServerPort
     *            the tbrServerPort to set
     */
    /*
     * public void setTbrServerPort(String tbrServerPort) { this.tbrServerPort =
     * tbrServerPort; }
     */

}
