package uk.gov.cca.api.workflow.request.application.verificationbodyappointed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.event.AccountVerificationBodyAppointedEvent;
import uk.gov.cca.api.workflow.request.application.verificationbodyappointed.AccountVerificationBodyAppointedEventListener;
import uk.gov.cca.api.workflow.request.application.verificationbodyappointed.RequestVerificationBodyService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountVerificationBodyAppointedEventListenerTest {

    @InjectMocks
    private AccountVerificationBodyAppointedEventListener listener;
    
    @Mock
    private RequestVerificationBodyService requestVerificationBodyService;
    
    @Test
    void onAccountVerificationBodyAppointedEvent() {
        Long verificationBodyId = 1L;
        Long accountId = 1L;
        AccountVerificationBodyAppointedEvent event = 
                AccountVerificationBodyAppointedEvent.builder().accountId(accountId).verificationBodyId(verificationBodyId).build();
        
        listener.onAccountVerificationBodyAppointedEvent(event);
        
        verify(requestVerificationBodyService, times(1)).appointVerificationBodyToRequestsOfAccount(verificationBodyId, accountId);
    }
}
