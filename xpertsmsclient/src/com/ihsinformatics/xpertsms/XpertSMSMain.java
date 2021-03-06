/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package com.ihsinformatics.xpertsms;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SplashScreen;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.github.zafarkhaja.semver.Version;
import com.ihsinformatics.xpertsms.ui.ControlPanel;

public class XpertSMSMain {
	
	public static Version version;
	
	private static ControlPanel settingsPanel;
	
	private static final boolean developmentMode = true;
	
	// private static JTextPane monitorPanel;
	
	private static void createAndShowGUI() {
		JFrame mainFrame = new JFrame("XpertSMS");
		
		// Show splash screen in live mode
		if (!developmentMode) {
			final SplashScreen splash = SplashScreen.getSplashScreen();
			if (splash == null) {
				System.out.println("SplashScreen.getSplashScreen() returned null");
				return;
			}
			Graphics2D g = splash.createGraphics();
			if (g == null) {
				System.out.println("g is null");
				return;
			}
			for (int i = 0; i < 100; i++) {
				renderSplashFrame(g, i);
				splash.update();
				try {
					Thread.sleep(10);
				}
				catch (InterruptedException e) {}
			}
			splash.close();
		}
		
		// set Frame size and location
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setLocation(d.width / 2 - 400, d.height / 2 - 300);
		mainFrame.setSize(800, 600);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// set frame's layout
		Container mainPane = mainFrame.getContentPane();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		mainPane.setLayout(gridbag);
		
		// add button panel
		// c.gridwidth = GridBagConstraints.REMAINDER; //last
		// c.anchor = GridBagConstraints.PAGE_START; c.weightx = 0.5; c.weighty
		// = 0.5;
		// c.gridx = 0; c.gridy = 0; //
		// c.fill = GridBagConstraints.BOTH;
		// oldControlPanel = new OldControlPanel(); ///
		// mainPane.add(oldControlPanel, c);
		
		// add settings panel
		// monitorPanel = new JTextPane();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		
		c.gridx = 1;
		c.gridy = 1;
		settingsPanel = new ControlPanel();
		mainPane.add(settingsPanel, c);
		
		// add text panel
		
		/*
		 * monitorPanel.setEditable(false); monitorPanel.setPreferredSize(new
		 * Dimension(300,300)); c.gridx = 1; c.gridy = 0; //c.fill =
		 * GridBagConstraints.BOTH; mainFrame.add(new JScrollPane( monitorPanel
		 * ), c);
		 */
		
		// monitorFrame.setResizable(true);
		// monitorFrame.pack();
		// monitorFrame.setVisible(true);
		mainFrame.pack();
		mainFrame.setResizable(false);
		mainFrame.setVisible(true);
	}
	
	static void renderSplashFrame(Graphics2D g, int frame) {
		final String[] comps = { "1", "2", "3" };
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(120, 140, 200, 40);
		g.setPaintMode();
		g.setColor(Color.BLACK);
		g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 120, 150);
	}
	
	/**
	 * @return the settingsPanel
	 */
	public ControlPanel getSettingsPanel() {
		return settingsPanel;
	}
	
	public static void main(String[] args) {
		XpertProperties.readProperties();
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
}
