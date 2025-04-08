package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.sectoruser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import static org.mockito.Mockito.when;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class SectorUserAccountAuthorizationServiceTest {
    @InjectMocks
    private SectorUserAccountAuthorizationService sectorUserAccountAuthorizationService;

    @Mock
    private TargetUnitAuthorityInfoProvider targetUnitAuthorityInfoProvider;

    @Mock
    private SectorUserSectorAssociationAuthorizationService sectorUserSectorAssociationAuthorizationService;

    private final AppUser user = AppUser.builder().roleType(CcaRoleTypeConstants.SECTOR_USER).build();

    @Test
    void isAuthorized_account_with_criteria_true() {
        Long accountId = 1L;
        Long sectorId = 1L;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                .build();
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId)).thenReturn(true);

        Assertions.assertTrue(sectorUserAccountAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_with_permission_true() {
        Long accountId = 1L;
        Long sectorId = 1L;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                .permission("permission1")
                .build();
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId, "permission1")).thenReturn(true);

        Assertions.assertTrue(sectorUserAccountAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_false() {
        Long accountId = 1L;
        Long sectorId = 1L;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                .build();
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId)).thenReturn(false);

        Assertions.assertFalse(sectorUserAccountAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_with_criteria_with_permission_false() {
        Long accountId = 1L;
        Long sectorId = 1L;
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                .permission("permission1")
                .build();
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId, "permission1")).thenReturn(false);

        Assertions.assertFalse(sectorUserAccountAuthorizationService.isAuthorized(user, authorizationCriteria));
    }

    @Test
    void isAuthorized_account_true() {
        Long accountId = 1L;
        Long sectorId = 1L;
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId)).thenReturn(true);

        Assertions.assertTrue(sectorUserAccountAuthorizationService.isAuthorized(user, accountId));
    }

    @Test
    void isAuthorized_account_false() {
        Long accountId = 1L;
        Long sectorId = 1L;
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId)).thenReturn(false);

        Assertions.assertFalse(sectorUserAccountAuthorizationService.isAuthorized(user, accountId));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        Long accountId = 1L;
        Long sectorId = 1L;
        String permission = "permission1";
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId, permission)).thenReturn(true);

        Assertions.assertTrue(sectorUserAccountAuthorizationService.isAuthorized(user, accountId, permission));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        Long accountId = 1L;
        Long sectorId = 1L;
        String permission = "permission1";
        when(targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
        when(sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorId, permission)).thenReturn(false);

        Assertions.assertFalse(sectorUserAccountAuthorizationService.isAuthorized(user, accountId, permission));
    }

    @Test
    void isApplicable_true() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(ResourceType.ACCOUNT, "1"))
                .build();
        Assertions.assertTrue(sectorUserAccountAuthorizationService.isApplicable(authorizationCriteria));
    }

    @Test
    void isApplicable_false() {
        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .build();
        Assertions.assertFalse(sectorUserAccountAuthorizationService.isApplicable(authorizationCriteria));
    }
}