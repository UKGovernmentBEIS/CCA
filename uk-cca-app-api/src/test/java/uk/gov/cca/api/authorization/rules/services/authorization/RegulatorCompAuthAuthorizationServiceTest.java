package uk.gov.cca.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.services.authorization.RegulatorCompAuthAuthorizationService;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegulatorCompAuthAuthorizationServiceTest {
    private final RegulatorCompAuthAuthorizationService regulatorCompAuthAuthorizationService = new RegulatorCompAuthAuthorizationService();

    private final AppAuthority appAuthority = AppAuthority.builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .permissions(List.of(Permission.PERM_TASK_ASSIGNMENT,
                    Permission.PERM_ACCOUNT_USERS_EDIT))
            .build();
    private final AppUser user = AppUser.builder().authorities(List.of(appAuthority)).roleType(RoleType.REGULATOR).build();

    @Test
    void isAuthorized_account_true() {
        assertTrue(regulatorCompAuthAuthorizationService.isAuthorized(user, CompetentAuthorityEnum.ENGLAND));
    }

    @Test
    void isAuthorized_account_false() {
        assertFalse(regulatorCompAuthAuthorizationService.isAuthorized(user, CompetentAuthorityEnum.SCOTLAND));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        assertTrue(regulatorCompAuthAuthorizationService.isAuthorized(user, CompetentAuthorityEnum.ENGLAND,
                Permission.PERM_TASK_ASSIGNMENT));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        assertFalse(regulatorCompAuthAuthorizationService.isAuthorized(user, CompetentAuthorityEnum.ENGLAND,
                Permission.PERM_CA_USERS_EDIT));
    }

    @Test
    void getType() {
        assertEquals(RoleType.REGULATOR, regulatorCompAuthAuthorizationService.getRoleType());
    }
}