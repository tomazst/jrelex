/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.comptus.jrelex.container;

import java.io.Serializable;

import si.comptus.jrelex.sql.AbstractDynamicQuery;

/**
 * 
 * @author tomaz
 */
public class CColumnTableReferences implements Serializable {

	private static final long serialVersionUID = 1101L;
	private String storedDatabaseName="";
	private String databaseName=""; 
	private String tableName=""; 
	private CColumn column=null; 
	private String value="";
	private AbstractDynamicQuery dq=null;

	public CColumnTableReferences() {}

	public String getStoredDatabaseName() {
		return storedDatabaseName;
	}

	public void setStoredDatabaseName(String storedDatabaseName) {
		this.storedDatabaseName = storedDatabaseName;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public CColumn getColumn() {
		return column;
	}

	public void setColumn(CColumn column) {
		this.column = column;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public AbstractDynamicQuery getDq() {
		return dq;
	}

	public void setDq(AbstractDynamicQuery dq) {
		this.dq = dq;
	}

}
