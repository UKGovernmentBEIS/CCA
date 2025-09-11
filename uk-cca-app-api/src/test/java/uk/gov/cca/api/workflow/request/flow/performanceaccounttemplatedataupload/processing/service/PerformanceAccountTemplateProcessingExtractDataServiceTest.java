package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PerformanceAccountTemplateProcessingExtractDataServiceTest {
    
    @Test
    void extractData_shouldReturnPopulatedData_whenAllRowsAndCellsExist() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        
        Row row3 = sheet.createRow(3);
        Cell cellD4 = row3.createCell(3);
        cellD4.setCellValue("TU_ID_Value");
        
        Row row4 = sheet.createRow(4);
        Cell cellD5 = row4.createCell(3);
        cellD5.setCellValue("Operator_Name_Value");
        
        Row row5 = sheet.createRow(5);
        Cell cellD6 = row5.createCell(3);
        cellD6.setCellValue("Absolute");
        
        Row row6 = sheet.createRow(6);
        Cell cellD7 = row6.createCell(3);
        cellD7.setCellValue("15.5");
        
        Row row7 = sheet.createRow(7);
        Cell cellD8 = row7.createCell(3);
        cellD8.setCellValue("5.0");
        
        Row row9 = sheet.createRow(9);
        Cell cellD10 = row9.createCell(3);
        cellD10.setCellValue("Yes");
        
        Cell cellF10 = row9.createCell(5);
        cellF10.setCellValue("Extra details when needed");
        
        Row row14 = sheet.createRow(14);
        row14.createCell(1).setCellValue("Total");
        
        PerformanceAccountTemplateProcessingExtractDataService service =
                new PerformanceAccountTemplateProcessingExtractDataService();
        
        PerformanceAccountTemplateReportData result = service.extractData(workbook);
        
        assertNotNull(result);
        assertNotNull(result.getEnergyOrCarbonSavingActionsAndMeasuresImplemented());
    }
    
}

