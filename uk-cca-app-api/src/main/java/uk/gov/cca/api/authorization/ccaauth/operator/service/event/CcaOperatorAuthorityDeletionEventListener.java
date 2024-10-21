package uk.gov.cca.api.authorization.ccaauth.operator.service.event;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.operator.event.CcaOperatorAuthorityDeletionEvent;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;

@RequiredArgsConstructor
@Component(value = "authorizationCcaOperatorAuthorityDeletionEventListener")
public class CcaOperatorAuthorityDeletionEventListener {

    private final UserRoleTypeService userRoleTypeService;

    @Order(100)
    @EventListener(CcaOperatorAuthorityDeletionEvent.class)
    public void onAuthorityDeletedEvent(CcaOperatorAuthorityDeletionEvent deletionEvent) {
        if (!deletionEvent.isExistAuthoritiesOnOtherAccounts()) {
            userRoleTypeService.deleteUserRoleType(deletionEvent.getUserId());
        }
    }
}