////////////////////////////////////////////////////////////////////////////////////////////////////
// JRelEx: Java application is intended for searching data using database relations.
// Copyright (C) 2015 tomazst <tomaz.stefancic@gmail.com>.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
////////////////////////////////////////////////////////////////////////////////////////////////////

package si.comptus.jrelex;

import si.comptus.jrelex.container.CPosition;
import javafx.scene.control.TableCell;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PositionCell<S, T> extends TableCell<S, T> {
	TableCell<S, T> tableCell;
	
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
