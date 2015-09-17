/*
Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.xpertsms.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.OutboundMessage.PeriodType;
import org.irdresearch.smstarseel.data.OutboundMessage.Priority;
import org.irdresearch.smstarseel.data.Project;
import org.irdresearch.smstarseel.service.SMSService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.constant.FileConstants;
import com.ihsinformatics.xpertsms.constant.XML;
import com.ihsinformatics.xpertsms.net.GxAlertSender;
import com.ihsinformatics.xpertsms.net.HttpSender;
import com.ihsinformatics.xpertsms.net.OpenMrsApiAuthRest;
import com.ihsinformatics.xpertsms.net.ResultServer;
import com.ihsinformatics.xpertsms.util.DateTimeUtil;
import com.ihsinformatics.xpertsms.util.PrintWriterUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 */
public class ResultsSender extends Thread {
	
	private ResultServer server;
	
	private HttpSender httpSender;
	
	private PrintWriterUtil printWriter;
	
	private PrintWriterUtil successWriter;
	
	private PrintWriter csvWriter;
	
	private int retries;
	
	private String message;
	
	private File successLog;
	
	private boolean detailedLog;
	
	/* Export methods */
	private String username;
	
	private String password;
	
	private boolean exportProbes;
	
	private boolean exportCsv;
	
	private boolean exportSms;
	
	private boolean exportWeb;
	
	private boolean exportGxa;
	
	private boolean exportOpenMrs;
	
