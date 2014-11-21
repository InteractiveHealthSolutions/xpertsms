
package com.ihsinformatics.xpertsms.parser.astm;

import com.ihsinformatics.xpertsms.constant.ASTMMessageConstants;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;
import com.ihsinformatics.xpertsms.net.exception.InvalidASTMMessageFormatException;

public class CommentRecordParser extends BaseParser
{

	public CommentRecordParser (XpertASTMResultUploadMessage record, String messageString)
	{
		super (record, messageString);
	}

	public void parse () throws InvalidASTMMessageFormatException
	{
		String[] fields = messageString.split ("\\" + XpertASTMResultUploadMessage.FIELD_DELIMITER);
		if (fields.length != ASTMMessageConstants.NUM_COMMENT_FIELDS)
		{
			throw new InvalidASTMMessageFormatException ("C001 - Comment record must have " + ASTMMessageConstants.NUM_ORDER_FIELDS + " fields");
		}
		if (fields[4].length () > ASTMMessageConstants.MAX_COMMENT_TYPE_LENGTH)
		{
			throw new InvalidASTMMessageFormatException ("C002 - Comment type must have at most " + ASTMMessageConstants.MAX_COMMENT_TYPE_LENGTH + " characters");
		}
		if (fields[4].charAt (0) == ASTMMessageConstants.COMMENT_TYPE_ERROR)
		{
			record.setError (true);
			setErrorDetails (fields[3]);
		}
	}

	public void setErrorDetails (String field) throws InvalidASTMMessageFormatException
	{
		String[] errorDetails = field.split ("\\" + XpertASTMResultUploadMessage.COMPONENT_DELIMITER);
		if (errorDetails.length != ASTMMessageConstants.MAX_COMMENT_SOURCE_COMPONENTS)
		{
			throw new InvalidASTMMessageFormatException ("C003 - Comment source must have " + ASTMMessageConstants.MAX_COMMENT_SOURCE_COMPONENTS + " components");
		}
		record.setErrorCode (errorDetails[1]);
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * XpertASTMResultUploadMessage message = new
	 * XpertASTMResultUploadMessage(); String messageString =
	 * "C|1|I|Error^5011^Post-run analysis error^Error 5011: Signal loss detected in the amplification curve for analyte [Probe B]. 12.6 decrease in signal with 23.6% decrease at cycle 5.^20111025171012|N"
	 * ; CommentRecordParser cp = new
	 * CommentRecordParser(message,messageString); try { cp.parse(); } catch
	 * (InvalidASTMMessageFormatException e) { e.printStackTrace(); }
	 * System.out.println(message.toString()); }
	 */

}