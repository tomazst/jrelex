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

import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.common.TableCriteria.Operator;
import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.comptus.jrelex.container.CColumnTableReferences;
import si.comptus.jrelex.container.CReferenceData;
import si.comptus.jrelex.container.CTable;

/**
 * It shows references in UI.
 * @author tomaz
 *
 * @param <T> type of record in row
 * @param <V> type of value in a cell
 */
public class ColumnValueReferences<T, V> {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ColumnValueReferences.class);

    HashMap<String, CColumnTableReferences> colReferences = null;

    /*
     * All references for column value found in database
     */
    HashMap<String, List<CReferenceData>> allColumnsValueReferences;
    
    public ColumnValueReferences() {
    }

    /**
     * List references in tree view
     * @param item
     * @param tableCell
     */
    public void showReferences(final HashMap<String, CColumnTableReferences> item, final TableCell<T, V> tableCell) {

        allColumnsValueReferences = new HashMap<>();

        // need new object with parameters to get references
        colReferences = item;

        TreeItem<String> rootItem = new TreeItem<String>("References ("
                + tableCell.getTableView().getId() + ")");
        rootItem.setExpanded(true);

        Iterator<String> iterator = colReferences.keySet().iterator();
        while (iterator.hasNext()) {

            // Column name
            String columnName = (String) iterator.next();
            
            CColumnTableReferences colReference = colReferences.get(columnName);

            Hyperlink columnlink = new Hyperlink();
            Image imageColumn = new Image(getClass().getResourceAsStream("/images/column.png"));
            ImageView imageViewColumn = new ImageView(imageColumn);

            columnlink.setGraphic(imageViewColumn);
            columnlink.setText(columnName);

            /**
             * Shows all referenced data in tables at the botom of the window.
             * This action creates tables to view all referenced data in database tables
             */
            columnlink.setOnAction(event -> {
                Hyperlink rdColumnlink = (Hyperlink) event.getSource();
                String rdColumnName = rdColumnlink.getText();
                this.initializeTableReferencePane(rdColumnName);
            });
            
            // Tree view
            TreeItem<String> columnItem = new TreeItem<String>("");
            columnItem.setExpanded(true);

            columnItem.setGraphic(columnlink);

            rootItem.getChildren().add(columnItem);

            // column value references in database. Counts data in the database.
            HashMap<String, CReferenceData> references_tmp = colReference.getDq().getReferencedData(
                    colReference.getStoredDatabaseName(),
                    colReference.getDatabaseName(),
                    colReference.getTableName(), 
                    colReference.getColumn(),
                    colReference.getValue());

            // order references HashMap in ArrayList. First are primary keys. Next are foreign keys.
            List<CReferenceData> references = new ArrayList<CReferenceData>(references_tmp.size());
            Iterator<String> ref_iterator = references_tmp.keySet().iterator();

            List<CReferenceData> primary_refs = new ArrayList<>();
            List<CReferenceData> foreign_refs = new ArrayList<>();

            while (ref_iterator.hasNext()) {
                String key = ref_iterator.next();
                CReferenceData refData = references_tmp.get(key);
                if (refData.getStrikes() > 0) {
                    if (refData.isColumnPrimaryKey()) {
                        primary_refs.add(refData);
                    }
                    if (refData.isColumnForeignKey()) {
                        foreign_refs.add(refData);
                    }
                }
            }
            references_tmp = null; // unset HashMap

            for (CReferenceData ref : primary_refs) { // first adding primary references
                references.add(ref);
            }
            primary_refs = null; // unset arraylist

            for (CReferenceData ref : foreign_refs) { // then adding foreign references
                references.add(ref);
            }
            foreign_refs = null; // unset arraylist

            allColumnsValueReferences.put(colReference.getColumn().getName(),
                    references);

            for (final CReferenceData refData : references) {

                String referencedTableName = refData.getTableName();

                Hyperlink link = new Hyperlink();

                if (refData.isColumnPrimaryKey()) {
                    Image imagePK = new Image(getClass()
                            .getResourceAsStream("/images/pkey-tbl.png"));
                    ImageView imageViewPrimaryKey = new ImageView(imagePK);
                    link.setGraphic(imageViewPrimaryKey);
                }

                if (refData.isColumnForeignKey()) {
                    Image imageFK = new Image(getClass()
                            .getResourceAsStream("/images/fkey-tbl.png"));
                    ImageView ImageViewForeignKey = new ImageView(imageFK);
                    link.setGraphic(ImageViewForeignKey);
                }

                link.setText(referencedTableName + "("
                        + refData.getStrikes() + ")");
                link.setOnAction(event -> {// from hier i call table
                        final ArrayList<TableCriteria<String>> filteredColumns = new ArrayList<>();
                        TableCriteria<String> tableCriteria = new TableCriteria<>();
                        tableCriteria.setAttributeName(refData
                                .getColumnName());
                        tableCriteria.setOperator(Operator.eq);
                        tableCriteria.setValue(refData.getValue());
                        filteredColumns.add(tableCriteria);

                        log.debug(refData.getColumnName());
                        
                        Tab tab = Common.getInstance().tabExists(
                                refData.getTableName(),
                                Common.getInstance().getTablesTabPane());

                        Connection conn = Common
                                .getInstance()
                                .getDatabaseInteraction()
                                .getConnection(
                                        refData.getStoredDatabase());
                        CTable table = Common.getInstance()
                                .getDbstore().getDatabases()
                                .get(refData.getStoredDatabase())
                                .getTables()
                                .get(refData.getTableName());
                        DatabaseExplorerTab databaseExplorerTab = new DatabaseExplorerTab(
                                Common.getInstance().getTablesTabPane(),
                                conn, table, refData.getDatabaseName(),
                                refData.getStoredDatabase(),
                                filteredColumns);
                        tab = databaseExplorerTab.getTab();

                        Common.getInstance().getTablesTabPane()
                                .getSelectionModel().select(tab);
                });
                TreeItem<String> tableItem = new TreeItem<String>("");
                tableItem.setGraphic(link);
                columnItem.getChildren().add(tableItem);
            }
        }
        rootItem.setExpanded(true);

        TreeView<String> tree = new TreeView<String>(rootItem);
        tree.setMaxHeight(Double.MAX_VALUE);

        /*
         * tree.setCellFactory(new
         * Callback<TreeView<String>,TreeCell<String>>(){
         *
         * @Override public TreeCell<String> call(TreeView<String> p) { return
         * new LinkFieldTreeCellImpl(); } });
         */
        // tree.setMinSize(200, 100);
        Tab tab = Common.getInstance().tabExists("Table references",
                Common.getInstance().getExplorerLeftSideTabPane());

        if (tab == null) {
            tab = new Tab();
            tab.setText("Table references");
            Common.getInstance().getExplorerLeftSideTabPane().getTabs()
                    .add(tab);
        }
        tab.setContent(tree);
        Common.getInstance().getExplorerLeftSideTabPane().getSelectionModel()
                .select(tab);

    }
    
    /**
     * Creates view for all column value references.
     * It is shown at the window bottom. 
     * @param columnName
     */
    private void initializeTableReferencePane(String columnName) {

    	CColumnTableReferences colReference = colReferences
                .get(columnName);
        //log.debug(colReference.getTableName());

        // column value references in database
        List<CReferenceData> references = allColumnsValueReferences
                .get(colReference.getColumn().getName());

        // TitledPane[] titledPanes = new
        // TitledPane[references.size()];
        VBox vbox = new VBox();

        int i = 0;
        for (final CReferenceData refData : references) {

            TableCriteria<String> tableCriteria = new TableCriteria<>(
                    refData.getColumnName(), Operator.eq,
                    refData.getValue());
            final List<TableCriteria<String>> filteredColumns = new ArrayList<>();
            filteredColumns.add(tableCriteria);

            CTable table = Common.getInstance().getDbstore()
                    .getDatabases()
                    .get(refData.getStoredDatabase())
                    .getTables().get(refData.getTableName());
            String databaseName = Common.getInstance()
                    .getDbstore().getDatabases()
                    .get(refData.getStoredDatabase()).getName();

            final Connection conn = Common.getInstance()
                    .getDatabaseInteraction()
                    .getConnection(refData.getStoredDatabase());

            // add data to pane
            Hyperlink title = new Hyperlink();

            title.setText(databaseName + "." + table.getName()
                    + " (column: " + refData.getColumnName()
                    + ", value: " + refData.getValue() + ")");
            title.setFont(Font.font("Arial", FontWeight.BOLD,
                    14));
            title.setStyle("-fx-color: #1919FF;");
            
            // Show referenced data in Database Explorer Tab
            title.setOnAction(event-> {
                    Hyperlink link = (Hyperlink) event.getSource();
                    link.setVisited(false);

                    //Tab tab = Common.getInstance().tabExists(refData.getTableName(), Common.getInstance().getTablesTabPane());                    
                    CTable refTable = Common.getInstance().getDbstore()
                            .getDatabases().get(refData.getStoredDatabase())
                            .getTables().get(refData.getTableName());

                    DatabaseExplorerTab<String> databaseExplorerTab = new DatabaseExplorerTab<>(
                            Common.getInstance().getTablesTabPane(),
                            conn, refTable, refData.getDatabaseName(),
                            refData.getStoredDatabase(),
                            filteredColumns);
                    
                    Tab tab = databaseExplorerTab.getTab();

                    Common.getInstance().getTablesTabPane()
                            .getSelectionModel().select(tab);
            });

            vbox.getChildren().add(title);

            ReferenceDataGrid tableGrid = null;
            try {
                tableGrid = new ReferenceDataGrid(
                        refData.getStoredDatabase(), 
                        refData.getColumnName(), conn
                        );
            }
            catch (JRelExException e2) {
                log.error(e2.getMessage(), e2);
                MessageDialogBuilder.error(e2).show(null);
            }
            GridPane grid = tableGrid.getTableGrid(
                    databaseName, table, filteredColumns);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
            scrollPane.setFitToHeight(true);
            scrollPane.setContent(grid);

            scrollPane.setPrefHeight(grid.getMaxHeight());
            scrollPane.setPrefWidth((Common.getInstance().getVerticalSplitPane().getWidth() - 20));
            scrollPane.setStyle("-fx-background-color: transparent");

            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            vbox.getChildren().add(scrollPane);

            Separator separator = new Separator();
            separator.setOrientation(Orientation.HORIZONTAL);
            vbox.getChildren().add(separator);

            i++;
        }

        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        //vbox.setStyle("-fx-border-color: #77ff11");
        //vbox.setPrefWidth(1050);
        VBox.setVgrow(vbox, Priority.ALWAYS);

        ScrollPane scrollPane2 = new ScrollPane();
        scrollPane2.setContent(vbox);

        if (Common.getInstance().getVerticalSplitPane().getItems().size() < 2) {

            TabPane tabPane = new TabPane();
            Tab tab = new Tab();

            tab.setText("Referenced data");
            //tab.setStyle("-fx-border-color: #FF0000");

            tab.setContent(scrollPane2);
            tabPane.getTabs().add(tab);

            Common.getInstance().getVerticalSplitPane().getItems().add(tabPane);

        } else {

            TabPane tabPane = (TabPane) Common.getInstance().getVerticalSplitPane().getItems().get(1);

            if (tabPane.getTabs().size() == 0) {
                Tab tab = new Tab();
                tab.setText("Referenced data");
                tab.setContent(scrollPane2);
                tabPane.getTabs().add(tab);
            } else {
                tabPane.getTabs().get(0).setContent(scrollPane2);
            }

        }

    }

}
