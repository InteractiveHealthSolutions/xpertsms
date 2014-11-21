/**
 * 
 */

package com.ihsinformatics.xpertsms.ui;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.GroupLayout;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class SmsConfigurationPanel extends javax.swing.JPanel
{
	private static final long		serialVersionUID	= 9014880754492532576L;
	private javax.swing.JLabel		addressLabel;
	private javax.swing.JTextField	addressTextField;
	private javax.swing.JPanel		bottomDialogPanel;
	private javax.swing.JComboBox	dateFormatComboBox;
	private javax.swing.JLabel		dateTimeFormatLabel;
	private javax.swing.JLabel		phoneLabel;
	private javax.swing.JTextField	phoneTextField;
	private javax.swing.JTextField	portTextField;
	private javax.swing.JButton		saveButton;
	private javax.swing.JCheckBox	sslCheckBox;
	private javax.swing.JPanel		topDialogPanel;
	private javax.swing.JButton		tryButton;
	private javax.swing.JCheckBox	usePortCheckBox;
	private javax.swing.JLabel		variablesLabel;
	private javax.swing.JList		variablesList;
	private javax.swing.JScrollPane	variablesScrollPanel;

	/** Creates new form SmsConfigurationPanel */
	public SmsConfigurationPanel ()
	{
		initComponents ();
	}

	private void initComponents ()
	{

		topDialogPanel = new javax.swing.JPanel ();
		addressLabel = new javax.swing.JLabel ();
		addressTextField = new javax.swing.JTextField ();
		usePortCheckBox = new javax.swing.JCheckBox ();
		portTextField = new javax.swing.JTextField ();
		phoneLabel = new javax.swing.JLabel ();
		phoneTextField = new javax.swing.JTextField ();
		dateTimeFormatLabel = new javax.swing.JLabel ();
		dateFormatComboBox = new javax.swing.JComboBox ();
		sslCheckBox = new javax.swing.JCheckBox ();
		variablesLabel = new javax.swing.JLabel ();
		variablesScrollPanel = new javax.swing.JScrollPane ();
		variablesList = new javax.swing.JList ();
		bottomDialogPanel = new javax.swing.JPanel ();
		tryButton = new javax.swing.JButton ();
		saveButton = new javax.swing.JButton ();

		addressLabel.setText ("SMS Server Address:");

		addressTextField.setText ("127.0.0.1/smstarseelweb/smstarseel");
		addressTextField.setToolTipText ("Must be a valid IP address, like \"127.0.0.1\" or a URL, like \"dev.gxalert.com/api/result\". Please do not write http(s)://");
		addressTextField.setCursor (new java.awt.Cursor (java.awt.Cursor.TEXT_CURSOR));
		addressTextField.setName ("smsTarseelAddress"); // NOI18N

		usePortCheckBox.setSelected (true);
		usePortCheckBox.setText ("Use Port:");

		portTextField.setHorizontalAlignment (javax.swing.JTextField.CENTER);
		portTextField.setText ("8080");
		portTextField.setToolTipText ("Port number to connect to the server. If you are not sure what this is, try 8080 or just 80, or ask your Network Admin");

		phoneLabel.setText ("Admin's Phone No.:");

		phoneTextField.setText ("+923452345345");
		phoneTextField.setToolTipText ("Here goes fully qualified mobile number of the receiver of results from GeneXpert. Please start from country code.");

		dateTimeFormatLabel.setText ("Date/Time Format:");

		dateFormatComboBox.setModel (new javax.swing.DefaultComboBoxModel (new String[] {"yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy", "M/d/yy", "dd/MM/yyyy hh:mm:ssa",
				"dd/MM/yyyy", "d/M/yy", "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss", "yyyy-MM-dd'T'HH:mm:ss'Z'"}));
		dateFormatComboBox
				.setToolTipText ("Choose a format for date and time fields.\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\n[M/d/yy] is short USA format date, e.g. 10/23/14\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\n[d/M/yy] is short UK format date, e.g. 23/10/14\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\n\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");

		sslCheckBox.setText ("Use SSL/TLS Encryption");

		variablesLabel.setText ("Variables to Send:");

		variablesList.setModel (new javax.swing.AbstractListModel ()
		{
			private static final long	serialVersionUID	= 8509842608936560429L;
			String[]					strings				= {"sampleId", "patientId", "user", "testStartedOn", "testEndedOn", "messageSentOn", "reagentLotId", "cartridgeExpirationDate",
																	"cartridgeSerial", "instrumentSerial", "resultText", "computerName", "errorCode", "probeA", "probeB", "probeC", "probeD", "probeE",
																	"probeSpc"};

			public int getSize ()
			{
				return strings.length;
			}

			public Object getElementAt (int i)
			{
				return strings[i];
			}
		});
		variablesList
				.setToolTipText ("Select all the attributes to be sent via SMS. Press Ctrl key and click multiple variables. In order to avoid long messages, choose only the essential ones. The default selections send an SMS of approximately 190 characters");
		variablesList.setSelectedIndices (new int[] {0, 1, 2, 4, 8, 11, 12});
		variablesScrollPanel.setViewportView (variablesList);

		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout (topDialogPanel);
		topDialogPanelLayout.setHorizontalGroup(
			topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(topDialogPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(topDialogPanelLayout.createSequentialGroup()
							.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(addressLabel)
								.addComponent(phoneLabel)
								.addComponent(dateTimeFormatLabel))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(topDialogPanelLayout.createSequentialGroup()
									.addComponent(addressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(usePortCheckBox)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(phoneTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(topDialogPanelLayout.createSequentialGroup()
									.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.TRAILING, false)
										.addGroup(Alignment.LEADING, topDialogPanelLayout.createSequentialGroup()
											.addGap(1)
											.addComponent(variablesScrollPanel))
										.addComponent(dateFormatComboBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(sslCheckBox))))
						.addComponent(variablesLabel))
					.addContainerGap(23, Short.MAX_VALUE))
		);
		topDialogPanelLayout.setVerticalGroup(
			topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(topDialogPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(addressLabel)
						.addComponent(addressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(usePortCheckBox)
						.addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(phoneTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(phoneLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(dateTimeFormatLabel)
						.addComponent(dateFormatComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(sslCheckBox))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(topDialogPanelLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(variablesLabel)
						.addComponent(variablesScrollPanel, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		topDialogPanel.setLayout (topDialogPanelLayout);

		tryButton.setText ("Try..");
		tryButton.setToolTipText ("Check if the settings are correct");

		saveButton.setFont (new java.awt.Font ("Tahoma", 2, 12)); // NOI18N
		saveButton.setText ("Save");
		saveButton.setToolTipText ("Save settings to configuration file");

		javax.swing.GroupLayout bottomDialogPanelLayout = new javax.swing.GroupLayout (bottomDialogPanel);
		bottomDialogPanel.setLayout (bottomDialogPanelLayout);
		bottomDialogPanelLayout.setHorizontalGroup (bottomDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING).addGroup (
				bottomDialogPanelLayout.createSequentialGroup ().addContainerGap ().addComponent (tryButton).addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent (saveButton).addContainerGap (306, Short.MAX_VALUE)));
		bottomDialogPanelLayout.setVerticalGroup (bottomDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING).addGroup (
				bottomDialogPanelLayout
						.createSequentialGroup ()
						.addGroup (
								bottomDialogPanelLayout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent (tryButton, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
										.addComponent (saveButton, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)).addContainerGap ()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout (this);
		this.setLayout (layout);
		layout.setHorizontalGroup (layout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent (bottomDialogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent (topDialogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup (layout.createParallelGroup (javax.swing.GroupLayout.Alignment.LEADING).addGroup (
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup ().addComponent (topDialogPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap (javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent (bottomDialogPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
	}
}
