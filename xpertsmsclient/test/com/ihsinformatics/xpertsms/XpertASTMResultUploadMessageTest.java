package com.ihsinformatics.xpertsms;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;

public class XpertASTMResultUploadMessageTest {
	
	static String username = "admin";
	
	static String password = "jingle94";
	
	static JSONObject sampleGxaObj = new JSONObject();
	
	static XpertASTMResultUploadMessage sampleMessage = new XpertASTMResultUploadMessage();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sampleGxaObj.put("apiKey", "ph8trUgestejugudR6fRa6he5u6heveZpruwuWrAthUruFuhuxuRe8ruGunuthub")
		        .put("assayHostTestCode", "MTB-RIF").put("assay", "Xpert MTB-RIF Assay").put("assayVersion", "5")
		        .put("sampleId", "130514_12544_00001").put("patientId", "101130800001-9").put("user", "OWAIS")
		        .put("testStartedOn", "2014-05-22T17:42:55Z").put("testEndedOn", "2014-05-22T20:33:12Z")
		        .put("messageSentOn", "2014-05-23T18:21:45Z").put("reagentLotId", "10713-AX")
		        .put("cartridgeExpirationDate", "2014-05-21T18:21:45Z").put("cartridgeSerial", "204304821")
		        .put("moduleSerial", "618255").put("instrumentSerial", "708228").put("softwareVersion", "4.4a")
		        .put("resultIdMtb", 2).put("resultIdRif", 2)
		        .put("resultText", "MTB DETECTED MEDIUM|Rif Resistance NOT DETECTED").put("deviceSerial", "CEPHEID5G183R1")
		        .put("hostId", "Machine API Test").put("systemName", "GeneXpert PC").put("computerName", "CepheidJRJRFQ1")
		        .put("notes", "Nothing serious, just XDR-TB").put("errorCode", "").put("errorNotes", "")
		        .put("externalTestId", "X-123-4-XXZ").put("probeA", "NEG").put("probeB", "POS").put("probeC", "NO RESULT")
		        .put("probeD", "NEG").put("probeE", "NEG").put("probeSpc", "POS").put("probeACt", 0).put("probeBCt", 1.1)
		        .put("probeCCt", 2.2).put("probeDCt", 3.3).put("probeECt", 4.4).put("probeSpcCt", 5.5)
		        .put("probeAEndpt", 8.8).put("probeBEndpt", 9.9).put("probeCEndpt", 1.2).put("probeDEndpt", 2.3)
		        .put("probeEEndpt", 3.4).put("probeSpcEndpt", 4.5);
		
