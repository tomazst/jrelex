package si.comptus.jrelex;

import com.panemu.tiwulfx.control.DetachableTabPane;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import si.comptus.jrelex.container.CDatabase;
import si.comptus.jrelex.container.CTable;

public class DatabaseExplorerController implements Initializable {
	@FXML
	public TreeView<String> trvDatabaseList;
	@FXML
	public TabPane leftSideTabPane;
	@FXML
	public DetachableTabPane exploreTablesTabPane;
	@FXML
	public SplitPane verticalSplitPane;
	@FXML
	public TextField txtDatabaseFilter;
	@FXML
	public ImageView imgViewDatabaseFilter;
	@FXML
	public TextField txtTableFilter;
	@FXML
	public ImageView imgViewTableFilter;
	@FXML
	public SplitPane explorerSplitPane;
	
	@FXML
	public ImageView imgViewReloadDatabaseList;
	
	public String DBFilterValue="";
	public String TBLFilterValue="";
	
	@FXML
	public HBox hboxFilter;
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		verticalSplitPane.setDividerPositions(0.7);
		Common.getInstance().setVerticalSplitPane(verticalSplitPane);
		Common.getInstance().setExplorerLeftSideTabPane(leftSideTabPane);
		Common.getInstance().setTablesTabPane(exploreTablesTabPane);
		Common.getInstance().setExplorerSplitPane(explorerSplitPane);
		//Common.getInstance().setReferencedDataPane(referencedDataPane);
		this.refreshTreeViewDatabaseList(Common.getInstance().getDbstore().getDatabases());
		trvDatabaseList.setMaxHeight(Double.MAX_VALUE);
		
		imgViewDatabaseFilter.setImage(new Image(getClass().getResourceAsStream("/images/database.png")));
		imgViewTableFilter.setImage(new Image(getClass().getResourceAsStream("/images/b_sbrowse.png")));
		imgViewReloadDatabaseList.setImage(new Image(getClass().getResourceAsStream("/images/reload.png")));
                
