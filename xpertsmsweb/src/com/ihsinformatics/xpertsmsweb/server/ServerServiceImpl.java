package com.ihsinformatics.xpertsmsweb.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/*import com.ihsinformatics.xpertsmsweb.mobileevent.DateTimeUtil;
 import com.ihsinformatics.xpertsmsweb.mobileevent.ModelUtil;
 import com.ihsinformatics.xpertsmsweb.mobileevent.XmlUtil;*/
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ihsinformatics.xpertsmsweb.client.ServerService;
import com.ihsinformatics.xpertsmsweb.shared.ListType;
import com.ihsinformatics.xpertsmsweb.shared.Parameter;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.model.Contact;
import com.ihsinformatics.xpertsmsweb.shared.model.Encounter;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterId;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterResults;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterResultsId;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResultsUnlinked;
import com.ihsinformatics.xpertsmsweb.shared.model.Location;
import com.ihsinformatics.xpertsmsweb.shared.model.MessageSettings;
import com.ihsinformatics.xpertsmsweb.shared.model.OtherMessageSetting;
import com.ihsinformatics.xpertsmsweb.shared.model.Patient;
import com.ihsinformatics.xpertsmsweb.shared.model.Person;
import com.ihsinformatics.xpertsmsweb.shared.model.SputumResults;
import com.ihsinformatics.xpertsmsweb.shared.model.UserRights;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

/**
 * The server side implementation of the RPC service.
 * 
 * @author owais.hussain@irdresearch.org
 */
