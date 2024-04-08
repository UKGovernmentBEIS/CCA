package uk.gov.cca.api.authorization.rules.services.authorization;

import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;

public interface AppResourceAuthorizationService {
    boolean isAuthorized(AppUser user, AuthorizationCriteria criteria);

    String getResourceType();
}
