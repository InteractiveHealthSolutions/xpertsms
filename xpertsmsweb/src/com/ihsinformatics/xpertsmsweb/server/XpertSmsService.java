package com.ihsinformatics.xpertsmsweb.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jmatrix.eproperties.EProperties;

import org.irdresearch.smstarseel.context.TarseelContext;

import com.ihsinformatics.xpertsmsweb.shared.SmsTarseelUtil;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;

public class XpertSmsService extends HttpServlet {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private HttpServletRequest request;
    private ServerServiceImpl ssl;

    @Override
    public void init() throws ServletException {
	try {
	    System.out.println(">>>>LOADING SYSTEM PROPERTIES...");
	    InputStream f = Thread.currentThread().getContextClassLoader()
		    .getResourceAsStream("smstarseel.properties");
	    // Java Properties do not seem to support substitutions hence
	    // EProperties are used to accomplish the task
	    EProperties root = new EProperties();
	    root.load(f);

	    // Java Properties to send to context and other APIs for
	    // configuration
	    Properties prop = new Properties();
	    prop.putAll(SmsTarseelUtil.convertEntrySetToMap(root.entrySet()));

	    TarseelContext.instantiate(prop, "smstarseel.cfg.xml");

	    System.out.println("......PROPERTIES LOADED SUCCESSFULLY......");

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ServletException(e);
	}
	// TODO uncomment if doing via socket
	// initSocket();
    }

    public XpertSmsService() {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	System.out.println("Doing post on Server for XpertSMS..");
	ssl = new ServerServiceImpl();
	PrintWriter out = resp.getWriter();
	String xmlResponse = "xmlResponse";
	this.setRequest(req);
	xmlResponse = this.handleEvent();
	System.out.println(xmlResponse);
	out.print(xmlResponse);
	out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	doPost(req, resp);
    }

    public void setRequest(HttpServletRequest request) {
	this.request = request;
    }

    public HttpServletRequest getRequest() {
	return request;
    }

    @SuppressWarnings("unused")
    public String handleEvent() {

	String xmlResponse = null;

	String reqType = request.getParameter("type");
	System.out.println("----->" + reqType);
	System.out.println("Handle Event!");

	if (reqType.equals("astmresult")) {
	    return doRemoteASTMResult();
	}

	return null;
    }

