package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.repository.AuthorizationRuleRepository;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.RoleTypeAuthorizationServiceDelegator;

@RequiredArgsConstructor
@Service
public class SectorAssociationRequestAuthorizationResourceService {

	private final AuthorizationRuleRepository authorizationRuleRepository;
    private final RoleTypeAuthorizationServiceDelegator roleTypeAuthorizationServiceDelegator;
    
    public Set<String> findRequestCreateActionsBySectorAssociationId(AppUser user, Long sectorAssociationId) {
        List<AuthorizationRuleScopePermission> rules = 
                authorizationRuleRepository.findRulePermissionsByResourceTypeScopeAndRoleType(
                		CcaResourceType.SECTOR_ASSOCIATION, Scope.REQUEST_CREATE, user.getRoleType());
        
        Set<String> allowedActions = new HashSet<>();
        rules.forEach(rule -> {
            if (roleTypeAuthorizationServiceDelegator.isAuthorized(user,
                    AuthorizationCriteria.builder()
                            .requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()))
                            .permission(rule.getPermission())
                            .build())) {
                allowedActions.add(rule.getResourceSubType());
            }
        });
        
        return allowedActions;
    }
}
