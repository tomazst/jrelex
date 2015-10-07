package si.comptus.jrelex;

import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CDatabase;
import si.comptus.jrelex.container.CDatabaseStore;
import si.comptus.jrelex.container.CReference;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.container.ConnBean;

import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import java.util.logging.Level;
import javafx.collections.FXCollections;

public class DatabaseMetaDataController implements Initializable {

	@FXML
	private ComboBox<String> cbDbms;
	@FXML
	private TextField txfName;
	@FXML
	private ComboBox<String> cbPort;
	@FXML
	private TextField txfHostname;
	@FXML
	private TextField txfUsername;
	@FXML
	private ComboBox<String> cbDatabase;
	@FXML
	private PasswordField pwfPassword;
	@FXML
	private TreeView trvDatabaseList;
	@FXML
	private Button btnSave;

        private DatabaseConnDataVO connVO;
	
	@FXML
	private ProgressBar progressBar;
	@FXML
	private TextArea txtProgress;
	
	@FXML
	private Button btnSaveSelection;
	@FXML
	private Button btnRevertSelection;
	
	@FXML
	private TextField txtDatabaseFilter;
	@FXML
	private TextField txtTableFilter;
	
	public String DBFilterValue="";
	public String TBLFilterValue="";
	
	/*
	 * labels for error reporting
	 */
	public Label lblDbms;
	public Label lblName;
	public Label lblHostname;
	public Label lblPort;
	public Label lblUsername;
	public Label lblPassword;
	public Label lblDatabase;
	
	private HashMap<String, Image> imageHash;
		
	private static final Logger log = LoggerFactory.getLogger(DatabaseMetaDataController.class);
	

	/**
	 * Initializes the controller class.
	 */
	public void initialize(URL url, ResourceBundle rb) {
		connVO = new DatabaseConnDataVO();
		refreshTreeViewDatabaseList(Common.getInstance().getDbstore().getDatabases());
		trvDatabaseList.getSelectionModel().setSelectionMode(
				SelectionMode.SINGLE);
		 
		trvDatabaseList.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
		
		trvDatabaseList.setEditable(true);
		trvDatabaseList.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Object>() {

					public void changed(ObservableValue<?> ov, Object t, Object t1) {
						TreeItem<?> treeItem = (TreeItem<?>) t1;
						if (treeItem != null) {
							if (treeItem.getParent() != null) {
								if (treeItem.getParent().getValue() == "Databases") {
									String itemValue = (String) treeItem
											.getValue();
									// we get index of saved database									
									String[] values = itemValue
											.split(" \\(");
									fillDatabaseManageForm(values[1].replace(")", ""));
								}
							}
						}
					}
				});
		
