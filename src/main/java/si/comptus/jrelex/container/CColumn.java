/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.comptus.jrelex.container;

import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author tomaz
 */
public class CColumn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004L;
	private String name;
	private boolean primaryKey;
	private boolean foreignKey;
	private String type;
	private boolean editable = true;
	private boolean mandatory = true;
	private int length = 80;
	private String[] options = null;
	private Object defaultValue = null;
	private ArrayList<CReference> references = null;
	private boolean visible = true;
	
	private static final Logger log = LoggerFactory.getLogger(CColumn.class);

	public CColumn() {

	}

	public CColumn(String name) {
		this.name = name;
	}

	public CColumn(String name, boolean mandatory) {
		this.name = name;
		this.mandatory = mandatory;
	}

	public CColumn(String name, boolean mandatory, String type) {
		this.name = name;
		this.mandatory = mandatory;
		this.type = type;
	}

	/**
	 * Set all parameters for column
	 * 
	 * @param name
	 * @param editable
	 * @param mandatory
	 * @param type
	 * @param length
	 */
	public CColumn(String name, boolean editable, boolean mandatory,
			String type, int length, Object defaultValue) {
		this.name = name;
		this.editable = editable;
		this.mandatory = mandatory;
		if (type.equals("select")) {
			log.error("For type select edit optional values");
		}
		this.type = type;
		this.length = length;
		this.defaultValue = defaultValue;
	}

	public CColumn(String name, boolean editable, boolean mandatory,
			String type, int length, String[] options) {
		this.name = name;
		this.editable = editable;
		this.mandatory = mandatory;
		this.type = type;
		this.length = length;
		this.options = options;

		String[] defaultVls = new String[1];
		defaultVls[0] = "";
		this.defaultValue = defaultVls;

	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}

	public ArrayList<CReference> getReferences() {
		return references;
	}

	public void setReferences(ArrayList<CReference> references) {
		this.references = references;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
}
