/**
 * Interface implemented by all Report Forms 
 */

package com.ihsinformatics.xpertsmsweb.client;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public interface IReport {
	void clearUp();

	boolean validate();

	void viewData(boolean export);

	void setRights(String menuName);

	void onClick(ClickEvent event);
}
