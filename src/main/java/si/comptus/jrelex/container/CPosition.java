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

public class CPosition implements Serializable{
	private int position;
	private int maxPositionStringLength;
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getMaxPositionStringLength() {
		return maxPositionStringLength;
	}
	public void setMaxPositionStringLength(int maxPositionStringLength) {
		this.maxPositionStringLength = maxPositionStringLength;
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.position;
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
        final CPosition other = (CPosition) obj;
        if (this.position != other.position) {
            return false;
        }
        return true;
    }
	
	
}
