package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.services.SectorAssociationAuthorizationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;

import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class SectorAssociationAccessRuleHandlerTest {

    @InjectMocks
    private SectorAssociationAccessRuleHandler sectorAssociationAccessRuleHandler;

    @Mock
    private SectorAssociationAuthorizationService sectorAssociationAuthorizationService;

    @Test
    void evaluateRules() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
            .handler("sectorAssociationRuleHandler")
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .build();

        sectorAssociationAccessRuleHandler.evaluateRules(Set.of(rule), user, String.valueOf(sectorAssociationId));

        verify(sectorAssociationAuthorizationService, times(1))
            .authorize(user, sectorAssociationId, rule.getPermission());
    }
}