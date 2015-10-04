package si.comptus.jrelex;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.common.TableCriteria.Operator;
import com.panemu.tiwulfx.table.TableControl;

import si.comptus.jrelex.container.CColumnTableReferences;
import si.comptus.jrelex.container.CReferenceData;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.sql.AbstractDynamicQuery;

public class ColumnRefToTablesView<T> {

	HashMap<String, CColumnTableReferences> colReferences = null;

	private static final Logger log = LoggerFactory
			.getLogger(ColumnRefToTablesView.class);
	/*
	 * All references found for column value in database
	 */
	HashMap<String, List<CReferenceData>> allColumnsValueReferences = null;

	public ColumnRefToTablesView() {
	}

	public void showReferences(T item, TableCell tableCell) {

		allColumnsValueReferences = new HashMap<>();

		// need new object with parameters to get references
		colReferences = (HashMap<String, CColumnTableReferences>) item;

		TreeItem<String> rootItem = new TreeItem<String>("References ("
				+ tableCell.getTableView().getId() + ")");
		rootItem.setExpanded(true);

		Iterator iterator = colReferences.keySet().iterator();
		while (iterator.hasNext()) {

			// Column name
			String columnName = (String) iterator.next();
			CColumnTableReferences colReference = colReferences.get(columnName);

			Hyperlink columnlink = new Hyperlink();
			Image imageColumn = new Image(getClass().getResourceAsStream(
					"/images/column.png"));
			ImageView imageViewColumn = new ImageView(imageColumn);

			columnlink.setGraphic(imageViewColumn);
			columnlink.setText(columnName);

			/**
			 * Shows all referenced data in tables in the botom of window.
			 * 
			 * This action creates tables to view all referenced data in
			 * database tables
			 */

			columnlink.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					Hyperlink columnlink = (Hyperlink) e.getSource();
					String columnName = columnlink.getText();

					CColumnTableReferences colReference = colReferences
							.get(columnName);
					//log.info(colReference.getTableName());

					// column value references in database
					List<CReferenceData> references = allColumnsValueReferences
							.get(colReference.getColumn().getName());

					// TitledPane[] titledPanes = new
					// TitledPane[references.size()];

					VBox vbox = new VBox();
					
					int i = 0;
					for(final CReferenceData refData : references){
					
						TableCriteria<String> tableCriteria = new TableCriteria<>(
								refData.getColumnName(), Operator.eq,
								refData.getValue());
						final List<TableCriteria> filteredColumns = new ArrayList<>();
						filteredColumns.add(tableCriteria);

						CTable table = Common.getInstance().getDbstore()
								.getDatabases()
								.get(refData.getStoredDatabase())
								.getTables().get(refData.getTableName());
						String databaseName = Common.getInstance()
								.getDbstore().getDatabases()
								.get(refData.getStoredDatabase()).getName();

						Connection conn = Common.getInstance()
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

						title.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent e) {
								Hyperlink link = (Hyperlink) e.getSource();
								link.setVisited(false);

								String tabName = refData.getTableName()
										+ "." + refData.getColumnName();
								Tab tab = Common.getInstance().tabExists(
										refData.getTableName(),
										Common.getInstance()
												.getTablesTabPane());

								//if (tab == null) {

									Connection conn = Common
											.getInstance()
											.getDatabaseInteraction()
											.getConnection(
													refData.getStoredDatabase());
									CTable table = Common
											.getInstance()
											.getDbstore()
											.getDatabases()
											.get(refData
													.getStoredDatabase())
											.getTables()
											.get(refData.getTableName());
									
									DatabaseExplorerTab databaseExplorerTab = new DatabaseExplorerTab(
											Common.getInstance()
													.getTablesTabPane(),
											conn, table, refData
													.getDatabaseName(),
											refData.getStoredDatabase(),
											filteredColumns);
									tab = databaseExplorerTab.getTab();

								//}

								Common.getInstance().getTablesTabPane()
										.getSelectionModel().select(tab);
							}
						});
						
