package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.ActionCategoryType;

class ActionCategoryTypeTest {
    
    @Test
    void fromDescription_shouldReturnNull_whenInputIsNull() {
        ActionCategoryType result = ActionCategoryType.fromDescription(null);
        assertNull(result);
    }
    
    @Test
    void fromDescription_shouldReturnNull_whenInputDoesNotMatch() {
        ActionCategoryType result = ActionCategoryType.fromDescription("Unknown Category");
        assertNull(result);
    }
    
    @Test
    void fromDescription_shouldReturnCorrectEnum_whenInputHasLeadingTrailingSpaces() {
        ActionCategoryType result = ActionCategoryType.fromDescription("   Energy Management   ");
        assertEquals(ActionCategoryType.ENERGY_MANAGEMENT, result);
    }
    
    @Test
    void fromDescription_shouldReturnCorrectEnum_whenCaseIsIgnored() {
        ActionCategoryType result = ActionCategoryType.fromDescription("PROCESS OPTIMISATION");
        assertEquals(ActionCategoryType.PROCESS_OPTIMISATION, result);
    }
    
    @Test
    void fromDescription_shouldReturnCorrectEnum_forExactMatches() {
        assertEquals(
                ActionCategoryType.ENERGY_MANAGEMENT,
                ActionCategoryType.fromDescription("Energy Management")
        );
        assertEquals(
                ActionCategoryType.PROCESS_OPTIMISATION,
                ActionCategoryType.fromDescription("Process Optimisation")
        );
        assertEquals(
                ActionCategoryType.NEW_TECHNOLOGY_UPTAKE,
                ActionCategoryType.fromDescription("New Technology Uptake")
        );
        assertEquals(
                ActionCategoryType.INSULATION_IMPROVEMENT,
                ActionCategoryType.fromDescription("Insulation Improvement")
        );
        assertEquals(
                ActionCategoryType.INFRASTRUCTURE_IMPROVEMENT,
                ActionCategoryType.fromDescription("Infrastructure Improvement")
        );
        assertEquals(
                ActionCategoryType.SWITCHING_NON_RENEWABLE_FUELS,
                ActionCategoryType.fromDescription("Switching Non-renewable fuels")
        );
        assertEquals(
                ActionCategoryType.HEAT_RECOVERY,
                ActionCategoryType.fromDescription("Heat Recovery")
        );
        assertEquals(
                ActionCategoryType.SWITCHING_TO_CHP,
                ActionCategoryType.fromDescription("Switching to CHP")
        );
        assertEquals(
                ActionCategoryType.SWITCHING_TO_BIOMASS_HEATING,
                ActionCategoryType.fromDescription("Switching to Biomass Heating")
        );
        assertEquals(
                ActionCategoryType.SWITCHING_TO_RENEWABLE_POWER,
                ActionCategoryType.fromDescription("Switching to Renewable Power")
        );
        assertEquals(
                ActionCategoryType.OTHER_CONTRIBUTIONS,
                ActionCategoryType.fromDescription("Other Contributions")
        );
    }
}

