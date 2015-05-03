package org.irdresearch.tbreach.server;

import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.jmatrix.eproperties.EProperties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.tbreach.server.HibernateUtil;
import org.irdresearch.tbreach.server.ServerServiceImpl;
import org.irdresearch.tbreach.server.UserAuthentication;
//import org.irdresearch.tbreach.server.XmlUtil;
import org.irdresearch.tbreach.shared.SmsTarseelUtil;
import org.irdresearch.tbreach.shared.model.Contact;
import org.irdresearch.tbreach.shared.model.Encounter;
import org.irdresearch.tbreach.shared.model.EncounterId;
import org.irdresearch.tbreach.shared.model.EncounterResults;
import org.irdresearch.tbreach.shared.model.EncounterResultsId;
import org.irdresearch.tbreach.shared.model.GeneXpertResults;
import org.irdresearch.tbreach.shared.model.Patient;
import org.irdresearch.tbreach.shared.model.Person;
import org.irdresearch.tbreach.shared.model.SputumResults;
import org.irdresearch.tbreach.shared.model.Users;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import com.sun.xml.internal.ws.api.model.MEP;

import sun.font.FontManager.FamilyDescription;

public class EventHandler {

	private HttpServletRequest request;
	private ServerServiceImpl ssl;
	private Date encounterStartDate;
	private Date encounterEndDate;
	private static EventHandler	service			= new EventHandler ();

	public EventHandler() {
          ssl = new ServerServiceImpl();
		
			try {
				System.out.println(">>>>LOADING SYSTEM PROPERTIES...");
				InputStream f = Thread.currentThread().getContextClassLoader().getResourceAsStream("smstarseel.properties");
				// Java Properties donot seem to support substitutions hence EProperties are used to accomplish the task
				EProperties root = new EProperties();
				root.load(f);

				// Java Properties to send to context and other APIs for configuration
				Properties prop = new Properties();
				prop.putAll(SmsTarseelUtil.convertEntrySetToMap(root.entrySet()));

				TarseelContext.instantiate(prop, "smstarseel.cfg.xml");

				System.out.println("......PROPERTIES LOADED SUCCESSFULLY......");
					
			} 
			catch (Exception e) {
				e.printStackTrace();
			} 
			     
			// TODO uncomment if doing via socket initSocket();
	}
	
