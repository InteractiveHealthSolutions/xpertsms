
package com.ihsinformatics.xpertsms.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.ihsinformatics.xpertsms.constant.FileConstants;
import com.ihsinformatics.xpertsms.constant.XML;
import com.ihsinformatics.xpertsms.model.XpertProperties;
import com.ihsinformatics.xpertsms.model.XpertResultUploadMessage;
import com.ihsinformatics.xpertsms.ui.ControlPanel;

/**
 * 
 * @author ali.habib@irdresearch.org
 *
 */
@SuppressWarnings("deprecation")
public class HttpSender extends Thread
{
	private ResultServer	server;
	private PrintWriter		pw;
	private PrintWriter		sw;

	private int				retries;
	private String			errorMsg;
	private String			msg;
	File					successLog;

	public HttpSender (ResultServer server)
	{
		this.server = server;
		errorMsg = null;
		msg = "";
	}

	public void run ()
	{
		XpertResultUploadMessage message = null;
		String response = "";
		try
		{
			pw = new PrintWriter (new BufferedWriter (new FileWriter (new File (FileConstants.XPERT_SMS_DIR + System.getProperty ("file.separator") + server.getFileNameDateString (new Date ())
					+ "_xpertSMS_send_log.txt"), true)));
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace ();
		}
		catch (IOException e1)
		{
			e1.printStackTrace ();
		}
		try
		{
			successLog = new File (FileConstants.XPERT_SMS_DIR + System.getProperty ("file.separator") + "successLog.txt");
			if (!successLog.exists ())
			{
				successLog.createNewFile ();
			}
			sw = new PrintWriter (new BufferedWriter (new FileWriter (successLog, true)));
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace ();
		}
		catch (IOException e1)
		{
			e1.printStackTrace ();
		}
		while (!server.getStopped ())
		{
			message = server.getOutgoingMessagesHead ();
			if (ControlPanel.props.getProperty (XpertProperties.SMS_EXPORT).equals ("YES"))
			{
				/*
				 * XpertASTMResultUploadMessage xpertMess = new
				 * XpertASTMResultUploadMessage();
				 * xpertMess.setCartridgeId("test");
				 * xpertMess.setInstrumentSerial("34323fffd");
				 * xpertMess.setMessageId("herereretere");
				 * xpertMess.setModuleId("erereffsddrere");
				 * xpertMess.setMtbResult("MTB DETECTED LOW");
				 * xpertMess.setPcId("ALITEST"); xpertMess.setProbeCtA("0.0");
				 * xpertMess.setProbeCtB("1.0"); xpertMess.setProbeCtC("2.0");
				 * xpertMess.setProbeCtD("3.0"); xpertMess.setProbeCtE("4.0");
				 * xpertMess.setProbeCtSPC("5.0");
				 * xpertMess.setProbeEndptA("10.0");
				 * xpertMess.setProbeEndptB("11.0");
				 * xpertMess.setProbeEndptC("12.0");
				 * xpertMess.setProbeEndptD("13.0");
				 * xpertMess.setProbeEndptE("14.0");
				 * xpertMess.setProbeEndptSPC("15.0");
				 * xpertMess.setProbeResultA("POS");
				 * xpertMess.setProbeResultB("NEG");
				 * xpertMess.setProbeResultC("N/A");
				 * xpertMess.setProbeResultD("POS");
				 * xpertMess.setProbeResultE("NEG");
				 * xpertMess.setProbeResultSPC("POS");
				 * xpertMess.setReagentLotId("PPPODLSS");
				 * xpertMess.setRifResult("NOT DETECTED");
				 * xpertMess.setSampleId("123456");//real
				 * //xpertMess.setSampleId("0001011");//fake
				 * xpertMess.setTestEndDate("20130101");
				 * xpertMess.setOperatorId("Ding dong");
				 * xpertMess.setExpDate("20140101");
				 * xpertMess.setPatientId("11305023ALAINBUR");
				 */
				try
				{
					queueSMS (message);
					// queueSMS(xpertMess);
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace ();
					println (e.getMessage (), true);
				}
				catch (SQLException e)
				{
					e.printStackTrace ();
					println (e.getMessage (), true);
				}
			}
			if (ControlPanel.props.getProperty (XpertProperties.WEB_EXPORT).equals ("YES"))
			{
				response = doSecurePost (message);
				parseResponse (response, message);
			}
			if (ControlPanel.props.getProperty (XpertProperties.OPENMRS_EXPORT).equals ("YES"))
			{
				String username = ControlPanel.props.getProperty (XpertProperties.OPENMRS_USER);
				String password = ControlPanel.props.getProperty (XpertProperties.OPENMRS_PASSWORD);
				String url = ControlPanel.props.getProperty (XpertProperties.OPENMRS_REST_ADDRESS);
				OpenMrsApiAuthRest openMrsApiAuthRest = new OpenMrsApiAuthRest (username, password, url);
				response = openMrsApiAuthRest.postResults (message);
				parseResponse (response, message);
			}
			if (ControlPanel.props.getProperty (XpertProperties.GXA_EXPORT).equals ("YES"))
			{
				GxAlertSender gxAlertSender = new GxAlertSender ();
				response = gxAlertSender.postToGxAlert (message);
				parseResponse (response, message);
			}
			
			else
			{
				response = doPost (message);
				parseResponse (response, message);
			}
		}
		if (server.getStopped ())
		{
			println ("Stopping Transmitting Thread!", true);
		}
		pw.flush ();
		pw.close ();
	}

