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
import org.irdresearch.tbreach.shared.model.Patient;
import org.irdresearch.tbreach.shared.model.SputumResults;
import org.irdresearch.tbreach.shared.model.Users;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class SputumResultsComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service					= GWT.create(ServerService.class);
	private static LoadingWidget		loading					= new LoadingWidget();
	private static final String			menuName				= "LABORATORY";
	private static final String			tableName				= "SputumResults";
	private UserRightsUtil				rights					= new UserRightsUtil();
	private boolean						valid;
	private SputumResults				current;
	private String						currentPatientID;

	private FlexTable					flexTable				= new FlexTable();
	private FlexTable					topFlexTable			= new FlexTable();
	private FlexTable					rightFlexTable			= new FlexTable();
	private FlexTable					leftFlexTable			= new FlexTable();
	private Grid						grid					= new Grid(1, 3);

	private Button						saveButton				= new Button("Save");
	private Button						closeButton				= new Button("Close");
	private Button						searchButton			= new Button("Search");

	private Label						lblTbReachLab			= new Label("TB REACH Smear Results");
	private Label						lblPatientId			= new Label("Patient ID:");
	private Label						lblSputumTestId			= new Label("Barcode:");
	private Label						lblMonth				= new Label("Month:");
	private Label						lblSmearResults			= new Label("Smear Result:");
	private Label						lblRemarks				= new Label("Remarks:");
	private Label						lblPatientsuspectId		= new Label("Patient ID");
	//private Label						lblIrsNo				= new Label("IRS No:");

	private TextBox						patientIdTextBox		= new TextBox();
	private TextBox						sputumTestIdTextBox		= new TextBox();
	private TextBox						monthTextBox			= new TextBox();
	private TextBox						remarksTextBox			= new TextBox();
	private TextBox						patientIDSearchTextBox	= new TextBox();
	//private TextBox						IRSTextBox				= new TextBox();

	private ListBox						smearResultsComboBox	= new ListBox();
	private ListBox						sputumIdListBox			= new ListBox();

	public SputumResultsComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 1, topFlexTable);
		lblTbReachLab.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachLab);
		flexTable.setWidget(1, 0, leftFlexTable);
		leftFlexTable.setWidget(0, 0, lblPatientsuspectId);
		leftFlexTable.setWidget(1, 0, patientIDSearchTextBox);
		patientIDSearchTextBox.setVisibleLength(12);
		patientIDSearchTextBox.setMaxLength(12);
		searchButton.setEnabled(false);
		searchButton.setText("Search");
		leftFlexTable.setWidget(2, 0, searchButton);
		leftFlexTable.setWidget(3, 0, sputumIdListBox);
		sputumIdListBox.setVisibleItemCount(5);
		leftFlexTable.getCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_LEFT);
		leftFlexTable.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		leftFlexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(1, 1, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblPatientId);
		patientIdTextBox.setVisibleLength(12);
		patientIdTextBox.setMaxLength(12);
		rightFlexTable.setWidget(0, 1, patientIdTextBox);
		rightFlexTable.setWidget(1, 0, lblSputumTestId);
		sputumTestIdTextBox.setVisibleLength(8);
		sputumTestIdTextBox.setMaxLength(8);
		rightFlexTable.setWidget(1, 1, sputumTestIdTextBox);
		/*rightFlexTable.setWidget(2, 0, lblIrsNo);
		IRSTextBox.setVisibleLength(9);
		IRSTextBox.setMaxLength(9);
		rightFlexTable.setWidget(2, 1, IRSTextBox);*/
		rightFlexTable.setWidget(3, 0, lblMonth);
		monthTextBox.setMaxLength(2);
		monthTextBox.setVisibleLength(2);
		rightFlexTable.setWidget(3, 1, monthTextBox);
		rightFlexTable.setWidget(4, 0, lblSmearResults);
		smearResultsComboBox.addItem("");
		smearResultsComboBox.addItem("NEGATIVE");
		smearResultsComboBox.addItem("1+");
		smearResultsComboBox.addItem("2+");
		smearResultsComboBox.addItem("3+");
		smearResultsComboBox.addItem("1-9AFB");
		rightFlexTable.setWidget(4, 1, smearResultsComboBox);
		rightFlexTable.setWidget(5, 0, lblRemarks);
		remarksTextBox.setVisibleLength(200);
		remarksTextBox.setMaxLength(255);
		rightFlexTable.setWidget(5, 1, remarksTextBox);
		remarksTextBox.setWidth("100%");
		rightFlexTable.setWidget(6, 1, grid);
		grid.setSize("100%", "100%");
		saveButton.setEnabled(false);
		grid.setWidget(0, 0, saveButton);
		grid.setWidget(0, 2, closeButton);
		flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
		saveButton.addClickHandler(this);
		searchButton.addClickHandler(this);
		sputumIdListBox.addClickHandler(this);
		closeButton.addClickHandler(this);

		setRights(menuName);
	}

	public void refreshList()
	{
		try
		{
			service.getObject("Patient", "PatientID", "PatientID='" + TBRClient.get(patientIDSearchTextBox) + "'", new AsyncCallback<String>()
			{
				@Override
				public void onSuccess(String result)
				{
					currentPatientID = result;
					try
					{
						service.getColumnData(tableName, "SputumTestID", "PatientID='" + currentPatientID + "' and ifnull(Remarks, '') <> '' and Remarks not like 'REJECTED%'",
								new AsyncCallback<String[]>()
								{
									@Override
									public void onSuccess(String[] result)
									{
										sputumIdListBox.clear();
										for (int i = 0; i < result.length; i++)
											sputumIdListBox.insertItem(result[i], i);
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
				public void onFailure(Throwable caught)
				{
					Window.alert(CustomMessage.getErrorMessage(ErrorType.ITEM_NOT_FOUND));
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
			service.findSputumResults(currentPatientID, TBRClient.get(sputumIdListBox), new AsyncCallback<SputumResults>()
			{
				@Override
				public void onSuccess(SputumResults result)
				{
					current = result;
					sputumTestIdTextBox.setText(String.valueOf(current.getSputumTestId()));
					patientIdTextBox.setText(current.getPatientId());
					monthTextBox.setText(String.valueOf(current.getMonth()));
					//IRSTextBox.setText(String.valueOf(current.getIrs()));
					smearResultsComboBox.setSelectedIndex(TBRClient.getIndex(smearResultsComboBox, current.getSmearResult()));
					remarksTextBox.setText(current.getRemarks());
					clearControls(leftFlexTable);
					load(false);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					Window.alert(CustomMessage.getErrorMessage(ErrorType.ITEM_NOT_FOUND));
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
		current.setDateTested(new Date());
		current.setMonth(Integer.parseInt(TBRClient.get(monthTextBox)));
		current.setSmearResult(TBRClient.get(smearResultsComboBox).toUpperCase());
		//current.setIrs(Integer.parseInt(TBRClient.get(IRSTextBox)));
		current.setRemarks(TBRClient.get(remarksTextBox).toUpperCase());
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
		if (TBRClient.get(patientIdTextBox).equals("") || TBRClient.get(sputumTestIdTextBox).equals(""))
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		/* Validate data-type rules 
		if (!RegexUtil.isNumeric(TBRClient.get(IRSTextBox), false))
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
			valid = false;
		}*/
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
				service.exists(tableName, "ColumnName='" + "VALUE" + "'", new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						if (!result)
						{
							/* SAVE METHOD */
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
				final Boolean isTBPositive = !TBRClient.get(smearResultsComboBox).equalsIgnoreCase("NEGATIVE");
				if (isTBPositive)
				{
					String passcode = Window
							.prompt("This is a sensitive operation and requires authentication.\nPlease enter first 4 characters of your password to proceed.",
									"");
					if (!TBRClient.verifyClientPasscode(passcode))
					{
						Window.alert(CustomMessage.getErrorMessage(ErrorType.AUTHENTICATION_ERROR));
						load(false);
						return;
					}
				}
				setCurrent();
				service.updateSputumResults(current, isTBPositive, new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						if (result)
						{
							if (isTBPositive)
							{
								try
								{
									service.findPatient(currentPatientID, new AsyncCallback<Patient>()
									{
										@Override
										public void onSuccess(Patient result)
										{
											result.setDiseaseConfirmed(new Boolean(true));
											try
											{
												service.updatePatient(result, new AsyncCallback<Boolean>()
												{
													@Override
													public void onSuccess(Boolean result)
													{
														if (result)
														{
															Window.alert(CustomMessage.getInfoMessage(InfoType.UPDATED));
															clearUp();
														}
														else
															Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
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
							Window.alert(CustomMessage.getInfoMessage(InfoType.UPDATED));
						}
						else
							Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
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
	}

	@Override
	public void deleteData()
	{
		if (validate())
		{
			try
			{
				// Not implemented
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
								sputumIdListBox.setEnabled(rights.getAccess(AccessType.SELECT));
								searchButton.setEnabled(rights.getAccess(AccessType.SELECT));
								saveButton.setEnabled(rights.getAccess(AccessType.UPDATE));
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
		if (sender == sputumIdListBox)
		{
			fillData();
		}
		else if (sender == searchButton)
		{
			refreshList();
		}
		else if (sender == saveButton)
		{
			updateData();
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
