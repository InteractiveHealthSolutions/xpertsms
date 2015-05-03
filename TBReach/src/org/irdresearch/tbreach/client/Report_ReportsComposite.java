package org.irdresearch.tbreach.client;

import java.util.Date;

import org.irdresearch.tbreach.shared.AccessType;
import org.irdresearch.tbreach.shared.Parameter;
import org.irdresearch.tbreach.shared.TBR;
import org.irdresearch.tbreach.shared.UserRightsUtil;
import org.irdresearch.tbreach.shared.model.Users;

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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.client.ui.TextBox;

public class Report_ReportsComposite extends Composite implements IReport, ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
	private static ServerServiceAsync	service						= GWT.create(ServerService.class);
	private static LoadingWidget		loading						= new LoadingWidget();
	private static final String			menuName					= "DATALOG";
	private String						filter						= "";
	private String						startDate					= "";
	private String						endDate						= "";
	private String						patientId					= "";
	private String						operatorId					= "";
	private String						gpId						= "";
	private String						chwId						= "";
	private String						monitorId					= "";
	private UserRightsUtil				rights						= new UserRightsUtil();

	private FlexTable					flexTable					= new FlexTable();
	private FlexTable					topFlexTable				= new FlexTable();
	private FlexTable					rightFlexTable				= new FlexTable();
	private FlexTable					filterFlexTable				= new FlexTable();
	private Grid						grid						= new Grid(1, 4);

	private Button						viewButton					= new Button("Save");
	private Button						closeButton					= new Button("Close");
	private Button						exportButton				= new Button("Export");

	private Label						lblSelectCategory			= new Label("Select Category:");
	private Label						lblSnapshot					= new Label("Snapshot:");
	private Label						snapshotLabel				= new Label();
	private Label						lblCaution					= new Label(
																			"Some reports may take 5 to 10 minutes to generate. Please wait until report download window appears.");
	private Label						lblTbReachLog				= new Label("TB REACH Report");
	private Label						lblSelectReport				= new Label("Select Report:");
	private Label						lblFilter					= new Label("Filter (Check all that apply):");

	private TextBox						patientIdTextBox			= new TextBox();
