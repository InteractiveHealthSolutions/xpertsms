/*
Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package xpertsmsweb;

import java.util.ArrayList;
import java.util.Date;

import com.ihsinformatics.xpertsmsweb.server.ServerServiceImpl;
import com.ihsinformatics.xpertsmsweb.shared.model.Contact;
import com.ihsinformatics.xpertsmsweb.shared.model.Encounter;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;
import com.ihsinformatics.xpertsmsweb.shared.model.Patient;
import com.ihsinformatics.xpertsmsweb.shared.model.Person;

import junit.framework.TestCase;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SMSUtilTest extends TestCase {

	ServerServiceImpl service;
	Patient patient1, patient2;
	Person person1, person2;
	Contact contact1, contact2;
	Encounter encounter1, encounter2;
	GeneXpertResults normal, error, mtbPositive, rifPositive;

	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		service = new ServerServiceImpl();
		patient1 = new Patient("101");
		patient2 = new Patient("102");
		person1 = new Person("101", "Sick", 'M');
		person2 = new Person("102", "Healthy", 'F');
		contact1 = new Contact("101");
		contact1.setMobile("03453174270");
		contact1 = new Contact("03332334556");
		service.saveNewPatient(patient1, person1, contact1, encounter1,
				new ArrayList<String>());
		service.saveNewPatient(patient2, person2, contact2, encounter2,
				new ArrayList<String>());
		String normalQuery = "insert into genexpertresults (PatientID,SputumTestID,LaboratoryID,DateTested,DrugResistance,GeneXpertResult,MTBBurden,ErrorCode,Remarks,InstrumentID,ModuleID,CartridgeID,ReagentLotID,PcID,OperatorID,CartridgeExpiryDate,ProbeResultA,ProbeResultB,ProbeResultC,ProbeResultD,ProbeResultE,ProbeResultSPC,ProbeCtA,ProbeCtB,ProbeCtC,ProbeCtD,ProbeCtE,ProbeCtSPC,ProbeEndptA,ProbeEndptB,ProbeEndptC,ProbeEndptD,ProbeEndptE,ProbeEndptSPC) VALUES "
		        + "('102','15071','Test PC','2015-06-23','Rif Resistance NOT DETECTED','MTB NOT DETECTED',null,'0','NRL-ISB','708228','618255','204304821','10713-AX','CEPHEID5G183R1','OWAIS','2014-06-21',"
		        + "'POS','NO RESULT','NEG','NEG','POS','0','1.1','2.2','2.3','1.3','1.4','2.5','3.6','4.7','4.5','3.2','1.0','0.0')";
		String errorQuery = "insert into genexpertresults (PatientID,SputumTestID,LaboratoryID,DateTested,DrugResistance,GeneXpertResult,MTBBurden,ErrorCode,Remarks,InstrumentID,ModuleID,CartridgeID,ReagentLotID,PcID,OperatorID,CartridgeExpiryDate) VALUES "
		        + "('101','15072','Test PC','2015-06-25',null,null,null,'5002','Post-run analysis error. IHS','708228','618255','204304821','10713-AX','CEPHEID5G183R1','OWAIS','2014-06-21')";
		String mtbPositiveQuery = "insert into genexpertresults (PatientID,SputumTestID,LaboratoryID,DateTested,DrugResistance,GeneXpertResult,MTBBurden,ErrorCode,Remarks,InstrumentID,ModuleID,CartridgeID,ReagentLotID,PcID,OperatorID,CartridgeExpiryDate,ProbeResultA,ProbeResultB,ProbeResultC,ProbeResultD,ProbeResultE,ProbeResultSPC,ProbeCtA,ProbeCtB,ProbeCtC,ProbeCtD,ProbeCtE,ProbeCtSPC,ProbeEndptA,ProbeEndptB,ProbeEndptC,ProbeEndptD,ProbeEndptE,ProbeEndptSPC) VALUES "
		        + "('101','15073','Test PC','2015-06-26','Rif Resistance NOT DETECTED','MTB DETECTED','VERY HIGH',null,'','708228','618255','204304821','10713-AX','CEPHEID5G183R1','OWAIS','2014-06-21',"
		        + "'POS','NO RESULT','NEG','NEG','POS','0','1.1','2.2','2.3','1.3','1.4','2.5','3.6','4.7','4.5','3.2','1.0','0.0')";
		String rifPositiveQuery = "insert into genexpertresults (PatientID,SputumTestID,LaboratoryID,DateTested,DrugResistance,GeneXpertResult,MTBBurden,ErrorCode,Remarks,InstrumentID,ModuleID,CartridgeID,ReagentLotID,PcID,OperatorID,CartridgeExpiryDate,ProbeResultA,ProbeResultB,ProbeResultC,ProbeResultD,ProbeResultE,ProbeResultSPC,ProbeCtA,ProbeCtB,ProbeCtC,ProbeCtD,ProbeCtE,ProbeCtSPC,ProbeEndptA,ProbeEndptB,ProbeEndptC,ProbeEndptD,ProbeEndptE,ProbeEndptSPC) VALUES "
		        + "('101','15074','Test PC','2015-06-26','Rif Resistance DETECTED','MTB DETECTED','LOW',null,'IRD','708228','618255','204304821','10713-AX','CEPHEID5G183R1','OWAIS','2014-06-21',"
		        + "'POS','NO RESULT','NEG','NEG','POS','0','1.1','2.2','2.3','1.3','1.4','2.5','3.6','4.7','4.5','3.2','1.0','0.0')";
		service.execute(normalQuery);
		service.execute(errorQuery);
		service.execute(mtbPositiveQuery);
		service.execute(rifPositiveQuery);
		normal = service.findGeneXpertResults("15071");
		error = service.findGeneXpertResults("15072");
		mtbPositive = service.findGeneXpertResults("15073");
		rifPositive = service.findGeneXpertResults("15074");
	}

	/*
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		service.deletePatient(patient1);
		service.deletePatient(patient2);
		service.deleteGeneXpertResults(normal);
		service.deleteGeneXpertResults(error);
		service.deleteGeneXpertResults(mtbPositive);
		service.deleteGeneXpertResults(rifPositive);
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.xpertsmsweb.server.SMSUtil#sendAlertsOnAutoGXPResults(com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults)}
	 * .
	 */
	public final void testSendAlertsOnAutoGXPResults() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.xpertsmsweb.server.SMSUtil#sendAlertsToPatient(com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults)}
	 * .
	 */
	public final void testSendAlertsToPatient() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.xpertsmsweb.server.SMSUtil#sendAlertsToCenter(com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults, java.lang.String)}
	 * .
	 */
	public final void testSendAlertsToProvider() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.xpertsmsweb.server.SMSUtil#sendAlertToProgram(com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults, java.lang.String, java.lang.String)}
	 * .
	 */
	public final void testSendAlertToProgram() {
		fail("Not yet implemented"); // TODO
	}

}
