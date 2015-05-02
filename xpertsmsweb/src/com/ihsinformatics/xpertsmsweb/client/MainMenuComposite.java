/**
 * Main Menu Composite for TB REACH client
 */

package com.ihsinformatics.xpertsmsweb.client;

import java.util.Date;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ihsinformatics.xpertsmsweb.shared.CustomMessage;
import com.ihsinformatics.xpertsmsweb.shared.ErrorType;

public class MainMenuComposite extends Composite {
    private static VerticalPanel mainVerticalPanel;

    private MenuBar mainMenuBar = new MenuBar(false);
    private MenuBar setupMenuBar = new MenuBar(true);
    private MenuBar formsMenuBar = new MenuBar(true);
    private MenuBar reportingMenuBar = new MenuBar(true);
    private MenuBar helpMenuBar = new MenuBar(true);

    private MenuItem setupMenuItem = new MenuItem("Setup", false, setupMenuBar);
    private MenuItem formsMenuItem = new MenuItem("Forms", false, formsMenuBar);
    private MenuItem reportingMenuItem = new MenuItem("Reporting", false,
	    reportingMenuBar);
    private MenuItem helpMenuItem = new MenuItem("Help", false, helpMenuBar);

    private MenuItem locationsMenuItem = new MenuItem("Locations", false,
	    (Command) null);
    private MenuItem usersMenuItem = new MenuItem("Users", false,
	    (Command) null);
    private MenuItem userRightsMenuItem = new MenuItem("User Rights", false,
	    (Command) null);
    private MenuItem messageSettingsMenuItem = new MenuItem("Message Settings",
	    false, (Command) null);

    private MenuItem encounterMenuItem = new MenuItem("Encounters", false,
	    (Command) null);
    private MenuItem reportsMenuItem = new MenuItem("Reports", false,
	    (Command) null);
    private MenuItem logsMenuItem = new MenuItem("Logs", false, (Command) null);
    // private MenuItem smsMenuItem = new MenuItem("SMS", false, (Command)
    // null);

    private MenuItem aboutMenuItem = new MenuItem("About Us", false,
	    (Command) null);
    private MenuItem aboutMeMenuItem = new MenuItem("About Me", false,
	    (Command) null);
    private MenuItem helpContentsMenuItem = new MenuItem("Help Contents",
	    false, (Command) null);
    private MenuItem logoutMenuItem = new MenuItem("Logout", false,
	    (Command) null);

    private MenuItem sputumReceivingMenuItem = new MenuItem("Sputum Receiving",
	    false, (Command) null);
    private MenuItem geneXpertResultsMenuItem = new MenuItem(
	    "Gene Xpert Results", false, (Command) null);

    private MenuItem AsuspectIdentificationMenuItem = new MenuItem(
	    "Suspect Identification", false, (Command) null);
    private MenuItem DPatientInformationMenuItem = new MenuItem(
	    "Patient Information", false, (Command) null);

    @SuppressWarnings("deprecation")
    public MainMenuComposite() {
	VerticalPanel topVerticalPanel = new VerticalPanel();
	initWidget(topVerticalPanel);
	mainVerticalPanel = new VerticalPanel();
	mainVerticalPanel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	mainVerticalPanel.setSize("100%", "100%");
	topVerticalPanel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
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
	/*
	 * mobileFormsMenuBar.setAutoOpen(true);
	 * mobileFormsMenuBar.setAnimationEnabled(true);
	 */
	reportingMenuBar.setAutoOpen(true);
	reportingMenuBar.setAnimationEnabled(true);
	helpMenuBar.setAutoOpen(true);
	helpMenuBar.setAnimationEnabled(true);

	locationsMenuItem.setCommand(new Command() {
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "SETUP");
		mainVerticalPanel.add(new LocationComposite().asWidget());
	    }
	});

	messageSettingsMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "SETUP");
		mainVerticalPanel.add(new MessageSettingsComposite().asWidget());
	    }
	});
	usersMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "USERS");
		mainVerticalPanel.add(new UsersComposite().asWidget());
	    }
	});
	userRightsMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "USERS");
		mainVerticalPanel.add(new UserRightsComposite().asWidget());
	    }
	});

	geneXpertResultsMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "LABORATORY");
		mainVerticalPanel.add(new GeneXpertResultsComposite()
			.asWidget());
	    }
	});

	encounterMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "ENCOUNTER");
		mainVerticalPanel.add(new EncounterComposite().asWidget());
	    }
	});

	reportsMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "DATALOG");
		mainVerticalPanel.add(new Report_ReportsComposite().asWidget());
	    }
	});
	logsMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "DATALOG");
		mainVerticalPanel.add(new Report_LogComposite().asWidget());
	    }
	});

	aboutMeMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		try {
		    String user = Cookies.getCookie("UserName");
		    Date loginDate = new Date(Long.parseLong(Cookies
			    .getCookie("LoginTime")));
		    int mins = new Date(new Date().getTime()
			    - loginDate.getTime()).getMinutes();
		    String str = "CURRENT USER: " + user + "\n"
			    + "LOGIN TIME: "
			    + loginDate.toGMTString().replace("GMT", "") + "\n"
			    + "CURRENT SESSION: " + mins + " mins";
		    Window.alert(str);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
	aboutMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		clear();
		Cookies.setCookie("CurrentMenu", "About Us");
		mainVerticalPanel.add(new AboutUsComposite().asWidget());
	    }
	});

	helpContentsMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		Cookies.setCookie("CurrentMenu", "Help Contents");
		Window.alert(CustomMessage
			.getErrorMessage(ErrorType.ITEM_NOT_FOUND));
	    }
	});
	logoutMenuItem.setCommand(new Command() {
	    @Override
	    public void execute() {
		XpertSmsWeb.logout();
	    }
	});

	mainMenuBar.addItem(setupMenuItem);
	mainMenuBar.addItem(formsMenuItem);
	mainMenuBar.addItem(reportingMenuItem);
	mainMenuBar.addItem(helpMenuItem);
	mainMenuBar.addItem(logoutMenuItem);

	setupMenuBar.addItem(usersMenuItem);
	setupMenuBar.addItem(userRightsMenuItem);
	setupMenuBar.addItem(locationsMenuItem);
	setupMenuBar.addItem(messageSettingsMenuItem);

	formsMenuBar.addItem(AsuspectIdentificationMenuItem);
	formsMenuBar.addItem(DPatientInformationMenuItem);
	formsMenuBar.addItem(sputumReceivingMenuItem);
	formsMenuBar.addItem(geneXpertResultsMenuItem);
	formsMenuBar.addItem(encounterMenuItem);

	reportingMenuBar.addItem(reportsMenuItem);
	reportingMenuBar.addItem(logsMenuItem);

	helpMenuBar.addItem(aboutMeMenuItem);
	helpMenuBar.addItem(aboutMenuItem);
	helpMenuBar.addItem(helpContentsMenuItem);
    }

    public static void clear() {
	Cookies.setCookie("CurrentMenu", "");
	mainVerticalPanel.clear();
    }
}
