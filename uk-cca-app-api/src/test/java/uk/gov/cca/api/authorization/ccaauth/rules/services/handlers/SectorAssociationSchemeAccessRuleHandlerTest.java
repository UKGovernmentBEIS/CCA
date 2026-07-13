package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SectorAssociationSchemeAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class SectorAssociationSchemeAccessRuleHandlerTest {

    @InjectMocks
    private SectorAssociationSchemeAccessRuleHandler sectorAssociationSchemeAccessHandler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private SectorAssociationSchemeAuthorityInfoProvider sectorAssociationSchemeAuthorityInfoProvider;

    @Test
    void evaluateRules() {
        String sectorAssociationSchemeId = String.valueOf(1L);
        Long sectorAssociationId = 2L;
        AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .handler("sectorAssociationSchemeRuleHandler")
                .permission("permission1")
                .build();

        when(sectorAssociationSchemeAuthorityInfoProvider.getSectorAssociationIdBySchemeId(Long.parseLong(sectorAssociationSchemeId))).thenReturn(sectorAssociationId);

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()))
                .permission(rule.getPermission())
                .build();

        sectorAssociationSchemeAccessHandler.evaluateRules(Set.of(rule), user, sectorAssociationSchemeId);

        verify(appAuthorizationService, times(1)).authorize(user, authorizationCriteria);
        verify(sectorAssociationSchemeAuthorityInfoProvider, times(1)).getSectorAssociationIdBySchemeId(Long.parseLong(sectorAssociationSchemeId));
    }
}
