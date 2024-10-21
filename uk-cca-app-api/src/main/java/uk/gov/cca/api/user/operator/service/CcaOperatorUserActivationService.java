package uk.gov.cca.api.user.operator.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.user.operator.service.OperatorUserRegisterValidationService;
import uk.gov.netz.api.user.operator.service.OperatorUserRegisteredAcceptInvitationService;
import uk.gov.netz.api.user.operator.service.OperatorUserTokenVerificationService;

@Service
@RequiredArgsConstructor
public class CcaOperatorUserActivationService {

    private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    private final CcaOperatorUserAuthService ccaOperatorUserAuthService;
    private final OperatorUserRegisterValidationService operatorUserRegisterValidationService;
    private final OperatorUserRegisteredAcceptInvitationService operatorUserRegisteredAcceptInvitationService;

    public CcaOperatorUserDTO acceptAuthorityAndEnableInvitedUserWithCredentials
            (CcaOperatorUserRegistrationWithCredentialsDTO ccaOperatorUserRegistrationWithCredentialsDTO) {

        AuthorityInfoDTO authorityInfo = operatorUserTokenVerificationService
                .verifyInvitationTokenForPendingAuthority(ccaOperatorUserRegistrationWithCredentialsDTO.getEmailToken());
        operatorUserRegisterValidationService.validateRegisterForAccount(authorityInfo.getUserId(), authorityInfo.getAccountId());

        CcaOperatorUserDTO operatorUserDTO = ccaOperatorUserAuthService
                .enableAndUpdateUserAndSetPassword(ccaOperatorUserRegistrationWithCredentialsDTO, authorityInfo.getUserId());

        operatorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(authorityInfo.getId());

        return operatorUserDTO;
    }

    public void acceptAuthorityAndSetCredentialsToUser(InvitedUserCredentialsDTO invitedUserCredentialsDTO) {
        AuthorityInfoDTO authorityInfo = operatorUserTokenVerificationService
                .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
        operatorUserRegisterValidationService.validateRegisterForAccount(authorityInfo.getUserId(), authorityInfo.getAccountId());
        ccaOperatorUserAuthService.setUserPassword(authorityInfo.getUserId(), invitedUserCredentialsDTO.getPassword());
        operatorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(authorityInfo.getId());
    }
}
