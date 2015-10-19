/**
 * Utility to send SMS alerts
 */
package com.ihsinformatics.xpertsmsweb.server;

import java.util.Date;

import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.OutboundMessage.PeriodType;
import org.irdresearch.smstarseel.data.OutboundMessage.Priority;

import com.ihsinformatics.xpertsmsweb.server.util.DateTimeUtil;
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
	private TarseelServices tarseelService;
	MessageSettings ms;
	public static SMSUtil util = new SMSUtil();
	public static ServerServiceImpl service = new ServerServiceImpl();

	boolean attachPatientId = true;
	boolean attachSampleId = true;
	boolean attachMTBResult = true;
	boolean attachRifResult = true;
	boolean attachCartridgeId = false;
	boolean attachOperatorId = false;
	boolean attachModuleId = false;
	boolean attachLocationId = true;
	boolean attachTestDate = true;

	/**
	 * 
	 */
	public SMSUtil() {
		ms = null;
		tarseelService = TarseelContext.getServices();
		try {
			ms = (MessageSettings) HibernateUtil.util
					.findObject("from MessageSettings");
			if (ms != null) {
				attachPatientId = ms.getAttachPatientId();
				attachSampleId = ms.getAttachSampleId();
				attachMTBResult = ms.getAttachMtb();
				attachRifResult = ms.getAttachRif();
				attachOperatorId = ms.getAttachOperatorId();
				attachLocationId = ms.getAttachLocationId();
				attachModuleId = ms.getAttachModuleId();
				attachCartridgeId = ms.getAttachCartridgeId();
				attachTestDate = ms.getAttachTestDate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAlertsOnAutoGXPResults(GeneXpertResults results) {
		tarseelService = TarseelContext.getServices();
		System.out.println("SENDING");
		// Only when results are positive
		String messageHeader = "*Automated Message*\n";
		// Send message to Patient if enabled
		if (ms.getSendToPatient()) {
			sendAlertsToPatient(results);
		}
		// Set message content
		StringBuilder text = new StringBuilder();
		text.append(messageHeader);
		if (attachPatientId) {
			text.append("PatientID:" + results.getPatientId() + "\n");
		}
		if (attachSampleId) {
			text.append("SampleID:" + results.getSputumTestId() + "\n");
		}
		text.append("Result:" + results.getGeneXpertResult() + "\n");
		if (attachMTBResult) {
			text.append("MTB Burden:"
					+ ((results.getMtbBurden() == null) ? "" : results
							.getMtbBurden()) + "\n");
		}
		if (attachRifResult) {
			text.append("Rif Resistance:"
					+ ((results.getDrugResistance() == null) ? "" : results
							.getDrugResistance()) + "\n");
		}
		if (attachOperatorId) {
			text.append("OperatorID:" + results.getOperatorId() + "\n");
		}
		if (attachCartridgeId) {
			text.append("CartridgeID:" + results.getCartridgeId() + "\n");
		}
		if (attachModuleId) {
			text.append("ModuleID:" + results.getModuleId() + "\n");
		}
		if (attachLocationId) {
			text.append("LocationID:" + results.getModuleId() + "\n");
		}
		if (attachTestDate) {
			text.append("TestDate:"
					+ DateTimeUtil.getSQLDate(results.getDateTested()) + "\n");
		}
		boolean send = ms.getAlertOnAll()
				| (ms.getAlertOnAllMtb() & results.getIsPositive())
				| ((ms.getAlertOnMtbHigh() | ms.getAlertOnMtbMedium()) & results
						.getMtbBurden().contains("HIGH"))
				| (ms.getAlertOnMtbMedium() & results.getMtbBurden()
						.equalsIgnoreCase("MEDIUM"))
				| (ms.getAlertOnRif() & results.getDrugResistance()
						.equalsIgnoreCase("DETECTED"));
		boolean hasError = results.getErrorCode() != null;
		if (hasError)
			hasError = results.getErrorCode() != 0;
		send = send | (ms.getAlertOnError() & hasError);
		/* Send alerts */
		// Always send an alert to the program number
		sendAlertToProgram(results, ms.getProgramNumber(), text.toString());
		// No alert to Patient in case of error
		if (ms.getSendToPatient() & !hasError) {
			sendAlertsToPatient(results);
		}
		// Send message to center
		if (ms.getSendToCenter()) {
			sendAlertsToCenter(results, text.toString());
		}
		// Send message to referred location (could be multiple contacts)
		if (ms.getSendToReferenceLocation()) {
			sendAlertsToReferenceLocation(results, text.toString());
		}
		// Send message to other number
		if (ms.getSendToManager()) {
			String managerNumber = ms.getManagerNumber();
			if (managerNumber != null) {
				tarseelService.getSmsService().createNewOutboundSms(
						managerNumber, text.toString(), new Date(),
						Priority.HIGH, 24, PeriodType.DAY, 1, null);
			}
		}
		try {
			tarseelService.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tarseelService.closeSession();
		}
	}

	public void sendAlertsToPatient(GeneXpertResults results) {
		try {
			StringBuilder text = new StringBuilder();
			Contact contact = service.findContact(results.getPatientId());
			String mobile = contact.getMobile();
			if (mobile == null)
				throw new Exception("Patient's mobile number not found.");
			text.append("Your test results are ready. Please pick up from the laboratory at your earliest convenience"
					+ "\n");
			tarseelService.getSmsService().createNewOutboundSms(mobile,
					text.toString(), new Date(), Priority.HIGH, 24,
					PeriodType.WEEK, 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Picks center ID from HostID in results and send alerts to its Mobile
	 * number. If that's not available, tries LaboratoryID
	 * 
	 * @param results
	 * @param text
	 */
	public void sendAlertsToCenter(GeneXpertResults results, String text) {
		try {
			try {
				String centerId = results.getHostId();
				Location location = service.findLocation(centerId);
				if (location == null) {
					location = service.findLocation(results.getLaboratoryId());
				}
				String phone = location.getPhone();
				if (phone != null) {
					tarseelService.getSmsService().createNewOutboundSms(phone,
							text, new Date(), Priority.HIGH, 24,
							PeriodType.WEEK, 1, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extracts reference location from Remarks field, finds in locations and
	 * and sends alerts to all numbers in Mobile field
	 * 
	 * @param results
	 * @param text
	 */
	public void sendAlertsToReferenceLocation(GeneXpertResults results,
			String text) {
		try {
			String remarks = results.getRemarks().toUpperCase();
			if (!remarks.equals("")) {
				// Try to find respective location
				Location location = service.findLocation(remarks);
				if (location != null) {
					String phone = location.getPhone();
					if (phone != null) {
						tarseelService.getSmsService().createNewOutboundSms(
								phone, text.toString(), new Date(),
								Priority.HIGH, 24, PeriodType.WEEK, 1, null);
					}
					String mobile = location.getMobile();
					if (mobile != null) {
						if (mobile.contains(",")) {
							String[] mobiles = mobile.split(",");
							for (String m : mobiles) {
								tarseelService.getSmsService()
										.createNewOutboundSms(m,
												text.toString().trim(),
												new Date(), Priority.HIGH, 24,
												PeriodType.WEEK, 1, null);
							}
						} else {
							tarseelService.getSmsService()
									.createNewOutboundSms(mobile,
											text.toString(), new Date(),
											Priority.HIGH, 24, PeriodType.WEEK,
											1, null);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAlertToProgram(GeneXpertResults results,
			String programNumber, String text) {
		try {
			tarseelService.getSmsService().createNewOutboundSms(programNumber,
					text.toString(), new Date(), Priority.HIGH, 24,
					PeriodType.WEEK, 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
