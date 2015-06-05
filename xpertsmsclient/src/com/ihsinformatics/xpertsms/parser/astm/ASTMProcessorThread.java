/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.parser.astm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.constant.ASTMMessageConstants;
import com.ihsinformatics.xpertsms.constant.FileConstants;
import com.ihsinformatics.xpertsms.model.ResultsSender;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;
import com.ihsinformatics.xpertsms.net.ResultServer;
import com.ihsinformatics.xpertsms.net.exception.InvalidASTMMessageFormatException;

/**
 * Processes single result set of GeneXpert results in ASTM standard
 * 
 * @author ali.habib@irdresearch.org
 */
public class ASTMProcessorThread extends Thread {
	
	private ResultServer server;
	
	private PrintWriter pw;
	
	public ASTMProcessorThread(ResultServer server) {
		this.server = server;
	}
	
	public void run() {
		
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FileConstants.XPERT_SMS_DIR
			        + System.getProperty("file.separator") + server.getFileNameDateString(new Date())
			        + "_XpertSMS_proc_log.txt"), true)));
			println("Processing Thread Started", true);
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		catch (IOException e1) {
			e1.printStackTrace();
		}
		while (!server.getStopped()) {
			if (server.getMessageListSize() > 0) {
				doTriage();
				
			}
		}
		if (server.getStopped()) {
			println("Stopping Processing Thread", true);
		}
		pw.flush();
		pw.close();
		
	}
	
	public void doTriage() {
		String message = server.getHead();
		server.removeMessage(0);
		
		String lines[] = message.split("[\r\n]+");
		
		XpertASTMResultUploadMessage xpertMessage = new XpertASTMResultUploadMessage();
		String line = "";
		for (int i = 0; i < lines.length; i++) {
			line = lines[i];
			
			if (line.trim().equals(""))
				continue;
			
			char recordType = line.charAt(0);
			
			switch (recordType) {
				case ASTMMessageConstants.HEADER_RECORD:
					HeaderParser hp = new HeaderParser(xpertMessage, line);
					try {
						hp.parse();
					}
					catch (InvalidASTMMessageFormatException e) {
						
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					break;
				
				case ASTMMessageConstants.ORDER_RECORD:
					OrderRecordParser op = new OrderRecordParser(xpertMessage, line);
					try {
						op.parse();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					break;
				
				case ASTMMessageConstants.PATIENT_RECORD:
					PatientRecordParser pp = new PatientRecordParser(xpertMessage, line);
					try {
						pp.parse();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					break;
				
				case ASTMMessageConstants.RESULT_RECORD:
					ResultRecordParser rp = new ResultRecordParser(xpertMessage, line);
					try {
						rp.parse();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					break;
				
				case ASTMMessageConstants.TERMINATOR_RECORD:
					TerminatorRecordParser tp = new TerminatorRecordParser(xpertMessage, line);
					try {
						tp.parse();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					break;
				
				case ASTMMessageConstants.COMMENT_RECORD:
					CommentRecordParser cp = new CommentRecordParser(xpertMessage, line);
					try {
						cp.parse();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					break;
				
				default:
					println("***NEW***:|" + line + "|", true);
			}
		}
		if (xpertMessage.getSampleId() != null) {
			/* CSV is being written by ResultsSender */
			if (!XpertProperties.props.getProperty(XpertProperties.CSV_EXPORT).equals("YES")) {
				server.putOutGoingMessage(xpertMessage);
				println("Result for sample " + xpertMessage.getSampleId() + " queued for transmission", true);
			}
		}
		
	}
	
	public void print(String text, boolean toGUI) {
		
		pw.print(server.getLogEntryDateString(new Date()) + ": " + text);
		pw.flush();
		if (toGUI)
			server.updateTextPane(server.getLogEntryDateString(new Date()) + ": " + text);
	}
	
	public void println(String text, boolean toGUI) {
		
		print(text + "\n", toGUI);
	}
}
