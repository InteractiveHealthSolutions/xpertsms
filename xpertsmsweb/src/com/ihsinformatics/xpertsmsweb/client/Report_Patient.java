package com.ihsinformatics.xpertsmsweb.client;

import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.ihsinformatics.xpertsmsweb.shared.AccessType;
import com.ihsinformatics.xpertsmsweb.shared.CustomMessage;
import com.ihsinformatics.xpertsmsweb.shared.DataType;
import com.ihsinformatics.xpertsmsweb.shared.ErrorType;
import com.ihsinformatics.xpertsmsweb.shared.Parameter;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class Report_Patient extends Composite implements IReport, ClickHandler,
		ChangeHandler {
	private static ServerServiceAsync service = GWT.create(ServerService.class);
	private static LoadingWidget loading = new LoadingWidget();
	private static final String menuName = "DATALOG";
	private UserRightsUtil rights = new UserRightsUtil();
	private boolean valid;

	private FlexTable flexTable = new FlexTable();
	private FlexTable topFlexTable = new FlexTable();
	private FlexTable rightFlexTable = new FlexTable();
	private Grid grid = new Grid(1, 3);
	private Button viewButton = new Button("Save");
	private Button closeButton = new Button("Close");

	private Label lblXpertSmsLog = new Label("XpertSMS Patient Reports");
	private CheckBox monitorCheckBox = new CheckBox("Monitor");
	private Label lblReportFilters = new Label("Report filters:");
	private Label lblFilterValues = new Label("Filter values:");
	private CheckBox gpCheckBox = new CheckBox("GP");
	private CheckBox chwCheckBox = new CheckBox("CHW");
	private CheckBox genderCheckBox = new CheckBox("Gender");
	private CheckBox ageCheckBox = new CheckBox("Age");
	private HorizontalPanel genderHorizontalPanel = new HorizontalPanel();
	private HorizontalPanel ageHorizontalPanel = new HorizontalPanel();
	private HorizontalPanel typeHorizontalPanel = new HorizontalPanel();
	private Label lblBetween = new Label("Between:");
	private IntegerBox fromTextBox = new IntegerBox();
	private Label lblAnd = new Label("and");
	private IntegerBox toTextBox = new IntegerBox();
	private RadioButton maleRadioButton = new RadioButton("new name", "Male");
	private RadioButton femaleRadioButton = new RadioButton("new name",
			"Female");
	private ListBox monitorComboBox = new ListBox();
	private ListBox gpComboBox = new ListBox();
	private ListBox chwComboBox = new ListBox();
	private CheckBox suspectCheckBox = new CheckBox("Suspect");
	private CheckBox patientCheckBox = new CheckBox("Patient");

	public Report_Patient() {
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");

		flexTable.setWidget(0, 0, topFlexTable);

		lblXpertSmsLog.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblXpertSmsLog);

		flexTable.setWidget(1, 0, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");

		rightFlexTable.setWidget(0, 0, lblReportFilters);

		rightFlexTable.setWidget(0, 1, lblFilterValues);

		rightFlexTable.setWidget(1, 1, typeHorizontalPanel);
		suspectCheckBox.setValue(true);

		typeHorizontalPanel.add(suspectCheckBox);
		patientCheckBox.setValue(true);

		typeHorizontalPanel.add(patientCheckBox);
		monitorCheckBox.setValue(true);

		rightFlexTable.setWidget(2, 0, monitorCheckBox);

		rightFlexTable.setWidget(2, 1, monitorComboBox);
		gpCheckBox.setValue(true);

		rightFlexTable.setWidget(3, 0, gpCheckBox);

		rightFlexTable.setWidget(3, 1, gpComboBox);
		chwCheckBox.setValue(true);

		rightFlexTable.setWidget(4, 0, chwCheckBox);

		rightFlexTable.setWidget(4, 1, chwComboBox);
		genderCheckBox.setValue(true);

		rightFlexTable.setWidget(5, 0, genderCheckBox);

		rightFlexTable.setWidget(5, 1, genderHorizontalPanel);
		maleRadioButton.setValue(true);

		genderHorizontalPanel.add(maleRadioButton);

		genderHorizontalPanel.add(femaleRadioButton);
		ageCheckBox.setValue(true);

		rightFlexTable.setWidget(6, 0, ageCheckBox);

		rightFlexTable.setWidget(6, 1, ageHorizontalPanel);

		ageHorizontalPanel.add(lblBetween);
		fromTextBox.setText("0");
		fromTextBox.setMaxLength(2);
		fromTextBox.setVisibleLength(2);

		ageHorizontalPanel.add(fromTextBox);

		ageHorizontalPanel.add(lblAnd);
		toTextBox.setText("100");
		toTextBox.setVisibleLength(3);
		toTextBox.setMaxLength(3);

		ageHorizontalPanel.add(toTextBox);

		rightFlexTable.setWidget(7, 1, grid);
		grid.setSize("100%", "100%");
		viewButton.setEnabled(false);
		viewButton.setText("View");

		grid.setWidget(0, 0, viewButton);
		grid.setWidget(0, 2, closeButton);
		flexTable.getRowFormatter().setVerticalAlign(1,
				HasVerticalAlignment.ALIGN_TOP);

		viewButton.addClickHandler(this);
		closeButton.addClickHandler(this);

		setRights(menuName);
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

	@Override
	public void clearUp() {
		clearControls(flexTable);
	}

	@Override
	public boolean validate() {
		final StringBuilder errorMessage = new StringBuilder();
		valid = true;
		/* Validate mandatory fields */
		if (true) {
			errorMessage.append(CustomMessage
					.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		/* Validate data-type rules */
		if (true) {
			errorMessage.append(CustomMessage
					.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
			valid = false;
		}
		if (!valid)
			Window.alert(errorMessage.toString());
		return valid;
	}

	@Override
	public void viewData(boolean export) {
		String reportName = "";
		Parameter[] params = { new Parameter("UserName", XSMS.getCurrentUser(),
				DataType.STRING) };
		try {
			service.generateReport(reportName, params, export,
					new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							Window.open(result, "_blank", "");
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
												viewButton.setEnabled(rights
														.getAccess(AccessType.PRINT));
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
		if (sender == viewButton) {
			viewData(false);
		} else if (sender == closeButton) {
			MainMenuComposite.clear();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		// Not implemented
	}
}
