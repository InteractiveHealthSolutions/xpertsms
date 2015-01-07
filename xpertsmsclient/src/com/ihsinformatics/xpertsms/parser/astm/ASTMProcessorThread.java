
package com.ihsinformatics.xpertsms.parser.astm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import com.ihsinformatics.xpertsms.constant.ASTMMessageConstants;
import com.ihsinformatics.xpertsms.constant.FileConstants;
import com.ihsinformatics.xpertsms.constant.SendMethods;
import com.ihsinformatics.xpertsms.model.astm.XpertASTMResultUploadMessage;
import com.ihsinformatics.xpertsms.net.ResultServer;
import com.ihsinformatics.xpertsms.net.exception.InvalidASTMMessageFormatException;
import com.ihsinformatics.xpertsms.ui.ControlPanel;

/**
 * @author ali.habib@irdresearch.org
 */
public class ASTMProcessorThread extends Thread
{
	private ResultServer	server;
	private PrintWriter		pw;

	public ASTMProcessorThread (ResultServer server)
	{
		this.server = server;
	}

	public void run ()
	{

		try
		{
			pw = new PrintWriter (new BufferedWriter (new FileWriter (new File (FileConstants.XPERT_SMS_DIR + System.getProperty ("file.separator") + server.getFileNameDateString (new Date ())
					+ "_XpertSMS_proc_log.txt"), true)));
			println ("Processing Thread Started", true);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace ();
		}

		catch (IOException e1)
		{
			e1.printStackTrace ();
		}
		while (!server.getStopped ())
		{
			if (server.getMessageListSize () > 0)
			{
				doTriage ();

			}
		}
		if (server.getStopped ())
		{
			println ("Stopping Processing Thread", true);
		}
		pw.flush ();
		pw.close ();

	}

	public void doTriage ()
	{
		String message = server.getHead ();
		server.removeMessage (0);

		String lines[] = message.split ("[\r\n]+");

		XpertASTMResultUploadMessage xpertMessage = new XpertASTMResultUploadMessage ();
		String line = "";
		for (int i = 0; i < lines.length; i++)
		{
			line = lines[i];

			if (line.trim ().equals (""))
				continue;

			char recordType = line.charAt (0);

			switch (recordType)
			{
				case ASTMMessageConstants.HEADER_RECORD :
					HeaderParser hp = new HeaderParser (xpertMessage, line);
					try
					{
						hp.parse ();
					}
					catch (InvalidASTMMessageFormatException e)
					{

						e.printStackTrace ();
					}
					catch (Exception e)
					{
						e.printStackTrace ();
					}
					break;

				case ASTMMessageConstants.ORDER_RECORD :
					OrderRecordParser op = new OrderRecordParser (xpertMessage, line);
					try
					{
						op.parse ();
					}
					catch (Exception e)
					{
						e.printStackTrace ();
					}
					break;

				case ASTMMessageConstants.PATIENT_RECORD :
					PatientRecordParser pp = new PatientRecordParser (xpertMessage, line);
					try
					{
						pp.parse ();
					}
					catch (Exception e)
					{
						e.printStackTrace ();
					}
					break;

				case ASTMMessageConstants.RESULT_RECORD :
					ResultRecordParser rp = new ResultRecordParser (xpertMessage, line);
					try
					{
						rp.parse ();
					}
					catch (Exception e)
					{
						e.printStackTrace ();
					}
					break;

				case ASTMMessageConstants.TERMINATOR_RECORD :
					TerminatorRecordParser tp = new TerminatorRecordParser (xpertMessage, line);
					try
					{
						tp.parse ();
					}
					catch (Exception e)
					{
						e.printStackTrace ();
					}
					break;

				case ASTMMessageConstants.COMMENT_RECORD :
					CommentRecordParser cp = new CommentRecordParser (xpertMessage, line);
					try
					{
						cp.parse ();
					}
					catch (Exception e)
					{
						e.printStackTrace ();
					}
					break;

				default :
					// TODO: check
					println ("***NEW***:|" + line + "|", true);
			};
		}

		// TODO check condition
		if (xpertMessage.getSampleId () != null)
		{
			if (ControlPanel.props.getProperty ("sendmethod").equals (SendMethods.CSV_DUMP))
			{

				server.writeToCSV (xpertMessage.toCsv ());
				println ("Result for sample " + xpertMessage.getSampleId () + " written to CSV", true);

			}

			else
			{
				server.putOutGoingMessage (xpertMessage);
				println ("Result for sample " + xpertMessage.getSampleId () + " queued for transmission", true);
			}
		}

	}

	public void print (String text, boolean toGUI)
	{

		pw.print (server.getLogEntryDateString (new Date ()) + ": " + text);
		pw.flush ();
		if (toGUI)
			server.updateTextPane (server.getLogEntryDateString (new Date ()) + ": " + text);
	}

	public void println (String text, boolean toGUI)
	{

		print (text + "\n", toGUI);
	}
}
