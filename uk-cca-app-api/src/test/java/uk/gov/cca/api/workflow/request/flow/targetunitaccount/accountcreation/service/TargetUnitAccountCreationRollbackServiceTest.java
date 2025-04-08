package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.repository.RequestRepository;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountCreationRollbackServiceTest {

    @InjectMocks
    private TargetUnitAccountCreationRollbackService targetUnitAccountCreationRollbackService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private TargetUnitAccountService targetUnitAccountService;

    @Test
    void rollback() {
        final String requestId = "request-id";
        final Long accountId = 1L;

        Request request = Request.builder()
                .id(requestId)
                .build();
        addResourcesToRequest(accountId, request);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        targetUnitAccountCreationRollbackService.rollback(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestRepository, times(1)).delete(request);
        verify(targetUnitAccountService, times(1)).deleteTargetUnitAccount(accountId);
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
