/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.model.astm;

import java.util.Date;

/**
 * @author ali.habib@irdresearch.org
 */
public class TestTransmissionContent {
	
	private String sampleId;
	
	private Date resultDate;
	
	private String mtbResult;
	
	private String rifResult;
	
	private String serverAddres;
	
	private int serverPort;
	
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
	 * @return the resultDate
	 */
	public Date getResultDate() {
		return resultDate;
	}
	
	/**
	 * @param resultDate the resultDate to set
	 */
	public void setResultDate(Date resultDate) {
		this.resultDate = resultDate;
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
	
	/**
	 * @return the serverAddres
	 */
	public String getServerAddres() {
		return serverAddres;
	}
	
	/**
	 * @param serverAddres the serverAddres to set
	 */
	public void setServerAddres(String serverAddres) {
		this.serverAddres = serverAddres;
	}
	
	/**
	 * @return the serverPort
	 */
	public int getServerPort() {
		return serverPort;
	}
	
	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	public String getUrl() {
		String url = null;
		
		return url;
	}
	
	public String getPostParams() {
		String postParams = null;
		
		return postParams;
	}
	
}
