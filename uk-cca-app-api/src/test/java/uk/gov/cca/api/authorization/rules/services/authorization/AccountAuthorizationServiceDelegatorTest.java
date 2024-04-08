package uk.gov.cca.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.services.authorization.AccountAuthorizationServiceDelegator;
import uk.gov.cca.api.authorization.rules.services.authorization.OperatorAccountAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.RegulatorAccountAuthorizationService;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class AccountAuthorizationServiceDelegatorTest {
    private AccountAuthorizationServiceDelegator accountAuthorizationServiceDelegator;
    private OperatorAccountAuthorizationService operatorAccountAuthorizationService;
    private RegulatorAccountAuthorizationService regulatorAccountAuthorizationService;

    @BeforeAll
    void setup() {
        operatorAccountAuthorizationService = Mockito.mock(OperatorAccountAuthorizationService.class);
        regulatorAccountAuthorizationService = Mockito.mock(RegulatorAccountAuthorizationService.class);
        accountAuthorizationServiceDelegator = new AccountAuthorizationServiceDelegator(
                List.of(operatorAccountAuthorizationService, regulatorAccountAuthorizationService));
        when(operatorAccountAuthorizationService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(regulatorAccountAuthorizationService.getRoleType()).thenReturn(RoleType.REGULATOR);
    }

    private final AppUser OPERATOR = AppUser.builder().roleType(RoleType.OPERATOR).build();
    private final AppUser REGULATOR = AppUser.builder().roleType(RoleType.REGULATOR).build();

    @Test
    void isAuthorized_operator_no_permissions() {
        Long accountId = 1L;
        when(operatorAccountAuthorizationService.isAuthorized(OPERATOR, accountId)).thenReturn(true);

        assertTrue(accountAuthorizationServiceDelegator.isAuthorized(OPERATOR, accountId));

        verify(operatorAccountAuthorizationService, times(1)).isAuthorized(OPERATOR, accountId);
    }

    @Test
    void isAuthorized_operator_with_permissions() {
        Long accountId = 1L;
        String permission = Permission.PERM_TASK_ASSIGNMENT;
        when(operatorAccountAuthorizationService.isAuthorized(OPERATOR, accountId, permission)).thenReturn(true);

        assertTrue(accountAuthorizationServiceDelegator.isAuthorized(OPERATOR, accountId, permission));

        verify(operatorAccountAuthorizationService, times(1)).isAuthorized(OPERATOR, accountId, permission);
    }

    @Test
    void isAuthorized_regulator_no_permissions() {
        Long accountId = 1L;
        when(regulatorAccountAuthorizationService.isAuthorized(REGULATOR, accountId)).thenReturn(true);

        assertTrue(accountAuthorizationServiceDelegator.isAuthorized(REGULATOR, accountId));

        verify(regulatorAccountAuthorizationService, times(1)).isAuthorized(REGULATOR, accountId);
    }

    @Test
    void isAuthorized_regulator_with_permissions() {
        Long accountId = 1L;
        String permission = Permission.PERM_ACCOUNT_USERS_EDIT;
        when(regulatorAccountAuthorizationService.isAuthorized(REGULATOR, accountId, permission)).thenReturn(true);

        assertTrue(accountAuthorizationServiceDelegator.isAuthorized(REGULATOR, accountId, permission));

        verify(regulatorAccountAuthorizationService, times(1)).isAuthorized(REGULATOR, accountId, permission);
    }
}