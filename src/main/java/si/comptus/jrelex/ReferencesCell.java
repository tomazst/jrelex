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

import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import si.comptus.jrelex.container.CColumnTableReferences;

public class ReferencesCell<S, T> extends TableCell<S, T> {
    TableCell<S,T> tableCell;

    public ReferencesCell() {
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
            Image image = new Image(getClass().getResourceAsStream("/images/b_relations.png"));
            ImageView imageView = new ImageView(image);
            imageView.setSmooth(true);

            Hyperlink link = new Hyperlink("");

            link.setGraphic(imageView);
            link.setOnAction(new EventHandler<ActionEvent>() {
                @SuppressWarnings("unchecked")
                @Override
                public void handle(ActionEvent e) {
                    // selection.isSelected(2);
                    Hyperlink link = (Hyperlink) e.getSource();
                    // TableCell tableCell = (TableCell)
                    // link.getParent().getParent();
                    TableCell<S,T> tableCell = (TableCell<S,T>) link.getParent();

                    // tableCell.getTableView().getFocusModel().focus(tableCell.getTableRow().getIndex());
                    tableCell.getTableView().getSelectionModel().select(tableCell.getTableRow().getIndex());
                    // Dialog dialog = new Dialog<T>(item, tableCell);

                    ColumnValueReferences ref = new ColumnValueReferences();
                    ref.showReferences((HashMap<String, CColumnTableReferences>) item, tableCell);

                }
            });
            setGraphic(link);
        }

    }

}