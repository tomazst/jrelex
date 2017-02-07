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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.container.CColumnTableReferences;
import si.comptus.jrelex.container.CPosition;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.database.DynamicQueryAbstract;

import com.panemu.tiwulfx.common.ExportToExcel;
import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.common.TableData;
import com.panemu.tiwulfx.dialog.MessageDialogBuilder;
import com.panemu.tiwulfx.table.BaseColumn;
import com.panemu.tiwulfx.table.TableControl;
import com.panemu.tiwulfx.table.TableController;
import si.comptus.jrelex.configuration.RDBMSType;
import si.comptus.jrelex.database.DynamicQueryFactory;

/**
 *
 * @author tomaz
 *
 * @param <T> record (table row)
 * @param <V> value (table cell value)
 */
public class DatabaseTableController<T, V> extends TableController<T> {

    private static final Logger log = LoggerFactory
            .getLogger(DatabaseTableController.class);

    private Class<T> clazz;
    private String databaseName;
    private DynamicQueryAbstract<V> dq;
    private List<TableCriteria<V>> filteredColumns = null;
    private String storedDatabaseName = "";
    private TableControl<T> exploreTable;
    private CTable databaseTable;

    public DatabaseTableController(TableControl<T> exploreTable,
            Connection conn, String databaseName, Object obj, CTable databaseTable,
            String storedDatabaseName) throws JRelExException {
        super();

        this.exploreTable = exploreTable;
        this.clazz = (Class<T>) obj.getClass();

        this.databaseName = databaseName;
        this.databaseTable = databaseTable;
        this.storedDatabaseName = storedDatabaseName;

        RDBMSType vendor = Common.getInstance().getDbstore().getDatabases()
                .get(storedDatabaseName).getConnBean().getDriver();

        this.dq = DynamicQueryFactory.getDynamicQuery(vendor, conn);

    }

    public List<TableCriteria<V>> getFilteredColumns() {
        return filteredColumns;
    }

    public void setFilteredColumns(List<TableCriteria<V>> filteredColumns) {
        this.filteredColumns = filteredColumns;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TableData<T> loadData(int startIndex,
            List<TableCriteria> filteredColumns, List<String> sortedColumns,
            List<SortType> sortingOrders, int maxResult) {

        ArrayList<String> filters = new ArrayList<>();

        if (this.filteredColumns != null) {
            for (TableCriteria<V> criteria : this.filteredColumns) {
                filteredColumns.add(criteria);
                filters.add(criteria.getAttributeName());
            }
            // then we empty local filters
            this.filteredColumns = null;
        }

        // Want to be Generic
        List<TableCriteria<V>> filteredColumnsG = new ArrayList<>();
        for(@SuppressWarnings("rawtypes") TableCriteria criteria : filteredColumns){
            filteredColumnsG.add((TableCriteria<V>)criteria);
        }

        String sql = this.dq.getSqlForTableData(databaseTable, databaseName,
                this.storedDatabaseName, filteredColumnsG, sortedColumns,
                sortingOrders, startIndex, maxResult);

        ArrayList<T> data = new ArrayList<>();
        int position = startIndex;

        try (Statement stmt = Common.getInstance().getDatabaseInteraction()
                .getConnection(this.storedDatabaseName).createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

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
            Integer maxPosition = position + maxResult;
            int maxPositionStringLength = maxPosition.toString().length();

            int n = 0;

            while (rs.next()) { // table row
                T obj = null;
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
                        if (databaseTable.getColumnByName(columnName).getReferences() != null) {

                            CColumnTableReferences referenceObj = new CColumnTableReferences();
                            referenceObj.setStoredDatabaseName(storedDatabaseName);
                            referenceObj.setDatabaseName(databaseName);
                            referenceObj.setTableName(databaseTable.getName());
                            referenceObj.setColumn(databaseTable.getColumnByName(columnName
                            ));
                            referenceObj.setValue(rs.getString(i));
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
                                PropertyUtils.setProperty(obj, columnName,
                                        rs.getBoolean(columnName));
                            case Types.BIT:
                                byte b = rs.getByte(columnName);
                                Boolean tmpVar = true;
                                if(b == 0){
                                    tmpVar = false;
                                }
                                PropertyUtils.setProperty(obj, columnName,
                                        tmpVar);
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

                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    log.error(ex.getMessage(), ex);
                    MessageDialogBuilder.error(ex).show(null);
                }
                data.add(n, obj);
                n++;
            }

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            MessageDialogBuilder.error(e).show(null);
        }

        int countAll = dq.getRecordCount(databaseTable, databaseName,
                filteredColumnsG);

        boolean moreRows = false;
        if (startIndex < countAll) {
            moreRows = true;
        }

        return new TableData<T>(data, moreRows, countAll);

    }

    @Override
    public void exportToExcel(String title, int maxResult, TableControl<T> tblView, List<TableCriteria> lstCriteria) {
        try {
            ExportToExcel<T> exporter = new ExportToExcel<>();
            List<Double> lstWidth = new ArrayList<>();
            List<T> data = new ArrayList<>();
            List<String> lstSortedColumn = new ArrayList<>();
            List<SortType> lstSortedType = new ArrayList<>();

            for (TableColumn<T, ?> tc : tblView.getTableView().getSortOrder()) {
                if (tc instanceof BaseColumn) {
                    lstSortedColumn.add(((BaseColumn<T, ?>) tc).getPropertyName());
                } else {
                    PropertyValueFactory<T, V> valFactory = (PropertyValueFactory) tc.getCellValueFactory();
                    lstSortedColumn.add(valFactory.getProperty());
                }
                lstSortedType.add(tc.getSortType());
            }

            TableData<T> vol;
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
