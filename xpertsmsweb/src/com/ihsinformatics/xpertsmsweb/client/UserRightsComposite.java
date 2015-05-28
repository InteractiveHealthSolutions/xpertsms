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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
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
import com.ihsinformatics.xpertsmsweb.shared.model.UserRights;
import com.ihsinformatics.xpertsmsweb.shared.model.UserRightsId;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class UserRightsComposite extends Composite implements IForm,
		ClickHandler, ChangeHandler {
	private static ServerServiceAsync service = GWT.create(ServerService.class);
	private static LoadingWidget loading = new LoadingWidget();
	private static final String menuName = "SETUP";
	private static final String tableName = "UserRights";

	private UserRightsUtil rights = new UserRightsUtil();
	private UserRights current;

	private FlexTable flexTable = new FlexTable();
	private FlexTable topFlexTable = new FlexTable();
	private FlexTable leftFlexTable = new FlexTable();
	private FlexTable rightFlexTable = new FlexTable();
	private Grid grid = new Grid(1, 3);

	private ListBox rolesListBox = new ListBox();

	private ToggleButton createButton = new ToggleButton("Create");
	private Button saveButton = new Button("Save");
	private Button deleteButton = new Button("Delete");
	private Button closeButton = new Button("Close");

	private Label lblXpertSmsUser = new Label("XpertSMS User Rights");
	private Label lblDataItem = new Label("Data Item:");
	private ListBox menuComboBox = new ListBox();
	private Label lblAccess = new Label("Access:");
	private FlexTable accessFlexTable = new FlexTable();
	private CheckBox searchCheckBox = new CheckBox("Search");
	private CheckBox insertCheckBox = new CheckBox("Insert");
	private CheckBox updateCheckBox = new CheckBox("Update");
	private CheckBox deleteCheckBox = new CheckBox("Delete");
	private CheckBox printCheckBox = new CheckBox("Print");

	public UserRightsComposite() {
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 1, topFlexTable);
		lblXpertSmsUser.setStyleName("title");
		topFlexTable.setWidget(0, 0, lblXpertSmsUser);
		flexTable.setWidget(1, 0, leftFlexTable);
		rolesListBox.setEnabled(false);
		rolesListBox.clear();
		leftFlexTable.setWidget(0, 0, rolesListBox);
		rolesListBox.setVisibleItemCount(5);
		flexTable.setWidget(1, 1, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblDataItem);
		rightFlexTable.setWidget(0, 1, menuComboBox);
		menuComboBox.setWidth("");
		rightFlexTable.setWidget(1, 0, lblAccess);
		rightFlexTable.setWidget(1, 1, accessFlexTable);
		accessFlexTable.setWidth("100%");
		accessFlexTable.setWidget(0, 0, insertCheckBox);
		accessFlexTable.setWidget(0, 1, searchCheckBox);
		accessFlexTable.setWidget(1, 0, updateCheckBox);
		accessFlexTable.setWidget(1, 1, printCheckBox);
		accessFlexTable.setWidget(2, 0, deleteCheckBox);
		createButton.setVisible(false);
		rightFlexTable.setWidget(2, 0, createButton);
		rightFlexTable.setWidget(2, 1, grid);
		grid.setSize("100%", "100%");
		grid.setWidget(0, 0, saveButton);
		grid.setWidget(0, 1, deleteButton);
		grid.setWidget(0, 2, closeButton);
		flexTable.getRowFormatter().setVerticalAlign(1,
				HasVerticalAlignment.ALIGN_TOP);

		rolesListBox.addClickHandler(this);
		createButton.addClickHandler(this);
		saveButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		closeButton.addClickHandler(this);
		menuComboBox.addChangeHandler(this);

		refreshLists();
		setRights(menuName);
	}

	private void refreshLists() {
		rolesListBox.clear();
		menuComboBox.clear();
		for (String s : XSMS.getList(ListType.USER_ROLE))
			rolesListBox.addItem(s);
		for (String s : XSMS.getList(ListType.MENU_NAME))
			menuComboBox.addItem(s);
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
		} else if (w instanceof CheckBox) {
			((CheckBox) w).setValue(true);
		}
	}

	public void setCurrent() {
		current.setSearchAccess(searchCheckBox.getValue());
		current.setInsertAccess(insertCheckBox.getValue());
		current.setUpdateAccess(updateCheckBox.getValue());
		current.setDeleteAccess(deleteCheckBox.getValue());
		current.setPrintAccess(printCheckBox.getValue());
	}

	@Override
	public void clearUp() {
		clearControls(flexTable);
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void saveData() {
		if (validate()) {
			/* Validate uniqueness */
			try {
				service.exists(tableName, "ColumnName='" + "'",
						new AsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								if (!result) {
									/* SAVE METHOD */
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
			}
		}
	}

	@Override
	public void updateData() {
		if (validate()) {
			setCurrent();
			try {
				service.updateUserRights(current, new AsyncCallback<Boolean>() {
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
				// Not implemented
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void fillData() {
		try {
			service.getRowRecord(
					"UserRights",
					new String[] { "SearchAccess", "InsertAccess",
							"UpdateAccess", "DeleteAccess", "PrintAccess" },
					"Role='" + XpertSmsWebClient.get(rolesListBox)
							+ "' and MenuName='"
							+ XpertSmsWebClient.get(menuComboBox) + "'",
					new AsyncCallback<String[]>() {
						@Override
						public void onSuccess(String[] result) {
							Boolean[] rights = new Boolean[result.length];
							for (int i = 0; i < result.length; i++)
								rights[i] = Boolean.parseBoolean(result[i]);
							current = new UserRights(new UserRightsId(
									XpertSmsWebClient.get(rolesListBox),
									XpertSmsWebClient.get(menuComboBox)),
									rights[0], rights[1], rights[2], rights[3],
									rights[4]);
							searchCheckBox.setValue(current.isSearchAccess());
							insertCheckBox.setValue(current.isInsertAccess());
							updateCheckBox.setValue(current.isUpdateAccess());
							deleteCheckBox.setValue(current.isDeleteAccess());
							printCheckBox.setValue(current.isPrintAccess());
							load(false);
						}

						@Override
						public void onFailure(Throwable caught) {
							// If User Rights are not available, try to Insert
							try {
								UserRights userRights = new UserRights(
										new UserRightsId(XpertSmsWebClient
												.get(rolesListBox),
												XpertSmsWebClient
														.get(menuComboBox)),
										false, false, false, false, false);
								service.saveUserRights(userRights,
										new AsyncCallback<Boolean>() {
											@Override
											public void onSuccess(Boolean result) {
												// Inserted
												fillData();
											}

											@Override
											public void onFailure(
													Throwable caught) {
												Window.alert(CustomMessage
														.getErrorMessage(ErrorType.DATA_ACCESS_ERROR));
											}
										});
							} catch (Exception e) {
								e.printStackTrace();
							}
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
												rolesListBox.setEnabled(rights
														.getAccess(AccessType.SELECT));
												createButton.setEnabled(false);
												saveButton.setEnabled(rights
														.getAccess(AccessType.UPDATE));
												deleteButton.setEnabled(false);
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
		if (sender == rolesListBox) {
			fillData();
		} else if (sender == createButton) {
			if (createButton.isDown())
				clearUp();
			rolesListBox.setEnabled(!createButton.isDown());
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
		if (sender == menuComboBox) {
			fillData();
		}
	}
}
