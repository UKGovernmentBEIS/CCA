package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class SectorAssociationRequestCreateRuleHandlerTest {
	
    @InjectMocks
    private SectorAssociationRequestCreateRuleHandler sectorAssociationRequestCreateRuleHandler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Test
    void evaluateRules() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .handler("sectorAssociationRequestCreateHandler")
                .permission("permission1")
                .build();

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
        		.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()))
                .permission(rule.getPermission())
                .build();

        sectorAssociationRequestCreateRuleHandler.evaluateRules(Set.of(rule), user, String.valueOf(sectorAssociationId));

        verify(appAuthorizationService, times(1))
                .authorize(user, authorizationCriteria);
    }
}