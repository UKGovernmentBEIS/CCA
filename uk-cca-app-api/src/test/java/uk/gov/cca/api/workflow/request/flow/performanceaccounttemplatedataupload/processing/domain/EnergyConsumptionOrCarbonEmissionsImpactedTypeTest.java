package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnergyConsumptionOrCarbonEmissionsImpactedTypeTest {
    
    @Test
    void fromDescription_shouldReturnNull_whenInputIsNull() {
        EnergyConsumptionOrCarbonEmissionsImpactedType result =
                EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription(null);
        assertNull(result);
    }
    
    @Test
    void fromDescription_shouldReturnNull_whenInputDoesNotMatch() {
        EnergyConsumptionOrCarbonEmissionsImpactedType result =
                EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription("SomethingElse");
        assertNull(result);
    }
    
    @Test
    void fromDescription_shouldIgnoreCaseAndTrim() {
        EnergyConsumptionOrCarbonEmissionsImpactedType result =
                EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription("   fIxEd aNd vArIaBlE   ");
        assertEquals(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE, result);
    }
    
    @Test
    void fromDescription_shouldReturnCorrectEnum_forExactMatches() {
        assertEquals(
                EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED,
                EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription("Fixed")
        );
        
        assertEquals(
                EnergyConsumptionOrCarbonEmissionsImpactedType.VARIABLE,
                EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription("Variable")
        );
        
        assertEquals(
                EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE,
                EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription("Fixed and Variable")
        );
    }
}

