package org.irdresearch.tbreach.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.management.InstanceAlreadyExistsException;

import org.irdresearch.tbreach.shared.RegexUtil;
import org.irdresearch.tbreach.shared.model.Encounter;
import org.irdresearch.tbreach.shared.model.EncounterId;
import org.irdresearch.tbreach.shared.model.EncounterResults;
import org.irdresearch.tbreach.shared.model.EncounterResultsId;

public class TBReachMain
{
	@SuppressWarnings("deprecation")
	public static void main(String[] args)
	{
		try
		{
			SmsTarseel.Instantiate ();
		}
		catch (InstanceAlreadyExistsException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("HELLO!");
		//ServerServiceImpl implObj = new ServerServiceImpl();
		//ReportUtil.generateCSVfromQuery("select * from Enc_CDF", ',');
		/*try
		{
			PatientDOTS obj = new PatientDOTS("99999999999", "9999999");
			obj.setDotsNo("1111111");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
	
		/* THIS PIECE OF CODE IS THE RESULT OF A BLUNDER THAT I MADE ON 16-Sep-2011 */
		String testId = "22513011501";
		
		System.out.println(RegexUtil.isNumeric(testId.toUpperCase(), false));
		String filePath = "D:\\Pick n Drop\\Forms-2011-16-09-1025\\";
		String[] files = {
		/*
		 "BASELINE.csv",
		 "CDF.csv",
		 "CT_SUSPECT.csv",
		 "DFR.csv",
		 "DRUG_ADM.csv",
		 "END_FOL.csv",
		 "FOLLOW_UP.csv",
		 "P_INFO.csv",
		 "PAED_CONF.csv",
		 "PAED_DIAG.csv",
		 "SPUTUM_COL.csv",
		 "SUSPECT_ID.csv",
		 "TB_HISTORY.csv"
		*/
		};

		/*String[] header = { "EncounterID", "PID1", "PID2", "DateEncounterStart", "DateEncounterEnd", "DateEncounterEntered" };
		for (String s : files)
		{
			try
			{
				File file = new File(filePath + s);
				Scanner sc = new Scanner(file);
				String firstLine = sc.nextLine();
				header = firstLine.split(",");
				while (sc.hasNextLine())
				{
					String line = sc.nextLine();
					String append = "000";
					Date limit = new Date(2011 + 1900, 8, 11, 23, 59, 59);
					line = line.replace("\"", "");
					String[] parts = line.split(",");
					int id = Integer.parseInt(parts[findIndex(header, "EncounterID")]);
					String pid1 = parts[findIndex(header, "PID1")];
					if (!pid1.contains("-"))
						pid1 = append.substring(0, 11 - pid1.length()) + pid1;
					String pid2 = parts[findIndex(header, "PID2")];
					String encounterType = file.getName().substring(0, file.getName().indexOf('.'));
					String locationId = "";
					Date dateEncounterStart = null, dateEncounterEnd = null, dateEncounterEntered = null;
					String details = "";
					try
					{
						dateEncounterStart = parseDate(parts[findIndex(header, "DateEncounterStart")]);
					}
					catch (Exception e1)
					{
						System.out.println("Bad date");
					}
					try
					{
						dateEncounterEnd = parseDate(parts[findIndex(header, "DateEncounterEnd")]);
					}
					catch (Exception e1)
					{
						System.out.println("Bad date");
					}
					try
					{
						dateEncounterEntered = parseDate(parts[findIndex(header, "DateEncounterEntered")]);
					}
					catch (Exception e1)
					{
						System.out.println("Bad date");
					}

					EncounterId encounterId = new EncounterId(id, pid1, pid2);
					Encounter encounter = new Encounter(encounterId, encounterType, locationId, dateEncounterStart, dateEncounterEnd,
							dateEncounterEntered, details);

					 IF DATE OF ENCOUNTER EXCEEDS THE LIMIT, THEN SKIP THE RECORD 
					if (encounter.getDateEncounterEntered() != null)
						if (encounter.getDateEncounterEntered().after(limit))
						{
							System.out.println("Skipping: " + encounter);
							continue;
						}

					// Encounter results
					ArrayList<EncounterResults> results = new ArrayList<EncounterResults>();
					for (int i = 8; i < parts.length; i++)
					{
						EncounterResultsId erId = new EncounterResultsId(encounter.getId().getEncounterId(), encounter.getId().getPid1(), encounter
								.getId().getPid2(), header[i]);
						EncounterResults r = new EncounterResults(erId, parts[i]);
						results.add(r);
					}

					try
					{
						ServerServiceImpl impl = new ServerServiceImpl();
						if (!impl.exists("Encounter", "EncounterId='" + encounter.getId().getEncounterId() + "' and PID1='"
								+ encounter.getId().getPid1() + "' and PID2='" + encounter.getId().getPid2() + "'"))
						{
							HibernateUtil.util.save(encounter);
							HibernateUtil.util.bulkSave(results.toArray());
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				System.out.println(s + " Done! Wait, I'm panting...");
				Thread.sleep(10000);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}*/
	}

	@SuppressWarnings("deprecation")
	public static Date parseDate(String str)
	{
		try
		{
			String[] parts = str.split(" ");
			String[] dateParts = parts[0].split("/");
			int date, month, year, hour = 0, min = 0;
			date = Integer.parseInt(dateParts[0]);
			month = Integer.parseInt(dateParts[1]);
			year = Integer.parseInt(dateParts[2]);

			try
			{
				String[] timeParts = parts[1].split(":");
				hour = Integer.parseInt(timeParts[0]);
				min = Integer.parseInt(timeParts[1]);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			Date dt = new Date(year - 1900, month - 1, date, hour, min, 0);
			return dt;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static int findIndex(String[] array, String str)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i].equalsIgnoreCase(str))
				return i;
		return -1;
	}
}