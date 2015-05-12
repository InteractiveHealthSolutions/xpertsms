package com.ihsinformatics.xpertsms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import com.ihsinformatics.xpertsms.model.XpertResultUploadMessage;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;
import com.ihsinformatics.xpertsms.util.CsvUtil;

public class TestClass {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XpertASTMResultUploadMessage[] sampleMessages = getMessagesFromCsv();
		for (int i = 0; i < sampleMessages.length; i++) {
			JSONObject json = sampleMessages[i].toJson();
			json.put("apiKey", "ph8trUgestejugudR6fRa6he5u6heveZpruwuWrAthUruFuhuxuRe8ruGunuthub");
			String response = "";
			// Enable to test GXA
			// response = postToGxa(json.toString());
			// Enable to test Web
			{
				SimpleDateFormat messageFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sampleMessages[i].setExpDate(messageFormat.format(new Date()));
				sampleMessages[i].setMessageDateTime(messageFormat.format(new Date()));
				sampleMessages[i].setOrderDateTime(messageFormat.format(new Date()));
				sampleMessages[i].setTestStartDate(messageFormat.format(new Date()));
				sampleMessages[i].setTestEndDate(messageFormat.format(new Date()));
				
				String mtb = sampleMessages[i].getMtbResult().equals("1") ? "MTB DETECTED HIGH" : sampleMessages[i]
				        .getMtbResult().equals("2") ? "MTB DETECTED MEDIUM" : "MTB NOT DETECTED";
				String rif = sampleMessages[i].getRifResult().equals("1") ? "RIF Resistance DETECTED" : sampleMessages[i]
				        .getRifResult().equals("2") ? "RIF Resistance NOT DETECTED" : "RIF Resistance INDETERMINATE";
				sampleMessages[i].setMtbResult(mtb);
				sampleMessages[i].setRifResult(rif);
				postToWeb(sampleMessages[i]);
			}
			System.out.println(response);
		}
		
