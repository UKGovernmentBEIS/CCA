package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.validator.AccountStatus;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.user.operator.domain.OperatorUserInvitationDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperatorUserInvitationService {

    private final UserAuthService authUserService;
    private final OperatorUserRegistrationService operatorUserRegistrationService;
    private final ExistingOperatorUserInvitationService existingOperatorUserInvitationService;

    /**
     * Invites a new user to join an account with a specified role.
     * @param accountId the account id
     * @param userRegistrationDTO the {@link OperatorUserInvitationDTO}
     * @param currentUser the current logged-in {@link AppUser}
     */
    @Transactional
    @AccountStatus(expression = "{#status != 'AWAITING_APPROVAL' && #status != 'DENIED'}")
    public void inviteUserToAccount(Long accountId, OperatorUserInvitationDTO userRegistrationDTO, AppUser currentUser) {
        Optional<UserInfoDTO> userOptional = authUserService.getUserByEmail(userRegistrationDTO.getEmail());

        userOptional.ifPresentOrElse(
            userRepresentation -> addExistingUserToAccount(userRepresentation, userRegistrationDTO, accountId, currentUser),
            () -> operatorUserRegistrationService.registerUserToAccountWithStatusPending(userRegistrationDTO, accountId, currentUser));
    }


    private void addExistingUserToAccount(UserInfoDTO userDTO, OperatorUserInvitationDTO userRegistrationDTO,
                                          Long accountId, AppUser currentUser) {
        existingOperatorUserInvitationService.addExistingUserToAccount(userRegistrationDTO, accountId, userDTO.getUserId(), userDTO.getStatus(), currentUser);
    }

}
