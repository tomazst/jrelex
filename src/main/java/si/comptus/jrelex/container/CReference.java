/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package si.comptus.jrelex.container;

import java.io.Serializable;

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
	


}
