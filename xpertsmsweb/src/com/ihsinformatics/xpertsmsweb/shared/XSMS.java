/**
 * This class contains constants and project-specific methods which will be used throughout the System
 */

package com.ihsinformatics.xpertsmsweb.shared;

/*
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public final class XSMS {
	public static final String hashingAlgorithm = "SHA";
	public static final String packageName = "com.ihsinformatics.xpertsmsweb";
	public static final String projectTitle = "Xpert SMS";
	public static final char separatorChar = ',';
	public static final int sessionLimit = 900000;
	public static final XSMS xsms = new XSMS();
	private static VersionUtil version;
	private static String currentUser;
	private static String passCode;
	public static final String[] COUNTRIES_LIST = { "PAKISTAN", "ARUBA",
			"AFGHANISTAN", "ANGOLA", "ANGUILLA", "ALBANIA", "ANDORRA",
			"NETHERLANDS ANTILLES", "UNITED ARAB EMIRATES", "ARGENTINA",
			"ARMENIA", "AMERICAN SAMOA", "ANTARCTICA",
			"FRENCH SOUTHERN TERRITORIES", "ANTIGUA AND BARBUDA", "AUSTRALIA",
			"AUSTRIA", "AZERBAIJAN", "BURUNDI", "BELGIUM", "BENIN",
			"BURKINA FASO", "BANGLADESH", "BULGARIA", "BAHRAIN", "BAHAMAS",
			"BOSNIA AND HERZEGOVINA", "BELARUS", "BELIZE", "BERMUDA",
			"BOLIVIA", "BRAZIL", "BARBADOS", "BRUNEI", "BHUTAN",
			"BOUVET ISLAND", "BOTSWANA", "CENTRAL AFRICAN REPUBLIC", "CANADA",
			"COCOS KEELING ISLANDS", "SWITZERLAND", "CHILE", "CHINA",
			"CAMEROON", "CONGO", "COOK ISLANDS", "COLOMBIA", "COMOROS",
			"CAPE VERDE", "COSTA RICA", "CUBA", "CHRISTMAS ISLAND",
			"CAYMAN ISLANDS", "CYPRUS", "CZECH REPUBLIC",
			"DEMOCRATIC REPUBLIC OF CONGO", "GERMANY", "DJIBOUTI", "DOMINICA",
			"DENMARK", "DOMINICAN REPUBLIC", "ALGERIA", "ECUADOR", "EGYPT",
			"ERITREA", "WESTERN SAHARA", "SPAIN", "ESTONIA", "ETHIOPIA",
			"FINLAND", "FIJI ISLANDS", "FALKLAND ISLANDS", "FRANCE",
			"FAROE ISLANDS", "MICRONESIA, FEDERATED STATES OF", "GABON",
			"UNITED KINGDOM", "GEORGIA", "GHANA", "GIBRALTAR", "GUINEA",
			"GUADELOUPE", "GAMBIA", "GUINEA-BISSAU", "EQUATORIAL GUINEA",
			"GREECE", "GRENADA", "GREENLAND", "GUATEMALA", "FRENCH GUIANA",
			"GUAM", "GUYANA", "HONG KONG", "HEARD ISLAND AND MCDONALD ISLANDS",
			"HONDURAS", "CROATIA", "HAITI", "HUNGARY", "INDONESIA", "INDIA",
			"BRITISH INDIAN OCEAN TERRITORY", "IRELAND", "IRAN", "IRAQ",
			"ICELAND", "ITALY", "JAMAICA", "JORDAN", "JAPAN", "KAZAKSTAN",
			"KENYA", "KYRGYZSTAN", "CAMBODIA", "KIRIBATI",
			"SAINT KITTS AND NEVIS", "SOUTH KOREA", "KUWAIT", "LAOS",
			"LEBANON", "LIBERIA", "LIBYAN ARAB JAMAHIRIYA", "SAINT LUCIA",
			"LIECHTENSTEIN", "SRI LANKA", "LESOTHO", "LITHUANIA", "LUXEMBOURG",
			"LATVIA", "MACAO", "MOROCCO", "MONACO", "MOLDOVA", "MADAGASCAR",
			"MALDIVES", "MEXICO", "MARSHALL ISLANDS", "MACEDONIA", "MALI",
			"MALTA", "MYANMAR", "MONGOLIA", "NORTHERN MARIANA ISLANDS",
			"MOZAMBIQUE", "MAURITANIA", "MONTSERRAT", "MARTINIQUE",
			"MAURITIUS", "MALAWI", "MALAYSIA", "MAYOTTE", "NAMIBIA",
			"NEW CALEDONIA", "NIGER", "NORFOLK ISLAND", "NIGERIA", "NICARAGUA",
			"NIUE", "NETHERLANDS", "NORWAY", "NEPAL", "NAURU", "NEW ZEALAND",
			"OMAN", "PANAMA", "PITCAIRN", "PERU", "PHILIPPINES", "PALAU",
			"PAPUA NEW GUINEA", "POLAND", "PUERTO RICO", "NORTH KOREA",
			"PORTUGAL", "PARAGUAY", "PALESTINE", "FRENCH POLYNESIA", "QATAR",
			"ROMANIA", "RUSSIAN FEDERATION", "RWANDA", "SAUDI ARABIA", "SUDAN",
			"SENEGAL", "SINGAPORE", "SOUTH GEORGIA AND SOUTH SANDWICH ISLANDS",
			"SAINT HELENA", "SVALBARD AND JAN MAYEN", "SOLOMON ISLANDS",
			"SIERRA LEONE", "EL SALVADOR", "SAN MARINO", "SOMALIA",
			"SAINT PIERRE AND MIQUELON", "SAO TOME AND PRINCIPE", "SURINAME",
			"SLOVAKIA", "SLOVENIA", "SWEDEN", "SWAZILAND", "SEYCHELLES",
			"SYRIA", "TURKS AND CAICOS ISLANDS", "CHAD", "TOGO", "THAILAND",
			"TAJIKISTAN", "TOKELAU", "TURKMENISTAN", "EAST TIMOR", "TONGA",
			"TRINIDAD AND TOBAGO", "TUNISIA", "TURKEY", "TUVALU", "TAIWAN",
			"TANZANIA", "UGANDA", "UKRAINE", "URUGUAY",
			"UNITED STATES OF AMERICA", "UZBEKISTAN",
			"SAINT VINCENT AND THE GRENADINES", "VENEZUELA",
			"VIRGIN ISLANDS, BRITISH", "VIRGIN ISLANDS, U.S.A", "VIETNAM",
			"VANUATU", "WALLIS AND FUTUNA", "SAMOA", "YEMEN", "YUGOSLAVIA",
			"SOUTH AFRICA", "ZAMBIA", "ZIMBABWE", "OTHER" };
	public static final String[] FORM_OPTIONS = { "YES", "NO", "DONT KNOW",
			"REJECTED" };
	public static final String[] LOCATION_TYPES = { "DISTRICT FACILITY",
			"LABORATORY", "HOSPITAL", "CLINIC", "OTHER" };
	public static final String[] MENU_NAME_LIST = { "DATALOG", "ENCOUNTER",
			"LOCATION", "PATIENT", "SETUP", "SMS", "USERS" };
	public static final String[] PATIENT_STATUS_LIST = { "", "NEW", "CURED",
			"COMPLETE", "LOST TO FOLLOW-UP", "TREATMENT FAILED", "TRANSFERRED",
			"DIED" };
	public static final String[] USER_ROLE_LIST = { "ADMIN", "GUEST",
			"SCREENER" };
	public static final String[] USER_STATUS_LIST = { "ACTIVE", "SUSPENDED" };

	private XSMS() {
		currentUser = "";
		passCode = "";
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
	 * @return the version
	 */
	public static VersionUtil getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public static void setVersion(VersionUtil version) {
		XSMS.version = version;
	}

	/**
	 * Get a list of values which will be constant throughout the application
	 * 
	 * @param listType
	 * @return array
	 */
	public static String[] getList(ListType listType) {
		switch (listType) {
		case COUNTRIES:
			return COUNTRIES_LIST;
		case LOCATION_TYPE:
			return LOCATION_TYPES;
		case MENU_NAME:
			return MENU_NAME_LIST;
		case PATIENT_STATUS:
			return PATIENT_STATUS_LIST;
		case FORM_OPTION:
			return FORM_OPTIONS;
		case USER_ROLE:
			return USER_ROLE_LIST;
		case USER_STATUS:
			return USER_STATUS_LIST;
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
}
