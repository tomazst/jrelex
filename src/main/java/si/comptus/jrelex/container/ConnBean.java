/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.comptus.jrelex.container;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author tomaz
 */
public class ConnBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1007L;

	private String name = "";

	private String driver = "";

	private String hostname = "";

	private int port = 3306;

	private String username = "";

	private String password = "";

	private String database = "";

	private ArrayList<String> tables;

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ArrayList<String> getTables() {
		return tables;
	}

	public void setTables(ArrayList<String> tables) {
		this.tables = tables;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

}
