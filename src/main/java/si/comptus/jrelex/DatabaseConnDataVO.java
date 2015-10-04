/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.comptus.jrelex;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author tomaz
 */
public class DatabaseConnDataVO {

	private StringProperty name = new SimpleStringProperty("");
	private StringProperty driver = new SimpleStringProperty("");
	private StringProperty hostname = new SimpleStringProperty("");
	private IntegerProperty port = new SimpleIntegerProperty(3306);
	private StringProperty username = new SimpleStringProperty("");
	private StringProperty password = new SimpleStringProperty("");
	private StringProperty database = new SimpleStringProperty("");

	public DatabaseConnDataVO() {
	}

	public StringProperty getNameObj() {
		return name;
	}


	public String getName() {
		return name.get();
	}

	public void setName(String text) {
		name.set(text);
	}

	public StringProperty getDatabaseObj() {
		return database;
	}

	public String getDatabase() {
		return database.get();
	}

	public void setDatabase(String database) {
		this.database.set(database);
	}

	public StringProperty getDriverObj() {
		return driver;
	}

	public String getDriver() {
		return driver.get();
	}

	public void setDriver(String driver) {
		this.driver.set(driver);
	}

	public StringProperty getHostnameObj() {
		return hostname;
	}

	public String getHostname() {
		return hostname.get();
	}

	public void setHostname(String hostname) {
		this.hostname.set(hostname);
	}

	public IntegerProperty getPortObj() {
		return port;
	}

	public int getPort() {
		return port.get();
	}

	public void setPort(int port) {
		this.port.set(port);
	}

	public StringProperty getUsernameObj() {
		return username;
	}

	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public StringProperty getPasswordObj() {
		return password;
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

}
