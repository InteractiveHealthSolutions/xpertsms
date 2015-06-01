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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.constant.GxVariables;
import com.ihsinformatics.xpertsms.net.OpenMrsApiAuthRest;
import com.ihsinformatics.xpertsms.util.RegexUtil;
import com.ihsinformatics.xpertsms.util.SwingUtil;

/**
 * GUI form to provide settings for OpenMRS using REST-WS to send results to
 * @author owais.hussain@ihsinformatics.com
 */
public class OpenMrsDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -6629569177787110764L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JPanel topDialogPanel;
	
	private JScrollPane conceptsScrollPane;
	
	private JLabel addressLabel;
	
	private JLabel usernameLabel;
	
	private JLabel passwordLabel;
	
	private JLabel dateTimeFormatLabel;
	
	private JLabel encounterTypeLabel;
	
	private JLabel conceptMappingLabel;
	
	private JTextField addressTextField;
	
	private JTextField usernameTextField;
	
	private JPasswordField passwordField;
	
	private JTextField encounterTypeTextField;
	
	private JTextArea conceptsTextArea;
	
	private JComboBox<?> dateFormatComboBox;
	
	private JCheckBox sslCheckBox;
	
	private JButton saveButton;
	
	private JButton tryButton;
	
	private Map<String, String> conceptMap;
	
	public OpenMrsDialog() {
		initComponents();
		initEvents();
		initValues();
	}
	
	/**
	 * Initialize form components and layout
	 */
	public void initComponents() {
		setName("openMrsDialog");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setMinimumSize(new Dimension(520, 300));
		setTitle("OpenMRS Configuration");
		setPreferredSize(new Dimension(480, 300));
		setBounds(100, 100, 520, 383);
		getContentPane().setLayout(new BorderLayout());
		
		topDialogPanel = new javax.swing.JPanel();
		getContentPane().add(topDialogPanel, BorderLayout.CENTER);
		dateTimeFormatLabel = new JLabel();
		dateFormatComboBox = new JComboBox<Object>();
		dateFormatComboBox.setName("dateTimeFormat");
		
		dateTimeFormatLabel.setText("Date/Time format:");
		dateFormatComboBox.setModel(new DefaultComboBoxModel(new String[] { "yyyy-MM-dd hh:mm:ss",
		        "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd", "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy", "M/d/yy",
		        "dd/MM/yyyy hh:mm:ssa", "dd/MM/yyyy", "d/M/yy", "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss" }));
		dateFormatComboBox
		        .setToolTipText("Choose a format for date and time fields.\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\n[M/d/yy] is short USA format date, e.g. 10/23/14\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\n[d/M/yy] is short UK format date, e.g. 23/10/14\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\n\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");
		tryButton = new javax.swing.JButton();
		
		tryButton.setText("Try..");
		tryButton.setToolTipText("Check if the application is able to connect to the database using given credentials.");
		saveButton = new javax.swing.JButton();
		
		saveButton.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
		saveButton.setText("Save");
		saveButton.setToolTipText("Creates database tables and save settings to configuration file.");
		
		addressLabel = new JLabel();
		addressLabel.setText("OpenMRS REST-WS Address:");
		
		addressTextField = new JTextField();
		addressTextField
		        .setToolTipText("Must be a valid address to OpenMRS REST service, like \"/openmrs/ws/rest/v1/\". Please do not write http(s)://");
		addressTextField.setText("demo.openmrs.org/openmrs/ws/rest/v1/");
		addressTextField.setName("openMrsRestAddress");
		
		usernameLabel = new JLabel();
		usernameLabel.setText("OpenMRS Username:");
		
		usernameTextField = new JTextField();
		usernameTextField.setName("username");
		usernameTextField.setToolTipText("This will be your OpenMRS user name.");
		usernameTextField.setText("admin");
		
		passwordLabel = new JLabel();
		passwordLabel.setText("Password:");
		
		passwordField = new JPasswordField();
		passwordField.setName("password");
		passwordField.setToolTipText("Here goes your OpenMRS password.");
		passwordField.setText("Admin123");
		
		encounterTypeTextField = new JTextField();
		encounterTypeTextField.setName("encounterType");
		encounterTypeTextField
		        .setToolTipText("Here goes an Encounter Type you use in OpenMRS to save the GeneXpert results to. If one does not exist, it will be created automatically");
		encounterTypeTextField.setText("GeneXpert_Results");
		
		encounterTypeLabel = new JLabel();
		encounterTypeLabel.setText("Encounter Type:");
		
		conceptMappingLabel = new JLabel();
		conceptMappingLabel.setText("Concept Mapping: Please map all the variables to respective OpenMRS concepts");
		
		conceptsScrollPane = new JScrollPane();
		
		sslCheckBox = new JCheckBox();
		sslCheckBox.setToolTipText("SSL/TLS encryption should be enabled if the server supports it.");
		sslCheckBox.setText("Use SSL/TLS Encryption");
		
		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout(topDialogPanel);
		topDialogPanelLayout.setHorizontalGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    topDialogPanelLayout
		            .createSequentialGroup()
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.LEADING)
		                        .addGroup(
		                            topDialogPanelLayout
		                                    .createSequentialGroup()
		                                    .addContainerGap()
		                                    .addGroup(
		                                        topDialogPanelLayout
		                                                .createParallelGroup(Alignment.LEADING)
		                                                .addGroup(
		                                                    topDialogPanelLayout.createSequentialGroup()
		                                                            .addComponent(addressLabel)
		                                                            .addPreferredGap(ComponentPlacement.RELATED))
		                                                .addComponent(usernameLabel, GroupLayout.PREFERRED_SIZE, 141,
		                                                    GroupLayout.PREFERRED_SIZE)
		                                                .addGroup(
		                                                    topDialogPanelLayout
		                                                            .createParallelGroup(Alignment.TRAILING, false)
		                                                            .addComponent(encounterTypeLabel, Alignment.LEADING,
		                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
		                                                                Short.MAX_VALUE)
		                                                            .addComponent(dateTimeFormatLabel, Alignment.LEADING,
		                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
		                                                                Short.MAX_VALUE)))
		                                    .addGap(6)
		                                    .addGroup(
		                                        topDialogPanelLayout
		                                                .createParallelGroup(Alignment.LEADING)
		                                                .addGroup(
		                                                    topDialogPanelLayout
		                                                            .createParallelGroup(Alignment.TRAILING, false)
		                                                            .addComponent(encounterTypeTextField)
		                                                            .addComponent(dateFormatComboBox, 0,
		                                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                                                            .addComponent(sslCheckBox, Alignment.LEADING,
		                                                                GroupLayout.PREFERRED_SIZE, 186,
		                                                                GroupLayout.PREFERRED_SIZE))
		                                                .addGroup(
		                                                    topDialogPanelLayout
		                                                            .createParallelGroup(Alignment.TRAILING)
		                                                            .addComponent(addressTextField,
		                                                                GroupLayout.PREFERRED_SIZE, 281,
		                                                                GroupLayout.PREFERRED_SIZE)
		                                                            .addGroup(
		                                                                topDialogPanelLayout
		                                                                        .createSequentialGroup()
		                                                                        .addComponent(usernameTextField,
		                                                                            GroupLayout.PREFERRED_SIZE, 64,
		                                                                            GroupLayout.PREFERRED_SIZE)
		                                                                        .addPreferredGap(
		                                                                            ComponentPlacement.UNRELATED)
		                                                                        .addComponent(passwordLabel,
		                                                                            GroupLayout.PREFERRED_SIZE, 62,
		                                                                            GroupLayout.PREFERRED_SIZE)
		                                                                        .addPreferredGap(ComponentPlacement.RELATED)
		                                                                        .addComponent(passwordField,
		                                                                            GroupLayout.PREFERRED_SIZE, 139,
		                                                                            GroupLayout.PREFERRED_SIZE)))))
		                        .addGroup(
		                            topDialogPanelLayout.createSequentialGroup().addGap(159).addComponent(tryButton)
		                                    .addPreferredGap(ComponentPlacement.RELATED).addComponent(saveButton))
		                        .addGroup(
		                            topDialogPanelLayout
		                                    .createSequentialGroup()
		                                    .addContainerGap()
		                                    .addComponent(conceptsScrollPane, GroupLayout.PREFERRED_SIZE, 490,
		                                        GroupLayout.PREFERRED_SIZE))
		                        .addGroup(
		                            topDialogPanelLayout
		                                    .createSequentialGroup()
		                                    .addContainerGap()
		                                    .addComponent(conceptMappingLabel, GroupLayout.PREFERRED_SIZE, 384,
		                                        GroupLayout.PREFERRED_SIZE))).addContainerGap(18, Short.MAX_VALUE)));
		topDialogPanelLayout.setVerticalGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    topDialogPanelLayout
		            .createSequentialGroup()
		            .addContainerGap()
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(addressLabel)
		                        .addComponent(addressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(usernameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE)
		                        .addComponent(passwordLabel)
		                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(usernameLabel))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(dateFormatComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(dateTimeFormatLabel))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(encounterTypeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(encounterTypeLabel))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(sslCheckBox)
		            .addGap(9)
		            .addComponent(conceptMappingLabel)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(conceptsScrollPane, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(saveButton)
		                        .addComponent(tryButton)).addGap(11)));
		
		conceptsTextArea = new JTextArea();
		conceptsTextArea.setName("conceptMapping");
		conceptsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
		conceptsTextArea
		        .setToolTipText("This map defines which concepts in OpenMRS correspond to the following variables used to store GeneXpert results:\r\nsampleId=X_SAMPLE_ID\r\ntestStartedOn=X_START_DATE\r\ntestEndedOn=X_END_DATE\r\nmessageSentOn=X_MESSAGE_DATE\r\nreagentLotId=X_LOT_ID\r\ncartridgeExpirationDate=X_EXP_DATE\r\ncartridgeSerial=X_CARTRIDGE_ID\r\nmoduleSerial=X_MODULE_ID\r\ninstrumentSerial=X_INSTRUMENT_ID\r\nmtbResultText=X_MTB\r\nrifResultText=X_RIF\r\nhostId=X_HOST_ID\r\ncomputerName=X_COMPUTER_NAME\r\nnotes=X_NOTES\r\nerrorCode=X_ERROR_CODE\r\nerrorNotes=X_ERROR_NOTES\r\nprobeA=X_PROBE_A\r\nprobeB=X_PROBE_B\r\nprobeC=X_PROBE_C\r\nprobeD=X_PROBE_D\r\nprobeE=X_PROBE_E\r\nprobeSpc=X_PROBE_SPC\r\nqc1=X_QC1\r\nqc2=X_QC2\r\nprobeACt=X_PROBE_ACT\r\nprobeBCt=X_PROBE_BCT\r\nprobeCCt=X_PROBE_CCT\r\nprobeDCt=X_PROBE_DCT\r\nprobeECt=X_PROBE_ECT\r\nprobeSpcCt=X_PROBE_SPC_CT\r\nqc1Ct=X_QC1_CT\r\nqc2Ct=X_QC2_CT\r\nprobeAEndpt=X_PROBE_A_END\r\nprobeBEndpt=X_PROBE_B_END\r\nprobeCEndpt=X_PROBE_C_END\r\nprobeDEndpt=X_PROBE_D_END\r\nprobeEEndpt=X_PROBE_E_END\r\nprobeSpcEndpt=X_PROBE_SPC_END\r\nqc1Endpt=X_QC1_END\r\nqc2Endpt=X_QC2_END");
		conceptsTextArea
		        .setText("sampleId=X_SAMPLE_ID\r\ntestStartedOn=X_START_DATE\r\ntestEndedOn=X_END_DATE\r\nmessageSentOn=X_MESSAGE_DATE\r\nreagentLotId=X_LOT_ID\r\ncartridgeExpirationDate=X_EXP_DATE\r\ncartridgeSerial=X_CARTRIDGE_ID\r\nmoduleSerial=X_MODULE_ID\r\ninstrumentSerial=X_INSTRUMENT_ID\r\nmtbResultText=X_MTB\r\nrifResultText=X_RIF\r\nhostId=X_HOST_ID\r\ncomputerName=X_COMPUTER_NAME\r\nnotes=X_NOTES\r\nerrorCode=X_ERROR_CODE\r\nerrorNotes=X_ERROR_NOTES\r\nprobeA=X_PROBE_A\r\nprobeB=X_PROBE_B\r\nprobeC=X_PROBE_C\r\nprobeD=X_PROBE_D\r\nprobeE=X_PROBE_E\r\nprobeSpc=X_PROBE_SPC\r\nqc1=X_QC1\r\nqc2=X_QC2\r\nprobeACt=X_PROBE_ACT\r\nprobeBCt=X_PROBE_BCT\r\nprobeCCt=X_PROBE_CCT\r\nprobeDCt=X_PROBE_DCT\r\nprobeECt=X_PROBE_ECT\r\nprobeSpcCt=X_PROBE_SPC_CT\r\nqc1Ct=X_QC1_CT\r\nqc2Ct=X_QC2_CT\r\nprobeAEndpt=X_PROBE_A_END\r\nprobeBEndpt=X_PROBE_B_END\r\nprobeCEndpt=X_PROBE_C_END\r\nprobeDEndpt=X_PROBE_D_END\r\nprobeEEndpt=X_PROBE_E_END\r\nprobeSpcEndpt=X_PROBE_SPC_END\r\nqc1Endpt=X_QC1_END\r\nqc2Endpt=X_QC2_END");
		conceptsTextArea.setRows(5);
		conceptsTextArea.setColumns(20);
		conceptsScrollPane.setViewportView(conceptsTextArea);
		topDialogPanel.setLayout(topDialogPanelLayout);
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 442, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.TRAILING).addGap(0, 261, Short.MAX_VALUE));
	}
	
	/**
	 * Add event handlers/listeners to controls
	 */
	public void initEvents() {
		tryButton.addActionListener(this);
		saveButton.addActionListener(this);
	}
	
	/**
	 * Set default values for controls from properties file
	 */
	public void initValues() {
		String address = XpertProperties.getProperty(XpertProperties.OPENMRS_REST_ADDRESS);
		String username = XpertProperties.getProperty(XpertProperties.OPENMRS_USER);
		String password = XpertProperties.getProperty(XpertProperties.OPENMRS_PASSWORD);
		String sslEncryption = XpertProperties.getProperty(XpertProperties.OPENMRS_SSL_ENCRYPTION);
		String dateFormat = XpertProperties.getProperty(XpertProperties.OPENMRS_DATE_FORMAT);
		String conceptMap = XpertProperties.getProperty(XpertProperties.OPENMRS_CONCEPT_MAP);
		String encounterType = XpertProperties.getProperty(XpertProperties.OPENMRS_ENCOUNTER_TYPE);
		
		if (!"".equals(address)) {
			addressTextField.setText(address);
		}
		if (!"".equals(username)) {
			usernameTextField.setText(username);
		}
		if (!"".equals(password)) {
			passwordField.setText(password);
		}
		if (!"".equals(sslEncryption)) {
			sslCheckBox.setSelected(sslEncryption.equals("YES"));
		}
		if (!"".equals(dateFormat)) {
			dateFormatComboBox.setSelectedItem(dateFormat);
		}
		if (!"".equals(conceptMap)) {
			StringBuilder sb = new StringBuilder();
			for (String str : conceptMap.split(","))
				sb.append(str + "\r\n");
			conceptsTextArea.setText(sb.toString());
		}
		if (!"".equals(encounterType)) {
			encounterTypeTextField.setText(encounterType);
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
		if ("".equals(SwingUtil.get(addressTextField))) {
			error.append("OpenMRS REST Address must be provided.\n");
			valid = false;
		} else if (!RegexUtil.isValidURL(SwingUtil.get(addressTextField))) {
			error.append("OpenMRS REST Address does not appear to be valid. You should copy-paste the address instead of typing in.\n");
			valid = false;
		}
		if ("".equals(SwingUtil.get(usernameTextField))) {
			error.append("OpenMRS username must be provided.\n");
			valid = false;
		} else if (!RegexUtil.isValidUsername(SwingUtil.get(usernameTextField))) {
			error.append("OpenMRS username does not appear to be valid. Allowed characters are alphabets, numbers, uderscore (_) and period (.) only.\n");
			valid = false;
		}
		if ("".equals(SwingUtil.get(passwordField))) {
			error.append("OpenMRS password must be provided.\n");
			valid = false;
		}
		if ("".equals(SwingUtil.get(encounterTypeTextField))) {
			error.append("OpenMRS Encounter Type must be provided.\n");
			valid = false;
		}
		if ("".equals(SwingUtil.get(conceptsTextArea))) {
			error.append("Concept mapping must be provided.\n");
			valid = false;
		} else {
			conceptMap = new HashMap<String, String>();
			// Parse the text. The pattern of each line should be String=String
			// strictly
			String map = SwingUtil.get(conceptsTextArea);
			String[] pairs = map.split("\r\n");
			for (String pairStr : pairs) {
				String[] pair = pairStr.split(":");
				if (pair == null) {
					error.append("Problem occurred while reading concept map " + pairStr);
					valid = false;
					break;
				} else if (pair.length < 0) {
					error.append(pairStr
					        + " does not seem to be a proper map entry. Make sure it is in the format: variable=concept\n");
					valid = false;
				} else {
					String variable = pair[0];
					String concept = pair[1];
					// Check if the variable name is defined in qualified
					// variables list
					if (!GxVariables.VARIABLES.contains(variable)) {
						error.append(variable
						        + " is undefined. Please write variable names properly (tooltip contains this list), these are case-sensitive, i.e. 'sampleId' is not equal to 'SampleID'\n");
						valid = false;
					} else {
						conceptMap.put(variable, concept);
					}
				}
			}
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
		String successMessage = "Connection to OpenMRS was successful. Missing Concepts will be created as specified on save. Save now?";
		String failureMessage = "Unable to process request to OpenMRS. This could be because of insufficient rights, wrong address or invalid username/password.";
		if (validateData()) {
			String prefix = sslCheckBox.isSelected() ? "https://" : "http://";
			String url = prefix + SwingUtil.get(addressTextField);
			String username = SwingUtil.get(usernameTextField);
			String password = SwingUtil.get(passwordField);
			String response = "";
			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				// Create a REST request to OpenMRS server to get a session
				HttpGet httpGet = new HttpGet(url + "session");
				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
				BasicScheme scheme = new BasicScheme();
				@SuppressWarnings("deprecation")
				Header authorizationHeader = scheme.authenticate(credentials, httpGet);
				httpGet.setHeader(authorizationHeader);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				System.out.println("Executing request: " + httpGet.getRequestLine());
				response = httpclient.execute(httpGet, responseHandler);
				int selected = JOptionPane.showConfirmDialog(new JFrame(), successMessage, "It works! Save now?",
				    JOptionPane.YES_NO_OPTION);
				if (selected == JOptionPane.YES_OPTION) {
					saveConfiguration();
				}
				System.out.println(response);
			}
			catch (UnknownHostException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage
				        + "\nI suspect wrong address or connectivity problem" + "\nServer says: " + e.getMessage(),
				    "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
			catch (AuthenticationException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage + "\nI suspect wrong username and/or password"
				        + "\nServer says: " + e.getMessage(), "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
			catch (ClientProtocolException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage + "\nServer says: " + e.getMessage(),
				    "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(
				    new JFrame(),
				    "The connection looks okay, but there was a problem in reading the response" + "\nServer says: "
				            + e.getMessage(), "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
			finally {
				httpclient.getConnectionManager().shutdown();
			}
		}
	}
	
	public boolean saveConfiguration() {
		if (validateData()) {
			String prefix = sslCheckBox.isSelected() ? "https" : "http" + "://";
			String url = prefix + SwingUtil.get(addressTextField);
			String username = SwingUtil.get(usernameTextField);
			String password = SwingUtil.get(passwordField);
			String response = "";
			try {
				// Check if the encounter type already exists
				OpenMrsApiAuthRest api = new OpenMrsApiAuthRest(username, password, url);
				response = api.get("encountertype?q=" + SwingUtil.get(encounterTypeTextField) + "&v=custom:(uuid,name)");
				JSONObject encounterObj = new JSONObject(response);
				JSONArray encounters = encounterObj.getJSONArray("results");
				// If not, create one
				if (encounters.isEmpty()) {
					String data = "{\"name\":\"" + SwingUtil.get(encounterTypeTextField)
					        + "\",\"description\":\"Encounter for GeneXpert results from XpertSMS\"}";
					response = api.post("encountertype", data);
					if (!response.equals("SUCCESS")) {
						JOptionPane.showMessageDialog(new JFrame(),
						    "Uh oh! A problem occurred while creating new Encounter type" + "\nServer says: " + response,
						    "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
				StringBuilder conceptMessage = new StringBuilder();
				int created = 0;
				int existed = 0;
				int error = 0;
				// Repeat the same with concepts
				for (String key : conceptMap.keySet()) {
					String value = conceptMap.get(key);
					// Check if a concept exists that matches value
					response = api.get("concept?q=" + value);
					JSONObject conceptObj = new JSONObject(response);
					JSONArray concepts = conceptObj.getJSONArray("results");
					// If not, create one
					if (concepts.isEmpty()) {
						conceptObj = new JSONObject();
						JSONArray names = new JSONArray();
						JSONObject name = new JSONObject();
						name.put("name", value);
						name.put("locale", "en");
						name.put("conceptNameType", "FULLY_SPECIFIED");
						names.put(name);
						conceptObj.put("names", names);
						conceptObj.put("datatype", "TEXT");
						conceptObj.put("conceptClass", "LabSet");
						JSONArray descriptions = new JSONArray();
						JSONObject description = new JSONObject();
						description.put("description", "Auto-generated concept for " + key + " for GeneXpert Result");
						description.put("locale", "en");
						descriptions.put(description);
						conceptObj.put("descriptions", descriptions);
						response = api.post("concept", conceptObj.toString());
						System.out.println(response);
						if (!response.equals("SUCCESS")) {
							error++;
						} else {
							created++;
						}
					} else {
						existed++;
					}
				}
				if (created > 0)
					conceptMessage.append(created + " new concepts created\n");
				if (existed > 0)
					conceptMessage.append(existed + " concepts already existed\n");
				if (error > 0)
					conceptMessage.append(error + " error(s) occurred while creating concpets. Please check the log file\n");
				JOptionPane.showMessageDialog(new JFrame(), conceptMessage.toString() + "\nServer says: " + response,
				    "Important", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			// Save configurations
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(XpertProperties.OPENMRS_DATE_FORMAT, dateFormatComboBox.getSelectedItem().toString());
			properties.put(XpertProperties.OPENMRS_REST_ADDRESS, SwingUtil.get(addressTextField));
			properties.put(XpertProperties.OPENMRS_USER, SwingUtil.get(usernameTextField));
			properties.put(XpertProperties.OPENMRS_PASSWORD, String.valueOf(passwordField.getPassword()));
			properties.put(XpertProperties.OPENMRS_ENCOUNTER_TYPE, SwingUtil.get(encounterTypeTextField));
			properties.put(XpertProperties.OPENMRS_SSL_ENCRYPTION, sslCheckBox.isSelected() ? "NO" : "YES");
			StringBuilder concepts = new StringBuilder();
			Set<String> keySet = conceptMap.keySet();
			for (String key : keySet) {
				String value = conceptMap.get(key);
				concepts.append(key + ":" + value + ",");
			}
			// Remove additional comma at the end
			concepts = concepts.replace(concepts.length() - 1, concepts.length() - 1, "");
			properties.put(XpertProperties.OPENMRS_CONCEPT_MAP, concepts.toString());
			boolean saved = XpertProperties.writeProperties(properties);
			return saved;
		}
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tryButton) {
			tryConfiguration();
		} else if (e.getSource() == saveButton) {
			
		}
		
	}
}
