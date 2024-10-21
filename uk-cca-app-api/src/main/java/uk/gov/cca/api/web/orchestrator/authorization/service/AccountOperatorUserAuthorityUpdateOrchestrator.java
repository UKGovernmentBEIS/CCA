package uk.gov.cca.api.web.orchestrator.authorization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityUpdateService;
import uk.gov.netz.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;
import uk.gov.netz.api.user.operator.service.OperatorUserNotificationGateway;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountOperatorUserAuthorityUpdateOrchestrator {

    private final CcaOperatorAuthorityUpdateService ccaOperatorAuthorityUpdateService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Transactional
    public void updateAccountOperatorAuthorities(List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities, Long accountId) {

        List<NewUserActivated> activatedOperators = ccaOperatorAuthorityUpdateService
                .updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId);

        if (!activatedOperators.isEmpty()) {
            operatorUserNotificationGateway.notifyUsersUpdateStatus(activatedOperators);
        }
    }
}
