/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package com.ihsinformatics.xpertsms;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import com.ihsinformatics.xpertsms.ui.XpertConfiguration;

/**
 *
 */
public class XpertMain {
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (Exception ex) {
			Logger.getLogger(XpertConfiguration.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		/* Create and display the form */
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				new XpertConfiguration().setVisible(true);
			}
		});
	}
	
}
