package uk.gov.cca.api.authorization.ccaauth.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectorAssociationAuthorizationServiceDelegator {

    private final List<UserRoleTypeSectorAssociationAuthService> sectorAssociationAuthorizationServices;

    public boolean isAuthorized(AppUser user, Long sectorAssociationId) {
        return getUserService(user)
            .map(authorizationService -> authorizationService.isAuthorized(user, sectorAssociationId))
            .orElse(false);
    }

    public boolean isAuthorized(AppUser user, Long sectorAssociationId, String permission) {
        return getUserService(user)
            .map(authorizationService -> authorizationService.isAuthorized(user, sectorAssociationId, permission))
            .orElse(false);
    }

    private Optional<UserRoleTypeSectorAssociationAuthService> getUserService(AppUser user) {
        return sectorAssociationAuthorizationServices.stream()
            .filter(authorizationService -> authorizationService.getRoleType().equals(user.getRoleType()))
            .findAny();
    }
}
