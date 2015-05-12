package com.ihsinformatics.xpertsmsweb.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ihsinformatics.xpertsmsweb.client.ServerService;
import com.ihsinformatics.xpertsmsweb.server.util.DateTimeUtil;
import com.ihsinformatics.xpertsmsweb.server.util.HibernateUtil;
import com.ihsinformatics.xpertsmsweb.server.util.MDHashUtil;
import com.ihsinformatics.xpertsmsweb.server.util.ReportUtil;
import com.ihsinformatics.xpertsmsweb.shared.ListType;
import com.ihsinformatics.xpertsmsweb.shared.Parameter;
import com.ihsinformatics.xpertsmsweb.shared.XSMS;
import com.ihsinformatics.xpertsmsweb.shared.model.Contact;
import com.ihsinformatics.xpertsmsweb.shared.model.Encounter;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterId;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterResults;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterResultsId;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResultsUnlinked;
import com.ihsinformatics.xpertsmsweb.shared.model.Location;
import com.ihsinformatics.xpertsmsweb.shared.model.MessageSettings;
import com.ihsinformatics.xpertsmsweb.shared.model.OtherMessageSetting;
import com.ihsinformatics.xpertsmsweb.shared.model.Patient;
import com.ihsinformatics.xpertsmsweb.shared.model.Person;
import com.ihsinformatics.xpertsmsweb.shared.model.UserRights;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

/*import com.ihsinformatics.xpertsmsweb.mobileevent.DateTimeUtil;
 import com.ihsinformatics.xpertsmsweb.mobileevent.ModelUtil;
 import com.ihsinformatics.xpertsmsweb.mobileevent.XmlUtil;*/

/**
 * The server side implementation of the RPC service.
 * 
 * @author owais.hussain@irdresearch.org
 */
