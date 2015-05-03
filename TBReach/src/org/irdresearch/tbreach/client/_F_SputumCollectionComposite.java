package org.irdresearch.tbreach.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.irdresearch.tbreach.shared.AccessType;
import org.irdresearch.tbreach.shared.CustomMessage;
import org.irdresearch.tbreach.shared.ErrorType;
import org.irdresearch.tbreach.shared.InfoType;
import org.irdresearch.tbreach.shared.RegexUtil;
import org.irdresearch.tbreach.shared.TBR;
import org.irdresearch.tbreach.shared.UserRightsUtil;
import org.irdresearch.tbreach.shared.model.Encounter;
import org.irdresearch.tbreach.shared.model.EncounterId;
import org.irdresearch.tbreach.shared.model.GeneXpertResults;
import org.irdresearch.tbreach.shared.model.Patient;
import org.irdresearch.tbreach.shared.model.SputumResults;
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
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;

public class _F_SputumCollectionComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service					= GWT.create(ServerService.class);
	private static LoadingWidget		loading					= new LoadingWidget();
	private static final String			menuName				= "FORM_F";

	private UserRightsUtil				rights					= new UserRightsUtil();
	private boolean						valid;
	private Patient						current;
	private Encounter					encounter;
	private ArrayList<String>			encounterResults;
	private GeneXpertResults			geneXpertResults		= null;
	private SputumResults				sputumResults			= null;

	private FlexTable					flexTable				= new FlexTable();
	private FlexTable					topFlexTable			= new FlexTable();
	private FlexTable					rightFlexTable			= new FlexTable();
	private Grid						grid					= new Grid(1, 3);

	private ToggleButton				createButton			= new ToggleButton("Create Collection");
	private Button						saveButton				= new Button("Save");
	private Button						deleteButton			= new Button("Delete");
	private Button						closeButton				= new Button("Close");

	private Label						lblTbReachPatient		= new Label("TB REACH Sputum Collection form (F)");
	private Label						lblChwmonitorId			= new Label("Collector ID:");
	private Label						lblSuspectsId			= new Label("Suspect's ID:");
	private Label						lblDateEntered			= new Label("Date Entered:");
	private Label						lblPatientsStatus		= new Label("Patient's Status:");
	private Label						lblMonthOfCollection	= new Label("Month of collection:");
	private Label						lblSampleNo				= new Label("Sample No:");
	private Label						lblBarcode				= new Label("Sample ID:");
	private Label						lblSputumCollected		= new Label("Sputum collected:");

	private TextBox						patientIdTextBox		= new TextBox();
	private TextBox						barcodeTextBox			= new TextBox();
	private DateBox						enteredDateBox			= new DateBox();
	private IntegerBox					sampleNoIntegerBox		= new IntegerBox();

	private ListBox						staffIdComboBox			= new ListBox();
	private ListBox						sputumMonthComboBox		= new ListBox();
	private ListBox						patientStatusComboBox	= new ListBox();
	private ListBox						sputumCollectedComboBox	= new ListBox();
	private Label						lblDoSmear				= new Label("Order Smear");
	private Label						lblDoXpert				= new Label("Order GeneXpert");
	
	private CheckBox					doSmearCheckBox			= new CheckBox();
	private CheckBox					doXpertCheckBox			= new CheckBox();

	public _F_SputumCollectionComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 0, topFlexTable);
		lblTbReachPatient.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachPatient);
		flexTable.setWidget(1, 0, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblChwmonitorId);
		rightFlexTable.setWidget(0, 1, staffIdComboBox);
		rightFlexTable.setWidget(1, 0, lblSuspectsId);
		patientIdTextBox.setMaxLength(12);
		patientIdTextBox.setVisibleLength(50);
		rightFlexTable.setWidget(1, 1, patientIdTextBox);
		rightFlexTable.setWidget(2, 0, lblPatientsStatus);
		patientStatusComboBox.addItem("SUSPECT");
		patientStatusComboBox.addItem("CONFIRMED");
		rightFlexTable.setWidget(2, 1, patientStatusComboBox);
		rightFlexTable.setWidget(3, 0, lblSputumCollected);
		sputumCollectedComboBox.addItem("YES");
		sputumCollectedComboBox.addItem("NO");
		rightFlexTable.setWidget(3, 1, sputumCollectedComboBox);
		rightFlexTable.setWidget(4, 0, lblMonthOfCollection);
		sputumMonthComboBox.addItem("0");
		sputumMonthComboBox.addItem("1");
		sputumMonthComboBox.addItem("2");
		sputumMonthComboBox.addItem("3");
		sputumMonthComboBox.addItem("4");
		sputumMonthComboBox.addItem("5");
		sputumMonthComboBox.addItem("6");
		sputumMonthComboBox.addItem("7");
		sputumMonthComboBox.addItem("8");
		sputumMonthComboBox.addItem("9");
		sputumMonthComboBox.addItem("10");
		rightFlexTable.setWidget(4, 1, sputumMonthComboBox);
		rightFlexTable.setWidget(5, 0, lblSampleNo);
		sampleNoIntegerBox.setVisibleLength(1);
		sampleNoIntegerBox.setMaxLength(1);
		rightFlexTable.setWidget(5, 1, sampleNoIntegerBox);
		rightFlexTable.setWidget(6, 0, lblBarcode);
		barcodeTextBox.setMaxLength(6);
		barcodeTextBox.setVisibleLength(6);
		rightFlexTable.setWidget(6, 1, barcodeTextBox);
		rightFlexTable.setWidget(7, 0, lblDateEntered);
		enteredDateBox.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")));
		rightFlexTable.setWidget(7, 1, enteredDateBox);
		enteredDateBox.setWidth("150px");
		
		rightFlexTable.setWidget(8, 0, lblDoSmear);
		rightFlexTable.setWidget(8, 1, doSmearCheckBox);
		
		rightFlexTable.setWidget(9, 0, lblDoXpert);
		rightFlexTable.setWidget(9, 1, doXpertCheckBox);
		
		createButton.setDown(true);
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
		sputumCollectedComboBox.addChangeHandler(this);

		refreshList();
		setRights(menuName);
	}

	public void refreshList()
	{
		try
		{
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
		encounter = new Encounter(new EncounterId(0, current.getPatientId(), TBRClient.get(staffIdComboBox)), "SPUTUM_COL",
				current.getTreatmentCenter(), new Date(), new Date(), enteredDateBox.getValue(), "");
		encounterResults = new ArrayList<String>();
		encounterResults.add("PATIENT_STATUS=" + TBRClient.get(patientStatusComboBox));
		encounterResults.add("SUSPECT_COLLECTED=" + TBRClient.get(sputumCollectedComboBox));
		if (TBRClient.get(sputumCollectedComboBox).equals("YES"))
		{
			encounterResults.add("SAMPLE_BARCODE=" + TBRClient.get(barcodeTextBox));
			encounterResults.add("COLLECTION_MONTH=" + TBRClient.get(sputumMonthComboBox));
			encounterResults.add("SUSPECT_SAMPLE=" + TBRClient.get(sampleNoIntegerBox));
			encounterResults.add("DO_SMEAR=" + (doSmearCheckBox.getValue()==true?"YES":"NO"));
			encounterResults.add("DO_XPERT=" + (doXpertCheckBox.getValue()==true?"YES":"NO"));
		}
		encounterResults.add("ENTERED_DATE=" + enteredDateBox.getValue().toString());
		
		if(doSmearCheckBox.getValue()) {
			sputumResults = new SputumResults();
			sputumResults.setPatientId(current.getPatientId());
			sputumResults.setMonth(sputumMonthComboBox.getSelectedIndex());
			sputumResults.setSputumTestId(TBRClient.get(barcodeTextBox));
			sputumResults.setDateSubmitted(enteredDateBox.getValue());
		}
		
		if(doXpertCheckBox.getValue()) {
			geneXpertResults = new GeneXpertResults();
			geneXpertResults.setPatientId(current.getPatientId());
			//geneXpertResults.setMonth(Integer.parseInt(TBRClient.get(sputumMonthComboBox)));
			geneXpertResults.setSputumTestId(TBRClient.get(barcodeTextBox));
			geneXpertResults.setDateSubmitted(enteredDateBox.getValue());
		}
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
			//System.out.println("here: " + TBRClient.get(patientIdTextBox).toUpperCase());
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
							service.saveSputumCollection(geneXpertResults, sputumResults, encounter, encounterResults, new AsyncCallback<Boolean>()
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
		if (sender == sputumCollectedComboBox)
		{
			boolean check = TBRClient.get(sputumCollectedComboBox).equals("YES");
			barcodeTextBox.setEnabled(check);
			sputumMonthComboBox.setEnabled(check);
			sampleNoIntegerBox.setEnabled(check);
		}
	}
}