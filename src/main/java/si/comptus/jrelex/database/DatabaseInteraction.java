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
package si.comptus.jrelex.database;

//import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.util.HashMap;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

//import org.apache.ddlutils.PlatformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.container.ConnBean;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import si.comptus.jrelex.Common;
import si.comptus.jrelex.DatabaseConnDataVO;
import si.comptus.jrelex.configuration.RDBMSType;

/**
 *
 * @author tomaz
 */
public class DatabaseInteraction {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInteraction.class);

    private HashMap<String, Connection> conections = null;

    public DatabaseInteraction() {
        this.conections = new HashMap<String, Connection>();
    }

    /**
     * Returns connection to database.
     *
     * @param connectionName
     * @return
     */
    public Connection getConnection(String connectionName) {
        if (this.conections.get(connectionName) == null) {
            DataSource dataSource = null;
            ConnBean bean = Common.getInstance().getDbstore().getDatabases()
                    .get(connectionName).getConnBean();
            try {
                dataSource = getDataSource(bean.getDriver(),
                        bean.getHostname(), bean.getPort(), bean.getUsername(),
                        bean.getPassword(), bean.getDatabase(),
                        bean.getOrclSID(), bean.getOrclDriver());
                this.conections.put(connectionName, dataSource.getConnection());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialogBuilder.error(e).show(null);
            }
        }
        return this.conections.get(connectionName);
    }

    public void closeConnections() throws SQLException {
        for (Map.Entry<String, Connection> entry : this.conections.entrySet()) {
            entry.getValue().close();
        }
    }

    /**
     * Generates the data source for database.
     *
     * @param driver
     * @param servername
     * @param port
     * @param user
     * @param password
     * @return
     * @throws Exception
     */
    public DataSource getDataSource(RDBMSType driver, String servername, int port,
            String user, String password, String orclDriver) throws SQLException {
        return this.getDataSource(driver, servername, port, user, password, orclDriver, null, null);
    }

    /**
     * Generates the data source for database.
     *
     * @param driver
     * @param servername
     * @param port
     * @param user
     * @param password
     * @param database
     * @return
     * @throws Exception
     */
    public DataSource getDataSource(RDBMSType driver, String servername, int port,
            String user, String password, String database,
            String orclSID, String orclDriver) throws SQLException {

        switch (driver) {
            case MSSQL:
                return (DataSource) this.getMSSqlDataSource(servername, port,
                        user, password, database);
            case MYSQL:
                return (DataSource) this.getMySqlDataSource(servername, port,
                        user, password, database);
            case ORACLE:
                return (DataSource) this.getOraclelDataSource(servername, port,
                        user, password, database, orclSID, orclDriver);
            default:
                return null;
        }

    }

    private MysqlDataSource getMySqlDataSource(String servername, int port,
            String user, String password, String database) throws SQLException {

        // Setting up the DataSource object
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(servername);
        ds.setPortNumber(port);
        if (database != null && !database.isEmpty()) {
            ds.setDatabaseName(database);
        }
        ds.setUser(user);
        ds.setPassword(password);

        return ds;

    }

    private SQLServerDataSource getMSSqlDataSource(String servername, int port,
            String user, String password, String database) throws SQLException {

        // Setting up the DataSource object
        SQLServerDataSource ds = new SQLServerDataSource();

        ds.setServerName(servername);
        ds.setPortNumber(port);
        if (database != null && !database.isEmpty()) {
            ds.setDatabaseName(database);
        }
        ds.setUser(user);
        ds.setPassword(password);

        return ds;
    }

    private OracleDataSource getOraclelDataSource(String servername, int port,
            String user, String password, String database,
            String orclSID, String orclDriver) throws SQLException {

        // Setting up the DataSource object
        OracleDataSource ds = new OracleDataSource();

        ds.setDriverType(orclDriver);
        ds.setServerName(servername);
        ds.setPortNumber(port);
        ds.setDatabaseName(orclSID);
        ds.setUser(user);
        ds.setPassword(password);

        return ds;
    }

    public List<String> getDatabaseList(DatabaseConnDataVO connVO) throws SQLException {
        List<String> databaseList = new ArrayList<>();

        DataSource ds = getDataSource(
                RDBMSType.valueOf(connVO.getDriver()),
                connVO.getHostname(),
                connVO.getPort(),
                connVO.getUsername(),
                connVO.getPassword(),
                connVO.getOrclDriver()
        );

        if (connVO.getDriver().equalsIgnoreCase("oracle")) {
            try(ResultSet dbs = ds.getConnection().getMetaData().getSchemas()){
                while (dbs.next()) {
                    databaseList.add(dbs.getString(1));
                }
            }
        } else {
        	Connection c = ds.getConnection();
            try(ResultSet dbs =c.getMetaData().getCatalogs()){
                while (dbs.next()) {
                    databaseList.add(dbs.getString(1));
                }
            }
        }
        return databaseList;
    }

}
