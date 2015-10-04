/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package si.comptus.jrelex.container;

import java.io.Serializable;
import java.util.ArrayList;

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

}
