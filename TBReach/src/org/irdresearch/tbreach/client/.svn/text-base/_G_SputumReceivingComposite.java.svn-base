package org.irdresearch.tbreach.client;

import java.util.Date;
import java.util.Iterator;

import org.irdresearch.tbreach.shared.AccessType;
import org.irdresearch.tbreach.shared.CustomMessage;
import org.irdresearch.tbreach.shared.ErrorType;
import org.irdresearch.tbreach.shared.InfoType;
import org.irdresearch.tbreach.shared.TBR;
import org.irdresearch.tbreach.shared.UserRightsUtil;
import org.irdresearch.tbreach.shared.model.GeneXpertResults;
import org.irdresearch.tbreach.shared.model.SputumResults;
import org.irdresearch.tbreach.shared.model.Users;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;

public class _G_SputumReceivingComposite extends Composite implements IForm, ClickHandler
{
	private static ServerServiceAsync	service					= GWT.create(ServerService.class);
	private static LoadingWidget		loading					= new LoadingWidget();
	private static final String			menuName				= "LABORATORY";
	// private static final String tableName = "SputumResults";

	private UserRightsUtil				rights					= new UserRightsUtil();
	private boolean						valid;
	private SputumResults				current;

	private FlexTable					flexTable				= new FlexTable();
	private FlexTable					topFlexTable			= new FlexTable();
	private FlexTable					rightFlexTable			= new FlexTable();
	private Grid						grid					= new Grid(1, 3);
	private HorizontalPanel				horizontalPanel			= new HorizontalPanel();

	private Button						saveButton				= new Button("Verify");
	private Button						closeButton				= new Button("Close");
	private Button						searchButton			= new Button("Search");

	private Label						formTitle				= new Label("TB REACH Sputum Receiving");
	private Label						lblBarcode				= new Label("Barcode:");
	private Label						lblPatientId			= new Label("Patient ID:");
	private Label						patientIDLabel			= new Label("");
	private Label						lblRejected				= new Label("Rejected?:");
	//private Label						lblMrNo					= new Label("MR No:");
	//private Label						MRNoLabel				= new Label("");
	private TextBox						sputumTestIdTextBox		= new TextBox();
	private TextBox						remarksTextBox			= new TextBox();
	private ListBox						remarksComboBox			= new ListBox();
	private CheckBox					rejectedCheckBox		= new CheckBox("");

	public _G_SputumReceivingComposite()
	{
		initWidget(flexTable);
		flexTable.setSize("80%", "100%");
		flexTable.setWidget(0, 0, topFlexTable);
		formTitle.setStyleName("title");
		topFlexTable.setWidget(0, 0, formTitle);
		flexTable.setWidget(1, 0, rightFlexTable);
		rightFlexTable.setSize("100%", "100%");
		rightFlexTable.setWidget(0, 0, lblBarcode);
		sputumTestIdTextBox.setVisibleLength(12);
		sputumTestIdTextBox.setMaxLength(12);
		rightFlexTable.setWidget(0, 1, sputumTestIdTextBox);
		searchButton.setEnabled(false);
		searchButton.setText("Search");
		rightFlexTable.setWidget(1, 1, searchButton);
		/*rightFlexTable.setWidget(2, 0, lblMrNo);
		rightFlexTable.setWidget(2, 1, MRNoLabel);*/
		rightFlexTable.setWidget(2, 0, lblPatientId);
		rightFlexTable.setWidget(2, 1, patientIDLabel);
		rightFlexTable.setWidget(3, 0, lblRejected);
		rightFlexTable.setWidget(3, 1, horizontalPanel);
		rejectedCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
		{
			public void onValueChange(ValueChangeEvent<Boolean> event)
			{
				remarksComboBox.setEnabled(event.getValue());
			}
		});

