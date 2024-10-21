package uk.gov.cca.api.authorization.ccaauth.rules.services;

import uk.gov.netz.api.authorization.core.domain.AppUser;

public interface UserRoleTypeSectorAssociationAuthService {

    boolean isAuthorized(AppUser user, Long sectorAssociationId);

    boolean isAuthorized(AppUser user, Long sectorAssociationId, String permission);

    String getRoleType();
}
