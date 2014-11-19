/**
 * Various utility functions for swing controls
 */

package com.ihsinformatics.xpertsms.util;

import java.io.File;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.text.JTextComponent;

/**
 * @author owais.hussain@irdresearch.org
 *
 */
public final class SwingUtil
{
	public static void main(String[] args)
	{
		JComboBox dateFormatComboBox = new javax.swing.JComboBox ();
		dateFormatComboBox.setModel (new javax.swing.DefaultComboBoxModel (new String[] {"yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd", "MM/dd/yyyy hh:mm:ssa", "MM/dd/yyyy",
				"M/d/yy", "dd/MM/yyyy hh:mm:ssa", "dd/MM/yyyy", "d/M/yy", "EEE, d MMM yyyy HH:mm:ss", "yyyyMMddhhmmss"}));
		System.out.println(getIndex (dateFormatComboBox, "M/d/yy"));
		System.out.println(getIndex (dateFormatComboBox, "dd/MM/yyyy"));
		System.out.println(getIndex (dateFormatComboBox, "d/m/Yy"));
	}
	
	/**
	 * Returns selected text/item/value for a JComponent, if the component type
	 * is other than Text, ComboBox or List, then returns the name of component
	 * 
	 * @param control
	 * @return
	 */
 	public static String get (JComponent control)
	{
		if (control == null)
			return null;
		if (control instanceof JTextComponent)
			return ((JTextComponent) control).getText ();
		else if (control instanceof JSpinner)
			return ((JTextComponent) control).getSelectedText ();
		else if (control instanceof JComboBox)
			return ((JComboBox) control).getSelectedItem ().toString ();
		else if (control instanceof JList)
			return ((JList) control).getSelectedValue ().toString ();
		else
			return control.getName ();
	}

	public static int getIndex (JComponent control, Object value)
	{
		if (control instanceof JComboBox)
		{
			ComboBoxModel model = ((JComboBox) control).getModel ();
			for (int i = 0; i < model.getSize (); i++)
				if (model.getElementAt (i).equals (value))
					return i;
		}
		else if (control instanceof JList)
		{
			ListModel model = ((JList) control).getModel ();
			for (int i = 0; i < model.getSize (); i++)
				if (model.getElementAt (i).equals (value))
					return i;
		}
		return -1;
	}
	
	/**
	 * Displays a file chooser and returns the selected file/folder
	 * @param directory
	 * 			set True to fetch a directory
	 * @return
	 */
	public static File chooseFile(String title, boolean directory)
	{
		JFileChooser chooser = new JFileChooser ();
		chooser.setCurrentDirectory (new File ("."));
		chooser.setDialogTitle (title);
		if (directory)
		{
			chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed (true);
		}
		if (chooser.showOpenDialog (null) == JFileChooser.APPROVE_OPTION)
		{
			return chooser.getSelectedFile ();
		}
		return null;
	}
	
	/**
	 * Get concatenated string from multiple items selected in a list, separated
	 * by separator
	 * 
	 * @param list
	 * @param separator
	 * @return
	 */
	public static String concatenatedItems (JList list, char separator)
	{
		if (list == null)
			return null;
		StringBuilder sb = new StringBuilder ();
		int[] indices = list.getSelectedIndices ();
		ListModel model = list.getModel ();
		for (int i : indices)
			sb.append (model.getElementAt (i).toString () + separator);
		return sb.toString ();
	}
}
