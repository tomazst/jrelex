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
public class CReference implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1010L;
	private String Table;
	private String Column;
	private String referencedTable;
	private String referencedColumn;
	private int referenceCount;

	public String getReferencedColumn() {
		return referencedColumn;
	}

	public void setReferencedColumn(String referencedColumn) {
		this.referencedColumn = referencedColumn;
	}

	public String getReferencedTable() {
		return referencedTable;
	}

	public void setReferencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
	}

	public String getColumn() {
		return Column;
	}

	public void setColumn(String Column) {
		this.Column = Column;
	}

	public String getTable() {
		return Table;
	}

	public void setTable(String Table) {
		this.Table = Table;
	}

	public int getReferenceCount() {
		return referenceCount;
	}

	public void setReferenceCount(int referenceCount) {
		this.referenceCount = referenceCount;
	}

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.Table);
        hash = 11 * hash + Objects.hashCode(this.Column);
        hash = 11 * hash + Objects.hashCode(this.referencedTable);
        hash = 11 * hash + Objects.hashCode(this.referencedColumn);
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
        final CReference other = (CReference) obj;
        if (this.referenceCount != other.referenceCount) {
            return false;
        }
        if (!Objects.equals(this.Table, other.Table)) {
            return false;
        }
        if (!Objects.equals(this.Column, other.Column)) {
            return false;
        }
        if (!Objects.equals(this.referencedTable, other.referencedTable)) {
            return false;
        }
        if (!Objects.equals(this.referencedColumn, other.referencedColumn)) {
            return false;
        }
        return true;
    }
	


}
