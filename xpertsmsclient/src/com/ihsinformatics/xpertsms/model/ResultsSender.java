/*
Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.xpertsms.model;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

/**
 * @author owais.hussain@ihsinformatics.com
 */
public class ResultsSender extends Thread {
	
	private ResultServer server;
	
	private HttpSender httpSender;
	
	private PrintWriter printWriter;
	
	private PrintWriter successWriter;
	
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
			File csvFile = new File(FileConstants.XPERT_SMS_DIR + System.getProperty("file.separator")
			        + DateTimeUtil.getSQLDate(new Date()) + "_xpertdump.csv");
			try {
				csvWriter = new PrintWriter(csvFile);
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isDetailedLog() {
		return detailedLog;
	}
	
	public void setDetailedLog(boolean detailedLog) {
		this.detailedLog = detailedLog;
	}
	
	/**
	 * Prints to text pane and appends a new line feed. If detailed logging is enabled, time stamp
	 * is prepend for each message
	 * 
	 * @param text
	 * @param toGUI
	 */
	public void println(String text, boolean toGUI) {
		printWriter.print(server.getLogEntryDateString(new Date()) + ": " + text + "\n");
		printWriter.flush();
		if (toGUI) {
			String prefix = detailedLog ? server.getLogEntryDateString(new Date()) + ": " : "";
			server.updateTextPane(prefix + text + "\n");
		}
	}
	
	/**
	 * Overloaded method. Converts text into HTML according to message type
	 * 
	 * @param text
	 * @param toGUI
	 */
	public void println(String text, boolean toGUI, MessageType messageType) {
		switch (messageType) {
			case ERROR:
			case EXCEPTION:
				// Set text to red
				break;
			case WARNING:
				// Set text to orange
				break;
			case INFO:
				// Set text to blue
				break;
			case SUCCESS:
				// Set text to green
				break;
		}
		println(text, toGUI);
	}
	
	public void run() {
		XpertResultUploadMessage message = null;
		String response = "";
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(new File(FileConstants.XPERT_SMS_DIR
			        + System.getProperty("file.separator") + server.getFileNameDateString(new Date())
			        + "_xpertSMS_send_log.txt"), true)));
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			successLog = new File(FileConstants.XPERT_SMS_DIR + System.getProperty("file.separator") + "successLog.txt");
			if (!successLog.exists()) {
				successLog.createNewFile();
			}
			successWriter = new PrintWriter(new BufferedWriter(new FileWriter(successLog, true)));
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (IOException e1) {
			e1.printStackTrace();
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
						println(e.getMessage(), true, MessageType.EXCEPTION);
				}
				catch (SQLException e) {
					e.printStackTrace();
					if (detailedLog)
						println(e.getMessage(), true, MessageType.EXCEPTION);
				}
			}
			if (exportWeb) {
				if (XpertProperties.props.getProperty(XpertProperties.WEB_SSL_ENCRYPTION).equals("YES"))
					response = httpSender.doPost(message, username, password, exportProbes);
				else
					response = httpSender.doSecurePost(message, username, password, exportProbes);
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
				response = httpSender.doPost(message, username, password, exportProbes);
				parseResponse(response, message);
			}
		}
		if (server.getStopped()) {
			println("Stopping Transmitting Thread!", true, MessageType.INFO);
		}
		printWriter.flush();
		printWriter.close();
	}
	
	public synchronized void queueSms(XpertResultUploadMessage xpertMessage) throws ClassNotFoundException, SQLException {
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
		String query = xpertMessage.toSqlQuery();
		Statement stmt = null;
		stmt = conn.createStatement();
		stmt.executeUpdate(query);
		conn.close();
	}
	
	public synchronized void writeToCsv(String text) {
		System.out.println(text);
		System.out.println("Writing to CSV file ....");
		csvWriter.println(text);
		csvWriter.flush();
		System.out.println("printed");
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
				println(
				    "Retries exceeded for Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				            + xpertMessage.getPatientId() + ": " + "! Please enter manually!", true, MessageType.ERROR);
				// remove from queue and set retry count to zero
				// server.removeOutgoingMessage(0);
			} else if (retry) {
				println("Retrying Sample ID " + xpertMessage.getSampleId() + " for Patient: " + xpertMessage.getPatientId()
				        + "! Attempts = " + (xpertMessage.getRetries() + 1), true, MessageType.WARNING);
				xpertMessage.setRetries(xpertMessage.getRetries() + 1);
				server.putOutGoingMessage(xpertMessage);
			} else {
				println("Could not send result for Sample ID " + xpertMessage.getSampleId() + " for Patient: "
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
			println(message, true, MessageType.EXCEPTION);
		}
		catch (SAXException e) {
			String message = "Error in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
			        + xpertMessage.getPatientId() + "! Please enter manually!";
			if (detailedLog)
				message = "Exception in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + ": " + e.getMessage();
			println(message, true, MessageType.EXCEPTION);
		}
		catch (IOException e) {
			String message = "Error in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
			        + xpertMessage.getPatientId() + "! Please enter manually!";
			if (detailedLog)
				message = "Exception in submitting Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + ": " + e.getMessage();
			println(message, true, MessageType.EXCEPTION);
		}
		if (success) {
			// log success
			if (message.length() == 0) {
				println(
				    "Result for Sample ID " + xpertMessage.getSampleId() + " for Patient: " + xpertMessage.getPatientId()
				            + " transmitted sucessfully", true, MessageType.SUCCESS);
				successWriter.println(xpertMessage.getPatientId() + ":" + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + ":" + server.getLogEntryDateString(new Date()));
				successWriter.flush();
			} else
				println(
				    "Result for Sample ID " + xpertMessage.getSampleId() + " for Patient: " + xpertMessage.getPatientId()
				            + " not saved. " + message, true, MessageType.ERROR);
		} else {
			if (retry && retryExceeded) {
				println(
				    "Retries exceeded for Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				            + xpertMessage.getPatientId() + "! Please enter manually!\nError Message: " + errorMessage,
				    true, MessageType.ERROR);
			} else if (retry) {
				println("Retrying Sample ID " + xpertMessage.getSampleId() + " for Patient: " + xpertMessage.getPatientId()
				        + "! Attempts = " + (xpertMessage.getRetries() + 1), true, MessageType.ERROR);
				xpertMessage.setRetries(xpertMessage.getRetries() + 1);
				server.putOutGoingMessage(xpertMessage);
			} else {
				println("Could not send result for Sample ID " + xpertMessage.getSampleId() + " for Patient: "
				        + xpertMessage.getPatientId() + "! Please enter manually!\nError Message: " + errorMessage, true,
				    MessageType.ERROR);
			}
		}
	}
}
