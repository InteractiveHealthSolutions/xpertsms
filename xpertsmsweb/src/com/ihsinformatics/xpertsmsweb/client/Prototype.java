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
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class Prototype extends Composite implements IForm, ClickHandler,
	ChangeHandler {
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static LoadingWidget loading = new LoadingWidget();
    private static final String menuName = "SETUP";
    private static final String tableName = "Users";

    private UserRightsUtil rights = new UserRightsUtil();
    private boolean valid;
    private Users current;

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable leftFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private Grid grid = new Grid(1, 3);

    private ListBox listBox = new ListBox();
    private TextBox textBox = new TextBox();

    private ToggleButton createButton = new ToggleButton("Create");
    private Button saveButton = new Button("Save");
    private Button deleteButton = new Button("Delete");
    private Button closeButton = new Button("Close");

    private Label label = new Label("TB REACH Users");
    private Label label_1 = new Label("New label");

    public Prototype() {
	initWidget(flexTable);
	flexTable.setSize("80%", "100%");
	flexTable.setWidget(0, 1, topFlexTable);
	label.setStyleName("title");
	topFlexTable.setWidget(0, 0, label);
	flexTable.setWidget(1, 0, leftFlexTable);
	listBox.setEnabled(false);
	leftFlexTable.setWidget(0, 0, listBox);
	listBox.setVisibleItemCount(5);
	flexTable.setWidget(1, 1, rightFlexTable);
	rightFlexTable.setSize("100%", "100%");
	rightFlexTable.setWidget(0, 0, label_1);
	rightFlexTable.setWidget(0, 1, textBox);
	createButton.setEnabled(false);
	rightFlexTable.setWidget(1, 0, createButton);
	rightFlexTable.setWidget(1, 1, grid);
	grid.setSize("100%", "100%");
	saveButton.setEnabled(false);
	grid.setWidget(0, 0, saveButton);
	deleteButton.setEnabled(false);
	grid.setWidget(0, 1, deleteButton);
	grid.setWidget(0, 2, closeButton);
	flexTable.getRowFormatter().setVerticalAlign(1,
		HasVerticalAlignment.ALIGN_TOP);

	createButton.addClickHandler(this);
	saveButton.addClickHandler(this);
	deleteButton.addClickHandler(this);
	closeButton.addClickHandler(this);
	refreshList();
	setRights(menuName);
    }

    public void refreshList() {
	try {
	    service.getColumnData(tableName, "", "",
		    new AsyncCallback<String[]>() {
			@Override
			public void onSuccess(String[] result) {
			    for (int i = 0; i < result.length; i++) {
				// Not implemented
			    }
			}

			@Override
			public void onFailure(Throwable caught) {
			    load(false);
			}
		    });
	} catch (Exception e) {
	    e.printStackTrace();
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
	current = null;
    }

    @Override
    public void fillData() {
	try {
	    service.getColumnData(tableName, "", "",
		    new AsyncCallback<String[]>() {
			@Override
			public void onSuccess(String[] result) {
			    load(false);
			}

			@Override
			public void onFailure(Throwable caught) {
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
	if (true) {
	    errorMessage.append(CustomMessage
		    .getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
	    valid = false;
	}
	/* Validate data-type rules */
	if (true) {
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

    @Override
    public void saveData() {
	if (validate()) {
	    /* Validate uniqueness */
	    try {
		service.exists(menuName, "ColumnName='" + "VALUE" + "'",
			new AsyncCallback<Boolean>() {
			    @Override
			    public void onSuccess(Boolean result) {
				if (!result) {
				    try {
					service.saveUser(current,
						new AsyncCallback<Boolean>() {
						    @Override
						    public void onSuccess(
							    Boolean result) {
							if (result) {
							    Window.alert(CustomMessage
								    .getInfoMessage(InfoType.INSERTED));
							    clearUp();
							} else
							    Window.alert(CustomMessage
								    .getErrorMessage(ErrorType.INSERT_ERROR));
							load(false);
						    }

						    @Override
						    public void onFailure(
							    Throwable caught) {
							Window.alert(CustomMessage
								.getErrorMessage(ErrorType.INSERT_ERROR));
							load(false);
						    }
						});
				    } catch (Exception e) {
					e.printStackTrace();
					load(false);
				    }
				} else
				    Window.alert(CustomMessage
					    .getErrorMessage(ErrorType.DUPLICATION_ERROR));
				load(false);
			    }

			    @Override
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
		service.updateUser(current, new AsyncCallback<Boolean>() {
		    @Override
		    public void onSuccess(Boolean result) {
			if (result)
			    Window.alert(CustomMessage
				    .getInfoMessage(InfoType.UPDATED));
			else
			    Window.alert(CustomMessage
				    .getErrorMessage(ErrorType.UPDATE_ERROR));
			load(false);
		    }

		    @Override
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
		service.deleteUser(current, new AsyncCallback<Boolean>() {
		    @Override
		    public void onSuccess(Boolean result) {
			if (result) {
			    Window.alert(CustomMessage
				    .getInfoMessage(InfoType.DELETED));
			    clearUp();
			} else
			    Window.alert(CustomMessage
				    .getErrorMessage(ErrorType.DELETE_ERROR));
			load(false);
		    }

		    @Override
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
			@Override
			public void onSuccess(Boolean[] result) {
			    final Boolean[] userRights = result;
			    try {
				service.findUser(XSMS.getCurrentUser(),
					new AsyncCallback<Users>() {
					    @Override
					    public void onSuccess(Users result) {
						rights.setRoleRights(
							result.getRole(),
							userRights);
						// TODO: Decide select rights
						listBox.setEnabled(rights
							.getAccess(AccessType.SELECT));
						createButton.setEnabled(rights
							.getAccess(AccessType.INSERT));
						saveButton.setEnabled(rights
							.getAccess(AccessType.UPDATE));
						deleteButton.setEnabled(rights
							.getAccess(AccessType.DELETE));
						load(false);
					    }

					    @Override
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

			@Override
			public void onFailure(Throwable caught) {
			    load(false);
			}
		    });
	} catch (Exception e) {
	    e.printStackTrace();
	    load(false);
	}
    }

    @Override
    public void onClick(ClickEvent event) {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == createButton) {
	    if (createButton.isDown())
		clearUp();
	    listBox.setEnabled(!createButton.isDown());
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

    @Override
    public void onChange(ChangeEvent event) {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == listBox) {
	    fillData();
	}
    }
}
