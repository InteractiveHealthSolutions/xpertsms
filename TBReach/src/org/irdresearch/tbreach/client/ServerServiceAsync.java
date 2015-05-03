package org.irdresearch.tbreach.client;

import java.util.ArrayList;

import org.irdresearch.tbreach.shared.Parameter;
import org.irdresearch.tbreach.shared.model.*;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Async counterpart of <code>GreetingService</code>.
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public interface ServerServiceAsync
{
	/* Authentication methods */
	void authenticate(String userName, String password, AsyncCallback<Boolean> callback) throws Exception;

	void authenticateUser(String userName, AsyncCallback<Boolean> callback) throws Exception;

	void verifySecretAnswer(String userName, String secretAnswer, AsyncCallback<Boolean> callback) throws Exception;

	void count(String tableName, String filter, AsyncCallback<Long> callback) throws Exception;

	void exists(String tableName, String filer, AsyncCallback<Boolean> asyncCallback) throws Exception;

	/* Delete methods */
	void deleteCity(SetupCity city, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteCountry(SetupCountry country, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteContact(Contact contact, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteEncounter(Encounter encounter, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteEncounterResults(EncounterResults encounterResults, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteEncounterResults(EncounterResultsId encounterResultsId, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteEncounterWithResults(Encounter encounter, AsyncCallback<Boolean> callback) throws Exception;

	void deleteGeneXpertResults(GeneXpertResults geneXpertResults, AsyncCallback<Boolean> callback) throws Exception;

	void deleteGP(Gp gp, Person person, Contact contact, Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteLocation(Location location, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deletePatient(Patient patient, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deletePerson(Person person, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteSputumResults(SputumResults sputumResults, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteUser(Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteUserRights(UserRights userRights, AsyncCallback<Boolean> callback) throws Exception;

	void deleteWorker(Worker worker, Person person, Contact contact, Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void deleteXrayResults(XrayResults xrayResults, AsyncCallback<Boolean> callback) throws Exception;

	/* Find methods */
	void findContact(String personID, AsyncCallback<Contact> callback) throws Exception;
	
	void findOtherMessageRecipient(AsyncCallback<OtherMessageSetting[]> callback);
	
    void findOtherMessageRecipientById (String id  , AsyncCallback<OtherMessageSetting> callback);

	void findEncounter(EncounterId encounterID, AsyncCallback<Encounter> callback) throws Exception;

	void findEncounterResults(EncounterResultsId encounterResultsID, AsyncCallback<EncounterResults[]> callback) throws Exception;

	void findEncounterResultsByElement(EncounterResultsId encounterResultsID, AsyncCallback<EncounterResults> callback);

	void findGeneXpertResults(String sputumTestID, AsyncCallback<GeneXpertResults> callback) throws Exception;
	
	void findGeneXpertResultsByTestID(String testID, AsyncCallback<GeneXpertResults> callback) throws Exception;

	void findGP(String GPID, AsyncCallback<Gp> callback) throws Exception;

	void findLocation(String locationID, AsyncCallback<Location> asyncCallback) throws Exception;

	void findMessageSettings(AsyncCallback<MessageSettings> callback) throws Exception;

	void findPatient(String patientID, AsyncCallback<Patient> callback) throws Exception;

	void findPerson(String PID, AsyncCallback<Person> callback) throws Exception;

	void findPersonsByName(String firstName, String lastName, AsyncCallback<Person[]> callback) throws Exception;

	void findPersonsByNIC(String NIC, AsyncCallback<Person> callback) throws Exception;

	void findSputumResults(String patientID, String sputumTestID, AsyncCallback<SputumResults> callback) throws Exception;

	void findSputumResultsByPatientID(String patientID, AsyncCallback<SputumResults[]> callback) throws Exception;

	void findSputumResultsBySputumTestID(String sputumTestID, AsyncCallback<SputumResults> callback) throws Exception;

	void findSupervisor(String supervisorID, AsyncCallback<Supervisor> callback) throws Exception;

	void findUser(String currentUserName, AsyncCallback<Users> asyncCallback) throws Exception;

	void findUserRights(String roleName, String menuName, AsyncCallback<UserRights> callback) throws Exception;

	void findWorker(String workerID, AsyncCallback<Worker> callback) throws Exception;

	void findXrayResults(String patientID, AsyncCallback<XrayResults> callback) throws Exception;

	/* Save methods */
	void saveCity(SetupCity city, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveCountry(SetupCountry country, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveContact(Contact contact, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveEncounter(Encounter encounter, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveEncounterResults(EncounterResults encounterResults, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveEncounterWithResults(Encounter encounter, ArrayList<String> encounterResults, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveGeneXpertResults(GeneXpertResults geneXpertResults, AsyncCallback<Boolean> asyncCallback) throws Exception;
	
	void saveGeneXpertResultsUnlinked(GeneXpertResultsUnlinked geneXpertResultsU, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveGP(Gp gp, Person person, Contact contact, Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveLocation(Location location, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveMessageSettings(MessageSettings messageSettings, AsyncCallback<Boolean> asyncCallback) throws Exception;
	
	void saveNewPatient(Patient patient, Person person, Contact contact, Encounter encounter, ArrayList<String> encounterResults, AsyncCallback<Boolean> asyncCallback)
			throws Exception;

	void savePatient(Patient patient, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void savePerson(Person person, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveSputumResults(SputumResults sputumResults, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveUser(Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveUserRights(UserRights userRights, AsyncCallback<Boolean> callback) throws Exception;

	void saveWorker(Worker worker, Person person, Contact contact, Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveXrayResults(XrayResults xrayResults, AsyncCallback<Boolean> callback) throws Exception;

	/* Update methods */
	void updateContact(Contact contact, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateEncounter(Encounter encounter, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateEncounterResults(EncounterResults encounterResults, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateEncounterResults(EncounterResultsId id, String value, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateGeneXpertResults(GeneXpertResults geneXpertResults, Boolean isTBPositive, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateGP(Gp gp, Person person, Contact contact, Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateLocation(Location location, AsyncCallback<Boolean> asyncCallback) throws Exception;
	
	void updateMessageSettings(MessageSettings messageSettings, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updatePassword(String userName, String newPassword, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updatePatient(Patient patient, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updatePerson(Person person, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateSetupCity(SetupCity city, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateSetupCountry(SetupCountry country, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateSputumResults(SputumResults sputumResults, Boolean isTBPositive, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateUserRights(UserRights userRights, AsyncCallback<Boolean> callback) throws Exception;

	void updateUser(Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateWorker(Worker worker, Person person, Contact contact, Users user, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void updateXrayResults(XrayResults xrayResults, AsyncCallback<Boolean> callback) throws Exception;

	/* Other methods */
	void generateCSVfromQuery(String query, AsyncCallback<String> callback) throws Exception;

	void generateReport(String fileName, Parameter[] params, boolean export, AsyncCallback<String> callback) throws Exception;

	void generateReportFromQuery(String reportName, String query, Boolean export, AsyncCallback<String> callback) throws Exception;

	void getColumnData(String tableName, String columnName, String filter, AsyncCallback<String[]> asyncCallback) throws Exception;

	void getCurrentUser(AsyncCallback<String> callback) throws Exception;

	void getLists(AsyncCallback<String[][]> asyncCallback) throws Exception;
	
	void findLocationsByType (String locationType, AsyncCallback<Location[]> asyncCallback) throws Exception;

	void getReportsList(AsyncCallback<String[][]> callback) throws Exception;

	void getRowRecord(String tableName, String[] columnNames, String filter, AsyncCallback<String[]> asyncCallback) throws Exception;

	void getObject(String tableName, String columnName, String filter, AsyncCallback<String> callback);

	void getSecretQuestion(String userName, AsyncCallback<String> callback) throws Exception;

	void getSnapshotTime(AsyncCallback<String> callback) throws Exception;

	void getTableData(String tableName, String[] columnNames, String filter, AsyncCallback<String[][]> asyncCallback) throws Exception;

	void getUserRgihts(String userName, String menuName, AsyncCallback<Boolean[]> asyncCallback) throws Exception;

	void execute(String query, AsyncCallback<Integer> callback) throws Exception;

	void execute(String[] queries, AsyncCallback<Boolean> callback) throws Exception;

	void executeProcedure(String procedure, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void recordLogin(String userName, AsyncCallback<Void> callback) throws Exception;

	void recordLogout(String userName, AsyncCallback<Void> callback) throws Exception;

	//void sendGenericSMSAlert(Sms sms, AsyncCallback<Void> callback) throws Exception;

	//void sendGenericSMSAlert(Sms[] sms, AsyncCallback<Void> callback) throws Exception;

	void saveSputumCollection(GeneXpertResults geneXpertResults, SputumResults sputumResults, Encounter encounter, ArrayList<String> encounterResults, AsyncCallback<Boolean> asyncCallback) throws Exception;

	void saveOtherMessageSetting(OtherMessageSetting setting, AsyncCallback<Boolean> callback);
	
	void updateOtherMessageSetting(OtherMessageSetting setting, AsyncCallback<Boolean> callback);

	void deleteOtherMessageSetting(OtherMessageSetting setting, AsyncCallback<Boolean> callback);

	void findFacilitiesByDistrictId(String districtValue, AsyncCallback<Location[]> asyncCallback);
	
	void findDistrict(String districtValue, AsyncCallback<Location> asyncCallback);
	
	void findFacility(String facilityId, String districtId, AsyncCallback<Location> asyncCallback);

}