/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class posts GeneXpert results to GXAlert server via HTTP request 
 */

package com.ihsinformatics.xpertsms.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.json.JSONObject;
import com.ihsinformatics.xpertsms.constant.MtbResults;
import com.ihsinformatics.xpertsms.constant.RifResults;
import com.ihsinformatics.xpertsms.model.XpertProperties;
import com.ihsinformatics.xpertsms.model.XpertResultUploadMessage;
import com.ihsinformatics.xpertsms.ui.ControlPanel;

/**
 * @author owais.hussain@irdresearch.org
 *
 */
public class GxAlertSender
{
	public GxAlertSender ()
	{
	}

	public String postToGxAlert (XpertResultUploadMessage message)
	{
		HttpURLConnection httpConnection = null;
		OutputStream os = null;
		// String url = null;
		int responseCode = 0;
		String response = null;
		String url = "http://" + ControlPanel.props.getProperty (XpertProperties.GXA_SERVER_ADDRESS);
		JSONObject messageObj = message.toJson ();
		// Put API Key if missing
		if (!messageObj.has ("apiKey"))
		{
			String apiKey = ControlPanel.props.getProperty (XpertProperties.GXA_API_KEY);
			messageObj.put ("apiKey", apiKey);
		}
		// Change result text to codes
		int mtbId = 1;
		int rifId = 1;
		if (messageObj.has ("resultMtb"))
		{
			String mtb = messageObj.remove ("resultMtb").toString ();
			if (mtb.equalsIgnoreCase (MtbResults.INVALID))
				mtbId = rifId = 2;
			else if (mtb.equalsIgnoreCase (MtbResults.ERROR))
				mtbId = rifId = 3;
			else if (mtb.equalsIgnoreCase (MtbResults.NOT_DETECTED))
				mtbId = rifId = 4;
			else
				mtbId = 5;
			messageObj.put ("resultIdMtb", mtbId);
			if (messageObj.has ("resultRif"))
			{
				String rif = messageObj.remove ("resultRif").toString ();
				if (rif.equalsIgnoreCase (RifResults.DETECTED))
					rifId = 5;
				else if (rif.equalsIgnoreCase (RifResults.INDETERMINATE))
					rifId = 6;
				messageObj.put ("resultIdRif", rifId);
			}
		}
		// Change all date formats to iso8601
		SimpleDateFormat iso8601 = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddhhmmss");
		String[] dateKeys = {"testStartedOn", "testEndedOn", "messageSentOn", "cartridgeExpirationDate"};
		for (String key : dateKeys)
		{
			if (messageObj.has (key))
			{
				String dateStr = messageObj.remove (key).toString ();
				Date date = null;
				try
				{
					date = formatter.parse (dateStr);
				}
				catch (Exception e)
				{
					try
					{
						date = new SimpleDateFormat ("yyyyMMdd").parse (dateStr);
					}
					catch (ParseException e1)
					{
						e1.printStackTrace ();
					}
				}
				messageObj.put (key, iso8601.format (date));
			}
		}
		System.out.println ("POSTING: " + messageObj.toString ());
		byte[] content = messageObj.toString ().getBytes ();
		try
		{
			URL gxaUrl = new URL (url);
			httpConnection = (HttpURLConnection) gxaUrl.openConnection ();
			httpConnection.setRequestProperty ("Content-Length", String.valueOf (content.length));
			httpConnection.setRequestProperty ("Content-Type", "application/json; charset=UTF-8");
			httpConnection.setRequestProperty ("Host", "dev.gxalert.com");
			httpConnection.setRequestMethod ("POST");
			httpConnection.setDoOutput (true);
			os = httpConnection.getOutputStream ();
			os.write (content);
			os.flush ();
			responseCode = httpConnection.getResponseCode ();
			if (responseCode != HttpURLConnection.HTTP_OK)
			{
				System.out.println ("Response Code " + responseCode + " for Sample ID " + message.getSampleId () + ": Could not submit");
			}
			BufferedReader br = new BufferedReader (new InputStreamReader (httpConnection.getInputStream ()));
			String line = "";
			response = "";
			while ((line = br.readLine ()) != null)
			{
				response += line;
			}
			try
			{
				os.close ();
				httpConnection.disconnect ();
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
		System.out.println (response);
		return response;
	}
}
