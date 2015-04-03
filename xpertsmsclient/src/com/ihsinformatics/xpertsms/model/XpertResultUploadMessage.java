/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Abstract class to be implemented for certain standard, like HL7 or ASTM
 */

package com.ihsinformatics.xpertsms.model;

import net.sf.json.JSONObject;

/**
 * @author ali.habib@irdresearch.org
 */
public abstract class XpertResultUploadMessage {
	
	protected String patientId;
	
	protected String sampleId;
	
	protected String mtbResult;
	
	protected String rifResult;
	
	protected String probeResultA;
	
	protected String probeResultB;
	
	protected String probeResultC;
	
	protected String probeResultD;
	
	protected String probeResultE;
	
	protected String probeResultSpc;
	
	protected String probeCtA;
	
	protected String probeCtB;
	
	protected String probeCtC;
	
	protected String probeCtD;
	
	protected String probeCtE;
	
	protected String probeCtSPC;
	
	protected String probeEndptA;
	
	protected String probeEndptB;
	
	protected String probeEndptC;
	
	protected String probeEndptD;
	
	protected String probeEndptE;
	
	protected String probeEndptSpc;
	
	protected String qc1;
	
	protected String qc2;
	
	protected String qc1Ct;
	
	protected String qc2Ct;
	
	protected String qc1Endpt;
	
	protected String qc2Endpt;
	
	protected int retries;
	
	public XpertResultUploadMessage() {
		patientId = null;
		sampleId = null;
		mtbResult = null;
		rifResult = null;
		
		probeResultA = null;
		probeResultB = null;
		probeResultC = null;
		probeResultD = null;
		probeResultE = null;
		probeResultSpc = null;
		
		probeCtA = null;
		probeCtB = null;
		probeCtC = null;
		probeCtD = null;
		probeCtE = null;
		probeCtSPC = null;
		
		probeEndptA = null;
		probeEndptB = null;
		probeEndptC = null;
		probeEndptD = null;
		probeEndptE = null;
		probeEndptSpc = null;
		
	}
	
	/**
	 * @return the patientId
	 */
	public String getPatientId() {
		return patientId;
	}
	
	/**
	 * @param patientId the patientId to set
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
	/**
	 * @return the sampleId
	 */
	public String getSampleId() {
		return sampleId;
	}
	
