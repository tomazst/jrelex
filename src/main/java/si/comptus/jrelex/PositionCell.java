package si.comptus.jrelex;

import si.comptus.jrelex.container.CPosition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PositionCell<S, T> extends TableCell<S, T> {
	TableCell tableCell;
	
	public PositionCell(){
		super();
		tableCell = this;
	}
	
	@Override 
	public void updateItem(final T item, boolean empty) {
		super.updateItem(item, empty);
		
        if (empty) {
        	setText(null);
            setGraphic(null);
        } else {
        	setText(null);
        	
        	CPosition positionObj = (CPosition)item;
        	String pos = Integer.toString(positionObj.getPosition());
        	
        	Text text = new Text();
        	text.setFont(Font.font("Arial", FontWeight.THIN, 12));
        	text.setWrappingWidth(positionObj.getMaxPositionStringLength() * 7);
        	text.setTextAlignment(TextAlignment.RIGHT);
        	text.setText(pos);
        	setGraphic(text);

        }
	}
}
