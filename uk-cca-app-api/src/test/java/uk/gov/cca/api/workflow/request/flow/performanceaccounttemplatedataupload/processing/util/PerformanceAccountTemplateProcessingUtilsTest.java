package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceAccountTemplateProcessingUtilsTest {
    
    @Test
    void findTableSize_shouldReturnMinusOne_whenNoTotalRowFound() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        
        int size = PerformanceAccountTemplateProcessingUtils.findTableSize(
                sheet, 20, 5, 2);
        
        assertEquals(-1, size,
                "Expected -1 if 'Total' is not found in the firstTableColumnIndex");
    }
    
    @Test
    void findTableSize_shouldReturnZero_whenTotalRowIsFoundImmediately() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        
        Row row5 = sheet.createRow(5);
        row5.createCell(2).setCellValue("Total");
        
        int size = PerformanceAccountTemplateProcessingUtils.findTableSize(
                sheet, 20, 5, 2);
        
        assertEquals(0, size,
                "Expected 0 when 'Total' is found at the very first row in the table range.");
    }
    
    @Test
    void findTableSize_shouldReturnCountUpToRowContainingTotal() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        
        for (int r = 10; r < 16; r++) {
            Row row = sheet.createRow(r);
            if (r == 14) {
                row.createCell(1).setCellValue("Total");
            } else {
                row.createCell(1).setCellValue("DataRow-" + r);
            }
        }
        
        int size = PerformanceAccountTemplateProcessingUtils.findTableSize(
                sheet, 100, 10, 1);
        
        assertEquals(4, size,
                "Expected the number of rows before hitting 'Total'.");
    }
    
    @Test
    void isRowEmpty_shouldReturnTrue_whenAllRelevantCellsAreBlank() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        
        Row row = sheet.createRow(5);
        for (int c = 2; c <= 12; c++) {
            if (c == 10 || c == 11) {
                // do nothing, columns 10 & 11 are formula columns
            } else {
                row.createCell(c, CellType.BLANK);
            }
        }
        
        boolean isEmpty = PerformanceAccountTemplateProcessingUtils.isRowEmpty(row, 2, 12, evaluator);
        
        assertTrue(isEmpty,
                "Expected isRowEmpty to return true if no user-updatable columns have data.");
    }
    
    @Test
    void isRowEmpty_shouldReturnFalse_whenAnyRelevantCellHasValue() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        
        Row row = sheet.createRow(10);
        for (int c = 2; c <= 12; c++) {
            if (c == 10 || c == 11) {
                continue;
            }
            row.createCell(c, CellType.BLANK);
        }
        
        row.getCell(3).setCellValue("NonBlank");
        
        boolean isEmpty = PerformanceAccountTemplateProcessingUtils.isRowEmpty(row, 2, 12, evaluator);
        
        assertFalse(isEmpty,
                "Expected isRowEmpty to return false if any user-updatable cell has a non-blank value.");
    }
    
    @Test
    void isRowEmpty_shouldIgnoreFormulaColumns_whenTheyContainValue() {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Test");
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        Row row = sheet.createRow(7);
        for (int c = 2; c <= 12; c++) {
            if (c == 10 || c == 11) {
                Cell cell = row.createCell(c, CellType.FORMULA);
                cell.setCellFormula("1+2");
            } else {
                row.createCell(c, CellType.BLANK);
            }
        }
        
        boolean isEmpty = PerformanceAccountTemplateProcessingUtils.isRowEmpty(row, 2, 12, evaluator);

        assertTrue(isEmpty,
                "Expected isRowEmpty to ignore formula columns 10 and 11, so row should be considered empty.");
    }
}