		horizontalPanel.add(rejectedCheckBox);
		remarksComboBox.addItem("SALIVA");
		remarksComboBox.addItem("FOOD PARTICLES");
		remarksComboBox.addItem("QUANTITY NOT SUFFICIENT");
		remarksComboBox.addItem("OTHER");
		remarksComboBox.setEnabled(false);
		remarksComboBox.addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event)
			{
				remarksTextBox.setEnabled(TBRClient.get(remarksComboBox).equals("OTHER"));
			}
		});

		horizontalPanel.add(remarksComboBox);
		remarksTextBox.setEnabled(false);
		remarksTextBox.setMaxLength(200);
		horizontalPanel.add(remarksTextBox);
		rightFlexTable.setWidget(5, 1, grid);
		grid.setSize("100%", "100%");
		saveButton.setEnabled(false);
		saveButton.setText("Verify");
		grid.setWidget(0, 0, saveButton);
		grid.setWidget(0, 2, closeButton);
		searchButton.addClickHandler(this);
		saveButton.addClickHandler(this);
		closeButton.addClickHandler(this);
		flexTable.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
		setRights(menuName);
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
		else if (w instanceof CheckBox)
		{
			((CheckBox) w).setValue(false);
		}
	}

	public void setCurrent()
	{
		// Not implemented
	}

	@Override
	public void clearUp()
	{
		clearControls(flexTable);
		//MRNoLabel.setText("");
		patientIDLabel.setText("");
	}

	@Override
	public boolean validate()
	{
		final StringBuilder errorMessage = new StringBuilder();
		valid = true;
		/* Validate mandatory fields */
		if (sputumTestIdTextBox.getText().equalsIgnoreCase(""))
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
			try
			{
				// Not implemented
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
				try
				{
					current.setLaboratoryId(TBR.getCurrentUser());
					current.setDateSubmitted(new Date());
					current.setDateTested(new Date());
					current.setRemarks("VERIFIED");
					current.setOtherRemarks("LAB INCHARGE ID: " + TBR.getCurrentUser());
					// If Sputum Remarks are REJECTED, then delete Gene Xpert entry
					if (rejectedCheckBox.getValue())
					{
						current.setRemarks("REJECTED " + TBRClient.get(remarksComboBox).toUpperCase());
						if (remarksTextBox.isEnabled())
							current.setOtherRemarks(TBRClient.get(remarksTextBox).toUpperCase());
						else
							current.setOtherRemarks("");
					}
					service.updateSputumResults(current, false, new AsyncCallback<Boolean>()
					{
						@Override
						public void onSuccess(Boolean result)
						{
							if (result)
							{
								Window.alert(CustomMessage.getInfoMessage(InfoType.VALID));
								sputumTestIdTextBox.setReadOnly(false);
								searchButton.setEnabled(true);
								rejectedCheckBox.setValue(false);
								remarksComboBox.setEnabled(false);
								remarksTextBox.setEnabled(false);
								clearUp();
							}
							else
							{
								Window.alert("Barcode was not validated.");
							}
							clearUp();
							load(false);
						}

						@Override
						public void onFailure(Throwable caught)
						{
							Window.alert(CustomMessage.getErrorMessage(ErrorType.UNKNOWN_ERROR));
							load(false);
						}
					});

					if (rejectedCheckBox.getValue())
					{
						load(true);
						service.findGeneXpertResults(TBRClient.get(sputumTestIdTextBox), new AsyncCallback<GeneXpertResults>()
						{
							@Override
							public void onSuccess(GeneXpertResults result)
							{
								try
								{
									service.deleteGeneXpertResults(result, new AsyncCallback<Boolean>()
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
				}
				catch (Exception e)
				{
					e.printStackTrace();
					load(false);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				load(false);
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
				// Not implemented
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
			service.findSputumResultsBySputumTestID(TBRClient.get(sputumTestIdTextBox), new AsyncCallback<SputumResults>()
			{
				@Override
				public void onSuccess(SputumResults result)
				{
					current = result;
					patientIDLabel.setText(current.getPatientId());
					sputumTestIdTextBox.setReadOnly(true);
					searchButton.setEnabled(false);
					saveButton.setEnabled(true);
					/*service.getObject("Patient", "MRNo", "PatientID='" + result.getPatientId() + "'", new AsyncCallback<String>()
					{
						@Override
						public void onSuccess(String result)
						{
							MRNoLabel.setText(result);
						}

						@Override
						public void onFailure(Throwable caught)
						{
							// Not implemented
						}
					});*/
					load(false);
				}

				@Override
				public void onFailure(Throwable caught)
				{
					Window.alert("The barcode is either invalid or not registered yet.");
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
								searchButton.setEnabled(rights.getAccess(AccessType.SELECT));
								saveButton.setEnabled(rights.getAccess(AccessType.UPDATE));
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
		if (sender == saveButton)
		{
			updateData();
		}
		else if (sender == searchButton)
		{
			fillData();
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear();
		}
	}
}
