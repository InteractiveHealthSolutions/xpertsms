/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OpenMrsConfiguration.java
 *
 * Created on Oct 23, 2014, 3:09:36 PM
 */

package com.ihsinformatics.xpertsms.ui;

/**
 *
 * @author owais.hussain@ihsinformatics.com
 */
public class OpenMrsConfiguration extends javax.swing.JPanel
{
	private static final long			serialVersionUID	= -8611826437747724820L;
	private javax.swing.JTextField		addressTextField;
	private javax.swing.JPanel			bottomDialogPanel;
	private javax.swing.JComboBox		dateFormatComboBox;
	private javax.swing.JLabel			dateTimeFormatLabel;
	private javax.swing.JLabel			encounterTypeLabel;
	private javax.swing.JTextField		encounterTypeTextField;
	private javax.swing.JLabel			infoLabel;
	private javax.swing.JScrollPane		jScrollPane1;
	private javax.swing.JTextArea		jTextArea1;
	private javax.swing.JLabel			mappingLabel;
	private javax.swing.JLabel			passwordLabel;
	private javax.swing.JPasswordField	passwordPasswordField;
	private javax.swing.JButton			saveButton;
	private javax.swing.JLabel			serverAddressLabel;
	private javax.swing.JPanel			topDialogPanel;
	private javax.swing.JButton			tryButton;
	private javax.swing.JLabel			usernameLabel;
	private javax.swing.JTextField		usernameTextField;

	/** Creates new form OpenMrsConfiguration */
	public OpenMrsConfiguration ()
	{
		initComponents ();
	}

