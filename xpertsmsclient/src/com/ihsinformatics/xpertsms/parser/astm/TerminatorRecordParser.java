
package com.ihsinformatics.xpertsms.parser.astm;

import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;

/**
 * @author ali.habib@irdresearch.org
 */
public class TerminatorRecordParser extends BaseParser
{
	public TerminatorRecordParser (XpertASTMResultUploadMessage record, String messageString)
	{
		super (record, messageString);
	}

	@Override
	public void parse () throws Exception
	{
	}
}
