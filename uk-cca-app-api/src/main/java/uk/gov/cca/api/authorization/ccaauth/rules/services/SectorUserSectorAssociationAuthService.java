package uk.gov.cca.api.authorization.ccaauth.rules.services;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.Objects;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
public class SectorUserSectorAssociationAuthService implements UserRoleTypeSectorAssociationAuthService {

    @Override
    public boolean isAuthorized(AppUser user, Long sectorAssociationId) {
        return user.getAuthorities()
            .stream()
            .map(AppCcaAuthority.class::cast)
            .filter(Objects::nonNull)
            .anyMatch(auth -> sectorAssociationId.equals(auth.getSectorAssociationId()));
    }

    @Override
    public boolean isAuthorized(AppUser user, Long sectorAssociationId, String permission) {
        return user.getAuthorities()
            .stream()
            .map(AppCcaAuthority.class::cast)
            .filter(Objects::nonNull)
            .filter(auth -> sectorAssociationId.equals(auth.getSectorAssociationId()))
            .flatMap(authority -> authority.getPermissions().stream()).toList().contains(permission);
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }
}
