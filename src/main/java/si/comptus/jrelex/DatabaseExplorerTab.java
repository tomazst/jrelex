package si.comptus.jrelex;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javafx.event.EventHandler;
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
import com.panemu.tiwulfx.table.BaseColumn;
import com.panemu.tiwulfx.table.DateColumn;
import com.panemu.tiwulfx.table.NumberColumn;
import com.panemu.tiwulfx.table.TableControl;
import com.panemu.tiwulfx.table.TextColumn;
import javafx.scene.control.Button;

public class DatabaseExplorerTab<T> {
	
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseExplorerTab.class);
	private TableControl<T> exploreTable;
	
	private Tab tab;
	
	public DatabaseExplorerTab(TabPane tabPaneExploreDatabases, 
			Connection conn, 
			CTable table, 
			String databaseName, 
			String storedDatabaseName){
		this.showTable(tabPaneExploreDatabases, conn, table, databaseName, storedDatabaseName, null);		
	}
	
	public DatabaseExplorerTab(TabPane tabPaneExploreDatabases, 
			Connection conn, 
			CTable table, 
			String databaseName, 
			String storedDatabaseName,
			List<TableCriteria<T>> filteredColumns){
		this.showTable(tabPaneExploreDatabases, conn, table, databaseName, storedDatabaseName, filteredColumns);		
	}
	
	public void showTable(TabPane tabPaneExploreDatabases, 
								Connection conn, 
								CTable table, 
								String databaseName, 
								String storedDatabaseName,
								List<TableCriteria<T>> filteredColumns
	){
		
		Object obj =  Common.getInstance().getPojoGenerator().createPojoForTable(table.getName(), table);
		
		// create table
		exploreTable = new TableControl(obj.getClass());
		exploreTable.setVisibleComponents(false, TableControl.Component.BUTTON_EDIT, 
                                                            TableControl.Component.BUTTON_DELETE,
                                                            TableControl.Component.BUTTON_SAVE,
                                                            TableControl.Component.BUTTON_INSERT,
                                                            TableControl.Component.BUTTON_RELOAD
							);
                //Button b = exploreTable.get            
		/*
		Button btnUnsetFilter = new Button("Unset filter");
		btnUnsetFilter.setTooltip(new Tooltip("Unset filter"));
		btnUnsetFilter.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				exploreTable.clearTableCriteria();
				exploreTable.reload();
				
			}
		});
		exploreTable.addButton(btnUnsetFilter);
		*/
                
		exploreTable.getTableView().setId(table.getName());
		
		// get data controller
		DatabaseTableController controller = new DatabaseTableController(exploreTable, conn, databaseName, obj, table, storedDatabaseName);
		if(filteredColumns != null){
			controller.setFilteredColumns(filteredColumns);
		}
		exploreTable.setController(controller);
		
		Map<String, Class<?>> tableProperties = Common.getInstance().getPojoGenerator()
				.getLastPojoProperties();

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
		referCol.setCellFactory(new Callback<TableColumn, TableCell>() {
			public TableCell call(TableColumn col) {
				return new ReferencesCell<>();
			}
		});
				
		referCol.setText("");
		referCol.setSortable(false);
		referCol.setFilterable(false);
					
		exploreTable.addColumn(referCol);	
				
		for (CColumn column: table.getColumns()) {
			// user can select to see column
			if(!column.isVisible()){
				continue;
			}
			
			Class clazz = tableProperties.get(column.getName());
			BaseColumn col = null;
			
			if (clazz.equals(Integer.class) || clazz.equals(Float.class)
					|| clazz.equals(Double.class) || clazz.equals(Float.class)) {
				col = (BaseColumn) new NumberColumn(column.getName(), clazz);
				col.setFilterable(true);
			} else if (clazz.equals(Date.class)) {
				DateColumn dateCol = new DateColumn(column.getName());
				dateCol.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
				col = (BaseColumn) dateCol;
				col.setId("dateColumn");
				col.setFilterable(true);
			} else {
				col = (BaseColumn)new TextColumn(column.getName());
				col.setFilterable(true);
			}
			
			
			if (column.isPrimaryKey()) {
				col.setGraphic(new ImageView(
						new Image(getClass().getResourceAsStream(
								"/images/primary-key.png"))
						));
			}
			if (column.isForeignKey()) {
				col.setGraphic(new ImageView(
						new Image(getClass().getResourceAsStream(
								"/images/foreign-keys.png"))
						));
			}
			
			exploreTable.addColumn(col);		

		}
		
		//exploreTable.getSelectionModel().cellSelectionEnabledProperty().unbind();
		//exploreTable.getSelectionModel().setCellSelectionEnabled(true);
		
                //exploreTable.setSelectionMode();
		//exploreTable.setSelectionMode(SelectionMode.MULTIPLE);
		exploreTable.setMaxRecord(Common.getInstance().getDbstore()
				.getAppSettings().getNumberRowsToDisplay());
		exploreTable.reloadFirstPage();
                //exploreTable.setAgileEditing(true);
		
		String text = table.getName();
		tab = Common.getInstance().tabExists(text, tabPaneExploreDatabases); 
		if(tab == null){
			tab = new Tab();
			tab.setText(text);
			tabPaneExploreDatabases.getTabs().add(tab);
		}
		
		//exploreTable.getTableView().setOnMouseReleased(tableRightClickListener);
		
		tab.setContent(exploreTable);
		//tabPaneExploreDatabases.getSelectionModel().select(tab);
		
	}
	
	private EventHandler<MouseEvent> tableRightClickListener = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (event.getButton().equals(MouseButton.SECONDARY)) {
				if (exploreTable.getTableView().getSelectionModel().getSelectedCells().isEmpty()) {
					return;
				}
				System.out.println("HMMM!");
				System.out.println(exploreTable.getTableView().getSelectionModel().getSelectedCells().get(0).getClass());
				
				TablePosition pos = exploreTable.getTableView().getSelectionModel().getSelectedCells().get(0);
				
				System.out.println(pos.getTableColumn().getClass());
				
				if (pos.getTableColumn() instanceof TableColumn){ // BaseColumn) {
					System.out.println("Kliknil si miško!");
				}
			}
		}
	};

	public Tab getTab() {
		return tab;
	}
	
	
	
}
