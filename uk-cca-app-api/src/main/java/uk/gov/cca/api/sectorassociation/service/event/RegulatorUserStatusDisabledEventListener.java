package uk.gov.cca.api.sectorassociation.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSiteContactService;
import uk.gov.netz.api.authorization.regulator.event.RegulatorUserStatusDisabledEvent;

@RequiredArgsConstructor
@Component(value =  "sectorAssociationRegulatorUserStatusDisabledEventListener")
public class RegulatorUserStatusDisabledEventListener {

    private final SectorAssociationSiteContactService sectorAssociationSiteContactService;

    @Order(1)
    @EventListener(RegulatorUserStatusDisabledEvent.class)
    public void onRegulatorUserStatusDisabledEvent(RegulatorUserStatusDisabledEvent event) {
        sectorAssociationSiteContactService.removeUserFromSectorAssociationSiteContact(event.getUserId());
    }
}
