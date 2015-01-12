/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * 
 */

package com.ihsinformatics.xpertsms.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import javax.swing.JOptionPane;
import com.ihsinformatics.xpertsms.constant.FileConstants;

/**
 * @author owais.hussain@irdresearch.org
 *
 */
public final class XpertProperties
{
	public static Properties	props;

	public static final String	CSV_EXPORT				= "csv_export";
	public static final String	SMS_EXPORT				= "sms_export";
	public static final String	GXA_EXPORT				= "gxa_export";
	public static final String	OPENMRS_EXPORT			= "openmrs_export";
	public static final String	WEB_EXPORT				= "web_export";

	public static final String	MTB_CODE				= "mtb_code";
	public static final String	RIF_CODE				= "rif_code";
	public static final String	QC_CODE					= "qc_code";
	public static final String	LOCAL_PORT				= "local_port";

	public static final String	XPERT_USER				= "xpert_user";
	public static final String	XPERT_PASSWORD			= "xpert_password";

	public static final String	SMS_SERVER_ADDRESS		= "sms_server_address";
	public static final String	SMS_PORT				= "sms_port";
	public static final String	SMS_ADMIN_PHONE			= "sms_admin_phone";
	public static final String	SMS_DATE_FORMAT			= "sms_date_format";
	public static final String	SMS_VARIABLES			= "sms_variables";

	public static final String	CSV_FOLDER_PATH			= "csv_folder_path";
	public static final String	CSV_FIELD_SEPARATOR		= "csv_field_separator";
	public static final String	CSV_USE_QUOTES			= "csv_use_quotes";
	public static final String	CSV_DATE_FORMAT			= "csv_date_format";
	public static final String	CSV_VARIABLES			= "csv_variables";

	public static final String	GXA_SERVER_ADDRESS		= "gxa_server_address";
	public static final String	GXA_PORT				= "gxa_port";
	public static final String	GXA_API_KEY				= "gxa_api_key";
	public static final String	GXA_DATE_FORMAT			= "gxa_date_format";
	public static final String	GXA_SSL_ENCRYPTION		= "gxa_ssl_encryption";

	public static final String	OPENMRS_REST_ADDRESS	= "openmrs_rest_address";
	public static final String	OPENMRS_USER			= "openmrs_user";
	public static final String	OPENMRS_PASSWORD		= "openmrs_password";
	public static final String	OPENMRS_DATE_FORMAT		= "openmrs_date_format";
	public static final String	OPENMRS_ENCOUNTER_TYPE	= "openmrs_encounter_type";
	public static final String	OPENMRS_CONCEPT_MAP		= "openmrs_concept_map";
	public static final String	OPENMRS_SSL_ENCRYPTION	= "openmrs_ssl_encryption";

	public static final String	SERVER_URL	= null;

	public static final String	SERVER_PORT	= null;

	public static final String	WEB_APP_STRING	= null;

	public static final String	EXPORT_PROBES	= null;

	/**
	 * Read properties from properties file
	 */
	public static void readProperties ()
	{
		props = new Properties ();
		if (new File (FileConstants.FILE_PATH).exists ())
		{
			try
			{
				props.load (new FileInputStream (FileConstants.FILE_PATH));
			}
			catch (IOException e)
			{
				e.printStackTrace ();
			}
		}
		else
		{
			props.setProperty (CSV_EXPORT, "NO");
			props.setProperty (SMS_EXPORT, "NO");
			props.setProperty (GXA_EXPORT, "NO");
			props.setProperty (OPENMRS_EXPORT, "NO");
			props.setProperty (WEB_EXPORT, "YES");
			props.setProperty (MTB_CODE, "");
			props.setProperty (RIF_CODE, "");
			props.setProperty (QC_CODE, "");
			props.setProperty (LOCAL_PORT, "");
			props.setProperty (XPERT_USER, "");
			props.setProperty (XPERT_PASSWORD, "");
			props.setProperty (SMS_SERVER_ADDRESS, "");
			props.setProperty (SMS_PORT, "");
			props.setProperty (SMS_ADMIN_PHONE, "");
			props.setProperty (SMS_DATE_FORMAT, "");
			props.setProperty (SMS_VARIABLES, "");
			props.setProperty (CSV_FOLDER_PATH, "");
			props.setProperty (CSV_FIELD_SEPARATOR, "");
			props.setProperty (CSV_USE_QUOTES, "");
			props.setProperty (CSV_DATE_FORMAT, "");
			props.setProperty (CSV_VARIABLES, "");
			props.setProperty (GXA_SERVER_ADDRESS, "");
			props.setProperty (GXA_PORT, "");
			props.setProperty (GXA_API_KEY, "");
			props.setProperty (GXA_DATE_FORMAT, "");
			props.setProperty (GXA_SSL_ENCRYPTION, "");
			props.setProperty (OPENMRS_REST_ADDRESS, "");
			props.setProperty (OPENMRS_USER, "");
			props.setProperty (OPENMRS_PASSWORD, "");
			props.setProperty (OPENMRS_DATE_FORMAT, "");
			props.setProperty (OPENMRS_ENCOUNTER_TYPE, "");
			props.setProperty (OPENMRS_CONCEPT_MAP, "");
			props.setProperty (OPENMRS_SSL_ENCRYPTION, "");
		}
	}

	/**
	 * Write properties to properties file and reads back
	 */
	public static boolean writeProperties (Map<String, String> properties)
	{
		boolean success = false;
		if (properties.isEmpty ())
		{
			System.out.println ("No properties to write to file.");
		}
		Set<Entry<String, String>> entrySet = properties.entrySet ();
		for (Iterator<Entry<String, String>> iter = entrySet.iterator (); iter.hasNext ();)
		{
			Entry<String, String> pair = iter.next ();
			props.setProperty (pair.getKey (), pair.getValue ());
		}
		try
		{
			if (!(new File (FileConstants.XPERT_SMS_DIR).exists ()))
			{
				boolean checkDir = new File (FileConstants.XPERT_SMS_DIR).mkdir ();
				if (!checkDir)
				{
					JOptionPane.showMessageDialog (null, "Could not create properties file. Please check the permissions of your home folder.", "Error!", JOptionPane.ERROR_MESSAGE);
				}
			}
			props.store (new FileOutputStream (FileConstants.FILE_PATH), null);
			props.load (new FileInputStream (FileConstants.FILE_PATH));
			JOptionPane.showMessageDialog (null, "Settings updated!", "Success!", JOptionPane.INFORMATION_MESSAGE);
			success = true;
		}
		catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace ();
			JOptionPane.showMessageDialog (null, "Could not create properties file. Please check the permissions of your home folder.", "Error!", JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace ();
			JOptionPane.showMessageDialog (null, "Could not create properties file.", "Error!", JOptionPane.ERROR_MESSAGE);
		}
		return success;
	}

	public static String getProperty(String key)
	{
		return props.getProperty (key);
	}
}
