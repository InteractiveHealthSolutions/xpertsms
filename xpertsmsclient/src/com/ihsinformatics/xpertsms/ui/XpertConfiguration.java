/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Main Configuration panel for XpertSMS to set up various results export channels
 */

package com.ihsinformatics.xpertsms.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import com.ihsinformatics.xpertsms.model.XpertProperties;
import com.ihsinformatics.xpertsms.util.SwingUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 */
public class XpertConfiguration extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 4866613443914433909L;
	
	private JPanel topPanel;
	
	private JPanel middlePanel;
	
	private JLabel headingLabel;
	
	private JButton smsConfigButton;
	
	private JButton csvConfigButton;
	
	private JButton webConfigButton;
	
	private JButton gxaConfigButton;
	
	private JButton openMrsConfigButton;
	
	private JCheckBox csvCheckBox;
	
	private JCheckBox gxaCheckBox;
	
	private JCheckBox openMrsCheckBox;
	
	private JCheckBox smsCheckBox;
	
	private JCheckBox webCheckBox;
	
	private JTextField mtbCodeTextField;
	
	private JTextField rifCodeTextField;
	
	private JTextField qcCodeTextField;
	
	private JTextField localPortTextField;
	
	private JTextField userTextField;
	
	private JPasswordField passwordField;
	
	private JButton importButton;
	
	private JButton startButton;
	
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
	
	public XpertConfiguration() {
		XpertProperties.readProperties();
		setPreferredSize(new Dimension(670, 420));
		setSize(new Dimension(654, 420));
		setName("mainFrame");
		initComponents();
		initEvents();
		applyProperties();
	}
	
	private void initComponents() {
		topPanel = new JPanel();
		topPanel.setBounds(1, 5, 653, 26);
		headingLabel = new JLabel();
		middlePanel = new JPanel();
		middlePanel.setBounds(11, 33, 635, 144);
		middlePanel.setBorder(new LineBorder(Color.GRAY, 1, true));
		smsCheckBox = new JCheckBox();
		csvCheckBox = new JCheckBox();
		webCheckBox = new JCheckBox();
		gxaCheckBox = new JCheckBox();
		openMrsCheckBox = new JCheckBox();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("XpertSMS Configuration");
		setMinimumSize(new Dimension(600, 375));
		
		headingLabel.setFont(new Font("Arial", 1, 12)); // NOI18N
		headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		headingLabel
		        .setText("Welcome to XpertSMS. Please specify how you would like to export results from GeneXpert software");
		headingLabel.setName(""); // NOI18N
		
		GroupLayout topPanelLayout = new GroupLayout(topPanel);
		topPanelLayout.setHorizontalGroup(topPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    topPanelLayout.createSequentialGroup()
		            .addComponent(headingLabel, GroupLayout.PREFERRED_SIZE, 629, GroupLayout.PREFERRED_SIZE)
		            .addContainerGap(24, Short.MAX_VALUE)));
		topPanelLayout.setVerticalGroup(topPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(headingLabel,
		    GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE));
		topPanel.setLayout(topPanelLayout);
		
		smsCheckBox
		        .setText("SMS: Ideal for exporting results in absence of a reliable internet connection. Requires SMS Tarseel installed");
		csvCheckBox.setText("CSV Export: To export results to a text file. Caution! Not recommended for insecure computers");
		webCheckBox
		        .setText("Web Service: Use this to post results to an external web service in Json/XML form. This option requires internet");
		gxaCheckBox.setText("GXAlert: Offers a strong reporting backbone. You will need to setup GXAlert server");
		openMrsCheckBox
		        .setText("OpenMRS: Integrate with OpenMRS. You will need to setup OpenMRS server to use this service");
		GroupLayout middlePanelLayout = new GroupLayout(middlePanel);
		middlePanelLayout.setHorizontalGroup(middlePanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    middlePanelLayout
		            .createSequentialGroup()
		            .addContainerGap()
		            .addGroup(
		                middlePanelLayout.createParallelGroup(Alignment.LEADING).addComponent(smsCheckBox)
		                        .addComponent(csvCheckBox).addComponent(webCheckBox).addComponent(gxaCheckBox)
		                        .addComponent(openMrsCheckBox)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		middlePanelLayout.setVerticalGroup(middlePanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    middlePanelLayout.createSequentialGroup().addComponent(smsCheckBox)
		            .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(csvCheckBox)
		            .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(webCheckBox)
		            .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(gxaCheckBox)
		            .addPreferredGap(ComponentPlacement.UNRELATED).addComponent(openMrsCheckBox)
		            .addContainerGap(15, Short.MAX_VALUE)));
		middlePanel.setLayout(middlePanelLayout);
		
		JPanel geneXpertPanel = new JPanel();
		geneXpertPanel.setBounds(11, 188, 634, 116);
		geneXpertPanel.setBorder(new LineBorder(Color.GRAY, 1, true));
		
		JLabel lblMtbCode = new JLabel("MTB Code:");
		
		mtbCodeTextField = new JTextField();
		mtbCodeTextField.setToolTipText("Enter the code in your project represents MTB");
		mtbCodeTextField.setText("TB_POS");
		mtbCodeTextField.setColumns(10);
		
		JLabel lblRifCodel = new JLabel("RIF Code:");
		
		rifCodeTextField = new JTextField();
		rifCodeTextField.setToolTipText("Enter the code in your project represents RIF resistance");
		rifCodeTextField.setText("RIF");
		rifCodeTextField.setColumns(10);
		
		JLabel lblQcCode = new JLabel("QC Code:");
		
		qcCodeTextField = new JTextField();
		qcCodeTextField.setToolTipText("Enter Quality control code (if any)");
		qcCodeTextField.setText("QC");
		qcCodeTextField.setColumns(10);
		
		localPortTextField = new JTextField();
		localPortTextField.setToolTipText("This is the port of your computer that the GeneXpert machine is connected to");
		localPortTextField.setText("12221");
		localPortTextField.setColumns(10);
		
		JLabel lblLocalPort = new JLabel("Local Port:");
		
		JLabel lblGenexpertUsername = new JLabel("GeneXpert Username:");
		
		userTextField = new JTextField();
		userTextField.setToolTipText("Username for your GeneXpert machine");
		userTextField.setText("admin");
		userTextField.setColumns(10);
		
		passwordField = new JPasswordField();
		
		JLabel lblPassword = new JLabel("Password:");
		GroupLayout geneXpertPanelLayout = new GroupLayout(geneXpertPanel);
		geneXpertPanelLayout.setHorizontalGroup(geneXpertPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    geneXpertPanelLayout
		            .createSequentialGroup()
		            .addContainerGap()
		            .addGroup(
		                geneXpertPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(lblMtbCode)
		                        .addComponent(lblRifCodel).addComponent(lblQcCode))
		            .addGap(18)
		            .addGroup(
		                geneXpertPanelLayout
		                        .createParallelGroup(Alignment.LEADING)
		                        .addComponent(qcCodeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE)
		                        .addGroup(
		                            geneXpertPanelLayout
		                                    .createSequentialGroup()
		                                    .addGroup(
		                                        geneXpertPanelLayout.createParallelGroup(Alignment.TRAILING, false)
		                                                .addComponent(rifCodeTextField, Alignment.LEADING)
		                                                .addComponent(mtbCodeTextField, Alignment.LEADING))
		                                    .addGap(14)
		                                    .addGroup(
		                                        geneXpertPanelLayout
		                                                .createParallelGroup(Alignment.LEADING)
		                                                .addComponent(lblGenexpertUsername)
		                                                .addGroup(
		                                                    geneXpertPanelLayout.createSequentialGroup().addGap(1)
		                                                            .addComponent(lblPassword)).addComponent(lblLocalPort))
		                                    .addGap(18)
		                                    .addGroup(
		                                        geneXpertPanelLayout.createParallelGroup(Alignment.TRAILING, false)
		                                                .addComponent(localPortTextField, Alignment.LEADING)
		                                                .addComponent(passwordField, Alignment.LEADING)
		                                                .addComponent(userTextField, Alignment.LEADING)))).addGap(238)));
		geneXpertPanelLayout.setVerticalGroup(geneXpertPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    geneXpertPanelLayout
		            .createSequentialGroup()
		            .addContainerGap()
		            .addGroup(
		                geneXpertPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblMtbCode)
		                        .addComponent(mtbCodeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE)
		                        .addComponent(localPortTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(lblLocalPort))
		            .addPreferredGap(ComponentPlacement.UNRELATED)
		            .addGroup(
		                geneXpertPanelLayout
		                        .createParallelGroup(Alignment.LEADING)
		                        .addGroup(
		                            geneXpertPanelLayout
		                                    .createSequentialGroup()
		                                    .addGroup(
		                                        geneXpertPanelLayout
		                                                .createParallelGroup(Alignment.BASELINE)
		                                                .addComponent(userTextField, GroupLayout.PREFERRED_SIZE,
		                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		                                                .addComponent(lblGenexpertUsername))
		                                    .addPreferredGap(ComponentPlacement.UNRELATED)
		                                    .addGroup(
		                                        geneXpertPanelLayout
		                                                .createParallelGroup(Alignment.BASELINE)
		                                                .addComponent(passwordField, GroupLayout.PREFERRED_SIZE,
		                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		                                                .addComponent(lblPassword)))
		                        .addGroup(
		                            geneXpertPanelLayout
		                                    .createSequentialGroup()
		                                    .addGroup(
		                                        geneXpertPanelLayout
		                                                .createParallelGroup(Alignment.BASELINE)
		                                                .addComponent(lblRifCodel)
		                                                .addComponent(rifCodeTextField, GroupLayout.PREFERRED_SIZE,
		                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		                                    .addPreferredGap(ComponentPlacement.UNRELATED)
		                                    .addGroup(
		                                        geneXpertPanelLayout
		                                                .createParallelGroup(Alignment.BASELINE)
		                                                .addComponent(lblQcCode)
		                                                .addComponent(qcCodeTextField, GroupLayout.PREFERRED_SIZE,
		                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
		            .addContainerGap(53, Short.MAX_VALUE)));
		geneXpertPanel.setLayout(geneXpertPanelLayout);
		getContentPane().setLayout(null);
		getContentPane().add(topPanel);
		getContentPane().add(geneXpertPanel);
		getContentPane().add(middlePanel);
		importButton = new JButton();
		importButton.setBounds(10, 347, 119, 23);
		importButton.setFont(new Font("Tahoma", 0, 12)); // NOI18N
		importButton.setText("Import Settings");
		getContentPane().add(importButton);
		startButton = new JButton();
		startButton.setBackground(Color.GRAY);
		startButton.setBounds(559, 347, 76, 23);
		
		startButton.setFont(new Font("Tahoma", 2, 12)); // NOI18N
		startButton.setText("START");
		getContentPane().add(startButton);
		
		smsConfigButton = new JButton("SMS Config");
		smsConfigButton.setBounds(10, 308, 119, 28);
		getContentPane().add(smsConfigButton);
		
		csvConfigButton = new JButton("CSV Config");
		csvConfigButton.setBounds(139, 308, 118, 28);
		getContentPane().add(csvConfigButton);
		
		webConfigButton = new JButton("Web Config");
		webConfigButton.setBounds(267, 308, 118, 28);
		getContentPane().add(webConfigButton);
		
		gxaConfigButton = new JButton("GXA Config");
		gxaConfigButton.setBounds(395, 308, 118, 28);
		getContentPane().add(gxaConfigButton);
		
		openMrsConfigButton = new JButton("OMRS Config");
		openMrsConfigButton.setBounds(527, 308, 118, 28);
		getContentPane().add(openMrsConfigButton);
		
		pack();
	}
	
	public void initEvents() {
		smsConfigButton.addActionListener(this);
		csvConfigButton.addActionListener(this);
		webConfigButton.addActionListener(this);
		gxaConfigButton.addActionListener(this);
		openMrsConfigButton.addActionListener(this);
		importButton.addActionListener(this);
		startButton.addActionListener(this);
	}
	
	public void applyProperties() {
		String csvExport = XpertProperties.getProperty(XpertProperties.CSV_EXPORT);
		String smsExport = XpertProperties.getProperty(XpertProperties.SMS_EXPORT);
		String gxaExport = XpertProperties.getProperty(XpertProperties.GXA_EXPORT);
		String openMrsExport = XpertProperties.getProperty(XpertProperties.OPENMRS_EXPORT);
		String webExport = XpertProperties.getProperty(XpertProperties.WEB_EXPORT);
		String xpertUser = XpertProperties.getProperty(XpertProperties.XPERT_USER);
		String xpertPassword = XpertProperties.getProperty(XpertProperties.XPERT_PASSWORD);
		String mtbCode = XpertProperties.getProperty(XpertProperties.MTB_CODE);
		String rifCode = XpertProperties.getProperty(XpertProperties.RIF_CODE);
		String qcCode = XpertProperties.getProperty(XpertProperties.QC_CODE);
		String localPort = XpertProperties.getProperty(XpertProperties.LOCAL_PORT);
		csvCheckBox.setSelected(csvExport.equals("YES"));
		smsCheckBox.setSelected(smsExport.equals("YES"));
		gxaCheckBox.setSelected(gxaExport.equals("YES"));
		openMrsCheckBox.setSelected(openMrsExport.equals("YES"));
		webCheckBox.setSelected(webExport.equals("YES"));
		userTextField.setText(xpertUser);
		passwordField.setText(xpertPassword);
		mtbCodeTextField.setText(mtbCode);
		rifCodeTextField.setText(rifCode);
		qcCodeTextField.setText(qcCode);
		localPortTextField.setText(localPort);
	}
	
	public boolean validateData() {
		// TODO: Apply validations
		// Open option must be checked
		// All fields are mandatory
		// Password is at least 8 characters
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == csvConfigButton) {
			CsvDialog csvDialog = new CsvDialog();
			csvDialog.setVisible(true);
		} else if (evt.getSource() == gxaConfigButton) {
			GxaDialog gxaDialog = new GxaDialog();
			gxaDialog.setVisible(true);
		} else if (evt.getSource() == openMrsConfigButton) {
			OpenMrsDialog openMrsDialog = new OpenMrsDialog();
			openMrsDialog.setVisible(true);
		} else if (evt.getSource() == smsConfigButton) {
			SmsDialog smsDialog = new SmsDialog();
			smsDialog.setVisible(true);
		} else if (evt.getSource() == webConfigButton) {
			// TODO: Show Web configuration dialog
		} else if (evt.getSource() == importButton) {
			File file = SwingUtil.chooseFile("Browse for the file to import", false);
			if (file != null) {
				// Check if the file is a valid properties file
				try {
					Properties newProps = new Properties();
					newProps.load(new FileInputStream(file.getAbsolutePath()));
					String p1 = newProps.getProperty(XpertProperties.CSV_EXPORT);
					String p2 = newProps.getProperty(XpertProperties.SMS_EXPORT);
					String p3 = newProps.getProperty(XpertProperties.GXA_EXPORT);
					String p4 = newProps.getProperty(XpertProperties.OPENMRS_EXPORT);
					String p5 = newProps.getProperty(XpertProperties.WEB_EXPORT);
					// Invalid if export properties are missing
					if ("".equals(p1) || "".equals(p2) || "".equals(p3) || "".equals(p4) || "".equals(p5)) {
						JOptionPane.showMessageDialog(new JFrame(),
						    "The file you have imported is either corrupt or invalid.", "Error!", JOptionPane.ERROR_MESSAGE);
					} else {
						Map<String, String> properties = new HashMap<String, String>();
						for (String property : newProps.stringPropertyNames())
							properties.put(property, newProps.getProperty(property));
						XpertProperties.writeProperties(properties);
						JOptionPane
						        .showMessageDialog(
						            new JFrame(),
						            "Properties have been successfully imported. Please restart the application to apply new configuration.",
						            "Imported!", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (evt.getSource() == startButton) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(XpertProperties.CSV_EXPORT, csvCheckBox.isSelected() ? "YES" : "NO");
			properties.put(XpertProperties.SMS_EXPORT, smsCheckBox.isSelected() ? "YES" : "NO");
			properties.put(XpertProperties.GXA_EXPORT, gxaCheckBox.isSelected() ? "YES" : "NO");
			properties.put(XpertProperties.OPENMRS_EXPORT, openMrsCheckBox.isSelected() ? "YES" : "NO");
			properties.put(XpertProperties.WEB_EXPORT, webCheckBox.isSelected() ? "YES" : "NO");
			properties.put(XpertProperties.XPERT_USER, SwingUtil.get(userTextField));
			properties.put(XpertProperties.XPERT_PASSWORD, SwingUtil.get(passwordField));
			properties.put(XpertProperties.MTB_CODE, SwingUtil.get(mtbCodeTextField));
			properties.put(XpertProperties.RIF_CODE, SwingUtil.get(rifCodeTextField));
			properties.put(XpertProperties.QC_CODE, SwingUtil.get(qcCodeTextField));
			properties.put(XpertProperties.LOCAL_PORT, SwingUtil.get(localPortTextField));
			XpertProperties.writeProperties(properties);
			// TODO: Launch Control panel
		}
	}
}
