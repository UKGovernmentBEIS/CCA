package uk.gov.cca.api.account.service.event;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserStatusDisabledEvent;

@ExtendWith(MockitoExtension.class)
class SectorUserStatusDisabledEventListenerTest {

    @InjectMocks
    private SectorUserStatusDisabledEventListener listener;

    @Mock
    private TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @Test
    void onSectorUserStatusDisabledEvent() {
        String userId = "user";
        Long sectorAssociationId = 1L;
        SectorUserStatusDisabledEvent event = new SectorUserStatusDisabledEvent(userId, sectorAssociationId);

        listener.onSectorUserStatusDisabledEvent(event);

        verify(targetUnitAccountSiteContactService, times(1)).removeUserFromTargetUnitAccountSiteContact(userId, sectorAssociationId);
    }
}
