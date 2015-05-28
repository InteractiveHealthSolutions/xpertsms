/**
 * Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
 * You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
 * Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 * Contributors: Owais
 */

package com.ihsinformatics.xpertsms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.util.SwingUtil;

/**
 * @author owais.hussain@irdresearch.org
 */
public class WebDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -2732459485154603453L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JPanel topDialogPanel;
	
	private JLabel webAddressLabel;
	
	private JLabel dataFormatLabel;
	
	private JLabel authenticationLabel;
	
	private JLabel dateTimeFormatLabel;
	
	private JLabel parametersLabel;
	
	private JTextField webAddressTextField;
	
	private JTextArea parametersTextArea;
	
	private JComboBox dataFormatComboBox;
	
	private JComboBox dateTimeFormatComboBox;
	
	private JComboBox authenticationComboBox;
	
	private JRadioButton utf8RadioButton;
	
	private JRadioButton utf16RadioButton;
	
	private JButton saveButton;
	
	private JButton tryButton;
	
	public WebDialog() {
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
		webAddressLabel = new JLabel("Web URI/Servlet:");
		dataFormatLabel = new JLabel("Data Format:");
		authenticationLabel = new JLabel("Authentication:");
		webAddressTextField = new JTextField();
		dataFormatComboBox = new JComboBox();
		dateTimeFormatComboBox = new JComboBox();
		authenticationComboBox = new JComboBox();
		utf8RadioButton = new JRadioButton("UTF-8");
		utf16RadioButton = new JRadioButton("UTF-16");
		tryButton = new JButton();
		saveButton = new JButton();
		
		setName("webDialog");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setMinimumSize(new Dimension(400, 300));
		setTitle("Web Configuration");
		setPreferredSize(new Dimension(400, 300));
		setBounds(100, 100, 415, 400);
		getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(topDialogPanel, BorderLayout.CENTER);
		tryButton.setText("Try..");
		tryButton.setToolTipText("Check if the application is able to connect to the database using given credentials.");
		saveButton.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
		saveButton.setText("Save");
		saveButton.setToolTipText("Creates database tables and save settings to configuration file.");
		webAddressTextField
		        .setToolTipText("Complete web URI or servlet's address to submit requests to. For example \"http://localhost:8080/myxperthandler/xpertresults");
		webAddressTextField.setText("http://localhost:8080/myxperthandler/xpertresults");
		webAddressTextField.setName("projectName");
		dateTimeFormatComboBox.setModel(new DefaultComboBoxModel(new String[] { "yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss",
		        "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy", "M/d/yy", "dd/MM/yyyy hh:mm:ssa", "dd/MM/yyyy", "d/M/yy",
		        "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss", "yyyy-MM-dd'T'HH:mm:ss'Z'" }));
		dateTimeFormatComboBox
		        .setToolTipText("Choose a format for date and time fields.\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\n[M/d/yy] is short USA format date, e.g. 10/23/14\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\n[d/M/yy] is short UK format date, e.g. 23/10/14\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\n\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");
		dataFormatComboBox.setModel(new DefaultComboBoxModel(new String[] { "Json", "XML", "Plain text" }));
		authenticationComboBox.setModel(new DefaultComboBoxModel(new String[] { "As header", "As parameter", "None" }));
		dateTimeFormatLabel = new JLabel("Date/Time Format:");
		utf8RadioButton.setSelected(true);
		JLabel encodingLabel = new JLabel("Encoding:");
		
		parametersLabel = new JLabel();
		parametersLabel.setText("Concept Mapping: Please map all the variables to respective OpenMRS concepts");
		
		JScrollPane scrollPane = new JScrollPane();
		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout(topDialogPanel);
		topDialogPanelLayout.setHorizontalGroup(topDialogPanelLayout.createParallelGroup(Alignment.TRAILING).addGroup(
		    topDialogPanelLayout
		            .createSequentialGroup()
		            .addContainerGap()
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.LEADING)
		                        .addGroup(
		                            topDialogPanelLayout
		                                    .createSequentialGroup()
		                                    .addGroup(
		                                        topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
		                                                .addComponent(dateTimeFormatLabel).addComponent(encodingLabel))
		                                    .addGap(30)
		                                    .addGroup(
		                                        topDialogPanelLayout
		                                                .createParallelGroup(Alignment.LEADING)
		                                                .addGroup(
		                                                    topDialogPanelLayout.createSequentialGroup()
		                                                            .addComponent(utf8RadioButton)
		                                                            .addPreferredGap(ComponentPlacement.RELATED)
		                                                            .addComponent(utf16RadioButton))
		                                                .addGroup(
		                                                    topDialogPanelLayout
		                                                            .createSequentialGroup()
		                                                            .addGroup(
		                                                                topDialogPanelLayout
		                                                                        .createParallelGroup(Alignment.LEADING)
		                                                                        .addComponent(authenticationComboBox,
		                                                                            GroupLayout.PREFERRED_SIZE,
		                                                                            GroupLayout.DEFAULT_SIZE,
		                                                                            GroupLayout.PREFERRED_SIZE)
		                                                                        .addComponent(dateTimeFormatComboBox, 0, 0,
		                                                                            Short.MAX_VALUE)
		                                                                        .addComponent(dataFormatComboBox,
		                                                                            GroupLayout.PREFERRED_SIZE,
		                                                                            GroupLayout.DEFAULT_SIZE,
		                                                                            GroupLayout.PREFERRED_SIZE)
		                                                                        .addGroup(
		                                                                            topDialogPanelLayout
		                                                                                    .createSequentialGroup()
		                                                                                    .addPreferredGap(
		                                                                                        ComponentPlacement.RELATED)
		                                                                                    .addComponent(tryButton)
		                                                                                    .addPreferredGap(
		                                                                                        ComponentPlacement.RELATED)
		                                                                                    .addComponent(saveButton)))
		                                                            .addPreferredGap(ComponentPlacement.RELATED, 148,
		                                                                Short.MAX_VALUE))))
		                        .addGroup(
		                            topDialogPanelLayout
		                                    .createSequentialGroup()
		                                    .addComponent(webAddressLabel, GroupLayout.PREFERRED_SIZE, 115,
		                                        GroupLayout.PREFERRED_SIZE)
		                                    .addGap(18)
		                                    .addComponent(webAddressTextField, GroupLayout.DEFAULT_SIZE, 270,
		                                        Short.MAX_VALUE))
		                        .addGroup(
		                            topDialogPanelLayout
		                                    .createSequentialGroup()
		                                    .addGroup(
		                                        topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
		                                                .addComponent(dataFormatLabel).addComponent(authenticationLabel))
		                                    .addPreferredGap(ComponentPlacement.RELATED, 323, Short.MAX_VALUE))
		                        .addGroup(
		                            topDialogPanelLayout
		                                    .createParallelGroup(Alignment.TRAILING, false)
		                                    .addComponent(scrollPane, Alignment.LEADING)
		                                    .addComponent(parametersLabel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE,
		                                        384, Short.MAX_VALUE))).addContainerGap()));
		topDialogPanelLayout.setVerticalGroup(topDialogPanelLayout.createParallelGroup(Alignment.TRAILING).addGroup(
		    topDialogPanelLayout
		            .createSequentialGroup()
		            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(webAddressLabel)
		                        .addComponent(webAddressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE))
		            .addGap(3)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(dataFormatLabel)
		                        .addComponent(dataFormatComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE))
		            .addGap(7)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(authenticationLabel)
		                        .addComponent(authenticationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE))
		            .addPreferredGap(ComponentPlacement.UNRELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(dateTimeFormatComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(dateTimeFormatLabel))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(utf8RadioButton)
		                        .addComponent(utf16RadioButton).addComponent(encodingLabel))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(parametersLabel)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(saveButton)
		                        .addComponent(tryButton)).addGap(164)));
		
		parametersTextArea = new JTextArea();
		parametersTextArea
		        .setToolTipText("This map defines which concepts in OpenMRS correspond to the following variables used to store GeneXpert results:\r\nsampleId=X_SAMPLE_ID\r\ntestStartedOn=X_START_DATE\r\ntestEndedOn=X_END_DATE\r\nmessageSentOn=X_MESSAGE_DATE\r\nreagentLotId=X_LOT_ID\r\ncartridgeExpirationDate=X_EXP_DATE\r\ncartridgeSerial=X_CARTRIDGE_ID\r\nmoduleSerial=X_MODULE_ID\r\ninstrumentSerial=X_INSTRUMENT_ID\r\nmtbResultText=X_MTB\r\nrifResultText=X_RIF\r\nhostId=X_HOST_ID\r\ncomputerName=X_COMPUTER_NAME\r\nnotes=X_NOTES\r\nerrorCode=X_ERROR_CODE\r\nerrorNotes=X_ERROR_NOTES\r\nprobeA=X_PROBE_A\r\nprobeB=X_PROBE_B\r\nprobeC=X_PROBE_C\r\nprobeD=X_PROBE_D\r\nprobeE=X_PROBE_E\r\nprobeSpc=X_PROBE_SPC\r\nqc1=X_QC1\r\nqc2=X_QC2\r\nprobeACt=X_PROBE_ACT\r\nprobeBCt=X_PROBE_BCT\r\nprobeCCt=X_PROBE_CCT\r\nprobeDCt=X_PROBE_DCT\r\nprobeECt=X_PROBE_ECT\r\nprobeSpcCt=X_PROBE_SPC_CT\r\nqc1Ct=X_QC1_CT\r\nqc2Ct=X_QC2_CT\r\nprobeAEndpt=X_PROBE_A_END\r\nprobeBEndpt=X_PROBE_B_END\r\nprobeCEndpt=X_PROBE_C_END\r\nprobeDEndpt=X_PROBE_D_END\r\nprobeEEndpt=X_PROBE_E_END\r\nprobeSpcEndpt=X_PROBE_SPC_END\r\nqc1Endpt=X_QC1_END\r\nqc2Endpt=X_QC2_END");
		parametersTextArea.setText("Version=ver\r\nUsername=user\r\nPassword=pwd\r\nEncoding=enc\r\nData=result");
		parametersTextArea.setRows(5);
		parametersTextArea.setName("conceptMapping");
		parametersTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
		parametersTextArea.setColumns(20);
		scrollPane.setViewportView(parametersTextArea);
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
		tryButton.addActionListener(this);
		saveButton.addActionListener(this);
	}
	
	/**
	 * Set default values for controls from properties file
	 */
	public void initValues() {
		String webAddress = XpertProperties.getProperty(XpertProperties.WEB_ADDRESS);
		String dataFormat = XpertProperties.getProperty(XpertProperties.WEB_DATA_FORMAT);
		String authentication = XpertProperties.getProperty(XpertProperties.WEB_AUTHENTICATION);
		String dateFormat = XpertProperties.getProperty(XpertProperties.WEB_DATE_FORMAT);
		String parameters = XpertProperties.getProperty(XpertProperties.WEB_PARAMETERS);
		if (!"".equals(webAddress)) {
			webAddressTextField.setText(webAddress);
		}
		if (!"".equals("")) {
			dataFormatComboBox.setSelectedItem(dataFormat);
		}
		if (!"".equals(authentication)) {
			authenticationComboBox.setSelectedItem(authentication);
		}
		if (!"".equals(dateFormat)) {
			dateTimeFormatComboBox.setSelectedItem(dateFormat);
		}
		if (!"".equals(parameters)) {
			StringBuilder sb = new StringBuilder();
			for (String str : webAddress.split("="))
				sb.append(str + "\r\n");
			parametersTextArea.setText(sb.toString());
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
		if (SwingUtil.get(webAddressTextField).equals("")) {
			error.append("SMS Server address must be provided.\n");
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
		String successMessage = "";
		String failureMessage = "";
		if (validateData()) {
			if (true && false) // TODO: Of course this needs to be completed
				JOptionPane.showMessageDialog(new JFrame(), failureMessage, "Error!", JOptionPane.ERROR_MESSAGE);
			else {
				int selected = JOptionPane.showConfirmDialog(new JFrame(), successMessage, "It works! Save now?",
				    JOptionPane.YES_NO_OPTION);
				if (selected == JOptionPane.YES_OPTION) {
					saveConfiguration();
				}
			}
		}
	}
	
	public boolean saveConfiguration() {
		if (validateData()) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(XpertProperties.WEB_APP_STRING, SwingUtil.get(webAddressTextField));
			boolean saved = XpertProperties.writeProperties(properties);
			return saved;
		}
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	}
}