	/**
     * 
     */
	public ResultsSender(ResultServer server) {
		this.server = server;
		message = "";
		// Get required properties
		username = XpertProperties.props.getProperty(XpertProperties.WEB_USERNAME);
		password = XpertProperties.props.getProperty(XpertProperties.WEB_PASSWORD);
		exportProbes = XpertProperties.props.getProperty(XpertProperties.EXPORT_PROBES).equals("YES");
		exportCsv = XpertProperties.getProperty(XpertProperties.CSV_EXPORT).equals("YES");
		exportWeb = XpertProperties.getProperty(XpertProperties.WEB_EXPORT).equals("YES");
		exportSms = XpertProperties.getProperty(XpertProperties.SMS_EXPORT).equals("YES");
		exportGxa = XpertProperties.getProperty(XpertProperties.GXA_EXPORT).equals("YES");
		exportOpenMrs = XpertProperties.getProperty(XpertProperties.OPENMRS_EXPORT).equals("YES");
		// Initialize Http only if such export method is activated
		if (exportWeb | exportGxa | exportOpenMrs) {
			httpSender = new HttpSender();
		}
		if (exportCsv) {
			try {
				csvWriter = new PrintWriter(FileConstants.XPERT_SMS_DIR + System.getProperty("file.separator")
				        + DateTimeUtil.getSQLDate(new Date()) + "_xpertdump.csv");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ResultsSender(){
		
	}
	
	public boolean isDetailedLog() {
		return detailedLog;
	}
	
	public void setDetailedLog(boolean detailedLog) {
		this.detailedLog = detailedLog;
	}
	
	public void run() {
		XpertResultUploadMessage message = null;
		String response = "";
		try {
			printWriter = new PrintWriterUtil(server, FileConstants.XPERT_SMS_DIR + System.getProperty("file.separator")
			        + server.getFileNameDateString(new Date()) + "_xpertSMS_send_log.txt");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			successLog = new File(FileConstants.XPERT_SMS_DIR + System.getProperty("file.separator") + "successLog.txt");
			if (!successLog.exists()) {
				successLog.createNewFile();
			}
			successWriter = new PrintWriterUtil(server, successLog.getPath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		while (!server.getStopped()) {
			message = server.getOutgoingMessagesHead();
			if (exportCsv) {
				writeToCsv(message.toCsv());
			}
			if (exportSms) {
				try {
					queueSms(message);
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
					if (detailedLog)
						printWriter.println(e.getMessage(), true, MessageType.EXCEPTION);
				}
				catch (SQLException e) {
					e.printStackTrace();
					if (detailedLog)
						printWriter.println(e.getMessage(), true, MessageType.EXCEPTION);
				}
			}
			if (exportWeb) {
				if (XpertProperties.props.getProperty(XpertProperties.WEB_SSL_ENCRYPTION).equals("YES"))
					response = httpSender.doSecurePost(message, username, password, exportProbes);
				else
					response = httpSender.doPost(message, username, password, exportProbes);
				parseResponse(response, message);
			}
			if (exportOpenMrs) {
				String username = XpertProperties.props.getProperty(XpertProperties.OPENMRS_USER);
				String password = XpertProperties.props.getProperty(XpertProperties.OPENMRS_PASSWORD);
				String url = XpertProperties.props.getProperty(XpertProperties.OPENMRS_REST_ADDRESS);
				OpenMrsApiAuthRest openMrsApiAuthRest = new OpenMrsApiAuthRest(username, password, url);
				response = openMrsApiAuthRest.postResults(message);
				parseResponse(response, message);
			}
			if (exportGxa) {
				GxAlertSender gxAlertSender = new GxAlertSender();
				response = gxAlertSender.postToGxAlert(message);
				parseResponse(response, message);
			} else {
			}
		}
		if (server.getStopped()) {
			printWriter.println("Stopping Transmitting Thread!", true, MessageType.INFO);
		}
		printWriter.flush();
		printWriter.close();
	}
	
	public synchronized void queueSms(XpertResultUploadMessage xpertMessage) throws ClassNotFoundException, SQLException {
		/* Save in SmsTarseel */
		String recipient = XpertProperties.getProperty(XpertProperties.SMS_ADMIN_PHONE);
		//		String projectname = XpertProperties.getProperty(XpertProperties.SMS_PROJECT_NAME);
		String text = xpertMessage.toSMS(false);
		//		Date duedate = new Date();
		//		TarseelServices services = TarseelContext.getServices();
		//		SMSService smsService = services.getSmsService();
		//		List<Project> projects = TarseelContext.getServices().getDeviceService().findProject(projectname);
		//		smsService.createNewOutboundSms(recipient, text, duedate, Priority.HIGH, 1, PeriodType.WEEK, projects.get(0)
		//		        .getProjectId(), "Sent from XpertSMS");
		//		services.commitTransaction();
		//		services.closeSession();
		
		// TODO: Remove this ugly method after SMSTarseel is fixed
		String dbIpAddress = XpertProperties.props.getProperty(XpertProperties.DB_IP_ADDRESS);
		String dbPort = XpertProperties.props.getProperty(XpertProperties.DB_PORT);
		String dbName = XpertProperties.props.getProperty(XpertProperties.DB_NAME);
		String dbUsername = XpertProperties.props.getProperty(XpertProperties.DB_USERNAME);
		String dbPassword = XpertProperties.props.getProperty(XpertProperties.DB_PASSWORD);
		String dbUrl = "jdbc:mysql://" + dbIpAddress + ":" + dbPort + "/" + dbName;
		String dbClass = "com.mysql.jdbc.Driver";
		Connection conn = null;
		Class.forName(dbClass);
		conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		
		String characterHeader = addingCharacters(text);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String date = sdf.format(new Date());
		
		StringBuilder addedHeaderText = new StringBuilder();
		//String textCheck = text.substring(0, 130);
		List<String> completeMessage = new ArrayList<String>();
		String message = "";
		if(text.length() > 130){
			completeMessage  = twoChunks(addedHeaderText, date, characterHeader, text);
		} else {
			message = oneChunk(addedHeaderText, date, characterHeader, text);
		}
			
		//int length = addedHeaderText.length();

		// finding the chunk size to break sms text into multiple parts
		// with length of 140 because header has to be added which has length
		// of nearly 20 characters
		//int textChunk = 0;
		
		//float chunkSize = (float) Math.ceil(text.length() / 135f);
		/*textChunk = (int) chunkSize;
		String[] temp = new String[textChunk];
		int start = 0;
		int end = 135;
		
		*/
		if( message == ""){
		for(int i = 0; i < completeMessage.size(); i++){
			/*temp[i] = text.substring(start,end);
			if(i < textChunk - 1){
				end = text.length();
			}else {
				end += 135;
			}
			start += 135;*/
			
			

			String query = "insert into smstarseel.outboundmessage (outboundId,createdDate,description,dueDate,periodType,priority,projectId,recipient,referenceNumber,status,text,type,validityPeriod) ";
			query += "values (0, now(), 'Sent from XpertSMS', curdate(), 'WEEK', 0, 1, '" + recipient
			        + "', unix_timestamp(), 'PENDING', '" + completeMessage.get(i) + "', 'SMS', 24)";
			try{
				Thread.sleep(2000);
			} catch ( Exception e ) {
				
			}
			Statement stmt = null;
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
		}	
		
		
		conn.close();
		} else {
			String query = "insert into smstarseel.outboundmessage (outboundId,createdDate,description,dueDate,periodType,priority,projectId,recipient,referenceNumber,status,text,type,validityPeriod) ";
			query += "values (0, now(), 'Sent from XpertSMS', curdate(), 'WEEK', 0, 1, '" + recipient
			        + "', unix_timestamp(), 'PENDING', '" + message + "', 'SMS', 24)";
			try{
				Thread.sleep(2000);
			} catch ( Exception e ) {
				
			}
			Statement stmt = null;
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			conn.close();
			}
		}
	
	public synchronized void writeToCsv(String text) {
		System.out.println(text);
		System.out.println("Writing to CSV file...");
		csvWriter.println(text);
		csvWriter.flush();
		System.out.println("Printed to CSV");
	}
	
	public void parseResponse(String response, XpertResultUploadMessage xpertMessage) {
		boolean success = false;
		boolean retry = true;
		boolean retryExceeded = false;
		retries = 3;
		String errorMessage = null;
		message = "";
		
		if (xpertMessage.getRetries() >= retries)
			retryExceeded = true;
		
		if (response == null) {
			if (retry && retryExceeded) {
				printWriter.println("Retries exceeded for Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + ": " + "! Please enter manually!", true, MessageType.ERROR);
				// remove from queue and set retry count to zero
				// server.removeOutgoingMessage(0);
			} else if (retry) {
				printWriter.println(
				    "Retrying Sample ID " + xpertMessage.getSampleId() + " for Patient: " + xpertMessage.getPatientId()
				            + "! Attempts = " + (xpertMessage.getRetries() + 1), true, MessageType.WARNING);
				xpertMessage.setRetries(xpertMessage.getRetries() + 1);
				server.putOutGoingMessage(xpertMessage);
			} else {
				printWriter.println("Could not send result for Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + "! Please enter manually!", true, MessageType.ERROR);
				// remove from queue
				// server.removeOutgoingMessage(0);
			}
			return;
		}
		
		// Determine success
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(new InputSource(new ByteArrayInputStream(response.getBytes()))); // Changed deprecated StringBufferredInputStream to ByteArrayInputStream
			Element domElement = dom.getDocumentElement();
			NodeList nl = domElement.getChildNodes();
			Element statusNode = (Element) nl.item(0);
			String status = statusNode.getFirstChild().getNodeValue();
			if (status.equals(XML.XML_SUCCESS)) {
				success = true;
			} else if (status.equals(XML.XML_ERROR)) {
				success = false;
				errorMessage = nl.item(1).getFirstChild().getNodeValue();
			} else if (status.equals(XML.XML_DIVERTED)) {
				success = true;
				message = nl.item(1).getFirstChild().getNodeValue();
			}
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			String message = "Error in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
			        + xpertMessage.getPatientId() + "! Please enter manually!";
			if (detailedLog)
				message = "Parse exception in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + ": " + e.getMessage();
			printWriter.println(message, true, MessageType.EXCEPTION);
		}
		catch (SAXException e) {
			String message = "Error in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
			        + xpertMessage.getPatientId() + "! Please enter manually!";
			if (detailedLog)
				message = "Exception in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + ": " + e.getMessage();
			printWriter.println(message, true, MessageType.EXCEPTION);
		}
		catch (IOException e) {
			String message = "Error in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
			        + xpertMessage.getPatientId() + "! Please enter manually!";
			if (detailedLog)
				message = "Exception in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + ": " + e.getMessage();
			printWriter.println(message, true, MessageType.EXCEPTION);
		}
		if (success) {
			// log success
			if (message.length() == 0) {
				printWriter.println(
				    "Result for Sample ID " + xpertMessage.getSampleId() + " for Patient: " + xpertMessage.getPatientId()
				            + " transmitted sucessfully", true, MessageType.SUCCESS);
				successWriter.println(xpertMessage.getPatientId() + ":" + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + ":" + server.getLogEntryDateString(new Date()));
				successWriter.flush();
			} else
				printWriter.println(
				    "Result for Sample ID " + xpertMessage.getSampleId() + " for Patient: " + xpertMessage.getPatientId()
				            + " not saved. " + message, true, MessageType.ERROR);
		} else {
			if (retry && retryExceeded) {
				printWriter.println("Retries exceeded for Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + "! Please enter manually!\nError Message: " + errorMessage, true,
				    MessageType.ERROR);
			} else if (retry) {
				printWriter.println(
				    "Retrying Sample ID " + xpertMessage.getSampleId() + " for Patient: " + xpertMessage.getPatientId()
				            + "! Attempts = " + (xpertMessage.getRetries() + 1), true, MessageType.ERROR);
				xpertMessage.setRetries(xpertMessage.getRetries() + 1);
				server.putOutGoingMessage(xpertMessage);
			} else {
				printWriter.println("Could not send result for Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + "! Please enter manually!\nError Message: " + errorMessage, true,
				    MessageType.ERROR);
			}
		}
	}
	
	public String addingCharacters(String text){
		String characterAdded = "";
		String variables = XpertProperties.props.getProperty(XpertProperties.SMS_VARIABLES);
		// adding the characters to identify which variables
		// have been selected
		int j = 18;
		if(variables.contains("assayHostTestCode;"))
			characterAdded += "A";
		if(variables.contains("assay;"))
			characterAdded += "B";
		if(variables.contains("assayVersion;"))
			characterAdded += "C";
		if(variables.contains("sampleId"))
			characterAdded += "D";
		if(variables.contains("patientId"))
			characterAdded += "E";
		if(variables.contains("user;"))
			characterAdded += "F";
		if(variables.contains("testStartedOn;"))
			characterAdded += "G";
		if(variables.contains("testEndedOn;"))
			characterAdded += "H";
		if(variables.contains("messageSentOn;"))
			characterAdded += "I";
		if(variables.contains("reagentLotId;"))
			characterAdded += "J";
		if(variables.contains("cartridgeExpirationDate;"))
			characterAdded += "K";
		if(variables.contains("cartridgeSerial;"))
			characterAdded += "L";
		if(variables.contains("moduleSerial;"))
			characterAdded += "M";
		if(variables.contains("instrumentSerial;"))
			characterAdded += "N";
		if(variables.contains("softwareVersion;"))
			characterAdded += "O";
		if(variables.contains("resultMtb"))
			characterAdded += "P";
		if(variables.contains("resultRif;"))
			characterAdded += "Q";
		if(variables.contains("resultText;"))
			characterAdded += "R";
		if(variables.contains("deviceSerial;"))
			characterAdded += "S";
		if(variables.contains("hostId"))
			characterAdded += "T";
		if(variables.contains("systemName"))
			characterAdded += "U";
		if(variables.contains("computerName"))
			characterAdded += "V";
		if(variables.contains("notes;"))
			characterAdded += "W";
		if(variables.contains("errorCode"))
			characterAdded += "X";
		if(variables.contains("errorNotes;"))
			characterAdded += "Y";
		if(variables.contains("externalTestId;"))
			characterAdded += "Z";
		if(variables.contains("probeA;"))
			characterAdded += "a";
		if(variables.contains("probeB;"))
			characterAdded += "b";
		if(variables.contains("probeC;"))
			characterAdded += "c";
		if(variables.contains("probeD;"))
			characterAdded += "d";
		if(variables.contains("probeE;"))
			characterAdded += "e";
		if(variables.contains("probeSpc"))
			characterAdded += "f";
		if(variables.contains("qc1;"))
			characterAdded += "g";
		if(variables.contains("qc2;"))
			characterAdded += "h";
		if(variables.contains("probeACt;"))
			characterAdded += "i";
		if(variables.contains("probeBCt"))
			characterAdded += "j";
		if(variables.contains("probeCCt;"))
			characterAdded += "k";
		if(variables.contains("probeDCt;"))
			characterAdded += "l";
		if(variables.contains("probeECt;"))
			characterAdded += "m";
		if(variables.contains("probeSpcCt"))
			characterAdded += "n";
		if(variables.contains("qc1Ct;"))
			characterAdded += "o";
		if(variables.contains("qc2Ct;"))
			characterAdded += "p";
		if(variables.contains("probeAEndpt;"))
			characterAdded += "q";
		if(variables.contains("probeBEndpt"))
			characterAdded += "r";
		if(variables.contains("probeCEndpt;"))
			characterAdded += "s";
		if(variables.contains("probeDEndpt;"))
			characterAdded += "t";
		if(variables.contains("probeEEndpt;"))
			characterAdded += "u";
		if(variables.contains("probeSpcEndpt"))
			characterAdded += "v";
		if(variables.contains("qc1Endpt;"))
			characterAdded += "w";
		if(variables.contains("qc2Endpt;"))
			characterAdded += "x";
		return characterAdded;
	}
	
	public String oneChunk(StringBuilder addedHeaderText, String date, String characterHeader, String text){
		addedHeaderText.insert(0, date + "^" + characterHeader + "^" + "1/1" + "^");
		addedHeaderText.append(text);
		return addedHeaderText.toString();
	}
	
	public ArrayList<String> twoChunks(StringBuilder addedHeaderText, String date, String characterHeader, String text){
		int previousIndex = 0;
		int chunkSize = 1;
		int startString = 0;
		int counter = 0;
		int characterCount = 0;
		int j = 0;
		ArrayList<String> completeMessage = new ArrayList<String>();
		for(int i = 0; i < text.length(); i++){
			if(text.charAt(i) == '^'){
				if(i > (130 * chunkSize)){
					// store it in arraylist and then find the length
					addedHeaderText.insert(0, date + "^" + chunkSize + "/^");
					addedHeaderText.append(text.substring(startString, (previousIndex))); 
					// assigning startString the value of previousIndex so that next time the 
					// text starts from where it was left
					startString = previousIndex + 1;
					previousIndex = i;
					// adding the character headers at 19th place as the count of chunks
					// is not yet calculated
					addedHeaderText.insert(18, characterHeader.substring(characterCount, counter) + "^");
					characterCount = counter;
					chunkSize++;
					completeMessage.add(addedHeaderText.toString());
					// resetting the string builder
					addedHeaderText.setLength(0);
				}
				// counter for knowing the headers to be included
				++counter;
				// keeping track of the last index
				previousIndex = i;
			}
			j = i;
		}
		
		if(previousIndex != j){
			// store it in arraylist and then find the length
			addedHeaderText.insert(0, date + "^" + chunkSize + "/^");
			addedHeaderText.append(text.substring(startString)); 
			// assigning startString the value of previousIndex so that next time the 
			// text starts from where it was left
			//startString = previousIndex + 1;
			//previousIndex = i;
			// adding the character headers at 19th place as the count of chunks
			// is not yet calculated
			addedHeaderText.insert(18, characterHeader.substring(characterCount, (counter + 1)) + "^");
			//characterCount = counter;
			//chunkSize++;
			completeMessage.add(addedHeaderText.toString());
			// resetting the string builder
			addedHeaderText.setLength(0);
		}
		
		for(int i = 0; i < completeMessage.size(); i++){
			String temp = completeMessage.get(i);
			temp.indexOf("/");
			completeMessage.set(i, temp.substring(0, (temp.indexOf("/") + 1)) + chunkSize + temp.substring( (temp.indexOf("/") + 1) ) );
		} 
		return completeMessage;
	}
}
