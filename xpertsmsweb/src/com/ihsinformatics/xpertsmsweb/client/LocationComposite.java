package com.ihsinformatics.xpertsmsweb.client;

import java.util.Date;
import java.util.Iterator;

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
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.ihsinformatics.xpertsmsweb.shared.CustomMessage;
import com.ihsinformatics.xpertsmsweb.shared.ErrorType;
import com.ihsinformatics.xpertsmsweb.shared.InfoType;
import com.ihsinformatics.xpertsmsweb.shared.model.Location;

public class LocationComposite extends Composite implements IForm,
	ClickHandler, ChangeHandler {
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static LoadingWidget loading = new LoadingWidget();

    private boolean valid;
    private Location currentLocation;

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable leftFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private FlexTable top2FlexTable = new FlexTable();
    private FlexTable left2FlexTable = new FlexTable();
    private FlexTable right2FlexTable = new FlexTable();
    private Grid grid = new Grid(1, 3);
    private Grid grid2 = new Grid(1, 3);

    private ToggleButton createButton = new ToggleButton("Create");
    private Button saveButton = new Button("Save");
    private Button deleteButton = new Button("Delete");
    private Button closeButton = new Button("Close");

    private ToggleButton create2Button = new ToggleButton("Create");
    private Button save2Button = new Button("Save");
    private Button delete2Button = new Button("Delete");
    private Button close2Button = new Button("Close");

    private Label label = new Label("Locations: District");
    private Label label2 = new Label("Locations: Health Facility");
    private Label lblLocationId = new Label("ID:");
    private Label lblLocationName = new Label("Name:");
    private Label lblAddress = new Label("Address1:");
    private Label lblAddress_1 = new Label("Address2:");
    private Label lblFacilityId = new Label("ID:");
    private Label lblFacilityName = new Label("Name:");
    private Label lblDistrict = new Label("District:");

    private TextBox locationIdTextBox = new TextBox();
    private TextBox locationNameTextBox = new TextBox();
    private TextBox address1TextBox = new TextBox();
    private TextBox address2TextBox = new TextBox();
    private TextBox facilityIdTextBox = new TextBox();
    private TextBox facilityNameTextBox = new TextBox();
    private TextBox districtTextBox = new TextBox();

    private ListBox districtNamesListBox = new ListBox();
    private ListBox facilityNamesListBox = new ListBox();

    public LocationComposite() {
	initWidget(flexTable);
	flexTable.setSize("80%", "100%");
	flexTable.setWidget(0, 1, topFlexTable);
	label.setWordWrap(false);
	label.setStyleName("title");
	topFlexTable.setWidget(0, 0, label);
	topFlexTable.getCellFormatter().setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.setWidget(1, 0, leftFlexTable);
	districtNamesListBox
		.setTitle("This list box contains Ditricts names. Clicking anyone fills details in right panel and associated facilities below.");
	leftFlexTable.setWidget(1, 0, districtNamesListBox);
	districtNamesListBox.setVisibleItemCount(5);
	flexTable.setWidget(1, 1, rightFlexTable);
	rightFlexTable.setSize("100%", "100%");
	rightFlexTable.setWidget(0, 0, lblLocationId);
	locationIdTextBox.setVisibleLength(2);
	locationIdTextBox.setMaxLength(2);
	locationIdTextBox.setEnabled(false);
	locationIdTextBox.setName("location;location_name");
	rightFlexTable.setWidget(0, 1, locationIdTextBox);
	rightFlexTable.setWidget(1, 0, lblLocationName);
	locationNameTextBox.setVisibleLength(35);
	locationNameTextBox.setName("location;location_name");
	rightFlexTable.setWidget(1, 1, locationNameTextBox);

	rightFlexTable.setWidget(13, 0, createButton);
	rightFlexTable.setWidget(13, 1, grid);
	grid.setSize("100%", "100%");

	grid.setWidget(0, 0, saveButton);

	grid.setWidget(0, 1, deleteButton);
	grid.setWidget(0, 2, closeButton);

	flexTable.setWidget(5, 1, top2FlexTable);
	label2.setWordWrap(false);
	label2.setStyleName("title");
	top2FlexTable.setWidget(6, 0, label2);
	top2FlexTable.getCellFormatter().setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.setWidget(6, 0, left2FlexTable);
	facilityNamesListBox
		.setTitle("This list box contains Facility names. Clicking anyone fills details in right panel.");
	left2FlexTable.setWidget(1, 0, facilityNamesListBox);
	facilityNamesListBox.setVisibleItemCount(10);
	flexTable.setWidget(6, 1, right2FlexTable);
	right2FlexTable.setWidget(0, 0, lblFacilityId);
	right2FlexTable.setSize("100%", "100%");
	right2FlexTable.setWidget(0, 0, lblFacilityId);
	facilityIdTextBox.setVisibleLength(2);
	facilityIdTextBox.setMaxLength(2);
	facilityIdTextBox.setEnabled(false);
	facilityIdTextBox.setName("location;location_name");
	right2FlexTable.setWidget(0, 1, facilityIdTextBox);
	right2FlexTable.setWidget(1, 0, lblFacilityName);
	facilityNameTextBox.setVisibleLength(35);
	facilityNameTextBox.setName("location;location_name");
	right2FlexTable.setWidget(1, 1, facilityNameTextBox);
	right2FlexTable.setWidget(2, 0, lblAddress);
	right2FlexTable.setWidget(2, 1, address1TextBox);
	address1TextBox.setVisibleLength(35);
	right2FlexTable.setWidget(3, 0, lblAddress_1);
	right2FlexTable.setWidget(3, 1, address2TextBox);
	address2TextBox.setVisibleLength(35);
	right2FlexTable.setWidget(4, 0, lblDistrict);
	right2FlexTable.setWidget(4, 1, districtTextBox);
	districtTextBox.setVisibleLength(35);
	districtTextBox.setEnabled(false);
	right2FlexTable.setWidget(13, 0, create2Button);
	right2FlexTable.setWidget(13, 1, grid2);
	grid2.setSize("100%", "100%");

	grid2.setWidget(0, 0, save2Button);

	grid2.setWidget(0, 1, delete2Button);
	grid2.setWidget(0, 2, close2Button);

	flexTable.getRowFormatter().setVerticalAlign(1,
		HasVerticalAlignment.ALIGN_TOP);

	facilityNamesListBox.addChangeHandler(this);
	districtNamesListBox.addChangeHandler(this);
	createButton.addClickHandler(this);
	saveButton.addClickHandler(this);
	deleteButton.addClickHandler(this);
	closeButton.addClickHandler(this);

	create2Button.addClickHandler(this);
	save2Button.addClickHandler(this);
	delete2Button.addClickHandler(this);
	close2Button.addClickHandler(this);

	refresh(flexTable);
    }

    /**
     * This method refreshes data inside a widget recursively. If the widget is
     * a List box and the "name" property of the list box is set, then the
     * method searches the value in "name" (e.g. MARITAL_STATUS) in definitions
     * and loads into the list. Otherwise if the "name" property is not set, the
     * list box is left untouched. If the widget is text-type, and the "name"
     * property is set, then the method sets max length to the allowed length in
     * table meta data. (The format of name property has to be in the format:
     * table_name;column_name)
     * 
     * @param widget
     */
    public void refresh(Widget widget) {
	try {
	    String locationType = "District";
	    service.findLocationsByType(locationType,
		    new AsyncCallback<Location[]>() {
			public void onSuccess(Location[] result) {
			    districtNamesListBox.clear();
			    for (Location s : result) {
				districtNamesListBox.addItem(
					s.getLocationName(), s.getLocationId());
			    }
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

    }

    public void fillData() {
	try {
	    service.findDistrict(XpertSmsWebClient.get(districtNamesListBox),
		    new AsyncCallback<Location>() {
			@Override
			public void onSuccess(Location result) {
			    if (result == null)
				Window.alert("ERROR!");
			    else {
				currentLocation = result;
				locationIdTextBox.setValue(currentLocation
					.getLocationId());
				locationNameTextBox.setValue(currentLocation
					.getLocationName());
				/*
				 * address1TextBox.setValue
				 * (currentLocation.getAddressHouse ());
				 * address2TextBox.setValue
				 * (currentLocation.getAddressStreet ());
				 * cityTextBox.setValue
				 * (currentLocation.getAddressSector ());
				 * stateTextBox.setValue
				 * (currentLocation.getAddressColony ());
				 */

			    }
			    load(false);
			}

			@Override
			public void onFailure(Throwable caught) {
			    caught.printStackTrace();
			    load(false);
			}
		    });

	    service.findFacilitiesByDistrictId(
		    XpertSmsWebClient.get(districtNamesListBox),
		    new AsyncCallback<Location[]>() {
			@Override
			public void onSuccess(Location[] result) {
			    facilityNamesListBox.clear();
			    for (Location s : result) {
				facilityNamesListBox.addItem(
					s.getLocationName(), s.getLocationId());
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

    public void clearUp() {
	clearControls(rightFlexTable);
    }

    public void clearUp2() {
	clearControls(right2FlexTable);
    }

    public boolean validate() {
	final StringBuilder errorMessage = new StringBuilder();
	valid = true;
	/* Validate mandatory fields */
	if (XpertSmsWebClient.get(locationIdTextBox).equals("")
		|| XpertSmsWebClient.get(locationNameTextBox).equals("")) {
	    errorMessage.append(CustomMessage
		    .getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
	    valid = false;
	} else if (XpertSmsWebClient.get(locationIdTextBox).length() != 2) {

	    errorMessage.append(CustomMessage
		    .getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	    valid = false;
	}
	if (!valid) {
	    Window.alert(errorMessage.toString());
	    load(false);
	}
	return valid;
    }

    public boolean validate2() {
	final StringBuilder errorMessage = new StringBuilder();
	valid = true;
	/* Validate mandatory fields */
	if (XpertSmsWebClient.get(facilityIdTextBox).equals("")
		|| XpertSmsWebClient.get(facilityNameTextBox).equals("")) {
	    errorMessage.append(CustomMessage
		    .getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
	    valid = false;
	} else if (XpertSmsWebClient.get(facilityIdTextBox).length() != 2) {

	    errorMessage.append(CustomMessage
		    .getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	    valid = false;
	}
	if (!valid) {
	    Window.alert(errorMessage.toString());
	    load(false);
	}
	return valid;
    }

    public void saveData() {
	if (validate()) {
	    try {
		final Location currentLocation = new Location();
		currentLocation.setLocationId(locationIdTextBox.getValue());
		currentLocation.setLocationName(locationNameTextBox.getValue());
		currentLocation.setCityId("0");
		currentLocation.setLocationType("DISTRICT");

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
				    districtNamesListBox.addItem(
					    currentLocation.getLocationName(),
					    currentLocation.getLocationId());
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

    public void updateData() {
	if (validate()) {
	    try {
		Location currentLocation = new Location();
		currentLocation.setLocationId(locationIdTextBox.getValue());
		currentLocation.setLocationName(locationNameTextBox.getValue());
		currentLocation.setAddressHouse("");
		currentLocation.setAddressStreet("");
		currentLocation.setAddressSector("");
		currentLocation.setAddressColony("");
		currentLocation.setLocationType("DISTRICT");

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
				    districtNamesListBox.removeItem(XpertSmsWebClient
					    .getIndex(districtNamesListBox,
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

    public void setRights(String menuName) {

    }

    public void onClick(ClickEvent event) {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == createButton) {
	    if (createButton.isDown())
		clearUp();
	    else
		fillData();
	    locationIdTextBox.setEnabled(createButton.isDown());
	    districtNamesListBox.setEnabled(!createButton.isDown());
	    load(false);
	}
	if (sender == create2Button) {
	    if (create2Button.isDown())
		clearUp2();
	    else
		fillFacilityData();
	    facilityIdTextBox.setEnabled(create2Button.isDown());
	    facilityNamesListBox.setEnabled(!create2Button.isDown());
	    districtTextBox.setValue(districtNamesListBox
		    .getItemText(districtNamesListBox.getSelectedIndex()));
	    load(false);
	} else if (sender == saveButton) {
	    if (createButton.isDown())
		saveData();
	    else
		updateData();
	} else if (sender == save2Button) {
	    if (create2Button.isDown())
		save2Data();
	    else
		updateData();
	} else if (sender == deleteButton) {
	    deleteData();
	} else if (sender == delete2Button) {
	    delete2Data();
	} else if (sender == closeButton || sender == close2Button) {
	    MainMenuComposite.clear();
	}
    }

    private void delete2Data() {
	if (validate2()) {
	    try {
		service.deleteLocation(currentLocation,
			new AsyncCallback<Boolean>() {
			    public void onSuccess(Boolean result) {
				if (result) {
				    Window.alert(CustomMessage
					    .getInfoMessage(InfoType.DELETED));
				    clearUp();
				    districtNamesListBox.removeItem(XpertSmsWebClient
					    .getIndex(facilityNamesListBox,
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

    private void save2Data() {
	if (validate2()) {
	    try {
		final Location currentLocation = new Location();
		currentLocation.setLocationId(facilityIdTextBox.getValue());
		currentLocation.setLocationName(facilityNameTextBox.getValue());
		currentLocation.setCityId(XpertSmsWebClient
			.get(districtNamesListBox));
		currentLocation.setLocationType("HEALTH FACILITY");

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
				    facilityNamesListBox.addItem(
					    currentLocation.getLocationName(),
					    currentLocation.getLocationId());
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

    public void onChange(ChangeEvent event) {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == districtNamesListBox) {
	    fillData();
	} else if (sender == facilityNamesListBox) {
	    fillFacilityData();
	}
    }

    public void fillFacilityData() {
	try {
	    districtTextBox.setValue(districtNamesListBox
		    .getItemText(districtNamesListBox.getSelectedIndex()));
	    service.findFacility(XpertSmsWebClient.get(facilityNamesListBox),
		    XpertSmsWebClient.get(districtNamesListBox),
		    new AsyncCallback<Location>() {
			@Override
			public void onSuccess(Location result) {
			    if (result == null)
				Window.alert("ERROR!");
			    else {
				currentLocation = result;
				facilityIdTextBox.setValue(currentLocation
					.getLocationId());
				facilityNameTextBox.setValue(currentLocation
					.getLocationName());
				address1TextBox.setValue(currentLocation
					.getAddressHouse());
				address2TextBox.setValue(currentLocation
					.getAddressStreet());

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

}
