/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Parser class for GeneXpert Patient records
 */
package com.ihsinformatics.xpertsms.parser.astm;

import com.ihsinformatics.xpertsms.constant.ASTMMessageConstants;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;
import com.ihsinformatics.xpertsms.net.exception.InvalidASTMMessageFormatException;

/**
 * @author ali.habib@irdresearch.org
 */
public class PatientRecordParser extends BaseParser {
	
	public PatientRecordParser(XpertASTMResultUploadMessage record,String messageString) {
		super(record,messageString);
	}

	public void parse() throws InvalidASTMMessageFormatException {
		
		String[] fields = messageString.split("\\" + XpertASTMResultUploadMessage.FIELD_DELIMITER);
		if (fields.length > ASTMMessageConstants.MAX_NUM_PATIENT_RECORD_FIELDS) {
			throw new InvalidASTMMessageFormatException("P001 - Patient record must have at most " + ASTMMessageConstants.MAX_NUM_PATIENT_RECORD_FIELDS + " fields");
		}
		if(fields.length == 5) {
			setPatientId(fields[4]);
		}
	}
	
	public void setPatientId(String field) throws InvalidASTMMessageFormatException {
		if(field.length() < 1 || field.length() > ASTMMessageConstants.MAX_PATIENT_ID_LENGTH) {
			throw new InvalidASTMMessageFormatException("P002 - Patient ID must be between 1 and " + ASTMMessageConstants.MAX_PATIENT_ID_LENGTH + " characters");
		}
		record.setPatientId(field.trim());
	}
}
