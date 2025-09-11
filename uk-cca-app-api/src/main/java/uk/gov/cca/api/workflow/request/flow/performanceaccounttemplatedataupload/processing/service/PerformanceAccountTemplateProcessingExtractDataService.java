package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.TargetUnitIdentityAndPerformanceCellsReferenceEnum;


@Service
public class PerformanceAccountTemplateProcessingExtractDataService {
    
    public PerformanceAccountTemplateReportData extractData(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        PerformanceAccountTemplateReportData performanceAccountTemplateReportData = new PerformanceAccountTemplateReportData();
        for (TargetUnitIdentityAndPerformanceCellsReferenceEnum cellRef
                : TargetUnitIdentityAndPerformanceCellsReferenceEnum.values()) {
            Row row = sheet.getRow(cellRef.getCellAddress().getRow());
            if (row != null) {
                Cell cell = row.getCell(cellRef.getCellAddress().getColumn());
                cellRef.populate(cell, performanceAccountTemplateReportData, evaluator);
            }
        }
        
        EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.populate(sheet, performanceAccountTemplateReportData, evaluator);
        
        return performanceAccountTemplateReportData;
    }
}
