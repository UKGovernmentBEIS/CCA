package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityService;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.userinfoapi.AuthenticationStatus;

@Service
@RequiredArgsConstructor
public class CcaOperatorUserRegistrationService {

    private final CcaOperatorUserAuthService ccaOperatorUserAuthService;
    private final CcaOperatorAuthorityService operatorUserAuthorityService;

    /**
     * Registers a new user with status {@link AuthenticationStatus#PENDING} and
     * adds him as {@link uk.gov.cca.api.common.domain.CcaRoleTypeConstants#SECTOR_USER}to the provided Sector Association.
     *
     * @param operatorUserInvitationDTO the {@link CcaOperatorUserInvitationDTO}
     * @param accountId                 the target unit id
     * @param currentUser               the logged-in {@link AppUser}
     */
    @Transactional
    public String registerUserToAccountWithStatusPending(CcaOperatorUserInvitationDTO operatorUserInvitationDTO,
                                                         Long accountId, AppUser currentUser) {
        String roleCode = operatorUserInvitationDTO.getRoleCode();

        String userId =
                ccaOperatorUserAuthService.registerOperatorUser(operatorUserInvitationDTO);

        return operatorUserAuthorityService
                .createPendingAuthorityForOperator(accountId, roleCode,
                        operatorUserInvitationDTO.getContactType(), userId, currentUser);
    }
}
