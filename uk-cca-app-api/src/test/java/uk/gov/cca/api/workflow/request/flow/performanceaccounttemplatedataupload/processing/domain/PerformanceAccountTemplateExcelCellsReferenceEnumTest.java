package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.utils.ExcelCellUtils;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateExcelCellsReferenceEnumTest {
    
    @Mock
    private Cell cell;
    
    @Mock
    private FormulaEvaluator eval;
    
    @Mock
    private PerformanceAccountTemplateReportData data;
    
    @Test
    void testTuIdentifierPopulate() {
        try (MockedStatic<ExcelCellUtils> mockedStatic = mockStatic(ExcelCellUtils.class)) {
            mockedStatic.when(() -> ExcelCellUtils.getCellValueAsString(cell, eval))
                    .thenReturn("TU123");
            
            TargetUnitIdentityAndPerformanceCellsReferenceEnum.TU_IDENTIFIER.populate(cell, data, eval);
            
            verify(data, times(1)).setTargetUnitAccountBusinessId("TU123");
        }
    }
    
    @Test
    void testOperatorNamePopulate() {
        try (MockedStatic<ExcelCellUtils> mockedStatic = mockStatic(ExcelCellUtils.class)) {
            mockedStatic.when(() -> ExcelCellUtils.getCellValueAsString(cell, eval))
                    .thenReturn("OperatorName");
            
            TargetUnitIdentityAndPerformanceCellsReferenceEnum.OPERATOR_NAME.populate(cell, data, eval);
            
            verify(data, times(1)).setOperatorName("OperatorName");
        }
    }
    
    @Test
    void testTargetTypePopulate() {
        try (MockedStatic<ExcelCellUtils> mockedStatic = mockStatic(ExcelCellUtils.class)) {
            mockedStatic.when(() -> ExcelCellUtils.getCellValueAsString(cell, eval))
                    .thenReturn("SomeTargetType");
            
            TargetUnitIdentityAndPerformanceCellsReferenceEnum.TARGET_TYPE.populate(cell, data, eval);
            
            verify(data, times(1)).setTargetType("SomeTargetType");
        }
    }
    
    @Test
    void testTargetPercentagePopulate() {
        try (MockedStatic<ExcelCellUtils> mockedStatic = mockStatic(ExcelCellUtils.class)) {
            mockedStatic.when(() -> ExcelCellUtils.getCellValueAsString(cell, eval))
                    .thenReturn("10.5");
            
            TargetUnitIdentityAndPerformanceCellsReferenceEnum.TARGET_PERCENTAGE.populate(cell, data, eval);
            
            verify(data, times(1)).setTargetPercentage("10.5");
        }
    }
    
    @Test
    void testImprovementAchievedPercentagePopulate() {
        try (MockedStatic<ExcelCellUtils> mockedStatic = mockStatic(ExcelCellUtils.class)) {
            mockedStatic.when(() -> ExcelCellUtils.getCellValueAsString(cell, eval))
                    .thenReturn("12.34");
            
            TargetUnitIdentityAndPerformanceCellsReferenceEnum.IMPROVEMENT_ACHIEVED_PERCENTAGE.populate(cell, data, eval);
            
            verify(data, times(1)).setImprovementAchievedPercentage("12.34");
        }
    }
    
    @Test
    void testImprovementAccountedPercentagePopulate() {
        try (MockedStatic<ExcelCellUtils> mockedStatic = mockStatic(ExcelCellUtils.class)) {
            mockedStatic.when(() -> ExcelCellUtils.getCellValueAsString(cell, eval))
                    .thenReturn("55");
            
            TargetUnitIdentityAndPerformanceCellsReferenceEnum.IMPROVEMENT_ACCOUNTED_PERCENTAGE.populate(cell, data, eval);
            
            verify(data, times(1)).setImprovementAccountedPercentage("55");
        }
    }
    
    @Test
    void testPerformanceImpactedByAnyImplementedMeasuresPopulate() {
        try (MockedStatic<ExcelCellUtils> mockedStatic = mockStatic(ExcelCellUtils.class)) {
            mockedStatic.when(() -> ExcelCellUtils.getCellValueAsString(cell, eval))
                    .thenReturn("Yes");
            
            TargetUnitIdentityAndPerformanceCellsReferenceEnum
                    .PERFORMANCE_IMPACTED_BY_ANY_IMPLEMENTED_MEASURES
                    .populate(cell, data, eval);
            
            verify(data, times(1)).setPerformanceImpactedByAnyImplementedMeasures("Yes");
        }
    }
    
    @Test
    void testPerformanceImpactedByAnyImplementedMeasuresSupportingTextPopulate() {
        try (MockedStatic<ExcelCellUtils> mockedStatic = mockStatic(ExcelCellUtils.class)) {
            mockedStatic.when(() -> ExcelCellUtils.getCellValueAsString(cell, eval))
                    .thenReturn("Supporting details");
            
            TargetUnitIdentityAndPerformanceCellsReferenceEnum
                    .PERFORMANCE_IMPACTED_BY_ANY_IMPLEMENTED_MEASURES_SUPPORTING_TEXT
                    .populate(cell, data, eval);
            
            verify(data, times(1))
                    .setPerformanceImpactedByAnyImplementedMeasuresSupportingText("Supporting details");
        }
    }
}
