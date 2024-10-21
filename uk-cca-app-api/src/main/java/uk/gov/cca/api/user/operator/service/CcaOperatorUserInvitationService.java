package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.cca.api.user.operator.transform.OperatorUserInvitationMapper;
import uk.gov.netz.api.account.service.validator.AccountStatus;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.netz.api.user.operator.service.OperatorUserNotificationGateway;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CcaOperatorUserInvitationService {

    private final UserAuthService authUserService;
    private final CcaOperatorUserRegistrationService operatorUserRegistrationService;
    private final CcaExistingOperatorUserInvitationService existingOperatorUserInvitationService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;
    private final OperatorUserInvitationMapper operatorUserInvitationMapper;

    /**
     * Invites a new user to join a sector with a specified role.
     *
     * @param accountId     the target unit id
     * @param userRegistrationDTO the {@link CcaOperatorUserInvitationDTO}
     * @param currentUser             the current logged-in {@link AppUser}
     */
    @Transactional
    @AccountStatus(expression = "{#status == 'NEW' || #status == 'LIVE'}")
    public void inviteUserToTargetUnit(Long accountId, String targetUnitName, CcaOperatorUserInvitationDTO userRegistrationDTO, AppUser currentUser) {

        Optional<UserInfoDTO> registeredEmail = authUserService.getUserByEmail(userRegistrationDTO.getEmail());

        String authorityUuid = registeredEmail
                .map(userRepresentation -> addExistingUserToTargetUnit(userRepresentation, userRegistrationDTO, accountId, currentUser))
                .orElseGet(() -> operatorUserRegistrationService.registerUserToAccountWithStatusPending(userRegistrationDTO, accountId, currentUser));

        final OperatorUserInvitationDTO userInvitationDTO = operatorUserInvitationMapper.toUserInvitationDTO(userRegistrationDTO);

        operatorUserNotificationGateway.notifyInvitedUser(userInvitationDTO, targetUnitName, authorityUuid);
    }

    private String addExistingUserToTargetUnit(UserInfoDTO userDTO, CcaOperatorUserInvitationDTO userRegistrationDTO,
                                               Long accountId, AppUser currentUser) {
        return existingOperatorUserInvitationService.addExistingUserToTargetUnit(userRegistrationDTO, accountId, userDTO.getUserId(), currentUser);
    }
}
