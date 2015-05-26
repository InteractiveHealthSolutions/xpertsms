/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Parser for GeneXpert results
 */
package com.ihsinformatics.xpertsms.parser.astm;

import com.ihsinformatics.xpertsms.constant.ASTMMessageConstants;
import com.ihsinformatics.xpertsms.model.XpertProperties;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;
import com.ihsinformatics.xpertsms.net.exception.InvalidASTMMessageFormatException;
import com.ihsinformatics.xpertsms.ui.ControlPanel;

/**
 * @author ali.habib@irdresearch.org
 */
public class ResultRecordParser extends BaseParser {
	
	private boolean isMtbResult;
	
	private boolean isRifResult;
	
	public ResultRecordParser(XpertASTMResultUploadMessage record, String messageString) {
		super(record, messageString);
		isMtbResult = false;
		isRifResult = false;
	}
	
	public void parse() throws InvalidASTMMessageFormatException {
		boolean isProbe = false;
		String[] fields = messageString.split("\\" + XpertASTMResultUploadMessage.FIELD_DELIMITER);
		if (fields.length > ASTMMessageConstants.MAX_NUM_RESULT_FIELDS
		        || fields.length < ASTMMessageConstants.MIN_NUM_RESULT_FIELDS) {
			throw new InvalidASTMMessageFormatException("R001 - Result record must have not more than "
			        + ASTMMessageConstants.MAX_NUM_RESULT_FIELDS + " fields");
		}
		// at this point we are only dealing with the main results and not
		// probes
		if (fields.length == ASTMMessageConstants.NUM_PROBE_RESULT_FIELDS) {
			isProbe = true;
		}
		if (!isProbe) {
			setUniversalTestId(fields[2]);
			// In case of QC
			if (!isMTBResult() && !isRifResult())
				return;
			setResultValue(fields[3]);
			setResultStatus(fields[8]);
			if (fields[10].length() != 0)
				setOperatorId(fields[10]);
			if (fields[11].length() != 0)
				setTestStartDate(fields[11]);
			if (fields[12].length() != 0)
				setTestEndDate(fields[12]);
			if (fields[13].length() != 0)
				setInstrumentData(fields[13]);
		}
		else if (ControlPanel.props.getProperty(XpertProperties.EXPORT_PROBES).equals("YES")) {
			setProbeResult(fields[2], fields[3]);
		}
	}
	
	private void setUniversalTestId(String field) throws InvalidASTMMessageFormatException {
		String universalTestId[] = field.split("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		if (universalTestId.length < 7) {
			throw new InvalidASTMMessageFormatException("R002 - Universal Test Id must contain at least 7 fields");
		}
		// if the result isn't MTB or Rif, return
		if (universalTestId[3].equals(ControlPanel.props.getProperty(XpertProperties.MTB_CODE))) {
			isMtbResult = true;
			isRifResult = false;
		} else if (universalTestId[3].equals(ControlPanel.props.getProperty(XpertProperties.RIF_CODE))) {
			isMtbResult = false;
			isRifResult = true;
		} else
			return;
		if (universalTestId[1].length() > ASTMMessageConstants.MAX_UNIVERSAL_TEST_ID_LENGTH) {
			throw new InvalidASTMMessageFormatException("R003 - Universal Test Id must contain at most "
			        + ASTMMessageConstants.MAX_UNIVERSAL_TEST_ID_LENGTH + " characters");
		}
		if (record.getUniversalTestId() == null)
			record.setUniversalTestId(universalTestId[1]);
		if (universalTestId[3].length() > ASTMMessageConstants.MAX_SYSTEM_DEFINED_TEST_ID_LENGTH) {
			throw new InvalidASTMMessageFormatException("R004 - System defined test Id must contain at most "
			        + ASTMMessageConstants.MAX_SYSTEM_DEFINED_TEST_ID_LENGTH + " characters");
		}
		if (universalTestId[4].length() > ASTMMessageConstants.MAX_SYSTEM_DEFINED_TEST_NAME_LENGTH) {
			throw new InvalidASTMMessageFormatException("R005 - System defined test name must contain at most "
			        + ASTMMessageConstants.MAX_SYSTEM_DEFINED_TEST_NAME_LENGTH + " characters");
		}
		record.setSystemDefinedTestName(universalTestId[4]);
		if (universalTestId[5].length() > ASTMMessageConstants.MAX_SYSTEM_DEFINED_TEST_VERSION_LENGTH) {
			throw new InvalidASTMMessageFormatException("R006 - System defined test version must contain at most "
			        + ASTMMessageConstants.MAX_SYSTEM_DEFINED_TEST_VERSION_LENGTH + " characters");
		}
		record.setSystemDefinedTestVersion(universalTestId[5]);
		
		// don't need to get into analyte name because we have the code
		/*
		 * if(universalTestId[6].length() >
		 * ASTMMessageConstants.MAX_ANALYTE_NAME_LENGTH) { throw new
		 * InvalidASTMMessageFormatException
		 * ("R007 - System defined test name must contain at most " +
		 * ASTMMessageConstants.MAX_ANALYTE_NAME_LENGTH + " characters"); }
		 */
	}
	
