package org.irdresearch.tbreach.client;

import java.util.Date;
import java.util.Iterator;

import org.irdresearch.tbreach.shared.AccessType;
import org.irdresearch.tbreach.shared.CustomMessage;
import org.irdresearch.tbreach.shared.ErrorType;
import org.irdresearch.tbreach.shared.InfoType;
import org.irdresearch.tbreach.shared.TBR;
import org.irdresearch.tbreach.shared.UserRightsUtil;
import org.irdresearch.tbreach.shared.model.Contact;
import org.irdresearch.tbreach.shared.model.Gp;
import org.irdresearch.tbreach.shared.model.Person;
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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * 
 * @author owais.hussain@irdresearch.org
 *
 */
public class GPComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service							= GWT.create(ServerService.class);
	private static LoadingWidget		loading							= new LoadingWidget();
	private static final String			menuName						= "SETUP";
	private static final String			tableName						= "GP";

	private UserRightsUtil				rights							= new UserRightsUtil();
	private boolean						valid;
	private Gp							current;
	private Person						currentPerson;
	private Contact						currentContact;
	private Users						currentUser;

	private FlexTable					flexTable						= new FlexTable();
	private FlexTable					topFlexTable					= new FlexTable();
	private FlexTable					leftFlexTable					= new FlexTable();
	private FlexTable					rightFlexTable					= new FlexTable();
	private Grid						grid							= new Grid(1, 3);

	private ListBox						GPsListBox						= new ListBox();
	private TextBox						GPIdTextBox						= new TextBox();

	private ToggleButton				createButton					= new ToggleButton("Create GP");
	private Button						saveButton						= new Button("Save");
	private Button						deleteButton					= new Button("Delete");
	private Button						closeButton						= new Button("Close");

	private Label						lblTbReachGeneral				= new Label("TB REACH Providers");
	private Label						lblGpId							= new Label("Provider ID:");
	private Label						lblSalutation					= new Label("Salutation:");
	private ListBox						salutationListBox				= new ListBox();
	private Label						lblFirstName					= new Label("First Name:");
	private Label						lblMiddleName					= new Label("Middle Name:");
	private Label						lblLastName						= new Label("Last Name:");
	private Label						lblGender						= new Label("Gender:");
	private Label						lblNationalId					= new Label("National ID:");
	private Label						lblPhone						= new Label("Phone:");
	private TextBox						firstNameTextBox				= new TextBox();
	private Label						lblMobile						= new Label("Mobile:");
	private Label						lblEmail						= new Label("Email:");
	private TextBox						middleNameTextBox				= new TextBox();
	private TextBox						lastNameTextBox					= new TextBox();
	private HorizontalPanel				genderHorizontalPanel			= new HorizontalPanel();
	private RadioButton					maleRadioButton					= new RadioButton("new name", "Male");
	private RadioButton					femaleRadioButton				= new RadioButton("new name", "Female");
	private TextBox						NICTextBox						= new TextBox();
	private TextBox						phoneTextBox					= new TextBox();
	private TextBox						mobileTextBox					= new TextBox();
	private TextBox						emailTextBox					= new TextBox();
	private Label						lblQualificationdegree			= new Label("Qualification/Degrees:");
	private HorizontalPanel				qualificationHorizontalPanel	= new HorizontalPanel();
	private TextBox						qualification1TextBox			= new TextBox();
	private TextBox						qualification2TextBox			= new TextBox();
	private TextBox						qualification3TextBox			= new TextBox();
	private Label						lblSpecialization				= new Label("Specialization:");
	private TextBox						specializationTextBox			= new TextBox();
	private Label						lblPracticeLocations			= new Label("Practice Locations:");
	//private ListBox						location1ComboBox				= new ListBox();
