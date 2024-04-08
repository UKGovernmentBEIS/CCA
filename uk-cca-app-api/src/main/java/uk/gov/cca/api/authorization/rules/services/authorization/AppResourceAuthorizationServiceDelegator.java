package uk.gov.cca.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;

import java.util.List;
import java.util.Optional;

/**
 * Service that delegates authorization to {@link ResourceType} based services.
 */
@Service
@RequiredArgsConstructor
public class AppResourceAuthorizationServiceDelegator {

    private final List<AppResourceAuthorizationService> appResourceAuthorizationServices;

    /**
     * Checks that user has authorization to the specified resource type.
     * @param resourceType the {@link ResourceType}
     * @param user the user
     * @param criteria {@link AuthorizationCriteria}
     * @return if the user is authorized on the resource type
     */
    public boolean isAuthorized(String resourceType, AppUser user, AuthorizationCriteria criteria) {
        return getResourceService(resourceType)
            .map(resourceAuthorizationService -> resourceAuthorizationService.isAuthorized(user, criteria))
            .orElse(false);
    }

    private Optional<AppResourceAuthorizationService> getResourceService(String resourceType) {
        return appResourceAuthorizationServices.stream()
            .filter(resourceAuthorizationService -> resourceAuthorizationService.getResourceType().equals(resourceType))
            .findAny();
    }
}
