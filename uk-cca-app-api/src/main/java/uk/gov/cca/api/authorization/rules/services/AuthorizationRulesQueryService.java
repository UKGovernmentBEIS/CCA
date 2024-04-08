package uk.gov.cca.api.authorization.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.rules.repository.AuthorizationRuleRepository;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorizationRulesQueryService {

    private final AuthorizationRuleRepository authorizationRuleRepository;
    
    public Optional<RoleType> findRoleTypeByResourceTypeAndSubType(String resourceType, String resourceSubType) {
        return authorizationRuleRepository
                .findRoleTypeByResourceTypeAndSubType(resourceType, resourceSubType);
    }
    
    public Map<String, Set<RoleType>> findResourceSubTypesRoleTypes() {
        return authorizationRuleRepository.findResourceSubTypesRoleTypes();
    }
    
    public Set<String> findResourceSubTypesByResourceTypeAndRoleType(String resourceType, RoleType roleType) {
        return authorizationRuleRepository.findResourceSubTypesByResourceTypeAndRoleType(resourceType, roleType);
    }
}
