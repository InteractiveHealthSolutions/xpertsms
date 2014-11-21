/**
 * Utility class for help and other documents
 */
package com.ihsinformatics.xpertsms.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.ihsinformatics.xpertsms.constant.FileConstants;

/**
 * @author owais.hussain@irdresearch.org
 *
 */
public class DocumentUtil
{
	private Properties props;
	/**
	 * @param args
	 */
	public static void main (String[] args)
	{
		DocumentUtil docUtil = new DocumentUtil (FileConstants.XPERT_DOCUMENT_FILE);
		String description = docUtil.getDescription ("assay");
		System.out.println (description);
	}

	public DocumentUtil (String file)
	{
		try
		{
			props = new Properties ();
			props.load (new FileInputStream (file));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch description from a JSON document file of given key
	 * 
	 * @param file
	 * @param field
	 * @return
	 */
	public String getDescription (String key)
	{
		String description = "";
		if(props.containsKey (key))
			description = props.getProperty (key);
		return description;
	}
}