						vbox.getChildren().add(title);

						DbTableInGridPane<T> tableGrid = new DbTableInGridPane<>(
								refData.getStoredDatabase(), refData.getColumnName(), conn);
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
					
					if(Common.getInstance().getVerticalSplitPane().getItems().size() < 2){
						
						TabPane tabPane = new TabPane();
						Tab  tab = new Tab();
						
						tab.setText("Referenced data");
						//tab.setStyle("-fx-border-color: #FF0000");
												
						tab.setContent(scrollPane2);
						tabPane.getTabs().add(tab);
																	
						Common.getInstance().getVerticalSplitPane().getItems().add(tabPane);
						
						
					} else {						
						
						TabPane tabPane = (TabPane) Common.getInstance().getVerticalSplitPane().getItems().get(1);
							
						if(tabPane.getTabs().size() == 0){
							Tab tab = new Tab();
							tab.setText("Referenced data");
							//tab.setStyle("-fx-border-color: #FF0000");
							
							tab.setText("Referenced data");
							tab.setContent(scrollPane2);
							tabPane.getTabs().add(tab);
						} else {
							tabPane.getTabs().get(0).setContent(scrollPane2);
						}
						
					}					
					
					
				}
			});

			TreeItem<String> columnItem = new TreeItem<String>("");
			columnItem.setExpanded(true);

			columnItem.setGraphic(columnlink);

			rootItem.getChildren().add(columnItem);

			// column references
			AbstractDynamicQuery dq = colReference.getDq();
			// column value references in database. Counts data in the database.
			HashMap<String, CReferenceData> references_tmp = dq.getReferencedData(
					colReference.getStoredDatabaseName(),
					colReference.getDatabaseName(),
					colReference.getTableName(), colReference.getColumn(),
					colReference.getValue());

			
			
			// order references HashMap in ArrayList. First are primary keys. Next are foreign keys.
			List<CReferenceData> references = new ArrayList<CReferenceData>(references_tmp.size());
			Iterator ref_iterator = references_tmp.keySet().iterator();
			
			List<CReferenceData> primary_refs = new ArrayList<>();
			List<CReferenceData> foreign_refs = new ArrayList<>();
			
			while (ref_iterator.hasNext()) {
				String key = (String) ref_iterator.next();
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
			
			for(CReferenceData ref : primary_refs){ // first adding primary references
				references.add(ref);
			}
			primary_refs = null; // unset arraylist
			
			for(CReferenceData ref : foreign_refs){ // then adding foreign references
				references.add(ref);
			}
			foreign_refs = null; // unset arraylist
			
			allColumnsValueReferences.put(colReference.getColumn().getName(),
					references);

			for(final CReferenceData refData : references) {
				
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
				link.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {// from hier i call table
						final ArrayList<TableCriteria> filteredColumns = new ArrayList<>();
						TableCriteria tableCriteria = new TableCriteria<>();
						tableCriteria.setAttributeName(refData
								.getColumnName());
						tableCriteria.setOperator(Operator.eq);
						tableCriteria.setValue(refData.getValue());
						filteredColumns.add(tableCriteria);

						//log.info(refData.getColumnName());

						// List filteredColumns = (List) list;
						/*
						 * Common.getInstance().getBrowserController().load(
						 * refData.getStoredDatabase(),
						 * refData.getTableName(), filteredColumns);
						 */
						String tabName = refData.getTableName() + "."
								+ refData.getColumnName();
						Tab tab = Common.getInstance().tabExists(
								refData.getTableName(),
								Common.getInstance().getTablesTabPane());

						//if (tab == null) {

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

						//}

						Common.getInstance().getTablesTabPane()
								.getSelectionModel().select(tab);
					}
				});
				TreeItem<String> tableItem = new TreeItem<String>("");
				tableItem.setGraphic(link);
				columnItem.getChildren().add(tableItem);
			}
			// references=null;
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

}
