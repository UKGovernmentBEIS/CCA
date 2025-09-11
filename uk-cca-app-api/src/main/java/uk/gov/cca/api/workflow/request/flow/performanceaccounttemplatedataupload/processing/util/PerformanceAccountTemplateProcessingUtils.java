package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.util;


import static uk.gov.cca.api.common.utils.ExcelCellUtils.getCellValueAsString;

import java.util.List;
import java.util.Optional;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

@UtilityClass
public class PerformanceAccountTemplateProcessingUtils {
    
    public static int findTableSize(Sheet sheet, int maxRowLimit, int firstTableRowIndex, int firstTableColumnIndex) {
        int rowIndex = firstTableRowIndex;
        DataFormatter formatter = new DataFormatter();
        
        while (rowIndex < maxRowLimit) {
            String cellValue = Optional.ofNullable(sheet.getRow(rowIndex))
                    .map(row -> row.getCell(firstTableColumnIndex))
                    .map(formatter::formatCellValue)
                    .orElse("");
            
            if ("Total".equals(cellValue)) {
                return rowIndex - firstTableRowIndex;
            }
            rowIndex++;
        }
        return -1;
    }
    
    public static boolean isRowEmpty(Row row, int firstTableColumnIndex, int lastTableColumnIndex, FormulaEvaluator eval) {
        
        for (int i = firstTableColumnIndex; i <= lastTableColumnIndex; i++) {
            Cell cell = row.getCell(i);
            // Skip non user-updatable fields(formula calculated)
            if (List.of(10,11).contains(i)) {
                continue;
            }
            if (StringUtils.isNotBlank(getCellValueAsString(cell, eval))) {
                return false;
            }
        }
        
        return true;
    }
}
