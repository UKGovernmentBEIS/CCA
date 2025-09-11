package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules;


import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.FIRST_TABLE_COLUMN_INDEX;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.FIRST_TABLE_ROW_INDEX;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.util.PerformanceAccountTemplateProcessingUtils.findTableSize;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.TargetUnitIdentityAndPerformanceCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;

@Component
public class PerformanceAccountTemplateStructureValidator {
    
    public List<PerformanceAccountTemplateViolation> validate(Sheet sheet) {
        List<PerformanceAccountTemplateViolation> violations = new ArrayList<>();
        
        for (TargetUnitIdentityAndPerformanceCellsReferenceEnum cellRef
                : TargetUnitIdentityAndPerformanceCellsReferenceEnum.values()) {
            Row row = sheet.getRow(cellRef.getCellAddress().getRow());
            if (row == null) {
                violations.add(
                        new PerformanceAccountTemplateViolation(
                                cellRef.getCellAddress(),
                                PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.CELL_ADDRESS_NOT_FOUND)
                );
            }
        }
        
        if (findTableSize(sheet, EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.MAX_ROW_LIMIT, FIRST_TABLE_ROW_INDEX, FIRST_TABLE_COLUMN_INDEX) == -1) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.TOTAL_KEYWORD_NOT_FOUND)
            );
        }
        return violations;
    }
}
