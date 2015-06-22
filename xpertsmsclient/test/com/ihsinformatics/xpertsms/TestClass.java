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
			/*String message1 = "H|@^\\|URM-tTjX+pTA-01||GeneXpert PC^GeneXpert^4.3|||||IRD_XPERT|| P|1394-97|20121020053034\n"
			+ "P|1|||1621005001\nO|1|051012274||^^^G4v5|R|20121006153947|||||||||ORH||||||||||F\nR|1|^G4v5^^TBPos^Xpert MTB-RIF Assay G4^5^MTB^|MTB DETECTED VERY LOW^|||||F||Karachi X-ray|20121006153947|20121006172116|Cepheid3WDBYQ1^707851^615337^101256275^04405^20130106\n"
			+ "R|2|^G4v5^^TBPos^^^Probe D^|POS^|||\nR|3|^G4v5^^TBPos^^^Probe D^Ct|^32.4|||\nR|4|^G4v5^^TBPos^^^Probe D^EndPt|^102.0|||\nR|5|^G4v5^^TBPos^^^Probe C^|POS^|||\nR|6|^G4v5^^TBPos^^^Probe C^Ct|^31.2|||\n"
			+ "R|7|^G4v5^^TBPos^^^Probe C^EndPt|^130.0|||\nR|8|^G4v5^^TBPos^^^Probe E^|NEG^|||\nR|9|^G4v5^^TBPos^^^Probe E^Ct|^0|||\nR|10|^G4v5^^TBPos^^^Probe E^EndPt|^4.0|||\nR|11|^G4v5^^TBPos^^^Probe B^|POS^|||\nR|12|^G4v5^^TBPos^^^Probe B^Ct|^31.5|||\nR|13|^G4v5^^TBPos^^^Probe B^EndPt|^88.0|||\n"
			+ "R|14|^G4v5^^TBPos^^^Probe A^|POS^|||\nR|15|^G4v5^^TBPos^^^Probe A^Ct|^30.7|||\nR|16|^G4v5^^TBPos^^^Probe A^EndPt|^92.0|||\nR|17|^G4v5^^TBPos^^^SPC^|NA^|||\nR|18|^G4v5^^TBPos^^^SPC^Ct|^28.1|||\nR|19|^G4v5^^TBPos^^^SPC^EndPt|^302.0|||\nR|20|^G4v5^^Rif^Xpert MTB-RIF Assay G4^5^Rif Resistance^|Rif Resistance DETECTED^|||||F||Karachi X-ray|20121006153947|20121006172116|Cepheid3WDBYQ1^707851^615337^101256275^04405^20130106"
			+ "R|21|^G4v5^^Rif^^^Probe D^|POS^|||\nR|22|^G4v5^^Rif^^^Probe D^Ct|^32.4|||\nR|23|^G4v5^^Rif^^^Probe D^EndPt|^102.0|||\nR|24|^G4v5^^Rif^^^Probe C^|POS^|||\nR|25|^G4v5^^Rif^^^Probe C^Ct|^31.2|||\nR|26|^G4v5^^Rif^^^Probe C^EndPt|^130.0|||\nR|27|^G4v5^^Rif^^^Probe E^|NEG^|||\nR|28|^G4v5^^Rif^^^Probe E^Ct|^0|||\nR|29|^G4v5^^Rif^^^Probe E^EndPt|^4.0|||\n"
			+ "R|30|^G4v5^^Rif^^^Probe B^|POS^|||\nR|31|^G4v5^^Rif^^^Probe B^Ct|^31.5|||\nR|32|^G4v5^^Rif^^^Probe B^EndPt|^88.0|||\nR|33|^G4v5^^Rif^^^Probe A^|POS^|||\nR|34|^G4v5^^Rif^^^Probe A^Ct|^30.7|||\nR|35|^G4v5^^Rif^^^Probe A^EndPt|^92.0|||\nR|36|^G4v5^^Rif^^^SPC^|NA^|||\nR|37|^G4v5^^Rif^^^SPC^Ct|^28.1|||\nR|38|^G4v5^^Rif^^^SPC^EndPt|^302.0|||\nR|39|^G4v5^^QC^Xpert MTB-RIF Assay G4^5^QC Check^|^|||||F||Karachi X-ray|20121006153947|20121006172116|Cepheid3WDBYQ1^707851^615337^101256275^04405^20130106\nR|40|^G4v5^^QC^^^QC-1^|NEG^|||\n"
			+ "R|41|^G4v5^^QC^^^QC-1^Ct|^0|||\nR|42|^G4v5^^QC^^^QC-1^EndPt|^0|||\nR|43|^G4v5^^QC^^^QC-2^|NEG^|||\nR|44|^G4v5^^QC^^^QC-2^Ct|^0|||\nR|45|^G4v5^^QC^^^QC-2^EndPt|^0|||\nL|1|N";
		putMessage(message1);
		String message2 = "H|@^\\|URM-b+UY+pTA-02||GeneXpert PC^GeneXpert^4.3|||||IRD_XPERT||P|1394-97|20121020053357\n"
			+ "P|1|||7761\nO|1|814913||^^^G4v5|R|20120913003937|||||||||ORH||||||||||F\nR|1|^G4v5^^TBPos^Xpert MTB-RIF Assay G4^5^MTB^|MTB DETECTED MEDIUM^|||||F||Sunil Asif|20120913003937|20120913022015|Cepheid3WDBYQ1^706593^611954^101259317^04405^20130106\nR|2|^G4v5^^TBPos^^^Probe D^|POS^|||\n"
			+ "R|3|^G4v5^^TBPos^^^Probe D^Ct|^18.9|||\nR|4|^G4v5^^TBPos^^^Probe D^EndPt|^242.0|||\nR|5|^G4v5^^TBPos^^^Probe C^|POS^|||\nR|6|^G4v5^^TBPos^^^Probe C^Ct|^18.2|||\nR|7|^G4v5^^TBPos^^^Probe C^EndPt|^250.0|||\nR|8|^G4v5^^TBPos^^^Probe E^|NEG^|||\nR|9|^G4v5^^TBPos^^^Probe E^Ct|^0|||\nR|10|^G4v5^^TBPos^^^Probe E^EndPt|^-3.0|||\n"
			+ "R|11|^G4v5^^TBPos^^^Probe B^|POS^|||\nR|12|^G4v5^^TBPos^^^Probe B^Ct|^19.2|||\nR|13|^G4v5^^TBPos^^^Probe B^EndPt|^133.0|||\nR|14|^G4v5^^TBPos^^^Probe A^|POS^|||\nR|15|^G4v5^^TBPos^^^Probe A^Ct|^17.5|||\nR|16|^G4v5^^TBPos^^^Probe A^EndPt|^156.0|||\nR|17|^G4v5^^TBPos^^^SPC^|NA^|||\nR|18|^G4v5^^TBPos^^^SPC^Ct|^27.4|||\n"
			+ "R|19|^G4v5^^TBPos^^^SPC^EndPt|^333.0|||\nR|20|^G4v5^^Rif^Xpert MTB-RIF Assay G4^5^Rif Resistance^|Rif Resistance DETECTED^|||||F||Sunil Asif|20120913003937|20120913022015|Cepheid3WDBYQ1^706593^611954^101259317^04405^20130106\nR|21|^G4v5^^Rif^^^Probe D^|POS^|||\nR|22|^G4v5^^Rif^^^Probe D^Ct|^18.9|||\nR|23|^G4v5^^Rif^^^Probe D^EndPt|^242.0|||\nR|24|^G4v5^^Rif^^^Probe C^|POS^|||\n"
			+ "R|25|^G4v5^^Rif^^^Probe C^Ct|^18.2|||\nR|26|^G4v5^^Rif^^^Probe C^EndPt|^250.0|||\nR|27|^G4v5^^Rif^^^Probe E^|NEG^|||\nR|28|^G4v5^^Rif^^^Probe E^Ct|^0|||\nR|29|^G4v5^^Rif^^^Probe E^EndPt|^-3.0|||\nR|30|^G4v5^^Rif^^^Probe B^|POS^|||\nR|31|^G4v5^^Rif^^^Probe B^Ct|^19.2|||\nR|32|^G4v5^^Rif^^^Probe B^EndPt|^133.0|||\nR|33|^G4v5^^Rif^^^Probe A^|POS^|||\nR|34|^G4v5^^Rif^^^Probe A^Ct|^17.5|||\n"
			+ "R|35|^G4v5^^Rif^^^Probe A^EndPt|^156.0|||\nR|36|^G4v5^^Rif^^^SPC^|NA^|||\nR|37|^G4v5^^Rif^^^SPC^Ct|^27.4|||\nR|38|^G4v5^^Rif^^^SPC^EndPt|^333.0|||\nR|39|^G4v5^^QC^Xpert MTB-RIF Assay G4^5^QC Check^|^|||||F||Sunil Asif|20120913003937|20120913022015|Cepheid3WDBYQ1^706593^611954^101259317^04405^20130106\nR|40|^G4v5^^QC^^^QC-1^|NEG^|||\nR|41|^G4v5^^QC^^^QC-1^Ct|^0|||\nR|42|^G4v5^^QC^^^QC-1^EndPt|^0|||\n"
			+ "R|43|^G4v5^^QC^^^QC-2^|NEG^|||\nR|44|^G4v5^^QC^^^QC-2^Ct|^0|||\nR|45|^G4v5^^QC^^^QC-2^EndPt|^0|||\nL|1|N";
		putMessage(message2);*/
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
