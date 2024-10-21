package uk.gov.cca.api.sectorassociation.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSiteContactService;
import uk.gov.netz.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private RegulatorAuthorityDeletionEventListener listener;

    @Mock
    private SectorAssociationSiteContactService sectorAssociationSiteContactService;

    @Test
    void onRegulatorUserDeletedEvent() {
        String userId = "user";
        RegulatorAuthorityDeletionEvent event = RegulatorAuthorityDeletionEvent.builder().userId(userId).build();

        listener.onRegulatorUserDeletedEvent(event);

        verify(sectorAssociationSiteContactService, times(1)).removeUserFromSectorAssociationSiteContact(userId);
    }
}
