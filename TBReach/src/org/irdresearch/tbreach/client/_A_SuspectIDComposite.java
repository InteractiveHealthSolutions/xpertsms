/**
 * Patient Information Composite. This Composite handles all Medical, Personal and Contact information about a Suspect/Patient.
 * User has to first search patients using different criteria. A successful search fills up the Patients list, clicking any PatientID fills all the controls in the form.
 * 
 */
package org.irdresearch.tbreach.client;

import java.util.ArrayList;
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
import com.google.gwt.user.client.ui.DecoratedStackPanel;
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
import com.google.gwt.user.client.ui.CheckBox;

public class _A_SuspectIDComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service						= GWT.create(ServerService.class);
	private static LoadingWidget		loading						= new LoadingWidget();
	private static final String			menuName					= "FORM_A";
	private static final String			tableName					= "Patient";

	private UserRightsUtil				rights						= new UserRightsUtil();
	private boolean						valid;
	private Patient						current;
	
	private Person						currentPerson;
	private Contact						currentContact;
	private Encounter					encounter;
	private ArrayList<String>			encounterResults;

	private DecoratedStackPanel			decoratedStackPanel			= new DecoratedStackPanel();
	private HorizontalPanel				genderHorizontalPanel		= new HorizontalPanel();

	private Grid						grid						= new Grid(2, 4);
	private FlexTable					flexTable					= new FlexTable();
	private FlexTable					suspectInfoStackPanel		= new FlexTable();
	private FlexTable					personalInfoStackPanel		= new FlexTable();
	private FlexTable					rightFlexTable				= new FlexTable();
	private FlexTable					topFlexTable				= new FlexTable();
	private FlexTable					symptomsStackPanel			= new FlexTable();
	private HorizontalPanel				dateEnteredHorizontalPanel	= new HorizontalPanel();

	private Button						closeButton					= new Button("Close");
	private Button						deleteButton				= new Button("Delete");
	private Button						saveButton					= new Button("Save");
	private ToggleButton				createButton				= new ToggleButton("Create Suspect");

	private Label						lblApproximateAge			= new Label("Approximate Age:");
	private Label						lblChwid					= new Label("Screener ID:");
	private Label						lblFirstName				= new Label("First Name:");
	private Label						lblGender					= new Label("Gender:");
	private Label						lblGpid						= new Label("Provider:");
	private Label						lblLastName					= new Label("Last Name:");
	private Label						lblTbReachSuspect			= new Label("TB REACH Suspect ID Form (A)");
	private Label						lblPatientId				= new Label("Patient ID:");
	private Label						lblDateEntered				= new Label("Date Entered:");
	private Label						lblDoYouHave				= new Label("Do you have cough?");
	private Label						lblProductiveCough			= new Label("Productive cough:");
	private Label						lblBloodInCough				= new Label("Blood in cough:");
	private Label						lblHadTbBefore				= new Label("Had TB before:");
	private Label						lblAnyoneInFamlity			= new Label("Anyone in famlity had TB before:");
	private Label						lblFeverDuringLast			= new Label("Fever during last 2 weeks:");
	private Label						lblNightSweatsDuring		= new Label("Night sweats during last 2 weeks:");
	private Label						lblUnexplainedWeightLoss	= new Label("Unexplained weight loss during last 2 weeks:");

	private TextBox						patientIdTextBox			= new TextBox();
	private TextBox						firstNameTextBox			= new TextBox();
	private TextBox						lastNameTextBox				= new TextBox();

	private IntegerBox					ageTextBox					= new IntegerBox();
	private DateBox						dobDateBox					= new DateBox();
	private DateBox						enteredDateBox				= new DateBox();

	private ListBox						chwComboBox					= new ListBox();
	private ListBox						gpComboBox					= new ListBox();
	private ListBox						coughDurationComboBox		= new ListBox();
	private ListBox						coughComboBox				= new ListBox();
	private ListBox						productiveCoughComboBox		= new ListBox();
	private ListBox						haemoptysisComboBox			= new ListBox();
	private ListBox						tbHistoryComboBox			= new ListBox();
	private ListBox						familyTbHistoryComboBox		= new ListBox();
	private ListBox						feverComboBox				= new ListBox();
	private ListBox						nightSweatsComboBox			= new ListBox();
	private ListBox						weightLossComboBox			= new ListBox();

	private RadioButton					femaleRadioButton			= new RadioButton("Gender", "Female");
	private RadioButton					maleRadioButton				= new RadioButton("Gender", "Male");
	private final Label					lblCoughDuration			= new Label("Cough duration:");
	private final CheckBox				personIsTbCheckBox			= new CheckBox("Person is TB Suspect");

	public _A_SuspectIDComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("500px", "100%");
		flexTable.setWidget(0, 0, topFlexTable);
		lblTbReachSuspect.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachSuspect);
		flexTable.setWidget(1, 0, dateEnteredHorizontalPanel);
		dateEnteredHorizontalPanel.add(lblDateEntered);
		dateEnteredHorizontalPanel.setCellVerticalAlignment(lblDateEntered, HasVerticalAlignment.ALIGN_MIDDLE);
		enteredDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MMM-yyyy")));
		dateEnteredHorizontalPanel.add(enteredDateBox);
		enteredDateBox.setWidth("100px");
		flexTable.setWidget(2, 0, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, decoratedStackPanel);
		decoratedStackPanel.setWidth("100%");
		decoratedStackPanel.add(suspectInfoStackPanel, "Suspect's Information", false);
		suspectInfoStackPanel.setSize("100%", "100%");
		suspectInfoStackPanel.setWidget(0, 0, lblGpid);
		suspectInfoStackPanel.setWidget(0, 1, gpComboBox);
		suspectInfoStackPanel.setWidget(1, 0, lblChwid);
		suspectInfoStackPanel.setWidget(1, 1, chwComboBox);
		suspectInfoStackPanel.setWidget(2, 0, lblPatientId);
		patientIdTextBox.setVisibleLength(20);
		patientIdTextBox.setMaxLength(150);
		suspectInfoStackPanel.setWidget(2, 1, patientIdTextBox);
		decoratedStackPanel.add(personalInfoStackPanel, "Personal Information", false);
		personalInfoStackPanel.setSize("100%", "100%");
		personalInfoStackPanel.setWidget(0, 0, lblFirstName);
		firstNameTextBox.setVisibleLength(20);
		personalInfoStackPanel.setWidget(0, 1, firstNameTextBox);
		firstNameTextBox.setWidth("100");
		firstNameTextBox.setMaxLength(20);
		personalInfoStackPanel.setWidget(1, 0, lblLastName);
		lastNameTextBox.setVisibleLength(20);
		personalInfoStackPanel.setWidget(1, 1, lastNameTextBox);
		lastNameTextBox.setWidth("100");
		lastNameTextBox.setMaxLength(20);
		personalInfoStackPanel.setWidget(2, 0, lblApproximateAge);
		personalInfoStackPanel.setWidget(2, 1, ageTextBox);
		ageTextBox.setWidth("50px");
		// Set approximate date of birth
		ageTextBox.addValueChangeHandler(new ValueChangeHandler<Integer>()
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
		ageTextBox.setVisibleLength(5);
		ageTextBox.setMaxLength(3);
		dobDateBox.setVisible(false);
		personalInfoStackPanel.setWidget(3, 1, dobDateBox);
		dobDateBox.setWidth("100px");
		dobDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		personalInfoStackPanel.setWidget(4, 0, lblGender);
		personalInfoStackPanel.setWidget(4, 1, genderHorizontalPanel);
		genderHorizontalPanel.setSpacing(5);
		maleRadioButton.setValue(true);
		genderHorizontalPanel.add(maleRadioButton);
		maleRadioButton.setSize("100%", "100%");
		genderHorizontalPanel.add(femaleRadioButton);
		femaleRadioButton.setSize("100%", "100%");
		decoratedStackPanel.add(symptomsStackPanel, "Symptoms", false);
		symptomsStackPanel.setSize("100%", "100%");
		symptomsStackPanel.setWidget(0, 0, lblDoYouHave);
		symptomsStackPanel.setWidget(0, 1, coughComboBox);

		symptomsStackPanel.setWidget(1, 0, lblCoughDuration);
		coughDurationComboBox.addItem("LESS THAN 2 WEEKS");
		coughDurationComboBox.addItem("2 TO 3 WEEKS");
		coughDurationComboBox.addItem("MORE THAN 3 WEEKS");
		coughDurationComboBox.addItem("DON'T KNOW");
		symptomsStackPanel.setWidget(1, 1, coughDurationComboBox);
		symptomsStackPanel.setWidget(2, 0, lblProductiveCough);
		productiveCoughComboBox.setEnabled(false);
		symptomsStackPanel.setWidget(2, 1, productiveCoughComboBox);
		symptomsStackPanel.setWidget(3, 0, lblBloodInCough);
		haemoptysisComboBox.setEnabled(false);
		symptomsStackPanel.setWidget(3, 1, haemoptysisComboBox);
		symptomsStackPanel.setWidget(4, 0, lblHadTbBefore);
		symptomsStackPanel.setWidget(4, 1, tbHistoryComboBox);
		symptomsStackPanel.setWidget(5, 0, lblAnyoneInFamlity);
		symptomsStackPanel.setWidget(5, 1, familyTbHistoryComboBox);
		symptomsStackPanel.setWidget(6, 0, lblFeverDuringLast);
		symptomsStackPanel.setWidget(6, 1, feverComboBox);
		symptomsStackPanel.setWidget(7, 0, lblNightSweatsDuring);
		symptomsStackPanel.setWidget(7, 1, nightSweatsComboBox);
		symptomsStackPanel.setWidget(8, 0, lblUnexplainedWeightLoss);
		symptomsStackPanel.setWidget(8, 1, weightLossComboBox);
		rightFlexTable.setWidget(1, 0, grid);
		grid.setSize("100%", "100%");
		personIsTbCheckBox.setValue(true);

		grid.setWidget(0, 0, personIsTbCheckBox);
		createButton.setDown(true);
		grid.setWidget(1, 0, createButton);
		grid.setWidget(1, 1, saveButton);
		grid.setWidget(1, 2, deleteButton);
		grid.setWidget(1, 3, closeButton);
		flexTable.getRowFormatter().setVerticalAlign(2, HasVerticalAlignment.ALIGN_TOP);

		createButton.addClickHandler(this);
		saveButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		closeButton.addClickHandler(this);
		gpComboBox.addChangeHandler(this);
		coughComboBox.addChangeHandler(this);
		coughDurationComboBox.addChangeHandler(this);
		productiveCoughComboBox.addChangeHandler(this);

		clearUp();
		fillLists();
		//setRights(menuName);
	}

	private void fillLists()
	{
		coughComboBox.clear();
		productiveCoughComboBox.clear();
		haemoptysisComboBox.clear();
		tbHistoryComboBox.clear();
		familyTbHistoryComboBox.clear();
		feverComboBox.clear();
		nightSweatsComboBox.clear();
		weightLossComboBox.clear();

		for (String s : TBR.getList(ListType.FORM_OPTION))
		{
			coughComboBox.addItem(s);
			productiveCoughComboBox.addItem(s);
			haemoptysisComboBox.addItem(s);
			tbHistoryComboBox.addItem(s);
			familyTbHistoryComboBox.addItem(s);
			feverComboBox.addItem(s);
			nightSweatsComboBox.addItem(s);
			weightLossComboBox.addItem(s);
		}
		try
		{
			load(true);
			service.getColumnData("GP", "GPID", "", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					gpComboBox.clear();
					for (String s : result)
						gpComboBox.addItem(s);
					load(false);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					load(false);
				}
			});
			load(true);
			// Fill the list on the basis of User Role
			service.findUser(TBR.getCurrentUser(), new AsyncCallback<Users>()
			{
				@Override
				public void onSuccess(Users result)
				{
					chwComboBox.clear();
					if (result.getRole().equals("CHW"))
					{
						chwComboBox.addItem(result.getPid());
						load(false);
						return;
					}
					try
					{
						service.getColumnData("Worker", "WorkerID", "", new AsyncCallback<String[]>()
						{
							@Override
							public void onSuccess(String[] result)
							{
								for (String s : result)
									chwComboBox.addItem(s);
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

	@SuppressWarnings("deprecation")
	public void setCurrent()
	{
		/*if (!personIsTbCheckBox.getValue())
		{
			screening.setChwid(TBRClient.get(chwComboBox));
//			screening.setGpid(TBRClient.get(gpComboBox));
//			screening.setFirstName(TBRClient.get(firstNameTextBox).toUpperCase());
//			screening.setLastName(TBRClient.get(lastNameTextBox).toUpperCase());
//			screening.setDob(dobDateBox.getValue());
			screening.setGender(maleRadioButton.getValue() ? 'M' : 'F');
			screening.setCough(TBRClient.get(coughComboBox));
			screening.setCoughDuration(TBRClient.get(coughDurationComboBox));
			screening.setFever(TBRClient.get(feverComboBox));
			screening.setHaemoptysis(TBRClient.get(haemoptysisComboBox));
//			screening.setNightSweats(TBRClient.get(nightSweatsComboBox));
			screening.setProductiveCough(TBRClient.get(productiveCoughComboBox));
			screening.setTbhistory(TBRClient.get(familyTbHistoryComboBox));
//			screening.setTbfamilyHistory(TBRClient.get(tbHistoryComboBox));
//			screening.setTwoWeeksCough(TBRClient.get(coughDurationComboBox).contains("3 WEEKS") ? "YES" : "NO");
			screening.setWeightLoss(TBRClient.get(weightLossComboBox));
			screening.setDateEntered(enteredDateBox.getValue());
		}*/
		current.setProviderId(TBRClient.get(gpComboBox).toUpperCase());
		current.setScreenerId(TBRClient.get(chwComboBox).toUpperCase());
		current.setPatientStatus("SUSPECT");
		currentPerson.setFirstName(TBRClient.get(firstNameTextBox).toUpperCase());
		currentPerson.setLastName(TBRClient.get(lastNameTextBox).toUpperCase());
		currentPerson.setDob(dobDateBox.getValue());
		currentPerson.setGender(maleRadioButton.getValue() ? 'M' : 'F');
		
		//set encounter	
		encounter = new Encounter(new EncounterId(0, current.getPatientId(), TBRClient.get(chwComboBox)), "SUSPECT_ID",
				current.getTreatmentCenter(), new Date(), new Date(), enteredDateBox.getValue(), "");
		
		// Set encounter results
		encounterResults = new ArrayList<String>();
		encounterResults.add("ENTERED_DATE=" + new Date().toGMTString().replace("GMT", ""));
		encounterResults.add("GP_ID=" + current.getProviderId());
		encounterResults.add("AGE=" + String.valueOf(new Date().getYear() - currentPerson.getDob().getYear()));
		encounterResults.add("COUGH=" + TBRClient.get(coughComboBox).toUpperCase());
		boolean coughCheck = TBRClient.get(coughComboBox).equals("YES");
		boolean productiveCheck = TBRClient.get(productiveCoughComboBox).equals("YES");
		if (coughCheck)
			encounterResults.add("COUGH_DURATION=" + TBRClient.get(coughDurationComboBox));
		if (productiveCheck)
			encounterResults.add("PRODUCTIVE_COUGH=" + TBRClient.get(productiveCoughComboBox));
		encounterResults.add("HAEMOPTYSIS=" + TBRClient.get(haemoptysisComboBox));
		encounterResults.add("TWO_WEEKS_COUGH=" + (coughCheck && TBRClient.get(coughDurationComboBox).contains("3 WEEKS") ? "YES" : "NO"));
		encounterResults.add("WEIGHT_LOSS=" + TBRClient.get(weightLossComboBox));
		encounterResults.add("FEVER=" + TBRClient.get(feverComboBox));
		encounterResults.add("NIGHT_SWEATS=" + TBRClient.get(nightSweatsComboBox));
		encounterResults.add("TB_HISTORY=" + TBRClient.get(tbHistoryComboBox));
		encounterResults.add("TB_FAMILY_HISTORY=" + TBRClient.get(familyTbHistoryComboBox));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void fillData()
	{
		try
		{
			patientIdTextBox.setText(current.getPatientId());
			if (current.getProviderId() != null)
				gpComboBox.setSelectedIndex(TBRClient.getIndex(gpComboBox, current.getProviderId()));
			if (current.getScreenerId() != null)
				chwComboBox.setSelectedIndex(TBRClient.getIndex(chwComboBox, current.getScreenerId()));
			firstNameTextBox.setText(currentPerson.getFirstName());
			lastNameTextBox.setText(currentPerson.getLastName());
			if (currentPerson.getDob() != null)
			{
				dobDateBox.setValue(currentPerson.getDob());
				ageTextBox.setValue(new Date().getYear() - currentPerson.getDob().getYear());
			}
			maleRadioButton.setValue(currentPerson.getGender() == 'M');
		}
		catch (Exception e)
		{
			e.printStackTrace();
			load(false);
		}
	}

	/**
	 * Clean up controls and reset to default values
	 */
	@Override
	public void clearUp()
	{
		clearControls(flexTable);
		ageTextBox.setText("");
		personIsTbCheckBox.setValue(true);
	}

	/**
	 * To check validation rules before executing data operations
	 * Mandatory fields for this form:
	 * PatientID, FirstName, DOB/Age, Gender
	 */
	@Override
	public boolean validate()
	{
		final StringBuilder errorMessage = new StringBuilder();
		valid = true;
		// Validate mandatory fields
		if (TBRClient.get(patientIdTextBox).equals("") || TBRClient.get(firstNameTextBox).equals("") || TBRClient.get(ageTextBox).equals(""))
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		// Validate Patient ID
		{
			String id = TBRClient.get(patientIdTextBox).toUpperCase().replace("P", "");
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
		if (/*!RegexUtil.isNumeric(TBRClient.get(patientIdTextBox).toUpperCase().replace("P", ""), false)
				|| */!RegexUtil.isWord(TBRClient.get(firstNameTextBox).replace(" ", "")))
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
			valid = false;
		}
		if (!valid)
		{
			Window.alert(errorMessage.toString());
			load(false);
		}
		return valid;
	}

	/**
	 * Create and save a new object
	 */
	public void saveData()
	{
		if (validate())
		{
			/*// If the person is not a TB Suspect, create a Non Suspect
			if (!personIsTbCheckBox.getValue())
			{
				try
				{
					 Validate uniqueness 
					service.exists("NonSuspect", "GPID='" + TBRClient.get(gpComboBox) + "' AND CHWID='" + TBRClient.get(chwComboBox)
							+ "' AND FirstName='" + TBRClient.get(firstNameTextBox) + "' AND LastName='" + TBRClient.get(lastNameTextBox) + "'",
							new AsyncCallback<Boolean>()
							{
								@Override
								public void onSuccess(Boolean result)
								{
									if (!result)
									{
										screening = new Screening();
										setCurrent();
										try
										{
											service.saveScreening(screening, new AsyncCallback<Boolean>()
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
										catch(Exception e)
										{
											Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR));
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
				}
			}*/
			/* Validate uniqueness */
			try
			{
				service.exists(tableName, "PatientID='" + TBRClient.get(patientIdTextBox) + "'", new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						if (!result)
						{
							/* SAVE METHOD */
							current = new Patient(TBRClient.get(patientIdTextBox));
							currentPerson = new Person(current.getPatientId(), TBRClient.get(firstNameTextBox),
									(maleRadioButton.getValue() == true ? 'M' : 'F'));
							currentContact = new Contact(current.getPatientId());
							setCurrent();
							try
							{
								String[] type = new String[encounterResults.size()];
								service.saveNewPatient(current, currentPerson, currentContact, encounter,encounterResults,
										new AsyncCallback<Boolean>()
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
			}
		}
	}

	/**
	 * Update existing "current" objects
	 */
	@Override
	public void updateData()
	{
		Window.alert(CustomMessage.getErrorMessage(ErrorType.UPDATE_ERROR));
	}

	/**
	 * Delete "current" objects. (Delete operation is not enabled in this form)
	 */
	@Override
	public void deleteData()
	{
		Window.alert(CustomMessage.getErrorMessage(ErrorType.DELETE_ERROR));
	}

	/**
	 * Get User Rights on this form for the current User and enable/disable operations
	 */
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
								patientIdTextBox.setEnabled(rights.getAccess(AccessType.SELECT));
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

	/**
	 * Click event handler. Executes various methods according to the Sender:
	 */
	@Override
	public void onClick(ClickEvent event)
	{
		Widget sender = (Widget) event.getSource();
		load(true);
		if (sender == createButton)
		{
			if (createButton.isDown())
				clearUp();
			patientIdTextBox.setEnabled(createButton.isDown());
			enteredDateBox.setEnabled(createButton.isDown());
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

	/**
	 * OnChange Event Handler
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onChange(ChangeEvent event)
	{
		Widget sender = (Widget) event.getSource();
		/*if (sender == gpComboBox)
		{
			// Patient Id is composite of [P][##](GP No)[ddmmyy][##](counter)
			String gp = TBRClient.get(gpComboBox);
			String gpNo = gp.substring(gp.lastIndexOf('-') + 1);
			Date dt = enteredDateBox.getValue();
			int dd = dt.getDate();
			int mm = dt.getMonth() + 1;
			int yy = (dt.getYear() - 100);
			String patientId = "P" + gpNo + (dd <= 9 ? "0" : "") + String.valueOf(dd) + (mm <= 9 ? "0" : "") + String.valueOf(mm)
					+ String.valueOf(yy) + "##";
			patientIdTextBox.setText(patientId);
		}
		else*/ if (sender == coughComboBox)
		{
			coughDurationComboBox.setEnabled(TBRClient.get(coughComboBox).equals("YES"));
		}
		else if (sender == coughDurationComboBox)
		{
			productiveCoughComboBox.setEnabled(TBRClient.get(coughDurationComboBox).contains("3 WEEKS"));
		}
		else if (sender == productiveCoughComboBox)
		{
			haemoptysisComboBox.setEnabled(TBRClient.get(productiveCoughComboBox).equals("YES"));
		}
	}
}