@SuppressWarnings("serial")
public class ServerServiceImpl extends RemoteServiceServlet implements
	ServerService {
    private String arrangeFilter(String filter) throws Exception {
	if (filter.trim().equalsIgnoreCase(""))
	    return "";
	return (filter.toUpperCase().contains("WHERE") ? "" : " where ")
		+ filter;
    }

    /**
     * Get full name (first name + middle name + last name + surname) of any
     * Person
     * 
     * @param Person
     *            ID as String
     * @return full name as String
     */
    public String getFullName(String PID) throws Exception {
	if (PID.equals(""))
	    return "";
	return HibernateUtil.util
		.selectObject(
			"select LTRIM(RTRIM(IFNULL(FirstName, '') + ' ' + IFNULL(MiddleName, '') + IFNULL(LastName, '') + IFNULL(Surname, ''))) from Person where PID='"
				+ PID + "'").toString().toUpperCase();
    }

    /**
     * Get Mobile phone number of any Person
     * 
     * @param Person
     *            ID as String
     * @return Mobile number as String
     */
    public String getMobileNumber(String PID) throws Exception {
	if (PID.equals(""))
	    return "";
	return HibernateUtil.util.selectObject(
		"select Mobile from Contact where PID='" + PID + "'")
		.toString();
    }

    /**
     * Sends multiple SMS
     * 
     * @param sms
     */
    /*
     * 
     * public void sendGenericSMSAlert(Sms[] sms) throws Exception { for (Sms s
     * : sms) sendGenericSMSAlert(s); }
     *//**
     * Sends a generic SMS
     * 
     * @param sms
     */
    /*
     * 
     * public void sendGenericSMSAlert(Sms sms) { if
     * (!sms.getTargetNumber().equals("")) HibernateUtil.util.save(sms); }
     *//**
     * Records messages to be send to various roles BUSINESS LOGIC: 1. If
     * isTBPositive is true, then: - Send alert to Monitor, GP and CHW about
     * Patient confirmation - Send alert to CHW, GP about their updated
     * incentive balance
     */
    /*
     * public void sendAlertsOnClinicalDiagnosis(EncounterId encounterId,
     * boolean isTBPositive, SetupIncentive gpIncentive, SetupIncentive
     * chwIncentive) throws Exception { try { Patient patient = (Patient)
     * HibernateUtil.util.findObject("from Patient where PatientID='" +
     * encounterId.getPid1() + "'"); // Get IDs String[] IDs =
     * getRowRecord("Patient", new String[] { "MonitorID", "GPID", "CHWID" },
     * "PatientID='" + patient.getPatientId() + "'"); String monitorID = IDs[0];
     * String GPID = IDs[1]; String CHWID = IDs[2]; // Get mobile numbers String
     * monitorMobile = getMobileNumber(monitorID); String GPMobile =
     * getMobileNumber(GPID); String CHWMobile = null;
     * 
     * if (CHWID != null) CHWMobile = getMobileNumber(CHWID); // Set SMS param
     * String targetNumber = ""; String messageText = ""; Date dueDateTime = new
     * Date(); String status = "PENDING";
     * 
     * if (isTBPositive) { // Send alert to Monitor, GP and CHW about Patient
     * confirmation targetNumber = monitorMobile; messageText =
     * "Dear Monitor! Suspect: " + patient.getPatientId() +
     * " has been confirmed as Patient based on clinical diagnosis. GP attending: "
     * + GPID; sendGenericSMSAlert(new Sms(targetNumber, messageText,
     * dueDateTime, null, status, null, null)); targetNumber = GPMobile;
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null));
     * 
     * if (CHWMobile != null) { targetNumber = CHWMobile; messageText =
     * "Dear Health worker! Suspect: " + patient.getPatientId() +
     * " has been confirmed as Patient based on clinical diagnosis. GP attending: "
     * + GPID; sendGenericSMSAlert(new Sms(targetNumber, messageText,
     * dueDateTime, null, status, null, null)); } // Send alert to CHW, GP about
     * their updated incentive balance targetNumber = GPMobile; messageText =
     * "Dear GP! You have been incentivised amount of " +
     * gpIncentive.getCurrency() + gpIncentive.getAmount() +
     * " on clinical confirmation of Suspect: " + patient.getPatientId();
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null));
     * 
     * if (CHWMobile != null) { targetNumber = CHWMobile; messageText =
     * "Dear Health worker! You have been incentivised amount of " +
     * chwIncentive.getCurrency() + chwIncentive.getAmount() +
     * " on clinical confirmation of Suspect: " + patient.getPatientId();
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); } } }
     * 
     * catch (Exception e) { e.printStackTrace(); } }
     *//**
     * Records messages to be sent to various roles BUSINESS LOGIC: 1. If the
     * suspect is not confirmed, then: - Do nothing 2. If the suspect is
     * confirmed, then: - Send alert to Monitor
     */
    /*
     * public void sendAlertsOnGPConfirmation(EncounterId encounterID) throws
     * Exception { try { Patient patient = (Patient)
     * HibernateUtil.util.findObject("from Patient where PatientID='" +
     * encounterID.getPid1() + "'");
     * 
     * Object[] obj = HibernateUtil.util.selectObjects(
     * "select MonitorID from GPMapping where GPID='" + encounterID.getPid2() +
     * "'"); String[] monitorIDs = new String[obj.length]; for (int i = 0; i <
     * obj.length; i++) monitorIDs[i] = obj[i].toString(); if
     * (patient.getPatientStatus().equalsIgnoreCase("GP_CONF")) { String
     * targetNumber = ""; String messageText = ""; Date dueDateTime = new
     * Date(); String status = "PENDING"; for (int i = 0; i < monitorIDs.length;
     * i++) { String monitorMobile = getMobileNumber(monitorIDs[i]);
     * targetNumber = monitorMobile; messageText = "Alert! Patient " +
     * patient.getPatientId() + " confirmed as suspect by GP " +
     * encounterID.getPid2(); HibernateUtil.util.save(new Sms(targetNumber,
     * messageText, dueDateTime, null, status, null, null)); } } } catch
     * (Exception e) { e.printStackTrace(); } }
     *//**
     * Records messages to be sent to various roles BUSINESS LOGIC: - Send
     * alert to Monitor - Send alert to GP about their updated incentive balance
     */
    /*
     * public void sendAlertsOnGPVisit(Encounter encounter, SetupIncentive
     * gpIncentive) throws Exception { try { Patient patient = (Patient)
     * HibernateUtil.util.findObject("from Patient where PatientID='" +
     * encounter.getId().getPid1() + "'");
     * 
     * Object[] obj = HibernateUtil.util.selectObjects(
     * "select MonitorID from GPMapping where GPID='" +
     * encounter.getId().getPid2() + "'"); String[] monitorIDs = new
     * String[obj.length]; for (int i = 0; i < obj.length; i++) monitorIDs[i] =
     * obj[i].toString();
     * 
     * String targetNumber = ""; String messageText = ""; Date dueDateTime = new
     * Date(); String status = "PENDING"; Incentive incentive = new Incentive();
     * 
     * for (int i = 0; i < monitorIDs.length; i++) { String monitorMobile =
     * getMobileNumber(monitorIDs[i]); targetNumber = monitorMobile; messageText
     * = "Alert! Patient/Suspect " + patient.getPatientId() + " visited GP " +
     * encounter.getId().getPid2() + " on " + encounter.getDateEncounterStart();
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); } messageText =
     * "Congratulations! You have been incentivised amount of " +
     * gpIncentive.getAmount() + " on visit of Patient/Suspect " +
     * patient.getPatientId(); targetNumber =
     * getMobileNumber(encounter.getId().getPid2()); incentive = new
     * Incentive(new IncentiveId(encounter.getId().getPid2(),
     * gpIncentive.getIncentiveId(), 0), "", new Date(), status,
     * "Original message sent: " + messageText);
     * HibernateUtil.util.save(incentive); sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null)); }
     * catch (Exception e) { e.printStackTrace(); } }
     *//**
     * Records messages to be sent to various roles BUSINESS LOGIC: 1. If
     * isCured is true - Send alert to whole population - Send alert to GP and
     * CHW about their updated incentive balance 2. If isCured is false and
     * isTreatmentCompleted is true - Send alert to Monitor - Send alert to GP
     * and CHW about their updated incentive balance 3. If isCured is false and
     * isTreatmentCompleted is false - Send alert to Monitor
     */
    /*
     * public void sendAlertsOnEndFollowUp(EncounterId encounterID,
     * SetupIncentive GPIncentive, SetupIncentive CHWIncentive, boolean isCured,
     * boolean isTreatmentCompleted) throws Exception { try { Patient patient =
     * (Patient) HibernateUtil.util.findObject("from Patient where PatientID='"
     * + encounterID.getPid1() + "'");
     * 
     * String[] monitorIDs = (String[]) HibernateUtil.util.selectObjects(
     * "select MonitorID from GPMapping where GPID='" + encounterID.getPid2() +
     * "'"); Monitor[] monitors = new Monitor[monitorIDs.length]; for (int i =
     * 0; i < monitorIDs.length; i++) { monitors[i] = (Monitor)
     * HibernateUtil.util.selectObject("from Monitor where MonitorID='" +
     * monitorIDs[i] + "'"); }
     * 
     * String targetNumber = ""; String messageText = ""; Date dueDateTime = new
     * Date(); String status = "PENDING";
     * 
     * String[] monitorMobile = new String[monitorIDs.length]; for (int i = 0; i
     * < monitorIDs.length; i++) { monitorMobile[i] =
     * getMobileNumber(monitorIDs[i]); } String GPMobile =
     * getMobileNumber(patient.getProviderId()); String CHWMobile =
     * getMobileNumber(patient.getScreenerId());
     * 
     * if (isCured) { // Send alerts messageText = "Congratulations! Patient " +
     * patient.getPatientId() + " has been cured successfully"; String[]
     * supervisorMobiles = (String[]) HibernateUtil.util.selectObjects(
     * "select Mobile from Contact where PID LIKE 'S%'"); for (int i = 0; i <
     * supervisorMobiles.length; i++) { targetNumber = supervisorMobiles[i];
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); } for (int i = 0; i < monitorMobile.length; i++) {
     * targetNumber = monitorMobile[i]; sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null)); }
     * targetNumber = GPMobile; sendGenericSMSAlert(new Sms(targetNumber,
     * messageText, dueDateTime, null, status, null, null)); targetNumber =
     * CHWMobile; sendGenericSMSAlert(new Sms(targetNumber, messageText,
     * dueDateTime, null, status, null, null)); // Send incentive alerts
     * messageText = "Congratulations! You have been incentivised amount of " +
     * GPIncentive.getAmount() + " on successful treatment of Patient " +
     * patient.getPatientId(); targetNumber = GPMobile; sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null));
     * messageText = "Congratulations! You have been incentivised amount of " +
     * CHWIncentive.getAmount() + " on successful treatment of Patient " +
     * patient.getPatientId(); targetNumber = CHWMobile; sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null)); }
     * else if (!isCured && isTreatmentCompleted) { // Send alerts messageText =
     * "Alert! Patient " + patient.getPatientId() + " has completed treatment";
     * for (int i = 0; i < monitorMobile.length; i++) { targetNumber =
     * monitorMobile[i]; sendGenericSMSAlert(new Sms(targetNumber, messageText,
     * dueDateTime, null, status, null, null)); } targetNumber = GPMobile;
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); targetNumber = CHWMobile; sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null));
     * // Send incentive alerts messageText =
     * "Congratulations! You have been incentivised amount of " +
     * GPIncentive.getAmount() + " on treatment completion of Patient " +
     * patient.getPatientId(); targetNumber = GPMobile; sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null));
     * messageText = "Congratulations! You have been incentivised amount of " +
     * CHWIncentive.getAmount() + " on treatment completion of Patient " +
     * patient.getPatientId(); targetNumber = CHWMobile; sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null)); }
     * else if (!isCured && !isTreatmentCompleted) { // Send alerts messageText
     * = "Alert! Treatment of Patient " + patient.getPatientId() +
     * " was incomplete"; for (int i = 0; i < monitorMobile.length; i++) {
     * targetNumber = monitorMobile[i]; sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null)); }
     * } } catch (Exception e) { e.printStackTrace(); } }
     *//**
     * Records messages to be sent to various roles BUSINESS LOGIC: 1. If
     * isTBPositive is true, then: - Send alert to Monitor, GP and CHW about
     * Patient confirmation - Send alert to CHW, GP about their updated
     * incentive balance 2. If isTBPositive is false (suspect with result
     * negative), then: - Send alert to Monitor about negative results
     */
    /*
     * public void sendAlertsOnGeneXpertResults(GeneXpertResults
     * geneXpertResults, boolean isTBPositive, SetupIncentive gpIncentive,
     * SetupIncentive chwIncentive) throws Exception { try { Patient patient =
     * (Patient) HibernateUtil.util.findObject("from Patient where PatientID='"
     * + geneXpertResults.getPatientId() + "'");
     * 
     * // Get IDs String[] IDs = getRowRecord("Patient", new String[] {
     * "MonitorID", "GPID", "CHWID" }, "PatientID='" + patient.getPatientId() +
     * "'"); String monitorID = IDs[0].toString(); String GPID =
     * IDs[1].toString(); String CHWID = IDs[2].toString(); // Get mobile
     * numbers String monitorMobile = getMobileNumber(monitorID); String
     * GPMobile = getMobileNumber(GPID); String CHWMobile =
     * getMobileNumber(CHWID); // Set SMS param String targetNumber = ""; String
     * messageText = ""; Date dueDateTime = new Date(); String status =
     * "PENDING"; Incentive incentive = new Incentive();
     * 
     * try { if (isTBPositive) { try { if (patient.getDiseaseConfirmed()) { //
     * Do nothing } else throw new Exception(); } catch (Exception e) {
     * 
     * 
     * // Send alert to Monitor, GP and CHW about Patient confirmation
     * targetNumber = monitorMobile; messageText = "Dear Monitor! Suspect: " +
     * patient.getPatientId() + " has been confirmed as Patient. GP attending: "
     * + GPID; sendGenericSMSAlert(new Sms(targetNumber, messageText,
     * dueDateTime, null, status, null, null)); targetNumber = GPMobile;
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); targetNumber = CHWMobile; messageText =
     * "Dear Health worker! Suspect: " + patient.getPatientId() +
     * " has been confirmed as Patient. GP attending: " + GPID;
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); // Send alert to CHW, GP about their updated
     * incentive balance targetNumber = GPMobile; messageText =
     * "Dear Treatment Provider! You have been incentivised amount of " +
     * gpIncentive.getCurrency() + gpIncentive.getAmount() +
     * " on confirmation of Suspect: " + patient.getPatientId(); incentive = new
     * Incentive(new IncentiveId(GPID, gpIncentive.getIncentiveId(), 0), "", new
     * Date(), status, "Original message sent: " + messageText);
     * HibernateUtil.util.save(incentive); sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null));
     * targetNumber = CHWMobile; messageText =
     * "Dear Health worker! You have been incentivised amount of " +
     * chwIncentive.getCurrency() + chwIncentive.getAmount() +
     * " on confirmation of Suspect: " + patient.getPatientId(); incentive = new
     * Incentive(new IncentiveId(CHWID, chwIncentive.getIncentiveId(), 0), "",
     * new Date(), status, "Original message sent: " + messageText);
     * HibernateUtil.util.save(incentive); sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null)); }
     * } else { // Send alert to Monitor targetNumber = monitorMobile;
     * messageText = "Dear Monitor! Gene Xpert Results for Suspect: " +
     * patient.getPatientId() + "  were found NEGATIVE.";
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); } } catch (Exception e) { e.printStackTrace(); } }
     * catch (Exception e) { e.printStackTrace(); } }
     */

    /**
     * Records messages to be sent to various roles BUSINESS LOGIC: 1. If
     * Suspect in screening is true, then: - Send alert to Monitor and CHW about
     * Suspect confirmation
     */
    /*
     * public void sendAlertsOnScreening(Screening screening) { if
     * (screening.getSuspect()) { try { screening.getChwid();
     * screening.getPatientId(); // Get IDs String monitorID =
     * getObject("Worker", "MonitorID", "WorkerID='" + screening.getChwid() +
     * "'"); // Get mobile numbers String monitorMobile =
     * getMobileNumber(monitorID); String CHWMobile = null;
     * 
     * if (screening.getChwid() != null) CHWMobile =
     * getMobileNumber(screening.getChwid()); // Set SMS param String
     * targetNumber = ""; String messageText = ""; Date dueDateTime = new
     * Date(); String status = "PENDING";
     * 
     * if (screening.getSuspect()) { // Send alert to Monitor, GP and CHW about
     * Patient confirmation targetNumber = monitorMobile; messageText =
     * "Dear Monitor! A Suspect has been identified by CHW: " +
     * screening.getChwid() + ". ID assigned: " + screening.getPatientId();
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null));
     * 
     * if (CHWMobile != null) { targetNumber = CHWMobile; messageText =
     * "Dear Screener! A Suspect has been identified. ID assigned: " +
     * screening.getPatientId(); sendGenericSMSAlert(new Sms(targetNumber,
     * messageText, dueDateTime, null, status, null, null)); } } }
     * 
     * catch (Exception e) { e.printStackTrace(); } } }
     *//**
     * Records messages to be sent to various roles BUSINESS LOGIC: 1. If the
     * disease is not confirmed and isTBPositive is true, then: - Send alert to
     * Monitor, GP and CHW about Patient confirmation - Send alert to CHW, GP
     * about their updated incentive balance 2. If the disease is not confirmed
     * and isTBPositive is false (suspect with result negative), then: - Send
     * alert to Monitor about negative results 3. If the disease is confirmed
     * and isTBPositive is true (existing Patient with result still positive),
     * then: - Do nothing 4. If the disease is confirmed and isTBPositive is
     * false (existing Patient with improvement), then: - Send alert to Monitor
     * - Send alert to CHW about his updated incentive balance 5. If the disease
     * is not confirmed and isTBPositive is false and Chest X-Ray has also been
     * taken (Cleared from suspicion), then: - Send alert to Monitor EXCEPTION:
     * If the Patient is like '086%' then skip the alert
     */
    /*
     * public void sendAlertsOnSputumResults(SputumResults sputumResults,
     * boolean isTBPositive, SetupIncentive gpIncentive, SetupIncentive
     * chwIncentive) throws Exception { try { Patient patient = (Patient)
     * HibernateUtil.util.findObject("from Patient where PatientID='" +
     * sputumResults.getPatientId() + "'"); // Return on Contact Tracing
     * Patients (ID like P86%) if (patient.getPatientId().contains("086"))
     * return; // Get IDs String[] IDs = getRowRecord("Patient", new String[] {
     * "MonitorID", "ProviderID", "ScreenerID" }, "PatientID='" +
     * patient.getPatientId() + "'"); String monitorID = IDs[0]; String GPID =
     * IDs[1]; String CHWID = IDs[2]; // Get mobile numbers String monitorMobile
     * = getMobileNumber(monitorID); String GPMobile = getMobileNumber(GPID);
     * String CHWMobile = getMobileNumber(CHWID); // Set SMS param String
     * targetNumber = ""; String messageText = ""; Date dueDateTime = new
     * Date(); String status = "PENDING"; Incentive incentive = new Incentive();
     * 
     * if (isTBPositive) { try { if (patient.getDiseaseConfirmed()) { // Do
     * nothing } else throw new Exception(); } catch (Exception e) {
     * 
     * 
     * // Send alert to Monitor, GP and CHW about Patient confirmation
     * targetNumber = monitorMobile; messageText = "Dear Monitor! Suspect: " +
     * patient.getPatientId() + " has been confirmed as Patient. GP attending: "
     * + GPID; sendGenericSMSAlert(new Sms(targetNumber, messageText,
     * dueDateTime, null, status, null, null)); targetNumber = GPMobile;
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); targetNumber = CHWMobile; messageText =
     * "Dear Screener! Suspect: " + patient.getPatientId() +
     * " has been confirmed as Patient. GP attending: " + GPID;
     * sendGenericSMSAlert(new Sms(targetNumber, messageText, dueDateTime, null,
     * status, null, null)); // Send alert to CHW, GP about their updated
     * incentive balance targetNumber = GPMobile; messageText =
     * "Dear Treatment Provider! You have been incentivised amount of " +
     * gpIncentive.getCurrency() + gpIncentive.getAmount() +
     * " on confirmation of Suspect: " + patient.getPatientId(); // Record GP
     * incentive incentive = new Incentive(new IncentiveId(GPID,
     * gpIncentive.getIncentiveId(), 0), "", new Date(), status,
     * "Original message sent: " + messageText);
     * HibernateUtil.util.save(incentive); // Send alert sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null));
     * targetNumber = CHWMobile; messageText =
     * "Dear Screener! You have been incentivised amount of " +
     * chwIncentive.getCurrency() + chwIncentive.getAmount() +
     * " on confirmation of Suspect: " + patient.getPatientId(); incentive = new
     * Incentive(new IncentiveId(CHWID, chwIncentive.getIncentiveId(), 0), "",
     * new Date(), status, "Original message sent: " + messageText);
     * HibernateUtil.util.save(incentive); sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null)); }
     * } else { try { if (patient.getDiseaseConfirmed()) { // Send alert to
     * Monitor targetNumber = monitorMobile; messageText =
     * "Dear Monitor! Sputum Results of Patient: " + patient.getPatientId() +
     * " for the month of " + sputumResults.getMonth() +
     * " were found NEGATIVE."; sendGenericSMSAlert(new Sms(targetNumber,
     * messageText, dueDateTime, null, status, null, null)); targetNumber =
     * CHWMobile; messageText =
     * "Dear Screener! You have been incentivised amount of " +
     * chwIncentive.getCurrency() + chwIncentive.getAmount() +
     * " on improvement of Patient: " + patient.getPatientId(); // Record GP
     * incentive incentive = new Incentive(new IncentiveId(CHWID,
     * chwIncentive.getIncentiveId(), 0), "", new Date(), "",
     * "Original message sent: " + messageText);
     * HibernateUtil.util.save(incentive); sendGenericSMSAlert(new
     * Sms(targetNumber, messageText, dueDateTime, null, status, null, null)); }
     * else throw new Exception(); } catch (Exception e) { e.printStackTrace();
     * } } } catch (Exception e) { e.printStackTrace(); } }
     *//**
     * Records messages to be sent to various roles BUSINESS LOGIC: 1. If
     * isTBPositive is true, then: - Send alert to Monitor M-VICTOR-##
     */
    /*
     * public void sendAlertsOnXRay(XrayResults xRayResults, boolean
     * isTBPositive) throws Exception { try { Patient patient = (Patient)
     * HibernateUtil.util.findObject("from Patient where PatientID = '" +
     * xRayResults.getId().getPatientId() + "'"); // Get mobile number String
     * monitorMobile = getMobileNumber("M-VICTOR-01"); // Set SMS param String
     * targetNumber = ""; String messageText = ""; Date dueDateTime = new
     * Date(); String status = "PENDING";
     * 
     * if (isTBPositive) { try { // Send alert to Monitor about Patient
     * confirmation targetNumber = monitorMobile; messageText =
     * "Dear Monitor! Suspect: " + patient.getPatientId() + " was found " +
     * xRayResults.getXrayResults() + " by the X-Ray results. (SMS sent on " +
     * dueDateTime.toString() + ")"; sendGenericSMSAlert(new Sms(targetNumber,
     * messageText, dueDateTime, null, status, null, null)); } catch (Exception
     * e) { e.printStackTrace(); } } } catch (Exception e) {
     * e.printStackTrace(); } }
     */

    /**
     * User authentication: Checks whether user exists, then match his password
     * 
     * @return Boolean
     */

    public Boolean authenticate(String userName, String password)
	    throws Exception {
	if (!UserAuthentication.userExsists(userName))
	    return false;
	else if (!UserAuthentication.validatePassword(userName, password))
	    return false;
	XSMS.setCurrentUser(userName.toUpperCase());
	return true;
    }

    /**
     * Checks if a user exists in the database
     * 
     * @return Boolean
     */

    public Boolean authenticateUser(String userName) throws Exception {
	if (!UserAuthentication.userExsists(userName))
	    return false;
	return true;
    }

    /**
     * Verifies secret answer against stored secret question
     * 
     * @return Boolean
     */

    public Boolean verifySecretAnswer(String userName, String secretAnswer)
	    throws Exception {
	if (!UserAuthentication.validateSecretAnswer(userName, secretAnswer))
	    return false;
	return true;
    }

    /**
     * Get number of records in a table, given appropriate filter
     * 
     * @return Long
     */

    public Long count(String tableName, String filter) throws Exception {
	Object obj = HibernateUtil.util.selectObject("select count(*) from "
		+ tableName + " " + arrangeFilter(filter));
	return Long.parseLong(obj.toString());
    }

    /**
     * Checks existence of data by counting number of records in a table, given
     * appropriate filter
     * 
     * @return Boolean
     */

    public Boolean exists(String tableName, String filter) throws Exception {
	long count = count(tableName, filter);
	return count > 0;
    }

    /**
     * Generates CSV file from query passed along with the filters
     * 
     * @param query
     * @return
     */

    public String generateCSVfromQuery(String query) throws Exception {
	return ReportUtil.generateCSVfromQuery(query, ',');
    }

    /**
     * Generate report on server side and return the path it was created to
     * 
     * @param Path
     *            of report as String Report parameters as Parameter[] Report to
     *            be exported in csv format as Boolean
     * @return String
     */

    public String generateReport(String fileName, Parameter[] params,
	    boolean export) throws Exception {
	return ReportUtil.generateReport(fileName, params, export);
    }

    /**
     * Generate report on server side based on the query saved in the Database
     * against the reportName and return the path it was created to
     * 
     * @param reportName
     * @param params
     * @param export
     * @return
     */

    public String generateReportFromQuery(String reportName, String query,
	    Boolean export) throws Exception {
	return ReportUtil.generateReportFromQuery(reportName, query, export);
    }

    public String[] getColumnData(String tableName, String columnName,
	    String filter) throws Exception {
	Object[] data = HibernateUtil.util.selectObjects("select distinct "
		+ columnName + " from " + tableName + " "
		+ arrangeFilter(filter));
	String[] columnData = new String[data.length];
	for (int i = 0; i < data.length; i++)
	    columnData[i] = data[i].toString();
	return columnData;
    }

    public String getCurrentUser() throws Exception {
	return XSMS.getCurrentUser();
    }

    public String getObject(String tableName, String columnName, String filter)
	    throws Exception {
	return HibernateUtil.util.selectObject(
		"select " + columnName + " from " + tableName
			+ arrangeFilter(filter)).toString();
    }

    public String[][] getReportsList() throws Exception {
	return ReportUtil.getReportList();
    }

    public String[] getRowRecord(String tableName, String[] columnNames,
	    String filter) throws Exception {
	return getTableData(tableName, columnNames, filter)[0];
    }

    public String getSecretQuestion(String userName) throws Exception {
	Users user = (Users) HibernateUtil.util
		.findObject("from Users where UserName = '" + userName + "'");
	return user.getSecretQuestion();
    }

    @SuppressWarnings("deprecation")
    public String getSnapshotTime() throws Exception {
	Date dt = new Date();
	Object obj = HibernateUtil.util
		.selectObject("select Max(DateEncounterEnd) from Encounter where DATE(DateEncounterEnd) < '"
			+ (dt.getYear() + 1900)
			+ "-"
			+ (dt.getMonth() + 1)
			+ "-" + dt.getDate() + "'");
	return obj.toString();
    }

    public String[][] getTableData(String tableName, String[] columnNames,
	    String filter) throws Exception {
	StringBuilder columnList = new StringBuilder();
	for (String s : columnNames) {
	    columnList.append(s);
	    columnList.append(",");
	}
	columnList.deleteCharAt(columnList.length() - 1);

	Object[][] data = HibernateUtil.util.selectData("select "
		+ columnList.toString() + " from " + tableName + " "
		+ arrangeFilter(filter));
	String[][] stringData = new String[data.length][columnNames.length];
	for (int i = 0; i < data.length; i++) {
	    for (int j = 0; j < columnNames.length; j++) {
		if (data[i][j] == null)
		    data[i][j] = "";
		String str = data[i][j].toString();
		stringData[i][j] = str;
	    }
	}
	return stringData;
    }

    public Boolean[] getUserRgihts(String userName, String menuName)
	    throws Exception {
	String role = HibernateUtil.util.selectObject(
		"select Role from Users where UserName='" + userName + "'")
		.toString();
	if (role.equalsIgnoreCase("ADMIN")) {
	    Boolean[] rights = { true, true, true, true, true };
	    return rights;
	}
	UserRights userRights = (UserRights) HibernateUtil.util
		.findObject("from UserRights where Role='" + role
			+ "' and MenuName='" + menuName + "'");
	Boolean[] rights = { userRights.isSearchAccess(),
		userRights.isInsertAccess(), userRights.isUpdateAccess(),
		userRights.isDeleteAccess(), userRights.isPrintAccess() };
	return rights;
    }

    public void recordLogin(String userName) throws Exception {
	Users user = (Users) HibernateUtil.util
		.findObject("from Users where UserName = '" + userName + "'");
	HibernateUtil.util.recordLog(LogType.LOGIN, user);
    }

    public void recordLogout(String userName) throws Exception {
	Users user = (Users) HibernateUtil.util
		.findObject("from Users where UserName = '" + userName + "'");
	HibernateUtil.util.recordLog(LogType.LOGOUT, user);
    }

    public int execute(String query) throws Exception {
	return HibernateUtil.util.runCommand(query);
    }

    public Boolean execute(String[] queries) throws Exception {
	for (String s : queries) {
	    boolean result = execute(s) >= 0;
	    if (!result)
		return false;
	}
	return true;
    }

    public Boolean executeProcedure(String procedure) throws Exception {
	return HibernateUtil.util.runProcedure(procedure);
    }

    /* Delete methods */
    public Boolean deleteContact(Contact contact) throws Exception {
	return HibernateUtil.util.delete(contact);
    }

    public Boolean deleteEncounter(Encounter encounter) throws Exception {
	return HibernateUtil.util.delete(encounter);
    }

    public Boolean deleteEncounterResults(EncounterResults encounterResults)
	    throws Exception {
	return HibernateUtil.util.delete(encounterResults);
    }

    public Boolean deleteEncounterResults(EncounterResultsId encounterResultsId)
	    throws Exception {
	boolean result = false;
	EncounterResults er = (EncounterResults) HibernateUtil.util
		.findObject("from EncounterResults where EncounterId="
			+ encounterResultsId.getEncounterId() + " and PID1='"
			+ encounterResultsId.getPid1() + "' and PID2='"
			+ encounterResultsId.getPid2() + "' and Element='"
			+ encounterResultsId.getElement() + "'");
	result = deleteEncounterResults(er);
	return result;
    }

    public Boolean deleteEncounterWithResults(Encounter encounter)
	    throws Exception {
	boolean result = false;
	try {
	    String[] elements = getColumnData("EncounterResults", "Element",
		    "EncounterID=" + encounter.getId().getEncounterId()
			    + " AND PID1='" + encounter.getId().getPid1()
			    + "' AND PID2='" + encounter.getId().getPid2()
			    + "'");
	    // Delete encounter results
	    for (String s : elements) {
		EncounterResultsId id = new EncounterResultsId(encounter
			.getId().getEncounterId(), encounter.getId().getPid1(),
			encounter.getId().getPid2(), s);
		result = deleteEncounterResults(id);
	    }
	    // Delete encounter
	    result = deleteEncounter(encounter);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return result;
    }

    public Boolean deleteGeneXpertResults(GeneXpertResults geneXpertResults)
	    throws Exception {
	return HibernateUtil.util.delete(geneXpertResults);
    }

    public Boolean deleteLocation(Location location) throws Exception {
	return HibernateUtil.util.delete(location);
    }

    public Boolean deleteOtherMessageSetting(OtherMessageSetting setting) {
	return HibernateUtil.util.delete(setting);
    }

    public Boolean deletePatient(Patient patient) throws Exception {
	return HibernateUtil.util.delete(patient);
    }

    public Boolean deletePerson(Person person) throws Exception {
	return HibernateUtil.util.delete(person);
    }

    public Boolean deleteSputumResults(SputumResults sputumResults)
	    throws Exception {
	return HibernateUtil.util.delete(sputumResults);
    }

    public Boolean deleteUser(Users user) throws Exception {
	return HibernateUtil.util.delete(user);
    }

    public Boolean deleteUserRights(UserRights userRights) throws Exception {
	return HibernateUtil.util.delete(userRights);
    }

    /* Find methods */

    public Contact findContact(String personID) throws Exception {
	return (Contact) HibernateUtil.util
		.findObject("from Contact where PID='" + personID + "'");
    }

    public Encounter findEncounter(EncounterId encounterID) throws Exception {
	return (Encounter) HibernateUtil.util
		.findObject("from Encounter where PID1='"
			+ encounterID.getPid1() + "' and PID2='"
			+ encounterID.getPid2() + "' and EncounterID='"
			+ encounterID.getEncounterId() + "'");
    }

    public EncounterResults[] findEncounterResults(
	    EncounterResultsId encounterResultsID) throws Exception {
	return (EncounterResults[]) HibernateUtil.util
		.findObjects("from EncounterResults where PID1='"
			+ encounterResultsID.getPid1() + "' and PID2='"
			+ encounterResultsID.getPid2() + "' and EncounterID='"
			+ encounterResultsID.getEncounterId() + "'");
    }

    public EncounterResults findEncounterResultsByElement(
	    EncounterResultsId encounterResultsID) throws Exception {
	return (EncounterResults) HibernateUtil.util
		.findObject("from EncounterResults where PID1='"
			+ encounterResultsID.getPid1() + "' and PID2='"
			+ encounterResultsID.getPid2() + "' and EncounterID='"
			+ encounterResultsID.getEncounterId()
			+ "' and Element='" + encounterResultsID.getElement()
			+ "'");
    }

    public GeneXpertResults findGeneXpertResults(String sputumTestID)
	    throws Exception {
	return (GeneXpertResults) HibernateUtil.util
		.findObject("from GeneXpertResults where SputumTestID='"
			+ sputumTestID + "'");
    }

    public GeneXpertResults[] findGeneXpertResults(String sputumTestID,
	    String patientID) throws Exception {
	Object[] objects = HibernateUtil.util
		.findObjects("from GeneXpertResults where SputumTestID='"
			+ sputumTestID + "' AND PatientID='" + patientID + "'");

	return Arrays.copyOf(objects, objects.length, GeneXpertResults[].class);
    }

    public GeneXpertResults findGeneXpertResultsByTestID(String testID)
	    throws Exception {
	return (GeneXpertResults) HibernateUtil.util
		.findObject("from GeneXpertResults where TestID='" + testID
			+ "'");
    }

    public Location findLocation(String locationID) throws Exception {
	return (Location) HibernateUtil.util
		.findObject("from Location where LocationID='" + locationID
			+ "'");
    }

    public Location findDistrict(String locationID) {
	return (Location) HibernateUtil.util
		.findObject("from Location where LocationID='" + locationID
			+ "' and locationType='DISTRICT'");
    }

    public Location findFacility(String facilityId, String districtId) {
	return (Location) HibernateUtil.util
		.findObject("from Location where locationType='HEALTH FACILITY' and CityID='"
			+ districtId + "' and LocationID='" + facilityId + "'");

    }

    public MessageSettings findMessageSettings() throws Exception {
	return (MessageSettings) HibernateUtil.util
		.findObject("from MessageSettings");
    }

    public Patient findPatient(String patientID) throws Exception {
	return (Patient) HibernateUtil.util
		.findObject("from Patient where PatientID='" + patientID + "'");
    }

    public Person findPerson(String PID) throws Exception {
	return (Person) HibernateUtil.util.findObject("from Person where PID='"
		+ PID + "'");
    }

    public Person[] findPersonsByName(String firstName, String lastName)
	    throws Exception {
	return (Person[]) HibernateUtil.util
		.findObjects("from Person where FirstName LIKE '" + firstName
			+ "%' and LastName LIKE '" + lastName + "%'");
    }

    public Person findPersonsByNIC(String NIC) throws Exception {
	return (Person) HibernateUtil.util.findObject("from Person where NIC='"
		+ NIC + "'");
    }

    public SputumResults findSputumResults(String patientID, String sputumTestID)
	    throws Exception {
	return (SputumResults) HibernateUtil.util
		.findObject("from SputumResults where PatientID='" + patientID
			+ "' and SputumTestID='" + sputumTestID + "'");
    }

    public SputumResults[] findSputumResultsByPatientID(String patientID)
	    throws Exception {
	return (SputumResults[]) HibernateUtil.util
		.findObjects("from SputumResults where PatientID='" + patientID
			+ "'");
    }

    public SputumResults findSputumResultsBySputumTestID(String sputumTestID)
	    throws Exception {
	return (SputumResults) HibernateUtil.util
		.findObject("from SputumResults where SputumTestID='"
			+ sputumTestID + "'");
    }

    public Users findUser(String userName) throws Exception {
	return (Users) HibernateUtil.util
		.findObject("from Users where UserName='" + userName + "'");
    }

    public UserRights findUserRights(String roleName, String menuName)
	    throws Exception {
	return (UserRights) HibernateUtil.util
		.findObject("from UserRights where Role='" + roleName
			+ "' and MenuName='" + menuName + "'");
    }

    /* Save methods */
    public Boolean saveContact(Contact contact) throws Exception {
	return HibernateUtil.util.save(contact);
    }

    public Boolean saveEncounter(Encounter encounter) throws Exception {
	// Get the max encounter ID and add 1
	EncounterId currentID = encounter.getId();
	Object[] max = HibernateUtil.util
		.selectObjects("select max(encounterID) from Encounter where pid1='"
			+ currentID.getPid1()
			+ "' and pid2='"
			+ currentID.getPid2() + "'");

	Integer maxInt = (Integer) max[0];
	if (maxInt == null) {
	    currentID.setEncounterId(1);
	} else {
	    currentID.setEncounterId((maxInt.intValue() + 1));
	}
	encounter.setId(currentID);
	return HibernateUtil.util.save(encounter);
    }

    public Boolean saveEncounterResults(EncounterResults encounterResults)
	    throws Exception {
	return HibernateUtil.util.save(encounterResults);
    }

    public Boolean saveEncounterWithResults(Encounter encounter,
	    ArrayList<String> encounterResults) throws Exception {
	boolean result = false;
	// Save an encounter
	try {
	    Long encounterID = HibernateUtil.util
		    .count("select IFNULL(max(EncounterID), 0) + 1 from Encounter where PID1='"
			    + encounter.getId().getPid1()
			    + "' and PID2='"
			    + encounter.getId().getPid2()
			    + "' and EncounterType='"
			    + encounter.getEncounterType() + "'");
	    result = saveEncounter(encounter);

	    for (String s : encounterResults) {
		String[] split = s.split("=");
		EncounterResults encounterResult = new EncounterResults();
		encounterResult.setId(new EncounterResultsId(encounterID
			.intValue(), encounter.getId().getPid1(), encounter
			.getId().getPid2(), split[0]));
		if (split.length == 2)
		    encounterResult.setValue((split[1]));
		else
		    encounterResult.setValue("");
		result = saveEncounterResults(encounterResult);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return result;
    }

    public Boolean saveGeneXpertResults(GeneXpertResults geneXpertResults)
	    throws Exception {
	Boolean value = HibernateUtil.util.save(geneXpertResults);
	SMSUtil.util.sendAlertsOnAutoGXPResults(geneXpertResults);
	return value;
    }

    public Boolean saveGeneXpertResultsUnlinked(
	    GeneXpertResultsUnlinked geneXpertResultsU) throws Exception {
	return HibernateUtil.util.save(geneXpertResultsU);
    }

    public Boolean saveLocation(Location location) throws Exception {
	return HibernateUtil.util.save(location);
    }

    public Boolean saveOtherMessageSetting(OtherMessageSetting setting) {
	return HibernateUtil.util.save(setting);
    }

    public Boolean saveMessageSettings(MessageSettings messageSettings)
	    throws Exception {
	return HibernateUtil.util.save(messageSettings);
    }

    public Boolean saveNewPatient(Patient patient, Person person,
	    Contact contact, Encounter encounter,
	    ArrayList<String> encounterResults) throws Exception {
	boolean result = false;
	// Save an encounter
	try {
	    result = saveEncounterWithResults(encounter, encounterResults);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	result = HibernateUtil.util.save(patient);
	result = HibernateUtil.util.save(person);
	result = HibernateUtil.util.save(contact);
	if (!result) // In case of failure of any save, delete all 3
	{
	    HibernateUtil.util.delete(patient);
	    HibernateUtil.util.delete(person);
	    HibernateUtil.util.delete(contact);
	    return false;
	}
	return result;
    }

    public Boolean savePatient(Patient patient) throws Exception {
	return HibernateUtil.util.save(patient);
    }

    public Boolean savePerson(Person person) throws Exception {
	return HibernateUtil.util.save(person);
    }

    public Boolean saveSputumResults(SputumResults sputumResults)
	    throws Exception {
	return HibernateUtil.util.save(sputumResults);
    }

    public Boolean saveUser(Users user) throws Exception {
	user.setPassword(MDHashUtil.getHashString(user.getPassword()));
	user.setSecretAnswer(MDHashUtil.getHashString(user.getSecretAnswer()));
	return HibernateUtil.util.save(user);
    }

    public Boolean saveUserRights(UserRights userRights) throws Exception {
	return HibernateUtil.util.save(userRights);
    }

    /* Update methods */
    public Boolean updateContact(Contact contact) throws Exception {
	return HibernateUtil.util.update(contact);
    }

    public Boolean updateEncounter(Encounter encounter) throws Exception {
	return HibernateUtil.util.update(encounter);
    }

    public boolean updateOtherMessageSetting(OtherMessageSetting setting) {
	return HibernateUtil.util.update(setting);
    }

    public Boolean updateEncounterResults(EncounterResults encounterResults)
	    throws Exception {
	return HibernateUtil.util.update(encounterResults);
    }

    public Boolean updateEncounterResults(
	    EncounterResultsId encounterResultsId, String newValue)
	    throws Exception {
	EncounterResults encounterResults = (EncounterResults) HibernateUtil.util
		.findObject("from EncounterResults where EncounterId="
			+ encounterResultsId.getEncounterId() + " and PID1='"
			+ encounterResultsId.getPid1() + "' and PID2='"
			+ encounterResultsId.getPid2() + "' and Element='"
			+ encounterResultsId.getElement() + "'");
	encounterResults.setValue(newValue);
	return HibernateUtil.util.update(encounterResults);
    }

    public Boolean updateGeneXpertResults(GeneXpertResults geneXpertResults,
	    Boolean isTBPositive) throws Exception {
	Boolean result = HibernateUtil.util.update(geneXpertResults);

	SMSUtil.util.sendAlertsOnAutoGXPResults(geneXpertResults);// ,
								  // isTBPositive,
								  // gpIncentive,
								  // chwIncentive);

	return result;
    }

    public Boolean updateLocation(Location location) throws Exception {
	return HibernateUtil.util.update(location);
    }

    public Boolean updateMessageSettings(MessageSettings messageSettings)
	    throws Exception {
	return HibernateUtil.util.update(messageSettings);
    }

    public Boolean updatePassword(String userName, String newPassword)
	    throws Exception {
	Users user = (Users) HibernateUtil.util
		.findObject("from Users where UserName = '" + userName + "'");
	user.setPassword(MDHashUtil.getHashString(newPassword));
	return HibernateUtil.util.update(user);
    }

    public Boolean updatePatient(Patient patient) throws Exception {
	return HibernateUtil.util.update(patient);
    }

    public Boolean updatePerson(Person person) throws Exception {
	return HibernateUtil.util.update(person);
    }

    public Boolean updateSputumResults(SputumResults sputumResults,
	    Boolean isTBPositive) throws Exception {
	Boolean result = HibernateUtil.util.update(sputumResults);
	return result;
    }

    public Boolean updateUser(Users user) throws Exception {
	user.setPassword(MDHashUtil.getHashString(user.getPassword()));
	user.setSecretAnswer(MDHashUtil.getHashString(user.getSecretAnswer()));
	return HibernateUtil.util.update(user);
    }

    public Boolean updateUserRights(UserRights userRights) throws Exception {
	return HibernateUtil.util.update(userRights);
    }

    public Boolean updateGeneXpertResultsAuto(
	    GeneXpertResults geneXpertResults, Boolean isTBPositive,
	    String operatorId, String pcId, String instrumentSerial,
	    String moduleId, String cartridgeId, String reagentLotId)
	    throws Exception {
	Boolean result = HibernateUtil.util.update(geneXpertResults);

	SMSUtil.util.sendAlertsOnAutoGXPResults(geneXpertResults);
	return result;
    }

    public Location[] findLocationsByType(String locationType) {
	Object[] list = HibernateUtil.util
		.findObjects("from Location where locationType='"
			+ locationType + "'");
	Location[] locations = new Location[list.length];
	for (int i = 0; i < list.length; i++)
	    locations[i] = (Location) list[i];
	return locations;
    }

    public Location[] findFacilitiesByDistrictId(String districtId) {
	Object[] list = HibernateUtil.util
		.findObjects("from Location where locationType='HEALTH FACILITY' and CityID='"
			+ districtId + "'");
	Location[] locations = new Location[list.length];
	for (int i = 0; i < list.length; i++)
	    locations[i] = (Location) list[i];
	return locations;
    }

    public OtherMessageSetting[] findOtherMessageRecipient() {
	Object[] list = HibernateUtil.util
		.findObjects("from OtherMessageSetting");
	OtherMessageSetting[] locations = new OtherMessageSetting[list.length];
	for (int i = 0; i < list.length; i++)
	    locations[i] = (OtherMessageSetting) list[i];
	return locations;
    }

    public OtherMessageSetting findOtherMessageRecipientById(String id) {
	return (OtherMessageSetting) HibernateUtil.util
		.findObject("from OtherMessageSetting where id = '" + id + "'");
    }

    public Boolean saveSputumCollection(GeneXpertResults geneXpertResults,
	    SputumResults sputumResults, Encounter encounter,
	    ArrayList<String> encounterResults) throws Exception {
	if (sputumResults != null) {
	    System.out.println("saving sputum");
	    try {
		exists("EncounterResults",
			" where Element='SAMPLE_BARCODE' AND Value='"
				+ sputumResults.getSputumTestId() + "'");
	    } catch (Exception e) {
		e.printStackTrace();
		return false;
	    }

	    saveSputumResults(sputumResults);
	}

	if (geneXpertResults != null) {
	    System.out.println("xpert");
	    try {
		exists("EncounterResults",
			" where Element='SAMPLE_BARCODE' AND Value='"
				+ geneXpertResults.getSputumTestId() + "'");
	    } catch (Exception e) {
		e.printStackTrace();
		return false;
	    }

	    saveGeneXpertResults(geneXpertResults);
	}
	saveEncounterWithResults(encounter, encounterResults);
	return true;
    }

    @Override
    public String[][] getLists() throws Exception {
	String[][] lists = null;
	ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
	try {
	    DocumentBuilderFactory buildFactory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder documentBuilder = buildFactory.newDocumentBuilder();
	    File file = new File(XSMS.getStaticFilePath());
	    Document doc = documentBuilder.parse(file);
	    Element docElement = doc.getDocumentElement();
	    for (ListType type : ListType.values()) {
		ArrayList<String> array = new ArrayList<String>();
		NodeList list = docElement
			.getElementsByTagName(type.toString());
		if (list != null) {
		    for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			NodeList children;
			if (node.getNodeType() == Node.ELEMENT_NODE) {
			    children = node.getChildNodes();
			    if (children.getLength() > 0) {
				for (int j = 0; j < children.getLength(); j++) {
				    NodeList items = children.item(j)
					    .getChildNodes();
				    for (int k = 0; k < items.getLength(); k++) {
					String str = items.item(k)
						.getTextContent();
					array.add(str);
				    }
				}
			    }
			}
		    }
		}
		arrayList.add(array);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	lists = new String[arrayList.size()][];
	for (int i = 0; i < arrayList.size(); i++) {
	    String[] str = new String[0];
	    lists[i] = arrayList.get(i).toArray(str);
	}
	return lists;
    }

}