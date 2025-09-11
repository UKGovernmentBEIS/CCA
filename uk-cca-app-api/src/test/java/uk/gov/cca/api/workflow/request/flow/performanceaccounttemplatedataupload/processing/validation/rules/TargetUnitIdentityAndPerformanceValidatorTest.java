package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TargetUnitIdentityAndPerformanceValidatorTest {
    
    private final TargetUnitIdentityAndPerformanceValidator validator = new TargetUnitIdentityAndPerformanceValidator();
    
    private static final String VALID_TARGET_UNIT_BUSINESS_ID = "EXPECTED_ID";
    private static final String VALID_TARGET_TYPE_ABSOLUTE = "Absolute";
    
    private PerformanceAccountTemplateReportData createValidData() {
        PerformanceAccountTemplateReportData data = new PerformanceAccountTemplateReportData();
        data.setTargetUnitAccountBusinessId(VALID_TARGET_UNIT_BUSINESS_ID);
        data.setTargetType(VALID_TARGET_TYPE_ABSOLUTE);
        data.setTargetPercentage("10.5");
        data.setImprovementAchievedPercentage("5.0");
        data.setImprovementAccountedPercentage("3.5");
        data.setPerformanceImpactedByAnyImplementedMeasures("No");
        data.setPerformanceImpactedByAnyImplementedMeasuresSupportingText("Some justification text.");
        data.setTotalRowIndex(15);
        data.setTotalEstimateChangeInEnergyConsumptionPercentage("1.0");
        data.setTotalEstimateChangeInCarbonEmissionsPercentage("1.65");

        return data;
    }
    
    @Test
    void validate_shouldReturnNoViolations_whenAllDataIsValid() {
        PerformanceAccountTemplateReportData data = createValidData();
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertTrue(violations.isEmpty(), "No violations should be returned for valid data");
    }
    
    @Test
    void validate_shouldReturnViolation_whenTargetUnitBusinessIdDoesNotMatch() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setTargetUnitAccountBusinessId("WRONG_ID");
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_TARGET_UNIT_BUSINESS_ID.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnViolation_whenTargetTypeIsInvalid() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setTargetType("Unknown Type"); // Not in the valid list
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_TARGET_TYPE.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnViolation_whenTargetPercentageIsNotNumeric() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setTargetPercentage("NotANumber");
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnViolation_whenImprovementAchievedPercentageIsNotNumeric() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setImprovementAchievedPercentage("ABC");
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnViolation_whenImprovementAccountedPercentageIsBlank() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setImprovementAccountedPercentage(""); // blank
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnViolation_whenPerformanceImpactedIsNoButSupportingTextIsBlank() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setPerformanceImpactedByAnyImplementedMeasures("No");
        data.setPerformanceImpactedByAnyImplementedMeasuresSupportingText(""); // blank
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_PERFORMANCE_IMPACTED_SUPPORTING_TEXT.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnViolation_whenPerformanceImpactedIsYesButNoMeasuresProvided() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setPerformanceImpactedByAnyImplementedMeasures("Yes");
        data.setPerformanceImpactedByAnyImplementedMeasuresSupportingText(null); // Could be anything
        data.setEnergyOrCarbonSavingActionsAndMeasuresImplemented(null); // No entries
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_PERFORMANCE_IMPACTED_EMPTY_TABLE.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnViolation_whenPerformanceImpactedValueIsUnknown() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setPerformanceImpactedByAnyImplementedMeasures("MAYBE");
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_PERFORMANCE_IMPACTED_VALUE.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnNoViolations_whenPerformanceImpactedIsYesWithValidMeasures() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setPerformanceImpactedByAnyImplementedMeasures("Yes");
        data.setPerformanceImpactedByAnyImplementedMeasuresSupportingText("Test measure text.");
        
        PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow measure =
                new PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow();
        measure.setSavingActionsImplemented("Some Saving Measure");
        measure.setNotes("Details about the measure");
        data.setEnergyOrCarbonSavingActionsAndMeasuresImplemented(List.of(measure));
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertTrue(violations.isEmpty(),
                "No violations expected when 'Yes' is properly supported with measures.");
    }
    
    @Test
    void validate_shouldReturnViolation_whenTotalEstimateChangeInEnergyConsumptionPercentageIsNotNumeric() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setTotalEstimateChangeInEnergyConsumptionPercentage("Definitely not a numeric value");
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC.getMessage(),
                violations.getFirst().getMessage());
    }
    
    @Test
    void validate_shouldReturnViolation_whenTotalEstimateChangeInCarbonEmissionPercentageIsNotNumeric() {
        PerformanceAccountTemplateReportData data = createValidData();
        data.setTotalEstimateChangeInCarbonEmissionsPercentage("Definitely not a numeric value");
        
        List<PerformanceAccountTemplateViolation> violations =
                validator.validate(data, VALID_TARGET_UNIT_BUSINESS_ID);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC.getMessage(),
                violations.getFirst().getMessage());
    }
    
}
