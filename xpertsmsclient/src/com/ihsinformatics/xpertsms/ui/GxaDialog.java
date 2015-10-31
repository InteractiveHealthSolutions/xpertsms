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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import net.sf.json.JSONObject;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.net.exception.HttpResponseException;
import com.ihsinformatics.xpertsms.util.SwingUtil;

/**
 * GUI form to provide settings for GXAlert to send results to
 * @author owais.hussain@ihsinformatics.com
 */
public class GxaDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 6667416512565715878L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JPanel topDialogPanel;
	
	private JLabel serverAddressLabel;
	
	private JLabel apiKeyLabel;
	
	private JLabel dateTimeFormatLabel;
	
	private JTextField serverAddressTextField;
	
	private JSpinner portSpinner;
	
	private JTextField apiKeyTextField;
	
	private JCheckBox usePortCheckBox;
	
	private JCheckBox sslCheckBox;
	
	private JComboBox<?> dateFormatComboBox;
	
	private JButton saveButton;
	
	private JButton tryButton;
	
	public GxaDialog() {
		setAlwaysOnTop(true);
		initComponents();
		initEvents();
		initValues();
	}
	
	/**
	 * Initialize form components and layout
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void initComponents() {
		setName("csvDialog");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setMinimumSize(new Dimension(450, 250));
		setTitle("GXAlert Configuration");
		setPreferredSize(new Dimension(450, 250));
		setBounds(100, 100, 470, 240);
		getContentPane().setLayout(new BorderLayout());
		
		topDialogPanel = new javax.swing.JPanel();
		getContentPane().add(topDialogPanel, BorderLayout.CENTER);
		dateTimeFormatLabel = new javax.swing.JLabel();
		dateFormatComboBox = new javax.swing.JComboBox();
		dateFormatComboBox.setEditable(true);
		
		dateTimeFormatLabel.setText("Date/Time format:");
		dateFormatComboBox.setModel(new DefaultComboBoxModel(new String[] { "yyyy-MM-dd'T'HH:mm:ss'Z'",
		        "yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy", "M/d/yy", "dd/MM/yyyy hh:mm:ssa",
		        "dd/MM/yyyy", "d/M/yy", "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss" }));
		dateFormatComboBox
		        .setToolTipText("Choose a format for date and time fields.\r\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\r\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\r\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\r\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\r\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\r\n[M/d/yy] is short USA format date, e.g. 10/23/14\r\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\r\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\r\n[d/M/yy] is short UK format date, e.g. 23/10/14\r\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\r\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\r\n\r\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");
		tryButton = new javax.swing.JButton();
		
		tryButton.setText("Try..");
		tryButton.setToolTipText("Check if the application is able to connect to the database using given credentials.");
		saveButton = new javax.swing.JButton();
		
		saveButton.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
		saveButton.setText("Save");
		saveButton.setToolTipText("Creates database tables and save settings to configuration file.");
		
		serverAddressLabel = new JLabel();
		serverAddressLabel.setText("* GXAlert Server Address:");
		
		serverAddressTextField = new JTextField();
		serverAddressTextField
		        .setToolTipText("Must be a valid IP address, like \"127.0.0.1\" or a URL, like \"dev.gxalert.com/api/result\". Please do not write http(s)://");
		serverAddressTextField.setText("dev.gxalert.com/api/result");
		serverAddressTextField.setName("gxaAddress");
		
		usePortCheckBox = new JCheckBox();
		usePortCheckBox.setEnabled(false);
		usePortCheckBox.setText("Use Port:");
		
		portSpinner = new JSpinner();
		portSpinner.setEnabled(false);
		portSpinner.setModel(new SpinnerNumberModel(8080, 1, 65535, 1));
		portSpinner
		        .setToolTipText("Port number to connect to the server. If you are not sure what this is, try 8080 or just 80, or ask your Network Admin");
		
		apiKeyLabel = new JLabel();
		apiKeyLabel.setText("* GXAlert API Code:");
		
		apiKeyTextField = new JTextField();
		apiKeyTextField.setToolTipText("Enter your unique GXAlert API key, provided by the GXAlert server admin");
		apiKeyTextField.setText("228ksh92a8wegwe43b2hhnvs88m62gkkagwe8dj5qg7exvv6uzkb82cuy2995czz");
		
		sslCheckBox = new JCheckBox();
		sslCheckBox.setToolTipText("SSL/TLS encryption should be enabled if the server supports it.");
		sslCheckBox.setText("Use SSL/TLS Encryption");
		
		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout(topDialogPanel);
		topDialogPanelLayout
		        .setHorizontalGroup(topDialogPanelLayout
		                .createParallelGroup(Alignment.LEADING)
		                .addGroup(
		                    topDialogPanelLayout
		                            .createSequentialGroup()
		                            .addContainerGap()
		                            .addGroup(
		                                topDialogPanelLayout
		                                        .createParallelGroup(Alignment.LEADING)
		                                        .addComponent(usePortCheckBox)
		                                        .addGroup(
		                                            topDialogPanelLayout
		                                                    .createParallelGroup(Alignment.TRAILING, false)
		                                                    .addComponent(apiKeyLabel, Alignment.LEADING,
		                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
		                                                        Short.MAX_VALUE)
		                                                    .addComponent(dateTimeFormatLabel, Alignment.LEADING,
		                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
		                                                        Short.MAX_VALUE)).addComponent(serverAddressLabel))
		                            .addPreferredGap(ComponentPlacement.UNRELATED)
		                            .addGroup(
		                                topDialogPanelLayout
		                                        .createParallelGroup(Alignment.LEADING)
		                                        .addComponent(apiKeyTextField, GroupLayout.PREFERRED_SIZE, 306,
		                                            GroupLayout.PREFERRED_SIZE)
		                                        .addComponent(serverAddressTextField, GroupLayout.PREFERRED_SIZE, 183,
		                                            GroupLayout.PREFERRED_SIZE)
		                                        .addComponent(portSpinner, GroupLayout.PREFERRED_SIZE, 71,
		                                            GroupLayout.PREFERRED_SIZE)
		                                        .addGroup(
		                                            topDialogPanelLayout
		                                                    .createParallelGroup(Alignment.TRAILING, false)
		                                                    .addComponent(sslCheckBox, Alignment.LEADING,
		                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
		                                                        Short.MAX_VALUE)
		                                                    .addComponent(dateFormatComboBox, Alignment.LEADING, 0,
		                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
		                            .addContainerGap(10, Short.MAX_VALUE))
		                .addGroup(
		                    Alignment.TRAILING,
		                    topDialogPanelLayout.createSequentialGroup().addContainerGap(178, Short.MAX_VALUE)
		                            .addComponent(tryButton).addPreferredGap(ComponentPlacement.RELATED)
		                            .addComponent(saveButton).addGap(176)));
		topDialogPanelLayout.setVerticalGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    topDialogPanelLayout
		            .createSequentialGroup()
		            .addContainerGap()
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(serverAddressLabel)
		                        .addComponent(serverAddressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(portSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(usePortCheckBox))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(apiKeyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(apiKeyLabel))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(dateFormatComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(dateTimeFormatLabel))
		            .addPreferredGap(ComponentPlacement.UNRELATED)
		            .addComponent(sslCheckBox)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout.createParallelGroup(Alignment.TRAILING).addComponent(tryButton)
		                        .addGroup(topDialogPanelLayout.createSequentialGroup().addComponent(saveButton).addGap(1)))
		            .addContainerGap(40, Short.MAX_VALUE)));
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
		usePortCheckBox.addActionListener(this);
		tryButton.addActionListener(this);
		saveButton.addActionListener(this);
	}
	
	/**
	 * Set default values for controls from properties file
	 */
	public void initValues() {
		String serverAddress = XpertProperties.getProperty(XpertProperties.GXA_SERVER_ADDRESS);
		String port = XpertProperties.getProperty(XpertProperties.GXA_PORT);
		String apiKey = XpertProperties.getProperty(XpertProperties.GXA_API_KEY);
		String dateFormat = XpertProperties.getProperty(XpertProperties.GXA_DATE_FORMAT);
		String ssl = XpertProperties.getProperty(XpertProperties.GXA_SSL_ENCRYPTION);
		if (!"".equals(serverAddress)) {}
		if (!"".equals(port)) {}
		if (!"".equals(apiKey)) {}
		if (!"".equals(dateFormat)) {
			dateFormatComboBox.setSelectedItem(dateFormat);
		}
		if (!"".equals(ssl)) {}
	}
	
	/**
	 * Check for data validity
	 * 
	 * @return
	 */
	public boolean validateData() {
		boolean valid = true;
		StringBuilder error = new StringBuilder();
		// Mandatory fields check
		if ("".equals(SwingUtil.get(serverAddressTextField))) {
			error.append("GXAlert Server Address must be provided.\n");
			valid = false;
		}
		if ("".equals(SwingUtil.get(apiKeyTextField))) {
			error.append("GXAlert unique API key must be provided.\n");
			valid = false;
		}
		if (usePortCheckBox.isSelected() && "".equals(SwingUtil.get(portSpinner))) {
			error.append("Use Port is checked but port number is not provided.\n");
			valid = false;
		}
		// Range check
		if ("".equals(SwingUtil.get(apiKeyTextField))) {
			String key = SwingUtil.get(apiKeyTextField);
			if (key.length() < 64)
				error.append("Length of API key is incorrent, it is recommended to copy-paste the key rather than typing\n");
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
		String successMessage = "Connection to the GXAlert server was successful, a test result was submitted to the server. The configuration seems Okay. Do you want to save?";
		String failureMessage = "Unable to submit dummy result to server.";
		if (validateData()) {
			// Make an HTTP(s) request to GXAlert server
			try {
				JSONObject sampleObj = new JSONObject();
				sampleObj.put("apiKey", SwingUtil.get(apiKeyTextField));
				sampleObj.put("assayHostTestCode", "MTB-RIF");
				sampleObj.put("assay", "Xpert MTB-RIF Assay");
				sampleObj.put("assayVersion", "5");
				sampleObj.put("sampleId", "130514_12544_00001");
				sampleObj.put("patientId", "101130800001-9");
				sampleObj.put("user", "Test");
				sampleObj.put("testStartedOn", "2014-11-07T17:42:55Z");
				sampleObj.put("testEndedOn", "2014-11-07T20:33:12Z");
				sampleObj.put("messageSentOn", "2014-11-07T23:21:45Z");
				sampleObj.put("reagentLotId", "10713-AX");
				sampleObj.put("cartridgeExpirationDate", "2014-12-31T00:00:00Z");
				sampleObj.put("cartridgeSerial", "204304821");
				sampleObj.put("moduleSerial", "618255");
				sampleObj.put("instrumentSerial", "708228");
				sampleObj.put("softwareVersion", "4.4a");
				sampleObj.put("resultIdMtb", 2);
				sampleObj.put("resultIdRif", 2);
				sampleObj.put("resultText", "MTB DETECTED MEDIUM|Rif Resistance NOT DETECTED");
				sampleObj.put("deviceSerial", "CEPHEID5G183R1");
				sampleObj.put("hostId", "Machine API Test");
				sampleObj.put("systemName", "GeneXpert PC");
				sampleObj.put("computerName", "CepheidJRJRFQ1");
				sampleObj.put("notes", "Nothing serious");
				sampleObj.put("errorCode", "");
				sampleObj.put("errorNotes", "");
				sampleObj.put("externalTestId", "X-123-4-XXZ");
				sampleObj.put("probeA", "NEG");
				sampleObj.put("probeB", "POS");
				sampleObj.put("probeC", "NO RESULT");
				sampleObj.put("probeD", "NEG");
				sampleObj.put("probeE", "NEG");
				sampleObj.put("probeSpc", "POS");
				sampleObj.put("probeACt", 0);
				sampleObj.put("probeBCt", 1.1);
				sampleObj.put("probeCCt", 2.2);
				sampleObj.put("probeDCt", 3.3);
				sampleObj.put("probeECt", 4.4);
				sampleObj.put("probeSpcCt", 5.5);
				sampleObj.put("probeAEndpt", 8.8);
				sampleObj.put("probeBEndpt", 9.9);
				sampleObj.put("probeCEndpt", 1.2);
				sampleObj.put("probeDEndpt", 2.3);
				sampleObj.put("probeEEndpt", 3.4);
				sampleObj.put("probeSpcEndpt", 4.5);
				
				HttpURLConnection httpConnection = null;
				OutputStream os = null;
				int responseCode = 0;
				String response = null;
				String url = sslCheckBox.isSelected() ? "https" : "http" + "://" + SwingUtil.get(serverAddressTextField);
				byte[] content = sampleObj.toString().getBytes();
				URL gxaUrl = new URL(url);
				httpConnection = (HttpURLConnection) gxaUrl.openConnection();
				httpConnection.setRequestProperty("Content-Length", String.valueOf(content.length));
				httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				httpConnection.setRequestProperty("Host", SwingUtil.get(serverAddressTextField));
				httpConnection.setRequestMethod("POST");
				httpConnection.setDoOutput(true);
				os = httpConnection.getOutputStream();
				os.write(content);
				os.flush();
				responseCode = httpConnection.getResponseCode();
				if (responseCode != HttpURLConnection.HTTP_OK)
					throw new HttpResponseException(responseCode);
				BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
				String line = "";
				response = "";
				while ((line = br.readLine()) != null)
					response += line;
				os.close();
				httpConnection.disconnect();
				System.out.println(response);
				int selected = JOptionPane.showConfirmDialog(new JFrame(), successMessage, "It works! Save now?",
				    JOptionPane.YES_NO_OPTION);
				if (selected == JOptionPane.YES_OPTION) {
					saveConfiguration();
				}
			}
			catch (HttpResponseException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage + "\nServer says: " + e.getMessage(),
				    "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage + "\nServer says: " + e.getMessage(),
				    "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public boolean saveConfiguration() {
		if (validateData()) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(XpertProperties.GXA_SERVER_ADDRESS, SwingUtil.get(serverAddressTextField));
			properties.put(XpertProperties.GXA_PORT, usePortCheckBox.isSelected() ? SwingUtil.get(portSpinner) : "");
			properties.put(XpertProperties.GXA_API_KEY, SwingUtil.get(apiKeyTextField));
			properties.put(XpertProperties.GXA_DATE_FORMAT, dateFormatComboBox.getSelectedItem().toString());
			properties.put(XpertProperties.GXA_SSL_ENCRYPTION, sslCheckBox.isSelected() ? "YES" : "NO");
			boolean saved = XpertProperties.writeProperties(properties);
			return saved;
		}
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == usePortCheckBox) {
			portSpinner.setEnabled(usePortCheckBox.isSelected());
		} else if (e.getSource() == tryButton) {
			tryConfiguration();
		} else if (e.getSource() == saveButton) {
			if (saveConfiguration()) {
				JOptionPane.showMessageDialog(new JFrame(),
				    "GXAlert Configurations have been saved successfully. Closing this dialog.", "Configuration saved!",
				    JOptionPane.INFORMATION_MESSAGE);
				dispose();
			}
		}
	}
}
