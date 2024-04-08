package uk.gov.cca.api.user.core.service.auth;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.cca.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final AuthService authService;

    public String registerInvitedUser(UserRepresentation userRepresentation) {
        return authService.getByUsername(userRepresentation.getEmail())
            .map(existingUserRepresentation -> handleExistingUserInvitation(existingUserRepresentation, userRepresentation))
            .orElseGet(() -> authService.registerUserWithStatusPending(userRepresentation));
    }

    private String handleExistingUserInvitation(UserRepresentation existingUserRepresentation,
                                                UserRepresentation newUserRepresentation) {
        AuthenticationStatus userStatus = AuthenticationStatus
            .valueOf(existingUserRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0));
        switch (userStatus) {
            case REGISTERED:
                throw new BusinessException(ErrorCode.USER_ALREADY_REGISTERED);
            case PENDING:
            case DELETED:
                authService.updateUserAndSetStatusAsPending(existingUserRepresentation.getId(), newUserRepresentation);
                break;
            default:
                throw new UnsupportedOperationException(String.format("Status %s of existing user %s is not supported",
                    userStatus, existingUserRepresentation.getId()));
        }
        return existingUserRepresentation.getId();
    }
}
