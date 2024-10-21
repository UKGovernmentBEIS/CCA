package uk.gov.cca.api.web.orchestrator.authorization.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityUpdateService;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;
import uk.gov.netz.api.user.operator.service.OperatorUserNotificationGateway;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountOperatorUserAuthorityUpdateOrchestratorTest {

    @InjectMocks
    private AccountOperatorUserAuthorityUpdateOrchestrator service;

    @Mock
    private CcaOperatorAuthorityUpdateService operatorAuthorityUpdateService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Test
    void updateAccountOperatorAuthorities() {
        Long accountId = 1L;
        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities = List.of(
                AccountOperatorAuthorityUpdateDTO.builder().userId("user").roleCode("newRole").authorityStatus(AuthorityStatus.ACTIVE).build()
        );

        List<NewUserActivated> activatedOperators = List.of(NewUserActivated.builder().userId("user").build());

        when(operatorAuthorityUpdateService.updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId))
                .thenReturn(activatedOperators);

        service.updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId);

        verify(operatorAuthorityUpdateService, times(1))
                .updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId);
        verify(operatorUserNotificationGateway, times(1)).notifyUsersUpdateStatus(activatedOperators);
    }

    @Test
    void updateAccountOperatorAuthorities_empty_notifications() {
        Long accountId = 1L;
        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities = List.of(
                AccountOperatorAuthorityUpdateDTO.builder().userId("user").roleCode("newRole").authorityStatus(AuthorityStatus.ACTIVE).build()
        );

        when(operatorAuthorityUpdateService.updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId))
                .thenReturn(List.of());

        service.updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId);

        verify(operatorAuthorityUpdateService, times(1))
                .updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId);
        verify(operatorUserNotificationGateway, never()).notifyUsersUpdateStatus(anyList());
    }
}
