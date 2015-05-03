/**
 * 
 */
package org.irdresearch.tbreach.server.util.test;

import junit.framework.TestCase;

import org.irdresearch.tbreach.server.MDHashUtil;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MD5HashTest extends TestCase
{
	private String[]	original	= { "jingle94" };

	private String[]	hashCode	= { "222117643951139901822193410624612819104125573923922" };

	/**
	 * Test method for
	 * {@link org.irdresearch.tbreach.server.MDHashUtil#getHashCode(java.lang.String)}
	 * .
	 */
	public final void testGetHashCode()
	{
		assertEquals(hashCode[0], MDHashUtil.getHashString(original[0]));
	}
}