/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import com.ihsinformatics.xpertsms.model.XpertProperties;
import com.ihsinformatics.xpertsms.util.RegexUtil;
import com.ihsinformatics.xpertsms.util.SwingUtil;
import javax.swing.AbstractListModel;

/**
 * @author owais.hussain@irdresearch.org
 */
public class SmsDialog extends JDialog implements ActionListener
{
	private static final long	serialVersionUID	= -6629569177787110764L;
	private final JPanel		contentPanel		= new JPanel ();
	private JPanel				topDialogPanel;
	private JList				conceptsList;
	private JScrollPane			verticalScrollPane;

	private JLabel				smsServerAddressLabel;
	private JLabel				adminPhoneLabel;
	private JLabel				dateTimeFormatLable;
	private JLabel				variablesListLabel;

	private JTextField			smsServerAddressTextField;
	private JTextField			adminPhoneTextField;
	private JCheckBox			useSslCheckBox;

	private JComboBox			dateTimeFormatComboBox;

	private JButton				recommendedButton;
	private JButton				saveButton;
	private JButton				tryButton;

	public SmsDialog ()
	{
		initComponents ();
		initEvents ();
		initValues ();
	}

	/**
	 * Initialize form components and layout
	 */
	public void initComponents ()
	{
		topDialogPanel = new javax.swing.JPanel ();
		smsServerAddressLabel = new JLabel ();
		smsServerAddressTextField = new JTextField ();
		adminPhoneLabel = new JLabel ();
		adminPhoneTextField = new JTextField ();
		dateTimeFormatLable = new JLabel ();
		dateTimeFormatComboBox = new JComboBox ();
		useSslCheckBox = new JCheckBox ();
		variablesListLabel = new JLabel ();
		verticalScrollPane = new JScrollPane ();
		conceptsList = new JList ();
		recommendedButton = new JButton ("Recommended");
		tryButton = new JButton ();
		saveButton = new JButton ();

		setName ("openMrsDialog");
		setDefaultCloseOperation (JDialog.DISPOSE_ON_CLOSE);
		setResizable (false);
		setMinimumSize (new Dimension(400, 300));
		setTitle ("OpenMRS Configuration");
		setPreferredSize (new Dimension(400, 300));
		setBounds (100, 100, 415, 360);
		getContentPane ().setLayout (new BorderLayout ());

		getContentPane ().add (topDialogPanel, BorderLayout.CENTER);
		tryButton.setText ("Try..");
		tryButton.setToolTipText ("Check if the application is able to connect to the database using given credentials.");
		saveButton.setFont (new java.awt.Font ("Tahoma", 2, 12)); // NOI18N
		saveButton.setText ("Save");
		saveButton.setToolTipText ("Creates database tables and save settings to configuration file.");
		smsServerAddressLabel.setText ("SMS Server Address:");
		smsServerAddressTextField.setToolTipText ("Must be a valid IP address, like \"127.0.0.1\" or a URL, like \"127.0.0.1/smstarseelweb/smstarseel\". Please do not write http(s)://");
		smsServerAddressTextField.setText ("localhost:8080/smstarseelweb/smstarseel");
		smsServerAddressTextField.setName ("smsTarseelAddress");
		adminPhoneLabel.setText ("Admin's Phone No.:");
		adminPhoneTextField.setToolTipText ("Here goes fully qualified mobile number of the receiver of results from GeneXpert. Please start from country code.");
		adminPhoneTextField.setText ("+923452345345");
		dateTimeFormatLable.setText ("Date/Time Format:");
		dateTimeFormatComboBox.setModel (new DefaultComboBoxModel (new String[] {"yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss", "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy", "M/d/yy", "dd/MM/yyyy hh:mm:ssa",
				"dd/MM/yyyy", "d/M/yy", "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss", "yyyy-MM-dd'T'HH:mm:ss'Z'"}));
		dateTimeFormatComboBox
				.setToolTipText ("Choose a format for date and time fields.\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\n[M/d/yy] is short USA format date, e.g. 10/23/14\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\n[d/M/yy] is short UK format date, e.g. 23/10/14\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\n\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");
		useSslCheckBox.setText ("Use SSL/TLS Encryption");
		variablesListLabel.setText ("Variables to Send:");
		conceptsList
				.setToolTipText ("Select all the attributes to be sent via SMS. Press Ctrl key and click multiple variables. In order to avoid long messages, choose only the essential ones. The default selections send an SMS of approximately 190 characters");
		verticalScrollPane.setViewportView (conceptsList);
		conceptsList.setModel (new AbstractListModel ()
		{
			private static final long	serialVersionUID	= 8641812103569559442L;
			String[]					values				= new String[] {"assayHostTestCode", "assay", "assayVersion", "sampleId", "patientId", "user", "testStartedOn", "testEndedOn",
																	"messageSentOn", "reagentLotId", "cartridgeExpirationDate", "cartridgeSerial", "moduleSerial", "instrumentSerial",
																	"softwareVersion", "resultMtb", "resultRif", "resultText", "deviceSerial", "hostId", "systemName", "computerName", "notes",
																	"errorCode", "errorNotes", "externalTestId", "probeA", "probeB", "probeC", "probeD", "probeE", "probeSpc", "qc1", "qc2",
																	"probeACt", "probeBCt", "probeCCt", "probeDCt", "probeECt", "probeSpcCt", "qc1Ct", "qc2Ct", "probeAEndpt", "probeBEndpt",
																	"probeCEndpt", "probeDEndpt", "probeEEndpt", "probeSpcEndpt", "qc1Endpt", "qc2Endpt", "validatedLab", "validatedLocal"};

			public int getSize ()
			{
				return values.length;
			}

			public Object getElementAt (int index)
			{
				return values[index];
			}
		});
		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout (topDialogPanel);
		topDialogPanelLayout.setHorizontalGroup(
			topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(topDialogPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addComponent(adminPhoneLabel, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
							.addGap(19)
							.addComponent(adminPhoneTextField, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE))
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(topDialogPanelLayout.createSequentialGroup()
									.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(dateTimeFormatLable, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
										.addComponent(variablesListLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
									.addGap(25))
								.addGroup(topDialogPanelLayout.createSequentialGroup()
									.addComponent(recommendedButton)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(useSslCheckBox, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
								.addComponent(dateTimeFormatComboBox, 0, 186, Short.MAX_VALUE)
								.addGroup(topDialogPanelLayout.createSequentialGroup()
									.addComponent(tryButton)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(saveButton))
								.addComponent(verticalScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addComponent(smsServerAddressLabel, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addGap(12)
							.addComponent(smsServerAddressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addContainerGap(28, Short.MAX_VALUE))
		);
		topDialogPanelLayout.setVerticalGroup(
			topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(topDialogPanelLayout.createSequentialGroup()
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGap(12)
							.addComponent(smsServerAddressLabel))
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(smsServerAddressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGap(6)
							.addComponent(adminPhoneLabel))
						.addComponent(adminPhoneTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGap(5)
							.addComponent(dateTimeFormatLable))
						.addComponent(dateTimeFormatComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(useSslCheckBox)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addComponent(variablesListLabel)
							.addPreferredGap(ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
							.addComponent(recommendedButton)
							.addGap(1))
						.addComponent(verticalScrollPane, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(saveButton)
						.addComponent(tryButton))
					.addGap(228))
		);
		topDialogPanel.setLayout (topDialogPanelLayout);
		contentPanel.setLayout (new FlowLayout ());
		contentPanel.setBorder (new EmptyBorder (5, 5, 5, 5));
		GroupLayout layout = new javax.swing.GroupLayout (this);
		layout.setHorizontalGroup (layout.createParallelGroup (Alignment.LEADING).addGap (0, 442, Short.MAX_VALUE));
		layout.setVerticalGroup (layout.createParallelGroup (Alignment.TRAILING).addGap (0, 261, Short.MAX_VALUE));
	}

	/**
	 * Add event handlers/listeners to controls
	 */
	public void initEvents ()
	{
		recommendedButton.addActionListener (this);
		tryButton.addActionListener (this);
		saveButton.addActionListener (this);
	}

	/**
	 * Set default values for controls from properties file
	 */
	public void initValues ()
	{
		String address = XpertProperties.getProperty (XpertProperties.SMS_SERVER_ADDRESS);
		String adminPhone = XpertProperties.getProperty (XpertProperties.SMS_ADMIN_PHONE);
		String dateFormat = XpertProperties.getProperty (XpertProperties.SMS_DATE_FORMAT);
		String port = XpertProperties.getProperty (XpertProperties.SMS_PORT);
		String variables = XpertProperties.getProperty (XpertProperties.SMS_VARIABLES);
	}

	/**
	 * Check for data validity
	 * 
	 * @return
	 */
	public boolean validateData ()
	{
		boolean valid = true;
		StringBuilder error = new StringBuilder ();
		if (SwingUtil.get (smsServerAddressTextField).equals (""))
		{
			error.append ("SMS Server address must be provided.\n");
			valid = false;
		}
		if (SwingUtil.get (adminPhoneTextField).equals (""))
		{
			error.append ("Admin (mobile) phone number must be provided.\n");
			valid = false;
		}
		if (conceptsList.getSelectedIndices ().length == 0)
		{
			error.append ("At least one variable must be selected from the list.\n");
			valid = false;
		}
		if (!RegexUtil.isValidURL (SwingUtil.get (smsServerAddressTextField)))
		{
			error.append ("SMS Server address seems to be invalid, it is recommended to copy-paste the key rather than typing.\n");
			valid = false;
		}
		if (!RegexUtil.isContactNumber (SwingUtil.get (adminPhoneTextField)))
		{
			error.append ("Admin (mobile) phone number seems to be invalid.\n");
			valid = false;
		}
		if (!valid)
		{
			JOptionPane.showMessageDialog (new JFrame (), error.toString (), "Error!", JOptionPane.ERROR_MESSAGE);
		}
		return valid;
	}

	/**
	 * Test the current configuration
	 */
	public void tryConfiguration ()
	{
		String successMessage = "";
		String failureMessage = "";
		if (validateData ())
		{
		}
	}

	public boolean saveConfiguration ()
	{
		if (validateData ())
		{
			Map<String, String> properties = new HashMap<String, String> ();
			// properties.put (XpertProperties.OPENMRS_DATE_FORMAT,
			// dateFormatComboBox.getSelectedItem ().toString ());
			boolean saved = XpertProperties.writeProperties (properties);
			return saved;
		}
		return false;
	}

	@Override
	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource () == recommendedButton)
		{
			conceptsList.setSelectedIndices (new int[] {});
			conceptsList.setSelectedIndices (new int[] {3, 4, 5, 7, 17, 23});
		}
		else if (e.getSource () == tryButton)
		{
			tryConfiguration ();
		}
		else if (e.getSource () == saveButton)
		{
			saveConfiguration ();
		}
	}
}