/*	private TextBox						gpIdTextBox					= new TextBox();
	private TextBox						chwIdTextBox				= new TextBox();
	private TextBox						monitorIdTextBox			= new TextBox();*/
	private TextBox						operatorIdTextBox			= new TextBox();

	private ListBox						categoryComboBox			= new ListBox();
	private ListBox						reportsListComboBox			= new ListBox();
	private ListBox						patientFilterTypeComboBox	= new ListBox();
	/*private ListBox						gpFilterTypeComboBox		= new ListBox();
	private ListBox						chwFilterTypeComboBox		= new ListBox();
	private ListBox						monitorFilterTypeComboBox	= new ListBox();*/
	private ListBox						operatorFilterTypeComboBox	= new ListBox();

	private DateBox						fromDateBox					= new DateBox();
	private DateBox						toDateBox					= new DateBox();
	private DateBox						fromTimeDateBox				= new DateBox();
	private DateBox						toTimeDateBox				= new DateBox();

	private CheckBox					dateRangeFilterCheckBox		= new CheckBox("Date Range:");
	private CheckBox					timeRangeFilterCheckBox		= new CheckBox("Time Range:");
	private CheckBox					patientIdCheckBox			= new CheckBox("Patient ID:");
	//private CheckBox					gpIdCheckBox				= new CheckBox("ProviderID:");
	//private CheckBox					monitorIdCheckBox			= new CheckBox("Monitor ID:");
	//private CheckBox					chwIdCheckBox				= new CheckBox("ScreenerID:");
	private CheckBox					operatorIdCheckBox			= new CheckBox("Operator ID:");

	@SuppressWarnings("deprecation")
	public Report_ReportsComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("100%", "100%");
		flexTable.setWidget(0, 0, topFlexTable);
		lblTbReachLog.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachLog);
		topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		topFlexTable.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		topFlexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTable.setWidget(1, 0, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblSnapshot);
		rightFlexTable.setWidget(0, 1, snapshotLabel);
		rightFlexTable.setWidget(1, 0, lblSelectCategory);
		categoryComboBox.addItem("-- Select Category --");
		categoryComboBox.addItem("Case Detection Reports");
		/*categoryComboBox.addItem("Follow-up Reports");
		categoryComboBox.addItem("Other Clinical Reports");
		categoryComboBox.addItem("Form Dumps");*/
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
		fromDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		fromDateBox.setWidth("100%");
		toDateBox.setEnabled(false);
		filterFlexTable.setWidget(0, 2, toDateBox);
		toDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		toDateBox.setWidth("100%");
		filterFlexTable.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		filterFlexTable.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
		filterFlexTable.setWidget(1, 0, timeRangeFilterCheckBox);
		fromTimeDateBox.setEnabled(false);
		fromTimeDateBox.setValue(new Date(1297969200000L));
		fromTimeDateBox.setFormat(new DefaultFormat(DateTimeFormat.getShortTimeFormat()));
		filterFlexTable.setWidget(1, 1, fromTimeDateBox);
		fromTimeDateBox.setWidth("50%");
		toTimeDateBox.setEnabled(false);
		toTimeDateBox.setValue(new Date(1298012400000L));
		toTimeDateBox.setFormat(new DefaultFormat(DateTimeFormat.getShortTimeFormat()));
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
		//gpIdCheckBox.setHTML("GP ID:");
		//filterFlexTable.setWidget(3, 0, gpIdCheckBox);
		//gpFilterTypeComboBox.setEnabled(false);
		//filterFlexTable.setWidget(3, 1, gpFilterTypeComboBox);
		//gpFilterTypeComboBox.setWidth("100%");
		//gpIdTextBox.setEnabled(false);
		//gpIdTextBox.setVisibleLength(12);
		//gpIdTextBox.setMaxLength(12);
		//filterFlexTable.setWidget(3, 2, gpIdTextBox);
		//gpIdTextBox.setWidth("100%");
		//chwIdCheckBox.setHTML("Screener ID:");
		//filterFlexTable.setWidget(4, 0, chwIdCheckBox);
		//chwFilterTypeComboBox.setEnabled(false);
		//filterFlexTable.setWidget(4, 1, chwFilterTypeComboBox);
		//chwFilterTypeComboBox.setWidth("100%");
		//chwIdTextBox.setEnabled(false);
		//chwIdTextBox.setVisibleLength(12);
		//chwIdTextBox.setMaxLength(12);
		//filterFlexTable.setWidget(4, 2, chwIdTextBox);
		//chwIdTextBox.setWidth("100%");
		//filterFlexTable.setWidget(5, 0, monitorIdCheckBox);
		//monitorFilterTypeComboBox.setEnabled(false);
		//filterFlexTable.setWidget(5, 1, monitorFilterTypeComboBox);
		//monitorFilterTypeComboBox.setWidth("100%");
		//monitorIdTextBox.setEnabled(false);
		//monitorIdTextBox.setVisibleLength(12);
		//monitorIdTextBox.setMaxLength(12);
		//filterFlexTable.setWidget(5, 2, monitorIdTextBox);
		//monitorIdTextBox.setWidth("100%");
		rightFlexTable.setWidget(5, 1, grid);
		grid.setSize("100%", "100%");
		viewButton.setEnabled(false);
		viewButton.setText("View");
		grid.setWidget(0, 0, viewButton);
		exportButton.setEnabled(false);
		grid.setWidget(0, 1, exportButton);
		grid.setWidget(0, 3, closeButton);
		rightFlexTable.getCellFormatter().setHorizontalAlignment(4, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
		rightFlexTable.getRowFormatter().setVerticalAlign(4, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		dateRangeFilterCheckBox.addValueChangeHandler(this);
		timeRangeFilterCheckBox.addValueChangeHandler(this);
		patientIdCheckBox.addValueChangeHandler(this);
	//	gpIdCheckBox.addValueChangeHandler(this);
	//	chwIdCheckBox.addValueChangeHandler(this);
		//monitorIdCheckBox.addValueChangeHandler(this);
		operatorIdCheckBox.addValueChangeHandler(this);
		categoryComboBox.addChangeHandler(this);
		viewButton.addClickHandler(this);
		exportButton.addClickHandler(this);
		closeButton.addClickHandler(this);

		refreshList();
		setRights(menuName);
	}

	private void refreshList()
	{
		String[] filterOptions = { "IS EXACTLY", "STARTS WITH", "ENDS ON", "LOOKS LIKE" };
		for (String s : filterOptions)
		{
			patientFilterTypeComboBox.addItem(s);
			/*gpFilterTypeComboBox.addItem(s);
			chwFilterTypeComboBox.addItem(s);
			monitorFilterTypeComboBox.addItem(s);*/
			operatorFilterTypeComboBox.addItem(s);
		}
		try
		{
			load(true);
			service.getSnapshotTime(new AsyncCallback<String>()
			{
				@Override
				public void onSuccess(String result)
				{
					snapshotLabel.setText(result);
					load(false);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					load(false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load(boolean status)
	{
		flexTable.setVisible(!status);
		if (status)
			loading.show();
		else
			loading.hide();
	}

	/**
	 * Creates appropriate filter for given column names
	 * @param patientColumnName
	 * @param dateColumnName
	 * @param gpColumnName
	 * @param monitorColumnName
	 * @param chwColumnName
	 */
	@SuppressWarnings("deprecation")
	private String filterData(String patientColumnName, String dateColumnName, String gpColumnName, String monitorColumnName, String chwColumnName)
	{
		filter = "";
		startDate = "";
		endDate = "";
		patientId = "";
		operatorId = "";
		

		if (dateRangeFilterCheckBox.getValue())
		{
			Date start = new Date(fromDateBox.getValue().getTime());
			Date end = new Date(toDateBox.getValue().getTime());
			StringBuilder startString = new StringBuilder();
			StringBuilder endString = new StringBuilder();
			if (timeRangeFilterCheckBox.getValue())
			{
				start.setHours(fromTimeDateBox.getValue().getHours());
				start.setMinutes(fromTimeDateBox.getValue().getMinutes());
				end.setHours(toTimeDateBox.getValue().getHours());
				end.setMinutes(toTimeDateBox.getValue().getMinutes());
			}
			startString.append((start.getYear() + 1900) + "-" + (start.getMonth() + 1) + "-" + start.getDate() + " " + start.getHours() + ":"
					+ start.getMinutes() + ":00");
			endString.append((end.getYear() + 1900) + "-" + (end.getMonth() + 1) + "-" + end.getDate() + " " + end.getHours() + ":"
					+ end.getMinutes() + ":00");
			startDate = startString.toString();
			endDate = endString.toString();
		}
		if (patientIdCheckBox.getValue())
		{
			switch (patientFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					patientId = " = '" + TBRClient.get(patientIdTextBox) + "'";
					break;
				case 1:
					patientId = " LIKE '" + TBRClient.get(patientIdTextBox) + "%'";
					break;
				case 2:
					patientId = " LIKE '%" + TBRClient.get(patientIdTextBox) + "'";
					break;
				case 3:
					patientId = " LIKE '%" + TBRClient.get(patientIdTextBox) + "%'";
					break;
			}
		}
		
		/*if (gpIdCheckBox.getValue())
		{
			switch (gpFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					gpId = " = '" + TBRClient.get(gpIdTextBox) + "'";
					break;
				case 1:
					gpId = " LIKE '" + TBRClient.get(gpIdTextBox) + "%'";
					break;
				case 2:
					gpId = " LIKE '%" + TBRClient.get(gpIdTextBox) + "'";
					break;
				case 3:
					gpId = " LIKE '%" + TBRClient.get(gpIdTextBox) + "%'";
					break;
			}
		}*/
		/*if (monitorIdCheckBox.getValue())
		{
			switch (monitorFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					monitorId = " = '" + TBRClient.get(monitorIdTextBox) + "'";
					break;
				case 1:
					monitorId = " LIKE '" + TBRClient.get(monitorIdTextBox) + "%'";
					break;
				case 2:
					monitorId = " LIKE '%" + TBRClient.get(monitorIdTextBox) + "'";
					break;
				case 3:
					monitorId = " LIKE '%" + TBRClient.get(monitorIdTextBox) + "%'";
					break;
			}
		}*/
		/*if (chwIdCheckBox.getValue())
		{
			switch (chwFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					chwId = " = '" + TBRClient.get(chwIdTextBox) + "'";
					break;
				case 1:
					chwId = " LIKE '" + TBRClient.get(chwIdTextBox) + "%'";
					break;
				case 2:
					chwId = " LIKE '%" + TBRClient.get(chwIdTextBox) + "'";
					break;
				case 3:
					chwId = " LIKE '%" + TBRClient.get(chwIdTextBox) + "%'";
					break;
			}
		}*/
		if (dateRangeFilterCheckBox.getValue() && !dateColumnName.equals(""))
			filter += " AND " + dateColumnName + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
		if (patientIdCheckBox.getValue() && !patientColumnName.equals(""))
			filter += " AND " + patientColumnName + patientId;
		/*if (gpIdCheckBox.getValue() && !gpColumnName.equals(""))
			filter += " AND " + gpColumnName + gpId;*/
		/*if (chwIdCheckBox.getValue() && !chwColumnName.equals(""))
			filter += " AND " + chwColumnName + chwId;*/
		/*if (monitorIdCheckBox.getValue() && !monitorColumnName.equals(""))
			filter += " AND " + monitorColumnName + monitorId;*/
		
		return filter;
	}

	/**
	 * Creates appropriate filter for given column names
	 * @param patientColumnName
	 * @param dateColumnName
	 * @param gpColumnName
	 * @param monitorColumnName
	 * @param chwColumnName
	 */
	@SuppressWarnings("deprecation")
	private String filterData(String patientColumnName, String dateColumnName, String gpColumnName, String monitorColumnName, String chwColumnName,String operatorColumnName)
	{
		filter = "";
		startDate = "";
		endDate = "";
		patientId = "";
		operatorId = "";
		

		if (dateRangeFilterCheckBox.getValue())
		{
			Date start = new Date(fromDateBox.getValue().getTime());
			Date end = new Date(toDateBox.getValue().getTime());
			StringBuilder startString = new StringBuilder();
			StringBuilder endString = new StringBuilder();
			if (timeRangeFilterCheckBox.getValue())
			{
				start.setHours(fromTimeDateBox.getValue().getHours());
				start.setMinutes(fromTimeDateBox.getValue().getMinutes());
				end.setHours(toTimeDateBox.getValue().getHours());
				end.setMinutes(toTimeDateBox.getValue().getMinutes());
			}
			startString.append((start.getYear() + 1900) + "-" + (start.getMonth() + 1) + "-" + start.getDate() + " " + start.getHours() + ":"
					+ start.getMinutes() + ":00");
			endString.append((end.getYear() + 1900) + "-" + (end.getMonth() + 1) + "-" + end.getDate() + " " + end.getHours() + ":"
					+ end.getMinutes() + ":00");
			startDate = startString.toString();
			endDate = endString.toString();
		}
		if (patientIdCheckBox.getValue())
		{
			switch (patientFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					patientId = " = '" + TBRClient.get(patientIdTextBox) + "'";
					break;
				case 1:
					patientId = " LIKE '" + TBRClient.get(patientIdTextBox) + "%'";
					break;
				case 2:
					patientId = " LIKE '%" + TBRClient.get(patientIdTextBox) + "'";
					break;
				case 3:
					patientId = " LIKE '%" + TBRClient.get(patientIdTextBox) + "%'";
					break;
			}
		}
		if (operatorIdCheckBox.getValue())
		{
			switch (operatorFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					operatorId = " = '" + TBRClient.get(operatorIdTextBox) + "'";
					break;
				case 1:
					operatorId = " LIKE '" + TBRClient.get(operatorIdTextBox) + "%'";
					break;
				case 2:
					operatorId = " LIKE '%" + TBRClient.get(operatorIdTextBox) + "'";
					break;
				case 3:
					operatorId = " LIKE '%" + TBRClient.get(operatorIdTextBox) + "%'";
					break;
			}
		}
		/*if (gpIdCheckBox.getValue())
		{
			switch (gpFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					gpId = " = '" + TBRClient.get(gpIdTextBox) + "'";
					break;
				case 1:
					gpId = " LIKE '" + TBRClient.get(gpIdTextBox) + "%'";
					break;
				case 2:
					gpId = " LIKE '%" + TBRClient.get(gpIdTextBox) + "'";
					break;
				case 3:
					gpId = " LIKE '%" + TBRClient.get(gpIdTextBox) + "%'";
					break;
			}
		}*/
		/*if (monitorIdCheckBox.getValue())
		{
			switch (monitorFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					monitorId = " = '" + TBRClient.get(monitorIdTextBox) + "'";
					break;
				case 1:
					monitorId = " LIKE '" + TBRClient.get(monitorIdTextBox) + "%'";
					break;
				case 2:
					monitorId = " LIKE '%" + TBRClient.get(monitorIdTextBox) + "'";
					break;
				case 3:
					monitorId = " LIKE '%" + TBRClient.get(monitorIdTextBox) + "%'";
					break;
			}
		}*/
		/*if (chwIdCheckBox.getValue())
		{
			switch (chwFilterTypeComboBox.getSelectedIndex())
			{
				case 0:
					chwId = " = '" + TBRClient.get(chwIdTextBox) + "'";
					break;
				case 1:
					chwId = " LIKE '" + TBRClient.get(chwIdTextBox) + "%'";
					break;
				case 2:
					chwId = " LIKE '%" + TBRClient.get(chwIdTextBox) + "'";
					break;
				case 3:
					chwId = " LIKE '%" + TBRClient.get(chwIdTextBox) + "%'";
					break;
			}
		}*/
		if (dateRangeFilterCheckBox.getValue() && !dateColumnName.equals(""))
			filter += " AND " + dateColumnName + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
		if (patientIdCheckBox.getValue() && !patientColumnName.equals(""))
			filter += " AND " + patientColumnName + patientId;
		/*if (gpIdCheckBox.getValue() && !gpColumnName.equals(""))
			filter += " AND " + gpColumnName + gpId;*/
		/*if (chwIdCheckBox.getValue() && !chwColumnName.equals(""))
			filter += " AND " + chwColumnName + chwId;*/
		/*if (monitorIdCheckBox.getValue() && !monitorColumnName.equals(""))
			filter += " AND " + monitorColumnName + monitorId;*/
		if (operatorIdCheckBox.getValue() && !operatorColumnName.equals(""))
			filter += " AND " + operatorColumnName + operatorId;
		return filter;
	}
	
	@Override
	public void clearUp()
	{
		// Not implemented
	}

	@Override
	public boolean validate()
	{
		return true;
	}

	@Override
	public void viewData(final boolean export)
	{
		String reportSelected = TBRClient.get(reportsListComboBox).replace(" ", "");
		String query = "";
		// Case Detection Reports
		if (TBRClient.get(categoryComboBox).equals("Case Detection Reports"))
		{
			if (reportSelected.equals("AdultClinicalDiagnosisReport"))
			{
				query = "SELECT A.PatientID, A.MRNo, A.GPID, A.StaffID, A.DateEntered, A.Diagnosis, A.DateBaselineVisit, P.PatientStatus FROM tbreach_rpt._AdultClinicalDiagnosis A INNER JOIN Patient P ON A.PatientID = P.PatientID WHERE 1 = 1"
						+ filterData("A.PatientID", "A.DateEntered", "A.GPID", "", "A.StaffID");
			}
			else if (reportSelected.equals("CHWSalaryReport"))
			{
				query = "SELECT WorkerID AS StaffID, DFR, Sputum1, Sputum2, PaedSmearPositive, PaedGXP, PaedPTB, PaedEPTB, AdultSmearPositive, AdultGXP, AdultPTB, AdultEPTB, Baseline, Followup2, Followup3, Followup5, Followup7, TreatmentComplete, Cured, (DFR * 75) AS DFRRate, (Sputum1 * 75) AS Sputum1Total, (Sputum2 * 75) AS Sputum2Total, (PaedSmearPositive * 1000) AS PaedSmearPositiveTotal, (PaedGXP * 1000) AS PaedGXPTotal, (PaedPTB * 500) AS PaedPTBTotal, (PaedEPTB * 500) AS PaedEPTBTotal, (AdultSmearPositive * 1000) AS AdultSmearPositiveTotal, (AdultGXP * 1000) AS AdultGXPTotal, (AdultPTB * 500) AS AdultPTBTotal, (AdultEPTB * 500) AS AdultEPTBTotal, (Baseline * 300) AS BaselineTotal, (Followup2 * 300) AS Followup2Total, (Followup3 * 300) AS Followup3Total, (Followup5 * 300) AS Followup5Total, (Followup7 * 300) AS Followup7Total, (TreatmentComplete * 400) AS TreatmentCompleteTotal, (Cured * 600) AS CuredTotal FROM tbreach_rpt._IncentiveSummary WHERE 1 = 1"
						+ filterData("", "", "", "WorkerID", "")
						+ " AND DFR + Sputum1 + Sputum2 + PaedSmearPositive + PaedGXP + PaedPTB + PaedEPTB + AdultSmearPositive + AdultGXP + AdultPTB + AdultEPTB + Baseline + Followup2 + Followup3 + Followup5 + Followup7 + TreatmentComplete + Cured > 0";
			}
			else if (reportSelected.equals("ConfirmedTBPatientsReport"))
			{
				query = "SELECT Source, PatientID, GPID, CHWID, MRNo, PatientStatus, Category, Diagnosis, DateTested, DateBaselineVisit FROM tbreach_rpt._ConfirmedTBPatients A WHERE 1 = 1"
						+ filterData("PatientID", "DateTested", "GPID", "CHWID", "");
			}
			else if (reportSelected.equals("DailySummary"))
			{
				query = "SELECT GPID, TotalScreened, TotalBaselineSputum, TotalXRay, TotalSmearPositive, TotalGXPPositive, TotalAdultPTBCases, TotalAdultEPTBCases, TotalPaediatricPTBCases, TotalPaediatricEPTBCases, TotalBaselineVisits FROM tbreach_rpt._DailySummary WHERE 1 = 1"
						+ filterData("", "", "GPID", "", "");
			}
			else if (reportSelected.equals("GeneXpertPositiveReport"))
			{
				query = "SELECT G.PatientID, P.MRNo, P.GPID, P.CHWID, P.PatientStatus, G.SputumTestID AS Barcode, G.DrugResistance, G.DateTested, (SELECT MaxEnteredDate FROM tbreach_rpt.tmp_Encounter E WHERE EncounterType = 'BASELINE' AND MaxEnteredDate IS NOT NULL AND PID = P.PatientID) AS DateBaselineVisit FROM GeneXpertResults G INNER JOIN tbreach_rpt.Patient P ON P.PatientID = G.PatientID  WHERE 1 = 1"
						+ filterData("G.PatientID", "G.DateTested", "P.GPID", "P.CHWID", "")
						+ " AND G.IsPositive = 1 AND G.PatientID NOT IN (SELECT DISTINCT PatientID FROM tbreach_rpt.SputumResults WHERE SmearResult <> 'NEGATIVE')";
			}
			
			else if(reportSelected.equals("GeneXpertAutoPositiveReport")) {
				query = "SELECT G.PatientID, G.SputumTestID AS SampleID, G.MTBBurden,G.DrugResistance, G.DateTested  FROM GeneXpertResults G where GeneXpertResult='MTB DETECTED'" + filterData("G.PatientID", "G.DateTested","","","","G.OperatorID");
			}
			
			else if(reportSelected.equals("GeneXpertAutoRifResistantReport")) {
				query = "SELECT G.PatientID, G.SputumTestID AS SampleID, G.MTBBurden,G.DateTested  FROM GeneXpertResults G where DrugResistance='DETECTED'" + filterData("G.PatientID", "G.DateTested","","","","G.OperatorID");
			}
			
			else if(reportSelected.equals("GeneXpertAutoErrorReport")) {
				query = "SELECT G.PatientID, G.SputumTestID AS SampleID, G.DateTested,G.ErrorCode,G.InstrumentID,G.ModuleID,G.CartridgeID,G.ReagentLotID  FROM GeneXpertResults G where (G.ErrorCode != 0 AND G.ErrorCode IS NOT NULL)" + filterData("G.PatientID", "G.DateTested","","","","G.OperatorID");
			}
			
			else if(reportSelected.equals("GeneXpertAutoProbeReport")) {
				query = "SELECT G.PatientID, G.SputumTestID AS SampleID,G.MTBBurden, G.DateTested,G.GeneXpertResult,G.DrugResistance,G.ProbeResultA,G.ProbeCtA,G.ProbeEndptA,G.ProbeResultB,G.ProbeCtB,G.ProbeEndptB,G.ProbeResultC,G.ProbeCtC,G.ProbeEndptC,G.ProbeResultD,G.ProbeCtD,G.ProbeEndptD,G.ProbeResultE,G.ProbeCtE,G.ProbeEndptE,G.ProbeResultSPC,G.ProbeCtSPC,G.ProbeEndptSPC  FROM GeneXpertResults G" + filterData("G.PatientID", "G.DateTested","","","","G.OperatorID");
			}
			
			else if(reportSelected.equals("GeneXpertResultswithAgeandGenderReport")) {
				query = "SELECT G.PatientID,floor(datediff(G.DateTested,P.DOB) / 365) as Age,P.Gender,G.SputumTestID AS SampleID, G.GeneXpertResult,G.MTBBurden,G.DrugResistance, G.DateTested  FROM GeneXpertResults G JOIN Person P ON G.PatientID=P.PID where G.GeneXpertResult IS NOT NULL";
			}
			/*else if(reportSelected.equals("GeneXpertUnlinkedPositiveReport")) {
				query = "SELECT G.SputumTestID AS SampleID, G.MTBBurden,G.DrugResistance, G.DateTested  FROM GeneXpertResults_Unlinked G where GeneXpertResult='MTB DETECTED'";
			}
			
			else if(reportSelected.equals("GeneXpertUnlinkedErrorReport")) {
				query = "SELECT G.SputumTestID AS SampleID, G.DateTested,G.ErrorCode,G.InstrumentID,G.ModuleID,G.CartridgeID,G.ReagentLotID  FROM GeneXpertResults_Unlinked G where (G.ErrorCode != 0 AND G.ErrorCode IS NOT NULL)";
			}
			
			else if(reportSelected.equals("GeneXpertUnlinkedProbeReport")) {
				query = "SELECT G.SputumTestID AS SampleID,G.MTBBurden, G.DateTested,G.GeneXpertResult,G.DrugResistance,G.ProbeResultA,G.ProbeCtA,G.ProbeEndptA,G.ProbeResultB,G.ProbeCtB,G.ProbeEndptB,G.ProbeResultC,G.ProbeCtC,G.ProbeEndptC,G.ProbeResultD,G.ProbeCtD,G.ProbeEndptD,G.ProbeResultE,G.ProbeCtE,G.ProbeEndptE,G.ProbeResultSPC,G.ProbeCtSPC,G.ProbeEndptSPC  FROM GeneXpertResults_Unlinked G" + filterData("G.PatientID", "G.DateTested","","","");;
			}*/
			
			else if (reportSelected.equals("PaediatricClinicalDiagnosisReport"))
			{
				query = "SELECT DISTINCT E.PID1, P.MRNo, P.PatientStatus, P.GPID, E.PID2 AS StaffID, MAX(E.DateEncounterEntered) AS DateEntered, ER.Value AS Diagnosis, (SELECT MaxEnteredDate FROM tbreach_rpt.tmp_Encounter WHERE EncounterType = 'BASELINE' AND PID = P.PatientID) AS DateBaselineVisit FROM tbreach_rpt.EncounterResults AS ER INNER JOIN tbreach_rpt.Encounter AS E ON ER.EncounterID = E.EncounterID AND ER.PID1 = E.PID1 AND ER.PID2 = E.PID2 INNER JOIN tbreach_rpt.Patient P ON P.PatientID = E.PID1 WHERE 1 = 1"
						+ filterData("E.PID1", "P.DateRegistered", "P.GPID", "", "")
						+ " AND E.EncounterType = 'PAED_DIAG' AND ER.Element = 'CONCLUSION' AND ER.Value IN ('EXTRA PULMONARY PEDIATRIC TB', 'SMEAR POSITVE PULMONARY PEDIATRIC TB', 'SMEAR NEGATIVE PULMONARY PEDIATRIC TB') GROUP BY E.PID1, P.MRNo, P.GPID, E.PID2";
			}
			else if (reportSelected.equals("PendingSmearResultReport"))
			{
				query = "SELECT S.PatientID, P.MRNo, P.PatientStatus, S.SputumTestID as Barcode, S.DateSubmitted FROM tbreach_rpt.SputumResults S INNER JOIN tbreach_rpt.Patient P ON P.PatientID = S.PatientID WHERE 1 = 1"
						+ filterData("S.PatientID", "S.DateSubmitted", "", "", "") + " AND S.Remarks NOT LIKE 'REJECTED%' AND S.SmearResult IS NULL ";
			}
			else if (reportSelected.equals("PendingXRayResultReport"))
			{
				query = "SELECT X.PatientID, Pt.MRNo, Pt.PatientStatus, CONCAT(IFNULL(P.FirstName, ''), ' ', IFNULL(P.LastName, '')) AS PatientName, X.IRS, X.XRayDate, X.XRayResults, X.Remarks FROM tbreach_rpt.XRayResults X INNER JOIN tbreach_rpt.Person P ON P.PID = X.PatientID INNER JOIN tbreach_rpt.Patient Pt ON Pt.PatientID = X.PatientID WHERE 1 = 1"
						+ filterData("X.PatientID", "X.XRayDate", "", "", "") + " AND X.DateReported IS NULL AND X.XRayResults = ''";
			}
			else if (reportSelected.equals("SmearPositiveReport"))
			{
				query = "SELECT PatientID, MRNo, GPID, CHWID, PatientStatus, DrugResistance, DateBaselineTested, DATE(DateBaselineVisit) AS DateBaselineVisit FROM tbreach_rpt._SmearPositive WHERE 1 = 1"
						+ filterData("PatientID", "DateBaselineTested", "GPID", "CHWID", "");
			}
			else if (reportSelected.equals("SputumRejectionReport"))
			{
				query = "SELECT S.PatientID, P.MRNo, P.GPID, P.CHWID, P.PatientStatus, S.SputumTestID as Barcode, S.DateSubmitted, S.Remarks FROM tbreach_rpt.SputumResults S INNER JOIN tbreach_rpt.Patient P ON P.PatientID = S.PatientID WHERE 1 = 1"
						+ filterData("S.PatientID", "S.DateSubmitted", "P.GPID", "P.CHWID", "")
						+ " AND S.Remarks LIKE 'REJECTED%' AND S.SmearResult IS NULL";
			}
			else if (reportSelected.equals("XRaySuggestiveReport"))
			{
				query = "SELECT DISTINCT X.PatientID, X.GPID, X.MRNo, X.XRayResults, X.Remarks, X.DateReported, X.DrugResistance, X.SmearPositiveDate, X.Diagnosis, X.DateBaselineVisit, P.PatientStatus FROM tbreach_rpt._XRaySuggestive X INNER JOIN tbreach_rpt.Patient P ON X.PatientID = P.PatientID WHERE 1 = 1"
						+ filterData("X.PatientID", "X.DateReported", "X.GPID", "", "");
			}
			else
			{
				query = "";
			}
		}
		else if (TBRClient.get(categoryComboBox).equals("Follow-up Reports"))
		{
			
			if (reportSelected.equals("FollowUpProgressReport"))
			{
				query = "select PatientID, GPID, CHWID, MRNo, Source, BaselineDiagnosis, DateBaselineVisit, BaselineTestDate, PatientStatus, Category, Visit1Date, Smear1Date, Smear1Result, "
						+ "Visit2Date, (case when Visit2Date is null and datediff(curdate(), DateBaselineVisit) >= 54 then 'YES' else null end) as Visit2Due, Smear2Date, Smear2Result, "
						+ "Visit3Date, (case when Visit3Date is null and datediff(curdate(), DateBaselineVisit) >= 84 then 'YES' else null end) as Visit3Due, Smear3Date, Smear3Result, "
						+ "Visit4Date, Smear4Date, Smear4Result, Visit5Date, (case when Visit5Date is null and datediff(curdate(), DateBaselineVisit) >= 144 then 'YES' else null end) as Visit5Due, Smear5Date, Smear5Result, "
						+ "Visit6Date, Smear6Date, Smear6Result, Visit7Date, (case when Visit7Date is null and datediff(curdate(), DateBaselineVisit) >= 174 then 'YES' else null end) as Visit7Due, Smear7Date, Smear7Result, "
						+ "Visit8Date, Smear8Date, Smear8Result, Visit9Date, Smear9Date, Smear9Result, Visit10Date, Smear10Date, Smear10Result from tbreach_rpt._FollowupProgress WHERE 1 = 1"
						+ filterData("PatientID", "DateBaselineVisit", "GPID", "CHWID", "");
			}
			else if (reportSelected.equals("PendingBaselineSputumReport"))
			{
				query = "select PatientID, GPID, CHWID, MonitorID, Attempts, Verified, Pending, SmearNegative from tbreach_rpt._SputumCounts WHERE 1 = 1"
						+ filterData("PatientID", "", "GPID", "CHWID", "MonitorID");
			}
			else
			{
				query = "";
			}
		}
		else if (TBRClient.get(categoryComboBox).equals("Form Dumps"))
		{
			query = "SELECT * FROM Enc_" + TBRClient.get(reportsListComboBox) + " WHERE 1 = 1"+ filterData("PID1", "EnteredDate", "", "", "");
		}

		load(true);
		if (query.equals(""))
		{
			try
			{
				service.generateReport(reportSelected, new Parameter[] {}, export, new AsyncCallback<String>()
				{
					@Override
					public void onSuccess(String result)
					{
						Window.open(result, "_blank", "");
						load(false);
					}

					@Override
					public void onFailure(Throwable caught)
					{
						caught.printStackTrace();
						load(false);
					}
				});
			}
			catch (Exception e)
			{
				load(false);
				e.printStackTrace();
			}
		}
		else
		{
			if (TBRClient.get(categoryComboBox).equals("Form Dumps"))
			{
				try
				{
					service.generateCSVfromQuery(query, new AsyncCallback<String>()
					{
						@Override
						public void onSuccess(String result)
						{
							
							Window.open(result, "_blank", "");
							load(false);
						}

						@Override
						public void onFailure(Throwable caught)
						{
							caught.printStackTrace();
							load(false);
						}
					});
				}
				catch (Exception e)
				{
					load(false);
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					service.generateReportFromQuery(reportSelected, query, export, new AsyncCallback<String>()
					{
						@Override
						public void onSuccess(String result)
						{
							
							String url = Window.Location.getHref()+"/"+result;
							Window.open(url, "_blank", "");
							load(false);
						}

						@Override
						public void onFailure(Throwable caught)
						{
							caught.printStackTrace();
							load(false);
						}
					});
				}
				catch (Exception e)
				{
					load(false);
					e.printStackTrace();
				}
			}
		}
	}

	public void setRights(String menuName)
	{
		try
		{
			load(true);
			service.getUserRgihts(TBR.getCurrentUser(), menuName, new AsyncCallback<Boolean[]>()
			{
				@Override
				public void onSuccess(Boolean[] result)
				{
					final Boolean[] userRights = result;
					try
					{
						service.findUser(TBR.getCurrentUser(), new AsyncCallback<Users>()
						{
							@Override
							public void onSuccess(Users result)
							{
								rights.setRoleRights(result.getRole(), userRights);
								viewButton.setEnabled(rights.getAccess(AccessType.PRINT));
								exportButton.setEnabled(rights.getAccess(AccessType.PRINT));
								load(false);
							}

							@Override
							public void onFailure(Throwable caught)
							{
								load(false);
							}
						});
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(Throwable caught)
				{
					load(false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(ClickEvent event)
	{
		Widget sender = (Widget) event.getSource();
		load(true);
		if (sender == viewButton)
		{
			viewData(false);
		}
		else if (sender == exportButton)
		{
			viewData(true);
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear();
		}
	}

	@Override
	public void onChange(ChangeEvent event)
	{
		Widget sender = (Widget) event.getSource();
		// Fill report names
		if (sender == categoryComboBox)
		{
			String text = TBRClient.get(sender);
			if (text.equals("Case Detection Reports"))
			{
				/*reportsListComboBox.clear();
				reportsListComboBox.addItem("Adult Clinical Diagnosis Report");
				reportsListComboBox.addItem("CHW List");
				reportsListComboBox.addItem("CHW Salary Report");
				reportsListComboBox.addItem("Confirmed TB Patients Report");
				reportsListComboBox.addItem("Daily Summary");
				reportsListComboBox.addItem("GeneXpert Positive Report");*/
				reportsListComboBox.addItem("GeneXpert Auto Positive Report");
				reportsListComboBox.addItem("GeneXpert Auto Rif Resistant Report");
				reportsListComboBox.addItem("GeneXpert Auto Error Report");
				reportsListComboBox.addItem("GeneXpert Auto Probe Report");
				reportsListComboBox.addItem("GeneXpertResults with Age and Gender Report");
				/*reportsListComboBox.addItem("GeneXpert Unlinked Positive Report");
				reportsListComboBox.addItem("GeneXpert Unlinked Error Report");
				reportsListComboBox.addItem("GeneXpert Unlinked Probe Report");*/
				/*reportsListComboBox.addItem("GP List");
				reportsListComboBox.addItem("Lab XRay Reporting Log");
				reportsListComboBox.addItem("Monitor List");
				reportsListComboBox.addItem("Paediatric Clinical Diagnosis Report");
				reportsListComboBox.addItem("Pending Smear Result Report");
				reportsListComboBox.addItem("Pending XRay Result Report");
				reportsListComboBox.addItem("Smear Positive Report");
				reportsListComboBox.addItem("Sputum Rejection Report");
				reportsListComboBox.addItem("XRay Suggestive Report");*/
			}
			/*else if (text.equals("Follow-up Reports"))
			{
				reportsListComboBox.clear();
				reportsListComboBox.addItem("CATI Patients With No Month 5 Sputum Report");
				reportsListComboBox.addItem("CATII Patients With No Month 5 Sputum Report");
				reportsListComboBox.addItem("EPTB Suspect Without Diagnosis Report");
				reportsListComboBox.addItem("Follow Up Progress Report");
				reportsListComboBox.addItem("Patient Visits Schedule Report");
				reportsListComboBox.addItem("Patients With Month 2 Positive And No Month 3 Sputum Report");
				reportsListComboBox.addItem("Patients With No Month 2 Followup Report");
				reportsListComboBox.addItem("Patients With No Month 2 Sputum Report");
				reportsListComboBox.addItem("Patients With No Month 3 Followup Report");
				reportsListComboBox.addItem("Patients With No Month 3 Sputum Report");
				reportsListComboBox.addItem("Patients With No Month 5 Followup Report");
				reportsListComboBox.addItem("Patients With No Month 7 Followup Report");
				reportsListComboBox.addItem("Patients With No Month 7 Sputum Report");
				reportsListComboBox.addItem("Pending Baseline Sputum Report");
				reportsListComboBox.addItem("Pending Baseline XRay Report");
				reportsListComboBox.addItem("Pending Diagnosis On XRay Suggestive Report");
				reportsListComboBox.addItem("Smear Positive Patients Without Baseline Report");
			}*/
			/*else if (text.equals("Other Clinical Reports"))
			{
				reportsListComboBox.clear();
				reportsListComboBox.addItem("");
			}
			else if (text.equals("Form Dumps"))
			{
				reportsListComboBox.clear();
				reportsListComboBox.addItem("ADDR_UPD");
				reportsListComboBox.addItem("AD_CLIVIS");
				reportsListComboBox.addItem("BASELINE");
				reportsListComboBox.addItem("CDF");
				reportsListComboBox.addItem("CS_COLL");
				reportsListComboBox.addItem("CT_SUSPECT");
				reportsListComboBox.addItem("DFR");
				reportsListComboBox.addItem("DRUG_ADM");
				reportsListComboBox.addItem("END_FOL");
				reportsListComboBox.addItem("FOLLOW_UP");
				reportsListComboBox.addItem("FUP_EFFORT");
				reportsListComboBox.addItem("GPS");
				reportsListComboBox.addItem("GP_NEW");
				reportsListComboBox.addItem("MR_ASSIGN");
				reportsListComboBox.addItem("PAED_CONF");
				reportsListComboBox.addItem("PAED_DIAG");
				reportsListComboBox.addItem("PATVER");
				reportsListComboBox.addItem("P_INFO");
				reportsListComboBox.addItem("REFUSAL");
				reportsListComboBox.addItem("SPUTUM_COL");
				reportsListComboBox.addItem("SUSPECTCON");
				reportsListComboBox.addItem("SUSPECTVER");
				reportsListComboBox.addItem("SUSPECT_ID");
				reportsListComboBox.addItem("TB_HISTORY");
			}*/
		}
		if (sender == reportsListComboBox)
		{
			viewButton.setEnabled(!TBRClient.get(reportsListComboBox).equals("Form Dumps"));
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event)
	{
		Widget sender = (Widget) event.getSource();
		if (sender == dateRangeFilterCheckBox)
		{
			fromDateBox.setEnabled(dateRangeFilterCheckBox.getValue());
			toDateBox.setEnabled(dateRangeFilterCheckBox.getValue());
		}
		else if (sender == timeRangeFilterCheckBox)
		{
			fromTimeDateBox.setEnabled(timeRangeFilterCheckBox.getValue());
			toTimeDateBox.setEnabled(timeRangeFilterCheckBox.getValue());
		}
		else if (sender == patientIdCheckBox)
		{
			patientFilterTypeComboBox.setEnabled(patientIdCheckBox.getValue());
			patientIdTextBox.setEnabled(patientIdCheckBox.getValue());
		}
		else if (sender == operatorIdCheckBox)
		{
			operatorFilterTypeComboBox.setEnabled(operatorIdCheckBox.getValue());
			operatorIdTextBox.setEnabled(operatorIdCheckBox.getValue());
		}
		/*else if (sender == gpIdCheckBox)
		{
			gpFilterTypeComboBox.setEnabled(gpIdCheckBox.getValue());
			gpIdTextBox.setEnabled(gpIdCheckBox.getValue());
		}
		else if (sender == chwIdCheckBox)
		{
			chwFilterTypeComboBox.setEnabled(chwIdCheckBox.getValue());
			chwIdTextBox.setEnabled(chwIdCheckBox.getValue());
		}*/
		/*else if (sender == monitorIdCheckBox)
		{
			monitorFilterTypeComboBox.setEnabled(monitorIdCheckBox.getValue());
			monitorIdTextBox.setEnabled(monitorIdCheckBox.getValue());
		}*/
	}
}
