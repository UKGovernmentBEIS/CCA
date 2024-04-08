package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.cca.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperatorRoleCodeAcceptInvitationServiceDelegator {

    private final List<OperatorRoleCodeAcceptInvitationService> operatorRoleCodeAcceptInvitationServices;

    @Transactional
    public UserInvitationStatus acceptInvitation(OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation, String roleCode) {
        return getOperatorUserAcceptInvitationService(roleCode)
            .map(service -> service.acceptInvitation(operatorUserAcceptInvitation))
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTHORITY_USER_IS_NOT_OPERATOR));
    }

    private Optional<OperatorRoleCodeAcceptInvitationService> getOperatorUserAcceptInvitationService(String roleCode) {
        return operatorRoleCodeAcceptInvitationServices.stream()
            .filter(service -> service.getRoleCodes().contains(roleCode))
            .findAny();
    }
}