@SuppressWarnings("serial")
public class ServerServiceImpl extends RemoteServiceServlet implements
	ServerService {
    private String arrangeFilter(String filter) throws Exception {
	if (filter.trim().equalsIgnoreCase(""))
	    return "";
	return (filter.toUpperCase().contains("WHERE") ? "" : " where ")
		+ filter;
    }

    /**
     * Get full name (first name + middle name + last name + surname) of any
     * Person
     * 
     * @param Person
     *            ID as String
     * @return full name as String
     */
    public String getFullName(String PID) throws Exception {
	if (PID.equals(""))
	    return "";
	return HibernateUtil.util
		.selectObject(
			"select LTRIM(RTRIM(IFNULL(FirstName, '') + ' ' + IFNULL(MiddleName, '') + IFNULL(LastName, '') + IFNULL(Surname, ''))) from Person where PID='"
				+ PID + "'").toString().toUpperCase();
    }

    /**
     * Get Mobile phone number of any Person
     * 
     * @param Person
     *            ID as String
     * @return Mobile number as String
     */
    public String getMobileNumber(String PID) throws Exception {
	if (PID.equals(""))
	    return "";
	return HibernateUtil.util.selectObject(
		"select Mobile from Contact where PID='" + PID + "'")
		.toString();
    }

    /**
     * User authentication: Checks whether user exists, then match his password
     * 
     * @return Boolean
     */
    public Boolean authenticate(String userName, String password)
	    throws Exception {
	if (!UserAuthentication.userExsists(userName))
	    return false;
	else if (!UserAuthentication.validatePassword(userName, password))
	    return false;
	XSMS.setCurrentUser(userName.toUpperCase());
	return true;
    }

    /**
     * Checks if a user exists in the database
     * 
     * @return Boolean
     */

    public Boolean authenticateUser(String userName) throws Exception {
	if (!UserAuthentication.userExsists(userName))
	    return false;
	return true;
    }

    /**
     * Verifies secret answer against stored secret question
     * 
     * @return Boolean
     */

    public Boolean verifySecretAnswer(String userName, String secretAnswer)
	    throws Exception {
	if (!UserAuthentication.validateSecretAnswer(userName, secretAnswer))
	    return false;
	return true;
    }

    /**
     * Get number of records in a table, given appropriate filter
     * 
     * @return Long
     */

    public Long count(String tableName, String filter) throws Exception {
	Object obj = HibernateUtil.util.selectObject("select count(*) from "
		+ tableName + " " + arrangeFilter(filter));
	return Long.parseLong(obj.toString());
    }

    /**
     * Checks existence of data by counting number of records in a table, given
     * appropriate filter
     * 
     * @return Boolean
     */

    public Boolean exists(String tableName, String filter) throws Exception {
	long count = count(tableName, filter);
	return count > 0;
    }

    /**
     * Generates CSV file from query passed along with the filters
     * 
     * @param query
     * @return
     */

    public String generateCSVfromQuery(String query) throws Exception {
	return ReportUtil.generateCSVfromQuery(query, ',');
    }

    /**
     * Generate report on server side and return the path it was created to
     * 
     * @param Path
     *            of report as String Report parameters as Parameter[] Report to
     *            be exported in csv format as Boolean
     * @return String
     */

    public String generateReport(String fileName, Parameter[] params,
	    boolean export) throws Exception {
	return ReportUtil.generateReport(fileName, params, export);
    }

    /**
     * Generate report on server side based on the query saved in the Database
     * against the reportName and return the path it was created to
     * 
     * @param reportName
     * @param params
     * @param export
     * @return
     */

    public String generateReportFromQuery(String reportName, String query,  Parameter[] params,
	    Boolean export) throws Exception {
	return ReportUtil.generateReportFromQuery(reportName, query, params, export);
    }

    public String[] getColumnData(String tableName, String columnName,
	    String filter) throws Exception {
	Object[] data = HibernateUtil.util.selectObjects("select distinct "
		+ columnName + " from " + tableName + " "
		+ arrangeFilter(filter));
	String[] columnData = new String[data.length];
	for (int i = 0; i < data.length; i++)
	    columnData[i] = data[i].toString();
	return columnData;
    }

    public String getCurrentUser() throws Exception {
	return XSMS.getCurrentUser();
    }

    public String getObject(String tableName, String columnName, String filter)
	    throws Exception {
	return HibernateUtil.util.selectObject(
		"select " + columnName + " from " + tableName
			+ arrangeFilter(filter)).toString();
    }

    public String[][] getReportsList() throws Exception {
	return ReportUtil.getReportList();
    }

    public String[] getRowRecord(String tableName, String[] columnNames,
	    String filter) throws Exception {
	return getTableData(tableName, columnNames, filter)[0];
    }

    public String getSecretQuestion(String userName) throws Exception {
	Users user = (Users) HibernateUtil.util
		.findObject("from Users where UserName = '" + userName + "'");
	return user.getSecretQuestion();
    }

    public String getSnapshotTime() throws Exception {
	Date dt = new Date();
	return DateTimeUtil.getSQLDate(dt);
    }

    public String[][] getTableData(String tableName, String[] columnNames,
	    String filter) throws Exception {
	StringBuilder columnList = new StringBuilder();
	for (String s : columnNames) {
	    columnList.append(s);
	    columnList.append(",");
	}
	columnList.deleteCharAt(columnList.length() - 1);

	Object[][] data = HibernateUtil.util.selectData("select "
		+ columnList.toString() + " from " + tableName + " "
		+ arrangeFilter(filter));
	String[][] stringData = new String[data.length][columnNames.length];
	for (int i = 0; i < data.length; i++) {
	    for (int j = 0; j < columnNames.length; j++) {
		if (data[i][j] == null)
		    data[i][j] = "";
		String str = data[i][j].toString();
		stringData[i][j] = str;
	    }
	}
	return stringData;
    }

    public Boolean[] getUserRgihts(String userName, String menuName)
	    throws Exception {
	if (userName.equalsIgnoreCase("ADMIN")) {
	    Boolean[] rights = { true, true, true, true, true };
	    return rights;
	}
	String role = HibernateUtil.util.selectObject(
		"select Role from Users where UserName='" + userName + "'")
		.toString();
	if (role.equalsIgnoreCase("ADMIN")) {
	    Boolean[] rights = { true, true, true, true, true };
	    return rights;
	}
	UserRights userRights = (UserRights) HibernateUtil.util
		.findObject("from UserRights where Role='" + role
			+ "' and MenuName='" + menuName + "'");
	Boolean[] rights = { userRights.isSearchAccess(),
		userRights.isInsertAccess(), userRights.isUpdateAccess(),
		userRights.isDeleteAccess(), userRights.isPrintAccess() };
	return rights;
    }

    public void recordLogin(String userName) throws Exception {
	Users user = (Users) HibernateUtil.util
		.findObject("from Users where UserName = '" + userName + "'");
	HibernateUtil.util.recordLog(LogType.LOGIN, user);
    }

    public void recordLogout(String userName) throws Exception {
	Users user = (Users) HibernateUtil.util
		.findObject("from Users where UserName = '" + userName + "'");
	HibernateUtil.util.recordLog(LogType.LOGOUT, user);
    }

    public int execute(String query) throws Exception {
	return HibernateUtil.util.runCommand(query);
    }

    public Boolean execute(String[] queries) throws Exception {
	for (String s : queries) {
	    boolean result = execute(s) >= 0;
	    if (!result)
		return false;
	}
	return true;
    }

    public Boolean executeProcedure(String procedure) throws Exception {
	return HibernateUtil.util.runProcedure(procedure);
    }

    /* Delete methods */
    public Boolean deleteContact(Contact contact) throws Exception {
	return HibernateUtil.util.delete(contact);
    }

    public Boolean deleteEncounter(Encounter encounter) throws Exception {
	return HibernateUtil.util.delete(encounter);
    }

    public Boolean deleteEncounterResults(EncounterResults encounterResults)
	    throws Exception {
	return HibernateUtil.util.delete(encounterResults);
    }

    public Boolean deleteEncounterResults(EncounterResultsId encounterResultsId)
	    throws Exception {
	boolean result = false;
	EncounterResults er = (EncounterResults) HibernateUtil.util
		.findObject("from EncounterResults where EncounterId="
			+ encounterResultsId.getEncounterId() + " and PID1='"
			+ encounterResultsId.getPid1() + "' and PID2='"
			+ encounterResultsId.getPid2() + "' and Element='"
			+ encounterResultsId.getElement() + "'");
	result = deleteEncounterResults(er);
	return result;
    }

    public Boolean deleteEncounterWithResults(Encounter encounter)
	    throws Exception {
	boolean result = false;
	try {
	    String[] elements = getColumnData("EncounterResults", "Element",
		    "EncounterID=" + encounter.getId().getEncounterId()
			    + " AND PID1='" + encounter.getId().getPid1()
			    + "' AND PID2='" + encounter.getId().getPid2()
			    + "'");
	    // Delete encounter results
	    for (String s : elements) {
		EncounterResultsId id = new EncounterResultsId(encounter
			.getId().getEncounterId(), encounter.getId().getPid1(),
			encounter.getId().getPid2(), s);
		result = deleteEncounterResults(id);
	    }
	    // Delete encounter
	    result = deleteEncounter(encounter);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return result;
    }

    public Boolean deleteGeneXpertResults(GeneXpertResults geneXpertResults)
	    throws Exception {
	return HibernateUtil.util.delete(geneXpertResults);
    }

    public Boolean deleteLocation(Location location) throws Exception {
	return HibernateUtil.util.delete(location);
    }

    public Boolean deleteOtherMessageSetting(OtherMessageSetting setting) {
	return HibernateUtil.util.delete(setting);
    }

    public Boolean deletePatient(Patient patient) throws Exception {
	return HibernateUtil.util.delete(patient);
    }

    public Boolean deletePerson(Person person) throws Exception {
	return HibernateUtil.util.delete(person);
    }

    public Boolean deleteUser(Users user) throws Exception {
	return HibernateUtil.util.delete(user);
    }

    public Boolean deleteUserRights(UserRights userRights) throws Exception {
	return HibernateUtil.util.delete(userRights);
    }

    /* Find methods */

    public Contact findContact(String personID) throws Exception {
	return (Contact) HibernateUtil.util
		.findObject("from Contact where PID='" + personID + "'");
    }

    public Encounter findEncounter(EncounterId encounterID) throws Exception {
	return (Encounter) HibernateUtil.util
		.findObject("from Encounter where PID1='"
			+ encounterID.getPid1() + "' and PID2='"
			+ encounterID.getPid2() + "' and EncounterID='"
			+ encounterID.getEncounterId() + "'");
    }

    public EncounterResults[] findEncounterResults(
	    EncounterResultsId encounterResultsID) throws Exception {
	return (EncounterResults[]) HibernateUtil.util
		.findObjects("from EncounterResults where PID1='"
			+ encounterResultsID.getPid1() + "' and PID2='"
			+ encounterResultsID.getPid2() + "' and EncounterID='"
			+ encounterResultsID.getEncounterId() + "'");
    }

    public EncounterResults findEncounterResultsByElement(
	    EncounterResultsId encounterResultsID) throws Exception {
	return (EncounterResults) HibernateUtil.util
		.findObject("from EncounterResults where PID1='"
			+ encounterResultsID.getPid1() + "' and PID2='"
			+ encounterResultsID.getPid2() + "' and EncounterID='"
			+ encounterResultsID.getEncounterId()
			+ "' and Element='" + encounterResultsID.getElement()
			+ "'");
    }

    public GeneXpertResults findGeneXpertResults(String sputumTestID)
	    throws Exception {
	return (GeneXpertResults) HibernateUtil.util
		.findObject("from GeneXpertResults where SputumTestID='"
			+ sputumTestID + "'");
    }

    public GeneXpertResults[] findGeneXpertResults(String sputumTestID,
	    String patientID) throws Exception {
	Object[] objects = HibernateUtil.util
		.findObjects("from GeneXpertResults where SputumTestID='"
			+ sputumTestID + "' AND PatientID='" + patientID + "'");

	return Arrays.copyOf(objects, objects.length, GeneXpertResults[].class);
    }

    public GeneXpertResults findGeneXpertResultsByTestID(String testID)
	    throws Exception {
	return (GeneXpertResults) HibernateUtil.util
		.findObject("from GeneXpertResults where TestID='" + testID
			+ "'");
    }

    public Location findLocation(String locationID) throws Exception {
	return (Location) HibernateUtil.util
		.findObject("from Location where LocationID='" + locationID
			+ "'");
    }

    public Location findDistrict(String locationID) {
	return (Location) HibernateUtil.util
		.findObject("from Location where LocationID='" + locationID
			+ "' and locationType='DISTRICT'");
    }

    public Location findFacility(String facilityId, String districtId) {
	return (Location) HibernateUtil.util
		.findObject("from Location where locationType='HEALTH FACILITY' and CityID='"
			+ districtId + "' and LocationID='" + facilityId + "'");

    }

    public MessageSettings findMessageSettings() throws Exception {
	return (MessageSettings) HibernateUtil.util
		.findObject("from MessageSettings");
    }

    public Patient findPatient(String patientID) throws Exception {
	return (Patient) HibernateUtil.util
		.findObject("from Patient where PatientID='" + patientID + "'");
    }

    public Person findPerson(String PID) throws Exception {
	return (Person) HibernateUtil.util.findObject("from Person where PID='"
		+ PID + "'");
    }

    public Person[] findPersonsByName(String firstName, String lastName)
	    throws Exception {
	return (Person[]) HibernateUtil.util
		.findObjects("from Person where FirstName LIKE '" + firstName
			+ "%' and LastName LIKE '" + lastName + "%'");
    }

    public Person findPersonsByNIC(String NIC) throws Exception {
	return (Person) HibernateUtil.util.findObject("from Person where NIC='"
		+ NIC + "'");
    }

    public Users findUser(String userName) throws Exception {
	return (Users) HibernateUtil.util
		.findObject("from Users where UserName='" + userName + "'");
    }

    public UserRights findUserRights(String roleName, String menuName)
	    throws Exception {
	return (UserRights) HibernateUtil.util
		.findObject("from UserRights where Role='" + roleName
			+ "' and MenuName='" + menuName + "'");
    }

    /* Save methods */
    public Boolean saveContact(Contact contact) throws Exception {
	return HibernateUtil.util.save(contact);
    }

    public Boolean saveEncounter(Encounter encounter) throws Exception {
	// Get the max encounter ID and add 1
	EncounterId currentID = encounter.getId();
	Object[] max = HibernateUtil.util
		.selectObjects("select max(encounterID) from Encounter where pid1='"
			+ currentID.getPid1()
			+ "' and pid2='"
			+ currentID.getPid2() + "'");

	Integer maxInt = (Integer) max[0];
	if (maxInt == null) {
	    currentID.setEncounterId(1);
	} else {
	    currentID.setEncounterId((maxInt.intValue() + 1));
	}
	encounter.setId(currentID);
	return HibernateUtil.util.save(encounter);
    }

    public Boolean saveEncounterResults(EncounterResults encounterResults)
	    throws Exception {
	return HibernateUtil.util.save(encounterResults);
    }

    public Boolean saveEncounterWithResults(Encounter encounter,
	    ArrayList<String> encounterResults) throws Exception {
	boolean result = false;
	// Save an encounter
	try {
	    Long encounterID = HibernateUtil.util
		    .count("select IFNULL(max(EncounterID), 0) + 1 from Encounter where PID1='"
			    + encounter.getId().getPid1()
			    + "' and PID2='"
			    + encounter.getId().getPid2()
			    + "' and EncounterType='"
			    + encounter.getEncounterType() + "'");
	    result = saveEncounter(encounter);

	    for (String s : encounterResults) {
		String[] split = s.split("=");
		EncounterResults encounterResult = new EncounterResults();
		encounterResult.setId(new EncounterResultsId(encounterID
			.intValue(), encounter.getId().getPid1(), encounter
			.getId().getPid2(), split[0]));
		if (split.length == 2)
		    encounterResult.setValue((split[1]));
		else
		    encounterResult.setValue("");
		result = saveEncounterResults(encounterResult);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return result;
    }

    public Boolean saveGeneXpertResults(GeneXpertResults geneXpertResults)
	    throws Exception {
	Boolean value = HibernateUtil.util.save(geneXpertResults);
	SMSUtil.util.sendAlertsOnAutoGXPResults(geneXpertResults);
	return value;
    }

    public Boolean saveGeneXpertResultsUnlinked(
	    GeneXpertResultsUnlinked geneXpertResultsU) throws Exception {
	return HibernateUtil.util.save(geneXpertResultsU);
    }

    public Boolean saveLocation(Location location) throws Exception {
	return HibernateUtil.util.save(location);
    }

    public Boolean saveOtherMessageSetting(OtherMessageSetting setting) {
	return HibernateUtil.util.save(setting);
    }

    public Boolean saveMessageSettings(MessageSettings messageSettings)
	    throws Exception {
	return HibernateUtil.util.save(messageSettings);
    }

    public Boolean saveNewPatient(Patient patient, Person person,
	    Contact contact, Encounter encounter,
	    ArrayList<String> encounterResults) throws Exception {
	boolean result = false;
	// Save an encounter
	try {
	    result = saveEncounterWithResults(encounter, encounterResults);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	result = HibernateUtil.util.save(patient);
	result = HibernateUtil.util.save(person);
	result = HibernateUtil.util.save(contact);
	if (!result) // In case of failure of any save, delete all 3
	{
	    HibernateUtil.util.delete(patient);
	    HibernateUtil.util.delete(person);
	    HibernateUtil.util.delete(contact);
	    return false;
	}
	return result;
    }

    public Boolean savePatient(Patient patient) throws Exception {
	return HibernateUtil.util.save(patient);
    }

    public Boolean savePerson(Person person) throws Exception {
	return HibernateUtil.util.save(person);
    }

    public Boolean saveUser(Users user) throws Exception {
	user.setPassword(MDHashUtil.getHashString(user.getPassword()));
	user.setSecretAnswer(MDHashUtil.getHashString(user.getSecretAnswer()));
	return HibernateUtil.util.save(user);
    }

    public Boolean saveUserRights(UserRights userRights) throws Exception {
	return HibernateUtil.util.save(userRights);
    }

    /* Update methods */
    public Boolean updateContact(Contact contact) throws Exception {
	return HibernateUtil.util.update(contact);
    }

    public Boolean updateEncounter(Encounter encounter) throws Exception {
	return HibernateUtil.util.update(encounter);
    }

    public boolean updateOtherMessageSetting(OtherMessageSetting setting) {
	return HibernateUtil.util.update(setting);
    }

    public Boolean updateEncounterResults(EncounterResults encounterResults)
	    throws Exception {
	return HibernateUtil.util.update(encounterResults);
    }

    public Boolean updateEncounterResults(
	    EncounterResultsId encounterResultsId, String newValue)
	    throws Exception {
	EncounterResults encounterResults = (EncounterResults) HibernateUtil.util
		.findObject("from EncounterResults where EncounterId="
			+ encounterResultsId.getEncounterId() + " and PID1='"
			+ encounterResultsId.getPid1() + "' and PID2='"
			+ encounterResultsId.getPid2() + "' and Element='"
			+ encounterResultsId.getElement() + "'");
	encounterResults.setValue(newValue);
	return HibernateUtil.util.update(encounterResults);
    }

    public Boolean updateGeneXpertResults(GeneXpertResults geneXpertResults,
	    Boolean isTBPositive) throws Exception {
	Boolean result = HibernateUtil.util.update(geneXpertResults);
	SMSUtil.util.sendAlertsOnAutoGXPResults(geneXpertResults);
	return result;
    }

    public Boolean updateLocation(Location location) throws Exception {
	return HibernateUtil.util.update(location);
    }

    public Boolean updateMessageSettings(MessageSettings messageSettings)
	    throws Exception {
	return HibernateUtil.util.update(messageSettings);
    }

    public Boolean updatePassword(String userName, String newPassword)
	    throws Exception {
	Users user = (Users) HibernateUtil.util
		.findObject("from Users where UserName = '" + userName + "'");
	user.setPassword(MDHashUtil.getHashString(newPassword));
	return HibernateUtil.util.update(user);
    }

    public Boolean updatePatient(Patient patient) throws Exception {
	return HibernateUtil.util.update(patient);
    }

    public Boolean updatePerson(Person person) throws Exception {
	return HibernateUtil.util.update(person);
    }

    public Boolean updateUser(Users user) throws Exception {
	user.setPassword(MDHashUtil.getHashString(user.getPassword()));
	user.setSecretAnswer(MDHashUtil.getHashString(user.getSecretAnswer()));
	return HibernateUtil.util.update(user);
    }

    public Boolean updateUserRights(UserRights userRights) throws Exception {
	return HibernateUtil.util.update(userRights);
    }

    public Boolean updateGeneXpertResultsAuto(
	    GeneXpertResults geneXpertResults, Boolean isTBPositive,
	    String operatorId, String pcId, String instrumentSerial,
	    String moduleId, String cartridgeId, String reagentLotId)
	    throws Exception {
	Boolean result = HibernateUtil.util.update(geneXpertResults);

	SMSUtil.util.sendAlertsOnAutoGXPResults(geneXpertResults);
	return result;
    }

    public Location[] findLocationsByType(String locationType) {
	Object[] list = HibernateUtil.util
		.findObjects("from Location where locationType='"
			+ locationType + "'");
	Location[] locations = new Location[list.length];
	for (int i = 0; i < list.length; i++)
	    locations[i] = (Location) list[i];
	return locations;
    }

    public Location[] findFacilitiesByDistrictId(String districtId) {
	Object[] list = HibernateUtil.util
		.findObjects("from Location where locationType='HEALTH FACILITY' and CityID='"
			+ districtId + "'");
	Location[] locations = new Location[list.length];
	for (int i = 0; i < list.length; i++)
	    locations[i] = (Location) list[i];
	return locations;
    }

    public OtherMessageSetting[] findOtherMessageRecipient() {
	Object[] list = HibernateUtil.util
		.findObjects("from OtherMessageSetting");
	OtherMessageSetting[] locations = new OtherMessageSetting[list.length];
	for (int i = 0; i < list.length; i++)
	    locations[i] = (OtherMessageSetting) list[i];
	return locations;
    }

    public OtherMessageSetting findOtherMessageRecipientById(String id) {
	return (OtherMessageSetting) HibernateUtil.util
		.findObject("from OtherMessageSetting where id = '" + id + "'");
    }

    @Override
    public String[][] getLists() throws Exception {
	String[][] lists = null;
	ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
	try {
	    DocumentBuilderFactory buildFactory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder documentBuilder = buildFactory.newDocumentBuilder();
	    File file = new File(XSMS.getStaticFilePath());
	    Document doc = documentBuilder.parse(file);
	    Element docElement = doc.getDocumentElement();
	    for (ListType type : ListType.values()) {
		ArrayList<String> array = new ArrayList<String>();
		NodeList list = docElement
			.getElementsByTagName(type.toString());
		if (list != null) {
		    for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			NodeList children;
			if (node.getNodeType() == Node.ELEMENT_NODE) {
			    children = node.getChildNodes();
			    if (children.getLength() > 0) {
				for (int j = 0; j < children.getLength(); j++) {
				    NodeList items = children.item(j)
					    .getChildNodes();
				    for (int k = 0; k < items.getLength(); k++) {
					String str = items.item(k)
						.getTextContent();
					array.add(str);
				    }
				}
			    }
			}
		    }
		}
		arrayList.add(array);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	lists = new String[arrayList.size()][];
	for (int i = 0; i < arrayList.size(); i++) {
	    String[] str = new String[0];
	    lists[i] = arrayList.get(i).toArray(str);
	}
	return lists;
    }
}