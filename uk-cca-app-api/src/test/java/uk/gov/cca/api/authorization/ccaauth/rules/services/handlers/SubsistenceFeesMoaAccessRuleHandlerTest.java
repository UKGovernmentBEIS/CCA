package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesMoaAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaAccessRuleHandlerTest {

	@InjectMocks
    private SubsistenceFeesMoaAccessRuleHandler handler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private SubsistenceFeesMoaAuthorityInfoProvider authorityInfoProvider;

    @Test
    void evaluateRules() {
        String resourceId = "1000";
        Pair<String, Long> moaResourceIdPair = Pair.of("SECTOR_MOA", 100L);
        AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        AuthorizationRuleScopePermission rule = AuthorizationRuleScopePermission.builder()
                .handler("subsistenceFeesMoaAccessHandler")
                .build();

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, "100"))
                .permission(rule.getPermission())
                .build();

        when(authorityInfoProvider.getSubsistenceFeesMoaResourceIdById(Long.parseLong(resourceId))).thenReturn(moaResourceIdPair);

        // invoke
        handler.evaluateRules(Set.of(rule), user, resourceId);

        // verify
        verify(appAuthorizationService, times(1)).authorize(user, authorizationCriteria);
        verify(authorityInfoProvider, times(1)).getSubsistenceFeesMoaResourceIdById(Long.parseLong(resourceId));
    }
}