	private void setResultValue(String field) throws InvalidASTMMessageFormatException {
		String result[] = field.split("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		if (result.length == 0)
			return;
		if (result.length > 2) {
			throw new InvalidASTMMessageFormatException("R007 - Result Measurement Value must contain at most 2 fields");
		}
		if (result[0].length() > ASTMMessageConstants.MAX_QUAL_VALUE_LENGTH) {
			throw new InvalidASTMMessageFormatException("R008 - Test result name must contain at most "
			        + ASTMMessageConstants.MAX_QUAL_VALUE_LENGTH + " characters");
		}
		if (isMTBResult()) {
			record.setMtbResult(result[0]);
		} else if (isRifResult()) {
			record.setRifResult(result[0]);
		}
	}
	
	private void setResultStatus(String field) throws InvalidASTMMessageFormatException {
		String status[] = field.split("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		char stat;
		for (int i = 0; i < status.length; i++) {
			if (status[i].length() > ASTMMessageConstants.MAX_RESULT_STATUS_LENGTH) {
				throw new InvalidASTMMessageFormatException("R009 - Each component of Result Status must contain at most "
				        + ASTMMessageConstants.MAX_RESULT_STATUS_LENGTH + " characters");
			}
			stat = status[i].charAt(0);
			switch (stat) {
				case ASTMMessageConstants.FINAL_RESULT:
					record.setFinal(true);
					break;
				case ASTMMessageConstants.PENDING_RESULT:
					record.setPending(true);
					break;
				case ASTMMessageConstants.ERROR_RESULT:
					record.setError(true);
					break;
				case ASTMMessageConstants.CORRECTED_RESULT:
					record.setCorrection(true);
					break;
			}
			;
		}
	}
	
	private void setOperatorId(String field) throws InvalidASTMMessageFormatException {
		if (field.length() > ASTMMessageConstants.MAX_OPERATOR_ID_LENGTH) {
			throw new InvalidASTMMessageFormatException("R010 - Operator ID must be at most "
			        + ASTMMessageConstants.MAX_OPERATOR_ID_LENGTH + " characters");
		}
		record.setOperatorId(field);
	}
	
	private void setTestStartDate(String field) throws InvalidASTMMessageFormatException {
		if (field.length() > ASTMMessageConstants.MAX_DATE_LENGTH) {
			throw new InvalidASTMMessageFormatException("R011 - Start date must be at most "
			        + ASTMMessageConstants.MAX_DATE_LENGTH + " characters");
		}
		record.setTestStartDate(field);
	}
	
	private void setTestEndDate(String field) throws InvalidASTMMessageFormatException {
		if (field.length() > ASTMMessageConstants.MAX_DATE_LENGTH) {
			throw new InvalidASTMMessageFormatException("R012 - End date must be at most "
			        + ASTMMessageConstants.MAX_DATE_LENGTH + " characters");
		}
		record.setTestEndDate(field);
	}
	
	private void setInstrumentData(String field) throws InvalidASTMMessageFormatException {
		String instData[] = field.split("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		if (instData.length > 0) {
			record.setPcId(instData[0]);
		}
		if (instData.length > 1) {
			record.setInstrumentSerial(instData[1]);
		}
		if (instData.length > 2) {
			record.setModuleId(instData[2]);
		}
		if (instData.length > 3) {
			record.setCartridgeId(instData[3]);
		}
		if (instData.length > 4) {
			record.setReagentLotId(instData[4]);
		}
		if (instData.length > 5) {
			record.setExpDate(instData[5]);
		}
	}
	
	public void setProbeResult(String type, String result) throws InvalidASTMMessageFormatException {
		String[] typeList = type.split("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		String[] resultList = result.split("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		/*
		 * R|2|^G4v5^^TBPos^^^Probe D^|NO RESULT^||| R|3|^G4v5^^TBPos^^^Probe
		 * D^Ct|^0||| R|4|^G4v5^^TBPos^^^Probe D^EndPt|^0|||
		 */
		String probeName = typeList[6];
		boolean isMainProbeName = false;
		if (typeList.length == 7) {
			isMainProbeName = true;
		}
		if (probeName.equals(ASTMMessageConstants.SPC)) {
			if (isMainProbeName) {
				if (record.getProbeResultSPC() == null) {
					record.setProbeResultSPC(resultList[0]);
				}
				return;
			} else {
				if (typeList[7].equals(ASTMMessageConstants.CT) && record.getProbeCtSPC() == null) {
					record.setProbeCtSPC(resultList[1]);
				} else if (typeList[7].equals(ASTMMessageConstants.ENDPT) && record.getProbeEndptSPC() == null) {
					record.setProbeEndPtSPC(resultList[1]);
				}
				return;
			}
		}
		
		else if (probeName.equalsIgnoreCase(ASTMMessageConstants.PROBE_A)) {
			if (isMainProbeName) {
				if (record.getProbeResultA() == null) {
					record.setProbeResultA(resultList[0]);
				}
				return;
			} else {
				if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.CT) && record.getProbeCtA() == null) {
					record.setProbeCtA(resultList[1]);
				} else if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.ENDPT) && record.getProbeEndptA() == null) {
					record.setProbeEndPtA(resultList[1]);
				}
				return;
			}
		}
		
