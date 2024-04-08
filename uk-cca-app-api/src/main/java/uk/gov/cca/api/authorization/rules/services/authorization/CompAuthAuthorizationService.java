package uk.gov.cca.api.authorization.rules.services.authorization;

import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

public interface CompAuthAuthorizationService {
    boolean isAuthorized(AppUser user, CompetentAuthorityEnum competentAuthority);

    boolean isAuthorized(AppUser user, CompetentAuthorityEnum competentAuthority, String permission);

    RoleType getRoleType();
}
