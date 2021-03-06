/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsmsweb.server;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.InboundMessage;
import org.irdresearch.smstarseel.data.InboundMessage.InboundStatus;

import com.ihsinformatics.xpertsmsweb.server.util.DateTimeUtil;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;

/**
 * This class is responsible to interact with SMSTarseel and parse SMS results
 * to save into XpertSMS database
 * 
 * @author ali.habib@irdresearch.org
 *
 */
public class ResponseReader extends TimerTask {

	@Override
	public void run() {
		TarseelServices tsc = TarseelContext.getServices();

		try {
			List<InboundMessage> list = tsc.getSmsService().findInbound(null,
					null, InboundStatus.UNREAD, null, null, null,
					tsc.getDeviceService().findProjectById(1).getProjectId(),
					false);

			System.out.println("Running Job: ResponseReaderJob " + new Date()
					+ ". Fetched " + list.size() + " UNREAD sms");

			for (InboundMessage ib : list) {
				// ServiceContext sc = Context.getServices();

				try {
					String sender = ib.getOriginator();

					if (sender.length() != 0) {// MAX_CELL_NUMBER_MATCH_LENGTH){
						sender = sender.substring(sender.length());// -
																	// MAX_CELL_NUMBER_MATCH_LENGTH);
					}
					String text = ib.getText();
					if (text == null || text.length() == 0)
						continue;

					parseText(text);

					tsc.getSmsService().markInboundAsRead(
							ib.getReferenceNumber());
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
			// without if it would throw exception transaction not successfully
			// started
			if (list.size() > 0)
				tsc.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			tsc.closeSession();
		}
	}

	public void parseText(String text) {
		boolean update = false;
		String[] fields = text.split("\\^");
		if (fields.length < 5)
			return;
		String sampleId = null;
		String mtb = null;
		String rif = null;
		String systemId = null;
		String rFinal = null;
		String rPending = null;
		String rError = null;
		String rCorrected = null;
		String resultDate = null;
		String errorCode = null;
		String errorNotes = null;
		String notes = null;

		String operatorId = null;
		String pcId = null;
		String instrumentSerial = null;
		String moduleId = null;
		String cartridgeId = null;
		String reagentLotId = null;

		String probeResultA = null;
		String probeResultB = null;
		String probeResultC = null;
		String probeResultD = null;
		String probeResultE = null;
		String probeResultSPC = null;

		String probeCtA = null;
		String probeCtB = null;
		String probeCtC = null;
		String probeCtD = null;
		String probeCtE = null;
		String probeCtSPC = null;

		String probeEndptA = null;
		String probeEndptB = null;
		String probeEndptC = null;
		String probeEndptD = null;
		String probeEndptE = null;
		String probeEndptSPC = null;
		String patientId = null;
		String expDate = null;

		int j = 0;
		patientId = fields[j++];
		sampleId = fields[j++];
		mtb = fields[j++];
		rif = fields[j++];
		systemId = fields[j++];
		pcId = fields[j++];
		operatorId = fields[j++];
		instrumentSerial = fields[j++];
		moduleId = fields[j++];
		cartridgeId = fields[j++];
		reagentLotId = fields[j++];
		resultDate = fields[j++];
		rFinal = fields[j++];
		rPending = fields[j++];
		rError = fields[j++];
		if (rError != null && rError.equals("yes")) {
			errorCode = fields[j++];
			errorNotes = fields[j++];
			rCorrected = fields[j++];
		} else
			rCorrected = fields[j++];
		notes = fields[j++];
		if (fields.length > j) {
			probeResultA = fields[j++];
			probeResultB = fields[j++];
			probeResultC = fields[j++];
			probeResultD = fields[j++];
			probeResultE = fields[j++];
			probeResultSPC = fields[j++];

			probeCtA = fields[j++];
			probeCtB = fields[j++];
			probeCtC = fields[j++];
			probeCtD = fields[j++];
			probeCtE = fields[j++];
			probeCtSPC = fields[j++];

			probeEndptA = fields[j++];
			probeEndptB = fields[j++];
			probeEndptC = fields[j++];
			probeEndptD = fields[j++];
			probeEndptE = fields[j++];
			probeEndptSPC = fields[j++];
		}

		Date resultDateObj = null;
		if (resultDate != null) {
			resultDateObj = parseDate(resultDate);
		}
		// if(rPending!=null) {
		ServerServiceImpl ssl = new ServerServiceImpl();
		GeneXpertResults[] gxp = null;
		GeneXpertResults gxpNew = null;
		try {
			gxp = ssl.findGeneXpertResults(sampleId, patientId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventHandler eh = new EventHandler();
		if (gxp == null || gxp.length == 0) {
			GeneXpertResults gxpU = eh.createGeneXpertResults(patientId,
					sampleId, mtb, rif, resultDateObj, instrumentSerial,
					moduleId, cartridgeId, reagentLotId, parseDate(expDate),
					operatorId, pcId, probeResultA, probeResultB, probeResultC,
					probeResultD, probeResultE, probeResultSPC, probeCtA,
					probeCtB, probeCtC, probeCtD, probeCtE, probeCtSPC,
					probeEndptA, probeEndptB, probeEndptC, probeEndptD,
					probeEndptE, probeEndptSPC, errorCode, errorNotes, notes,
					systemId);
			try {
				ssl.saveGeneXpertResults(gxpU);
				System.out.println("New GeneXpert result saved. Patient ID: "
						+ patientId + "; Sample ID: " + sampleId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else {
			for (int i = 0; i < gxp.length; i++) {
				if (gxp[i].getDateTested() != null) {
					System.out.println("STORED TIME:"
							+ gxp[i].getDateTested().getTime());
					if (resultDateObj.getTime() == gxp[i].getDateTested()
							.getTime()) {
						gxpNew = gxp[i];
						update = true;
						break;
					}
				}
			}
			if (!update) {
				for (int i = 0; i < gxp.length; i++) {
					if (gxp[i].getGeneXpertResult() == null) {
						update = true;
						gxpNew = gxp[i];
						System.out.println("ID match null result");
						break;
					}
				}
			}
		}

		// set mtb
		if (update == true) {
			if (mtb != null) {
				// System.out.println("----MTB----" + mtb);
				int index = mtb.indexOf("MTB DETECTED");
				String mtbBurden = null;
				if (index != -1) {
					mtbBurden = mtb.substring(index + "MTB DETECTED".length()
							+ 1);
					gxpNew.setGeneXpertResult("MTB DETECTED");
					gxpNew.setIsPositive(new Boolean(true));
					gxpNew.setMtbBurden(mtbBurden);
					gxpNew.setErrorCode(0);
				} else {
					index = mtb.indexOf("MTB NOT DETECTED");
					// System.out.println("mtb :" + index + " " + mtb);
					if (index != -1) {
						gxpNew.setGeneXpertResult("MTB NOT DETECTED");
						gxpNew.setIsPositive(new Boolean(false));
					} else {
						gxpNew.setGeneXpertResult(mtb);
					}
				}
			}

			if (rif != null) {
				int index = rif.indexOf("NOT DETECTED");
				String rifResult = null;
				if (index != -1) {
					rifResult = "NOT DETECTED";
				} else if (rif.indexOf("DETECTED") != -1) {
					rifResult = "DETECTED";
				} else {
					rifResult = rif.toUpperCase();
				}
				gxpNew.setDrugResistance(rifResult);
			}
			gxpNew.setDateTested(resultDateObj);
			gxpNew.setInstrumentSerial(instrumentSerial);
			gxpNew.setModuleId(moduleId);
			gxpNew.setReagentLotId(reagentLotId);
			gxpNew.setCartridgeId(cartridgeId);
			gxpNew.setLaboratoryId(systemId);
			gxpNew.setPcId(pcId);
			gxpNew.setOperatorId(operatorId);
			if (errorCode != null) {
				gxpNew.setErrorCode(Integer.parseInt(errorCode));
			}
			gxpNew.setRemarks(errorNotes + ". " + notes);
			// Probes
			gxpNew.setProbeResultA(probeResultA);
			gxpNew.setProbeResultB(probeResultB);
			gxpNew.setProbeResultC(probeResultC);
			gxpNew.setProbeResultD(probeResultD);
			gxpNew.setProbeResultE(probeResultE);
			gxpNew.setProbeResultSPC(probeResultSPC);

			if (probeCtA != null)
				gxpNew.setProbeCtA(Double.parseDouble(probeCtA));
			if (probeCtB != null)
				gxpNew.setProbeCtB(Double.parseDouble(probeCtB));
			if (probeCtC != null)
				gxpNew.setProbeCtC(Double.parseDouble(probeCtC));
			if (probeCtD != null)
				gxpNew.setProbeCtD(Double.parseDouble(probeCtD));
			if (probeCtE != null)
				gxpNew.setProbeCtE(Double.parseDouble(probeCtE));
			if (probeCtSPC != null)
				gxpNew.setProbeCtSPC(Double.parseDouble(probeCtSPC));

			if (probeEndptA != null)
				gxpNew.setProbeEndptA(Double.parseDouble(probeEndptA));
			if (probeEndptB != null)
				gxpNew.setProbeEndptB(Double.parseDouble(probeEndptB));
			if (probeEndptC != null)
				gxpNew.setProbeEndptC(Double.parseDouble(probeEndptC));
			if (probeEndptD != null)
				gxpNew.setProbeEndptD(Double.parseDouble(probeEndptD));
			if (probeEndptE != null)
				gxpNew.setProbeEndptE(Double.parseDouble(probeEndptE));
			if (probeEndptSPC != null)
				gxpNew.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));

			try {
				ssl.updateGeneXpertResults(gxpNew, true);
				System.out.println("Results updated for Patient ID: "
						+ patientId + "; Sample ID: " + sampleId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private Date parseDate(String dateStr) {
		Date dateObj = null;
		if (dateStr != null) {
			try {
				if (dateStr.length() > 10)
					dateObj = DateTimeUtil.getDateFromString(dateStr,
							DateTimeUtil.SQL_DATETIME);
				else
					dateObj = DateTimeUtil.getDateFromString(dateStr,
							DateTimeUtil.SQL_DATE);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateObj;
	}

	public static void main(String args[]) {
		ResponseReader reader = new ResponseReader();
		// Results with probes
		reader.parseText("101130800001-9^141016_001^MTB DETECTED MEDIUM^Rif Resistance NOT DETECTED^Machine API Test^CEPHEID5G183R1^OWAIS^708228^618255^204304821^10713-AX^2015-05-23^no^no^yes^5002^Post-run analysis error^no^Just XDR-TB^POS^NO RESULT^NEG^NEG^POS^0^1.1^2.2^2.3^1.3^1.4^2.5^3.6^4.7^4.5^3.2^1.0^0.0");
		// Results without probes
		reader.parseText("101130800001-9^141016_001^MTB DETECTED MEDIUM^Rif Resistance NOT DETECTED^Machine API Test^CEPHEID5G183R1^OWAIS^708228^618255^204304821^10713-AX^2015-05-23^no^no^yes^5002^Post-run analysis error^no^No PROBlems");
	}
}