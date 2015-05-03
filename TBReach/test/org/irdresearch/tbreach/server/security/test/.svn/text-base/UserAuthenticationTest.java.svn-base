/**
 * Test Class for UserAuthentication
 */
package org.irdresearch.tbreach.server.security.test;

import junit.framework.TestCase;

import org.irdresearch.tbreach.server.UserAuthentication;

/**
 * @author sohaib
 *
 */
public class UserAuthenticationTest extends TestCase
{
	private String	userName;
	private String	password;
	private String	secretAnswer;

	public UserAuthenticationTest()
	{
		userName = "OWAIS";
		password = "jingle94";
		secretAnswer = "9622045454";
	}

	/**
	 * Test method for {@link org.irdresearch.tbreach.server.UserAuthentication#userExsists(java.lang.String)}.
	 */
	public final void testUserExsists()
	{
		/* Testing correct */
		assertTrue("User exists test failed!", UserAuthentication.userExsists(userName));
		/* Testing blank */
		assertFalse("User exists test failed!", UserAuthentication.userExsists(""));
		/* Testing case sensitivity */
		assertTrue("User exists test failed!", UserAuthentication.userExsists(userName.toLowerCase()));
	}

	/**
	 * Test method for {@link org.irdresearch.tbreach.server.UserAuthentication#validatePassword(java.lang.String, java.lang.String)}.
	 */
	public final void testValidatePassword()
	{
		/* Testing correct data */
		assertTrue("Password authentication test failed!", UserAuthentication.validatePassword(userName, password));
		/* Testing invalid password */
		assertFalse("Password authentication test failed!", UserAuthentication.validatePassword(userName, "InvalidPassword"));
		/* Testing blank password */
		assertFalse("Password authentication test failed!", UserAuthentication.validatePassword(userName, ""));
		/* Testing invalid user */
		assertFalse("Password authentication test failed!", UserAuthentication.validatePassword("INVALIDUSER", password));
		/* Testing password case sensitivity */
		assertFalse("Password authentication test failed!", UserAuthentication.validatePassword(userName, password.toUpperCase()));
	}

	/**
	 * Test method for {@link org.irdresearch.tbreach.server.UserAuthentication#validateSecretAnswer(org.irdresearch.tbreach.model.mapping.Users, java.lang.String)}.
	 */
	public final void testValidateSecretAnswer()
	{
		/* Testing correct */
		assertTrue("Secret Answer authentication failed!", UserAuthentication.validateSecretAnswer(userName, secretAnswer));
		/* Testing blank */
		assertFalse("Secret Answer authentication failed!", UserAuthentication.validateSecretAnswer(userName, ""));
		/* Testing case sensitivity */
		assertTrue("Secret Answer authentication failed!", UserAuthentication.validateSecretAnswer(userName, secretAnswer.toLowerCase()));
		assertTrue("Secret Answer authentication failed!", UserAuthentication.validateSecretAnswer(userName, secretAnswer.toUpperCase()));
	}

}
