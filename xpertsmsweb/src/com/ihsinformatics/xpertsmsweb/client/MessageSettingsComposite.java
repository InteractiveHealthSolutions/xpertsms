package com.ihsinformatics.xpertsmsweb.client;

import java.util.Date;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.ihsinformatics.xpertsmsweb.shared.AccessType;
import com.ihsinformatics.xpertsmsweb.shared.CustomMessage;
import com.ihsinformatics.xpertsmsweb.shared.ErrorType;
import com.ihsinformatics.xpertsmsweb.shared.InfoType;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.model.Location;
import com.ihsinformatics.xpertsmsweb.shared.model.MessageSettings;
import com.ihsinformatics.xpertsmsweb.shared.model.OtherMessageSetting;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;
//import com.ihsinformatics.xpertsmsweb.shared.RegexUtil;
import com.google.gwt.core.client.GWT;

public class MessageSettingsComposite extends Composite implements IForm,
	ClickHandler, ChangeHandler, ValueChangeHandler<Boolean> {
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static LoadingWidget loading = new LoadingWidget();
    private static final String menuName = "SETUP";

    private UserRightsUtil rights = new UserRightsUtil();
    private boolean valid;
    private MessageSettings current;

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable leftFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private Grid grid = new Grid(1, 3);
    private FlexTable leftRecipientFlexTable = new FlexTable();
    private FlexTable rightRecipientFlexTable = new FlexTable();

    private CheckBox sendToPatientCheckBox = new CheckBox(/* "Send Message to Patient" */);
    // private TextArea patientTextTextArea = new TextArea();
    private CheckBox sendToProviderCheckBox = new CheckBox(/* "Send Message to Provider" */);
    // private TextArea providerTextTextArea = new TextArea();
    private CheckBox sendToProgramCheckBox = new CheckBox(/* "Send Message to Program" */);
    // private TextArea programTextTextArea = new TextArea();
    private TextBox programNumberTextBox = new TextBox();
    private CheckBox sendToOtherCheckBox = new CheckBox(/* "Send Message to Other Number" */);
    // private TextArea otherTextTextArea = new TextArea();
    private TextBox otherNumberTextBox = new TextBox();

    private Anchor advanceMessageSettingAnchor = new Anchor("Advance Settings");

    // private Button
    private Button saveButton = new Button("Save");
    private Button deleteButton = new Button("Delete");
    private Button closeButton = new Button("Close");

    private Label lblMessageSettings = new Label("Message Settings");
    private Label lblSendToPatient = new Label("Send Message to Patient");
    // private Label lblTextForPatient = new Label("Patient SMS Text:");
    private Label lblSendToProvider = new Label("Send Message to Provider");
    // private Label lblTextForProvider = new Label("Provider SMS Text:");
    private Label lblSendToProgram = new Label("Send Message to Program");
    // private Label lblTextForProgram = new Label("Program SMS Text:");
    private Label lblProgramNumber = new Label("Program Cell Number:");
    private Label lblSendToOther = new Label("Send Message to Other Number");
    // private Label lblTextForOther = new Label("Other SMS Text:");
    private Label lblOtherNumber = new Label("Other Cell Number:");
    private Label lblCellNumber = new Label("Cell Number:");

    private Label districtLabel = new Label("District:");
    private Label healthFacilityLabel = new Label("Health Facility:");
    private Label recipientName = new Label("Recipient Name:");

    private ListBox alertRecipientListBox = new ListBox();

    private ListBox locationDistrictComboBox = new ListBox();
    private ListBox locationHealthFacilityComboBox = new ListBox();

    private TextBox nameTextBox = new TextBox();
    private TextBox cellNumberTextBox = new TextBox();

    private ToggleButton createButton = new ToggleButton("Create");
    private Button saveOtherButton = new Button("Save");
    private Button deleteOtherButton = new Button("Delete");

    private Grid otherGrid = new Grid(1, 3);

    public MessageSettingsComposite() {
	initWidget(flexTable);
	flexTable.setSize("80%", "100%");
	flexTable.setWidget(0, 1, topFlexTable);
	lblMessageSettings.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblMessageSettings);
	flexTable.setWidget(1, 0, leftFlexTable);

	flexTable.setWidget(1, 1, rightFlexTable);
	rightFlexTable.setSize("100%", "100%");

	rightFlexTable.setWidget(0, 0, sendToPatientCheckBox);
	rightFlexTable.setWidget(0, 1, lblSendToPatient);

	// rightFlexTable.setWidget(2, 0, lblSendToProvider);
	rightFlexTable.setWidget(1, 0, sendToProviderCheckBox);
	rightFlexTable.setWidget(1, 1, lblSendToProvider);

	// rightFlexTable.setWidget(4, 0, lblSendToProgram);
	rightFlexTable.setWidget(2, 0, sendToProgramCheckBox);
	rightFlexTable.setWidget(2, 1, lblSendToProgram);
	rightFlexTable.setWidget(3, 0, lblProgramNumber);
	rightFlexTable.setWidget(3, 1, programNumberTextBox);

	// rightFlexTable.setWidget(7, 0, lblSendToOther);
	rightFlexTable.setWidget(4, 0, sendToOtherCheckBox);
	rightFlexTable.setWidget(4, 1, lblSendToOther);
	rightFlexTable.setWidget(5, 0, lblOtherNumber);
	rightFlexTable.setWidget(5, 1, otherNumberTextBox);

	rightFlexTable.setWidget(9, 0, advanceMessageSettingAnchor);

	rightFlexTable.setWidget(10, 0, leftRecipientFlexTable);
	alertRecipientListBox.setVisibleItemCount(10);
	leftRecipientFlexTable.setWidget(0, 0, alertRecipientListBox);

	rightFlexTable.setWidget(10, 1, rightRecipientFlexTable);
	rightRecipientFlexTable.setWidget(0, 0, recipientName);
	rightRecipientFlexTable.setWidget(0, 1, nameTextBox);
	rightRecipientFlexTable.setWidget(1, 0, districtLabel);
	rightRecipientFlexTable.setWidget(1, 1, locationDistrictComboBox);
	rightRecipientFlexTable.setWidget(2, 0, healthFacilityLabel);
	rightRecipientFlexTable.setWidget(2, 1, locationHealthFacilityComboBox);
	rightRecipientFlexTable.setWidget(3, 0, lblCellNumber);
	rightRecipientFlexTable.setWidget(3, 1, cellNumberTextBox);

	rightRecipientFlexTable.setWidget(4, 1, otherGrid);
	otherGrid.setWidget(0, 0, createButton);
	otherGrid.setWidget(0, 1, saveOtherButton);
	otherGrid.setWidget(0, 2, deleteOtherButton);

	rightFlexTable.setWidget(6, 1, grid);
	grid.setSize("100%", "100%");
	saveButton.setEnabled(false);
	grid.setWidget(0, 0, saveButton);
	deleteButton.setEnabled(false);
	// grid.setWidget(0, 1, deleteButton);
	grid.setWidget(0, 1, closeButton);
	flexTable.getRowFormatter().setVerticalAlign(1,
		HasVerticalAlignment.ALIGN_TOP);

	rightFlexTable.getRowFormatter().setVisible(10, false);

	createButton.addClickHandler(this);
	saveButton.addClickHandler(this);
	deleteButton.addClickHandler(this);
	closeButton.addClickHandler(this);
	saveOtherButton.addClickHandler(this);
	deleteOtherButton.addClickHandler(this);
	advanceMessageSettingAnchor.addClickHandler(this);
	locationDistrictComboBox.addChangeHandler(this);

	sendToPatientCheckBox.addValueChangeHandler(this);
	sendToProviderCheckBox.addValueChangeHandler(this);
	sendToProgramCheckBox.addValueChangeHandler(this);
	sendToOtherCheckBox.addValueChangeHandler(this);
	alertRecipientListBox.addChangeHandler(this);

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
	current.setSendToPatient(sendToPatientCheckBox.getValue());
	// current.setPatientText(XpertSmsWebClient.get(patientTextTextArea));

	current.setSendToProvider(sendToProviderCheckBox.getValue());
	// current.setProviderText(XpertSmsWebClient.get(providerTextTextArea));

	current.setSendToProgram(sendToProgramCheckBox.getValue());
	// current.setProgramText(XpertSmsWebClient.get(programTextTextArea));
	current.setProgramNumber(XpertSmsWebClient.get(programNumberTextBox));

	current.setSendToOther(sendToOtherCheckBox.getValue());
	// current.setOtherText(XpertSmsWebClient.get(otherTextTextArea));
	current.setOtherNumber(XpertSmsWebClient.get(otherNumberTextBox));

    }

    @Override
    public void fillData() {
	try {
	    service.findMessageSettings(new AsyncCallback<MessageSettings>() {
		@Override
		public void onSuccess(MessageSettings result) {
		    current = result;

		    // patient
		    sendToPatientCheckBox.setValue(current.getSendToPatient());
		    // patientTextTextArea.setText(current.getPatientText());
		    // patientTextTextArea.setEnabled(current.getSendToPatient());

		    // provider
		    sendToProviderCheckBox.setValue(current.getSendToProvider());
		    // providerTextTextArea.setText(current.getProviderText());
		    // providerTextTextArea.setEnabled(current.getSendToProvider());

		    // program
		    sendToProgramCheckBox.setValue(current.getSendToProgram());
		    // programTextTextArea.setText(current.getProgramText());
		    programNumberTextBox.setText(current.getProgramNumber());
		    // programTextTextArea.setEnabled(current.getSendToProgram());
		    programNumberTextBox.setEnabled(current.getSendToProgram());

		    // other
		    sendToOtherCheckBox.setValue(current.getSendToOther());
		    // otherTextTextArea.setText(current.getOtherText());
		    otherNumberTextBox.setText(current.getOtherNumber());
		    // otherTextTextArea.setEnabled(current.getSendToOther());
		    otherNumberTextBox.setEnabled(current.getSendToOther());

		    try {
			String locationDistrictType = "DISTRICT";
			service.findLocationsByType(locationDistrictType,
				new AsyncCallback<Location[]>() {

				    @Override
				    public void onFailure(Throwable caught) {
					load(false);
				    }

				    @Override
				    public void onSuccess(Location[] result) {
					locationDistrictComboBox.clear();
					for (Location s : result)
					    locationDistrictComboBox.addItem(
						    s.getLocationName(),
						    s.getLocationId());
					load(false);

					service.findFacilitiesByDistrictId(
						XpertSmsWebClient
							.get(locationDistrictComboBox),
						new AsyncCallback<Location[]>() {
						    @Override
						    public void onSuccess(
							    Location[] result) {
							locationHealthFacilityComboBox
								.clear();
							for (Location s : result) {
							    locationHealthFacilityComboBox.addItem(
								    s.getLocationName(),
								    s.getLocationId());
							}

							load(false);
						    }

						    @Override
						    public void onFailure(
							    Throwable caught) {
							caught.printStackTrace();
							load(false);
						    }
						});

				    }

				});

		    } catch (Exception e) {
			e.printStackTrace();
			load(false);
		    }

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
	clearControls(rightRecipientFlexTable);
	service.findFacilitiesByDistrictId(
		XpertSmsWebClient.get(locationDistrictComboBox),
		new AsyncCallback<Location[]>() {
		    @Override
		    public void onSuccess(Location[] result) {
			locationHealthFacilityComboBox.clear();
			for (Location s : result) {
			    locationHealthFacilityComboBox.addItem(
				    s.getLocationName(), s.getLocationId());
			}

			load(false);
		    }

		    @Override
		    public void onFailure(Throwable caught) {
			caught.printStackTrace();
			load(false);
		    }
		});
    }

    @Override
    public boolean validate() {
	final StringBuilder errorMessage = new StringBuilder();
	valid = true;
	if (sendToProgramCheckBox.getValue()
		&& XpertSmsWebClient.get(programNumberTextBox).equals("")) {

	    errorMessage.append("Please enter a Program cell number");
	    valid = false;
	}

	if (sendToOtherCheckBox.getValue()
		&& XpertSmsWebClient.get(otherNumberTextBox).equals("")) {

	    errorMessage.append("Please enter a cell number for Other");
	    valid = false;
	}
	/*
	 * Validate mandatory fields if
	 * (XpertSmsWebClient.get(patientIdTextBox).equals("") ||
	 * XpertSmsWebClient.get(mrNoTextBox).equals("") ||
	 * XpertSmsWebClient.get(irsTextBox).equals("")) {
	 * errorMessage.append(CustomMessage
	 * .getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n"); valid = false;
	 * } Validate data-type rules if
	 * (!RegexUtil.isNumeric(XpertSmsWebClient.get(irsTextBox), false) ||
	 * XpertSmsWebClient.get(patientIdTextBox).length() < 11) {
	 * errorMessage.
	 * append(CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) +
	 * "\n"); valid = false; } Validate Patient ID for(char ch :
	 * XpertSmsWebClient.get(patientIdTextBox).toCharArray()) {
	 * if(!Character.isDigit(ch)) {
	 * errorMessage.append("Patient ID is invalid.\n"); valid = false; } }
	 */
	if (!valid) {
	    Window.alert(errorMessage.toString());
	    load(false);
	}
	return valid;
    }

    @Override
    public void saveData() {

	if (otherValidate()) {

	    final OtherMessageSetting newSetting = new OtherMessageSetting();
	    newSetting.setName(nameTextBox.getText());
	    newSetting.setCellNumber(cellNumberTextBox.getText());
	    newSetting.setDistrictId(locationDistrictComboBox
		    .getValue(locationDistrictComboBox.getSelectedIndex()));
	    newSetting
		    .setHealthFacilityId(locationHealthFacilityComboBox
			    .getValue(locationHealthFacilityComboBox
				    .getSelectedIndex()));

	    try {
		service.saveOtherMessageSetting(newSetting,
			new AsyncCallback<Boolean>() {
			    public void onSuccess(Boolean result) {
				if (result == null)
				    Window.alert(CustomMessage
					    .getErrorMessage(ErrorType.DUPLICATION_ERROR));
				else if (result) {
				    Window.alert(CustomMessage
					    .getInfoMessage(InfoType.INSERTED));
				    clearUp();
				    fillData();
				} else
				    Window.alert(CustomMessage
					    .getErrorMessage(ErrorType.INSERT_ERROR));
				load(false);
			    }

			    public void onFailure(Throwable caught) {
				Window.alert(CustomMessage
					.getErrorMessage(ErrorType.INSERT_ERROR));
				load(false);
			    }
			});
	    } catch (Exception e) {
		e.printStackTrace();
		load(false);
	    }

	}
    }

    private boolean otherValidate() {
	final StringBuilder errorMessage = new StringBuilder();
	valid = true;
	if (XpertSmsWebClient.get(nameTextBox).equals("")) {

	    errorMessage.append("Please enter a name.");
	    valid = false;
	}

	if (XpertSmsWebClient.get(cellNumberTextBox).equals("")) {

	    errorMessage.append("Please enter a cell number.");
	    valid = false;
	}

	if (!valid) {
	    Window.alert(errorMessage.toString());
	    load(false);
	}
	return valid;
    }

    public void updateOtherData() {
	try {
	    final OtherMessageSetting newSetting = new OtherMessageSetting();
	    newSetting.setName(nameTextBox.getText());
	    newSetting.setCellNumber(cellNumberTextBox.getText());
	    newSetting.setDistrictId(locationDistrictComboBox
		    .getValue(locationDistrictComboBox.getSelectedIndex()));
	    newSetting
		    .setHealthFacilityId(locationHealthFacilityComboBox
			    .getValue(locationHealthFacilityComboBox
				    .getSelectedIndex()));

	    service.updateOtherMessageSetting(newSetting,
		    new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
			    if (result)
				Window.alert(CustomMessage
					.getInfoMessage(InfoType.UPDATED));
			    else
				Window.alert(CustomMessage
					.getErrorMessage(ErrorType.UPDATE_ERROR));
			    load(false);

			}

			@Override
			public void onFailure(Throwable caught) {
			    Window.alert(CustomMessage
				    .getErrorMessage(ErrorType.UPDATE_ERROR));
			}
		    });
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void updateData() {
	if (validate()) {
	    try {
		setCurrent();
		service.updateMessageSettings(current,
			new AsyncCallback<Boolean>() {
			    @Override
			    public void onSuccess(Boolean result) {
				if (result)
				    Window.alert(CustomMessage
					    .getInfoMessage(InfoType.UPDATED));
				else
				    Window.alert(CustomMessage
					    .getErrorMessage(ErrorType.UPDATE_ERROR));
				load(false);
			    }

			    @Override
			    public void onFailure(Throwable caught) {
				Window.alert(CustomMessage
					.getErrorMessage(ErrorType.UPDATE_ERROR));
			    }
			});
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void deleteData() {
	try {

	    String id = XpertSmsWebClient.get(alertRecipientListBox);
	    try {
		service.findOtherMessageRecipientById(id,
			new AsyncCallback<OtherMessageSetting>() {

			    @Override
			    public void onFailure(Throwable caught) {
				load(false);
			    }

			    @Override
			    public void onSuccess(OtherMessageSetting result) {
				service.deleteOtherMessageSetting(result,
					new AsyncCallback<Boolean>() {

					    @Override
					    public void onFailure(
						    Throwable caught) {
						Window.alert(CustomMessage
							.getErrorMessage(ErrorType.DELETE_ERROR));
						load(false);

					    }

					    @Override
					    public void onSuccess(Boolean result) {
						if (result) {
						    Window.alert(CustomMessage
							    .getInfoMessage(InfoType.DELETED));
						    clearUp();
						    fillData();
						} else
						    Window.alert(CustomMessage
							    .getErrorMessage(ErrorType.DELETE_ERROR));
						load(false);

					    }

					});
				load(false);
			    }

			});

	    } catch (Exception e) {
		e.printStackTrace();
		load(false);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

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
						// searchButton.setEnabled(rights.getAccess(AccessType.SELECT));
						// createButton.setEnabled(rights.getAccess(AccessType.INSERT));
						saveButton.setEnabled(rights
							.getAccess(AccessType.UPDATE));
						deleteButton.setEnabled(rights
							.getAccess(AccessType.DELETE));
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
	if (sender == createButton) {
	    if (createButton.isDown())
		clearUp();
	    alertRecipientListBox.setEnabled(!createButton.isDown());
	    load(false);
	}/*
	  * else if (sender == IRSListBox) { fillData(); } else if (sender ==
	  * createButton) { if (createButton.isDown()) clearUp();
	  * IRSListBox.setEnabled(!createButton.isDown()); load(false); } else
	  */
	if (sender == saveButton) {
	    /*
	     * if (createButton.isDown()) saveData(); else
	     */
	    updateData();
	}
	/*
	 * else if (sender == deleteButton) { deleteData(); }
	 */
	else if (sender == closeButton) {
	    MainMenuComposite.clear();
	} else if (sender == saveOtherButton) {
	    if (createButton.isDown())
		saveData();
	    else
		updateOtherData();
	} else if (sender == deleteOtherButton) {
	    deleteData();
	} else if (sender == advanceMessageSettingAnchor) {
	    Boolean value = rightFlexTable.getRowFormatter().isVisible(10);
	    if (value == true)
		rightFlexTable.getRowFormatter().setVisible(10, false);
	    else
		rightFlexTable.getRowFormatter().setVisible(10, true);

	    load(false);

	}

    }

    @Override
    public void onChange(ChangeEvent event) {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == alertRecipientListBox) {
	    fillMessageValue();
	}

	else if (sender == locationDistrictComboBox) {
	    service.findFacilitiesByDistrictId(
		    XpertSmsWebClient.get(locationDistrictComboBox),
		    new AsyncCallback<Location[]>() {
			@Override
			public void onSuccess(Location[] result) {
			    locationHealthFacilityComboBox.clear();
			    for (Location s : result) {
				locationHealthFacilityComboBox.addItem(
					s.getLocationName(), s.getLocationId());
			    }

			    load(false);
			}

			@Override
			public void onFailure(Throwable caught) {
			    caught.printStackTrace();
			    load(false);
			}
		    });
	}
    }

    private void fillMessageValue() {
	String id = XpertSmsWebClient.get(alertRecipientListBox);
	try {
	    service.findOtherMessageRecipientById(id,
		    new AsyncCallback<OtherMessageSetting>() {

			@Override
			public void onFailure(Throwable caught) {
			    load(false);
			}

			@Override
			public void onSuccess(OtherMessageSetting result) {
			    final OtherMessageSetting otherSetting = result;
			    nameTextBox.setText(result.getName());
			    locationDistrictComboBox
				    .setSelectedIndex(XpertSmsWebClient
					    .getIndex(locationDistrictComboBox,
						    result.getDistrictId()));

			    service.findFacilitiesByDistrictId(
				    XpertSmsWebClient
					    .get(locationDistrictComboBox),
				    new AsyncCallback<Location[]>() {
					@Override
					public void onSuccess(Location[] result) {
					    locationHealthFacilityComboBox
						    .clear();
					    for (Location s : result) {
						locationHealthFacilityComboBox.addItem(
							s.getLocationName(),
							s.getLocationId());
					    }
					    locationHealthFacilityComboBox
						    .setSelectedIndex(XpertSmsWebClient
							    .getIndex(
								    locationHealthFacilityComboBox,
								    otherSetting
									    .getHealthFacilityId()));
					    load(false);
					}

					@Override
					public void onFailure(Throwable caught) {
					    caught.printStackTrace();
					    load(false);
					}
				    });

			    cellNumberTextBox.setText(result.getCellNumber());
			    load(false);
			}

		    });

	} catch (Exception e) {
	    e.printStackTrace();
	    load(false);
	}

	load(false);

    }

    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
	Widget sender = (Widget) event.getSource();
	load(false);

	if (sender == sendToPatientCheckBox) {
	    // patientTextTextArea.setEnabled(sendToPatientCheckBox.getValue());

	}

	else if (sender == sendToProviderCheckBox) {
	    // providerTextTextArea.setEnabled(sendToProviderCheckBox.getValue());

	}

	else if (sender == sendToProgramCheckBox) {
	    // programTextTextArea.setEnabled(sendToProgramCheckBox.getValue());
	    programNumberTextBox.setEnabled(sendToProgramCheckBox.getValue());

	}

	else if (sender == sendToOtherCheckBox) {
	    // otherTextTextArea.setEnabled(sendToOtherCheckBox.getValue());
	    otherNumberTextBox.setEnabled(sendToOtherCheckBox.getValue());

	}

    }

}