	private void initComponents ()
	{

		topDialogPanel = new javax.swing.JPanel ();
		serverAddressLabel = new javax.swing.JLabel ();
		addressTextField = new javax.swing.JTextField ();
		usernameLabel = new javax.swing.JLabel ();
		usernameTextField = new javax.swing.JTextField ();
		passwordLabel = new javax.swing.JLabel ();
		passwordPasswordField = new javax.swing.JPasswordField ();
		dateTimeFormatLabel = new javax.swing.JLabel ();
		dateFormatComboBox = new javax.swing.JComboBox ();
		encounterTypeLabel = new javax.swing.JLabel ();
		encounterTypeTextField = new javax.swing.JTextField ();
		mappingLabel = new javax.swing.JLabel ();
		jScrollPane1 = new javax.swing.JScrollPane ();
		jTextArea1 = new javax.swing.JTextArea ();
		infoLabel = new javax.swing.JLabel ();
		bottomDialogPanel = new javax.swing.JPanel ();
		tryButton = new javax.swing.JButton ();
		saveButton = new javax.swing.JButton ();

		serverAddressLabel.setText ("OpenMRS REST-WS Address:");

		addressTextField.setText ("http://demo.openmrs.org/openmrs/openmrs/ws/rest/v1/");
		addressTextField.setToolTipText ("Must be a valid IP address, like \"127.0.0.1\" or a database URI, like \"localhost\".");
		addressTextField.setCursor (new java.awt.Cursor (java.awt.Cursor.TEXT_CURSOR));
		addressTextField.setName ("smsTarseelAddress"); // NOI18N

		usernameLabel.setText ("OpenMRS Username:");

		usernameTextField.setText ("admin");
		usernameTextField.setToolTipText ("This will be your database user name.");

		passwordLabel.setText ("Password:");

		passwordPasswordField.setText ("Admin123");
		passwordPasswordField.setToolTipText ("Enter your databse password for given username");

		dateTimeFormatLabel.setText ("Date/Time format:");

		dateFormatComboBox.setEditable (true);
		dateFormatComboBox.setModel (new javax.swing.DefaultComboBoxModel (new String[] {"yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd", "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy",
				"M/d/yy", "dd/MM/yyyy hh:mm:ssa", "dd/MM/yyyy", "d/M/yy", "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss"}));
		dateFormatComboBox
				.setToolTipText ("Choose a format for date and time fields.\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\n[M/d/yy] is short USA format date, e.g. 10/23/14\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\n[d/M/yy] is short UK format date, e.g. 23/10/14\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\n\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");
		encounterTypeLabel.setText ("Encounter Type:");

		encounterTypeTextField.setText ("GeneXpert Results");
		encounterTypeTextField.setToolTipText ("This will be your database user name.");

		mappingLabel.setText ("Concept Mapping: Please map all the variables to respective OpenMRS concepts");

		jTextArea1.setColumns (20);
		jTextArea1.setRows (5);
		jTextArea1
				.setText ("sampleId=X_SAMPLE_ID\ntestStartedOn=X_START_DATE\ntestEndedOn=X_END_DATE\nmessageSentOn=X_MESSAGE_DATE\nreagentLotId=X_LOT_ID\ncartridgeExpirationDate=X_EXP_DATE\ncartridgeSerial=X_CARTRIDGE_ID\nmoduleSerial=X_MODULE_ID\ninstrumentSerial=X_INSTRUMENT_ID\nmtbResultText=X_MTB\nrifResultText=X_RIF\nhostId=X_HOST_ID\ncomputerName=X_COMPUTER_NAME\nnotes=X_NOTES\nerrorCode=X_ERROR_CODE\nerrorNotes=X_ERROR_NOTES\nprobeA=X_PROBE_A\nprobeB=X_PROBE_B\nprobeC=X_PROBE_C\nprobeD=X_PROBE_D\nprobeE=X_PROBE_E\nprobeSpc=X_PROBE_SPC\nqc1=X_QC1\nqc2=X_QC2\nprobeACt=X_PROBE_ACT\nprobeBCt=X_PROBE_BCT\nprobeCCt=X_PROBE_CCT\nprobeDCt=X_PROBE_DCT\nprobeECt=X_PROBE_ECT\nprobeSpcCt=X_PROBE_SPC_CT\nqc1Ct=X_QC1_CT\nqc2Ct=X_QC2_CT\nprobeAEndpt=X_PROBE_A_END\nprobeBEndpt=X_PROBE_B_END\nprobeCEndpt=X_PROBE_C_END\nprobeDEndpt=X_PROBE_D_END\nprobeEEndpt=X_PROBE_E_END\nprobeSpcEndpt=X_PROBE_SPC_END\nqc1Endpt=X_QC1_END\nqc2Endpt=X_QC2_END");
		jTextArea1.setToolTipText ("");
		jScrollPane1.setViewportView (jTextArea1);

		infoLabel.setForeground (new java.awt.Color (230, 139, 44));
		infoLabel
				.setText ("<html><b>IMPORTANT:<b> In order to use this service:<br>\n1. User must have privileges to create concepts.<br>\n2. Compatible REST-WS module must be installed in OpenMRS.<br>\n3. Patient ID passed from GeneXpert should exist in OpenMRS.<br>\n4. Encounter Type should be created with same name as specified in OpenMRS.<br>\nThe app takes some time to create concepts on Save, depending on the internet speed</html>");

		tryButton.setText ("Try..");
		tryButton.setToolTipText ("Check if the application is able to connect to the database using given credentials.");

		saveButton.setFont (new java.awt.Font ("Tahoma", 2, 12));
		saveButton.setText ("Save");
		saveButton.setToolTipText ("Creates database tables and save settings to configuration file.");

		javax.swing.GroupLayout bottomDialogPanelLayout = new javax.swing.GroupLayout (bottomDialogPanel);
		bottomDialogPanel.setLayout (bottomDialogPanelLayout);
		bottomDialogPanelLayout.setHorizontalGroup (bottomDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING).addGroup (
				bottomDialogPanelLayout.createSequentialGroup ().addContainerGap ().addComponent (tryButton).addGap (10, 10, 10).addComponent (saveButton).addContainerGap (374, Short.MAX_VALUE)));
		bottomDialogPanelLayout.setVerticalGroup (bottomDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.BASELINE)
				.addComponent (saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE).addComponent (tryButton, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE));

		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout (topDialogPanel);
		topDialogPanel.setLayout (topDialogPanelLayout);
		topDialogPanelLayout.setHorizontalGroup (topDialogPanelLayout
				.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup (
						topDialogPanelLayout
								.createSequentialGroup ()
								.addContainerGap ()
								.addGroup (
										topDialogPanelLayout
												.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup (
														topDialogPanelLayout
																.createSequentialGroup ()
																.addGroup (
																		topDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING).addComponent (serverAddressLabel)
																				.addComponent (usernameLabel).addComponent (encounterTypeLabel).addComponent (dateTimeFormatLabel))
																.addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup (
																		topDialogPanelLayout
																				.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup (
																						topDialogPanelLayout
																								.createParallelGroup (javax.swing.GroupLayout.Alignment.TRAILING, false)
																								.addComponent (addressTextField, javax.swing.GroupLayout.Alignment.LEADING)
																								.addGroup (
																										javax.swing.GroupLayout.Alignment.LEADING,
																										topDialogPanelLayout
																												.createSequentialGroup ()
																												.addComponent (usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64,
																														javax.swing.GroupLayout.PREFERRED_SIZE)
																												.addGap (18, 18, 18)
																												.addComponent (passwordLabel)
																												.addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																												.addComponent (passwordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 139,
																														javax.swing.GroupLayout.PREFERRED_SIZE)))
																				.addGroup (
																						topDialogPanelLayout
																								.createParallelGroup (javax.swing.GroupLayout.Alignment.TRAILING, false)
																								.addComponent (encounterTypeTextField, javax.swing.GroupLayout.Alignment.LEADING)
																								.addComponent (dateFormatComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0,
																										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))).addComponent (mappingLabel)
												.addComponent (jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
												.addComponent (infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap ())
				.addComponent (bottomDialogPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		topDialogPanelLayout.setVerticalGroup (topDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING).addGroup (
				topDialogPanelLayout
						.createSequentialGroup ()
						.addContainerGap ()
						.addGroup (
								topDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.BASELINE).addComponent (serverAddressLabel)
										.addComponent (addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup (
								topDialogPanelLayout
										.createParallelGroup (javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addGroup (
												topDialogPanelLayout
														.createParallelGroup (javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent (usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE).addComponent (usernameLabel))
										.addGroup (
												topDialogPanelLayout
														.createParallelGroup (javax.swing.GroupLayout.Alignment.BASELINE, false)
														.addComponent (passwordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE).addComponent (passwordLabel)))
						.addGap (8, 8, 8)
						.addGroup (
								topDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent (dateFormatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent (dateTimeFormatLabel))
						.addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup (
								topDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent (encounterTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent (encounterTypeLabel)).addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent (mappingLabel)
						.addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent (jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent (infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent (bottomDialogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap ()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout (this);
		this.setLayout (layout);
		layout.setHorizontalGroup (layout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING).addComponent (topDialogPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup (layout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING).addComponent (topDialogPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 402,
				javax.swing.GroupLayout.PREFERRED_SIZE));
	}
}
