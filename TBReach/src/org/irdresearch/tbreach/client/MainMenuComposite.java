/**
 * Main Menu Composite for TB REACH client
 */

package org.irdresearch.tbreach.client;

import java.util.Date;

import org.irdresearch.tbreach.shared.CustomMessage;
import org.irdresearch.tbreach.shared.ErrorType;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MainMenuComposite extends Composite
{
	private static VerticalPanel		mainVerticalPanel;

	private MenuBar						mainMenuBar							= new MenuBar(false);
	private MenuBar						setupMenuBar						= new MenuBar(true);
	private MenuBar						formsMenuBar						= new MenuBar(true);
	//private MenuBar						mobileFormsMenuBar					= new MenuBar(true);
	private MenuBar						reportingMenuBar					= new MenuBar(true);
	private MenuBar						helpMenuBar							= new MenuBar(true);

	private MenuItem					setupMenuItem						= new MenuItem("Setup", false, setupMenuBar);
	private MenuItem					formsMenuItem						= new MenuItem("Forms", false, formsMenuBar);
	//private MenuItem					mobileFormsMenuItem					= new MenuItem("Mobile Forms", false, mobileFormsMenuBar);
	private MenuItem					reportingMenuItem					= new MenuItem("Reporting", false, reportingMenuBar);
	private MenuItem					helpMenuItem						= new MenuItem("Help", false, helpMenuBar);

	private MenuItem					chwMenuItem							= new MenuItem("Screener", false, (Command) null);
	//private MenuItem					setupIncentiveMenuItem				= new MenuItem("Incentive Setup", false, (Command) null);
	private MenuItem					locationsMenuItem					= new MenuItem("Locations", false, (Command) null);
	private MenuItem					gpMenuItem							= new MenuItem("Provider", false, (Command) null);
	//private MenuItem					gpMonitorMappingMenuItem			= new MenuItem("Map GP-Monitor", false, (Command) null);
	private MenuItem					usersMenuItem						= new MenuItem("Users", false, (Command) null);
	private MenuItem					userRightsMenuItem					= new MenuItem("User Rights", false, (Command) null);
	private MenuItem					messageSettingsMenuItem				= new MenuItem("Message Settings", false, (Command) null);
	
	//private MenuItem					incentiveMenuItem					= new MenuItem("Incentives", false, (Command) null);
	//private MenuItem					incentiveAccountsMenuItem			= new MenuItem("Incentive Accounts", false, (Command) null);
	private MenuItem					sputumResultsMenuItem				= new MenuItem("Sputum Results", false, (Command) null);
	private MenuItem					encounterMenuItem					= new MenuItem("Encounters", false, (Command) null);
	private MenuItem					reportsMenuItem						= new MenuItem("Reports", false, (Command) null);
	private MenuItem					logsMenuItem						= new MenuItem("Logs", false, (Command) null);
	private MenuItem					smsMenuItem							= new MenuItem("SMS", false, (Command) null);

	private MenuItem					aboutMenuItem						= new MenuItem("About Us", false, (Command) null);
	private MenuItem					aboutMeMenuItem						= new MenuItem("About Me", false, (Command) null);
	private MenuItem					helpContentsMenuItem				= new MenuItem("Help Contents", false, (Command) null);
	//private MenuItem					feedbackMenuItem					= new MenuItem("Feedback", false, (Command) null);
	private MenuItem					logoutMenuItem						= new MenuItem("Logout", false, (Command) null);

	
	//private MenuItem					householdInformationMenuItem		= new MenuItem("Household Information", false, (Command) null);
	//private MenuItem					MRNoAssignmentMenuItem				= new MenuItem("MR No. Assignment", false, (Command) null);
	private MenuItem					sputumReceivingMenuItem				= new MenuItem("Sputum Receiving", false, (Command) null);
	//private MenuItem					sputumReceivingForContactsMenuItem	= new MenuItem("Sputum Receiving for TB-Contacts", false, (Command) null);
	private MenuItem					geneXpertResultsMenuItem			= new MenuItem("Gene Xpert Results", false, (Command) null);
	private MenuItem					xrayResultsMenuItem					= new MenuItem("X-Ray Results", false, (Command) null);
	//private MenuItem					relationshipsMenuItem				= new MenuItem("Relationships", false, (Command) null);

	private MenuItem					AsuspectIdentificationMenuItem		= new MenuItem("Suspect Identification", false, (Command) null);
	//private MenuItem					BSuspectConfirmationMenuItem		= new MenuItem("B: Suspect Confirmation", false, (Command) null);
	//private MenuItem					CSuspectVerificationMenuItem		= new MenuItem("C: Suspect Verification", false, (Command) null);
	private MenuItem					DPatientInformationMenuItem			= new MenuItem("Patient Information", false, (Command) null);
	//private MenuItem					DPatientTBInformationMenuItem		= new MenuItem("D2: Patient's TB Information", false, (Command) null);
	//private MenuItem					DPatientGPSInformationMenuItem		= new MenuItem("D3: Patient's GPS Information", false, (Command) null);
	//private MenuItem					ERefusalMenuItem					= new MenuItem("E: Refusal Form", false, (Command) null);
	private MenuItem					FSputumCollectionMenuItem			= new MenuItem("Sputum Collection", false, (Command) null);
	private MenuItem					IBaselineTreatmentMenuItem			= new MenuItem("Baseline Treatment", false, (Command) null);
	//private MenuItem					JFollowupTreatmentMenuItem			= new MenuItem("J: Follow-up Treatment", false, (Command) null);
	//private MenuItem					KDrugAdministrationMenuItem			= new MenuItem("K: Drug Administration", false, (Command) null);
	//private MenuItem					LEndFollowupMenuItem				= new MenuItem("L: End of Follow-up", false, (Command) null);
	private MenuItem					MXRaySubmissionMenuItem				= new MenuItem("X-Ray Submission", false, (Command) null);
	//private MenuItem					NReferralMenuItem					= new MenuItem("N: Referral", false, (Command) null);
	//private MenuItem					OPediatricDiagnosisMenuItem			= new MenuItem("O: Pediatric Clinical Diagnosis", false, (Command) null);
	//private MenuItem					PPediatricConfirmationMenuItem		= new MenuItem("P: Pediatric Confirmation", false, (Command) null);
	//private MenuItem					QAdultDiagnosisMenuItem				= new MenuItem("Q: Adult Clinical Diagnosis", false, (Command) null);

	@SuppressWarnings("deprecation")
	public MainMenuComposite()
	{
		VerticalPanel topVerticalPanel = new VerticalPanel();
		initWidget(topVerticalPanel);
		mainVerticalPanel = new VerticalPanel();
		mainVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainVerticalPanel.setSize("100%", "100%");
		topVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		topVerticalPanel.add(mainMenuBar);
		topVerticalPanel.setSize("400px", "100%");
		topVerticalPanel.add(mainVerticalPanel);
		mainMenuBar.setSize("100%", "100%");
		mainMenuBar.setAutoOpen(true);
		mainMenuBar.setAnimationEnabled(true);
		setupMenuBar.setAutoOpen(true);
		setupMenuBar.setAnimationEnabled(true);
		formsMenuBar.setAutoOpen(true);
		formsMenuBar.setAnimationEnabled(true);
		/*mobileFormsMenuBar.setAutoOpen(true);
		mobileFormsMenuBar.setAnimationEnabled(true);*/
		reportingMenuBar.setAutoOpen(true);
		reportingMenuBar.setAnimationEnabled(true);
		helpMenuBar.setAutoOpen(true);
		helpMenuBar.setAnimationEnabled(true);
		chwMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "CHW");
				mainVerticalPanel.add(new WorkerComposite().asWidget());
			}
		});
		
		gpMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "GP");
				mainVerticalPanel.add(new GPComposite().asWidget());
			}
		});
		
		locationsMenuItem.setCommand (new Command()
		{
			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "SETUP");
				mainVerticalPanel.add (new LocationComposite ().asWidget ());
			}
		});
		
		messageSettingsMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "SETUP");
				mainVerticalPanel.add(new MessageSettingsComposite().asWidget());
			}
		});
		usersMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "USERS");
				mainVerticalPanel.add(new UsersComposite().asWidget());
			}
		});
		userRightsMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "USERS");
				mainVerticalPanel.add(new UserRightsComposite().asWidget());
			}
		});
		
		
		sputumReceivingMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "LABORATORY");
				mainVerticalPanel.add(new _G_SputumReceivingComposite().asWidget());
			}
		});
		
		sputumResultsMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "LABORATORY");
				mainVerticalPanel.add(new SputumResultsComposite().asWidget());
			}
		});
		geneXpertResultsMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "LABORATORY");
				mainVerticalPanel.add(new GeneXpertResultsComposite().asWidget());
			}
		});
		xrayResultsMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "LABORATORY");
				mainVerticalPanel.add(new XrayResultsComposite().asWidget());
			}
		});
		
		encounterMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "ENCOUNTER");
				mainVerticalPanel.add(new EncounterComposite().asWidget());
			}
		});
		AsuspectIdentificationMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "FORM_A");
				mainVerticalPanel.add(new _A_SuspectIDComposite().asWidget());
			}
		});
		
		DPatientInformationMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "FORM_D1");
				mainVerticalPanel.add(new _D1_PatientInfoComposite().asWidget());
			}
		});
		
		FSputumCollectionMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "FORM_F");
				mainVerticalPanel.add(new _F_SputumCollectionComposite().asWidget());
			}
		});
		IBaselineTreatmentMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "FORM_I");
				mainVerticalPanel.add(new _I_BaselineTreatmentComposite().asWidget());
			}
		});
		
		MXRaySubmissionMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "FORM_M");
				mainVerticalPanel.add(new _M_XRaySubmissionComposite().asWidget());
			}
		});
		
		
		reportsMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "DATALOG");
				// mainVerticalPanel.add(new Report_CaseDetectionReportsComposite().asWidget());
				// mainVerticalPanel.add(new Report_CaseHoldingReportsComposite().asWidget());
				mainVerticalPanel.add(new Report_ReportsComposite().asWidget());
			}
		});
		logsMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "DATALOG");
				mainVerticalPanel.add(new Report_LogComposite().asWidget());
			}
		});
		
		aboutMeMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				try
				{
					String user = Cookies.getCookie("UserName");
					Date loginDate = new Date(Long.parseLong(Cookies.getCookie("LoginTime")));
					int mins = new Date(new Date().getTime() - loginDate.getTime()).getMinutes();
					String str = "CURRENT USER: " + user + "\n" + "LOGIN TIME: " + loginDate.toGMTString().replace("GMT", "") + "\n"
							+ "CURRENT SESSION: " + mins + " mins";
					Window.alert(str);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		aboutMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				clear();
				Cookies.setCookie("CurrentMenu", "About Us");
				mainVerticalPanel.add(new AboutUsComposite().asWidget());
			}
		});
		
		helpContentsMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				Cookies.setCookie("CurrentMenu", "Help Contents");
				Window.alert(CustomMessage.getErrorMessage(ErrorType.ITEM_NOT_FOUND));
			}
		});
		logoutMenuItem.setCommand(new Command()
		{
			@Override
			public void execute()
			{
				TBReach.logout();
			}
		});

		mainMenuBar.addItem(setupMenuItem);
		mainMenuBar.addItem(formsMenuItem);
		mainMenuBar.addItem(reportingMenuItem);
		mainMenuBar.addItem(helpMenuItem);
		mainMenuBar.addItem(logoutMenuItem);
		
		setupMenuBar.addItem(chwMenuItem);
		setupMenuBar.addItem(gpMenuItem);
		setupMenuBar.addItem(usersMenuItem);
		setupMenuBar.addItem(userRightsMenuItem);
		setupMenuBar.addItem (locationsMenuItem);
		setupMenuBar.addItem(messageSettingsMenuItem);
		
		formsMenuBar.addItem(AsuspectIdentificationMenuItem);
		formsMenuBar.addItem(DPatientInformationMenuItem);
		formsMenuBar.addItem(FSputumCollectionMenuItem);
		formsMenuBar.addItem(sputumReceivingMenuItem);
		formsMenuBar.addItem(sputumResultsMenuItem);
		formsMenuBar.addItem(geneXpertResultsMenuItem);
		formsMenuBar.addItem(MXRaySubmissionMenuItem);
		formsMenuBar.addItem(xrayResultsMenuItem);
		formsMenuBar.addItem(IBaselineTreatmentMenuItem);
		formsMenuBar.addItem(encounterMenuItem);
		
		//mainMenuBar.addItem(mobileFormsMenuItem);
		//setupMenuBar.addItem(setupIncentiveMenuItem);
		//setupMenuBar.addItem(locationsMenuItem);
		//setupMenuBar.addItem(gpMonitorMappingMenuItem);
		//formsMenuBar.addItem(patientInformationMenuItem);
		//formsMenuBar.addItem(householdInformationMenuItem);
		//formsMenuBar.addItem(MRNoAssignmentMenuItem);
		//formsMenuBar.addItem(sputumReceivingForContactsMenuItem);
		//formsMenuBar.addItem(relationshipsMenuItem);
		//formsMenuBar.addItem(incentiveMenuItem);
		//formsMenuBar.addItem(incentiveAccountsMenuItem);
		//mobileFormsMenuBar.addItem(BSuspectConfirmationMenuItem);
		//mobileFormsMenuBar.addItem(CSuspectVerificationMenuItem);
		//mobileFormsMenuBar.addItem(DPatientTBInformationMenuItem);
		//.addItem(DPatientGPSInformationMenuItem);
		//mobileFormsMenuBar.addItem(ERefusalMenuItem);
		//mobileFormsMenuBar.addItem(JFollowupTreatmentMenuItem);
		//mobileFormsMenuBar.addItem(KDrugAdministrationMenuItem);
		//mobileFormsMenuBar.addItem(LEndFollowupMenuItem);
		//mobileFormsMenuBar.addItem(NReferralMenuItem);
		//mobileFormsMenuBar.addItem(OPediatricDiagnosisMenuItem);
		//mobileFormsMenuBar.addItem(PPediatricConfirmationMenuItem);
		//mobileFormsMenuBar.addItem(QAdultDiagnosisMenuItem);

		reportingMenuBar.addItem(reportsMenuItem);
		reportingMenuBar.addItem(logsMenuItem);
		//reportingMenuBar.addItem(smsMenuItem);

		helpMenuBar.addItem(aboutMeMenuItem);
		helpMenuBar.addItem(aboutMenuItem);
		//helpMenuBar.addItem(feedbackMenuItem);
		helpMenuBar.addItem(helpContentsMenuItem);
	}

	public static void clear()
	{
		Cookies.setCookie("CurrentMenu", "");
		mainVerticalPanel.clear();
	}
}
