package uk.gov.cca.api.sectorassociation.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSiteContactService;
import uk.gov.netz.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;

@RequiredArgsConstructor
@Component(value =  "sectorAssociationRegulatorAuthorityDeletionEventListener")
public class RegulatorAuthorityDeletionEventListener {

    private final SectorAssociationSiteContactService sectorAssociationSiteContactService;

    @Order(1)
    @EventListener(RegulatorAuthorityDeletionEvent.class)
    public void onRegulatorUserDeletedEvent(RegulatorAuthorityDeletionEvent event) {
        sectorAssociationSiteContactService.removeUserFromSectorAssociationSiteContact(event.getUserId());
    }
}
