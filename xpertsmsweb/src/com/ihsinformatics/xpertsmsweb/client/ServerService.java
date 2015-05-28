package com.ihsinformatics.xpertsmsweb.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ihsinformatics.xpertsmsweb.shared.Parameter;
import com.ihsinformatics.xpertsmsweb.shared.model.Contact;
import com.ihsinformatics.xpertsmsweb.shared.model.Encounter;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterId;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterResults;
import com.ihsinformatics.xpertsmsweb.shared.model.EncounterResultsId;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;
import com.ihsinformatics.xpertsmsweb.shared.model.Location;
import com.ihsinformatics.xpertsmsweb.shared.model.MessageSettings;
import com.ihsinformatics.xpertsmsweb.shared.model.OtherMessageSetting;
import com.ihsinformatics.xpertsmsweb.shared.model.Patient;
import com.ihsinformatics.xpertsmsweb.shared.model.Person;
import com.ihsinformatics.xpertsmsweb.shared.model.UserRights;
import com.ihsinformatics.xpertsmsweb.shared.model.Users;

/**
 * The client side stub for the RPC service.
 * 
 * @author owais.hussain@irdresearch.org
 */
@RemoteServiceRelativePath("greet")
public interface ServerService extends RemoteService {
	/* Authentication methods */
	Boolean authenticate(String userName, String password) throws Exception;

	Boolean authenticateUser(String userName) throws Exception;

	Long count(String tableName, String filter) throws Exception;

	Boolean verifySecretAnswer(String userName, String secretAnswer)
			throws Exception;

	/* Delete methods */
	Boolean deleteContact(Contact contact) throws Exception;

	Boolean deleteEncounter(Encounter encounter) throws Exception;

	Boolean deleteEncounterResults(EncounterResults encounterResults)
			throws Exception;

	Boolean deleteEncounterResults(EncounterResultsId encounterResultsId)
			throws Exception;

	Boolean deleteEncounterWithResults(Encounter encounter) throws Exception;

	Boolean deleteGeneXpertResults(GeneXpertResults geneXpertResults)
			throws Exception;

	Boolean deleteLocation(Location location) throws Exception;

	Boolean deletePatient(Patient patient) throws Exception;

	Boolean deletePerson(Person person) throws Exception;

	Boolean deleteUser(Users user) throws Exception;

	Boolean deleteUserRights(UserRights userRights) throws Exception;

	/* Find methods */
	Contact findContact(String personID) throws Exception;

	Encounter findEncounter(EncounterId encounterID) throws Exception;

	EncounterResults[] findEncounterResults(
			EncounterResultsId encounterResultsID) throws Exception;

	EncounterResults findEncounterResultsByElement(
			EncounterResultsId encounterResultsID) throws Exception;

	GeneXpertResults findGeneXpertResults(String sputumTestID) throws Exception;

	GeneXpertResults findGeneXpertResultsByTestID(String testID)
			throws Exception;

	Location findLocation(String locationID) throws Exception;

	MessageSettings findMessageSettings() throws Exception;

	Patient findPatient(String patientID) throws Exception;

	Person findPerson(String PID) throws Exception;

	Person[] findPersonsByName(String firstName, String lastName)
			throws Exception;

	Person findPersonsByNIC(String NIC) throws Exception;

	Location[] findLocationsByType(String locationType) throws Exception;

	Users findUser(String currentUserName) throws Exception;

	UserRights findUserRights(String roleName, String menuName)
			throws Exception;

	/* Save methods */
	Boolean saveContact(Contact contact) throws Exception;

	Boolean saveEncounter(Encounter encounter) throws Exception;

	Boolean saveEncounterResults(EncounterResults encounterResults)
			throws Exception;

	Boolean saveEncounterWithResults(Encounter encounter,
			ArrayList<String> encounterResults) throws Exception;

	Boolean saveGeneXpertResults(GeneXpertResults geneXpertResults)
			throws Exception;

	Boolean saveLocation(Location location) throws Exception;

	Boolean saveMessageSettings(MessageSettings messageSettings)
			throws Exception;

	Boolean saveNewPatient(Patient patient, Person person, Contact contact,
			Encounter encounter, ArrayList<String> encounterResults)
			throws Exception;

	Boolean savePatient(Patient patient) throws Exception;

	Boolean savePerson(Person person) throws Exception;

	Boolean saveUser(Users user) throws Exception;

	Boolean saveUserRights(UserRights userRights) throws Exception;

	/* Update methods */
	Boolean updateContact(Contact contact) throws Exception;

	Boolean updateEncounter(Encounter encounter) throws Exception;

	Boolean updateEncounterResults(EncounterResults encounterResults)
			throws Exception;

	Boolean updateEncounterResults(EncounterResultsId id, String value)
			throws Exception;

	Boolean updateGeneXpertResults(GeneXpertResults geneXpertResults,
			Boolean isTBPositive) throws Exception;

	Boolean updateLocation(Location location) throws Exception;

	Boolean updateMessageSettings(MessageSettings messageSettings)
			throws Exception;

	Boolean updatePassword(String userName, String newPassword)
			throws Exception;

	Boolean updatePatient(Patient patient) throws Exception;

	Boolean updatePerson(Person person) throws Exception;

	Boolean updateUser(Users user) throws Exception;

	Boolean updateUserRights(UserRights userRights) throws Exception;

	/* Other methods */
	String generateCSVfromQuery(String query) throws Exception;

	String generateReport(String fileName, Parameter[] params, boolean export)
			throws Exception;

	String generateReportFromQuery(String reportName, String query,
			Parameter[] params, Boolean export) throws Exception;

	String[] getColumnData(String tableName, String columnName, String filter)
			throws Exception;

	String getCurrentUser() throws Exception;

	String[][] getLists() throws Exception;

	String[][] getReportsList() throws Exception;

	String[] getRowRecord(String tableName, String[] columnNames, String filter)
			throws Exception;

	String getObject(String tableName, String columnName, String filter)
			throws Exception;

	String getSecretQuestion(String userName) throws Exception;

	String getSnapshotTime() throws Exception;

	String[][] getTableData(String tableName, String[] columnNames,
			String filter) throws Exception;

	Boolean[] getUserRgihts(String userName, String menuName) throws Exception;

	Boolean exists(String tableName, String filter) throws Exception;

	int execute(String query) throws Exception;

	Boolean execute(String[] queries) throws Exception;

	Boolean executeProcedure(String procedure) throws Exception;

	void recordLogin(String userName) throws Exception;

	void recordLogout(String userName) throws Exception;

	OtherMessageSetting[] findOtherMessageRecipient();

	OtherMessageSetting findOtherMessageRecipientById(String id);

	Boolean saveOtherMessageSetting(OtherMessageSetting setting);

	boolean updateOtherMessageSetting(OtherMessageSetting setting);

	Boolean deleteOtherMessageSetting(OtherMessageSetting setting);

	Location[] findFacilitiesByDistrictId(String districtValue);

	Location findDistrict(String districtValue);

	Location findFacility(String facilityId, String districtId);
}
