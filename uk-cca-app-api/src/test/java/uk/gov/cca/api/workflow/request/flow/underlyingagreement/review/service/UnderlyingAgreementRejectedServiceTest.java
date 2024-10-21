package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementRejectedServiceTest {

    @InjectMocks
    UnderlyingAgreementRejectedService service;
    @Mock
    private RequestService requestService;
    @Mock
    private TargetUnitAccountUpdateService targetUnitAccountUpdateService;
    @Test
    void reject() {
        final String requestId = "1";
        final Long accountId = 1L;
        final Request request = Request.builder().id(requestId).accountId(accountId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        //Invoke
        service.reject(requestId);

        // Verify
        verify(targetUnitAccountUpdateService, times(1))
                .handleTargetUnitAccountRejected(accountId);

    }
}