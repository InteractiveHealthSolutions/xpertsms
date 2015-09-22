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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.irdresearch.smstarseel.context.TarseelContext;
import org.irdresearch.smstarseel.context.TarseelServices;
import org.irdresearch.smstarseel.data.OutboundMessage;

import com.ihsinformatics.xpertsmsweb.server.ServerServiceImpl;
import com.ihsinformatics.xpertsmsweb.shared.model.GeneXpertResults;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class EventHandlerTest extends TestCase {

	ServerServiceImpl service;
	HttpServletRequest mockedRequest;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		mockedRequest = EasyMock.createMock(HttpServletRequest.class);
		service = new ServerServiceImpl();
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
		String queryString = "type=astmresult&username=admin&password=jingle94&pid=1621008001&sampleid=091012091&mtb=null&rif=ERROR&final=yes&error=yes&errorcode=5007&errornotes=Error 5007: [SPC] probe check failed  Probe check value of 219 0 for reading number 2 was below the minimum of 226 0: Error 5007: [SPC] probe check failed  Probe check value of 219 0 for reading number 2 was below the minimum of 226 0&enddate=20121009054907&operatorid=Karachi X-ray&pcid=owais-ird&receiverid=IHS&instserial=707851&moduleid=615337&cartrigeid=101256226&reagentlotid=04405&systemid=GeneXpert PC&receiverid=IRD Xpert&expdate=20130107&probea=NO RESULT&probeb=NO RESULT&probec=NO RESULT&probed=NO RESULT&probee=NO RESULT&probespc=NO RESULT&probeact=0&probebct=0&probecct=0&probedct=0&probeect=0&probespcct=0&probeaendpt=0&probebendpt=0&probecendpt=0&probedendpt=0&probeeendpt=0&probespcendpt=0";
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
			assertEquals("Event not handled", responseCode, HttpURLConnection.HTTP_OK);
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
	 * {@link com.ihsinformatics.xpertsmsweb.server.EventHandler#handleEvent(javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	public final void testPositiveResult() {
		String queryString = "type=astmresult&username=admin&password=jingle94&pid=101130800001-9&sampleid=141016_001&mtb=MTB DETECTED MEDIUM&rif=Rif Resistance NOT DETECTED&error=yes&errorcode=5002&errornotes=Post-run analysis error&notes=Just XDR-TB&enddate=2015-05-23&operatorid=OWAIS&pcid=CEPHEID5G183R1&receiverid=IHS&instserial=708228&moduleid=618255&cartrigeid=204304821&reagentlotid=10713-AX&systemid=Machine API Test&expdate=2014-06-21&probea=POS&probeb=NO RESULT&probec=NEG&probed=NEG&probee=POS&probespc=0&probeact=1.1&probebct=2.2&probecct=2.3&probedct=1.3&probeect=1.4&probespcct=2.5&probeaendpt=3.6&probebendpt=4.7&probecendpt=4.5&probedendpt=3.2&probeeendpt=1.0&probespcendpt=0.0";
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
			assertEquals("Positive Results not saving", responseCode, HttpURLConnection.HTTP_OK);
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
	 * {@link com.ihsinformatics.xpertsmsweb.server.EventHandler#handleEvent(javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	public final void testForPositiveSmsAlerts() {
		try {
			// Get existing number of outbound messages
			TarseelServices services = TarseelContext.getServices();
			List<OutboundMessage> outbounds = services.getSmsService().findPendingOutboundTillNow("XpertSMS", false, Integer.MAX_VALUE);
			// TODO: Needs to be completed
			/*GeneXpertResults gxp = new GeneXpertResults("07-01-1297-15-R", "Muhammad Owais", "PRL-Sindh", collectedBy, new Date(), "20150709131815", geneXpertResult, false, "MTB NOT DETECTED", drugResistance, "5002", "Post analysis error", "Cepheid2H0D7V1", "PRL-SINDH", "802274", "624130", "235826832", "18303", null, "NO RESULT", "NO RESULT", "NO RESULT", "NO RESULT", "NO RESULT", "NO RESULT", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
			service.saveGeneXpertResults(gxp);
			List<OutboundMessage> outboundsNow = services.getSmsService().findPendingOutboundTillNow("XpertSMS", false, Integer.MAX_VALUE);
			assertFalse("SMS Alerts either failed to generate or are disabled in MessageSettings", outbounds.size() == outboundsNow.size());*/
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
	public final void testForNegativeSmsAlerts() {
	}

	/**
	 * Test method for
	 * {@link com.ihsinformatics.xpertsmsweb.server.EventHandler#postToBackup(javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	public final void testForErrorSmsAlerts() {
	}

}
