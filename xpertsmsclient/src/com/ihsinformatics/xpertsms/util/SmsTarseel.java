/**
 * 
 */
package com.ihsinformatics.xpertsms.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;

import net.jmatrix.eproperties.EProperties;

import org.irdresearch.smstarseel.context.TarseelContext;

import com.ihsinformatics.xpertsms.constant.FileConstants;

/**
 * @author Owais
 */
public class SmsTarseel {
	
	public static boolean instantiate() {
		try {
			String propFilePath = FileConstants.XPERT_SMS_DIR + System.getProperty("file.separator") + "smstarseel.properties";;
			System.out.println("SMSTARSEEL: Loading properties...");
			File f = new File(propFilePath);
			// Java Properties donot seem to support substitutions hence
			// EProperties
			// are used to accomplish the task
			EProperties root = new EProperties();
			root.load(f);
			// Java Properties to send to context and other APIs for
			// configuration
			Properties prop = new Properties();
			prop.putAll(convertEntrySetToMap(root.entrySet()));
			TarseelContext.instantiate(prop, null);
			return true;
		}
		catch (InstanceAlreadyExistsException e) {
			System.out.println("An instance of the same service already exists.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Map<Object, Object> convertEntrySetToMap(Set<Entry<Object, Object>> entrySet) {
		Map<Object, Object> mapFromSet = new HashMap<Object, Object>();
		for (Entry<Object, Object> entry : entrySet) {
			mapFromSet.put(entry.getKey(), entry.getValue());
		}
		return mapFromSet;
	}
}
