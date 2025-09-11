package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import static uk.gov.cca.api.common.utils.ExcelCellUtils.getCellValueAsString;

import java.util.function.BiConsumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellAddress;

@Getter
@AllArgsConstructor
public enum TargetUnitIdentityAndPerformanceCellsReferenceEnum {
    
    // TU Identity & Performance
    TU_IDENTIFIER(new CellAddress(2, 3),
            (value, data) -> data.setTargetUnitAccountBusinessId(value)),
    OPERATOR_NAME(new CellAddress(3, 3),
            (value, data) -> data.setOperatorName(value)),
    TARGET_TYPE(new CellAddress(4, 3),
            (value, data) -> data.setTargetType(value)),
    TARGET_PERCENTAGE(new CellAddress(5, 3),
            (value, data) -> data.setTargetPercentage(value)),
    IMPROVEMENT_ACHIEVED_PERCENTAGE(new CellAddress(6, 3),
            (value, data) -> data.setImprovementAchievedPercentage(
                    value)),
    IMPROVEMENT_ACCOUNTED_PERCENTAGE(new CellAddress(7, 3),
            (value, data) -> data.setImprovementAccountedPercentage(
                    value)),
    PERFORMANCE_IMPACTED_BY_ANY_IMPLEMENTED_MEASURES(new CellAddress(9, 3),
            (value, data) -> data.setPerformanceImpactedByAnyImplementedMeasures(
                    value)),
    PERFORMANCE_IMPACTED_BY_ANY_IMPLEMENTED_MEASURES_SUPPORTING_TEXT(new CellAddress(9, 5),
            (value, data) -> data.setPerformanceImpactedByAnyImplementedMeasuresSupportingText(
                    value));
    
    
    private final CellAddress cellAddress;
    private final BiConsumer<String, PerformanceAccountTemplateReportData> consumer;
    
    public void populate(Cell cell, PerformanceAccountTemplateReportData data, FormulaEvaluator eval) {
        consumer.accept(getCellValueAsString(cell, eval), data);
    }
}
