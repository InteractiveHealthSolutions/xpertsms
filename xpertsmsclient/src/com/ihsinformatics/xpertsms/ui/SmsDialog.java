/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. 
*/

package com.ihsinformatics.xpertsms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
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

import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.Project;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.util.RegexUtil;
import com.ihsinformatics.xpertsms.util.SmsTarseel;
import com.ihsinformatics.xpertsms.util.SwingUtil;

/**
 * GUI form to provide settings for SMS Tarseel to send results via SMS
 * @author owais.hussain@ihsinformatics.com
 */
public class SmsDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -6629569177787110764L;
	
	public static final String TARSEEL_CFG = "smstarseel.cfg.xml";
	
	public static final String TARSEEL_PROP = "smstarseel.properties";
	
	private static TarseelServices tarseelService;
	
	private final JPanel contentPanel = new JPanel();
	
	private JPanel topDialogPanel;
	
	private JList variablesList;
	
	private JScrollPane verticalScrollPane;
	
	private JLabel projectNameLabel;
	
	private JLabel adminPhoneLabel;
	
	private JLabel dateTimeFormatLabel;
	
	private JLabel variablesListLabel;
	
	private JTextField projectNameTextField;
	
	private JTextField adminPhoneTextField;
	
	private JComboBox dateTimeFormatComboBox;
	
	private JButton recommendedButton;
	
	private JButton saveButton;
	
	private JButton tryButton;
	
	public SmsDialog() {
		setAlwaysOnTop(true);
		initComponents();
		initEvents();
		initValues();
	}
	
	public static Map<Object, Object> convertEntrySetToMap(Set<Entry<Object, Object>> entrySet) {
		Map<Object, Object> mapFromSet = new HashMap<Object, Object>();
		for (Entry<Object, Object> entry : entrySet) {
			mapFromSet.put(entry.getKey(), entry.getValue());
		}
		return mapFromSet;
	}
	
	/**
	 * Initialize form components and layout
	 */
	public void initComponents() {
		topDialogPanel = new javax.swing.JPanel();
		projectNameLabel = new JLabel();
		projectNameTextField = new JTextField();
		adminPhoneLabel = new JLabel();
		adminPhoneTextField = new JTextField();
		dateTimeFormatLabel = new JLabel();
		dateTimeFormatComboBox = new JComboBox();
		variablesListLabel = new JLabel();
		verticalScrollPane = new JScrollPane();
		variablesList = new JList();
		recommendedButton = new JButton("Recommended");
		tryButton = new JButton();
		saveButton = new JButton();
		
		setName("openMrsDialog");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setMinimumSize(new Dimension(400, 300));
		setTitle("SMS Configuration");
		setPreferredSize(new Dimension(400, 300));
		setBounds(100, 100, 415, 360);
		getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(topDialogPanel, BorderLayout.CENTER);
		tryButton.setText("Try..");
		tryButton.setToolTipText("Check if the application is able to connect to the database using given credentials.");
		saveButton.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
		saveButton.setText("Save");
		saveButton.setToolTipText("Creates database tables and save settings to configuration file.");
		projectNameLabel.setText("* Project's Name:");
		projectNameTextField.setToolTipText("Project must be configured in the SMS Tarseel web application");
		projectNameTextField.setText("XSMSKHI");
		projectNameTextField.setName("projectName");
		adminPhoneLabel.setText("* Admin's Phone No.:");
		adminPhoneTextField
		        .setToolTipText("Here goes fully qualified mobile number of the receiver of results from GeneXpert. Please start from country code.");
		adminPhoneTextField.setText("+923452345345");
		dateTimeFormatLabel.setText("Date/Time Format:");
		dateTimeFormatComboBox.setModel(new DefaultComboBoxModel(new String[] { "yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss",
		        "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy", "M/d/yy", "dd/MM/yyyy hh:mm:ssa", "dd/MM/yyyy", "d/M/yy",
		        "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss", "yyyy-MM-dd'T'HH:mm:ss'Z'" }));
		dateTimeFormatComboBox
		        .setToolTipText("Choose a format for date and time fields.\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\n[M/d/yy] is short USA format date, e.g. 10/23/14\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\n[d/M/yy] is short UK format date, e.g. 23/10/14\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\n\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");
		variablesListLabel.setText("Variables to Send:");
		variablesList
		        .setToolTipText("Select all the attributes to be sent via SMS. Press Ctrl key and click multiple variables. In order to avoid long messages, choose only the essential ones. The default selections send an SMS of approximately 190 characters");
		verticalScrollPane.setViewportView(variablesList);
		variablesList.setModel(new AbstractListModel() {
			
			private static final long serialVersionUID = 8641812103569559442L;
			
			String[] values = new String[] { "assayHostTestCode", "assay", "assayVersion", "sampleId", "patientId", "user",
			        "testStartedOn", "testEndedOn", "messageSentOn", "reagentLotId", "cartridgeExpirationDate",
			        "cartridgeSerial", "moduleSerial", "instrumentSerial", "softwareVersion", "resultMtb", "resultRif",
			        "resultText", "deviceSerial", "hostId", "systemName", "computerName", "notes", "errorCode",
			        "errorNotes", "externalTestId", "probeA", "probeB", "probeC", "probeD", "probeE", "probeSpc", "qc1",
			        "qc2", "probeACt", "probeBCt", "probeCCt", "probeDCt", "probeECt", "probeSpcCt", "qc1Ct", "qc2Ct",
			        "probeAEndpt", "probeBEndpt", "probeCEndpt", "probeDEndpt", "probeEEndpt", "probeSpcEndpt", "qc1Endpt",
			        "qc2Endpt", "validatedLab", "validatedLocal" };
			
			public int getSize() {
				return values.length;
			}
			
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout(topDialogPanel);
		topDialogPanelLayout.setHorizontalGroup(
			topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(topDialogPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addComponent(projectNameLabel, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(projectNameTextField, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE))
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(topDialogPanelLayout.createSequentialGroup()
									.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(adminPhoneLabel, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
										.addComponent(dateTimeFormatLabel, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
										.addComponent(variablesListLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
									.addGap(25))
								.addGroup(topDialogPanelLayout.createSequentialGroup()
									.addComponent(recommendedButton)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(verticalScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING, false)
									.addComponent(adminPhoneTextField, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
									.addGroup(topDialogPanelLayout.createSequentialGroup()
										.addComponent(tryButton)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(saveButton))
									.addComponent(dateTimeFormatComboBox, 0, 0, Short.MAX_VALUE)))))
					.addContainerGap(24, Short.MAX_VALUE))
		);
		topDialogPanelLayout.setVerticalGroup(
			topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(topDialogPanelLayout.createSequentialGroup()
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGap(12)
							.addComponent(projectNameLabel))
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(projectNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGap(6)
							.addComponent(adminPhoneLabel))
						.addComponent(adminPhoneTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGap(17)
							.addComponent(dateTimeFormatLabel))
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(dateTimeFormatComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGap(28)
							.addComponent(variablesListLabel)
							.addGap(36)
							.addComponent(recommendedButton))
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(verticalScrollPane, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)))
					.addGap(41)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(saveButton)
						.addComponent(tryButton))
					.addGap(34))
		);
		topDialogPanel.setLayout(topDialogPanelLayout);
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GroupLayout layout = new javax.swing.GroupLayout(this);
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 442, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.TRAILING).addGap(0, 261, Short.MAX_VALUE));
	}
	
	/**
	 * Add event handlers/listeners to controls
	 */
	public void initEvents() {
		recommendedButton.addActionListener(this);
		tryButton.addActionListener(this);
		saveButton.addActionListener(this);
	}
	
	/**
	 * Set default values for controls from properties file
	 */
	public void initValues() {
		String projectName = XpertProperties.getProperty(XpertProperties.SMS_PROJECT_NAME);
		String adminPhone = XpertProperties.getProperty(XpertProperties.SMS_ADMIN_PHONE);
		String dateTimeFormat = XpertProperties.getProperty(XpertProperties.SMS_DATE_FORMAT);
		String variables = XpertProperties.getProperty(XpertProperties.SMS_VARIABLES);
		
		if (!"".equals(projectName)) {
			projectNameTextField.setText(projectName);
		}
		if (!"".equals(adminPhone)) {
			adminPhoneTextField.setText(adminPhone);
		}
		if (!"".equals(dateTimeFormat)) {
			dateTimeFormatComboBox.setSelectedItem(dateTimeFormat);
		}
		/*if(!variables.contains("sampleId") || !variables.contains("patientId") || !variables.contains("user;") || !variables.contains("testEndedOn;") || variables.contains("resultText;") || !variables.contains("errorCode;")){
			
		}*/
		
		if (!"".equals(variables)) {
			// Parse the list of variables
			String[] split = variables.split(";");
			int[] indices = new int[split.length];
			int i = 0;
			for (String var : split) {
				int index = SwingUtil.getIndex(variablesList, var);
				if (index != -1)
					indices[i++] = index;
			}
			variablesList.setSelectedIndices(indices);
		}
	}
	
	/**
	 * Check for data validity
	 * 
	 * @return
	 */
	public boolean validateData() {
		boolean valid = true;
		StringBuilder error = new StringBuilder();
		if (SwingUtil.get(projectNameTextField).equals("")) {
			error.append("SMS Server address must be provided.\n");
			valid = false;
		}
		if (SwingUtil.get(adminPhoneTextField).equals("")) {
			error.append("Admin (mobile) phone number must be provided.\n");
			valid = false;
		}
		if (variablesList.getSelectedIndices().length == 0) {
			error.append("Select atleast one variable to display.\n");
			valid = false;
		}
		if (!RegexUtil.isContactNumber(SwingUtil.get(adminPhoneTextField))) {
			error.append("Admin (mobile) phone number seems to be invalid.\n");
			valid = false;
		}
		if (!valid) {
			JOptionPane.showMessageDialog(new JFrame(), error.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
		}
		return valid;
	}
	
	/**
	 * Test the current configuration
	 */
	public void tryConfiguration() {
		String successMessage = "Connection to SMS Tarseel was successful. The specified project name was located in SMS Tarseel.";
		String failureMessage = "Unable to connect with the SMS Tarseel server. Please make sure that you can access it on the web first.";
		String failureMessage2 = "Connection with SMS Tarseel seems okay, but the project specified was not found. Can you please confirm if you have written the same name as in SMS Tarseel web app?";
		if (validateData()) {
			try {
				SmsTarseel.instantiate();
				tarseelService = TarseelContext.getServices();
				List<Project> projects = tarseelService.getDeviceService().findProject(SwingUtil.get(projectNameTextField));
				if (projects.size() == 0)
					JOptionPane.showMessageDialog(new JFrame(), failureMessage2, "Error!", JOptionPane.ERROR_MESSAGE);
				else {
					int selected = JOptionPane.showConfirmDialog(new JFrame(), successMessage, "It works! Save now?",
					    JOptionPane.YES_NO_OPTION);
					if (selected == JOptionPane.YES_OPTION) {
						saveConfiguration();
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage, "Error!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public boolean saveConfiguration() {
		if (validateData()) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(XpertProperties.SMS_PROJECT_NAME, SwingUtil.get(projectNameTextField));
			properties.put(XpertProperties.SMS_ADMIN_PHONE, SwingUtil.get(adminPhoneTextField));
			properties.put(XpertProperties.SMS_DATE_FORMAT, SwingUtil.get(dateTimeFormatComboBox));
			String concatVariables = SwingUtil.concatenatedItems(variablesList, ';');
			if(!concatVariables.contains("sampleId")){
				concatVariables += "sampleId;";
			}
			if(!concatVariables.contains("patientId")){
				concatVariables += "patientId;";
			}
			if(!concatVariables.contains("user")){
				concatVariables += "user;";
			}
			if(!concatVariables.contains("testEndedOn")){
				concatVariables += "testEndedOn;";
			}
			if(!concatVariables.contains("resultText")){
				concatVariables += "resultText;";
			}
			if(!concatVariables.contains("errorCode")){
				concatVariables += "errorCode;";
			}
			properties.put(XpertProperties.SMS_VARIABLES, concatVariables);
			boolean saved = XpertProperties.writeProperties(properties);
			return saved;
		}
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == recommendedButton) {
			variablesList.setSelectedIndices(new int[] {});
			variablesList.setSelectedIndices(new int[] { 3, 4, 5, 7, 17, 23 });
		} else if (e.getSource() == tryButton) {
			tryConfiguration();
		} else if (e.getSource() == saveButton) {
			saveConfiguration();
		}
	}
}
