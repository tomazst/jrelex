package si.comptus.jrelex.sql;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import si.comptus.jrelex.Common;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CReferenceData;
import javafx.scene.control.TableColumn;

import com.panemu.tiwulfx.common.TableCriteria;

public abstract class AbstractDynamicQuery {
	
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
	
        /**
	 * Example;
	 * select
	(
	select count(*) as opomini from 
	opomini
	where id_opomin = 1
	) as opomini,
	(
	select count(*) as napovedani_izklopi from 
	napovedani_izklopi
	where id_opomin = 1
	) as napovedani_izklopi,
	(
	select count(*) as racuni from 
	where racuni
	id_opomin = 1
	) as racuni
	*/
	abstract public HashMap<String, CReferenceData> getReferencedData(
			String storedDatabaseName, String databaseName, String tableName,
			CColumn column, String value);
	
	abstract public ResultSet getTableData(CTable table, 
			String databaseName,
			String storedDatabaseName,
			List<TableCriteria> filteredColumns, 
			List<String> sortedColumns,
			List<TableColumn.SortType> sortingOrders, 
			int startIndex,
			int maxResult);
	
	abstract public ResultSet getTableData(CTable table, 
			String databaseName,
			String storedDatabaseName,
			List<TableCriteria> filteredColumns);
	
	abstract public int getRecordCount(CTable table, String databaseName,
			List<TableCriteria> filteredColumns);
	
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
