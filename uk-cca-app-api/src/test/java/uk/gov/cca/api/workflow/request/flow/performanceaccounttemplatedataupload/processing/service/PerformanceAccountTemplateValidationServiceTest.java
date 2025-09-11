package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules.EnergyOrCarbonSavingActionsAndMeasuresImplementedValidator;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules.PerformanceAccountTemplateStructureValidator;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules.TargetUnitIdentityAndPerformanceValidator;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_ACTION_CATEGORY;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.TOTAL_KEYWORD_NOT_FOUND;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateValidationServiceTest {
    
    @Mock
    private PerformanceAccountTemplateStructureValidator performanceAccountTemplateStructureValidator;
    
    @Mock
    private EnergyOrCarbonSavingActionsAndMeasuresImplementedValidator energyOrCarbonSavingActionsAndMeasuresImplementedValidator;
    
    @Mock
    private TargetUnitIdentityAndPerformanceValidator targetUnitIdentityAndPerformanceValidator;
    
    @InjectMocks
    private PerformanceAccountTemplateProcessingValidationService service;
    
    @Test
    void validateData_shouldReturnEmptyList_whenNoViolationsFromValidators() {
        PerformanceAccountTemplateReportData data = new PerformanceAccountTemplateReportData();
        String targetUnitId = "TARGET-123";
        
        List<PerformanceAccountTemplateViolation> result = service.validateData(data, targetUnitId);
        
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected no violations if both validators return an empty list.");
    }
    
    @Test
    void validateData_shouldCombineViolations_whenValidatorsReturnViolations() {
        PerformanceAccountTemplateReportData data = new PerformanceAccountTemplateReportData();
        String targetUnitId = "TARGET-123";
        
        PerformanceAccountTemplateViolation violation1 =
                new PerformanceAccountTemplateViolation(INVALID_VALUE_NUMERIC);
        PerformanceAccountTemplateViolation violation2 =
                new PerformanceAccountTemplateViolation(INVALID_ACTION_CATEGORY);
        
        when(targetUnitIdentityAndPerformanceValidator.validate(data, targetUnitId))
                .thenReturn(Arrays.asList(violation1));
        when(energyOrCarbonSavingActionsAndMeasuresImplementedValidator.validate(data))
                .thenReturn(Arrays.asList(violation2));
        
        List<PerformanceAccountTemplateViolation> result = service.validateData(data, targetUnitId);
        
        assertEquals(2, result.size());
        assertTrue(result.contains(violation1));
        assertTrue(result.contains(violation2));
    }
    
    @Test
    void validateTemplateStructure_shouldReturnStructureViolations_whenValidatorReturnsViolations() {
        Workbook workbook = mock(Workbook.class);
        Sheet sheet = mock(Sheet.class);
        PerformanceAccountTemplateViolation violation =
                new PerformanceAccountTemplateViolation(TOTAL_KEYWORD_NOT_FOUND);
        List<PerformanceAccountTemplateViolation> structureViolations = Arrays.asList(violation);
        
        when(workbook.getSheetAt(0)).thenReturn(sheet);
        when(performanceAccountTemplateStructureValidator.validate(sheet)).thenReturn(structureViolations);
        
        List<PerformanceAccountTemplateViolation> result = service.validateTemplateStructure(workbook);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(violation, result.get(0));
    }
    
    @Test
    void validateTemplateStructure_shouldReturnEmptyList_whenValidatorReturnsNoViolations() {
        Workbook workbook = mock(Workbook.class);
        Sheet sheet = mock(Sheet.class);
        when(workbook.getSheetAt(0)).thenReturn(sheet);
        
        List<PerformanceAccountTemplateViolation> result = service.validateTemplateStructure(workbook);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
