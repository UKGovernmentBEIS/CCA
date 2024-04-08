package uk.gov.cca.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.cca.api.authorization.rules.services.authorization.RegulatorAccountAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.RegulatorCompAuthAuthorizationService;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatorAccountAuthorizationServiceTest {
    @InjectMocks
    private RegulatorAccountAuthorizationService regulatorAccountAuthorizationService;

    @Mock
    private AccountAuthorityInfoProvider accountAuthorityInfoProvider;

    @Mock
    private RegulatorCompAuthAuthorizationService regulatorCompAuthAuthorizationService;

    private final AppUser user = AppUser.builder().roleType(RoleType.REGULATOR).build();

    @Test
    void isAuthorized_account_true() {
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(true);

        assertTrue(regulatorAccountAuthorizationService.isAuthorized(user, accountId));
    }

    @Test
    void isAuthorized_account_false() {
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority)).thenReturn(false);

        assertFalse(regulatorAccountAuthorizationService.isAuthorized(user, accountId));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String permission = Permission.PERM_TASK_ASSIGNMENT;
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).thenReturn(true);

        assertTrue(regulatorAccountAuthorizationService.isAuthorized(user, accountId, permission));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        Long accountId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String permission = Permission.PERM_TASK_ASSIGNMENT;
        when(accountAuthorityInfoProvider.getAccountCa(accountId)).thenReturn(competentAuthority);
        when(regulatorCompAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).thenReturn(false);

        assertFalse(regulatorAccountAuthorizationService.isAuthorized(user, accountId, permission));
    }

    @Test
    void getType() {
        assertEquals(RoleType.REGULATOR, regulatorAccountAuthorizationService.getRoleType());
    }
}