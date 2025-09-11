package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTerminationCancelledServiceTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private AdminTerminationCancelledService service;

    @Test
    void testAdd() {
        String requestId = "ACCT_1-ATER-1";
        String regulatorAssignee = UUID.randomUUID().toString();

        Request request = new Request();
        AdminTerminationRequestPayload payload = AdminTerminationRequestPayload.builder().regulatorAssignee(regulatorAssignee).build();
        request.setPayload(payload);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.cancel(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request,
                null,
                CcaRequestActionType.ADMIN_TERMINATION_APPLICATION_CANCELLED,
                regulatorAssignee);
    }
}