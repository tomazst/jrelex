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
import java.util.ArrayList;
import java.util.Objects;

/**
 * 
 * @author tomazst
 */
public class CTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1011L;
	private String name;
	private ArrayList<String> columnNames = null;
	private ArrayList<CColumn> columns;
	private boolean visible = true;
        
        /**
         * Constructor.
         */
        public CTable() { }
        
        /**
         * Returns new column instance.
         * @return 
         */
        public CColumn getNewColumnInstance() {
            return new CColumn();
        }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<CColumn> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<CColumn> columns) {
		this.columns = columns;
	}

	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public CColumn getColumnByName(String name) {
		CColumn column = new CColumn();
		for (CColumn col : columns) {
			if (col.getName().equals(name)) {
				return col;
			}
		}
		return column;
	}

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.name);
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
        final CTable other = (CTable) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
        
        

}
