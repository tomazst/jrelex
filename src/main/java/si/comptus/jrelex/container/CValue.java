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

/**
 * 
 * @author tomaz
 */
public class CValue implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1012L;
	private Object old;
	private Object current;

	public CValue(Object current) {
		this.setCurrent(current);
	}

	public Object getCurrent() {
		return current;
	}

	public Object getOld() {
		return old;
	}

	public void setCurrent(Object current) {
		this.old = this.current;
		this.current = current;
	}

}
