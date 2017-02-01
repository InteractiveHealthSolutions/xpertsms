package com.ihsinformatics.xpertsmsweb.client;

import java.util.Date;
import java.util.Iterator;

//import com.ihsinformatics.xpertsmsweb.shared.RegexUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.ihsinformatics.xpertsmsweb.shared.AccessType;
import com.ihsinformatics.xpertsmsweb.shared.CustomMessage;
import com.ihsinformatics.xpertsmsweb.shared.ErrorType;
import com.ihsinformatics.xpertsmsweb.shared.InfoType;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.model.MessageSettings;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class MessageSettingsComposite extends Composite implements IForm,
		ClickHandler, ChangeHandler, ValueChangeHandler<Boolean> {
	private static ServerServiceAsync service = GWT.create(ServerService.class);
	private static LoadingWidget loading = new LoadingWidget();
	private static final String menuName = "SETUP";
	private UserRightsUtil rights = new UserRightsUtil();
	private boolean valid;
	private MessageSettings current;

	private VerticalPanel verticalPanel = new VerticalPanel();

	private FlexTable recepientsFlexTable = new FlexTable();

	private FlexTable contentFlexTable = new FlexTable();

	private FlexTable flexTable = new FlexTable();

	private FlexTable rightFlexTable = new FlexTable();

	private Grid grid = new Grid(1, 2);

	private Label lblSelectRecepientsOf = new Label(
			"Select recepients of Message alerts");

	private Label lblSelectMessageContents = new Label(
			"Select Message contents");

	private Label lblSelectwhenTo = new Label("Select \"When\" to send Alerts");

	private Label lblProgramNumber = new Label("Program Number:");

	private Label lblOtherNumber = new Label("Program Manager's Number:");

	private TextBox programNumberTextBox = new TextBox();

	private TextBox managerNumberTextBox = new TextBox();

	private CheckBox sendToPatientCheckBox = new CheckBox();

	private CheckBox sendToCenterCheckBox = new CheckBox();

	private CheckBox sendToReferenceLocationCheckBox = new CheckBox();

	private CheckBox sendToManagerCheckBox = new CheckBox(
			"Send Message to Program Manager");

	private CheckBox patientIdCheckBox = new CheckBox("Patient ID");

	private CheckBox sampleIdCheckBox = new CheckBox("Sample ID");

	private CheckBox mtbResultCheckBox = new CheckBox("MTB Result");

	private CheckBox rifResultCheckBox = new CheckBox("Rif Result");

	private CheckBox operatorIdCheckBox = new CheckBox("Operator ID");

	private CheckBox locationIdCheckBox = new CheckBox("Location ID");

	private CheckBox moduleIdCheckBox = new CheckBox("Module ID");

	private CheckBox cartridgeIdCheckBox = new CheckBox("Cartridge ID");

	private CheckBox testDateCheckBox = new CheckBox("Test Date");

	private CheckBox sendAlertsOnErrorCheckBox = new CheckBox(
			"Send Alerts on ERROR");

	private ListBox eventComboBox = new ListBox();

	private Button saveButton = new Button("Save");

	private Button closeButton = new Button("Close");

	public MessageSettingsComposite() {
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 0, verticalPanel);
		verticalPanel.setWidth("350px");
		verticalPanel.add(lblSelectRecepientsOf);
		verticalPanel.add(recepientsFlexTable);
		recepientsFlexTable.setWidth("100%");
		recepientsFlexTable.setWidget(0, 0, sendToPatientCheckBox);
		sendToPatientCheckBox.setText("Send Message to Patient");
		recepientsFlexTable.setWidget(1, 0, sendToCenterCheckBox);
		sendToCenterCheckBox.setText("Send Message to Center");
		recepientsFlexTable.setWidget(2, 0, sendToReferenceLocationCheckBox);
		sendToReferenceLocationCheckBox
				.setText("Send Message to Reference Location");
		recepientsFlexTable.setWidget(3, 0, sendToManagerCheckBox);
		verticalPanel.add(lblSelectMessageContents);
		verticalPanel.add(contentFlexTable);
		contentFlexTable.setWidth("100%");
		contentFlexTable.setWidget(0, 0, patientIdCheckBox);
		contentFlexTable.setWidget(0, 1, sampleIdCheckBox);
		contentFlexTable.setWidget(1, 0, mtbResultCheckBox);
		contentFlexTable.setWidget(1, 1, rifResultCheckBox);
		contentFlexTable.setWidget(2, 0, operatorIdCheckBox);
		contentFlexTable.setWidget(2, 1, locationIdCheckBox);
		contentFlexTable.setWidget(3, 0, moduleIdCheckBox);
		contentFlexTable.setWidget(3, 1, cartridgeIdCheckBox);
		contentFlexTable.setWidget(4, 0, testDateCheckBox);
		verticalPanel.add(lblSelectwhenTo);
		verticalPanel.add(eventComboBox);
		eventComboBox.addItem("ALERT ON ALL RESULTS");
		eventComboBox.addItem("ALERT ON MTB POSITIVE");
		eventComboBox.addItem("ALERT ON MEDIUM MTB");
		eventComboBox.addItem("ALERT ON HIGH MTB");
		eventComboBox.addItem("ALERT ON RIF POSITIVE");
		verticalPanel.add(sendAlertsOnErrorCheckBox);
		verticalPanel.add(rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblProgramNumber);
		programNumberTextBox.setVisibleLength(15);
		programNumberTextBox.setMaxLength(20);
		rightFlexTable.setWidget(0, 1, programNumberTextBox);
		rightFlexTable.setWidget(1, 0, lblOtherNumber);
		managerNumberTextBox.setVisibleLength(15);
		managerNumberTextBox.setMaxLength(20);
		rightFlexTable.setWidget(1, 1, managerNumberTextBox);
		rightFlexTable.getRowFormatter().setVisible(2, false);
		verticalPanel.add(grid);
		verticalPanel.setCellHorizontalAlignment(grid,
				HasHorizontalAlignment.ALIGN_CENTER);
		grid.setSize("100%", "");
		saveButton.setEnabled(false);
		grid.setWidget(0, 0, saveButton);
		saveButton.setWidth("100%");
		grid.setWidget(0, 1, closeButton);
		closeButton.setWidth("100%");

		saveButton.addClickHandler(this);
		closeButton.addClickHandler(this);
		sendToReferenceLocationCheckBox.addValueChangeHandler(this);
		sendToCenterCheckBox.addValueChangeHandler(this);
		sendToPatientCheckBox.addValueChangeHandler(this);

		setRights(menuName);
		fillData();

	}

	public void refreshList() {
		/* not implemented */
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load(boolean status) {
		flexTable.setVisible(!status);
		if (status)
			loading.show();
		else
			loading.hide();
	}

	public void clearControls(Widget w) {
		if (w instanceof Panel) {
			Iterator<Widget> iter = ((Panel) w).iterator();
			while (iter.hasNext())
				clearControls(iter.next());
		} else if (w instanceof TextBoxBase) {
			((TextBoxBase) w).setText("");
		} else if (w instanceof RichTextArea) {
			((RichTextArea) w).setText("");
		} else if (w instanceof ListBox) {
			((ListBox) w).setSelectedIndex(0);
		} else if (w instanceof DatePicker) {
			((DatePicker) w).setValue(new Date());
		}
	}

	public void setCurrent() {
		current = new MessageSettings();
		String event = XpertSmsWebClient.get(eventComboBox);
		current.setSendToPatient(sendToPatientCheckBox.getValue());
		current.setSendToCenter(sendToCenterCheckBox.getValue());
		current.setSendToReferenceLocation(sendToReferenceLocationCheckBox
				.getValue());
		current.setSendToManager(sendToManagerCheckBox.getValue());
		current.setProgramNumber(XpertSmsWebClient.get(programNumberTextBox));
		current.setManagerNumber(XpertSmsWebClient.get(managerNumberTextBox));
		current.setAlertOnAll(event.equals("ALERT ON ALL RESULTS"));
		current.setAlertOnAllMtb(event.equals("ALERT ON MTB POSITIVE"));
		current.setAlertOnError(sendAlertsOnErrorCheckBox.getValue());
		current.setAlertOnMtbHigh(event.equals("ALERT ON MEDIUM MTB"));
		current.setAlertOnMtbMedium(event.equals("ALERT ON HIGH MTB"));
		current.setAlertOnRif(event.equals("ALERT ON RIF POSITIVE"));
		current.setAttachPatientId(patientIdCheckBox.getValue());
		current.setAttachSampleId(sampleIdCheckBox.getValue());
		current.setAttachMtb(mtbResultCheckBox.getValue());
		current.setAttachRif(rifResultCheckBox.getValue());
		current.setAttachLocationId(locationIdCheckBox.getValue());
		current.setAttachOperatorId(operatorIdCheckBox.getValue());
		current.setAttachCartridgeId(cartridgeIdCheckBox.getValue());
		current.setAttachModuleId(moduleIdCheckBox.getValue());
		current.setAttachTestDate(testDateCheckBox.getValue());
	}

	@Override
	public void fillData() {
		try {
			service.findMessageSettings(new AsyncCallback<MessageSettings>() {
				@Override
				public void onSuccess(MessageSettings result) {
					current = result;
					sendToPatientCheckBox.setValue(current.getSendToPatient());
					sendToCenterCheckBox.setValue(current.getSendToCenter());
					sendToReferenceLocationCheckBox.setValue(current
							.getSendToReferenceLocation());
					sendToManagerCheckBox.setValue(current.getSendToManager());
					programNumberTextBox.setValue(current.getProgramNumber());
					managerNumberTextBox.setValue(current.getManagerNumber());
					if (current.getAlertOnAll())
						eventComboBox.setSelectedIndex(0);
					if (current.getAlertOnAllMtb())
						eventComboBox.setSelectedIndex(1);
					if (current.getAlertOnMtbMedium())
						eventComboBox.setSelectedIndex(2);
					if (current.getAlertOnMtbHigh())
						eventComboBox.setSelectedIndex(3);
					if (current.getAlertOnRif())
						eventComboBox.setSelectedIndex(4);
					sendAlertsOnErrorCheckBox.setValue(current
							.getAlertOnError());
					patientIdCheckBox.setValue(current.getAttachPatientId());
					sampleIdCheckBox.setValue(current.getAttachSampleId());
					mtbResultCheckBox.setValue(current.getAttachMtb());
					rifResultCheckBox.setValue(current.getAttachRif());
					locationIdCheckBox.setValue(current.getAttachLocationId());
					operatorIdCheckBox.setValue(current.getAttachOperatorId());
					cartridgeIdCheckBox.setValue(current.getAttachCartridgeId());
					moduleIdCheckBox.setValue(current.getAttachModuleId());
					testDateCheckBox.setValue(current.getAttachTestDate());
					load(false);
				}

				@Override
				public void onFailure(Throwable caught) {
					load(false);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			load(false);
		}
	}

	@Override
	public void clearUp() {
		clearControls(flexTable);
	}

	@Override
	public boolean validate() {
		final StringBuilder errorMessage = new StringBuilder();
		valid = true;
		if (sendToManagerCheckBox.getValue()
				&& XpertSmsWebClient.get(managerNumberTextBox).equals("")) {
			errorMessage.append("Please enter a cell number for Manager");
			valid = false;
		}
		if (!valid) {
			Window.alert(errorMessage.toString());
			load(false);
		}
		return valid;
	}

	@Override
	public void saveData() {

		if (validate()) {
			try {
				setCurrent();
				service.saveMessageSettings(current,
						new AsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if (result == null)
									Window.alert(CustomMessage
											.getErrorMessage(ErrorType.UPDATE_ERROR));
								else if (result) {
									Window.alert(CustomMessage
											.getInfoMessage(InfoType.UPDATED));
									clearUp();
									fillData();
								} else
									Window.alert(CustomMessage
											.getErrorMessage(ErrorType.UPDATE_ERROR));
								load(false);
							}

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(CustomMessage
										.getErrorMessage(ErrorType.UPDATE_ERROR));
								load(false);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				load(false);
			}

		}
	}

	@Override
	public void updateData() {
		// Not implemented
	}

	@Override
	public void deleteData() {
		// Not implemented
	}

	@Override
	public void setRights(String menuName) {
		try {
			load(true);
			service.getUserRgihts(XSMS.getCurrentUser(), menuName,
					new AsyncCallback<Boolean[]>() {
						@Override
						public void onSuccess(Boolean[] result) {
							final Boolean[] userRights = result;
							try {
								service.findUser(XSMS.getCurrentUser(),
										new AsyncCallback<Users>() {
											@Override
											public void onSuccess(Users result) {
												rights.setRoleRights(
														result.getRole(),
														userRights);
												saveButton.setEnabled(rights
														.getAccess(AccessType.UPDATE));
												load(false);
											}

											@Override
											public void onFailure(
													Throwable caught) {
												load(false);
											}
										});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							load(false);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		load(true);
		if (sender == saveButton) {
			saveData();
		} else if (sender == closeButton) {
			MainMenuComposite.clear();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		// Not implemented
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		Widget sender = (Widget) event.getSource();
		load(false);
		if (sender == sendToManagerCheckBox) {
			managerNumberTextBox.setEnabled(sendToManagerCheckBox.getValue());
		}
	}
}