	public static EventHandler getService ()
	{
		return EventHandler.service;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String handleEvent(HttpServletRequest request) {
		
		setRequest(request);

		String xmlResponse = null;

		String reqType = request.getParameter("type");
		System.out.println("----->" + reqType);
		
		String id = request.getParameter("id");
		if(id!=null && id.length()>0 && id.charAt(0)=='P') {
			return XmlUtil.createErrorXml("Phone update karain aur dobara form bharain.");
		}

		Boolean uCheck = null;
		/*if(request.getParameter("chwid")!=null) 
		{
			try {
				uCheck = ssl.exists("Users", "where PID = '" + request.getParameter("chwid").toUpperCase() + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(uCheck.booleanValue()!=true){
				return XmlUtil.createErrorXml("Aap ne ghalat CHW ID ya Monitor ID darj kia hai. Sahih ID enter karain aur dobara koshish karain");
			}
		}
		
		if(request.getParameter("mid")!=null) 
		{
			try {
				uCheck = ssl.exists("Users", "where PID = '" + request.getParameter("mid").toUpperCase() + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(uCheck.booleanValue()!=true){
				return XmlUtil.createErrorXml("Aap ne ghalat CHW ID ya Monitor ID darj kia hai. Sahih ID enter karain aur dobara koshish karain");
			}
		}
		
		if(request.getParameter("gpid")!=null) 
		{
			try {
				uCheck = ssl.exists("Users", "where PID = '" + request.getParameter("gpid").toUpperCase() + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(uCheck.booleanValue()!=true){
				return XmlUtil.createErrorXml("Aap ne ghalat GP ID darj kia hai. Sahih ID enter karain aur dobara koshish karain");
			}
		}*/
		
		if (reqType.equals(RequestType.LOGIN)) {
			return doLogin();
		}

		/*else if (reqType.equals(RequestType.FORM_QUERY)) {
			return doFormQuery();
		}
		
		else if (reqType.equals(RequestType.REPORT_QUERY)) {
			return handleReportFormQuery();
		}*/
		
		else if(reqType.equals(RequestType.REMOTE_ASTM_RESULT)) {
			return doRemoteASTMResult();
		}
		
		else {

			String startDate = request.getParameter("sd");
			String startTime = request.getParameter("st");
			String endTime = request.getParameter("et");

			try {
				encounterStartDate = DateTimeUtil.getDateFromString(startDate
						+ " " + startTime, DateTimeUtil.FE_FORMAT);
				encounterEndDate = DateTimeUtil.getDateFromString(startDate
						+ " " + endTime, DateTimeUtil.FE_FORMAT);

			} catch (ParseException e2) {
				//  Auto-generated catch block
				return XmlUtil
						.createErrorXml("Invalid Date Format. Please contact technical support!");
			}
			
			if(!reqType.equals(RequestType.DFR) && !reqType.equals(RequestType.SUSPECT_ID) && !reqType.equals(RequestType.DOTS_ASSIGN) && !reqType.equals(RequestType.REMOTE_ASTM_RESULT)) {
				Boolean closed = null;
				if(request.getParameter("id")!=null) {
				try {
					closed = ModelUtil.isPatientClosed(request.getParameter("id"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return XmlUtil.createErrorXml("Error tracking patient status. Please try again!");
				}
				
				if(closed==null) {
					return XmlUtil.createErrorXml("Error tracking patient status. Please try again!");
				}
				
				if(closed.booleanValue()==true) {
					return XmlUtil.createErrorXml("Is Patient ka End of Follow-up form bhar diya gaya hai. In ka koi bhi form bhara nahin ja sakta!");
				}
				}
			}

			/*if (reqType.equals(RequestType.SUSPECT_ID)) {

				return doSuspectIdentification();
			}

			else if (reqType.equals(RequestType.SUSPECT_CONF)) {
				return doSuspectConfirm();
			}

			else if (reqType.equals(RequestType.SUSPECT_VERIFY)) {
				return doSuspectVerify();
			}
			
			else if (reqType.equals(RequestType.PATIENT_FUP_EFFORT)) {
				
				return doPatientFollowupEffort();
			}
			
			else if (reqType.equals(RequestType.PATIENT_INFO)) {
				return doPatientInfo();
			}

			else if (reqType.equals(RequestType.PATIENT_TB_INFO)) {
				return doPatientTBInfo();
			}

			else if (reqType.equals(RequestType.PATIENT_GPS_INFO)) {
				return doPatientGPSInfo();
			}

			else if (reqType.equals(RequestType.SPUTUM_COLLECTION)) {
				return doSputumCollection();
			}

			else if (reqType.equals(RequestType.PAEDS_CINICAL_VISIT)) {
				return doPaedClinicalVisit();
			}
	
			else if (reqType.equals(RequestType.CINICAL_VISIT)) {
				return doClinicalVisit();
			}
			
			else if (reqType.equals(RequestType.BASELINE_TX)) {
				return doBaselineTreatment();
			}

			else if (reqType.equals(RequestType.FOLLOWUP_TX)) {
				return doFollowupTreatment();
			}

			else if (reqType.equals(RequestType.DRUG_ADMIN)) {
				return doDrugAdm();
			}

			else if (reqType.equals(RequestType.END_FOLLOWUP)) {
				return doEndFollowup();
			}

			else if (reqType.equals(RequestType.REFUSAL)) {
				return doRefusal();
			}
			
			else if(reqType.equals(RequestType.MR_ASSIGN)) {
				return doMRAssign();
			}
			
			else if(reqType.equals(RequestType.DFR)) {
				return doDFR();
			}
			
			else if(reqType.equals(RequestType.CDF)) {
				return doCDF();
			}
			
			else if(reqType.equals(RequestType.CTNS)) {
				return doSuspectIdentificationCT();
			}
			
			else if(reqType.equals(RequestType.PAED_CONF_FORM)) {
				return doPaedConfirmation();
			}
			
			else if(reqType.equals(RequestType.PAED_DIAG_FORM)) {
				return doPaedClinicalDiagnosis();
			}
			
			else if(reqType.equals(RequestType.CONTACT_SPUTUM_COLLECTION)) {
				return doContactSputumCollection();
			}
			
			else if(reqType.equals(RequestType.DRUG_DISPENSATION)) {
				return doDrugDispensation();
			}
			else if(reqType.equals(RequestType.DOTS_ASSIGN)) {
				return doDOTSAssign();
			}
			
			else if(reqType.equals(RequestType.ADDRESS_UPDATE)) {
				return doAddressUpdate();
			}
			
			else if(reqType.equals(RequestType.PAT_VERIFY)) {
				return doPatientVerify();
			}
			
			else if(reqType.equals(RequestType.NO_ACTIVE_FOLLOWUP)) {
				return doNoActiveFollowup();
			}
			
			else if(reqType.equals(RequestType.YIELD)) {
				return doYield();
			}
		}*/

		return xmlResponse;
		}
	}

	private String doLogin() {
		String xml = null;

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String role = "";
		String pid = "";
		
		//START TIME CHECK
		String phoneTime = request.getParameter("phoneTime");

		if(phoneTime==null) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara login karain");
		}
		
		Date phoneDate = null;
		try {
			phoneDate = DateTimeUtil.getDateFromString(phoneTime, DateTimeUtil.FE_FORMAT);
			System.out.println("pdate: " + phoneDate.toString());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad date!");
		}
		
		
		
		//.GregorianCalendar phoneCal = new GregorianCalendar();
		//phoneCal.setTimeInMillis(phoneDate.getTime());
		DateTime dt = new DateTime();
		
		String year = new Integer(dt.get(DateTimeFieldType.year())).toString();
		String month = new Integer(dt.get(DateTimeFieldType.monthOfYear())).toString();
		String day =  new Integer(dt.get(DateTimeFieldType.dayOfMonth())).toString();
		String hour =  new Integer(dt.get(DateTimeFieldType.hourOfDay())).toString();
		String minute =  new Integer(dt.get(DateTimeFieldType.minuteOfHour())).toString();
		String second =  new Integer(dt.get(DateTimeFieldType.secondOfMinute())).toString();
		
		String dateTimeString = ""; 
		dateTimeString = day.length()==2 ? dateTimeString + day : dateTimeString + "0" + day;
		dateTimeString += "/";
		dateTimeString = month.length()==2 ? dateTimeString + month : dateTimeString + "0" + month;
		dateTimeString += "/" + year;
		dateTimeString += " ";
		dateTimeString = hour.length()==2 ? dateTimeString + hour : dateTimeString + "0" + hour;
		dateTimeString += ":";
		dateTimeString = minute.length()==2 ? dateTimeString + minute : dateTimeString + "0" + minute;
		dateTimeString += ":";
		dateTimeString = second.length()==2 ? dateTimeString + second : dateTimeString + "0" + second;
		
		
		Date serverDate = null;
		try {
			 serverDate = DateTimeUtil.getDateFromString(dateTimeString, DateTimeUtil.FE_FORMAT);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("--->" + serverDate.toString());
		
		//Interval i = new Interval(phoneDate.getTime(), serverDate.getTime());
		
		
		//java.sql.Date nowDate = new java.sql.Date(System.currentTimeMillis());
		
		//
		
		//
		//System.out.println("nowDate: " + dt);
		
		long milliDiff = Math.abs(phoneDate.getTime() - serverDate.getTime());
		//long milliDiff = Math.abs(i.toDuration().getMillis());
		System.out.println(milliDiff);
		double secDiff = milliDiff/1000;
		System.out.println(secDiff);
		double hrDiff = secDiff/3600;
		
		System.out.println(hrDiff);
		
		if(hrDiff > 1) {
			return XmlUtil.createErrorXml("Aap ke phone par date ya time ghalat hai. Sahih date aur time set kar ke phir koshish karain");
		}
		try{
			if (ssl.authenticate(username, password)) {
	
				try {
					Users users = ssl.findUser(username);
					pid = users.getPid();
					role = users.getRole();
	
				} catch (Exception e) {
					//  Auto-generated catch block
					e.printStackTrace();
					return XmlUtil.createErrorXml("Error logging in. Please try again");
				}
				ssl.recordLogin(username);
			}
	
			else {
				return XmlUtil.createErrorXml("Invalid Username or Password. Please try again");
			}
		}
		catch (Exception e) {
			return XmlUtil.createErrorXml("Error logging in. Please try again");
		}
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element statusNode = doc.createElement("role");
		Text statusValue = doc.createTextNode(role.toUpperCase());
		statusNode.appendChild(statusValue);

		responseNode.appendChild(statusNode);

		Element uidNode = doc.createElement("uid");
		Text uidValue = doc.createTextNode(pid);
		uidNode.appendChild(uidValue);

		responseNode.appendChild(uidNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}

	// *****************************************
	// doSuspectIdentification*************************/

	
	/*private String doSuspectIdentification() {

		Boolean advSeen = false;
		Boolean isSuspect = false;
		String xml = null;
		String v = request.getParameter("v");
		
	
		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid");
		String gpId = request.getParameter("gpid");
		String firstName = request.getParameter("fn");
		String lastName = request.getParameter("ln");
		
		
		String sex = request.getParameter("sex").toUpperCase();
		String age = request.getParameter("age");
		String cough = request.getParameter("cough").toUpperCase();
		String coughDuration = request.getParameter("cd");
		if(coughDuration!=null)
			coughDuration = coughDuration.toUpperCase();
		String productiveCough = request.getParameter("pc");
		if(productiveCough !=null)
			productiveCough = productiveCough.toUpperCase();
		String tbHistory = request.getParameter("tbh").toUpperCase();
		String tbFamilyHistory = request.getParameter("ftbh").toUpperCase();
		String eptbSuspect = request.getParameter("eptb").toUpperCase();
		
		String fever = request.getParameter("fev");
		if(fever!=null)
			fever = fever.toUpperCase();
		String nightSweat = request.getParameter("ns");
		if(nightSweat!=null)
			nightSweat = nightSweat.toUpperCase();
		String weightLoss = request.getParameter("wl");
		if(weightLoss!=null)
			weightLoss = weightLoss.toUpperCase();
		String haemoptysis = request.getParameter("ha");
		if(haemoptysis!=null)
			haemoptysis = haemoptysis.toUpperCase();
		String howhear = request.getParameter("hhr");
		if(howhear!=null)
			howhear = howhear.toUpperCase();
		String advertisement = request.getParameter("advt");
		if(advertisement==null)
			advertisement="NO|NO|NO|NO|NO|NO|NO|NO|NO";
		/*if(advertisement != null)
			advertisement = advertisement.toUpperCase();
		String advertisementSeen = request.getParameter("advSeen");
		if(advertisementSeen!=null)
			advertisementSeen = advertisementSeen.toUpperCase();
		String whotold = request.getParameter("whtld");
		if(whotold != null)
			whotold = whotold.toUpperCase();
		String consentRead = request.getParameter("cr");
		if(consentRead != null) {
			consentRead = consentRead.toUpperCase();
		}
		String conclusion = request.getParameter("conc").toUpperCase();

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		System.out.println(startDate);
		System.out.println(startTime);
		System.out.println(endTime);
		System.out.println(enteredDate);

		if(cough==null || v==null) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara form save karain");
		}
		
		if(firstName==null)
			firstName = "";
		
		if(lastName==null)
			lastName = "";
		
		if(advertisementSeen!=null && advertisementSeen.equals("YES")) {
			advSeen = true;
		}
		
		else {
			advSeen  = false;
		}
		
		Date enterDate =  null;
		try {
			enterDate = DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		////Screening is saved in the end
		if(conclusion.equalsIgnoreCase("suspect")){
			
			if(gpId==null || firstName==null || lastName==null) {
				return XmlUtil.createErrorXml("Phone update karain aur dobara form save karain");
			}
			Person checkPerson = null;
			isSuspect = true;
			
			try {
				checkPerson = ssl.findPerson(id);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			
			if(checkPerson!=null) {
				return XmlUtil.createErrorXml("Person with ID " + id + " already exists!");
			}
			
			Date dob = getDOBFromAge(Integer.parseInt(age));
			
			Person p = new Person();
			p.setPid(id);
			p.setFirstName(firstName.toUpperCase());
			p.setLastName(lastName.toUpperCase());
			p.setGender(sex.toUpperCase().charAt(0));
			p.setDob(dob);
			p.setRoleInSystem("PATIENT");
	
			boolean pCreated = true;
			try {
				pCreated = ssl.savePerson(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			if (!pCreated) {
				return XmlUtil.createErrorXml("Could not create Person. Please try again");
			}
	
			Patient pat = new Patient();
			pat.setPatientId(id);
			pat.setDateRegistered(new Date());
			
			/*if (conclusion != null && conclusion.equalsIgnoreCase("confirmed")) {			
				pat.setPatientStatus("GP_CONF");
				pat.setDiseaseSuspected(new Boolean(true));
				pat.setDiseaseConfirmed(new Boolean(false));
			}
			else {
				pat.setPatientStatus("SUSPECT");
				pat.setDiseaseSuspected(new Boolean(false));
				pat.setDiseaseConfirmed(new Boolean(false));
				pat.setGpid(gpId.toUpperCase());
			//}
			
			//if (conclusion == null) {
				pat.setChwid(chwId.toUpperCase());
			//}
			//
	
				Boolean patCreated = null;
			try {
				patCreated = ssl.savePatient(pat);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			if (!patCreated) {
				return XmlUtil.createErrorXml("Could not create Patient. Please try again");
			}
		EncounterId encId = new EncounterId();
		encId.setEncounterId(-1);
		encId.setPid1(id.toUpperCase());
		encId.setPid2(chwId.toUpperCase());
		/*if (conclusion == null)
			encId.setPid2(chwId.toUpperCase());
		else
			encId.setPid2(chwId.toUpperCase());

		Encounter e = new Encounter();
		e.setId(encId);
		//if (conclusion == null)
			e.setEncounterType("SUSPECT_ID");
		//else
		//	e.setEncounterType("GP_NEW");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		// EncounterResultsId erId = new
		// EncounterResultsId(e.getId().getEncounterId(), e.getId().getPid1(),
		// e.getId().getPid2(), "entered_date");
		// EncounterResults er = new EncounterResults(erId, enteredDate);

		ArrayList<String> encounters = new ArrayList<String>();
		
		encounters.add("ENTERED_DATE="+enteredDate);
		encounters.add("AGE="+age);
		encounters.add("COUGH="+cough.toUpperCase());
		if(coughDuration!=null) {
			encounters.add("COUGH_DURATION="+coughDuration.toUpperCase());
			encounters.add("PRODUCTIVE_COUGH="+productiveCough.toUpperCase());
		}
		encounters.add("TB_HISTORY="+tbHistory.toUpperCase());
		encounters.add("TB_FAMILY_HISTORY="+tbFamilyHistory.toUpperCase());
		encounters.add("EPTB_SUSPECT="+eptbSuspect.toUpperCase());
		if(gpId!=null) {
			encounters.add("GP_ID="+gpId.toUpperCase());
		}
		if(fever!=null) {
			encounters.add("FEVER="+fever.toUpperCase());
		}
		if(nightSweat!=null) {
			encounters.add("NIGHT_SWEATS="+nightSweat.toUpperCase());
		}
		
		if(weightLoss!=null) {
			encounters.add("WEIGHT_LOSS="+weightLoss.toUpperCase());
		}
		
		if(haemoptysis!=null) {
			encounters.add("HAEMOPTYSIS="+haemoptysis.toUpperCase());
		}
		
		if(howhear!=null) {
			encounters.add("TB_AWARENESS="+howhear.toUpperCase());
		}
		
		if(advertisement!=null) {
			EncounterResults advertismentResult = ModelUtil.createEncounterResult(e,
					"advertisment", advertisement.toUpperCase());
			encounters.add(advertismentResult);
			
			String[] advtArray = advertisement.split("\\|");
		
		
			encounters.add("BILLBOARD=" + advtArray[0]);
			encounters.add("BANNER="+advtArray[1]);
			encounters.add("POSTER="+advtArray[2]);
			encounters.add("PAMPHLET="+advtArray[3]);
			encounters.add("TV_AD="+advtArray[4]);
			encounters.add("FLYER="+advtArray[5]);
			encounters.add("TICKER_TAPE="+advtArray[6]);
			encounters.add("DON'T KNOW="+advtArray[7]);
			encounters.add("REFUSED="+advtArray[8]);
		}
		
		if(advertisementSeen!=null) {
			encounters.add("ADVERTISEMENT_SEEN="+advertisementSeen.toUpperCase());
		}
		
		if(whotold!=null) {
			encounters.add("AWARENESS_SOURCE="+whotold.toUpperCase());
			
		}
		
		if (conclusion != null) {
			encounters.add("CONCLUSION="+conclusion.toUpperCase());
		}
		
		if (consentRead != null) {
			encounters.add("CONSENT_READ="+consentRead.toUpperCase());
		}
		
		try {
			ssl.saveEncounterWithResults(e, encounters);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			return XmlUtil.createErrorXml("ERROR");
		}
		
		
		}
//ideally it should not happen. Existing screening id must be in person table
		Screening scr = null;
		
		scr = new Screening(id, chwId,Integer.parseInt(age),sex.charAt(0),cough,coughDuration,
				productiveCough,fever,nightSweat,weightLoss,haemoptysis,tbHistory,tbFamilyHistory,howhear,eptbSuspect,whotold,
				advSeen,advertisement,consentRead,isSuspect,enterDate,encounterStartDate,encounterEndDate);;
		//add eptb to constructor,object,xml,database
		Boolean issaved = null;
		try {
			issaved = ssl.saveScreening(scr);
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		
		if(issaved==null || !issaved) {
			return XmlUtil.createErrorXml("Screening was not saved. Try again.");
		}
		
		//ssl.sendAlertsOnScreening(scr);
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}*/

	// ******************************************
	// doSuspectVerify*******************************/
	/*private String doSuspectVerify() {
		
		
		String xml = null;
		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid");
		String gpId = request.getParameter("gpid");
		String seen = request.getParameter("seen");
		String whyNotSeen = request.getParameter("wns");
		String conclusion = request.getParameter("conc");
		String whyNotVerfied = request.getParameter("wnv");
		
		String takenTreatment = request.getParameter("tt");
		String whereTaken = request.getParameter("wt");
		
		String diagBefore = request.getParameter("pd");
		String whereDiagnosed = request.getParameter("wd");
		
		String indusLocation = request.getParameter("il");
		String whereAtIndus = request.getParameter("wl");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		Patient pat = null;
		try {
			pat = (Patient) (ssl.findPatient(id));
		} catch (Exception e3) {
			//  Auto-generated catch block
			e3.printStackTrace();
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		if(pat.getPatientStatus()!=null && !pat.getPatientStatus().equals("GP_CONF")) {
			return XmlUtil.createErrorXml("Patient not in CONFIRMED state! Verification may already have been done!");
		}

		if (conclusion.equalsIgnoreCase("Verified")) {
			
			pat.setPatientStatus("VERIFIED");
			
		}

		else {
			
			pat.setPatientStatus("NO_VERIFY");
		}

		pat.setMonitorId(chwId);
		boolean patUpdate = false;
		try {
			patUpdate = ssl.updatePatient(pat);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
		}
		
		if(!patUpdate) {
			return XmlUtil.createErrorXml("Could not update patient with ID: "  + id);
		}

		EncounterId encId = new EncounterId();
		encId.setEncounterId(-1);
		encId.setPid1(id);
		encId.setPid2(chwId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("SUSPECTVER");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);
		
		EncounterResults gpIdResult = ModelUtil.createEncounterResult(e,
				"gp_id".toUpperCase(), gpId.toUpperCase());
		encounters.add(gpIdResult);

		EncounterResults seenResult = ModelUtil.createEncounterResult(e,
				"patient_seen".toUpperCase(), seen.toUpperCase());
		encounters.add(seenResult);

		if (seen.equals("No")) {
			EncounterResults whyNotSeenResult = ModelUtil
					.createEncounterResult(e, "why_not_seen".toUpperCase(),
							whyNotSeen.toUpperCase());
			encounters.add(whyNotSeenResult);
		}
		//----------------
		
		else {
			
			if(takenTreatment!=null) {
				EncounterResults takenTreatmentResult = ModelUtil
				.createEncounterResult(e, "taken_treatment".toUpperCase(),
						takenTreatment.toUpperCase());
				encounters.add(takenTreatmentResult);
			}
			if (whereTaken != null) {
				EncounterResults whereTreatmentResult = ModelUtil
						.createEncounterResult(e, "where_treatment"
								.toUpperCase(), whereTaken.toUpperCase());
				encounters.add(whereTreatmentResult);
			}

			if(diagBefore!=null) {
				EncounterResults diagBeforeResult = ModelUtil
				.createEncounterResult(e, "previous_diagnosis"
							.toUpperCase(), diagBefore.toUpperCase());
				encounters.add(diagBeforeResult);
			}
			
			if (whereDiagnosed != null) {
				EncounterResults whereDiagnosedResult = ModelUtil
						.createEncounterResult(e, "where_diagnosed"
								.toUpperCase(), whereDiagnosed.toUpperCase());
				encounters.add(whereDiagnosedResult);
			}

			if(indusLocation != null) {
				EncounterResults indusEncounterResults = ModelUtil
				.createEncounterResult(e, "indus_location".toUpperCase(),
						indusLocation.toUpperCase());
				encounters.add(indusEncounterResults);
			}
			if (whereAtIndus != null) {
				EncounterResults otherIndusResult = ModelUtil
						.createEncounterResult(e, "other_indus_location"
								.toUpperCase(), whereAtIndus.toUpperCase());
				encounters.add(otherIndusResult);
			}
		
		}
		//----------------

		EncounterResults concResult = ModelUtil.createEncounterResult(e,
				"conclusion".toUpperCase(), conclusion.toUpperCase());
		encounters.add(concResult);

		if (conclusion.equals("Not Verified")) {
			EncounterResults whyNotVerifiedResult = ModelUtil
					.createEncounterResult(e, "why_not_verified".toUpperCase(),
							whyNotVerfied.toUpperCase());
			encounters.add(whyNotVerifiedResult);
		}

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}

		}

		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	// ******************************************
	// doPatientInfo*******************************/

	/*private String doPatientInfo() {
		String xml = null;
		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid");
		String nic = request.getParameter("nic");
		String nicType = request.getParameter("nt");
		String nicOwner = request.getParameter("no");
		String fatherFirstName = request.getParameter("ffn");
		String fatherLastName = request.getParameter("fln");

		String dob = request.getParameter("dob");
		String age = request.getParameter("age");
		String sex = request.getParameter("sex");
		String maritalStatus = request.getParameter("ms");
		String religion = request.getParameter("rel");
		String ethnicity = request.getParameter("eth");
		String phone = request.getParameter("phn");
		String numPpl = request.getParameter("ppl"); // <----------
		String numAdult = request.getParameter("adl"); // <--------
		String numChildren = request.getParameter("chl");// <------
		String houseNumber = request.getParameter("hn");
		String streetName = request.getParameter("sn");
		String sectorName = request.getParameter("sec");
		String colonyName = request.getParameter("cn");
		String townName = request.getParameter("tn");
		String landmark = request.getParameter("lm");
		String uc = request.getParameter("uc"); // <--------
		String gasMeter = request.getParameter("gmn");
		String pastTBDrugs = request.getParameter("ptd");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		// /////////////////////
		
		if(pastTBDrugs==null) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara form save karain");
		}

		Person person = null;
		Contact c = null;
		try {
			person = (Person) (ssl.findPerson(id));
		} catch (Exception e3) {
			
			e3.printStackTrace();
		}

		if(person==null) {
			return XmlUtil.createErrorXml("Could not find Person with ID: " + id);
		}
		
		person.setNic(nic);
		if (nicOwner != null)
			person.setNicownerName(nicOwner.toUpperCase());
		person.setGuardianName(fatherFirstName.toUpperCase() + " "
				+ fatherLastName.toUpperCase());
		try {
			person.setDob(DateTimeUtil.getDateFromString(dob,
					DateTimeUtil.DOB_FORMAT));
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		person.setGender(sex.toUpperCase().charAt(0));
		person.setMaritalStatus(maritalStatus.toUpperCase());
		person.setReligion(religion.toUpperCase());
		person.setCaste(ethnicity.toUpperCase());

		c = new Contact();
		c.setPid(person.getPid());
		c.setAddressHouse(houseNumber.toUpperCase());
		c.setAddressStreet(streetName.toUpperCase());
		c.setAddressSector(sectorName.toUpperCase());
		c.setAddressColony(colonyName.toUpperCase());
		c.setAddressTown(townName.toUpperCase());
		c.setAddressLandMark(landmark.toUpperCase());
		c.setAddressUc(uc.toUpperCase());
		if (numAdult != null) {
			c.setHouseHoldAdults(new Integer(numAdult));
		}
		if (numChildren != null) {
			c.setHouseHoldChildren(new Integer(numChildren));
		}
		
		if(phone!=null) {
			c.setPhone(phone);
		}
		
		if(gasMeter!=null) {
			c.setMeterNo(gasMeter);
		}

		boolean pUpdated = true;

		try {
			ssl.updatePerson(person);
		} catch (Exception e3) {
			//  Auto-generated catch block
			e3.printStackTrace();
		}
		if(!pUpdated) {
			return XmlUtil.createErrorXml("Could not update Person Data! Please try again");
		}
		boolean cUpdated = true;
		try {
			cUpdated = ssl.saveContact(c);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		if(!cUpdated) {
			return XmlUtil.createErrorXml("Could not save Person Address! Please try again");
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(chwId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("P_INFO");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);

		ArrayList<String> encounters = new ArrayList<String>();
		
		encounters.add("ENTERED_DATE="+enteredDate);
		encounters.add("PAST_TB_DRUG_HISTORY_D1="+pastTBDrugs.toUpperCase());
		
		try {
			ssl.saveEncounterWithResults(e, encounters);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			return XmlUtil.createErrorXml("ERROR");
		}
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;

	}

	/*private String doContactInfo() {
		String xml = null;
		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid");
		
		
		
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		return xml;
	}*/
	
	// ******************************************
	// doPatientTBInfo*******************************/
	//
	/*private String doPatientTBInfo() {
		String xml = null;

		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid");
		String currentFamilyHistory = request.getParameter("cfh");
		String currentFamilyHistoryMembers = null;
		if (currentFamilyHistory.equals("Yes")) {
			currentFamilyHistoryMembers = request.getParameter("cfm");
		}

		String previousFamilyHistory = request.getParameter("pfh");
		String previousFamilyHistoryMembers = null;
		if (previousFamilyHistory.equals("Yes")) {
			previousFamilyHistoryMembers = request.getParameter("pfm");
		}

		String pastTBDrugs = request.getParameter("ptd");
		String pastTBDrugsTime = null;
		if (pastTBDrugs.equals("Yes")) {
			pastTBDrugsTime = request.getParameter("pdttm");
		}

		String pastInterruptedTreatment = request.getParameter("it");
		String pastInterruptedTime = null;
		if (pastInterruptedTreatment.equals("Yes")) {
			pastInterruptedTime = request.getParameter("ittm");
		}

		String streptoTaken = request.getParameter("strep");
		String whyStrept = null;
		String streptTime = null;

		if (streptoTaken.equals("Yes")) {
			whyStrept = request.getParameter("strepd");
			streptTime = request.getParameter("strept");
		}

		String tabletTaken = request.getParameter("tab");
		String whyTab = null;
		String tabTime = null;

		if (tabletTaken.equals("Yes")) {
			whyTab = request.getParameter("tabd");
			tabTime = request.getParameter("tabt");
		}

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		
		Boolean exists = null;
		
		try {
			exists = ssl.exists("Encounter"," where PID1='" + id + "' AND EncounterType='TB_HISTORY'");
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		if(exists==null) {
			return XmlUtil.createErrorXml("Error tracking Patient ID");
		}
		
		else if(exists.booleanValue()==true) {
			return XmlUtil.createErrorXml("Patient " + id + " ka D2 form pehlay bhar diya gaya hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}
		

		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (pat == null) {
			return XmlUtil.createErrorXml("Could not update find Patient with id " + id +"! Please try again");
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(chwId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("TB_HISTORY");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		boolean eCreated = true;
		try {
			eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(!eCreated) {
			return XmlUtil.createErrorXml("Could not save Encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		EncounterResults currentFamilyResult = ModelUtil.createEncounterResult(
				e, "current_history".toUpperCase(), currentFamilyHistory
						.toUpperCase());
		encounters.add(currentFamilyResult);

		if (currentFamilyHistoryMembers != null) {
			EncounterResults currentFamilyMembersResult = ModelUtil
					.createEncounterResult(e, "current_family_members"
							.toUpperCase(), currentFamilyHistoryMembers
							.toUpperCase());
			encounters.add(currentFamilyMembersResult);
		}

		EncounterResults previousFamilyResult = ModelUtil
				.createEncounterResult(e, "previous_history".toUpperCase(),
						previousFamilyHistory.toUpperCase());
		encounters.add(previousFamilyResult);

		if (previousFamilyHistoryMembers != null) {
			EncounterResults previousFamilyMembersResult = ModelUtil
					.createEncounterResult(e, "previous_family_members"
							.toUpperCase(), previousFamilyHistoryMembers
							.toUpperCase());
			encounters.add(previousFamilyMembersResult);
		}
		// ------
		EncounterResults pastTBDrugsResult = ModelUtil
				.createEncounterResult(e, "past_tb_drug_history".toUpperCase(),
						pastTBDrugs.toUpperCase());
		encounters.add(pastTBDrugsResult);

		if (pastTBDrugsTime != null) {
			EncounterResults pastTBDrugsTimeResult = ModelUtil
					.createEncounterResult(e, "past_tb_drug_duration"
							.toUpperCase(), pastTBDrugsTime.toUpperCase());
			encounters.add(pastTBDrugsTimeResult);
		}

		// ------

		EncounterResults interruptedTreatmentResult = ModelUtil
				.createEncounterResult(e, "interrtupted_tb_treatment"
						.toUpperCase(), pastInterruptedTreatment.toUpperCase());
		encounters.add(interruptedTreatmentResult);

		if (pastInterruptedTime != null) {
			EncounterResults interruptedTreatmentTimeResult = ModelUtil
					.createEncounterResult(e,
							"interrtupted_tb_treatment_duration".toUpperCase(),
							pastInterruptedTime.toUpperCase());
			encounters.add(interruptedTreatmentTimeResult);
		}

		// -------

		EncounterResults streptoTakenResult = ModelUtil.createEncounterResult(
				e, "taken_streptomycin".toUpperCase(), streptoTaken
						.toUpperCase());
		encounters.add(streptoTakenResult);

		if (whyStrept != null) {
			EncounterResults whyStreptoTakenResult = ModelUtil
					.createEncounterResult(e, "why_streptomycin".toUpperCase(),
							whyStrept.toUpperCase());
			encounters.add(whyStreptoTakenResult);

			EncounterResults streptoTimeResult = ModelUtil
					.createEncounterResult(e, "streptomycin_duration"
							.toUpperCase(), streptTime.toUpperCase());
			encounters.add(streptoTimeResult);
		}

		// --------

		EncounterResults tabTakenResult = ModelUtil.createEncounterResult(e,
				"taken_tablets".toUpperCase(), tabletTaken.toUpperCase());
		encounters.add(tabTakenResult);

		if (whyTab != null) {
			EncounterResults whyTabTakenResults = ModelUtil
					.createEncounterResult(e, "why_tablets".toUpperCase(),
							whyTab.toUpperCase());
			encounters.add(whyTabTakenResults);

			EncounterResults tabTimeResult = ModelUtil.createEncounterResult(e,
					"tablets_duration".toUpperCase(), tabTime.toUpperCase());
			encounters.add(tabTimeResult);
		}

		// -------

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}

		}

		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	// ******************************************
	// doPatientGPS*******************************/

	/*private String doPatientGPSInfo() {
		String xml = null;

		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid");
		String structNum = request.getParameter("sn");
		String lat = request.getParameter("lat");
		String lng = request.getParameter("lng");
		String encType = request.getParameter("enc");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		// /////////////////////

		Person person = null;
		Contact c = null;

		if(!encType.equals("Sign-in")) {
		try {
			person = (Person) (ssl.findPerson(id));

		} catch (Exception e3) {
			//  Auto-generated catch block
			e3.printStackTrace();
		}

		if(person==null) {
			return XmlUtil.createErrorXml("Person with id " + id + " does not exist. Please recheck ID and try again.");
		}
		}
		Float latFloat = null;
		Float lngFloat = null;
		
		if(lat!=null)
			latFloat = Float.valueOf(lat);
		
		if(lng!=null)
			lngFloat = Float.valueOf(lng);
		

		// gpsc.setLat(latFloat);
		// gpsc.setLng(lngFloat);

		if (encType.equals("Baseline")) {
			try {
				c = ssl.findContact(id);
			} catch (Exception e) {
				//  Auto-generated catch block
				e.printStackTrace();
			}
			
			if(c==null) {
				return XmlUtil.createErrorXml("Contact for patient with id " + id + " does not exist. Please recheck ID and try again.");
			}
			
			if (structNum != null)
				c.setIrdstructureNo(structNum);

			c.setAddressLocationLat(latFloat);
			c.setAddressLocationLon(lngFloat);
			

			try {
				ssl.updateContact(c);
			} catch (Exception e) {
				//  Auto-generated catch block
				e.printStackTrace();
				return XmlUtil.createErrorXml("Could not update Contact for Patient with id: "+ id + "! Please try again!");
			}
		}
		
		

		EncounterId encId = new EncounterId();

		if (!encType.equals("Sign-in"))
			encId.setPid1(id);
		else
			encId.setPid1(chwId);

		encId.setPid2(chwId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("GPS");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			XmlUtil.createErrorXml("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		if (structNum != null && structNum.length() != 0) {
			EncounterResults snResult = ModelUtil.createEncounterResult(e,
					"struct_number".toUpperCase(), structNum.toUpperCase());
			encounters.add(snResult);
		}

		if (lat != null) {
			EncounterResults latResult = ModelUtil.createEncounterResult(e,
					"gps_lat".toUpperCase(), lat.toUpperCase());
			encounters.add(latResult);
		}

		if (lng != null) {
			EncounterResults lngResult = ModelUtil.createEncounterResult(e,
					"gps_long".toUpperCase(), lng.toUpperCase());
			encounters.add(lngResult);
		}

		EncounterResults etResult = ModelUtil.createEncounterResult(e,
				"enc_type".toUpperCase(), encType.toUpperCase());
		encounters.add(etResult);

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR: Please try again");
			}

		}
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	/*private String doPatientFollowupEffort(){
		String xml = null;
		String id = request.getParameter("id");
		String chwOrmMonId = request.getParameter("chw-mon-id");
		String pstatus = request.getParameter("pstat");
		String effortreason = request.getParameter("effrsn");

		String month = request.getParameter("mon");
		String efforttype = request.getParameter("efftype");
		String calloutcome = request.getParameter("coutcm");
		String otherCalloutcome = request.getParameter("coutcmother");
		String hholdvisitoutcome = request.getParameter("hhvisoutcm");
		String otherHhholdVisitoutcome = request.getParameter("hhvisoutcmother");

		String patientspoke = request.getParameter("patspok");
		String otherPatientSpoke = request.getParameter("patspokother");
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		
		
		
	//chw-mon-id check
	Boolean uCheck = false;
			try {
				uCheck = ssl.exists("Users", "where PID = '" + chwOrmMonId.toUpperCase() + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(uCheck.booleanValue()!=true){
				return XmlUtil.createErrorXml("Aap ne ghalat CHW ID ya Monitor ID darj kia hai. Sahih ID enter karain aur dobara koshish karain");
			}
				
		
		
		

		EncounterId encId = new EncounterId();
		encId.setEncounterId(-1);
		encId.setPid1(id.toUpperCase());
		encId.setPid2(chwOrmMonId.toUpperCase());

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("FUP_EFFORT");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Could not create Encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate.toUpperCase());
		encounters.add(dateResult);

		EncounterResults pStatusResult = ModelUtil.createEncounterResult(e,
				"patient_status".toUpperCase(), pstatus.toUpperCase());
		encounters.add(pStatusResult);
		
		EncounterResults effortreasonResult = ModelUtil.createEncounterResult(e,
				"effort_reason".toUpperCase(), effortreason.toUpperCase());
		encounters.add(effortreasonResult);
		
		EncounterResults monthResult = ModelUtil.createEncounterResult(e,
				"month".toUpperCase(), (month == null ? "" : month.toUpperCase()) );
		encounters.add(monthResult);

		EncounterResults efforttypeResult = ModelUtil.createEncounterResult(e,
				"effort_type".toUpperCase(), efforttype.toUpperCase());
		encounters.add(efforttypeResult);
		
		EncounterResults calloutcomeResult = ModelUtil.createEncounterResult(e,
				"call_outcome".toUpperCase(), (calloutcome == null ? "" : calloutcome.toUpperCase()) );
		encounters.add(calloutcomeResult);
		
		EncounterResults otherCalloutcomeResult = ModelUtil.createEncounterResult(e,
				"other_call_outcome".toUpperCase(), (otherCalloutcome == null ? "" : otherCalloutcome.toUpperCase()) );
		encounters.add(otherCalloutcomeResult);
		
		EncounterResults patientspokeResult = ModelUtil.createEncounterResult(e,
				"patient_reply".toUpperCase(), (patientspoke == null ? "" : patientspoke.toUpperCase()));
		encounters.add(patientspokeResult);
		
		EncounterResults otherPatientspokeResult = ModelUtil.createEncounterResult(e,
				"other_patient_reply".toUpperCase(), (otherPatientSpoke == null ? "" : otherPatientSpoke.toUpperCase()));
		encounters.add(otherPatientspokeResult);
		
		EncounterResults hholdvisitoutcomeResult = ModelUtil.createEncounterResult(e,
				"household_visit_outcome".toUpperCase(), (hholdvisitoutcome == null ? "" : hholdvisitoutcome.toUpperCase()));
		encounters.add(hholdvisitoutcomeResult);

		EncounterResults otherHholdvisitoutcomeResult = ModelUtil.createEncounterResult(e,
				"other_household_visit_outcome".toUpperCase(), (otherHhholdVisitoutcome == null ? "" : otherHhholdVisitoutcome.toUpperCase()));
		encounters.add(otherHholdvisitoutcomeResult);
		
		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}
		}

		xml = XmlUtil.createSuccessXml();
		return xml;
	}
	// ******************************************
	// doRefusal*******************************/

	/*private String doRefusal() {
		String xml = null;
		String id = request.getParameter("id");
		String mid = request.getParameter("mid");
		String pStatus = request.getParameter("ps");
		String whatRefused = request.getParameter("wr");
		String collectionMonth = request.getParameter("cm");
		String whichSample = request.getParameter("ws");

		String reason = request.getParameter("rr");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		EncounterId encId = new EncounterId();
		encId.setEncounterId(-1);
		encId.setPid1(id);
		encId.setPid2(mid);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("REFUSAL");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			XmlUtil.createErrorXml("Could not create Encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate.toUpperCase());
		encounters.add(dateResult);

		EncounterResults pStatusResult = ModelUtil.createEncounterResult(e,
				"patient_status".toUpperCase(), pStatus.toUpperCase());
		encounters.add(pStatusResult);
		
		EncounterResults whatRefusedResult = ModelUtil.createEncounterResult(e,
				"what_refused".toUpperCase(), whatRefused.toUpperCase());
		encounters.add(whatRefusedResult);

		if (whatRefused.equals("Sputum Collection")) {
			EncounterResults sputumMonthResult = ModelUtil
					.createEncounterResult(e, "sputum_month".toUpperCase(),
							collectionMonth.toUpperCase());
			encounters.add(sputumMonthResult);

			EncounterResults sampleNumberResult = ModelUtil
					.createEncounterResult(e, "sample_no".toUpperCase(),
							whichSample.toUpperCase());
			encounters.add(sampleNumberResult);

		}

		if (reason != null) {

			EncounterResults reasonResult = ModelUtil.createEncounterResult(e,
					"reason".toUpperCase(), reason.toUpperCase());
			encounters.add(reasonResult);
		}

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}

		}

		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	// ******************************************
	// doSputumCollection*******************************/
	/*private String doSputumCollection() {
		String xml = null;

		String v= request.getParameter("v");
		
		
		String id = request.getParameter("id");

		String mid = request.getParameter("mid");

		String patientStatus = request.getParameter("ps");
		String sputumMonth = request.getParameter("scm");
		String sampleNumber = request.getParameter("ws");
		String sputumCollected = request.getParameter("sc");
		String barCode = request.getParameter("sbc");
		String doSmear = request.getParameter("smear");
		String doXpert = request.getParameter("xpert");


		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		if(v==null || sputumMonth==null || sputumMonth.length()==0) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara koshish karain");
		}
		
		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		if(barCode!=null && barCode.length()!=0) {
			Boolean exists = null;
			
			try {
				exists = ssl.exists("EncounterResults", " where Element='SAMPLE_BARCODE' AND Value='" + barCode + "'");
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			if(exists==null) {
				System.out.println("null");
				return XmlUtil.createErrorXml("Error tracking Bar Code Number. Please try again!");
			}
			
			else if(exists.booleanValue()==true) {
				System.out.println("true");
				return XmlUtil.createErrorXml("This Bar Code has already been collected. Please recheck Bar Code and try again");
			}
			
			exists = null;
		
			try {
				exists = ssl.exists("SputumResults", " where SputumTestID='" + barCode + "'");
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			if(exists==null) {
				System.out.println("null");
				return XmlUtil.createErrorXml("Error tracking Bar Code Number. Please try again!");
			}
			
			else if(exists.booleanValue()==true) {
				System.out.println("true");
				return XmlUtil.createErrorXml("This Bar Code has already been collected. Please recheck Bar Code and try again");
			}
			
			//if not found in SputumResults check in ContactSputumResults
			if(exists.booleanValue()==false) {
				try {
					exists = ssl.exists("ContactSputumResults", " where SputumTestID='" + barCode + "'");
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			
				if(exists==null) {
					System.out.println("null");
					return XmlUtil.createErrorXml("Error tracking Bar Code Number. Please try again!");
				}
			
				else if(exists.booleanValue()==true) {
					System.out.println("true");
					return XmlUtil.createErrorXml("This Bar Code has already been collected. Please recheck Bar Code and try again");
				}	
			}
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(mid);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("SPUTUM_COL");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Error occurred. Please try again");
		}
		
		//if(barCode!=null && barCode.length()!=0) {
			//SputumResultsId sri1 = new SputumResultsId();
			String encMonth = "";
			System.out.println("SPUTUM MONTH---->" + sputumMonth);
			
			if(sputumMonth.equals("0")) {
				encMonth = "BASELINE";
			}
			
			else if(sputumMonth.equals("1")) {
				encMonth = "1ST";
			}
			
			else if(sputumMonth.equals("2")) {
				encMonth = "2ND";
			}
			
			else if(sputumMonth.equals("3")) {
				encMonth = "3RD";
			}
			
			else {
				encMonth = sputumMonth + "TH";
			}
		//}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		EncounterResults patientStatusResult = ModelUtil.createEncounterResult(
				e, "patient_status".toUpperCase(), patientStatus.toUpperCase());
		encounters.add(patientStatusResult);

		EncounterResults collectionMonthResult = ModelUtil
				.createEncounterResult(e, "collection_month".toUpperCase(),
						encMonth.toUpperCase());
		encounters.add(collectionMonthResult);

		EncounterResults suspectSampleNumberResult = ModelUtil
				.createEncounterResult(e, "suspect_sample".toUpperCase(),
						sampleNumber.toUpperCase());
		encounters.add(suspectSampleNumberResult);

		EncounterResults sputumCollectedResult = ModelUtil
				.createEncounterResult(e, "sputum_collected".toUpperCase(),
						sputumCollected.toUpperCase());
		encounters.add(sputumCollectedResult);

		if (barCode != null) {
			EncounterResults barcodeResult = ModelUtil.createEncounterResult(e,
					"sample_barcode".toUpperCase(), barCode.toUpperCase());
			encounters.add(barcodeResult);
			
		if(doSmear!=null) {	
			EncounterResults doSmearResult = ModelUtil.createEncounterResult(e,
					"do_smear".toUpperCase(), doSmear.toUpperCase());
			encounters.add(doSmearResult);
		
		}
		
		if(doXpert!=null) {
			
			EncounterResults doXpertResult = ModelUtil.createEncounterResult(e,
					"do_xpert".toUpperCase(), doXpert.toUpperCase());
			encounters.add(doXpertResult);
		}
		}

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("Error occurred. Please try again");
			}

		}

		// //TODO: Modify for three tables:
		//If baseline: Add to SputumResults, GXPert Results, Chest Xray
		//Else: Just add to SputumResults
		if(barCode!=null && barCode.length()!=0) {
			//SputumResultsId sri1 = new SputumResultsId();
			int month = -1;
			
			if(sputumMonth.equals("Baseline")) {
				month = 0;
			}
			
			else if(sputumMonth.equals("2nd")) {
				month = 2;
			}
			
			else if(sputumMonth.equals("3rd")) {
				month = 3;
			}
			
			else if(sputumMonth.equals("5th")) {
				month = 5;
			}
			
			else if(sputumMonth.equals("7th")) {
				month = 7;
			}
			
			//sri1.setPatientId(id);
			//sri1.setSputumTestId(Integer.parseInt(barCode));
			if (doSmear != null && doSmear.equalsIgnoreCase("yes")) {
				SputumResults sr = new SputumResults();
				sr.setSputumTestId(Integer.parseInt(barCode));
				sr.setPatientId(id);
				sr.setMonth(Integer.parseInt(sputumMonth));
				sr.setIrs(0);
				// sr.setDateSubmitted(encounterStartDate);

				try {
					ssl.saveSputumResults(sr);
				} catch (Exception e2) {
					e2.printStackTrace();
					return XmlUtil
							.createErrorXml("Error saving Sputum Results. Please try again");
				}
			}
			if(doXpert!=null && doXpert.equalsIgnoreCase("yes")) {
				
				//GXP
				GeneXpertResults gxp = new GeneXpertResults();
				gxp.setIsPositive(new Boolean(false));
				gxp.setPatientId(id);
				gxp.setSputumTestId(Integer.parseInt(barCode));
				gxp.setIrs(0);
				
				try {
					ssl.saveGeneXpertResults(gxp);
				} catch (Exception e1) {
					e1.printStackTrace();
					return XmlUtil.createErrorXml("Error saving GeneXpert Results. Please try again");
				}
				
				
			
			}
		}
		
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	// ******************************************
	// doBaselineTreatment*******************************/
	/*private String doBaselineTreatment() {
		String xml = null;

		String id = request.getParameter("id");

		String gpId = request.getParameter("gpid").toUpperCase();

		String height = request.getParameter("ht");
		String weight = request.getParameter("wt");
		String phase = request.getParameter("pt").toUpperCase();
		String patientType = request.getParameter("ptp").toUpperCase();
		String patientCategory = request.getParameter("pc").toUpperCase();
		String diseaseSite = request.getParameter("ds").toUpperCase();
		String regimen = request.getParameter("reg").toUpperCase();
		String regimenType = request.getParameter("mt").toUpperCase();
		String medicationForm = request.getParameter("mf").toUpperCase();
		String tablets = "0";//request.getParameter("tab");
		String streptoDose = "0";//request.getParameter("str");
		String rDose = "0";
		String hDose = "0";
		String zDose = "0";
		String eDose = "0";
		
		if(regimenType.equals("ADULT") || medicationForm.equals("TABLET")) {
			tablets = request.getParameter("tab");
			if(regimen.indexOf('S')!=-1) {
    			streptoDose = request.getParameter("str");
    		}
		}
		
		else if(medicationForm.equals("SYRUP")) {
			
			if(regimen.indexOf('R')!=-1) {
    			rDose = request.getParameter("rdose");
    		}
			
			if(regimen.indexOf('H')!=-1) {
				hDose = request.getParameter("hdose");
    		}
			
			if(regimen.indexOf('Z')!=-1) {
				zDose = request.getParameter("zdose");
    		}
			
			if(regimen.indexOf('E')!=-1) {
				eDose = request.getParameter("edose");
    		}
			
			if(regimen.indexOf('S')!=-1) {
    			streptoDose = request.getParameter("str");
    		}
			
			if(regimen.indexOf('S')!=-1) {
    			streptoDose = request.getParameter("str");
    		}
		}
		
		else if(medicationForm.equals("BOTH")) {
			
			tablets = request.getParameter("tab");
			
			if(regimen.indexOf('R')!=-1) {
    			rDose = request.getParameter("rdose");
    		}
			
			if(regimen.indexOf('H')!=-1) {
				hDose = request.getParameter("hdose");
    		}
			
			if(regimen.indexOf('Z')!=-1) {
				zDose = request.getParameter("zdose");
    		}
			
			if(regimen.indexOf('E')!=-1) {
				eDose = request.getParameter("edose");
    		}
			
			if(regimen.indexOf('S')!=-1) {
    			streptoDose = request.getParameter("str");
    		}
			
			
		}
		
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		

		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}

		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		Boolean exists = null;
		
		try {
			exists = ssl.exists("Encounter"," where PID1='" + id + "' AND EncounterType='BASELINE'");
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			return XmlUtil.createErrorXml("Error tracking Patient ID");
		}
		
		if(exists==null) {
			return XmlUtil.createErrorXml("Error tracking Patient ID");
		}
		
		else if(exists.booleanValue()==true) {
			return XmlUtil.createErrorXml("Patient " + id + " ka Baseline Treatment Form pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}
		
		//add check for Baseline date
		Date checkDate = null;
		java.sql.Date checkSqlDate;
		try {
			checkDate = DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT);
			checkSqlDate = new java.sql.Date(checkDate.getTime());
		}
		
		catch(Exception e) {
			e.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Bad Entered Date");
		}
		
		exists = null;
		
		try {
			exists = ssl.exists("Encounter", " where EncounterType='CDF' AND PID1='" + id + "' AND DateEncounterEntered >'" + checkSqlDate.toString() + "'" );
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if(exists==null) {
			return XmlUtil.createErrorXml("ERROR entering data. Please try again");
		}
		
		else if(exists.booleanValue()==true) {
			return XmlUtil.createErrorXml("ERROR: Is date ke baad is patient ka Q Form bhara ja chuka hai. Agar kisi bhi form mein ghalati huwi hai to TB REACH team se foran rujoo karain");
		}
		
		//

		//pat.setGpid(gpId);
		pat.setHeight(Float.parseFloat(height));
		pat.setWeight(Float.parseFloat(weight));
		pat.setTreatmentPhase(phase.toUpperCase());
		pat.setPatientType(patientType.toUpperCase());
		pat.setDiseaseCategory(patientCategory.toUpperCase());
		pat.setDiseaseSite(diseaseSite.toUpperCase());
		pat.setRegimen(regimen.toUpperCase());
		pat.setMedicationForm(medicationForm);
		pat.setRegimenType(regimenType);
		
		if(tablets!=null)
			pat.setDoseCombination(Float.parseFloat(tablets));
		if (streptoDose != null) {
			pat.setOtherDoseDescription(streptoDose);
		}
		if (rDose != null) {
			pat.setRDose(Float.parseFloat(rDose));
		}
		if (hDose != null) {
			pat.setHDose(Float.parseFloat(hDose));
		}
		if (zDose != null) {
			pat.setZDose(Float.parseFloat(zDose));
		}
		if (eDose != null) {
			pat.setEDose(Float.parseFloat(eDose));
		}
		
		if(pat.getPatientStatus().equals("SUSPENDED")) {
			pat.setPatientStatus("SUSPECT");
		}
		

		try {
			ssl.updatePatient(pat);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
			return XmlUtil.createErrorXml("Could not update Patient. Please try again");
			
			
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(gpId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("BASELINE");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}
		

		ArrayList<String> encounters = new ArrayList<String>();

		encounters.add("ENTERED_DATE="+enteredDate);
		encounters.add("HEIGHT="+height);
		encounters.add("WEIGHT="+weight);
		encounters.add("TREATMENT_PHASE="+phase.toUpperCase());
		encounters.add("PATIENT_TYPE="+patientType.toUpperCase());
		encounters.add("PATIENT_CATEGORY="+patientCategory.toUpperCase());
		encounters.add("DISEASE_SITE="+diseaseSite.toUpperCase());
		encounters.add("REGIMEN="+regimen.toUpperCase());
		encounters.add("REGIMEN_TYPE="+regimenType.toUpperCase());
		encounters.add("MEDICATION_FORM="+medicationForm.toUpperCase());
		encounters.add("FDC_TABLETS="+tablets.toUpperCase());
		
		if (streptoDose != null) {
			encounters.add("STREPOMYCIN="+streptoDose.toUpperCase());
		}
		
		if (rDose != null) {		
			encounters.add("R_DOSE=" + rDose.toUpperCase());
		}
		
		if (hDose != null) {
			encounters.add("H_DOSE=" + hDose.toUpperCase());			
		}
		
		if (zDose != null) {
			encounters.add("Z_DOSE="+ zDose.toUpperCase());	
		}
		
		if (eDose != null) {
			encounters.add("E_DOSE="+eDose.toUpperCase());
		}
		
		try {
			ssl.saveEncounterWithResults(e, encounters);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			return XmlUtil.createErrorXml("ERROR");
		}
		
		//TODO: Send Alert/Incentive
		IncentiveId gpIncentive = new IncentiveId();
		gpIncentive.setPid(gpId);
		gpIncentive.setIncentiveId("GP_BL_VST");
		
		Incentive gpInc = new Incentive();
		gpInc.setId(gpIncentive);
		gpInc.setDateTransferred(new Date(System.currentTimeMillis()));
		gpInc.setStatus("PENDING");


		try {
			ssl.saveIncentive(gpInc);
		} catch (Exception e1) {
			
			e1.printStackTrace();
			System.out.println("ERROR: Could not save Baseline Visit Incentive for GP " + gpId + "/ Patient " + id);
		}
		
		SetupIncentive gpVisitSetupIncentive = null;
		
		try {
			gpVisitSetupIncentive = ssl.findSetupIncentive("GP_BL_VST");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	// ******************************************
	// doFollowupTreatment*******************************/
/*	private String doFollowupTreatment() {
		String xml = null;

		String id = request.getParameter("id");

		String gpId = request.getParameter("gpid");

		String height = request.getParameter("ht");
		String weight = request.getParameter("wt");
		String phase = request.getParameter("pt");
		String month = request.getParameter("mon");
		String regimen = request.getParameter("reg");
		String tablets = request.getParameter("tab");
		String streptoDose = request.getParameter("str");
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		if(month==null) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara form save karain");
		}
		
		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}

		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		//begin dup followup check
		String results[] = null;
		
		try {
			results = ssl.getColumnData("Encounter", "EncounterID",  " where PID1='" + id + "' AND EncounterType='FOLLOW_UP'");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String filledMonth = "";
		String er[] = null;
		if(results!=null)
		{
			for(int i=0; i<results.length;i++) {
				System.out.println((String)(results[i]));
			}
			
			for(int i=0; i<results.length;i++) {
				
					try {
						er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='MONTH' AND EncounterID='" + (String)(results[i]) + "'");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(er!=null && er.length>0)
					{
						filledMonth = er[0];
						if(filledMonth!=null && filledMonth.equalsIgnoreCase(month)) {
							return XmlUtil.createErrorXml("Patient " + id + " ka month " + month + " ka Follow Up Form bhara ja chuka hai. Agar form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
						}
					}
					
			}
			
		}
		
		//end dup Followup check

		pat.setHeight(Float.parseFloat(height));
		pat.setWeight(Float.parseFloat(weight));
		pat.setTreatmentPhase(phase.toUpperCase());

		pat.setRegimen(regimen.toUpperCase());
		// pat.setDoseCombination(Integer.parseInt(tablets)); <----
		//if (streptoDose != null) {
			pat.setOtherDoseDescription(streptoDose);
		//}

		try {
			ssl.updatePatient(pat);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
			
			return XmlUtil.createErrorXml("Could not update Patient. Please try again.");
			
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(gpId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("FOLLOW_UP");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Could not create Encounter. Please try again.");
		}
		

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		EncounterResults heightResult = ModelUtil.createEncounterResult(e,
				"height".toUpperCase(), height);
		encounters.add(heightResult);

		EncounterResults weightResult = ModelUtil.createEncounterResult(e,
				"weight".toUpperCase(), weight);
		encounters.add(weightResult);

		EncounterResults phaseResult = ModelUtil.createEncounterResult(e,
				"treatment_phase".toUpperCase(), phase.toUpperCase());
		encounters.add(phaseResult);

		EncounterResults monthResult = ModelUtil.createEncounterResult(e,
				"month".toUpperCase(), month.toUpperCase());
		encounters.add(monthResult);
		
		EncounterResults regimenResult = ModelUtil.createEncounterResult(e,
				"regimen".toUpperCase(), regimen.toUpperCase());
		encounters.add(regimenResult);

		EncounterResults tabletResult = ModelUtil.createEncounterResult(e,
				"fdc_tablets".toUpperCase(), tablets.toUpperCase());
		encounters.add(tabletResult);

		if (streptoDose != null) {
			EncounterResults streptoResult = ModelUtil.createEncounterResult(e,
					"strepomycin".toUpperCase(), streptoDose.toUpperCase());
			encounters.add(streptoResult);
		}

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR! Please try again");
			}

		}

		//TODO: Send Alert/Incentive
		IncentiveId gpIncentive = new IncentiveId();
		gpIncentive.setPid(gpId);
		gpIncentive.setIncentiveId("GP_FU_VST");
		
		Incentive gpInc = new Incentive();
		gpInc.setId(gpIncentive);


		try {
			ssl.saveIncentive(gpInc);
		} catch (Exception e1) {
			
			e1.printStackTrace();
			System.out.println("ERROR: Could not save Followup Visit Incentive for GP " + gpId + "/ Patient " + id);
		}
		
		
		SetupIncentive gpVisitSetupIncentive = null;
		
		try {
			gpVisitSetupIncentive = ssl.findSetupIncentive("GP_FU_VST");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		try {
			ssl.sendAlertsOnGPVisit(e, gpVisitSetupIncentive);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}*/

	// ******************************************
	// doSuspectConfirm*******************************/
	/*private String doSuspectConfirm() {
		String xml = null;
	
		String id = request.getParameter("id");
	
		String gpId = request.getParameter("gpid");
	
		String cough = request.getParameter("cough");
		String coughDuration = request.getParameter("cd");
		String productiveCough = request.getParameter("pc");
		String tbHistory = request.getParameter("tbh");
		String tbFamilyHistory = request.getParameter("ftbh");
		String fever = request.getParameter("fev");
		String nightSweat = request.getParameter("ns");
		String weightLoss = request.getParameter("wl");
		String haemoptysis = request.getParameter("ha");
		String conclusion = request.getParameter("conc");
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
	
		if(cough==null) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara form save karain");
		}
		
		Patient pat = null;
		try {
			pat = (Patient) (ssl.findPatient(id));
		} catch (Exception e3) {
			//  Auto-generated catch block
			e3.printStackTrace();
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		if(pat.getPatientStatus()!=null && !pat.getPatientStatus().equals("SUSPECT")) {
			return XmlUtil.createErrorXml("Patient not in SUSPECT state! Confirmation may already have been done!");
		}
	
		if (conclusion.equalsIgnoreCase("confirmed")) {
			pat.setDiseaseSuspected(new Boolean(true));
			pat.setPatientStatus("GP_CONF");
		}
	
		else {
			pat.setDiseaseSuspected(new Boolean(false));
			pat.setPatientStatus("GP_NO_CONF");
		}
	
		pat.setGpid(gpId.toUpperCase());
		
		
		try {
			boolean patUpdate = ssl.updatePatient(pat);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
			
			return XmlUtil.createErrorXml("Could not update Patient. Please try again.");
			
		}
	
		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(gpId);
	
		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("SUSPECTCON");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}
	
		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Could not save encounter. Please try again");
			
		}
	
		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();
	
		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);
	
		EncounterResults coughResult = ModelUtil.createEncounterResult(e,
				"cough".toUpperCase(), cough.toUpperCase());
		encounters.add(coughResult);
		
		if(coughDuration!=null) {
			EncounterResults coughDurationResult = ModelUtil.createEncounterResult(e,
					"cough_duration".toUpperCase(), coughDuration.toUpperCase());
			encounters.add(coughDurationResult);
			
			if(productiveCough!=null) {
				EncounterResults productiveCoughResult = ModelUtil.createEncounterResult(e,
						"productive_cough".toUpperCase(), productiveCough.toUpperCase());
				encounters.add(productiveCoughResult);
						
			}
		}
			
		if(coughDuration!=null && productiveCough!=null && (coughDuration.equals("2 to 3 weeks") || coughDuration.equals("more than 3 weeks") ) && productiveCough.equals("Yes")) {
			EncounterResults twoWeeksProdCoughResult = ModelUtil.createEncounterResult(e,
					"two_weeks_cough".toUpperCase(), "YES");
			encounters.add(twoWeeksProdCoughResult);
		}
		
		else {
			EncounterResults twoWeeksProdCoughResult = ModelUtil.createEncounterResult(e,
					"two_weeks_cough".toUpperCase(), "NO");
			encounters.add(twoWeeksProdCoughResult);
		}
		
		EncounterResults tbHistoryResult = ModelUtil.createEncounterResult(e,
				"tb_history".toUpperCase(), tbHistory.toUpperCase());
		encounters.add(tbHistoryResult);
		
		EncounterResults tbFamilyHistoryResult = ModelUtil.createEncounterResult(e,
				"tb_family_history".toUpperCase(), tbFamilyHistory.toUpperCase());
		encounters.add(tbFamilyHistoryResult);
		
		EncounterResults feverResult = ModelUtil.createEncounterResult(e,
				"fever".toUpperCase(), fever.toUpperCase());
		encounters.add(feverResult);
	
		EncounterResults nsResult = ModelUtil.createEncounterResult(e,
				"night_sweats".toUpperCase(), nightSweat.toUpperCase());
		encounters.add(nsResult);
	
		EncounterResults wlResult = ModelUtil.createEncounterResult(e,
				"weight_loss".toUpperCase(), weightLoss.toUpperCase());
		encounters.add(wlResult);
	
		EncounterResults hResult = ModelUtil.createEncounterResult(e,
				"haemoptysis".toUpperCase(), haemoptysis.toUpperCase());
		encounters.add(hResult);
	
	
		EncounterResults concResult = ModelUtil.createEncounterResult(e,
				"conclusion".toUpperCase(), conclusion.toUpperCase());
		encounters.add(concResult);
	
		boolean resultSave = true;
	
		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}
	
			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR! Please try again");
			}
	
		}
		
		
		
		try {
			ssl.sendAlertsOnGPConfirmation(encId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}*/

	// ******************************************
	// doDrugAdm*******************************/
	/*private String doDrugAdm() {
		String xml = null;

		String id = request.getParameter("id");

		String chwId = request.getParameter("chwid");

		String treatmentDate = request.getParameter("dt");
		String doseGroup = request.getParameter("dose");
		String observeDose = request.getParameter("od");
		String notObservedReason = request.getParameter("rsn");
		String whereObserved = request.getParameter("loc");
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");

		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}

		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(chwId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("DRUG_ADM");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(treatmentDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Could not create Encounter. Please try again.");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"treatment_date".toUpperCase(), treatmentDate);
		encounters.add(dateResult);

		EncounterResults doseResult = ModelUtil.createEncounterResult(e,
				"dose_amount".toUpperCase(), doseGroup.toUpperCase());
		encounters.add(doseResult);

		EncounterResults observedResult = ModelUtil.createEncounterResult(e,
				"patient_observed".toUpperCase(), observeDose.toUpperCase());
		encounters.add(observedResult);

		if (observeDose.equals("Yes")) {
			EncounterResults whereObservedResult = ModelUtil
					.createEncounterResult(e, "where_observed".toUpperCase(),
							whereObserved.toUpperCase());
			encounters.add(whereObservedResult);
		} else if (observeDose.equals("No")) {
			EncounterResults whyNotObservedResult = ModelUtil
					.createEncounterResult(e, "why_not_observed".toUpperCase(),
							notObservedReason.toUpperCase());
			encounters.add(whyNotObservedResult);
		}

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR! Please try again");
			}

		}

		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	// ******************************************
	// doEndFollowup*******************************/
	/*private String doEndFollowup() {
		String xml = null;
		String id = request.getParameter("id");

		String gpId = request.getParameter("gpid");

		String reason = request.getParameter("rsn");
		String otherReason = request.getParameter("otrrsn");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		System.out.println("------->" + enteredDate);
		
		if(otherReason==null) {
			otherReason = "";
		}

		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
		}

		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		try {
			pat.setPatientStatus("CLOSED");
			String mr = pat.getMrno();
			
			if(mr!=null && mr.length()!=0)
			{
				mr = "9" + mr;
				PatientDOTS pdots = null;
				try {
					pdots = ssl.findPatientDOTSByMR(pat.getMrno());
				}
				
				catch(Exception e) {
					e.printStackTrace();
				}
				if(pdots!=null)
				{
					String mr2 = pdots.getMrNo();
					if(mr2!=null && mr2.length()!=0 && mr2.charAt(0)!=9) {
						mr2 = "9" + mr2;
						//pdots.setMrNo(mr2);
						//ssl.updatePatientDOTS(pdots);
						HibernateUtil.util.runCommand("update PatientDOTS set MRNo='" + mr2 + "' where MRNo='" + pdots.getMrNo() + "'");
					}
				}
				
			}
			pat.setMrno(mr);
			boolean patUpdated = ssl.updatePatient(pat);
		}
		
		catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
			return XmlUtil.createErrorXml("Error updating patient");
		}
		
		

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(gpId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("END_FOL");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		ArrayList<String> encounters = new ArrayList<String>();

		
		encounters.add("ENTERED_DATE="+enteredDate);
		encounters.add("REASON="+reason.toUpperCase());
		encounters.add("OTHER_REASON="+otherReason.toUpperCase());
		
		try {
			ssl.saveEncounterWithResults(e, encounters);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			return XmlUtil.createErrorXml("ERROR");
		}
		
		
		//send incentive and alerts
		//
		
		
		boolean isCured = false;
		boolean isTreatmentCompleted = false;
		
		
		if(reason.equals("Cured"))
			isCured = true;
		else if(reason.equals("Tx Completed"))
			isTreatmentCompleted = true;
		
		
		
		
		if (isCured || isTreatmentCompleted) {
			

			IncentiveId gpIncentiveId = new IncentiveId();
			IncentiveId chwIncentiveId = new IncentiveId();
			
			gpIncentiveId.setPid(gpId);
			chwIncentiveId.setPid(pat.getChwid());
			
			if(isCured) {
				gpIncentiveId.setIncentiveId("GP_CURED");
				chwIncentiveId.setIncentiveId("CHW_CURED");
			}
			
			else if(isTreatmentCompleted) {
				gpIncentiveId.setIncentiveId("GP_TX_CMP");
				chwIncentiveId.setIncentiveId("CHW_TX_CMP");
			}
			
			Incentive gpIncent = new Incentive();
			gpIncent.setId(gpIncentiveId);
			
			Incentive chwIncent = new Incentive();
			chwIncent.setId(chwIncentiveId);
			
			try {
				ssl.saveIncentive(gpIncent);
			}
			
			catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Could not add END FOLLOWUP " + reason + " incentive for GP: " + gpId + "/Patient " + id);
			}
			
			try {
				ssl.saveIncentive(chwIncent);
			}
			
			catch(Exception ex2) {
				ex2.printStackTrace();
				System.out.println("Could not add END FOLLOWUP " + reason + " incentive for CHW: " + pat.getChwid() + "/Patient " + id);
			}
			
			
			//send alerts
			SetupIncentive gpIncentive = null;
			SetupIncentive chwIncentive = null;
			
			try {
			if (isCured) {
				gpIncentive = ssl.findSetupIncentive("GP_CURED");
				chwIncentive = ssl.findSetupIncentive("CHW_CURED");
			}

			else if (isTreatmentCompleted) {
				gpIncentive = ssl.findSetupIncentive("GP_TX_CMP");
				chwIncentive = ssl.findSetupIncentive("CHW_TX_CMP");
			}

			
			ssl.sendAlertsOnEndFollowUp(encId, gpIncentive, chwIncentive,
					isCured, isTreatmentCompleted);
			}
			
			catch(Exception e7) {
				e7.printStackTrace();
				System.out.println(new Date(System.currentTimeMillis()).toString() + ": Could not send GP Confirmation incentive for Encounter: " + encId.getEncounterId() + "|" + encId.getPid1() + "|" + encId.getPid2());
			}
		}
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}
	
	//******************************************
	//doMRAssign
	
	/*private String doMRAssign() {
		String xml = null;
	
		String id = request.getParameter("id");

		String mid = request.getParameter("mid");

		String mrNum = request.getParameter("mr");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		
		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
		}

		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		if(pat.getMrno()!=null && pat.getMrno().length()!=0) {
			return XmlUtil.createErrorXml("Patient already has MR Number");
		}
		
		Boolean mrAssigned = null;
		
		try {
			mrAssigned = ssl.exists("Patient"," where MrNo='" + mrNum + "'");
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		if(mrAssigned==null) {
			return XmlUtil.createErrorXml("Error tracking MR Number");
		}
		
		else if(mrAssigned.booleanValue()==true) {
			return XmlUtil.createErrorXml("MR Number " + mrNum + " has already been assigned.");
		}
		
		pat.setMrno(mrNum);
		
		boolean patUpdated = true;
		
		try {
			patUpdated = ssl.updatePatient(pat);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if(!patUpdated) {
			return XmlUtil.createErrorXml("Could not update Patient! Please try again!");
		}
		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(mid);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("MR_ASSIGN");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Could not create Encounter. Please try again.");
		}
		
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		return XmlUtil.createSuccessXml();
	}*/

	// ******************************************
	// doFormQuery*******************************/

	/*private String doFormQuery() {
		String xml = null;

		String username = request.getParameter("un");
		// ssl = new ServerServiceImpl();

		if (!UserAuthentication.userExsists(username)) {
			return XmlUtil
					.createErrorXml("Authentcation Failure. Please try again!");
		}*/

	/*	String requestType = request.getParameter("qtype");
		System.out.println(requestType);
		String id = request.getParameter("pid");
		System.out.println(id);

		if (requestType.equals(RequestType.QUERY_SUSPECT_CONF)) {
			xml = getSuspectConfInfo(id);
		}

		else if (requestType.equals(RequestType.QUERY_SPUTUM_COLLECTION)) {
			xml = getSputumCollectInfo(id);
		}

		else if (requestType.equals(RequestType.QUERY_BASELINE_TX)) {
			xml = getBaselineTreatmentInfo(id);
		}

		else if (requestType.equals(RequestType.QUERY_FOLLOWUP_TX)) {
			xml = getFollowupTreatmentInfo(id);
		}

		else if (requestType.equals(RequestType.QUERY_FUP_EFFORT)) {
			xml = getPatientFollowupEffortInfo(id);
		}
		
		else if (requestType.equals(RequestType.QUERY_DRUG_ADMIN)) {
			xml = getDrugAdmInfo(id);
		}

		else if (requestType.equals(RequestType.QUERY_END_FOL)) {
			xml = getFollowupTreatmentInfo(id);
		}
		
		else if(requestType.equals(RequestType.QUERY_SEARCH)) {
			xml = getSearchInfo();
		}
		
		else if(requestType.equals(RequestType.QUERY_SPUTUM_SEARCH)) {
			xml =  getSputumSearchInfo();
		}
		
		else if(requestType.equals(RequestType.QUERY_LAB)) {
			xml = getLabResultsData();
		}
		
		else if(requestType.equals(RequestType.QUERY_FORM_COUNT)) {
			xml = getFormCount();
		}
		
		else if(requestType.equals(RequestType.QUERY_CLINICAL_DIAG)) {
			//xml = getBaselineTreatmentInfo(id);
			xml = getCDFInfo(id);
		}

		else if(requestType.equals(RequestType.QUERY_PAED_CLINICAL_DIAG)) {
			xml = getPaedClinicalDiagInfo(id);
		}*/
		
		/*else if(requestType.equals(RequestType.QUERY_PEADS_CINICAL_VISIT)) {
			xml = getPaedClinicalVisitInfo(id);
		}
		
		else if(requestType.equals(RequestType.QUERY_CINICAL_VISIT)) {
			xml = getClinicalVisitInfo(id);
		}
		
		else if(requestType.equals(RequestType.QUERY_PAED_CONF)) {
			xml = getPaedConfirmationInfo(id);
		}
		
		else if(requestType.equals(RequestType.QUERY_CONTACT_SPUTUM_COLLECTION)) {
			
			xml = getContactSputumCollectInfo(id);
		}*/
		
		/*else if(requestType.equals(RequestType.QUERY_DRUG_DISPENATION)) {
			
			xml = getDrugDispenationInfo(id);
		}
		
		else if(requestType.equals(RequestType.QUERY_PAT_VERIFY)) {
			
			xml = getPatientVerifyInfo(id);
			
		}
		
		else if(requestType.equals(RequestType.QUERY_NO_ACTIVE_FOLLOWUP)) {
			return getNoActiveFollowupInfo(id);
		}
		return xml;
	}*/

	/*public String getSuspectConfInfo(String id) {
		String xml = null;

		ssl = new ServerServiceImpl();
		String[] rowData = null;

		try {
			rowData = ssl
					.getColumnData("Person", "CONCAT(FirstName, '|',LastName)",
							"where pid='" + id + "'");
			
			 * rowData = ssl.getRowRecord("Person", new String[] {"firstName",
			 * "lastName"}, " where pid = '" + id + "'");
			 }

		catch (Exception e) {
			e.printStackTrace();
			return XmlUtil.createErrorXml("Error! Please try again");
		}

		if (rowData.length==0) {
			return XmlUtil.createErrorXml("Person with id " + id + " does not exist. Please recheck id and try again.");
		}

		String[] data = rowData[0].split("\\|");
		System.out.println(data[0]);
		System.out.println(data[1]);

		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element fNameNode = doc.createElement("fname");
		Text fNameValue = doc.createTextNode(data[0]);
		fNameNode.appendChild(fNameValue);

		responseNode.appendChild(fNameNode);

		Element lNameNode = doc.createElement("lname");
		Text lNameValue = doc.createTextNode(data[1]);
		lNameNode.appendChild(lNameValue);

		responseNode.appendChild(lNameNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}
	
	public String getContactSputumCollectInfo(String id) {
		
		return getSputumCollectInfo(id);
	}

	// ******************************************
	// getSputumCollectionInfo*******************************/

	/*public String getSputumCollectInfo(String id) {
		String xml = null;

		
		 ssl = new ServerServiceImpl();
		 String allowXpert = "false";
		 Person p = null; 
		 Patient pat = null;
		 Contact c = null;
		 Date dob = null;
		 int month = 0;
		 String monthStr = "";
		  
		  try { 
			  p = (Person)ssl.findPerson(id); 
		  } 
		  catch (Exception e2) {
			  e2.printStackTrace(); 
		  }
		  
		  if(p==null) {
			  return XmlUtil.createErrorXml("Could not find Person with id " + id + "Please try again.");
		  }
		  
		  long age = 0;
		  dob = p.getDob();
		  if(dob!=null)
		  {	  age = getAgeInYears(dob);
		  	  System.out.println("AGE:->>>" + age);
		  }
		  
		  else {
			  age = -1;
			  System.out.println("AGE:->>>" + age);
		  }
		  
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + "Please try again.");
		  }
		  
		  try { 
			  c = ssl.findContact(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace(); 
			  return XmlUtil.createErrorXml("Could not find Contact for patient with id " + id + " Please try again.");
		  }
		  
		  if(c==null) {
			  
				  return XmlUtil.createErrorXml("Could not find Contact for patient with id " + id + " Please try again.");
			  
		  }
		  
		  String address = ""; 
		  if(!c.getAddressHouse().equals("N/A")) {
			  address += c.getAddressHouse() + ","; 
		  } 
		  
		  if(!c.getAddressStreet().equals("N/A")) {
			  address += c.getAddressStreet() + ",";
		  }
		  
		  if(!c.getAddressSector().equals("N/A")) { 
			  address += "Sector " + c.getAddressSector() + ","; 
		  } 
		  
		  if(!c.getAddressTown().equals("N/A")) {
			  address += c.getAddressTown() + ","; 
		  }
		  
		  if(!c.getAddressUc().equals("N/A")) { 
			  address += "UC " + c.getAddressUc() + ",";
		  } 
		  
		  if(!c.getAddressLandMark().equals("N/A")) {
			  address += "near " + c.getAddressLandMark();
		  }
		  
		  String firstname = p.getFirstName();
		  String lastname = p.getLastName(); 
		  String diseaseCategory = pat.getDiseaseCategory();
		  
		  if(diseaseCategory==null) { 
			  diseaseCategory = "";
		  }
		  
		  String treatmentStartDate = ""; 
		  String rowRecord[] = null;
		  try { 
			  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
		  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		  
		  rowRecord = ssl.getRowRecord("Encounter", new String[]{"DateEncounterEntered","PID2"}, " where PID1= '" + id + "' AND EncounterType='BASELINE'");
		  
			
	
		  }	
		  catch (Exception e1) 
		  { 
			  e1.printStackTrace(); 
		  }
		  
		  if(rowRecord==null || rowRecord.length!=2) {
			 treatmentStartDate = "N/A"; 
			 if(rowRecord==null)
				 System.out.println("null");
			 else
				 System.out.println(rowRecord.length);
		  }
		  
		  else { 
			   
			  treatmentStartDate = rowRecord[0];
		  } 
		  
		  if(diseaseCategory.length()!=0 &&  treatmentStartDate!=null && treatmentStartDate.length()!=0 && !treatmentStartDate.equals("N/A")) {
			  boolean old = false;
			try {
				old = isOldRegimen(treatmentStartDate);
				if(old) {
					  diseaseCategory += XmlStrings.OLD;
				  }
				  
				  else
					  diseaseCategory += XmlStrings.NEW;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		  }
		
		  if(treatmentStartDate==null || treatmentStartDate.equals("N/A")) {
			  month = 0;
		  }
		  
		  //else month calculation
		  else {
			  month = DateTimeUtil.calculateMonthOfTreatment(treatmentStartDate);
		  }
		  
		  monthStr = "" + month;
			  
			  String[] er1 = null;
				try {
					er1 = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY_D1'");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String tbhist;
				if(er1!=null && er1.length > 0)
					tbhist = er1[0];
				else {
					try {
						er1 = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY'");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(er1!=null && er1.length>0)
						tbhist = er1[0];
					else
						tbhist = "Not Entered";
				}
		  Encounter e = findEncounter
		  where encounter type = baseline treatment and pid1=patient ID.
		  //EncounterResult = get encounter result where element is Entered
		  date and encounter id is from above //
		 
				
		// Check for allowing Xpert
				
				if(tbhist.equalsIgnoreCase("YES")) {
					allowXpert = "true";
				}
				
				else  {
				Boolean exists = false;
			
				try {
					exists = ssl.exists("SputumResults", " where Month = 0 AND PatientID='" + id + "' AND SmearResult IS NOT NULL AND SmearResult!='NEGATIVE'");
					//sputumresults = ssl.getColumnData("SputumResults", "SmearResult", " where Month = 0 AND PatientID='" + id + "' AND SmearResult='POSITIVE'");
				}
				
				catch(Exception e) {
					e.printStackTrace();
					return XmlUtil.createErrorXml("Error tracking Smear Status");
				}
				
				if(exists.booleanValue()==true) {
					System.out.println("POSITIVE FOUND");
					allowXpert = "false";
				}
				
				else {//check for 2 smear -ve and a suggestive/suspicious Xray
					
					Long numSmearNegative = null;
					try {
						numSmearNegative = ssl.count("SputumResults", " where Month = 0 AND PatientID='" + id + "' AND SmearResult='NEGATIVE'");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					if(numSmearNegative==null) {
						return XmlUtil.createErrorXml("Could not track Smear Result status");
					}
				
					if(numSmearNegative.longValue()<2) {
						System.out.println("less then 2 negative sputum");
						allowXpert = "false";
					
					}
					
					else { //check for Xray
						
						try {
							exists = ssl.exists("XRayResults", " where PatientID='" + id + "' AND (XRayResults='SUGGESTIVE OF TB' OR XRayResults='SUSPICIOUS OF TB')");
						}
						
						catch(Exception e) {
							e.printStackTrace();
						}
						
						if(exists==null) {
							return XmlUtil.createErrorXml("Error tracking Xray Status");
						}
						
						else if (exists==true){
							System.out.println("found suggestive Xray");
							allowXpert = "true";
						}
						
						
					} 
				}
		
				}
				
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element fNameNode = doc.createElement("fname");
		//Text fNameValue = doc.createTextNode("Test");
		Text fNameValue = doc.createTextNode(firstname);
		fNameNode.appendChild(fNameValue);

		responseNode.appendChild(fNameNode);

		Element lNameNode = doc.createElement("lname");
		//Text lNameValue = doc.createTextNode("Patient");
		Text lNameValue = doc.createTextNode(lastname);
		lNameNode.appendChild(lNameValue);

		responseNode.appendChild(lNameNode);
		
		Element ageNode = doc.createElement("age");
		//Text lNameValue = doc.createTextNode("Patient");
		Text ageValue = doc.createTextNode(new Long(age).toString());
		ageNode.appendChild(ageValue);

		responseNode.appendChild(ageNode);

		Element addrNode = doc.createElement("address");
		Text addrValue = doc.createTextNode(address);
		addrNode.appendChild(addrValue);

		responseNode.appendChild(addrNode);

		Element txStartNode = doc.createElement("trstart");
		//Text txStartValue = doc.createTextNode("29/11/10");
		Text txStartValue = doc.createTextNode(treatmentStartDate);
		txStartNode.appendChild(txStartValue);

		responseNode.appendChild(txStartNode);
		
		Element txMonthNode = doc.createElement("txmonth");
		//Text txStartValue = doc.createTextNode("29/11/10");
		Text txMonthValue = doc.createTextNode(monthStr);
		txMonthNode.appendChild(txMonthValue);

		responseNode.appendChild(txMonthNode);

		Element catNode = doc.createElement("cat");
		Text catValue = doc.createTextNode(diseaseCategory);
		catNode.appendChild(catValue);

		responseNode.appendChild(catNode);
		
		Element tbHistNode = doc.createElement("tbhist");
		Text tbHistValue = doc.createTextNode(tbhist);
		tbHistNode.appendChild(tbHistValue);

		responseNode.appendChild(tbHistNode);
		
		Element allowXpertNode = doc.createElement("allowxpert");
		//Text fNameValue = doc.createTextNode("Test");
		Text allowXpertValue = doc.createTextNode(allowXpert);
		allowXpertNode.appendChild(allowXpertValue);

		responseNode.appendChild(allowXpertNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	
		
	}*/
	
	

	/*// ******************************************
	// getBaselineTreatmentInfo*******************************/

	/*public String getBaselineTreatmentInfo(String id) {
		String xml = null;

		Patient pat = null;
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}

		if (pat == null) {
			return XmlUtil
					.createErrorXml("Patient does not exist. Please confirm ID and try again");
		}
		
		Patient pat2 = null;
		
		if(pat.getMrno() != null) {
			try {
				pat2 = ssl.findPatientByMR("9" + pat.getMrno());
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
		
		
		//P/Q check for Smear Negative Patients
		
		Boolean exists = true;
		
		try {
			exists = ssl.exists("SputumResults", "where PatientID='" + id + "' AND SmearResult != 'NEGATIVE' AND SmearResult IS NOT NULL");
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Please try again");
		}
		
		if(exists==null) {
			return XmlUtil.createErrorXml("ERROR: Please try again");
		}
		
		//GeneXpert check
		if(exists==false) {
			try {
				exists = ssl.exists("GeneXpertResults","where PatientID='"+ id + "' AND isPositive=true");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return XmlUtil.createErrorXml("ERROR: Please try again");
			}
			
			if(exists==null) {
				return XmlUtil.createErrorXml("ERROR: Please try again");
			}

		}
		
		if(exists==false) {
			try {
				exists = ssl.exists("Encounter","where PID1='"+ id + "' AND (EncounterType='CDF' OR EncounterType='PAED_DIAG')");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return XmlUtil.createErrorXml("ERROR: Please try again");
			}
			
			if(exists==null) {
				return XmlUtil.createErrorXml("ERROR: Please try again");
			}
			
			if(exists==false) {
				//add check for treatment outcomes. If treatment outcomes don't exist, show error.
				if(pat2!=null)
				{
					try {
						exists = ssl.exists("EncounterResults","where PID1='"+ pat2.getPatientId() + "' AND Element='REASON' AND (Value='DEFAULTED' OR Value='FAILURE')");
					} catch (Exception e) {
						e.printStackTrace();
						return XmlUtil.createErrorXml("ERROR: Please try again");
					}
				
					if(exists==null) {
						return XmlUtil.createErrorXml("ERROR: Please try again");
					}
				}
				
				if(exists==false) {
					return XmlUtil.createErrorXml("Pehlay is patient ka P ya Q form bhariye or phir Baseline Treatment Form bhariye");
				}
			}
		}
		
		try {
			exists = ssl.exists("EncounterResults","where PID1='"+ id + "' AND Element='DIAGNOSIS' AND Value='OTHER THAN TB'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Please try again");
		}
		
		if(exists==null) {
			return XmlUtil.createErrorXml("ERROR: Please try again");
		}
		
		if(exists==true) {
			return XmlUtil.createErrorXml("Ye patient TB ka mareez nahin hai. Q form mein Diagnosis 'OTHER THAN TB' hai.");
		}
		
		
		//get base sputum results, gxp results, gxp resistance, chest x-ray
		//
		String[] sputumResults = null;
		int numResults = -1;
		try {
			sputumResults = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=0 AND SmearResult IS NOT NULL AND DateSubmitted IS NOT NULL AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		
		if(sputumResults==null) {
			return XmlUtil.createErrorXml("Could not find base Sputum Results for patient with id " + id + " Please try again.");
		}
		
		String baseSmear = " ";
		String baseGxp = " ";
		String baseGxpRes = " ";
		String cxr = " ";
		
		if(sputumResults!=null) {
			numResults = sputumResults.length;
			
			for(int i=0; i<numResults; i++) {
				baseSmear += sputumResults[i] + " ";
					
			}
		}
		
		//get base Gxp results
		
		String[][] gxpResults=null;
		try {
			gxpResults = ssl.getTableData("GeneXpertResults", new String[] {"IRS","GeneXpertResult", "DrugResistance"}, " where PatientID='" + id + "' AND Remarks NOT LIKE '%REJECTED%' AND DateTested IS NOT NULL ORDER BY DateTested ASC");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		if(gxpResults!=null) {
			for(int i=0; i<gxpResults.length; i++) {
				if(gxpResults[i][0]!=null && !gxpResults[i][0].equals("0")) {
					
						baseGxp += gxpResults[i][1] + " ";
						baseGxpRes += gxpResults[i][2] + " ";
					else if(gxpResults[i][0].equals("true"))
						baseGxp += "Positive ";
				}
				else
				{
					baseGxp += "N/A" + " ";
					baseGxpRes += "N/A" + " ";
				}
				
				if(gxpResults[i][1]!=null)
					
				else
					
			}
		}
		
		String[] xRayResult = null;
		
		try {
			xRayResult = ssl.getColumnData("XRayResults", "XRayResults", " where PatientId='"+ id + "' AND DateReported IS NOT NULL ORDER BY DateReported ASC");
		
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(xRayResult!=null) {
			for(int i=0; i<xRayResult.length; i++) {
				if(xRayResult[i]!=null)
					cxr += xRayResult[i] + " ";
			}
		}
		
		String[] er = null;
		try {
			er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY_D1'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String tbhist;
		if(er!=null && er.length > 0)
			tbhist = er[0];
		else {
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(er!=null && er.length>0)
				tbhist = er[0];
			else
				tbhist = "Not Entered";
		}
		
		
		
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element smearNode = doc.createElement("basesmear");
		Text smearValue = doc.createTextNode(baseSmear);
		smearNode.appendChild(smearValue);

		responseNode.appendChild(smearNode);

		Element gxpNode = doc.createElement("basegxp");
		Text gxpValue = doc.createTextNode(baseGxp);
		gxpNode.appendChild(gxpValue);

		responseNode.appendChild(gxpNode);

		Element dstNode = doc.createElement("basedst");
		Text dstValue = doc.createTextNode(baseGxpRes);
		dstNode.appendChild(dstValue);

		responseNode.appendChild(dstNode);

		Element cxrNode = doc.createElement("cxr");
		Text cxrValue = doc.createTextNode(cxr);
		cxrNode.appendChild(cxrValue);

		responseNode.appendChild(cxrNode);
		
		Element tbHistNode = doc.createElement("tbhist");
		Text tbHistValue = doc.createTextNode(tbhist);
		tbHistNode.appendChild(tbHistValue);

		responseNode.appendChild(tbHistNode);

		
		 * Element typeNode = doc.createElement("type"); Text typeValue =
		 * doc.createTextNode("New"); typeNode.appendChild(typeValue);
		 * 
		 * responseNode.appendChild(typeNode);
		 * 
		 * Element catNode = doc.createElement("cat"); Text catValue =
		 * doc.createTextNode("2"); catNode.appendChild(catValue);
		 * 
		 * responseNode.appendChild(catNode);
		 

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}

	// ******************************************
	// getFollowupTreatmentInfo*******************************/

	/*public String getFollowupTreatmentInfo(String id) {
		String xml = null;

		ssl = new ServerServiceImpl();
		Patient pat = null;
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}

		if (pat == null) {
			return XmlUtil
					.createErrorXml("Patient does not exist. Please confirm ID and try again");
		}
		
		String diseaseSite = pat.getDiseaseSite();
		if(diseaseSite==null) {
			diseaseSite = " ";
		}
		String type = pat.getPatientType();
		if(type==null) {
			type = " ";
		}
		
		String category = pat.getDiseaseCategory();
		if(category==null) {
			category = " ";
		}
		//get base sputum results, gxp results, gxp resistance, chest x-ray
		//TODO: Modify for three tables
		String[] sputumResults = null;
		int numResults = -1;
		try {
			sputumResults = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=0 AND SmearResult IS NOT NULL AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		
		if(sputumResults==null) {
			return XmlUtil.createErrorXml("Could not find base Sputum Results for patient with id " + id + " Please try again.");
		}
		
		String baseSmear = " ";
		String baseGxp = " ";
		String baseGxpRes = " ";
		String cxr = " ";
		
		if(sputumResults!=null) {
			numResults = sputumResults.length;
			
			for(int i=0; i<numResults; i++) {
				baseSmear += sputumResults[i] + " ";
					
			}
		}
		
		//get base Gxp results
		
		String[][] gxpResults=null;
		try {
			gxpResults = ssl.getTableData("GeneXpertResults", new String[] {"IRS","GeneXpertResult", "DrugResistance"}, " where PatientID='" + id + "' AND Remarks NOT LIKE '%REJECTED%' AND DateTested IS NOT NULL ORDER BY DateTested ASC");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		if(gxpResults!=null) {
			for(int i=0; i<gxpResults.length; i++) {
				if(gxpResults[i][0]!=null && !gxpResults[i][0].equals("0")) {
					
						baseGxp += gxpResults[i][1] + " ";
						baseGxpRes += gxpResults[i][2] + " ";
					else if(gxpResults[i][0].equals("true"))
						baseGxp += "Positive ";
				}
				else
				{
					baseGxp += "N/A" + " ";
					baseGxpRes += "N/A" + " ";
				}
				
				if(gxpResults[i][1]!=null)
					
				else
					
			}
		}
		
		String[] xRayResult = null;
		
		try {
			xRayResult = ssl.getColumnData("XRayResults", "XRayResults", " where PatientId='"+ id + "' AND DateReported IS NOT NULL ORDER BY DateReported ASC");
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if(xRayResult!=null) {
			for(int i=0; i<xRayResult.length; i++) {
				if(xRayResult[i]!=null)
					cxr += xRayResult[i] + " ";
			}
		}
		
		//follow up smear 2
		String[] sputum2Results = null;
		int numSmear2Results = -1;
		try {
			sputum2Results = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=2 AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		
		String smear2 = " ";
		
		
		if(sputum2Results!=null) {
			numSmear2Results = sputum2Results.length;
			
			for(int i=0; i<numSmear2Results; i++) {
				smear2 += sputum2Results[i] + " ";
						
			}
		}
		
		//follow up smear 3
		String[] sputum3Results = null;
		int numSmear3Results = -1;
		try {
			sputum3Results = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=3 AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		
		String smear3 = " ";
		
		
		if(sputum3Results!=null) {
			numSmear3Results = sputum3Results.length;
			
			for(int i=0; i<numSmear3Results; i++) {
				smear3 += sputum3Results[i] + " ";
						
			}
		}
		
		//follow up smear 5
		String[] sputum5Results = null;
		int numSmear5Results = -1;
		try {
			sputum5Results = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=5 AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		
		String smear5 = " ";
		
		
		if(sputum5Results!=null) {
			numSmear5Results = sputum5Results.length;
			
			for(int i=0; i<numSmear5Results; i++) {
				smear5 += sputum5Results[i] + " ";
						
			}
		}
		
		//follow up smear 7
		String[] sputum7Results = null;
		int numSmear7Results = -1;
		try {
			sputum7Results = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=7 AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		
		String smear7 = " ";
		
		
		if(sputum7Results!=null) {
			numSmear7Results = sputum7Results.length;
			
			for(int i=0; i<numSmear7Results; i++) {
				smear7 += sputum7Results[i] + " ";
						
			}
		}
		

		
		String treatmentStartDate = "";
		try {
			treatmentStartDate = getTxStartDate(id);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(category!=null && category.length()!=0 && !category.equals(" ") &&  treatmentStartDate!=null && treatmentStartDate.length()!=0 && !treatmentStartDate.equals("N/A")) {
			  boolean old = false;
			try {
				old = isOldRegimen(treatmentStartDate);
				if(old) {
					  category += XmlStrings.OLD;
				  }
				  
				  else
					  category += XmlStrings.NEW;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		  }
		  try { 
			  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
		  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		  
		  rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID", "PID2"}, " where PID1= '" + id + "'AND EncounterType='BASELINE' ORDER BY DateEncounterStart ASC");
			}	
		  catch (Exception e1) 
		  { 
			  e1.printStackTrace(); 
		  }
		  
		  if(rowRecord==null || rowRecord.length!=2) {
			 treatmentStartDate = "N/A"; 
			 if(rowRecord==null)
				 System.out.println("null");
			 else
				 System.out.println(rowRecord.length);
		  }
		  
		  else { 
			   
			  System.out.println(rowRecord[0]);
			  System.out.print(rowRecord[1]);
			  int encId = Integer.parseInt(rowRecord[0]); 
			  String pid2 = rowRecord[1]; 
			  EncounterResultsId eri = new EncounterResultsId(encId,id,pid2,"entered_date"); 
			  EncounterResults er = null;
			  try { 
				  er =
					  ssl.findEncounterResultsByElement(eri); 
			  } catch (Exception e) {
				  e.printStackTrace(); 
			  } 
			  
			  if(er!=null) { 
				  treatmentStartDate = er.getValue(); 
			  }
		  
			  else { 
				  treatmentStartDate = "N/A"; 
			  } 
		  } 
		  
		  String[] er = null;
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY_D1'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String tbhist;
			if(er!=null && er.length > 0)
				tbhist = er[0];
			else {
				try {
					er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY'");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(er!=null && er.length>0)
					tbhist = er[0];
				else
					tbhist = "Not Entered";
			}

		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dateNode = doc.createElement("txstart");
		Text dateValue = doc.createTextNode(treatmentStartDate);
		dateNode.appendChild(dateValue);

		responseNode.appendChild(dateNode);

		Element smearNode = doc.createElement("basesmear");
		Text smearValue = doc.createTextNode(baseSmear);
		smearNode.appendChild(smearValue);

		responseNode.appendChild(smearNode);

		Element gxpNode = doc.createElement("basegxp");
		Text gxpValue = doc.createTextNode(baseGxp);
		gxpNode.appendChild(gxpValue);

		responseNode.appendChild(gxpNode);

		Element dstNode = doc.createElement("basedst");
		Text dstValue = doc.createTextNode(baseGxpRes);
		dstNode.appendChild(dstValue);

		responseNode.appendChild(dstNode);

		Element cxrNode = doc.createElement("cxr");
		Text cxrValue = doc.createTextNode(cxr);
		cxrNode.appendChild(cxrValue);

		responseNode.appendChild(cxrNode);

		Element fosmear2Node = doc.createElement("fosmear2");
		Text fosmear2Value = doc.createTextNode(smear2);
		fosmear2Node.appendChild(fosmear2Value);

		responseNode.appendChild(fosmear2Node);

		Element fosmear3Node = doc.createElement("fosmear3");
		Text fosmear3Value = doc.createTextNode(smear3);
		fosmear3Node.appendChild(fosmear3Value);

		responseNode.appendChild(fosmear3Node);

		Element fosmear5Node = doc.createElement("fosmear5");
		Text fosmear5Value = doc.createTextNode(smear5);
		fosmear5Node.appendChild(fosmear5Value);

		responseNode.appendChild(fosmear5Node);

		Element fosmear7Node = doc.createElement("fosmear7");
		Text fosmear7Value = doc.createTextNode(smear7);
		fosmear7Node.appendChild(fosmear7Value);

		responseNode.appendChild(fosmear7Node);

		Element siteNode = doc.createElement("site");
		Text siteValue = doc.createTextNode(diseaseSite);
		siteNode.appendChild(siteValue);

		responseNode.appendChild(siteNode);

		Element typeNode = doc.createElement("type");
		Text typeValue = doc.createTextNode(type);
		typeNode.appendChild(typeValue);

		responseNode.appendChild(typeNode);

		Element catNode = doc.createElement("cat");
		Text catValue = doc.createTextNode(category);
		catNode.appendChild(catValue);

		responseNode.appendChild(catNode);
		
		Element tbHistNode = doc.createElement("tbhist");
		Text tbHistValue = doc.createTextNode(tbhist);
		tbHistNode.appendChild(tbHistValue);

		responseNode.appendChild(tbHistNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}
*/

	// ******************************************
	// getSuspectConfInfo*******************************/

	
	/*public String getSearchInfo() {
		String xml = null;
		
		ssl = new ServerServiceImpl();
		
		
		String id = request.getParameter("id");
		String mr = request.getParameter("mr");
		String iType = request.getParameter("itype");
		
		Person p = null;
		Patient pat = null;
		Contact c = null;
		PatientDOTS pd = null;
		
		if(iType.equals("MR")) {
			String[] mrs = null;
			try {
				mrs = ssl.getColumnData("Patient", "PatientID", " where MrNo='" + mr + "'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(mrs==null) {
				return XmlUtil.createErrorXml("Error finding MR Number");
			}
			
			else if (mrs.length==0) {
				return XmlUtil.createErrorXml("MR number not found");
			}
			
			else {
				id = mrs[0];
				//System.out.println(id);
			}
		}
		
		System.out.println(id);
		
		
		try {
			p = ssl.findPerson(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(p==null) {
			return XmlUtil.createErrorXml("Patient with ID: " + id + " not found.");
		}
		
		String fName = p.getFirstName();
		String fathersName = p.getGuardianName();
		Date dobDate = p.getDob();
		String dob = null;
		if(dobDate!=null)
			dob = dobDate.toString();
		String mStatus = p.getMaritalStatus();
		String religion = p.getReligion();
		long age = 0;
		
		if(dob!=null) {
			age = getAgeInYears(p.getDob());
		}
		
		String nic = p.getNic();
		char gender = p.getGender();
		String genderString = "" + gender;
		String state = " ";
		String mrNum = " ";
		String otherPID = " ";
		String houseNum = " ";
		String street = " ";
		String sector = " ";
		String town = " ";
		String colony = " ";
		String uc = " ";
		String landmark = " ";
		String phone = " ";
		String numAdults = " ";
		String numChildren = " ";
		
		if(fName==null)
			fName = " ";
		if(fathersName==null)
			fathersName= " ";
		if(dob==null)
			dob = " ";
		if(nic==null)
			nic= " ";
		if(mStatus==null)
			mStatus =" ";
		if(religion==null)
			religion = " ";
		
	    try {
			c = ssl.findContact(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		if(c!=null) {
			if(!c.getAddressHouse().equals("N/A")) {
				  houseNum = c.getAddressHouse();
			  } 
			  
			  if(!c.getAddressStreet().equals("N/A")) {
				  street = c.getAddressStreet();
			  }
			  
			  if(!c.getAddressSector().equals("N/A")) { 
				  sector = c.getAddressSector();
			  } 
			  
			  if(!c.getAddressColony().equals("N/A")) {
				  colony = c.getAddressColony();
			  }
			  
			  if(!c.getAddressTown().equals("N/A")) {
				  town = c.getAddressTown();
			  }
			 
			  if(!c.getAddressUc().equals("N/A")) { 
				  uc = c.getAddressUc();
			  } 
			  
			  if(!c.getAddressLandMark().equals("N/A")) {
				  landmark = c.getAddressLandMark();
			  }
			  
			  if(c.getPhone()!=null) {
				  phone = c.getPhone();
			  }
			  
			  if(c.getHouseHoldAdults()!=null) {
				  numAdults = c.getHouseHoldAdults().toString();
			  }
			  
			  if(c.getHouseHoldChildren()!=null) {
				  numChildren = c.getHouseHoldChildren().toString();
			  }
			  
		}
		
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(pat!=null) {
			
			if(pat.getMrno()!=null)
				mrNum = pat.getMrno();
			
			if(pat.getPatientStatus()!=null) {
				state = pat.getPatientStatus();
			}
			
			if(pat.getMrno()!=null) {
				String tempMr = pat.getMrno();
				
				if(tempMr.charAt(0)=='9')
					tempMr = tempMr.substring(1,tempMr.length());
				else
					tempMr = "9" + tempMr;
				Patient p2 = null;
				
				try {
					p2 = ssl.findPatientByMR(tempMr);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				if(p2!=null)
					otherPID = p2.getPatientId();
				
				
			}
			
		}
	    
		if(age<=0) {
			String[] er = null;
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + " ' AND Element='AGE'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String ageString = "0";
			
			if(er!=null && er.length>0)
				ageString = er[0];
			
			try {
				age = Long.parseLong(ageString);
			}
			
			catch(Exception e) {
				e.printStackTrace();
				age = 0;
			}
		}
		
		//String[] rowdata = null;
		
		Boolean dotsExists = null;
		String dots = "";
		try {
			dotsExists = ssl.exists("PatientDOTS", " where MRNo='" + mrNum + "'");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			
			e2.printStackTrace();
			return XmlUtil.createErrorXml("Error finding DOTS Number");
		}
		
		if(dotsExists==null) {
			return XmlUtil.createErrorXml("Error finding DOTS Number");
		}
		
		else if(dotsExists) {
		
		
		
		try {
			dots = ssl.getObject("PatientDOTS", "DOTSNo", " where MRNo='" + mrNum + "'");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Error finding DOTS Number");
		}
		
		
		if(dots==null) //|| rowdata.length!=2) 
			{
			dots = "Not Available";
		}
		}
		
		else {
			dots = "Not Available";
		}
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element idNode = doc.createElement("pid");
		Text idValue = doc.createTextNode(id);
		idNode.appendChild(idValue);

		responseNode.appendChild(idNode);
		
		Element dotsNode = doc.createElement("dots");
		Text dotsValue = doc.createTextNode(dots);
		dotsNode.appendChild(dotsValue);

		responseNode.appendChild(dotsNode);
		
		Element fnameNode = doc.createElement("fname");
		Text fnameValue = doc.createTextNode(fName);
		fnameNode.appendChild(fnameValue);

		responseNode.appendChild(fnameNode);

		Element fatNameNode = doc.createElement("fatname");
		Text fatNameValue = doc.createTextNode(fathersName);
		fatNameNode.appendChild(fatNameValue);

		responseNode.appendChild(fatNameNode);
		
		Element genderNode = doc.createElement("gender");
		Text genderValue = doc.createTextNode(genderString);
		genderNode.appendChild(genderValue);

		responseNode.appendChild(genderNode);
		
		Element nicNode = doc.createElement("nic");
		Text nicValue = doc.createTextNode(nic);
		nicNode.appendChild(nicValue);

		responseNode.appendChild(nicNode);

		Element dobNode = doc.createElement("dob");
		Text dobValue = doc.createTextNode(dob);
		dobNode.appendChild(dobValue);

		responseNode.appendChild(dobNode);

		Element ageNode = doc.createElement("age");
		Text ageValue = doc.createTextNode(new Long(age).toString());
		ageNode.appendChild(ageValue);

		responseNode.appendChild(ageNode);

		Element phoneNode = doc.createElement("phone");
		Text phoneValue = doc.createTextNode(phone);
		phoneNode.appendChild(phoneValue);

		responseNode.appendChild(phoneNode);
		
		Element mStatusNode = doc.createElement("mstatus");
		Text mStatusValue = doc.createTextNode(mStatus);
		mStatusNode.appendChild(mStatusValue);

		responseNode.appendChild(mStatusNode);
		
		Element religionNode = doc.createElement("religion");
		Text religionValue = doc.createTextNode(religion);
		religionNode.appendChild(religionValue);

		responseNode.appendChild(religionNode);

		Element houseNumNode = doc.createElement("housenum");
		Text houseNumValue = doc.createTextNode(houseNum);
		houseNumNode.appendChild(houseNumValue);

		responseNode.appendChild(houseNumNode);

		Element streetNode = doc.createElement("street");
		Text streetValue = doc.createTextNode(street);
		streetNode.appendChild(streetValue);

		responseNode.appendChild(streetNode);

		Element sectorNode = doc.createElement("sector");
		Text sectorValue = doc.createTextNode(sector);
		sectorNode.appendChild(sectorValue);

		responseNode.appendChild(sectorNode);

		Element colonyNode = doc.createElement("colony");
		Text colonyValue = doc.createTextNode(colony);
		colonyNode.appendChild(colonyValue);

		responseNode.appendChild(colonyNode);

		Element townNode = doc.createElement("town");
		Text townValue = doc.createTextNode(town);
		townNode.appendChild(townValue);

		responseNode.appendChild(townNode);

		Element ucNode = doc.createElement("uc");
		Text ucValue = doc.createTextNode(uc);
		ucNode.appendChild(ucValue);

		responseNode.appendChild(ucNode);

		Element landmarkNode = doc.createElement("landmark");
		Text landmarkValue = doc.createTextNode(landmark);
		landmarkNode.appendChild(landmarkValue);

		responseNode.appendChild(landmarkNode);
		
		Element numAdultsNode = doc.createElement("numadults");
		Text numAdultsValue = doc.createTextNode(numAdults);
		numAdultsNode.appendChild(numAdultsValue);

		responseNode.appendChild(numAdultsNode);
		
		Element numChildrenNode = doc.createElement("numchildren");
		Text numChildrenValue = doc.createTextNode(numChildren);
		numChildrenNode.appendChild(numChildrenValue);

		responseNode.appendChild(numChildrenNode);
		
		Element mrNode = doc.createElement("mr");
		Text mrValue = doc.createTextNode(mrNum);
		mrNode.appendChild(mrValue);

		responseNode.appendChild(mrNode);
		
		Element stateNode = doc.createElement("state");
		Text stateValue = doc.createTextNode(state);
		stateNode.appendChild(stateValue);

		responseNode.appendChild(stateNode);
		
		Element otherPIDNode = doc.createElement("otherpid");
		Text otherPIDValue = doc.createTextNode(otherPID);
		otherPIDNode.appendChild(otherPIDValue);

		responseNode.appendChild(otherPIDNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}

	// ******************************************
	// getDrugAdmInfo*******************************/

	/*public String getDrugAdmInfo(String id) {
		String xml = null;

		ssl = new ServerServiceImpl();
		
		Patient pat = null;
		
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Could not find Patient with id " + id + " Please try again.");
		}
		
		String phase = pat.getTreatmentPhase();
		String regimen = pat.getRegimen();
		Float fixedDose = pat.getDoseCombination();
		String fixedDoseString = null;
		
		if(fixedDose==null) {
			fixedDoseString = " ";
		}
		
		else {
			fixedDoseString = fixedDose.toString();
		}
		String otherDose = pat.getOtherDoseDescription();
		
		if(otherDose==null) {
			otherDose = " ";
		}
		
		//String[] colData = null; 
		String [] rowRecord = null;
		
		String treatmentStartDate = "";
		  try { 
			  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
		  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		  
		  rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID", "PID2"}, " where PID1= '" + id + "'AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
			}	
		  catch (Exception e1) 
		  { 
			  e1.printStackTrace(); 
		  }
		  
		  if(rowRecord==null || rowRecord.length!=2) {
			 treatmentStartDate = "N/A"; 
		  }
		  
		  else { 
			   
			  System.out.println(rowRecord[0]);
			  System.out.print(rowRecord[1]);
			  int encId = Integer.parseInt(rowRecord[0]); 
			  String pid2 = rowRecord[1]; 
			  EncounterResultsId eri = new EncounterResultsId(encId,id,pid2,"entered_date"); 
			  EncounterResults er = null;
			  try { 
				  er =
					  ssl.findEncounterResultsByElement(eri); 
			  } catch (Exception e) {
				  e.printStackTrace(); 
			  } 
			  
			  if(er!=null) { 
				  treatmentStartDate = er.getValue(); 
			  }
		  
			  else { 
				  treatmentStartDate = "N/A"; 
			  } 
		  } 
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element phaseNode = doc.createElement("phase");
		Text phaseValue = doc.createTextNode(phase);
		phaseNode.appendChild(phaseValue);

		responseNode.appendChild(phaseNode);

		Element regimenNode = doc.createElement("regimen");
		Text regimenValue = doc.createTextNode(regimen);
		regimenNode.appendChild(regimenValue);

		responseNode.appendChild(regimenNode);

		Element trStartNode = doc.createElement("trstart");
		Text trStartValue = doc.createTextNode(treatmentStartDate);
		trStartNode.appendChild(trStartValue);

		responseNode.appendChild(regimenNode);

		Element fdctNode = doc.createElement("fdct");
		Text fdctValue = doc.createTextNode(fixedDoseString);
		fdctNode.appendChild(fdctValue);

		responseNode.appendChild(fdctNode);

		Element streptNode = doc.createElement("strept");
		Text streptValue = doc.createTextNode(otherDose);
		streptNode.appendChild(streptValue);

		responseNode.appendChild(streptNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
		
		
		return xml;
	}
	
	public String getPatientEncounterHistory(String id) {
		String xml = null;
		
		ssl = new ServerServiceImpl();
		
		String[][] encounters = null;
		
		try {
			encounters = ssl.getTableData("Encounter", new String[]{"EncounterType","DATE_FORMAT(DateEncounterStart,'%d%/%m%/%Y')"}," where PID1='" + id + "'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return XmlUtil.createErrorXml("Error performing search. Please try again!");
		
		}
		
		if(encounters==null) {
			return XmlUtil.createErrorXml("Error performing search. Please try again!");
		}
		
		else if(encounters.length==0) {
			return XmlUtil.createErrorXml("No results found!");
		}
		
		String type = "";
		String date = "";
		String data = "EncounterType - Date";
		for(int i=0;i<encounters.length;i++) {
			type = encounters[i][0];
			date = encounters[i][1];
			
			data += "\n" + type + " - " + date;
		}
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement("data");
		Text dataValue = doc.createTextNode(data);
		dataNode.appendChild(dataValue);

		responseNode.appendChild(dataNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
		
		return xml;
	}
	
	public String getSputumSearchInfo() {
		String xml = null;
		String monitorId = request.getParameter("mid");
		String date = request.getParameter("date");
		
		date = DateTimeUtil.convertFromSlashFormatToSQL(date);
		
		System.out.println(monitorId);
		System.out.println(date);
		ssl = new ServerServiceImpl();
		
		String[][]encounters =null;
		
		try {
			encounters = ssl.getTableData("Encounter", new String[]{"PID1","EncounterID"}, " where PID2='" + monitorId.toUpperCase() + "' AND EncounterType='SPUTUM_COL' AND DateEncounterStart >= '" + date + " 00:00:01' AND DateEncounterStart <= '" + date + " 23:59:59'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(encounters==null) {
			return XmlUtil.createErrorXml("Error while performing search. Please try again");
		}
		
		else if(encounters.length==0) {
			return XmlUtil.createErrorXml("No results found");
		}
		
		String data = "PatientID - Barcode\n";
		String pid1 = "";
		String encId = "";
		EncounterResultsId encResultId = null;
		EncounterResults result = null;
		String bc = "";
		
		for(int i=0; i<encounters.length; i++) {
			pid1 = encounters[i][0];
			encId = encounters[i][1];
			
			encResultId = new EncounterResultsId(Integer.parseInt(encId),pid1,monitorId.toUpperCase(),"SAMPLE_BARCODE");
			
			try {
				result = ssl.findEncounterResultsByElement(encResultId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return XmlUtil.createErrorXml("Error while performing search. Please try again");
			}
			
			if(result!=null) {
				bc = result.getValue();
				if(bc==null) {
					bc = "";
				}
			}
			
			data += pid1 + "-" + bc + "\n";
			
		}
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement("data");
		Text dataValue = doc.createTextNode(data);
		dataNode.appendChild(dataValue);

		responseNode.appendChild(dataNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
		
		return xml;
		
	}*/
	
	/*private String getLabResultsData() {
		data += "Patient ID: " + patientId;
			data += "\nTx Start: " + (String)queryData.get("txstart");
			data += "\nBase Smear: " + (String)queryData.get("basesmear");
			data += "\nBase GX Result: " + (String)queryData.get("basegxp");
			data += "\nBase X DST: " + (String)queryData.get("basedst");
			data += "\nCXR: " + (String)queryData.get("cxr");
			data += "\nSmear - 2m: " + (String)queryData.get("fosmear2");
			data += "\nSmear - 3m: " + (String)queryData.get("fosmear3");
			data += "\nSmear - 5m: " + (String)queryData.get("fosmear5");
			data += "\nSmear - 7m: " + (String)queryData.get("fosmear7");
			data += "\nDisease Site: " + (String)queryData.get("site");
			data += "\nPatient Type: " + (String)queryData.get("type"); 
			data += "\nCategory: " + (String)queryData.get("cat");
		
		String xml = null;
		String id = request.getParameter("id");
		String mr = request.getParameter("mr");
		String iType = request.getParameter("itype");
		String diagType = "";
		
		ssl = new ServerServiceImpl();
	
		if(iType.equals("MR")) {
			String[] mrs = null;
			try {
				mrs = ssl.getColumnData("Patient", "PatientID", " where MrNo='" + mr + "'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(mrs==null) {
				return XmlUtil.createErrorXml("Error finding MR Number");
			}
			
			else if (mrs.length==0) {
				return XmlUtil.createErrorXml("MR number not found");
			}
			
			else {
				id = mrs[0];
				//System.out.println(id);
			}
		}
		System.out.println(id);
		Patient pat = null;
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}

		if (pat == null) {
			return XmlUtil
					.createErrorXml("Patient does not exist. Please confirm ID and try again");
		}
		
		String diseaseSite = pat.getDiseaseSite();
		if(diseaseSite==null) {
			diseaseSite = " ";
		}
		String type = pat.getPatientType();
		if(type==null) {
			type = " ";
		}
		
		String category = pat.getDiseaseCategory();
		if(category==null) {
			category = " ";
		}
		
		String mrNum = pat.getMrno();
		if(mrNum==null) {
			mrNum = " ";
		}
			
		if(category==null) {
			category = " ";
		}
		
		String otherPID = " ";
		if(pat.getMrno()!=null) {
			String tempMr = pat.getMrno();
			
			if(tempMr.charAt(0)=='9')
				tempMr = tempMr.substring(1,tempMr.length());
			else
				tempMr = "9" + tempMr;
			Patient p2 = null;
			
			try {
				p2 = ssl.findPatientByMR(tempMr);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
			if(p2!=null)
				otherPID = p2.getPatientId();
			
			
		}
		//get base sputum results, gxp results, gxp resistance, chest x-ray
		//TODO: Modify for three tables
		Object[] sputumResults = null;
		int numResults = -1;
		try {
			sputumResults = HibernateUtil.util.selectObjects("select DISTINCT er1.value from (select * from Encounter e where PID1='" + id + "' AND EncounterType='SPUTUM_COL') as e, (select * from EncounterResults where PID1='" + id + "' AND Element='SAMPLE_BARCODE' AND Value!='') as er1, (select * from EncounterResults where PID1='" + id + "' AND Element='COLLECTION_MONTH' AND Value='BASELINE') as er2 where e.PID2=er1.PID2 AND e.EncounterID = er1.EncounterID AND e.PID2 = er2.PID2 AND e.EncounterID=er2.EncounterID ORDER BY DateEncounterStart ASC");
			//sputumResults =HibernateUtil.util.selectData("select )
			//sputumResults = ssl.getTableData("SputumResults", new String[]{"SmearResult","Remarks"}, " where PatientID='" + id + "' AND Month=0 ORDER BY DateSubmitted ASC");
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		
		if(sputumResults==null) {
			return XmlUtil.createErrorXml("Could not find base Sputum Results for patient with id " + id + " Please try again.");
		}
		
		String baseSmear = " ";
		String baseGxp = " ";
		String baseGxpRes = " ";
		String cxr = " ";
		String barcode = "";
		String smearRes = "";
		String remarks = "";
		SputumResults temp = null;
		
		if(sputumResults!=null) {
			numResults = sputumResults.length;
			
			for(int i=0; i<numResults; i++) {
				temp = null;
				barcode = (String)(sputumResults[i]);
				if(barcode.length()==5 && barcode.charAt(0)=='0')
					barcode = barcode.substring(1);
				
				try {
					temp = ssl.findSputumResultsBySputumTestID(barcode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(temp==null)
					continue;
				
				smearRes = temp.getSmearResult();
				remarks = temp.getRemarks();
				
				if(smearRes!=null){
					baseSmear += smearRes + ","; 
				}
				
				else if(remarks!=null) {
					if(remarks.startsWith("REJECTED"))
						baseSmear += "Rejected,";
					else if(remarks.startsWith("VERIFIED"))
						baseSmear += "Pending,";
				}
				else {
					baseSmear += "Collected,";
				}	
			}
			
			if(baseSmear.charAt(baseSmear.length()-1)==',') {
				baseSmear = baseSmear.substring(0, baseSmear.length()-1);
			}
		}
		
		//get base Gxp results
		
		String[][] gxpResults=null;
		try {
			gxpResults = ssl.getTableData("GeneXpertResults", new String[] {"IRS","GeneXpertResult", "DrugResistance"}, " where PatientID='" + id + "' AND Remarks NOT LIKE '%REJECTED%' AND DateTested IS NOT NULL ORDER BY DateTested ASC");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		if(gxpResults!=null) {
			for(int i=0; i<gxpResults.length; i++) {
				if(gxpResults[i][0]!=null && !gxpResults[i][0].equals("0")) {
					
						baseGxp += gxpResults[i][1] + " ";
						baseGxpRes += gxpResults[i][2] + " ";
					else if(gxpResults[i][0].equals("true"))
						baseGxp += "Positive ";
				}
				else
				{
					baseGxp += "N/A" + " ";
					baseGxpRes += "N/A" + " ";
				}
				
				if(gxpResults[i][1]!=null)
					
				else
					
			}
		}
		
		String[][] xRayResult = null;
		
		try {
			xRayResult = ssl.getTableData("XRayResults", new String[]{"XRayResults","Remarks"}, " where PatientId='"+ id + "' AND DateReported IS NOT NULL ORDER BY DateReported ASC");
		
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(xRayResult!=null) {
			for(int i=0; i<xRayResult.length; i++) {
				if(xRayResult[i]!=null)
					cxr += xRayResult[i][0];
					if(xRayResult[i][1]!=null && xRayResult[i][1].length()!=0) {
						cxr += " (" + xRayResult[i][1] + ")" + " ";
					}
					
					else {
						cxr += " ";
					}
					
			}
		}
		
		String[][] sputumresults = null;
		//int numSputumResults = -1;
		String smears[] = new String[10];
		
		for(int i=0; i< 10; i++) {
			smears[i] = " ";
		}
		
		int month = -1;
		String res = "";
		String rem = "";
	
		try {
			sputumresults = ssl.getTableData("SputumResults", new String[]{"Month","SmearResult","Remarks"}, " where Month != 0 AND PatientID='" + id + "' ORDER BY Month ASC");
		}

		catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(sputumresults!=null) {
			
			for (int i=0; i<sputumresults.length;i++) {
				
				res = "";
				
				month = Integer.parseInt(sputumresults[i][0]);
				
				smearRes = sputumresults[i][1];
				remarks = sputumresults[i][2];
	
				
				
				if(!smearRes.equals("")){
					res = smearRes;
				}
				
				else if(!remarks.equals("")) {
					
					if(remarks.startsWith("REJECTED"))
						res = "Rejected";
					else if(remarks.startsWith("VERIFIED"))
						res = "Pending";
				}
				
				else {
					res = "Collected";
				}
				
				smears[month-1] += res + ",";
				
			}
			
			for(int i=0; i<smears.length;i++) {
				if(smears[i].charAt(smears[i].length()-1)==',') {
					smears[i] = smears[i].substring(0, smears[i].length()-1);
				}
			}
		}
		
		
		//String [] rowRecord = null;
		
		String treatmentStartDate = "";
		
		try {
			treatmentStartDate = getTxStartDate(id);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(category!=null && category.length()!=0 && !category.equals(" ") &&  treatmentStartDate!=null && treatmentStartDate.length()!=0 && !treatmentStartDate.equals("N/A")) {
			  boolean old = false;
			try {
				old = isOldRegimen(treatmentStartDate);
				if(old) {
					  category += XmlStrings.OLD;
				  }
				  
				  else
					  category += XmlStrings.NEW;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		  }
		  try { 
			  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
		  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		  
		  rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID", "PID2"}, " where PID1= '" + id + "'AND EncounterType='BASELINE' ORDER BY DateEncounterStart ASC");
			}	
		  catch (Exception e1) 
		  { 
			  e1.printStackTrace(); 
		  }
		  
		  if(rowRecord==null || rowRecord.length!=2) {
			 treatmentStartDate = "N/A"; 
			 if(rowRecord==null)
				 System.out.println("null");
			 else
				 System.out.println(rowRecord.length);
		  }
		  
		  else { 
			   
			  System.out.println(rowRecord[0]);
			  System.out.print(rowRecord[1]);
			  int encId = Integer.parseInt(rowRecord[0]); 
			  String pid2 = rowRecord[1]; 
			  EncounterResultsId eri = new EncounterResultsId(encId,id,pid2,"entered_date"); 
			  EncounterResults er = null;
			  try { 
				  er =
					  ssl.findEncounterResultsByElement(eri); 
			  } catch (Exception e) {
				  e.printStackTrace(); 
			  } 
			  
			  if(er!=null) { 
				  treatmentStartDate = er.getValue(); 
				  treatmentStartDate = treatmentStartDate.split(" ")[0];
			  }
		  
			  else { 
				  treatmentStartDate = "N/A"; 
			  } 
		  } 
		
		Person person = null;
		
		try {
			person = ssl.findPerson(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(person==null) {
			XmlUtil.createErrorXml("No personal information for Patient with ID: " + id);
		}
		
		String fname = person.getFirstName();
		String lname = person.getLastName();
		
		String[] er = null;
		try {
			er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY_D1'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String tbhist;
		if(er!=null && er.length > 0)
			tbhist = er[0];
		else {
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(er!=null && er.length>0)
				tbhist = er[0];
			else
				tbhist = "Not Entered";
		}
		
		//////////////ADULT DIAG CHECK
		String diagDate = "";
		String diag = "";
		String pid2 = "";
		String encId = "";
		String enc[] = null;
		try {
			enc = ssl.getRowRecord("Encounter", new String[]{"PID2","EncounterID","DateEncounterEntered"}, " where PID1='"+ id + "' AND EncounterType='CDF'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(enc!=null && enc.length > 0) {
			diagType = "Adult ";
			pid2 = enc[0];
			encId = enc[1];
			diagDate = enc[2];
		
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND PID2='" + pid2 + "' AND EncounterID='" + encId + "' AND Element='DIAGNOSIS'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(er!=null && er.length>0)
				diag = er[0];
			else
				diag = "N/A";
		}
		
		//IF NOT ADULT THEN CHECK FOR PAED
		else {

			try {
				enc = ssl.getRowRecord("Encounter", new String[]{"PID2","EncounterID","DateEncounterEntered"}, " where PID1='"+ id + "' AND EncounterType='PAED_DIAG' ORDER BY DateEncounterEntered ASC");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if(enc!=null && enc.length > 0) {
				diagType = "Paed ";
				pid2 = enc[0];
				encId = enc[1];
				diagDate = enc[2];
			
				try {
					er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND PID2='" + pid2 + "' AND EncounterID='" + encId + "' AND Element='DIAGNOSIS'");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(er!=null && er.length>0)
					diag = er[0];
				else
					diag = "N/A";
			}
			
			////////
		}
		
		////////CHECK FOR END OF FOLLOW-UP
		String endOfFollowup = "N/A";
		
		try {
			enc = ssl.getRowRecord("Encounter", new String[]{"PID2","EncounterID"}, " where PID1='"+ id + "' AND EncounterType='END_FOL'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(enc!=null && enc.length > 0) {
			//diagType = "Paed ";
			pid2 = enc[0];
			encId = enc[1];
			//diagDate = enc[2];
		
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND PID2='" + pid2 + "' AND EncounterID='" + encId + "' AND Element='REASON'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(er!=null && er.length>0)
				endOfFollowup = "YES: " + er[0];
			else
				endOfFollowup = "N/A";
		}
		
////////CHECK FOR NAF
		String naf = "N/A";
		enc = null;
		pid2 = null;
		encId = null;
		try {
			enc = ssl.getRowRecord("Encounter", new String[]{"PID2","EncounterID"}, " where PID1='"+ id + "' AND EncounterType='NO_ACT_FOL'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(enc!=null && enc.length > 0) {
			//diagType = "Paed ";
			pid2 = enc[0];
			encId = enc[1];
			//diagDate = enc[2];
		
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND PID2='" + pid2 + "' AND EncounterID='" + encId + "' AND Element='REASON'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(er!=null && er.length>0)
				naf = "YES: " + er[0];
			else
				naf = "N/A";
		}
		
		try {
			Boolean check = ssl.exists("Encounter", " where PID1='" + id + "' AND EncounterType='END_FOL'");
			
			if(check==null) {
				endOfFollowup = "<error>";
			}
			
			else if(check.booleanValue()) {
				endOfFollowup = "YES";
			}
			
			else {
				endOfFollowup="NO";
			}
		}
		
		catch(Exception e) {
			return XmlUtil.createErrorXml("Could not access encounter record. Please try again");
		}
		
		//GET FOLLOW-UP INFO
		
		String followUpData = "";
		
		String[] months = null;
		
		try {
			months  =  ssl.getColumnData("EncounterResults", "Value", " where PID1='" + id + "' AND Element='MONTH' ORDER BY Value ASC");
			
			if(months==null)
				followUpData = "<Error getting data>";
			else if(months.length==0) {
				followUpData = "N/A";
			}
			else {
				for(int i=0; i<months.length;i++) {
					followUpData += months[i] + ", ";
				}
				
				followUpData = followUpData.trim();
				if(followUpData.endsWith(",")) {
					followUpData = followUpData.substring(0, followUpData.length()-1);
				}
			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			followUpData = "<Error getting data>";
		}
		
		ChartWalker cw = new ChartWalker(id);
		String flowPosition= "";
		
		try {
			flowPosition = cw.walk();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			flowPosition = "ERROR";
		}
		
		
		String data = "Lab results for Patient: " + id;
		
		data += "\nOtherPID: " + otherPID;
		data += "\nFirst Name: " + fname;
		data += "\nLast Name: " + lname;
		data += "\nMR: " + mrNum;
		data += "\nTx Start: " + treatmentStartDate;
		data += "\nBase Smear: " + baseSmear;
		data += "\nCXR: " + cxr;
		data += "\nBase GXP: " + baseGxp;
		data += "\nRIF Resistance: " + baseGxpRes;
		data += "\nDisease Site: " + diseaseSite;
		data += "\nPatient Type: " + type;
		data += "\nCategory: " + category;
		data += "\nHistory of TB Drugs: " + tbhist;
		for(int i=0; i<smears.length; i++) {
			if(!smears[i].equals(" ")) {
				data += "\nSmear - " + (i+1) + "m: " + smears[i];
			}
		}
		data += "\nSmear - 1m: " + smears[0];
		data += "\nSmear - 2m: " + smears[1];
		data += "\nSmear - 3m: " + smears[2];
		data += "\nSmear - 4m: " + smears[3];
		data += "\nSmear - 5m: " + smears[4];
		data += "\nSmear - 6m: " + smears[5];
		data += "\nSmear - 7m: " + smears[6];
		data += "\nSmear - 8m: " + smears[7];
		data += "\nSmear - 9m: " + smears[8];
		data += "\nSmear - 10m: " + smears[9];
		data += "\n" + diagType + "Clinical Diagnosis: " + diag;
		data += "\n" + diagType + "Clinical Diagnosis Date: " + diagDate;
		data += "\nFollow-ups: " + followUpData; 
		data += "\nEnd of Followup: " + endOfFollowup;
		data += "\nNo Active Followup: " + naf;
		if(request.getParameter("un").equalsIgnoreCase("G-QUACK-99"))
		{
			data += "\nNext Steps: " + flowPosition;
		}
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement("data");
		Text dataValue = doc.createTextNode(data);
		dataNode.appendChild(dataValue);

		responseNode.appendChild(dataNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
		
		return xml;
	}
	
	private String doDFR() {
		String xml = null;
		String id = request.getParameter("id").toUpperCase();
		
		String location = request.getParameter("loc");
		String locationDetail = request.getParameter("locd");
		String gpId = request.getParameter("gpid");
		String attempted = request.getParameter("att");
		String screened = request.getParameter("scr");
		String refused = request.getParameter("ref");
		String missed = request.getParameter("mis");
		String suspects = request.getParameter("sus");
		

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		
		if(gpId==null) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara form save karain");
		}
		
		//start CHW ID validity check
 		Boolean cCheck = null;
		if((cCheck = ModelUtil.isValidCHWID(id))!=null) {
			if(!cCheck.booleanValue()) {
				return XmlUtil.createErrorXml("Ye CHW ID sahih nahin. Sahih CHW ID darj karain aur dobara koshish karain");
			}
		}
		
		else {
			return XmlUtil.createErrorXml("ERROR: Please try again");
		}
		//end GP ID validity check
		
		//start GP ID validity check
		Boolean gCheck = null;
		if((gCheck = ModelUtil.isValidGPID(gpId))!=null) {
			if(!gCheck.booleanValue()) {
				return XmlUtil.createErrorXml("Ye GP ID sahih nahin. Sahih GP ID darj karain aur dobara koshish karain");
			}
		}
		
		else {
			return XmlUtil.createErrorXml("ERROR: Please try again");
		}
		//end GP ID validity check
		
		//begin dup DFR check
		//String query = "select EncounterID from Encounter where EncounterType='DFR' AND DateEnounterEntered IS NOT NULL AND DateEnounterEntered LIKE '" + DateTimeUtil.convertFromSlashFormatToSQL(enteredDate.split(" ")[0]) +  "%'";
		
		String results[] = null;
		
		try {
			results = ssl.getColumnData("Encounter", "EncounterID",  " where PID1='" + id + "' AND EncounterType='DFR' AND DateEncounterEntered IS NOT NULL AND DateEncounterEntered LIKE '" + DateTimeUtil.convertFromSlashFormatToSQL(enteredDate.split(" ")[0]) +  "%'");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String gp = "";
		String er[] = null;
		if(results!=null)
		{
			for(int i=0; i<results.length;i++) {
				System.out.println((String)(results[i]));
			}
			
			for(int i=0; i<results.length;i++) {
				
					try {
						er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='GP_ID' AND EncounterID='" + (String)(results[i]) + "'");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(er!=null && er.length>0)
					{
						gp = er[0];
						if(gp!=null && gp.equalsIgnoreCase(gpId)) {
							return XmlUtil.createErrorXml("Aap ne aaj is GPID ka ek DFR pehlay hi bhar diya hai. Agar form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain.");
						}
					}
					
			}
			
		}
		
		//end dup DFR check
		
		
		EncounterId encId = new EncounterId();
		encId.setPid1(id.toUpperCase());
		encId.setPid2(id.toUpperCase());

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("DFR");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Error occurred. Please try again");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		EncounterResults locationResult = ModelUtil.createEncounterResult(
				e, "location".toUpperCase(), location.toUpperCase());
		encounters.add(locationResult);

		EncounterResults locationDetailResult = ModelUtil
				.createEncounterResult(e, "location_detail".toUpperCase(),
						locationDetail.toUpperCase());
		encounters.add(locationDetailResult);
		
		EncounterResults gpIdResult = ModelUtil
		.createEncounterResult(e, "gp_id".toUpperCase(),
				gpId.toUpperCase());
		encounters.add(gpIdResult);

		EncounterResults attemptedResult = ModelUtil
				.createEncounterResult(e, "attempted".toUpperCase(),
						attempted.toUpperCase());
		encounters.add(attemptedResult);

		EncounterResults screenedResult = ModelUtil
				.createEncounterResult(e, "screened".toUpperCase(),
						screened.toUpperCase());
		encounters.add(screenedResult);

		EncounterResults missedResult = ModelUtil.createEncounterResult(e,
				"missed".toUpperCase(), missed.toUpperCase());
		encounters.add(missedResult);
		
		EncounterResults refusedResult = ModelUtil.createEncounterResult(e,
				"refused".toUpperCase(), refused.toUpperCase());
		encounters.add(refusedResult);

		EncounterResults suspectsResult = ModelUtil.createEncounterResult(e,
				"suspects".toUpperCase(), suspects.toUpperCase());
		encounters.add(suspectsResult);
		
		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("Error occurred. Please try again");
			}

		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}
	
	
	private String doCDF() {
		String xml = null;
		String id = request.getParameter("id");
		
		String gpid = request.getParameter("gpid");
		String diagnosis = request.getParameter("cd");
		String otherDiagnosis = request.getParameter("otd");
		if(otherDiagnosis==null) {
			otherDiagnosis = "";
		}
		String clinicalDiagnosis = request.getParameter("clinTB");
		String xraySuggestive = request.getParameter("xray");
		String pastHistory = request.getParameter("phist");
		String contactHistory = request.getParameter("chist");
		String antibioticTrial = request.getParameter("anti");
		String largeLymph = request.getParameter("lymph");
		String lymphBiopsy = request.getParameter("lbiop");
		
		String mantoux = request.getParameter("mt");
		
		String other = request.getParameter("other");
		
		String otherDiagnostic = request.getParameter("otherdiag");
		if(otherDiagnostic==null) {
			otherDiagnostic = "";
		}
		
		String notes = request.getParameter("notes");
		if(notes==null) {
			notes="";
		}

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		
		try {
			Boolean check = ssl.exists("Encounter", " where PID1='" + id + "' AND EncounterType='CDF'");
			
			if(check==null) {
				return XmlUtil.createErrorXml("Could not save data. Please try again");
			}
			
			else if(check.booleanValue()) {
				return XmlUtil.createErrorXml("Adult Clinical Diagnosis for this patient is already complete");
			}
		}
		
		catch(Exception e) {
			return XmlUtil.createErrorXml("Could not access encounter record. Please try again");
		}
		
		Patient pat = null;
		
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return XmlUtil.createErrorXml("Could not access patient record. Please try again");
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Could not access patient record. Please try again");
		}
		
		
		
		Boolean patientPositive = pat.getDiseaseConfirmed();
		
		if(patientPositive.booleanValue()) {
			return XmlUtil.createErrorXml("This patient has already been diagnosed with TB");
		}
		
		else if(!diagnosis.equalsIgnoreCase("OTHER THAN TB")) {
			pat.setDiseaseConfirmed(new Boolean(true));
			try {
				boolean check = ssl.updatePatient(pat);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return XmlUtil.createErrorXml("Error updating Patient. Please try again");
			}
			
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(gpid);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("CDF");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Error occurred. Please try again");
		}

		ArrayList<String> encounters = new ArrayList<String>();

		encounters.add("ENTERED_DATE="+enteredDate);
		encounters.add("DIAGNOSIS="+diagnosis.toUpperCase());
		encounters.add("OTHER_THAN_TB_DETAIL="+otherDiagnosis.toUpperCase());
		encounters.add("CLINICAL_SYMP_TB="+clinicalDiagnosis.toUpperCase());
		encounters.add("XRAY_SUGGESTIVE="+xraySuggestive.toUpperCase());
		encounters.add("PAST_HISTORY="+pastHistory.toUpperCase());
		encounters.add("CONTACT_HISTORY="+contactHistory.toUpperCase());
		encounters.add("ANTIBIOTIC_TRIAL="+antibioticTrial.toUpperCase());
		encounters.add("LARGE_LYMPH_NODES="+largeLymph.toUpperCase());
		encounters.add("LYMPH_NODE_BIOPSY="+lymphBiopsy.toUpperCase());
		encounters.add("MANTOUX="+mantoux.toUpperCase());
		encounters.add("OTHER_DIAGNOSTIC="+other.toUpperCase());
		encounters.add("OTHER_DIAGNOSTIC_DETAIL="+otherDiagnostic.toUpperCase());
		encounters.add("NOTES="+notes.toUpperCase());
		
		try {
			ssl.saveEncounterWithResults(e, encounters);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			return XmlUtil.createErrorXml("ERROR");
		}
		
		if(!diagnosis.equals("OTHER THAN TB")) {
			
			IncentiveId gpIncentive = new IncentiveId();
			
			String gpId = pat.getGpid();
			gpIncentive.setPid(gpId);
			gpIncentive.setIncentiveId("GP_DETECT");
			
			Incentive gpInc = new Incentive();
			gpInc.setId(gpIncentive);
			gpInc.setDateTransferred(new Date(System.currentTimeMillis()));
			gpInc.setStatus("PENDING");


			try {
				ssl.saveIncentive(gpInc);
			} catch (Exception e1) {
				
				e1.printStackTrace();
				System.out.println("ERROR: Could not save Clinical Diagnosis Detection Incentive for GP " + gpId + "/ Patient " + id);
			}
			
			SetupIncentive gpDetectIncentive = null;
			
			try {
				gpDetectIncentive = ssl.findSetupIncentive("GP_DETECT");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//for the CHW
			IncentiveId chwIncentive = new IncentiveId();
			
			String chwId = pat.getChwid();
			if(chwId!=null) {
				chwIncentive.setPid(chwId);
				chwIncentive.setIncentiveId("CHW_DETECT");
			
				Incentive chwInc = new Incentive();
				chwInc.setId(chwIncentive);
				chwInc.setDateTransferred(new Date(System.currentTimeMillis()));
				chwInc.setStatus("PENDING");


				try {
					ssl.saveIncentive(chwInc);
				} catch (Exception e1) {
					
					e1.printStackTrace();
					System.out.println("ERROR: Could not save Clinical Diagnosis Detection Incentive for CHW " + chwId + "/ Patient " + id);
				}
			}
			
			SetupIncentive chwDetectIncentive = null;
			
			try {
				chwDetectIncentive = ssl.findSetupIncentive("CHW_DETECT");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				ssl.sendAlertsOnClinicalDiagnosis(encId, true, gpDetectIncentive, chwDetectIncentive);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}*/
	
	/*private String getFormCount() {
		Hashtable types = new Hashtable();
		
		types.put("SUSPECT_ID", "Form A");
		types.put("SUSPECTCON", "Form B");
		types.put("SUSPECTVER", "Form C");
		types.put("P_INFO", "Form D1");
		types.put("TB_HISTORY", "Form D2");
		types.put("GPS", "Form D3");
		types.put("REFUSAL", "Form E");
		types.put("SPUTUM_COL", "Form F");
		types.put("BASELINE", "Form I");
		types.put("FOLLOW_UP", "Form J");
		types.put("DRUG_ADM", "Form K");
		types.put("END_FOL", "Form L");
		types.put("MR_ASSIGN", "MR Assigned");
		types.put("GP_NEW", "GP Screened Suspect");
		types.put("CDF", "Form Q");
		
		String xml = null;
		String id = request.getParameter("mid");
		String date = request.getParameter("date");
		
		date = DateTimeUtil.convertFromSlashFormatToSQL(date);
		
		System.out.println(id);
		System.out.println(date);
		ssl = new ServerServiceImpl();
		
		Object[][] encounters =null;
		
		try {
			encounters = HibernateUtil.util.selectData("select count(*),EncounterType from Encounter where PID2='" + id.toUpperCase() + "' AND DateEncounterStart >= '" + date + " 00:00:01' AND DateEncounterStart <= '" + date + " 23:59:59' GROUP BY EncounterType ORDER BY EncounterType");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(encounters==null) {
			return XmlUtil.createErrorXml("Error while performing search. Please try again");
		}
		
		else if(encounters.length==0) {
			return XmlUtil.createErrorXml("No results found");
		}
		
		String data = "EncounterType:  Submitted\n";
		String encType = "";
		BigInteger count = null;
		EncounterResultsId encResultId = null;
		EncounterResults result = null;
		String bc = "";
		
		for(int i=0; i<encounters.length; i++) {
			count = (BigInteger)encounters[i][0];
			encType = (String)encounters[i][1];
			
			if(types.get(encType)!=null) {
				encType=(String)(types.get(encType));
			}
					
			data += encType + ": " + count + "\n";
			
		}
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement("data");
		Text dataValue = doc.createTextNode(data);
		dataNode.appendChild(dataValue);

		responseNode.appendChild(dataNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
		
		return xml;
		
	}
	
	
	private long getAgeInYears(Date dob) {
		long age = -1;
		
		GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
		gc.setTime(dob);
		
		GregorianCalendar gcNow = (GregorianCalendar) GregorianCalendar.getInstance();
		gcNow.setTimeInMillis(System.currentTimeMillis());
		
		long diff = gcNow.getTimeInMillis() - gc.getTimeInMillis();
		System.out.println("DIFF:" + diff);
		
		double diffInSeconds = diff/1000;
		System.out.println(diffInSeconds);
		double diffInMinutes = diffInSeconds/60;
		System.out.println(diffInMinutes);
		double diffInHours = diffInMinutes/60;
		System.out.println(diffInHours);
		double diffInDays = diffInHours/24;
		System.out.println(diffInDays);
		double diffInYears = diffInDays/365;
		System.out.println(diffInYears);
		
		Double ageDbl = new Double(diffInYears);
		
		return ageDbl.longValue();
	
	}
	
	/////////////////
	// *****************************************
	// doSuspectIdentification*************************/

	/*private String doSuspectIdentificationCT() {

		String xml = null;
		String id = request.getParameter("id");
		String contactMr = request.getParameter("cmr");
		String chwId = request.getParameter("chwid");
		String gpId = request.getParameter("gpid");
		String firstName = request.getParameter("fn");
		String lastName = request.getParameter("ln");
		String sex = request.getParameter("sex");
		String age = request.getParameter("age");
		String cough = request.getParameter("cough");
		String coughDuration = request.getParameter("cd");
		String productiveCough = request.getParameter("pc");
		String tbHistory = request.getParameter("tbh");
		
		String fever = request.getParameter("fev");
		String nightSweat = request.getParameter("ns");
		String weightLoss = request.getParameter("wl");
		String haemoptysis = request.getParameter("ha");
		String diabetes = request.getParameter("diab");
		String contactAgeLessThanFive = request.getParameter("cage");
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		

		System.out.println(startDate);
		System.out.println(startTime);
		System.out.println(endTime);
		System.out.println(enteredDate);

		// Transaction t = HibernateUtil.util.getSession().beginTransaction();
		if(cough==null) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara form save karain");
		}
		
		
		Person checkPerson = null;
		
		try {
			checkPerson = ssl.findPerson(id);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
		}
		
		if(checkPerson!=null) {
			return XmlUtil.createErrorXml("Person with ID " + id + " already exists!");
		}
		
		Date dob = getDOBFromAge(Integer.parseInt(age));
		
		Person p = new Person();
		p.setPid(id);
		p.setFirstName(firstName.toUpperCase());
		p.setLastName(lastName.toUpperCase());
		p.setGender(sex.toUpperCase().charAt(0));
		p.setDob(dob);
		p.setRoleInSystem("PATIENT");

		boolean pCreated = true;
		try {
			pCreated = ssl.savePerson(p);
		} catch (Exception e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}

		if (!pCreated) {
			return XmlUtil
					.createErrorXml("Could not create Person. Please try again");
		}

		Patient pat = new Patient();
		pat.setPatientId(id);
		pat.setDateRegistered(new Date());
		
		
		pat.setPatientStatus("SUSPECT");
		pat.setDiseaseSuspected(new Boolean(false));
		pat.setDiseaseConfirmed(new Boolean(false));
		
		pat.setChwid(chwId.toUpperCase());
		
		pat.setGpid(gpId.toUpperCase());

		try {
			boolean patCreated = ssl.savePatient(pat);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}

		EncounterId encId = new EncounterId();
		encId.setEncounterId(-1);
		encId.setPid1(id.toUpperCase());
		
		encId.setPid2(chwId.toUpperCase());
		
		Encounter e = new Encounter();
		e.setId(encId);
		
		e.setEncounterType("CT_SUSPECT");
		
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		// EncounterResultsId erId = new
		// EncounterResultsId(e.getId().getEncounterId(), e.getId().getPid1(),
		// e.getId().getPid2(), "entered_date");
		// EncounterResults er = new EncounterResults(erId, enteredDate);

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);
		
		EncounterResults cmrResult = ModelUtil.createEncounterResult(e,
				"contact_mr".toUpperCase(), contactMr);
		encounters.add(cmrResult);

		EncounterResults gpIdResult = ModelUtil.createEncounterResult(e,
				"gp_id".toUpperCase(), gpId.toUpperCase());
		encounters.add(gpIdResult);

		EncounterResults ageResult = ModelUtil.createEncounterResult(e,
				"age".toUpperCase(), age);
		encounters.add(ageResult);
		
		EncounterResults phoneResult = ModelUtil.createEncounterResult(e,
				"phone".toUpperCase(), phone);
		encounters.add(phoneResult);
		
		EncounterResults coughResult = ModelUtil.createEncounterResult(e,
				"cough".toUpperCase(), cough.toUpperCase());
		encounters.add(coughResult);
		
		if(coughDuration!=null) {
			EncounterResults coughDurationResult = ModelUtil.createEncounterResult(e,
					"cough_duration".toUpperCase(), coughDuration.toUpperCase());
			encounters.add(coughDurationResult);
			
			if(productiveCough!=null) {
				EncounterResults productiveCoughResult = ModelUtil.createEncounterResult(e,
						"productive_cough".toUpperCase(), productiveCough.toUpperCase());
				encounters.add(productiveCoughResult);
						
			}
		}
			
		if(coughDuration!=null && productiveCough!=null && (coughDuration.equals("2 to 3 weeks") || coughDuration.equals("more than 3 weeks") ) && productiveCough.equals("Yes")) {
			EncounterResults twoWeeksProdCoughResult = ModelUtil.createEncounterResult(e,
					"two_weeks_cough".toUpperCase(), "YES");
			encounters.add(twoWeeksProdCoughResult);
		}
		
		else {
			EncounterResults twoWeeksProdCoughResult = ModelUtil.createEncounterResult(e,
					"two_weeks_cough".toUpperCase(), "NO");
			encounters.add(twoWeeksProdCoughResult);
		}
		
		EncounterResults tbHistoryResult = ModelUtil.createEncounterResult(e,
				"tb_history".toUpperCase(), tbHistory.toUpperCase());
		encounters.add(tbHistoryResult);
		
		if(fever!=null) {
			EncounterResults feverResult = ModelUtil.createEncounterResult(e,
					"fever".toUpperCase(), fever.toUpperCase());
			encounters.add(feverResult);
		}
		
		if(nightSweat!=null) {
			EncounterResults nsResult = ModelUtil.createEncounterResult(e,
					"night_sweats".toUpperCase(), nightSweat.toUpperCase());
			encounters.add(nsResult);
		}
		
		if(weightLoss!=null) {
			EncounterResults wlResult = ModelUtil.createEncounterResult(e,
					"weight_loss".toUpperCase(), weightLoss.toUpperCase());
			encounters.add(wlResult);
		}
		
		if(haemoptysis!=null) {
			EncounterResults hResult = ModelUtil.createEncounterResult(e,
					"haemoptysis".toUpperCase(), haemoptysis.toUpperCase());
			encounters.add(hResult);
		}
		
		if(diabetes!=null) {
			EncounterResults hResult = ModelUtil.createEncounterResult(e,
					"diabetes".toUpperCase(), diabetes.toUpperCase());
			encounters.add(hResult);
		}
		
		if(contactAgeLessThanFive!=null) {
			EncounterResults hResult = ModelUtil.createEncounterResult(e,
					"contact_less_than_five".toUpperCase(), contactAgeLessThanFive.toUpperCase());
			encounters.add(hResult);
		}
		
		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}

		}
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}
	
	/*private String doPaedConfirmation() {
		String xml = null;
		
		String id = request.getParameter("id");
		String gpId = request.getParameter("gpid");
		String weight = request.getParameter("wt");
		String weightPercentile = request.getParameter("wp");
		String cough = request.getParameter("cough");
		String coughDuration = request.getParameter("cd");
		String productiveCough = request.getParameter("pc");
		String fever = request.getParameter("fev");
		String nightSweat = request.getParameter("ns");
		String weightLoss = request.getParameter("wl");
		String haemoptysis = request.getParameter("ha");
		String appetite = request.getParameter("app");
		String chestExam = request.getParameter("chest");
		String lymphExam = request.getParameter("lymph");
		String abdm = request.getParameter("abdm");
		String otherExam = request.getParameter("otherEx");
		String otherDet = request.getParameter("otherDet");
		if(otherDet==null) {
			otherDet="";
		}
		String bcgScar = request.getParameter("bcg");
		String currentHistory = request.getParameter("cfh");
		String numCurrentHistory = request.getParameter("numcfh");
			
		String previousHistory = request.getParameter("pfh");
		String numPreviousHistory = request.getParameter("numpfh");
		String conclusion = request.getParameter("conc");
		
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		String un = request.getParameter("un");
		
		if(un==null) {
			return XmlUtil.createErrorXml("Error! Please contact technical support");
		}
		
		if(cough==null) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara form save karain");
		}
		
		un = un.toUpperCase();
		Users users = null;
		try {
			users = ssl.findUser(un);
		} catch (Exception e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
			return XmlUtil.createErrorXml("Bad username. Please try again");
		}
		String pid2 = users.getPid();
		
		
		
		Patient pat = null;
		try {
			pat = (Patient) (ssl.findPatient(id));
		} catch (Exception e3) {
			//  Auto-generated catch block
			e3.printStackTrace();
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		if(pat.getPatientStatus()!=null && !pat.getPatientStatus().equals("SUSPECT")) {
			return XmlUtil.createErrorXml("Patient has already been confirmed!");
		}

		if (conclusion.equalsIgnoreCase("confirmed")) {
			pat.setDiseaseSuspected(new Boolean(true));
			//pat.setPatientStatus("GP_CONF");
		}

		else {
			pat.setDiseaseSuspected(new Boolean(false));
			//pat.setPatientStatus("GP_NO_CONF");
		}

		//pat.setGpid(gpId.toUpperCase());
		pat.setWeight(Float.parseFloat(weight));
		
		try {
			boolean patUpdate = ssl.updatePatient(pat);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
			
			return XmlUtil.createErrorXml("Could not update Patient. Please try again.");
			
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(pid2);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("PAED_CONF");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Could not save encounter. Please try again");
			
		}
		
		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		EncounterResults weightResult = ModelUtil.createEncounterResult(e,
				"weight".toUpperCase(), weight.toUpperCase());
		encounters.add(weightResult);

		EncounterResults phaseResult = ModelUtil.createEncounterResult(e,
				"weight_percentile".toUpperCase(), weightPercentile.toUpperCase());
		encounters.add(phaseResult);

		EncounterResults coughResult = ModelUtil.createEncounterResult(e,
				"cough".toUpperCase(), cough.toUpperCase());
		encounters.add(coughResult);
		
		if(coughDuration!=null) {
			EncounterResults coughDurationResult = ModelUtil.createEncounterResult(e,
					"cough_duration".toUpperCase(), coughDuration.toUpperCase());
			encounters.add(coughDurationResult);
			
			if(productiveCough!=null) {
				EncounterResults productiveCoughResult = ModelUtil.createEncounterResult(e,
						"productive_cough".toUpperCase(), productiveCough.toUpperCase());
				encounters.add(productiveCoughResult);
						
			}
		}
			
		if(coughDuration!=null && productiveCough!=null && (coughDuration.equals("2 to 3 weeks") || coughDuration.equals("more than 3 weeks") ) && productiveCough.equals("Yes")) {
			EncounterResults twoWeeksProdCoughResult = ModelUtil.createEncounterResult(e,
					"two_weeks_cough".toUpperCase(), "YES");
			encounters.add(twoWeeksProdCoughResult);
		}
		
		else {
			EncounterResults twoWeeksProdCoughResult = ModelUtil.createEncounterResult(e,
					"two_weeks_cough".toUpperCase(), "NO");
			encounters.add(twoWeeksProdCoughResult);
		}
		
		EncounterResults feverResult = ModelUtil.createEncounterResult(e,
				"fever".toUpperCase(), fever.toUpperCase());
		encounters.add(feverResult);

		EncounterResults nsResult = ModelUtil.createEncounterResult(e,
				"night_sweats".toUpperCase(), nightSweat.toUpperCase());
		encounters.add(nsResult);

		EncounterResults wlResult = ModelUtil.createEncounterResult(e,
				"weight_loss".toUpperCase(), weightLoss.toUpperCase());
		encounters.add(wlResult);

		EncounterResults hResult = ModelUtil.createEncounterResult(e,
				"haemoptysis".toUpperCase(), haemoptysis.toUpperCase());
		encounters.add(hResult);

		EncounterResults appResult = ModelUtil.createEncounterResult(e,
				"appetite".toUpperCase(), appetite.toUpperCase());
		encounters.add(appResult);
		
		EncounterResults chestResult = ModelUtil.createEncounterResult(e,
				"chest_exam".toUpperCase(), chestExam.toUpperCase());
		encounters.add(chestResult);
		
		EncounterResults lymphResult = ModelUtil.createEncounterResult(e,
				"lymph_exam".toUpperCase(), lymphExam.toUpperCase());
		encounters.add(lymphResult);
		
		EncounterResults abdResult = ModelUtil.createEncounterResult(e,
				"abd_mass".toUpperCase(), abdm.toUpperCase());
		encounters.add(abdResult);
		
		EncounterResults otherResult = ModelUtil.createEncounterResult(e,
				"other_exam".toUpperCase(), otherExam.toUpperCase());
		encounters.add(otherResult);

		EncounterResults otherDetailResult = ModelUtil.createEncounterResult(e,
				"other_exam_detail".toUpperCase(), otherDet.toUpperCase());
		encounters.add(otherDetailResult);
		
		EncounterResults bcgResult = ModelUtil.createEncounterResult(e,
				"bcg_scar".toUpperCase(), bcgScar.toUpperCase());
		encounters.add(bcgResult);
		
		EncounterResults currFamHistResult = ModelUtil.createEncounterResult(e,
				"current_history".toUpperCase(), currentHistory.toUpperCase());
		encounters.add(currFamHistResult);
		
		if(numCurrentHistory!=null)
		{
			EncounterResults numCurrFamHistResult = ModelUtil.createEncounterResult(e,
				"num_current_history".toUpperCase(), numCurrentHistory.toUpperCase());
			encounters.add(numCurrFamHistResult);
			
			//loop for current history
			int numCurr = Integer.parseInt(numCurrentHistory);
			
			for(int q=0;q<numCurr;q++) {
				encounters.add(ModelUtil.createEncounterResult(e, "current_rel_".toUpperCase() + (q+1), request.getParameter("crel" + (q+1))));
				encounters.add(ModelUtil.createEncounterResult(e, "current_other_rel_".toUpperCase() + (q+1), request.getParameter("cothrel" + (q+1))));
				encounters.add(ModelUtil.createEncounterResult(e, "current_tb_form_".toUpperCase() + (q+1), request.getParameter("ctbf" + (q+1))));
				encounters.add(ModelUtil.createEncounterResult(e, "current_tb_type_".toUpperCase() + (q+1), request.getParameter("ctbt" + (q+1))));
				encounters.add(ModelUtil.createEncounterResult(e, "current_ss_".toUpperCase() + (q+1), request.getParameter("css" + (q+1))));
			}
		}
		EncounterResults prevFamHistResult = ModelUtil.createEncounterResult(e,
				"previous_history".toUpperCase(), previousHistory.toUpperCase());
		encounters.add(prevFamHistResult);
		
		if(numPreviousHistory!=null) {
			EncounterResults numPrevFamHistResult = ModelUtil.createEncounterResult(e,
					"num_previous_history".toUpperCase(), numPreviousHistory.toUpperCase());
			encounters.add(numPrevFamHistResult);
			
			//loop for previous history
			int numPrev = Integer.parseInt(numPreviousHistory);
			
			for(int q=0;q<numPrev;q++) {
				encounters.add(ModelUtil.createEncounterResult(e, "previous_rel_".toUpperCase() + (q+1), request.getParameter("prel" + (q+1))));
				encounters.add(ModelUtil.createEncounterResult(e, "previous_other_rel_".toUpperCase() + (q+1), request.getParameter("pothrel" + (q+1))));
				encounters.add(ModelUtil.createEncounterResult(e, "previous_tb_form_".toUpperCase() + (q+1), request.getParameter("ptbf" + (q+1))));
				encounters.add(ModelUtil.createEncounterResult(e, "previous_tb_type_".toUpperCase() + (q+1), request.getParameter("ptbt" + (q+1))));
				encounters.add(ModelUtil.createEncounterResult(e, "previous_ss_".toUpperCase() + (q+1), request.getParameter("pss" + (q+1))));
			}
		}
		
		EncounterResults concResult = ModelUtil.createEncounterResult(e,
				"conclusion".toUpperCase(), conclusion.toUpperCase());
		encounters.add(concResult);


		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR! Please try again");
			}

		}
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}
///////////////////////////////////////
	
	/*public String getPaedConfirmationInfo(String id) {
		String xml = null;

		
		 ssl = new ServerServiceImpl();
		 Person p = null; 
		 Patient pat = null;
		 
		 Date dob = null;
		  
		  try { 
			  p = (Person)ssl.findPerson(id); 
		  } 
		  catch (Exception e2) {
			  e2.printStackTrace(); 
		  }
		  
		  if(p==null) {
			  return XmlUtil.createErrorXml("Could not find Person with id " + id + "Please try again.");
		  }
		  
		  long age = 0;
		  dob = p.getDob();
		  if(dob!=null)
		  {	  age = getAgeInYears(dob);
		  	  System.out.println("AGE:->>>" + age);
		  }
		  
		  else {
			  age = -1;
			  System.out.println("AGE:->>>" + age);
		  }
		  
		  if(age >= 15) {
			  return XmlUtil.createErrorXml("Ye patient 15 ya us se ziada umer ka hai. In ke liye Q form bharain");
		  }
		  
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + "Please try again.");
		  }
		  
		 
		  String firstname = p.getFirstName();
		  String lastname = p.getLastName(); 
		  String gender = "";
		  if(p.getGender()=='M') {
			  gender = "Male";
		  }
		  
		  else {
			  gender = "Female";
		  }
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element fNameNode = doc.createElement("fname");
		//Text fNameValue = doc.createTextNode("Test");
		Text fNameValue = doc.createTextNode(firstname);
		fNameNode.appendChild(fNameValue);

		responseNode.appendChild(fNameNode);

		Element lNameNode = doc.createElement("lname");
		//Text lNameValue = doc.createTextNode("Patient");
		Text lNameValue = doc.createTextNode(lastname);
		lNameNode.appendChild(lNameValue);

		responseNode.appendChild(lNameNode);
		
		Element mrNode = doc.createElement("mr");
		//Text lNameValue = doc.createTextNode("Patient");
		Text mrValue = doc.createTextNode(pat.getMrno());
		mrNode.appendChild(mrValue);

		responseNode.appendChild(mrNode);

		Element genderNode = doc.createElement("gender");
		//Text lNameValue = doc.createTextNode("Patient");
		Text genderValue = doc.createTextNode(gender);
		genderNode.appendChild(genderValue);

		responseNode.appendChild(genderNode);
		
		Element ageNode = doc.createElement("age");
		//Text lNameValue = doc.createTextNode("Patient");
		Text ageValue = doc.createTextNode(new Long(age).toString());
		ageNode.appendChild(ageValue);

		responseNode.appendChild(ageNode);
		
		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	
		
	}*/
/*/////////////// P FORM
	private String doPaedClinicalDiagnosis() {
		String xml = null;
		
		String id = request.getParameter("id");
		String gpId = request.getParameter("gpid");
		String afb = request.getParameter("afb");
		String ppd = request.getParameter("ppd");
		String ppdr = request.getParameter("ppdr");
		if(ppdr==null)
			ppdr = "";
		String cxr = request.getParameter("cxr");
		String granuloma = request.getParameter("gran");
		String ppaDone = request.getParameter("ppa");
		String initPPA = request.getParameter("initppa");
		if(initPPA==null)
			initPPA ="";
		String finalPPA = request.getParameter("finalppa");
		if(finalPPA==null) {
			finalPPA = "";
		}
		String conclusion = request.getParameter("conc");
		String antiFollowup = request.getParameter("antitime");
		if(antiFollowup==null)
			antiFollowup = "";
		String otherDisease = request.getParameter("otherdis");
		if(otherDisease==null)
			otherDisease = "";
		String otherDetails = request.getParameter("other");
		if(otherDetails==null)
			otherDetails = "";
		
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		
		String un = request.getParameter("un");
		
		if(un==null) {
			return XmlUtil.createErrorXml("Error! Please contact technical support");
		}
		
		un = un.toUpperCase();
		Users users = null;
		try {
			users = ssl.findUser(un);
		} catch (Exception e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
			return XmlUtil.createErrorXml("Bad username. Please try again");
		}
		String pid2 = users.getPid();
		
		Patient pat = null;
		
		boolean incentFlag = false;
		try {
			pat = (Patient) (ssl.findPatient(id));
		} catch (Exception e3) {
			//  Auto-generated catch block
			e3.printStackTrace();
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		Boolean exists = null;
		
		try {
			exists = ssl.exists("Encounter"," where PID1='" + id + "' AND DateEncounterEntered LIKE '" + DateTimeUtil.convertFromSlashFormatToSQL(enteredDate.split(" ")[0]) +  "%' AND EncounterType='PAED_DIAG'");
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		if(exists==null) {
			return XmlUtil.createErrorXml("Error tracking Patient ID");
		}
		
		else if(exists.booleanValue()==true) {
			return XmlUtil.createErrorXml("Patient " + id + " ka P form aaj hi ki tareekh mein pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}

		//set disease confirmed to true if  not already true and if TB is diagnosed
		//if already true, do not send alert/incentive
		
		//pat.setGpid(gpId.toUpperCase());
		if(!conclusion.equals("Given Antibiotic Followup") && !conclusion.equals("No Follow up required (other disease)") && !pat.getDiseaseConfirmed()) {
			pat.setDiseaseConfirmed(new Boolean(true));
			incentFlag = true;
		}
		
		try {
			boolean patUpdate = ssl.updatePatient(pat);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
			
			return XmlUtil.createErrorXml("Could not update Patient. Please try again.");
			
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(pid2);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("PAED_DIAG");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Could not save encounter. Please try again");
			
		}
		
		ArrayList<String> encounters = new ArrayList<String>();

	
				encounters.add("ENTERED_DATE="+enteredDate);
				encounters.add("GPID="+gpId.toUpperCase());
				encounters.add("AFB="+afb.toUpperCase());
				encounters.add("PPD="+ppd.toUpperCase());
				encounters.add("PPDR="+ppdr.toUpperCase());
				encounters.add("CXR="+cxr.toUpperCase());
				encounters.add("GRANULOMA="+granuloma.toUpperCase());
				encounters.add("PPA_DONE="+ppaDone.toUpperCase());
				encounters.add("INIT_PPA="+initPPA.toUpperCase());
				encounters.add("FINAL_PPA="+finalPPA.toUpperCase());
				encounters.add("DIAGNOSIS="+conclusion.toUpperCase());
				encounters.add("ANTIBIOTIC_FO_TIME="+antiFollowup.toUpperCase());
				encounters.add("OTHER_DISEASE="+otherDisease.toUpperCase());
				encounters.add("OTHER_DETAIL="+otherDetails.toUpperCase());
				try {
					ssl.saveEncounterWithResults(e, encounters);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					return XmlUtil.createErrorXml("ERROR");
				}
		
				
				
		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR! Please try again");
			}

		}
		
		//add incentive/alert here if incentFlag is true
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}
///////////////////////////////////////
	
	public String getPaedClinicalDiagInfo(String id) {
		String xml = null;

		
		 ssl = new ServerServiceImpl();
		 Person p = null; 
		 Patient pat = null;
		 
		 Date dob = null;
		  
		  try { 
			  p = (Person)ssl.findPerson(id); 
		  } 
		  catch (Exception e2) {
			  e2.printStackTrace(); 
		  }
		  
		  if(p==null) {
			  return XmlUtil.createErrorXml("Could not find Person with id " + id + "Please try again.");
		  }
		  
		  long age = 0;
		  dob = p.getDob();
		  if(dob!=null)
		  {	  age = getAgeInYears(dob);
		  	  System.out.println("AGE:->>>" + age);
		  }
		  
		  else {
			  age = -1;
			  System.out.println("AGE:->>>" + age);
		  }
		  
				
		  if(age >= 15) {
			  return XmlUtil.createErrorXml("Ye patient 15 ya us se ziada umer ka hai. In ke liye Q form bharain");
		  }
		  
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + "Please try again.");
		  }
		  
		 
		  String firstname = p.getFirstName();
		  String lastname = p.getLastName(); 
		  String gender = "";
		  if(p.getGender()=='M') {
			  gender = "Male";
		  }
		  
		  else {
			  gender = "Female";
		  }
		  
		//get other data here 
		  String weight  = "";
		  if(pat.getWeight()!=null)
		   weight = pat.getWeight().toString();
		  
		//get confirmation encounter
		String rowRecord[] = null;
		
		try {
			rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID", "PID2"}, " where PID1= '" + id + "'AND encounterType='PAED_CONF'");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(rowRecord==null || rowRecord.length==0) {
			return XmlUtil.createErrorXml("No paediatric confirmation found. Please fill in O form first and then try again.");
		}
		
		String encounterId = rowRecord[0];
		int encId = Integer.parseInt(encounterId);
		String pid2 = rowRecord[1];
	
		
		String weightPercentile = null;
		String chestExam = null;
		String lymphExam = null;
		String abdMass = null;
		String otherExam = null;
		String otherExamDetail = null;
		String bcgScar = null;
		String currentHistory = null;
		String previousHistory = null;
		String numCurrentHistory = null;
		String numPreviousHistory = null;
		int numCurr = 0;
		int numPrev = 0;
		
		String currentHistoryDetails = "";
		String previousHistoryDetails = "";
		
		EncounterResultsId eri = null;
		String data = "";
		
		try {
			//get weight percentile	
			eri = new EncounterResultsId(encId,id,pid2,"WEIGHT_PERCENTILE");
			EncounterResults er = null;
			
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				weightPercentile = er.getValue();
			}
			
			//get various exam results
			eri.setElement("CHEST_EXAM");
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				chestExam = er.getValue();
			}
			
			eri.setElement("LYMPH_EXAM");
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				lymphExam = er.getValue();
			}
			
			eri.setElement("ABD_MASS");
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				abdMass = er.getValue();
			}
			
			eri.setElement("OTHER_EXAM");
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				otherExam = er.getValue();
			}
			  
			eri.setElement("OTHER_EXAM_DETAIL");
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				otherExamDetail = er.getValue();
			}
			
			//get bcg result
			eri.setElement("BCG_SCAR");
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				bcgScar = er.getValue();
			}
			
			//get current family history results
			eri.setElement("CURRENT_HISTORY");
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				currentHistory = er.getValue();
			}
			
			eri.setElement("NUM_CURRENT_HISTORY");
			try {
				er = ssl.findEncounterResultsByElement(eri);
			}
			
			catch(ArrayIndexOutOfBoundsException ao) {
				er = null;
			}
			if(er!=null) {
				numCurrentHistory = er.getValue();
				if(numCurrentHistory!=null && numCurrentHistory.length()>0) {
					numCurr = Integer.parseInt(numCurrentHistory);
					currentHistoryDetails += "\nNo. with current TB: " + numCurr;

 *  "current_rel_";
	"current_other_rel_"
	current_tb_form_"
	"current_tb_type_"
	"current_ss_"
 
					for(int i=0; i<numCurr; i++) {
						currentHistoryDetails += "\nFam. member " + (i+1);
						eri.setElement("CURRENT_REL_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							currentHistoryDetails += "\nRelationship: " + er.getValue();
						}
						
						eri.setElement("CURRENT_OTHER_REL_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							currentHistoryDetails += "\nOther Relationship: " + er.getValue();
						}
						
						eri.setElement("CURRENT_TB_FORM_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							currentHistoryDetails += "\nTB Form: " + er.getValue();
						}
						
						eri.setElement("CURRENT_TB_TYPE_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							currentHistoryDetails += "\nTB Type: " + er.getValue();
						}
						
						eri.setElement("CURRENT_SS_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							currentHistoryDetails += "\nSS +ve: " + er.getValue();
						}
					}
				}
			}
			  
			//get previous family history results
			eri.setElement("PREVIOUS_HISTORY");
			er = ssl.findEncounterResultsByElement(eri);
			if(er!=null) {
				previousHistory = er.getValue();
			}
			
			eri.setElement("NUM_PREVIOUS_HISTORY");
			try {
				er = ssl.findEncounterResultsByElement(eri);
			}
			
			catch(ArrayIndexOutOfBoundsException ao) {
				er = null;
			}
			if(er!=null) {
				numPreviousHistory = er.getValue();
				if(numPreviousHistory!=null && numPreviousHistory.length()>0) {
					numPrev = Integer.parseInt(numPreviousHistory);
					previousHistoryDetails += "\nNo. with previous TB: " + numPrev;
					
					for(int i=0; i<numPrev; i++) {
						previousHistoryDetails += "\nFam. member " + (i+1);
						eri.setElement("PREVIOUS_REL_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							previousHistoryDetails += "\nRelationship: " + er.getValue();
						}
						
						eri.setElement("PREVIOUS_OTHER_REL_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							previousHistoryDetails += "\nOther Relationship: " + er.getValue();
						}
						
						eri.setElement("PREVIOUS_TB_FORM_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							previousHistoryDetails += "\nTB Form: " + er.getValue();
						}
						
						eri.setElement("PREVIOUS_TB_TYPE_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							previousHistoryDetails += "\nTB Type: " + er.getValue();
						}
						
						eri.setElement("PREVIOUS_SS_" + (i+1));
						er = ssl.findEncounterResultsByElement(eri);
						if(er!=null) {
							previousHistoryDetails += "\nSS +ve: " + er.getValue();
						}
					}
				}
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Error retrieving Patient data");
		}
		
		data += "Patient ID: " + id;
		data += "\nMR Number: "+ pat.getMrno(); 
		data += "\nFirst Name: " + firstname;
		data += "\nLast Name: " + lastname;
		data += "\nAge: " + age;
		data += "\nGender: " + gender;
		data += "\nWeight: " + weight;
		data += "\nWeight Percentile: " + weightPercentile;
		data += "\nChest Exam: " + chestExam;
		data += "\nLymph Node Exam: " + lymphExam;
		data += "\nAbd. Mass: " + abdMass;
		data += "\nOther Exam: " + otherExam;
		data += "\nOther Exam Details: " + otherExamDetail;
		data += "\nBCG + Scar: " + bcgScar;
		data += "\nCurrent Family History: " + currentHistory;
		data += currentHistoryDetails;
		data += "\nPrevious Family History: " + previousHistory;
		data += previousHistoryDetails;
		  
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);

		Element dataNode = doc.createElement("data");
		Text dataValue = doc.createTextNode(data);
		dataNode.appendChild(dataValue);

		responseNode.appendChild(dataNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;	
	}
	
	///////////////////////////////////////
		
		public String getPaedClinicalDiagInfo(String id) {
			String xml = null;
	
			
			 ssl = new ServerServiceImpl();
			 Person p = null; 
			 Patient pat = null;
			 
			 Date dob = null;
			  
			  try { 
				  p = (Person)ssl.findPerson(id); 
			  } 
			  catch (Exception e2) {
				  e2.printStackTrace(); 
			  }
			  
			  if(p==null) {
				  return XmlUtil.createErrorXml("Could not find Person with id " + id + "Please try again.");
			  }
			  
			  long age = 0;
			  dob = p.getDob();
			  if(dob!=null)
			  {	  age = getAgeInYears(dob);
			  	  System.out.println("AGE:->>>" + age);
			  }
			  
			  else {
				  age = -1;
				  System.out.println("AGE:->>>" + age);
			  }
			  
					
			  if(age >= 15) {
				  return XmlUtil.createErrorXml("Ye patient 15 ya us se ziada umer ka hai. In ke liye Q form bharain");
			  }
			  
			  try {
				  pat = ssl.findPatient(id); 
			  } 
			  catch (Exception e1) {
				  e1.printStackTrace();
				  return XmlUtil.createErrorXml("Could not find Patient with id " + id + ". Please try again.");
			  }
			  
			  if(pat==null) {
				  return XmlUtil.createErrorXml("Could not find Patient with id " + id + "Please try again.");
			  }
			  
			 
			  String firstname = p.getFirstName();
			  String lastname = p.getLastName(); 
			  String gender = "";
			  if(p.getGender()=='M') {
				  gender = "Male";
			  }
			  
			  else {
				  gender = "Female";
			  }
			  
			//get other data here 
			  String weight  = "";
			  if(pat.getWeight()!=null)
			   weight = pat.getWeight().toString();
			  
			//get confirmation encounter
			String rowRecord[] = null;
			
			try {
				rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID", "PID2"}, " where PID1= '" + id + "'AND encounterType='PAED_CONF'");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(rowRecord==null || rowRecord.length==0) {
				return XmlUtil.createErrorXml("No paediatric confirmation found. Please fill in O form first and then try again.");
			}
			
			String encounterId = rowRecord[0];
			int encId = Integer.parseInt(encounterId);
			String pid2 = rowRecord[1];
		
			
			String weightPercentile = null;
			String chestExam = null;
			String lymphExam = null;
			String abdMass = null;
			String otherExam = null;
			String otherExamDetail = null;
			String bcgScar = null;
			String currentHistory = null;
			String previousHistory = null;
			String numCurrentHistory = null;
			String numPreviousHistory = null;
			int numCurr = 0;
			int numPrev = 0;
			
			String currentHistoryDetails = "";
			String previousHistoryDetails = "";
			
			EncounterResultsId eri = null;
			String data = "";
			
			try {
				//get weight percentile	
				eri = new EncounterResultsId(encId,id,pid2,"WEIGHT_PERCENTILE");
				EncounterResults er = null;
				
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					weightPercentile = er.getValue();
				}
				
				//get various exam results
				eri.setElement("CHEST_EXAM");
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					chestExam = er.getValue();
				}
				
				eri.setElement("LYMPH_EXAM");
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					lymphExam = er.getValue();
				}
				
				eri.setElement("ABD_MASS");
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					abdMass = er.getValue();
				}
				
				eri.setElement("OTHER_EXAM");
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					otherExam = er.getValue();
				}
				  
				eri.setElement("OTHER_EXAM_DETAIL");
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					otherExamDetail = er.getValue();
				}
				
				//get bcg result
				eri.setElement("BCG_SCAR");
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					bcgScar = er.getValue();
				}
				
				//get current family history results
				eri.setElement("CURRENT_HISTORY");
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					currentHistory = er.getValue();
				}
				
				eri.setElement("NUM_CURRENT_HISTORY");
				try {
					er = ssl.findEncounterResultsByElement(eri);
				}
				
				catch(ArrayIndexOutOfBoundsException ao) {
					er = null;
				}
				if(er!=null) {
					numCurrentHistory = er.getValue();
					if(numCurrentHistory!=null && numCurrentHistory.length()>0) {
						numCurr = Integer.parseInt(numCurrentHistory);
						currentHistoryDetails += "\nNo. with current TB: " + numCurr;
	
	 *  "current_rel_";
		"current_other_rel_"
		current_tb_form_"
		"current_tb_type_"
		"current_ss_"
	 
						for(int i=0; i<numCurr; i++) {
							currentHistoryDetails += "\nFam. member " + (i+1);
							eri.setElement("CURRENT_REL_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								currentHistoryDetails += "\nRelationship: " + er.getValue();
							}
							
							eri.setElement("CURRENT_OTHER_REL_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								currentHistoryDetails += "\nOther Relationship: " + er.getValue();
							}
							
							eri.setElement("CURRENT_TB_FORM_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								currentHistoryDetails += "\nTB Form: " + er.getValue();
							}
							
							eri.setElement("CURRENT_TB_TYPE_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								currentHistoryDetails += "\nTB Type: " + er.getValue();
							}
							
							eri.setElement("CURRENT_SS_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								currentHistoryDetails += "\nSS +ve: " + er.getValue();
							}
						}
					}
				}
				  
				//get previous family history results
				eri.setElement("PREVIOUS_HISTORY");
				er = ssl.findEncounterResultsByElement(eri);
				if(er!=null) {
					previousHistory = er.getValue();
				}
				
				eri.setElement("NUM_PREVIOUS_HISTORY");
				try {
					er = ssl.findEncounterResultsByElement(eri);
				}
				
				catch(ArrayIndexOutOfBoundsException ao) {
					er = null;
				}
				if(er!=null) {
					numPreviousHistory = er.getValue();
					if(numPreviousHistory!=null && numPreviousHistory.length()>0) {
						numPrev = Integer.parseInt(numPreviousHistory);
						previousHistoryDetails += "\nNo. with previous TB: " + numPrev;
						
						for(int i=0; i<numPrev; i++) {
							previousHistoryDetails += "\nFam. member " + (i+1);
							eri.setElement("PREVIOUS_REL_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								previousHistoryDetails += "\nRelationship: " + er.getValue();
							}
							
							eri.setElement("PREVIOUS_OTHER_REL_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								previousHistoryDetails += "\nOther Relationship: " + er.getValue();
							}
							
							eri.setElement("PREVIOUS_TB_FORM_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								previousHistoryDetails += "\nTB Form: " + er.getValue();
							}
							
							eri.setElement("PREVIOUS_TB_TYPE_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								previousHistoryDetails += "\nTB Type: " + er.getValue();
							}
							
							eri.setElement("PREVIOUS_SS_" + (i+1));
							er = ssl.findEncounterResultsByElement(eri);
							if(er!=null) {
								previousHistoryDetails += "\nSS +ve: " + er.getValue();
							}
						}
					}
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return XmlUtil.createErrorXml("Error retrieving Patient data");
			}
			
			data += "Patient ID: " + id;
			data += "\nMR Number: "+ pat.getMrno(); 
			data += "\nFirst Name: " + firstname;
			data += "\nLast Name: " + lastname;
			data += "\nAge: " + age;
			data += "\nGender: " + gender;
			data += "\nWeight: " + weight;
			data += "\nWeight Percentile: " + weightPercentile;
			data += "\nChest Exam: " + chestExam;
			data += "\nLymph Node Exam: " + lymphExam;
			data += "\nAbd. Mass: " + abdMass;
			data += "\nOther Exam: " + otherExam;
			data += "\nOther Exam Details: " + otherExamDetail;
			data += "\nBCG + Scar: " + bcgScar;
			data += "\nCurrent Family History: " + currentHistory;
			data += currentHistoryDetails;
			data += "\nPrevious Family History: " + previousHistory;
			data += previousHistoryDetails;
			  
			Document doc = null;
	
			try {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.newDocument();
			} catch (ParserConfigurationException e) {
				//  Auto-generated catch block
				e.printStackTrace();
				return "";
			}
			
			Element responseNode = doc.createElement(XmlStrings.RESPONSE);
	
			Element dataNode = doc.createElement("data");
			Text dataValue = doc.createTextNode(data);
			dataNode.appendChild(dataValue);
	
			responseNode.appendChild(dataNode);
	
			doc.appendChild(responseNode);
	
			xml = XmlUtil.docToString(doc);
	
			return xml;	
		}

	private String getPatientFollowupEffortInfo(String id){
		String xml = null;
		
		 Patient pat = null;
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Error finding Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + " Recheck and try again.");
		  }
		  
		  if(pat.getPatientStatus().equals("SUSPENDED")) {
			  return XmlUtil.createErrorXml("Is Patient ka No Active Follow-up Effort form bhar diya gaya hai.");
		  }

		 String mrNum = pat.getMrno() == null ? "Not available" : pat.getMrno();
		 String gpid = pat.getGpid() == null ? "Not available" : pat.getGpid();
		 
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element mrNode = doc.createElement("mr");
		Text mrValue = doc.createTextNode(mrNum);
		mrNode.appendChild(mrValue);

		responseNode.appendChild(mrNode);

		Element gpidNode = doc.createElement("gpid");
		Text gpidValue = doc.createTextNode(gpid);
		gpidNode.appendChild(gpidValue);

		responseNode.appendChild(gpidNode);
		
		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}

	private String doPaedClinicalVisit(){
		String xml = null;
		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid");
		String weight = request.getParameter("wt");
		String height = request.getParameter("ht");
		String month = request.getParameter("month");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		 Patient pat = null;
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Error finding Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + " Recheck id and try again.");
		  }
		  
		  try{
			pat.setWeight(Float.parseFloat(weight));
		  	pat.setHeight(Float.parseFloat(height));
		  }
		  catch (Exception e) {
			  System.out.println(e.getMessage());
			  return XmlUtil.createErrorXml("Could not set height or weight for Patient with id " + id + ". Invalid parameters.");
		  }
		  
		  try {
			  if(!ssl.updatePatient(pat)){
				  return XmlUtil.createErrorXml("Could not update Patient. Try again");
			  }
		} catch (Exception e2) {
			e2.printStackTrace();
			  return XmlUtil.createErrorXml("Error while updating Patient. Try again");

		}
		
		EncounterId encId = new EncounterId();
		encId.setEncounterId(-1);
		encId.setPid1(id.toUpperCase());
		encId.setPid2(chwId.toUpperCase());

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("PD_CLIVIS");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Could not create Encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate.toUpperCase());
		encounters.add(dateResult);

		EncounterResults pStatusResult = ModelUtil.createEncounterResult(e,
				"weight".toUpperCase(), weight.toUpperCase());
		encounters.add(pStatusResult);
		
		EncounterResults effortreasonResult = ModelUtil.createEncounterResult(e,
				"height".toUpperCase(), height.toUpperCase());
		encounters.add(effortreasonResult);
		
		EncounterResults monthResult = ModelUtil.createEncounterResult(e,
				"month".toUpperCase(), (month == null ? "" : month.toUpperCase()) );
		encounters.add(monthResult);

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}
		}
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	private String doClinicalVisit(){
		String xml = null;
		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid");
		String gpid = request.getParameter("gpid");
		String weight = request.getParameter("wt");
		String height = request.getParameter("ht");

		String month = request.getParameter("mnt");
		String regimenChange = request.getParameter("regchg");
		String phase = request.getParameter("phs");
		String regimen = request.getParameter("reg");
		String fdcTablet = request.getParameter("fdc");
		String strepto = request.getParameter("strp");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		if(month==null || month.length()==0) {
			return XmlUtil.createErrorXml("Phone update karain aur dobara koshish karain");
		}
		
		 Patient pat = null;
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Error finding Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + " Recheck id and try again.");
		  }
		  
		  try{
			pat.setWeight(Float.parseFloat(weight));
		  	pat.setHeight(Float.parseFloat(height));
		  }
		  catch (Exception e) {
			  return XmlUtil.createErrorXml("Could not set height or weight for Patient with id " + id + ". Invalid parameters.");
		  }
		  
		  
		  if(regimenChange.toLowerCase().equals("yes"))
		  {
			  pat.setRegimen(regimen);
			  pat.setTreatmentPhase(phase.toUpperCase());
		  
			  try{
				  pat.setDoseCombination(Float.parseFloat(fdcTablet));
			  }
			  catch (Exception e) {
				  e.printStackTrace();
			  }
			  pat.setOtherDoseDescription(strepto);
		  }
		  
		  else {
			  regimen = null;
			  fdcTablet = null;
			  strepto = null;
			  phase = null;
		  }
		  
		  try {
			  if(!ssl.updatePatient(pat)){
				  return XmlUtil.createErrorXml("Could not update Patient. Try again");
			  }
		} catch (Exception e2) {
			e2.printStackTrace();
			  return XmlUtil.createErrorXml("Error while updating Patient. Try again");
		}
		
		EncounterId encId = new EncounterId();
		encId.setEncounterId(-1);
		encId.setPid1(id.toUpperCase());
		encId.setPid2(chwId.toUpperCase());

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("AD_CLIVIS");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Could not create Encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate.toUpperCase());
		encounters.add(dateResult);
		
		EncounterResults gpidResult = ModelUtil.createEncounterResult(e,
				"gp_id".toUpperCase(), gpid.toUpperCase());
		encounters.add(gpidResult);

		EncounterResults pStatusResult = ModelUtil.createEncounterResult(e,
				"weight".toUpperCase(), weight.toUpperCase());
		encounters.add(pStatusResult);
		
		EncounterResults effortreasonResult = ModelUtil.createEncounterResult(e,
				"height".toUpperCase(), height.toUpperCase());
		encounters.add(effortreasonResult);
		
		EncounterResults monthResult = ModelUtil.createEncounterResult(e,
				"month".toUpperCase(), (month == null ? "" : month.toUpperCase()) );
		encounters.add(monthResult);
		
		EncounterResults phaseResult = ModelUtil.createEncounterResult(e,
				"phase".toUpperCase(), (phase == null ? "" : phase.toUpperCase()) );
		encounters.add(phaseResult);
		
		EncounterResults regimenChangeResult = ModelUtil.createEncounterResult(e,
				"regimen_change".toUpperCase(), (regimenChange == null ? "" : regimenChange.toUpperCase()) );
		encounters.add(regimenChangeResult);
		
		EncounterResults regimenResult = ModelUtil.createEncounterResult(e,
				"regimen".toUpperCase(), (regimen == null ? "" : regimen.toUpperCase()) );
		encounters.add(regimenResult);
		
		EncounterResults fdcTabletResult = ModelUtil.createEncounterResult(e,
				"fdc_tablet".toUpperCase(), (fdcTablet == null ? "" : fdcTablet.toUpperCase()) );
		encounters.add(fdcTabletResult);
		
		EncounterResults streptoResult = ModelUtil.createEncounterResult(e,
				"streptomycin".toUpperCase(), (strepto == null ? "" : strepto.toUpperCase()) );
		encounters.add(streptoResult);

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}
		}
		
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	private String getPaedClinicalVisitInfo(String id) {
		String xml = null;
		int month = -1;
		 ssl = new ServerServiceImpl();
		 Patient pat = null;
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Error finding Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + " Recheck and try again.");
		  }

		 String mrNum = pat.getMrno() == null ? "Not available" : pat.getMrno();
		 String gpid = pat.getGpid() == null ? "Not available" : pat.getGpid();
		 String cat = pat.getDiseaseCategory() == null ? "Not available" : pat.getDiseaseCategory();
		 String type = pat.getPatientType() == null ? "Not available" : pat.getPatientType();

		 String [] rowRecord = null;
			
		 String treatmentStartDate = "";
			  try { 
				  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
			  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
			  
			  rowRecord = ssl.getRowRecord("Encounter", new String[]{"DateEncounterEntered","PID2"}, " where PID1= '" + id + "'AND EncounterType='BASELINE'");
				}	
			  catch (Exception e1) 
			  { 
				  e1.printStackTrace(); 
			  }
			  
			  if(rowRecord==null || rowRecord.length!=2) {
				 treatmentStartDate = "N/A"; 
				 if(rowRecord==null)
					 System.out.println("null");
				 else
					 System.out.println(rowRecord.length);
			  }
			  
			  else { 
				   
				  treatmentStartDate = rowRecord[0];
			  } 
			  
				
				
				 String monthOfTrt = "";
				  try { 
					  if(treatmentStartDate==null || treatmentStartDate.equals("N/A")) {
						  return XmlUtil.createErrorXml("Is patient ka ilaaj shuroo nahin huwa.");
					  }
					  
					  //else month calculation
					  else {
						  month = DateTimeUtil.calculateMonthOfTreatment(treatmentStartDate);
					  }
					monthOfTrt = "" + month;
				  
				  
					}	
				  catch (Exception e1) 
				  { 
					  e1.printStackTrace(); 
				  }
				  
				  if(cat!=null && cat.length()!=0 && !cat.equals(" ") &&  treatmentStartDate!=null && treatmentStartDate.length()!=0 && !treatmentStartDate.equals("N/A")) {
					  boolean old = false;
					try {
						old = isOldRegimen(treatmentStartDate);
						if(old) {
							  cat += XmlStrings.OLD;
						  }
						  
						  else
							  cat += XmlStrings.NEW;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					  
				  }		 
				  
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element mrNode = doc.createElement("mr");
		Text mrValue = doc.createTextNode(mrNum);
		mrNode.appendChild(mrValue);

		responseNode.appendChild(mrNode);

		Element gpidNode = doc.createElement("gpid");
		Text gpidValue = doc.createTextNode(gpid);
		gpidNode.appendChild(gpidValue);

		responseNode.appendChild(gpidNode);

		Element catNode = doc.createElement("cat");
		Text catValue = doc.createTextNode(cat);
		catNode.appendChild(catValue);

		responseNode.appendChild(catNode);
		
		Element typeNode = doc.createElement("type");
		Text typeValue = doc.createTextNode(type);
		typeNode.appendChild(typeValue);

		responseNode.appendChild(typeNode);
		
		Element treatmentStartDateNode = doc.createElement("tx_date");
		Text treatmentStartDateValue = doc.createTextNode(treatmentStartDate);
		treatmentStartDateNode.appendChild(treatmentStartDateValue);

		responseNode.appendChild(treatmentStartDateNode);
		
		Element monthOftrtNode = doc.createElement("month");
		Text monthOftrtValue = doc.createTextNode(monthOfTrt);
		monthOftrtNode.appendChild(monthOftrtValue);

		responseNode.appendChild(monthOftrtNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}
	
	private String getClinicalVisitInfo(String id) {
		String xml = null;
		
		 Patient pat = null;
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Error finding Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + " Recheck and try again.");
		  }

		 String mrNum = pat.getMrno() == null ? "Not available" : pat.getMrno();
		 String gpid = pat.getGpid() == null ? "Not available" : pat.getGpid();
		 String cat = pat.getDiseaseCategory() == null ? "Not available" : pat.getDiseaseCategory();
		 String type = pat.getPatientType() == null ? "Not available" : pat.getPatientType();
		 String phase = pat.getTreatmentPhase() == null ? "N/A" : pat.getTreatmentPhase();
		 String reg = pat.getRegimen() == null ? "N/A" : pat.getRegimen();
		 String fdc = "N/A";
		 //int month = 0;
		 try{
			 fdc = pat.getDoseCombination() == null ? "N/A" : Float.toString(pat.getDoseCombination());
		 }
		 catch (Exception e) {
			e.printStackTrace();
		 }
		 String strep = pat.getOtherDoseDescription() == null ? "N/A" : pat.getOtherDoseDescription();
		 
		 String [] rowRecord = null;
			
		 String treatmentStartDate = "";
			  try { 
				  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
			  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
			  
			  rowRecord = ssl.getRowRecord("Encounter", new String[]{"DateEncounterEntered","PID2"}, " where PID1= '" + id + "'AND EncounterType='BASELINE'");
				}	
			  catch (Exception e1) 
			  { 
				  e1.printStackTrace(); 
			  }
			  
			  if(rowRecord==null || rowRecord.length!=2) {
				 treatmentStartDate = "N/A"; 
				 if(rowRecord==null)
					 System.out.println("null");
				 else
					 System.out.println(rowRecord.length);
			  }
			  
			  else { 
				   
				  treatmentStartDate = rowRecord[0];
			  } 
			  
			  if(treatmentStartDate==null || treatmentStartDate.equals("N/A")) {
				  return XmlUtil.createErrorXml("Is patient ka ilaaj shuroo nahin huwa.");
			  }
			  
			  if(cat!=null && cat.length()!=0 && !cat.equals(" ") &&  treatmentStartDate!=null && treatmentStartDate.length()!=0 && !treatmentStartDate.equals("N/A")) {
				  boolean old = false;
				try {
					old = isOldRegimen(treatmentStartDate);
					if(old) {
						  cat += XmlStrings.OLD;
					  }
					  
					  else
						  cat += XmlStrings.NEW;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
			  }		 
			  //else month calculation
			  else {
				  month = DateTimeUtil.calculateMonthOfTreatment(treatmentStartDate);
			  }
			  
			  
				//get base sputum results, gxp results, gxp resistance, chest x-ray
				//TODO: Modify for three tables
				String[] sputumResults = null;
				int numResults = -1;
				try {
					sputumResults = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=0 AND SmearResult IS NOT NULL AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
				} catch (Exception e1) {
					//  Auto-generated catch block
					e1.printStackTrace();
				} 
				
				if(sputumResults==null) {
					return XmlUtil.createErrorXml("Could not find base Sputum Results for patient with id " + id + " Please try again.");
				}
				
				String bsmear = " ";
				String bgxp = " ";
				String gxpres = " ";
				String bcxr = " ";
				
				if(sputumResults!=null) {
					numResults = sputumResults.length;
					
					for(int i=0; i<numResults; i++) {
						bsmear += sputumResults[i] + " ";
					}
				}
				
				//get base Gxp results
				
				String[][] gxpResults=null;
				try {
					gxpResults = ssl.getTableData("GeneXpertResults", new String[] {"IRS","GeneXpertResult", "DrugResistance"}, " where PatientID='" + id + "' AND Remarks NOT LIKE '%REJECTED%' AND DateTested IS NOT NULL ORDER BY DateTested ASC");
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				if(gxpResults!=null) {
					for(int i=0; i<gxpResults.length; i++) {
						if(gxpResults[i][0]!=null && !gxpResults[i][0].equals("0")) {
							
								bgxp += gxpResults[i][1] + " ";
								gxpres += gxpResults[i][2] + " ";
							else if(gxpResults[i][0].equals("true"))
								baseGxp += "Positive ";
						}
						else
						{
							bgxp += "N/A" + " ";
							gxpres += "N/A" + " ";
						}
						
						if(gxpResults[i][1]!=null)
							
						else
							
					}
				}
				
				String[] xRayResult = null;
				
				try {
					xRayResult = ssl.getColumnData("XRayResults", "XRayResults", " where PatientId='"+ id + "' AND DateReported IS NOT NULL ORDER BY DateReported ASC");
				
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				if(xRayResult!=null) {
					for(int i=0; i<xRayResult.length; i++) {
						if(xRayResult[i]!=null)
							bcxr += xRayResult[i] + " ";
					}
				}
				
				//follow up smear 2
				String[] sputum2Results = null;
				int numSmear2Results = -1;
				try {
					sputum2Results = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=2 AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				
				String smear2 = " ";
				
				if(sputum2Results!=null) {
					numSmear2Results = sputum2Results.length;
					
					for(int i=0; i<numSmear2Results; i++) {
						smear2 += sputum2Results[i] + " ";
					}
				}
				
				//follow up smear 3
				String[] sputum3Results = null;
				int numSmear3Results = -1;
				try {
					sputum3Results = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=3 AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				
				String smear3 = " ";
				
				if(sputum3Results!=null) {
					numSmear3Results = sputum3Results.length;
					
					for(int i=0; i<numSmear3Results; i++) {
						smear3 += sputum3Results[i] + " ";
					}
				}
				
				//follow up smear 5
				String[] sputum5Results = null;
				int numSmear5Results = -1;
				try {
					sputum5Results = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=5 AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				
				String smear5 = " ";
				
				if(sputum5Results!=null) {
					numSmear5Results = sputum5Results.length;
					
					for(int i=0; i<numSmear5Results; i++) {
						smear5 += sputum5Results[i] + " ";
					}
				}
				
				//follow up smear 7
				String[] sputum7Results = null;
				int numSmear7Results = -1;
				try {
					sputum7Results = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=7 AND DateSubmitted IS NOT NULL  AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				
				String smear7 = " ";
				
				if(sputum7Results!=null) {
					numSmear7Results = sputum7Results.length;
					
					for(int i=0; i<numSmear7Results; i++) {
						smear7 += sputum7Results[i] + " ";
					}
				}
				
				// String monthOftrt = "" + month;
				  try { 
					  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
				  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
				  
				  rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID", "PID2"}, " where PID1= '" + id + "'AND EncounterType='FOLLOW_UP' ORDER BY DateEncounterStart DESC");
					}	
				  catch (Exception e1) 
				  { 
					  e1.printStackTrace(); 
				  }
				  
				  if(rowRecord==null || rowRecord.length!=2) {
					  monthOftrt = "N/A"; 
				  }
				  else { 
					   
					  System.out.println(rowRecord[0]);
					  System.out.print(rowRecord[1]);
					  int encId = Integer.parseInt(rowRecord[0]); 
					  String pid2 = rowRecord[1]; 
					  EncounterResultsId eri = new EncounterResultsId(encId,id,pid2,"month"); 
					  EncounterResults er = null;
					  try { 
						  er = ssl.findEncounterResultsByElement(eri); 
					  } catch (Exception e) {
						  e.printStackTrace(); 
					  } 
					  
					  if(er!=null) { 
						  monthOftrt = er.getValue(); 
					  }
				  
					  else { 
						  monthOftrt = "N/A"; 
					  } 
				  } 
				  
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element mrNode = doc.createElement("mr");
		Text mrValue = doc.createTextNode(mrNum);
		mrNode.appendChild(mrValue);

		responseNode.appendChild(mrNode);

		Element gpidNode = doc.createElement("gpid");
		Text gpidValue = doc.createTextNode(gpid);
		gpidNode.appendChild(gpidValue);

		responseNode.appendChild(gpidNode);

		Element catNode = doc.createElement("cat");
		Text catValue = doc.createTextNode(cat);
		catNode.appendChild(catValue);

		responseNode.appendChild(catNode);
		
		Element typeNode = doc.createElement("type");
		Text typeValue = doc.createTextNode(type);
		typeNode.appendChild(typeValue);

		responseNode.appendChild(typeNode);
		
		Element treatmentStartDateNode = doc.createElement("tx_date");
		Text treatmentStartDateValue = doc.createTextNode(treatmentStartDate);
		treatmentStartDateNode.appendChild(treatmentStartDateValue);

		responseNode.appendChild(treatmentStartDateNode);
		
		Element treatmentMonthNode = doc.createElement("tx_month");
		Text treatmentMonthValue = doc.createTextNode(monthOftrt);
		treatmentMonthNode.appendChild(treatmentMonthValue);

		responseNode.appendChild(treatmentMonthNode);
		
		Element phaseOftrtNode = doc.createElement("phase");
		Text phaseOftrtValue = doc.createTextNode(phase);
		phaseOftrtNode.appendChild(phaseOftrtValue);

		responseNode.appendChild(phaseOftrtNode);

		Element regNode = doc.createElement("reg");
		Text regValue = doc.createTextNode(reg);
		regNode.appendChild(regValue);

		responseNode.appendChild(regNode);
		
		Element fdcNode = doc.createElement("fdc");
		Text fdcValue = doc.createTextNode(fdc);
		fdcNode.appendChild(fdcValue);

		responseNode.appendChild(fdcNode);
		
		Element strepNode = doc.createElement("strep");
		Text strepValue = doc.createTextNode(strep);
		strepNode.appendChild(strepValue);

		responseNode.appendChild(strepNode);
		
		Element bcxrNode = doc.createElement("bcxr");
		Text bcxrValue = doc.createTextNode(bcxr);
		bcxrNode.appendChild(bcxrValue);

		responseNode.appendChild(bcxrNode);
		
		Element bgxpNode = doc.createElement("bgxp");
		Text bgxpValue = doc.createTextNode(bgxp);
		bgxpNode.appendChild(bgxpValue);

		responseNode.appendChild(bgxpNode);
		
		Element gxpresNode = doc.createElement("gxpres");
		Text gxpresValue = doc.createTextNode(gxpres);
		gxpresNode.appendChild(gxpresValue);

		responseNode.appendChild(gxpresNode);
		
		Element bsmearNode = doc.createElement("bsmear");
		Text bsmearValue = doc.createTextNode(bsmear);
		bsmearNode.appendChild(bsmearValue);

		responseNode.appendChild(bsmearNode);
		
		Element smear23Node = doc.createElement("smear23");
		Text smear23Value = doc.createTextNode(smear2+" : "+smear3);
		smear23Node.appendChild(smear23Value);

		responseNode.appendChild(smear23Node);
		
		Element smear5Node = doc.createElement("smear5");
		Text smear5Value = doc.createTextNode(smear5);
		smear5Node.appendChild(smear5Value);

		responseNode.appendChild(smear5Node);
		
		Element smear7Node = doc.createElement("smear7");
		Text smear7Value = doc.createTextNode(smear7);
		smear7Node.appendChild(smear7Value);

		responseNode.appendChild(smear7Node);
		
		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
System.out.println("CLINICAL VISIT XML: " + xml);
		return xml;
	}
	
	public Date getDOBFromAge(int age) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(System.currentTimeMillis()));
		c.set(Calendar.YEAR, c.get(Calendar.YEAR)-age);
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.DATE, 30);
		return c.getTime();
		
	}
	
	// ******************************************
	// doSputumCollection*******************************/
	/*private String doContactSputumCollection() {
		String xml = null;

		String id = request.getParameter("id");

		String mid = request.getParameter("mid");

		String patientStatus = request.getParameter("ps");
		String mdrContact = request.getParameter("mdrc");
		String sputumMonth = request.getParameter("cm");
		String sampleNumber = request.getParameter("ws");
		String sputumCollected = request.getParameter("sc");
		String barCode = request.getParameter("sbc");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		if(barCode!=null && barCode.length()!=0) {
			Boolean exists = null;
		
			try {
				exists = ssl.exists("SputumResults", " where SputumTestID='" + barCode + "'");
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			if(exists==null) {
				System.out.println("null");
				return XmlUtil.createErrorXml("Error tracking Bar Code Number. Please try again!");
			}
			
			else if(exists.booleanValue()==true) {
				System.out.println("true");
				return XmlUtil.createErrorXml("This Bar Code has already been collected. Please recheck Bar Code and try again");
			}
		
			//if not found in SputumResults check in ContactSputumResults
			if(exists.booleanValue()==false) {
				try {
					exists = ssl.exists("ContactSputumResults", " where SputumTestID='" + barCode + "'");
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			
				if(exists==null) {
					System.out.println("null");
					return XmlUtil.createErrorXml("Error tracking Bar Code Number. Please try again!");
				}
			
				else if(exists.booleanValue()==true) {
					System.out.println("true");
					return XmlUtil.createErrorXml("This Bar Code has already been collected. Please recheck Bar Code and try again");
				}	
			}
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(mid);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("CS_COLL");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Error occurred. Please try again");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		EncounterResults patientStatusResult = ModelUtil.createEncounterResult(
				e, "patient_status".toUpperCase(), patientStatus.toUpperCase());
		encounters.add(patientStatusResult);

		EncounterResults mdrContactResult = ModelUtil.createEncounterResult(
				e, "mdr_contact".toUpperCase(), mdrContact.toUpperCase());
		encounters.add(mdrContactResult);
		
		EncounterResults collectionMonthResult = ModelUtil
				.createEncounterResult(e, "collection_month".toUpperCase(),
						sputumMonth.toUpperCase());
		encounters.add(collectionMonthResult);

		EncounterResults suspectSampleNumberResult = ModelUtil
				.createEncounterResult(e, "suspect_sample".toUpperCase(),
						sampleNumber.toUpperCase());
		encounters.add(suspectSampleNumberResult);

		EncounterResults sputumCollectedResult = ModelUtil
				.createEncounterResult(e, "sputum_collected".toUpperCase(),
						sputumCollected.toUpperCase());
		encounters.add(sputumCollectedResult);

		if (barCode != null) {
			EncounterResults barcodeResult = ModelUtil.createEncounterResult(e,
					"sample_barcode".toUpperCase(), barCode.toUpperCase());
			encounters.add(barcodeResult);
		}

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("Error occurred. Please try again");
			}

		}

		// //TODO: Modify for three tables:
		//If baseline: Add to SputumResults, GXPert Results, Chest Xray
		//Else: Just add to SputumResults
		if(barCode!=null && barCode.length()!=0) {
			//SputumResultsId sri1 = new SputumResultsId();
			int month = -1;
			
			if(sputumMonth.equals("Baseline")) {
				month = 0;
			}
			
			else if(sputumMonth.equals("2nd")) {
				month = 2;
			}
			
			else if(sputumMonth.equals("3rd")) {
				month = 3;
			}
			
			else if(sputumMonth.equals("5th")) {
				month = 5;
			}
			
			else if(sputumMonth.equals("7th")) {
				month = 7;
			}
			
		
			
			//sri1.setPatientId(id);
			//sri1.setSputumTestId(Integer.parseInt(barCode));
			ContactSputumResults csr = new ContactSputumResults();
			csr.setSputumTestId(Integer.parseInt(barCode));
			csr.setPatientId(id);
			csr.setMonth(month);
			csr.setIrs(0);
			
			//confirm this with Owais
			if(mdrContact.equalsIgnoreCase("Yes")) {
				csr.setIsTestPending(new Boolean(true));
			}
			
			else {
				csr.setIsTestPending(new Boolean(false));
			}
			//sr.setDateSubmitted(encounterStartDate);
			
			try {
				ssl.saveContactSputumResults(csr);
			} catch (Exception e1) {
				e1.printStackTrace();
				return XmlUtil.createErrorXml("Error saving Contact Sputum Results. Please try again");
			}
			
			//if(month==0) {
				
				//GXP
				GeneXpertResults gxp = new GeneXpertResults();
				gxp.setIsPositive(new Boolean(false));
				gxp.setPatientId(id);
				gxp.setSputumTestId(Integer.parseInt(barCode));
				gxp.setIrs(0);
				
				try {
					ssl.saveGeneXpertResults(gxp);
				} catch (Exception e1) {
					e1.printStackTrace();
					return XmlUtil.createErrorXml("Error saving GeneXpert Results. Please try again");
				}
				
				
			
			//}
		}
		

		xml = XmlUtil.createSuccessXml();
		return xml;
	}
	
	// ******************************************
	// getBaselineTreatmentInfo*******************************/

	/*public String getCDFInfo(String id) {
		String xml = null;

		Patient pat = null;
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			
		}

		if (pat == null) {
			return XmlUtil
					.createErrorXml("Patient does not exist. Please confirm ID and try again");
		}
		
		String[] er = null;
		try {
			er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='AGE'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String age = null;
		int ageInt = 0;
		if(er!=null && er.length > 0)
			age = er[0];
		
		else {
			XmlUtil
			.createErrorXml("Could not find patient age. Please try again");
		}
		
		if(age!=null)
			ageInt = Integer.parseInt(age);
		
		else {
			
		}
		
		if (ageInt < 15) {
			return XmlUtil.createErrorXml("Ye patient 15 saal se kam umer ka hai. Patient ko ilaaj ke liye Indus Hospital refer karwaiye");
		}
		
		//get base sputum results, gxp results, gxp resistance, chest x-ray
		//
		String[] sputumResults = null;
		int numResults = -1;
		try {
			sputumResults = ssl.getColumnData("SputumResults", "SmearResult", " where PatientID='" + id + "' AND Month=0 AND SmearResult IS NOT NULL AND DateSubmitted IS NOT NULL AND (Remarks IS NULL OR Remarks NOT LIKE '%REJECTED%') ORDER BY DateSubmitted ASC");
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} 
		
		if(sputumResults==null) {
			return XmlUtil.createErrorXml("Could not find base Sputum Results for patient with id " + id + " Please try again.");
		}
		
		String baseSmear = " ";
		String baseGxp = " ";
		String baseGxpRes = " ";
		String cxr = " ";
		
		if(sputumResults!=null) {
			numResults = sputumResults.length;
			
			for(int i=0; i<numResults; i++) {
				baseSmear += sputumResults[i] + " ";
					
			}
		}
		
		//get base Gxp results
		
		String[][] gxpResults=null;
		try {
			gxpResults = ssl.getTableData("GeneXpertResults", new String[] {"IRS","GeneXpertResult", "DrugResistance"}, " where PatientID='" + id + "' AND Remarks NOT LIKE '%REJECTED%' AND DateTested IS NOT NULL ORDER BY DateTested ASC");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		if(gxpResults!=null) {
			for(int i=0; i<gxpResults.length; i++) {
				if(gxpResults[i][0]!=null && !gxpResults[i][0].equals("0")) {
					
						baseGxp += gxpResults[i][1] + " ";
						baseGxpRes += gxpResults[i][2] + " ";
					else if(gxpResults[i][0].equals("true"))
						baseGxp += "Positive ";
				}
				else
				{
					baseGxp += "N/A" + " ";
					baseGxpRes += "N/A" + " ";
				}
				
				if(gxpResults[i][1]!=null)
					
				else
					
			}
		}
		
		String[] xRayResult = null;
		
		try {
			xRayResult = ssl.getColumnData("XRayResults", "XRayResults", " where PatientId='"+ id + "' AND DateReported IS NOT NULL ORDER BY DateReported ASC");
		
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(xRayResult!=null) {
			for(int i=0; i<xRayResult.length; i++) {
				if(xRayResult[i]!=null)
					cxr += xRayResult[i] + " ";
			}
		}
		
		System.out.println("CXR:" + cxr);
		
		er = null;
		try {
			er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY_D1'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String tbhist;
		if(er!=null && er.length > 0)
			tbhist = er[0];
		else {
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(er!=null && er.length>0)
				tbhist = er[0];
			else
				tbhist = "Not Entered";
		}
		
		
		
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element smearNode = doc.createElement("basesmear");
		Text smearValue = doc.createTextNode(baseSmear);
		smearNode.appendChild(smearValue);

		responseNode.appendChild(smearNode);

		Element gxpNode = doc.createElement("basegxp");
		Text gxpValue = doc.createTextNode(baseGxp);
		gxpNode.appendChild(gxpValue);

		responseNode.appendChild(gxpNode);

		Element dstNode = doc.createElement("basedst");
		Text dstValue = doc.createTextNode(baseGxpRes);
		dstNode.appendChild(dstValue);

		responseNode.appendChild(dstNode);

		Element cxrNode = doc.createElement("cxr");
		Text cxrValue = doc.createTextNode(cxr.trim());
		cxrNode.appendChild(cxrValue);

		responseNode.appendChild(cxrNode);
		
		Element tbHistNode = doc.createElement("tbhist");
		Text tbHistValue = doc.createTextNode(tbhist);
		tbHistNode.appendChild(tbHistValue);

		responseNode.appendChild(tbHistNode);

		
		 * Element typeNode = doc.createElement("type"); Text typeValue =
		 * doc.createTextNode("New"); typeNode.appendChild(typeValue);
		 * 
		 * responseNode.appendChild(typeNode);
		 * 
		 * Element catNode = doc.createElement("cat"); Text catValue =
		 * doc.createTextNode("2"); catNode.appendChild(catValue);
		 * 
		 * responseNode.appendChild(catNode);
		 

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}
	
	// ******************************************
	// doDrugAdm*******************************/
	/*private String doDrugDispensation() {
		String xml = null;

		String id = request.getParameter("id");

		String chwId = request.getParameter("chwid");

		String delivered = request.getParameter("ddeliv");
		String notDeliveredReason = request.getParameter("rsn");
		String otherReason = request.getParameter("otrrsn");
		String privatePurchasing = request.getParameter("priv");
		String deliveredTo = request.getParameter("who");
		String numPillsStr = request.getParameter("pillnum");
		String numPillDaysStr = request.getParameter("pilltime");
		String numStrepStr = request.getParameter("strepnum");
		String numStrepDaysStr = request.getParameter("streptime");
		String numSyrupStr = request.getParameter("syrnum");
		String numSyrupDaysStr = request.getParameter("syrtime");
		
		delivered = delivered.toUpperCase();
		notDeliveredReason = notDeliveredReason == null ? "" : notDeliveredReason.toUpperCase();
		otherReason = otherReason == null ? "" : otherReason.toUpperCase();
		privatePurchasing = privatePurchasing == null ? "" : privatePurchasing.toUpperCase();
		deliveredTo = deliveredTo == null ? "" : deliveredTo.toUpperCase();

		int numPills = (numPillsStr == null || numPillsStr.length()==0) ? 0 : Integer.parseInt(numPillsStr);
		int numPillsDays = (numPillDaysStr == null || numPillDaysStr.length() ==0 ) ? 0 : Integer.parseInt(numPillDaysStr);
		int numStrep = (numStrepStr == null || numStrepStr.length()==0) ? 0 : Integer.parseInt(numStrepStr);
		int numStrepDays = (numStrepDaysStr == null || numStrepDaysStr.length()==0) ? 0 : Integer.parseInt(numStrepDaysStr);
		int numSyrup = (numSyrupStr == null || numSyrupStr.length()==0) ? 0 : Integer.parseInt(numSyrupStr);
		int numSyrupDays = (numSyrupDaysStr == null || numSyrupDaysStr.length()==0)? 0 : Integer.parseInt(numSyrupDaysStr);
		
		
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");

		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}

		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		
		if(delivered.equals("YES")) {
			notDeliveredReason = "";
			otherReason = "";
			privatePurchasing = "";
		DrugHistory dh = new DrugHistory();
		dh.setId(new DrugHistoryId());
		dh.getId().setPatientId(id);
		
		try {
			dh.setDateDispensed(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch (ParseException e) {
			return XmlUtil.createErrorXml("Bad Entered Date!");
			
		}
		dh.setDrugsDeliveredTo(deliveredTo);
		dh.setPeriodType("DAYS");
		dh.setPillsDelivered(numPills);
		dh.setPillsQuotaDelivered(numPillsDays);
		dh.setStreptomycinDelivered(numStrep);
		dh.setStreptomycinQuotaDelivered(numStrepDays);
		dh.setSyrupDelivered(numSyrup);
		dh.setSyrupQuotaDelivered(numSyrupDays);
	
		try {
			ssl.saveDrugHistory(dh);
		}
		
		catch (Exception e) {
			e.printStackTrace();
			return XmlUtil.createErrorXml("Error saving Drug Dispensation. Please try again.");
		}
		}
		
		else {
			
			deliveredTo = "";
			
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(chwId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("DRUG_DISP");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Could not create Encounter. Please try again.");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		EncounterResults deliveredResult = ModelUtil.createEncounterResult(e,
				"delivered".toUpperCase(), delivered);
		encounters.add(deliveredResult);

		EncounterResults reasonResult = ModelUtil.createEncounterResult(e,
				"reason".toUpperCase(), notDeliveredReason);
		encounters.add(reasonResult);

		EncounterResults otherReasonResult = ModelUtil.createEncounterResult(e,
				"other_reason".toUpperCase(),otherReason);
		encounters.add(otherReasonResult);
		
		EncounterResults deliveredToResult = ModelUtil.createEncounterResult(e,
				"delivered_to".toUpperCase(),deliveredTo);
		encounters.add(deliveredToResult);
		
		EncounterResults patientPrivateResult = ModelUtil.createEncounterResult(e,
				"private_drugs".toUpperCase(),privatePurchasing);
		encounters.add(patientPrivateResult);
		
		EncounterResults numPillsResult = ModelUtil.createEncounterResult(e,
				"num_pills".toUpperCase(),"" + numPills);
		encounters.add(numPillsResult);
		
		EncounterResults numPillsQuotaResult = ModelUtil.createEncounterResult(e,
				"num_pills_quota".toUpperCase(),"" + numPillsDays);
		encounters.add(numPillsQuotaResult);
		
		EncounterResults numStrepResult = ModelUtil.createEncounterResult(e,
				"num_strep".toUpperCase(),"" + numStrep);
		encounters.add(numStrepResult);
		
		EncounterResults numStrepQuotaResult = ModelUtil.createEncounterResult(e,
				"num_strep_quota".toUpperCase(),"" + numStrepDays);
		encounters.add(numStrepQuotaResult);
		
		EncounterResults numSyrupResult = ModelUtil.createEncounterResult(e,
				"num_syrup".toUpperCase(),"" + numSyrup);
		encounters.add(numSyrupResult);
		
		EncounterResults numSyrupQuotaResult = ModelUtil.createEncounterResult(e,
				"num_syrup_quota".toUpperCase(),"" + numSyrupDays);
		encounters.add(numSyrupQuotaResult);
	
		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR! Please try again");
			}

		}
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}*/
	
	// ******************************************
	// getDrugDispensationInfo*******************************/

	/*public String getDrugDispenationInfo(String id) {
		String xml = null;

		ssl = new ServerServiceImpl();
		
		Patient pat = null;
		
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(pat==null) {
			return XmlUtil.createErrorXml("Could not find Patient with id " + id + " Please try again.");
		}
		
		String phase = pat.getTreatmentPhase();
		
		if(phase==null || phase.length()==0) {
			return XmlUtil.createErrorXml("Is patient ka I form nahin bhara gaya. Pehlay I form bharain aur phir koshish karain");
		}
		
		String regimen = pat.getRegimen();
		Float fixedDose = pat.getDoseCombination();
		
		String category = pat.getDiseaseCategory();
		
		String regForm = pat.getMedicationForm();
		if(regForm==null)
			 regForm = " ";
		String mr = pat.getMrno();
		if(mr==null) {
			mr = " ";
		}
		String gpid = pat.getGpid();
		String regType = pat.getRegimenType();
		if(regType == null) {
			regType = " ";
		}
		String fixedDoseString = null;
		
		if(fixedDose==null) {
			fixedDoseString = " ";
		}
		
		else {
			fixedDoseString = fixedDose.toString();
		}
		String otherDose = pat.getOtherDoseDescription();
		
		if(otherDose==null) {
			otherDose = " ";
		}
		
		//String[] colData = null; 
		String [] rowRecord = null;
		
		String treatmentStartDate = "";
		  try { 
			  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
		  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		  
		  rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID", "PID2"}, " where PID1= '" + id + "'AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
			}	
		  catch (Exception e1) 
		  { 
			  e1.printStackTrace(); 
		  }
		  
		  if(rowRecord==null || rowRecord.length!=2) {
			 treatmentStartDate = "N/A"; 
		  }
		  
		  else { 
			   
			  System.out.println(rowRecord[0]);
			  System.out.print(rowRecord[1]);
			  int encId = Integer.parseInt(rowRecord[0]); 
			  String pid2 = rowRecord[1]; 
			  EncounterResultsId eri = new EncounterResultsId(encId,id,pid2,"entered_date"); 
			  EncounterResults er = null;
			  try { 
				  er =
					  ssl.findEncounterResultsByElement(eri); 
			  } catch (Exception e) {
				  e.printStackTrace(); 
			  } 
			  
			  if(er!=null) { 
				  treatmentStartDate = er.getValue(); 
			  }
		  
			  else { 
				  treatmentStartDate = "N/A"; 
			  } 
		  } 
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element phaseNode = doc.createElement("phase");
		Text phaseValue = doc.createTextNode(phase);
		phaseNode.appendChild(phaseValue);

		responseNode.appendChild(phaseNode);

		Element regimenNode = doc.createElement("regimen");
		Text regimenValue = doc.createTextNode(regimen);
		regimenNode.appendChild(regimenValue);

		responseNode.appendChild(regimenNode);

		Element trStartNode = doc.createElement("trstart");
		Text trStartValue = doc.createTextNode(treatmentStartDate);
		trStartNode.appendChild(trStartValue);

		responseNode.appendChild(trStartNode);

		Element fdctNode = doc.createElement("fdct");
		Text fdctValue = doc.createTextNode(fixedDoseString);
		fdctNode.appendChild(fdctValue);

		responseNode.appendChild(fdctNode);

		Element streptNode = doc.createElement("strept");
		Text streptValue = doc.createTextNode(otherDose);
		streptNode.appendChild(streptValue);

		responseNode.appendChild(streptNode);
		
		Element catNode = doc.createElement("cat");
		Text catValue = doc.createTextNode(category);
		catNode.appendChild(catValue);

		responseNode.appendChild(catNode);
		
		Element gpNode = doc.createElement("gpid");
		Text gpValue = doc.createTextNode(gpid);
		gpNode.appendChild(gpValue);

		responseNode.appendChild(gpNode);
		
		Element rTypeNode = doc.createElement("regtype");
		Text rTypeValue = doc.createTextNode(regType);
		rTypeNode.appendChild(rTypeValue);

		responseNode.appendChild(rTypeNode);
		
		Element rFormNode = doc.createElement("regform");
		Text rFormValue = doc.createTextNode(regForm);
		rFormNode.appendChild(rFormValue);

		responseNode.appendChild(rFormNode);
		
		Element mrNode = doc.createElement("mr");
		Text mrValue = doc.createTextNode(mr);
		mrNode.appendChild(mrValue);

		responseNode.appendChild(mrNode);
		

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
		
		
		return xml;
	}
	
	//******************************************
	//doMRAssign
	
	private String doDOTSAssign() {
		String xml = null;
	
		String mr = request.getParameter("mr");

		String mid = request.getParameter("mid");

		String dots = request.getParameter("dots");

		String id = null;
		
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		
		String[] mrs = null;
		try {
			mrs = ssl.getColumnData("Patient", "PatientID", " where MrNo='" + mr + "'");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(mrs==null) {
			return XmlUtil.createErrorXml("Error finding MR Number");
		}
		
		else if (mrs.length==0) {
			return XmlUtil.createErrorXml("MR number not found");
		}
		
		else {
			id = mrs[0];
			//System.out.println(id);
		}
		
		Boolean dotsExists = false;
		
		try {
			dotsExists = ssl.exists("PatientDOTS", " where DOTSNo= '" + dots + "'");
		}
	
		catch(Exception e) {
			e.printStackTrace();
			return XmlUtil.createErrorXml("Error checking DOTS Number");
		}
		
		if(dotsExists==null) {
			return XmlUtil.createErrorXml("Error checking DOTS Number");
		}
		
		else if(dotsExists.booleanValue()==true) {
			return XmlUtil.createErrorXml("Ye DOTS Number - " + dots + " pehlay hi assign kiya gaya hai");
		}
		
		Boolean mrExists = false;
		
		try {
			mrExists = ssl.exists("PatientDOTS", " where MRNo= '" + mr + "'");
		}
	
		catch(Exception e) {
			e.printStackTrace();
			return XmlUtil.createErrorXml("Error checking MR Number");
		}
		
		if(mrExists==null) {
			return XmlUtil.createErrorXml("Error checking MR Number");
		}
		
		else if(mrExists.booleanValue()==true) {
			return XmlUtil.createErrorXml("MR Number - " + mr + " ko DOTS number pehlay hi assign kiya gaya hai");
		}
			
		Boolean dotsAssigned= false;
		
		PatientDOTS pd = new PatientDOTS(mr, dots);
		
		try {
			dotsAssigned = ssl.savePatientDOTS(pd);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if(dotsAssigned==null || !dotsAssigned) {
			return XmlUtil.createErrorXml("Could not update Patient record! Please try again!");
		}
		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(mid);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("DOT_ASSN");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Could not create Encounter. Please try again.");
		}
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		return XmlUtil.createSuccessXml();
	}*/
	
	// ******************************************
	// doPatientInfo******************************

	/*private String doAddressUpdate() {
		String xml = null;
		String id = request.getParameter("id");
		String monitorId = request.getParameter("mid");
		
		
		String houseNumber = request.getParameter("hn");
		String streetName = request.getParameter("sn");
		String sectorName = request.getParameter("sec");
		String colonyName = request.getParameter("cn");
		String townName = request.getParameter("tn");
		String landmark = request.getParameter("lm");
		String phone = request.getParameter("phn");
		String uc = request.getParameter("uc");
		String lat = request.getParameter("lat");
		String lng = request.getParameter("lng");
		

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		// /////////////////////
		
		Float latFloat = null;
		Float lngFloat = null;
		
		if(lat!=null)
			latFloat = Float.valueOf(lat);
		
		if(lng!=null)
			lngFloat = Float.valueOf(lng);

		Person person = null;
		Contact c = null;
		try {
			person = (Person) (ssl.findPerson(id));
		} catch (Exception e3) {
			
			e3.printStackTrace();
		}

		if(person==null) {
			return XmlUtil.createErrorXml("Could not find Person with ID: " + id);
		}
		
		
		try {
			c = (Contact) (ssl.findContact(id));
		} catch (Exception e3) {
			
			e3.printStackTrace();
		}

		if(c==null) {
			return XmlUtil.createErrorXml("Could not find Contact Information with ID: " + id);
		}
		
		

		//c = new Contact();
		c.setPid(person.getPid());
		c.setSecondaryAddressHouse(c.getAddressHouse());
		c.setSecondaryAddressStreet(c.getAddressStreet());
		c.setSecondaryAddressSector(c.getAddressSector());
		c.setSecondaryAddressColony(c.getAddressColony());
		c.setSecondaryAddressTown(c.getAddressTown());
		c.setSecondaryAddressLandMark(c.getAddressLandMark());
		c.setSecondaryAddressUc(c.getAddressUc());
		c.setSecondaryAddressLocationLat(c.getAddressLocationLat());
		c.setSecondaryAddressLocationLon(c.getAddressLocationLon());
		c.setSecondaryPhone(c.getPhone());
		
		c.setAddressHouse(houseNumber.toUpperCase());
		c.setAddressStreet(streetName.toUpperCase());
		c.setAddressSector(sectorName.toUpperCase());
		c.setAddressColony(colonyName.toUpperCase());
		c.setAddressTown(townName.toUpperCase());
		c.setAddressLandMark(landmark.toUpperCase());
		c.setAddressUc(uc.toUpperCase());
		c.setAddressLocationLat(latFloat);
		c.setAddressLocationLon(lngFloat);
		c.setPhone(phone);
		
		
		boolean cUpdated = true;
		try {
			cUpdated = ssl.updateContact(c);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		if(!cUpdated) {
			return XmlUtil.createErrorXml("Could not save Person Address! Please try again");
		}

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(monitorId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("ADDR_UPD");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);
		
		EncounterResults houseResult = ModelUtil.createEncounterResult(e,
				"HOUSE_NUM".toUpperCase(), c.getAddressHouse());
		encounters.add(houseResult);
		
		EncounterResults streetResult = ModelUtil.createEncounterResult(e,
				"STREET".toUpperCase(), c.getAddressStreet());
		encounters.add(streetResult);
		
		EncounterResults sectorResult = ModelUtil.createEncounterResult(e,
				"SECTOR".toUpperCase(), c.getAddressSector());
		encounters.add(sectorResult);
		
		EncounterResults colonyResult = ModelUtil.createEncounterResult(e,
				"COLONY".toUpperCase(), c.getAddressColony());
		encounters.add(colonyResult);
		
		EncounterResults townResult = ModelUtil.createEncounterResult(e,
				"TOWN".toUpperCase(), c.getAddressTown());
		encounters.add(townResult);
		
		EncounterResults landmarkResult = ModelUtil.createEncounterResult(e,
				"LANDMARK".toUpperCase(), c.getAddressLandMark());
		encounters.add(landmarkResult);
		
		EncounterResults ucResult = ModelUtil.createEncounterResult(e,
				"UC".toUpperCase(), c.getAddressUc());
		encounters.add(ucResult);
		
		EncounterResults latResult = ModelUtil.createEncounterResult(e,
				"LAT".toUpperCase(), c.getAddressLocationLat().toString());
		encounters.add(latResult);

		EncounterResults lngResult = ModelUtil.createEncounterResult(e,
				"LONG".toUpperCase(), c.getAddressLocationLon().toString());
		encounters.add(lngResult);
		
		EncounterResults phoneResult = ModelUtil.createEncounterResult(e,
				"PHONE".toUpperCase(), c.getPhone());
		encounters.add(phoneResult);
		

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}

		}
		
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;

	}*/
	
	// ******************************************
	// doPatientVerify*******************************/
	/*private String doPatientVerify() {
		
		
		String xml = null;
		String id = request.getParameter("id");
		String mid = request.getParameter("mid").toUpperCase();
		String metPatient = request.getParameter("metp").toUpperCase();
		String whyNotMetPatient = request.getParameter("whyNotMet");
		
		if(whyNotMetPatient==null) {
			whyNotMetPatient = "";
		}
		
		else {
			whyNotMetPatient = whyNotMetPatient.toUpperCase();
		}
		
		//name
		String nameVerified = request.getParameter("vername").toUpperCase();
		
		String fname = request.getParameter("fname");
		
		fname = (fname==null) ? "" : fname.toUpperCase();
		
		String lname = request.getParameter("lname");
		
		lname = (lname==null) ? "" : lname.toUpperCase();
		
		//gender
		String genderVerified = request.getParameter("vergender").toUpperCase();
		
		String gender = request.getParameter("gender");
		
		gender = (gender==null) ? "" : gender.toUpperCase();
		
		
		//age
		String ageVerified = request.getParameter("verage").toUpperCase();
		
		String age = request.getParameter("age");
		
		age = (age==null) ? "" : age.toUpperCase();
		
		//address
		String addrVerified = request.getParameter("veraddr").toUpperCase();
		
		//tbType
		String tbTypeVerified = request.getParameter("vertbtype").toUpperCase();
		
		String tbType = request.getParameter("tbtype");
		
		tbType = (tbType==null) ? "" : tbType.toUpperCase();
		
		//patient type
		String patTypeVerified = request.getParameter("verptype").toUpperCase();
		
		String patType = request.getParameter("ptype");
		
		patType = (patType==null) ? "" : patType.toUpperCase();
		
		//tb treatment
		String takenTreatment = request.getParameter("tt").toUpperCase();
		String moreThanFourMonthsTreatment = request.getParameter("fm").toUpperCase();
		
		//category
		String catVerified = request.getParameter("vercat").toUpperCase();
		
		String cat = request.getParameter("cat");
		
		cat = (cat==null) ? "" : cat.toUpperCase();
		
		//CHW took money
		String chwmoney = request.getParameter("chwmoney").toUpperCase();
		
		
		//seen GP and if not, why not
		String seenGp = request.getParameter("metgp").toUpperCase();
		String whyNotSeen = request.getParameter("wng");
		whyNotSeen = (whyNotSeen==null) ? "" : whyNotSeen.toUpperCase();
		
		String otherWhyNotSeen = request.getParameter("otrwhy");
		otherWhyNotSeen = (otherWhyNotSeen==null) ? "" : otherWhyNotSeen.toUpperCase();
		
		String numSamples = request.getParameter("ns");
		numSamples = (numSamples==null) ? "" : numSamples.toUpperCase();
		
		String comments = request.getParameter("comm");
		comments = (comments==null) ? "" : comments.toUpperCase();
		
		
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		
		EncounterId encId = new EncounterId();
		encId.setEncounterId(-1);
		encId.setPid1(id);
		encId.setPid2(mid);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("PATVER");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);

		EncounterResults metPatientResult = ModelUtil.createEncounterResult(e,
				"met_patient".toUpperCase(), metPatient.toUpperCase());
		encounters.add(metPatientResult);

		
		EncounterResults whyNotMetResult = ModelUtil
					.createEncounterResult(e, "why_not_met".toUpperCase(),
							whyNotMetPatient);
			encounters.add(whyNotMetResult);
			
			
			EncounterResults verNameResults = ModelUtil
			.createEncounterResult(e, "name_verified".toUpperCase(),
					nameVerified);
			encounters.add(verNameResults);
			
			EncounterResults firstNameResults = ModelUtil
			.createEncounterResult(e, "first_name".toUpperCase(),
					fname);
			encounters.add(firstNameResults);
			
			EncounterResults lastNameResults = ModelUtil
			.createEncounterResult(e, "last_name".toUpperCase(),
					lname);
			encounters.add(lastNameResults);
		
			EncounterResults verGenderResult = ModelUtil
					.createEncounterResult(e, "gender_verified".toUpperCase(), genderVerified);
			encounters.add(verGenderResult);	
			
			EncounterResults genderResult = ModelUtil
				.createEncounterResult(e, "gender".toUpperCase(), gender);
			encounters.add(genderResult);
			
			EncounterResults verAgeResult = ModelUtil
				.createEncounterResult(e, "age_verified".toUpperCase(), ageVerified);
			encounters.add(verAgeResult);	
	
			EncounterResults ageResult = ModelUtil
				.createEncounterResult(e, "age".toUpperCase(), age);
			encounters.add(ageResult);
			
			EncounterResults verAddrResult = ModelUtil
				.createEncounterResult(e, "addr_verified".toUpperCase(), addrVerified);
			encounters.add(verAddrResult);	
			
			EncounterResults verTBTypeResult = ModelUtil
				.createEncounterResult(e, "tb_type_verified".toUpperCase(), tbTypeVerified);
			encounters.add(verTBTypeResult);	

			EncounterResults tbTypeResult = ModelUtil
				.createEncounterResult(e, "tb_type".toUpperCase(), tbType);
			encounters.add(tbTypeResult);
			
			EncounterResults verPatTypeResult = ModelUtil
				.createEncounterResult(e, "pat_type_verified".toUpperCase(), patTypeVerified);
			encounters.add(verPatTypeResult);	

			EncounterResults patTypeResult = ModelUtil
			.createEncounterResult(e, "pat_type".toUpperCase(), patType);
			encounters.add(patTypeResult);
		
				
		EncounterResults takenTreatmentResult = ModelUtil
				.createEncounterResult(e, "taken_treatment".toUpperCase(),
						takenTreatment);
				encounters.add(takenTreatmentResult);
		
		EncounterResults fourMonthsTreatmentResult = ModelUtil
				.createEncounterResult(e, "four_months_treatment"
						.toUpperCase(), moreThanFourMonthsTreatment);
				encounters.add(fourMonthsTreatmentResult);
			

				EncounterResults verCatResult = ModelUtil
				.createEncounterResult(e, "cat_verified".toUpperCase(), catVerified);
			encounters.add(verCatResult);	

			EncounterResults catResult = ModelUtil
			.createEncounterResult(e, "cat".toUpperCase(), cat);
			encounters.add(catResult);
			
			
			
				EncounterResults chwMoneyResult = ModelUtil
						.createEncounterResult(e, "chw_wanted_money"
								.toUpperCase(), chwmoney);
				encounters.add(chwMoneyResult);
				
				EncounterResults metGPResult = ModelUtil
					.createEncounterResult(e, "met_gp"
						.toUpperCase(), seenGp);
				encounters.add(metGPResult);
				
				EncounterResults whyNotMetGPResult = ModelUtil
				.createEncounterResult(e, "why_not_met_gp"
					.toUpperCase(), whyNotSeen);
			encounters.add(whyNotMetGPResult);
			
			EncounterResults otherWhyNotMetGPResult = ModelUtil
			.createEncounterResult(e, "other_why_not_met_gp"
				.toUpperCase(), otherWhyNotSeen);
		encounters.add(otherWhyNotMetGPResult);
	
	
		EncounterResults numSamplesResult = ModelUtil.createEncounterResult(e,
				"num_of_samples".toUpperCase(), numSamples);
		encounters.add(numSamplesResult);

		
			EncounterResults commentsResult = ModelUtil
					.createEncounterResult(e, "comments".toUpperCase(),
							comments);
			encounters.add(commentsResult);
		

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR");
			}

		}
		try {
			updateReturningPatientAfterNAF(id);
		} catch (Exception e1) {
			e1.printStackTrace();
			XmlUtil.createErrorXml("Tafseel bhej di gayi laikin patient status update nahin huwa. TB REACH team se foran rujoo karain");
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}*/
	
	// ******************************************
	//getPatientVerifyInfo*******************************/
/*	private String getPatientVerifyInfo(String id) {
		
		
		String xml = null;
		
		ssl = new ServerServiceImpl();
		
		
		Person p = null;
		Patient pat = null;
		Contact c = null;
		
		
		try {
			p = ssl.findPerson(id);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		if(p==null) {
			return XmlUtil.createErrorXml("Patient with ID: " + id + " not found.");
		}
		
		Boolean exists = null;
		
		try {
			exists = ssl.exists("Encounter",  " where PID1='" + id + "' AND EncounterType='BASELINE'");
		} catch (Exception e1) {
			
			e1.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Could not track patient treatment status" );
		}
		
		if(exists.booleanValue()==false) {
			return XmlUtil.createErrorXml("Is Patient ka I form nahin bhara gaya. Patient Verification se pehlay I form bharna laazmi hai" );
		}
		
		String fName = p.getFirstName();
		String fathersName = p.getGuardianName();
		Date dobDate = p.getDob();
		String dob = null;
		if(dobDate!=null)
			dob = dobDate.toString();
		String mStatus = p.getMaritalStatus();
		String religion = p.getReligion();
		long age = 0;
		
		if(dob!=null) {
			age = getAgeInYears(p.getDob());
		}
		
		String nic = p.getNic();
		char gender = p.getGender();
		String genderString = "" + gender;
		String state = " ";
		String mrNum = " ";
		String houseNum = " ";
		String street = " ";
		String sector = " ";
		String town = " ";
		String colony = " ";
		String uc = " ";
		String landmark = " ";
		String phone = " ";
		String numAdults = " ";
		String numChildren = " ";
		
		if(fName==null)
			fName = " ";
		if(fathersName==null)
			fathersName= " ";
		if(dob==null)
			dob = " ";
		if(nic==null)
			nic= " ";
		if(mStatus==null)
			mStatus =" ";
		if(religion==null)
			religion = " ";
		
	    try {
			c = ssl.findContact(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		if(c!=null) {
			if(!c.getAddressHouse().equals("N/A")) {
				  houseNum = c.getAddressHouse();
			  } 
			  
			  if(!c.getAddressStreet().equals("N/A")) {
				  street = c.getAddressStreet();
			  }
			  
			  if(!c.getAddressSector().equals("N/A")) { 
				  sector = c.getAddressSector();
			  } 
			  
			  if(!c.getAddressColony().equals("N/A")) {
				  colony = c.getAddressColony();
			  }
			  
			  if(!c.getAddressTown().equals("N/A")) {
				  town = c.getAddressTown();
			  }
			 
			  if(!c.getAddressUc().equals("N/A")) { 
				  uc = c.getAddressUc();
			  } 
			  
			  if(!c.getAddressLandMark().equals("N/A")) {
				  landmark = c.getAddressLandMark();
			  }
			  
			  if(c.getPhone()!=null) {
				  phone = c.getPhone();
			  }
			  
			  if(c.getHouseHoldAdults()!=null) {
				  numAdults = c.getHouseHoldAdults().toString();
			  }
			  
			  if(c.getHouseHoldChildren()!=null) {
				  numChildren = c.getHouseHoldChildren().toString();
			  }
			  
		}
		
		try {
			pat = ssl.findPatient(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String gpId = "";
		String monitorId = "";
		String chwId = "";
		String type = "";
		String category = "";
		String site = "";
		String phase = "";
		String regimen = "";
		
		if(pat!=null) {
			
			if(pat.getMrno()!=null)
				mrNum = pat.getMrno();
			
			if(pat.getPatientStatus()!=null) {
				state = pat.getPatientStatus();
			}
			
			if(pat.getGpid()!=null) {
				gpId = pat.getGpid();
			}
			
			if(pat.getChwid()!=null) {
				chwId = pat.getChwid();
			}
			
			if(pat.getMonitorId()!=null) {
				monitorId = pat.getMonitorId();
			}
			
			if(pat.getDiseaseCategory()!=null) {
				category = pat.getDiseaseCategory();
			}
			
			if(pat.getTreatmentPhase()!=null) {
				phase = pat.getTreatmentPhase();
			}
			
			if(pat.getDiseaseSite()!=null) {
				site = pat.getDiseaseSite();
			}
			
			if(pat.getPatientType()!=null) {
				type = pat.getPatientType();
			}
			
			if(pat.getRegimen()!=null) {
				regimen = pat.getRegimen();
			}
		}
	    
		if(age<=0) {
			String[] er = null;
			try {
				er = ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id + " ' AND Element='AGE'");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String ageString = "0";
			
			if(er!=null && er.length>0)
				ageString = er[0];
			
			try {
				age = Long.parseLong(ageString);
			}
			
			catch(Exception e) {
				e.printStackTrace();
				age = 0;
			}
		}
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element idNode = doc.createElement("pid");
		Text idValue = doc.createTextNode(id);
		idNode.appendChild(idValue);

		responseNode.appendChild(idNode);
		
		Element gpIdNode = doc.createElement("gpid");
		Text gpIdValue = doc.createTextNode(gpId);
		gpIdNode.appendChild(gpIdValue);
		
		responseNode.appendChild(gpIdNode);
		
		Element monitorIdNode = doc.createElement("mid");
		Text monitorIdValue = doc.createTextNode(monitorId);
		monitorIdNode.appendChild(monitorIdValue);
		
		responseNode.appendChild(monitorIdNode);
		
		Element chwIdNode = doc.createElement("chwid");
		Text chwIdValue = doc.createTextNode(chwId);
		chwIdNode.appendChild(chwIdValue);

		responseNode.appendChild(chwIdNode);
		
		Element fnameNode = doc.createElement("fname");
		Text fnameValue = doc.createTextNode(fName);
		fnameNode.appendChild(fnameValue);

		responseNode.appendChild(fnameNode);

		Element fatNameNode = doc.createElement("fatname");
		Text fatNameValue = doc.createTextNode(fathersName);
		fatNameNode.appendChild(fatNameValue);

		responseNode.appendChild(fatNameNode);
		
		Element genderNode = doc.createElement("gender");
		Text genderValue = doc.createTextNode(genderString);
		genderNode.appendChild(genderValue);

		responseNode.appendChild(genderNode);
		
		Element nicNode = doc.createElement("nic");
		Text nicValue = doc.createTextNode(nic);
		nicNode.appendChild(nicValue);

		responseNode.appendChild(nicNode);

		Element dobNode = doc.createElement("dob");
		Text dobValue = doc.createTextNode(dob);
		dobNode.appendChild(dobValue);

		responseNode.appendChild(dobNode);

		Element ageNode = doc.createElement("age");
		Text ageValue = doc.createTextNode(new Long(age).toString());
		ageNode.appendChild(ageValue);

		responseNode.appendChild(ageNode);
		
		Element typeNode = doc.createElement("type");
		Text typeValue = doc.createTextNode(type);
		typeNode.appendChild(typeValue);

		responseNode.appendChild(typeNode);
		
		Element catNode = doc.createElement("cat");
		Text catValue = doc.createTextNode(category);
		catNode.appendChild(catValue);

		responseNode.appendChild(catNode);
		
		Element siteNode = doc.createElement("site");
		Text siteValue = doc.createTextNode(site);
		siteNode.appendChild(siteValue);

		responseNode.appendChild(siteNode);
		
		Element regimenNode = doc.createElement("regimen");
		Text regimenValue = doc.createTextNode(regimen);
		regimenNode.appendChild(regimenValue);

		responseNode.appendChild(regimenNode);
		
		Element phaseNode = doc.createElement("phase");
		Text phaseValue = doc.createTextNode(phase);
		phaseNode.appendChild(phaseValue);

		responseNode.appendChild(phaseNode);

		Element phoneNode = doc.createElement("phone");
		Text phoneValue = doc.createTextNode(phone);
		phoneNode.appendChild(phoneValue);

		responseNode.appendChild(phoneNode);
		
		Element mStatusNode = doc.createElement("mstatus");
		Text mStatusValue = doc.createTextNode(mStatus);
		mStatusNode.appendChild(mStatusValue);

		responseNode.appendChild(mStatusNode);
		
		Element religionNode = doc.createElement("religion");
		Text religionValue = doc.createTextNode(religion);
		religionNode.appendChild(religionValue);

		responseNode.appendChild(religionNode);

		Element houseNumNode = doc.createElement("housenum");
		Text houseNumValue = doc.createTextNode(houseNum);
		houseNumNode.appendChild(houseNumValue);

		responseNode.appendChild(houseNumNode);

		Element streetNode = doc.createElement("street");
		Text streetValue = doc.createTextNode(street);
		streetNode.appendChild(streetValue);

		responseNode.appendChild(streetNode);

		Element sectorNode = doc.createElement("sector");
		Text sectorValue = doc.createTextNode(sector);
		sectorNode.appendChild(sectorValue);

		responseNode.appendChild(sectorNode);

		Element colonyNode = doc.createElement("colony");
		Text colonyValue = doc.createTextNode(colony);
		colonyNode.appendChild(colonyValue);

		responseNode.appendChild(colonyNode);

		Element townNode = doc.createElement("town");
		Text townValue = doc.createTextNode(town);
		townNode.appendChild(townValue);

		responseNode.appendChild(townNode);

		Element ucNode = doc.createElement("uc");
		Text ucValue = doc.createTextNode(uc);
		ucNode.appendChild(ucValue);

		responseNode.appendChild(ucNode);

		Element landmarkNode = doc.createElement("landmark");
		Text landmarkValue = doc.createTextNode(landmark);
		landmarkNode.appendChild(landmarkValue);

		responseNode.appendChild(landmarkNode);
		
		Element numAdultsNode = doc.createElement("numadults");
		Text numAdultsValue = doc.createTextNode(numAdults);
		numAdultsNode.appendChild(numAdultsValue);

		responseNode.appendChild(numAdultsNode);
		
		Element numChildrenNode = doc.createElement("numchildren");
		Text numChildrenValue = doc.createTextNode(numChildren);
		numChildrenNode.appendChild(numChildrenValue);

		responseNode.appendChild(numChildrenNode);
		
		Element mrNode = doc.createElement("mr");
		Text mrValue = doc.createTextNode(mrNum);
		mrNode.appendChild(mrValue);

		responseNode.appendChild(mrNode);
		
		Element stateNode = doc.createElement("state");
		Text stateValue = doc.createTextNode(state);
		stateNode.appendChild(stateValue);

		responseNode.appendChild(stateNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);

		return xml;
	}     */
	
	/*private String doNoActiveFollowup() {
		String xml = null;
		String id = request.getParameter("id");
		String monitorId = request.getParameter("mid");
		String gpId = request.getParameter("gpid");

		String reason = request.getParameter("rsn");
		String otherReason = request.getParameter("otrrsn");

		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		System.out.println("------->" + enteredDate);
		
		if(otherReason==null) {
			otherReason = "";
		}

		Patient pat = null;

		try {
			pat = ssl.findPatient(id);
		} catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
		}

		if(pat==null) {
			return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}
		
		try {
			pat.setPatientStatus("SUSPENDED");
			boolean patUpdated = ssl.updatePatient(pat);
		}
		
		catch (Exception e2) {
			//  Auto-generated catch block
			e2.printStackTrace();
			return XmlUtil.createErrorXml("Error updating patient");
		}
		
		

		EncounterId encId = new EncounterId();
		encId.setPid1(id);
		encId.setPid2(monitorId);

		Encounter e = new Encounter();
		e.setId(encId);
		e.setEncounterType("NO_ACT_FOL");
		e.setDateEncounterStart(encounterStartDate);
		e.setDateEncounterEnd(encounterEndDate);
		try {
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate, DateTimeUtil.FE_FORMAT));
		}
		
		catch(Exception e1) {
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Bad entered date. Please try again");
		}

		try {
			boolean eCreated = ssl.saveEncounter(e);
		} catch (Exception e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
			return XmlUtil.createErrorXml("Could not create Encounter. Please try again.");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

		EncounterResults dateResult = ModelUtil.createEncounterResult(e,
				"entered_date".toUpperCase(), enteredDate);
		encounters.add(dateResult);
		
		EncounterResults gpidResult = ModelUtil.createEncounterResult(e,
				"gp_id".toUpperCase(), gpId);
		encounters.add(gpidResult);

		EncounterResults reasonResult = ModelUtil.createEncounterResult(e,
				"reason".toUpperCase(), reason.toUpperCase());
		encounters.add(reasonResult);
		
		EncounterResults otrReasonResult = ModelUtil.createEncounterResult(e,
				"other_reason".toUpperCase(), otherReason.toUpperCase());
		encounters.add(otrReasonResult);

		boolean resultSave = true;

		for (int i = 0; i < encounters.size(); i++) {
			try {
				System.out.println(i);
				resultSave = ssl.saveEncounterResults(encounters.get(i));
			} catch (Exception e1) {
				//  Auto-generated catch block
				e1.printStackTrace();
				break;
			}

			if (!resultSave) {
				return XmlUtil.createErrorXml("ERROR! Please try again");
			}

		}
		

		xml = XmlUtil.createSuccessXml();
		
		
		return xml;
	}
	
	private String doYield() {
		String xml = null;
		String id = request.getParameter("id");
		String chwId = request.getParameter("chwid").toUpperCase();
		String gpId = request.getParameter("gpid").toUpperCase();
		
		String age = request.getParameter("age");
		String sex = request.getParameter("sex").toUpperCase();
	
	
		String diab = request.getParameter("diab").toUpperCase();
		String diabf = request.getParameter("diabf").toUpperCase();
		String lungd = request.getParameter("lungd").toUpperCase();
		String lungdf = request.getParameter("lungdf").toUpperCase();
		String smoke = request.getParameter("smoke").toUpperCase();
		String smokeh = request.getParameter("smokeh").toUpperCase();
		String narc = request.getParameter("narc").toUpperCase();
		
		String startDate = request.getParameter("sd");
		String startTime = request.getParameter("st");
		String endTime = request.getParameter("et");
		String enteredDate = request.getParameter("ed");
		System.out.println("------->" + enteredDate);
		
		

		
		if(id!=null) {
			Patient pat = null;
			try {
				pat = ssl.findPatient(id);
			} catch (Exception e2) {
				//  Auto-generated catch block
				e2.printStackTrace();
			}

			if(pat==null) {
				return XmlUtil.createErrorXml("Patient with id " + id + " does not exist. Please recheck ID and try again.");
			}
		}
		
		NonSuspect ns = new NonSuspect(id);
		ns.setChwid(chwId);
		ns.setGpid(gpId);
		ns.setAge(Integer.parseInt(age));
		if(sex.equalsIgnoreCase("male"))
			ns.setGender(new Character('M'));
		
		else
			ns.setGender(new Character('F'));
			
		ns.setDiabetesHistory(diab);
		ns.setDiabetesFamilyHistory(diabf);
		ns.setLungDiseaseHistory(lungd);
		ns.setLungDiseaseFamilyHistory(lungdf);
		ns.setSmokingHistory(smokeh);
		ns.setCurrentlySmoking(smoke);
		ns.setUsedNarcotics(narc);
		
		try {
			ns.setDateEntered(DateTimeUtil.getDateFromString(
							enteredDate, DateTimeUtil.FE_FORMAT));
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return XmlUtil
					.createErrorXml("Bad entered date. Please try again");
		}
		
		try {
			ssl.saveNonSuspect(ns);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return XmlUtil
			.createErrorXml("Error saving Yield data. Please try again");
		}
		
		//if patient then create encounter
		
		if(id!=null) {
			EncounterId encId = new EncounterId();
			encId.setPid1(id);
			encId.setPid2(chwId);

			Encounter e = new Encounter();
			e.setId(encId);
			e.setEncounterType("YIELD");
			e.setDateEncounterStart(encounterStartDate);
			e.setDateEncounterEnd(encounterEndDate);
			try {
				e.setDateEncounterEntered(DateTimeUtil.getDateFromString(
						enteredDate, DateTimeUtil.FE_FORMAT));
			}

			catch (Exception e1) {
				e1.printStackTrace();
				return XmlUtil
						.createErrorXml("Bad entered date. Please try again");
			}

			try {
				boolean eCreated = ssl.saveEncounter(e);
			} catch (Exception e1) {
				// Auto-generated catch block
				e1.printStackTrace();
				return XmlUtil
						.createErrorXml("Could not create Encounter. Please try again.");
			}

			ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults>();

			EncounterResults dateResult = ModelUtil.createEncounterResult(e,
					"ENTERED_DATE", enteredDate);
			encounters.add(dateResult);

			EncounterResults gpidResult = ModelUtil.createEncounterResult(e,
					"GP_ID", gpId);
			encounters.add(gpidResult);
			
			EncounterResults ageResult = ModelUtil.createEncounterResult(e,
					"Y_AGE", age);
			encounters.add(ageResult);
			
			EncounterResults genderResult = ModelUtil.createEncounterResult(e,
					"Y_GENDER", sex);
			encounters.add(genderResult);
			
			EncounterResults diabetesHistoryResult = ModelUtil.createEncounterResult(e,
					"DIABETES_HISTORY", diab);
			encounters.add(diabetesHistoryResult);
			
			EncounterResults diabetesFamilyHistoryResult = ModelUtil.createEncounterResult(e,
					"DIABETES_FAMILY_HISTORY", diabf);
			encounters.add(diabetesFamilyHistoryResult);
			
			EncounterResults lungDiseaseHistoryResult = ModelUtil.createEncounterResult(e,
					"LUNG_DISEASE_HISTORY", lungd);
			encounters.add(lungDiseaseHistoryResult);
			
			EncounterResults lungDiseaseFamilyHistoryResult = ModelUtil.createEncounterResult(e,
					"LUNG_DISEASE_FAMILY_HISTORY", lungdf);
			encounters.add(lungDiseaseFamilyHistoryResult);
			
			EncounterResults smokingResult = ModelUtil.createEncounterResult(e,
					"SMOKER", smoke);
			encounters.add(smokingResult);
			
			EncounterResults smokingHistoryResult = ModelUtil.createEncounterResult(e,
					"SMOKING_HISTORY", smokeh);
			encounters.add(smokingHistoryResult);

			EncounterResults narcResult = ModelUtil.createEncounterResult(e,
					"NARC_USE", narc);
			encounters.add(narcResult);
			
			boolean resultSave = true;

			for (int i = 0; i < encounters.size(); i++) {
				try {
					System.out.println(i);
					resultSave = ssl.saveEncounterResults(encounters.get(i));
				} catch (Exception e1) {
					// Auto-generated catch block
					e1.printStackTrace();
					break;
				}

				if (!resultSave) {
					return XmlUtil.createErrorXml("ERROR! Please try again");
				}

			}
		}

		xml = XmlUtil.createSuccessXml();
		
		
		return xml;
	}
	
	private String getNoActiveFollowupInfo(String id) {
		String xml = null;

		
		 ssl = new ServerServiceImpl();
		 
		 Patient pat = null;
		 
		  
		  
		  
		  try {
			  pat = ssl.findPatient(id); 
		  } 
		  catch (Exception e1) {
			  e1.printStackTrace();
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + ". Please try again.");
		  }
		  
		  if(pat==null) {
			  return XmlUtil.createErrorXml("Could not find Patient with id " + id + "Please try again.");
		  }
		  
		  
		  String status = pat.getPatientStatus();
		  String gpId = pat.getGpid();
		  if(gpId==null)
			  gpId = "";
		  String chwId = pat.getChwid();
		  if(chwId==null)
			  chwId = "";
		  String mr = pat.getMrno();
		  if(mr==null)
			  mr = "";
		  String diagnosis = "";
		  String rowRecord[] = null;
		  String encId = null;
		  String pid2 = null;
		  
		  if(pat.getDiseaseConfirmed()==true) {
			  status="CONFIRMED";
		  try { 
			  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
		  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		  
		  rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID","PID2"}, " where PID1= '" + id + "' AND EncounterType='CDF'");
		  
		  }	
		  catch (Exception e1) 
		  { 
			  e1.printStackTrace(); 
		  }
		  
		  if(rowRecord!=null && rowRecord.length==2) {
			  encId = rowRecord[0];
			  pid2 = rowRecord[1];
			  EncounterResultsId eri = new EncounterResultsId(Integer.parseInt(encId),id,pid2,"DIAGNOSIS");
			  EncounterResults er = null;
			  
			  try {
				  er = ssl.findEncounterResultsByElement(eri);
			  }
			  
			  catch(Exception e) {
				  e.printStackTrace();
				  return XmlUtil.createErrorXml("ERROR: Could not track diagnosis");
			  }
			  
			  if(er!=null) {
				  diagnosis = er.getValue();
			  }
			  
		  }
		  
		  else { 
			  try { 
					  colData = ssl.getColumnData("Encounter", "CONCAT(encounterId, '|', pid2) AS column1",
				  " where pid1='" + id + "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
				  
				  rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID","PID2"}, " where PID1= '" + id + "' AND EncounterType='PAED_DIAG'");
				  
				  }	
				  catch (Exception e1) 
				  { 
					  e1.printStackTrace(); 
				  }
				  
				  if(rowRecord!=null && rowRecord.length==2) {
					  encId = rowRecord[0];
					  pid2 = rowRecord[1];
					  EncounterResultsId eri = new EncounterResultsId(Integer.parseInt(encId),id,pid2,"DIAGNOSIS");
					  EncounterResults er = null;
					  
					  try {
						  er = ssl.findEncounterResultsByElement(eri);
					  }
					  
					  catch(Exception e) {
						  e.printStackTrace();
						  return XmlUtil.createErrorXml("ERROR: Could not track diagnosis");
					  }
					  
					  if(er!=null) {
						  diagnosis = er.getValue();
					  }
					  
				  }
			  
		  }
		  
		  if(diagnosis.length()==0) {
			  diagnosis = "SMEAR POSITIVE PULMONARY TB";
		  }
		  }
		  else {
			  diagnosis = "N/A";
		  }
		  EFFORT_TYPE
		    PHONE CALL
		    HOUSEHOLD VISIT
		  
		  long phoneCount = 0;
		  long hhCount = 0;
		try {
			phoneCount = ssl.count("EncounterResults", " where PID1='" + id + "' AND Element='EFFORT_TYPE' AND Value='PHONE CALL'");
		} catch (Exception e1) {
			
			e1.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Could not find phone call count");
		}
		try {
			hhCount = ssl.count("EncounterResults", " where PID1='" + id + "' AND Element='EFFORT_TYPE' AND Value='HOUSEHOLD VISIT'");
		} catch (Exception e1) {
			
			e1.printStackTrace();
			return XmlUtil.createErrorXml("ERROR: Could not find household visit count");
		}
			 
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		

		Element gpidNode = doc.createElement("gpid");
		
		Text gpidValue = doc.createTextNode(gpId);
		gpidNode.appendChild(gpidValue);

		responseNode.appendChild(gpidNode);

		Element chwidNode = doc.createElement("chwid");
		Text chwidValue = doc.createTextNode(chwId);
		chwidNode.appendChild(chwidValue);

		responseNode.appendChild(chwidNode);
		
		Element mrNode = doc.createElement("mr");
		//Text lNameValue = doc.createTextNode("Patient");
		Text mrValue = doc.createTextNode(mr);
		mrNode.appendChild(mrValue);

		responseNode.appendChild(mrNode);

		Element statusNode = doc.createElement("status");
		Text statusValue = doc.createTextNode(status);
		statusNode.appendChild(statusValue);

		responseNode.appendChild(statusNode);

		Element diagnosisNode = doc.createElement("diagnosis");
		Text diagnosisValue = doc.createTextNode(diagnosis);
		diagnosisNode.appendChild(diagnosisValue);

		responseNode.appendChild(diagnosisNode);

		Element numphoneNode = doc.createElement("numphone");
		Text numphoneValue = doc.createTextNode(new Long(phoneCount).toString());
		numphoneNode.appendChild(numphoneValue);

		responseNode.appendChild(numphoneNode);
		
		Element numhhNode = doc.createElement("numhh");
		Text numhhValue = doc.createTextNode(new Long(hhCount).toString());
		numhhNode.appendChild(numhhValue);

		responseNode.appendChild(numhhNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
		return xml;
	}
	
	public void updateReturningPatientAfterNAF(String id) throws Exception {
		
		Patient pat = null;
		
		pat = ssl.findPatient(id);
		
		updateReturningPatientAfterNAF(pat);
		
	}
	
	public void updateReturningPatientAfterNAF(Patient pat) throws Exception {
		
		if(pat==null)
			return;
		
		String status = pat.getPatientStatus();
		
		if(status.equals("SUSPENDED")) {
			pat.setPatientStatus("SUSPECT");
			ssl.updatePatient(pat);
		}
		
	}
	
	private String handleReportFormQuery(){
		String xml = null;
		
		String requestType = request.getParameter("qtype");

		if (requestType.equals(RequestType.REPORT_PENDING_FOLLOWUP)) {
			xml = getPendingFollowupReport();
		}
		else if (requestType.equals(RequestType.REPORT_PENDING_SPUTUM)) {
			xml = getPendingSputumReport();
		}
		
		
		return xml;
	}
	
	private String getPendingSputumReport() {
		String xml = null;
		
		
		
		
		return xml;
	}
	
	private String getPendingFollowupReport() {
		String xml = null;
		String data = "";
		String idType = request.getParameter("idType");
		String id = request.getParameter("id");
		int month = Integer.parseInt(request.getParameter("month"));
		
		String filter = " AND " + idType+ "ID = '" + id + "'";
		
		String mainQuery[] = new String[8];
		String subQuery[] = new String[8];
		
		//month 2 for CAT I - OLD AND CAT I - NEW
		mainQuery[1] = "select PatientID from _FollowupProgress where (Category = 'CAT I' OR Category = 'CAT I-NEW') and datediff(curdate(), DateBaselineVisit) >= 54 and Visit2Date is null AND Visit3Date is null AND Visit4Date is null AND Visit5Date is null AND Visit6Date is null AND Visit7Date is null AND Visit8Date is null AND Visit9Date is null AND Visit10Date is null and PatientStatus <> 'CLOSED' " + filter;
		subQuery[1] = "select PatientID from _FollowupProgress where (Category = 'CAT I' OR Category = 'CAT I-NEW') and datediff(curdate(), DateBaselineVisit) >= 54 and Visit2Date is null AND Visit3Date is null AND Visit4Date is null AND (Visit5Date is NOT null OR Visit6Date is  NOT null OR Visit7Date is NOT null OR Visit8Date is NOT null OR Visit9Date is NOT null OR Visit10Date is NOT null) and PatientStatus <> 'CLOSED' " + filter;
	
		//month 3 for CAT II - OLD AND CAT II - NEW
		mainQuery[2] = "select PatientID from _FollowupProgress where (Category = 'CAT II' OR Category = 'CAT II-NEW') and datediff(curdate(), DateBaselineVisit) >= 84 and Visit3Date is null AND Visit4Date is null AND Visit5Date is null AND Visit6Date is null AND Visit7Date is null AND Visit8Date is null AND Visit9Date is null AND Visit10Date is null and PatientStatus <> 'CLOSED' " + filter;
		subQuery[2] = "select PatientID from _FollowupProgress where (Category = 'CAT II' OR Category = 'CAT II-NEW') and datediff(curdate(), DateBaselineVisit) >= 84 and Visit3Date is null AND Visit4Date is null AND (Visit5Date is NOT null OR Visit6Date is  NOT null OR Visit7Date is NOT null OR Visit8Date is NOT null OR Visit9Date is NOT null OR Visit10Date is NOT null) and PatientStatus <> 'CLOSED' " + filter;
		
		//month 4 for CAT I - NEW  NEW ADDITION
		mainQuery[3] = "select PatientID from _FollowupProgress where Category='CAT I' AND DateBaselineVisit >= '2012-05-18' AND datediff(curdate(), DateBaselineVisit) >= 114 and  Visit4Date is null AND Visit5Date is null AND Visit6Date is null AND Visit7Date is null AND Visit8Date is null AND Visit9Date is null AND Visit10Date is null and PatientStatus <> 'CLOSED' " + filter;
		subQuery[3] = "select PatientID from _FollowupProgress where Category='CAT I' AND DateBaselineVisit >= '2012-05-18' AND datediff(curdate(), DateBaselineVisit) >= 114 and  Visit4Date is null AND (Visit5Date is NOT null OR Visit6Date is NOT null OR Visit7Date is NOT null OR Visit8Date is NOT null OR Visit9Date is NOT null OR Visit10Date is NOT null) and PatientStatus <> 'CLOSED' " + filter;
		
		//month 5 for CAT 1 - OLD AND CAT II
		mainQuery[4] = "select PatientID from _FollowupProgress where ((Category='CAT I' AND DateBaselineVisit < '2012-05-18') OR Category='CAT II') AND datediff(curdate(), DateBaselineVisit) >= 144 and  Visit5Date is null AND Visit6Date is null AND Visit7Date is null AND Visit8Date is null AND Visit9Date is null AND Visit10Date is null and PatientStatus <> 'CLOSED' " + filter;
		subQuery[4] = "select PatientID from _FollowupProgress where ((Category='CAT I' AND DateBaselineVisit < '2012-05-18') OR Category='CAT II') AND datediff(curdate(), DateBaselineVisit) >= 144 and Visit5Date is null AND (Visit6Date is  NOT null OR Visit7Date is NOT null OR Visit8Date is NOT null OR Visit9Date is NOT null OR Visit10Date is NOT null) and PatientStatus <> 'CLOSED' " + filter;
		
		//month 5 for all
		mainQuery[4] = "select PatientID from _FollowupProgress where datediff(curdate(), DateBaselineVisit) >= 144 and  Visit5Date is null AND Visit6Date is null AND Visit7Date is null AND Visit8Date is null AND Visit9Date is null AND Visit10Date is null and PatientStatus <> 'CLOSED' " + filter;
		subQuery[4] = "select PatientID from _FollowupProgress where datediff(curdate(), DateBaselineVisit) >= 144 and Visit5Date is null AND (Visit6Date is  NOT null OR Visit7Date is NOT null OR Visit8Date is NOT null OR Visit9Date is NOT null OR Visit10Date is NOT null) and PatientStatus <> 'CLOSED' " + filter;
		
		//month 6 for CAT I - NEW  NEW ADDITION
		mainQuery[5] = "select PatientID from _FollowupProgress where Category='CAT I-NEW' AND datediff(curdate(), DateBaselineVisit) >= 174 and  Visit6Date is null AND Visit7Date is null AND Visit8Date is null AND Visit9Date is null AND Visit10Date is null and PatientStatus <> 'CLOSED' " + filter;
		subQuery[5] = "select PatientID from _FollowupProgress where Category='CAT I-NEW' AND datediff(curdate(), DateBaselineVisit) >= 174 and  Visit6Date is null AND (Visit7Date is NOT null OR Visit8Date is NOT null OR Visit9Date is NOT null OR Visit10Date is NOT null) and PatientStatus <> 'CLOSED' " + filter;
		
		//month 7 for CAT I - OLD and CAT II - OLD
		mainQuery[6] = "select PatientID from _FollowupProgress where (Category='CAT I' OR Category='CAT II') AND datediff(curdate(), DateBaselineVisit) >= 204 and  Visit7Date is null AND Visit8Date is null AND Visit9Date is null AND Visit10Date is null and PatientStatus <> 'CLOSED' " + filter;
		subQuery[6] = "select PatientID from _FollowupProgress where (Category='CAT I' OR Category='CAT II') AND datediff(curdate(), DateBaselineVisit) >= 204 and Visit7Date is null AND (Visit8Date is NOT null OR Visit9Date is NOT null OR Visit10Date is NOT null) and PatientStatus <> 'CLOSED' " + filter;
		
		//month 8 for CAT II NEW NEW ADDITION
		mainQuery[7] = "select PatientID from _FollowupProgress where Category='CAT II-NEW' AND datediff(curdate(), DateBaselineVisit) >= 234 and  Visit8Date is null AND Visit9Date is null AND Visit10Date is null and PatientStatus <> 'CLOSED' " + filter;
		subQuery[7] = "select PatientID from _FollowupProgress where Category='CAT II-NEW' AND datediff(curdate(), DateBaselineVisit) >= 234 and Visit8Date is null AND (Visit9Date is NOT null OR Visit10Date is NOT null) and PatientStatus <> 'CLOSED' " + filter;
		Connection conn = null;
		
		try {
			conn = getReportConnection();
		}
		
		catch(SQLException se) {
			se.printStackTrace();
			
			XmlUtil.createErrorXml("Error getting report data. Please try again!");
		}
		
		
		
		Statement st1 = null; 
		ResultSet rs1 = null;
		
		try {
			st1 = conn.createStatement();
			rs1 = st1.executeQuery(mainQuery[month-1]);
		
		
			data += "PENDING:\n\n";
		
		
			while(rs1.next()) {
				data += rs1.getString(1) + "\n";
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			XmlUtil.createErrorXml("Error accessing database for pending results. Please try again!");
		}
		
		try {
			st1 = conn.createStatement();
			rs1 = st1.executeQuery(subQuery[month-1]);
		
		
			data += "\nNOT DONE:\n\n";
		
		
			while(rs1.next()) {
				data += rs1.getString(1) + "\n";
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			XmlUtil.createErrorXml("Error accessing database for pending results. Please try again!");
		}
		
		Document doc = null;

		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
		} catch (ParserConfigurationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		Element responseNode = doc.createElement(XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement("data");
		Text dataValue = doc.createTextNode(data);
		dataNode.appendChild(dataValue);

		responseNode.appendChild(dataNode);

		doc.appendChild(responseNode);

		xml = XmlUtil.docToString(doc);
		
		return xml;
	}
	
	public Connection getReportConnection() throws SQLException {
		Connection conn = HibernateUtil.util.getSession().connection();
		conn.setCatalog("tbreach_rpt");
		
		return conn;
		
	}
	
	
	public String getTxStartDate (String patientid) throws Exception {
		
		
		String treatmentStartDate = ""; 
		  String rowRecord[] = null;
		  try { 
			
		  
		  rowRecord = ssl.getRowRecord("Encounter", new String[]{"DateEncounterEntered","PID2"}, " where PID1= '" + patientid + "' AND EncounterType='BASELINE'");
		  
			
	
		  }	
		  catch (Exception e1) 
		  { 
			  e1.printStackTrace(); 
		  }
		  
		  if(rowRecord==null || rowRecord.length!=2) {
			 treatmentStartDate = "N/A"; 
			 if(rowRecord==null)
				 System.out.println("null");
			 else
				 System.out.println(rowRecord.length);
		  }
		  
		  else { 
			   
			  treatmentStartDate = rowRecord[0];
		  }
		  
		  return treatmentStartDate;
	}
	
	public boolean isOldRegimen (String treatmentStartDate) throws Exception {
		
		boolean old = false;
		  
		  
			  
			  String[] txSplit = treatmentStartDate.split(" ");
			  String dateSection = txSplit[0];
			  
			  String[] dateSplit = dateSection.split("-");
			  
			  if(dateSplit.length!=3)
				  throw new Exception("Bad Date");
			  
			  int year = Integer.parseInt(dateSplit[0]);
			  int month = Integer.parseInt(dateSplit[1]);
			  int date = Integer.parseInt(dateSplit[2]);
			  
			  GregorianCalendar cutoffCal = new GregorianCalendar();
			  cutoffCal.set(Calendar.YEAR, 2012);
			  cutoffCal.set(Calendar.MONTH, 4);
			  cutoffCal.set(Calendar.DATE, 18);
			  cutoffCal.set(Calendar.HOUR,0);
			  cutoffCal.set(Calendar.MINUTE, 0);
			  cutoffCal.set(Calendar.SECOND, 1);
			  
			  GregorianCalendar nowCal = new GregorianCalendar();
			  nowCal.set(Calendar.YEAR, year);
			  nowCal.set(Calendar.MONTH, month-1);
			  nowCal.set(Calendar.DATE, date);
			  nowCal.set(Calendar.HOUR,0);
			  nowCal.set(Calendar.MINUTE, 0);
			  nowCal.set(Calendar.SECOND, 1);
			  
			  long cutoffTime = cutoffCal.getTimeInMillis();
			  long nowTime = nowCal.getTimeInMillis();
			  
			  if(nowTime>=cutoffTime)
				  return false; //new
			  
			  return true; //old
			  
	
	}*/
	
	@SuppressWarnings("unused")
	private String doRemoteASTMResult() {
		String xml = null;
		boolean insert = false;
		boolean update = false;
		
		/*
		 * MTB DETECTED (HIGH|LOW|MEDIUM|VERY LOW); RIF Resistance (DETECTED|NOT DETECTED|INDETERMINATE)
MTB NOT DETECTED
NO RESULT
ERROR
INVALID
		 */
		
		System.out.println("iside func");
		String patientId = request.getParameter("sampleid");
		String sampleId = request.getParameter("sampleid");
		String mtb = request.getParameter("mtb");
		String systemId = request.getParameter("systemid");
			
		String rif = request.getParameter("rif");
		if(rif!=null && rif.equalsIgnoreCase("null"))
			rif = null;
		
		String rFinal = request.getParameter("final");
		String rPending = request.getParameter("pending");
		String rError = request.getParameter("error");
		String rCorrected = request.getParameter("correction");
		String resultDate = request.getParameter("enddate");
		String errorCode = request.getParameter("errorcode");
		
		String operatorId = request.getParameter("operatorid");//=" + operatorId;// e.g Karachi Xray
		String pcId = request.getParameter("pcid");
		String instrumentSerial = request.getParameter("instserial");
		String moduleId = request.getParameter("moduleid");
		String cartridgeId = request.getParameter("cartrigeid");
		String reagentLotId = request.getParameter("reagentlotid");
		String expDate = request.getParameter("expdate");
		System.out.println("------>" + operatorId);
		String probeResultA = request.getParameter("probea");
		String probeResultB = request.getParameter("probeb");
		String probeResultC = request.getParameter("probec");
		String probeResultD = request.getParameter("probed");
		String probeResultE = request.getParameter("probee");
		String probeResultSPC = request.getParameter("probespc");
		
		String probeCtA = request.getParameter("probeact");
		String probeCtB = request.getParameter("probebct");
		String probeCtC = request.getParameter("probecct");
		String probeCtD = request.getParameter("probedct");
		String probeCtE = request.getParameter("probeect");
		String probeCtSPC = request.getParameter("probespcct");
		
		String probeEndptA = request.getParameter("probeaendpt");
		String probeEndptB = request.getParameter("probebendpt");
		String probeEndptC = request.getParameter("probecendpt");
		String probeEndptD = request.getParameter("probedendpt");
		String probeEndptE = request.getParameter("probeeendpt");
		String probeEndptSPC = request.getParameter("probespcendpt");
		Date resultDateObj = null;
		if(resultDate!=null) {
			System.out.println("handling time");
			String year = resultDate.substring(0,4);
			String month = resultDate.substring(4,6);
			String date = resultDate.substring(6,8);
			String hour = null;
			String minute = null;
			String second = null;
		
			if(resultDate.length()==14) {
				hour = resultDate.substring(8,10);
				minute = resultDate.substring(10,12);
				second = resultDate.substring(12,14);
				
			}
			
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
			cal.set(Calendar.DATE, Integer.parseInt(date));
			if(hour!=null)
				cal.set(Calendar.HOUR, Integer.parseInt(hour));
			else
				cal.set(Calendar.HOUR,0);
			if(minute!=null)
				cal.set(Calendar.MINUTE, Integer.parseInt(minute));
			else
				cal.set(Calendar.MINUTE,0);
			if(second!=null)
				cal.set(Calendar.SECOND, Integer.parseInt(second));
			cal.set(Calendar.SECOND,0);
			
			cal.set(Calendar.MILLISECOND,0);
			
			resultDateObj = cal.getTime();
			System.out.println("TIME" + resultDateObj.getTime());
			
			
			
		}
		
		//if(rPending!=null) {
			
			ssl = new ServerServiceImpl();
			GeneXpertResults[] gxp = null;
			GeneXpertResults gxpNew = null;
//			GeneXpertResultsAuto gxp = null;
			try {
				gxp = ssl.findGeneXpertResults(sampleId,patientId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println ("Here!");
			if(gxp==null || gxp.length==0) {
				//System.out.println("NOT FOUND");
				
				GeneXpertResults gxpU = null;
				try {
					gxpU = createGeneXpertResults(patientId,sampleId, mtb, rif, resultDate,instrumentSerial,moduleId,cartridgeId,reagentLotId,expDate, operatorId,pcId,probeResultA,probeResultB,probeResultC,probeResultD,probeResultE,probeResultSPC,probeCtA,probeCtB,probeCtC,probeCtD,probeCtE,probeCtSPC,probeEndptA,probeEndptB,probeEndptC,probeEndptD,probeEndptE,probeEndptSPC,errorCode, systemId);

					//ssl.updateGeneXpertResultsAuto(gxp, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
					ssl.saveGeneXpertResults(gxpU);//, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
					return XmlUtil.createSuccessXml();
				} catch (Exception e) {
					e.printStackTrace();
					return XmlUtil.createErrorXml("Could not save data for Sample ID " + gxpU.getSputumTestId());
				}
				
			}
			
			else {
				
				for(int i=0;i<gxp.length;i++) {
					if(gxp[i].getDateTested()!=null) {
					System.out.println("STORED TIME:" + gxp[i].getDateTested().getTime());
						if(resultDateObj.getTime()== gxp[i].getDateTested().getTime()){
								gxpNew = gxp[i];
								update = true;
								//System.out.println("date match");
								break;
						}
						
					}
				}
		
				if(!update) {
					for(int i=0;i<gxp.length;i++) {
						if(gxp[i].getGeneXpertResult()==null){//F form filled
							update = true;
							gxpNew = gxp[i];
							System.out.println("ID match null result");
							break;
						
						}
					}
				}
				
			}
			

			//set mtb
			if(update==true) {
				if(mtb != null) {
					//System.out.println("----MTB----" + mtb);
					int index = mtb.indexOf("MTB DETECTED");
					String mtbBurden = null;
					if(index!=-1) {
						mtbBurden = mtb.substring(index+"MTB DETECTED".length()+1);
					
						gxpNew.setGeneXpertResult("MTB DETECTED");
						gxpNew.setIsPositive(new Boolean(true));
						gxpNew.setMtbBurden(mtbBurden);
						gxpNew.setErrorCode(0);
					}
				
					else {
						index = mtb.indexOf("MTB NOT DETECTED");
						//System.out.println("mtb :" + index + " " + mtb);
						if(index!=-1) {
							gxpNew.setGeneXpertResult("MTB NOT DETECTED");
							gxpNew.setIsPositive(new Boolean(false));
							mtbBurden = null;
					}
					
					else {
						gxpNew.setGeneXpertResult(mtb);
						mtbBurden = null;
					}
				}
			}
			
			if(rif != null) {
				int index = rif.indexOf("NOT DETECTED");
				String rifResult = null;
				if(index!=-1) {
					rifResult = "NOT DETECTED";
				}
				
				else if(rif.indexOf("DETECTED")!=-1){
					rifResult = "DETECTED";
				}
				
				else {
					rifResult = rif.toUpperCase();
				}
				
				gxpNew.setDrugResistance(rifResult);
			}
			
			
			gxpNew.setDateTested(resultDateObj);
			gxpNew.setInstrumentSerial(instrumentSerial);
			gxpNew.setModuleId(moduleId);
			gxpNew.setReagentLotId(reagentLotId);
			gxpNew.setExpDate(expDate);
			gxpNew.setCartridgeId(cartridgeId);
			gxpNew.setPcId(pcId);
			gxpNew.setOperatorId(operatorId);
			if(errorCode!=null)
				gxpNew.setErrorCode(Integer.parseInt(errorCode));
			//Probes
			gxpNew.setProbeResultA(probeResultA);
			gxpNew.setProbeResultB(probeResultB);
			gxpNew.setProbeResultC(probeResultC);
			gxpNew.setProbeResultD(probeResultD);
			gxpNew.setProbeResultE(probeResultE);
			gxpNew.setProbeResultSPC(probeResultSPC);
			
			if(probeCtA!=null)
				gxpNew.setProbeCtA(Double.parseDouble(probeCtA));
			if(probeCtB!=null)
				gxpNew.setProbeCtB(Double.parseDouble(probeCtB));
			if(probeCtC!=null)
				gxpNew.setProbeCtC(Double.parseDouble(probeCtC));
			if(probeCtD!=null)
				gxpNew.setProbeCtD(Double.parseDouble(probeCtD));
			if(probeCtE!=null)
				gxpNew.setProbeCtE(Double.parseDouble(probeCtE));
			if(probeCtSPC!=null)
				gxpNew.setProbeCtSPC(Double.parseDouble(probeCtSPC));
			
			if(probeEndptA!=null)
				gxpNew.setProbeEndptA(Double.parseDouble(probeEndptA));
			if(probeEndptB!=null)
				gxpNew.setProbeEndptB(Double.parseDouble(probeEndptB));
			if(probeEndptC!=null)
				gxpNew.setProbeEndptC(Double.parseDouble(probeEndptC));
			if(probeEndptD!=null)
				gxpNew.setProbeEndptD(Double.parseDouble(probeEndptD));
			if(probeEndptE!=null)
				gxpNew.setProbeEndptE(Double.parseDouble(probeEndptE));
			if(probeEndptSPC!=null)
				gxpNew.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));
			
			 
			try {
				//ssl.updateGeneXpertResultsAuto(gxp, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
				ssl.updateGeneXpertResults(gxpNew, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	
			
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}
	
	public GeneXpertResults createGeneXpertResults(String patientId, String SampleID, String mtb, String rif, String resultDate,String instrumentSerial,String moduleId,String cartridgeId,String reagentLotId,String expDate,String operatorId,String pcId,String probeResultA,String probeResultB,String probeResultC,String probeResultD,String probeResultE,String probeResultSPC,String probeCtA,String probeCtB,String probeCtC,String probeCtD,String probeCtE,String probeCtSPC,String probeEndptA,String probeEndptB,String probeEndptC,String probeEndptD,String probeEndptE,String probeEndptSPC,String errorCode, String systemId) {
		GeneXpertResults gxp = new GeneXpertResults();
		gxp.setSputumTestId(SampleID);
		gxp.setPatientId(patientId);
		gxp.setLaboratoryId(systemId);
		
		
		if(rif!=null && rif.equalsIgnoreCase("null"))
			rif = null;
		
		if(mtb != null) {
			//System.out.println("----MTB----" + mtb);
			int index = mtb.indexOf("MTB DETECTED");
			String mtbBurden = null;
			if(index!=-1) {
				mtbBurden = mtb.substring(index+"MTB DETECTED".length()+1);
				
				gxp.setGeneXpertResult("MTB DETECTED");
				gxp.setIsPositive(new Boolean(true));
				gxp.setMtbBurden(mtbBurden);
				gxp.setErrorCode(0);
			}
			
			else {
				index = mtb.indexOf("MTB NOT DETECTED");
				//System.out.println("mtb :" + index + " " + mtb);
				if(index!=-1) {
					gxp.setGeneXpertResult("MTB NOT DETECTED");
					gxp.setIsPositive(new Boolean(false));
					mtbBurden = null;
				}
				
				else {
					gxp.setGeneXpertResult(mtb);
					mtbBurden = null;
				}
			}
		}
		
		if(rif != null) {
			int index = rif.indexOf("NOT DETECTED");
			String rifResult = null;
			if(index!=-1) {
				rifResult = "NOT DETECTED";
			}
			
			else if(rif.indexOf("DETECTED")!=-1){
				rifResult = "DETECTED";
			}
			
			else {
				rifResult = rif.toUpperCase();
			}
			
			gxp.setDrugResistance(rifResult);
		}
		
		if(resultDate!=null) {
			String year = resultDate.substring(0,4);
			String month = resultDate.substring(4,6);
			String date = resultDate.substring(6,8);
			String hour = null;
			String minute = null;
			String second = null;
		
			if(resultDate.length()==14) {
				hour = resultDate.substring(8,10);
				minute = resultDate.substring(10,12);
				second = resultDate.substring(12,14);
				
			}
			
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
			cal.set(Calendar.DATE, Integer.parseInt(date));
			if(hour!=null)
				cal.set(Calendar.HOUR, Integer.parseInt(hour));
			else
				cal.set(Calendar.HOUR,0);
			if(minute!=null)
				cal.set(Calendar.MINUTE, Integer.parseInt(minute));
			cal.set(Calendar.MINUTE,0);
			if(second!=null)
				cal.set(Calendar.SECOND, Integer.parseInt(second));
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			gxp.setDateTested(cal.getTime());
			
		}
		
		gxp.setInstrumentSerial(instrumentSerial);
		gxp.setModuleId(moduleId);
		gxp.setReagentLotId(reagentLotId);
		gxp.setExpDate(expDate);
		gxp.setCartridgeId(cartridgeId);
		gxp.setPcId(pcId);
		gxp.setOperatorId(operatorId);
		if(errorCode!=null)
			gxp.setErrorCode(Integer.parseInt(errorCode));
		
		
		//Probes
		gxp.setProbeResultA(probeResultA);
		gxp.setProbeResultB(probeResultB);
		gxp.setProbeResultC(probeResultC);
		gxp.setProbeResultD(probeResultD);
		gxp.setProbeResultE(probeResultE);
		gxp.setProbeResultSPC(probeResultSPC);
		
		if(probeCtA!=null)
			gxp.setProbeCtA(Double.parseDouble(probeCtA));
		if(probeCtB!=null)
			gxp.setProbeCtB(Double.parseDouble(probeCtB));
		if(probeCtC!=null)
			gxp.setProbeCtC(Double.parseDouble(probeCtC));
		if(probeCtD!=null)
			gxp.setProbeCtD(Double.parseDouble(probeCtD));
		if(probeCtE!=null)
			gxp.setProbeCtE(Double.parseDouble(probeCtE));
		if(probeCtSPC!=null)
			gxp.setProbeCtSPC(Double.parseDouble(probeCtSPC));
		
		if(probeEndptA!=null)
			gxp.setProbeEndptA(Double.parseDouble(probeEndptA));
		if(probeEndptB!=null)
			gxp.setProbeEndptB(Double.parseDouble(probeEndptB));
		if(probeEndptC!=null)
			gxp.setProbeEndptC(Double.parseDouble(probeEndptC));
		if(probeEndptD!=null)
			gxp.setProbeEndptD(Double.parseDouble(probeEndptD));
		if(probeEndptE!=null)
			gxp.setProbeEndptE(Double.parseDouble(probeEndptE));
		if(probeEndptSPC!=null)
			gxp.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));
		
		return gxp;
	}
	
}
