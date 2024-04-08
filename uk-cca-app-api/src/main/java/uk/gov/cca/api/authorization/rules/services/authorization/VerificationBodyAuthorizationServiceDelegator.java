package uk.gov.cca.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.List;
import java.util.Optional;

/**
 * Service that delegates verification body related authorization to {@link RoleType} based services.
 */
@Service
@RequiredArgsConstructor
public class VerificationBodyAuthorizationServiceDelegator {

    private final List<VerificationBodyAuthorizationService> verificationBodyAuthorizationServices;

    /**
     * Checks that user has access to verification body.
     * @param user the user to authorize
     * @param verificationBodyId the verification body to check permission on
     * @return if the user is authorized on verification body
     */
    public boolean isAuthorized(AppUser user, Long verificationBodyId) {
        return getUserService(user)
            .map(authorizationService -> authorizationService.isAuthorized(user, verificationBodyId))
            .orElse(false);
    }

    /**
     * Checks that user has access to verification body.
     * @param user the user to authorize.
     * @param verificationBodyId the verification body to check permission on
     * @param permission to check
     * @return if the user has the permissions on verification body
     */
    public boolean isAuthorized(AppUser user, Long verificationBodyId, String permission) {
        return getUserService(user)
            .map(authorizationService -> authorizationService.isAuthorized(user, verificationBodyId, permission))
            .orElse(false);
    }

    private Optional<VerificationBodyAuthorizationService> getUserService(AppUser user) {
        return verificationBodyAuthorizationServices.stream()
            .filter(authorizationService -> authorizationService.getRoleType().equals(user.getRoleType()))
            .findAny();
    }


}
