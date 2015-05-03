package com.ihsinformatics.xpertsmsweb.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ihsinformatics.xpertsmsweb.shared.Parameter;
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

/**
 * The Async counterpart of <code>GreetingService</code>.
 * 
 * @author owais.hussain@irdresearch.org
 * 
 */
public interface ServerServiceAsync {
    /* Authentication methods */
    void authenticate(String userName, String password,
	    AsyncCallback<Boolean> callback) throws Exception;

    void authenticateUser(String userName, AsyncCallback<Boolean> callback)
	    throws Exception;

    void verifySecretAnswer(String userName, String secretAnswer,
	    AsyncCallback<Boolean> callback) throws Exception;

    void count(String tableName, String filter, AsyncCallback<Long> callback)
	    throws Exception;

    void exists(String tableName, String filer,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    /* Delete methods */
    void deleteContact(Contact contact, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void deleteEncounter(Encounter encounter,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void deleteEncounterResults(EncounterResults encounterResults,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void deleteEncounterResults(EncounterResultsId encounterResultsId,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void deleteEncounterWithResults(Encounter encounter,
	    AsyncCallback<Boolean> callback) throws Exception;

    void deleteGeneXpertResults(GeneXpertResults geneXpertResults,
	    AsyncCallback<Boolean> callback) throws Exception;

    void deleteLocation(Location location, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void deletePatient(Patient patient, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void deletePerson(Person person, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void deleteUser(Users user, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void deleteUserRights(UserRights userRights, AsyncCallback<Boolean> callback)
	    throws Exception;

    /* Find methods */
    void findContact(String personID, AsyncCallback<Contact> callback)
	    throws Exception;

    void findOtherMessageRecipient(AsyncCallback<OtherMessageSetting[]> callback);

    void findOtherMessageRecipientById(String id,
	    AsyncCallback<OtherMessageSetting> callback);

    void findEncounter(EncounterId encounterID,
	    AsyncCallback<Encounter> callback) throws Exception;

    void findEncounterResults(EncounterResultsId encounterResultsID,
	    AsyncCallback<EncounterResults[]> callback) throws Exception;

    void findEncounterResultsByElement(EncounterResultsId encounterResultsID,
	    AsyncCallback<EncounterResults> callback);

    void findGeneXpertResults(String sputumTestID,
	    AsyncCallback<GeneXpertResults> callback) throws Exception;

    void findGeneXpertResultsByTestID(String testID,
	    AsyncCallback<GeneXpertResults> callback) throws Exception;

    void findLocation(String locationID, AsyncCallback<Location> asyncCallback)
	    throws Exception;

    void findMessageSettings(AsyncCallback<MessageSettings> callback)
	    throws Exception;

    void findPatient(String patientID, AsyncCallback<Patient> callback)
	    throws Exception;

    void findPerson(String PID, AsyncCallback<Person> callback)
	    throws Exception;

    void findPersonsByName(String firstName, String lastName,
	    AsyncCallback<Person[]> callback) throws Exception;

    void findPersonsByNIC(String NIC, AsyncCallback<Person> callback)
	    throws Exception;

    void findUser(String currentUserName, AsyncCallback<Users> asyncCallback)
	    throws Exception;

    void findUserRights(String roleName, String menuName,
	    AsyncCallback<UserRights> callback) throws Exception;

    /* Save methods */
    void saveContact(Contact contact, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void saveEncounter(Encounter encounter, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void saveEncounterResults(EncounterResults encounterResults,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void saveEncounterWithResults(Encounter encounter,
	    ArrayList<String> encounterResults,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void saveGeneXpertResults(GeneXpertResults geneXpertResults,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void saveGeneXpertResultsUnlinked(
	    GeneXpertResultsUnlinked geneXpertResultsU,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void saveLocation(Location location, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void saveMessageSettings(MessageSettings messageSettings,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void saveNewPatient(Patient patient, Person person, Contact contact,
	    Encounter encounter, ArrayList<String> encounterResults,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void savePatient(Patient patient, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void savePerson(Person person, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void saveUser(Users user, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void saveUserRights(UserRights userRights, AsyncCallback<Boolean> callback)
	    throws Exception;

    /* Update methods */
    void updateContact(Contact contact, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void updateEncounter(Encounter encounter,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void updateEncounterResults(EncounterResults encounterResults,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void updateEncounterResults(EncounterResultsId id, String value,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void updateGeneXpertResults(GeneXpertResults geneXpertResults,
	    Boolean isTBPositive, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void updateLocation(Location location, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void updateMessageSettings(MessageSettings messageSettings,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void updatePassword(String userName, String newPassword,
	    AsyncCallback<Boolean> asyncCallback) throws Exception;

    void updatePatient(Patient patient, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void updatePerson(Person person, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void updateUserRights(UserRights userRights, AsyncCallback<Boolean> callback)
	    throws Exception;

    void updateUser(Users user, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    /* Other methods */
    void generateCSVfromQuery(String query, AsyncCallback<String> callback)
	    throws Exception;

    void generateReport(String fileName, Parameter[] params, boolean export,
	    AsyncCallback<String> callback) throws Exception;

    void generateReportFromQuery(String reportName, String query,
	    Boolean export, AsyncCallback<String> callback) throws Exception;

    void getColumnData(String tableName, String columnName, String filter,
	    AsyncCallback<String[]> asyncCallback) throws Exception;

    void getCurrentUser(AsyncCallback<String> callback) throws Exception;

    void getLists(AsyncCallback<String[][]> asyncCallback);

    void findLocationsByType(String locationType,
	    AsyncCallback<Location[]> asyncCallback) throws Exception;

    void getReportsList(AsyncCallback<String[][]> callback) throws Exception;

    void getRowRecord(String tableName, String[] columnNames, String filter,
	    AsyncCallback<String[]> asyncCallback) throws Exception;

    void getObject(String tableName, String columnName, String filter,
	    AsyncCallback<String> callback);

    void getSecretQuestion(String userName, AsyncCallback<String> callback)
	    throws Exception;

    void getSnapshotTime(AsyncCallback<String> callback) throws Exception;

    void getTableData(String tableName, String[] columnNames, String filter,
	    AsyncCallback<String[][]> asyncCallback) throws Exception;

    void getUserRgihts(String userName, String menuName,
	    AsyncCallback<Boolean[]> asyncCallback) throws Exception;

    void execute(String query, AsyncCallback<Integer> callback)
	    throws Exception;

    void execute(String[] queries, AsyncCallback<Boolean> callback)
	    throws Exception;

    void executeProcedure(String procedure, AsyncCallback<Boolean> asyncCallback)
	    throws Exception;

    void recordLogin(String userName, AsyncCallback<Void> callback)
	    throws Exception;

    void recordLogout(String userName, AsyncCallback<Void> callback)
	    throws Exception;

    void saveOtherMessageSetting(OtherMessageSetting setting,
	    AsyncCallback<Boolean> callback);

    void updateOtherMessageSetting(OtherMessageSetting setting,
	    AsyncCallback<Boolean> callback);

    void deleteOtherMessageSetting(OtherMessageSetting setting,
	    AsyncCallback<Boolean> callback);

    void findFacilitiesByDistrictId(String districtValue,
	    AsyncCallback<Location[]> asyncCallback);

    void findDistrict(String districtValue,
	    AsyncCallback<Location> asyncCallback);

    void findFacility(String facilityId, String districtId,
	    AsyncCallback<Location> asyncCallback);

}