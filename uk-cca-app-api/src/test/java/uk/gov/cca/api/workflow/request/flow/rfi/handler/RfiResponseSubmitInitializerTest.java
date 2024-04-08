package uk.gov.cca.api.workflow.request.flow.rfi.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestTaskAttachmentsUncoupleService;
import uk.gov.cca.api.workflow.request.flow.rfi.handler.RfiResponseSubmitInitializer;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RfiResponseSubmitInitializerTest {

    @InjectMocks
    private RfiResponseSubmitInitializer initializer;

    @Mock
    private RequestTaskAttachmentsUncoupleService uncoupleService;

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of());
    }
}
