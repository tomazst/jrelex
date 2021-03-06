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

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CTable;

import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import com.panemu.tiwulfx.table.BaseColumn;
import com.panemu.tiwulfx.table.CheckBoxColumn;
import com.panemu.tiwulfx.table.DateColumn;
import com.panemu.tiwulfx.table.NumberColumn;
import com.panemu.tiwulfx.table.TableControl;
import com.panemu.tiwulfx.table.TextColumn;

/**
 *
 * @author tomaz
 *
 * @param <V>
 *            Type of table cell value
 */
public class DatabaseExplorerTab<V> {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseExplorerTab.class);
    private TableControl<? extends Object> exploreTable;

    private Tab tab;

    public DatabaseExplorerTab(TabPane tabPaneExploreDatabases, Connection conn, CTable table, String databaseName,
            String storedDatabaseName) {
        this.showTable(tabPaneExploreDatabases, conn, table, databaseName, storedDatabaseName, null);
    }

    public DatabaseExplorerTab(TabPane tabPaneExploreDatabases, Connection conn, CTable table, String databaseName,
            String storedDatabaseName, List<TableCriteria<V>> filteredColumns) {
        this.showTable(tabPaneExploreDatabases, conn, table, databaseName, storedDatabaseName, filteredColumns);
    }

    public void showTable(TabPane tabPaneExploreDatabases, Connection conn, CTable table, String databaseName,
            String storedDatabaseName, List<TableCriteria<V>> filteredColumns) {

        Object obj = Common.getInstance().getPojoGenerator().createPojoForTable(table.getName(), table);

        // create table
        exploreTable = new TableControl<>(obj.getClass());
        exploreTable.setVisibleComponents(false, TableControl.Component.BUTTON_EDIT,
                TableControl.Component.BUTTON_DELETE, TableControl.Component.BUTTON_SAVE,
                TableControl.Component.BUTTON_INSERT, TableControl.Component.BUTTON_RELOAD);

        exploreTable.getTableView().setId(table.getName());

        try {
            // get data controller
            DatabaseTableController controller = null;
            controller = new DatabaseTableController(exploreTable, conn, databaseName, obj, table, storedDatabaseName);

            if (filteredColumns != null) {
                controller.setFilteredColumns(filteredColumns);
            }

            exploreTable.setController(controller);
        } catch (JRelExException e) {
            LOG.error(e.getMessage(), e);
            MessageDialogBuilder.error(e).show(null);
        }

        Map<String, Class<?>> tableProperties = Common.getInstance().getPojoGenerator().getLastPojoProperties();

        // Adding table columns
        // first is column for position numbering
        BaseColumn positionCol = new BaseColumn("position", 30);
        positionCol.setId("position");
        positionCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            public TableCell call(TableColumn col) {
                return new PositionCell<>();
            }
        });

        positionCol.setText("");
        positionCol.setSortable(false);
        positionCol.setFilterable(false);

        exploreTable.addColumn(positionCol);

        // Second is column for references
        BaseColumn referCol = new BaseColumn("references", 30);
        referCol.setId("references");
        referCol.setCellFactory((Callback<TableColumn, TableCell>) (TableColumn col) -> {
            return new ReferencesCell<>();
        });

        referCol.setText("References");
        referCol.setSortable(false);
        referCol.setFilterable(false);

        exploreTable.addColumn(referCol);

        for (CColumn column : table.getColumns()) {
            // user can select to see column
            if (!column.isVisible()) {
                continue;
            }

            Class clazz = tableProperties.get(column.getName());
            BaseColumn col = null;

            if (clazz.equals(Integer.class) || clazz.equals(Float.class) || clazz.equals(Double.class)
                    || clazz.equals(Float.class)) {
                col = (BaseColumn) new NumberColumn(column.getName(), clazz);
                col.setFilterable(true);
            } else if (clazz.equals(Date.class)) {
                DateColumn dateCol = new DateColumn(column.getName());
                dateCol.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
                col = (BaseColumn) dateCol;
                col.setId("dateColumn");
                col.setFilterable(true);
            } else if (clazz.equals(Boolean.class)) {
                CheckBoxColumn<? extends Object> chkCol = new CheckBoxColumn<>(column.getName());
                col = (BaseColumn) chkCol;
                col.setFilterable(true);
            } else {
                col = (BaseColumn) new TextColumn(column.getName());
                col.setFilterable(true);
            }

            if (column.isPrimaryKey()) {
                col.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/primary-key.png"))));
            }
            if (column.isForeignKey()) {
                col.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/foreign-keys.png"))));
            }

            exploreTable.addColumn(col);

        }

        // exploreTable.getSelectionModel().cellSelectionEnabledProperty().unbind();
        // exploreTable.getSelectionModel().setCellSelectionEnabled(true);
        // exploreTable.setSelectionMode();
        // exploreTable.setSelectionMode(SelectionMode.MULTIPLE);
        exploreTable.setMaxRecord(Common.getInstance().getDbstore().getAppSettings().getNumberRowsToDisplay());
        exploreTable.reloadFirstPage();
        // exploreTable.setAgileEditing(true);

        String text = table.getName();
        tab = Common.getInstance().tabExists(text, tabPaneExploreDatabases);
        if (tab == null) {
            tab = new Tab();
            
            tab.setText(text);
            tabPaneExploreDatabases.getTabs().add(tab);
            
        }

        // exploreTable.getTableView().setOnMouseReleased(tableRightClickListener);
        tab.setContent(exploreTable);
        // tabPaneExploreDatabases.getSelectionModel().select(tab);

    }

    public Tab getTab() {
        return tab;
    }
    
    

}
