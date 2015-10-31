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

package com.ihsinformatics.xpertsms.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
import com.ihsinformatics.xpertsms.net.exception.HttpResponseException;

/**
 * This class provides OpenMRS authentication/get/post via RESTWS
 * 
 * @author Victor Aravena victor.aravena@ciochile.cl
 */

public class OpenMrsApiAuthRest {
	
	String username = null;
	
	String password = null;
	
	String urlBase = null;
	
	public OpenMrsApiAuthRest(String username, String password, String urlBase) {
		this.urlBase = urlBase;
		this.username = username;
		this.password = password;
	}
	
	public String post(String entity, String jsonObj) {
		String response = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost(urlBase + entity);
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
			BasicScheme scheme = new BasicScheme();
			Header authorizationHeader = scheme.authenticate(credentials, httpPost);
			httpPost.setHeader(authorizationHeader);
			System.out.println("Executing request: " + httpPost.getRequestLine());
			StringEntity input = new StringEntity(jsonObj);
			input.setContentType("application/json");
			httpPost.setEntity(input);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 204 && responseCode != 201)
				throw new HttpResponseException(responseCode);
			response = "SUCCESS";
		}
		catch (AuthenticationException e) {
			e.printStackTrace();
			response = "ERROR:" + e.getMessage();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			response = "ERROR:" + e.getMessage();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
			response = "ERROR:" + e.getMessage();
		}
		catch (IOException e) {
			e.printStackTrace();
			response = "ERROR:" + e.getMessage();
		}
		catch (HttpResponseException e) {
			e.printStackTrace();
		}
		finally {
			httpClient.getConnectionManager().shutdown();
		}
		return response;
	}
	
	/**
	 * HTTP GET
	 * 
	 * @param query
	 * @return
	 */
	public String get(String query) {
		String response = "";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			String URL = urlBase + query;
			HttpGet httpGet = new HttpGet(URL);
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
			BasicScheme scheme = new BasicScheme();
			Header authorizationHeader = scheme.authenticate(credentials, httpGet);
			httpGet.setHeader(authorizationHeader);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httpGet, responseHandler);
		}
		catch (AuthenticationException e) {
			e.printStackTrace();
			response = "ERROR:" + e.getMessage();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
			response = "ERROR:" + e.getMessage();
		}
		catch (IOException e) {
			e.printStackTrace();
			response = "ERROR:" + e.getMessage();
		}
		finally {
			httpClient.getConnectionManager().shutdown();
		}
		return response;
	}
	
	/**
	 * HTTP POST GXP Results
	 * 
	 * @param URLPath
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public String postResults(XpertResultUploadMessage message) {
		String URLPath = "/ws/rest/v1/";
		StringEntity input = null; // TODO: create input object from message
		String URL = urlBase + URLPath;
		String response = "";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost(URL);
			System.out.println(URL);
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
			BasicScheme scheme = new BasicScheme();
			Header authorizationHeader = scheme.authenticate(credentials, httpPost);
			httpPost.setHeader(authorizationHeader);
			HttpResponse responseRequest = httpclient.execute(httpPost);
			
			if (responseRequest.getStatusLine().getStatusCode() != 204
			        && responseRequest.getStatusLine().getStatusCode() != 201) {
				response = "Failed : HTTP error code : " + responseRequest.getStatusLine().getStatusCode();
			} else {
				response = "SUCCESS";
			}
			httpclient.getConnectionManager().shutdown();
		}
		catch (AuthenticationException e) {
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			httpclient.getConnectionManager().shutdown();
		}
		return response;
	}
}
