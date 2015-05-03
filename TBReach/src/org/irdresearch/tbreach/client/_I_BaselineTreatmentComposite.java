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
import org.irdresearch.tbreach.shared.model.Encounter;
import org.irdresearch.tbreach.shared.model.EncounterId;
import org.irdresearch.tbreach.shared.model.Patient;
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
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class _I_BaselineTreatmentComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service							= GWT.create(ServerService.class);
	private static LoadingWidget		loading							= new LoadingWidget();
	private static final String			menuName						= "FORM_I";

	private UserRightsUtil				rights							= new UserRightsUtil();
	private boolean						valid;
	private Patient						current;
	private Encounter					encounter;
	private ArrayList<String>			encounterResults;

	private FlexTable					flexTable						= new FlexTable();
	private FlexTable					topFlexTable					= new FlexTable();
	private FlexTable					rightFlexTable					= new FlexTable();
	private Grid						grid							= new Grid(1, 3);
	private HorizontalPanel				horizontalPanel					= new HorizontalPanel();

	private ToggleButton				createButton					= new ToggleButton("Create");
	private Button						saveButton						= new Button("Save");
	private Button						deleteButton					= new Button("Delete");
	private Button						closeButton						= new Button("Close");

	private Label						lblTbReachBaseline				= new Label("TB REACH Baseline Treatment Form (I)");
	private Label						lblGpid							= new Label("Provider:");
	private Label						lblDate							= new Label("Date Entered:");
	private Label						lblWeight						= new Label("Weight (Kg):");
	private Label						lblHeight						= new Label("Height (cm):");
	private Label						lblNewLabel						= new Label("Treatment Phase:");
	private Label						lblPatientType					= new Label("Patient Type:");
	private Label						lblPatientCategory				= new Label("Patient Category:");
	private Label						lblDiseaseSite					= new Label("Disease Site:");
	private Label						lblRegimen						= new Label("Regimen:");
	private Label						lblDoseCombination				= new Label("Dose Combination:");
	private Label						lblStreptomycin					= new Label("Streptomycin:");
	private Label						lblPatientsId					= new Label("Patient's ID:");

	private TextBox						diseaseSiteTextBox				= new TextBox();
	private TextBox						patientIdTextBox				= new TextBox();
	private DateBox						enteredDateBox					= new DateBox();
	private DoubleBox					weightDoubleBox					= new DoubleBox();
	private DoubleBox					heightDoubleBox					= new DoubleBox();

	private ListBox						gpComboBox						= new ListBox();
	private ListBox						patientTypeComboBox				= new ListBox();
	private ListBox						treatmentPhaseComboBox			= new ListBox();
	private ListBox						regimenComboBox					= new ListBox();
	private ListBox						diseaseCategoryComboBox			= new ListBox();
	private ListBox						doseCombinationComboBox			= new ListBox();
	private ListBox						otherDoseDescriptionComboBox	= new ListBox();
	private ListBox						diseaseSiteComboBox				= new ListBox();

	public _I_BaselineTreatmentComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 0, topFlexTable);
		lblTbReachBaseline.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachBaseline);
		flexTable.setWidget(1, 0, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblGpid);
		rightFlexTable.setWidget(0, 1, gpComboBox);
		rightFlexTable.setWidget(1, 0, lblPatientsId);
		patientIdTextBox.setMaxLength(12);
		patientIdTextBox.setVisibleLength(12);
		rightFlexTable.setWidget(1, 1, patientIdTextBox);
		rightFlexTable.setWidget(2, 0, lblWeight);
		weightDoubleBox.setText("0");
		weightDoubleBox.setVisibleLength(3);
		weightDoubleBox.setMaxLength(3);
		rightFlexTable.setWidget(2, 1, weightDoubleBox);
		rightFlexTable.setWidget(3, 0, lblHeight);
		heightDoubleBox.setText("0");
		heightDoubleBox.setVisibleLength(3);
		heightDoubleBox.setMaxLength(4);
		rightFlexTable.setWidget(3, 1, heightDoubleBox);
		rightFlexTable.setWidget(4, 0, lblNewLabel);
		rightFlexTable.setWidget(4, 1, treatmentPhaseComboBox);
		rightFlexTable.setWidget(5, 0, lblPatientType);
		rightFlexTable.setWidget(5, 1, patientTypeComboBox);
		rightFlexTable.setWidget(6, 0, lblPatientCategory);
		rightFlexTable.setWidget(6, 1, diseaseCategoryComboBox);
		rightFlexTable.setWidget(7, 0, lblDiseaseSite);
		rightFlexTable.setWidget(7, 1, horizontalPanel);
		diseaseSiteComboBox.addItem("PULMONARY");
		diseaseSiteComboBox.addItem("EXTRA PULMONARY");
		horizontalPanel.add(diseaseSiteComboBox);
		diseaseSiteTextBox.setEnabled(false);
		horizontalPanel.add(diseaseSiteTextBox);
		diseaseSiteTextBox.setMaxLength(20);
		rightFlexTable.setWidget(8, 0, lblRegimen);
		rightFlexTable.setWidget(8, 1, regimenComboBox);
		rightFlexTable.setWidget(9, 0, lblDoseCombination);
		rightFlexTable.setWidget(9, 1, doseCombinationComboBox);
		rightFlexTable.setWidget(10, 0, lblStreptomycin);
		rightFlexTable.setWidget(10, 1, otherDoseDescriptionComboBox);
		rightFlexTable.setWidget(11, 0, lblDate);
		enteredDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")));
		rightFlexTable.setWidget(11, 1, enteredDateBox);
		enteredDateBox.setWidth("150px");
		createButton.setDown(true);
		createButton.setHTML("Create Baseline");
		createButton.setEnabled(false);
		rightFlexTable.setWidget(12, 0, createButton);
		rightFlexTable.setWidget(12, 1, grid);
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
		diseaseCategoryComboBox.addChangeHandler(this);
		diseaseSiteComboBox.addChangeHandler(this);

		refreshList();
		setRights(menuName);
	}

	public void refreshList()
	{
		gpComboBox.clear();
		patientTypeComboBox.clear();
		treatmentPhaseComboBox.clear();
		regimenComboBox.clear();
		diseaseCategoryComboBox.clear();
		doseCombinationComboBox.clear();
		otherDoseDescriptionComboBox.clear();

		doseCombinationComboBox.addItem("");
		otherDoseDescriptionComboBox.addItem("");
		gpComboBox.addItem(TBR.getCurrentUser());
		for (String s : TBR.getList(ListType.PATIENT_TYPE))
			patientTypeComboBox.addItem(s);
		for (String s : TBR.getList(ListType.TREATMENT_PHASE))
			treatmentPhaseComboBox.addItem(s);
		for (String s : TBR.getList(ListType.REGIMEN))
			regimenComboBox.addItem(s);
		for (String s : TBR.getList(ListType.DISEASE_CATEGORY))
			diseaseCategoryComboBox.addItem(s);
		for (String s : TBR.getList(ListType.DOSE_COMBINATION))
			doseCombinationComboBox.addItem(s);
		for (String s : TBR.getList(ListType.DOSE))
			otherDoseDescriptionComboBox.addItem(s);
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
		current.setWeight(weightDoubleBox.getValue().floatValue());
		current.setHeight(heightDoubleBox.getValue().floatValue());
		current.setTreatmentPhase(TBRClient.get(treatmentPhaseComboBox));
		current.setPatientType(TBRClient.get(patientTypeComboBox));
		current.setDiseaseCategory(TBRClient.get(diseaseCategoryComboBox));
		current.setDiseaseSite(TBRClient.get(diseaseSiteComboBox));
		current.setRegimen(TBRClient.get(regimenComboBox));
		current.setDoseCombination(Float.parseFloat(TBRClient.get(doseCombinationComboBox)));
		current.setOtherDoseDescription(TBRClient.get(otherDoseDescriptionComboBox));

		encounter = new Encounter(new EncounterId(0, current.getPatientId(), TBRClient.get(gpComboBox)), "BASELINE", current.getTreatmentCenter(),
				new Date(), new Date(), enteredDateBox.getValue(), "");
		encounterResults = new ArrayList<String>();
		encounterResults.add("HEIGHT=" + TBRClient.get(heightDoubleBox));
		encounterResults.add("WEIGHT=" + TBRClient.get(weightDoubleBox));
		encounterResults.add("TREATMENT_PHASE=" + TBRClient.get(treatmentPhaseComboBox));
		encounterResults.add("PATIENT_TYPE=" + TBRClient.get(patientTypeComboBox));
		encounterResults.add("PATIENT_CATEGORY=" + TBRClient.get(diseaseCategoryComboBox));
		encounterResults.add("DISEASE_SITE=" + TBRClient.get(diseaseSiteComboBox));
		encounterResults.add("REGIMEN=" + TBRClient.get(regimenComboBox));
		encounterResults.add("FDC_TABLETS=" + TBRClient.get(doseCombinationComboBox));
		if (!TBRClient.get(otherDoseDescriptionComboBox).equals(""))
			encounterResults.add("STREPTOMYCIN=" + TBRClient.get(otherDoseDescriptionComboBox));
		encounterResults.add("ENTERED_DATE=" + enteredDateBox.getValue().toString());
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
		}
		load(false);
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
						current = result;
						setCurrent();
						try
						{
							service.saveEncounterWithResults(encounter, encounterResults, new AsyncCallback<Boolean>()
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
							service.savePatient(current, new AsyncCallback<Boolean>()
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
				Window.alert(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR));
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
		Widget sender = (Widget) event.getSource();
		if (sender == diseaseCategoryComboBox)
		{
			diseaseSiteComboBox.setEnabled(TBRClient.get(diseaseCategoryComboBox).equals("CAT2"));
		}
		else if (sender == diseaseSiteComboBox)
		{
			diseaseSiteTextBox.setEnabled(TBRClient.get(diseaseSiteComboBox).equals("EX-PULMONERY"));
		}
	}
}