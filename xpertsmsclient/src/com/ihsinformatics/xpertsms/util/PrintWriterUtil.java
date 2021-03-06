/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.ihsinformatics.xpertsms.model.MessageType;
import com.ihsinformatics.xpertsms.net.ResultServer;

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
	 * is prepend for each message. Styles the message in HTML according to messageType
	 * 
	 * @param text
	 * @param toGUI
	 * @param messageType
	 */
	public void println(String text, boolean toGUI, MessageType messageType) {
		if (toGUI) {
			updateTextPane(server.getMonitorPane(), text, messageType, server.isDetailedLog());
		}
		text = messageType.toString() + ":" + text;
		print(server.getLogEntryDateString(new Date()) + ": " + text + "\n");
		flush();
	}
	
	public synchronized static void updateTextPane(final JTextPane textPane, final String text,
	                                               final MessageType messageType, final boolean detailedLog) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				String prefix = detailedLog ? DateTimeUtil.getSQLDateTime(new Date()) + ": " : "";
				String allText = prefix + text + "\n";
				StyledDocument styledDoc = textPane.getStyledDocument();
				SimpleAttributeSet attrs = new SimpleAttributeSet();
				StyleConstants.setFontSize(attrs, 14);
				try {
					switch (messageType) {
						case ERROR:
						case EXCEPTION:
							StyleConstants.setForeground(attrs, Color.RED);
							StyleConstants.setBold(attrs, true);
							break;
						case WARNING:
							StyleConstants.setForeground(attrs, Color.ORANGE);
							StyleConstants.setBold(attrs, true);
							break;
						case INFO:
							StyleConstants.setForeground(attrs, Color.BLUE.darker());
							break;
						case SUCCESS:
							StyleConstants.setForeground(attrs, Color.GREEN.darker());
							break;
					}
					styledDoc.insertString(styledDoc.getLength(), allText, attrs);
				}
				catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
				textPane.setCaretPosition(styledDoc.getLength() - 1);
			}
		});
	}
}
