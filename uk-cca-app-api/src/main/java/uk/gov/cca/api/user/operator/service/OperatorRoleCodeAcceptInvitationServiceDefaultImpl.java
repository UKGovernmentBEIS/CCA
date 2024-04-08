package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.AuthorityConstants;
import uk.gov.cca.api.authorization.core.service.RoleService;
import uk.gov.cca.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.cca.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorRoleCodeAcceptInvitationServiceDefaultImpl implements OperatorRoleCodeAcceptInvitationService {

    private final OperatorAuthorityService operatorAuthorityService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;
    private final RoleService roleService;
    private final OperatorUserAuthService operatorUserAuthService;
    private final UserAuthService userAuthService;

    @Transactional
    public UserInvitationStatus acceptInvitation(OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation) {
        switch (operatorUserAcceptInvitation.getUserAuthenticationStatus()) {
            case DELETED:
                throw new BusinessException(ErrorCode.USER_STATUS_DELETED);
            case PENDING:
                return UserInvitationStatus.PENDING_USER_REGISTRATION;
            case REGISTERED:
                if (operatorUserAuthService.hasOperatorUserPassword(operatorUserAcceptInvitation.getUserId())) {
                    // Update status to Accepted and get inviter
                    String userId = operatorAuthorityService.acceptAuthority(operatorUserAcceptInvitation.getUserAuthorityId())
                            .getCreatedBy();
                    UserInfoDTO inviterUser = userAuthService.getUserByUserId(userId);

                    // Notify invitee and inviter
                    operatorUserNotificationGateway.notifyInviteeAcceptedInvitation(operatorUserAcceptInvitation);
                    operatorUserNotificationGateway.notifyInviterAcceptedInvitation(operatorUserAcceptInvitation, inviterUser);

                    return UserInvitationStatus.ACCEPTED;
                } else {
                    return UserInvitationStatus.PENDING_USER_ENABLE;
                }
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public Set<String> getRoleCodes() {
        return roleService.getCodesByType(RoleType.OPERATOR).stream()
            .filter(roleCode -> !AuthorityConstants.EMITTER_CONTACT.equals(roleCode))
            .collect(Collectors.toSet());
    }

}
