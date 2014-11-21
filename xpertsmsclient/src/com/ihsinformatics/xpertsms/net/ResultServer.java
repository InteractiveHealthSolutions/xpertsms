
package com.ihsinformatics.xpertsms.net;

//import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
//import javax.swing.text.SimpleAttributeSet;
//import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import com.ihsinformatics.xpertsms.constant.FileConstants;
import com.ihsinformatics.xpertsms.model.XpertProperties;
import com.ihsinformatics.xpertsms.model.XpertResultUploadMessage;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;
import com.ihsinformatics.xpertsms.parser.astm.ASTMProcessorThread;
import com.ihsinformatics.xpertsms.ui.ControlPanel;
//import com.ihsinformatics.xpertsms.constants.astm.ASTMNetworkConstants;
import com.ihsinformatics.xpertsms.util.CsvUtil;

public class ResultServer extends Thread
{
	// create socket and listen for results from Xpert
	// when result arrives,
	// pass socket to newly spawned thread

	// go back to listening
	// private static Vector<String> messages;
	private ArrayBlockingQueue<String>						messages;
	private ArrayBlockingQueue<XpertResultUploadMessage>	outgoingMessages;
	private int												threadCount;
	private int												status;
	private String											errorCode;
	private JTextPane										monitorPane;
	private Boolean											stopped;
	private ServerSocket									socket;
	private PrintWriter										csvWriter;

	private SimpleDateFormat								logEntryFormatter	= null;
	private SimpleDateFormat								fileNameFormatter	= null;	;

	public ResultServer (JTextPane monitorPane)
	{
		messages = new ArrayBlockingQueue<String> (15);
		outgoingMessages = new ArrayBlockingQueue<XpertResultUploadMessage> (15);
		threadCount = 0;
		this.monitorPane = monitorPane;
		stopped = false;

		logEntryFormatter = new SimpleDateFormat (FileConstants.FILE_ENTRY_DATE_FORMAT);
		fileNameFormatter = new SimpleDateFormat (FileConstants.FILE_NAME_DATE_FORMAT);

		File csv = new File (FileConstants.XPERT_SMS_DIR + System.getProperty ("file.separator") + fileNameFormatter.format (new Date ()) + "_xpertdump.csv");
		try
		{
			csvWriter = new PrintWriter (csv);
			String header = "\"messageId\",\"systemId\",\"systemName\",\"softwareVersion\",\"receiverId\",\"processingId\",\"versionNumber\",\"messageDateTime\",\"instrumentSpecimenId\",\"universalTestId\",\"priority\",\"orderDateTime\",\"actionCode\",\"";
			header += "specimenType\",\"reportType\",\"systemDefinedTestName\",\"systemDefinedTestVersion\",\"resultStatus\",\"operatorId\",\"testStartDate\",\"testEndDate\",\"pcId\",\"instrumentSerial\",\"moduleId\",\"cartridgeId\",\"reagentLotId\",\"expDate\",\"";
			header += "isFinal\",\"isPending\",\"isError\",\"isCorrection\",\"errorCode\",\"patientId\",\"sampleId\",\"mtbResult\",\"rifResult\",\"probeResultA\",\"probeResultB\",\"probeResultC\",\"probeResultD\",\"";
			header += "probeResultE\",\"probeResultSpc\",\"probeCtA\",\"probeCtB\",\"probeCtC\",\"probeCtD\",\"probeCtE\",\"probeCtSPC\",\"probeEndptA\",\"probeEndptB\",\"probeEndptC\",\"probeEndptD\",\"probeEndptE\",\"probeEndptSpc\"";

			writeToCSV (header);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace ();
		}
	}

	@Override
	public void run ()
	{
		startServer ();
		if (status == -1)
		{
			JOptionPane.showMessageDialog (null, errorCode, "Error occurred", JOptionPane.ERROR_MESSAGE);
		}
		else if (status == 0)
		{
			JOptionPane.showMessageDialog (null, MessageCodes.SERVER_STOPPED, "Notification!", JOptionPane.WARNING_MESSAGE);
		}
	}

