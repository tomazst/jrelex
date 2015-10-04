/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.comptus.jrelex.container;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 
 * @author tomazst
 */
public class CDatabase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1002L;
	private String name;
	private HashMap<String, CTable> tables;
	private ConnBean connBean;
	private boolean visible=true;

	public CDatabase() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, CTable> getTables() {
		return tables;
	}

	public void setTables(HashMap<String, CTable> tables) {
		this.tables = tables;
	}

	public ConnBean getConnBean() {
		return connBean;
	}

	public void setConnBean(ConnBean connBean) {
		this.connBean = connBean;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
