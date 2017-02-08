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
import java.util.HashMap;
import java.util.Objects;

/**
 * 
 * @author tomazst
 */
public class CDatabase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1002L;
	private String name;
	private HashMap<String, CTable> tables;
	private ConnBean connBean;
	private boolean visible=true;

	public CDatabase() { }
        
        public CTable getNewTableInstance(){
            return new CTable();
        }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, CTable> getTables() {
		return tables;
	}

	public void setTables(HashMap<String, CTable> tables) {
		this.tables = tables;
	}

	public ConnBean getConnBean() {
		return connBean;
	}

	public void setConnBean(ConnBean connBean) {
		this.connBean = connBean;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.name);
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
        final CDatabase other = (CDatabase) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

}
