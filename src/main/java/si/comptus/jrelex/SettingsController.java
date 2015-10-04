package si.comptus.jrelex;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.container.CSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController implements Initializable {
	
	@FXML
	private TextField tfNumberRowsForDisplay;
	@FXML	
	private TextField tfStringLengthInCell;
	@FXML
	private CheckBox cbShowRefToEmptyData;
	@FXML
	private CheckBox cbShowRefToNullData;
	
	@FXML
	private Button btnSave;
	@FXML
	private Button btnClose;
	
	@FXML
	private Label errNumberRowsForDisplay;
	@FXML	
	private Label errStringLengthInCell;
	@FXML
	private Label errShowRefToEmptyData;
	@FXML
	private Label errShowRefToNullData;
	
	private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		CSettings settings = Common.getInstance().getDbstore().getAppSettings();
		tfNumberRowsForDisplay.setText(String.valueOf(settings.getNumberRowsToDisplay()));
		tfStringLengthInCell.setText(String.valueOf(settings.getMaxStringLength()));
		cbShowRefToEmptyData.setSelected(settings.isShowRefToEmptyData());
		cbShowRefToNullData.setSelected(settings.isShowRefToNullData());		
		
		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent e) {
				
				CSettings settings = Common.getInstance().getDbstore().getAppSettings();
				
				settings.setNumberRowsToDisplay(Integer.parseInt(tfNumberRowsForDisplay.getText()));
				settings.setMaxStringLength(Integer.parseInt(tfStringLengthInCell.getText()));
				settings.setShowRefToEmptyData(cbShowRefToEmptyData.isSelected());
				settings.setShowRefToNullData(cbShowRefToNullData.isSelected());
				
				Common.getInstance().saveSerializedDBMetaDataToDisk(Common.getInstance().getDbstore());
				
				Node node = (Node) e.getSource();
				Stage dialog = (Stage) node.getScene().getWindow();
				dialog.close();
			}
		});
		
		btnClose.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				Node node = (Node) e.getSource();
				Stage dialog = (Stage) node.getScene().getWindow();
				dialog.close();				
			}
		});
		
	}
	
	public void updateTfNumberRowsForDisplay() {
		String text = tfNumberRowsForDisplay.getText();
		FormValidator.getInstance().isTextEmpty(text, errNumberRowsForDisplay, 
				"Input is empty!");
		FormValidator.getInstance().isNumeric(text, errNumberRowsForDisplay, 
				"Input is not numeric!");
	}
	
	public void updateTfStringLengthInCell() {
		String text = tfStringLengthInCell.getText();
		FormValidator.getInstance().isTextEmpty(text, errStringLengthInCell, 
				"Input is empty!");
		FormValidator.getInstance().isNumeric(text, errStringLengthInCell, 
				"Input is not numeric!");
	}
	
	public void updateCbShowRefToEmptyData() {
		String text = cbShowRefToEmptyData.getText();
	}
	
	public void updateCbShowRefToNullData() {
		String text = cbShowRefToNullData.getText();
	}
	

}
