package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.TargetUnitIdentityAndPerformanceCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.TargetType;
import static uk.gov.cca.api.common.utils.ExcelCellUtils.isBigDecimal;

@Component
@AllArgsConstructor
public class TargetUnitIdentityAndPerformanceValidator {
    
    public List<PerformanceAccountTemplateViolation> validate(PerformanceAccountTemplateReportData data, String expectedTargetUnitAccountBusinessId) {
        List<PerformanceAccountTemplateViolation> violations = new ArrayList<>();
        
        validateTargetUnitBusinessId(data.getTargetUnitAccountBusinessId(), expectedTargetUnitAccountBusinessId, violations);
        validateTargetType(data.getTargetType(), violations);
        validateNumericFields(data, violations);
        validatePerformanceImpacted(data, violations);
        
        return violations;
    }
    
    private void validateTargetUnitBusinessId(String targetUnitBusinessId, String expectedTargetUnitAccountBusinessId, List<PerformanceAccountTemplateViolation> violations) {
        if (targetUnitBusinessId == null
                || !targetUnitBusinessId.equalsIgnoreCase(expectedTargetUnitAccountBusinessId)) {
            violations.add(new PerformanceAccountTemplateViolation(
                    TargetUnitIdentityAndPerformanceCellsReferenceEnum.TU_IDENTIFIER.getCellAddress(),
                    PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_TARGET_UNIT_BUSINESS_ID)
            );
        }
    }
    
    private void validateTargetType(String targetType, List<PerformanceAccountTemplateViolation> violations) {
        if (targetType != null && TargetType.fromDescription(targetType) == null) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            TargetUnitIdentityAndPerformanceCellsReferenceEnum.TARGET_TYPE.getCellAddress(),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_TARGET_TYPE
                    ));
        }
    }
    
    private void validateNumericFields(PerformanceAccountTemplateReportData data, List<PerformanceAccountTemplateViolation> violations) {
        if (StringUtils.isNotBlank(data.getTargetPercentage()) && !isBigDecimal(data.getTargetPercentage())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            TargetUnitIdentityAndPerformanceCellsReferenceEnum.TARGET_PERCENTAGE.getCellAddress(),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC)
            );
        }
        
        if (StringUtils.isNotBlank(data.getImprovementAchievedPercentage()) && !isBigDecimal(data.getImprovementAchievedPercentage())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            TargetUnitIdentityAndPerformanceCellsReferenceEnum.IMPROVEMENT_ACHIEVED_PERCENTAGE.getCellAddress(),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC)
            );
        }
        
        if (StringUtils.isBlank(data.getImprovementAccountedPercentage()) || !isBigDecimal(data.getImprovementAccountedPercentage())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            TargetUnitIdentityAndPerformanceCellsReferenceEnum.IMPROVEMENT_ACCOUNTED_PERCENTAGE.getCellAddress(),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC)
            );
        }
        
        if (StringUtils.isBlank(data.getTotalEstimateChangeInEnergyConsumptionPercentage())
                || !isBigDecimal(data.getTotalEstimateChangeInEnergyConsumptionPercentage())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(data.getTotalRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.ESTIMATED_CHANGE_IN_ENERGY_CONSUMPTION_PERCENTAGE.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC)
            );
        }
        
        if (StringUtils.isBlank(data.getTotalEstimateChangeInCarbonEmissionsPercentage())
                || !isBigDecimal(data.getTotalEstimateChangeInCarbonEmissionsPercentage())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(data.getTotalRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.ESTIMATED_CHANGE_IN_CARBON_EMISSIONS_PERCENTAGE.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC)
            );
        }
    }
    
    private void validatePerformanceImpacted(PerformanceAccountTemplateReportData data, List<PerformanceAccountTemplateViolation> violations) {
        String impactedByMeasures = data.getPerformanceImpactedByAnyImplementedMeasures();
        String text = data.getPerformanceImpactedByAnyImplementedMeasuresSupportingText();
        List<PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow> actionsList = data.getEnergyOrCarbonSavingActionsAndMeasuresImplemented();
        
        if ("No".equalsIgnoreCase(impactedByMeasures)) {
            if (StringUtils.isBlank(text)) {
                violations.add(
                        new PerformanceAccountTemplateViolation(
                                TargetUnitIdentityAndPerformanceCellsReferenceEnum.PERFORMANCE_IMPACTED_BY_ANY_IMPLEMENTED_MEASURES_SUPPORTING_TEXT.getCellAddress(),
                                PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_PERFORMANCE_IMPACTED_SUPPORTING_TEXT)
                );
            }
        } else if ("Yes".equalsIgnoreCase(impactedByMeasures)) {
            if (actionsList == null || actionsList.isEmpty()) {
                violations.add(
                        new PerformanceAccountTemplateViolation(
                                TargetUnitIdentityAndPerformanceCellsReferenceEnum.PERFORMANCE_IMPACTED_BY_ANY_IMPLEMENTED_MEASURES.getCellAddress(),
                                PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_PERFORMANCE_IMPACTED_EMPTY_TABLE)
                );
            }
        } else {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            TargetUnitIdentityAndPerformanceCellsReferenceEnum.PERFORMANCE_IMPACTED_BY_ANY_IMPLEMENTED_MEASURES.getCellAddress(),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_PERFORMANCE_IMPACTED_VALUE)
            );
        }
    }
}
