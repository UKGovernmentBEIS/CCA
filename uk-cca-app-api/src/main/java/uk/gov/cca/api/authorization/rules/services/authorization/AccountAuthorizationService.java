package uk.gov.cca.api.authorization.rules.services.authorization;

import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.netz.api.common.domain.RoleType;

public interface AccountAuthorizationService {
    boolean isAuthorized(AppUser user, Long accountId);

    boolean isAuthorized(AppUser user, Long accountId, String permission);

    RoleType getRoleType();
}
