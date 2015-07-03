/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.model.astm;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ihsinformatics.xpertsms.model.XpertResultUploadMessage;

/**
 * Implementation of XpertResultUploadMessage class based on ASTM standard
 * 
 * @author ali.habib@irdresearch.org
 */
public class XpertASTMResultUploadMessage extends XpertResultUploadMessage {
	
	public static char FIELD_DELIMITER = '|';
	
	public static char REPEAT_DELIMITER = '@';
	
	public static char COMPONENT_DELIMITER = '^';
	
	public static char ESCAPE_DELIMITER = '\\';
	
	// header fields
	protected String computerName;
	
	protected String messageId;
	
	protected String systemId; // e.g. GENEXPERT_PC (as
	                           // assigned in System Name
	                           // in system configuration)
	
	protected String instrumentSpecimenID; // e.g. GeneXpert
	
	protected String softwareVersion;
	
	protected String receiverId; // Host ID under system
	                             // config
	
	protected String processingId;
	
	protected String versionNumber; // 1384-97
	
	protected String messageDateTime;
	
	// order fields
	protected String instrumentSpecimenId;
	
	protected String universalTestId; // Assay Host Test Code e.g.
	                                  // G4v5
	
	protected String priority;
	
	protected String orderDateTime;
	
	protected String actionCode;
	
	protected String specimenType;
	
	protected String reportType;
	
	// result fields
	// protected String systemDefinedTestId; //e.g. TBPos or Rif as per Host
	// Configuration
	protected String systemDefinedTestName; // e.g. Xpert MTB-RIF Assay
	                                        // G4
	
	protected String systemDefinedTestVersion; // e.g 5
	
	protected String resultStatus; // e.g. F
	
	protected String operatorId; // e.g Karachi Xray
	
	protected String testStartDate;
	
	protected String testEndDate;
	
	protected String pcId;
	
	protected String instrumentSerial;
	
	protected String moduleId;
	
	protected String cartridgeId;
	
	protected String reagentLotId;
	
	protected String expDate;
	
	protected Boolean isFinal;
	
	protected Boolean isPending;
	
	protected Boolean isError;
	
	protected Boolean isCorrection;
	
	// comments fields
	protected String errorCode;
	
	protected String errorNotes;
	
	protected String notes;
	
	// R|2|^G4v5^^TBPos^^^Probe D^|POS^|||
	// R|3|^G4v5^^TBPos^^^Probe D^Ct|^32.4|||
	// R|4|^G4v5^^TBPos^^^Probe D^EndPt|^102.0|||
	// R|5|^G4v5^^TBPos^^^Probe C^|POS^|||
	// R|6|^G4v5^^TBPos^^^Probe C^Ct|^31.2|||
	// R|7|^G4v5^^TBPos^^^Probe C^EndPt|^130.0|||
	// R|8|^G4v5^^TBPos^^^Probe E^|NEG^|||
	// R|9|^G4v5^^TBPos^^^Probe E^Ct|^0|||
	// R|10|^G4v5^^TBPos^^^Probe E^EndPt|^4.0|||
	// R|11|^G4v5^^TBPos^^^Probe B^|POS^|||
	// R|12|^G4v5^^TBPos^^^Probe B^Ct|^31.5|||
	// R|13|^G4v5^^TBPos^^^Probe B^EndPt|^88.0|||
	// R|14|^G4v5^^TBPos^^^Probe A^|POS^|||
	// R|15|^G4v5^^TBPos^^^Probe A^Ct|^30.7|||
	// R|16|^G4v5^^TBPos^^^Probe A^EndPt|^92.0|||
	// R|17|^G4v5^^TBPos^^^SPC^|NA^|||
	// R|18|^G4v5^^TBPos^^^SPC^Ct|^28.1|||
	// R|19|^G4v5^^TBPos^^^SPC^EndPt|^302.0|||
	
