
package com.ihsinformatics.xpertsms.net;

import com.ihsinformatics.xpertsms.ui.ControlPanel;

/**
 * 
 * @author ali.habib@irdresearch.org
 *
 */
public class MessageCodes
{

	public static final String	PROPERTIES_NOT_SET			= "Please click on ControlPanel to configure the system before you start!";
	public static final String	PORT_INACCESSIBLE			= "Could not listen on port " + ControlPanel.props.getProperty ("serverport");
	public static final String	ERROR_LOADING_PROPERTIES	= "Error while loading system configuration. Please recheck your ControlPanel";
	public static final String	GENERAL_ERROR				= "Error occurred";

	public static final String	SERVER_STOPPED				= "The result server has been shut down!";

}
