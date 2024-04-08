package uk.gov.cca.api.authorization.rules.services.authorization;

import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.netz.api.common.domain.RoleType;

public interface VerificationBodyAuthorizationService {
    boolean isAuthorized(AppUser user, Long verificationBodyId);

    boolean isAuthorized(AppUser user, Long verificationBodyId, String permission);

    RoleType getRoleType();
}
