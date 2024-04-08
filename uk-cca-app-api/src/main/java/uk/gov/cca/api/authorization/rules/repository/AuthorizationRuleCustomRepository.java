package uk.gov.cca.api.authorization.rules.repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AuthorizationRuleCustomRepository {

    @Transactional(readOnly = true)
    Map<String, Set<RoleType>> findResourceSubTypesRoleTypes();
    
    @Transactional(readOnly = true)
    Optional<RoleType> findRoleTypeByResourceTypeAndSubType(String resourceType, String resourceSubType);
    
    @Transactional(readOnly = true)
    Set<String> findResourceSubTypesByResourceTypeAndRoleType(String resourceType, RoleType roleType);
}
