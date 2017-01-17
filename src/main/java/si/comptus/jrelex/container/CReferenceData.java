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

package si.comptus.jrelex.container;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CReferenceData other = (CReferenceData) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
        
        

}
