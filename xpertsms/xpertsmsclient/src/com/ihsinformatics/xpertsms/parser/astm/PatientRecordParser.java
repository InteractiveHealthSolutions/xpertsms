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
