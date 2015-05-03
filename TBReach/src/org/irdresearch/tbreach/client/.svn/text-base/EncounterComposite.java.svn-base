package org.irdresearch.tbreach.client;

import java.util.Date;
import java.util.Iterator;

import org.irdresearch.tbreach.shared.AccessType;
import org.irdresearch.tbreach.shared.CustomMessage;
import org.irdresearch.tbreach.shared.ErrorType;
import org.irdresearch.tbreach.shared.InfoType;
import org.irdresearch.tbreach.shared.ListType;
import org.irdresearch.tbreach.shared.TBR;
import org.irdresearch.tbreach.shared.UserRightsUtil;
import org.irdresearch.tbreach.shared.model.Encounter;
import org.irdresearch.tbreach.shared.model.EncounterId;
import org.irdresearch.tbreach.shared.model.EncounterResultsId;
import org.irdresearch.tbreach.shared.model.Users;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
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
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;

public class EncounterComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service					= GWT.create(ServerService.class);
	private static LoadingWidget		loading					= new LoadingWidget();
	private static final String			menuName				= "ENCOUNTER";
	private static final String			tableName				= "Encounter";

	private UserRightsUtil				rights					= new UserRightsUtil();
	private boolean						valid;
	private Encounter					current;

	private FlexTable					flexTable				= new FlexTable();
	private FlexTable					topFlexTable			= new FlexTable();
	private FlexTable					leftFlexTable			= new FlexTable();
	private FlexTable					rightFlexTable			= new FlexTable();
	private DecoratorPanel				encounterResultsPanel	= new DecoratorPanel();
	private Grid						grid					= new Grid(1, 3);
	private Grid						resultsGrid;

	private Label						lblTbReachEncounters	= new Label("TB REACH Encounters");
	private Label						lblPatientId			= new Label("Patient ID:");
	private Label						lblGpmonitorchwId		= new Label("Provider/Screener ID:");
	private Label						lblEncounterId			= new Label("Encounter ID:");
	private Label						lblEncounterType		= new Label("Encounter Type:");
	private Label						lblEncounterStartDate	= new Label("Encounter Start Date:");
	private Label						lblEncounterEndDate		= new Label("Encounter End Date:");
	private Label						lblEncounterEnteredDate	= new Label("Encounter Entered Date:");
	private Label						lblDetails				= new Label("Details:");
	private Label						lblResults				= new Label("Results:");
	private Label						lblPatientsName			= new Label("NAME");

	private TextBox						patientIdTextBox		= new TextBox();
	private TextBox						staffTextBox			= new TextBox();
	private TextBox						detailsTextBox			= new TextBox();
	private DateBox						encounterStartDateBox	= new DateBox();
	private DateBox						encounterEndDateBox		= new DateBox();
	private DateBox						encounterEnteredDateBox	= new DateBox();
	private ListBox						encounterIdListBox		= new ListBox();

	private ListBox						mainListBox				= new ListBox();
	private ListBox						staffListBox			= new ListBox();
	private ListBox						patientListBox			= new ListBox();
	private ListBox						encounterTypeComboBox	= new ListBox();

	private ToggleButton				createButton			= new ToggleButton("Create Encounter");
	private Button						saveButton				= new Button("Save");
	private Button						deleteButton			= new Button("Delete");
	private Button						closeButton				= new Button("Close");

	public EncounterComposite()
	{
		patientListBox.setVisibleItemCount(25);
		staffListBox.setVisibleItemCount(20);
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 2, topFlexTable);
		lblTbReachEncounters.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachEncounters);
		flexTable.setWidget(1, 0, leftFlexTable);
		mainListBox.setEnabled(false);
		leftFlexTable.setWidget(0, 0, mainListBox);
		mainListBox.setWidth("110px");
		mainListBox.setVisibleItemCount(4);
		leftFlexTable.setWidget(1, 0, staffListBox);
		staffListBox.setWidth("110px");
		flexTable.setWidget(1, 1, patientListBox);
		patientListBox.setWidth("110px");
		flexTable.setWidget(1, 2, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblEncounterId);
		rightFlexTable.setWidget(0, 1, encounterIdListBox);
		rightFlexTable.setWidget(1, 0, lblPatientId);
		patientIdTextBox.setMaxLength(12);
		patientIdTextBox.setVisibleLength(12);
		rightFlexTable.setWidget(1, 1, patientIdTextBox);

		rightFlexTable.setWidget(2, 1, lblPatientsName);
		rightFlexTable.setWidget(3, 0, lblGpmonitorchwId);
		staffTextBox.setMaxLength(12);
		staffTextBox.setVisibleLength(12);
		rightFlexTable.setWidget(3, 1, staffTextBox);
		rightFlexTable.setWidget(4, 0, lblEncounterType);
		rightFlexTable.setWidget(4, 1, encounterTypeComboBox);
		rightFlexTable.setWidget(5, 0, lblEncounterStartDate);
		encounterStartDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")));
		rightFlexTable.setWidget(5, 1, encounterStartDateBox);
		rightFlexTable.setWidget(6, 0, lblEncounterEndDate);
		encounterEndDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")));
		rightFlexTable.setWidget(6, 1, encounterEndDateBox);
		rightFlexTable.setWidget(7, 0, lblEncounterEnteredDate);
		encounterEnteredDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")));
		rightFlexTable.setWidget(7, 1, encounterEnteredDateBox);
		rightFlexTable.setWidget(8, 0, lblDetails);
		detailsTextBox.setMaxLength(255);
		detailsTextBox.setVisibleLength(50);
		rightFlexTable.setWidget(8, 1, detailsTextBox);
		rightFlexTable.setWidget(9, 0, lblResults);
		rightFlexTable.setWidget(9, 1, encounterResultsPanel);
		encounterResultsPanel.setSize("100%", "100%");
		createButton.setEnabled(false);
		rightFlexTable.setWidget(10, 0, createButton);
		rightFlexTable.setWidget(10, 1, grid);
		grid.setSize("100%", "100%");
		saveButton.setEnabled(false);
		grid.setWidget(0, 0, saveButton);
		deleteButton.setEnabled(false);
		grid.setWidget(0, 1, deleteButton);
		grid.setWidget(0, 2, closeButton);
		flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);

		createButton.addClickHandler(this);
		saveButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		closeButton.addClickHandler(this);
		mainListBox.addChangeHandler(this);
		staffListBox.addChangeHandler(this);
		patientListBox.addChangeHandler(this);
		encounterIdListBox.addChangeHandler(this);

		refreshList();
		setRights(menuName);
	}

	public void refreshList()
	{
		try
		{
			mainListBox.clear();
			mainListBox.addItem("GP");
			mainListBox.addItem("CHW");
			mainListBox.addItem("MONITOR");
			encounterTypeComboBox.clear();
			for (String s : TBR.getList(ListType.ENCOUNTER_TYPE))
				encounterTypeComboBox.addItem(s);
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

	public void clearControls(Widget w)
	{
		if (w instanceof Panel)
		{
			Iterator<Widget> iter = ((Panel) w).iterator();
			while (iter.hasNext())
				clearControls(iter.next());
		}
		else if (w instanceof TextBoxBase)
		{
			((TextBoxBase) w).setText("");
		}
		else if (w instanceof RichTextArea)
		{
			((RichTextArea) w).setText("");
		}
		else if (w instanceof ListBox)
		{
			((ListBox) w).setSelectedIndex(0);
		}
		else if (w instanceof DatePicker)
		{
			((DatePicker) w).setValue(new Date());
		}
	}

	public void setCurrent()
	{
		try
		{
			current.setDateEncounterStart(encounterStartDateBox.getValue());
			current.setDateEncounterEnd(encounterEndDateBox.getValue());
			current.setDateEncounterEntered(encounterEnteredDateBox.getValue());
			current.setDetails(TBRClient.get(detailsTextBox));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void fillStaffList()
	{
		try
		{
			String selection = TBRClient.get(mainListBox);
			String table;
			if (selection.equals("CHW"))
				table = "Worker";
			else if (selection.equals("GP"))
				table = "GP";
			else
				table = "Monitor";
			service.getColumnData(table, table + "ID", "", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					staffListBox.clear();
					for (String s : result)
						staffListBox.addItem(s);
					load(false);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
					load(false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			load(false);
		}
	}

	public void fillPatientList()
	{
		try
		{
			service.getColumnData(tableName, "PID1", "PID2='" + TBRClient.get(staffListBox) + "'", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					patientListBox.clear();
					for (String s : result)
						patientListBox.addItem(s);
					load(false);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
					load(false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			load(false);
		}
	}

	public void fillEncounterList()
	{
		try
		{
			service.getColumnData(tableName, "EncounterID", "PID1='" + TBRClient.get(patientListBox) + "' AND PID2='" + TBRClient.get(staffListBox)
					+ "'", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					encounterIdListBox.clear();
					encounterIdListBox.addItem("");
					for (String s : result)
						encounterIdListBox.addItem(s);
					load(false);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
					load(false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			load(false);
		}
	}

	@Override
	public void fillData()
	{
		try
		{
			EncounterId encounterID = new EncounterId(Integer.parseInt(TBRClient.get(encounterIdListBox)), TBRClient.get(patientIdTextBox),
					TBRClient.get(staffTextBox));
			service.findEncounter(encounterID, new AsyncCallback<Encounter>()
			{
				@Override
				public void onSuccess(Encounter result)
				{
					current = result;
					encounterTypeComboBox.setSelectedIndex(TBRClient.getIndex(encounterTypeComboBox, current.getEncounterType()));
					encounterStartDateBox.setValue(current.getDateEncounterStart());
					encounterEndDateBox.setValue(current.getDateEncounterEnd());
					encounterEnteredDateBox.setValue(current.getDateEncounterEntered());
					detailsTextBox.setValue(current.getDetails());
					try
					{
						String filter = "EncounterID=" + current.getId().getEncounterId() + " AND " + "PID1='" + current.getId().getPid1() + "' AND "
								+ "PID2='" + current.getId().getPid2() + "'";
						String[] columns = { "Element", "Value" };
						service.getTableData("EncounterResults", columns, filter, new AsyncCallback<String[][]>()
						{
							@Override
							public void onSuccess(String[][] result)
							{
								encounterResultsPanel.clear();
								resultsGrid = new Grid(result.length, 3);
								for (int i = 0; i < result.length; i++)
								{
									Label label = new Label(result[i][0]);
									TextBox textBox = new TextBox();
									CheckBox checkBox = new CheckBox("Delete");
									textBox.setValue(result[i][1]);
									resultsGrid.setWidget(i, 0, label);
									resultsGrid.setWidget(i, 1, textBox);
									resultsGrid.setWidget(i, 2, checkBox);
								}
								encounterResultsPanel.add(resultsGrid);
								load(false);
							}

							@Override
							public void onFailure(Throwable caught)
							{
								Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
								load(false);
							}
						});
					}
					catch (Exception e)
					{
						e.printStackTrace();
						load(false);
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
			load(false);
		}

	}

	@Override
	public void clearUp()
	{
		clearControls(flexTable);
	}

	@Override
	public boolean validate()
	{
		final StringBuilder errorMessage = new StringBuilder();
		valid = true;
		/* Validate mandatory fields */
		if (TBRClient.get(patientIdTextBox).equals("") || TBRClient.get(staffTextBox).equals(""))
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		/* Validate data-type rules */
		if (!valid)
		{
			Window.alert(errorMessage.toString());
			load(false);
		}
		return valid;
	}

	@Override
	public void saveData()
	{
		Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR));
	}

	/**
	 * Updates encounter with results. The results with 'delete' check boxes checked get deleted, the others update
	 */
	@Override
	public void updateData()
	{
		if (validate())
		{
			try
			{
				setCurrent();
				service.updateEncounter(current, new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						for (int i = 0; i < resultsGrid.getRowCount(); i++)
						{
							if (resultsGrid.getWidget(i, 2) instanceof CheckBox)
							{
								CheckBox chk = (CheckBox) resultsGrid.getWidget(i, 2);
								if (chk.getValue())
								{
									Label lbl = (Label) resultsGrid.getWidget(i, 0);
									EncounterResultsId id = new EncounterResultsId(current.getId().getEncounterId(), current.getId().getPid1(),
											current.getId().getPid2(), lbl.getText());
									try
									{
										service.deleteEncounterResults(id, new AsyncCallback<Boolean>()
										{
											@Override
											public void onSuccess(Boolean result)
											{
												// Deleted
											}

											@Override
											public void onFailure(Throwable caught)
											{
												// Failed
											}
										});
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
								else
								{
									Label lbl = (Label) resultsGrid.getWidget(i, 0);
									EncounterResultsId id = new EncounterResultsId(current.getId().getEncounterId(), current.getId().getPid1(),
											current.getId().getPid2(), lbl.getText());
									try
									{
										TextBox txt = (TextBox) resultsGrid.getWidget(i, 1);
										service.updateEncounterResults(id, txt.getValue(), new AsyncCallback<Boolean>()
										{
											@Override
											public void onSuccess(Boolean result)
											{
												// Update
											}

											@Override
											public void onFailure(Throwable caught)
											{
												// Failed
											}
										});
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							}
						}

						try
						{
							if (result)
								Window.alert(CustomMessage.getInfoMessage(InfoType.UPDATED));
							else
								Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
							load(false);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							load(false);
						}
					}

					@Override
					public void onFailure(Throwable caught)
					{
						Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
						load(false);
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace();
				load(false);
			}
		}
	}

	@Override
	public void deleteData()
	{
		if (validate())
		{
			try
			{
				if (Window.confirm("This will delete following Encounter with results:\n" + current.toString() + "\n"
						+ CustomMessage.getInfoMessage(InfoType.CONFIRM_OPERATION)))
				{
					service.deleteEncounterWithResults(current, new AsyncCallback<Boolean>()
					{
						@Override
						public void onSuccess(Boolean result)
						{
							if (result)
							{
								Window.alert(CustomMessage.getInfoMessage(InfoType.DELETED));
								clearUp();
							}
							else
								Window.alert(CustomMessage.getErrorMessage(ErrorType.DELETE_ERROR));
							load(false);
						}

						@Override
						public void onFailure(Throwable caught)
						{
							Window.alert(CustomMessage.getErrorMessage(ErrorType.DELETE_ERROR));
							load(false);
						}
					});
				}
				else
					load(false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				load(false);
			}
		}
	}

	@Override
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
								mainListBox.setEnabled(rights.getAccess(AccessType.SELECT));
								createButton.setEnabled(rights.getAccess(AccessType.INSERT));
								saveButton.setEnabled(rights.getAccess(AccessType.UPDATE));
								deleteButton.setEnabled(rights.getAccess(AccessType.DELETE));
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
		if (sender == createButton)
		{
			if (createButton.isDown())
				clearUp();
			mainListBox.setEnabled(!createButton.isDown());
			load(false);
		}
		else if (sender == saveButton)
		{
			if (createButton.isDown())
				saveData();
			else
				updateData();
		}
		else if (sender == deleteButton)
		{
			deleteData();
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
		load(true);
		if (sender == mainListBox)
		{
			fillStaffList();
		}
		else if (sender == staffListBox)
		{
			fillPatientList();
			staffTextBox.setValue(TBRClient.get(staffListBox));
		}
		else if (sender == patientListBox)
		{
			fillEncounterList();
			patientIdTextBox.setValue(TBRClient.get(patientListBox));
			try
			{
				service.getObject("Person", "CONCAT(IFNULL(FirstName, ''), ' ', IFNULL(LastName, '')) AS FullName",
						"PID='" + TBRClient.get(patientIdTextBox) + "'", new AsyncCallback<String>()
						{
							@Override
							public void onSuccess(String result)
							{
								lblPatientsName.setText(result);
							}

							@Override
							public void onFailure(Throwable caught)
							{
								caught.printStackTrace();
							}
						});
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (sender == encounterIdListBox)
		{
			fillData();
		}
	}
}
