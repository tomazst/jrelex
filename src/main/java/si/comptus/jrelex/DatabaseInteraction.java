/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.comptus.jrelex;

//import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.util.HashMap;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

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
		// JOptionPane.showMessageDialog(null,
		// "Odpira oz išče povezavo na: "+connectionName);
		if (this.conections.get(connectionName) == null) {
			DataSource dataSource = null;
			ConnBean bean = Common.getInstance().getDbstore().getDatabases()
					.get(connectionName).getConnBean();
			try {
				dataSource = getDataSource(bean.getDriver(),
						bean.getHostname(), bean.getPort(), bean.getUsername(),
						bean.getPassword(), bean.getDatabase());
				this.conections.put(connectionName, dataSource.getConnection());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				MessageDialogBuilder.error(e).show(null);
			}
		}
		return this.conections.get(connectionName);
	}
        
        public void closeConnections() throws SQLException {
            for(Map.Entry<String, Connection> entry : this.conections.entrySet()){
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
	 * @param database
	 * @return
	 * @throws Exception
	 */
	public DataSource getDataSource(String driver, String servername, int port,
			String user, String password, String database) throws Exception {

		if (driver.equalsIgnoreCase("mysql")) {
			return (DataSource) this.getMySqlDataSource(servername, port,
					user, password, database);
		}

		if (driver.equalsIgnoreCase("mssql")) {
			return (DataSource) this.getMSSqlDataSource(servername, port,
					user, password, database);
		}
		
		if (driver.equalsIgnoreCase("oracle")) {
			return (DataSource) this.getOraclelDataSource(servername, port, 
					user, password, database);
		}

		return null;
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
	public DataSource getDataSource(String driver, String servername, int port,
			String user, String password) throws Exception {

		if (driver.equalsIgnoreCase("mysql")) {
			return (DataSource) this.getMySqlDataSource(servername, port,
					user, password, null);
		}

		if (driver.equalsIgnoreCase("mssql")) {
			return (DataSource) this.getMSSqlDataSource(servername, port,
					user, password, null);
		}
		
		if (driver.equalsIgnoreCase("oracle")) {
			return (DataSource) this.getOraclelDataSource(servername, port, 
					user, password, null);
		}

		return null;
	}

	private MysqlDataSource getMySqlDataSource(String servername, int port, 
                    String user, String password, String database) throws Exception {

		// Setting up the DataSource object
		MysqlDataSource ds = new MysqlDataSource();
		ds.setServerName(servername);
		ds.setPortNumber(port);
                if(database != null){
                    ds.setDatabaseName(database);
                }
		ds.setUser(user);
		ds.setPassword(password);

		return ds;

	}

	private SQLServerDataSource getMSSqlDataSource(String servername, int port,
			String user, String password, String database) throws Exception {

		// Setting up the DataSource object
		SQLServerDataSource ds = new SQLServerDataSource();
		
		ds.setServerName(servername);
		ds.setPortNumber(port);
                if(database != null){
                    ds.setDatabaseName(database);
                }
		ds.setUser(user);
		ds.setPassword(password);

		return ds;
	}
	
	private OracleDataSource getOraclelDataSource(String servername, int port,
			String user, String password, String database) throws Exception {

		// Setting up the DataSource object
		
		OracleDataSource ds = new OracleDataSource();

		ds.setDriverType("thin");
		
		ds.setServerName(servername);
		ds.setPortNumber(port);
                if(database != null){
                    ds.setDatabaseName(database);
                }
		ds.setUser(user);
		ds.setPassword(password);

		return ds;
	}
        
        public List<String> getDatabaseList(DatabaseConnDataVO connVO) throws SQLException, Exception{
            List<String> databaseList = new ArrayList<>();
            
            DataSource ds = getDataSource(
                        connVO.getDriver(),
                        connVO.getHostname(),
                        connVO.getPort(),
                        connVO.getUsername(),
                        connVO.getPassword()
                );
            
            ResultSet dbs;
            if(connVO.getDriver().equalsIgnoreCase("oracle")){
                dbs = ds.getConnection().getMetaData().getSchemas();
            } else {
                dbs = ds.getConnection().getMetaData().getCatalogs();
            }
            
            while(dbs.next())
            {
                databaseList.add(dbs.getString(1));
            }
            
            return databaseList;
        }

}
