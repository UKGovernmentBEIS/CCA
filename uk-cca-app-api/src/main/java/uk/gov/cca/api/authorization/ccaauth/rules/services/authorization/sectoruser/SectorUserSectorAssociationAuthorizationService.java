package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.sectoruser;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.Objects;

@Service
@Order(100)
public class SectorUserSectorAssociationAuthorizationService implements SectorUserResourceTypeAuthorizationService {

    @Override
    public boolean isAuthorized(AppUser user, AuthorizationCriteria criteria) {
        long sectorId = Long.parseLong(criteria.getRequestResources().get(CcaResourceType.SECTOR_ASSOCIATION));
        if (criteria.getPermission() == null) {
            return isAuthorized(user, sectorId);
        } else {
            return isAuthorized(user, sectorId, criteria.getPermission());
        }
    }

    @Override
    public boolean isApplicable(AuthorizationCriteria criteria) {
        return criteria.getRequestResources().containsKey(CcaResourceType.SECTOR_ASSOCIATION);
    }

    public boolean isAuthorized(AppUser user, Long sectorAssociationId) {
        return user.getAuthorities()
            .stream()
            .map(AppCcaAuthority.class::cast)
            .filter(Objects::nonNull)
            .anyMatch(auth -> sectorAssociationId.equals(auth.getSectorAssociationId()));
    }

    public boolean isAuthorized(AppUser user, Long sectorAssociationId, String permission) {
        return user.getAuthorities()
            .stream()
            .map(AppCcaAuthority.class::cast)
            .filter(Objects::nonNull)
            .filter(auth -> sectorAssociationId.equals(auth.getSectorAssociationId()))
            .flatMap(authority -> authority.getPermissions().stream()).toList().contains(permission);
    }
}
