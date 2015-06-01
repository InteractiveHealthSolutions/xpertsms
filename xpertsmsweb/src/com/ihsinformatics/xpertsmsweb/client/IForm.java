/**
 * Interface implemented by all Forms 
 */

package com.ihsinformatics.xpertsmsweb.client;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public interface IForm {
	void clearUp();

	boolean validate();

	void saveData();

	void updateData();

	void deleteData();

	void fillData();

	void setRights(String menuName);

	void onClick(ClickEvent event);
}
