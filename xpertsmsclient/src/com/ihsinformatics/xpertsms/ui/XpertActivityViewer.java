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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.net.ResultServer;

/**
 * Demon GUI form to view activity of messages between GX DX and XpertSMS
 * 
 * @author owais.hussain@ihsinformatics.com
 */
public class XpertActivityViewer extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 550000139271750414L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JPanel bottomDialogPanel;
	
	private JPanel topDialogPanel;
	
	private JTextPane logTextPane;
	
	private JCheckBox showDetailedLogCheckBox;
	
	private JButton exitButton;
	
	private JToggleButton startStopButton;
	
	private ResultServer server;
	
	private boolean exportCsv;
	
	private boolean exportSms;
	
	private boolean exportWeb;
	
	private boolean exportGxa;
	
	private boolean exportOpenMrs;
	
	public XpertActivityViewer() {
		initComponents();
		initEvents();
		initValues();
	}
	
	/**
	 * Initialize form components and layout
	 */
	public void initComponents() {
		setName("xpertActivityViewer");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(520, 300));
		setTitle("XpertSMS Activity");
		setPreferredSize(new Dimension(480, 300));
		setBounds(100, 100, 480, 340);
		getContentPane().setLayout(new BorderLayout());
		
		topDialogPanel = new JPanel();
		getContentPane().add(topDialogPanel, BorderLayout.CENTER);
		
		logTextPane = new JTextPane();
		
		GroupLayout topDialogPanelLayout = new GroupLayout(topDialogPanel);
		topDialogPanelLayout.setHorizontalGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    topDialogPanelLayout.createSequentialGroup().addContainerGap()
		            .addComponent(logTextPane, GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE).addContainerGap()));
		topDialogPanelLayout.setVerticalGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    topDialogPanelLayout.createSequentialGroup().addContainerGap()
		            .addComponent(logTextPane, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE).addContainerGap()));
		topDialogPanel.setLayout(topDialogPanelLayout);
		
		bottomDialogPanel = new JPanel();
		getContentPane().add(bottomDialogPanel, BorderLayout.SOUTH);
		
		showDetailedLogCheckBox = new JCheckBox("Show detailed log");
		bottomDialogPanel.add(showDetailedLogCheckBox);
		
		startStopButton = new JToggleButton("Start");
		bottomDialogPanel.add(startStopButton);
		exitButton = new JButton();
		exitButton.setHorizontalAlignment(SwingConstants.RIGHT);
		bottomDialogPanel.add(exitButton);
		
		exitButton.setFont(new Font("Tahoma", 2, 12)); // NOI18N
		exitButton.setText("Exit");
		exitButton.setToolTipText("Creates database tables and save settings to configuration file.");
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GroupLayout layout = new GroupLayout(this);
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 442, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.TRAILING).addGap(0, 261, Short.MAX_VALUE));
	}
	
	/**
	 * Add event handlers/listeners to controls
	 */
	public void initEvents() {
		startStopButton.addActionListener(this);
		exitButton.addActionListener(this);
	}
	
	/**
	 * Set default values for controls from properties file
	 */
	public void initValues() {
		exportCsv = XpertProperties.getProperty(XpertProperties.CSV_EXPORT).equals("YES");
		exportWeb = XpertProperties.getProperty(XpertProperties.WEB_EXPORT).equals("YES");
		exportSms = XpertProperties.getProperty(XpertProperties.SMS_EXPORT).equals("YES");
		exportGxa = XpertProperties.getProperty(XpertProperties.GXA_EXPORT).equals("YES");
		exportOpenMrs = XpertProperties.getProperty(XpertProperties.OPENMRS_EXPORT).equals("YES");
		StringBuilder text = new StringBuilder();
		text.append("Data export services:\n");
		if (exportCsv)
			text.append("- CSV exports to " + XpertProperties.getProperty(XpertProperties.CSV_FOLDER_PATH) + "\n");
		if (exportWeb)
			text.append("- Web exports to " + XpertProperties.getProperty(XpertProperties.WEB_APP_STRING)
			        + " (data encryption = " + XpertProperties.getProperty(XpertProperties.WEB_SSL_ENCRYPTION) + ")\n");
		if (exportSms)
			text.append("- SMS exports to " + XpertProperties.getProperty(XpertProperties.SMS_ADMIN_PHONE) + "\n");
		if (exportGxa)
			text.append("- GXAlert exports to " + XpertProperties.getProperty(XpertProperties.GXA_SERVER_ADDRESS)
			        + " (data encryption = " + XpertProperties.getProperty(XpertProperties.GXA_SSL_ENCRYPTION) + ")\n");
		if (exportOpenMrs)
			text.append("- OpenMRS exports to " + XpertProperties.getProperty(XpertProperties.OPENMRS_REST_ADDRESS)
			        + " (data encryption = " + XpertProperties.getProperty(XpertProperties.OPENMRS_SSL_ENCRYPTION) + ")\n");
		logTextPane.setText(text.toString());
	}
	
	public void startServer() {
		server = new ResultServer(logTextPane, showDetailedLogCheckBox.isSelected());
		server.start();
	}
	
	private void stopServer() {
		server.setStopped(true);
		try {
			server.getSocket().close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		server.getMessages().clear();
		server = null;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startStopButton) {
			// Toggle down is stop
			if (startStopButton.isSelected()) {
				startStopButton.setText("Start");
				startServer();
			} else {
				startStopButton.setText("Stop");
				exitButton.setEnabled(true);
				stopServer();
			}
		} else if (e.getSource() == exitButton) {
			XpertConfiguration xpertConfiguration = new XpertConfiguration();
			xpertConfiguration.setVisible(true);
			dispose();
		}
	}
}
