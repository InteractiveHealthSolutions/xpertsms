/*
Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package xpertsmsweb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class EventHandlerTest extends TestCase {

	HttpServletRequest mockedRequest;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		mockedRequest = EasyMock.createMock(HttpServletRequest.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.xpertsmsweb.server.EventHandler#handleEvent(javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	public final void testHandleEvent() {
		String queryString = "type=astmresult&username=admin&password=jingle94&pid=1621008001&sampleid=091012091&mtb=null&rif=ERROR&final=yes&error=yes&errorcode=5007&errornotes=Error 5007: [SPC] probe check failed  Probe check value of 219 0 for reading number 2 was below the minimum of 226 0: Error 5007: [SPC] probe check failed  Probe check value of 219 0 for reading number 2 was below the minimum of 226 0&enddate=20121009054907&operatorid=Karachi X-ray&pcid=owais-ird&instserial=707851&moduleid=615337&cartrigeid=101256226&reagentlotid=04405&systemid=GeneXpert PC&receiverid=IRD Xpert&expdate=20130107&probea=NO RESULT&probeb=NO RESULT&probec=NO RESULT&probed=NO RESULT&probee=NO RESULT&probespc=NO RESULT&probeact=0&probebct=0&probecct=0&probedct=0&probeect=0&probespcct=0&probeaendpt=0&probebendpt=0&probecendpt=0&probedendpt=0&probeeendpt=0&probespcendpt=0";
		HttpURLConnection httpConnection = null;
		OutputStream outputStream = null;
		// String url = null;
		int responseCode = 0;
		URL url;
		try {
			url = new URL("http://127.0.0.1:8888/xpertsmsweb.jsp");
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConnection.setRequestProperty("Content-Language", "en-US");
			httpConnection.setDoOutput(true);
			outputStream = httpConnection.getOutputStream();
			outputStream.write(queryString.getBytes());
			outputStream.flush();
			responseCode = httpConnection.getResponseCode();
			outputStream.close();
			httpConnection.disconnect();
			assertEquals("", responseCode, HttpURLConnection.HTTP_OK);
			BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.xpertsmsweb.server.EventHandler#createGeneXpertResults(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	public final void testCreateGeneXpertResults() {
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.xpertsmsweb.server.EventHandler#postToBackup(javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	public final void testPostToBackup() {
	}

}
