/**
 * Utility to send SMS alerts
 */
package com.ihsinformatics.xpertsmsweb.server;

import java.util.Date;

import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.OutboundMessage.PeriodType;
import org.irdresearch.smstarseel.data.OutboundMessage.Priority;

import com.ihsinformatics.xpertsmsweb.server.util.HibernateUtil;
import com.ihsinformatics.xpertsmsweb.shared.model.Contact;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;
import com.ihsinformatics.xpertsmsweb.shared.model.Location;
import com.ihsinformatics.xpertsmsweb.shared.model.MessageSettings;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SMSUtil {
	public static final String status = "PENDING";
	private TarseelServices context;
	public static SMSUtil util = new SMSUtil();
	public static ServerServiceImpl service = new ServerServiceImpl();
	
	public static final boolean sendPatientID = true;
	public static final boolean sendSampleID = true;
	public static final boolean sendMTBResult = true;
	public static final boolean sendRifResult = true;
	public static final boolean sendCartridgeID = false;
	public static final boolean sendOperatorID = false;
	public static final boolean sendModuleID = false;
	public static final boolean sendLocationID = true;

	public void sendAlertsOnAutoGXPResults(GeneXpertResults results) {
		System.out.println("SENDING");
		MessageSettings messageSettings = null;
		context = TarseelContext.getServices();
		try {
			messageSettings = (MessageSettings) HibernateUtil.util.findObject("from  MessageSettings");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Only when results are positive
		if (results.getIsPositive()) {
			String messageHeader = "*Automated Message*\n";
			// Send message to Patient if enabled
			if (messageSettings.getSendToPatient()) {
				sendAlertsToPatient(results);
			}
			// Set message content
			StringBuilder text = new StringBuilder();
			text.append(messageHeader);
			if (sendPatientID) {
				text.append("PatientID:" + results.getPatientId() + "\n");
			}
			if (sendSampleID) {
				text.append("SampleID:" + results.getSputumTestId() + "\n");
			}
			text.append("Result:" + results.getGeneXpertResult() + "\n");
			if (sendMTBResult) {
				text.append("MTB Burden:" + ((results.getMtbBurden() == null) ? "" : results.getMtbBurden()) + "\n");
			}
			if (sendRifResult) {
				text.append("Rif Resistance:" + ((results.getDrugResistance() == null) ? "" : results.getDrugResistance()) + "\n");
			}
			if (sendOperatorID) {
				text.append("OperatorID:" + results.getOperatorId() + "\n");
			}
			if (sendCartridgeID) {
				text.append("CartridgeID:" + results.getCartridgeId() + "\n");
			}
			if (sendModuleID) {
				text.append("ModuleID:" + results.getModuleId() + "\n");
			}
			if (sendLocationID) {
				text.append("LocationID:" + results.getModuleId() + "\n");
			}
			// Send message to Provider (GeneXpert test location, fetch from
			// HostId in results) if enabled
			if (messageSettings.getSendToProvider()) {
				sendAlertsToProvider(results, text.toString());
			}
			// Send message to program along with referred location
			if (messageSettings.getSendToProgram()) {
				sendAlertToProgram(results, messageSettings.getProgramNumber(), text.toString());
			}
			// Send message to other number
			if (messageSettings.getSendToOther()) {
				String otherNumber = messageSettings.getOtherNumber();
				context.getSmsService().createNewOutboundSms(otherNumber, text.toString(), new Date(), Priority.HIGH, 24, PeriodType.DAY, 1, null);
			}
			context.commitTransaction();
			context.closeSession();
		}

	}

	public void sendAlertsToPatient(GeneXpertResults results) {
		try {
			StringBuilder text = new StringBuilder();
			Contact contact = service.findContact(results.getPatientId());
			String mobile = contact.getMobile();
			if (mobile == null)
				throw new Exception("Patient's mobile number not found.");
			text.append("Your test results are ready. Please pick up from the laboratory at your earliest convenience" + "\n");
			context.getSmsService().createNewOutboundSms(mobile, text.toString(), new Date(), Priority.HIGH, 24, PeriodType.DAY, 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAlertsToProvider(GeneXpertResults results, String text) {
		try {
			try {
				String laboratoryId = results.getLaboratoryId();
				Location location = service.findLocation(laboratoryId);
				String phone = location.getPhone();
				context.getSmsService().createNewOutboundSms(phone, text, new Date(), Priority.HIGH, 24, PeriodType.DAY, 1, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendAlertToProgram(GeneXpertResults results, String programNumber, String text) {
		try {
			context.getSmsService().createNewOutboundSms(programNumber, text.toString(), new Date(), Priority.HIGH, 24, PeriodType.DAY, 1, null);
			// Also send to the referred location (try to fetch from
			// remarks in results)
			String remarks = results.getRemarks().toUpperCase();
			if (!remarks.equals("")) {
				// Try to find respective location
				Location location = service.findLocation(remarks);
				if (location != null) {
					String phone = location.getPhone();
					if (phone != null) {
						context.getSmsService().createNewOutboundSms(phone, text.toString(), new Date(), Priority.HIGH, 24, PeriodType.DAY, 1, null);
					}
					String mobile = location.getMobile();
					if (mobile != null) {
						if (mobile.contains(",")) {
							String[] mobiles = mobile.split(",");
							for (String m : mobiles) {
								context.getSmsService().createNewOutboundSms(m, text.toString(), new Date(), Priority.HIGH, 24, PeriodType.DAY, 1, null);
							}
						} else {
							context.getSmsService().createNewOutboundSms(mobile, text.toString(), new Date(), Priority.HIGH, 24, PeriodType.DAY, 1, null);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
