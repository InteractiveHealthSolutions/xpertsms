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
import org.irdresearch.tbreach.shared.model.Person;
import org.irdresearch.tbreach.shared.model.Users;
import org.irdresearch.tbreach.shared.model.Worker;

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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class WorkerComposite extends Composite implements IForm, ClickHandler, ChangeHandler
{
	private static ServerServiceAsync	service					= GWT.create(ServerService.class);
	private static LoadingWidget		loading					= new LoadingWidget();
	private static final String			menuName				= "CHW";
	private static final String			tableName				= "Worker";

	private UserRightsUtil				rights					= new UserRightsUtil();
	private boolean						valid;
	private Worker						current;
	private Person						currentPerson;
	private Contact						currentContact;
	private Users						currentUser;

	private FlexTable					flexTable				= new FlexTable();
	private FlexTable					topFlexTable			= new FlexTable();
	private FlexTable					leftFlexTable			= new FlexTable();
	private FlexTable					rightFlexTable			= new FlexTable();
	private Grid						grid					= new Grid(1, 3);

	private ListBox						CHWListBox				= new ListBox();

	private ToggleButton				createButton			= new ToggleButton("Create Screener");
	private Button						saveButton				= new Button("Save");
	private Button						deleteButton			= new Button("Delete");
	private Button						closeButton				= new Button("Close");

	private Label						lblTbReachMonitors		= new Label("Screeners");
	//private Label						lblMonitorId			= new Label("Monitor ID:");
	private Label						lblSalutation			= new Label("Salutation:");
	private Label						lblFirstName			= new Label("First Name:");
	private Label						lblLastName				= new Label("Last Name:");
	private Label						lblMiddleName			= new Label("Middle Name:");
	private Label						lblGender				= new Label("Gender:");
	private Label						lblNationalId			= new Label("National ID:");
	private Label						lblPhone				= new Label("Phone:");
	private Label						lblMobile				= new Label("Mobile:");
	private Label						lblEmail				= new Label("Email:");
	private ListBox						salutationComboBox		= new ListBox();
	private TextBox						firstNameTextBox		= new TextBox();
	private TextBox						middleNameTextBox		= new TextBox();
	private TextBox						lastNameTextBox			= new TextBox();
	private HorizontalPanel				genderHorizontalPanel	= new HorizontalPanel();
	private RadioButton					maleRadioButton			= new RadioButton("new name", "Male");
	private RadioButton					femaleRadioButton		= new RadioButton("new name", "Female");
	private TextBox						NICTextBox				= new TextBox();
	private TextBox						phoneTextBox			= new TextBox();
	private TextBox						mobileTextBox			= new TextBox();
	private TextBox						emailTextBox			= new TextBox();
	//private Label						lblSupervisorId			= new Label("Supervisor ID:");
	//private ListBox						supervisorIdComboBox	= new ListBox();
	private Label						lblChwId				= new Label("Screener ID:");
	private TextBox						CHWIdTextBox			= new TextBox();
	//private ListBox						monitorIdComboBox		= new ListBox();

	public WorkerComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");

		flexTable.setWidget(0, 1, topFlexTable);

		lblTbReachMonitors.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblTbReachMonitors);

		flexTable.setWidget(1, 0, leftFlexTable);
		CHWListBox.setEnabled(false);

		leftFlexTable.setWidget(0, 0, CHWListBox);
		CHWListBox.setVisibleItemCount(5);

		flexTable.setWidget(1, 1, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");

		rightFlexTable.setWidget(0, 0, lblChwId);
		CHWIdTextBox.setVisibleLength(12);
		CHWIdTextBox.setMaxLength(12);
		rightFlexTable.setWidget(0, 1, CHWIdTextBox);
		/*rightFlexTable.setWidget(1, 0, lblMonitorId);
		rightFlexTable.setWidget(1, 1, monitorIdComboBox);
		rightFlexTable.setWidget(2, 0, lblSupervisorId);
		rightFlexTable.setWidget(2, 1, supervisorIdComboBox);*/
		rightFlexTable.setWidget(3, 0, lblSalutation);
		salutationComboBox.addItem("MR");
		salutationComboBox.addItem("MS");
		salutationComboBox.addItem("MRS");
		salutationComboBox.addItem("DR");
		salutationComboBox.addItem("PROF");
		rightFlexTable.setWidget(3, 1, salutationComboBox);
		rightFlexTable.setWidget(4, 0, lblFirstName);
		firstNameTextBox.setMaxLength(20);
		rightFlexTable.setWidget(4, 1, firstNameTextBox);
		rightFlexTable.setWidget(5, 0, lblMiddleName);
		middleNameTextBox.setMaxLength(20);
		rightFlexTable.setWidget(5, 1, middleNameTextBox);
		rightFlexTable.setWidget(6, 0, lblLastName);
		lastNameTextBox.setMaxLength(20);
		rightFlexTable.setWidget(6, 1, lastNameTextBox);
		rightFlexTable.setWidget(7, 0, lblGender);
		rightFlexTable.setWidget(7, 1, genderHorizontalPanel);
		maleRadioButton.setValue(true);
		genderHorizontalPanel.add(maleRadioButton);
		genderHorizontalPanel.add(femaleRadioButton);
		rightFlexTable.setWidget(8, 0, lblNationalId);
		NICTextBox.setMaxLength(20);
		rightFlexTable.setWidget(8, 1, NICTextBox);
		rightFlexTable.setWidget(9, 0, lblPhone);
		phoneTextBox.setMaxLength(20);
		rightFlexTable.setWidget(9, 1, phoneTextBox);
		rightFlexTable.setWidget(10, 0, lblMobile);
		mobileTextBox.setMaxLength(20);
		rightFlexTable.setWidget(10, 1, mobileTextBox);
		rightFlexTable.setWidget(11, 0, lblEmail);
		emailTextBox.setMaxLength(100);
		rightFlexTable.setWidget(11, 1, emailTextBox);
		emailTextBox.setWidth("100%");
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
		CHWListBox.addClickHandler(this);
		setRights(menuName);
		refreshList();
	}

	public void refreshList()
	{
		/*try
		{
			service.getColumnData("Monitor", "MonitorID", "", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					monitorIdComboBox.clear();
					for (int i = 0; i < result.length; i++)
						monitorIdComboBox.insertItem(result[i], i);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					// Not implemented
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			service.getColumnData("Supervisor", "SupervisorID", "", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					supervisorIdComboBox.clear();
					for (int i = 0; i < result.length; i++)
						supervisorIdComboBox.insertItem(result[i], i);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					// Not implemented
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
		try
		{
			service.getColumnData(tableName, "WorkerID", "", new AsyncCallback<String[]>()
			{
				@Override
				public void onSuccess(String[] result)
				{
					CHWListBox.clear();
					for (int i = 0; i < result.length; i++)
						CHWListBox.insertItem(result[i], i);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					// Not implemented
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
		/*current.setMonitorId(TBRClient.get(monitorIdComboBox).toUpperCase());
		current.setSupervisorId(TBRClient.get(supervisorIdComboBox).toUpperCase());*/
		currentPerson.setSalutation(TBRClient.get(salutationComboBox).toUpperCase());
		currentPerson.setFirstName(TBRClient.get(firstNameTextBox).toUpperCase());
		currentPerson.setMiddleName(TBRClient.get(middleNameTextBox).toUpperCase());
		currentPerson.setLastName(TBRClient.get(lastNameTextBox).toUpperCase());
		currentPerson.setGender((maleRadioButton.getValue() ? 'M' : 'F'));
		currentPerson.setNic(TBRClient.get(NICTextBox).toUpperCase());
		currentContact.setPhone(TBRClient.get(phoneTextBox).toUpperCase());
		currentContact.setMobile(TBRClient.get(mobileTextBox).toUpperCase());
		currentContact.setEmail(TBRClient.get(emailTextBox).toUpperCase());
		currentUser.setPid(TBRClient.get(CHWIdTextBox).toUpperCase());
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
		if (TBRClient.get(CHWIdTextBox).equals("") || TBRClient.get(firstNameTextBox).equals(""))
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
				current = new Worker();
				current.setWorkerId(TBRClient.get(CHWIdTextBox).toUpperCase());
				currentPerson = new Person();
				currentPerson.setPid(TBRClient.get(CHWIdTextBox).toUpperCase());
				currentContact = new Contact();
				currentContact.setPid(TBRClient.get(CHWIdTextBox).toUpperCase());
				currentUser = new Users(current.getWorkerId(), current.getWorkerId(), "CHW", "ACTIVE", current.getWorkerId().toLowerCase(),
						"WHAT PHONE MODEL ARE YOU PLANNING TO PURCHASE NEXT?", "nokia n8");
				setCurrent();
				service.exists(tableName, "WorkerID='" + TBRClient.get(CHWIdTextBox) + "'", new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						if (!result)
						{
							try
							{
								service.saveWorker(current, currentPerson, currentContact, currentUser, new AsyncCallback<Boolean>()
								{
									@Override
									public void onSuccess(Boolean result)
									{
										if (result)
											Window.alert(CustomMessage.getInfoMessage(InfoType.INSERTED));
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

	@Override
	public void updateData()
	{
		if (validate())
		{
			try
			{
				setCurrent();
				service.updateWorker(current, currentPerson, currentContact, currentUser, new AsyncCallback<Boolean>()
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
				setCurrent();
				service.deleteWorker(current, currentPerson, currentContact, currentUser, new AsyncCallback<Boolean>()
				{
					@Override
					public void onSuccess(Boolean result)
					{
						if (result)
							Window.alert(CustomMessage.getInfoMessage(InfoType.DELETED));
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
	public void fillData()
	{
		try
		{
			load(true);
			//monitorIdComboBox.setSelectedIndex(TBRClient.getIndex(monitorIdComboBox, TBRClient.get(CHWListBox)));
			service.findWorker(TBRClient.get(CHWListBox), new AsyncCallback<Worker>()
			{
				@Override
				public void onSuccess(Worker result)
				{
					current = result;
					CHWIdTextBox.setText(current.getWorkerId());
					/*monitorIdComboBox.setSelectedIndex(TBRClient.getIndex(monitorIdComboBox, current.getMonitorId()));
					supervisorIdComboBox.setSelectedIndex(TBRClient.getIndex(supervisorIdComboBox, current.getSupervisorId()));*/
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
			service.findPerson(TBRClient.get(CHWListBox), new AsyncCallback<Person>()
			{
				@Override
				public void onSuccess(Person result)
				{
					currentPerson = result;
					salutationComboBox.setSelectedIndex(TBRClient.getIndex(salutationComboBox, currentPerson.getSalutation()));
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
			service.findContact(TBRClient.get(CHWListBox), new AsyncCallback<Contact>()
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
		// Fill user
		try
		{
			load(true);
			service.findUser(TBRClient.get(CHWListBox), new AsyncCallback<Users>()
			{
				@Override
				public void onSuccess(Users result)
				{
					currentUser = result;
					// refreshList();
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
								CHWListBox.setEnabled(rights.getAccess(AccessType.SELECT));
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
		if (sender == CHWListBox)
		{
			fillData();
		}
		else if (sender == createButton)
		{
			if (createButton.isDown())
				clearUp();
			CHWListBox.setEnabled(!createButton.isDown());
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
