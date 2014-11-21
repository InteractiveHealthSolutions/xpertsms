/**
 * Throws 
 */

package com.ihsinformatics.xpertsms.net.exception;

/**
 * @author owais.hussain@irdresearch.org
 *
 */
public class HttpResponseException extends Exception
{
	private static final long	serialVersionUID	= -598856147844011662L;

	public HttpResponseException (int responseCode)
	{
		super (getMessage (responseCode));
	}

	public static String getMessage (int responseCode)
	{
		String error = "";
		switch (responseCode)
		{
			case 400 :
				error = "Bad request! Please contact developer.";
				break;
			case 401 :
				error = "Unauthorized! Please recheck API Key.";
				break;
			case 403 :
				error = "Forbidden! Does your Firewall/ISP allow this address? Check with network administrator.";
				break;
			case 404 :
				error = "Not found! Please check the address. Here's a tip, try it on a browser first.";
				break;
			case 408 :
				error = "Timed out! Please contact network administrator.";
				break;
			case 500 :
				error = "Internal Server error! Please contact network administrator.";
				break;
			case 502 :
				error = "Bad gateway! Please contact network administrator.";
				break;
			case 504 :
				error = "Gateway timed out! Please contact network administrator.";
				break;
		}
		return error;
	}
}
