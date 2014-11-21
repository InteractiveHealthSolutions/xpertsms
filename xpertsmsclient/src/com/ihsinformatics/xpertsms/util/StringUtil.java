/**
 * This class contains several methods for String manupulation that are not available in built in libraries
 */

package com.ihsinformatics.xpertsms.util;

import java.util.ArrayList;

/**
 * @author owais.hussain@irdresearch.org
 *
 */
public class StringUtil
{
	public static ArrayList<String> toArrayList (String[] array)
	{
		ArrayList<String> arrayList = new ArrayList<String> (array.length);
		for (String s : array)
			arrayList.add (s);
		return arrayList;
	}

}
