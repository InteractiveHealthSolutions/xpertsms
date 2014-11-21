
package com.ihsinformatics.xpertsms.parser.astm;

import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;

public abstract class BaseParser
{
	protected XpertASTMResultUploadMessage	record;
	protected String						messageString;

	public BaseParser (XpertASTMResultUploadMessage record, String messageString)
	{
		this.record = record;
		this.messageString = messageString;
	}
	
	public abstract void parse () throws Exception;
}
