/**
 * 
 */

package com.ihsinformatics.xpertsms.net;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import com.ihsinformatics.xpertsms.model.XpertResultUploadMessage;

/**
 * 
 * @author Victor Aravena victor.aravena@ciochile.cl
 *
 */

public class OpenMrsApiAuthRest
{
	String	username	= null;
	String	password	= null;
	String	UrlBase		= null;
	
	public OpenMrsApiAuthRest (XpertResultUploadMessage message)
	{
		// TODO: Extract URI and credentials from restUri
		this.UrlBase = "";
		this.username = "";
		this.password = "";
	}

	/**
	 * HTTP POST
	 * 
	 * @param URLPath
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public String postRequest (XpertResultUploadMessage message)
	{
		String URLPath = "/ws/rest/v1/";
		StringEntity input = null; // TODO: create input object from message
		String URL = UrlBase + URLPath;
		String response = "";
		DefaultHttpClient httpclient = new DefaultHttpClient ();
		try
		{
			HttpPost httpPost = new HttpPost (URL);
			System.out.println (URL);
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials (username, password);
			BasicScheme scheme = new BasicScheme ();
			Header authorizationHeader = scheme.authenticate (credentials, httpPost);
			httpPost.setHeader (authorizationHeader);
			HttpResponse responseRequest = httpclient.execute (httpPost);

			if (responseRequest.getStatusLine ().getStatusCode () != 204 && responseRequest.getStatusLine ().getStatusCode () != 201)
			{
				response = "Failed : HTTP error code : " + responseRequest.getStatusLine ().getStatusCode ();
			}
			else
			{
				response = "Success!";
			}
			httpclient.getConnectionManager ().shutdown ();
		}
		catch (AuthenticationException e)
		{
			e.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			httpclient.getConnectionManager ().shutdown ();
		}
		return response;
	}

	/**
	 * HTTP GET
	 * 
	 * @param URLPath
	 * @return
	 * @throws Exception
	 */
	public String getRequest (String URLPath)
	{
		String URL = UrlBase + URLPath;
		String response = "";
		DefaultHttpClient httpclient = new DefaultHttpClient ();
		try
		{
			HttpGet httpGet = new HttpGet (URL);

			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials (username, password);
			BasicScheme scheme = new BasicScheme ();
			Header authorizationHeader = scheme.authenticate (credentials, httpGet);
			httpGet.setHeader (authorizationHeader);
			ResponseHandler<String> responseHandler = new BasicResponseHandler ();

			// System.out.println("Executing request: " +
			// httpGet.getRequestLine());
			// System.out.println(response);
			response = httpclient.execute (httpGet, responseHandler);

		}
		catch (AuthenticationException e)
		{
			e.printStackTrace();
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			httpclient.getConnectionManager ().shutdown ();
		}
		return response;
	}
}