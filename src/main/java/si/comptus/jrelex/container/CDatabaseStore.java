/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.comptus.jrelex.container;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 
 * @author tomaz
 */
public class CDatabaseStore implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1001L;
	private HashMap<String, CDatabase> databases;
	private CSettings appSettings;

	public CDatabaseStore() {
		this.databases = new HashMap<String, CDatabase>();
		this.setAppSettings(new CSettings());
	}

	public HashMap<String, CDatabase> getDatabases() {
		return databases;
	}

	public void setDatabases(HashMap<String, CDatabase> databases) {
		this.databases = databases;
	}

	public CSettings getAppSettings() {
		return appSettings;
	}

	public void setAppSettings(CSettings appSettings) {
		this.appSettings = appSettings;
	}
	
	

}
