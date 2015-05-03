package org.irdresearch.tbreach.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


import org.irdresearch.tbreach.server.ServerServiceImpl;



import org.irdresearch.tbreach.shared.model.GeneXpertResults;
import org.irdresearch.tbreach.shared.model.GeneXpertResultsUnlinked;


import org.irdresearch.tbreach.shared.model.Users;


import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;


public class MobileService extends HttpServlet {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 962734804285479133L;
	private HttpServletRequest request;
	private ServerServiceImpl ssl;
	private Date encounterStartDate;
	private Date encounterEndDate;

	public MobileService() {
		ssl = new ServerServiceImpl();
		
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		System.out.println ("Doing post on Server..");
		String xmlResponse = null;
		PrintWriter out = resp.getWriter ();
		request = req;
		String reqType = request.getParameter("type");
		System.out.println("----->" + reqType);
		
		String id = request.getParameter("id");
		if(id!=null && id.length()>0 && id.charAt(0)=='P') {
			out.println(XmlUtil.createErrorXml("Phone update karain aur dobara form bharain."));
		}

		Boolean uCheck = null;
		if(request.getParameter("chwid")!=null) 
		{
			try {
				uCheck = ssl.exists("Users", "where PID = '" + request.getParameter("chwid").toUpperCase() + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(uCheck.booleanValue()!=true){
				out.println(XmlUtil.createErrorXml("Aap ne ghalat CHW ID ya Monitor ID darj kia hai. Sahih ID enter karain aur dobara koshish karain"));
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
				out.println(XmlUtil.createErrorXml("Aap ne ghalat CHW ID ya Monitor ID darj kia hai. Sahih ID enter karain aur dobara koshish karain"));
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
				out.println(XmlUtil.createErrorXml("Aap ne ghalat GP ID darj kia hai. Sahih ID enter karain aur dobara koshish karain"));
			}
		}
		
		if (reqType.equals(RequestType.LOGIN)) {
			out.println(doLogin());
		}

		
		
		else if(reqType.equals(RequestType.REMOTE_ASTM_RESULT)) {
			out.println( doRemoteASTMResult());
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
				out.println(XmlUtil
						.createErrorXml("Invalid Date Format. Please contact technical support!"));
			}
			
			if(!reqType.equals(RequestType.DFR) && !reqType.equals(RequestType.SUSPECT_ID) && !reqType.equals(RequestType.DOTS_ASSIGN) && !reqType.equals(RequestType.REMOTE_ASTM_RESULT)) {
				Boolean closed = null;
				if(request.getParameter("id")!=null) {
				try {
					closed = ModelUtil.isPatientClosed(request.getParameter("id"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					out.println( XmlUtil.createErrorXml("Error tracking patient status. Please try again!"));
				}
				
				if(closed==null) {
					out.println( XmlUtil.createErrorXml("Error tracking patient status. Please try again!"));
				}
				
				if(closed.booleanValue()==true) {
					out.println( XmlUtil.createErrorXml("Is Patient ka End of Follow-up form bhar diya gaya hai. In ka koi bhi form bhara nahin ja sakta!"));
				}
				
			
				}
			}
		}

		out.println( xmlResponse);
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp)
	{
		/* not implemented */
	
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


	
	//////
	private String doRemoteASTMResult() {
		String xml = null;
		/*
		 * MTB DETECTED (HIGH|LOW|MEDIUM|VERY LOW); RIF Resistance (DETECTED|NOT DETECTED|INDETERMINATE)
MTB NOT DETECTED
NO RESULT
ERROR
INVALID
		 */
		
		String sampleId = request.getParameter("sampleid");
		String mtb = request.getParameter("mtb");
		String rif = request.getParameter("rif");
		
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
		
		//if(rPending!=null) {
			
			ssl = new ServerServiceImpl();
			GeneXpertResults gxpO = null;
			//GeneXpertResultsAuto gxp = null;
			try {
				gxpO = ssl.findGeneXpertResults(sampleId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(gxpO==null) {
				System.out.println("NOT FOUND");
				GeneXpertResultsUnlinked gxpU = createGeneXpertResultsUnlinked(mtb, rif, resultDate,instrumentSerial,moduleId,cartridgeId,reagentLotId,pcId,probeResultA,probeResultB,probeResultC,probeResultD,probeResultE,probeResultSPC,probeCtA,probeCtB,probeCtC,probeCtD,probeCtE,probeCtSPC,probeEndptA,probeEndptB,probeEndptC,probeEndptD,probeEndptE,probeEndptSPC);
				try {
					//ssl.updateGeneXpertResultsAuto(gxp, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
					ssl.saveGeneXpertResultsUnlinked(gxpU);//, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return XmlUtil.createErrorXml("Could not save data for Sample ID " + gxpU.getSputumTestId());
				}
				
				
			}
			
			/*gxp = new GeneXpertResultsAuto();
			
			gxp.setCollectedBy(gxpO.getCollectedBy());
			gxp.setDateSubmitted(gxpO.getDateSubmitted());
			gxp.setPatientId(gxpO.getPatientId());
			gxp.setRemarks(gxpO.getRemarks());
			gxp.setSputumTestId("" + gxpO.getSputumTestId());*/
			

			//set mtb
			
			if(mtb != null) {
				int index = mtb.indexOf("MTB DETECTED");
				String mtbBurden = null;
				if(index!=-1) {
					mtbBurden = mtb.substring(index+"MTB DETECTED".length()+1);
					
					gxpO.setGeneXpertResult("POSITIVE");
					gxpO.setIsPositive(new Boolean(true));
					gxpO.setMtbBurden(mtbBurden);
					gxpO.setErrorCode(0);
				}
				
				else {
					index = mtb.indexOf("MTB NOT DETECTED");
					
					if(index!=1) {
						gxpO.setGeneXpertResult("NEGATIVE");
						gxpO.setIsPositive(new Boolean(false));
					}
					
					else {
						gxpO.setGeneXpertResult(mtb);
					}
				}
			}
			
			if(rif != null) {
				int index = rif.indexOf("NOT DETECTED");
				String rifResult = null;
				if(index!=-1) {
					rifResult = "RIFAMPICIN SUSCEPTIBLE";
				}
				
				else if(rif.indexOf("DETECTED")!=-1){
					rifResult = "RIFAMPICIN RESISTANT";
				}
				
				else {
					rifResult = rif.toUpperCase();
				}
				
				gxpO.setDrugResistance(rifResult);
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
				cal.set(cal.YEAR, Integer.parseInt(year));
				cal.set(cal.MONTH, Integer.parseInt(month)-1);
				cal.set(cal.DATE, Integer.parseInt(date));
				if(hour!=null)
					cal.set(cal.HOUR, Integer.parseInt(hour));
				if(minute!=null)
					cal.set(cal.MINUTE, Integer.parseInt(minute));
				if(second!=null)
					cal.set(cal.SECOND, Integer.parseInt(second));
				
				gxpO.setDateTested(cal.getTime());
				
			}
			
			gxpO.setInstrumentSerial(instrumentSerial);
			gxpO.setModuleId(moduleId);
			gxpO.setReagentLotId(reagentLotId);
			gxpO.setCartridgeId(cartridgeId);
			gxpO.setPcId(pcId);
			
			if(errorCode!=null) {
				gxpO.setErrorCode(Integer.parseInt(errorCode));
			}
			
			//Probes
			gxpO.setProbeResultA(probeResultA);
			gxpO.setProbeResultB(probeResultB);
			gxpO.setProbeResultC(probeResultC);
			gxpO.setProbeResultD(probeResultD);
			gxpO.setProbeResultE(probeResultE);
			gxpO.setProbeResultSPC(probeResultSPC);
			
			if(probeCtA!=null)
				gxpO.setProbeCtA(Double.parseDouble(probeCtA));
			if(probeCtB!=null)
				gxpO.setProbeCtB(Double.parseDouble(probeCtB));
			if(probeCtC!=null)
				gxpO.setProbeCtC(Double.parseDouble(probeCtC));
			if(probeCtD!=null)
				gxpO.setProbeCtD(Double.parseDouble(probeCtD));
			if(probeCtE!=null)
				gxpO.setProbeCtE(Double.parseDouble(probeCtE));
			if(probeCtSPC!=null)
				gxpO.setProbeCtSPC(Double.parseDouble(probeCtSPC));
			
			if(probeEndptA!=null)
				gxpO.setProbeEndptA(Double.parseDouble(probeEndptA));
			if(probeEndptB!=null)
				gxpO.setProbeEndptB(Double.parseDouble(probeEndptB));
			if(probeEndptC!=null)
				gxpO.setProbeEndptC(Double.parseDouble(probeEndptC));
			if(probeEndptD!=null)
				gxpO.setProbeEndptD(Double.parseDouble(probeEndptD));
			if(probeEndptE!=null)
				gxpO.setProbeEndptE(Double.parseDouble(probeEndptE));
			if(probeEndptSPC!=null)
				gxpO.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));
			/*
			 * String operatorId = request.getParameter("operatorid");//=" + operatorId;// e.g Karachi Xray
		String pcId = request.getParameter("pcid");
		String instrumentSerial = request.getParameter("instserial");
		String moduleId = request.getParameter("moduleid");
		String cartridgeId = request.getParameter("cartrigeid");
		String reagentLotId = request.getParameter("reagentlotid");
			 */
			try {
				//ssl.updateGeneXpertResultsAuto(gxp, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
				ssl.saveGeneXpertResults(gxpO);//, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return XmlUtil.createErrorXml("Could not save data for Sample ID " + gxpO.getSputumTestId());
			}
			
			
		//}
		/*postParams += "type=astmresult";
		postParams += "&sampleid=" + sampleId;
		postParams += "&mtb=" + mtbResult;
		postParams += "&rif=" + rifResult;
		
		if(isFinal())
			postParams += "&final=yes";
		if(isPending())
			postParams += "&pending=yes";
		if(isError())
			postParams += "&error=yes";
		if(isFinal())
			postParams += "&final=yes";
		
		return postParams;*/
		
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}
	
	public GeneXpertResultsUnlinked createGeneXpertResultsUnlinked(String mtb, String rif, String resultDate,String instrumentSerial,String moduleId,String cartridgeId,String reagentLotId,String pcId,String probeResultA,String probeResultB,String probeResultC,String probeResultD,String probeResultE,String probeResultSPC,String probeCtA,String probeCtB,String probeCtC,String probeCtD,String probeCtE,String probeCtSPC,String probeEndptA,String probeEndptB,String probeEndptC,String probeEndptD,String probeEndptE,String probeEndptSPC) {
		GeneXpertResultsUnlinked gxp = new GeneXpertResultsUnlinked();
		if(mtb != null) {
			int index = mtb.indexOf("MTB DETECTED");
			String mtbBurden = null;
			if(index!=-1) {
				mtbBurden = mtb.substring(index+"MTB DETECTED".length()+1);
				
				gxp.setGeneXpertResult("POSITIVE");
				gxp.setIsPositive(new Boolean(true));
				gxp.setMtbBurden(mtbBurden);
				gxp.setErrorCode(0);
			}
			
			else {
				index = mtb.indexOf("MTB NOT DETECTED");
				
				if(index!=1) {
					gxp.setGeneXpertResult("NEGATIVE");
					gxp.setIsPositive(new Boolean(false));
				}
				
				else {
					gxp.setGeneXpertResult(mtb);
				}
			}
		}
		
		if(rif != null) {
			int index = rif.indexOf("NOT DETECTED");
			String rifResult = null;
			if(index!=-1) {
				rifResult = "RIFAMPICIN SUSCEPTIBLE";
			}
			
			else if(rif.indexOf("DETECTED")!=-1){
				rifResult = "RIFAMPICIN RESISTANT";
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
			cal.set(cal.YEAR, Integer.parseInt(year));
			cal.set(cal.MONTH, Integer.parseInt(month)-1);
			cal.set(cal.DATE, Integer.parseInt(date));
			if(hour!=null)
				cal.set(cal.HOUR, Integer.parseInt(hour));
			if(minute!=null)
				cal.set(cal.MINUTE, Integer.parseInt(minute));
			if(second!=null)
				cal.set(cal.SECOND, Integer.parseInt(second));
			
			gxp.setDateTested(cal.getTime());
			
		}
		
		gxp.setInstrumentSerial(instrumentSerial);
		gxp.setModuleId(moduleId);
		gxp.setReagentLotId(reagentLotId);
		gxp.setCartridgeId(cartridgeId);
		gxp.setPcId(pcId);
		
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
