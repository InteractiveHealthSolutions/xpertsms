/**
 * This class contains constants and project-specific methods which will be used throughout the System
 */

package com.ihsinformatics.xpertsmsweb.shared;

/*
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class XSMS {
    private static final boolean devMode = false;

    public static final XSMS xsms = new XSMS();
    private static String resourcesPath;
    private static String currentUser;
    private static String passCode;
    public static String hashingAlgorithm;
    public static String packageName;
    public static String projectTitle;
    public static char separatorChar;
    public static int sessionLimit;
    public static String[] formOptions = { "YES", "NO", "DONT KNOW", "REJECTED" };
    public static String[] locationTypes = { "DISTRICT", "HEALTH FACILITY",
	    "LABORATORY" };
    public static String[] menuNames = { "DATALOG", "ENCOUNTER", "LOCATION",
	    "PATIENT", "SETUP", "SMS", "USERS" };
    public static String[] userRoles = { "ADMIN", "GUEST", "SCREENER" };
    public static String[] userStatuses = { "ACTIVE", "SUSPENDED" };

    private XSMS() {
	if (System.getProperty("os.name", "windows").toLowerCase()
		.startsWith("windows"))
	{
	    if (devMode)
		resourcesPath = "c:\\Users\\Owais\\SkyDrive\\workspace\\xpertsms\\xpertsmsweb\\war" + System.getProperty("file.separator", "/");
	    else
		resourcesPath = "c:\\apache-tomcat-6.0\\webapps\\xpertsmsweb" + System.getProperty("file.separator", "/");
	}
	else
	    resourcesPath = "/var/lib/tomcat6/webapps/xpertsmsweb" + System.getProperty("file.separator", "/");
	currentUser = "";
	passCode = "";
	hashingAlgorithm = "SHA";
	packageName = "com.ihsinformatics.xpertsmsweb";
	projectTitle = "Xpert SMS";
	separatorChar = ',';
	sessionLimit = 900000;
    }

    /**
     * Concatenate an Array of Strings into single String
     * 
     * @param array
     * @return string
     */
    public static String concatenateArray(String[] array) {
	StringBuilder concatenated = new StringBuilder();
	for (String s : array) {
	    concatenated.append(s);
	    concatenated.append(XSMS.separatorChar);
	}
	concatenated.deleteCharAt(concatenated.length() - 1);
	return concatenated.toString();
    }

    /**
     * Get a list of values which will be constant throughout the application
     * 
     * @param listType
     * @return array
     */
    public static String[] getList(ListType listType) {
	switch (listType) {
	case LOCATION_TYPE:
	    return locationTypes;
	case MENU_NAME:
	    return menuNames;
	case FORM_OPTION:
	    return formOptions;
	case USER_ROLE:
	    return userRoles;
	case USER_STATUS:
	    return userStatuses;
	default:
	    break;
	}
	return new String[] {};
    }

    /**
     * Get secret question
     * 
     * @return array
     */
    public static String[] getSecretQuestions() {
	String[] questions = { "WHO IS YOUR FAVOURITE NATIONAL HERO?",
		"WHAT PHONE MODEL ARE YOU PLANNING TO PURCHASE NEXT?",
		"WHERE WAS YOUR MOTHER BORN?",
		"WHEN DID YOU BUY YOUR FIRST CAR?",
		"WHAT WAS YOUR CHILDHOOD NICKNAME?",
		"WHAT IS YOUR FAVOURITE CARTOON CHARACTER?" };
	return questions;
    }

    /**
     * Get current User Name (saved in cookies on client-side)
     * 
     * @return currentUser
     */
    public static String getCurrentUser() {
	return currentUser;
    }

    /**
     * Set current user
     * 
     * @param currentUser
     */
    public static void setCurrentUser(String currentUser) {
	XSMS.currentUser = currentUser.toUpperCase();
    }

    /**
     * Get pass code (first 4 characters of User's password)
     * 
     * @return passCode
     */
    public static String getPassCode() {
	return passCode;
    }

    /**
     * Set pass code for current user
     * 
     * @param passCode
     */
    public static void setPassCode(String passCode) {
	XSMS.passCode = passCode;
    }

    /**
     * @return the reportPath
     */
    public static String getReportPath() {
	return getResourcesPath() + "rpt" + System.getProperty("file.separator", "/");
    }

    /**
     * @return the staticFilePath
     */
    public static String getStaticFilePath() {
	return getResourcesPath() + "StaticData.xml";
    }

    /**
     * @return the resourcesPath
     */
    public static String getResourcesPath() {
	return resourcesPath;
    }
}
