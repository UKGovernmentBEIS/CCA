package uk.gov.cca.api.workflow.request.core.domain.enumeration;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestTaskTypeTest {

    @Test
    void getSupportingRequestTaskTypes() {
        Set<RequestTaskType> expectedRequestTaskTypes = Set.of();

        assertEquals(expectedRequestTaskTypes, RequestTaskType.getSupportingRequestTaskTypes());
    }
}