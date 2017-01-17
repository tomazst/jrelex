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

public class CSettings implements Serializable {

	/*
	 * Allowed number of rows to display in a table
	 */
	private int numberRowsToDisplay=50;
	/*
	 * Maximum string length in a table cell
	 */
	private int maxStringLength=50;
	/*
	 * References to empty data
	 */
	private boolean showRefToEmptyData=false;
	/*
	 * References to null data
	 */
	private boolean showRefToNullData=false;

	public int getNumberRowsToDisplay() {
		return numberRowsToDisplay;
	}

	public void setNumberRowsToDisplay(int numberRowsToDisplay) {
		this.numberRowsToDisplay = numberRowsToDisplay;
	}

	public int getMaxStringLength() {
		return maxStringLength;
	}

	public void setMaxStringLength(int maxStringLength) {
		this.maxStringLength = maxStringLength;
	}

	public boolean isShowRefToEmptyData() {
		return showRefToEmptyData;
	}

	public void setShowRefToEmptyData(boolean showRefToEmptyData) {
		this.showRefToEmptyData = showRefToEmptyData;
	}

	public boolean isShowRefToNullData() {
		return showRefToNullData;
	}

	public void setShowRefToNullData(boolean showRefToNullData) {
		this.showRefToNullData = showRefToNullData;
	}
	
	
	
}