		else if (probeName.equalsIgnoreCase(ASTMMessageConstants.PROBE_B)) {
			if (isMainProbeName) {
				if (record.getProbeResultB() == null) {
					record.setProbeResultB(resultList[0]);
				}
				return;
			} else {
				if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.CT) && record.getProbeCtB() == null) {
					record.setProbeCtB(resultList[1]);
				} else if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.ENDPT) && record.getProbeEndptB() == null) {
					record.setProbeEndPtB(resultList[1]);
				}
				return;
			}
		}
		
		else if (probeName.equalsIgnoreCase(ASTMMessageConstants.PROBE_C)) {
			if (isMainProbeName) {
				if (record.getProbeResultC() == null) {
					record.setProbeResultC(resultList[0]);
				}
				return;
			} else {
				if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.CT) && record.getProbeCtC() == null) {
					record.setProbeCtC(resultList[1]);
				} else if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.ENDPT) && record.getProbeEndptC() == null) {
					record.setProbeEndPtC(resultList[1]);
				}
				return;
			}
		}
		
		else if (probeName.equalsIgnoreCase(ASTMMessageConstants.PROBE_D)) {
			if (isMainProbeName) {
				if (record.getProbeResultD() == null) {
					record.setProbeResultD(resultList[0]);
				}
				return;
			} else {
				if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.CT) && record.getProbeCtD() == null) {
					record.setProbeCtD(resultList[1]);
				} else if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.ENDPT) && record.getProbeEndptD() == null) {
					record.setProbeEndPtD(resultList[1]);
				}
				return;
			}
		}
		
		else if (probeName.equalsIgnoreCase(ASTMMessageConstants.PROBE_E)) {
			if (isMainProbeName) {
				if (record.getProbeResultE() == null) {
					record.setProbeResultE(resultList[0]);
				}
				return;
			} else {
				if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.CT) && record.getProbeCtE() == null) {
					record.setProbeCtE(resultList[1]);
				} else if (typeList[7].equalsIgnoreCase(ASTMMessageConstants.ENDPT) && record.getProbeEndptE() == null) {
					record.setProbeEndPtE(resultList[1]);
				}
				return;
			}
		}
	}
	
	/**
	 * @return the isMtbResult
	 */
	public boolean isMTBResult() {
		return isMtbResult;
	}
	
	/**
	 * @param isMtbResult the isMtbResult to set
	 */
	public void setMTBResult(boolean isMTBResult) {
		this.isMtbResult = isMTBResult;
	}
	
	/**
	 * @return the isRifResult
	 */
	public boolean isRifResult() {
		return isRifResult;
	}
	
	/**
	 * @param isRifResult the isRifResult to set
	 */
	public void setRifResult(boolean isRifResult) {
		this.isRifResult = isRifResult;
	}
	
}
