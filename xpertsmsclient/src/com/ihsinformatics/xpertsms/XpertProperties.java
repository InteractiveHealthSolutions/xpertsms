/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms;

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
 * @author owais.hussain@ihsinformatics.com
 */
public final class XpertProperties {
	
	public static Properties props;
	
	public static final String VERSION = "version";
	
	public static final String BUILD = "build";
	
	public static final String CSV_EXPORT = "csv_export";
	
	public static final String SMS_EXPORT = "sms_export";
	
	public static final String GXA_EXPORT = "gxa_export";
	
	public static final String OPENMRS_EXPORT = "openmrs_export";
	
	public static final String WEB_EXPORT = "web_export";
	
	public static final String MTB_CODE = "mtb_code";
	
	public static final String RIF_CODE = "rif_code";
	
	public static final String QC_CODE = "qc_code";
	
	public static final String LOCAL_PORT = "local_port";
	
	public static final String SMS_PROJECT_NAME = "sms_server_address";
	
	public static final String SMS_PORT = "sms_port";
	
	public static final String SMS_ADMIN_PHONE = "sms_admin_phone";
	
	public static final String SMS_DATE_FORMAT = "sms_date_format";
	
	public static final String SMS_VARIABLES = "sms_variables";
	
	public static final String CSV_FOLDER_PATH = "csv_folder_path";
	
	public static final String CSV_FIELD_SEPARATOR = "csv_field_separator";
	
	public static final String CSV_USE_QUOTES = "csv_use_quotes";
	
	public static final String CSV_DATE_FORMAT = "csv_date_format";
	
	public static final String CSV_VARIABLES = "csv_variables";
	
	public static final String DB_IP_ADDRESS = "db_ip_address";
	
	public static final String DB_PORT = "db_port";
	
	public static final String DB_NAME = "db_name";
	
	public static final String DB_USERNAME = "db_username";
	
	public static final String DB_PASSWORD = "db_password";
	
	public static final String GXA_SERVER_ADDRESS = "gxa_server_address";
	
	public static final String GXA_PORT = "gxa_port";
	
	public static final String GXA_API_KEY = "gxa_api_key";
	
	public static final String GXA_DATE_FORMAT = "gxa_date_format";
	
	public static final String GXA_SSL_ENCRYPTION = "gxa_ssl_encryption";
	
	public static final String OPENMRS_REST_ADDRESS = "openmrs_rest_address";
	
	public static final String OPENMRS_USER = "openmrs_user";
	
	public static final String OPENMRS_PASSWORD = "openmrs_password";
	
	public static final String OPENMRS_DATE_FORMAT = "openmrs_date_format";
	
	public static final String OPENMRS_ENCOUNTER_TYPE = "openmrs_encounter_type";
	
	public static final String OPENMRS_CONCEPT_MAP = "openmrs_concept_map";
	
	public static final String OPENMRS_SSL_ENCRYPTION = "openmrs_ssl_encryption";
	
	public static final String WEB_DATA_FORMAT = "web_data_format";
	
	public static final String WEB_AUTHENTICATION = "web_authentication";
	
	public static final String WEB_DATE_FORMAT = "web_date_format";
	
	public static final String WEB_ENCODING = "web_encoding";
	
	public static final String WEB_APP_STRING = "server_app_string";
	
	public static final String WEB_USERNAME = "xpert_user";
	
	public static final String WEB_PASSWORD = "xpert_password";
	
	public static final String WEB_SSL_ENCRYPTION = "default_send_method";

