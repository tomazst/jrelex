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

package si.comptus.jrelex.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.formula.functions.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.Common;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CReferenceData;
import javafx.scene.control.TableColumn;

import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.common.TableCriteria.Operator;

/**
 * @author tomaz
 *
 * @param <T> Type of table cell value.
 */
public abstract class DynamicQueryAbstract<T> {

    private static final Logger log = LoggerFactory
            .getLogger(DynamicQueryAbstract.class);
    private Connection conn;

    private ArrayList<String> getSQLNumericTypes(){
        ArrayList<String> types = new ArrayList<>();
        types.add("DECIMAL");
        types.add("DOUBLE");
        types.add("FLOAT");
        types.add("NUMERIC");
        types.add("REAL");
        types.add("BIGINT");
        types.add("INTEGER");
        types.add("SMALLINT");
        types.add("TINYINT");
        return types;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public boolean isNumericType(String type){
        ArrayList<String> types = this.getSQLNumericTypes();
        if(types.contains(type)){
            return true;
        }
        return false;
    }

    public void setKeys(String storedDatabaseName,
            CReferenceData rdata
            ){

        if(Common.getInstance().getDbstore().getDatabases()
                .get(storedDatabaseName).getTables().containsKey(rdata.getTableName())){ // check if user has access to table

            CColumn column = Common.getInstance().getDbstore().getDatabases()
                    .get(storedDatabaseName).getTables()
                    .get(rdata.getTableName()).getColumnByName(rdata.getColumnName());

            rdata.setColumnForeignKey(column.isForeignKey());
            rdata.setColumnPrimaryKey(column.isPrimaryKey());
        }


    }

    abstract public HashMap<String, CReferenceData> getReferencedData(
            String storedDatabaseName, String databaseName, String tableName,
            CColumn column, String value);

    abstract public PreparedStatement getPrepStmtTableData(CTable table,
            String databaseName,
            String storedDatabaseName,
            List<TableCriteria<T>> filteredColumns,
            List<String> sortedColumns,
            List<TableColumn.SortType> sortingOrders,
            int startIndex,
            int maxResult) throws SQLException;

    abstract public PreparedStatement getPrepStmtTableData(CTable table,
            String databaseName,
            String storedDatabaseName,
            List<TableCriteria<T>> filteredColumns) throws SQLException;

    abstract public int getRecordCount(CTable table, String databaseName,
            List<TableCriteria<T>> filteredColumns);

    protected ArrayList<String> conditions(CTable table, List<TableCriteria<T>> filteredColumns) {

        ArrayList<String> conditions = new ArrayList<>(filteredColumns.size());

        for (TableCriteria<? extends Object> filteredColumn : filteredColumns) {

            String condition = "";

            Operator operator = filteredColumn.getOperator();
            String fieldName = filteredColumn.getAttributeName();


            switch (operator) {
            case eq:
                condition += fieldName+" = ?";
                break;
            case ne:
                condition += fieldName+" != ?";
                break;
            case le:
                condition += fieldName+" <= ?";
                break;
            case lt:
                condition += fieldName+" < ?";
                break;
            case ge:
                condition += fieldName+" >= ?";
                break;
            case gt:
                condition += fieldName+" > ?";
                break;
            case like_begin:
                condition += fieldName+" LIKE ?";
                break;
            case like_anywhere:
                condition += fieldName+" LIKE ?";
                break;
            case like_end:
                condition += fieldName+" LIKE ?";
                break;
            case ilike_begin:
                condition += fieldName+" LIKE ?";
                break;
            case ilike_anywhere:
                condition += fieldName+" LIKE ?";
                break;
            case ilike_end:
                condition += fieldName+" LIKE ?";
                break;
            case is_null:
                condition += fieldName+" IS NULL";
                break;
            case is_not_null:
                condition += fieldName+" IS NOT NULL";
                break;
            case in:
                condition += fieldName+" IN (?)";
                break;
            case not_in:
                condition += fieldName+" NOT IN (?)";
                break;
            default:
            }
            conditions.add(condition);
        }
        return conditions;
    }

    protected PreparedStatement getPrepStmtWithValues(String sql, List<TableCriteria<T>> filteredColumns) throws SQLException{
        PreparedStatement stmt = this.getConn().prepareStatement(sql);
        this.setWhereValues(stmt, filteredColumns);
        return stmt;
    }

    private void setWhereValues(PreparedStatement stmt, List<TableCriteria<T>> filteredColumns) throws SQLException {
        int n = 1;
        for (TableCriteria<? extends Object> filteredColumn : filteredColumns) {
            Operator operator = filteredColumn.getOperator();
            if(operator.name().equals("is_null")
                    || operator.name().equals("is_not_null")) { // don't add values
                continue;
            }

            String prefix = "";
            String suffix = "";
            switch (operator) {
                case like_begin:
                    suffix = "%";
                    break;
                case like_anywhere:
                    prefix = "%";
                    suffix = "%";
                    break;
                case like_end:
                    prefix = "%";
                    break;
                case ilike_begin:
                    suffix = "%";
                    break;
                case ilike_anywhere:
                    prefix = "%";
                    suffix = "%";
                    break;
                case ilike_end:
                    prefix = "%";
                    break;
                default:
            }

            if(filteredColumn.getValue() instanceof String) {
                // Check if it is date or datetime!
                Pattern p = Pattern.compile("....-..-.. .*");
                Matcher m = p.matcher(filteredColumn.getValue().toString());
                log.debug(filteredColumn.getValue().toString());
                if(m.matches()){
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                        Date tmpDate = formatter.parse(filteredColumn.getValue().toString());
                        stmt.setTimestamp(n, new java.sql.Timestamp(tmpDate.getTime()));
                        log.debug(formatter.format(tmpDate));
                    }catch (ParseException e){
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date tmpDate = formatter.parse(filteredColumn.getValue().toString());
                            stmt.setDate(n, new java.sql.Date(tmpDate.getTime()));
                            log.debug(formatter.format(tmpDate));
                        } catch (ParseException e2){
                            // it's string
                            stmt.setString(n, prefix + filteredColumn.getValue().toString() + suffix);
                            log.debug(filteredColumn.getValue().toString());
                        }
                    }
                } else {
                    stmt.setString(n, prefix + filteredColumn.getValue().toString() + suffix);
                    log.debug(filteredColumn.getValue().toString());
                }


            }
            if(filteredColumn.getValue() instanceof Date) {
                stmt.setDate(n, (java.sql.Date)filteredColumn.getValue());
            }
            if(filteredColumn.getValue() instanceof Boolean) {
                stmt.setBoolean(n, (Boolean)filteredColumn.getValue());
            }
            if(filteredColumn.getValue() instanceof Integer) {
                stmt.setInt(n, (Integer)filteredColumn.getValue());
            }
            if(filteredColumn.getValue() instanceof Float) {
                stmt.setFloat(n, (Float)filteredColumn.getValue());
            }
            if(filteredColumn.getValue() instanceof Double) {
                stmt.setDouble(n, (Double)filteredColumn.getValue());
            }
            if(filteredColumn.getValue() instanceof Double) {
                stmt.setDouble(n, (Double)filteredColumn.getValue());
            }
            n++;
        }
    }

    /**
     * because we want the unique name of the field name is made from table name,
     * column name, foreign table name and foreign column name
     * @return String
     */
    public String getRefId(String table, String column, String refTable, String refColumn){
        return table+"_"+column+"_"+refTable+"_"+refColumn;
    }

    public String getColumnAlias(int i){
        String alias ="C"+i;
        return alias;
    }

}
