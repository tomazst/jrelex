////////////////////////////////////////////////////////////////////////////////////////////////////
// JRelEx: Java application is intended for searching data using database relations.
// Copyright (C) 2015 tomazst <tomaz.stefancic@gmail.com>.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
////////////////////////////////////////////////////////////////////////////////////////////////////

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
    private StringProperty orclSID = new SimpleStringProperty("");
    private StringProperty orclDriver = new SimpleStringProperty("");

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

    public String getOrclSID() {
        return orclSID.get();
    }

    public void setOrclSID(String orclSID) {
        this.orclSID.set(orclSID);
    }

    public String getOrclDriver() {
        return orclDriver.get();
    }

    public void setOrclDriver(String orclDriver) {
        this.orclDriver.set(orclDriver);
    }

}
