package si.comptus.jrelex;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.PaneBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.comptus.jrelex.container.CColumn;
import si.comptus.jrelex.container.CTable;
import si.comptus.jrelex.sql.AbstractDynamicQuery;
import si.comptus.jrelex.sql.DynamicQueryMysql;
import si.comptus.jrelex.sql.DynamicQueryOracle;
import si.comptus.jrelex.sql.DynamicQuerySqlserver;

import com.panemu.tiwulfx.common.TableCriteria;
import com.panemu.tiwulfx.dialog.MessageDialogBuilder;

public class DbTableInGridPane<T> {

	private static final Logger log = LoggerFactory
			.getLogger(DatabaseTableController.class);

	private AbstractDynamicQuery dq;
	private List<TableCriteria<T>> filteredColumns = null;
	private String storedDatabaseName = "";
	
	private String referedColumn = "";
	
	public DbTableInGridPane(String storedDatabaseName, String referedColumn, Connection conn) {
		super();
		
		this.referedColumn = referedColumn;

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

	public GridPane getTableGrid(String databaseName, CTable table,	List<TableCriteria> filteredColumns) {

		ArrayList<String> filters = new ArrayList<String>();
		// Grid pane
		GridPane grid = new GridPane();
		//grid.setStyle("-fx-background-color: rgb(180, 180, 180);");
		//grid.setPadding(new Insets(20));
		grid.setVgap(1);
		grid.setHgap(1);
		
		// too improove performance
		//grid.setCache(true);
		grid.setCacheHint(CacheHint.SPEED);

		if (this.filteredColumns != null) {
			for (TableCriteria<T> criteria : this.filteredColumns) {
				filteredColumns.add(criteria);
				filters.add(criteria.getAttributeName());
			}
			// then we empty local filters
			this.filteredColumns = null;
		}
		
		int rowNumber = 0;

		// Create table header and adding icons for foreign and primary keys
		int colNumber = 0;
		for (CColumn column : table.getColumns()) { // go
			
			Label colHead = new Label();
			colHead.setAlignment(Pos.CENTER);
			colHead.setPrefHeight(22);
			//colHead.getStyleClass().removeAll("label");
			colHead.setFont(Font.font ("Regular", FontWeight.BOLD, 12));
		
										
			if (column.isForeignKey() || column.isPrimaryKey()) {

				if (column.isPrimaryKey()) {
					Image imagePK = new Image(getClass().getResourceAsStream(
							"/images/primary-key.png"));
					ImageView imageViewPrimaryKey = new ImageView(imagePK);
					colHead.setText(column.getName());
					colHead.setGraphic(imageViewPrimaryKey);
					
				}
				if (column.isForeignKey()) {
					Image imageFK = new Image(getClass().getResourceAsStream(
							"/images/foreign-keys.png"));
					ImageView ImageViewForeignKey = new ImageView(imageFK);
					colHead.setText(column.getName());
					colHead.setGraphic(ImageViewForeignKey);
				}

			} else {
				colHead.setText(column.getName());
			}
									
			colHead.setGraphicTextGap(5);
			
			// hack for padding
			colHead.setText("  "+colHead.getText()+"  ");
			colHead.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			
			String colHeadStyle = "-fx-background-color: #CCCCCC;";
			if(this.referedColumn.equals(column.getName())){
				if(column.isPrimaryKey()){
					colHeadStyle = "-fx-background-color: #B2B2FF;";
				}
				
				if(column.isForeignKey()){
					colHeadStyle = "-fx-background-color: #B2B2FF;";
				}	
			}
			
			colHead.setStyle(colHeadStyle);
			
			// too improove performance
			colHead.setCache(true);
			colHead.setCacheHint(CacheHint.SPEED);			
			
			grid.add(colHead, colNumber, rowNumber);
			
			colNumber++;
		}
		
		rowNumber++;

		ResultSet rs = this.dq.getTableData(table, databaseName,
				this.storedDatabaseName, filteredColumns);
		
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();

			while (rs.next()) { // table row
				
				//HashMap<String, CColumnTableReferences> references = new HashMap<>(count);
				
					colNumber = 0;
					for (int i = 1; i <= rsmd.getColumnCount(); i++) { // columns
						int type = rsmd.getColumnType(i);

						String columnName = rsmd.getColumnName(i);
						Label cellValue = new Label();
						cellValue.setPrefHeight(20);
						//cellValue.setStyle("-fx-font-weight: thin;");
						//cellValue.setFont(Font.font ("Regular", FontWeight.THIN, 30));
						//log.info(cellValue.getFont().toString());
												 
						switch (type) {
							case Types.INTEGER:
							case Types.TINYINT:
							case Types.SMALLINT:
							case Types.BIGINT:
								cellValue.setText(Integer.toString(rs.getInt(columnName)));
								cellValue.setAlignment(Pos.CENTER_RIGHT);
								break;
							case Types.FLOAT:
							case Types.DECIMAL:
							case Types.NUMERIC:
								cellValue.setText(Float.toString(rs.getFloat(columnName)));
								cellValue.setAlignment(Pos.CENTER_RIGHT);
								break;
							case Types.DOUBLE:
								cellValue.setText(Double.toString(rs.getDouble(columnName)));
								break;
							case Types.BOOLEAN:
							case Types.BIT:
								cellValue.setText(Boolean.toString(rs.getBoolean(columnName)));
								break;
							case Types.DATE:
								if(rs.getDate(columnName) != null) {
									cellValue.setText(rs.getDate(columnName).toString());
								} else {
									cellValue.setText("");
								}
								break;
							case Types.TIMESTAMP:
								Timestamp ts = rs.getTimestamp(columnName);
								if(ts != null){
									cellValue.setText(ts.toString());
								} else {
									cellValue.setText("");
								}
								break;
							case Types.TIME:
								Time time = rs.getTime(columnName);
								if(time != null){
									cellValue.setText(time.toString());
								} else {
									cellValue.setText("");
								}
								break;
							default:
								String val = rs.getString(columnName);
								if(val != null) {
									if(val.length() > Common.getInstance().getDbstore()
											.getAppSettings().getMaxStringLength()){
										val = String.copyValueOf(val.toCharArray(), 0, Common.getInstance()
												.getDbstore().getAppSettings().getMaxStringLength()) + " ...";
									}
								}
								cellValue.setText(val);
						}
						cellValue.setText("  "+cellValue.getText()+"  ");
						cellValue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
						
						if(columnName.equals(this.referedColumn)){
							cellValue.setFont(Font.font ("Regular", FontWeight.BOLD, 12));
						}
						
						if(rowNumber % 2 == 0){
							cellValue.setStyle("-fx-background-color: #EFEFEF;");
						} else {
							cellValue.setStyle("-fx-background-color: #FFFFFF;");
						}
						
						// too improove performance
						cellValue.setCache(true);
						cellValue.setCacheHint(CacheHint.SPEED);	
																		
						grid.add(cellValue, colNumber, rowNumber);
						
						colNumber++;
					}
	
				rowNumber++;
			}
			
		} catch (SQLException ex) {
			log.error(ex.getMessage(), ex);
			MessageDialogBuilder.error(ex).show(null);
		}
		
	    // make all of the Controls and Panes inside the grid fill their grid cell, 
	    // align them in the center and give them a filled background.
	    // you could also place each of them in their own centered StackPane with 
	    // a styled background to achieve the same effect.
		/*
	    for (Node n: grid.getChildren()) {
	      if (n instanceof Control) {
	        Control control = (Control) n;
	        control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	        
	        control.setStyle("-fx-background-color: white;");
	      }
	    }*/
	   
		// grid height
		int gridHeight = ((rowNumber - 1) * 20) + 30;
		grid.setMaxHeight(gridHeight);
		return grid;
	}

}



