/**
 * Test class for Regular Expression utility
 */
package org.irdresearch.tbreach.server.util.test;

import junit.framework.TestCase;

import org.irdresearch.tbreach.shared.RegexUtil;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class RegexUtilTest extends TestCase
{

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isAlphaNumeric(java.lang.String)}
	 * 
	 */
	public final void testIsAlphaNumeric()
	{
		assertTrue("Cannot validate Alpha-numeric String.", RegexUtil.isAlphaNumeric("q34gv5q46fhda"));
		assertTrue("Cannot validate Alpha-numeric String.", RegexUtil.isAlphaNumeric("aaabbbccczzz"));
		assertTrue("Cannot validate Alpha-numeric String.", RegexUtil.isAlphaNumeric("34864268126"));
		assertFalse("Validated String.", RegexUtil.isAlphaNumeric(" "));
		assertFalse("Validated String.", RegexUtil.isAlphaNumeric("fER@t^#%Ddfs1243"));
		assertFalse("Validated String.", RegexUtil.isAlphaNumeric("_"));
		assertFalse("Validated String.", RegexUtil.isAlphaNumeric("989-."));
	}

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isContactNumber(java.lang.String)}
	 * 
	 */
	public final void testIsContactNumber()
	{
		assertTrue("Cannot validate Contact No.", RegexUtil.isContactNumber("03453174270"));
		assertTrue("Cannot validate Contact No.", RegexUtil.isContactNumber("+923453174270"));
	}

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isEmailAddress(java.lang.String)}
	 * 
	 */
	public final void testIsEmailAddress()
	{
		assertTrue("Cannot validate Email.", RegexUtil.isEmailAddress("owais.hussain@irdresearch.org"));
		assertTrue("Cannot validate Email.", RegexUtil.isEmailAddress("omarahmed_@hotmail.co.pk"));
		assertFalse("Validated Email.", RegexUtil.isEmailAddress("myname.mail.net"));
		assertFalse("Validated Email.", RegexUtil.isEmailAddress("@mail.net"));
	}

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isNumeric(java.lang.String)}
	 * 
	 */
	public final void testIsNumeric()
	{
		assertTrue("Cannot validate Number.", RegexUtil.isNumeric("0", false));
		assertTrue("Cannot validate Number.", RegexUtil.isNumeric("1005180545035430254420424132", false));
		assertTrue("Cannot validate Number.", RegexUtil.isNumeric("10.125", true));
		assertTrue("Cannot validate Number.", RegexUtil.isNumeric("0.00", true));
		assertTrue("Cannot validate Number.", RegexUtil.isNumeric("021389", true));
		assertTrue("Cannot validate Number.", RegexUtil.isNumeric("0000.0000", true));
		assertFalse("Validated Number.", RegexUtil.isNumeric(".55", true));
		assertFalse("Validated Number.", RegexUtil.isNumeric("0000.", true));
		assertFalse("Validated Number.", RegexUtil.isNumeric(".", true));
		assertFalse("Validated Number.", RegexUtil.isNumeric("15.26", false));
	}

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isValidDate(java.lang.String)}
	 * 
	 */
	public final void testIsValidDate()
	{
		assertTrue("Cannot validate Date.", RegexUtil.isValidDate("01/01/2005"));
		// TODO: Add tests
	}

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isValidNIC(java.lang.String)}
	 * 
	 */
	public final void testIsValidNIC()
	{
		// TODO: Add tests
	}

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isValidTime(java.lang.String)}
	 * 
	 */
	public final void testIsValidTime()
	{
		assertTrue("Cannot validate Time.", RegexUtil.isValidTime("10:10am", true));
		// TODO: Add tests
	}

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isValidURL(java.lang.String)}
	 * 
	 */
	public final void testIsValidURL()
	{
		assertTrue("Cannot validate URL.", RegexUtil.isValidURL("www.google.com"));
		assertTrue("Cannot validate URL.", RegexUtil.isValidURL("www.google.com.pk"));
		assertTrue("Cannot validate URL.", RegexUtil.isValidURL("www.google-pk.com.pk"));
		assertTrue("Cannot validate URL.", RegexUtil.isValidURL("http://www.mywebsite.net"));
		assertTrue("Cannot validate URL.", RegexUtil.isValidURL("http://www.mywebsite.org:8080/"));
		assertTrue("Cannot validate URL.", RegexUtil.isValidURL("https://project.mygroup.org/projectname"));
		assertTrue("Cannot validate URL.", RegexUtil.isValidURL("https://project.mygroup.org/projectname.html"));
		assertFalse("Validated URL.", RegexUtil.isValidURL("https://org"));
		assertFalse("Validated URL.", RegexUtil.isValidURL("www"));
		assertFalse("Validated URL.", RegexUtil.isValidURL("htp:/www.mywebsite"));
	}

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.shared.RegexUtil#isWord(java.lang.String)}
	 * 
	 */
	public final void testIsWord()
	{
		assertTrue("Cannot validate String.", RegexUtil.isWord("acknowledgements"));
		assertTrue("Cannot validate String.", RegexUtil.isWord("o w a i s"));
		assertFalse("Validated String.", RegexUtil.isWord("owais.hussain@irdresearch.org"));
		assertFalse("Validated String.", RegexUtil.isWord("239074"));
		assertFalse("Validated String.", RegexUtil.isWord("0"));
		assertFalse("Validated String.", RegexUtil.isWord("q34gv5q46fhda"));
	}
}
