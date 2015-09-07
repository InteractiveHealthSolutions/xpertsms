/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsmsweb.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
		/*HibernateUtil hb = new HibernateUtil();
		hb.create("update smstarseel.inboundmessage set status = 'UNREAD' where text like '%^%'");*/
		try {
			List<InboundMessage> list = tsc.getSmsService().findInbound(null,
					null, InboundStatus.UNREAD, null, null, null,
					tsc.getDeviceService().findProjectById(1).getProjectId(),
					false);

			int j = 0;
			System.out.println("Running Job: ResponseReaderJob " + new Date()
					+ ". Fetched " + list.size() + " UNREAD sms");

			for (InboundMessage ib : list) {
				if(ib == null)
					continue;
				j = 0;
				
				try {
					String sender = ib.getOriginator();

					if (sender.length() != 0) {// MAX_CELL_NUMBER_MATCH_LENGTH){
						sender = sender.substring(sender.length());// -
																	// MAX_CELL_NUMBER_MATCH_LENGTH);
					}
					String text = ib.getText();
					if (text == null || text.length() == 0)
						continue;
					// Decide whether the message is single or concatenated
					String str = text.substring(15);
					// Concatenated messages contain timestamp in the beginning
					if(str.matches("^[0-9]{15,15}")) {
						ArrayList<String> temp = new ArrayList<String>();
						// adding the text of first message with unique datetime stamp
						temp.add(ib.getText().substring(15));
						// looping to find all the messages with the same datetime stamp
						for(InboundMessage im : list){
							if(im == null){
								j++;
								continue;
							}
							if(ib.getText().substring(0, 14).equals(im.getText().substring(0, 14))){
								// if match is found, insert it into the arraylist
								// so that the arraylist contains all the parts of the message
								temp.add(im.getText().substring(19));
								tsc.getSmsService().markInboundAsRead(im.getReferenceNumber());
								list.set(j, null);
							}
							j++;
						}
						
						temp.remove(0);
						// find the part/chunk size in which message is split
						int chunk = temp.size();
						
						// assigning keys with same number as the chunk so that they are
						// concatenated in same order
						HashMap<Integer,String> messagePart = new HashMap<Integer,String>();
						for(int i = 0; i < temp.size(); i++){
							messagePart.put(Integer.parseInt(temp.get(i).substring(0, 1)), temp.get(i).substring(4));
						}
						// add a for loop here to concat using the maxlength
						// which is taken as chunk above
						String concatenatedMessage = "";
						for(int i = 1; i <= chunk; i++){
							concatenatedMessage += messagePart.get(i);
						}
						parseText(concatenatedMessage);
						ib.setStatus(InboundStatus.READ);
						tsc.getSmsService().markInboundAsRead(ib.getInboundId());
					}
					else {
						parseText(text);
						ib.setStatus(InboundStatus.READ);
						tsc.getSmsService().markInboundAsRead(ib.getInboundId());
					}
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
		//System.out.println(text.endsWith("/^,b-[0-9]+$/i"));
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
		String hostId = null;
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
		hostId = fields[j++];
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				resultDateObj = sdf.parse(resultDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//resultDateObj = parseDate(resultDate);
		}
		// if(rPending!=null) {
		ServerServiceImpl ssl = new ServerServiceImpl();
		GeneXpertResults[] gxp = null;
		GeneXpertResults gxpNew = null;
		try {
			System.out.println("Inside");
			gxp = ssl.findGeneXpertResults(sampleId, patientId);
			System.out.println("GXP:" + gxp.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventHandler eh = new EventHandler();
		if (gxp == null || gxp.length == 0) {
			GeneXpertResults gxpU = eh.createGeneXpertResults(patientId,
					sampleId, mtb, rif, resultDateObj, instrumentSerial,
					moduleId, cartridgeId, reagentLotId, parseDate(expDate),
					operatorId, pcId, hostId, probeResultA, probeResultB, probeResultC,
					probeResultD, probeResultE, probeResultSPC, probeCtA,
					probeCtB, probeCtC, probeCtD, probeCtE, probeCtSPC,
					probeEndptA, probeEndptB, probeEndptC, probeEndptD,
					probeEndptE, probeEndptSPC, errorCode, errorNotes, notes,
					systemId);
			if(gxpU.getDateTested() == null){
				gxpU.setDateTested(new Date());
			}
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
			if(gxpNew.getDateTested() == null){
				gxpNew.setDateTested(new Date());
			}
			gxpNew.setInstrumentSerial(instrumentSerial);
			gxpNew.setModuleId(moduleId);
			gxpNew.setReagentLotId(reagentLotId);
			gxpNew.setCartridgeId(cartridgeId);
			gxpNew.setLaboratoryId(systemId);
			gxpNew.setPcId(pcId);
			gxpNew.setHostId(hostId);
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
		/*reader.parseText("101130800001-9^141016_001^MTB DETECTED MEDIUM^Rif Resistance NOT DETECTED^Machine API Test^CEPHEID5G183R1^IHS^OWAIS^708228^618255^204304821^10713-AX^2015-05-23^no^no^yes^5002^Post-run analysis error^no^Just XDR-TB^POS^NO RESULT^NEG^NEG^POS^0^1.1^2.2^2.3^1.3^1.4^2.5^3.6^4.7^4.5^3.2^1.0^0.0");
		// Results without probes
		reader.parseText("101130800001-9^141016_001^MTB DETECTED MEDIUM^Rif Resistance NOT DETECTED^Machine API Test^CEPHEID5G183R1^IHS^OWAIS^708228^618255^204304821^10713-AX^2015-05-23^no^no^yes^5002^Post-run analysis error^no^No PROBlems");*/
		// Test Record
		reader.parseText("Test-R^07-01-1297-15-R^MTB DETECTED^^PRL-SINDH^Cepheid2H0D7V1^PRL-SINDH^PRL-Sindh^802274^624130^235826832^18303^20150709131815^yes^no^no^no^W-02,b-54");
	}
}