		sampleMessage.setUniversalTestId("MTB-RIF");
		sampleMessage.setSystemDefinedTestName("Xpert MTB-RIF Assay");
		sampleMessage.setSystemDefinedTestVersion("7");
		sampleMessage.setSampleId("141016_001");
		sampleMessage.setPatientId("101130800001-9");
		sampleMessage.setOperatorId("OWAIS");
		sampleMessage.setTestStartDate("2015-05-22");
		sampleMessage.setTestEndDate("2015-05-23");
		sampleMessage.setReagentLotId("10713-AX");
		sampleMessage.setExpDate("2014-06-21");
		sampleMessage.setCartridgeId("204304821");
		sampleMessage.setModuleId("618255");
		sampleMessage.setInstrumentSerial("708228");
		sampleMessage.setSoftwareVersion("4.4a");
		sampleMessage.setMtbResult("MTB DETECTED MEDIUM");
		sampleMessage.setRifResult("Rif Resistance NOT DETECTED");
		sampleMessage.setPcId("CEPHEID5G183R1");
		sampleMessage.setSystemId("Machine API Test");
		sampleMessage.setSystemName("GeneXpert PC");
		sampleMessage.setComputerName("CepheidJRJRFQ1");
		sampleMessage.setNotes("Just XDR-TB");
		sampleMessage.setErrorCode("5002");
		sampleMessage.setErrorNotes("Post-run analysis error");
		sampleMessage.setMessageId("");
		sampleMessage.setProbeResultA("POS");
		sampleMessage.setProbeResultB("NO RESULT");
		sampleMessage.setProbeResultC("NEG");
		sampleMessage.setProbeResultD("NEG");
		sampleMessage.setProbeResultE("POS");
		sampleMessage.setProbeResultSPC("0");
		sampleMessage.setProbeCtA("1.1");
		sampleMessage.setProbeCtB("2.2");
		sampleMessage.setProbeCtC("2.3");
		sampleMessage.setProbeCtD("1.3");
		sampleMessage.setProbeCtE("1.4");
		sampleMessage.setProbeCtSPC("2.5");
		sampleMessage.setProbeEndPtA("3.6");
		sampleMessage.setProbeEndPtB("4.7");
		sampleMessage.setProbeEndPtC("4.5");
		sampleMessage.setProbeEndPtD("3.2");
		sampleMessage.setProbeEndPtE("1.0");
		sampleMessage.setProbeEndPtSPC("0.0");
		SimpleDateFormat messageFormat = new SimpleDateFormat("yyyy-MM-dd");
		sampleMessage.setMessageDateTime(messageFormat.format(new Date()));
	}
	
	@Test
	public void testToPostParams() {
		String params = sampleMessage.toPostParams(true, username, password);
		String should = "type=astmresult&username=" + username + "&password=" + password + "&pid=101130800001-9&sampleid=141016_001&mtb=MTB DETECTED&rif=Rif Resistance NOT DETECTED&notes=Just XDR-TB&enddate=2015-05-23&operatorid=OWAIS&pcid=CEPHEID5G183R1&instserial=708228&moduleid=618255&cartrigeid=204304821&reagentlotid=10713-AX&systemid=Machine API Test&receiverid=null&expdate=2014-06-21&probea=POS&probeb=NO RESULT&probec=NEG&probed=NEG&probee=POS&probespc=0&probeact=1.1&probebct=2.2&probecct=2.3&probedct=1.3&probeect=1.4&probespcct=2.5&probeaendpt=3.6&probebendpt=4.7&probecendpt=4.5&probedendpt=3.2&probeeendpt=1.0&probespcendpt=0.0&";
		Assert.assertTrue("Parameters mismatch", params.equalsIgnoreCase(should));
	}
	
	@Test
	public void testToSMS() {
		String sms = sampleMessage.toSMS(true);
		String should = "101130800001-9^141016_001^MTB DETECTED^Rif Resistance NOT DETECTED^Machine API Test^CEPHEID5G183R1^OWAIS^708228^618255^204304821^10713-AX^2015-05-23^no^no^no^no^POS^NO RESULT^NEG^NEG^POS^0^1.1^2.2^2.3^1.3^1.4^2.5^3.6^4.7^4.5^3.2^1.0^0.0";
		Assert.assertTrue("Parameters mismatch", sms.equalsIgnoreCase(should));
	}
	
	@Test
	public void testToCsv() {
		String csv = sampleMessage.toCsv();
		String should = "\"101130800001-9\",\"141016_001\",\"MTB DETECTED\",\"Rif Resistance NOT DETECTED\",\"\",\"\",\"\",\"\",\"\",\"OWAIS\",\"2015-05-22\",\"2015-05-23\",\"CEPHEID5G183R1\",\"708228\",\"618255\",\"204304821\",\"10713-AX\",\"2014-06-21\","
		        + "\"5002\",\"Post-run analysis error\",\"Just XDR-TB\",\"\",\"Machine API Test\",\"4.4a\",\"\",\"\",\"\",\"2015-05-04\",\"\",\"MTB-RIF\",\"\",\"\",\"\",\"\",\"\",\"Xpert MTB-RIF Assay\",\"7\","
		        + "\"POS\",\"NO RESULT\",\"NEG\",\"NEG\",\"POS\",\"0\",\"1.1\",\"2.2\",\"2.3\",\"1.3\",\"1.4\",\"2.5\",\"3.6\",\"4.7\",\"4.5\",\"3.2\",\"1.0\",\"0.0\"";
		Assert.assertTrue("Parameters mismatch", csv.equalsIgnoreCase(should));
	}
	
	@Test
	public void testToSqlQuery() {
		String sql = sampleMessage.toSqlQuery();
		String should = "insert into genexpertresults (PatientID,SputumTestID,LaboratoryID,DateTested,DrugResistance,GeneXpertResult,MTBBurden,ErrorCode,Remarks,InstrumentID,ModuleID,CartridgeID,ReagentLotID,PcID,OperatorID,CartridgeExpiryDate,ProbeResultA,ProbeResultB,ProbeResultC,ProbeResultD,ProbeResultE,ProbeResultSPC,ProbeCtA,ProbeCtB,ProbeCtC,ProbeCtD,ProbeCtE,ProbeCtSPC,ProbeEndptA,ProbeEndptB,ProbeEndptC,ProbeEndptD,ProbeEndptE,ProbeEndptSPC) VALUES "
		        + "('101130800001-9','141016_001','GeneXpert PC','2015-05-23','Rif Resistance NOT DETECTED','MTB DETECTED','MEDIUM','5002','Post-run analysis error. Just XDR-TB','708228','618255','204304821','10713-AX','CEPHEID5G183R1','OWAIS','2014-06-21',"
		        + "'POS','NO RESULT','NEG','NEG','POS','0','1.1','2.2','2.3','1.3','1.4','2.5','3.6','4.7','4.5','3.2','1.0','0.0')";
		Assert.assertTrue("Parameters mismatch", sql.equalsIgnoreCase(should));
	}
	
	@Test
	public void testToJson() {
	}
	
}
