package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessorTest {
    
    @Test
    void parseEnergyCarbonSavingActions_shouldReturnEmptyList_whenNoDataRows() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");
        Row totalRow = sheet.createRow(EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.FIRST_TABLE_ROW_INDEX);
        totalRow.createCell(1).setCellValue("Total");
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        
        PerformanceAccountTemplateReportData data = new PerformanceAccountTemplateReportData();
        EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.populate(sheet, data, evaluator);
        
        assertNotNull(data.getEnergyOrCarbonSavingActionsAndMeasuresImplemented());
        assertTrue(data.getEnergyOrCarbonSavingActionsAndMeasuresImplemented().isEmpty());
    }
    
    @Test
    void parseEnergyCarbonSavingActions_shouldReturnPopulatedList_whenValidRowsPresent() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        Row row14 = sheet.createRow(14);
        createCell(row14, 1, "FACILITY-001");
        createCell(row14, 2, "Energy Management");
        createCell(row14, 3, "Lighting Upgrade");
        createCell(row14, 4, "Reduce consumption");
        createCell(row14, 5, "2025-01-01");
        createCell(row14, 6, "Fixed");
        createCell(row14, 7, "35.5");
        createCell(row14, 8, "50.0");
        createCell(row14, 9, "10");
        createCell(row14, 10, "");
        createCell(row14, 11, "");
        createCell(row14, 12, "Some notes");

        Row totalRow = sheet.createRow(20);
        createCell(totalRow, 1, "Total");
        
        PerformanceAccountTemplateReportData data = new PerformanceAccountTemplateReportData();
        EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.populate(sheet, data, evaluator);
        
        List<EnergyOrCarbonSavingActionsAndMeasuresImplementedRow> resultList =
                data.getEnergyOrCarbonSavingActionsAndMeasuresImplemented();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow first = resultList.getFirst();
        assertEquals(14, first.getExcelRowIndex());
        assertEquals("FACILITY-001", first.getFacilityId());
        assertEquals("Energy Management", first.getActionCategoryType());
        assertEquals("Lighting Upgrade", first.getSavingActionsImplemented());
        assertEquals("Reduce consumption", first.getReasonsForImplementation());
        assertEquals("2025-01-01", first.getImplementationDate());
        assertEquals("Fixed", first.getFixedEnergyConsumptionOrCarbonEmissionsImpacted());
        assertEquals("35.5", first.getEnergyConsumptionOrCarbonEmissionsImpactedPercentage());
        assertEquals("50.0", first.getExpectedExtentOfChangeImplementedPercentage());
        assertEquals("10", first.getExpectedSavingsFromTheChangeImplementedPercentage());
        assertEquals("", first.getEstimatedChangeInEnergyConsumptionPercentage());
        assertEquals("", first.getEstimatedChangeInCarbonEmissionsPercentage());
        assertEquals("Some notes", first.getNotes());
    }
    
    @Test
    void parseEnergyCarbonSavingActions_shouldStopParsing_whenItReachesTotalRow() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        
        Row row14 = sheet.createRow(14);
        createCell(row14, 1, "FAC-123");
        createCell(row14, 2, "Some category");
        
        Row row15 = sheet.createRow(15);
        createCell(row15, 1, "Total");
        
        PerformanceAccountTemplateReportData data = new PerformanceAccountTemplateReportData();
        EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.populate(sheet, data, evaluator);
        
        List<EnergyOrCarbonSavingActionsAndMeasuresImplementedRow> resultList =
                data.getEnergyOrCarbonSavingActionsAndMeasuresImplemented();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals("FAC-123", resultList.getFirst().getFacilityId());
    }
    
    private static void createCell(Row row, int columnIndex, String value) {
        Cell cell = row.createCell(columnIndex, CellType.STRING);
        cell.setCellValue(value);
    }
}