	/**
	 * Read properties from properties file
	 */
	public static void readProperties() {
		props = new Properties();
		if (new File(FileConstants.FILE_PATH).exists()) {
			try {
				props.load(new FileInputStream(FileConstants.FILE_PATH));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			props.setProperty(VERSION, "2.0.0-beta");
			props.setProperty(BUILD, "1");
			props.setProperty(CSV_EXPORT, "NO");
			props.setProperty(SMS_EXPORT, "NO");
			props.setProperty(WEB_EXPORT, "YES");
			props.setProperty(GXA_EXPORT, "NO");
			props.setProperty(OPENMRS_EXPORT, "NO");
			props.setProperty(WEB_EXPORT, "YES");
			props.setProperty(MTB_CODE, "MTB_CODE");
			props.setProperty(RIF_CODE, "RIF_CODE");
			props.setProperty(QC_CODE, "QC");
			props.setProperty(LOCAL_PORT, "12221");
			props.setProperty(SMS_PROJECT_NAME, "XpertSMS");
			props.setProperty(SMS_PORT, "");
			props.setProperty(SMS_ADMIN_PHONE, "");
			props.setProperty(SMS_DATE_FORMAT, "");
			props.setProperty(SMS_VARIABLES, "");
			props.setProperty(CSV_FOLDER_PATH, "");
			props.setProperty(CSV_FIELD_SEPARATOR, ",");
			props.setProperty(CSV_USE_QUOTES, "NO");
			props.setProperty(CSV_DATE_FORMAT, "yyyy-mm-dd");
			props.setProperty(CSV_VARIABLES, "");
			props.setProperty(DB_NAME, "xpertsms");
			props.setProperty(DB_USERNAME, "root");
			props.setProperty(DB_PASSWORD, "");
			props.setProperty(DB_IP_ADDRESS, "127.0.0.1");
			props.setProperty(DB_PORT, "3306");
			props.setProperty(GXA_SERVER_ADDRESS, "dev.gxalert.com/api/result");
			props.setProperty(GXA_PORT, "8080");
			props.setProperty(GXA_API_KEY, "abcdefghijklmnopqrstuvwxyz-0123456789-ABCDEFGHIJKLMNOPQRSTUVWXYZ");
			props.setProperty(GXA_DATE_FORMAT, "yyyy-MM-dd'T'HH:mm:ss'Z'");
			props.setProperty(GXA_SSL_ENCRYPTION, "NO");
			props.setProperty(OPENMRS_REST_ADDRESS, "demo.openmrs.org/openmrs/ws/rest/v1/");
			props.setProperty(OPENMRS_USER, "admin");
			props.setProperty(OPENMRS_PASSWORD, "");
			props.setProperty(OPENMRS_DATE_FORMAT, "yyyy-MM-dd");
			props.setProperty(OPENMRS_ENCOUNTER_TYPE, "GeneXpert_Results");
			props.setProperty(OPENMRS_CONCEPT_MAP, "");
			props.setProperty(OPENMRS_SSL_ENCRYPTION, "NO");
			props.setProperty(WEB_APP_STRING, "127.0.0.1:8080/xpertsmsweb/xpertsmsweb.jsp"); //127.0.0.1:8080
			props.setProperty(WEB_SSL_ENCRYPTION, "NO");
			props.setProperty(WEB_DATA_FORMAT, "Plain text");
			props.setProperty(WEB_AUTHENTICATION, "As header");
			props.setProperty(WEB_DATE_FORMAT, "yyyy-MM-dd");
			props.setProperty(WEB_ENCODING, "");
			props.setProperty(WEB_USERNAME, "admin");
			props.setProperty(WEB_PASSWORD, "");
		}
	}
	
	/**
	 * Write properties to properties file and reads back
	 */
	public static boolean writeProperties(Map<String, String> properties) {
		boolean success = false;
		if (properties.isEmpty()) {
			System.out.println("No properties to write to file.");
		}
		Set<Entry<String, String>> entrySet = properties.entrySet();
		for (Iterator<Entry<String, String>> iter = entrySet.iterator(); iter.hasNext();) {
			Entry<String, String> pair = iter.next();
			props.setProperty(pair.getKey(), pair.getValue());
		}
		try {
			if (!(new File(FileConstants.XPERT_SMS_DIR).exists())) {
				boolean checkDir = new File(FileConstants.XPERT_SMS_DIR).mkdir();
				if (!checkDir) {
					JOptionPane.showMessageDialog(null,
					    "Could not create properties file. Please check the permissions of your home folder.", "Error!",
					    JOptionPane.ERROR_MESSAGE);
				}
			}
			props.store(new FileOutputStream(FileConstants.FILE_PATH), null);
			props.load(new FileInputStream(FileConstants.FILE_PATH));
			JOptionPane.showMessageDialog(null, "Settings updated!", "Success!", JOptionPane.INFORMATION_MESSAGE);
			success = true;
		}
		catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			JOptionPane.showMessageDialog(null,
			    "Could not create properties file. Please check the permissions of your home folder.", "Error!",
			    JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not create properties file.", "Error!", JOptionPane.ERROR_MESSAGE);
		}
		return success;
	}
	
	public static String getProperty(String key) {
		String value = props.getProperty(key);
		if (value == null)
			value = "";
		return value;
	}
}
