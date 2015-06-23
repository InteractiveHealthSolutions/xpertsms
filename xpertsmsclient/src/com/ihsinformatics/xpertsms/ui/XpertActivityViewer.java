/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.xpertsms.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.model.MessageType;
import com.ihsinformatics.xpertsms.net.ResultServer;
import com.ihsinformatics.xpertsms.util.DateTimeUtil;
import javax.swing.JScrollPane;

/**
 * Demon GUI form to view activity of messages between GX DX and XpertSMS
 * 
 * @author owais.hussain@ihsinformatics.com
 */
public class XpertActivityViewer extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 550000139271750414L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JPanel bottomDialogPanel;
	
	private JPanel topDialogPanel;
	
	private static JScrollPane logScrollPane;
	
	private static JTextPane logTextPane;
	
	private static JCheckBox detailedLogCheckBox;
	
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
		
		GroupLayout topDialogPanelLayout = new GroupLayout(topDialogPanel);
		topDialogPanelLayout.setHorizontalGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 512,
		    Short.MAX_VALUE));
		topDialogPanelLayout.setVerticalGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addGap(0, 271,
		    Short.MAX_VALUE));
		topDialogPanel.setLayout(topDialogPanelLayout);
		
		logTextPane = new JTextPane();
		getContentPane().add(logTextPane, BorderLayout.WEST);
		logTextPane.setContentType("text/html");
		
		logScrollPane = new JScrollPane(logTextPane);
		logScrollPane.setAutoscrolls(true);
		getContentPane().add(logScrollPane, BorderLayout.CENTER);
		
		bottomDialogPanel = new JPanel();
		getContentPane().add(bottomDialogPanel, BorderLayout.SOUTH);
		
		detailedLogCheckBox = new JCheckBox("Show detailed log");
		detailedLogCheckBox.setSelected(true);
		bottomDialogPanel.add(detailedLogCheckBox);
		
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
		detailedLogCheckBox.addActionListener(this);
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
		updateTextPane("Data export services:", MessageType.INFO);
		if (exportCsv)
			updateTextPane("- CSV exports to " + XpertProperties.getProperty(XpertProperties.CSV_FOLDER_PATH),
			    MessageType.INFO);
		if (exportWeb)
			updateTextPane("- Web exports to " + XpertProperties.getProperty(XpertProperties.WEB_APP_STRING)
			        + " (data encryption = " + XpertProperties.getProperty(XpertProperties.WEB_SSL_ENCRYPTION) + ")",
			    MessageType.INFO);
		if (exportSms)
			updateTextPane("- SMS exports to " + XpertProperties.getProperty(XpertProperties.SMS_ADMIN_PHONE),
			    MessageType.INFO);
		if (exportGxa)
			updateTextPane("- GXAlert exports to " + XpertProperties.getProperty(XpertProperties.GXA_SERVER_ADDRESS)
			        + " (data encryption = " + XpertProperties.getProperty(XpertProperties.GXA_SSL_ENCRYPTION) + ")",
			    MessageType.INFO);
		if (exportOpenMrs)
			updateTextPane("- OpenMRS exports to " + XpertProperties.getProperty(XpertProperties.OPENMRS_REST_ADDRESS)
			        + " (data encryption = " + XpertProperties.getProperty(XpertProperties.OPENMRS_SSL_ENCRYPTION) + ")",
			    MessageType.INFO);
		logTextPane.setText(text.toString());
	}
	
	public void startServer() {
		server = new ResultServer(logTextPane, detailedLogCheckBox.isSelected());
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
		} else if (e.getSource() == detailedLogCheckBox) {
			if (server != null)
				server.setDetailedLog(detailedLogCheckBox.isSelected());
		}
	}
	
	public static synchronized void updateTextPane(final String text, final MessageType messageType) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				String prefix = detailedLogCheckBox.isSelected() ? DateTimeUtil.getSQLDateTime(new Date()) + ": " : "";
				String allText = prefix + text + "\n";
				StyledDocument styledDoc = logTextPane.getStyledDocument();
				SimpleAttributeSet attrs = new SimpleAttributeSet();
				StyleConstants.setFontSize(attrs, 14);
				try {
					switch (messageType) {
						case ERROR:
						case EXCEPTION:
							StyleConstants.setForeground(attrs, Color.RED);
							StyleConstants.setBold(attrs, true);
							break;
						case WARNING:
							StyleConstants.setForeground(attrs, Color.ORANGE);
							StyleConstants.setBold(attrs, true);
							break;
						case INFO:
							StyleConstants.setForeground(attrs, Color.BLUE.darker());
							break;
						case SUCCESS:
							StyleConstants.setForeground(attrs, Color.GREEN.darker());
							break;
					}
					styledDoc.insertString(styledDoc.getLength(), allText, attrs);
				}
				catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
				logTextPane.setCaretPosition(styledDoc.getLength() - 1);
			}
		});
	}
}
