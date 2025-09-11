package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.cca.api.common.utils.ExcelCellUtils.isBigDecimal;

@Component
public class EnergyOrCarbonSavingActionsAndMeasuresImplementedValidator {

    public List<PerformanceAccountTemplateViolation> validate(PerformanceAccountTemplateReportData data) {
        List<EnergyOrCarbonSavingActionsAndMeasuresImplementedRow> rows =
                data.getEnergyOrCarbonSavingActionsAndMeasuresImplemented();
        List<PerformanceAccountTemplateViolation> violations = new ArrayList<>();

        if (rows != null && !rows.isEmpty()) {
            rows.forEach(row -> validateRow(row, violations));
        }

        return violations;
    }

    private void validateRow(EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row, List<PerformanceAccountTemplateViolation> violations) {
        validateCategory(row, violations);
        validateNonEmptyFields(row, violations);
        validateImpactedType(row, violations);
        validateNumericFields(row, violations);
    }

    private void validateCategory(EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row, List<PerformanceAccountTemplateViolation> violations) {
        String category = row.getActionCategoryType();
        if (ActionCategoryType.fromDescription(category) == null) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(
                                    row.getExcelRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.ACTION_CATEGORY_TYPE.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_ACTION_CATEGORY
                    )
            );
        }
    }

    private void validateNonEmptyFields(EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row,
                                        List<PerformanceAccountTemplateViolation> violations) {
        if (StringUtils.isBlank(row.getSavingActionsImplemented())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(
                                    row.getExcelRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.SAVING_ACTIONS_IMPLEMENTED.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_PROVIDED
                    )
            );
        }

        if (StringUtils.isBlank(row.getReasonsForImplementation())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(
                                    row.getExcelRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.REASONS_FOR_IMPLEMENTATION.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_PROVIDED
                    )
            );
        }

        if (StringUtils.isBlank(row.getImplementationDate())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(
                                    row.getExcelRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.IMPLEMENTATION_DATE.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_PROVIDED
                    )
            );
        }
    }

    private void validateImpactedType(EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row,
                                      List<PerformanceAccountTemplateViolation> violations) {
        String impactedValue = row.getFixedEnergyConsumptionOrCarbonEmissionsImpacted();
        if (EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription(impactedValue) == null) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(
                                    row.getExcelRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.FIXED_ENERGY_CONSUMPTION_OR_CARBON_EMISSIONS_IMPACTED.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_IMPACTED_TYPE
                    )
            );
        }
    }

    private void validateNumericFields(PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row,
                                       List<PerformanceAccountTemplateViolation> violations) {
        if (StringUtils.isBlank(row.getEnergyConsumptionOrCarbonEmissionsImpactedPercentage()) || !isBigDecimal(row.getEnergyConsumptionOrCarbonEmissionsImpactedPercentage())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(
                                    row.getExcelRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.ENERGY_CONSUMPTION_OR_CARBON_EMISSIONS_IMPACTED_PERCENTAGE.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC
                    )
            );
        }

        if (StringUtils.isBlank(row.getExpectedExtentOfChangeImplementedPercentage()) || !isBigDecimal(row.getExpectedExtentOfChangeImplementedPercentage())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(
                                    row.getExcelRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.EXPECTED_EXTENT_OF_CHANGE_IMPLEMENTED_PERCENTAGE.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC
                    )
            );
        }

        if (StringUtils.isBlank(row.getExpectedSavingsFromTheChangeImplementedPercentage()) || !isBigDecimal(row.getExpectedSavingsFromTheChangeImplementedPercentage())) {
            violations.add(
                    new PerformanceAccountTemplateViolation(
                            new CellAddress(
                                    row.getExcelRowIndex(),
                                    EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns.EXPECTED_SAVINGS_FROM_CHANGE_IMPLEMENTED_PERCENTAGE.getIndex()),
                            PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC
                    )
            );
        }
    }
}
