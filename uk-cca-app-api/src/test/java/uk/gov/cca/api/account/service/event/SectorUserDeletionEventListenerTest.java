package uk.gov.cca.api.account.service.event;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserDeletionEvent;

@ExtendWith(MockitoExtension.class)
class SectorUserDeletionEventListenerTest {

    @InjectMocks
    private SectorUserDeletionEventListener listener;

    @Mock
    private TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @Test
    void onSectorUserDeletedEvent() {
        String userId = "user";
        Long sectorAssociationId = 1L;
        
        SectorUserDeletionEvent event = SectorUserDeletionEvent.builder().userId(userId).sectorAssociationId(sectorAssociationId).build();

        listener.onSectorUserDeletedEvent(event);

        verify(targetUnitAccountSiteContactService, times(1)).removeUserFromTargetUnitAccountSiteContact(userId, sectorAssociationId);
    }
}
