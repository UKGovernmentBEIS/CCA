package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceCancelledServiceTest {

    @InjectMocks
    private NonComplianceCancelledService service;

    @Mock
    private RequestService requestService;

    @Test
    void cancelAudit() {
        String requestId = "ADS_1-T00003-NCOM-1";
        String regulatorAssignee = UUID.randomUUID().toString();

        Request request = new Request();
        FacilityAuditRequestPayload payload = FacilityAuditRequestPayload.builder().regulatorAssignee(regulatorAssignee).build();
        request.setPayload(payload);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.cancel(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request,
                null,
                CcaRequestActionType.NON_COMPLIANCE_CANCELLED,
                regulatorAssignee);
    }
}
