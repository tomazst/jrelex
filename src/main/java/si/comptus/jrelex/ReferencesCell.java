package si.comptus.jrelex;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.common.TableCriteria.Operator;
import com.panemu.tiwulfx.table.TableControl;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import si.comptus.jrelex.Common;
import si.comptus.jrelex.sql.AbstractDynamicQuery;
import si.comptus.jrelex.container.CColumnTableReferences;
import si.comptus.jrelex.container.CReferenceData;
import si.comptus.jrelex.container.CTable;

public class ReferencesCell<S, T> extends TableCell<S, T>{
	TableCell tableCell;
	
	public ReferencesCell(){
		super();
		tableCell = this;
	}
		
	@Override public void updateItem(final T item, boolean empty) {
		super.updateItem(item, empty);
		
        if (empty) {
        	setText(null);
            setGraphic(null);
        } else {
        	Image image = new Image(getClass().getResourceAsStream("/images/b_relations.png"));
    		ImageView imageView = new ImageView(image);
    		imageView.setSmooth(true);    		
        	
        	Hyperlink link = new Hyperlink("");
        	
        	link.setGraphic(imageView);
        	link.setOnAction(new EventHandler<ActionEvent>() {
        	    @Override
        	    public void handle(ActionEvent e) {
        	    	//selection.isSelected(2);
        	    	Hyperlink link = (Hyperlink) e.getSource();
        	    	//TableCell tableCell = (TableCell) link.getParent().getParent();
        	    	TableCell tableCell = (TableCell) link.getParent();
        	    	
        	    	//tableCell.getTableRow().setStyle("-fx-background-color: #eed3d7");
        	    	//tableCell.getTableView().getFocusModel().focus(tableCell.getTableRow().getIndex());
        	    	tableCell.getTableView().getSelectionModel().select(tableCell.getTableRow().getIndex());
        	        //Dialog dialog = new Dialog<T>(item, tableCell);
        	    	
        	    	ColumnRefToTablesView<T> ref = new ColumnRefToTablesView<>();
        	    	ref.showReferences(item, tableCell);
        	    	
        	    }
        	});
        	
        	setGraphic(link);
        }
        
	}
	
}