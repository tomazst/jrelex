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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import si.comptus.jrelex.Common;
import si.comptus.jrelex.configuration.JRelExIterator;
import si.comptus.jrelex.configuration.RDBMSType;
import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CDatabase;
import si.comptus.jrelex.container.CReference;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.container.ConnBean;

/**
 * It collects database meta data.
 *
 * @author tomazst
 */
public class DatabaseMetaDataDAO {

    /**
     * Logger.
     */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DatabaseMetaDataDAO.class);

    private String username;
    private RDBMSType rdbmsType;
    private CDatabase databaseContainer = null;

    private DatabaseMetaData metaData = null;
    private String catalog = "";
    private String shema = "";

    private int tableCount = 0;

    private ResultSet resultSetTables;

    public DatabaseMetaDataDAO(Connection connection, ConnBean bean) throws SQLException {

        this.username = bean.getUsername();
        this.rdbmsType = bean.getDriver();

        this.metaData = connection.getMetaData();
        this.catalog = connection.getCatalog();
        this.shema = connection.getSchema();

        this.databaseContainer = new CDatabase();
        this.databaseContainer.setConnBean(bean);

        this.databaseContainer.setTables(
                new HashMap<String, CTable>(
                        this.getTableCount()
                )
        );

    }

    public JRelExIterator<CTable> Iterator() throws SQLException{
        String[] types = {"TABLE"};
        this.resultSetTables = this.metaData.getTables(this.catalog, null, null, types);

        return new DatabaseMetaDataDAO.TableIterator(
                this.resultSetTables,
                this.databaseContainer,
                this.metaData,
                this.catalog,
                this.shema
            );
    }

    public CDatabase getDatabaseContainer() {
        return this.databaseContainer;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return this.metaData;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getShema() {
        return shema;
    }

    public int getTableCount() throws SQLException {
        if(this.tableCount == 0) {
            this.tableCount = this.countTables();
        }
        return tableCount;
    }

    public ResultSet getResultSetTables(){
        return resultSetTables;
    }

    private int countTables() throws SQLException {

        int tableCount = 0;
        final String[] types = {"TABLE"};

        ResultSet resultSetTables = null;
        try {
            resultSetTables = this.getMetaData().getTables(
                    this.catalog, null, null, types);

            while (resultSetTables.next()) {
                // we check if user has access. MSSQL has always dbo.
                String TABLE_SCHEM = resultSetTables.getString(2);
                if (TABLE_SCHEM != null && !TABLE_SCHEM.equalsIgnoreCase("dbo")) {
                    if (!TABLE_SCHEM.equalsIgnoreCase(this.username)) {
                        continue;
                    }
                }
                //we check if is table
                String TYPE = resultSetTables.getString(4);
                if (!TYPE.equals("TABLE")) {
                    continue;
                }
                if (this.rdbmsType.equals(RDBMSType.ORACLE)) {
                    // Tables that begin on BIN are omited
                    if (resultSetTables.getString(3).startsWith("BIN")) {
                        continue;
                    }
                }
                tableCount++;
            }
        } finally {
            if (resultSetTables != null) {
                resultSetTables.close();
            }
        }
        return tableCount;
    }

    class TableIterator implements JRelExIterator<CTable> {

        private ResultSet resultSetTables = null;
        private int currentTableIndex = 0;
        private CDatabase databaseContainer = null;

        private DatabaseMetaData metaData = null;
        private String catalog = "";
        private String shema = "";

        private ArrayList<CReference> tempReferences = new ArrayList<CReference>();

        public TableIterator(ResultSet resultSetTables,
                    CDatabase databaseContainer,
                    DatabaseMetaData metaData,
                    String catalog,
                    String shema) throws SQLException {

            this.resultSetTables = resultSetTables;
            this.databaseContainer = databaseContainer;
            this.metaData = metaData;
            this.catalog = catalog;
            this.shema = shema;

            //resultSetTables.beforeFirst(); // dela le z mysql resultset

        }

        @Override
        public boolean hasNext() {
            boolean hasNext = true;
            try {
                if (this.resultSetTables.isAfterLast()) {
                    hasNext = false;
                }
            }
            finally {
                return hasNext;
            }
        }

        @Override
        public CTable next() {
            CTable table = null;
            try {

                while (this.resultSetTables.next()) {

                    if (this.isTableValid()) {
                        this.currentTableIndex++;
                        // add data to table
                        table = databaseContainer.getNewTableInstance();
                        table.setName(this.resultSetTables.getString(3));
                        // add table to container
                        databaseContainer.getTables().put(table.getName(), table);
                        this.fillTable(table);

                        // loop end
                        break;
                    }
                }

            }
            catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
            return table;
        }

        @Override
        public int currentIndex() {
            return currentTableIndex;
        }

        private void fillTable(CTable table) throws SQLException {
            this.addColumns(table);
            this.setPrimaryKeys(table);
            this.setExportedKeys(table);
            this.setImportedKeys(table);

        }

        private boolean isTableValid() throws SQLException {
            // we check if user has access
            String TABLE_SCHEM = this.resultSetTables.getString(2);
            if (TABLE_SCHEM != null && !TABLE_SCHEM.equalsIgnoreCase("dbo")) {
                if (!TABLE_SCHEM.equalsIgnoreCase(databaseContainer.getConnBean().getUsername())) {
                    return false;
                }
            }

            //we check if is table
            String TYPE = resultSetTables.getString(4);
            if (!TYPE.equals("TABLE")) {
                return false;
            }

            String tableName = resultSetTables.getString(3); // get table name
            if (databaseContainer.getConnBean().getDriver().equals(RDBMSType.ORACLE)) {
                if (tableName.startsWith("BIN")) {
                    return false;
                }
            }

            return true;
        }

        private int countTableColumns(String tableName) throws SQLException {
            int initialSize = 0;
            ResultSet resultSetColumns = null;
            try {
                resultSetColumns = this.metaData.getColumns(
                        this.catalog,
                        this.shema, tableName, null);
                // get initial size for columns
                while (resultSetColumns.next()) {
                    initialSize++;
                }
            } finally {
                if (resultSetColumns != null) {
                    resultSetColumns.close();
                }
            }
            return initialSize;
        }

        private void addColumns(CTable table) throws SQLException {
            ResultSet resultSetColumns = null;
            try {
                resultSetColumns = this.metaData.getColumns(
                        this.catalog,
                        this.shema, table.getName(), null
                );

                int colCount = this.countTableColumns(table.getName());
                table.setColumns(new ArrayList<CColumn>(colCount));
                table.setColumnNames(new ArrayList<String>(colCount));

                while (resultSetColumns.next()) {
                    CColumn column = table.getNewColumnInstance();

                    column.setName(resultSetColumns.getString(4)); // column name
                    Integer type = resultSetColumns.getInt(5);
                    column.setType((String) Common.getInstance().getjDBCTypes().get(type)); // column type

                    table.getColumns().add(column);
                    table.getColumnNames().add(column.getName());
                }
            } finally {
                if (resultSetColumns != null) {
                    resultSetColumns.close();
                }
            }
        }

        private void setPrimaryKeys(CTable table) throws SQLException{
            // we add primary key info
            ResultSet resultSetPrimaryKeys = this.metaData
                    .getPrimaryKeys(this.catalog, null, table.getName());
            try {
                while (resultSetPrimaryKeys.next()) {
                    String columnName = resultSetPrimaryKeys.getString(4);
                    table.getColumnByName(columnName).setPrimaryKey(true);
                }
            } finally {
                resultSetPrimaryKeys.close();
            }
        }

        private void setExportedKeys(CTable table) throws SQLException {
            ResultSet resultExportedKeys = this.metaData
                    .getExportedKeys(this.catalog, null, table.getName());
            try {
                while (resultExportedKeys.next()) {
                    String pKColumnName = resultExportedKeys.getString(4);

                    CReference creference = table.getColumnByName(pKColumnName)
                            .getNewReferenceInstance();
                    creference.setTable(resultExportedKeys.getString(3)); // pktable_name
                    creference.setColumn(pKColumnName); // pkcolum_name
                    creference.setReferencedTable(resultExportedKeys.getString(7)); // fktable_name
                    creference.setReferencedColumn(resultExportedKeys.getString(8)); // fkcolum_name

                    this.tempReferences.add(creference);

                    //ctable.getColumnByName(pKColumnName).setForeignKey(true);
                    if (table.getColumnByName(pKColumnName).getReferences() == null) {
                        table.getColumnByName(pKColumnName).setReferences(new ArrayList<CReference>());
                    }
                    table.getColumnByName(pKColumnName).getReferences().add(creference);
                }
            } finally {
                resultExportedKeys.close();
            }
        }

        private void setImportedKeys(CTable table) throws SQLException {
            ResultSet resultImportedKeys = this.metaData
                    .getImportedKeys(this.catalog, null, table.getName());
            try {
                while (resultImportedKeys.next()) {
                    String pKColumnName = resultImportedKeys.getString(4);
                    String fKColumnName = resultImportedKeys.getString(8);

                    CReference creference = table.getColumnByName(fKColumnName)
                            .getNewReferenceInstance();
                    creference.setReferencedTable(resultImportedKeys.getString(3)); // pktable_name
                    creference.setReferencedColumn(pKColumnName); // pkcolum_name
                    creference.setTable(resultImportedKeys.getString(7)); // fktable_name
                    creference.setColumn(fKColumnName); // fkcolum_name

                    this.tempReferences.add(creference);

                    table.getColumnByName(fKColumnName).setForeignKey(true);

                    if (table.getColumnByName(fKColumnName).getReferences() == null) {
                        table.getColumnByName(fKColumnName).setReferences(new ArrayList<CReference>());
                    }
                    table.getColumnByName(fKColumnName).getReferences().add(creference);
                }
            } finally {
                resultImportedKeys.close();
            }
        }

    }

}