	/**
	 * @param sampleId the sampleId to set
	 */
	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}
	
	/**
	 * @return the mtbResult
	 */
	public String getMtbResult() {
		return mtbResult;
	}
	
	/**
	 * @param mtbResult the mtbResult to set
	 */
	public void setMtbResult(String mtbResult) {
		this.mtbResult = mtbResult;
	}
	
	/**
	 * @return the rifResult
	 */
	public String getRifResult() {
		return rifResult;
	}
	
	/**
	 * @param rifResult the rifResult to set
	 */
	public void setRifResult(String rifResult) {
		this.rifResult = rifResult;
	}
	
	public String toPostParams() {
		return "";
	}
	
	/**
	 * @return the probeResultA
	 */
	public String getProbeResultA() {
		return probeResultA;
	}
	
	/**
	 * @param probeResultA the probeResultA to set
	 */
	public void setProbeResultA(String probeResultA) {
		this.probeResultA = probeResultA;
	}
	
	/**
	 * @return the probeResultB
	 */
	public String getProbeResultB() {
		return probeResultB;
	}
	
	/**
	 * @param probeResultB the probeResultB to set
	 */
	public void setProbeResultB(String probeResultB) {
		this.probeResultB = probeResultB;
	}
	
	/**
	 * @return the probeResultC
	 */
	public String getProbeResultC() {
		return probeResultC;
	}
	
	/**
	 * @param probeResultC the probeResultC to set
	 */
	public void setProbeResultC(String probeResultC) {
		this.probeResultC = probeResultC;
	}
	
	/**
	 * @return the probeResultD
	 */
	public String getProbeResultD() {
		return probeResultD;
	}
	
	/**
	 * @param probeResultD the probeResultD to set
	 */
	public void setProbeResultD(String probeResultD) {
		this.probeResultD = probeResultD;
	}
	
	/**
	 * @return the probeResultE
	 */
	public String getProbeResultE() {
		return probeResultE;
	}
	
	/**
	 * @param probeResultE the probeResultE to set
	 */
	public void setProbeResultE(String probeResultE) {
		this.probeResultE = probeResultE;
	}
	
	/**
	 * @return the probeResultSpc
	 */
	public String getProbeResultSPC() {
		return probeResultSpc;
	}
	
	/**
	 * @param probeResultSpc the probeResultSpc to set
	 */
	public void setProbeResultSPC(String probeResultSPC) {
		this.probeResultSpc = probeResultSPC;
	}
	
	/**
	 * @return the probeCtA
	 */
	public String getProbeCtA() {
		return probeCtA;
	}
	
	/**
	 * @param probeCtA the probeCtA to set
	 */
	public void setProbeCtA(String probeCtA) {
		this.probeCtA = probeCtA;
	}
	
	/**
	 * @return the probeCtB
	 */
	public String getProbeCtB() {
		return probeCtB;
	}
	
	/**
	 * @param probeCtB the probeCtB to set
	 */
	public void setProbeCtB(String probeCtB) {
		this.probeCtB = probeCtB;
	}
	
	/**
	 * @return the probeCtC
	 */
	public String getProbeCtC() {
		return probeCtC;
	}
	
	/**
	 * @param probeCtC the probeCtC to set
	 */
	public void setProbeCtC(String probeCtC) {
		this.probeCtC = probeCtC;
	}
	
	/**
	 * @return the probeCtD
	 */
	public String getProbeCtD() {
		return probeCtD;
	}
	
	/**
	 * @param probeCtD the probeCtD to set
	 */
	public void setProbeCtD(String probeCtD) {
		this.probeCtD = probeCtD;
	}
	
	/**
	 * @return the probeCtE
	 */
	public String getProbeCtE() {
		return probeCtE;
	}
	
	/**
	 * @param probeCtE the probeCtE to set
	 */
	public void setProbeCtE(String probeCtE) {
		this.probeCtE = probeCtE;
	}
	
	/**
	 * @return the probeCtSPC
	 */
	public String getProbeCtSPC() {
		return probeCtSPC;
	}
	
	/**
	 * @param probeCtSPC the probeCtSPC to set
	 */
	public void setProbeCtSPC(String probeCtSPC) {
		this.probeCtSPC = probeCtSPC;
	}
	
	/**
	 * @return the probeEndptA
	 */
	public String getProbeEndptA() {
		return probeEndptA;
	}
	
	/**
	 * @param probeEndptA the probeEndptA to set
	 */
	public void setProbeEndPtA(String probeEndptA) {
		this.probeEndptA = probeEndptA;
	}
	
	/**
	 * @return the probeEndptB
	 */
	public String getProbeEndptB() {
		return probeEndptB;
	}
	
	/**
	 * @param probeEndptB the probeEndptB to set
	 */
	public void setProbeEndPtB(String probeEndptB) {
		this.probeEndptB = probeEndptB;
	}
	
	/**
	 * @return the probeEndptC
	 */
	public String getProbeEndptC() {
		return probeEndptC;
	}
	
	/**
	 * @param probeEndptC the probeEndptC to set
	 */
	public void setProbeEndPtC(String probeEndptC) {
		this.probeEndptC = probeEndptC;
	}
	
	/**
	 * @return the probeEndptD
	 */
	public String getProbeEndptD() {
		return probeEndptD;
	}
	
	/**
	 * @param probeEndptD the probeEndptD to set
	 */
	public void setProbeEndPtD(String probeEndptD) {
		this.probeEndptD = probeEndptD;
	}
	
	/**
	 * @return the probeEndptE
	 */
	public String getProbeEndptE() {
		return probeEndptE;
	}
	
	/**
	 * @param probeEndptE the probeEndptE to set
	 */
	public void setProbeEndPtE(String probeEndptE) {
		this.probeEndptE = probeEndptE;
	}
	
	/**
	 * @return the probeEndptSpc
	 */
	public String getProbeEndptSPC() {
		return probeEndptSpc;
	}
	
	/**
	 * @param probeEndptSpc the probeEndptSpc to set
	 */
	public void setProbeEndPtSPC(String probeEndptSPC) {
		this.probeEndptSpc = probeEndptSPC;
	}
	
	/**
	 * @return the qc1
	 */
	public String getQc1() {
		return qc1;
	}
	
	/**
	 * @param qc1 the qc1 to set
	 */
	public void setQc1(String qc1) {
		this.qc1 = qc1;
	}
	
	/**
	 * @return the qc2
	 */
	public String getQc2() {
		return qc2;
	}
	
	/**
	 * @param qc2 the qc2 to set
	 */
	public void setQc2(String qc2) {
		this.qc2 = qc2;
	}
	
	/**
	 * @return the qc1Ct
	 */
	public String getQc1Ct() {
		return qc1Ct;
	}
	
	/**
	 * @param qc1Ct the qc1Ct to set
	 */
	public void setQc1Ct(String qc1Ct) {
		this.qc1Ct = qc1Ct;
	}
	
	/**
	 * @return the qc2Ct
	 */
	public String getQc2Ct() {
		return qc2Ct;
	}
	
	/**
	 * @param qc2Ct the qc2Ct to set
	 */
	public void setQc2Ct(String qc2Ct) {
		this.qc2Ct = qc2Ct;
	}
	
	/**
	 * @return the qc1Endpt
	 */
	public String getQc1Endpt() {
		return qc1Endpt;
	}
	
	/**
	 * @param qc1Endpt the qc1Endpt to set
	 */
	public void setQc1Endpt(String qc1Endpt) {
		this.qc1Endpt = qc1Endpt;
	}
	
	/**
	 * @return the qc2Endpt
	 */
	public String getQc2Endpt() {
		return qc2Endpt;
	}
	
	/**
	 * @param qc2Endpt the qc2Endpt to set
	 */
	public void setQc2Endpt(String qc2Endpt) {
		this.qc2Endpt = qc2Endpt;
	}
	
	/**
	 * @return the retries
	 */
	public int getRetries() {
		return retries;
	}
	
	/**
	 * @param retries the retries to set
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}
	
	public String toSMS() {
		String smsText = "";
		smsText += replaceNull(sampleId) + "^" + replaceNull(mtbResult) + "^" + replaceNull(rifResult) + "^"
		        + replaceNull(probeResultA) + "^" + replaceNull(probeResultB) + "^" + replaceNull(probeResultC) + "^"
		        + replaceNull(probeResultD) + "^" + replaceNull(probeResultE) + "^" + replaceNull(probeResultSpc) + "^"
		        + replaceNull(probeCtA) + "^" + replaceNull(probeCtB) + "^" + replaceNull(probeCtC) + "^"
		        + replaceNull(probeCtD) + "^" + replaceNull(probeCtE) + "^" + replaceNull(probeCtSPC) + "^"
		        + replaceNull(probeEndptA) + "^" + replaceNull(probeEndptB) + "^" + replaceNull(probeEndptB) + "^"
		        + replaceNull(probeEndptC) + "^" + replaceNull(probeEndptD) + "^" + replaceNull(probeEndptE) + "^"
		        + replaceNull(probeEndptSpc);
		
		return smsText;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return patientId + ", " + sampleId + ", " + mtbResult + ", " + rifResult + ", " + probeResultA + ", " + probeResultB
		        + ", " + probeResultC + ", " + probeResultD + ", " + probeResultE + ", " + probeResultSpc + ", " + probeCtA
		        + ", " + probeCtB + ", " + probeCtC + ", " + probeCtD + ", " + probeCtE + ", " + probeCtSPC + ", "
		        + probeEndptA + ", " + probeEndptB + ", " + probeEndptC + ", " + probeEndptD + ", " + probeEndptE + ", "
		        + probeEndptSpc + ", " + qc1 + ", " + qc2 + ", " + qc1Ct + ", " + qc2Ct + ", " + qc1Endpt + ", " + qc2Endpt
		        + ", " + retries;
	}
	
	/**
	 * Checks if an object is NULL, returns empty string if true
	 * 
	 * @param text
	 * @return
	 */
	public String replaceNull(Object text) {
		if (text == null)
			return "";
		return text.toString();
	}
	
	public abstract String toCsv();
	
	public abstract String toSqlQuery();
	
	public abstract JSONObject toJson();
}
