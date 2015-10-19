/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.net;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.constant.FileConstants;
import com.ihsinformatics.xpertsms.model.MessageType;
import com.ihsinformatics.xpertsms.model.ResultsSender;
import com.ihsinformatics.xpertsms.model.XpertResultUploadMessage;
import com.ihsinformatics.xpertsms.parser.astm.ASTMProcessorThread;
import com.ihsinformatics.xpertsms.util.PrintWriterUtil;

/**
 * GeneXpert results processing daemon that provides the messages service
 * 
 * @author ali.habib@irdresearch.org
 */
public class ResultServer extends Thread {
	
	// create socket and listen for results from Xpert
	// when result arrives,
	// pass socket to newly spawned thread
	
	// go back to listening
	// private static Vector<String> messages;
	private ArrayBlockingQueue<String> messages;
	
	private ArrayBlockingQueue<XpertResultUploadMessage> outgoingMessages;
	
	private ResultsSender resultsSender;
	
	private int threadCount;
	
	private int status;
	
	private String errorCode;
	
	private JTextPane monitorPane;
	
	private Boolean stopped;
	
	private ServerSocket socket;
	
	private SimpleDateFormat logEntryFormatter = null;
	
	private SimpleDateFormat fileNameFormatter = null;
	
	private boolean detailedLog;
	
	public ResultServer(JTextPane monitorPane, boolean detailedLog) {
		messages = new ArrayBlockingQueue<String>(15);
		outgoingMessages = new ArrayBlockingQueue<XpertResultUploadMessage>(15);
		logEntryFormatter = new SimpleDateFormat(FileConstants.FILE_ENTRY_DATE_FORMAT);
		fileNameFormatter = new SimpleDateFormat(FileConstants.FILE_NAME_DATE_FORMAT);
		threadCount = 0;
		this.monitorPane = monitorPane;
		this.setDetailedLog(detailedLog);
		stopped = false;
	}
	
	@Override
	public void run() {
		startServer();
		if (status == -1) {
			JOptionPane.showMessageDialog(null, errorCode, "Error occurred", JOptionPane.ERROR_MESSAGE);
		} else if (status == 0) {
			JOptionPane.showMessageDialog(null, MessageCodes.SERVER_STOPPED, "Notification!", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void startServer() {
		socket = null;
		boolean propsLoaded = false;
		try {
			propsLoaded = loadProperties();
		}
		catch (Exception e) {
			e.printStackTrace();
			errorCode = MessageCodes.ERROR_LOADING_PROPERTIES;
			status = -1;
		}
		if (!propsLoaded) {
			errorCode = MessageCodes.PROPERTIES_NOT_SET;
			status = -1;
		}
		// stopped = true;
		ASTMProcessorThread apt = new ASTMProcessorThread(this, detailedLog);
		apt.start();
		resultsSender = new ResultsSender(this);
		resultsSender.start();
		int port = Integer.parseInt(XpertProperties.props.getProperty(XpertProperties.LOCAL_PORT));
		try {
			socket = new ServerSocket(port);
			System.out.println("Listening on port: " + port + "...");
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Could not listen on port " + port);
			errorCode = MessageCodes.PORT_INACCESSIBLE;
			status = -1;
		}
		try {
			while (!stopped) {
				threadCount++;
				PrintWriterUtil.updateTextPane(monitorPane, "Waiting for GeneXpert...", MessageType.INFO, detailedLog);
				ResultThread rt = new ResultThread(socket.accept(), threadCount, this, detailedLog);
				rt.start();
				PrintWriterUtil.updateTextPane(monitorPane, "Connected to GeneXpert", MessageType.INFO, detailedLog);
			}
			if (stopped) {
				PrintWriterUtil.updateTextPane(monitorPane, "Stop message received", MessageType.INFO, detailedLog);
			}
			if (!socket.isClosed())
				socket.close();
			status = 0;
		}
		catch (IOException e) {
			if (stopped) {
				if (!socket.isClosed()) {
					try {
						socket.close();
					}
					catch (IOException e1) {
						e1.printStackTrace();
					}
					
					status = 0;
				}
			} else {
				status = -1;
				errorCode = MessageCodes.GENERAL_ERROR;
				e.printStackTrace();
			}
		}
	}
	
	public boolean loadProperties() throws FileNotFoundException, IOException {
		XpertProperties.props.load(new FileInputStream(FileConstants.FILE_PATH));
		String[] mandatory = { XpertProperties.MTB_CODE, XpertProperties.RIF_CODE, XpertProperties.QC_CODE,
		        XpertProperties.LOCAL_PORT };
		for (String s : mandatory) {
			String property = XpertProperties.props.getProperty(s);
			if (property == null || property.length() == 0) {
				return false;
			}
		}
		return true;
	}
	
	public ArrayBlockingQueue<String> getMessages() {
		return messages;
	}
	
	public void putMessage(String s) {
		try {
			messages.put(s);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String getHead() {
		String s = null;
		try {
			s = messages.take();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			s = null;
		}
		
		return s;
	}
	
	public void removeMessage(int index) {
		messages.remove(index);
	}
	
	public int getMessageListSize() {
		return messages.size();
	}
	
	public ArrayBlockingQueue<XpertResultUploadMessage> getOutgoingMessages() {
		return outgoingMessages;
	}
	
	public int getOutgoingMessageListSize() {
		return outgoingMessages.size();
	}
	
	public void putOutGoingMessage(XpertResultUploadMessage message) {
		try {
			outgoingMessages.put(message);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public XpertResultUploadMessage getOutgoingMessagesHead() {
		XpertResultUploadMessage message = null;
		try {
			message = outgoingMessages.take();
			System.out.println(message.toString().length());
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			message = null;
		}
		return message;
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
	 * @return the monitorPane
	 */
	public JTextPane getMonitorPane() {
		return monitorPane;
	}
	
	/**
	 * @param monitorPane the monitorPane to set
	 */
	public void setMonitorPane(JTextPane monitorPane) {
		this.monitorPane = monitorPane;
	}
	
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * @return the stopped
	 */
	public Boolean getStopped() {
		return stopped;
	}
	
	/**
	 * @param stopped the stopped to set
	 */
	public void setStopped(Boolean stopped) {
		this.stopped = stopped;
		System.out.println("Server: " + stopped.toString());
		
	}
	
	/**
	 * @return the socket
	 */
	public ServerSocket getSocket() {
		return socket;
	}
	
	/**
	 * @param socket the socket to set
	 */
	public void setSocket(ServerSocket socket) {
		this.socket = socket;
	}
	
	public boolean isDetailedLog() {
		return detailedLog;
	}
	
	public void setDetailedLog(boolean detailedLog) {
		this.detailedLog = detailedLog;
	}
	
	public String getLogEntryDateString(Date date) {
		return logEntryFormatter.format(date);
	}
	
	public String getFileNameDateString(Date date) {
		return fileNameFormatter.format(date);
	}
}
