package uk.gov.cca.api.user.operator.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.user.operator.domain.CcaOperatorInvitedUserInfoDTO;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserAcceptInvitationMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.netz.api.user.operator.domain.OperatorUserDTO;
import uk.gov.netz.api.user.operator.domain.OperatorUserWithAuthorityDTO;
import uk.gov.netz.api.user.operator.service.OperatorRoleCodeAcceptInvitationServiceDelegator;
import uk.gov.netz.api.user.operator.service.OperatorUserAuthService;
import uk.gov.netz.api.user.operator.service.OperatorUserRegisterValidationService;
import uk.gov.netz.api.user.operator.service.OperatorUserTokenVerificationService;
import uk.gov.netz.api.user.operator.transform.OperatorUserAcceptInvitationMapper;


@Service
public class CcaOperatorUserAcceptInvitationService {

    private final OperatorUserAuthService operatorUserAuthService;
    private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    private final CcaOperatorUserAcceptInvitationMapper ccaOperatorUserAcceptInvitationMapper;
    private final OperatorRoleCodeAcceptInvitationServiceDelegator operatorRoleCodeAcceptInvitationServiceDelegator;
    private final CcaAuthorityDetailsRepository ccaAuthorityDetailsRepository;
    private final OperatorUserAcceptInvitationMapper operatorUserAcceptInvitationMapper;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final OperatorUserRegisterValidationService operatorUserRegisterValidationService;

    public CcaOperatorUserAcceptInvitationService(OperatorUserAuthService operatorUserAuthService,
                                                  OperatorUserTokenVerificationService operatorUserTokenVerificationService,
                                                  CcaOperatorUserAcceptInvitationMapper ccaOperatorUserAcceptInvitationMapper,
                                                  OperatorRoleCodeAcceptInvitationServiceDelegator operatorRoleCodeAcceptInvitationServiceDelegator,
                                                  CcaAuthorityDetailsRepository ccaAuthorityDetailsRepository,
                                                  OperatorUserAcceptInvitationMapper operatorUserAcceptInvitationMapper,
                                                  TargetUnitAccountQueryService targetUnitAccountQueryService,
                                                  OperatorUserRegisterValidationService operatorUserRegisterValidationService) {
        this.operatorUserAuthService = operatorUserAuthService;
        this.operatorUserTokenVerificationService = operatorUserTokenVerificationService;
        this.ccaOperatorUserAcceptInvitationMapper = ccaOperatorUserAcceptInvitationMapper;
        this.operatorRoleCodeAcceptInvitationServiceDelegator = operatorRoleCodeAcceptInvitationServiceDelegator;
        this.ccaAuthorityDetailsRepository = ccaAuthorityDetailsRepository;
        this.operatorUserAcceptInvitationMapper = operatorUserAcceptInvitationMapper;
        this.targetUnitAccountQueryService = targetUnitAccountQueryService;
        this.operatorUserRegisterValidationService = operatorUserRegisterValidationService;
    }

    @Transactional
    public CcaOperatorInvitedUserInfoDTO acceptInvitation(String invitationToken, AppUser appUser) {
        AuthorityInfoDTO authority = this.operatorUserTokenVerificationService.verifyInvitationToken(invitationToken, appUser);

        final Long accountId = authority.getAccountId();

        operatorUserRegisterValidationService.validateRegisterForAccount(authority.getUserId(), accountId);

        CcaAuthorityDetails ccaAuthorityDetails = ccaAuthorityDetailsRepository.findCcaAuthorityDetailsByAuthorityId(authority.getId());
        OperatorUserDTO userDTO = this.operatorUserAuthService.getUserById(authority.getUserId());

        final String accountName = targetUnitAccountQueryService.getAccountBusinessIdAndName(accountId);

        final OperatorUserWithAuthorityDTO operatorUserWithAuthorityDTO = operatorUserAcceptInvitationMapper
                .toOperatorUserWithAuthorityDTO(userDTO, authority, accountName);

        UserInvitationStatus invitationStatus = this.operatorRoleCodeAcceptInvitationServiceDelegator
                .acceptInvitation(operatorUserWithAuthorityDTO, authority.getCode());

        return this.ccaOperatorUserAcceptInvitationMapper.toOperatorInvitedUserInfoDTO(
                operatorUserWithAuthorityDTO,
                authority.getCode(),
                invitationStatus,
                ccaAuthorityDetails.getContactType());
    }
}
