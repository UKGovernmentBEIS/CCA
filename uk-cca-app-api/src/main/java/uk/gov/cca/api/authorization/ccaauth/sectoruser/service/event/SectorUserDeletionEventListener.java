package uk.gov.cca.api.authorization.ccaauth.sectoruser.service.event;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserDeletionEvent;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;

@RequiredArgsConstructor
@Component(value = "authorizationSectorUserDeletionEventListener")
public class SectorUserDeletionEventListener {

    private final UserRoleTypeService userRoleTypeService;

    @Order(100)
    @EventListener(SectorUserDeletionEvent.class)
    public void onSectorUserDeletedEvent(SectorUserDeletionEvent deletionEvent) {
        if (!deletionEvent.isExistCcaAuthoritiesOnOtherSectorAssociations()) {
            userRoleTypeService.deleteUserRoleType(deletionEvent.getUserId());
        }
    }
}
