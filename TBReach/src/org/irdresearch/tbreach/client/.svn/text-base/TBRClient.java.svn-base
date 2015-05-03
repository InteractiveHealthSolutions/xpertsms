/**
 * Provides various Client-side methods used in the Application
 */

package org.irdresearch.tbreach.client;

import java.util.Date;

import org.irdresearch.tbreach.shared.TBR;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class TBRClient
{
	public static boolean isLoggedIn(String userName)
	{
		String loginTimeCookie = Cookies.getCookie("LoginTime");
		String userLogged = Cookies.getCookie("UserName");
		if (!userLogged.equals(userName))
			return false;
		if (loginTimeCookie.length() < 8)
			return false;
		long loginTime = Long.parseLong(loginTimeCookie);
		// Check if the session has expired
		if (new Date().getTime() > new Date(loginTime + TBR.sessionLimit).getTime())
			return false;
		return false;
	}

	/**
	 * Creates a 'long' code for a given string using some mathematical mechanism
	 * 
	 * @param string
	 * @return
	 */
	public static long getSimpleCode(String string)
	{
		long code = 1;
		for (int i = 0; i < string.length(); i++)
			code *= string.charAt(i);
		return code;
	}

	/**
	 * Verifies whether client has entered a valid pass code (required for some sensitive operations)
	 * 
	 * @return
	 */
	public static boolean verifyClientPasscode(String passcode)
	{
		try
		{
			String storedPasscode = Cookies.getCookie("Pass");
			long passedCode = getSimpleCode(passcode.substring(0, 3));
			long existing = Long.parseLong(storedPasscode);
			return (passedCode == existing);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Get usually desired value from a widget
	 * 
	 * 1. Text fields return their respective text
	 * 
	 * 2. List boxes return selected value
	 * 
	 * @param control
	 * @return
	 */
	public static String get(Widget control)
	{
		if (control instanceof TextBoxBase)
			return ((TextBoxBase) control).getText();
		if (control instanceof ListBox)
			return ((ListBox) control).getValue(((ListBox) control).getSelectedIndex());
		if (control instanceof ValueBoxBase<?>)
			return ((ValueBoxBase<?>) control).getText();
		return "";
	}

	/**
	 * Get index of a given value from a widget (probably ListBox)
	 * 
	 * @param control
	 * 
	 * @param value
	 * @return
	 */
	public static int getIndex(Widget control, String value)
	{
		if (control instanceof ListBox)
		{
			ListBox listBox = (ListBox) control;
			for (int i = 0; i < listBox.getItemCount(); i++)
				if (listBox.getValue(i).equalsIgnoreCase(value))
					return i;
		}
		return -1;
	}
}
