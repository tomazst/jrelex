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

import com.panemu.tiwulfx.common.TiwulFXUtil;
import com.panemu.tiwulfx.table.BaseColumn;
import com.panemu.tiwulfx.table.LookupColumn;
import com.panemu.tiwulfx.table.TableControl;

import java.io.FileOutputStream;
import java.util.List;

import javafx.scene.control.TableColumn;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExportToExcel<T> {

    private HSSFWorkbook wb;
    protected HSSFCellStyle csText;
    protected HSSFCellStyle csHeader;
    private HSSFCellStyle csTitle;
    protected HSSFCellStyle csIntNum;
    private HSSFCellStyle csDoubleNum;

    /**
     *
     * @param sheet
     * @param header
     * @param rowIdx
     * @param clmIdx
     * @param offset [x,y] where x = rowSpan (headerDepth) . y = colSpan. 0
     * means no span
     * @return
     */
    private int[] createHeader(HSSFSheet sheet, List<TableColumn<T, ?>> header, int rowIdx, int clmIdx, int[] offset) {
        HSSFRow row = sheet.createRow(rowIdx);
        int nextCellOffset = clmIdx;
        int columnOffset = 0;
        int rowOffset = offset[0];
        System.out.println(header.toString());
        
        
        for (int i = 2; i < header.size(); i++) {
            TableColumn column = header.get(i);
            int ix = i - 2;
            
            if ((!(column instanceof BaseColumn) && column.getColumns().isEmpty()) || (!column.isVisible())) {
                /**
                 * Skip column that is not BaseColumn and doesn't have inner
                 * columns
                 */
                nextCellOffset--;
                continue;
            }
            int[] headerOffset = new int[]{offset[0], offset[1]};
            HSSFCell cell = row.createCell(ix + nextCellOffset);
            cell.setCellValue(column.getText());
            if (!column.getColumns().isEmpty()) {
                rowOffset--;
                headerOffset = createHeader(sheet, column.getColumns(), rowIdx + 1, ix + nextCellOffset, new int[]{headerOffset[0] - 1, headerOffset[1]});
            }
            columnOffset = columnOffset + headerOffset[1];
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + headerOffset[0], ix + nextCellOffset, ix + headerOffset[1] + nextCellOffset));
            cell.setCellStyle(csHeader);
            if (!column.getColumns().isEmpty()) {
                nextCellOffset = nextCellOffset + headerOffset[1];
            }
        }
        return new int[]{rowOffset, offset[1] + header.size() - 1 + columnOffset};
    }

    private int getHeaderDepth(TableColumn<T, ?> column, int depth) {
        if (column.getColumns().isEmpty()) {
            return depth;
        }
        int result = depth;
        for (TableColumn clm : column.getColumns()) {
            int newDepth = getHeaderDepth(clm, depth + 1);
            result = Math.max(result, newDepth);
        }

        return result;
    }

    public void export(String title, String targetFile, TableControl<T> tableControl, List<T> data, List<Double> columnWidths) throws Exception {
        int headerDepth = 1;
        List<TableColumn<T, ?>> lstColumn = tableControl.getTableView().getColumns();
       
        for (TableColumn column : lstColumn) {
            int depth = getHeaderDepth(column, 1);
            headerDepth = Math.max(depth, headerDepth);
        }
        // create a new workbook
        wb = new HSSFWorkbook();
        prepareStyle();
        // create a new sheet
        HSSFSheet sheet = wb.createSheet();
        int rowIdx = 0;
        HSSFRow row = sheet.createRow(rowIdx);

        // set column width
        for (short i = 0; i < columnWidths.size(); i++) {
            sheet.setColumnWidth(i, (int) (258 / 8 * columnWidths.get(i)));
        }

        HSSFCell cell = row.createCell(0);
        cell.setCellStyle(csTitle);
        cell.setCellValue(title);
        row.setHeight((short) (row.getHeight() * 3));
        rowIdx++;
        rowIdx++;

        createHeader(sheet, lstColumn, rowIdx, 0, new int[]{headerDepth - 1, 0});
        List<TableColumn<T, ?>> lstLeafColumn = tableControl.getLeafColumns();
       
        if (headerDepth > 1) {
            row = sheet.getRow(rowIdx + headerDepth - 1);
            int i = 0;
            for (TableColumn column : lstLeafColumn) {
                if (column.isVisible() && column instanceof BaseColumn) {
                    sheet.setColumnWidth(i, (int) (258 / 8 * column.getPrefWidth()));
                    cell = row.getCell(i);
                    if (cell == null) {
                        cell = row.createCell(i);
                    }
                    cell.setCellStyle(csHeader);
                    i++;
                }
            }
        }
        rowIdx = rowIdx + headerDepth;
        Object value;

        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(rowIdx);
            int j = 0;
            for (TableColumn column : lstLeafColumn) {
            	           	
                if (column instanceof BaseColumn && column.isVisible()) {
                    BaseColumn baseColumn = (BaseColumn) column;
                    String propertyName = baseColumn.getPropertyName();
                    
                    if(propertyName.equals("references") || 
                    		propertyName.equals("position")){ // ignore first two columns
                		continue;
                	}
                    
                    if (baseColumn instanceof LookupColumn) {
                        propertyName = propertyName + "." + ((LookupColumn) baseColumn).getLookupPropertyName();
                    }
                    cell = row.createCell(j);

                    if (propertyName.contains(".")) {
                        try {
                            value = PropertyUtils.getNestedProperty(data.get(i), propertyName);
                        } catch (NestedNullException ex) {
                            value = null;
                        }
                    } else {
                        value = PropertyUtils.getSimpleProperty(data.get(i), propertyName);
                    }
                    
                    if (value instanceof Long || value instanceof Integer) {
                        cell.setCellStyle(csIntNum);
                        cell.setCellValue(Double.parseDouble(value.toString()));
                    } else if (value instanceof Double || value instanceof Float) {
                        cell.setCellStyle(csDoubleNum);
                        cell.setCellValue(Double.parseDouble(value.toString()));
                    } else {
                        cell.setCellStyle(csText);
                        cell.setCellValue(baseColumn.convertToString(value));
                    }
                    j++;
                }
            }

            rowIdx++;
        }

        writeFooter(sheet, rowIdx, data.size());
        
        FileOutputStream fileOut;
        if (!targetFile.endsWith(".xls")) {
            targetFile = targetFile + ".xls";
        }
        fileOut = new FileOutputStream(targetFile);
        wb.write(fileOut);
        fileOut.close();
        TiwulFXUtil.openFile(targetFile);
    }

    /**
     * Callback to write footer. This method is called after writing records to
     * spreadsheet, before saving the sheet to file and close the output stream.
     *
     * @param sheet
     * @param rowIdx
     * @param dataCount
     */
    protected void writeFooter(HSSFSheet sheet, int rowIdx, int dataCount) {
    }

    private void prepareStyle() {
        csText = wb.createCellStyle();
        csText.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
        csText.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        csText.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        csText.setBorderRight(HSSFCellStyle.BORDER_THIN);
        csText.setBorderTop(HSSFCellStyle.BORDER_THIN);

        csHeader = wb.createCellStyle();
        csHeader.setFillBackgroundColor(HSSFColor.BLUE_GREY.index);
        csHeader.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
        csHeader.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        csHeader.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        csHeader.setBorderRight(HSSFCellStyle.BORDER_THIN);
        csHeader.setBorderTop(HSSFCellStyle.BORDER_THIN);
        csHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        csHeader.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        HSSFFont f = wb.createFont();
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        csHeader.setFont(f);

        csTitle = wb.createCellStyle();
        csTitle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
        f = wb.createFont();
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        csTitle.setFont(f);

        // -------INTEGER
        csIntNum = wb.createCellStyle();
        csIntNum.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
        csIntNum.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        csIntNum.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        csIntNum.setBorderRight(HSSFCellStyle.BORDER_THIN);
        csIntNum.setBorderTop(HSSFCellStyle.BORDER_THIN);

        csDoubleNum = wb.createCellStyle();
        csDoubleNum.setDataFormat(wb.createDataFormat().getFormat("#.##"));
        csDoubleNum.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        csDoubleNum.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        csDoubleNum.setBorderRight(HSSFCellStyle.BORDER_THIN);
        csDoubleNum.setBorderTop(HSSFCellStyle.BORDER_THIN);
    }
}
