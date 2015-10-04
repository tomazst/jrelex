/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.comptus.jrelex.container;

import java.io.Serializable;

/**
 * 
 * @author tomaz
 */
public class CForeignReferences implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1005L;
	private Object value;
	private CReference[] references;

	public CForeignReferences() {
	}

	public CReference[] getReferences() {
		return references;
	}

	public void setReferences(CReference[] references) {
		this.references = references;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
