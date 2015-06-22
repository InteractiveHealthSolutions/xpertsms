/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.model.XpertResultUploadMessage;
import com.ihsinformatics.xpertsms.util.PrintWriterUtil;

/**
 * Get/Post XpertSMS results via HTTP(s)
 * 
 * @author ali.habib@irdresearch.org
 */
public class HttpSender {
	
	private PrintWriterUtil printWriter;
	
	File successLog;
	
	public HttpSender() {
	}
	
	public String doPost(XpertResultUploadMessage message, String username, String password, boolean exportProbes) {
		HttpURLConnection httpConnection = null;
		OutputStream outputStream = null;
		// String url = null;
		int responseCode = 0;
		String response = null;
		URL url;
		try {
			url = new URL(getURL(false));
		}
		catch (MalformedURLException e1) {
			e1.printStackTrace();
			printWriter.println("Error submitting Sample ID " + message.getSampleId() + ": " + e1.getMessage(), false);
			return null;
		}
		
		try {
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConnection.setRequestProperty("Content-Language", "en-US");
			httpConnection.setDoOutput(true);
			try {
				outputStream = httpConnection.getOutputStream();
				outputStream.write(message.toPostParams(exportProbes, username, password).getBytes());
				outputStream.flush();
				responseCode = httpConnection.getResponseCode();
			}
			catch (Exception e) {
				e.printStackTrace();
				printWriter.println("Error submitting Sample ID " + message.getSampleId() + ": " + e.getMessage(), false);
				return null;
			}
			if (responseCode != HttpURLConnection.HTTP_OK) {
				printWriter.println("Response Code " + responseCode + " for Sample ID " + message.getSampleId() + ": Could not submit",
				    false);
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String line = "";
			response = "";
			while ((line = br.readLine()) != null) {
				response += line;
			}
		}
		catch (ClassCastException e) {
			// throw new IllegalArgumentException("Not an HTTP URL");
			printWriter.println("Error submitting Sample ID " + message.getSampleId() + ": " + e.getMessage(), false);
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			printWriter.println("Error submitting Sample ID " + message.getSampleId() + ": " + e.getMessage(), false);
			return null;
		}
		catch (SecurityException e) {
			e.printStackTrace();
			printWriter.println("Error submitting Sample ID " + message.getSampleId() + ": " + e.getMessage(), false);
			return null;
		}
		catch (Exception e) {
			if (e instanceof InterruptedException) {
				// TODO: What goes here, Ali?
			}
			e.printStackTrace();
		}
		finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (httpConnection != null) {
				try {
					httpConnection.disconnect();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		return response;
	}
	
	public String getURL(boolean secure) {
		String url = "";
		// url += "http://" + ASTMNetworkConstants.smsServerAddress + ":" + ASTMNetworkConstants.smsServerPort + ASTMNetworkConstants.webappString;
		String webappString = XpertProperties.props.getProperty(XpertProperties.WEB_APP_STRING);
		url += "http" + (secure ? "s" : "") + "://" + webappString;
		return url;
	}
	
	public String doSecurePost(XpertResultUploadMessage message, String username, String password, boolean exportProbes) {
		HttpsURLConnection hc = null;
		OutputStream os = null;
		int responseCode = 0;
		String response = null;
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}
			
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
		}
		catch (NoSuchAlgorithmException e2) {
			e2.printStackTrace();
		}
		try {
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		}
		catch (KeyManagementException e2) {
			e2.printStackTrace();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		/*
		 * end of the fix
		 */
		URL url;
		try {
			url = new URL(getURL(true));
		}
		catch (MalformedURLException e1) {
			e1.printStackTrace();
			printWriter.println("Exception submitting Sample ID " + message.getSampleId() + " for Patient: " + message.getPatientId()
			        + ": " + e1.getMessage(), false);
			return null;
		}
		System.out.println("URL:" + url);
		System.out.println("Connecting");
		try {
			hc = (HttpsURLConnection) url.openConnection();
			hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			hc.setRequestProperty("Content-Language", "en-US");
			System.out.println("Post Paramters :" + message.toPostParams(exportProbes, username, password));
			hc.setDoOutput(true);
			try {
				os = hc.getOutputStream();
				os.write(message.toPostParams(exportProbes, username, password).getBytes());
				os.write("lang=ru".getBytes());
				os.flush();
				responseCode = hc.getResponseCode();
			}
			catch (Exception e) {
				e.printStackTrace();
				printWriter.println(
				    "Exception submitting Sample ID " + message.getSampleId() + " for Patient: " + message.getPatientId()
				            + ": " + e.getMessage(), false);
				return null;
			}
			if (responseCode != HttpsURLConnection.HTTP_OK) {
				printWriter.println("Response Code " + responseCode + " for Sample ID " + message.getSampleId() + " for Patient: "
				        + message.getPatientId() + ": Could not submit", false);
			}
			System.out.println("Parsing response");
			BufferedReader br = new BufferedReader(new InputStreamReader(hc.getInputStream()));
			String line = "";
			response = "";
			while ((line = br.readLine()) != null) {
				response += line;
			}
			System.out.println("Response Complete:\n" + response);
		}
		catch (ClassCastException e) {
			printWriter.println("Exception submitting Sample ID " + message.getSampleId() + " for Patient: " + message.getPatientId()
			        + ": " + e.getMessage(), false);
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			printWriter.println("Exception submitting Sample ID " + message.getSampleId() + " for Patient: " + message.getPatientId()
			        + ": " + e.getMessage(), false);
			return null;
		}
		catch (SecurityException e) {
			e.printStackTrace();
			printWriter.println("Exception submitting Sample ID " + message.getSampleId() + " for Patient: " + message.getPatientId()
			        + ": " + e.getMessage(), false);
			return null;
		}
		catch (Exception e) {
			if (e instanceof InterruptedException) {}
			e.printStackTrace();
		}
		finally {
			if (os != null) {
				try {
					os.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (hc != null) {
				try {
					hc.disconnect();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}
}
