package com.ihsinformatics.xpertsmsweb.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.ihsinformatics.xpertsmsweb.server.util.DateTimeUtil;
import com.ihsinformatics.xpertsmsweb.server.util.XmlUtil;
import com.ihsinformatics.xpertsmsweb.shared.VersionUtil;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.model.Encounter;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterId;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;

//import com.ihsinformatics.xpertsmsweb.server.XmlUtil;

public class EventHandler extends HttpServlet {

	/**
     * 
     */
	private static final long serialVersionUID = -1089336095687159274L;

	private static final Properties prop = new Properties();
	private static ServerServiceImpl ssl = new ServerServiceImpl();
	private static EventHandler service = new EventHandler();
	private static String backupPostUrl;
	private HttpServletRequest request;

	public EventHandler() {
	}

	/**
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		try {
			if (SmsTarseel.instantiate()) {
				System.out.println("SmsTarseel service started.");
			} else {
				System.out.println("ERROR! Unable to instantiate SmsTarseel.");
			}
			if (SmsProcesserService.instantiate()) {
				System.out.println("Sms Processer service started.");
			} else {
				System.out
						.println("ERROR! Unable to instantiate SMS Processor.");
			}
			System.out.println("Locating Backup post URL property......");
			prop.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("xpertsmsweb.properties"));
			backupPostUrl = prop.getProperty("backup.post.url");
			if (backupPostUrl == null)
				System.out
						.println("Warning! Backup post URL property not found.");
			else if (backupPostUrl.equals(""))
				System.out
						.println("Warning! Backup post URL property not found.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static EventHandler getService() {
		return EventHandler.service;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String handleEvent(final HttpServletRequest request) {
		setRequest(request);
		String xmlResponse = null;
		/* Try to match version if present */
		try {
			String version = request.getParameter("version");
			if (version != null) {
				VersionUtil clientVersion = new VersionUtil();
				clientVersion.parseVersion(version);
				if (!clientVersion.isCompatible(XSMS.getVersion())) {
					System.out.println("Client version " + version
							+ " is not compatible with version on server "
							+ XSMS.getVersion().toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String reqType = request.getParameter("type");
		if (reqType == null) {
			xmlResponse = XmlUtil.createErrorXml("ERROR: Request type is null");
		} else {
			if (reqType.equals(RequestType.REMOTE_ASTM_RESULT)) {
				xmlResponse = doRemoteASTMResult();
			}
		}
		// If the response was successful, make another request to backup URL if
		// available
		if (backupPostUrl != null) {
			System.out.println("Posting request to backup server:"
					+ request.getParameter("type"));
			String response = postToBackup(request);
			System.out.println("Response from backup server: " + response);
		} else {
			System.out
					.println("Warning! No backup post URL defiend. Skipping backup...s");
		}
		return xmlResponse;
	}

	private String doRemoteASTMResult() {
		String xml = null;
		boolean update = false;
		/*
		 * MTB DETECTED (HIGH|LOW|MEDIUM|VERY LOW); RIF Resistance (DETECTED|NOT
		 * DETECTED|INDETERMINATE) MTB NOT DETECTED NO RESULT ERROR INVALID
		 */
		String patientId = request.getParameter("pid");
		String sampleId = request.getParameter("sampleid");
		if (patientId == null || patientId.equalsIgnoreCase("null"))
			return XmlUtil.createErrorXml("Cannot save without Patient ID");
		if (sampleId == null || sampleId.equalsIgnoreCase("null"))
			return XmlUtil.createErrorXml("Cannot save without Sample ID");
		String mtb = request.getParameter("mtb");
		if (mtb != null && mtb.equalsIgnoreCase("null"))
			mtb = null;
		String systemId = request.getParameter("systemid");
		String rif = request.getParameter("rif");
		if (rif != null && rif.equalsIgnoreCase("null"))
			rif = null;

		// String rFinal = request.getParameter("final");
		// String rPending = request.getParameter("pending");
		// String rError = request.getParameter("error");
		// String rCorrected = request.getParameter("correction");
		String resultDateStr = request.getParameter("enddate");
		String errorCode = request.getParameter("errorcode");
		String errorNotes = request.getParameter("errornotes");
		String notes = request.getParameter("notes");

		String operatorId = request.getParameter("operatorid");
		String pcId = request.getParameter("pcid");
		String hostId = request.getParameter("receiverid");
		String instrumentSerial = request.getParameter("instserial");
		String moduleId = request.getParameter("moduleid");
		String cartridgeId = request.getParameter("cartrigeid");
		String reagentLotId = request.getParameter("reagentlotid");
		String expDateStr = request.getParameter("expdate");

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
		System.out.println("DATE/TIME: " + resultDateStr);

		GeneXpertResults[] gxp = null;
		GeneXpertResults gxpNew = null;
		try {
			gxp = ssl.findGeneXpertResults(sampleId, patientId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (gxp == null || gxp.length == 0) {
			GeneXpertResults gxpU = null;
			try {
				gxpU = createGeneXpertResults(patientId, sampleId, mtb, rif,
						parseDate(resultDateStr), instrumentSerial, moduleId,
						cartridgeId, reagentLotId, parseDate(expDateStr),
						operatorId, pcId, hostId, probeResultA, probeResultB,
						probeResultC, probeResultD, probeResultE,
						probeResultSPC, probeCtA, probeCtB, probeCtC, probeCtD,
						probeCtE, probeCtSPC, probeEndptA, probeEndptB,
						probeEndptC, probeEndptD, probeEndptE, probeEndptSPC,
						errorCode, errorNotes, notes, systemId);
				gxpU.setDateSubmitted(new Date());
				ssl.saveGeneXpertResults(gxpU);
				return XmlUtil.createSuccessXml();
			} catch (Exception e) {
				e.printStackTrace();
				return XmlUtil
						.createErrorXml("Could not save data for Sample ID "
								+ gxpU.getSputumTestId());
			}
		} else {
			for (int i = 0; i < gxp.length; i++) {
				if (gxp[i].getDateTested() != null) {
					System.out.println("STORED TIME:"
							+ gxp[i].getDateTested().getTime());
					if (parseDate(resultDateStr).getTime() == gxp[i]
							.getDateTested().getTime()) {
						gxpNew = gxp[i];
						update = true;
						break;
					}
				}
			}
			if (!update) {
				for (int i = 0; i < gxp.length; i++) {
					if (gxp[i].getGeneXpertResult() == null || "null".equalsIgnoreCase(gxp[i].getGeneXpertResult())) {// F form filled
						update = true;
						gxpNew = gxp[i];
						System.out.println("ID match null result");
						break;
					}
				}
			}
		}
		// set mtb
		if (update == true) {
			if (mtb != null) {
				// System.out.println("----MTB----" + mtb);
				int index = mtb.indexOf("MTB DETECTED");
				String mtbBurden = null;
				if (index != -1) {
					mtbBurden = mtb.substring(index + "MTB DETECTED".length()
							+ 1);
					gxpNew.setGeneXpertResult("MTB DETECTED");
					gxpNew.setIsPositive(new Boolean(true));
					gxpNew.setMtbBurden(mtbBurden);
					gxpNew.setErrorCode(0);
				}

				else {
					index = mtb.indexOf("MTB NOT DETECTED");
					// System.out.println("mtb :" + index + " " + mtb);
					if (index != -1) {
						gxpNew.setGeneXpertResult("MTB NOT DETECTED");
						gxpNew.setIsPositive(new Boolean(false));
						mtbBurden = null;
					} else {
						gxpNew.setGeneXpertResult(mtb);
						mtbBurden = null;
					}
				}
			}

			if (rif != null) {
				int index = rif.indexOf("NOT DETECTED");
				String rifResult = null;
				if (index != -1)
					rifResult = "NOT DETECTED";
				else if (rif.indexOf("DETECTED") != -1)
					rifResult = "DETECTED";
				else
					rifResult = rif.toUpperCase();
				gxpNew.setDrugResistance(rifResult);
			}
			gxpNew.setDateSubmitted(new Date());
			gxpNew.setDateTested(parseDate(resultDateStr));
			gxpNew.setInstrumentSerial(instrumentSerial);
			gxpNew.setModuleId(moduleId);
			gxpNew.setReagentLotId(reagentLotId);
			gxpNew.setCartridgeExpiryDate(parseDate(expDateStr));
			gxpNew.setCartridgeId(cartridgeId);
			gxpNew.setPcId(pcId);
			gxpNew.setHostId(hostId);
			gxpNew.setOperatorId(operatorId);
			String remarks = "";
			if (errorCode != null) {
				gxpNew.setErrorCode(Integer.parseInt(errorCode));
				if (!"".equals(errorNotes) && !"null".equalsIgnoreCase(errorNotes)) {
					remarks += errorNotes + ". ";
				}
			}
			if (!"".equals(notes)) {
				remarks += notes;
			}
			gxpNew.setRemarks(remarks);
			// Probes
			gxpNew.setProbeResultA(probeResultA);
			gxpNew.setProbeResultB(probeResultB);
			gxpNew.setProbeResultC(probeResultC);
			gxpNew.setProbeResultD(probeResultD);
			gxpNew.setProbeResultE(probeResultE);
			gxpNew.setProbeResultSPC(probeResultSPC);
			if (probeCtA != null)
				gxpNew.setProbeCtA(Double.parseDouble(probeCtA));
			if (probeCtB != null)
				gxpNew.setProbeCtB(Double.parseDouble(probeCtB));
			if (probeCtC != null)
				gxpNew.setProbeCtC(Double.parseDouble(probeCtC));
			if (probeCtD != null)
				gxpNew.setProbeCtD(Double.parseDouble(probeCtD));
			if (probeCtE != null)
				gxpNew.setProbeCtE(Double.parseDouble(probeCtE));
			if (probeCtSPC != null)
				gxpNew.setProbeCtSPC(Double.parseDouble(probeCtSPC));
			if (probeEndptA != null)
				gxpNew.setProbeEndptA(Double.parseDouble(probeEndptA));
			if (probeEndptB != null)
				gxpNew.setProbeEndptB(Double.parseDouble(probeEndptB));
			if (probeEndptC != null)
				gxpNew.setProbeEndptC(Double.parseDouble(probeEndptC));
			if (probeEndptD != null)
				gxpNew.setProbeEndptD(Double.parseDouble(probeEndptD));
			if (probeEndptE != null)
				gxpNew.setProbeEndptE(Double.parseDouble(probeEndptE));
			if (probeEndptSPC != null)
				gxpNew.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));

			try {
				ssl.updateGeneXpertResults(gxpNew, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		xml = XmlUtil.createSuccessXml();
		return xml;
	}

	private Date parseDate(String dateStr) {
		Date dateObj = null;
		if (dateStr != null) {
			try {
				if (dateStr.length() > 10)
					dateObj = DateTimeUtil.getDateFromString(dateStr,
							DateTimeUtil.SQL_DATETIME);
				else
					dateObj = DateTimeUtil.getDateFromString(dateStr,
							DateTimeUtil.SQL_DATE);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateObj;
	}

	public GeneXpertResults createGeneXpertResults(String patientId,
			String sampleId, String mtb, String rif, Date resultDate,
			String instrumentSerial, String moduleId, String cartridgeId,
			String reagentLotId, Date expDate, String operatorId, String pcId, String hostId,
			String probeResultA, String probeResultB, String probeResultC,
			String probeResultD, String probeResultE, String probeResultSPC,
			String probeCtA, String probeCtB, String probeCtC, String probeCtD,
			String probeCtE, String probeCtSPC, String probeEndptA,
			String probeEndptB, String probeEndptC, String probeEndptD,
			String probeEndptE, String probeEndptSPC, String errorCode,
			String errorNotes, String notes, String systemId) {
		GeneXpertResults gxp = new GeneXpertResults();
		gxp.setSputumTestId(sampleId);
		gxp.setPatientId(patientId);
		gxp.setLaboratoryId(systemId);

		if (rif != null && rif.equalsIgnoreCase("null"))
			rif = null;

		if (mtb != null) {
			// System.out.println("----MTB----" + mtb);
			int index = mtb.indexOf("MTB DETECTED");
			String mtbBurden = null;
			if (index != -1) {
				mtbBurden = mtb.substring(index + "MTB DETECTED".length() + 1);
				gxp.setGeneXpertResult("MTB DETECTED");
				gxp.setIsPositive(new Boolean(true));
				gxp.setMtbBurden(mtbBurden);
				gxp.setErrorCode(0);
			}

			else {
				index = mtb.indexOf("MTB NOT DETECTED");
				// System.out.println("mtb :" + index + " " + mtb);
				if (index != -1) {
					gxp.setGeneXpertResult("MTB NOT DETECTED");
					gxp.setIsPositive(new Boolean(false));
					mtbBurden = null;
				} else {
					gxp.setGeneXpertResult(mtb);
					mtbBurden = null;
				}
			}
		}

		if (rif != null) {
			int index = rif.indexOf("NOT DETECTED");
			String rifResult = null;
			if (index != -1) {
				rifResult = "NOT DETECTED";
			}

			else if (rif.indexOf("DETECTED") != -1) {
				rifResult = "DETECTED";
			}

			else {
				rifResult = rif.toUpperCase();
			}

			gxp.setDrugResistance(rifResult);
		}

		gxp.setDateTested(resultDate);
		gxp.setInstrumentSerial(instrumentSerial);
		gxp.setModuleId(moduleId);
		gxp.setReagentLotId(reagentLotId);
		gxp.setCartridgeExpiryDate(expDate);
		gxp.setCartridgeId(cartridgeId);
		gxp.setPcId(pcId);
		gxp.setHostId(hostId);
		gxp.setOperatorId(operatorId);
		String remarks = "";
		if (errorCode != null) {
			gxp.setErrorCode(Integer.parseInt(errorCode));
			if (!"".equals(errorNotes)) {
				remarks += errorNotes + ". ";
			}
		}
		if (!("".equals(notes) || "null".equalsIgnoreCase(notes))) {
			remarks += notes;
		}
		gxp.setRemarks(remarks);
		// Probes
		gxp.setProbeResultA(probeResultA);
		gxp.setProbeResultB(probeResultB);
		gxp.setProbeResultC(probeResultC);
		gxp.setProbeResultD(probeResultD);
		gxp.setProbeResultE(probeResultE);
		gxp.setProbeResultSPC(probeResultSPC);

		if (probeCtA != null)
			gxp.setProbeCtA(Double.parseDouble(probeCtA));
		if (probeCtB != null)
			gxp.setProbeCtB(Double.parseDouble(probeCtB));
		if (probeCtC != null)
			gxp.setProbeCtC(Double.parseDouble(probeCtC));
		if (probeCtD != null)
			gxp.setProbeCtD(Double.parseDouble(probeCtD));
		if (probeCtE != null)
			gxp.setProbeCtE(Double.parseDouble(probeCtE));
		if (probeCtSPC != null)
			gxp.setProbeCtSPC(Double.parseDouble(probeCtSPC));

		if (probeEndptA != null)
			gxp.setProbeEndptA(Double.parseDouble(probeEndptA));
		if (probeEndptB != null)
			gxp.setProbeEndptB(Double.parseDouble(probeEndptB));
		if (probeEndptC != null)
			gxp.setProbeEndptC(Double.parseDouble(probeEndptC));
		if (probeEndptD != null)
			gxp.setProbeEndptD(Double.parseDouble(probeEndptD));
		if (probeEndptE != null)
			gxp.setProbeEndptE(Double.parseDouble(probeEndptE));
		if (probeEndptSPC != null)
			gxp.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));
		return gxp;
	}

	public String postToBackup(HttpServletRequest request) {
		setRequest(request);
		HttpURLConnection hc = null;
		OutputStream os = null;
		int responseCode = 0;
		String response = null;
		URL url;
		try {
			url = new URL(backupPostUrl);
			hc = (HttpURLConnection) url.openConnection();
			hc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			hc.setRequestProperty("Content-Language", "en-US");
			hc.setDoOutput(true);
			try {
				os = hc.getOutputStream();
				Enumeration<String> names = request.getParameterNames();
				StringBuilder queryString = new StringBuilder();
				while (names.hasMoreElements()) {
					String parameter = names.nextElement();
					String value = request.getParameter(parameter);
					queryString.append(parameter + "=" + value + "&");
				}
				String requestStr = queryString.toString();
//				System.out.println(">>> DEBUG >>> request string: "
//						+ requestStr);
				os.write(requestStr.getBytes());
				os.flush();
				responseCode = hc.getResponseCode();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println("Could not submit to Backup. Response Code "
						+ responseCode + "");
			}
			try {
				// Save the error in DB
				String patientId = request.getParameter("pid");
				String systemId = request.getParameter("systemid");
				String sampleId = request.getParameter("sampleid");
				String operatorId = request.getParameter("operatorid");
				String pcId = request.getParameter("pcid");
				String hostId = request.getParameter("hostid");
				Encounter encounter = new Encounter(new EncounterId(0,
						patientId, operatorId), "SYNC_RESULT", systemId,
						new Date(), new Date(), new Date(), "");
				ArrayList<String> encounterResults = new ArrayList<String>();
				encounterResults.add("POST_URL" + "=" + backupPostUrl);
				encounterResults.add("PATIENT_ID" + "=" + patientId);
				encounterResults.add("SAMPLE_ID" + "=" + sampleId);
				encounterResults.add("SYSTEM_ID" + "=" + systemId);
				encounterResults.add("OPERATOR_ID" + "=" + operatorId);
				encounterResults.add("PC_ID" + "=" + pcId);
				encounterResults.add("HOST_ID" + "=" + hostId);
				encounterResults.add("RESP_CODE" + "=" + responseCode);
				if (responseCode == HttpURLConnection.HTTP_OK)
					encounterResults.add("RESPONSE" + "=" + "SUCCESS");
				else
					encounterResults.add("RESPONSE" + "=" + "FAILURE");
				ssl.saveEncounterWithResults(encounter, encounterResults);
				System.out.println("Sync Result saved in Encounters...");
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Sync Result could not be saved in Encounters...");
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					hc.getInputStream()));
			String line = "";
			response = "";
			while ((line = br.readLine()) != null) {
				response += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
					hc.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Response from Backup URL: " + response);
		return response;
	}
}