	public void startServer ()
	{
		socket = null;
		boolean propsLoaded = false;
		try
		{
			propsLoaded = loadProperties ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			errorCode = MessageCodes.ERROR_LOADING_PROPERTIES;
			status = -1;
		}
		if (!propsLoaded)
		{
			errorCode = MessageCodes.PROPERTIES_NOT_SET;
			status = -1;
		}
		// stopped = true;

		ASTMProcessorThread apt = new ASTMProcessorThread (this);
		apt.start ();

		HttpSender hs = new HttpSender (this);
		hs.start ();

		int port = Integer.parseInt (ControlPanel.props.getProperty ("localport"));
		try
		{
			socket = new ServerSocket (port);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace ();
			System.out.println ("could not listen on port " + port);
			errorCode = MessageCodes.PORT_INACCESSIBLE;
			status = -1;
		}
		System.out.println ("listening on port " + port);

		// Test HTTP using Sample data (moved to TestClass)
		CsvUtil csvUtil = new CsvUtil ("res/GxaSamples.csv", true);
		String[][] data = csvUtil.readData ();
		XpertASTMResultUploadMessage[] sampleMessages = new XpertASTMResultUploadMessage[data.length];
		for (int i = 0; i < data.length; i++)
		{
			try
			{
				int j = 0;
				sampleMessages[i] = new XpertASTMResultUploadMessage ();
				sampleMessages[i].setUniversalTestId(data[i][j++]);
				sampleMessages[i].setSystemDefinedTestName(data[i][j++]);
				sampleMessages[i].setSystemDefinedTestVersion(data[i][j++]);
				sampleMessages[i].setSampleId (data[i][j++]);
				sampleMessages[i].setPatientId (data[i][j++]);
				sampleMessages[i].setOperatorId (data[i][j++]);
				sampleMessages[i].setTestStartDate (data[i][j++]);
				sampleMessages[i].setTestEndDate (data[i][j++]);
				j++;	// Message sent on is not recorded
				sampleMessages[i].setReagentLotId (data[i][j++]);
				sampleMessages[i].setExpDate (data[i][j++]);
				sampleMessages[i].setCartridgeId (data[i][j++]);
				sampleMessages[i].setModuleId (data[i][j++]);
				sampleMessages[i].setInstrumentSerial (data[i][j++]);
				sampleMessages[i].setSoftwareVersion(data[i][j++]);
				sampleMessages[i].setMtbResult (data[i][j++]);
				sampleMessages[i].setRifResult (data[i][j++]);
				j++;	// Result Text is implemented in XpertASTMResultUploadMethod
				sampleMessages[i].setPcId (data[i][j++]);
				sampleMessages[i].setSystemId (data[i][j++]);
				sampleMessages[i].setSystemName(data[i][j++]);
				sampleMessages[i].setComputerName(data[i][j++]);
				j++;	// Notes not implemented
				sampleMessages[i].setErrorCode(data[i][j++]);
				sampleMessages[i].setErrorNotes(data[i][j++]);
				sampleMessages[i].setMessageId (data[i][j++]);
				sampleMessages[i].setProbeResultA (data[i][j++]);
				sampleMessages[i].setProbeResultB (data[i][j++]);
				sampleMessages[i].setProbeResultC (data[i][j++]);
				sampleMessages[i].setProbeResultD (data[i][j++]);
				sampleMessages[i].setProbeResultE (data[i][j++]);
				sampleMessages[i].setProbeResultSPC (data[i][j++]);
				sampleMessages[i].setProbeCtA (data[i][j++]);
				sampleMessages[i].setProbeCtB (data[i][j++]);
				sampleMessages[i].setProbeCtC (data[i][j++]);
				sampleMessages[i].setProbeCtD (data[i][j++]);
				sampleMessages[i].setProbeCtE (data[i][j++]);
				sampleMessages[i].setProbeCtSPC (data[i][j++]);
				sampleMessages[i].setProbeEndPtA (data[i][j++]);
				sampleMessages[i].setProbeEndPtB (data[i][j++]);
				sampleMessages[i].setProbeEndPtC (data[i][j++]);
				sampleMessages[i].setProbeEndPtD (data[i][j++]);
				sampleMessages[i].setProbeEndPtE (data[i][j++]);
				sampleMessages[i].setProbeEndPtSPC (data[i][j++]);
				SimpleDateFormat messageFormat = new SimpleDateFormat("yyyyMMdd");
				sampleMessages[i].setMessageDateTime(messageFormat.format(new Date()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// Put 
//		for(XpertASTMResultUploadMessage message : sampleMessages)
//		{
//			putOutGoingMessage (message);
//		}

		/*String message1 = "H|@^\\|URM-tTjX+pTA-01||GeneXpert PC^GeneXpert^4.3|||||IRD_XPERT|| P|1394-97|20121020053034\n"
			+ "P|1|||1621005001\nO|1|051012274||^^^G4v5|R|20121006153947|||||||||ORH||||||||||F\nR|1|^G4v5^^TBPos^Xpert MTB-RIF Assay G4^5^MTB^|MTB DETECTED VERY LOW^|||||F||Karachi X-ray|20121006153947|20121006172116|Cepheid3WDBYQ1^707851^615337^101256275^04405^20130106\n"
			+ "R|2|^G4v5^^TBPos^^^Probe D^|POS^|||\nR|3|^G4v5^^TBPos^^^Probe D^Ct|^32.4|||\nR|4|^G4v5^^TBPos^^^Probe D^EndPt|^102.0|||\nR|5|^G4v5^^TBPos^^^Probe C^|POS^|||\nR|6|^G4v5^^TBPos^^^Probe C^Ct|^31.2|||\n"
			+ "R|7|^G4v5^^TBPos^^^Probe C^EndPt|^130.0|||\nR|8|^G4v5^^TBPos^^^Probe E^|NEG^|||\nR|9|^G4v5^^TBPos^^^Probe E^Ct|^0|||\nR|10|^G4v5^^TBPos^^^Probe E^EndPt|^4.0|||\nR|11|^G4v5^^TBPos^^^Probe B^|POS^|||\nR|12|^G4v5^^TBPos^^^Probe B^Ct|^31.5|||\nR|13|^G4v5^^TBPos^^^Probe B^EndPt|^88.0|||\n"
			+ "R|14|^G4v5^^TBPos^^^Probe A^|POS^|||\nR|15|^G4v5^^TBPos^^^Probe A^Ct|^30.7|||\nR|16|^G4v5^^TBPos^^^Probe A^EndPt|^92.0|||\nR|17|^G4v5^^TBPos^^^SPC^|NA^|||\nR|18|^G4v5^^TBPos^^^SPC^Ct|^28.1|||\nR|19|^G4v5^^TBPos^^^SPC^EndPt|^302.0|||\nR|20|^G4v5^^Rif^Xpert MTB-RIF Assay G4^5^Rif Resistance^|Rif Resistance DETECTED^|||||F||Karachi X-ray|20121006153947|20121006172116|Cepheid3WDBYQ1^707851^615337^101256275^04405^20130106"
			+ "R|21|^G4v5^^Rif^^^Probe D^|POS^|||\nR|22|^G4v5^^Rif^^^Probe D^Ct|^32.4|||\nR|23|^G4v5^^Rif^^^Probe D^EndPt|^102.0|||\nR|24|^G4v5^^Rif^^^Probe C^|POS^|||\nR|25|^G4v5^^Rif^^^Probe C^Ct|^31.2|||\nR|26|^G4v5^^Rif^^^Probe C^EndPt|^130.0|||\nR|27|^G4v5^^Rif^^^Probe E^|NEG^|||\nR|28|^G4v5^^Rif^^^Probe E^Ct|^0|||\nR|29|^G4v5^^Rif^^^Probe E^EndPt|^4.0|||\n"
			+ "R|30|^G4v5^^Rif^^^Probe B^|POS^|||\nR|31|^G4v5^^Rif^^^Probe B^Ct|^31.5|||\nR|32|^G4v5^^Rif^^^Probe B^EndPt|^88.0|||\nR|33|^G4v5^^Rif^^^Probe A^|POS^|||\nR|34|^G4v5^^Rif^^^Probe A^Ct|^30.7|||\nR|35|^G4v5^^Rif^^^Probe A^EndPt|^92.0|||\nR|36|^G4v5^^Rif^^^SPC^|NA^|||\nR|37|^G4v5^^Rif^^^SPC^Ct|^28.1|||\nR|38|^G4v5^^Rif^^^SPC^EndPt|^302.0|||\nR|39|^G4v5^^QC^Xpert MTB-RIF Assay G4^5^QC Check^|^|||||F||Karachi X-ray|20121006153947|20121006172116|Cepheid3WDBYQ1^707851^615337^101256275^04405^20130106\nR|40|^G4v5^^QC^^^QC-1^|NEG^|||\n"
			+ "R|41|^G4v5^^QC^^^QC-1^Ct|^0|||\nR|42|^G4v5^^QC^^^QC-1^EndPt|^0|||\nR|43|^G4v5^^QC^^^QC-2^|NEG^|||\nR|44|^G4v5^^QC^^^QC-2^Ct|^0|||\nR|45|^G4v5^^QC^^^QC-2^EndPt|^0|||\nL|1|N";

		putMessage(message1);
		
		String message2 = "H|@^\\|URM-b+UY+pTA-02||GeneXpert PC^GeneXpert^4.3|||||IRD_XPERT||P|1394-97|20121020053357\n"
			+ "P|1|||7761\nO|1|814913||^^^G4v5|R|20120913003937|||||||||ORH||||||||||F\nR|1|^G4v5^^TBPos^Xpert MTB-RIF Assay G4^5^MTB^|MTB DETECTED MEDIUM^|||||F||Sunil Asif|20120913003937|20120913022015|Cepheid3WDBYQ1^706593^611954^101259317^04405^20130106\nR|2|^G4v5^^TBPos^^^Probe D^|POS^|||\n"
			+ "R|3|^G4v5^^TBPos^^^Probe D^Ct|^18.9|||\nR|4|^G4v5^^TBPos^^^Probe D^EndPt|^242.0|||\nR|5|^G4v5^^TBPos^^^Probe C^|POS^|||\nR|6|^G4v5^^TBPos^^^Probe C^Ct|^18.2|||\nR|7|^G4v5^^TBPos^^^Probe C^EndPt|^250.0|||\nR|8|^G4v5^^TBPos^^^Probe E^|NEG^|||\nR|9|^G4v5^^TBPos^^^Probe E^Ct|^0|||\nR|10|^G4v5^^TBPos^^^Probe E^EndPt|^-3.0|||\n"
			+ "R|11|^G4v5^^TBPos^^^Probe B^|POS^|||\nR|12|^G4v5^^TBPos^^^Probe B^Ct|^19.2|||\nR|13|^G4v5^^TBPos^^^Probe B^EndPt|^133.0|||\nR|14|^G4v5^^TBPos^^^Probe A^|POS^|||\nR|15|^G4v5^^TBPos^^^Probe A^Ct|^17.5|||\nR|16|^G4v5^^TBPos^^^Probe A^EndPt|^156.0|||\nR|17|^G4v5^^TBPos^^^SPC^|NA^|||\nR|18|^G4v5^^TBPos^^^SPC^Ct|^27.4|||\n"
			+ "R|19|^G4v5^^TBPos^^^SPC^EndPt|^333.0|||\nR|20|^G4v5^^Rif^Xpert MTB-RIF Assay G4^5^Rif Resistance^|Rif Resistance DETECTED^|||||F||Sunil Asif|20120913003937|20120913022015|Cepheid3WDBYQ1^706593^611954^101259317^04405^20130106\nR|21|^G4v5^^Rif^^^Probe D^|POS^|||\nR|22|^G4v5^^Rif^^^Probe D^Ct|^18.9|||\nR|23|^G4v5^^Rif^^^Probe D^EndPt|^242.0|||\nR|24|^G4v5^^Rif^^^Probe C^|POS^|||\n"
			+ "R|25|^G4v5^^Rif^^^Probe C^Ct|^18.2|||\nR|26|^G4v5^^Rif^^^Probe C^EndPt|^250.0|||\nR|27|^G4v5^^Rif^^^Probe E^|NEG^|||\nR|28|^G4v5^^Rif^^^Probe E^Ct|^0|||\nR|29|^G4v5^^Rif^^^Probe E^EndPt|^-3.0|||\nR|30|^G4v5^^Rif^^^Probe B^|POS^|||\nR|31|^G4v5^^Rif^^^Probe B^Ct|^19.2|||\nR|32|^G4v5^^Rif^^^Probe B^EndPt|^133.0|||\nR|33|^G4v5^^Rif^^^Probe A^|POS^|||\nR|34|^G4v5^^Rif^^^Probe A^Ct|^17.5|||\n"
			+ "R|35|^G4v5^^Rif^^^Probe A^EndPt|^156.0|||\nR|36|^G4v5^^Rif^^^SPC^|NA^|||\nR|37|^G4v5^^Rif^^^SPC^Ct|^27.4|||\nR|38|^G4v5^^Rif^^^SPC^EndPt|^333.0|||\nR|39|^G4v5^^QC^Xpert MTB-RIF Assay G4^5^QC Check^|^|||||F||Sunil Asif|20120913003937|20120913022015|Cepheid3WDBYQ1^706593^611954^101259317^04405^20130106\nR|40|^G4v5^^QC^^^QC-1^|NEG^|||\nR|41|^G4v5^^QC^^^QC-1^Ct|^0|||\nR|42|^G4v5^^QC^^^QC-1^EndPt|^0|||\n"
			+ "R|43|^G4v5^^QC^^^QC-2^|NEG^|||\nR|44|^G4v5^^QC^^^QC-2^Ct|^0|||\nR|45|^G4v5^^QC^^^QC-2^EndPt|^0|||\nL|1|N";
		
		putMessage(message2);*/

		// TEST COMMENTED OUT
		try
		{
			while (!stopped)
			{
				threadCount++;
				updateTextPane (getLogEntryDateString (new Date ()) + ": Waiting for GeneXpert\n");
				ResultThread rt = new ResultThread (socket.accept (), threadCount, this);
				rt.start ();
				updateTextPane (getLogEntryDateString (new Date ()) + "Connected to GeneXpert\n");
			}

			if (stopped)
			{
				updateTextPane (getLogEntryDateString (new Date ()) + "Stop message received\n");
			}
			if (!socket.isClosed ())
				socket.close ();
			status = 0;
		}
		catch (IOException e)
		{
			if (stopped)
			{
				if (!socket.isClosed ())
				{
					try
					{
						socket.close ();
					}
					catch (IOException e1)
					{
						e1.printStackTrace ();
					}

					status = 0;
				}
			}
			else
			{
				status = -1;
				errorCode = MessageCodes.GENERAL_ERROR;
				e.printStackTrace ();
			}
		}
	}

	public boolean loadProperties () throws FileNotFoundException, IOException
	{

		ControlPanel.props.load (new FileInputStream (FileConstants.FILE_PATH));
		//TODO: Owais, refactor this
		//		String[] mandatory = {XpertProperties.MTB_CODE, XpertProperties.RIF_CODE, XpertProperties.QC_CODE, XpertProperties.SERVER_URL, XpertProperties.SERVER_PORT, XpertProperties.LOCAL_PORT,
		//		XpertProperties.EXPORT_PROBES};
		String[] mandatory = {XpertProperties.MTB_CODE, XpertProperties.RIF_CODE, XpertProperties.QC_CODE, XpertProperties.LOCAL_PORT};
		for (String s : mandatory)
		{
			String property = ControlPanel.props.getProperty (s);
			if (property == null || property.length () == 0)
			{
				return false;
			}
		}
		return true;
	}

	public ArrayBlockingQueue<String> getMessages ()
	{
		return messages;
	}

	public void putMessage (String s)
	{
		try
		{
			messages.put (s);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace ();
		}
	}

	public String getHead ()
	{
		String s = null;
		try
		{
			s = messages.take ();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace ();
			s = null;
		}

		return s;
	}

	public void removeMessage (int index)
	{
		messages.remove (index);
	}

	/*
	 * public void addMessageAtIndex(String message, int index) {
	 * messages.add(index, message); }
	 */

	public int getMessageListSize ()
	{
		return messages.size ();
	}

	public ArrayBlockingQueue<XpertResultUploadMessage> getOutgoingMessages ()
	{
		return outgoingMessages;
	}

	public int getOutgoingMessageListSize ()
	{
		return outgoingMessages.size ();
	}

	public void putOutGoingMessage (XpertResultUploadMessage message)
	{
		try
		{
			outgoingMessages.put (message);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace ();
		}
	}

	public XpertResultUploadMessage getOutgoingMessagesHead ()
	{
		XpertResultUploadMessage message = null;
		try
		{
			message = outgoingMessages.take ();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace ();
			message = null;
		}

		return message;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode ()
	{
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode (String errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * @return the monitorPane
	 */
	public JTextPane getMonitorPane ()
	{
		return monitorPane;
	}

	/**
	 * @param monitorPane
	 *            the monitorPane to set
	 */
	public void setMonitorPane (JTextPane monitorPane)
	{
		this.monitorPane = monitorPane;
	}

	/**
	 * @return the status
	 */
	public int getStatus ()
	{
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus (int status)
	{
		this.status = status;
	}

	/**
	 * @return the stopped
	 */
	public Boolean getStopped ()
	{
		return stopped;
	}

	/**
	 * @param stopped
	 *            the stopped to set
	 */
	public void setStopped (Boolean stopped)
	{
		this.stopped = stopped;
		System.out.println ("Server " + stopped.toString ());

	}

	/**
	 * @return the socket
	 */
	public ServerSocket getSocket ()
	{
		return socket;
	}

	/**
	 * @param socket
	 *            the socket to set
	 */
	public void setSocket (ServerSocket socket)
	{
		this.socket = socket;
	}

	public synchronized void updateTextPane (final String text)
	{

		SwingUtilities.invokeLater (new Runnable ()
		{
			public void run ()
			{

				StyledDocument doc = monitorPane.getStyledDocument ();
				try
				{
					doc.insertString (doc.getLength (), text, null);
				}
				catch (BadLocationException e)
				{
					throw new RuntimeException (e);
				}
				monitorPane.setCaretPosition (doc.getLength () - 1);
			}
		});
	}

	public synchronized void writeToCSV (String text)
	{
		System.out.println (text);
		System.out.println ("printing....");
		csvWriter.println (text);
		csvWriter.flush ();
		System.out.println ("printed");
	}

	public String getLogEntryDateString (Date date)
	{
		return logEntryFormatter.format (date);
	}

	public String getFileNameDateString (Date date)
	{
		return fileNameFormatter.format (date);
	}
}
