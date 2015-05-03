/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.parser.astm;

import com.ihsinformatics.xpertsms.constant.ASTMMessageConstants;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;
import com.ihsinformatics.xpertsms.net.exception.InvalidASTMMessageFormatException;

public class CommentRecordParser extends BaseParser {
	
	public CommentRecordParser(XpertASTMResultUploadMessage record, String messageString) {
		super(record, messageString);
	}
	
	public void parse() throws InvalidASTMMessageFormatException {
		String[] fields = messageString.split("\\" + XpertASTMResultUploadMessage.FIELD_DELIMITER);
		if (fields.length != ASTMMessageConstants.NUM_COMMENT_FIELDS) {
			throw new InvalidASTMMessageFormatException("C001 - Comment record must have "
			        + ASTMMessageConstants.NUM_ORDER_FIELDS + " fields");
		}
		if (fields[4].length() > ASTMMessageConstants.MAX_COMMENT_TYPE_LENGTH) {
			throw new InvalidASTMMessageFormatException("C002 - Comment type must have at most "
			        + ASTMMessageConstants.MAX_COMMENT_TYPE_LENGTH + " characters");
		}
		if (fields[4].charAt(0) == ASTMMessageConstants.COMMENT_TYPE_ERROR) {
			record.setError(true);
			setErrorDetails(fields[3]);
		}
		else if (fields[4].charAt(0) == ASTMMessageConstants.COMMENT_TYPE_NOTES) {
			setNotes(fields[3]);
		}
	}
	
	public void setErrorDetails(String field) throws InvalidASTMMessageFormatException {
		String[] errorDetails = field.split("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		if (errorDetails.length != ASTMMessageConstants.MAX_COMMENT_SOURCE_COMPONENTS) {
			throw new InvalidASTMMessageFormatException("C003 - Comment source must have "
			        + ASTMMessageConstants.MAX_COMMENT_SOURCE_COMPONENTS + " components");
		}
		record.setErrorCode(errorDetails[1]);
		if (errorDetails.length > 3)
			record.setErrorNotes(errorDetails[3] + ": " + errorDetails[3]);
	}
	
	/**
	 * Parses the Notes field and sets to ASTMMessage object
	 * @param field
	 * @throws InvalidASTMMessageFormatException
	 */
	public void setNotes(String field) throws InvalidASTMMessageFormatException {
		String[] notesParts = field.split("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		if (notesParts.length != ASTMMessageConstants.MAX_NOTES_COMPONENTS) {
			throw new InvalidASTMMessageFormatException("C004 - Notes source must have "
			        + ASTMMessageConstants.MAX_NOTES_COMPONENTS + " components");
		}
		record.setNotes(notesParts[2]);
	}
	
	public static void main(String[] args) {
		XpertASTMResultUploadMessage message = new XpertASTMResultUploadMessage();
		String messageString = "C|1|I|Error^5011^Post-run analysis error^Error 5011: Signal loss detected in the amplification curve for analyte [Probe B]. 12.6 decrease in signal with 23.6% decrease at cycle 5.^20111025171012|N";
		CommentRecordParser cp = new CommentRecordParser(message, messageString);
		try {
			cp.parse();
		}
		catch (InvalidASTMMessageFormatException e) {
			e.printStackTrace();
		}
		System.out.println(message.toString());
		messageString = "C|1|I|Notes^^Iducing Error - Test|I";
		cp = new CommentRecordParser(message, messageString);
		try {
			cp.parse();
		}
		catch (InvalidASTMMessageFormatException e) {
			e.printStackTrace();
		}
		System.out.println(message.toString());
	}
}
