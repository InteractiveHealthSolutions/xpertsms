package com.ihsinformatics.xpertsmsweb.client;

import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.ihsinformatics.xpertsmsweb.shared.AccessType;
import com.ihsinformatics.xpertsmsweb.shared.CustomMessage;
import com.ihsinformatics.xpertsmsweb.shared.ErrorType;
import com.ihsinformatics.xpertsmsweb.shared.InfoType;
import com.ihsinformatics.xpertsmsweb.shared.UserRightsUtil;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.model.Patient;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

public class PatientsComposite extends Composite implements IForm,
		ClickHandler, ChangeHandler {

	private static ServerServiceAsync service = GWT.create(ServerService.class);
	private static LoadingWidget loading = new LoadingWidget();
	private static final String menuName = "PATIENT";
	private UserRightsUtil rights = new UserRightsUtil();
	private boolean valid;
	private Patient currentPatient;

	private FlexTable flexTable = new FlexTable();
	private FlexTable middleFlexTable = new FlexTable();
	private Grid grid = new Grid(1, 3);
	private HorizontalPanel horizontalPanel = new HorizontalPanel();
	private HorizontalPanel nameHorizontalPanel = new HorizontalPanel();
	private HorizontalPanel genderHorizontalPanel = new HorizontalPanel();

	private Button searchButton = new Button("Search...");
	private ToggleButton createButton = new ToggleButton("Create");
	private Button saveButton = new Button("Save");
	private Button deleteButton = new Button("Delete");
	private Button closeButton = new Button("Close");

	private Label label = new Label("Patients");
	private Label lblPatientId = new Label("Patient ID:");
	private Label lblFullName = new Label("Name (First, Last, Surname):");
	private Label lblGender = new Label("Gender:");
	private Label lblDateOfBirth = new Label("Date of Birth:");
	private Label lblAddress1 = new Label("Address 1:");
	private Label lblAddress2 = new Label("Address 2:");
	private Label lblTown = new Label("Town:");
	private Label lblLandmark = new Label("Landmark:");
	private Label lblCountry = new Label("Country:");
	private Label lblCity = new Label("City:");
	private Label lblPhone = new Label("Phone:");
	private Label lblMobile = new Label("Mobile:");
	private Label lblEmailAddress = new Label("Email Address:");
	private Label lblRegistrationDate = new Label("Registration Date:");
	private Label lblPatientInformation = new Label("Patient Information");
	private Label lblMrNo = new Label("MR No.:");
	private Label lblWeightkg = new Label("Weight (kg):");
	private Label lblHeightcm = new Label("Height (cm):");
	private Label lblBloodGroup = new Label("Blood Group:");
	private Label lblTreatmentStatus = new Label("Treatment Status:");
	private Label lblDiseaseHistory = new Label("Disease History:");
	private Label lblFullDescription = new Label("Full Description:");

	private TextBox patientIdTextBox = new TextBox();
	private TextBox firstNameTextBox = new TextBox();
	private TextBox lastNameTextBox = new TextBox();
	private TextBox surnameTextBox = new TextBox();
	private TextBox address1TextBox = new TextBox();
	private TextBox address2TextBox = new TextBox();
	private TextBox townTextBox = new TextBox();
	private TextBox landmarkTextBox = new TextBox();
	private TextBox cityTextBox = new TextBox();
	private TextBox phoneTextBox = new TextBox();
	private TextBox mobileTextBox = new TextBox();
	private TextBox emailTextBox = new TextBox();
	private TextBox mrNoTextBox = new TextBox();
	private TextArea diseaseHistoryTextArea = new TextArea();
	private TextArea fullDescriptionTextArea = new TextArea();

	private DoubleBox weightDoubleBox = new DoubleBox();
	private DoubleBox heightDoubleBox = new DoubleBox();

	private DateBox dobDateBox = new DateBox();
	private DateBox registrationDateBox = new DateBox();

	private RadioButton maleRadioButton = new RadioButton("GENDER", "Male");
	private RadioButton femaleRadioButton = new RadioButton("GENDER", "Female");

	private ListBox countryComboBox = new ListBox();
	private ListBox bloodGroupComboBox = new ListBox();
	private ListBox patientStatusComboBox = new ListBox();

	@SuppressWarnings("deprecation")
	public PatientsComposite() {
		initWidget(flexTable);
		flexTable.setSize("602px", "100%");
		flexTable.setWidget(0, 0, label);
		label.setWordWrap(false);
		label.setStyleName("title");
		flexTable.setWidget(1, 0, middleFlexTable);
		middleFlexTable.setSize("631px", "100%");
		middleFlexTable.setWidget(0, 0, lblPatientId);
		middleFlexTable.setWidget(0, 1, horizontalPanel);
		patientIdTextBox.setMaxLength(14);
		horizontalPanel.add(patientIdTextBox);
		patientIdTextBox.setVisibleLength(12);
		horizontalPanel.add(searchButton);
		middleFlexTable.setWidget(1, 0, lblFullName);
		middleFlexTable.setWidget(1, 1, nameHorizontalPanel);
		firstNameTextBox.setVisibleLength(15);
		firstNameTextBox.setMaxLength(15);
		nameHorizontalPanel.add(firstNameTextBox);
		firstNameTextBox.setName("location;location_name");
		lastNameTextBox.setVisibleLength(15);
		lastNameTextBox.setMaxLength(15);
		nameHorizontalPanel.add(lastNameTextBox);
		surnameTextBox.setMaxLength(15);
		surnameTextBox.setVisibleLength(15);
		nameHorizontalPanel.add(surnameTextBox);
		middleFlexTable.setWidget(2, 0, lblGender);
		middleFlexTable.setWidget(2, 1, genderHorizontalPanel);
		maleRadioButton.setValue(true);
		genderHorizontalPanel.add(maleRadioButton);
		genderHorizontalPanel.add(femaleRadioButton);
		middleFlexTable.setWidget(3, 0, lblDateOfBirth);
		middleFlexTable.setWidget(3, 1, dobDateBox);
		middleFlexTable.setWidget(4, 0, lblAddress1);
		address1TextBox.setMaxLength(50);
		address1TextBox.setVisibleLength(50);
		middleFlexTable.setWidget(4, 1, address1TextBox);
		middleFlexTable.setWidget(5, 0, lblAddress2);
		address2TextBox.setMaxLength(50);
		address2TextBox.setVisibleLength(50);
		middleFlexTable.setWidget(5, 1, address2TextBox);
		middleFlexTable.setWidget(6, 0, lblTown);
		townTextBox.setMaxLength(50);
		townTextBox.setVisibleLength(50);
		middleFlexTable.setWidget(6, 1, townTextBox);
		middleFlexTable.setWidget(7, 0, lblLandmark);
		landmarkTextBox.setVisibleLength(50);
		landmarkTextBox.setMaxLength(50);
		middleFlexTable.setWidget(7, 1, landmarkTextBox);
		middleFlexTable.setWidget(8, 0, lblCountry);
		countryComboBox.addItem("PAKISTAN");
		countryComboBox.addItem("ARUBA");
		countryComboBox.addItem("AFGHANISTAN");
		countryComboBox.addItem("ANGOLA");
		countryComboBox.addItem("ANGUILLA");
		countryComboBox.addItem("ALBANIA");
		countryComboBox.addItem("ANDORRA");
		countryComboBox.addItem("NETHERLANDS ANTILLES");
		countryComboBox.addItem("UNITED ARAB EMIRATES");
		countryComboBox.addItem("ARGENTINA");
		countryComboBox.addItem("ARMENIA");
		countryComboBox.addItem("AMERICAN SAMOA");
		countryComboBox.addItem("ANTARCTICA");
		countryComboBox.addItem("FRENCH SOUTHERN TERRITORIES");
		countryComboBox.addItem("ANTIGUA AND BARBUDA");
		countryComboBox.addItem("AUSTRALIA");
		countryComboBox.addItem("AUSTRIA");
		countryComboBox.addItem("AZERBAIJAN");
		countryComboBox.addItem("BURUNDI");
		countryComboBox.addItem("BELGIUM");
		countryComboBox.addItem("BENIN");
		countryComboBox.addItem("BURKINA FASO");
		countryComboBox.addItem("BANGLADESH");
		countryComboBox.addItem("BULGARIA");
		countryComboBox.addItem("BAHRAIN");
		countryComboBox.addItem("BAHAMAS");
		countryComboBox.addItem("BOSNIA AND HERZEGOVINA");
		countryComboBox.addItem("BELARUS");
		countryComboBox.addItem("BELIZE");
		countryComboBox.addItem("BERMUDA");
		countryComboBox.addItem("BOLIVIA");
		countryComboBox.addItem("BRAZIL");
		countryComboBox.addItem("BARBADOS");
		countryComboBox.addItem("BRUNEI");
		countryComboBox.addItem("BHUTAN");
		countryComboBox.addItem("BOUVET ISLAND");
		countryComboBox.addItem("BOTSWANA");
		countryComboBox.addItem("CENTRAL AFRICAN REPUBLIC");
		countryComboBox.addItem("CANADA");
		countryComboBox.addItem("COCOS (KEELING) ISLANDS");
		countryComboBox.addItem("SWITZERLAND");
		countryComboBox.addItem("CHILE");
		countryComboBox.addItem("CHINA");
		countryComboBox.addItem("C\u00C3\u00B4TE D\u00C2\u2019IVOIRE");
		countryComboBox.addItem("CAMEROON");
		countryComboBox.addItem("CONGO, THE DEMOCRATIC REPUBLIC OF THE");
		countryComboBox.addItem("CONGO");
		countryComboBox.addItem("COOK ISLANDS");
		countryComboBox.addItem("COLOMBIA");
		countryComboBox.addItem("COMOROS");
		countryComboBox.addItem("CAPE VERDE");
		countryComboBox.addItem("COSTA RICA");
		countryComboBox.addItem("CUBA");
		countryComboBox.addItem("CHRISTMAS ISLAND");
		countryComboBox.addItem("CAYMAN ISLANDS");
		countryComboBox.addItem("CYPRUS");
		countryComboBox.addItem("CZECH REPUBLIC");
		countryComboBox.addItem("GERMANY");
		countryComboBox.addItem("DJIBOUTI");
		countryComboBox.addItem("DOMINICA");
		countryComboBox.addItem("DENMARK");
		countryComboBox.addItem("DOMINICAN REPUBLIC");
		countryComboBox.addItem("ALGERIA");
		countryComboBox.addItem("ECUADOR");
		countryComboBox.addItem("EGYPT");
		countryComboBox.addItem("ERITREA");
		countryComboBox.addItem("WESTERN SAHARA");
		countryComboBox.addItem("SPAIN");
		countryComboBox.addItem("ESTONIA");
		countryComboBox.addItem("ETHIOPIA");
		countryComboBox.addItem("FINLAND");
		countryComboBox.addItem("FIJI ISLANDS");
		countryComboBox.addItem("FALKLAND ISLANDS");
		countryComboBox.addItem("FRANCE");
		countryComboBox.addItem("FAROE ISLANDS");
		countryComboBox.addItem("MICRONESIA, FEDERATED STATES OF");
		countryComboBox.addItem("GABON");
		countryComboBox.addItem("UNITED KINGDOM");
		countryComboBox.addItem("GEORGIA");
		countryComboBox.addItem("GHANA");
		countryComboBox.addItem("GIBRALTAR");
		countryComboBox.addItem("GUINEA");
		countryComboBox.addItem("GUADELOUPE");
		countryComboBox.addItem("GAMBIA");
		countryComboBox.addItem("GUINEA-BISSAU");
		countryComboBox.addItem("EQUATORIAL GUINEA");
		countryComboBox.addItem("GREECE");
		countryComboBox.addItem("GRENADA");
		countryComboBox.addItem("GREENLAND");
		countryComboBox.addItem("GUATEMALA");
		countryComboBox.addItem("FRENCH GUIANA");
		countryComboBox.addItem("GUAM");
		countryComboBox.addItem("GUYANA");
		countryComboBox.addItem("HONG KONG");
		countryComboBox.addItem("HEARD ISLAND AND MCDONALD ISLANDS");
		countryComboBox.addItem("HONDURAS");
		countryComboBox.addItem("CROATIA");
		countryComboBox.addItem("HAITI");
		countryComboBox.addItem("HUNGARY");
		countryComboBox.addItem("INDONESIA");
		countryComboBox.addItem("INDIA");
		countryComboBox.addItem("BRITISH INDIAN OCEAN TERRITORY");
		countryComboBox.addItem("IRELAND");
		countryComboBox.addItem("IRAN");
		countryComboBox.addItem("IRAQ");
		countryComboBox.addItem("ICELAND");
		countryComboBox.addItem("ITALY");
		countryComboBox.addItem("JAMAICA");
		countryComboBox.addItem("JORDAN");
		countryComboBox.addItem("JAPAN");
		countryComboBox.addItem("KAZAKSTAN");
		countryComboBox.addItem("KENYA");
		countryComboBox.addItem("KYRGYZSTAN");
		countryComboBox.addItem("CAMBODIA");
		countryComboBox.addItem("KIRIBATI");
		countryComboBox.addItem("SAINT KITTS AND NEVIS");
		countryComboBox.addItem("SOUTH KOREA");
		countryComboBox.addItem("KUWAIT");
		countryComboBox.addItem("LAOS");
		countryComboBox.addItem("LEBANON");
		countryComboBox.addItem("LIBERIA");
		countryComboBox.addItem("LIBYAN ARAB JAMAHIRIYA");
		countryComboBox.addItem("SAINT LUCIA");
		countryComboBox.addItem("LIECHTENSTEIN");
		countryComboBox.addItem("SRI LANKA");
		countryComboBox.addItem("LESOTHO");
		countryComboBox.addItem("LITHUANIA");
		countryComboBox.addItem("LUXEMBOURG");
		countryComboBox.addItem("LATVIA");
		countryComboBox.addItem("MACAO");
		countryComboBox.addItem("MOROCCO");
		countryComboBox.addItem("MONACO");
		countryComboBox.addItem("MOLDOVA");
		countryComboBox.addItem("MADAGASCAR");
		countryComboBox.addItem("MALDIVES");
		countryComboBox.addItem("MEXICO");
		countryComboBox.addItem("MARSHALL ISLANDS");
		countryComboBox.addItem("MACEDONIA");
		countryComboBox.addItem("MALI");
		countryComboBox.addItem("MALTA");
		countryComboBox.addItem("MYANMAR");
		countryComboBox.addItem("MONGOLIA");
		countryComboBox.addItem("NORTHERN MARIANA ISLANDS");
		countryComboBox.addItem("MOZAMBIQUE");
		countryComboBox.addItem("MAURITANIA");
		countryComboBox.addItem("MONTSERRAT");
		countryComboBox.addItem("MARTINIQUE");
		countryComboBox.addItem("MAURITIUS");
		countryComboBox.addItem("MALAWI");
		countryComboBox.addItem("MALAYSIA");
		countryComboBox.addItem("MAYOTTE");
		countryComboBox.addItem("NAMIBIA");
		countryComboBox.addItem("NEW CALEDONIA");
		countryComboBox.addItem("NIGER");
		countryComboBox.addItem("NORFOLK ISLAND");
		countryComboBox.addItem("NIGERIA");
		countryComboBox.addItem("NICARAGUA");
		countryComboBox.addItem("NIUE");
		countryComboBox.addItem("NETHERLANDS");
		countryComboBox.addItem("NORWAY");
		countryComboBox.addItem("NEPAL");
		countryComboBox.addItem("NAURU");
		countryComboBox.addItem("NEW ZEALAND");
		countryComboBox.addItem("OMAN");
		countryComboBox.addItem("PANAMA");
		countryComboBox.addItem("PITCAIRN");
		countryComboBox.addItem("PERU");
		countryComboBox.addItem("PHILIPPINES");
		countryComboBox.addItem("PALAU");
		countryComboBox.addItem("PAPUA NEW GUINEA");
		countryComboBox.addItem("POLAND");
		countryComboBox.addItem("PUERTO RICO");
		countryComboBox.addItem("NORTH KOREA");
		countryComboBox.addItem("PORTUGAL");
		countryComboBox.addItem("PARAGUAY");
		countryComboBox.addItem("PALESTINE");
		countryComboBox.addItem("FRENCH POLYNESIA");
		countryComboBox.addItem("QATAR");
		countryComboBox.addItem("R\u00C3\u00A9UNION");
		countryComboBox.addItem("ROMANIA");
		countryComboBox.addItem("RUSSIAN FEDERATION");
		countryComboBox.addItem("RWANDA");
		countryComboBox.addItem("SAUDI ARABIA");
		countryComboBox.addItem("SUDAN");
		countryComboBox.addItem("SENEGAL");
		countryComboBox.addItem("SINGAPORE");
		countryComboBox.addItem("SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS");
		countryComboBox.addItem("SAINT HELENA");
		countryComboBox.addItem("SVALBARD AND JAN MAYEN");
		countryComboBox.addItem("SOLOMON ISLANDS");
		countryComboBox.addItem("SIERRA LEONE");
		countryComboBox.addItem("EL SALVADOR");
		countryComboBox.addItem("SAN MARINO");
		countryComboBox.addItem("SOMALIA");
		countryComboBox.addItem("SAINT PIERRE AND MIQUELON");
		countryComboBox.addItem("SAO TOME AND PRINCIPE");
		countryComboBox.addItem("SURINAME");
		countryComboBox.addItem("SLOVAKIA");
		countryComboBox.addItem("SLOVENIA");
		countryComboBox.addItem("SWEDEN");
		countryComboBox.addItem("SWAZILAND");
		countryComboBox.addItem("SEYCHELLES");
		countryComboBox.addItem("SYRIA");
		countryComboBox.addItem("TURKS AND CAICOS ISLANDS");
		countryComboBox.addItem("CHAD");
		countryComboBox.addItem("TOGO");
		countryComboBox.addItem("THAILAND");
		countryComboBox.addItem("TAJIKISTAN");
		countryComboBox.addItem("TOKELAU");
		countryComboBox.addItem("TURKMENISTAN");
		countryComboBox.addItem("EAST TIMOR");
		countryComboBox.addItem("TONGA");
		countryComboBox.addItem("TRINIDAD AND TOBAGO");
		countryComboBox.addItem("TUNISIA");
		countryComboBox.addItem("TURKEY");
		countryComboBox.addItem("TUVALU");
		countryComboBox.addItem("TAIWAN");
		countryComboBox.addItem("TANZANIA");
		countryComboBox.addItem("UGANDA");
		countryComboBox.addItem("UKRAINE");
		countryComboBox.addItem("UNITED STATES MINOR OUTLYING ISLANDS");
		countryComboBox.addItem("URUGUAY");
		countryComboBox.addItem("UNITED STATES");
		countryComboBox.addItem("UZBEKISTAN");
		countryComboBox.addItem("HOLY SEE (VATICAN CITY STATE)");
		countryComboBox.addItem("SAINT VINCENT AND THE GRENADINES");
		countryComboBox.addItem("VENEZUELA");
		countryComboBox.addItem("VIRGIN ISLANDS, BRITISH");
		countryComboBox.addItem("VIRGIN ISLANDS, U.S.");
		countryComboBox.addItem("VIETNAM");
		countryComboBox.addItem("VANUATU");
		countryComboBox.addItem("WALLIS AND FUTUNA");
		countryComboBox.addItem("SAMOA");
		countryComboBox.addItem("YEMEN");
		countryComboBox.addItem("YUGOSLAVIA");
		countryComboBox.addItem("SOUTH AFRICA");
		countryComboBox.addItem("ZAMBIA");
		countryComboBox.addItem("ZIMBABWE");
		countryComboBox.addItem("UNLISTED");
		countryComboBox.setName("COUNTRY");
		middleFlexTable.setWidget(8, 1, countryComboBox);
		middleFlexTable.setWidget(9, 0, lblCity);
		cityTextBox.setVisibleLength(50);
		cityTextBox.setMaxLength(50);
		middleFlexTable.setWidget(9, 1, cityTextBox);
		middleFlexTable.setWidget(10, 0, lblPhone);
		phoneTextBox.setMaxLength(20);
		phoneTextBox.setVisibleLength(15);
		middleFlexTable.setWidget(10, 1, phoneTextBox);
		middleFlexTable.setWidget(11, 0, lblMobile);
		mobileTextBox.setMaxLength(20);
		mobileTextBox.setVisibleLength(15);
		middleFlexTable.setWidget(11, 1, mobileTextBox);
		middleFlexTable.setWidget(12, 0, lblEmailAddress);
		emailTextBox.setMaxLength(255);
		emailTextBox.setVisibleLength(50);
		middleFlexTable.setWidget(12, 1, emailTextBox);
		middleFlexTable.setWidget(13, 1, lblPatientInformation);
		middleFlexTable.setWidget(14, 0, lblRegistrationDate);
		registrationDateBox.setFormat(new DefaultFormat(DateTimeFormat
				.getShortDateFormat()));
		middleFlexTable.setWidget(14, 1, registrationDateBox);
		middleFlexTable.setWidget(15, 0, lblMrNo);
		mrNoTextBox.setVisibleLength(12);
		mrNoTextBox.setMaxLength(12);
		middleFlexTable.setWidget(15, 1, mrNoTextBox);
		middleFlexTable.setWidget(16, 0, lblWeightkg);
		weightDoubleBox.setVisibleLength(5);
		middleFlexTable.setWidget(16, 1, weightDoubleBox);
		middleFlexTable.setWidget(17, 0, lblHeightcm);
		heightDoubleBox.setVisibleLength(5);
		middleFlexTable.setWidget(17, 1, heightDoubleBox);
		middleFlexTable.setWidget(18, 0, lblBloodGroup);
		bloodGroupComboBox.addItem("");
		bloodGroupComboBox.addItem("A+");
		bloodGroupComboBox.addItem("A-");
		bloodGroupComboBox.addItem("B+");
		bloodGroupComboBox.addItem("B-");
		bloodGroupComboBox.addItem("O+");
		bloodGroupComboBox.addItem("O-");
		bloodGroupComboBox.addItem("AB+");
		bloodGroupComboBox.addItem("AB-");
		middleFlexTable.setWidget(18, 1, bloodGroupComboBox);
		middleFlexTable.setWidget(19, 0, lblTreatmentStatus);
		patientStatusComboBox.addItem("NEW");
		patientStatusComboBox.addItem("CURED");
		patientStatusComboBox.addItem("COMPLETE");
		patientStatusComboBox.addItem("LOST TO FOLLOW-UP");
		patientStatusComboBox.addItem("TREATMENT FAILED");
		patientStatusComboBox.addItem("TRANSFERRED");
		patientStatusComboBox.addItem("DIED");
		middleFlexTable.setWidget(19, 1, patientStatusComboBox);
		middleFlexTable.setWidget(20, 0, lblDiseaseHistory);
		diseaseHistoryTextArea.setCharacterWidth(50);
		diseaseHistoryTextArea.setVisibleLines(3);
		middleFlexTable.setWidget(20, 1, diseaseHistoryTextArea);
		middleFlexTable.setWidget(21, 0, lblFullDescription);
		fullDescriptionTextArea.setVisibleLines(3);
		fullDescriptionTextArea.setCharacterWidth(50);
		middleFlexTable.setWidget(21, 1, fullDescriptionTextArea);
		createButton.setEnabled(false);
		middleFlexTable.setWidget(22, 0, createButton);
		middleFlexTable.getRowFormatter().setVerticalAlign(0,
				HasVerticalAlignment.ALIGN_MIDDLE);
		middleFlexTable.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		middleFlexTable.setWidget(22, 1, grid);
		grid.setSize("100%", "100%");
		saveButton.setEnabled(false);
		grid.setWidget(0, 0, saveButton);
		deleteButton.setEnabled(false);
		grid.setWidget(0, 1, deleteButton);
		grid.setWidget(0, 2, closeButton);
		flexTable.getRowFormatter().setVerticalAlign(1,
				HasVerticalAlignment.ALIGN_TOP);

		searchButton.addClickHandler(this);
		saveButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		closeButton.addClickHandler(this);
		createButton.addClickHandler(this);

		setRights(menuName);
		refreshList();
	}

	public void refreshList() {
		// Not implemented
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
		currentPatient.setPatientId(XpertSmsWebClient.get(patientIdTextBox));
		currentPatient.setFirstName(XpertSmsWebClient.get(firstNameTextBox));
		currentPatient.setLastName(XpertSmsWebClient.get(lastNameTextBox));
		currentPatient.setSurname(XpertSmsWebClient.get(surnameTextBox));
		currentPatient.setGender(maleRadioButton.getValue() ? "M" : "F");
		currentPatient.setDob(dobDateBox.getValue());
		currentPatient.setAddress1(XpertSmsWebClient.get(address1TextBox));
		currentPatient.setAddress2(XpertSmsWebClient.get(address2TextBox));
		currentPatient.setTown(XpertSmsWebClient.get(townTextBox));
		currentPatient.setLandmark(XpertSmsWebClient
				.get(landmarkTextBox));
		currentPatient.setCountry(XpertSmsWebClient.get(countryComboBox));
		currentPatient.setCity(XpertSmsWebClient.get(cityTextBox));
		currentPatient.setPhone(XpertSmsWebClient.get(phoneTextBox));
		currentPatient.setMobile(XpertSmsWebClient.get(mobileTextBox));
		currentPatient.setEmail(XpertSmsWebClient.get(emailTextBox));
		currentPatient.setDateRegistered(registrationDateBox.getValue());
		currentPatient.setMrNo(XpertSmsWebClient.get(mrNoTextBox));
		currentPatient.setWeight(weightDoubleBox.getValue().floatValue());
		currentPatient.setHeight(heightDoubleBox.getValue().floatValue());
		currentPatient.setBloodGroup(XpertSmsWebClient.get(bloodGroupComboBox));
		currentPatient.setPatientStatus(XpertSmsWebClient
				.get(patientStatusComboBox));
		currentPatient.setDiseaseHistory(XpertSmsWebClient
				.get(diseaseHistoryTextArea));
		currentPatient.setFullDescription(XpertSmsWebClient
				.get(fullDescriptionTextArea));
	}

	@Override
	public void fillData() {
		try {
			service.findPatient(XpertSmsWebClient.get(patientIdTextBox),
					new AsyncCallback<Patient>() {
						@Override
						public void onSuccess(Patient result) {
							if (result == null)
								Window.alert(CustomMessage
										.getErrorMessage(ErrorType.ITEM_NOT_FOUND));
							else {
								currentPatient = result;
								patientIdTextBox.setValue(currentPatient
										.getPatientId());
								firstNameTextBox.setValue(currentPatient
										.getFirstName());
								lastNameTextBox.setValue(currentPatient
										.getLastName());
								surnameTextBox.setValue(currentPatient
										.getSurname());
								maleRadioButton.setValue(currentPatient
										.getGender() == "M");
								dobDateBox.setValue(currentPatient.getDob());
								address1TextBox.setValue(currentPatient
										.getAddress1());
								address2TextBox.setValue(currentPatient
										.getAddress2());
								townTextBox.setValue(currentPatient.getTown());
								landmarkTextBox.setValue(currentPatient
										.getLandmark());
								countryComboBox
										.setSelectedIndex(XpertSmsWebClient
												.getIndex(countryComboBox,
														currentPatient
																.getCountry()));
								cityTextBox.setValue(currentPatient.getCity());
								phoneTextBox.setValue(currentPatient.getPhone());
								mobileTextBox.setValue(currentPatient
										.getMobile());
								emailTextBox.setValue(currentPatient.getEmail());
								phoneTextBox.setValue(currentPatient.getPhone());
								registrationDateBox.setValue(currentPatient
										.getDateRegistered());
								mrNoTextBox.setValue(currentPatient.getMrNo());
								weightDoubleBox.setValue(currentPatient
										.getWeight().doubleValue());
								heightDoubleBox.setValue(currentPatient
										.getHeight().doubleValue());
								bloodGroupComboBox.setSelectedIndex(XpertSmsWebClient
										.getIndex(bloodGroupComboBox,
												currentPatient.getBloodGroup()));
								patientStatusComboBox.setSelectedIndex(XpertSmsWebClient
										.getIndex(patientStatusComboBox,
												currentPatient
														.getPatientStatus()));
								diseaseHistoryTextArea.setValue(currentPatient
										.getDiseaseHistory());
								fullDescriptionTextArea.setValue(currentPatient
										.getFullDescription());
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
		if (XpertSmsWebClient.get(patientIdTextBox).equals("")
				|| XpertSmsWebClient.get(firstNameTextBox).equals("")) {
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
				String patientId = XpertSmsWebClient.get(patientIdTextBox);
				String firstName = XpertSmsWebClient.get(firstNameTextBox);
				String gender = maleRadioButton.getValue() ? "M" : "F";
				currentPatient = new Patient(patientId, firstName, gender);
				setCurrent();
				service.savePatient(currentPatient,
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
				service.updatePatient(currentPatient,
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
				service.deletePatient(currentPatient,
						new AsyncCallback<Boolean>() {
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
												searchButton.setEnabled(rights
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
		if (sender == searchButton) {
			fillData();
		} else if (sender == createButton) {
			if (createButton.isDown())
				clearUp();
			patientIdTextBox.setEnabled(createButton.isDown());
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
		// Not implemented
	}
}
