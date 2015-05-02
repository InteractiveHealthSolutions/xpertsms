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
import com.google.gwt.user.client.ui.PasswordTextBox;
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
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class UsersComposite extends Composite implements IForm, ClickHandler,
	ChangeHandler {
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static LoadingWidget loading = new LoadingWidget();
    private static final String menuName = "SETUP";
    private static final String tableName = "Users";

    private UserRightsUtil rights = new UserRightsUtil();
    private boolean valid;
    private Users currentUser;

    private FlexTable flexTable = new FlexTable();
    private FlexTable leftFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private Grid grid = new Grid(1, 3);

    private ListBox userRolesComboBox = new ListBox();
    private ListBox usersListBox = new ListBox();

    private Label lblUserName = new Label("User Name:");
    private TextBox userNameTextBox = new TextBox();
    private Label lblUserRole = new Label("User Role:");
    private ListBox userRoleComboBox = new ListBox();
    private Label lblStatus = new Label("Status:");
    private ListBox userStatusComboBox = new ListBox();
    private Label lblPassword = new Label("Password:");
    private PasswordTextBox passwordTextBox = new PasswordTextBox();
    private Label lblSecretQuestion = new Label("Secret Question:");
    private ListBox secretQuestionComboBox = new ListBox();
    private Label lblSecretAnswer = new Label("Secret Answer:");
    private PasswordTextBox secretAnswerPasswordTextBox = new PasswordTextBox();

    private Button saveButton = new Button("Save");
    private ToggleButton createButton = new ToggleButton("Create User");
    private Button deleteButton = new Button("Delete");
    private Button closeButton = new Button("Close");
    private FlexTable topFlexTable = new FlexTable();
    private Label lblXpertSmsUsers = new Label("XpertSMS Users");

    public UsersComposite() {
	initWidget(flexTable);
	flexTable.setSize("80%", "100%");
	flexTable.setWidget(0, 1, topFlexTable);
	lblXpertSmsUsers.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblXpertSmsUsers);
	flexTable.setWidget(1, 0, leftFlexTable);
	leftFlexTable.setSize("100%", "100%");
	userRolesComboBox.setEnabled(false);
	userRolesComboBox.addChangeHandler(this);
	leftFlexTable.setWidget(0, 0, userRolesComboBox);
	usersListBox.addClickHandler(this);
	leftFlexTable.setWidget(1, 0, usersListBox);
	usersListBox.setVisibleItemCount(5);
	leftFlexTable.getCellFormatter().setVerticalAlignment(1, 0,
		HasVerticalAlignment.ALIGN_MIDDLE);
	leftFlexTable.getCellFormatter().setHorizontalAlignment(1, 0,
		HasHorizontalAlignment.ALIGN_LEFT);
	flexTable.setWidget(1, 1, rightFlexTable);
	rightFlexTable.setSize("100%", "100%");
	rightFlexTable.setWidget(0, 0, lblUserName);
	rightFlexTable.setWidget(0, 1, userNameTextBox);
	rightFlexTable.setWidget(1, 0, lblUserRole);
	rightFlexTable.setWidget(1, 1, userRoleComboBox);
	rightFlexTable.setWidget(2, 0, lblStatus);
	rightFlexTable.setWidget(2, 1, userStatusComboBox);
	rightFlexTable.setWidget(3, 0, lblPassword);
	passwordTextBox.setMaxLength(20);
	rightFlexTable.setWidget(3, 1, passwordTextBox);
	rightFlexTable.setWidget(4, 0, lblSecretQuestion);
	rightFlexTable.setWidget(4, 1, secretQuestionComboBox);
	rightFlexTable.setWidget(5, 0, lblSecretAnswer);
	secretAnswerPasswordTextBox.setMaxLength(20);
	rightFlexTable.setWidget(5, 1, secretAnswerPasswordTextBox);
	createButton.setEnabled(false);
	rightFlexTable.setWidget(6, 0, createButton);
	rightFlexTable.setWidget(6, 1, grid);
	grid.setSize("100%", "100%");
	saveButton.setEnabled(false);
	grid.setWidget(0, 0, saveButton);
	deleteButton.setEnabled(false);
	grid.setWidget(0, 1, deleteButton);
	grid.setWidget(0, 2, closeButton);
	flexTable.getCellFormatter().setVerticalAlignment(1, 0,
		HasVerticalAlignment.ALIGN_TOP);
	flexTable.getCellFormatter().setHorizontalAlignment(1, 1,
		HasHorizontalAlignment.ALIGN_LEFT);
	flexTable.getCellFormatter().setVerticalAlignment(1, 1,
		HasVerticalAlignment.ALIGN_TOP);

	createButton.addClickHandler(this);
	saveButton.addClickHandler(this);
	deleteButton.addClickHandler(this);
	closeButton.addClickHandler(this);

	refreshLists();
	setRights(menuName.toUpperCase());
    }

    private void refreshLists() {
	secretQuestionComboBox.clear();
	userStatusComboBox.clear();
	userRoleComboBox.clear();
	userRolesComboBox.clear();
	for (String s : XSMS.getSecretQuestions())
	    secretQuestionComboBox.addItem(s);
	for (String s : XSMS.getList(ListType.USER_STATUS))
	    userStatusComboBox.addItem(s);
	for (String s : XSMS.getList(ListType.USER_ROLE)) {
	    userRoleComboBox.addItem(s);
	    userRolesComboBox.addItem(s);
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

    @Override
    public void clearUp() {
	clearControls(flexTable);
	usersListBox.clear();
	userNameTextBox.setFocus(true);
    }

    /**
     * Validation Rules for Users form
     * 
     * 1. All fields are mandatory
     * 
     * 2. User name is unique and cannot contain spaces
     * 
     * 3. Password must be of at least 8 characters
     */
    @Override
    public boolean validate() {
	final StringBuilder errorMessage = new StringBuilder();
	valid = true;
	/* Validate mandatory fields */
	if (userNameTextBox.getText().equals("")
		|| passwordTextBox.getText().equals("")
		|| secretAnswerPasswordTextBox.getText().equals("")) {
	    errorMessage.append(CustomMessage
		    .getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
	    valid = false;
	}
	/* Validate data-type rules */
	if (userNameTextBox.getText().contains(" ")) {
	    errorMessage.append(CustomMessage
		    .getErrorMessage(ErrorType.INVALID_DATA_ERROR) + "\n");
	    valid = false;
	}
	/* Validate password */
	if (passwordTextBox.getText().length() < 8) {
	    errorMessage
		    .append("Password is too short. Please enter a strong password of at least 8 characters\n");
	    valid = false;
	}
	/* Validate uniqueness */
	try {
	    service.exists(tableName, "UserName='" + userNameTextBox.getText()
		    + "'", new AsyncCallback<Boolean>() {
		@Override
		public void onSuccess(Boolean result) {
		    if (result) {
			errorMessage
				.append("Username already exists. Please provide different user name.\n");
			valid = false;
		    }
		}

		@Override
		public void onFailure(Throwable caught) {
		    errorMessage.append(CustomMessage
			    .getErrorMessage(ErrorType.DATA_ACCESS_ERROR)
			    + "\n");
		}
	    });
	} catch (Exception e) {
	    e.printStackTrace();
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
	    Users user = new Users(
		    XpertSmsWebClient.get(userNameTextBox).toUpperCase(),
		    XpertSmsWebClient.get(userNameTextBox).toUpperCase(),
		    XpertSmsWebClient.get(userRoleComboBox).toUpperCase(),
		    XpertSmsWebClient.get(userStatusComboBox).toUpperCase(),
		    passwordTextBox.getText(),
		    XpertSmsWebClient.get(secretQuestionComboBox).toUpperCase(),
		    secretAnswerPasswordTextBox.getText());
	    try {
		service.saveUser(user, new AsyncCallback<Boolean>() {
		    @Override
		    public void onSuccess(Boolean result) {
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
		    public void onFailure(Throwable caught) {
			Window.alert(CustomMessage
				.getErrorMessage(ErrorType.INSERT_ERROR));
			load(false);
		    }
		});
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void updateData() {
	if (validate()) {
	    try {
		service.findUser(currentUser.getUserName(),
			new AsyncCallback<Users>() {
			    @Override
			    public void onSuccess(Users result) {
				try {
				    result.setRole(XpertSmsWebClient.get(
					    userRoleComboBox).toUpperCase());
				    result.setStatus(XpertSmsWebClient.get(
					    userStatusComboBox).toUpperCase());
				    result.setPassword(XpertSmsWebClient
					    .get(passwordTextBox));
				    result.setSecretQuestion(XpertSmsWebClient
					    .get(secretQuestionComboBox)
					    .toUpperCase());
				    result.setSecretAnswer(XpertSmsWebClient
					    .get(secretAnswerPasswordTextBox));
				    service.updateUser(result,
					    new AsyncCallback<Boolean>() {
						@Override
						public void onSuccess(
							Boolean result) {
						    if (result)
							Window.alert(CustomMessage
								.getInfoMessage(InfoType.UPDATED));
						    else
							Window.alert(CustomMessage
								.getErrorMessage(ErrorType.UPDATE_ERROR));
						    load(false);
						}

						@Override
						public void onFailure(
							Throwable caught) {
						    Window.alert(CustomMessage
							    .getErrorMessage(ErrorType.UPDATE_ERROR));
						    load(false);
						}
					    });
				} catch (Exception e) {
				    e.printStackTrace();
				    Window.alert(CustomMessage
					    .getErrorMessage(ErrorType.UPDATE_ERROR));
				} finally {
				    load(false);
				}
			    }

			    @Override
			    public void onFailure(Throwable caught) {
				Window.alert(CustomMessage
					.getErrorMessage(ErrorType.ITEM_NOT_FOUND));
				load(false);
			    }
			});
	    } catch (Exception e) {
		e.printStackTrace();
		Window.alert(CustomMessage
			.getErrorMessage(ErrorType.ITEM_NOT_FOUND));
	    }
	}
    }

    @Override
    public void deleteData() {
	if (validate()) {
	    try {
		service.deleteUser(currentUser, new AsyncCallback<Boolean>() {
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
		Window.alert(CustomMessage
			.getErrorMessage(ErrorType.DELETE_ERROR));
	    }
	}
    }

    @Override
    public void fillData() {
	try {
	    service.findUser(XpertSmsWebClient.get(usersListBox),
		    new AsyncCallback<Users>() {
			@Override
			public void onSuccess(Users result) {
			    currentUser = result;
			    userNameTextBox.setText(result.getUserName());
			    passwordTextBox.setText(result.getPassword());
			    userRoleComboBox.setSelectedIndex(XpertSmsWebClient
				    .getIndex(userRoleComboBox,
					    result.getRole()));
			    userStatusComboBox
				    .setSelectedIndex(XpertSmsWebClient
					    .getIndex(userStatusComboBox,
						    result.getStatus()));
			    secretQuestionComboBox.setSelectedIndex(XpertSmsWebClient
				    .getIndex(secretQuestionComboBox,
					    result.getSecretQuestion()));
			    secretAnswerPasswordTextBox.setText(result
				    .getSecretAnswer());
			    load(false);
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
						userRolesComboBox.setEnabled(rights
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

    @Override
    public void onClick(ClickEvent event) {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == usersListBox) {
	    fillData();
	} else if (sender == createButton) {
	    if (createButton.isDown())
		clearUp();
	    userRolesComboBox.setEnabled(!createButton.isDown());
	    usersListBox.setEnabled(!createButton.isDown());
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
	load(true);
	String selectedValue = XpertSmsWebClient.get(userRolesComboBox);
	try {
	    service.getColumnData(tableName, "UserName", "role='"
		    + selectedValue + "'", new AsyncCallback<String[]>() {
		@Override
		public void onSuccess(String[] result) {
		    usersListBox.clear();
		    for (int i = 0; i < result.length; i++)
			usersListBox.insertItem(result[i], i);
		    load(false);
		}

		@Override
		public void onFailure(Throwable caught) {
		    Window.alert(CustomMessage
			    .getErrorMessage(ErrorType.ITEM_NOT_FOUND));
		    load(false);
		}
	    });
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
