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
public class CPrimaryReferences implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1008L;
	private Object value;
	private CReference[] references;

	public CPrimaryReferences() {
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
