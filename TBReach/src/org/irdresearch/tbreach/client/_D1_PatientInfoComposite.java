package org.irdresearch.tbreach.client;

import java.util.Date;
import java.util.Iterator;

import org.irdresearch.tbreach.shared.AccessType;
import org.irdresearch.tbreach.shared.CustomMessage;
import org.irdresearch.tbreach.shared.ErrorType;
import org.irdresearch.tbreach.shared.InfoType;
import org.irdresearch.tbreach.shared.ListType;
import org.irdresearch.tbreach.shared.RegexUtil;
import org.irdresearch.tbreach.shared.TBR;
import org.irdresearch.tbreach.shared.UserRightsUtil;
import org.irdresearch.tbreach.shared.model.Contact;
import org.irdresearch.tbreach.shared.model.Encounter;
import org.irdresearch.tbreach.shared.model.EncounterId;
import org.irdresearch.tbreach.shared.model.Patient;
import org.irdresearch.tbreach.shared.model.Person;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;

public class _D1_PatientInfoComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service					= GWT.create(ServerService.class);
	private static LoadingWidget		loading					= new LoadingWidget();
	private static final String			menuName				= "FORM_D1";

	private UserRightsUtil				rights					= new UserRightsUtil();
	private boolean						valid;
	private Patient						current;
	private Person						currentPerson;
	private Contact						currentContact;
	private Encounter					encounter;

	private FlexTable					flexTable				= new FlexTable();
	private FlexTable					topFlexTable			= new FlexTable();
	private FlexTable					rightFlexTable			= new FlexTable();
	private FlexTable					addressFlexTable		= new FlexTable();
	private FlexTable					householdFlexTable		= new FlexTable();
	private Grid						grid					= new Grid(1, 3);
	private HorizontalPanel				genderHorizontalPanel	= new HorizontalPanel();

	private ToggleButton				createButton			= new ToggleButton("Create Patient's Info");
	private Button						saveButton				= new Button("Save");
	private Button						deleteButton			= new Button("Delete");
	private Button						closeButton				= new Button("Close");

	private Label						lblTbReachPatientInfo	= new Label("TB REACH Patient Information Form (D1)");
	private Label						lblMonitorId			= new Label("Screener ID:");
	private Label						lblSuspectsId			= new Label("Suspect's ID:");
	/*private Label						lblNic					= new Label("NIC:");
	private Label						lblNicType				= new Label("NIC Type:");
	private Label						lblNicOwner				= new Label("NIC Owner:");*/
	private Label						lblFathersFirstName		= new Label("Father's First Name:");
	private Label						lblFathersLastName		= new Label("Father's Last Name:");
	private Label						lblDateOfBirth			= new Label("Date of Birth:");
	private Label						lblAge					= new Label("Age:");
	private Label						lblGender				= new Label("Gender:");
	private Label						lblMaritalStatus		= new Label("Marital Status:");
	//private Label						lblEthnicity			= new Label("Ethnicity:");
	private Label						lblPhone				= new Label("Phone:");
	private Label						lblAddress				= new Label("Address:");
	private Label						lblHouse				= new Label("House:");
	//private Label						lblGasMeterNo			= new Label("Gas Meter No:");
	private Label						lblStreet				= new Label("Street:");
	//private Label						lblSector				= new Label("Sector:");
	private Label						lblCity					= new Label("City:");
	//private Label						lblTown					= new Label("Town:");
	//private Label						lblLandmark				= new Label("Landmark:");
	//private Label						lblUnionCouncil			= new Label("Union Council:");
	private Label						lblDateEntered			= new Label("Date Entered:");
	//private Label						lblHouseholdPeople		= new Label("Household People:");
	//private Label						lblTotal				= new Label("Total:");
	//private Label						lblAdults				= new Label("Adults:");
	//private Label						lblChildren				= new Label("Children:");

	private TextBox						patientIdTextBox		= new TextBox();
	private TextBox						phoneTextBox			= new TextBox();
	private TextBox						houseTextBox			= new TextBox();
	private TextBox						streetTextBox			= new TextBox();
	//private TextBox						sectorTextBox			= new TextBox();
	private TextBox						cityTextBox				= new TextBox();
	/*private TextBox						townTextBox				= new TextBox();
	private TextBox						landmarkTextBox			= new TextBox();
	private TextBox						ucTextBox				= new TextBox();
	private TextBox						gasMeterNoTextBox		= new TextBox();
	private TextBox						nicTextBox				= new TextBox();
	private TextBox						nicOwnerTextBox			= new TextBox();*/
	private TextBox						fatherFirstNameTextBox	= new TextBox();
	private TextBox						fatherLastNameTextBox	= new TextBox();

	private IntegerBox					ageIntegerBox			= new IntegerBox();
	/*private IntegerBox					adultsIntegerBox		= new IntegerBox();
	private IntegerBox					childrenIntegerBox		= new IntegerBox();
	private IntegerBox					totalIntegerBox			= new IntegerBox();*/
	private DateBox						dobDateBox				= new DateBox();
	private DateBox						enteredDateBox			= new DateBox();

	//private ListBox						nicTypeComboBox			= new ListBox();
	private ListBox						staffIdComboBox			= new ListBox();
	private ListBox						maritalStatusComboBox	= new ListBox();
	//private ListBox						casteComboBox			= new ListBox();

	private RadioButton					maleRadioButton			= new RadioButton("Gender", "Male");
	private RadioButton					femaleRadioButton		= new RadioButton("Gender", "Female");

	public _D1_PatientInfoComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 0, topFlexTable);
		lblTbReachPatientInfo.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachPatientInfo);
		flexTable.setWidget(1, 0, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblMonitorId);
		rightFlexTable.setWidget(0, 1, staffIdComboBox);
		rightFlexTable.setWidget(1, 0, lblSuspectsId);
		patientIdTextBox.setVisibleLength(12);
		patientIdTextBox.setMaxLength(50);
		rightFlexTable.setWidget(1, 1, patientIdTextBox);
		/*rightFlexTable.setWidget(2, 0, lblNic);
		nicTextBox.setMaxLength(15);
		nicTextBox.setVisibleLength(15);
		rightFlexTable.setWidget(2, 1, nicTextBox);
		rightFlexTable.setWidget(3, 0, lblNicType);
		nicTypeComboBox.addItem("COMPUTERIZED");
		nicTypeComboBox.addItem("OLD");
		rightFlexTable.setWidget(3, 1, nicTypeComboBox);
		rightFlexTable.setWidget(4, 0, lblNicOwner);
		nicOwnerTextBox.setVisibleLength(20);
		nicOwnerTextBox.setMaxLength(50);
		rightFlexTable.setWidget(4, 1, nicOwnerTextBox);*/
		rightFlexTable.setWidget(5, 0, lblFathersFirstName);
		fatherFirstNameTextBox.setVisibleLength(20);
		fatherFirstNameTextBox.setMaxLength(150);
		rightFlexTable.setWidget(5, 1, fatherFirstNameTextBox);
		rightFlexTable.setWidget(6, 0, lblFathersLastName);
		fatherLastNameTextBox.setVisibleLength(20);
		fatherLastNameTextBox.setMaxLength(50);
		rightFlexTable.setWidget(6, 1, fatherLastNameTextBox);
		rightFlexTable.setWidget(7, 0, lblAge);
		// Set approximate date of birth
		ageIntegerBox.addValueChangeHandler(new ValueChangeHandler<Integer>()
		{
			@SuppressWarnings("deprecation")
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				Date approxDOB = new Date();
				approxDOB.setYear(approxDOB.getYear() - event.getValue());
				approxDOB.setMonth(5);
				approxDOB.setDate(30);
				dobDateBox.setValue(approxDOB);
			}
		});
		ageIntegerBox.setVisibleLength(3);
		rightFlexTable.setWidget(7, 1, ageIntegerBox);
		rightFlexTable.setWidget(8, 0, lblDateOfBirth);
		dobDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MMM-yyyy")));
		rightFlexTable.setWidget(8, 1, dobDateBox);
		dobDateBox.setWidth("100px");
		rightFlexTable.setWidget(9, 0, lblGender);
		rightFlexTable.setWidget(9, 1, genderHorizontalPanel);
		maleRadioButton.setValue(true);
		genderHorizontalPanel.add(maleRadioButton);
		genderHorizontalPanel.add(femaleRadioButton);
		rightFlexTable.setWidget(10, 0, lblMaritalStatus);
		rightFlexTable.setWidget(10, 1, maritalStatusComboBox);
		/*rightFlexTable.setWidget(11, 0, lblEthnicity);
		rightFlexTable.setWidget(11, 1, casteComboBox);*/
		rightFlexTable.setWidget(12, 0, lblPhone);
		phoneTextBox.setVisibleLength(20);
		phoneTextBox.setMaxLength(20);
		rightFlexTable.setWidget(12, 1, phoneTextBox);
		rightFlexTable.setWidget(13, 0, lblAddress);
		rightFlexTable.setWidget(13, 1, addressFlexTable);
		addressFlexTable.setWidget(0, 0, lblHouse);
		houseTextBox.setMaxLength(50);
		houseTextBox.setVisibleLength(20);
		addressFlexTable.setWidget(0, 1, houseTextBox);
		addressFlexTable.setWidget(1, 0, lblStreet);
		streetTextBox.setMaxLength(50);
		streetTextBox.setVisibleLength(20);
		addressFlexTable.setWidget(1, 1, streetTextBox);
		/*addressFlexTable.setWidget(2, 0, lblSector);
		sectorTextBox.setMaxLength(50);
		sectorTextBox.setVisibleLength(20);
		addressFlexTable.setWidget(2, 1, sectorTextBox);*/
		addressFlexTable.setWidget(3, 0, lblCity);
		cityTextBox.setMaxLength(50);
		cityTextBox.setVisibleLength(20);
		addressFlexTable.setWidget(3, 1, cityTextBox);
		/*addressFlexTable.setWidget(4, 0, lblTown);
		townTextBox.setMaxLength(50);
		townTextBox.setVisibleLength(20);
		addressFlexTable.setWidget(4, 1, townTextBox);
		addressFlexTable.setWidget(5, 0, lblLandmark);
		landmarkTextBox.setMaxLength(50);
		landmarkTextBox.setVisibleLength(20);
		addressFlexTable.setWidget(5, 1, landmarkTextBox);
		addressFlexTable.setWidget(6, 0, lblUnionCouncil);
		ucTextBox.setMaxLength(50);
		ucTextBox.setVisibleLength(20);
		addressFlexTable.setWidget(6, 1, ucTextBox);
		addressFlexTable.setWidget(7, 0, lblGasMeterNo);
		gasMeterNoTextBox.setMaxLength(50);
		gasMeterNoTextBox.setVisibleLength(20);
		addressFlexTable.setWidget(7, 1, gasMeterNoTextBox);
		rightFlexTable.setWidget(14, 0, lblHouseholdPeople);
		rightFlexTable.setWidget(14, 1, householdFlexTable);
		householdFlexTable.setWidget(0, 0, lblTotal);
		totalIntegerBox.setVisibleLength(2);
		householdFlexTable.setWidget(0, 1, totalIntegerBox);
		householdFlexTable.setWidget(1, 0, lblAdults);
		adultsIntegerBox.setVisibleLength(2);
		householdFlexTable.setWidget(1, 1, adultsIntegerBox);
		householdFlexTable.setWidget(2, 0, lblChildren);
		childrenIntegerBox.setVisibleLength(2);
		householdFlexTable.setWidget(2, 1, childrenIntegerBox);*/
		rightFlexTable.setWidget(15, 0, lblDateEntered);
		enteredDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")));
		rightFlexTable.setWidget(15, 1, enteredDateBox);
		enteredDateBox.setWidth("150px");
		createButton.setDown(true);
		createButton.setEnabled(false);
		rightFlexTable.setWidget(16, 0, createButton);
		rightFlexTable.setWidget(16, 1, grid);
		grid.setSize("100%", "100%");
		saveButton.setEnabled(false);
		grid.setWidget(0, 0, saveButton);
		deleteButton.setEnabled(false);
		grid.setWidget(0, 1, deleteButton);
		grid.setWidget(0, 2, closeButton);
		rightFlexTable.getCellFormatter().setVerticalAlignment(13, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);

		createButton.addClickHandler(this);
		saveButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		closeButton.addClickHandler(this);

		refreshList();
		setRights(menuName);
	}

	public void refreshList()
	{
		try
		{
			maritalStatusComboBox.clear();
			//casteComboBox.clear();
			for (String s : TBR.getList(ListType.MARITAL_STATUS))
				maritalStatusComboBox.addItem(s);
			/*for (String s : TBR.getList(ListType.CASTE))
				casteComboBox.addItem(s);*/
			staffIdComboBox.clear();
			staffIdComboBox.addItem(TBR.getCurrentUser());
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
		else if (w instanceof DateBox)
		{
			((DateBox) w).setValue(null);
		}
		else if (w instanceof IntegerBox)
		{
			((IntegerBox) w).setValue(0);
		}
		else if (w instanceof DoubleBox)
		{
			((DoubleBox) w).setValue(0D);
		}
	}

	public void setCurrent()
	{
		String guardianName = TBRClient.get(fatherFirstNameTextBox) + " " + TBRClient.get(fatherLastNameTextBox);
		currentPerson.setGuardianName(guardianName.toUpperCase());
		currentPerson.setDob(dobDateBox.getValue());
		currentPerson.setGender(maleRadioButton.getValue() ? 'M' : 'F');
		//currentPerson.setCaste(TBRClient.get(casteComboBox).toUpperCase());
		currentPerson.setMaritalStatus(TBRClient.get(maritalStatusComboBox).toUpperCase());
		//currentPerson.setNic(TBRClient.get(nicTextBox).toUpperCase());
		currentContact.setAddressHouse(TBRClient.get(houseTextBox).toUpperCase());
		//currentContact.setAddressSector(TBRClient.get(sectorTextBox).toUpperCase());
		currentContact.setAddressStreet(TBRClient.get(streetTextBox).toUpperCase());
		//currentContact.setAddressTown(TBRClient.get(townTextBox).toUpperCase());
		currentContact.setAddressCity(TBRClient.get(cityTextBox).toUpperCase());
		//currentContact.setAddressLandMark(TBRClient.get(landmarkTextBox).toUpperCase());
		//currentContact.setCityId(21);
		//currentContact.setCountryId(92);
		//currentContact.setMeterNo(TBRClient.get(gasMeterNoTextBox).toUpperCase());
		currentContact.setMobile(TBRClient.get(phoneTextBox).toUpperCase());
		//currentContact.setRegion(0);
		//currentContact.setHouseHoldAdults(adultsIntegerBox.getValue());
		//currentContact.setHouseHoldChildren(childrenIntegerBox.getValue());

		encounter = new Encounter(new EncounterId(0, current.getPatientId(), TBRClient.get(staffIdComboBox)), "P_INFO", current.getTreatmentCenter(),
				new Date(), new Date(), enteredDateBox.getValue(), "");
	}

	@Override
	public void fillData()
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

	@Override
	public void clearUp()
	{
		clearControls(flexTable);
		dobDateBox.setValue(null);
		enteredDateBox.setValue(null);
		ageIntegerBox.setValue(null);
		/*totalIntegerBox.setValue(null);
		adultsIntegerBox.setValue(null);
		childrenIntegerBox.setValue(null);*/
	}

	@Override
	public boolean validate()
	{
		final StringBuilder errorMessage = new StringBuilder();
		valid = true;
		/* Validate mandatory fields */
		if (TBRClient.get(patientIdTextBox).equals(""))
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		// Validate Patient ID
		{
			String id = TBRClient.get(patientIdTextBox).toUpperCase();//.replace("P", "");
			/*if (id.length() < 10)
			{
				errorMessage.append("Invalid Patient ID" + "\n");
				valid = false;
			}*/
			if (!Window.prompt("Please re-enter Patient ID for confirmation", "").endsWith(id))
			{
				errorMessage.append("Patient ID does not match. Please try again." + "\n");
				valid = false;
			}
		}
		// Validate data-type rules
		/*if (!RegexUtil.isNumeric(TBRClient.get(patientIdTextBox).toUpperCase(),false))//.replace("P", ""), false))
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
				service.findPatient(TBRClient.get(patientIdTextBox), new AsyncCallback<Patient>()
				{
					@Override
					public void onSuccess(Patient result)
					{
						try
						{
							if (result == null)
								throw new Exception();
							current = result;
							if (current.getPatientStatus() == null || current.getPatientStatus().equals(""))
							{
								Window.alert("Some primary Information is missing for this Patient. Make sure pre-requisite form(s) are filled.");
								load(false);
								return;
							}
							service.findPerson(current.getPatientId(), new AsyncCallback<Person>()
							{
								@Override
								public void onSuccess(Person result)
								{
									currentPerson = result;
									try
									{
										service.findContact(result.getPid(), new AsyncCallback<Contact>()
										{
											@Override
											public void onSuccess(Contact result)
											{
												currentContact = result;
												setCurrent();
												try
												{
													service.updatePerson(currentPerson, new AsyncCallback<Boolean>()
													{
														@Override
														public void onSuccess(Boolean result)
														{
															try
															{
																service.updateContact(currentContact, new AsyncCallback<Boolean>()
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
																		Window.alert("Patient's information was not updated.");
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
											public void onFailure(Throwable caught)
											{
												// Do nothing
											}
										});
										service.saveEncounter(encounter, new AsyncCallback<Boolean>()
										{
											@Override
											public void onSuccess(Boolean result)
											{
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
									// Do nothing
								}
							});
						}
						catch (Exception e)
						{
							Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
							load(false);
						}
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
		Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
	}

	@Override
	public void deleteData()
	{
		Window.alert(CustomMessage.getErrorMessage(ErrorType.DELETE_ERROR));
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