                exploreTablesTabPane.setSceneFactory(new Callback<DetachableTabPane, Scene>() {
			@Override
			public Scene call(DetachableTabPane p) {
				//create your scene here
				Label lbl = new Label("Exploring by JRelEx");
				lbl.setId("JRelEx");
				p.setPrefSize(600, 400);
				VBox vbox = new VBox();
//			hbox
				HBox hbox = new HBox();
				hbox.setAlignment(Pos.CENTER_RIGHT);
				HBox.setHgrow(p, Priority.ALWAYS);
				hbox.setPrefHeight(-1.0);
				hbox.setPrefWidth(-1.0);
				hbox.setSpacing(10.0);
				hbox.setPadding(new Insets(10));
				hbox.getStyleClass().add("top-panel");
				hbox.getChildren().add(lbl);
				
				
				vbox.getChildren().add(hbox);
				vbox.getChildren().add(p);
				VBox.setVgrow(p, Priority.ALWAYS);
				Scene scene = new Scene(vbox);
				return scene;
			}
		});
		
	}
	
	public void filterDatabasesFromTreeView(KeyEvent event){
		if(event.getCode().equals(KeyCode.ENTER)){
			this.DBFilterValue = ((TextField)event.getSource()).getText();
			this.refreshTreeViewDatabaseList(Common.getInstance()
				.getDbstore().getDatabases());
		}
	}
	
	public void filterTablesFromTreeView(KeyEvent event){
		if(event.getCode().equals(KeyCode.ENTER)){
			this.TBLFilterValue = ((TextField)event.getSource()).getText();
			this.refreshTreeViewDatabaseList(Common.getInstance()
				.getDbstore().getDatabases());
		}
	}
	
	public void reloadDatabaseTree(MouseEvent event){
		this.DBFilterValue = txtDatabaseFilter.getText();
		this.TBLFilterValue = txtTableFilter.getText();
		this.refreshTreeViewDatabaseList(Common.getInstance()
				.getDbstore().getDatabases());
	}
	
	private boolean filterName(String name, String typedFilter){
		
		if(typedFilter.equals("")){
			return false;
		}
		
		name = name.toLowerCase();
		typedFilter = typedFilter.toLowerCase();
		
		if(name.startsWith(typedFilter)){
			return false;
		}
		return true;
	}
	
	/**
	 * Creates tree view for all databases that are stored in the application serialized file.
	 * User can select tables.
	 * 
	 * @param databases
	 */
	public void refreshTreeViewDatabaseList(HashMap<String, CDatabase> databases) {

		TreeItem<String> rootItem = new TreeItem<String>("Databases");

		Iterator<Map.Entry<String, CDatabase>> iterator = databases.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, CDatabase> pairs = iterator.next();

			CDatabase storedDatabase = pairs.getValue();
			
			// user can select to see database in tree view
			/*
			if(!storedDatabase.isVisible()){
				continue;
			}
			*/
			String storedDatabaseName = pairs.getKey();
			
			if(this.filterName(storedDatabaseName, this.DBFilterValue)){
				continue;
			}

			TreeItem<String> databaseItem = new TreeItem<>(pairs.getKey()
					+ " - " + storedDatabase.getName());
			ImageView imgViewDatabase = new ImageView(new Image(getClass().getResourceAsStream("/images/database.png")));
			databaseItem.setGraphic(imgViewDatabase);

			Map<String, CTable> sortedTables = new TreeMap<>(storedDatabase.getTables());
			if (sortedTables != null) {
				Iterator<Map.Entry<String, CTable>> iterator2 = sortedTables
						.entrySet().iterator();
				while (iterator2.hasNext()) {
					Map.Entry<String, CTable> entryTables = iterator2.next();
					
					String tableName = entryTables.getKey();
					CTable table = entryTables.getValue();
					
					// user can select to see database in tree view
					if(!table.isVisible()){
						continue;
					}
					
					if(this.filterName(tableName, this.TBLFilterValue)){
						continue;
					}

					TreeItem<String> tableItem = new TreeItem<>("");
					
					Hyperlink tablelink = new Hyperlink();
					Image imageColumn = new Image(getClass().getResourceAsStream(
							"/images/bsmall_sbrowse.png"));
					ImageView imageViewColumn = new ImageView(imageColumn);
					
					tablelink.setGraphic(imageViewColumn);
					tablelink.setText(tableName);
					tablelink.setId(pairs.getKey() + "." + entryTables.getKey());
					//tablelink.setPrefHeight(17);
					tableItem.setGraphic(tablelink);
					/*
					Label label = new Label(tableName);
					label.setMaxWidth(Double.MAX_VALUE);
					label.setId(pairs.getKey() + "." + entryTables.getKey());
					tableItem.setGraphic(label);
					 */
					tableItem.getGraphic().setOnMouseClicked(
							new EventHandler<MouseEvent>() {

								@Override
								public void handle(MouseEvent t) {
									
									Hyperlink tableItem = (Hyperlink) t.getSource();

									String nodeId = tableItem.getId();
									String[] analizedId = nodeId.split("\\.");
									String storedDatabase = analizedId[0];
									String tableName = analizedId[1];
									// System.out.println("---"+storedDatabase+"."+tableName);
									
									CDatabase database = Common.getInstance().getDbstore().getDatabases().get(storedDatabase);
									
									String databaseName = database.getName();
									CTable table = database.getTables().get(tableName);
									Connection conn = Common.getInstance().getDatabaseInteraction().getConnection(storedDatabase);
									
									showTableContent(exploreTablesTabPane, conn, table, databaseName, storedDatabase);
																											
								}

							});

					databaseItem.getChildren().add(tableItem);

				}
				if(!DBFilterValue.equals("") || !TBLFilterValue.equals("")){
					databaseItem.setExpanded(true);
				}
			}
			rootItem.getChildren().add(databaseItem);
			
		}
		rootItem.setExpanded(true);
		trvDatabaseList.setRoot(rootItem);
		trvDatabaseList.setShowRoot(false);
		

	}
	
	/**
	 * Create's table in tab pane exploreTablesTabPane
	 * 
	 * @param exploreTablesTabPane It's used to show tables from database
	 * @param conn Connection to database
	 * @param table Meta data from database table
	 * @param databaseName 
	 * @param storedDatabase
	 */
	public final void showTableContent(TabPane exploreTablesTabPane, Connection conn, CTable table, String databaseName, String storedDatabase){
	
                DatabaseExplorerTab databaseExplorerTab = new DatabaseExplorerTab(exploreTablesTabPane, 
                                            conn, table, databaseName, storedDatabase); 
            
		Tab tab = Common.getInstance().tabExists(table.getName(), exploreTablesTabPane);
		if(tab == null){
                    tab = databaseExplorerTab.getTab();
		} else {
                    tab.setContent(databaseExplorerTab.getTab().getContent());
                }
		exploreTablesTabPane.getSelectionModel().select(tab);
		
	}

}
