package si.comptus.jrelex;

import javafx.scene.control.Label;

public class FormValidator {
	private static FormValidator instance;
	
	private boolean formValid=true;
	
	private FormValidator(){}
	
	public static FormValidator getInstance(){
		if(instance == null){
			instance = new FormValidator();
		}
		return instance;
	}
	
	public boolean isFormValid() {
		return formValid;
	}
	
	public void setFormValid(boolean formValid) {
		this.formValid = formValid;
	}

	public boolean isTextEmpty(String t){
		boolean empty = false;
		
		if(t != null) {
			if(t.isEmpty()){
				empty = true;	
			}
		} else {
			empty = true;
		}
		
		if(empty){
			formValid = false;
		}
		
		return empty;
	}
	
	public boolean isTextEmpty(String t, Label lbl, String o){
		boolean empty = isTextEmpty(t);
		
		if(empty){
			lbl.setStyle("-fx-text-fill: red");
			lbl.setText(o);
		} else {
			lbl.setText("");
		}		
		return empty;
	}
	
	public boolean isAllowedLength(String t, int l){
		boolean ok = false;
		if(t.length() <= l){
			ok = true;
		}
		
		if(!ok){
			formValid = false;
		}
		
		return ok;
	}
	
	public boolean isAllowedLength(String t, int l, Label lbl, String o){
		boolean ok = isAllowedLength(t, l);
		if(!ok){
			lbl.setText(o);
		} else {
			lbl.setText("");
		}
		return ok;
	}
	
	public boolean isNumeric(String t){
		boolean numeric = t.matches("[-+]?\\d*\\.?\\d+");
		if(!numeric){
			formValid = false;
		}
		return numeric;
	}
	
	public boolean isNumeric(String t, Label lbl, String o){
		boolean numeric = isNumeric(t);
		if(!numeric){
			lbl.setStyle("-fx-text-fill: red");
			lbl.setText(o);
		} else {
			lbl.setText("");
		}
		return numeric;
	}
	
}
