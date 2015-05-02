package com.ihsinformatics.xpertsmsweb.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.ihsinformatics.xpertsmsweb.shared.AccessType;
import com.ihsinformatics.xpertsmsweb.shared.Parameter;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class Report_ReportsComposite extends Composite implements IReport,
	ClickHandler, ChangeHandler, ValueChangeHandler<Boolean> {
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static LoadingWidget loading = new LoadingWidget();
    private static final String menuName = "DATALOG";
    private String filter = "";
    private String startDate = "";
    private String endDate = "";
    private String patientId = "";
    private String operatorId = "";
    private UserRightsUtil rights = new UserRightsUtil();

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private FlexTable filterFlexTable = new FlexTable();
    private Grid grid = new Grid(1, 4);

    private Button viewButton = new Button("Save");
    private Button closeButton = new Button("Close");
    private Button exportButton = new Button("Export");

    private Label lblSelectCategory = new Label("Select Category:");
    private Label lblSnapshot = new Label("Snapshot:");
    private Label snapshotLabel = new Label();
    private Label lblCaution = new Label(
	    "Some reports may take 5 to 10 minutes to generate. Please wait until report download window appears.");
    private Label lblXpertSmsLog = new Label("XpertSMS Reports");
    private Label lblSelectReport = new Label("Select Report:");
    private Label lblFilter = new Label("Filter (Check all that apply):");

    private TextBox patientIdTextBox = new TextBox();
    private TextBox operatorIdTextBox = new TextBox();

    private ListBox categoryComboBox = new ListBox();
    private ListBox reportsListComboBox = new ListBox();
    private ListBox patientFilterTypeComboBox = new ListBox();
    private ListBox operatorFilterTypeComboBox = new ListBox();

    private DateBox fromDateBox = new DateBox();
    private DateBox toDateBox = new DateBox();
    private DateBox fromTimeDateBox = new DateBox();
    private DateBox toTimeDateBox = new DateBox();

    private CheckBox dateRangeFilterCheckBox = new CheckBox("Date Range:");
    private CheckBox timeRangeFilterCheckBox = new CheckBox("Time Range:");
    private CheckBox patientIdCheckBox = new CheckBox("Patient ID:");
    private CheckBox operatorIdCheckBox = new CheckBox("Operator ID:");

    @SuppressWarnings("deprecation")
    public Report_ReportsComposite() {
	initWidget(flexTable);
	flexTable.setSize("100%", "100%");
	flexTable.setWidget(0, 0, topFlexTable);
	lblXpertSmsLog.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblXpertSmsLog);
	topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	topFlexTable.getRowFormatter().setVerticalAlign(0,
		HasVerticalAlignment.ALIGN_MIDDLE);
	topFlexTable.getCellFormatter().setVerticalAlignment(0, 0,
		HasVerticalAlignment.ALIGN_MIDDLE);
	flexTable.setWidget(1, 0, rightFlexTable);
	rightFlexTable.setSize("100%", "100%");
	rightFlexTable.setWidget(0, 0, lblSnapshot);
	rightFlexTable.setWidget(0, 1, snapshotLabel);
	rightFlexTable.setWidget(1, 0, lblSelectCategory);
	categoryComboBox.addItem("-- Select Category --");
	categoryComboBox.addItem("Case Detection Reports");
	rightFlexTable.setWidget(1, 1, categoryComboBox);
	categoryComboBox.setWidth("100%");
	rightFlexTable.setWidget(2, 0, lblSelectReport);
	rightFlexTable.setWidget(2, 1, reportsListComboBox);
	reportsListComboBox.setWidth("100%");
	reportsListComboBox.addChangeHandler(this);
	lblCaution.setStyleName("gwt-MenuItem-selected");
	rightFlexTable.setWidget(3, 1, lblCaution);
	lblCaution.setWidth("100%");
	rightFlexTable.setWidget(4, 0, lblFilter);
	rightFlexTable.setWidget(4, 1, filterFlexTable);
	filterFlexTable.setWidth("100%");
	filterFlexTable.setWidget(0, 0, dateRangeFilterCheckBox);
	dateRangeFilterCheckBox.setVisible(true);
	timeRangeFilterCheckBox.setVisible(true);
	dateRangeFilterCheckBox.setWidth("");
	fromDateBox.setEnabled(false);
	filterFlexTable.setWidget(0, 1, fromDateBox);
	fromDateBox.setFormat(new DefaultFormat(DateTimeFormat
		.getFormat("dd-MM-yyyy")));
	fromDateBox.setWidth("100%");
	toDateBox.setEnabled(false);
	filterFlexTable.setWidget(0, 2, toDateBox);
	toDateBox.setFormat(new DefaultFormat(DateTimeFormat
		.getFormat("dd-MM-yyyy")));
	toDateBox.setWidth("100%");
	filterFlexTable.getCellFormatter().setHorizontalAlignment(0, 1,
		HasHorizontalAlignment.ALIGN_CENTER);
	filterFlexTable.getCellFormatter().setHorizontalAlignment(0, 2,
		HasHorizontalAlignment.ALIGN_CENTER);
	filterFlexTable.setWidget(1, 0, timeRangeFilterCheckBox);
	fromTimeDateBox.setEnabled(false);
	fromTimeDateBox.setValue(new Date(1297969200000L));
	fromTimeDateBox.setFormat(new DefaultFormat(DateTimeFormat
		.getShortTimeFormat()));
	filterFlexTable.setWidget(1, 1, fromTimeDateBox);
	fromTimeDateBox.setWidth("50%");
	toTimeDateBox.setEnabled(false);
	toTimeDateBox.setValue(new Date(1298012400000L));
	toTimeDateBox.setFormat(new DefaultFormat(DateTimeFormat
		.getShortTimeFormat()));
	filterFlexTable.setWidget(1, 2, toTimeDateBox);
	toTimeDateBox.setWidth("50%");

	filterFlexTable.setWidget(2, 0, patientIdCheckBox);
	patientFilterTypeComboBox.setEnabled(false);

	filterFlexTable.setWidget(2, 1, patientFilterTypeComboBox);
	patientFilterTypeComboBox.setWidth("100%");
	patientIdTextBox.setVisibleLength(12);
	patientIdTextBox.setMaxLength(12);
	patientIdTextBox.setEnabled(false);

	filterFlexTable.setWidget(2, 2, patientIdTextBox);
	patientIdTextBox.setWidth("100%");
	operatorIdCheckBox.setHTML("Operator ID:");
	filterFlexTable.setWidget(3, 0, operatorIdCheckBox);
	operatorFilterTypeComboBox.setEnabled(false);
	filterFlexTable.setWidget(3, 1, operatorFilterTypeComboBox);
	operatorFilterTypeComboBox.setWidth("100%");
	operatorIdTextBox.setEnabled(false);
	operatorIdTextBox.setVisibleLength(12);
	operatorIdTextBox.setMaxLength(12);
	filterFlexTable.setWidget(3, 2, operatorIdTextBox);
	operatorIdTextBox.setWidth("100%");
	rightFlexTable.setWidget(5, 1, grid);
	grid.setSize("100%", "100%");
	viewButton.setEnabled(false);
	viewButton.setText("View");
	grid.setWidget(0, 0, viewButton);
	exportButton.setEnabled(false);
	grid.setWidget(0, 1, exportButton);
	grid.setWidget(0, 3, closeButton);
	rightFlexTable.getCellFormatter().setHorizontalAlignment(4, 1,
		HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.getRowFormatter().setVerticalAlign(1,
		HasVerticalAlignment.ALIGN_TOP);
	rightFlexTable.getRowFormatter().setVerticalAlign(4,
		HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_CENTER);

	dateRangeFilterCheckBox.addValueChangeHandler(this);
	timeRangeFilterCheckBox.addValueChangeHandler(this);
	patientIdCheckBox.addValueChangeHandler(this);
	operatorIdCheckBox.addValueChangeHandler(this);
	categoryComboBox.addChangeHandler(this);
	viewButton.addClickHandler(this);
	exportButton.addClickHandler(this);
	closeButton.addClickHandler(this);

	refreshList();
	setRights(menuName);
    }

    private void refreshList() {
	String[] filterOptions = { "IS EXACTLY", "STARTS WITH", "ENDS ON",
		"LOOKS LIKE" };
	for (String s : filterOptions) {
	    patientFilterTypeComboBox.addItem(s);
	    operatorFilterTypeComboBox.addItem(s);
	}
	try {
	    load(true);
	    service.getSnapshotTime(new AsyncCallback<String>() {
		@Override
		public void onSuccess(String result) {
		    snapshotLabel.setText(result);
		    load(false);
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

    /**
     * Creates appropriate filter for given column names
     * 
     * @param patientColumnName
     * @param dateColumnName
     * @param gpColumnName
     * @param monitorColumnName
     * @param chwColumnName
     */
    @SuppressWarnings("deprecation")
    private String filterData(String patientColumnName, String dateColumnName,
	    String gpColumnName, String monitorColumnName, String chwColumnName) {
	filter = "";
	startDate = "";
	endDate = "";
	patientId = "";
	operatorId = "";
	if (dateRangeFilterCheckBox.getValue()) {
	    Date start = new Date(fromDateBox.getValue().getTime());
	    Date end = new Date(toDateBox.getValue().getTime());
	    StringBuilder startString = new StringBuilder();
	    StringBuilder endString = new StringBuilder();
	    if (timeRangeFilterCheckBox.getValue()) {
		start.setHours(fromTimeDateBox.getValue().getHours());
		start.setMinutes(fromTimeDateBox.getValue().getMinutes());
		end.setHours(toTimeDateBox.getValue().getHours());
		end.setMinutes(toTimeDateBox.getValue().getMinutes());
	    }
	    startString.append((start.getYear() + 1900) + "-"
		    + (start.getMonth() + 1) + "-" + start.getDate() + " "
		    + start.getHours() + ":" + start.getMinutes() + ":00");
	    endString.append((end.getYear() + 1900) + "-"
		    + (end.getMonth() + 1) + "-" + end.getDate() + " "
		    + end.getHours() + ":" + end.getMinutes() + ":00");
	    startDate = startString.toString();
	    endDate = endString.toString();
	}
	if (patientIdCheckBox.getValue()) {
	    switch (patientFilterTypeComboBox.getSelectedIndex()) {
	    case 0:
		patientId = " = '" + XpertSmsWebClient.get(patientIdTextBox)
			+ "'";
		break;
	    case 1:
		patientId = " LIKE '" + XpertSmsWebClient.get(patientIdTextBox)
			+ "%'";
		break;
	    case 2:
		patientId = " LIKE '%"
			+ XpertSmsWebClient.get(patientIdTextBox) + "'";
		break;
	    case 3:
		patientId = " LIKE '%"
			+ XpertSmsWebClient.get(patientIdTextBox) + "%'";
		break;
	    }
	}
	if (dateRangeFilterCheckBox.getValue() && !dateColumnName.equals(""))
	    filter += " AND " + dateColumnName + " BETWEEN '" + startDate
		    + "' AND '" + endDate + "'";
	if (patientIdCheckBox.getValue() && !patientColumnName.equals(""))
	    filter += " AND " + patientColumnName + patientId;
	return filter;
    }

    /**
     * Creates appropriate filter for given column names
     * 
     * @param patientColumnName
     * @param dateColumnName
     * @param gpColumnName
     * @param monitorColumnName
     * @param chwColumnName
     */
    @SuppressWarnings("deprecation")
    private String filterData(String patientColumnName, String dateColumnName,
	    String gpColumnName, String monitorColumnName,
	    String chwColumnName, String operatorColumnName) {
	filter = "";
	startDate = "";
	endDate = "";
	patientId = "";
	operatorId = "";

	if (dateRangeFilterCheckBox.getValue()) {
	    Date start = new Date(fromDateBox.getValue().getTime());
	    Date end = new Date(toDateBox.getValue().getTime());
	    StringBuilder startString = new StringBuilder();
	    StringBuilder endString = new StringBuilder();
	    if (timeRangeFilterCheckBox.getValue()) {
		start.setHours(fromTimeDateBox.getValue().getHours());
		start.setMinutes(fromTimeDateBox.getValue().getMinutes());
		end.setHours(toTimeDateBox.getValue().getHours());
		end.setMinutes(toTimeDateBox.getValue().getMinutes());
	    }
	    startString.append((start.getYear() + 1900) + "-"
		    + (start.getMonth() + 1) + "-" + start.getDate() + " "
		    + start.getHours() + ":" + start.getMinutes() + ":00");
	    endString.append((end.getYear() + 1900) + "-"
		    + (end.getMonth() + 1) + "-" + end.getDate() + " "
		    + end.getHours() + ":" + end.getMinutes() + ":00");
	    startDate = startString.toString();
	    endDate = endString.toString();
	}
	if (patientIdCheckBox.getValue()) {
	    switch (patientFilterTypeComboBox.getSelectedIndex()) {
	    case 0:
		patientId = " = '" + XpertSmsWebClient.get(patientIdTextBox)
			+ "'";
		break;
	    case 1:
		patientId = " LIKE '" + XpertSmsWebClient.get(patientIdTextBox)
			+ "%'";
		break;
	    case 2:
		patientId = " LIKE '%"
			+ XpertSmsWebClient.get(patientIdTextBox) + "'";
		break;
	    case 3:
		patientId = " LIKE '%"
			+ XpertSmsWebClient.get(patientIdTextBox) + "%'";
		break;
	    }
	}
	if (operatorIdCheckBox.getValue()) {
	    switch (operatorFilterTypeComboBox.getSelectedIndex()) {
	    case 0:
		operatorId = " = '" + XpertSmsWebClient.get(operatorIdTextBox)
			+ "'";
		break;
	    case 1:
		operatorId = " LIKE '"
			+ XpertSmsWebClient.get(operatorIdTextBox) + "%'";
		break;
	    case 2:
		operatorId = " LIKE '%"
			+ XpertSmsWebClient.get(operatorIdTextBox) + "'";
		break;
	    case 3:
		operatorId = " LIKE '%"
			+ XpertSmsWebClient.get(operatorIdTextBox) + "%'";
		break;
	    }
	}
	if (dateRangeFilterCheckBox.getValue() && !dateColumnName.equals(""))
	    filter += " AND " + dateColumnName + " BETWEEN '" + startDate
		    + "' AND '" + endDate + "'";
	if (patientIdCheckBox.getValue() && !patientColumnName.equals(""))
	    filter += " AND " + patientColumnName + patientId;
	if (operatorIdCheckBox.getValue() && !operatorColumnName.equals(""))
	    filter += " AND " + operatorColumnName + operatorId;
	return filter;
    }

    @Override
    public void clearUp() {
    }

    @Override
    public boolean validate() {
	return true;
    }

    @Override
    public void viewData(final boolean export) {
	String reportSelected = XpertSmsWebClient.get(reportsListComboBox)
		.replace(" ", "");
	String query = "";
	// Case Detection Reports
	if (XpertSmsWebClient.get(categoryComboBox).equals(
		"Case Detection Reports")) {
	    if (reportSelected.equals("GeneXpertAutoPositiveReport")) {
		query = "SELECT G.PatientID, G.SputumTestID AS SampleID, G.MTBBurden,G.DrugResistance, G.DateTested  FROM GeneXpertResults G where GeneXpertResult='MTB DETECTED'"
			+ filterData("G.PatientID", "G.DateTested", "", "", "",
				"G.OperatorID");
	    }

	    else if (reportSelected.equals("GeneXpertAutoRifResistantReport")) {
		query = "SELECT G.PatientID, G.SputumTestID AS SampleID, G.MTBBurden,G.DateTested  FROM GeneXpertResults G where DrugResistance='DETECTED'"
			+ filterData("G.PatientID", "G.DateTested", "", "", "",
				"G.OperatorID");
	    }

	    else if (reportSelected.equals("GeneXpertAutoErrorReport")) {
		query = "SELECT G.PatientID, G.SputumTestID AS SampleID, G.DateTested,G.ErrorCode,G.InstrumentID,G.ModuleID,G.CartridgeID,G.ReagentLotID  FROM GeneXpertResults G where (G.ErrorCode != 0 AND G.ErrorCode IS NOT NULL)"
			+ filterData("G.PatientID", "G.DateTested", "", "", "",
				"G.OperatorID");
	    }

	    else if (reportSelected.equals("GeneXpertAutoProbeReport")) {
		query = "SELECT G.PatientID, G.SputumTestID AS SampleID,G.MTBBurden, G.DateTested,G.GeneXpertResult,G.DrugResistance,G.ProbeResultA,G.ProbeCtA,G.ProbeEndptA,G.ProbeResultB,G.ProbeCtB,G.ProbeEndptB,G.ProbeResultC,G.ProbeCtC,G.ProbeEndptC,G.ProbeResultD,G.ProbeCtD,G.ProbeEndptD,G.ProbeResultE,G.ProbeCtE,G.ProbeEndptE,G.ProbeResultSPC,G.ProbeCtSPC,G.ProbeEndptSPC  FROM GeneXpertResults G"
			+ filterData("G.PatientID", "G.DateTested", "", "", "",
				"G.OperatorID");
	    }

	    else if (reportSelected
		    .equals("GeneXpertResultswithAgeandGenderReport")) {
		query = "SELECT G.PatientID,floor(datediff(G.DateTested,P.DOB) / 365) as Age,P.Gender,G.SputumTestID AS SampleID, G.GeneXpertResult,G.MTBBurden,G.DrugResistance, G.DateTested  FROM GeneXpertResults G JOIN Person P ON G.PatientID=P.PID where G.GeneXpertResult IS NOT NULL";
	    } else {
		query = "";
	    }
	}

	load(true);
	if (query.equals("")) {
	    try {
		service.generateReport(reportSelected, new Parameter[] {},
			export, new AsyncCallback<String>() {
			    @Override
			    public void onSuccess(String result) {
				Window.open(result, "_blank", "");
				load(false);
			    }

			    @Override
			    public void onFailure(Throwable caught) {
				caught.printStackTrace();
				load(false);
			    }
			});
	    } catch (Exception e) {
		load(false);
		e.printStackTrace();
	    }
	} else {
	    if (XpertSmsWebClient.get(categoryComboBox).equals("Form Dumps")) {
		try {
		    service.generateCSVfromQuery(query,
			    new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {

				    Window.open(result, "_blank", "");
				    load(false);
				}

				@Override
				public void onFailure(Throwable caught) {
				    caught.printStackTrace();
				    load(false);
				}
			    });
		} catch (Exception e) {
		    load(false);
		    e.printStackTrace();
		}
	    } else {
		try {
		    service.generateReportFromQuery(reportSelected, query,
			    export, new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {

				    String url = Window.Location.getHref()
					    + "/" + result;
				    Window.open(url, "_blank", "");
				    load(false);
				}

				@Override
				public void onFailure(Throwable caught) {
				    caught.printStackTrace();
				    load(false);
				}
			    });
		} catch (Exception e) {
		    load(false);
		    e.printStackTrace();
		}
	    }
	}
    }

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
						exportButton.setEnabled(rights
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
	} else if (sender == exportButton) {
	    viewData(true);
	} else if (sender == closeButton) {
	    MainMenuComposite.clear();
	}
    }

    @Override
    public void onChange(ChangeEvent event) {
	Widget sender = (Widget) event.getSource();
	// Fill report names
	if (sender == categoryComboBox) {
	    String text = XpertSmsWebClient.get(sender);
	    if (text.equals("Case Detection Reports")) {
		reportsListComboBox.addItem("GeneXpert Auto Positive Report");
		reportsListComboBox
			.addItem("GeneXpert Auto Rif Resistant Report");
		reportsListComboBox.addItem("GeneXpert Auto Error Report");
		reportsListComboBox.addItem("GeneXpert Auto Probe Report");
		reportsListComboBox
			.addItem("GeneXpertResults with Age and Gender Report");
	    }
	}
	if (sender == reportsListComboBox) {
	    viewButton.setEnabled(!XpertSmsWebClient.get(reportsListComboBox)
		    .equals("Form Dumps"));
	}
    }

    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
	Widget sender = (Widget) event.getSource();
	if (sender == dateRangeFilterCheckBox) {
	    fromDateBox.setEnabled(dateRangeFilterCheckBox.getValue());
	    toDateBox.setEnabled(dateRangeFilterCheckBox.getValue());
	} else if (sender == timeRangeFilterCheckBox) {
	    fromTimeDateBox.setEnabled(timeRangeFilterCheckBox.getValue());
	    toTimeDateBox.setEnabled(timeRangeFilterCheckBox.getValue());
	} else if (sender == patientIdCheckBox) {
	    patientFilterTypeComboBox.setEnabled(patientIdCheckBox.getValue());
	    patientIdTextBox.setEnabled(patientIdCheckBox.getValue());
	} else if (sender == operatorIdCheckBox) {
	    operatorFilterTypeComboBox
		    .setEnabled(operatorIdCheckBox.getValue());
	    operatorIdTextBox.setEnabled(operatorIdCheckBox.getValue());
	}
    }
}
