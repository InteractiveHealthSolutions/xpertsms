/**
 * Pop up to display some information about Project 
 */

package com.ihsinformatics.xpertsmsweb.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea;

public class AboutUsComposite extends PopupPanel {
    FlexTable flexTable = new FlexTable();
    RichTextArea richTextArea = new RichTextArea();
    Image ihsLogoImage = new Image("images/ihsLogo.png");
    Image irdLogoImage = new Image("images/irdLogo.jpg");
    Button closeButton = new Button("Close");

    public AboutUsComposite() {
	super(true);
	setStyleName("body");
	setSize("350px", "138px");
	setPreviewingAllNativeEvents(true);
	setAnimationEnabled(true);
	setGlassEnabled(true);
	generateComposite();
	setProjectProperties();
    }

    private void generateComposite() {
	setWidget(flexTable);
	flexTable.setSize("338px", "126px");
	FlexTable topFlexTable = new FlexTable();
	flexTable.setWidget(0, 0, topFlexTable);
	topFlexTable.setSize("100%", "100%");

	HorizontalPanel horizontalPanel = new HorizontalPanel();
	topFlexTable.setWidget(0, 0, horizontalPanel);

	Image XpertSmsLogoImage = new Image((String) null);
	horizontalPanel.add(XpertSmsLogoImage);

	HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
	topFlexTable.setWidget(0, 1, horizontalPanel_1);

	horizontalPanel_1.add(richTextArea);

	FlexTable middleFlexTable = new FlexTable();
	flexTable.setWidget(1, 0, middleFlexTable);
	middleFlexTable.setSize("100%", "100%");

	middleFlexTable.setWidget(0, 0, ihsLogoImage);
	ihsLogoImage.setSize("50%", "50%");

	middleFlexTable.setWidget(0, 1, irdLogoImage);
	irdLogoImage.setSize("50%", "50%");
	middleFlexTable.getRowFormatter().setVerticalAlign(0,
		HasVerticalAlignment.ALIGN_MIDDLE);
	middleFlexTable.getCellFormatter().setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	middleFlexTable.getCellFormatter().setHorizontalAlignment(0, 1,
		HasHorizontalAlignment.ALIGN_CENTER);

	FlexTable bottomFlexTable = new FlexTable();
	flexTable.setWidget(2, 0, bottomFlexTable);
	bottomFlexTable.setSize("100%", "100%");
	closeButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		MainMenuComposite.clear();
	    }
	});

	bottomFlexTable.setWidget(0, 0, closeButton);
	bottomFlexTable.getRowFormatter().setVerticalAlign(0,
		HasVerticalAlignment.ALIGN_MIDDLE);
	bottomFlexTable.getCellFormatter().setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	bottomFlexTable.getCellFormatter().setVerticalAlignment(0, 0,
		HasVerticalAlignment.ALIGN_MIDDLE);
	flexTable.getRowFormatter().setVerticalAlign(2,
		HasVerticalAlignment.ALIGN_MIDDLE);
	flexTable.getCellFormatter().setHorizontalAlignment(2, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.getCellFormatter().setHorizontalAlignment(1, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	flexTable.getCellFormatter().setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
    }

    private void setProjectProperties() {
	StringBuilder about = new StringBuilder();
	richTextArea.setTitle("About XpertSMS");
	about.append("Project: XpertSMS aims to transport GeneXpert results through SMS as well as HTTP(s)");
	about.append("\n");
	about.append("Developed by: Interactive Health Solutions Pvt. Ltd. Pakistan");
	about.append("\n");
	about.append("Contributors: Ali Habib, Rabbia Hassan, Owais Hussain");
	about.append("");
	richTextArea.setText(about.toString());
    }
}
