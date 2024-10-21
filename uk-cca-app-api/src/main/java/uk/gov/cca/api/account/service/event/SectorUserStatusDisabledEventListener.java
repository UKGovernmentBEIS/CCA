package uk.gov.cca.api.account.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserStatusDisabledEvent;

@RequiredArgsConstructor
@Component(value =  "sectorUserStatusDisabledEventListener")
public class SectorUserStatusDisabledEventListener {

    private final TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @Order(1)
    @EventListener(SectorUserStatusDisabledEvent.class)
    public void onSectorUserStatusDisabledEvent(SectorUserStatusDisabledEvent event) {
        targetUnitAccountSiteContactService.removeUserFromTargetUnitAccountSiteContact(event.getUserId(), event.getSectorAssociationId());
    }
}
