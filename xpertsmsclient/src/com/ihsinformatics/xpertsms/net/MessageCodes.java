/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

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
