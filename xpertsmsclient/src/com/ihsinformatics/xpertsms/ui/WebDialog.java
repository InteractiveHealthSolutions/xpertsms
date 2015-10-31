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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.ihsinformatics.xpertsms.XpertProperties;
import com.ihsinformatics.xpertsms.util.RegexUtil;
import com.ihsinformatics.xpertsms.util.SwingUtil;

/**
 * GUI form to provide settings for Web application to send results to
 * 
 * @author owais.hussain@ihsinformatics.com
 */
public class WebDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -2732459485154603453L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JPanel topDialogPanel;
	
	private JLabel webAddressLabel;
	
	private JLabel dataFormatLabel;
	
	private JLabel authenticationLabel;
	
	private JLabel dateTimeFormatLabel;
	
	private JLabel usernameLabel;
	
	private JLabel passwordLabel;
	
	private JTextField webAddressTextField;
	
	private JTextField usernameTextField;
	
	private JPasswordField passwordField;
	
	private JCheckBox sslCheckBox;
	
	private JComboBox<?> dataFormatComboBox;
	
	private JComboBox<?> dateTimeFormatComboBox;
	
	private JComboBox<?> authenticationComboBox;
	
	private JRadioButton utf8RadioButton;
	
	private JRadioButton utf16RadioButton;
	
	private JButton saveButton;
	
	private JButton tryButton;
	
	public WebDialog() {
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void initComponents() {
		topDialogPanel = new javax.swing.JPanel();
		webAddressLabel = new JLabel("* Web URI/Servlet:");
		dataFormatLabel = new JLabel("Data Format:");
		authenticationLabel = new JLabel("Authentication:");
		webAddressTextField = new JTextField();
		dataFormatComboBox = new JComboBox();
		dataFormatComboBox.setEnabled(false);
		dateTimeFormatComboBox = new JComboBox();
		authenticationComboBox = new JComboBox();
		authenticationComboBox.setEnabled(false);
		utf8RadioButton = new JRadioButton("UTF-8");
		utf16RadioButton = new JRadioButton("UTF-16");
		tryButton = new JButton();
		saveButton = new JButton();
		
		setName("webDialog");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setMinimumSize(new Dimension(450, 250));
		setTitle("Web Configuration");
		setPreferredSize(new Dimension(400, 300));
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(topDialogPanel, BorderLayout.CENTER);
		tryButton.setText("Try..");
		tryButton.setToolTipText("Check if the application is able to connect to the database using given credentials.");
		saveButton.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
		saveButton.setText("Save");
		saveButton.setToolTipText("Creates database tables and save settings to configuration file.");
		webAddressTextField
		        .setToolTipText("XpertSMS URI to submit requests to, without \"http(s)://\" part. For example \"localhost:8080/xpertsmsweb/xpertsmsweb.jsp");
		webAddressTextField.setText("127.0.0.1:8080/xpertsmsweb/xpertsmsweb.jsp");
		webAddressTextField.setName("projectName");
		dateTimeFormatComboBox.setModel(new DefaultComboBoxModel(new String[] { "yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss",
		        "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy", "M/d/yy", "dd/MM/yyyy hh:mm:ssa", "dd/MM/yyyy", "d/M/yy",
		        "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss", "yyyy-MM-dd'T'HH:mm:ss'Z'" }));
		dateTimeFormatComboBox
		        .setToolTipText("Choose a format for date and time fields.\n[yyyy-MM-dd'T'hh:mm:ss'Z'] is ISO-8601 standard date format, 2014-10-23T12:00:00Z\n[yyyy-MM-dd hh:mm:ss] is typical SQL format, e.g. 2014-10-23 14:00:00\n[yyyy-MM-dd] is date-only variant of SQL format, e.g. 2014-10-23\n[MM/dd/yyyy hh:mm:ssa] is standard USA date format, e.g. 10/23/2014 2:00:00pm\n[MM/dd/yyyy] is date-only variant of USA format, e.g. 10/23/2014\n[M/d/yy] is short USA format date, e.g. 10/23/14\n[dd/MM/yyyy hh:mm:ssa] is standard UK date format, e.g. 23/10/2014 2:00:00pm\n[dd/MM/yyyy] is date-only variant of UK format, e.g. 23/10/2014\n[d/M/yy] is short UK format date, e.g. 23/10/14\n[EEE, d MMM yyyy HH:mm:ss] is usually for printing, e.g. Thu, 23 Oct 2014 12:00:00\n[yyyyMMddhhmmss] is concatenated date/time, e.g. 20141023140000\n\nTip: longer date/time format will increase the length of your message. Choose shorter format whenever possible");
		dataFormatComboBox.setModel(new DefaultComboBoxModel(new String[] { "Plain text", "Json", "XML" }));
		authenticationComboBox.setModel(new DefaultComboBoxModel(new String[] { "As parameter", "As header", "None" }));
		dateTimeFormatLabel = new JLabel("Date/Time Format:");
		utf8RadioButton.setSelected(true);
		JLabel encodingLabel = new JLabel("Encoding:");
		
		usernameLabel = new JLabel();
		usernameLabel.setText("* Username:");
		
		usernameTextField = new JTextField();
		usernameTextField.setToolTipText("Here goes your username defined in the XpertSMS web app");
		usernameTextField.setText("admin");
		usernameTextField.setName("username");
		
		passwordLabel = new JLabel();
		passwordLabel.setText("* Password:");
		
		passwordField = new JPasswordField();
		passwordField.setToolTipText("Respective password for XpertSMS web username");
		passwordField.setName("password");
		
		sslCheckBox = new JCheckBox();
		sslCheckBox.setToolTipText("SSL/TLS encryption should be enabled if the server supports it.");
		sslCheckBox.setText("Use SSL/TLS");
		javax.swing.GroupLayout topDialogPanelLayout = new javax.swing.GroupLayout(topDialogPanel);
		topDialogPanelLayout
		        .setHorizontalGroup(topDialogPanelLayout
		                .createParallelGroup(Alignment.TRAILING)
		                .addGroup(
		                    Alignment.LEADING,
		                    topDialogPanelLayout
		                            .createSequentialGroup()
		                            .addContainerGap()
		                            .addGroup(
		                                topDialogPanelLayout
		                                        .createParallelGroup(Alignment.TRAILING, false)
		                                        .addComponent(usernameLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
		                                            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                                        .addComponent(dateTimeFormatLabel, Alignment.LEADING)
		                                        .addComponent(encodingLabel, Alignment.LEADING)
		                                        .addComponent(webAddressLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
		                                            115, Short.MAX_VALUE)
		                                        .addComponent(dataFormatLabel, Alignment.LEADING)
		                                        .addComponent(authenticationLabel, Alignment.LEADING)
		                                        .addComponent(passwordLabel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE,
		                                            82, GroupLayout.PREFERRED_SIZE))
		                            .addGroup(
		                                topDialogPanelLayout
		                                        .createParallelGroup(Alignment.LEADING)
		                                        .addGroup(
		                                            topDialogPanelLayout
		                                                    .createSequentialGroup()
		                                                    .addGap(17)
		                                                    .addGroup(
		                                                        topDialogPanelLayout
		                                                                .createParallelGroup(Alignment.LEADING)
		                                                                .addGroup(
		                                                                    topDialogPanelLayout
		                                                                            .createSequentialGroup()
		                                                                            .addComponent(dataFormatComboBox,
		                                                                                GroupLayout.PREFERRED_SIZE,
		                                                                                GroupLayout.DEFAULT_SIZE,
		                                                                                GroupLayout.PREFERRED_SIZE)
		                                                                            .addPreferredGap(
		                                                                                ComponentPlacement.RELATED)
		                                                                            .addComponent(sslCheckBox,
		                                                                                GroupLayout.DEFAULT_SIZE, 130,
		                                                                                Short.MAX_VALUE))
		                                                                .addGroup(
		                                                                    topDialogPanelLayout
		                                                                            .createParallelGroup(Alignment.LEADING,
		                                                                                false)
		                                                                            .addComponent(usernameTextField)
		                                                                            .addGroup(
		                                                                                topDialogPanelLayout
		                                                                                        .createSequentialGroup()
		                                                                                        .addComponent(
		                                                                                            utf8RadioButton)
		                                                                                        .addPreferredGap(
		                                                                                            ComponentPlacement.RELATED)
		                                                                                        .addComponent(
		                                                                                            utf16RadioButton))
		                                                                            .addComponent(passwordField)
		                                                                            .addGroup(
		                                                                                topDialogPanelLayout
		                                                                                        .createSequentialGroup()
		                                                                                        .addComponent(tryButton)
		                                                                                        .addPreferredGap(
		                                                                                            ComponentPlacement.RELATED)
		                                                                                        .addComponent(saveButton)))
		                                                                .addGroup(
		                                                                    topDialogPanelLayout
		                                                                            .createParallelGroup(Alignment.TRAILING,
		                                                                                false)
		                                                                            .addComponent(dateTimeFormatComboBox,
		                                                                                Alignment.LEADING, 0, 0,
		                                                                                Short.MAX_VALUE)
		                                                                            .addComponent(authenticationComboBox,
		                                                                                Alignment.LEADING, 0,
		                                                                                GroupLayout.DEFAULT_SIZE,
		                                                                                Short.MAX_VALUE))))
		                                        .addGroup(
		                                            topDialogPanelLayout
		                                                    .createSequentialGroup()
		                                                    .addGap(18)
		                                                    .addComponent(webAddressTextField, GroupLayout.PREFERRED_SIZE,
		                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
		                            .addGap(37)));
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
		                            GroupLayout.PREFERRED_SIZE).addComponent(sslCheckBox))
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
		            .addPreferredGap(ComponentPlacement.UNRELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(usernameLabel)
		                        .addComponent(usernameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout
		                        .createParallelGroup(Alignment.BASELINE)
		                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		                            GroupLayout.PREFERRED_SIZE).addComponent(passwordLabel))
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(
		                topDialogPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(saveButton)
		                        .addComponent(tryButton)).addGap(256)));
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
		String webAddress = XpertProperties.getProperty(XpertProperties.WEB_APP_STRING);
		String useSsl = XpertProperties.getProperty(XpertProperties.WEB_SSL_ENCRYPTION);
		String dataFormat = XpertProperties.getProperty(XpertProperties.WEB_DATA_FORMAT);
		String authentication = XpertProperties.getProperty(XpertProperties.WEB_AUTHENTICATION);
		String dateFormat = XpertProperties.getProperty(XpertProperties.WEB_DATE_FORMAT);
		String username = XpertProperties.getProperty(XpertProperties.WEB_USERNAME);
		String password = XpertProperties.getProperty(XpertProperties.WEB_PASSWORD);
		if (!"".equals(webAddress)) {
			webAddressTextField.setText(webAddress);
		}
		if (!"".equals(useSsl)) {
			sslCheckBox.setSelected(useSsl.equals("YES"));
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
		if (!"".equals(username)) {
			usernameTextField.setText(username);
		}
		if (!"".equals(password)) {
			passwordField.setText(password);
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
		if ("".equals(SwingUtil.get(webAddressTextField))) {
			error.append("Address of web server must be provided.\n");
			valid = false;
		} else if (SwingUtil.get(webAddressTextField).startsWith("http")) {
			error.append("Address of the web server should not contain \"http(s)://\".\n");
			valid = false;
		}
		if (SwingUtil.get(usernameTextField).equals("")) {
			error.append("XpertSMS Username must be provided.\n");
			valid = false;
		} else if (!RegexUtil.isValidUsername(SwingUtil.get(usernameTextField))) {
			error.append("Username can only be Alphanumeric.\n");
			valid = false;
		}
		if (String.valueOf(passwordField.getPassword()).equals("")) {
			error.append("XpertSMS user's respective password must be provided.\n");
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
		String successMessage = "Connection to XpertSMS Web was successful. Don't worry, I didn't save anything on the server. Save settings now?";
		String failureMessage = "Unable to process request to XpertSMS Web. This could be because of wrong address URI, unsupported data format or invalid username/password.";
		if (validateData()) {
			String prefix = sslCheckBox.isSelected() ? "https://" : "http://";
			String username = SwingUtil.get(usernameTextField);
			String password = String.valueOf(passwordField.getPassword());
			String request = "type=astmresult" + "&username=" + username + "&password=" + password;
			String response = "";
			try {
				HttpURLConnection httpConnection = null;
				OutputStream outputStream = null;
				int responseCode = 0;
				URL url = new URL(prefix + SwingUtil.get(webAddressTextField));
				httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				httpConnection.setRequestProperty("Content-Language", "en-US");
				httpConnection.setDoOutput(true);
				outputStream = httpConnection.getOutputStream();
				outputStream.write(request.getBytes());
				outputStream.flush();
				responseCode = httpConnection.getResponseCode();
				if (responseCode != HttpURLConnection.HTTP_OK) {
					throw new Exception("Response code: " + responseCode);
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
				String line = "";
				response = "";
				while ((line = br.readLine()) != null) {
					response += line;
				}
				if (outputStream != null) {
					outputStream.close();
					if (httpConnection != null) {
						httpConnection.disconnect();
					}
				}
				int selected = JOptionPane.showConfirmDialog(new JFrame(), successMessage, "It works!",
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
			catch (MalformedURLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage
				        + "\nI suspect wrong address or connectivity problem" + "\nServer says: " + e.getMessage(),
				    "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
			catch (SecurityException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), failureMessage + "\nI suspect wrong username and/or password"
				        + "\nServer says: " + e.getMessage(), "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(
				    new JFrame(),
				    "The connection looks okay, but there was a problem in reading the response" + "\nServer says: "
				            + e.getMessage(), "Nope! Something is wrong", JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane
				        .showMessageDialog(
				            new JFrame(),
				            failureMessage
				                    + "\nI have no clue of the issue. If you see a response code, try Googling it or report to the Engineers responsible for such a buggy program"
				                    + "\nServer says: " + e.getMessage(), "Nope! Something is wrong",
				            JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public boolean saveConfiguration() {
		if (validateData()) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(XpertProperties.WEB_APP_STRING, SwingUtil.get(webAddressTextField));
			properties.put(XpertProperties.WEB_SSL_ENCRYPTION, sslCheckBox.isSelected() ? "YES" : "NO");
			properties.put(XpertProperties.WEB_DATA_FORMAT, SwingUtil.get(dataFormatComboBox));
			properties.put(XpertProperties.WEB_AUTHENTICATION, SwingUtil.get(authenticationComboBox));
			properties.put(XpertProperties.WEB_DATE_FORMAT, SwingUtil.get(dateTimeFormatComboBox));
			properties.put(XpertProperties.WEB_USERNAME, SwingUtil.get(usernameTextField));
			properties.put(XpertProperties.WEB_PASSWORD, String.valueOf(passwordField.getPassword()));
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
			saveConfiguration();
		}
	}
}
