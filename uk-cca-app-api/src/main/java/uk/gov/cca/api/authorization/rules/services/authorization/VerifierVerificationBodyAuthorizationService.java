package uk.gov.cca.api.authorization.rules.services.authorization;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.verificationbody.domain.VerificationBody;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service that checks if a VERIFIER user is authorized on {@link VerificationBody}.
 */
@Service
public class VerifierVerificationBodyAuthorizationService implements VerificationBodyAuthorizationService {

    /**
     * Checks that VERIFIER has access to the provided verification body.
     * @param user the user to authorize
     * @param verificationBodyId the verification body to check permission on
     * @return if the user is authorized on verification body.
     */
    @Override
    public boolean isAuthorized(AppUser user, Long verificationBodyId) {
        return user.getAuthorities()
            .stream()
            .filter(Objects::nonNull)
            .anyMatch(auth -> verificationBodyId.equals(auth.getVerificationBodyId()));
    }

    /**
     * Checks that a VERIFIER has the permissions to verification body.
     * @param user the user to authorize.
     * @param verificationBodyId the verification body to check permission on
     * @param permission to check
     * @return if the user has the permissions on the verification body
     */
    @Override
    public boolean isAuthorized(AppUser user, Long verificationBodyId, String permission) {
        return user.getAuthorities()
            .stream()
            .filter(Objects::nonNull)
            .filter(auth -> verificationBodyId.equals(auth.getVerificationBodyId()))
            .flatMap(authority -> authority.getPermissions().stream()).toList().contains(permission);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }
}