	public String doPost (XpertResultUploadMessage message)
	{
		HttpURLConnection hc = null;
		OutputStream os = null;
		// String url = null;
		int responseCode = 0;
		String response = null;

		URL url;
		try
		{
			url = new URL (getURL (false));
		}
		catch (MalformedURLException e1)
		{
			e1.printStackTrace ();
			println ("Error submitting Sample ID " + message.getSampleId () + ": " + e1.getMessage (), false);
			return null;
		}

		try
		{
			hc = (HttpURLConnection) url.openConnection ();
			hc.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
			hc.setRequestProperty ("Content-Language", "en-US");
			hc.setDoOutput (true);
			try
			{
				os = hc.getOutputStream ();
				os.write (message.toPostParams ().getBytes ());
				os.flush ();
				responseCode = hc.getResponseCode ();
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				println ("Error submitting Sample ID " + message.getSampleId () + ": " + e.getMessage (), false);
				return null;
			}
			if (responseCode != HttpURLConnection.HTTP_OK)
			{
				println ("Response Code " + responseCode + " for Sample ID " + message.getSampleId () + ": Could not submit", false);
			}

			BufferedReader br = new BufferedReader (new InputStreamReader (hc.getInputStream ()));
			String line = "";
			response = "";
			while ((line = br.readLine ()) != null)
			{
				response += line;
			}
		}
		catch (ClassCastException e)
		{
			// throw new IllegalArgumentException("Not an HTTP URL");
			println ("Error submitting Sample ID " + message.getSampleId () + ": " + e.getMessage (), false);
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace ();
			println ("Error submitting Sample ID " + message.getSampleId () + ": " + e.getMessage (), false);
			return null;
		}
		catch (SecurityException e)
		{
			e.printStackTrace ();
			println ("Error submitting Sample ID " + message.getSampleId () + ": " + e.getMessage (), false);
			return null;
		}
		catch (Exception e)
		{
			if (e instanceof InterruptedException)
			{
				// TODO: What goes here, Ali?
			}
			e.printStackTrace ();
		}
		finally
		{
			if (os != null)
			{
				try
				{
					os.close ();
				}
				catch (IOException e)
				{
					e.printStackTrace ();
				}
			}

			if (hc != null)
			{
				try
				{
					hc.disconnect ();
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}

		}
		return response;
	}

	public String getURL (boolean secure)
	{
		String url = "";
		// url += "http://" + ASTMNetworkConstants.smsServerAddress + ":" + ASTMNetworkConstants.smsServerPort + ASTMNetworkConstants.webappString;
		String webappString = ControlPanel.props.getProperty ("webappstring");
		if (webappString.charAt (0) != '/')
			webappString = "/" + webappString;
		
		if (!secure)
			url += "http://" + ControlPanel.props.getProperty ("serverurl") + ":" + ControlPanel.props.getProperty ("serverport") + webappString;
		else
			url += "https://" + ControlPanel.props.getProperty ("serverurl") + ":" + ControlPanel.props.getProperty ("serverport") + webappString;
		return url;
	}

	public void print (String text, boolean toGUI)
	{
		pw.print (server.getLogEntryDateString (new Date ()) + ": " + text);
		pw.flush ();
		if (toGUI)
			server.updateTextPane (server.getLogEntryDateString (new Date ()) + ": " + text);
	}

	public void println (String text, boolean toGUI)
	{
		print (text + "\n", toGUI);
	}

	public void parseResponse (String response, XpertResultUploadMessage xpertMessage)
	{
		boolean success = false;
		boolean retry = true;
		boolean retryExceeded = false;
		retries = 3;
		errorMsg = null;
		msg = "";

		if (xpertMessage.getRetries () >= retries)
			retryExceeded = true;

		if (response == null)
		{
			if (retry && retryExceeded)
			{
				// TODO log error
				println ("Retries exceeded for Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + ": " + "! Please enter manually!\nError Message: "
						+ errorMsg, true);
				// remove from queue and set retry count to zero
				// server.removeOutgoingMessage(0);
			}

			else if (retry)
			{
				println ("Retrying Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + "! Attempts = " + (xpertMessage.getRetries () + 1), true);
				xpertMessage.setRetries (xpertMessage.getRetries () + 1);
				server.putOutGoingMessage (xpertMessage);
			}
			else
			{
				// log error
				println ("Could not send result for Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + "! Please enter manually!\nError Message: "
						+ errorMsg, true);
				// remove from queue
				// server.removeOutgoingMessage(0);
			}
			return;
		}

		// ///determine success
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
		try
		{
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder ();
			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse (new InputSource (new StringBufferInputStream (response)));
			Element domElement = dom.getDocumentElement ();
			NodeList nl = domElement.getChildNodes ();
			Element statusNode = (Element) nl.item (0);
			String status = statusNode.getFirstChild ().getNodeValue ();
			if (status.equals (XML.XML_SUCCESS))
			{
				success = true;
			}
			else if (status.equals (XML.XML_ERROR))
			{
				success = false;
				errorMsg = nl.item (1).getFirstChild ().getNodeValue ();
			}
			else if (status.equals (XML.XML_DIVERTED))
			{
				success = true;
				msg = nl.item (1).getFirstChild ().getNodeValue ();
			}
		}
		catch (ParserConfigurationException pce)
		{
			pce.printStackTrace ();
			// TODO log error
			println ("Error submitting Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + "! Please enter manually!", true);
			println ("Exception submitting Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + ": " + pce.getMessage (), false);
		}
		catch (SAXException se)
		{
			println ("Error submitting Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + "! Please enter manually!", true);
			println ("Exception submitting Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + ": " + se.getMessage (), false);
		}
		catch (IOException ioe)
		{
			println ("Error submitting Sample ID " + xpertMessage.getSampleId () + "! Please enter manually!", true);
			println ("Exception submitting Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + ": " + ioe.getMessage (), false);
		}
		if (success)
		{
			// log success
			if (msg.length () == 0)
			{
				println ("Result for Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + " transmitted sucessfully", true);
				sw.println (xpertMessage.getPatientId () + ":" + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + ":" + server.getLogEntryDateString (new Date ()));
				sw.flush ();
			}
			else
				println ("Result for Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + " not saved - " + msg, true);
		}
		else
		{
			if (retry && retryExceeded)
			{
				// TODO log error
				println ("Retries exceeded for Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + "! Please enter manually!\nError Message: " + errorMsg,
						true);
			}
			else if (retry)
			{
				println ("Retrying Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + "! Attempts = " + (xpertMessage.getRetries () + 1), true);
				xpertMessage.setRetries (xpertMessage.getRetries () + 1);
				server.putOutGoingMessage (xpertMessage);
			}
			else
			{
				// log error
				println ("Could not send result for Sample ID " + xpertMessage.getSampleId () + " for Patient: " + xpertMessage.getPatientId () + "! Please enter manually!\nError Message: "
						+ errorMsg, true);
			}
		}
	}

	private void queueSMS (XpertResultUploadMessage xpertMessage) throws ClassNotFoundException, SQLException
	{
		String dbPortString = ControlPanel.props.getProperty ("localdbport");
		String dbNameString = ControlPanel.props.getProperty ("localdbname");
		String dbUserString = ControlPanel.props.getProperty ("localdbuser");
		String dbPasswordString = ControlPanel.props.getProperty ("localdbpass");
		String dbUrl = "jdbc:mysql://localhost:" + dbPortString + "/" + dbNameString;
		String dbClass = "com.mysql.jdbc.Driver";
		Connection conn = null;
		Class.forName (dbClass);
		conn = DriverManager.getConnection (dbUrl, dbUserString, dbPasswordString);
		String query = xpertMessage.toSqlQuery ();
		Statement stmt = null;
		stmt = conn.createStatement ();
		stmt.executeUpdate (query);
		conn.close ();
	}

	public String doSecurePost (XpertResultUploadMessage message)
	{
		HttpsURLConnection hc = null;
		OutputStream os = null;
		int responseCode = 0;
		String response = null;
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager ()
		{
			public java.security.cert.X509Certificate[] getAcceptedIssuers ()
			{
				return null;
			}
			public void checkClientTrusted (X509Certificate[] certs, String authType)
			{
			}
			public void checkServerTrusted (X509Certificate[] certs, String authType)
			{
			}
		}};
		SSLContext sc = null;
		try
		{
			sc = SSLContext.getInstance ("SSL");
		}
		catch (NoSuchAlgorithmException e2)
		{
			e2.printStackTrace ();
		}
		try
		{
			sc.init (null, trustAllCerts, new java.security.SecureRandom ());
		}
		catch (KeyManagementException e2)
		{
			e2.printStackTrace ();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory (sc.getSocketFactory ());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier ()
		{
			public boolean verify (String hostname, SSLSession session)
			{
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier (allHostsValid);
		/*
		 * end of the fix
		 */
		URL url;
		try
		{
			url = new URL (getURL (true));
		}
		catch (MalformedURLException e1)
		{
			e1.printStackTrace ();
			println ("Exception submitting Sample ID " + message.getSampleId () + " for Patient: " + message.getPatientId () + ": " + e1.getMessage (), false);
			return null;
		}
		System.out.println ("URL:" + url);
		System.out.println ("Connecting");
		try
		{
			hc = (HttpsURLConnection) url.openConnection ();
			hc.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
			hc.setRequestProperty ("Content-Language", "en-US");
			System.out.println ("Post Paramters :" + message.toPostParams ());
			hc.setDoOutput (true);
			try
			{
				os = hc.getOutputStream ();
				os.write (message.toPostParams ().getBytes ());
				os.write ("lang=ru".getBytes ());
				os.flush ();
				responseCode = hc.getResponseCode ();
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				println ("Exception submitting Sample ID " + message.getSampleId () + " for Patient: " + message.getPatientId () + ": " + e.getMessage (), false);
				return null;
			}
			if (responseCode != HttpsURLConnection.HTTP_OK)
			{
				println ("Response Code " + responseCode + " for Sample ID " + message.getSampleId () + " for Patient: " + message.getPatientId () + ": Could not submit", false);
			}
			System.out.println ("Parsing response");
			BufferedReader br = new BufferedReader (new InputStreamReader (hc.getInputStream ()));
			String line = "";
			response = "";
			while ((line = br.readLine ()) != null)
			{
				response += line;
			}
			System.out.println ("Response Complete:\n" + response);
		}
		catch (ClassCastException e)
		{
			println ("Exception submitting Sample ID " + message.getSampleId () + " for Patient: " + message.getPatientId () + ": " + e.getMessage (), false);
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace ();
			println ("Exception submitting Sample ID " + message.getSampleId () + " for Patient: " + message.getPatientId () + ": " + e.getMessage (), false);
			return null;
		}
		catch (SecurityException e)
		{
			e.printStackTrace ();
			println ("Exception submitting Sample ID " + message.getSampleId () + " for Patient: " + message.getPatientId () + ": " + e.getMessage (), false);
			return null;
		}
		catch (Exception e)
		{
			if (e instanceof InterruptedException)
			{
			}
			e.printStackTrace ();
		}
		finally
		{
			if (os != null)
			{
				try
				{
					os.close ();
				}
				catch (IOException e)
				{
					e.printStackTrace ();
				}
			}
			if (hc != null)
			{
				try
				{
					hc.disconnect ();
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
		}
		return response;
	}
}
