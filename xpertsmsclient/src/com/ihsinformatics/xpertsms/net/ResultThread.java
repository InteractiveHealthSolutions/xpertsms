/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.ihsinformatics.xpertsms.constant.ASTMNetworkConstants;
import com.ihsinformatics.xpertsms.constant.FileConstants;
import com.ihsinformatics.xpertsms.model.MessageType;
import com.ihsinformatics.xpertsms.util.PrintWriterUtil;

/**
 * Processes Results from GeneXpert DX
 * 
 * @author ali.habib@irdresearch.org
 */
public class ResultThread extends Thread {
	
	private Socket socket = null;
	
	int threadCount;
	
	// private XpertASTMResultUploadMessage xpertMessage;
	private PrintWriterUtil printWriter;
	
	private int frameCounter;
	
	private ResultServer server;
	
	public ResultThread(Socket socket, int threadCount, ResultServer server, boolean detailedLog) {
		super("resultthread");
		this.threadCount = threadCount;
		this.socket = socket;
		printWriter = null;
		frameCounter = 0;
		this.server = server;
	}
	
	public void run() {
		try {
			printWriter = new PrintWriterUtil(server, FileConstants.XPERT_SMS_DIR
			        + System.getProperty("file.separator") + "log_t" + threadCount + ".txt");
		}
        catch (IOException e) {
	        e.printStackTrace();
        }
		printWriter.println("Establishing XpertSMS connection", true, MessageType.INFO);
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			printWriter.println("Establishing XpertSMS connection", true, MessageType.INFO);
			boolean etxRecvd = false;
			byte b;
			byte[] textByte = new byte[1];
			String text = "";
			String message = "";
			printWriter.println("Reading...", true, MessageType.INFO);
			b = dis.readByte();
			System.out.print(b);
			// Establishment phase ends
			// Contention phase won't happen because receiver never sends ENQ
			// Messages
			while (!server.getStopped() && (b = dis.readByte()) != ASTMNetworkConstants.NULL) {
				if (b == ASTMNetworkConstants.ENQ) {
					dos.write(ASTMNetworkConstants.ACK);
				} else if (b == ASTMNetworkConstants.STX) {
					dis.readByte();
					frameCounterPlusPlus();
					while (!server.getStopped()) {
						textByte[0] = dis.readByte();
						// System.out.println("received text " + textByte[0]);
						if (textByte[0] == ASTMNetworkConstants.ETB || textByte[0] == ASTMNetworkConstants.ETX) {
							b = textByte[0];
							break;
						}
						text += new String(textByte);
					}
				}
				if (b == ASTMNetworkConstants.ETB) {
					// expect next frame
					// println(pw,"ETB received");
				} else if (b == ASTMNetworkConstants.ETX) {
					// println(pw,"ETX received");
					etxRecvd = true;
					// last frame
				} else if (b == ASTMNetworkConstants.CR) {
					// println(pw,"CR received");
					// expect LF
					if ((b = dis.readByte()) == ASTMNetworkConstants.LF) {// if LF end, else keep going
						                                                  // println(pw,"LF Received");
						message += text;
						text = "";
						if (etxRecvd) {
							// dos.write(NetworkConstants.EOT);
							etxRecvd = false;
							// process();
						}
						dos.write(ASTMNetworkConstants.ACK);
					}
				} else if (b == ASTMNetworkConstants.EOT) {
					// break;
					// println(pw,"EOT Received.");
					// println(pw,"message\n" + message);
					server.putMessage(message);
					printWriter.println("Result Received", true, MessageType.INFO);
					message = "";
				}
				
				else {
					System.out.println("Other byte: " + b);
				}
			}
			if (server.getStopped()) {
				printWriter.println("Stop Message Received!", true, MessageType.INFO);
			}
			if (socket != null)
				socket.close();
			
			message = null;
			printWriter.println("Clean up completed", true, MessageType.INFO);
			printWriter.flush();
			printWriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void frameCounterPlusPlus() {
		frameCounter = frameCounter + 1;
		if (frameCounter == 8)
			frameCounter = 0;
	}
}
