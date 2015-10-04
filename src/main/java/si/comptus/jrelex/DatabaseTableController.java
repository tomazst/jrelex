package si.comptus.jrelex;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CColumnTableReferences;
import si.comptus.jrelex.container.CPosition;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.sql.AbstractDynamicQuery;
import si.comptus.jrelex.sql.DynamicQueryMysql;
import si.comptus.jrelex.sql.DynamicQueryOracle;
import si.comptus.jrelex.sql.DynamicQuerySqlserver;

import com.panemu.tiwulfx.common.ExportToExcel;
import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.common.TableData;
import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import com.panemu.tiwulfx.table.BaseColumn;
import com.panemu.tiwulfx.table.TableControl;
import com.panemu.tiwulfx.table.TableController;

public class DatabaseTableController<T> extends TableController<T> {

	private static final Logger log = LoggerFactory
			.getLogger(DatabaseTableController.class);

	private Class<T> clazz;
	private String databaseName;
	private AbstractDynamicQuery dq;
	private List<TableCriteria<T>> filteredColumns = null;
	private String storedDatabaseName = "";
	private TableControl<T> exploreTable;
	private CTable databaseTable;
	
	public DatabaseTableController(TableControl<T> exploreTable,
			Connection conn, String databaseName, T obj, CTable databaseTable,
			String storedDatabaseName) {
		super();

		this.exploreTable = exploreTable;
		this.clazz = (Class<T>) obj.getClass();

		this.databaseName = databaseName;
		this.databaseTable = databaseTable;
		this.storedDatabaseName = storedDatabaseName;
		
		String vendor = Common.getInstance().getDbstore().getDatabases()
				.get(storedDatabaseName).getConnBean().getDriver();
		if (vendor.equalsIgnoreCase("MySQL")) {
			this.dq = new DynamicQueryMysql(conn);
		}

		if (vendor.equalsIgnoreCase("MSSQL")) {
			this.dq = new DynamicQuerySqlserver(conn);
		}

		if (vendor.equalsIgnoreCase("ORACLE")) {
			this.dq = new DynamicQueryOracle(conn);
		}
	}
	
	public List<TableCriteria<T>> getFilteredColumns() {
		return filteredColumns;
	}

	public void setFilteredColumns(List<TableCriteria<T>> filteredColumns) {
		this.filteredColumns = filteredColumns;
	}
		
	@Override
	public TableData loadData(int startIndex,
			List<TableCriteria> filteredColumns, List<String> sortedColumns,
			List<SortType> sortingOrders, int maxResult) {

		ArrayList<String> filters = new ArrayList<String>();

		if (this.filteredColumns != null) {
			for (TableCriteria<T> criteria : this.filteredColumns) {
				filteredColumns.add(criteria);
				filters.add(criteria.getAttributeName());
			}
			// then we empty local filters
			this.filteredColumns = null;
		}
/*
		// Hack - adding icons for foreign and primary keys
		for (TableColumn<T, ?> col : exploreTable.getTableView().getColumns()) { // go
																							// throo
																							// all
																							// columns
																							// of
																							// tableview
					//TableColumn<T, ?> col = obj;

					CColumn column = databaseTable.getColumnByName(col.getText()); // get
																					// database
																					// data
																					// for
																					// table
																					// column

					if (column.isForeignKey() || column.isPrimaryKey()) {

						boolean filterIsSet = false;
						for (TableCriteria<T> criteria : filteredColumns) {
							if (criteria.getAttributeName().equals(column.getName())) {
								filterIsSet = true;
							}
						}

						if (filterIsSet) {
							continue;
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

					}
				}
*/
		ResultSet rs = this.dq.getTableData(databaseTable, databaseName,
				this.storedDatabaseName, filteredColumns, sortedColumns,
				sortingOrders, startIndex, maxResult);

		ArrayList<T> data = new ArrayList<T>();

		int position = startIndex;
		// table data
		// ResultSet rs = rc.intoResultSet();
		// rc = null;
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			
			// we get number of referenced columns (primary and foreign) for
			// initialize capacity for hashmap
			int count = 0;
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (databaseTable.getColumnByName(rsmd.getColumnName(i))
						.getReferences() != null) {
					count++;
				}
			}
			
			// calculate max length from position numbers
			Integer maxPosition = position+maxResult;
			int maxPositionStringLength = maxPosition.toString().length();

