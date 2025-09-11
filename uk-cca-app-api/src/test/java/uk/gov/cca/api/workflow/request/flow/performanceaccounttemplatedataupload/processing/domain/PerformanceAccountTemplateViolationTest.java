package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import org.apache.poi.ss.util.CellAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.*;

class PerformanceAccountTemplateViolationTest {
    
    @Test
    void constructor_shouldSetMessage_whenOnlyMessageIsProvided() {
        PerformanceAccountTemplateViolation violation =
                new PerformanceAccountTemplateViolation(INVALID_VALUE_NUMERIC);
        
        assertNull(violation.getCellAddress());
        assertNotNull(violation.getMessage());
    }
    
    @Test
    void constructor_shouldSetBothCellAddressAndMessage_whenBothAreProvided() {
        CellAddress cellAddress = new CellAddress("B14");
        
        PerformanceAccountTemplateViolation violation =
                new PerformanceAccountTemplateViolation(cellAddress, INVALID_ACTION_CATEGORY);
        
        assertEquals(cellAddress, violation.getCellAddress());
        assertNotNull(violation.getMessage());
    }
}
