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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractListModel;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.constant.FileConstants;
import com.ihsinformatics.xpertsms.util.DocumentUtil;
import com.ihsinformatics.xpertsms.util.SwingUtil;

/**
 * GUI form to provide settings for CSV to send results to
 * @author owais.hussain@ihsinformatics.com
 */
public class CsvDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -6629569177787110764L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JPanel topDialogPanel;
	
	private JLabel dateTimeFormatLabel;
	
	private JLabel pathLabel;
	
	private JLabel fieldSeparatorLabel;
	
	private JLabel variablesLabel;
	
	private JTextField csvFolderPathTextField;
	
	private JTextArea descriptionTextArea;
	
	private JCheckBox useQuotesCheckBox;
	
	private JComboBox dateFormatComboBox;
	
	private JComboBox fieldSeparatorComboBox;
	
	private JButton selectFolderButton;
	
	private JButton saveButton;
	
	private JButton selectAllButton;
	
	private JButton selectNoneButton;
	
	private JButton tryButton;
	
	private JList variablesList;
	
	private JScrollPane variablesScrollPanel;
	
	private DocumentUtil documentUtil;
	
	private String csvFolderPath;
	
	private JScrollPane descriptionScrollPanel;
	
	public CsvDialog() {
		setAlwaysOnTop(true);
		documentUtil = new DocumentUtil(FileConstants.XPERT_DOCUMENT_FILE);
		csvFolderPath = FileConstants.XPERT_SMS_DIR;
		initComponents();
		initEvents();
		initValues();
	}
	
	/**
	 * Initialize form components and layout
	 */
	public void initComponents() {
		setName("csvDialog");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setMinimumSize(new Dimension(520, 300));
		setTitle("CSV Configuration");
		setPreferredSize(new Dimension(480, 300));
		setBounds(100, 100, 480, 340);
		getContentPane().setLayout(new BorderLayout());
		
		topDialogPanel = new javax.swing.JPanel();
		getContentPane().add(topDialogPanel, BorderLayout.CENTER);
		pathLabel = new javax.swing.JLabel();
		csvFolderPathTextField = new javax.swing.JTextField();
		csvFolderPathTextField.setEditable(false);
		csvFolderPathTextField.setText("C:\\");
		fieldSeparatorLabel = new javax.swing.JLabel();
		useQuotesCheckBox = new javax.swing.JCheckBox();
		dateTimeFormatLabel = new javax.swing.JLabel();
		dateFormatComboBox = new javax.swing.JComboBox();
		variablesLabel = new javax.swing.JLabel();
		selectAllButton = new javax.swing.JButton();
		selectNoneButton = new javax.swing.JButton();
		variablesScrollPanel = new javax.swing.JScrollPane();
		variablesList = new javax.swing.JList();
		variablesList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					JList list = (JList) e.getSource();
					StringBuilder sb = new StringBuilder();
					for (Object value : list.getSelectedValues()) {
						sb.append("- " + value.toString() + ": " + documentUtil.getDescription(value.toString()) + "\n");
					}
					descriptionTextArea.setText("Description:\n" + sb.toString());
				}
			}
		});
		
		pathLabel.setText("Folder Path:");
		csvFolderPathTextField
		        .setToolTipText("Choose the folder path where you want to store CSV files. The folder must have write permissions.");
		
		fieldSeparatorLabel.setText("Field Separator:");
		
		useQuotesCheckBox.setSelected(true);
		useQuotesCheckBox.setText("Use Quotes");
		
		dateTimeFormatLabel.setText("Date/Time format:");
		dateFormatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "yyyy-MM-dd hh:mm:ss",
		        "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd", "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy", "M/d/yy",
		        "dd/MM/yyyy hh:mm:ssa", "dd/MM/yyyy", "d/M/yy", "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss" }));
		dateFormatComboBox
		        .setToolTipText("Choose a format for date and time fields.\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\n[M/d/yy] is short USA format date, e.g. 10/23/14\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\n[d/M/yy] is short UK format date, e.g. 23/10/14\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\n\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");
		variablesLabel.setText("Variables to Send:");
		
		selectAllButton.setText("All");
		
		selectNoneButton.setText("None");
		
		variablesList.setModel(new AbstractListModel() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -4663706860639157610L;
			
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
		variablesList
		        .setToolTipText("Select all the attributes to be saved to CSV. Press Ctrl key and click multiple variables.");
		variablesScrollPanel.setViewportView(variablesList);
		tryButton = new javax.swing.JButton();
		
		tryButton.setText("Try..");
		tryButton.setToolTipText("Check if the application is able to connect to the database using given credentials.");
		saveButton = new javax.swing.JButton();
		
		saveButton.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
		saveButton.setText("Save");
		saveButton.setToolTipText("Creates database tables and save settings to configuration file.");
		
		selectFolderButton = new JButton("...");
		selectFolderButton.setToolTipText("Choose a folder to save CSV files");
		
		fieldSeparatorComboBox = new JComboBox();
		fieldSeparatorComboBox.setToolTipText("Select field separator for CSV file");
		fieldSeparatorComboBox.setModel(new DefaultComboBoxModel(
		        new String[] { "COMMA", "SEMICOLON", "SPACE", "TAB", "PIPE" }));
		
		descriptionScrollPanel = new JScrollPane();
		
		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout(topDialogPanel);
		topDialogPanelLayout
		        .setHorizontalGroup(topDialogPanelLayout
		                .createParallelGroup(Alignment.LEADING)
		                .addGroup(
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
		                                                                    topDialogPanelLayout
		                                                                            .createSequentialGroup()
		                                                                            .addGroup(
		                                                                                topDialogPanelLayout
		                                                                                        .createParallelGroup(
		                                                                                            Alignment.LEADING)
		                                                                                        .addComponent(
		                                                                                            dateTimeFormatLabel)
		                                                                                        .addComponent(pathLabel)
		                                                                                        .addComponent(
		                                                                                            fieldSeparatorLabel))
		                                                                            .addPreferredGap(
		                                                                                ComponentPlacement.UNRELATED)
		                                                                            .addGroup(
		                                                                                topDialogPanelLayout
		                                                                                        .createParallelGroup(
		                                                                                            Alignment.LEADING)
		                                                                                        .addGroup(
		                                                                                            topDialogPanelLayout
		                                                                                                    .createSequentialGroup()
		                                                                                                    .addComponent(
		                                                                                                        csvFolderPathTextField,
		                                                                                                        259, 259,
		                                                                                                        259)
		                                                                                                    .addPreferredGap(
		                                                                                                        ComponentPlacement.RELATED)
		                                                                                                    .addComponent(
		                                                                                                        selectFolderButton,
		                                                                                                        GroupLayout.PREFERRED_SIZE,
		                                                                                                        32,
		                                                                                                        GroupLayout.PREFERRED_SIZE))
		                                                                                        .addGroup(
		                                                                                            topDialogPanelLayout
		                                                                                                    .createSequentialGroup()
		                                                                                                    .addComponent(
		                                                                                                        fieldSeparatorComboBox,
		                                                                                                        GroupLayout.PREFERRED_SIZE,
		                                                                                                        GroupLayout.DEFAULT_SIZE,
		                                                                                                        GroupLayout.PREFERRED_SIZE)
		                                                                                                    .addGap(18)
		                                                                                                    .addComponent(
		                                                                                                        useQuotesCheckBox))
		                                                                                        .addComponent(
		                                                                                            dateFormatComboBox,
		                                                                                            GroupLayout.PREFERRED_SIZE,
		                                                                                            GroupLayout.DEFAULT_SIZE,
		                                                                                            GroupLayout.PREFERRED_SIZE)
		                                                                                        .addGroup(
		                                                                                            topDialogPanelLayout
		                                                                                                    .createSequentialGroup()
		                                                                                                    .addComponent(
		                                                                                                        variablesScrollPanel,
		                                                                                                        GroupLayout.PREFERRED_SIZE,
		                                                                                                        GroupLayout.DEFAULT_SIZE,
		                                                                                                        GroupLayout.PREFERRED_SIZE)
		                                                                                                    .addPreferredGap(
		                                                                                                        ComponentPlacement.RELATED)
		                                                                                                    .addComponent(
		                                                                                                        descriptionScrollPanel,
		                                                                                                        GroupLayout.DEFAULT_SIZE,
		                                                                                                        233,
		                                                                                                        Short.MAX_VALUE))))
		                                                                .addGroup(
		                                                                    topDialogPanelLayout
		                                                                            .createParallelGroup(Alignment.TRAILING)
		                                                                            .addGroup(
		                                                                                topDialogPanelLayout
		                                                                                        .createParallelGroup(
		                                                                                            Alignment.LEADING, false)
		                                                                                        .addComponent(
		                                                                                            selectAllButton,
		                                                                                            Alignment.TRAILING,
		                                                                                            GroupLayout.DEFAULT_SIZE,
		                                                                                            GroupLayout.DEFAULT_SIZE,
		                                                                                            Short.MAX_VALUE)
		                                                                                        .addComponent(
		                                                                                            selectNoneButton,
		                                                                                            Alignment.TRAILING))
		                                                                            .addComponent(variablesLabel)))
		                                                    .addPreferredGap(ComponentPlacement.RELATED))
		                                        .addGroup(
		                                            topDialogPanelLayout.createSequentialGroup().addGap(161)
		                                                    .addComponent(tryButton)
		                                                    .addPreferredGap(ComponentPlacement.RELATED)
		                                                    .addComponent(saveButton))).addGap(0)));
		topDialogPanelLayout.setVerticalGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
		    topDialogPanelLayout
		            .createSequentialGroup()
		            .addContainerGap()
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(pathLabel)
		                        .addComponent(csvFolderPathTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(selectFolderButton))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(fieldSeparatorLabel)
		                        .addComponent(fieldSeparatorComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(useQuotesCheckBox))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(dateTimeFormatLabel)
		                        .addComponent(dateFormatComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.LEADING)
		                        .addComponent(descriptionScrollPanel)
		                        .addGroup(
		                            topDialogPanelLayout.createSequentialGroup().addComponent(variablesLabel).addGap(46)
		                                    .addComponent(selectAllButton).addPreferredGap(ComponentPlacement.RELATED)
		                                    .addComponent(selectNoneButton)).addComponent(variablesScrollPanel))
		            .addGap(7)
		            .addGroup(
		                topDialogPanelLayout.createParallelGroup(Alignment.TRAILING).addComponent(tryButton)
		                        .addGroup(topDialogPanelLayout.createSequentialGroup().addComponent(saveButton).addGap(1)))
		            .addGap(6)));
		
		descriptionTextArea = new JTextArea();
		descriptionTextArea.setEditable(false);
		descriptionScrollPanel.setViewportView(descriptionTextArea);
		descriptionTextArea.setWrapStyleWord(true);
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
		selectFolderButton.addActionListener(this);
		selectAllButton.addActionListener(this);
		selectNoneButton.addActionListener(this);
		tryButton.addActionListener(this);
		saveButton.addActionListener(this);
	}
	
	/**
	 * Set default values for controls from properties file
	 */
	public void initValues() {
		String folderPath = XpertProperties.getProperty(XpertProperties.CSV_FOLDER_PATH);
		String fieldSeparator = XpertProperties.getProperty(XpertProperties.CSV_FIELD_SEPARATOR);
		String useQuotes = XpertProperties.getProperty(XpertProperties.CSV_USE_QUOTES);
		String dateFormat = XpertProperties.getProperty(XpertProperties.CSV_DATE_FORMAT);
		String variables = XpertProperties.getProperty(XpertProperties.CSV_VARIABLES);
		if (!"".equals(folderPath)) {
			csvFolderPath = folderPath;
			csvFolderPathTextField.setText(csvFolderPath);
		}
		if (!"".equals(fieldSeparator)) {
			fieldSeparatorComboBox.setSelectedItem(fieldSeparator.toUpperCase());
		}
		if (!"".equals(useQuotes)) {
			useQuotesCheckBox.setSelected(useQuotes.equals("YES"));
		}
		if (!"".equals(dateFormat)) {
			dateFormatComboBox.setSelectedItem(dateFormat);
		}
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
		if (variablesList.getSelectedIndices().length == 0 || variablesList.getSelectedIndex() == -1) {
			error.append("At least one variable must be selected from variables list.");
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
		String successMessage = "A temporary csv file was successfully created and deleted. The configuration seems Okay. Do you want to Save it?";
		String failureMessage = "Unable to create temporary csv file in the target folder. This could be because of insufficient rights, zero disk space or wrong address.";
		if (validateData()) {
			// Check folder permissions by creating empty file and deleting
			File temp = new File(csvFolderPath + "delete_me.tmp");
			try {
				temp.createNewFile();
				temp.delete();
				int selected = JOptionPane.showConfirmDialog(new JFrame(), successMessage, "It works! Save now?",
				    JOptionPane.YES_NO_OPTION);
				if (selected == JOptionPane.YES_OPTION) {
					saveConfiguration();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage + "\nServer says: " + e.getMessage(),
				    "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public boolean saveConfiguration() {
		if (validateData()) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(XpertProperties.CSV_FOLDER_PATH, csvFolderPath);
			properties.put(XpertProperties.CSV_FIELD_SEPARATOR, fieldSeparatorComboBox.getSelectedItem().toString());
			properties.put(XpertProperties.CSV_USE_QUOTES, useQuotesCheckBox.isSelected() ? "YES" : "NO");
			properties.put(XpertProperties.CSV_DATE_FORMAT, dateFormatComboBox.getSelectedItem().toString());
			properties.put(XpertProperties.CSV_VARIABLES, SwingUtil.concatenatedItems(variablesList, ';'));
			boolean saved = XpertProperties.writeProperties(properties);
			return saved;
		}
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == selectFolderButton) {
			File file = SwingUtil.chooseFile("Please choose a directory for CSV files", true);
			if (file != null) {
				csvFolderPathTextField.setText(file.getAbsolutePath());
			}
		} else if (e.getSource() == selectAllButton) {
			variablesList.setSelectionInterval(0, variablesList.getModel().getSize() - 1);
		} else if (e.getSource() == selectNoneButton) {
			variablesList.setSelectedIndices(new int[] {});
		} else if (e.getSource() == tryButton) {
			tryConfiguration();
		} else if (e.getSource() == saveButton) {
			if (saveConfiguration()) {
				JOptionPane.showMessageDialog(new JFrame(),
				    "CSV Configurations have been saved successfully. Closing this dialog.", "Configuration saved!",
				    JOptionPane.INFORMATION_MESSAGE);
				dispose();
			}
		}
	}
}
