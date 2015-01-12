/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
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