    private String doRemoteASTMResult() {
	String xml = null;
	boolean update = false;
	/*
	 * MTB DETECTED (HIGH|LOW|MEDIUM|VERY LOW); RIF Resistance (DETECTED|NOT
	 * DETECTED|INDETERMINATE) MTB NOT DETECTED NO RESULT ERROR INVALID
	 */
	String patientId = request.getParameter("pid"); // Why was this sampleId before?
	String sampleId = request.getParameter("sampleid");
	String mtb = request.getParameter("mtb");
	String systemId = request.getParameter("systemid");

	String rif = request.getParameter("rif");
	if (rif != null && rif.equalsIgnoreCase("null"))
	    rif = null;

	String resultFinal = request.getParameter("final");
	String resultPending = request.getParameter("pending");
	String resultError = request.getParameter("error");
	String resultCorrected = request.getParameter("correction");
	String resultDate = request.getParameter("enddate");
	String errorCode = request.getParameter("errorcode");
	String errorNotes = request.getParameter("errornotes");
	String notes = request.getParameter("notes");

	String operatorId = request.getParameter("operatorid");
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
	if (resultDate != null) {
	    System.out.println("handling time");
	    String year = resultDate.substring(0, 4);
	    String month = resultDate.substring(4, 6);
	    String date = resultDate.substring(6, 8);
	    String hour = null;
	    String minute = null;
	    String second = null;
	    if (resultDate.length() == 14) {
		hour = resultDate.substring(8, 10);
		minute = resultDate.substring(10, 12);
		second = resultDate.substring(12, 14);
	    }

	    GregorianCalendar cal = new GregorianCalendar();
	    cal.set(Calendar.YEAR, Integer.parseInt(year));
	    cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
	    cal.set(Calendar.DATE, Integer.parseInt(date));
	    if (hour != null)
		cal.set(Calendar.HOUR, Integer.parseInt(hour));
	    else
		cal.set(Calendar.HOUR, 0);
	    if (minute != null)
		cal.set(Calendar.MINUTE, Integer.parseInt(minute));
	    else
		cal.set(Calendar.MINUTE, 0);
	    if (second != null)
		cal.set(Calendar.SECOND, Integer.parseInt(second));
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    resultDateObj = cal.getTime();
	    System.out.println("TIME" + resultDateObj.getTime());
	}

	ssl = new ServerServiceImpl();
	GeneXpertResults[] gxp = null;
	GeneXpertResults gxpNew = null;
	try {
	    gxp = ssl.findGeneXpertResults(sampleId, patientId);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Here!");
	if (gxp == null) {
	    GeneXpertResults gxpU = null;
	    try {
		gxpU = createGeneXpertResults(patientId, sampleId, mtb, rif,
			resultDate, instrumentSerial, moduleId, cartridgeId,
			reagentLotId, expDate, operatorId, pcId, probeResultA,
			probeResultB, probeResultC, probeResultD, probeResultE,
			probeResultSPC, probeCtA, probeCtB, probeCtC, probeCtD,
			probeCtE, probeCtSPC, probeEndptA, probeEndptB,
			probeEndptC, probeEndptD, probeEndptE, probeEndptSPC,
			errorCode, systemId);
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
		    if (resultDateObj.getTime() == gxp[i].getDateTested()
			    .getTime()) {
			gxpNew = gxp[i];
			update = true;
			break;
		    }
		}
	    }

	    if (!update) {
		for (int i = 0; i < gxp.length; i++) {
		    if (gxp[i].getGeneXpertResult() == null) {// F form filled
			update = true;
			gxpNew = gxp[i];
			System.out.println("ID match null result");
			break;

		    }
		}
	    }
	}

	// set MTB
	if (update == true) {
	    if (mtb != null) {
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
		    if (index != -1) {
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
	    if (errorCode != null)
		gxpNew.setErrorCode(Integer.parseInt(errorCode));
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

    public GeneXpertResults createGeneXpertResults(String patientId,
	    String SampleID, String mtb, String rif, String resultDate,
	    String instrumentSerial, String moduleId, String cartridgeId,
	    String reagentLotId, String expDate, String operatorId,
	    String pcId, String probeResultA, String probeResultB,
	    String probeResultC, String probeResultD, String probeResultE,
	    String probeResultSPC, String probeCtA, String probeCtB,
	    String probeCtC, String probeCtD, String probeCtE,
	    String probeCtSPC, String probeEndptA, String probeEndptB,
	    String probeEndptC, String probeEndptD, String probeEndptE,
	    String probeEndptSPC, String errorCode, String systemId) {
	GeneXpertResults gxp = new GeneXpertResults();
	gxp.setSputumTestId(SampleID);
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
		}

		else {
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

	if (resultDate != null) {
	    String year = resultDate.substring(0, 4);
	    String month = resultDate.substring(4, 6);
	    String date = resultDate.substring(6, 8);
	    String hour = null;
	    String minute = null;
	    String second = null;

	    if (resultDate.length() == 14) {
		hour = resultDate.substring(8, 10);
		minute = resultDate.substring(10, 12);
		second = resultDate.substring(12, 14);

	    }

	    GregorianCalendar cal = new GregorianCalendar();
	    cal.set(Calendar.YEAR, Integer.parseInt(year));
	    cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
	    cal.set(Calendar.DATE, Integer.parseInt(date));
	    if (hour != null)
		cal.set(Calendar.HOUR, Integer.parseInt(hour));
	    else
		cal.set(Calendar.HOUR, 0);
	    if (minute != null)
		cal.set(Calendar.MINUTE, Integer.parseInt(minute));
	    cal.set(Calendar.MINUTE, 0);
	    if (second != null)
		cal.set(Calendar.SECOND, Integer.parseInt(second));
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    gxp.setDateTested(cal.getTime());

	}

	gxp.setInstrumentSerial(instrumentSerial);
	gxp.setModuleId(moduleId);
	gxp.setReagentLotId(reagentLotId);
	gxp.setExpDate(expDate);
	gxp.setCartridgeId(cartridgeId);
	gxp.setPcId(pcId);
	gxp.setOperatorId(operatorId);
	if (errorCode != null)
	    gxp.setErrorCode(Integer.parseInt(errorCode));

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
}
