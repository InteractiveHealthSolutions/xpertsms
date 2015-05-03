/**
 * Utility to send SMS alerts
 */
package org.irdresearch.tbreach.server;

import java.util.Date;

import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.OutboundMessage.PeriodType;
import org.irdresearch.smstarseel.data.OutboundMessage.Priority;
import org.irdresearch.tbreach.shared.model.GeneXpertResults;
import org.irdresearch.tbreach.shared.model.MessageSettings;
import org.irdresearch.tbreach.shared.model.OtherMessageSetting;

/**
 * @author owais.hussain@irdresearch.org
 *
 */
public class SMSUtil
{
	private static final String	status	= "PENDING";
	public static SMSUtil		util	= new SMSUtil();

	private String getMobileNumber(String PID)
	{
		if (PID.equals(""))
			return "";
		return HibernateUtil.util.selectObject("select Mobile from Contact where PID='" + PID + "'").toString();
	}

	
	public void sendAlertsOnAutoGXPResults(GeneXpertResults results)
	{
		System.out.println("SENDING");
		String targetNumber = "";
		String messageTextPatient = null;
		String messageTextOther = null;
		String messageTextGP = null;
		String messageTextProgram = null;
		String gpNumber = null;
		String patientNumber = null;
		String programNumber = null;
		String otherNumber = null;
		MessageSettings ms = null;
		String othersMessageSettingText = null;
		
		try {
			ms = (MessageSettings) HibernateUtil.util.findObject("from MessageSettings");
		}
		
		catch(Exception e) {
			System.out.println("no settings");
			e.printStackTrace();
		}
		//if (results.getIsPositive())
		{
			
			
			String messageHeader = "*Automated GeneXpert Result Message - " + results.getSputumTestId() + "*\n";
			// Send alert to GP
			String gpId = null;
			
			try {
				patientNumber = getMobileNumber(results.getPatientId());//HibernateUtil.util.selectObject("select Phone from Contact where PID='" + results.getPatientId() + "'").toString();
			}
			catch(Exception e) {
				patientNumber = null;
			}
			if(patientNumber!=null && ms.getSendToPatient())
			{
				
				messageTextPatient = messageHeader +
				"Your result is ready. Please pick up from the lab at your earliest convenience";
			}
			
			try{
				gpId = HibernateUtil.util.selectObject("select ProviderID from Patient where PatientID='" + results.getPatientId() + "'").toString();
				System.out.println("GPID:" + gpId);
			}
			catch(Exception e) {
				gpId = null;
			}
			if(gpId!=null && ms.getSendToProvider())
			{
				gpNumber = getMobileNumber(gpId);
				System.out.println("gpnumber:" + gpNumber);
				messageTextGP = messageHeader +
				"Result: " + results.getGeneXpertResult() + "\nMTB Burden: " + ( (results.getMtbBurden()==null) ? "" : results.getMtbBurden()) + "\nRif Resistance: " + (( results.getDrugResistance()==null) ? "" : results.getDrugResistance());
			}
			
			programNumber = ms.getProgramNumber();
			
			if(programNumber!=null && ms.getSendToProgram())
			{
				//TODO Expand
				messageTextProgram = messageHeader +
				"Result: " + results.getGeneXpertResult() + "\nMTB Burden: " + ( (results.getMtbBurden()==null) ? "" : results.getMtbBurden()) + "\nRif Resistance: " + (( results.getDrugResistance()==null) ? "" : results.getDrugResistance());
				messageTextProgram += "\nOperator: " + results.getOperatorId(); 
				
			}
			
			otherNumber = ms.getOtherNumber();//(String) HibernateUtil.util.selectObject("select OtherNumber from MessageSettings");// where PatientID='" + results.getPatientId() + "'").toString();
			
			if(otherNumber!=null && ms.getSendToOther())
			{
				//TODO Expand
				messageTextOther = messageHeader +
				"Result: " + results.getGeneXpertResult() + "\nMTB Burden: " +( (results.getMtbBurden()==null) ? "" : results.getMtbBurden()) + "\nRif Resistance: " + (( results.getDrugResistance()==null) ? "" : results.getDrugResistance());
				messageTextOther += "\nOperator: " + results.getOperatorId();
			}
			
			TarseelServices services = TarseelContext.getServices ();
			
			/*String pid = results.getPatientId();
			//String pid = "1212/NameKhan";
			String districtId = pid.substring(0,2);
			String healthFacilityId = pid.substring(2,4);
			
			OtherMessageSetting[] settings = findOtherMessageRecipientByLocationId(districtId, healthFacilityId);
			
			for(OtherMessageSetting setting : settings){
				
				String number = setting.getCellNumber();
				services.getSmsService ().createNewOutboundSms (number, messageTextOther, new Date (), Priority.HIGHEST, 24, PeriodType.HOUR, 1, null);
				
			}*/
			
			//services.getSmsService ().createNewOutboundSms (targetNumber, messageTextGP, new Date (), Priority.HIGHEST, 24, PeriodType.HOUR, 1, null);
			if(messageTextPatient!=null)
				services.getSmsService ().createNewOutboundSms (patientNumber, messageTextPatient, new Date (), Priority.HIGHEST, 24, PeriodType.HOUR, 1, null);
			if(messageTextGP!=null)	
				services.getSmsService ().createNewOutboundSms (gpNumber, messageTextGP, new Date (), Priority.HIGHEST, 24, PeriodType.HOUR, 1, null);
			if(messageTextProgram!=null)
				services.getSmsService ().createNewOutboundSms (programNumber, messageTextProgram, new Date (), Priority.HIGHEST, 24, PeriodType.HOUR, 1, null);
			if(messageTextOther!=null)
				services.getSmsService ().createNewOutboundSms (otherNumber, messageTextOther, new Date (), Priority.HIGHEST, 24, PeriodType.HOUR, 1, null);
			services.commitTransaction();
			services.closeSession();
			
		}
		
	}
	
	public OtherMessageSetting[] findOtherMessageRecipientByLocationId (String districtId, String healthFacilityId)
	{
		Object[] list = HibernateUtil.util.findObjects ("from OtherMessageSetting where districtId = '"+districtId+"' AND healthFacilityId = '"+healthFacilityId+"'");
		OtherMessageSetting[] locations = new OtherMessageSetting[list.length];
		for (int i = 0; i < list.length; i++)
			locations[i] = (OtherMessageSetting) list[i];
		return locations;
	}
	
}
