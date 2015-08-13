/**
 * 
 */
package com.ihsinformatics.xpertsms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.ihsinformatics.xpertsms.model.ResultsSender;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;


/**
 * @author Muhammad Safwan
 *
 */
public class ResultsSenderTest extends ResultsSender {
	
	static String username = "admin";
	
	static String password = "jingle94";
	
	//static JSONObject sampleGxaObj = new JSONObject();
	
	XpertASTMResultUploadMessage sampleMessage = new XpertASTMResultUploadMessage();
	
	ResultsSender result = new ResultsSender();
	
	/*@BeforeClass
	public static void atStartup() throws Exception {
		sampleMessage.setMtbResult("Mtb Not Detected");
		sampleMessage.setPatientId("TB Patient");
		sampleMessage.setSampleId("101130800001-9");
		sampleMessage.setProbeResultA("Result A");
		sampleMessage.setProbeResultB("Result B");
		sampleMessage.setProbeResultC("Result C");
		sampleMessage.setProbeResultD("Result D");
		sampleMessage.setProbeResultE("Result E");
		sampleMessage.setProbeCtA("Probe Ct A");
		sampleMessage.setProbeCtB("Probe Ct B");
		sampleMessage.setProbeCtC("Probe Ct C");
		sampleMessage.setProbeCtD("Probe Ct D");
		sampleMessage.setProbeCtE("Probe Ct E");
	}*/
	
	@Test
	public void toCheckSingleChunkMessage() throws Exception {
		sampleMessage.setOperatorId("Safwan");
		sampleMessage.setTestEndDate("2015-05-23");
		sampleMessage.setErrorCode("6002");
		sampleMessage.setMtbResult("MTB DETECTED");
		sampleMessage.setRifResult("Rif");
		sampleMessage.setPatientId("TB Patient");
		sampleMessage.setSampleId("101130800001-9");
		sampleMessage.setProbeResultA("Result A");
		sampleMessage.setProbeResultB("Result B");
		sampleMessage.setProbeResultC("Result C");
		sampleMessage.setProbeResultD("Result D");
		sampleMessage.setProbeResultE("Result E");
		XpertProperties.readProperties();
		String expectedOutput = "^DEFHRXabcde^1/1^101130800001-9^TB Patient^Safwan^2015-05-23^MTB DETECTED|Rif^6002^Result A^Result B^Result C^Result D^Result E";
		String text = sampleMessage.toSMS(true);
		String characterHeader = result.addingCharacters(text);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String date = sdf.format(new Date());
		StringBuilder addedHeaderText = new StringBuilder();
		String actualOutput = result.oneChunk(addedHeaderText, date, characterHeader, text);
		Assert.assertEquals(expectedOutput, actualOutput.substring(17));
	}
	
	@Test
	public void toCheckTwoChunks() throws Exception {
		sampleMessage.setOperatorId("Safwan");
		sampleMessage.setTestEndDate("2015-05-23");
		sampleMessage.setErrorCode("6002");
		sampleMessage.setMtbResult("MTB DETECTED");
		sampleMessage.setRifResult("Rif");
		sampleMessage.setPatientId("TB Patient");
		sampleMessage.setSampleId("101130800001-9");
		sampleMessage.setProbeResultA("Result A");
		sampleMessage.setProbeResultB("Result B");
		sampleMessage.setProbeResultC("Result C");
		sampleMessage.setProbeResultD("Result D");
		sampleMessage.setProbeResultE("Result E");
		sampleMessage.setProbeCtA("probeCtA");
		sampleMessage.setProbeCtB("probeCtB");
		sampleMessage.setProbeCtC("probeCtC");
		sampleMessage.setProbeCtD("probeCtD");
		sampleMessage.setProbeCtE("probeCtE");
		XpertProperties.readProperties();
		ArrayList<String> expectedOutput = new ArrayList<String>();
		expectedOutput.add("^DEFHRXabcdeij^1/2^101130800001-9^TB Patient^Safwan^2015-05-23^MTB DETECTED|Rif^6002^Result A^Result B^Result C^Result D^Result E^probeCtA^probeCtB");
		expectedOutput.add("^klm^2/2^probeCtC^probeCtD^probeCtE");
		String text = sampleMessage.toSMS(true);
		String characterHeader = result.addingCharacters(text);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String date = sdf.format(new Date());
		StringBuilder addedHeaderText = new StringBuilder();
		ArrayList<String> actualOutput = result.twoChunks(addedHeaderText, date, characterHeader, text);
		for(int i = 0; i < expectedOutput.size(); i++){
			Assert.assertEquals(expectedOutput.get(i), actualOutput.get(i).substring(17));
		}
		
	}
}
