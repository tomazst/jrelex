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

import si.comptus.jrelex.database.DynamicQueryAbstract;

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
	private DynamicQueryAbstract dq=null;

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
	
	public DynamicQueryAbstract getDq() {
		return dq;
	}

	public void setDq(DynamicQueryAbstract dq) {
		this.dq = dq;
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.databaseName);
        hash = 37 * hash + Objects.hashCode(this.tableName);
        hash = 37 * hash + Objects.hashCode(this.column);
        hash = 37 * hash + Objects.hashCode(this.value);
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
        final CColumnTableReferences other = (CColumnTableReferences) obj;
        if (!Objects.equals(this.databaseName, other.databaseName)) {
            return false;
        }
        if (!Objects.equals(this.tableName, other.tableName)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.column, other.column)) {
            return false;
        }
        return true;
    }
        
        

}
