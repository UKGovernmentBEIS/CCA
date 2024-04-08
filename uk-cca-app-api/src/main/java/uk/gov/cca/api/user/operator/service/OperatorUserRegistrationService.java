package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.AccountQueryService;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.cca.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.cca.api.user.operator.domain.OperatorUserDTO;
import uk.gov.cca.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.cca.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.common.domain.RoleType;

@Service
@RequiredArgsConstructor
public class OperatorUserRegistrationService {

    private final OperatorUserAuthService operatorUserAuthService;
    private final OperatorAuthorityService operatorAuthorityService;
    private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    private final AccountQueryService accountQueryService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;

    /**
     * Registers a new user with status {@link AuthenticationStatus#PENDING} and
     * adds him as {@link RoleType#OPERATOR}to the provided account.
     * @param operatorUserInvitationDTO the {@link OperatorUserInvitationDTO}
     * @param accountId the account id
     * @param currentUser the logged-in {@link AppUser}
     */
    @Transactional
    public void registerUserToAccountWithStatusPending(OperatorUserInvitationDTO operatorUserInvitationDTO,
                                                       Long accountId, AppUser currentUser) {
        String roleCode = operatorUserInvitationDTO.getRoleCode();

        String userId =
                operatorUserAuthService.registerOperatorUserAsPending(
                        operatorUserInvitationDTO.getEmail(),
                        operatorUserInvitationDTO.getFirstName(),
                        operatorUserInvitationDTO.getLastName());
        String authorityUuid =
                operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser);

        String accountName = accountQueryService.getAccountName(accountId);

        operatorUserNotificationGateway.notifyInvitedUser(
                operatorUserInvitationDTO,
                accountName,
                authorityUuid);
    }

    /**
     * Registers an operator user in Keycloak.
     * @param operatorUserRegistrationWithCredentialsDTO {@link OperatorUserRegistrationWithCredentialsDTO} user's under registration
     * @return {@link OperatorUserDTO}
     */
    public OperatorUserDTO registerUser(OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO) {
        final String email = operatorUserTokenVerificationService
                .verifyRegistrationToken(operatorUserRegistrationWithCredentialsDTO.getEmailToken());

        OperatorUserDTO operatorUserDTO = operatorUserAuthService
                .registerOperatorUser(operatorUserRegistrationWithCredentialsDTO, email);

        operatorUserNotificationGateway.notifyRegisteredUser(operatorUserDTO);

        return operatorUserDTO;
    }

    public void sendVerificationEmail(String email) {
        operatorUserNotificationGateway.notifyEmailVerification(email);
    }
}