	public XpertASTMResultUploadMessage() {
		super();
		messageId = null;
		retries = 0;
		try {
			setComputerName("Unknown");
			setComputerName(InetAddress.getLocalHost().getHostName());
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the computerName
	 */
	public String getComputerName() {
		return computerName;
	}
	
	/**
	 * @param computerName the computerName to set
	 */
	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}
	
	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}
	
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return systemId;
	}
	
	/**
	 * @param systemId the systemId to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	
	/**
	 * @return the instrumentSpecimenID
	 */
	public String getSystemName() {
		return instrumentSpecimenID;
	}
	
	/**
	 * @param instrumentSpecimenID the instrumentSpecimenID to set
	 */
	public void setSystemName(String instrumentSpecimenID) {
		this.instrumentSpecimenID = instrumentSpecimenID;
	}
	
	/**
	 * @return the softwareVersion
	 */
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	
	/**
	 * @param softwareVersion the softwareVersion to set
	 */
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	
	/**
	 * @return the receiverId
	 */
	public String getReceiverId() {
		return receiverId;
	}
	
	/**
	 * @param receiverId the receiverId to set
	 */
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	
	/**
	 * @return the processingId
	 */
	public String getProcessingId() {
		return processingId;
	}
	
	/**
	 * @param processingId the processingId to set
	 */
	public void setProcessingId(String processingId) {
		this.processingId = processingId;
	}
	
	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}
	
	/**
	 * @param versionNumber the versionNumber to set
	 */
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	/**
	 * @return the messageDateTime
	 */
	public String getMessageDateTime() {
		return messageDateTime;
	}
	
	/**
	 * @param messageDateTime the messageDateTime to set
	 */
	public void setMessageDateTime(String messageDateTime) {
		this.messageDateTime = messageDateTime;
	}
	
	/**
	 * @return the intrumentSpecimenId
	 */
	public String getInstrumentSpecimenId() {
		return instrumentSpecimenId;
	}
	
	/**
	 * @param intrumentSpecimenId the intrumentSpecimenId to set
	 */
	public void setInstrumentSpecimenId(String instrumentSpecimenId) {
		this.instrumentSpecimenId = instrumentSpecimenId;
	}
	
	/**
	 * @return the universalTestId
	 */
	public String getUniversalTestId() {
		return universalTestId;
	}
	
	/**
	 * @param universalTestId the universalTestId to set
	 */
	public void setUniversalTestId(String universalTestId) {
		this.universalTestId = universalTestId;
	}
	
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}
	
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	/**
	 * @return the orderDateTime
	 */
	public String getOrderDateTime() {
		return orderDateTime;
	}
	
	/**
	 * @param orderDateTime the orderDateTime to set
	 */
	public void setOrderDateTime(String orderDateTime) {
		this.orderDateTime = orderDateTime;
	}
	
	/**
	 * @return the actionCode
	 */
	public String getActionCode() {
		return actionCode;
	}
	
	/**
	 * @param actionCode the actionCode to set
	 */
	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
	
	/**
	 * @return the specimenType
	 */
	public String getSpecimenType() {
		return specimenType;
	}
	
	/**
	 * @param specimenType the specimenType to set
	 */
	public void setSpecimenType(String specimenType) {
		this.specimenType = specimenType;
	}
	
	/**
	 * @return the reportType
	 */
	public String getReportType() {
		return reportType;
	}
	
	/**
	 * @param reportType the reportType to set
	 */
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	
	/**
	 * @return the systemDefinedTestId
	 */
	/*
	 * public String getSystemDefinedTestId() { return systemDefinedTestId; }
	 *//**
	 * @param systemDefinedTestId the systemDefinedTestId to set
	 */
	/*
	 * public void setSystemDefinedTestId(String systemDefinedTestId) {
	 * this.systemDefinedTestId = systemDefinedTestId; }
	 */
	
	/**
	 * @return the systemDefinedTestName
	 */
	public String getSystemDefinedTestName() {
		return systemDefinedTestName;
	}
	
	/**
	 * @param systemDefinedTestName the systemDefinedTestName to set
	 */
	public void setSystemDefinedTestName(String systemDefinedTestName) {
		this.systemDefinedTestName = systemDefinedTestName;
	}
	
	/**
	 * @return the systemDefinedTestVersion
	 */
	public String getSystemDefinedTestVersion() {
		return systemDefinedTestVersion;
	}
	
	/**
	 * @param systemDefinedTestVersion the systemDefinedTestVersion to set
	 */
	public void setSystemDefinedTestVersion(String systemDefinedTestVersion) {
		this.systemDefinedTestVersion = systemDefinedTestVersion;
	}
	
	/**
	 * @return the resultStatus
	 */
	public String getResultStatus() {
		return resultStatus;
	}
	
	/**
	 * @param resultStatus the resultStatus to set
	 */
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	
	/**
	 * @return the operatorId
	 */
	public String getOperatorId() {
		return operatorId;
	}
	
	/**
	 * @param operatorId the operatorId to set
	 */
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	
	/**
	 * @return the testStartDate
	 */
	public String getTestStartDate() {
		return testStartDate;
	}
	
	/**
	 * @param testStartDate the testStartDate to set
	 */
	public void setTestStartDate(String testStartDate) {
		this.testStartDate = testStartDate;
	}
	
	/**
	 * @return the testEndDate
	 */
	public String getTestEndDate() {
		return testEndDate;
	}
	
	/**
	 * @param testEndDate the testEndDate to set
	 */
	public void setTestEndDate(String testEndDate) {
		this.testEndDate = testEndDate;
	}
	
	/**
	 * @return the pcId
	 */
	public String getPcId() {
		return pcId;
	}
	
	/**
	 * @param pcId the pcId to set
	 */
	public void setPcId(String pcId) {
		this.pcId = pcId;
	}
	
	/**
	 * @return the instrumentSerial
	 */
	public String getInstrumentSerial() {
		return instrumentSerial;
	}
	
	/**
	 * @param instrumentSerial the instrumentSerial to set
	 */
	public void setInstrumentSerial(String instrumentSerial) {
		this.instrumentSerial = instrumentSerial;
	}
	
	/**
	 * @return the moduleId
	 */
	public String getModuleId() {
		return moduleId;
	}
	
	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	/**
	 * @return the cartridgeId
	 */
	public String getCartridgeId() {
		return cartridgeId;
	}
	
	/**
	 * @param cartridgeId the cartridgeId to set
	 */
	public void setCartridgeId(String cartridgeId) {
		this.cartridgeId = cartridgeId;
	}
	
	/**
	 * @return the reagentLotId
	 */
	public String getReagentLotId() {
		return reagentLotId;
	}
	
	/**
	 * @param reagentLotId the reagentLotId to set
	 */
	public void setReagentLotId(String reagentLotId) {
		this.reagentLotId = reagentLotId;
	}
	
	/**
	 * @return the expDate
	 */
	public String getExpDate() {
		return expDate;
	}
	
	/**
	 * @param expDate the expDate to set
	 */
	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}
	
	/**
	 * @return the isFinal
	 */
	public Boolean isFinal() {
		if (isFinal != null)
			return isFinal.booleanValue();
		return null;
	}
	
	/**
	 * @param isFinal the isFinal to set
	 */
	public void setFinal(boolean isFinal) {
		if (isFinal() == null)
			this.isFinal = new Boolean(isFinal);
	}
	
	/**
	 * @return the isPending
	 */
	public Boolean isPending() {
		if (isPending != null)
			return isPending.booleanValue();
		return null;
	}
	
	/**
	 * @param isPending the isPending to set
	 */
	public void setPending(boolean isPending) {
		if (isPending() == null)
			this.isPending = new Boolean(isPending);
	}
	
	/**
	 * @return the isError
	 */
	public Boolean isError() {
		if (isError != null)
			return isError.booleanValue();
		
		return null;
	}
	
	/**
	 * @param isError the isError to set
	 */
	public void setError(boolean isError) {
		if (isError() == null)
			this.isError = new Boolean(isError);
	}
	
	/**
	 * @return the isCorrection
	 */
	public Boolean isCorrection() {
		if (isCorrection != null)
			return isCorrection.booleanValue();
		return null;
	}
	
	/**
	 * @param isCorrection the isCorrection to set
	 */
	public void setCorrection(boolean isCorrection) {
		if (isCorrection() == null)
			this.isCorrection = new Boolean(isCorrection);
	}
	
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * @return the errorNotes
	 */
	public String getErrorNotes() {
		return errorNotes;
	}
	
	/**
	 * @param errorNotes the errorNotes to set
	 */
	public void setErrorNotes(String errorNotes) {
		this.errorNotes = errorNotes;
	}
	
	/**
	 * @return
	 */
	public String getNotes() {
		return notes;
	}
	
	/**
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	@Override
	public String toCsv() {
		StringBuilder csv = new StringBuilder();
		csv.append("\"" + replaceNull(patientId) + "\",");
		csv.append("\"" + replaceNull(sampleId) + "\",");
		csv.append("\"" + replaceNull(mtbResult) + "\",");
		csv.append("\"" + replaceNull(rifResult) + "\",");
		csv.append("\"" + replaceNull(isFinal) + "\",");
		csv.append("\"" + replaceNull(isPending) + "\",");
		csv.append("\"" + replaceNull(isError) + "\",");
		csv.append("\"" + replaceNull(isCorrection) + "\",");
		csv.append("\"" + replaceNull(resultStatus) + "\",");
		csv.append("\"" + replaceNull(operatorId) + "\",");
		csv.append("\"" + replaceNull(testStartDate) + "\",");
		csv.append("\"" + replaceNull(testEndDate) + "\",");
		csv.append("\"" + replaceNull(pcId) + "\",");
		csv.append("\"" + replaceNull(receiverId) + "\",");
		csv.append("\"" + replaceNull(instrumentSerial) + "\",");
		csv.append("\"" + replaceNull(moduleId) + "\",");
		csv.append("\"" + replaceNull(cartridgeId) + "\",");
		csv.append("\"" + replaceNull(reagentLotId) + "\",");
		csv.append("\"" + replaceNull(expDate) + "\",");
		csv.append("\"" + replaceNull(errorCode) + "\",");
		csv.append("\"" + replaceNull(errorNotes) + "\",");
		csv.append("\"" + replaceNull(notes) + "\",");
		csv.append("\"" + replaceNull(messageId) + "\",");
		csv.append("\"" + replaceNull(systemId) + "\",");
		csv.append("\"" + replaceNull(softwareVersion) + "\",");
		csv.append("\"" + replaceNull(versionNumber) + "\",");
		csv.append("\"" + replaceNull(processingId) + "\",");
		csv.append("\"" + replaceNull(messageDateTime) + "\",");
		csv.append("\"" + replaceNull(instrumentSpecimenId) + "\",");
		csv.append("\"" + replaceNull(universalTestId) + "\",");
		csv.append("\"" + replaceNull(priority) + "\",");
		csv.append("\"" + replaceNull(orderDateTime) + "\",");
		csv.append("\"" + replaceNull(actionCode) + "\",");
		csv.append("\"" + replaceNull(specimenType) + "\",");
		csv.append("\"" + replaceNull(reportType) + "\",");
		csv.append("\"" + replaceNull(systemDefinedTestName) + "\",");
		csv.append("\"" + replaceNull(systemDefinedTestVersion) + "\",");
		csv.append("\"" + replaceNull(probeResultA) + "\",");
		csv.append("\"" + replaceNull(probeResultB) + "\",");
		csv.append("\"" + replaceNull(probeResultC) + "\",");
		csv.append("\"" + replaceNull(probeResultD) + "\",");
		csv.append("\"" + replaceNull(probeResultE) + "\",");
		csv.append("\"" + replaceNull(probeResultSpc) + "\",");
		csv.append("\"" + replaceNull(probeCtA) + "\",");
		csv.append("\"" + replaceNull(probeCtB) + "\",");
		csv.append("\"" + replaceNull(probeCtC) + "\",");
		csv.append("\"" + replaceNull(probeCtD) + "\",");
		csv.append("\"" + replaceNull(probeCtE) + "\",");
		csv.append("\"" + replaceNull(probeCtSpc) + "\",");
		csv.append("\"" + replaceNull(probeEndptA) + "\",");
		csv.append("\"" + replaceNull(probeEndptB) + "\",");
		csv.append("\"" + replaceNull(probeEndptC) + "\",");
		csv.append("\"" + replaceNull(probeEndptD) + "\",");
		csv.append("\"" + replaceNull(probeEndptE) + "\",");
		csv.append("\"" + replaceNull(probeEndptSpc) + "\"");
		return csv.toString();
	}
	
	public Element toXmlNode() {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		
		if (doc == null) {
			return null;
		}
		
		Element messageNode = doc.createElement("outgoingmessage");
		
		Element patientIdNode = doc.createElement("patientid");
		Text patientIdText = doc.createTextNode(replaceNull(patientId));
		patientIdNode.appendChild(patientIdText);
		messageNode.appendChild(patientIdNode);
		
		Element sampleIdNode = doc.createElement("sampleid");
		Text sampleIdText = doc.createTextNode(replaceNull(sampleId));
		sampleIdNode.appendChild(sampleIdText);
		messageNode.appendChild(sampleIdNode);
		
		Element mtbNode = doc.createElement("mtb");
		Text mtbText = doc.createTextNode(replaceNull(mtbResult));
		mtbNode.appendChild(mtbText);
		messageNode.appendChild(mtbNode);
		
		Element rifNode = doc.createElement("rif");
		Text rifText = doc.createTextNode(replaceNull(rifResult));
		rifNode.appendChild(rifText);
		messageNode.appendChild(rifNode);
		
		Element finalNode = doc.createElement("final");
		Text finalText = doc.createTextNode(replaceNull(isFinal));
		finalNode.appendChild(finalText);
		messageNode.appendChild(finalNode);
		
		Element errorNode = doc.createElement("error");
		Text errorText = doc.createTextNode(replaceNull(isError));
		errorNode.appendChild(errorText);
		messageNode.appendChild(errorNode);
		
		Element errorCodeNode = doc.createElement("errorcode");
		Text errorCodeText = doc.createTextNode(replaceNull(errorCode));
		errorCodeNode.appendChild(errorCodeText);
		messageNode.appendChild(errorCodeNode);
		
		Element errorNotesNode = doc.createElement("errornotes");
		Text errorNotesText = doc.createTextNode(replaceNull(errorNotes));
		errorCodeNode.appendChild(errorNotesText);
		messageNode.appendChild(errorNotesNode);
		
		Element notesNode = doc.createElement("notes");
		Text notesNodeText = doc.createTextNode(replaceNull(notes));
		errorCodeNode.appendChild(notesNodeText);
		messageNode.appendChild(notesNode);
		
		Element pendingNode = doc.createElement("pending");
		Text pendingText = doc.createTextNode(replaceNull(isPending));
		pendingNode.appendChild(pendingText);
		messageNode.appendChild(pendingNode);
		
		Element correctionNode = doc.createElement("correction");
		Text correctionText = doc.createTextNode(replaceNull(isCorrection));
		correctionNode.appendChild(correctionText);
		messageNode.appendChild(correctionNode);
		
		Element instrumentIdNode = doc.createElement("instrumentid");
		Text instrumentIdText = doc.createTextNode(replaceNull(instrumentSerial));
		instrumentIdNode.appendChild(instrumentIdText);
		messageNode.appendChild(instrumentIdNode);
		
		Element pcIdNode = doc.createElement("pcid");
		Text pcIdText = doc.createTextNode(replaceNull(pcId));
		pcIdNode.appendChild(pcIdText);
		messageNode.appendChild(pcIdNode);
		
		Element cartridgeIdNode = doc.createElement("cartridgeid");
		Text cartridgeIdText = doc.createTextNode(replaceNull(cartridgeId));
		cartridgeIdNode.appendChild(cartridgeIdText);
		messageNode.appendChild(cartridgeIdNode);
		
		Element moduleIdNode = doc.createElement("moduleid");
		Text moduleIdText = doc.createTextNode(replaceNull(moduleId));
		moduleIdNode.appendChild(moduleIdText);
		messageNode.appendChild(moduleIdNode);
		
		Element reagentLotIdNode = doc.createElement("reagentlotid");
		Text reagentLotIdText = doc.createTextNode(replaceNull(reagentLotId));
		reagentLotIdNode.appendChild(reagentLotIdText);
		messageNode.appendChild(reagentLotIdNode);
		
		Element operatorIdNode = doc.createElement("operatorid");
		Text operatorIdText = doc.createTextNode(replaceNull(operatorId));
		operatorIdNode.appendChild(operatorIdText);
		messageNode.appendChild(operatorIdNode);
		
		Element resultDateNode = doc.createElement("resultdate");
		Text resultDateText = doc.createTextNode(replaceNull(testEndDate));
		resultDateNode.appendChild(resultDateText);
		messageNode.appendChild(resultDateNode);
		
		Element probeResultANode = doc.createElement("proberesulta");
		Text probeResultAText = doc.createTextNode(replaceNull(probeResultA));
		probeResultANode.appendChild(probeResultAText);
		messageNode.appendChild(probeResultANode);
		
		Element probeCtANode = doc.createElement("probecta");
		Text probeCtAText = doc.createTextNode(replaceNull(probeCtA));
		probeCtANode.appendChild(probeCtAText);
		messageNode.appendChild(probeCtAText);
		
		Element probeEndptANode = doc.createElement("probeendpta");
		Text probeEndptAText = doc.createTextNode(replaceNull(probeEndptA));
		probeEndptANode.appendChild(probeEndptAText);
		messageNode.appendChild(probeEndptAText);
		
		Element probeResultBNode = doc.createElement("proberesultb");
		Text probeResultBText = doc.createTextNode(replaceNull(probeResultB));
		probeResultBNode.appendChild(probeResultBText);
		messageNode.appendChild(probeResultBNode);
		
		Element probeCtBNode = doc.createElement("probectb");
		Text probeCtBText = doc.createTextNode(replaceNull(probeCtB));
		probeCtBNode.appendChild(probeCtBText);
		messageNode.appendChild(probeCtBText);
		
		Element probeEndptBNode = doc.createElement("probeendptb");
		Text probeEndptBText = doc.createTextNode(replaceNull(probeEndptB));
		probeEndptBNode.appendChild(probeEndptBText);
		messageNode.appendChild(probeEndptBText);
		
		Element probeResultCNode = doc.createElement("proberesultc");
		Text probeResultCText = doc.createTextNode(replaceNull(probeResultC));
		probeResultCNode.appendChild(probeResultCText);
		messageNode.appendChild(probeResultCNode);
		
		Element probeCtCNode = doc.createElement("probectc");
		Text probeCtCText = doc.createTextNode(replaceNull(probeCtC));
		probeCtCNode.appendChild(probeCtCText);
		messageNode.appendChild(probeCtCText);
		
		Element probeEndptCNode = doc.createElement("probeendptc");
		Text probeEndptCText = doc.createTextNode(replaceNull(probeEndptC));
		probeEndptCNode.appendChild(probeEndptCText);
		messageNode.appendChild(probeEndptCText);
		
		Element probeResultDNode = doc.createElement("proberesultd");
		Text probeResultDText = doc.createTextNode(replaceNull(probeResultD));
		probeResultDNode.appendChild(probeResultDText);
		messageNode.appendChild(probeResultDNode);
		
		Element probeCtDNode = doc.createElement("probectd");
		Text probeCtDText = doc.createTextNode(replaceNull(probeCtD));
		probeCtDNode.appendChild(probeCtDText);
		messageNode.appendChild(probeCtDText);
		
		Element probeEndptDNode = doc.createElement("probeendptd");
		Text probeEndptDText = doc.createTextNode(replaceNull(probeEndptD));
		probeEndptDNode.appendChild(probeEndptDText);
		messageNode.appendChild(probeEndptDText);
		
		Element probeResultENode = doc.createElement("proberesulte");
		Text probeResultEText = doc.createTextNode(replaceNull(probeResultE));
		probeResultENode.appendChild(probeResultEText);
		messageNode.appendChild(probeResultENode);
		
		Element probeCtENode = doc.createElement("probecte");
		Text probeCtEText = doc.createTextNode(replaceNull(probeCtE));
		probeCtENode.appendChild(probeCtEText);
		messageNode.appendChild(probeCtEText);
		
		Element probeEndptENode = doc.createElement("probeendpte");
		Text probeEndptEText = doc.createTextNode(replaceNull(probeEndptE));
		probeEndptENode.appendChild(probeEndptEText);
		messageNode.appendChild(probeEndptEText);
		
		Element probeResultSPCNode = doc.createElement("proberesultspc");
		Text probeResultSPCText = doc.createTextNode(replaceNull(probeResultSpc));
		probeResultSPCNode.appendChild(probeResultSPCText);
		messageNode.appendChild(probeResultSPCNode);
		
		Element probeCtSPCNode = doc.createElement("probectspc");
		Text probeCtSPCText = doc.createTextNode(replaceNull(probeCtSpc));
		probeCtSPCNode.appendChild(probeCtSPCText);
		messageNode.appendChild(probeCtSPCText);
		
		Element probeEndptSPCNode = doc.createElement("probeendptspc");
		Text probeEndptSPCText = doc.createTextNode(replaceNull(probeEndptSpc));
		probeEndptSPCNode.appendChild(probeEndptSPCText);
		messageNode.appendChild(probeEndptSPCText);
		
		Element messageIdNode = doc.createElement("messageid");
		Text messageIdText = doc.createTextNode(replaceNull(messageId));
		messageIdNode.appendChild(messageIdText);
		messageNode.appendChild(messageIdNode);
		
		Element systemIdNode = doc.createElement("systemid");
		Text systemIdText = doc.createTextNode(replaceNull(systemId));
		systemIdNode.appendChild(systemIdText);
		messageNode.appendChild(systemIdNode);
		
		Element instrumentSpecimenIDNode = doc.createElement("systemname");
		Text instrumentSpecimenIDText = doc.createTextNode(replaceNull(instrumentSpecimenID));
		instrumentSpecimenIDNode.appendChild(instrumentSpecimenIDText);
		messageNode.appendChild(instrumentSpecimenIDNode);
		
		Element softwareVersionNode = doc.createElement("softwareversion");
		Text softwareVersionText = doc.createTextNode(replaceNull(softwareVersion));
		softwareVersionNode.appendChild(softwareVersionText);
		messageNode.appendChild(softwareVersionNode);
		
		Element receiverIdNode = doc.createElement("receiverid");
		Text receiverIdText = doc.createTextNode(replaceNull(receiverId));
		receiverIdNode.appendChild(receiverIdText);
		messageNode.appendChild(receiverIdNode);
		
		Element processingIdNode = doc.createElement("processingid");
		Text processingIdText = doc.createTextNode(replaceNull(processingId));
		processingIdNode.appendChild(processingIdText);
		messageNode.appendChild(processingIdNode);
		
		Element versionNumberNode = doc.createElement("versionNumber");
		Text versionNumberText = doc.createTextNode(replaceNull(versionNumber));
		versionNumberNode.appendChild(versionNumberText);
		messageNode.appendChild(versionNumberNode);
		
		Element messageDateTimeNode = doc.createElement("messagedatetime");
		Text messageDateTimeText = doc.createTextNode(replaceNull(messageDateTime));
		messageDateTimeNode.appendChild(messageDateTimeText);
		messageNode.appendChild(messageDateTimeNode);
		
		Element instrumentSpecimenIdNode = doc.createElement("instrumentspecimenid");
		Text instrumentSpecimenIdText = doc.createTextNode(replaceNull(instrumentSpecimenID));
		instrumentSpecimenIdNode.appendChild(instrumentSpecimenIdText);
		messageNode.appendChild(instrumentSpecimenIdNode);
		
		Element universalTestIdNode = doc.createElement("universaltestid");
		Text universalTestIdText = doc.createTextNode(replaceNull(universalTestId));
		universalTestIdNode.appendChild(universalTestIdText);
		messageNode.appendChild(universalTestIdNode);
		
		Element orderDateTimeNode = doc.createElement("orderdatetime");
		Text orderDateTimeText = doc.createTextNode(replaceNull(orderDateTime));
		orderDateTimeNode.appendChild(orderDateTimeText);
		messageNode.appendChild(orderDateTimeNode);
		
		Element priorityNode = doc.createElement("priority");
		Text priorityText = doc.createTextNode(replaceNull(priority));
		priorityNode.appendChild(priorityText);
		messageNode.appendChild(priorityNode);
		
		Element actionCodeNode = doc.createElement("actioncode");
		Text actionCodeText = doc.createTextNode(replaceNull(actionCode));
		actionCodeNode.appendChild(actionCodeText);
		messageNode.appendChild(actionCodeNode);
		
		Element specimenTypeNode = doc.createElement("specimentype");
		Text specimenTypeText = doc.createTextNode(replaceNull(specimenType));
		specimenTypeNode.appendChild(specimenTypeText);
		messageNode.appendChild(specimenTypeNode);
		
		Element reportTypeNode = doc.createElement("reporttype");
		Text reportTypeText = doc.createTextNode(replaceNull(reportType));
		reportTypeNode.appendChild(reportTypeText);
		messageNode.appendChild(reportTypeNode);
		
		Element systemDefinedTestNameNode = doc.createElement("systemdefinedtestname");
		Text systemDefinedTestNameText = doc.createTextNode(replaceNull(systemDefinedTestName));
		systemDefinedTestNameNode.appendChild(systemDefinedTestNameText);
		messageNode.appendChild(systemDefinedTestNameNode);
		
		Element systemDefinedTestVersionNode = doc.createElement("systemdefinedtestversion");
		Text systemDefinedTestVersionText = doc.createTextNode(replaceNull(systemDefinedTestVersion));
		systemDefinedTestVersionNode.appendChild(systemDefinedTestVersionText);
		messageNode.appendChild(systemDefinedTestVersionNode);
		
		Element resultStatusNode = doc.createElement("resultstatus");
		Text resultStatusText = doc.createTextNode(replaceNull(resultStatus));
		resultStatusNode.appendChild(resultStatusText);
		messageNode.appendChild(resultStatusNode);
		
		doc.appendChild(messageNode);
		return messageNode;
	}
	
	public void loadXML(String xml) {
		// File fXmlFile = new File("/Users/mkyong/staff.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));
		
		Document doc = null;
		
		try {
			doc = dBuilder.parse(is);
		}
		catch (SAXException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		NodeList main = doc.getElementsByTagName("outgoingmessage").item(0).getChildNodes();
		
		int i = 0;
		Node node = main.item(i++);
		setPatientId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setSampleId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setMtbResult(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setRifResult(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setFinal(Boolean.parseBoolean(node.getFirstChild().getNodeValue()));
		node = main.item(i++);
		setError(Boolean.parseBoolean(node.getFirstChild().getNodeValue()));
		node = main.item(i++);
		setErrorCode(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setErrorNotes(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setNotes(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setCorrection(Boolean.parseBoolean(node.getFirstChild().getNodeValue()));
		node = main.item(i++);
		setInstrumentSerial(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setPcId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setCartridgeId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setModuleId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setReagentLotId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setOperatorId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setTestEndDate(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeResultA(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeCtA(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeEndPtA(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeResultB(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeCtB(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeEndPtB(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeResultC(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeCtC(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeEndPtC(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeResultD(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeCtD(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeEndPtD(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeResultE(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeCtE(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeEndPtE(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeResultSPC(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeCtSPC(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProbeEndPtSPC(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setMessageId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setSystemId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setInstrumentSpecimenId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setSoftwareVersion(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setReceiverId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setProcessingId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setVersionNumber(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setMessageDateTime(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setInstrumentSpecimenId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setUniversalTestId(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setOrderDateTime(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setPriority(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setActionCode(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setSpecimenType(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setReportType(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setSystemDefinedTestName(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setSystemDefinedTestVersion(node.getFirstChild().getNodeValue());
		node = main.item(i++);
		setResultStatus(node.getFirstChild().getNodeValue());
	}
	
	@Override
	public String toSqlQuery() {
		StringBuffer query = new StringBuffer();
		String mtbBurden = null;
		if (mtbResult != null) {
			int index = mtbResult.indexOf("MTB DETECTED");
			if (index != -1) {
				mtbBurden = mtbResult.substring(index + "MTB DETECTED".length() + 1);
				mtbResult = "MTB DETECTED";
			} else {
				index = mtbResult.indexOf("MTB NOT DETECTED");
				if (index != -1) {
					mtbResult = "MTB NOT DETECTED";
					mtbBurden = "";
				} else {
					mtbBurden = "";
				}
			}
		}
		
		query.append("insert into genexpertresults (PatientID,SputumTestID,LaboratoryID,DateTested,DrugResistance,GeneXpertResult,MTBBurden,ErrorCode,Remarks,InstrumentID,ModuleID,CartridgeID,ReagentLotID,PcID,HostID,OperatorID,CartridgeExpiryDate,ProbeResultA,ProbeResultB,ProbeResultC,ProbeResultD,ProbeResultE,ProbeResultSPC,ProbeCtA,ProbeCtB,ProbeCtC,ProbeCtD,ProbeCtE,ProbeCtSPC,ProbeEndptA,ProbeEndptB,ProbeEndptC,ProbeEndptD,ProbeEndptE,ProbeEndptSPC)");
		query.append(" VALUES ");
		query.append("('" + getPatientId() + "',");
		query.append("'" + getSampleId() + "',");
		query.append("'" + getSystemName() + "',");
		query.append("'" + getTestEndDate() + "',");
		query.append("'" + getRifResult() + "',");
		query.append("'" + getMtbResult() + "',");
		query.append("'" + mtbBurden + "',");
		query.append("'" + getErrorCode() + "',");
		query.append("'" + getErrorNotes() + ". " + getNotes() + "',");
		query.append("'" + getInstrumentSerial() + "',");
		query.append("'" + getModuleId() + "',");
		query.append("'" + getCartridgeId() + "',");
		query.append("'" + getReagentLotId() + "',");
		query.append("'" + getPcId() + "',");
		query.append("'" + getReceiverId() + "',");
		query.append("'" + getOperatorId() + "',");
		query.append("'" + getExpDate() + "',");
		query.append("'" + getProbeResultA() + "',");
		query.append("'" + getProbeResultB() + "',");
		query.append("'" + getProbeResultC() + "',");
		query.append("'" + getProbeResultD() + "',");
		query.append("'" + getProbeResultE() + "',");
		query.append("'" + getProbeResultSPC() + "',");
		query.append("'" + getProbeCtA() + "',");
		query.append("'" + getProbeCtB() + "',");
		query.append("'" + getProbeCtC() + "',");
		query.append("'" + getProbeCtD() + "',");
		query.append("'" + getProbeCtE() + "',");
		query.append("'" + getProbeCtSPC() + "',");
		query.append("'" + getProbeEndptA() + "',");
		query.append("'" + getProbeEndptB() + "',");
		query.append("'" + getProbeEndptC() + "',");
		query.append("'" + getProbeEndptD() + "',");
		query.append("'" + getProbeEndptE() + "',");
		query.append("'" + getProbeEndptSPC() + "')");
		return query.toString();
	}
	
	/**
	 * Convert result object into query string for HTTP(s) post
	 * 
	 * @param exportProbes
	 * @param username as in Property: serveruser
	 * @param password as in Property: serverpass
	 * @return
	 */
	@Override
	public String toPostParams(boolean exportProbes, String username, String password) {
		String postParams = "";
		postParams += "type=astmresult";
		if (username != null)
			postParams += "&username=" + username;
		if (password != null)
			postParams += "&password=" + password;
		if (patientId != null)
			postParams += "&pid=" + patientId;
		if (sampleId != null)
			postParams += "&sampleid=" + sampleId.replaceAll("\\(", "").replaceAll("\\)", "");
		postParams += "&mtb=" + mtbResult;
		postParams += "&rif=" + rifResult;
		if (isFinal() != null && isFinal())
			postParams += "&final=yes";
		if (isPending() != null && isPending())
			postParams += "&pending=yes";
		if (isError() != null && isError()) {
			postParams += "&error=yes";
			postParams += "&errorcode=" + errorCode;
			postParams += "&errornotes=" + errorNotes.replaceAll("([&+!@#$%^&*()\\/<>\"\'=,.])", " ");
		}
		if (notes != null)
			postParams += "&notes=" + notes.replaceAll("([&+!@#$%^&*()\\/<>\"\'=,.])", " ");
		if (isCorrection() != null && isCorrection())
			postParams += "&correction=yes";
		postParams += "&enddate=" + testEndDate;
		postParams += "&operatorid=" + operatorId;// e.g Karachi Xray
		postParams += "&pcid=" + pcId;
		postParams += "&receiverid=" + receiverId;
		postParams += "&instserial=" + instrumentSerial;
		postParams += "&moduleid=" + moduleId;
		postParams += "&cartrigeid=" + cartridgeId;
		postParams += "&reagentlotid=" + reagentLotId;
		postParams += "&systemid=" + systemId;
		postParams += "&expdate=" + expDate;
		
		if (exportProbes) {
			postParams += "&probea=" + probeResultA;
			postParams += "&probeb=" + probeResultB;
			postParams += "&probec=" + probeResultC;
			postParams += "&probed=" + probeResultD;
			postParams += "&probee=" + probeResultE;
			postParams += "&probespc=" + probeResultSpc;
			postParams += "&probeact=" + probeCtA;
			postParams += "&probebct=" + probeCtB;
			postParams += "&probecct=" + probeCtC;
			postParams += "&probedct=" + probeCtD;
			postParams += "&probeect=" + probeCtE;
			postParams += "&probespcct=" + probeCtSpc;
			postParams += "&probeaendpt=" + probeEndptA;
			postParams += "&probebendpt=" + probeEndptB;
			postParams += "&probecendpt=" + probeEndptC;
			postParams += "&probedendpt=" + probeEndptD;
			postParams += "&probeeendpt=" + probeEndptE;
			postParams += "&probespcendpt=" + probeEndptSpc;
		}
		return postParams + "&";
	}
	
	@Override
	public String toSMS(boolean exportProbes) {
		StringBuilder smsText = new StringBuilder();
		smsText.append(replaceNull(patientId) + "^");
		smsText.append(replaceNull(sampleId) + "^");
		smsText.append(replaceNull(mtbResult) + "^");
		smsText.append(replaceNull(rifResult) + "^");
		smsText.append(replaceNull(systemId) + "^");
		smsText.append(replaceNull(pcId) + "^");
		smsText.append(replaceNull(receiverId) + "^");
		smsText.append(replaceNull(operatorId) + "^");
		smsText.append(replaceNull(instrumentSerial) + "^");
		smsText.append(replaceNull(moduleId) + "^");
		smsText.append(replaceNull(cartridgeId) + "^");
		smsText.append(replaceNull(reagentLotId) + "^");
		smsText.append(replaceNull(testEndDate) + "^");
		if (isFinal() != null && isFinal())
			smsText.append("yes" + "^");
		else
			smsText.append("no" + "^");
		if (isPending() != null && isPending())
			smsText.append("yes" + "^");
		else
			smsText.append("no" + "^");
		if (isError() != null && isError())
			smsText.append("yes" + "^" + errorCode + "^" + errorNotes + "^");
		else
			smsText.append("no" + "^");
		if (isCorrection() != null && isCorrection())
			smsText.append("yes" + "^");
		else
			smsText.append("no" + "^");
		smsText.append(notes);
		if (exportProbes) {
			smsText.append("^");
			smsText.append(replaceNull(probeResultA) + "^");
			smsText.append(replaceNull(probeResultB) + "^");
			smsText.append(replaceNull(probeResultC) + "^");
			smsText.append(replaceNull(probeResultD) + "^");
			smsText.append(replaceNull(probeResultE) + "^");
			smsText.append(replaceNull(probeResultSpc) + "^");
			
			smsText.append(replaceNull(probeCtA) + "^");
			smsText.append(replaceNull(probeCtB) + "^");
			smsText.append(replaceNull(probeCtC) + "^");
			smsText.append(replaceNull(probeCtD) + "^");
			smsText.append(replaceNull(probeCtE) + "^");
			smsText.append(replaceNull(probeCtSpc) + "^");
			
			smsText.append(replaceNull(probeEndptA) + "^");
			smsText.append(replaceNull(probeEndptB) + "^");
			smsText.append(replaceNull(probeEndptC) + "^");
			smsText.append(replaceNull(probeEndptD) + "^");
			smsText.append(replaceNull(probeEndptE) + "^");
			smsText.append(replaceNull(probeEndptSpc));
		}
		return smsText.toString();
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("assayHostTestCode", universalTestId);
		jsonObj.put("assay", systemDefinedTestName);
		jsonObj.put("assayVersion", systemDefinedTestVersion);
		jsonObj.put("sampleId", sampleId);
		jsonObj.put("patientId", patientId);
		jsonObj.put("user", operatorId);
		jsonObj.put("testStartedOn", testStartDate);
		jsonObj.put("testEndedOn", testEndDate);
		jsonObj.put("messageSentOn", messageDateTime);
		jsonObj.put("reagentLotId", reagentLotId);
		jsonObj.put("cartridgeExpirationDate", expDate);
		jsonObj.put("cartridgeSerial", cartridgeId);
		jsonObj.put("moduleSerial", moduleId);
		jsonObj.put("instrumentSerial", instrumentSerial);
		jsonObj.put("softwareVersion", softwareVersion);
		jsonObj.put("resultMtb", mtbResult);
		jsonObj.put("resultRif", rifResult);
		jsonObj.put("resultText", mtbResult + "|" + rifResult);
		jsonObj.put("deviceSerial", pcId);
		jsonObj.put("hostId", receiverId);
		jsonObj.put("systemName", computerName);
		jsonObj.put("computerName", computerName);
		jsonObj.put("errorCode", errorCode);
		jsonObj.put("errorNotes", errorNotes);
		jsonObj.put("externalTestId", sampleId);
		jsonObj.put("notes", notes);
		if (!"".equals(probeResultA) && probeResultA != null)
			jsonObj.put("probeA", probeResultA);
		if (!"".equals(probeResultB) && probeResultB != null)
			jsonObj.put("probeB", probeResultB);
		if (!"".equals(probeResultC) && probeResultC != null)
			jsonObj.put("probeC", probeResultC);
		if (!"".equals(probeResultD) && probeResultD != null)
			jsonObj.put("probeD", probeResultD);
		if (!"".equals(probeResultE) && probeResultE != null)
			jsonObj.put("probeE", probeResultE);
		if (!"".equals(probeResultSpc) && probeResultSpc != null)
			jsonObj.put("probeSpc", probeResultSpc);
		if (!"".equals(probeCtA) && probeCtA != null)
			jsonObj.put("probeACt", Double.parseDouble(probeCtA));
		if (!"".equals(probeCtB) && probeCtB != null)
			jsonObj.put("probeBCt", Double.parseDouble(probeCtB));
		if (!"".equals(probeCtC) && probeCtC != null)
			jsonObj.put("probeCCt", Double.parseDouble(probeCtC));
		if (!"".equals(probeCtD) && probeCtD != null)
			jsonObj.put("probeDCt", Double.parseDouble(probeCtD));
		if (!"".equals(probeCtE) && probeCtE != null)
			jsonObj.put("probeECt", Double.parseDouble(probeCtE));
		if (!"".equals(probeCtSpc) && probeCtSpc != null)
			jsonObj.put("probeSpcCt", Double.parseDouble(probeCtSpc));
		if (!"".equals(probeEndptA) && probeEndptA != null)
			jsonObj.put("probeAEndpt", Double.parseDouble(probeEndptA));
		if (!"".equals(probeEndptB) && probeEndptB != null)
			jsonObj.put("probeBEndpt", Double.parseDouble(probeEndptB));
		if (!"".equals(probeEndptC) && probeEndptC != null)
			jsonObj.put("probeCEndpt", Double.parseDouble(probeEndptC));
		if (!"".equals(probeEndptD) && probeEndptD != null)
			jsonObj.put("probeDEndpt", Double.parseDouble(probeEndptD));
		if (!"".equals(probeEndptE) && probeEndptE != null)
			jsonObj.put("probeEEndpt", Double.parseDouble(probeEndptE));
		if (!"".equals(probeEndptSpc) && probeEndptSpc != null)
			jsonObj.put("probeSpcEndpt", Double.parseDouble(probeEndptSpc));
		if (!"".equals(qc1) && qc1 != null)
			jsonObj.put("qc1", qc1);
		if (!"".equals(qc2) && qc2 != null)
			jsonObj.put("qc2", qc2);
		if (!"".equals(qc1Ct) && qc1Ct != null)
			jsonObj.put("qc1Ct", Float.parseFloat(qc1Ct));
		if (!"".equals(qc2Ct) && qc2Ct != null)
			jsonObj.put("qc2Ct", Float.parseFloat(qc2Ct));
		if (!"".equals(qc1Endpt) && qc1Endpt != null)
			jsonObj.put("qc1Endpt", Float.parseFloat(qc1Endpt));
		if (!"".equals(qc2Endpt) && qc2Endpt != null)
			jsonObj.put("qc2Endpt", Float.parseFloat(qc2Endpt));
		return jsonObj;
	}
	
	@Override
	public String toString() {
		return "XpertASTMResultUploadMessage [FIELD_DELIMITER=" + FIELD_DELIMITER + ", REPEAT_DELIMITER=" + REPEAT_DELIMITER
		        + ", COMPONENT_DELIMITER=" + COMPONENT_DELIMITER + ", ESCAPE_DELIMITER=" + ESCAPE_DELIMITER + ", messageId="
		        + messageId + ", systemId=" + systemId + ", instrumentSpecimenID=" + instrumentSpecimenID
		        + ", softwareVersion=" + softwareVersion + ", hostId=" + receiverId + ", processingId=" + processingId
		        + ", versionNumber=" + versionNumber + ", messageDateTime=" + messageDateTime + ", instrumentSpecimenId="
		        + instrumentSpecimenId + ", universalTestId=" + universalTestId + ", priority=" + priority
		        + ", orderDateTime=" + orderDateTime + ", actionCode=" + actionCode + ", specimenType=" + specimenType
		        + ", reportType=" + reportType + ", systemDefinedTestName=" + systemDefinedTestName
		        + ", systemDefinedTestVersion=" + systemDefinedTestVersion + ", resultStatus=" + resultStatus
		        + ", operatorId=" + operatorId + ", testStartDate=" + testStartDate + ", testEndDate=" + testEndDate
		        + ", pcId=" + pcId + ", instrumentSerial=" + instrumentSerial + ", moduleId=" + moduleId + ", cartridgeId="
		        + cartridgeId + ", reagentLotId=" + reagentLotId + ", expDate=" + expDate + ", isFinal=" + isFinal
		        + ", isPending=" + isPending + ", isError=" + isError + ", isCorrection=" + isCorrection + ", errorCode="
		        + errorCode + ", errorNotes=" + errorNotes + ", notes=" + notes + ", patientId=" + patientId + ", sampleId="
		        + sampleId + ", mtbResult=" + mtbResult + ", rifResult=" + rifResult + ", probeResultA=" + probeResultA
		        + ", probeResultB=" + probeResultB + ", probeResultC=" + probeResultC + ", probeResultD=" + probeResultD
		        + ", probeResultE=" + probeResultE + ", probeResultSpc=" + probeResultSpc + ", probeCtA=" + probeCtA
		        + ", probeCtB=" + probeCtB + ", probeCtC=" + probeCtC + ", probeCtD=" + probeCtD + ", probeCtE=" + probeCtE
		        + ", probeCtSpc=" + probeCtSpc + ", probeEndptA=" + probeEndptA + ", probeEndptB=" + probeEndptB
		        + ", probeEndptC=" + probeEndptC + ", probeEndptD=" + probeEndptD + ", probeEndptE=" + probeEndptE
		        + ", probeEndptSpc=" + probeEndptSpc + ", retries=" + retries + "]";
	}
}
