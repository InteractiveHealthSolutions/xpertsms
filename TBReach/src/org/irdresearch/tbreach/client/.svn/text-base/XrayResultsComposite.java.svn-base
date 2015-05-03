package org.irdresearch.tbreach.client;

import java.util.Date;
import java.util.Iterator;

import org.irdresearch.tbreach.shared.AccessType;
import org.irdresearch.tbreach.shared.CustomMessage;
import org.irdresearch.tbreach.shared.ErrorType;
import org.irdresearch.tbreach.shared.InfoType;
import org.irdresearch.tbreach.shared.RegexUtil;
import org.irdresearch.tbreach.shared.TBR;
import org.irdresearch.tbreach.shared.UserRightsUtil;
import org.irdresearch.tbreach.shared.model.Users;
import org.irdresearch.tbreach.shared.model.XrayResults;
import org.irdresearch.tbreach.shared.model.XrayResultsId;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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

public class XrayResultsComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service				= GWT.create(ServerService.class);
	private static LoadingWidget		loading				= new LoadingWidget();
	private static final String			menuName			= "LABORATORY";
	private static final String			tableName			= "XRayResults";

	private UserRightsUtil				rights				= new UserRightsUtil();
	private boolean						valid;
	private XrayResults					current;

	private FlexTable					flexTable			= new FlexTable();
	private FlexTable					topFlexTable		= new FlexTable();
	private FlexTable					leftFlexTable		= new FlexTable();
	private FlexTable					rightFlexTable		= new FlexTable();
	private Grid						grid				= new Grid(1, 3);

	private ListBox						IRSListBox			= new ListBox();
	private TextBox						patientIdTextBox	= new TextBox();
	private TextBox						searchPatientIdSearchBox	= new TextBox();
	private TextBox						remarksTextBox		= new TextBox();
	//private TextBox						mrNoTextBox			= new TextBox();
	private TextBox						irsTextBox			= new TextBox();

	private DateBox						dateReportedDateBox	= new DateBox();
	private DateBox						xRayDateDateBox		= new DateBox();

	private Button						searchButton		= new Button("Search");
	private ToggleButton				createButton		= new ToggleButton("Create X-Ray");
	private Button						saveButton			= new Button("Save");
	private Button						deleteButton		= new Button("Delete");
	private Button						closeButton			= new Button("Close");

	private Label						lblTbReachXray		= new Label("TB REACH X-Ray Results");
	//private Label						lblMRNo				= new Label("MR No:");
	private Label						lblPatientId		= new Label("Patient ID:");
	private Label						lblSearchPatientId	= new Label("Search Patient ID");
	private Label						lblIrs				= new Label("Xray ID:");
	private Label						lblXrayDate			= new Label("Date of X-Ray:");
	private Label						lblDateReported		= new Label("Date of Report:");
	private Label						lblXrayResults		= new Label("Conclusion:");
	private Label						lblRemarks			= new Label("Remarks:");
	private ListBox						xRayResultsComboBox	= new ListBox();

	public XrayResultsComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 1, topFlexTable);
		lblTbReachXray.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachXray);
		flexTable.setWidget(1, 0, leftFlexTable);
		leftFlexTable.setWidget(0, 0, lblSearchPatientId);
		searchPatientIdSearchBox.setVisibleLength(11);
		searchPatientIdSearchBox.setMaxLength(11);
		leftFlexTable.setWidget(1, 0, searchPatientIdSearchBox);
		searchButton.setEnabled(false);
		searchButton.setText("Search");
		leftFlexTable.setWidget(2, 0, searchButton);
		leftFlexTable.setWidget(3, 0, IRSListBox);
		IRSListBox.setVisibleItemCount(5);
		leftFlexTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(1, 1, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		/*rightFlexTable.setWidget(0, 0, lblMRNo);
		mrNoTextBox.setMaxLength(11);
		rightFlexTable.setWidget(0, 1, mrNoTextBox);*/
		rightFlexTable.setWidget(0, 0, lblPatientId);
		patientIdTextBox.setMaxLength(11);
		rightFlexTable.setWidget(0, 1, patientIdTextBox);
		rightFlexTable.setWidget(1, 0, lblIrs);
		irsTextBox.setVisibleLength(15);
		irsTextBox.setMaxLength(15);
		rightFlexTable.setWidget(1, 1, irsTextBox);
		rightFlexTable.setWidget(2, 0, lblXrayDate);
		xRayDateDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		rightFlexTable.setWidget(2, 1, xRayDateDateBox);
		rightFlexTable.setWidget(3, 0, lblDateReported);
		dateReportedDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		rightFlexTable.setWidget(3, 1, dateReportedDateBox);
		rightFlexTable.setWidget(4, 0, lblXrayResults);
		xRayResultsComboBox.addItem("");
		xRayResultsComboBox.addItem("NORMAL");
		xRayResultsComboBox.addItem("SUGGESTIVE OF TB");
		xRayResultsComboBox.addItem("SUSPICIOUS OF TB");
		xRayResultsComboBox.addItem("OTHER ABNORMALITY (SPECIFY IN REMARKS)");
		xRayResultsComboBox.addItem("IMAGE UNCLEAR");

		rightFlexTable.setWidget(4, 1, xRayResultsComboBox);
		rightFlexTable.setWidget(5, 0, lblRemarks);
		remarksTextBox.setVisibleLength(50);
		remarksTextBox.setMaxLength(255);
		rightFlexTable.setWidget(5, 1, remarksTextBox);
		createButton.setEnabled(false);
		rightFlexTable.setWidget(6, 0, createButton);
		rightFlexTable.setWidget(6, 1, grid);
		grid.setSize("100%", "100%");
		saveButton.setEnabled(false);
		grid.setWidget(0, 0, saveButton);
		deleteButton.setEnabled(false);
		grid.setWidget(0, 1, deleteButton);
		grid.setWidget(0, 2, closeButton);
		flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);

		searchButton.addClickHandler(this);
		createButton.addClickHandler(this);
		saveButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		closeButton.addClickHandler(this);
		IRSListBox.addClickHandler(this);

		setRights(menuName);
		if (createButton.isEnabled())
		{
			createButton.setDown(true);
			clearUp();
			IRSListBox.setEnabled(!createButton.isDown());
			load(false);
		}
	}

	public void refreshList()
	{
		try
		{
			service.getColumnData(tableName, "IRS", "where PatientID='" + TBRClient.get(searchPatientIdSearchBox)
					+ "')", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					IRSListBox.clear();
					for (int i = 0; i < result.length; i++)
						IRSListBox.insertItem(result[i], i);
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
			load(false);
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
		current.setXrayDate(xRayDateDateBox.getValue());
		current.setDateReported(dateReportedDateBox.getValue());
		current.setXrayResults(TBRClient.get(xRayResultsComboBox).replace("(SPECIFY IN REMARKS)", "").toUpperCase());
		current.setRemarks(TBRClient.get(remarksTextBox).toUpperCase());
	}

	@Override
	public void fillData()
	{
		try
		{
			service.findXrayResults(TBRClient.get(IRSListBox), new AsyncCallback<XrayResults>()
			{
				@Override
				public void onSuccess(XrayResults result)
				{
					current = result;
					
					patientIdTextBox.setText(current.getId().getPatientId());
					irsTextBox.setText(String.valueOf(current.getId().getIrs()));
					xRayDateDateBox.setValue(current.getXrayDate());
					dateReportedDateBox.setValue(current.getDateReported());
					xRayResultsComboBox.setSelectedIndex(TBRClient.getIndex(xRayResultsComboBox, current.getXrayResults()));
					remarksTextBox.setText(current.getRemarks());
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
		if (TBRClient.get(patientIdTextBox).equals("") || TBRClient.get(irsTextBox).equals(""))
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		/* Validate data-type rules 
		if (!RegexUtil.isNumeric(TBRClient.get(irsTextBox), false) || TBRClient.get(patientIdTextBox).length() < 11)
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
			valid = false;
		}*/
		/* Validate Patient ID */
		for(char ch : TBRClient.get(patientIdTextBox).toCharArray())
		{
			if(!Character.isDigit(ch))
			{
				errorMessage.append("Patient ID is invalid.\n");
				valid = false;
			}
		}
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
		if (validate())
		{
			/* Validate uniqueness */
			try
			{
				service.exists(tableName, "IRS='" + TBRClient.get(irsTextBox) + "'", new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						if (!result)
						{
							current = new XrayResults(new XrayResultsId(TBRClient.get(irsTextBox), TBRClient.get(patientIdTextBox)
									.toUpperCase()));
							setCurrent();
							try
							{
								service.saveXrayResults(current, new AsyncCallback<Boolean>()
								{
									@Override
									public void onSuccess(Boolean result)
									{
										if (result)
										{
											Window.alert(CustomMessage.getInfoMessage(InfoType.INSERTED));
											clearUp();
										}
										else
											Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR));
										load(false);
									}

									@Override
									public void onFailure(Throwable caught)
									{
										Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR));
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
						else
							Window.alert(CustomMessage.getErrorMessage(ErrorType.DUPLICATION_ERROR));
						load(false);
					}

					@Override
					public void onFailure(Throwable caught)
					{
						Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR));
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
	public void updateData()
	{
		if (validate())
		{
			try
			{
				setCurrent();
				service.updateXrayResults(current, new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						if (result)
							Window.alert(CustomMessage.getInfoMessage(InfoType.UPDATED));
						else
							Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
						load(false);
					}

					@Override
					public void onFailure(Throwable caught)
					{
						Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace();
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
				if (!Window.confirm(CustomMessage.getInfoMessage(InfoType.CONFIRM_OPERATION)))
					return;
				service.deleteXrayResults(current, new AsyncCallback<Boolean>()
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
					}
				});
			}
			catch (Exception e)
			{
				e.printStackTrace();
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
								searchButton.setEnabled(rights.getAccess(AccessType.SELECT));
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
		if (sender == searchButton)
		{
			refreshList();
		}
		else if (sender == IRSListBox)
		{
			fillData();
		}
		else if (sender == createButton)
		{
			if (createButton.isDown())
				clearUp();
			IRSListBox.setEnabled(!createButton.isDown());
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
		// Not implemented
	}
}