//	private ListBox						location2ComboBox				= new ListBox();
	private VerticalPanel				verticalPanel					= new VerticalPanel();
	private HorizontalPanel				location1HorizontalPanel		= new HorizontalPanel();
	private Label						locationNameLabel				= new Label("");
	private HorizontalPanel				location2HorizontalPanel		= new HorizontalPanel();
	private Label						locationName2Label				= new Label("");

	public GPComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");

		flexTable.setWidget(0, 1, topFlexTable);

		lblTbReachGeneral.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachGeneral);

		flexTable.setWidget(1, 0, leftFlexTable);
		GPsListBox.setEnabled(false);

		leftFlexTable.setWidget(0, 0, GPsListBox);
		GPsListBox.setVisibleItemCount(5);

		flexTable.setWidget(1, 1, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");

		rightFlexTable.setWidget(0, 0, lblGpId);
		GPIdTextBox.setMaxLength(12);

		rightFlexTable.setWidget(0, 1, GPIdTextBox);

		rightFlexTable.setWidget(1, 0, lblSalutation);
		salutationListBox.addItem("MR");
		salutationListBox.addItem("MS");
		salutationListBox.addItem("MRS");
		salutationListBox.addItem("DR");
		salutationListBox.addItem("PROF");

		rightFlexTable.setWidget(1, 1, salutationListBox);

		rightFlexTable.setWidget(2, 0, lblFirstName);
		firstNameTextBox.setMaxLength(20);

		rightFlexTable.setWidget(2, 1, firstNameTextBox);

		rightFlexTable.setWidget(3, 0, lblMiddleName);
		middleNameTextBox.setMaxLength(20);

		rightFlexTable.setWidget(3, 1, middleNameTextBox);

		rightFlexTable.setWidget(4, 0, lblLastName);
		lastNameTextBox.setMaxLength(20);

		rightFlexTable.setWidget(4, 1, lastNameTextBox);

		rightFlexTable.setWidget(5, 0, lblGender);

		rightFlexTable.setWidget(5, 1, genderHorizontalPanel);
		maleRadioButton.setValue(true);

		genderHorizontalPanel.add(maleRadioButton);

		genderHorizontalPanel.add(femaleRadioButton);

		rightFlexTable.setWidget(6, 0, lblNationalId);
		NICTextBox.setMaxLength(20);

		rightFlexTable.setWidget(6, 1, NICTextBox);

		rightFlexTable.setWidget(7, 0, lblPhone);
		phoneTextBox.setMaxLength(20);

		rightFlexTable.setWidget(7, 1, phoneTextBox);

		rightFlexTable.setWidget(8, 0, lblMobile);
		mobileTextBox.setMaxLength(20);

		rightFlexTable.setWidget(8, 1, mobileTextBox);

		rightFlexTable.setWidget(9, 0, lblEmail);
		emailTextBox.setMaxLength(100);

		rightFlexTable.setWidget(9, 1, emailTextBox);
		emailTextBox.setWidth("100%");

		rightFlexTable.setWidget(10, 0, lblQualificationdegree);

		rightFlexTable.setWidget(10, 1, qualificationHorizontalPanel);
		qualification1TextBox.setVisibleLength(10);
		qualification1TextBox.setMaxLength(10);

		qualificationHorizontalPanel.add(qualification1TextBox);
		qualification2TextBox.setMaxLength(10);
		qualification2TextBox.setVisibleLength(10);

		qualificationHorizontalPanel.add(qualification2TextBox);
		qualification3TextBox.setVisibleLength(10);
		qualification3TextBox.setMaxLength(10);

		qualificationHorizontalPanel.add(qualification3TextBox);

		rightFlexTable.setWidget(11, 0, lblSpecialization);
		specializationTextBox.setMaxLength(100);

		rightFlexTable.setWidget(11, 1, specializationTextBox);
		specializationTextBox.setWidth("100%");

		/*rightFlexTable.setWidget(12, 0, lblPracticeLocations);

		rightFlexTable.setWidget(12, 1, verticalPanel);

		verticalPanel.add(location1HorizontalPanel);
		location1HorizontalPanel.add(location1ComboBox);

		location1HorizontalPanel.add(locationNameLabel);
		verticalPanel.add(location2HorizontalPanel);
		location2HorizontalPanel.add(location2ComboBox);

		location2HorizontalPanel.add(locationName2Label);
		createButton.setEnabled(false);*/

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
		GPsListBox.addClickHandler(this);

		setRights(menuName);
		/*try
		{
			load(true);
			service.getColumnData("Location", "LocationID", "", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					location1ComboBox.clear();
					location2ComboBox.clear();
					for (int i = 0; i < result.length; i++)
					{
						location1ComboBox.insertItem(result[i], i);
						location2ComboBox.insertItem(result[i], i);
					}
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
		}*/
		refreshList();
	}

	public void refreshList()
	{
		try
		{
			load(true);
			service.getColumnData(tableName, "GPID", "", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					GPsListBox.clear();
					for (int i = 0; i < result.length; i++)
						GPsListBox.insertItem(result[i], i);
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
		current.setQualification1(TBRClient.get(qualification1TextBox).toUpperCase());
		current.setQualification2(TBRClient.get(qualification2TextBox).toUpperCase());
		current.setQualification3(TBRClient.get(qualification3TextBox).toUpperCase());
		current.setSpeciality1(TBRClient.get(specializationTextBox).toUpperCase());
		/*current.setWorkplaceId1(TBRClient.get(location1ComboBox).toUpperCase());
		current.setWorkplaceId2(TBRClient.get(location2ComboBox).toUpperCase());*/
		currentPerson.setSalutation(TBRClient.get(salutationListBox).toUpperCase());
		currentPerson.setFirstName(TBRClient.get(firstNameTextBox).toUpperCase());
		currentPerson.setMiddleName(TBRClient.get(middleNameTextBox).toUpperCase());
		currentPerson.setLastName(TBRClient.get(lastNameTextBox).toUpperCase());
		currentPerson.setGender((maleRadioButton.getValue() ? 'M' : 'F'));
		currentPerson.setNic(TBRClient.get(NICTextBox).toUpperCase());
		currentContact.setPhone(TBRClient.get(phoneTextBox).toUpperCase());
		currentContact.setMobile(TBRClient.get(mobileTextBox).toUpperCase());
		currentContact.setEmail(TBRClient.get(emailTextBox).toUpperCase());
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
		if (TBRClient.get(GPIdTextBox).equals("") || TBRClient.get(firstNameTextBox).equals("") || TBRClient.get(lastNameTextBox).equals("")
				|| TBRClient.get(mobileTextBox).equals(""))
		{
			errorMessage.append(CustomMessage.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
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
				current = new Gp(TBRClient.get(GPIdTextBox));
				currentPerson = new Person(current.getGpid(), TBRClient.get(firstNameTextBox), (maleRadioButton.getValue() ? 'M' : 'F'));
				currentContact = new Contact(current.getGpid());
				currentUser = new Users(current.getGpid(), current.getGpid(), "PROVIDER", "ACTIVE", current.getGpid().toLowerCase(),
						"WHAT PHONE MODEL ARE YOU PLANNING TO PURCHASE NEXT?", "nokia n8");
				setCurrent();
				service.exists(tableName, "GPID='" + TBRClient.get(GPIdTextBox) + "'", new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						if (!result)
						{
							try
							{
								service.saveGP(current, currentPerson, currentContact, currentUser, new AsyncCallback<Boolean>()
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
									}
								});
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
						else
							Window.alert(CustomMessage.getErrorMessage(ErrorType.DUPLICATION_ERROR));
						load(false);
					}

					@Override
					public void onFailure(Throwable caught)
					{
						Window.alert(CustomMessage.getErrorMessage(ErrorType.INSERT_ERROR) + caught.getMessage());
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
				setCurrent();
				service.updateGP(current, currentPerson, currentContact, currentUser, new AsyncCallback<Boolean>()
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
				service.deleteGP(current, currentPerson, currentContact, currentUser, new AsyncCallback<Boolean>()
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
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void fillData()
	{
		try
		{
			load(true);
			GPIdTextBox.setText(TBRClient.get(GPsListBox));
			service.findGP(TBRClient.get(GPsListBox), new AsyncCallback<Gp>()
			{
				@Override
				public void onSuccess(Gp result)
				{
					current = result;
					qualification1TextBox.setText(current.getQualification1());
					qualification2TextBox.setText(current.getQualification2());
					qualification3TextBox.setText(current.getQualification3());
					specializationTextBox.setText(current.getSpeciality1());
					/*location1ComboBox.setSelectedIndex(TBRClient.getIndex(location1ComboBox, current.getWorkplaceId1()));
					location2ComboBox.setSelectedIndex(TBRClient.getIndex(location2ComboBox, current.getWorkplaceId2()));*/
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
		// Fill person
		try
		{
			load(true);
			service.findPerson(TBRClient.get(GPsListBox), new AsyncCallback<Person>()
			{
				@Override
				public void onSuccess(Person result)
				{
					currentPerson = result;
					salutationListBox.setSelectedIndex(TBRClient.getIndex(salutationListBox, currentPerson.getSalutation()));
					firstNameTextBox.setText(currentPerson.getFirstName());
					middleNameTextBox.setText(currentPerson.getMiddleName());
					lastNameTextBox.setText(currentPerson.getLastName());
					femaleRadioButton.setValue(currentPerson.getGender() == 'F');
					NICTextBox.setText(currentPerson.getNic());
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
		// Fill contact
		try
		{
			load(true);
			service.findContact(TBRClient.get(GPsListBox), new AsyncCallback<Contact>()
			{
				@Override
				public void onSuccess(Contact result)
				{
					currentContact = result;
					phoneTextBox.setText(currentContact.getPhone());
					mobileTextBox.setText(currentContact.getMobile());
					emailTextBox.setText(currentContact.getEmail());
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
								GPsListBox.setEnabled(rights.getAccess(AccessType.SELECT));
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
		if (sender == GPsListBox)
		{
			fillData();
		}
		else if (sender == createButton)
		{
			if (createButton.isDown())
				clearUp();
			GPsListBox.setEnabled(!createButton.isDown());
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