		JSONObject sampleObj = new JSONObject();
		sampleObj.put("apiKey", "ph8trUgestejugudR6fRa6he5u6heveZpruwuWrAthUruFuhuxuRe8ruGunuthub");
		sampleObj.put("assayHostTestCode", "MTB-RIF");
		sampleObj.put("assay", "Xpert MTB-RIF Assay");
		sampleObj.put("assayVersion", "5");
		sampleObj.put("sampleId", "130514_12544_00001");
		sampleObj.put("patientId", "101130800001-9");
		sampleObj.put("user", "OWAIS");
		sampleObj.put("testStartedOn", "2014-05-22T17:42:55Z");
		sampleObj.put("testEndedOn", "2014-05-22T20:33:12Z");
		sampleObj.put("messageSentOn", "2014-05-23T18:21:45Z");
		sampleObj.put("reagentLotId", "10713-AX");
		sampleObj.put("cartridgeExpirationDate", "2014-05-21T18:21:45Z");
		sampleObj.put("cartridgeSerial", "204304821");
		sampleObj.put("moduleSerial", "618255");
		sampleObj.put("instrumentSerial", "708228");
		sampleObj.put("softwareVersion", "4.4a");
		sampleObj.put("resultIdMtb", 2);
		sampleObj.put("resultIdRif", 2);
		sampleObj.put("resultText", "MTB DETECTED MEDIUM|Rif Resistance NOT DETECTED");
		sampleObj.put("deviceSerial", "CEPHEID5G183R1");
		sampleObj.put("hostId", "Machine API Test");
		sampleObj.put("systemName", "GeneXpert PC");
		sampleObj.put("computerName", "CepheidJRJRFQ1");
		sampleObj.put("notes", "Nothing serious, just XDR-TB");
		sampleObj.put("errorCode", "");
		sampleObj.put("errorNotes", "");
		sampleObj.put("externalTestId", "X-123-4-XXZ");
		sampleObj.put("probeA", "NEG");
		sampleObj.put("probeB", "POS");
		sampleObj.put("probeC", "NO RESULT");
		sampleObj.put("probeD", "NEG");
		sampleObj.put("probeE", "NEG");
		sampleObj.put("probeSpc", "POS");
		sampleObj.put("probeACt", 0);
		sampleObj.put("probeBCt", 1.1);
		sampleObj.put("probeCCt", 2.2);
		sampleObj.put("probeDCt", 3.3);
		sampleObj.put("probeECt", 4.4);
		sampleObj.put("probeSpcCt", 5.5);
		sampleObj.put("probeAEndpt", 8.8);
		sampleObj.put("probeBEndpt", 9.9);
		sampleObj.put("probeCEndpt", 1.2);
		sampleObj.put("probeDEndpt", 2.3);
		sampleObj.put("probeEEndpt", 3.4);
		sampleObj.put("probeSpcEndpt", 4.5);
	}
	
	private static XpertASTMResultUploadMessage[] getMessagesFromCsv() {
		String sep = System.getProperty("file.separator");
		String csvPath = System.getProperty("user.dir") + sep + "res" + sep + "GxaSamples.csv";
		CsvUtil csvUtil = new CsvUtil(csvPath, true);
		String[][] data = csvUtil.readData();
		XpertASTMResultUploadMessage[] sampleMessages = new XpertASTMResultUploadMessage[data.length];
		for (int i = 0; i < data.length; i++) {
			try {
				int j = 0;
				sampleMessages[i] = new XpertASTMResultUploadMessage();
				sampleMessages[i].setUniversalTestId(data[i][j++]);
				sampleMessages[i].setSystemDefinedTestName(data[i][j++]);
				sampleMessages[i].setSystemDefinedTestVersion(data[i][j++]);
				sampleMessages[i].setSampleId(data[i][j++]);
				sampleMessages[i].setPatientId(data[i][j++]);
				sampleMessages[i].setOperatorId(data[i][j++]);
				sampleMessages[i].setTestStartDate(data[i][j++]);
				sampleMessages[i].setTestEndDate(data[i][j++]);
				j++; // Message sent on is not recorded
				sampleMessages[i].setReagentLotId(data[i][j++]);
				sampleMessages[i].setExpDate(data[i][j++]);
				sampleMessages[i].setCartridgeId(data[i][j++]);
				sampleMessages[i].setModuleId(data[i][j++]);
				sampleMessages[i].setInstrumentSerial(data[i][j++]);
				sampleMessages[i].setSoftwareVersion(data[i][j++]);
				sampleMessages[i].setMtbResult(data[i][j++]);
				sampleMessages[i].setRifResult(data[i][j++]);
				j++; // Result Text is implemented in
				     // XpertASTMResultUploadMethod
				sampleMessages[i].setPcId(data[i][j++]);
				sampleMessages[i].setSystemId(data[i][j++]);
				sampleMessages[i].setSystemName(data[i][j++]);
				sampleMessages[i].setComputerName(data[i][j++]);
				sampleMessages[i].setNotes(data[i][j++]);
				sampleMessages[i].setErrorCode(data[i][j++]);
				sampleMessages[i].setErrorNotes(data[i][j++]);
				sampleMessages[i].setMessageId(data[i][j++]);
				sampleMessages[i].setProbeResultA(data[i][j++]);
				sampleMessages[i].setProbeResultB(data[i][j++]);
				sampleMessages[i].setProbeResultC(data[i][j++]);
				sampleMessages[i].setProbeResultD(data[i][j++]);
				sampleMessages[i].setProbeResultE(data[i][j++]);
				sampleMessages[i].setProbeResultSPC(data[i][j++]);
				sampleMessages[i].setProbeCtA(data[i][j++]);
				sampleMessages[i].setProbeCtB(data[i][j++]);
				sampleMessages[i].setProbeCtC(data[i][j++]);
				sampleMessages[i].setProbeCtD(data[i][j++]);
				sampleMessages[i].setProbeCtE(data[i][j++]);
				sampleMessages[i].setProbeCtSPC(data[i][j++]);
				sampleMessages[i].setProbeEndPtA(data[i][j++]);
				sampleMessages[i].setProbeEndPtB(data[i][j++]);
				sampleMessages[i].setProbeEndPtC(data[i][j++]);
				sampleMessages[i].setProbeEndPtD(data[i][j++]);
				sampleMessages[i].setProbeEndPtE(data[i][j++]);
				sampleMessages[i].setProbeEndPtSPC(data[i][j++]);
				SimpleDateFormat messageFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				sampleMessages[i].setMessageDateTime(messageFormat.format(new Date()));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sampleMessages;
	}
	
	public static String postToGxa(String jsonString) {
		HttpURLConnection httpConnection = null;
		OutputStream outStream = null;
		int responseCode = 0;
		String response = null;
		String url = "http://dev.gxalert.com/api/result";
		try {
			URL gxaUrl = new URL(url);
			byte[] content = jsonString.getBytes();
			httpConnection = (HttpURLConnection) gxaUrl.openConnection();
			httpConnection.setRequestProperty("Content-Length", String.valueOf(content.length));
			httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			httpConnection.setRequestProperty("Host", "dev.gxalert.com");
			httpConnection.setRequestMethod("POST");
			httpConnection.setDoOutput(true);
			outStream = httpConnection.getOutputStream();
			System.out.println(new String(content));
			outStream.write(content);
			outStream.flush();
			responseCode = httpConnection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println("Response Code: " + responseCode);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String line = "";
			response = "";
			while ((line = br.readLine()) != null) {
				response += line;
			}
			try {
				outStream.close();
				httpConnection.disconnect();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public static String postToWeb(XpertResultUploadMessage message) {
		String address = "http://localhost:8888/xpertsmsweb.jsp";
		HttpURLConnection hc = null;
		OutputStream os = null;
		int responseCode = 0;
		String response = null;
		URL url;
		try {
			url = new URL(address);
			hc = (HttpURLConnection) url.openConnection();
			hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			hc.setRequestProperty("Content-Language", "en-US");
			hc.setDoOutput(true);
			try {
				os = hc.getOutputStream();
				String params = message.toPostParams(true, "admin", "jingle94");
				os.write(params.getBytes());
				os.flush();
				responseCode = hc.getResponseCode();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println("Response Code " + responseCode + " for Sample ID " + message.getSampleId()
				        + ": Could not submit");
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(hc.getInputStream()));
			String line = "";
			response = "";
			while ((line = br.readLine()) != null) {
				response += line;
			}
		}
		catch (ClassCastException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
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