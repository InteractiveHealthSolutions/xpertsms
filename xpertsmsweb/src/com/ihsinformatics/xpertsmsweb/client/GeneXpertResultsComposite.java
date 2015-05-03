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
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.ihsinformatics.xpertsmsweb.shared.AccessType;
import com.ihsinformatics.xpertsmsweb.shared.CustomMessage;
import com.ihsinformatics.xpertsmsweb.shared.ErrorType;
import com.ihsinformatics.xpertsmsweb.shared.InfoType;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;
import com.ihsinformatics.xpertsmsweb.shared.model.Patient;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class GeneXpertResultsComposite extends Composite implements IForm,
	ClickHandler, ChangeHandler {
    private static ServerServiceAsync service = GWT.create(ServerService.class);
    private static LoadingWidget loading = new LoadingWidget();
    private static final String menuName = "LABORATORY";
    private static final String tableName = "GeneXpertResults";
    private UserRightsUtil rights = new UserRightsUtil();
    private boolean valid;
    private GeneXpertResults current;
    private String currentPatientID;

    DecoratedStackPanel mainStackPanel = new DecoratedStackPanel();

    private FlexTable flexTable = new FlexTable();
    private FlexTable topFlexTable = new FlexTable();
    private FlexTable rightFlexTable = new FlexTable();
    private FlexTable leftFlexTable = new FlexTable();
    private FlexTable metadataFlexTable = new FlexTable();
    private FlexTable resultFlexTable = new FlexTable();
    private FlexTable instrumentFlexTable = new FlexTable();
    private FlexTable probeFlexTable = new FlexTable();

    private Grid grid = new Grid(1, 3);

    private Button saveButton = new Button("Save");
    private Button closeButton = new Button("Close");
    private Button searchButton = new Button("Search");

    private Label lblXpertSmsLab = new Label("XpertSMS Gene Xpert Results");
    private Label lblPatientId = new Label("Patient ID:");
    private Label lblSputumTestId = new Label("Test ID:");
    private Label lblGeneXpertResults = new Label("GeneXpert Results:");
    private Label lblResult = new Label("MTB Result:");
    private Label lblErrorCode = new Label("Error Code:");
    private Label lblMtbBurden = new Label("MTB Burden:");
    private Label lblSmearResults = new Label("Rif Resistance:");
    private Label lblRemarks = new Label("Remarks:");
    private Label lblSputumTestIdSearch = new Label("Test ID");
    private Label lblPatientIdSearch = new Label("Patient ID");

    // newly added for Xpert
    private Label lblInstrumentId = new Label("Instrument ID");
    private Label lblModuleId = new Label("Module ID");
    private Label lblCartidgeId = new Label("Cartidge ID");
    private Label lblReagentLotId = new Label("Reagent Lot ID");
    private Label lblPcId = new Label("PC ID");
    private Label lblOperatorId = new Label("Operator ID");

    // Probe data
    private Label lblProbeResultA = new Label("Probe A Result");
    private Label lblProbeResultB = new Label("Probe B Result");
    private Label lblProbeResultC = new Label("Probe C Result");
    private Label lblProbeResultD = new Label("Probe D Result");
    private Label lblProbeResultE = new Label("Probe E Result");
    private Label lblProbeResultSPC = new Label("Probe SPC Result");

    private Label lblProbeCtA = new Label("Probe A Ct");
    private Label lblProbeCtB = new Label("Probe B Ct");
    private Label lblProbeCtC = new Label("Probe C Ct");
    private Label lblProbeCtD = new Label("Probe D Ct");
    private Label lblProbeCtE = new Label("Probe E Ct");
    private Label lblProbeCtSPC = new Label("Probe SPC Ct");

    private Label lblProbeEndptA = new Label("Probe A Endpt");
    private Label lblProbeEndptB = new Label("Probe B Endpt");
    private Label lblProbeEndptC = new Label("Probe C Endpt");
    private Label lblProbeEndptD = new Label("Probe D Endpt");
    private Label lblProbeEndptE = new Label("Probe E Endpt");
    private Label lblProbeEndptSPC = new Label("Probe SPC Endpt");

    private TextBox patientIdTextBox = new TextBox();
    private TextBox sputumTestID = new TextBox();
    private TextBox testIdSearchTextBox = new TextBox();
    private TextBox pidSearchTextBox = new TextBox();
    private TextBox remarksTextBox = new TextBox();
    private IntegerBox errorCodeIntegerBox = new IntegerBox();

    private ListBox drugResistanceComboBox = new ListBox();
    private ListBox sputumIdListBox = new ListBox();
    private ListBox geneXpertResultComboBox = new ListBox();
    private ListBox mtbBurdenComboBox = new ListBox();

    // newly added for Xpert
    private TextBox instrumentIdTextBox = new TextBox();
    private TextBox moduleIdTextBox = new TextBox();
    private TextBox cartidgeIdTextBox = new TextBox();
    private TextBox reagentLotIdTextBox = new TextBox();
    private TextBox pcIdTextBox = new TextBox();
    private TextBox operatorIdTextBox = new TextBox();

    // Probe data
    private ListBox probeResultAListBox = new ListBox();
    private ListBox probeResultBListBox = new ListBox();
    private ListBox probeResultCListBox = new ListBox();
    private ListBox probeResultDListBox = new ListBox();
    private ListBox probeResultEListBox = new ListBox();
    private ListBox probeResultSPCListBox = new ListBox();

    private DoubleBox probeCtADoubleBox = new DoubleBox();
    private DoubleBox probeCtBDoubleBox = new DoubleBox();
    private DoubleBox probeCtCDoubleBox = new DoubleBox();
    private DoubleBox probeCtDDoubleBox = new DoubleBox();
    private DoubleBox probeCtEDoubleBox = new DoubleBox();
    private DoubleBox probeCtSPCDoubleBox = new DoubleBox();

    private DoubleBox probeEndptADoubleBox = new DoubleBox();
    private DoubleBox probeEndptBDoubleBox = new DoubleBox();
    private DoubleBox probeEndptCDoubleBox = new DoubleBox();
    private DoubleBox probeEndptDDoubleBox = new DoubleBox();
    private DoubleBox probeEndptEDoubleBox = new DoubleBox();
    private DoubleBox probeEndptSPCDoubleBox = new DoubleBox();

    public GeneXpertResultsComposite() {
	initWidget(flexTable);
	flexTable.setSize("80%", "100%");
	flexTable.setWidget(0, 1, topFlexTable);
	lblXpertSmsLab.setStyleName("title");
	topFlexTable.setWidget(0, 0, lblXpertSmsLab);
	flexTable.setWidget(1, 0, leftFlexTable);
	leftFlexTable.setWidget(0, 0, lblSputumTestIdSearch);
	leftFlexTable.setWidget(0, 1, testIdSearchTextBox);
	testIdSearchTextBox.setVisibleLength(12);
	testIdSearchTextBox.setMaxLength(12);

	leftFlexTable.setWidget(1, 0, lblPatientIdSearch);
	leftFlexTable.setWidget(1, 1, pidSearchTextBox);
	testIdSearchTextBox.setVisibleLength(12);
	testIdSearchTextBox.setMaxLength(12);

	searchButton.setEnabled(false);
	searchButton.setText("Search");
	leftFlexTable.setWidget(2, 0, searchButton);
	leftFlexTable.setWidget(3, 0, sputumIdListBox);
	sputumIdListBox.setVisibleItemCount(5);
	sputumIdListBox.setEnabled(true);
	leftFlexTable.getCellFormatter().setHorizontalAlignment(3, 0,
		HasHorizontalAlignment.ALIGN_LEFT);
	leftFlexTable.getCellFormatter().setHorizontalAlignment(2, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	leftFlexTable.getCellFormatter().setHorizontalAlignment(1, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.setWidget(1, 1, rightFlexTable);
	rightFlexTable.setSize("100%", "100%");
	rightFlexTable.setWidget(0, 0, mainStackPanel);

	mainStackPanel.setWidth("100%");
	mainStackPanel.add(metadataFlexTable, "Results and Test Data", false);

	lblPatientId.setWordWrap(false);
	metadataFlexTable.setWidget(0, 0, lblPatientId);
	patientIdTextBox.setVisibleLength(12);
	patientIdTextBox.setMaxLength(12);
	metadataFlexTable.setWidget(0, 1, patientIdTextBox);

	lblSputumTestId.setWordWrap(false);
	metadataFlexTable.setWidget(1, 0, lblSputumTestId);
	sputumTestID.setVisibleLength(8);
	sputumTestID.setMaxLength(8);
	metadataFlexTable.setWidget(1, 1, sputumTestID);
	// rightFlexTable.setWidget(2, 0, lblIrs);
	// irsTextBox.setVisibleLength(8);
	// irsTextBox.setMaxLength(8);
	// rightFlexTable.setWidget(2, 1, irsTextBox);
	lblGeneXpertResults.setWordWrap(false);
	metadataFlexTable.setWidget(2, 0, lblGeneXpertResults);
	metadataFlexTable.setWidget(2, 1, resultFlexTable);

	lblResult.setWordWrap(false);
	resultFlexTable.setWidget(0, 0, lblResult);
	geneXpertResultComboBox.addItem("MTB NOT DETECTED");
	geneXpertResultComboBox.addItem("MTB DETECTED");
	geneXpertResultComboBox.addItem("INVALID");
	geneXpertResultComboBox.addItem("NO RESULT");
	geneXpertResultComboBox.addItem("ERROR");
	resultFlexTable.setWidget(0, 1, geneXpertResultComboBox);

	lblSmearResults.setWordWrap(false);
	resultFlexTable.setWidget(1, 0, lblSmearResults);
	drugResistanceComboBox.addItem("NOT DETECTED");
	drugResistanceComboBox.addItem("DETECTED");
	drugResistanceComboBox.addItem("INDETERMINATE");
	drugResistanceComboBox.setEnabled(false);
	resultFlexTable.setWidget(1, 1, drugResistanceComboBox);

	lblMtbBurden.setWordWrap(false);
	resultFlexTable.setWidget(2, 0, lblMtbBurden);
	mtbBurdenComboBox.addItem("VERY LOW");
	mtbBurdenComboBox.addItem("LOW");
	mtbBurdenComboBox.addItem("MEDIUM");
	mtbBurdenComboBox.addItem("HIGH");
	mtbBurdenComboBox.setEnabled(false);
	resultFlexTable.setWidget(2, 1, mtbBurdenComboBox);

	lblErrorCode.setWordWrap(false);
	resultFlexTable.setWidget(3, 0, lblErrorCode);
	errorCodeIntegerBox.setEnabled(false);
	errorCodeIntegerBox.setText("0");
	errorCodeIntegerBox.setVisibleLength(4);
	resultFlexTable.setWidget(3, 1, errorCodeIntegerBox);

	lblRemarks.setWordWrap(false);
	metadataFlexTable.setWidget(3, 0, lblRemarks);
	remarksTextBox.setVisibleLength(200);
	remarksTextBox.setMaxLength(255);
	metadataFlexTable.setWidget(3, 1, remarksTextBox);
	remarksTextBox.setWidth("100%");
	/*
	 * instrumentIdTextBox = new TextBox(); private TextBox moduleIdTextBox
	 * = new TextBox(); private TextBox cartidgeIdTextBox = new TextBox();
	 * private TextBox reagentLotIdTextBox = new TextBox(); private TextBox
	 * pcIdTextBox
	 */

	mainStackPanel.add(instrumentFlexTable,
		"Instrument and Materials Data", false);

	lblInstrumentId.setWordWrap(false);
	instrumentFlexTable.setWidget(0, 0, lblInstrumentId);
	instrumentFlexTable.setWidget(0, 1, instrumentIdTextBox);

	lblCartidgeId.setWordWrap(false);
	instrumentFlexTable.setWidget(1, 0, lblCartidgeId);
	instrumentFlexTable.setWidget(1, 1, cartidgeIdTextBox);

	lblReagentLotId.setWordWrap(false);
	instrumentFlexTable.setWidget(2, 0, lblReagentLotId);
	instrumentFlexTable.setWidget(2, 1, reagentLotIdTextBox);

	lblModuleId.setWordWrap(false);
	instrumentFlexTable.setWidget(3, 0, lblModuleId);
	instrumentFlexTable.setWidget(3, 1, moduleIdTextBox);

	lblPcId.setWordWrap(false);
	instrumentFlexTable.setWidget(4, 0, lblPcId);
	instrumentFlexTable.setWidget(4, 1, pcIdTextBox);

	lblOperatorId.setWordWrap(false);
	instrumentFlexTable.setWidget(5, 0, lblOperatorId);
	instrumentFlexTable.setWidget(5, 1, operatorIdTextBox);

	mainStackPanel.add(probeFlexTable, "Probe Data", false);

	// Probe A
	lblProbeResultA.setWordWrap(false);
	probeFlexTable.setWidget(0, 0, lblProbeResultA);
	probeFlexTable.setWidget(0, 1, probeResultAListBox);

	probeResultAListBox.addItem("POS");
	probeResultAListBox.addItem("NEG");
	probeResultAListBox.addItem("NA");
	probeResultAListBox.addItem("NO RESULT");

	lblProbeCtA.setWordWrap(false);
	;
	probeFlexTable.setWidget(1, 0, lblProbeCtA);
	probeFlexTable.setWidget(1, 1, probeCtADoubleBox);
	lblProbeEndptA.setWordWrap(false);
	probeFlexTable.setWidget(2, 0, lblProbeEndptA);
	probeFlexTable.setWidget(2, 1, probeEndptADoubleBox);

	// Probe B
	lblProbeResultB.setWordWrap(false);
	probeFlexTable.setWidget(3, 0, lblProbeResultB);
	probeFlexTable.setWidget(3, 1, probeResultBListBox);
	probeResultBListBox.addItem("POS");
	probeResultBListBox.addItem("NEG");
	probeResultBListBox.addItem("NA");
	probeResultBListBox.addItem("NO RESULT");

	lblProbeCtB.setWordWrap(false);
	probeFlexTable.setWidget(4, 0, lblProbeCtB);
	probeFlexTable.setWidget(4, 1, probeCtBDoubleBox);
	lblProbeEndptB.setWordWrap(false);
	probeFlexTable.setWidget(5, 0, lblProbeEndptB);
	probeFlexTable.setWidget(5, 1, probeEndptBDoubleBox);

	// Probe C
	lblProbeResultC.setWordWrap(false);
	probeFlexTable.setWidget(6, 0, lblProbeResultC);
	probeFlexTable.setWidget(6, 1, probeResultCListBox);
	probeResultCListBox.addItem("POS");
	probeResultCListBox.addItem("NEG");
	probeResultCListBox.addItem("NA");
	probeResultCListBox.addItem("NO RESULT");

	lblProbeCtC.setWordWrap(false);
	probeFlexTable.setWidget(7, 0, lblProbeCtC);
	probeFlexTable.setWidget(7, 1, probeCtCDoubleBox);
	lblProbeEndptC.setWordWrap(false);
	probeFlexTable.setWidget(8, 0, lblProbeEndptC);
	probeFlexTable.setWidget(8, 1, probeEndptCDoubleBox);

	// Probe D
	lblProbeResultD.setWordWrap(false);
	probeFlexTable.setWidget(9, 0, lblProbeResultD);
	probeFlexTable.setWidget(9, 1, probeResultDListBox);
	probeResultDListBox.addItem("POS");
	probeResultDListBox.addItem("NEG");
	probeResultDListBox.addItem("NA");
	probeResultDListBox.addItem("NO RESULT");

	lblProbeCtD.setWordWrap(false);
	probeFlexTable.setWidget(10, 0, lblProbeCtD);
	probeFlexTable.setWidget(10, 1, probeCtDDoubleBox);
	lblProbeEndptD.setWordWrap(false);
	probeFlexTable.setWidget(11, 0, lblProbeEndptD);
	probeFlexTable.setWidget(11, 1, probeEndptDDoubleBox);

	// Probe E
	lblProbeResultE.setWordWrap(false);
	probeFlexTable.setWidget(12, 0, lblProbeResultE);
	probeFlexTable.setWidget(12, 1, probeResultEListBox);
	probeResultEListBox.addItem("POS");
	probeResultEListBox.addItem("NEG");
	probeResultEListBox.addItem("NA");
	probeResultEListBox.addItem("NO RESULT");

	lblProbeCtE.setWordWrap(false);
	probeFlexTable.setWidget(13, 0, lblProbeCtE);
	probeFlexTable.setWidget(13, 1, probeCtEDoubleBox);
	lblProbeEndptE.setWordWrap(false);
	probeFlexTable.setWidget(14, 0, lblProbeEndptE);
	probeFlexTable.setWidget(14, 1, probeEndptEDoubleBox);

	// ProbeSPC
	lblProbeResultSPC.setWordWrap(false);
	probeFlexTable.setWidget(15, 0, lblProbeResultSPC);
	probeFlexTable.setWidget(15, 1, probeResultSPCListBox);
	probeResultSPCListBox.addItem("POS");
	probeResultSPCListBox.addItem("NEG");
	probeResultSPCListBox.addItem("NA");
	probeResultSPCListBox.addItem("NO RESULT");

	lblProbeCtE.setWordWrap(false);
	probeFlexTable.setWidget(16, 0, lblProbeCtSPC);
	probeFlexTable.setWidget(16, 1, probeCtSPCDoubleBox);
	lblProbeEndptE.setWordWrap(false);
	probeFlexTable.setWidget(17, 0, lblProbeEndptSPC);
	probeFlexTable.setWidget(17, 1, probeEndptSPCDoubleBox);

	rightFlexTable.setWidget(1, 0, grid);
	grid.setSize("100%", "100%");
	saveButton.setEnabled(false);
	grid.setWidget(0, 0, saveButton);
	grid.setWidget(0, 2, closeButton);
	rightFlexTable.getCellFormatter().setVerticalAlignment(3, 0,
		HasVerticalAlignment.ALIGN_TOP);
	flexTable.getRowFormatter().setVerticalAlign(1,
		HasVerticalAlignment.ALIGN_TOP);

	saveButton.addClickHandler(this);
	searchButton.addClickHandler(this);
	sputumIdListBox.addClickHandler(this);
	closeButton.addClickHandler(this);
	geneXpertResultComboBox.addChangeHandler(this);

	setRights(menuName);
    }

    public void refreshList() {
	try {
	    service.getColumnData(
		    "GeneXpertResults",
		    "TestID",
		    "SputumTestID='"
			    + XpertSmsWebClient.get(testIdSearchTextBox)
			    + "' AND PatientID='"
			    + XpertSmsWebClient.get(pidSearchTextBox) + "'",
		    new AsyncCallback<String[]>() {
			@Override
			public void onSuccess(String[] result) {
			    sputumIdListBox.clear();
			    for (int i = 0; i < result.length; i++)
				sputumIdListBox.insertItem(result[i], i);
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

    public void fillData() {
	System.out.println("filldata");
	try {
	    service.findGeneXpertResultsByTestID(
		    XpertSmsWebClient.get(sputumIdListBox),
		    new AsyncCallback<GeneXpertResults>() {
			@Override
			public void onSuccess(GeneXpertResults result) {
			    System.out.println("success");
			    current = result;
			    sputumTestID.setText(String.valueOf(current
				    .getSputumTestId()));
			    patientIdTextBox.setText(current.getPatientId());
			    geneXpertResultComboBox.setSelectedIndex(XpertSmsWebClient
				    .getIndex(geneXpertResultComboBox,
					    current.getGeneXpertResult()));
			    mtbBurdenComboBox
				    .setSelectedIndex(XpertSmsWebClient
					    .getIndex(mtbBurdenComboBox,
						    current.getMtbBurden()));
			    drugResistanceComboBox.setSelectedIndex(XpertSmsWebClient
				    .getIndex(drugResistanceComboBox,
					    current.getDrugResistance()));
			    errorCodeIntegerBox.setValue(current.getErrorCode());
			    remarksTextBox.setText(current.getRemarks());

			    instrumentIdTextBox.setText(current
				    .getInstrumentSerial());
			    moduleIdTextBox.setText(current.getModuleId());
			    cartidgeIdTextBox.setText(current.getCartridgeId());
			    reagentLotIdTextBox.setText(current
				    .getReagentLotId());
			    pcIdTextBox.setText(current.getPcId());
			    operatorIdTextBox.setText(current.getOperatorId());

			    probeResultAListBox
				    .setSelectedIndex(XpertSmsWebClient
					    .getIndex(probeResultAListBox,
						    current.getProbeResultA()));
			    probeResultBListBox
				    .setSelectedIndex(XpertSmsWebClient
					    .getIndex(probeResultBListBox,
						    current.getProbeResultB()));
			    probeResultCListBox
				    .setSelectedIndex(XpertSmsWebClient
					    .getIndex(probeResultCListBox,
						    current.getProbeResultC()));
			    probeResultDListBox
				    .setSelectedIndex(XpertSmsWebClient
					    .getIndex(probeResultDListBox,
						    current.getProbeResultD()));
			    probeResultEListBox
				    .setSelectedIndex(XpertSmsWebClient
					    .getIndex(probeResultEListBox,
						    current.getProbeResultE()));
			    probeResultSPCListBox.setSelectedIndex(XpertSmsWebClient
				    .getIndex(probeResultSPCListBox,
					    current.getProbeResultSPC()));

			    probeCtADoubleBox.setValue(current.getProbeCtA());
			    probeCtBDoubleBox.setValue(current.getProbeCtB());
			    probeCtCDoubleBox.setValue(current.getProbeCtC());
			    probeCtDDoubleBox.setValue(current.getProbeCtD());
			    probeCtEDoubleBox.setValue(current.getProbeCtE());
			    probeCtSPCDoubleBox.setValue(current
				    .getProbeCtSPC());

			    probeEndptADoubleBox.setValue(current
				    .getProbeEndptA());
			    probeEndptBDoubleBox.setValue(current
				    .getProbeEndptB());
			    probeEndptCDoubleBox.setValue(current
				    .getProbeEndptC());
			    probeEndptDDoubleBox.setValue(current
				    .getProbeEndptD());
			    probeEndptEDoubleBox.setValue(current
				    .getProbeEndptE());
			    probeEndptSPCDoubleBox.setValue(current
				    .getProbeEndptSPC());
			    load(false);
			}

			@Override
			public void onFailure(Throwable caught) {
			    System.out.println("failure");
			    Window.alert(CustomMessage
				    .getErrorMessage(ErrorType.ITEM_NOT_FOUND));
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
	String result = XpertSmsWebClient.get(geneXpertResultComboBox);
	current.setLaboratoryId(XSMS.getCurrentUser());
	current.setDateTested(new Date());
	// current.setIrs(Integer.parseInt(XpertSmsWebClient.get(irsTextBox)));
	current.setGeneXpertResult(result);
	current.setIsPositive(result.equals("MTB DETECTED"));
	current.setDrugResistance("");
	current.setMtbBurden("");
	if (result.equals("MTB DETECTED")) {
	    current.setDrugResistance(XpertSmsWebClient
		    .get(drugResistanceComboBox));
	    current.setMtbBurden(XpertSmsWebClient.get(mtbBurdenComboBox));
	} else if (result.equals("ERROR")) {
	    current.setErrorCode(errorCodeIntegerBox.getValue());
	}
	current.setRemarks(XpertSmsWebClient.get(remarksTextBox).toUpperCase());

	// Materials
	current.setInstrumentSerial(XpertSmsWebClient.get(instrumentIdTextBox)
		.toUpperCase());
	current.setModuleId(XpertSmsWebClient.get(moduleIdTextBox)
		.toUpperCase());
	current.setReagentLotId(XpertSmsWebClient.get(reagentLotIdTextBox)
		.toUpperCase());
	current.setCartridgeId(XpertSmsWebClient.get(cartidgeIdTextBox)
		.toUpperCase());
	current.setPcId(XpertSmsWebClient.get(pcIdTextBox).toUpperCase());
	current.setOperatorId(XpertSmsWebClient.get(operatorIdTextBox)
		.toUpperCase());

	// Probes
	if (probeResultAListBox.getSelectedIndex() != -1)
	    current.setProbeResultA(XpertSmsWebClient.get(probeResultAListBox)
		    .toUpperCase());
	if (probeResultBListBox.getSelectedIndex() != -1)
	    current.setProbeResultB(XpertSmsWebClient.get(probeResultBListBox)
		    .toUpperCase());
	if (probeResultCListBox.getSelectedIndex() != -1)
	    current.setProbeResultC(XpertSmsWebClient.get(probeResultCListBox)
		    .toUpperCase());
	if (probeResultDListBox.getSelectedIndex() != -1)
	    current.setProbeResultD(XpertSmsWebClient.get(probeResultDListBox)
		    .toUpperCase());
	if (probeResultEListBox.getSelectedIndex() != -1)
	    current.setProbeResultE(XpertSmsWebClient.get(probeResultEListBox)
		    .toUpperCase());
	if (probeResultSPCListBox.getSelectedIndex() != -1)
	    current.setProbeResultSPC(XpertSmsWebClient.get(
		    probeResultSPCListBox).toUpperCase());

	if (XpertSmsWebClient.get(probeCtADoubleBox).length() != 0)
	    current.setProbeCtA(Double.parseDouble(XpertSmsWebClient
		    .get(probeCtADoubleBox)));
	if (XpertSmsWebClient.get(probeCtBDoubleBox).length() != 0)
	    current.setProbeCtB(Double.parseDouble(XpertSmsWebClient
		    .get(probeCtBDoubleBox)));
	if (XpertSmsWebClient.get(probeCtCDoubleBox).length() != 0)
	    current.setProbeCtC(Double.parseDouble(XpertSmsWebClient
		    .get(probeCtCDoubleBox)));
	if (XpertSmsWebClient.get(probeCtDDoubleBox).length() != 0)
	    current.setProbeCtD(Double.parseDouble(XpertSmsWebClient
		    .get(probeCtDDoubleBox)));
	if (XpertSmsWebClient.get(probeCtEDoubleBox).length() != 0)
	    current.setProbeCtE(Double.parseDouble(XpertSmsWebClient
		    .get(probeCtEDoubleBox)));
	if (XpertSmsWebClient.get(probeCtSPCDoubleBox).length() != 0)
	    current.setProbeCtSPC(Double.parseDouble(XpertSmsWebClient
		    .get(probeCtSPCDoubleBox)));

	if (XpertSmsWebClient.get(probeEndptADoubleBox).length() != 0)
	    current.setProbeEndptA(Double.parseDouble(XpertSmsWebClient
		    .get(probeEndptADoubleBox)));
	if (XpertSmsWebClient.get(probeEndptBDoubleBox).length() != 0)
	    current.setProbeEndptB(Double.parseDouble(XpertSmsWebClient
		    .get(probeEndptBDoubleBox)));
	if (XpertSmsWebClient.get(probeEndptCDoubleBox).length() != 0)
	    current.setProbeEndptC(Double.parseDouble(XpertSmsWebClient
		    .get(probeEndptCDoubleBox)));
	if (XpertSmsWebClient.get(probeEndptDDoubleBox).length() != 0)
	    current.setProbeEndptD(Double.parseDouble(XpertSmsWebClient
		    .get(probeEndptDDoubleBox)));
	if (XpertSmsWebClient.get(probeEndptEDoubleBox).length() != 0)
	    current.setProbeEndptE(Double.parseDouble(XpertSmsWebClient
		    .get(probeEndptEDoubleBox)));
	if (XpertSmsWebClient.get(probeEndptSPCDoubleBox).length() != 0)
	    current.setProbeEndptSPC(Double.parseDouble(XpertSmsWebClient
		    .get(probeEndptSPCDoubleBox)));
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
	if (errorCodeIntegerBox.isEnabled()
		&& XpertSmsWebClient.get(errorCodeIntegerBox).equals("")) {
	    errorMessage.append(CustomMessage
		    .getErrorMessage(ErrorType.EMPTY_DATA_ERROR) + "\n");
	    valid = false;
	}

	/* Validate data-type rules */
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
		service.exists(tableName, "ColumnName='" + "VALUE" + "'",
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
	    try {
		Boolean isTBPositive = (XpertSmsWebClient
			.get(geneXpertResultComboBox).equals("DETECTED"));
		if (isTBPositive) {
		    String passcode = Window
			    .prompt("This is a sensitive operation and requires authentication.\nPlease enter first 4 characters of your password to proceed.",
				    "");
		    if (!XpertSmsWebClient.verifyClientPasscode(passcode)) {
			Window.alert(CustomMessage
				.getErrorMessage(ErrorType.AUTHENTICATION_ERROR));
			load(false);
			return;
		    }
		}
		setCurrent();
		service.updateGeneXpertResults(current, isTBPositive,
			new AsyncCallback<Boolean>() {
			    @Override
			    public void onSuccess(Boolean result) {
				if (result) {
				    if (current.getIsPositive()) {
					try {
					    service.findPatient(
						    currentPatientID,
						    new AsyncCallback<Patient>() {
							@Override
							public void onSuccess(
								Patient result) {
							    result.setDiseaseConfirmed(new Boolean(
								    true));
							    try {
								service.updatePatient(
									result,
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
										load(false);
									    }
									});
							    } catch (Exception e) {
								e.printStackTrace();
							    }
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
				    Window.alert(CustomMessage
					    .getInfoMessage(InfoType.UPDATED));
				} else
				    Window.alert(CustomMessage
					    .getErrorMessage(ErrorType.UPDATE_ERROR));
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
						sputumIdListBox.setEnabled(rights
							.getAccess(AccessType.SELECT));
						searchButton.setEnabled(rights
							.getAccess(AccessType.SELECT));
						saveButton.setEnabled(rights
							.getAccess(AccessType.UPDATE));
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
	if (sender == sputumIdListBox) {
	    fillData();
	} else if (sender == searchButton) {
	    refreshList();
	    // fillData();
	} else if (sender == saveButton) {
	    updateData();
	} else if (sender == closeButton) {
	    MainMenuComposite.clear();
	}
    }

    @Override
    public void onChange(ChangeEvent event) {
	Widget sender = (Widget) event.getSource();
	load(true);
	if (sender == geneXpertResultComboBox) {
	    mtbBurdenComboBox.setEnabled(XpertSmsWebClient.get(
		    geneXpertResultComboBox).equals("MTB DETECTED"));
	    drugResistanceComboBox.setEnabled(XpertSmsWebClient.get(
		    geneXpertResultComboBox).equals("MTB DETECTED"));
	    errorCodeIntegerBox.setEnabled(XpertSmsWebClient.get(
		    geneXpertResultComboBox).equals("ERROR"));
	    load(false);
	}
    }
}
