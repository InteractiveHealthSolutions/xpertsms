/**
 * This class contains constants and project-specific methods which will be used throughout the System
 */

package com.ihsinformatics.xpertsmsweb.shared;

/*
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class XSMS {
    public static final XSMS xsms = new XSMS();
    private static String resourcesPath;
    private static String currentUser;
    private static String passCode;
    public static String hashingAlgorithm;
    public static String packageName;
    public static String projectTitle;
    public static char separatorChar;
    public static int sessionLimit;
    public static String[][] lists;

    private XSMS() {
	if (System.getProperty("os.name", "unix").toLowerCase()
		.startsWith("windows"))
	    resourcesPath = System.getProperty("user.dir",
		    "c:\\workspace\\xpertsms\\xpertsmsweb")
		    + System.getProperty("file.separator", "/")
		    + "war"
		    + System.getProperty("file.separator", "/");
	else
	    resourcesPath = "/var/lib/tomcat6/webapps/xpertsmsweb"
		    + System.getProperty("file.separator", "/");
	currentUser = "";
	passCode = "";
	hashingAlgorithm = "SHA";
	packageName = "com.ihsinformatics.xpertsmsweb";
	projectTitle = "Xpert SMS";
	separatorChar = ',';
	sessionLimit = 900000;
    }

    public static void fillLists(String[][] lists) {
	XSMS.lists = lists;
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
	concatenated.deleteCharAt(concatenated.length() - 1); // Remove
							      // additional
							      // separator
	return concatenated.toString();
    }

    /**
     * Get a list of values which will be constant throughout the application
     * 
     * @param listType
     * @return array
     */
    public static String[] getList(ListType listType) {
	return lists[listType.ordinal()];
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
	return getResourcesPath() + "rpt";
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
	return resourcesPath + System.getProperty("file.separator", "/");
    }
}