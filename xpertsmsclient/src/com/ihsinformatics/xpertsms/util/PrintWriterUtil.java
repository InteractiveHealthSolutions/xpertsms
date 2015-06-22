/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.ihsinformatics.xpertsms.model.MessageType;
import com.ihsinformatics.xpertsms.net.ResultServer;
import com.ihsinformatics.xpertsms.ui.XpertActivityViewer;

/**
 * @author owais.hussain@ihsinformatics.com
 */
public class PrintWriterUtil extends PrintWriter {
	
	private ResultServer server;
	
	public PrintWriterUtil(ResultServer server, String fileName) throws IOException {
		super(new BufferedWriter(new FileWriter(new File(fileName), true)));
		this.server = server;
	}
	
	/**
	 * Prints to text pane and appends a new line feed. If detailed logging is enabled, time stamp
	 * is prepend for each message
	 * 
	 * @param text
	 * @param toGUI
	 */
	public void println(String text, boolean toGUI) {
		print(server.getLogEntryDateString(new Date()) + ": " + text + "\n");
		flush();
		if (toGUI) {
			XpertActivityViewer.updateTextPane(text);
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
//				text = "<html><b style=\"color:red\">" + text + "</b></html>";
				break;
			case WARNING:
//				text = "<html><b style=\"color:orange\">" + text + "</b></html>";
				break;
			case INFO:
//				text = "<html><b style=\"color:blue\">" + text + "</b></html>";
				break;
			case SUCCESS:
//				text = "<html><b style=\"color:green\">" + text + "</b></html>";
				break;
		}
		println(text, toGUI);
	}
}