			int n = 0;
			while (rs.next()) { // table row
				T obj=null;
				HashMap<String, CColumnTableReferences> references = new HashMap<>(count);
				try {

					obj = clazz.newInstance();
					
					for (int i = 1; i <= rsmd.getColumnCount(); i++) { // columns
						int type = rsmd.getColumnType(i);

						String columnName = rsmd.getColumnName(i);
						/*
						 * If column has references to other tables then we
						 * save and ad it to the data
						 */
						 if(databaseTable.getColumnByName(columnName).getReferences() != null) {
						  
							 CColumnTableReferences referenceObj = new CColumnTableReferences();
							 referenceObj.setStoredDatabaseName(storedDatabaseName);
							 referenceObj.setDatabaseName(databaseName);
							 referenceObj.setTableName(databaseTable.getName());
							 referenceObj.setColumn(databaseTable.getColumnByName(columnName
							 )); referenceObj.setValue(rs.getString(i));
							 referenceObj.setDq(this.dq);
							 references.put(columnName, referenceObj);
						 
						 }
						 
						switch (type) {
						case Types.INTEGER:
						case Types.TINYINT:
						case Types.SMALLINT:
						case Types.BIGINT:
							PropertyUtils.setProperty(obj, columnName,
									rs.getInt(columnName));
							break;
						case Types.FLOAT:
						case Types.DECIMAL:
						case Types.NUMERIC:
							PropertyUtils.setProperty(obj, columnName,
									rs.getFloat(columnName));
							break;
						case Types.DOUBLE:
							PropertyUtils.setProperty(obj, columnName,
									rs.getDouble(columnName));
							break;
						case Types.BOOLEAN:
						case Types.BIT:
							PropertyUtils.setProperty(obj, columnName,
									rs.getBoolean(columnName));
							break;
						case Types.DATE:
							PropertyUtils.setProperty(obj, columnName,
									rs.getDate(columnName));
							break;
						case Types.TIMESTAMP:
							// TODO tiwulfx table does't support TIMESTAMP
							Timestamp ts = rs.getTimestamp(columnName);
							if (ts == null) {
								PropertyUtils.setProperty(obj, columnName, ts);
							} else {
								PropertyUtils.setProperty(obj, columnName,
										ts.toString());
							}
							break;
						case Types.TIME:
							// TODO tiwulfx table does't support TIME
							Time time = rs.getTime(rsmd.getColumnName(i));
							if (time == null) {
								PropertyUtils
										.setProperty(obj, columnName, time);
							} else {
								PropertyUtils.setProperty(obj, columnName,
										time.toString());
							}
							break;
						default:
							PropertyUtils.setProperty(obj, columnName,
									rs.getString(columnName));

						}

					}

					
					PropertyUtils.setProperty(obj, "references", references);
					CPosition positionObj = new CPosition();
					positionObj.setPosition(++position);
					positionObj.setMaxPositionStringLength(maxPositionStringLength);
					PropertyUtils.setProperty(obj, "position", positionObj);
					

				} catch (InstantiationException | NoSuchMethodException
						| InvocationTargetException | IllegalAccessException ex) {
					log.error(ex.getMessage(), ex);
					MessageDialogBuilder.error(ex).show(null);
				}
				data.add(n, obj);
				n++;
			}
			
			rs.close();
			
		} catch (SQLException ex) {
			log.error(ex.getMessage(), ex);
			MessageDialogBuilder.error(ex).show(null);
		}
		
		

		int countAll = dq.getRecordCount(databaseTable, databaseName,
				filteredColumns);
		
		boolean moreRows=false;
		if(startIndex < countAll){
			moreRows=true;
		}
		
		return new TableData<T>(data, moreRows, countAll);

	}

	@Override
	public void exportToExcel(String title, int maxResult, TableControl<T> tblView, List<TableCriteria> lstCriteria) {
		try {
            ExportToExcel exporter = new ExportToExcel();
            List<Double> lstWidth = new ArrayList<>();
            List<T> data = new ArrayList<>();
            List<String> lstSortedColumn = new ArrayList<>();
            List<SortType> lstSortedType = new ArrayList<>();

            for (TableColumn<T, ?> tc : tblView.getTableView().getSortOrder()) {
                if (tc instanceof BaseColumn) {
                    lstSortedColumn.add(((BaseColumn) tc).getPropertyName());
                } else {
                    PropertyValueFactory valFactory = (PropertyValueFactory) tc.getCellValueFactory();
                    lstSortedColumn.add(valFactory.getProperty());
                }
                lstSortedType.add(tc.getSortType());
            }

            TableData vol;
            int startIndex2 = 0;

            do {
                vol = loadData(startIndex2, lstCriteria, lstSortedColumn, lstSortedType, maxResult);
                data.addAll((ObservableList<T>) FXCollections.observableArrayList(vol.getRows()));
                startIndex2 = startIndex2 + maxResult;
            } while (vol.isMoreRows());

            String tmpFolder = System.getProperty("java.io.tmpdir");
            File targetFile = File.createTempFile(tblView.getTableView().getId(), ".xls", new File(tmpFolder));
            
            exporter.export(tblView.getTableView().getId(), targetFile.getAbsolutePath(), tblView, data, lstWidth);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
	}
	
}
