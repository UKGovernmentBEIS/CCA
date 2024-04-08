package uk.gov.cca.api.account.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.account.service.AccountCaSiteContactService;
import uk.gov.cca.api.authorization.regulator.event.RegulatorUserStatusDisabledEvent;

@RequiredArgsConstructor
@Component(value =  "accountRegulatorUserStatusDisabledEventListener")
public class RegulatorUserStatusDisabledEventListener {

    private final AccountCaSiteContactService accountCaSiteContactService;

    @Order(1)
    @EventListener(RegulatorUserStatusDisabledEvent.class)
    public void onRegulatorUserStatusDisabledEvent(RegulatorUserStatusDisabledEvent event) {
        removeUserFromCaSiteContact(event.getUserId());
    }

    private void removeUserFromCaSiteContact(String userId) {
        accountCaSiteContactService.removeUserFromCaSiteContact(userId);
    }
}
