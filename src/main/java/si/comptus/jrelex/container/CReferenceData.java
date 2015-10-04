/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package si.comptus.jrelex.container;

import java.io.Serializable;

/**
 * 
 * @author tomazst
 */
public class CReferenceData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1003L;
	private String id="";
	private String storedDatabase="";
	private String databaseName="";
	private String tableName="";
	private String columnName="";
	private boolean columnPrimaryKey=false;
	private boolean columnForeignKey=false;
	private int strikes=0;
	private String value="";
	
	public String getStoredDatabase() {
		return storedDatabase;
	}
	public void setStoredDatabase(String storedDatabase) {
		this.storedDatabase = storedDatabase;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public int getStrikes() {
		return strikes;
	}
	public void setStrikes(int strikes) {
		this.strikes = strikes;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public boolean isColumnPrimaryKey() {
		return columnPrimaryKey;
	}
	public void setColumnPrimaryKey(boolean columnPrimaryKey) {
		this.columnPrimaryKey = columnPrimaryKey;
	}
	public boolean isColumnForeignKey() {
		return columnForeignKey;
	}
	public void setColumnForeignKey(boolean columnForeignKey) {
		this.columnForeignKey = columnForeignKey;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
