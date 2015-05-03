package com.ihsinformatics.xpertsmsweb.shared.model;

// Generated Dec 1, 2011 4:10:50 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;

/**
 * Patient generated by hbm2java
 */
public class Patient implements java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1892494241230445127L;
    private String patientId;
    private String screenerId;
    private String providerId;
    private String externalMrno;
    private Float weight;
    private Float height;
    private String bloodGroup;
    private Date dateRegistered;
    private String treatmentSupporter;
    private String treatmentCenter;
    private Boolean diseaseSuspected;
    private Boolean diseaseConfirmed;
    private String diseaseCategory;
    private String diseaseSite;
    private String severity;
    private String medicationForm;
    private String regimenType;
    private Float RDose;
    private Float HDose;
    private Float ZDose;
    private Float EDose;
    private String regimen;
    private Float doseCombination;
    private String otherDoseDescription;
    private String treatmentPhase;
    private String patientStatus;
    private String patientType;
    private String diseaseHistory;
    private Boolean treatedPreviously;
    private Boolean completedPreviousTreatment;
    private String fullDescription;

    public Patient() {
	// Not implemented
    }

    public Patient(String patientId) {
	this.patientId = patientId;
    }

    public Patient(String patientId, String screenerId, String providerId,
	    String externalMrno, Float weight, Float height, String bloodGroup,
	    Date dateRegistered, String treatmentSupporter,
	    String treatmentCenter, Boolean diseaseSuspected,
	    Boolean diseaseConfirmed, String diseaseCategory,
	    String diseaseSite, String severity, String medicationForm,
	    String regimenType, Float RDose, Float HDose, Float ZDose,
	    Float EDose, String regimen, Float doseCombination,
	    String otherDoseDescription, String treatmentPhase,
	    String patientStatus, String patientType, String diseaseHistory,
	    Boolean treatedPreviously, Boolean completedPreviousTreatment,
	    String fullDescription) {
	this.patientId = patientId;
	this.screenerId = screenerId;
	this.providerId = providerId;
	this.externalMrno = externalMrno;
	this.weight = weight;
	this.height = height;
	this.bloodGroup = bloodGroup;
	this.dateRegistered = dateRegistered;
	this.treatmentSupporter = treatmentSupporter;
	this.treatmentCenter = treatmentCenter;
	this.diseaseSuspected = diseaseSuspected;
	this.diseaseConfirmed = diseaseConfirmed;
	this.diseaseCategory = diseaseCategory;
	this.diseaseSite = diseaseSite;
	this.severity = severity;
	this.medicationForm = medicationForm;
	this.regimenType = regimenType;
	this.RDose = RDose;
	this.HDose = HDose;
	this.ZDose = ZDose;
	this.EDose = EDose;
	this.regimen = regimen;
	this.doseCombination = doseCombination;
	this.otherDoseDescription = otherDoseDescription;
	this.treatmentPhase = treatmentPhase;
	this.patientStatus = patientStatus;
	this.patientType = patientType;
	this.diseaseHistory = diseaseHistory;
	this.treatedPreviously = treatedPreviously;
	this.completedPreviousTreatment = completedPreviousTreatment;
	this.fullDescription = fullDescription;
    }

    public String getPatientId() {
	return this.patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getScreenerId() {
	return this.screenerId;
    }

    public void setScreenerId(String screenerId) {
	this.screenerId = screenerId;
    }

    public String getProviderId() {
	return this.providerId;
    }

    public void setProviderId(String providerId) {
	this.providerId = providerId;
    }

    public String getExternalMrno() {
	return this.externalMrno;
    }

    public void setExternalMrno(String externalMrno) {
	this.externalMrno = externalMrno;
    }

    public Float getWeight() {
	return this.weight;
    }

    public void setWeight(Float weight) {
	this.weight = weight;
    }

    public Float getHeight() {
	return this.height;
    }

    public void setHeight(Float height) {
	this.height = height;
    }

    public String getBloodGroup() {
	return this.bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public Date getDateRegistered() {
	return this.dateRegistered;
    }

    public void setDateRegistered(Date dateRegistered) {
	this.dateRegistered = dateRegistered;
    }

    public String getTreatmentSupporter() {
	return this.treatmentSupporter;
    }

    public void setTreatmentSupporter(String treatmentSupporter) {
	this.treatmentSupporter = treatmentSupporter;
    }

    public String getTreatmentCenter() {
	return this.treatmentCenter;
    }

    public void setTreatmentCenter(String treatmentCenter) {
	this.treatmentCenter = treatmentCenter;
    }

    public Boolean getDiseaseSuspected() {
	return this.diseaseSuspected;
    }

    public void setDiseaseSuspected(Boolean diseaseSuspected) {
	this.diseaseSuspected = diseaseSuspected;
    }

    public Boolean getDiseaseConfirmed() {
	return this.diseaseConfirmed;
    }

    public void setDiseaseConfirmed(Boolean diseaseConfirmed) {
	this.diseaseConfirmed = diseaseConfirmed;
    }

    public String getDiseaseCategory() {
	return this.diseaseCategory;
    }

    public void setDiseaseCategory(String diseaseCategory) {
	this.diseaseCategory = diseaseCategory;
    }

    public String getDiseaseSite() {
	return this.diseaseSite;
    }

    public void setDiseaseSite(String diseaseSite) {
	this.diseaseSite = diseaseSite;
    }

    public String getSeverity() {
	return this.severity;
    }

    public void setSeverity(String severity) {
	this.severity = severity;
    }

    public String getMedicationForm() {
	return this.medicationForm;
    }

    public void setMedicationForm(String medicationForm) {
	this.medicationForm = medicationForm;
    }

    public String getRegimenType() {
	return this.regimenType;
    }

    public void setRegimenType(String regimenType) {
	this.regimenType = regimenType;
    }

    public Float getRDose() {
	return this.RDose;
    }

    public void setRDose(Float RDose) {
	this.RDose = RDose;
    }

    public Float getHDose() {
	return this.HDose;
    }

    public void setHDose(Float HDose) {
	this.HDose = HDose;
    }

    public Float getZDose() {
	return this.ZDose;
    }

    public void setZDose(Float ZDose) {
	this.ZDose = ZDose;
    }

    public Float getEDose() {
	return this.EDose;
    }

    public void setEDose(Float EDose) {
	this.EDose = EDose;
    }

    public String getRegimen() {
	return this.regimen;
    }

    public void setRegimen(String regimen) {
	this.regimen = regimen;
    }

    public Float getDoseCombination() {
	return this.doseCombination;
    }

    public void setDoseCombination(Float doseCombination) {
	this.doseCombination = doseCombination;
    }

    public String getOtherDoseDescription() {
	return this.otherDoseDescription;
    }

    public void setOtherDoseDescription(String otherDoseDescription) {
	this.otherDoseDescription = otherDoseDescription;
    }

    public String getTreatmentPhase() {
	return this.treatmentPhase;
    }

    public void setTreatmentPhase(String treatmentPhase) {
	this.treatmentPhase = treatmentPhase;
    }

    public String getPatientStatus() {
	return this.patientStatus;
    }

    public void setPatientStatus(String patientStatus) {
	this.patientStatus = patientStatus;
    }

    public String getPatientType() {
	return this.patientType;
    }

    public void setPatientType(String patientType) {
	this.patientType = patientType;
    }

    public String getDiseaseHistory() {
	return this.diseaseHistory;
    }

    public void setDiseaseHistory(String diseaseHistory) {
	this.diseaseHistory = diseaseHistory;
    }

    public Boolean getTreatedPreviously() {
	return this.treatedPreviously;
    }

    public void setTreatedPreviously(Boolean treatedPreviously) {
	this.treatedPreviously = treatedPreviously;
    }

    public Boolean getCompletedPreviousTreatment() {
	return this.completedPreviousTreatment;
    }

    public void setCompletedPreviousTreatment(Boolean completedPreviousTreatment) {
	this.completedPreviousTreatment = completedPreviousTreatment;
    }

    public String getFullDescription() {
	return this.fullDescription;
    }

    public void setFullDescription(String fullDescription) {
	this.fullDescription = fullDescription;
    }

    @Override
    public String toString() {
	return patientId + ", " + screenerId + ", " + providerId + ", "
		+ externalMrno + ", " + weight + ", " + height + ", "
		+ bloodGroup + ", " + dateRegistered + ", "
		+ treatmentSupporter + ", " + treatmentCenter + ", "
		+ diseaseSuspected + ", " + diseaseConfirmed + ", "
		+ diseaseCategory + ", " + diseaseSite + ", " + severity + ", "
		+ medicationForm + ", " + regimenType + ", " + RDose + ", "
		+ HDose + ", " + ZDose + ", " + EDose + ", " + regimen + ", "
		+ doseCombination + ", " + otherDoseDescription + ", "
		+ treatmentPhase + ", " + patientStatus + ", " + patientType
		+ ", " + diseaseHistory + ", " + treatedPreviously + ", "
		+ completedPreviousTreatment + ", " + fullDescription;
    }

}