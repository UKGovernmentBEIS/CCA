package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementCancelledServiceTest {
    @InjectMocks
    private UnderlyingAgreementCancelledService service;

    @Mock
    private RequestService requestService;

    @Mock
    private TargetUnitAccountUpdateService targetUnitAccountUpdateService;


    @Test
    void cancel() {
        String requestId = "requestId";
        Long accountId = 1L;
        String requestTaskAssignee = "assignee";
        UnderlyingAgreementRequestPayload payload = UnderlyingAgreementRequestPayload.builder()
                .sectorUserAssignee(requestTaskAssignee)
                .businessId("businessId")
                .build();

        Request request = Request.builder()
                .payload(payload)
                .build();
        addResourcesToRequest(accountId, request);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.cancel(requestId, SECTOR_USER);

        verify(targetUnitAccountUpdateService, times(1)).handleTargetUnitAccountCancelled(accountId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addActionToRequest(request, null, CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_CANCELLED, requestTaskAssignee);
    }
    
    private void addResourcesToRequest(Long accountId, Request request) {
		RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.ACCOUNT)
				.resourceId(accountId.toString())
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
	}

}
