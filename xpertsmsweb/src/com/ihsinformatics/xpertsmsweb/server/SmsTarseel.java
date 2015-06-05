package com.ihsinformatics.xpertsmsweb.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;

import net.jmatrix.eproperties.EProperties;

import org.irdresearch.smstarseel.context.TarseelContext;

import com.ihsinformatics.xpertsmsweb.shared.SmsTarseelUtil;

public class SmsTarseel {

	@SuppressWarnings("unchecked")
	public static boolean Instantiate() throws IOException,
			InstanceAlreadyExistsException {

		System.out.println(">>>>LOADING SYSTEM PROPERTIES...");
		InputStream f = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("xpertsmsweb.properties");
		// Java Properties donot seem to support substitutions hence EProperties
		// are used to accomplish the task

		EProperties root = new EProperties();
		root.load(f);

		// Java Properties to send to context and other APIs for configuration
		Properties prop = new Properties();
		prop.putAll(SmsTarseelUtil.convertEntrySetToMap(root.entrySet()));

		TarseelContext.instantiate(prop, "smstarseel.cfg.xml");
		return true;
	}
}
