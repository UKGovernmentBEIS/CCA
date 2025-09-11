package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules.EnergyOrCarbonSavingActionsAndMeasuresImplementedValidator;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules.PerformanceAccountTemplateStructureValidator;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules.TargetUnitIdentityAndPerformanceValidator;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingValidationService {
    
    private final PerformanceAccountTemplateStructureValidator performanceAccountTemplateStructureValidator;
    private final EnergyOrCarbonSavingActionsAndMeasuresImplementedValidator energyOrCarbonSavingActionsAndMeasuresImplementedValidator;
    private final TargetUnitIdentityAndPerformanceValidator targetUnitIdentityAndPerformanceValidator;
    
    
    public List<PerformanceAccountTemplateViolation> validateData(PerformanceAccountTemplateReportData data,
                                                                  String targetUnitAccountBusinessId) {
        List<PerformanceAccountTemplateViolation> allViolations = new ArrayList<>();
        allViolations.addAll(targetUnitIdentityAndPerformanceValidator.validate(data, targetUnitAccountBusinessId));
        allViolations.addAll(energyOrCarbonSavingActionsAndMeasuresImplementedValidator.validate(data));
        
        return allViolations;
    }
    
    public List<PerformanceAccountTemplateViolation> validateTemplateStructure(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        return performanceAccountTemplateStructureValidator.validate(sheet);
    }
}
