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
