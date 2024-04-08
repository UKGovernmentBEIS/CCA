package uk.gov.cca.api.account.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.AccountCaSiteContactService;
import uk.gov.cca.api.account.service.event.RegulatorAuthorityDeletionEventListener;
import uk.gov.cca.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private RegulatorAuthorityDeletionEventListener listener;
    
    @Mock
    private AccountCaSiteContactService accountCaSiteContactService;
    
    @Test
    void onRegulatorUserDeletedEvent() {
        String userId = "user";
        RegulatorAuthorityDeletionEvent event = RegulatorAuthorityDeletionEvent.builder().userId(userId).build();

        listener.onRegulatorUserDeletedEvent(event);
        
        verify(accountCaSiteContactService, times(1)).removeUserFromCaSiteContact(userId);
    }
}
