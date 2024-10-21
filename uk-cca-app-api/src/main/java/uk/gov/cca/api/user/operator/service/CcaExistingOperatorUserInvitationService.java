package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityService;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.cca.api.user.operator.transform.OperatorUserInvitationMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.netz.api.user.operator.service.OperatorUserAuthService;

@Log4j2
@Service
@RequiredArgsConstructor
public class CcaExistingOperatorUserInvitationService {

    private final CcaOperatorAuthorityService operatorUserAuthorityService;
    private final OperatorUserAuthService operatorUserAuthService;
    private final OperatorAuthorityQueryService operatorAuthorityQueryService;
    private final OperatorUserInvitationMapper operatorUserInvitationMapper;

    @Transactional
    public String addExistingUserToTargetUnit(CcaOperatorUserInvitationDTO operatorUserInvitationDTO,
                                              Long accountId, String userId, AppUser currentUser) {

        log.debug("Adding existing operator user '{}' to target unit '{}'", () -> userId, () -> accountId);

        checkInvitedUserStatusAndRole(userId, operatorUserInvitationDTO);

        return operatorUserAuthorityService.createPendingAuthorityForOperator(
        		accountId, operatorUserInvitationDTO.getRoleCode(), operatorUserInvitationDTO.getContactType(), userId, currentUser);
    }

    private void checkInvitedUserStatusAndRole(String userId, CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO) {
        if (operatorAuthorityQueryService.existsAuthorityNotForAccount(userId)) {
            log.error("User '{}' already exists in CCA", () -> userId);
            throw new BusinessException(ErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_THAN_OPERATOR);
        }

        final OperatorUserInvitationDTO operatorUserInvitationDTO = operatorUserInvitationMapper.toUserInvitationDTO(ccaOperatorUserInvitationDTO);
        operatorUserAuthService.updateUser(operatorUserInvitationDTO);
    }
}
