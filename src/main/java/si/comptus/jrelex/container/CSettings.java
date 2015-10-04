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
