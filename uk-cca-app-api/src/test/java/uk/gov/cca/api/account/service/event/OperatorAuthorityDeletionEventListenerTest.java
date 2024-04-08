package uk.gov.cca.api.account.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.AccountContactDeletionService;
import uk.gov.cca.api.account.service.event.OperatorAuthorityDeletionEventListener;
import uk.gov.cca.api.authorization.operator.event.OperatorAuthorityDeletionEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private OperatorAuthorityDeletionEventListener operatorAuthorityDeletionEventListener;

    @Mock
    private AccountContactDeletionService accountContactDeletionService;

    @Test
    void onOperatorUserDeletionEventListener() {
        String userId = "userId";
        Long accountId = 1L;
        OperatorAuthorityDeletionEvent event = OperatorAuthorityDeletionEvent.builder()
            .accountId(accountId)
            .userId(userId)
            .build();

        operatorAuthorityDeletionEventListener.onOperatorUserDeletionEventListener(event);

        verify(accountContactDeletionService,times(1)).removeUserFromAccountContacts(userId, accountId);
    }
}