		cbDatabase.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				
				
			}
		});
	}
        
        public void saveSelection() {
            CDatabaseStore dbStore = Common.getInstance().getDbstore();
            Common.getInstance().saveSerializedDBMetaDataToDisk(dbStore);
        }
        
        public void revertSelection() {
            Common.getInstance().setDbstore();
            refreshTreeViewDatabaseList(Common.getInstance().getDbstore().getDatabases());
        }
        
        public void reloadDatabaseList(){
            List<String> dbList = new ArrayList<>();
            connVO = this.extractDataFromForm(connVO);
            
            DatabaseInteraction dbInteraction = Common.getInstance()
				.getDatabaseInteraction();

            try {
                dbList = dbInteraction.getDatabaseList(connVO);
            } catch (Exception ex) {
                log.error("Error while connecting to database", ex);
            }
            
            cbDatabase.setItems(FXCollections.observableList(dbList));
            cbDatabase.show();
        }

	public void newConnection() {
		
		cbDbms.setValue("");
		txfName.setText("");
		txfHostname.setText("");
		cbPort.setValue("0000");
		txfUsername.setText("");
		cbDatabase.setValue("");
		pwfPassword.setText("");
		
	}
	
	public void saveConnection() {

		if (isFormValid()) {
			//if (false) {
			//log.info("form is valid");
			
			
			Connection conn = null;
			try {
				conn = connectToDatabase(connVO);
			} catch (Exception e) {
				log.error("Error while connecting to database", e);
				MessageDialogBuilder.error(e).show(null);
				return;
			}
			
			final Connection connection = conn;
                        
                        //readAndSaveDBMetaData(connVO, connection);
			// crate tread
			Task<Void> task = new Task<Void>(){
                            @Override
                            public Void call() throws Exception  {
                                btnSave.setDisable(true);
                                try {
                                    readAndSaveDBMetaData(connVO, connection);
                                } catch (final Exception e) {						
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            log.error("Error while saving database meta data!", e);
                                            MessageDialogBuilder.error(e).show(null);
                                        }
                                    });
                                }
                                btnSave.setDisable(false);
                                return null;                
                            }
                            
                            @Override
                            protected void succeeded(){
                                super.succeeded();
                                refreshTreeViewDatabaseList(Common.getInstance()
                                    .getDbstore().getDatabases());
                            }
                        };
                    Thread th = new Thread(task);
                    th.setDaemon(true);
                    th.start();
		}
	}
	
	private boolean isFormValid(){
            if(this.validateForm()){
                this.extractDataFromForm(connVO);
                return true;
            }
            return false;
	}
        
        private boolean validateForm(){
            FormValidator.getInstance().setFormValid(true);
            FormValidator.getInstance().isTextEmpty(cbDbms.getValue(), lblDbms, "DBMS is empty!");
            FormValidator.getInstance().isTextEmpty(txfName.getText(), lblName, "Name is empty!");
            FormValidator.getInstance().isTextEmpty(txfHostname.getText(), lblHostname, "Hostname is empty!");
            if(cbPort.getValue() == null){
                    FormValidator.getInstance().isTextEmpty(cbPort.getEditor().getText(), lblPort, "Port is empty!");
                    FormValidator.getInstance().isAllowedLength(cbPort.getEditor().getText(), 4, lblPort, "Only 4 numbers are allowed!");
                    FormValidator.getInstance().isNumeric(cbPort.getEditor().getText(), lblPort, "Only numbers are allowed!");
            } else {
                    FormValidator.getInstance().isTextEmpty(cbPort.getValue(), lblPort, "Port is empty!");
                    FormValidator.getInstance().isAllowedLength(cbPort.getValue(), 4, lblPort, "Only 4 numbers are allowed!");
                    FormValidator.getInstance().isNumeric(cbPort.getValue(), lblPort, "Only numbers are allowed!");
            }
            FormValidator.getInstance().isTextEmpty(txfUsername.getText(), lblUsername, "Username is empty!");
            //FormValidator.getInstance().isTextEmpty(pwfPassword.getText(), lblPassword, "Password is empty!");
            if(cbDatabase.getValue() == null){
                FormValidator.getInstance().isTextEmpty(
                        cbDatabase.getEditor().getText(),
                        lblDatabase, 
                        "Database is empty!");
            } else {
                FormValidator.getInstance().isTextEmpty(
                        cbDatabase.getValue(), 
                        lblDatabase, 
                        "Database is empty!"
                );
            }                
            return FormValidator.getInstance().isFormValid();
        }
        
        private DatabaseConnDataVO extractDataFromForm(DatabaseConnDataVO vo){
            vo.setDriver(cbDbms.getValue());
            vo.setName(txfName.getText());
            vo.setHostname(txfHostname.getText());
            if(cbPort.getValue() == null){
                vo.setPort(Integer.parseInt(cbPort.getEditor().getText()));
            } else {
                vo.setPort(Integer.parseInt(cbPort.getValue()));
            }
            vo.setUsername(txfUsername.getText());
            vo.setPassword(pwfPassword.getText());
            if(cbDatabase.getValue() == null){
                vo.setDatabase(cbDatabase.getEditor().getText());
            } else {
                vo.setDatabase(cbDatabase.getValue());
            }
            return vo;
        }

	public void deleteConnection() {
		Common.getInstance().getDbstore().getDatabases().remove(connVO.getName());
		Common.getInstance().saveSerializedDBMetaDataToDisk(Common.getInstance().getDbstore());
		newConnection();
		refreshTreeViewDatabaseList(Common.getInstance().getDbstore().getDatabases());
	}

	public void fillDatabaseManageForm(String storedDatabaseId) {
		CDatabase database = Common.getInstance().getDbstore().getDatabases()
				.get(storedDatabaseId);

		ConnBean connBean = database.getConnBean();
		
		cbDbms.setValue(connBean.getDriver());
		txfName.setText(connBean.getName());
		txfHostname.setText(connBean.getHostname());
		cbPort.setValue(String.valueOf(connBean.getPort()));
		txfUsername.setText(connBean.getUsername());
		pwfPassword.setText(connBean.getPassword());
		cbDatabase.setValue(connBean.getDatabase());
		
		connVO.setDriver(connBean.getDriver());
		connVO.setName(connBean.getName());
		connVO.setHostname(connBean.getHostname());
		connVO.setPort(connBean.getPort());
		connVO.setUsername(connBean.getUsername());
		connVO.setPassword(connBean.getPassword());
		connVO.setDatabase(connBean.getDatabase());

	}

	private void refreshTreeViewDatabaseList(
			HashMap<String, CDatabase> databases) {
		
		CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>("Databases");

		Map<String, CDatabase> sortedDatabases = new TreeMap<>(databases);
		
		Iterator<Map.Entry<String, CDatabase>> iterator = sortedDatabases.entrySet().iterator();
		/*
		imageHash = new HashMap<>();
		imageHash.put("database", new Image(getClass().getResourceAsStream("/images/database.png")));
		imageHash.put("table", new Image(getClass().getResourceAsStream("/images/bsmall_sbrowse.png")));
		imageHash.put("column", new Image(getClass().getResourceAsStream("/images/column.png")));
		*/
		while (iterator.hasNext()) {
			Map.Entry<String, CDatabase> pairs = (Map.Entry<String, CDatabase>)iterator.next();

			CDatabase storedDatabase = (CDatabase) pairs.getValue();
			
			String databaseName = storedDatabase.getName();
			
			if(this.filterName(databaseName, this.DBFilterValue)){
				continue;
			}
			
			CheckBoxTreeItem<String> databaseItem = new CheckBoxTreeItem<>(databaseName 
					+ " (" + pairs.getKey() + ")");
			
			//databaseItem.setGraphic(new Text("Hello"));
			
			
			databaseItem.setSelected(storedDatabase.isVisible());
			
			databaseItem.selectedProperty().addListener(new InvalidationListener() {
				
				@Override
				public void invalidated(Observable o) {					
					BooleanProperty property = (BooleanProperty)o;
					
					@SuppressWarnings("unchecked")
					TreeItem<String> item = (TreeItem<String>) property.getBean();
					String itemName = item.getValue();
					String[] splitedItemName = itemName.split(" \\(");
					String storedDatabase = splitedItemName[1].replace(")", "");
					Common.getInstance().getDbstore()
						.getDatabases().get(storedDatabase).setVisible(property.getValue());
				
				}
			});
			
			// databaseItem.checkBoxSelectionChangedEvent();

			Map<String, CTable> tables = storedDatabase.getTables();
			
			if (tables != null) {
				
				Map<String, CTable> sortedTables = new TreeMap<>(tables);
				Iterator<Map.Entry<String, CTable>> iterator2 = sortedTables.entrySet().iterator();
				while (iterator2.hasNext()) {
										
					Map.Entry<String, CTable> pairs2 = (Map.Entry<String, CTable>)iterator2.next();
					 
					CTable table = pairs2.getValue();
					
					String tableName = pairs2.getKey();
					
					if(this.filterName(tableName, this.TBLFilterValue)){
						continue;
					}
					
					//ImageView imgViewTable = new ImageView(imageHash.get("table"));
					CheckBoxTreeItem<String> tableItem = new CheckBoxTreeItem<String>(databaseName 
							+ "." + tableName);
					
					//tableItem.setGraphic(imgViewTable);
					tableItem.setSelected(table.isVisible());
					
					tableItem.selectedProperty().addListener(new InvalidationListener() {
						
						@Override
						public void invalidated(Observable o) {
							BooleanProperty property = (BooleanProperty)o;
							
							@SuppressWarnings("unchecked")
							TreeItem<String> item = (TreeItem<String>) property.getBean();
							String table = item.getValue().toString();
							String[] splitedTableName = table.split("\\.");
							String tableName = splitedTableName[1];
							
							TreeItem<String> itemParent = (TreeItem<String>) item.getParent();
							
							String itemParentName = itemParent.getValue().toString();
							
							String[] splitedItemParentName = itemParentName.split(" \\(");
							String storedDatabase = splitedItemParentName[1].replace(")", "");
							
							Common.getInstance().getDbstore()
								.getDatabases().get(storedDatabase).getTables()
								.get(tableName).setVisible(property.getValue());
							 
						}
					});
					
					List<CColumn> columns = table.getColumns();
					if(columns != null){
						
						for(CColumn column : columns){
							//ImageView imgViewColumn = new ImageView(imageHash.get("column"));
							CheckBoxTreeItem<String> columnItem = new CheckBoxTreeItem<String>(column.getName());
							
							columnItem.setSelected(column.isVisible());
							
							columnItem.selectedProperty().addListener(new InvalidationListener() {
								
								@Override
								public void invalidated(Observable o) {
									BooleanProperty property = (BooleanProperty)o;
									@SuppressWarnings("unchecked")
									TreeItem<String> item = (TreeItem<String>) property.getBean();
									String columnName = item.getValue().toString();
									
									TreeItem<String> itemParent = (TreeItem<String>) item.getParent();
									String table = itemParent.getValue().toString();
									String[] splitedTableName = table.split("\\.");
									String tableName = splitedTableName[1];
									
									String itemParentParentName = itemParent.getParent().getValue().toString();
									String[] spliteditemParentParentName = itemParentParentName.split(" \\(");
									String storedDatabase = spliteditemParentParentName[1].replace(")", "");
									
									Common.getInstance().getDbstore()
										.getDatabases().get(storedDatabase).getTables()
										.get(tableName).getColumnByName(columnName).setVisible(property.getValue());
								}
							});
							
							tableItem.getChildren().add(columnItem);
							
						}
					}
									
					databaseItem.getChildren().add(tableItem);
				}
			}
			
			rootItem.getChildren().add(databaseItem);
		}
		rootItem.setExpanded(true);
		trvDatabaseList.setRoot(rootItem);
		trvDatabaseList.setShowRoot(false);
		

	}
	
	private Connection connectToDatabase(DatabaseConnDataVO vo) throws Exception{
		DataSource ds = null;
		Connection connection = null;
			
		DatabaseInteraction dbInteraction = Common.getInstance()
				.getDatabaseInteraction();

		ds = dbInteraction.getDataSource(
				vo.getDriver(), 
				vo.getHostname(),
				vo.getPort(), 
				vo.getUsername(), 
				vo.getPassword(),
				vo.getDatabase()
				);
		
		connection = ds.getConnection();
		
		return connection;
	}
	
	/**
	 * Reads meta data from database, stores it to disk and returns it
	 * 
	 * @param bean
	 * @throws Exception 
	 */
	public void readAndSaveDBMetaData(DatabaseConnDataVO vo, Connection connection) {

		// we add informations on connection
		ConnBean bean = new ConnBean();
		bean.setDriver(vo.getDriver());
		bean.setName(vo.getName());
		bean.setHostname(vo.getHostname());
		bean.setPort(vo.getPort());
		bean.setUsername(vo.getUsername());
		bean.setPassword(vo.getPassword());
		bean.setDatabase(vo.getDatabase());
		
		// Collect meta data from database
		CDatabase metaData = this.readDBMetaData(bean, connection);
		metaData.setConnBean(bean);

		Common.getInstance().setDbstore();
		
		
		Common.getInstance().getDbstore().getDatabases()
				.put(bean.getName(), metaData);
		
		// Save meta data in to file
		if (Common.getInstance().saveSerializedDBMetaDataToDisk(
				Common.getInstance().getDbstore())) 
		{
			JOptionPane.showMessageDialog(null, "Sucsesss :) !!!");
		}
	}
		
	private CDatabase readDBMetaData(ConnBean bean, Connection connection){
		
		CDatabase dbcontainer = new CDatabase();
		try {
			
                    dbcontainer.setName(bean.getDatabase());

                    DatabaseMetaData databaseMetaData = connection.getMetaData();

                    String[] types = { "TABLE" };
                    ResultSet resultSetTables = databaseMetaData.getTables(connection.getCatalog(), null, null, types);
                    try {                   

                        ArrayList<CReference> tempReferences = new ArrayList<CReference>();

                        // count tables
                        int tableCount=0;
                        while(resultSetTables.next()) {
                            // we check if user has access. MSSQL has always dbo.
                            String TABLE_SCHEM = resultSetTables.getString(2);
                            if(TABLE_SCHEM != null && !TABLE_SCHEM.equalsIgnoreCase("dbo")){
                                    if(!TABLE_SCHEM.equalsIgnoreCase(bean.getUsername())){
                                            continue;
                                    }
                            }		

                            //we check if is table
                            String TYPE = resultSetTables.getString(4);
                            if(!TYPE.equals("TABLE")){
                                    continue;
                            }
                            
                            if(bean.getDriver().equals("ORACLE")){
                                if(resultSetTables.getString(3).startsWith("BIN")){ // Tables that begin on BIN are omited
                                    continue;
                                }
                            }
                            
                            tableCount++;
                        }

                        if(bean.getDriver().equals("MSSQL")) {// does not iterate the cursor
                            resultSetTables = databaseMetaData.getTables(connection.getCatalog(), null, null, types);
                        } else if(bean.getDriver().equals("ORACLE")){
                            resultSetTables = databaseMetaData.getTables(connection.getCatalog(), connection.getSchema(), null, types);
                        } else {
                            resultSetTables.beforeFirst();
                        }

                        // TABLES
                        dbcontainer.setTables(new HashMap<String, CTable>(tableCount));
                        int ix = 1;
                        txtProgress.setText("");
                        while(resultSetTables.next()) {

                            // we check if user has access
                            String TABLE_SCHEM = resultSetTables.getString(2);
                            if(TABLE_SCHEM != null && !TABLE_SCHEM.equalsIgnoreCase("dbo")){
                                    if(!TABLE_SCHEM.equalsIgnoreCase(bean.getUsername())){
                                            continue;
                                    }
                            }

                            //we check if is table
                            String TYPE = resultSetTables.getString(4);
                            if(!TYPE.equals("TABLE")){
                                
                                    continue;
                            }

                            String tableName = resultSetTables.getString(3); // get table name
                            if(bean.getDriver().equals("ORACLE")){
                                if(tableName.startsWith("BIN")){
                                    continue;
                                }
                            }
                            //log.info(ix+". Obdelujem tabelo "+tableName);
                            txtProgress.setText(txtProgress.getText()+"Reading table: "+tableName+" - "+ix+" of "+tableCount+"\n");
                            txtProgress.setScrollTop(Double.MAX_VALUE);
                            float progress = (float)ix / (float)tableCount;
                            progressBar.setProgress(progress);
                            ix++;

                            CTable ctable = new CTable();
                            ctable.setName(tableName);

                            // COLUMNS
                            ResultSet resultSetColumns = null;
                            int initialSize=0;
                            try  {
                                resultSetColumns = databaseMetaData.getColumns(
                                            connection.getCatalog(), 
                                            connection.getSchema(), tableName, null);
                                // get initial size for columns
                                while(resultSetColumns.next()){
                                        initialSize++;
                                }
                            }finally{
                                resultSetColumns.close();
                            }
                            
                            try {

                                // COLUMNS
                                /*if(bean.getDriver().equals("MSSQL") ||
                                        bean.getDriver().equals("ORACLE")) {*/
                                resultSetColumns = databaseMetaData.getColumns(
                                                connection.getCatalog(), 
                                                connection.getSchema(), tableName, null);
                                /*} else {
                                        resultSetColumns.beforeFirst();
                                }*/
                                /*
                                resultSetColumns = databaseMetaData.getColumns(
                                                connection.getCatalog(), 
                                                null, tableName, null);
                                */
                                ctable.setColumns(new ArrayList<CColumn>(initialSize));
                                ctable.setColumnNames(new ArrayList<String>(initialSize));

                                while(resultSetColumns.next()){
                                        CColumn ccolumn = new CColumn();

                                        ccolumn.setName(resultSetColumns.getString(4)); // column name
                                        Integer type = resultSetColumns.getInt(5);
                                        ccolumn.setType((String) Common.getInstance().getjDBCTypes().get(type)); // column type

                                        ctable.getColumns().add(ccolumn);
                                        ctable.getColumnNames().add(ccolumn.getName());

                                }
                            }finally{
                                resultSetColumns.close();
                            }
                                
                            //Collections.reverse(ctable.getColumnNames());

                            // we add primary key info
                            ResultSet resultSetPrimaryKeys = databaseMetaData
                                            .getPrimaryKeys(connection.getCatalog(), null, tableName);
                            try {
                                while (resultSetPrimaryKeys.next()){
                                        String columnName = resultSetPrimaryKeys.getString(4);
                                        ctable.getColumnByName(columnName).setPrimaryKey(true);
                                }
                            } finally {
                                resultSetPrimaryKeys.close();
                            }
                            //String catalog = connection.getCatalog();

                            ResultSet resultExportedKeys = databaseMetaData
                                            .getExportedKeys(connection.getCatalog(), null, tableName);
                            try {
                                while(resultExportedKeys.next()){
                                        String pKColumnName = resultExportedKeys.getString(4);

                                        CReference creference = new CReference();
                                        creference.setTable(resultExportedKeys.getString(3)); // pktable_name
                                        creference.setColumn(pKColumnName); // pkcolum_name
                                        creference.setReferencedTable(resultExportedKeys.getString(7)); // fktable_name
                                        creference.setReferencedColumn(resultExportedKeys.getString(8)); // fkcolum_name

                                        tempReferences.add(creference);

                                        //ctable.getColumnByName(pKColumnName).setForeignKey(true);

                                        if(ctable.getColumnByName(pKColumnName).getReferences() == null){
                                                ctable.getColumnByName(pKColumnName).setReferences(new ArrayList<CReference>());
                                        }
                                        ctable.getColumnByName(pKColumnName).getReferences().add(creference);
                                }
                            } finally {
                                resultExportedKeys.close();
                            }


                            ResultSet resultImportedKeys = databaseMetaData
                                            .getImportedKeys(connection.getCatalog(), null, tableName);
                            try {
                                while(resultImportedKeys.next()){
                                    String pKColumnName = resultImportedKeys.getString(4);
                                    String fKColumnName = resultImportedKeys.getString(8);

                                    CReference creference = new CReference();
                                    creference.setReferencedTable(resultImportedKeys.getString(3)); // pktable_name
                                    creference.setReferencedColumn(pKColumnName); // pkcolum_name
                                    creference.setTable(resultImportedKeys.getString(7)); // fktable_name
                                    creference.setColumn(fKColumnName); // fkcolum_name

                                    tempReferences.add(creference);

                                    ctable.getColumnByName(fKColumnName).setForeignKey(true);

                                    if(ctable.getColumnByName(fKColumnName).getReferences() == null){
                                            ctable.getColumnByName(fKColumnName).setReferences(new ArrayList<CReference>());
                                    }
                                    ctable.getColumnByName(fKColumnName).getReferences().add(creference);
                            }
                            }finally {
                                resultImportedKeys.close();
                            }

                            dbcontainer.getTables().put(ctable.getName(), ctable);	

                        }
                    }finally{
                        resultSetTables.close();
                    }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			txtProgress.setText(txtProgress.getText()+"Error while reading meta data!"+"\n"+e.getMessage());
			txtProgress.setScrollTop(Double.MAX_VALUE);
			log.error("Error while reading meta data!", e);
		}
		
		return dbcontainer;
	}
	
	public void filterDatabasesFromTreeView(KeyEvent event){
		TextField tf = (TextField)event.getSource();
		this.DBFilterValue = tf.getText();
		this.refreshTreeViewDatabaseList(Common.getInstance()
				.getDbstore().getDatabases());
	}
	
	public void filterTablesFromTreeView(KeyEvent event){
		TextField tf = (TextField)event.getSource();
		this.TBLFilterValue = tf.getText();
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

}
