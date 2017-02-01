package com.ihsinformatics.xpertsmsweb.client;

import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
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
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.ihsinformatics.xpertsmsweb.shared.AccessType;
import com.ihsinformatics.xpertsmsweb.shared.CustomMessage;
import com.ihsinformatics.xpertsmsweb.shared.ErrorType;
import com.ihsinformatics.xpertsmsweb.shared.InfoType;
import com.ihsinformatics.xpertsmsweb.shared.ListType;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.model.Location;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class LocationsComposite extends Composite implements IForm,
		ClickHandler, ChangeHandler {

	private static ServerServiceAsync service = GWT.create(ServerService.class);
	private static LoadingWidget loading = new LoadingWidget();
	private static final String menuName = "SETUP";
	private UserRightsUtil rights = new UserRightsUtil();
	private boolean valid;
	private Location currentLocation;

	private FlexTable flexTable = new FlexTable();
	private FlexTable topFlexTable = new FlexTable();
	private FlexTable leftFlexTable = new FlexTable();
	private FlexTable rightFlexTable = new FlexTable();
	private Grid grid = new Grid(1, 3);

	private ToggleButton createButton = new ToggleButton("Create");
	private Button saveButton = new Button("Save");
	private Button deleteButton = new Button("Delete");
	private Button closeButton = new Button("Close");

	private Label label = new Label("Locations");
	private Label lblLocationId = new Label("Location ID:");
	private Label lblLocationName = new Label("Location Name:");
	private Label lblLocationType = new Label("Location Type:");
	private Label lblAddress = new Label("Address 1:");
	private Label lblAddress_1 = new Label("Address 2:");
	private Label lblCountry = new Label("Country:");
	private Label lblRegion = new Label("Region:");
	private Label lblPhone = new Label("Phone:");
	private Label lblMobile = new Label("Mobile(s):");
	private Label lblEmailAddress = new Label("Email Address:");
	private Label lblReportingTo = new Label("Reporting To:");

	private TextBox locationIdTextBox = new TextBox();
	private TextBox locationNameTextBox = new TextBox();
	private TextBox address1TextBox = new TextBox();
	private TextBox address2TextBox = new TextBox();
	private TextBox phoneTextBox = new TextBox();
	private TextBox mobileTextBox = new TextBox();
	private TextBox emailTextBox = new TextBox();
	private TextBox cityTextBox = new TextBox();

	private ListBox locationNamesListBox = new ListBox();
	private ListBox locationTypesComboBox = new ListBox();
	private ListBox locationTypeComboBox = new ListBox();
	private ListBox countryComboBox = new ListBox();
	private ListBox reportingToComboBox = new ListBox();

	public LocationsComposite() {
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 1, topFlexTable);
		label.setWordWrap(false);
		label.setStyleName("title");
		topFlexTable.setWidget(0, 0, label);
		topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(1, 0, leftFlexTable);
		locationTypesComboBox.setName("LOCATION_TYPE");
		locationTypesComboBox
				.setTitle("This box contains Location Types. Selecting anyone fills the list box below.");
		leftFlexTable.setWidget(0, 0, locationTypesComboBox);
		locationNamesListBox
				.setTitle("This list box contains Location names of selected type. Clicking anyone fills details in right panel.");
		leftFlexTable.setWidget(1, 0, locationNamesListBox);
		locationNamesListBox.setVisibleItemCount(10);
		flexTable.setWidget(1, 1, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblLocationId);
		locationIdTextBox.setVisibleLength(5);
		locationIdTextBox.setEnabled(false);
		locationIdTextBox.setName("location;location_name");
		rightFlexTable.setWidget(0, 1, locationIdTextBox);
		rightFlexTable.setWidget(1, 0, lblLocationName);
		locationNameTextBox.setVisibleLength(35);
		locationNameTextBox.setName("location;location_name");
		rightFlexTable.setWidget(1, 1, locationNameTextBox);
		rightFlexTable.setWidget(2, 0, lblLocationType);
		locationTypeComboBox.setName("LOCATION_TYPE");
		rightFlexTable.setWidget(2, 1, locationTypeComboBox);
		rightFlexTable.setWidget(3, 0, lblAddress);
		address1TextBox.setVisibleLength(35);
		address1TextBox.setName("location;address1");
		rightFlexTable.setWidget(3, 1, address1TextBox);
		rightFlexTable.setWidget(4, 0, lblAddress_1);
		address2TextBox.setVisibleLength(35);
		address2TextBox.setName("location;address2");
		rightFlexTable.setWidget(4, 1, address2TextBox);
		rightFlexTable.setWidget(7, 0, lblCountry);
		countryComboBox.setName("COUNTRY");
		rightFlexTable.setWidget(7, 1, countryComboBox);
		rightFlexTable.setWidget(8, 0, lblRegion);
		cityTextBox.setName("REGION");
		rightFlexTable.setWidget(8, 1, cityTextBox);
		rightFlexTable.setWidget(9, 0, lblPhone);
		rightFlexTable.setWidget(9, 1, phoneTextBox);
		rightFlexTable.setWidget(10, 0, lblMobile);
		mobileTextBox.setVisibleLength(40);
		rightFlexTable.setWidget(10, 1, mobileTextBox);
		rightFlexTable.setWidget(11, 0, lblEmailAddress);
		emailTextBox.setVisibleLength(35);
		rightFlexTable.setWidget(11, 1, emailTextBox);
		rightFlexTable.setWidget(12, 0, lblReportingTo);
		rightFlexTable.setWidget(12, 1, reportingToComboBox);
		createButton.setEnabled(false);
		rightFlexTable.setWidget(13, 0, createButton);
		rightFlexTable.setWidget(13, 1, grid);
		grid.setSize("100%", "100%");
		saveButton.setEnabled(false);
		grid.setWidget(0, 0, saveButton);
		deleteButton.setEnabled(false);
		grid.setWidget(0, 1, deleteButton);
		grid.setWidget(0, 2, closeButton);
		flexTable.getRowFormatter().setVerticalAlign(1,
				HasVerticalAlignment.ALIGN_TOP);

		mobileTextBox.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				Window.alert("You can use multiple comma separated numbers. This feature is used to send message alerts.");
			}
		});
		locationTypesComboBox.addChangeHandler(this);
		locationNamesListBox.addChangeHandler(this);
		createButton.addClickHandler(this);
		saveButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		closeButton.addClickHandler(this);

		setRights(menuName);
		refreshList();
		try {
			service.findLocationsByType("HOSPITAL",
					new AsyncCallback<Location[]>() {
						@Override
						public void onSuccess(Location[] result) {
							for (Location location : result) {
								reportingToComboBox.addItem(location
										.getLocationId());
							}
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshList() {
		for (String type : XSMS.getList(ListType.LOCATION_TYPE)) {
			locationTypeComboBox.addItem(type);
			locationTypesComboBox.addItem(type);
		}
		for (String country : XSMS.getList(ListType.COUNTRIES)) {
			countryComboBox.addItem(country);
		}
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load(boolean status) {
		flexTable.setVisible(!status);
		if (status)
			loading.show();
		else
			loading.hide();
	}

	public void clearControls(Widget w) {
		if (w instanceof Panel) {
			Iterator<Widget> iter = ((Panel) w).iterator();
			while (iter.hasNext())
				clearControls(iter.next());
		} else if (w instanceof TextBoxBase) {
			((TextBoxBase) w).setText("");
		} else if (w instanceof RichTextArea) {
			((RichTextArea) w).setText("");
		} else if (w instanceof ListBox) {
			((ListBox) w).setSelectedIndex(0);
		} else if (w instanceof DatePicker) {
			((DatePicker) w).setValue(new Date());
		}
	}

	public void setCurrent() {
		currentLocation.setLocationType(XpertSmsWebClient
				.get(locationTypeComboBox));
		currentLocation.setAddressHouse(XpertSmsWebClient.get(address1TextBox)
				.toUpperCase());
		currentLocation.setAddressStreet(XpertSmsWebClient.get(address2TextBox)
				.toUpperCase());
		currentLocation.setCityId(XpertSmsWebClient.get(cityTextBox));
		currentLocation.setCountryId(XpertSmsWebClient.get(countryComboBox));
		currentLocation.setPhone(XpertSmsWebClient.get(phoneTextBox));
		currentLocation.setMobile(XpertSmsWebClient.get(mobileTextBox));
		currentLocation.setEmail(XpertSmsWebClient.get(emailTextBox)
				.toUpperCase());
		currentLocation.setParentLocation(XpertSmsWebClient
				.get(reportingToComboBox));
	}

	@Override
	public void fillData() {
		try {
			service.findLocation(XpertSmsWebClient.get(locationNamesListBox),
					new AsyncCallback<Location>() {

						@Override
						public void onSuccess(Location result) {
							if (result == null)
								Window.alert(CustomMessage
										.getErrorMessage(ErrorType.ITEM_NOT_FOUND));
							else {
								currentLocation = result;
								locationIdTextBox.setValue(currentLocation
										.getLocationId());
								locationNameTextBox.setValue(currentLocation
										.getLocationName());
								locationTypeComboBox.setSelectedIndex(XpertSmsWebClient
										.getIndex(locationTypeComboBox,
												currentLocation
														.getLocationType()));
								address1TextBox.setValue(currentLocation
										.getAddressHouse());
								address2TextBox.setValue(currentLocation
										.getAddressStreet());
								cityTextBox.setValue(currentLocation
										.getCityId());
								countryComboBox.setSelectedIndex(XpertSmsWebClient
										.getIndex(countryComboBox,
												currentLocation.getCountryId()));
								phoneTextBox.setValue(currentLocation
										.getPhone());
								mobileTextBox.setValue(currentLocation
										.getMobile());
								emailTextBox.setValue(currentLocation
										.getEmail());
								if (currentLocation.getParentLocation() != null) {
									reportingToComboBox.setSelectedIndex(XpertSmsWebClient
											.getIndex(
													reportingToComboBox,
													currentLocation
															.getParentLocation()));
								}
							}
							load(false);
						}

						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
							load(false);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			load(false);
		}
	}

	@Override
	public void clearUp() {
		clearControls(flexTable);
	}

	@Override
	public boolean validate() {
		final StringBuilder errorMessage = new StringBuilder();
		valid = true;
		/* Validate mandatory fields */
		if (XpertSmsWebClient.get(locationIdTextBox).equals("")
				|| XpertSmsWebClient.get(locationNameTextBox).equals("")
				|| XpertSmsWebClient.get(locationTypeComboBox).equals("")) {
			errorMessage.append(CustomMessage
					.getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
			valid = false;
		}
		if (!valid) {
			Window.alert(errorMessage.toString());
			load(false);
		}
		return valid;
	}

	@Override
	public void saveData() {
		if (validate()) {
			/* Validate uniqueness */
			try {
				currentLocation = new Location(XpertSmsWebClient.get(
						locationIdTextBox).toUpperCase(), XpertSmsWebClient
						.get(locationNameTextBox).toUpperCase(),
						XpertSmsWebClient.get(locationTypeComboBox));
				setCurrent();
				service.saveLocation(currentLocation,
						new AsyncCallback<Boolean>() {

							public void onSuccess(Boolean result) {
								if (result == null)
									Window.alert(CustomMessage
											.getErrorMessage(ErrorType.DUPLICATION_ERROR));
								else if (result) {
									Window.alert(CustomMessage
											.getInfoMessage(InfoType.INSERTED));
									clearUp();
								} else
									Window.alert(CustomMessage
											.getErrorMessage(ErrorType.INSERT_ERROR));
								load(false);
							}

							public void onFailure(Throwable caught) {
								Window.alert(CustomMessage
										.getErrorMessage(ErrorType.INSERT_ERROR));
								load(false);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				load(false);
			}
		}
	}

	@Override
	public void updateData() {
		if (validate()) {
			try {
				setCurrent();
				service.updateLocation(currentLocation,
						new AsyncCallback<Boolean>() {

							public void onSuccess(Boolean result) {
								if (result)
									Window.alert(CustomMessage
											.getInfoMessage(InfoType.UPDATED));
								else
									Window.alert(CustomMessage
											.getErrorMessage(ErrorType.UPDATE_ERROR));
								load(false);
							}

							public void onFailure(Throwable caught) {
								Window.alert(CustomMessage
										.getErrorMessage(ErrorType.UPDATE_ERROR));
								load(false);
							}
						});

			} catch (Exception e) {
				e.printStackTrace();
				load(false);
			}
		}
	}

	@Override
	public void deleteData() {
		if (validate()) {
			try {
				service.deleteLocation(currentLocation,
						new AsyncCallback<Boolean>() {

							public void onSuccess(Boolean result) {
								if (result) {
									Window.alert(CustomMessage
											.getInfoMessage(InfoType.DELETED));
									clearUp();
									locationNamesListBox.removeItem(XpertSmsWebClient
											.getIndex(locationNamesListBox,
													currentLocation
															.getLocationId()));
								} else
									Window.alert(CustomMessage
											.getErrorMessage(ErrorType.DELETE_ERROR));
								load(false);
							}

							public void onFailure(Throwable caught) {
								Window.alert(CustomMessage
										.getErrorMessage(ErrorType.DELETE_ERROR));
								load(false);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				load(false);
			}
		}
	}

	@Override
	public void setRights(String menuName) {
		try {
			load(true);
			service.getUserRgihts(XSMS.getCurrentUser(), menuName,
					new AsyncCallback<Boolean[]>() {

						public void onSuccess(Boolean[] result) {
							final Boolean[] userRights = result;
							try {
								service.findUser(XSMS.getCurrentUser(),
										new AsyncCallback<Users>() {

											public void onSuccess(Users result) {
												rights.setRoleRights(
														result.getRole(),
														userRights);
												locationTypeComboBox.setEnabled(rights
														.getAccess(AccessType.SELECT));
												locationNamesListBox.setEnabled(rights
														.getAccess(AccessType.SELECT));
												createButton.setEnabled(rights
														.getAccess(AccessType.INSERT));
												saveButton.setEnabled(rights
														.getAccess(AccessType.UPDATE));
												deleteButton.setEnabled(rights
														.getAccess(AccessType.DELETE));
												load(false);
											}

											public void onFailure(
													Throwable caught) {
												load(false);
											}
										});
							} catch (Exception e) {
								e.printStackTrace();
								load(false);
							}
						}

						public void onFailure(Throwable caught) {
							load(false);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			load(false);
		}
	}

	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		load(true);
		if (sender == createButton) {
			if (createButton.isDown())
				clearUp();
			locationIdTextBox.setEnabled(createButton.isDown());
			locationTypesComboBox.setEnabled(!createButton.isDown());
			locationNamesListBox.setEnabled(!createButton.isDown());
			load(false);
		} else if (sender == saveButton) {
			if (createButton.isDown())
				saveData();
			else
				updateData();
		} else if (sender == deleteButton) {
			deleteData();
		} else if (sender == closeButton) {
			MainMenuComposite.clear();
		}
	}

	public void onChange(ChangeEvent event) {
		Widget sender = (Widget) event.getSource();
		load(true);
		if (sender == locationTypesComboBox) {
			try {
				String locationType = XpertSmsWebClient
						.get(locationTypesComboBox);
				service.findLocationsByType(locationType,
						new AsyncCallback<Location[]>() {

							public void onSuccess(Location[] result) {
								locationNamesListBox.clear();
								for (Location s : result)
									locationNamesListBox.addItem(
											s.getLocationName(),
											s.getLocationId());
								load(false);
							}

							public void onFailure(Throwable caught) {
								load(false);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				load(false);
			}
		} else if (sender == locationNamesListBox) {
			fillData();
		}
	}
}
