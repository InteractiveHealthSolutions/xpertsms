/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.net.exception;

/**
 * @author owais.hussain@ihsinformatics.com
 */
public class HttpResponseException extends Exception {
	
	private static final long serialVersionUID = -598856147844011662L;
	
	public HttpResponseException(int responseCode) {
		super(getMessage(responseCode));
	}
	
	public static String getMessage(int responseCode) {
		String error = "";
		switch (responseCode) {
			case 400:
				error = "Bad request! Please contact developer.";
				break;
			case 401:
				error = "Unauthorized! Please recheck API Key.";
				break;
			case 403:
				error = "Forbidden! Does your Firewall/ISP allow this address? Check with network administrator.";
				break;
			case 404:
				error = "Not found! Please check the address. Here's a tip, try it on a browser first.";
				break;
			case 408:
				error = "Timed out! Please contact network administrator.";
				break;
			case 500:
				error = "Internal Server error! Please contact network administrator.";
				break;
			case 502:
				error = "Bad gateway! Please contact network administrator.";
				break;
			case 504:
				error = "Gateway timed out! Please contact network administrator.";
				break;
		}
		return error;
	}
}
