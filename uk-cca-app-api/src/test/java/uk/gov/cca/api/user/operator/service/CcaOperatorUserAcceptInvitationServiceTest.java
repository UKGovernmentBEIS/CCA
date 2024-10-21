package uk.gov.cca.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.user.operator.domain.CcaOperatorInvitedUserInfoDTO;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserAcceptInvitationMapper;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.netz.api.user.operator.domain.OperatorUserDTO;
import uk.gov.netz.api.user.operator.domain.OperatorUserWithAuthorityDTO;
import uk.gov.netz.api.user.operator.service.OperatorRoleCodeAcceptInvitationServiceDelegator;
import uk.gov.netz.api.user.operator.service.OperatorUserAuthService;
import uk.gov.netz.api.user.operator.service.OperatorUserRegisterValidationService;
import uk.gov.netz.api.user.operator.service.OperatorUserTokenVerificationService;
import uk.gov.netz.api.user.operator.transform.OperatorUserAcceptInvitationMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaOperatorUserAcceptInvitationServiceTest {

    @InjectMocks
    private CcaOperatorUserAcceptInvitationService service;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;
    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    @Mock
    private CcaOperatorUserAcceptInvitationMapper ccaOperatorUserAcceptInvitationMapper;
    @Mock
    private OperatorRoleCodeAcceptInvitationServiceDelegator operatorRoleCodeAcceptInvitationServiceDelegator;
    @Mock
    private CcaAuthorityDetailsRepository ccaAuthorityDetailsRepository;

    @Mock
    private OperatorUserAcceptInvitationMapper operatorUserAcceptInvitationMapper;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private OperatorUserRegisterValidationService operatorUserRegisterValidationService;

    @Test
    void acceptInvitation() {
        String invitationToken = "token";
        String userId = "userId";
        Long accountId = 1L;
        String authorityRoleCode = "roleCode";
        String accountInstallationName = "ADS_48-T00005-AccountName";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder().userId(userId).accountId(accountId).code(authorityRoleCode).build();
        OperatorUserDTO operatorUser = OperatorUserDTO.builder().build();
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation = OperatorUserWithAuthorityDTO.builder().build();
        UserInvitationStatus userInvitationStatus = UserInvitationStatus.ACCEPTED;
        CcaAuthorityDetails authorityDetails = CcaAuthorityDetails.builder()
                .id(1L)
                .organisationName("test_organisation")
                .contactType(ContactType.OPERATOR)
                .build();
        CcaOperatorInvitedUserInfoDTO operatorInvitedUserInfoDTO = CcaOperatorInvitedUserInfoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .roleCode("code")
                .accountName(accountInstallationName)
                .invitationStatus(UserInvitationStatus.ACCEPTED)
                .contactType(ContactType.OPERATOR.getName())
                .build();

        when(operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(operatorUserAuthService.getUserById(authorityInfo.getUserId())).thenReturn(operatorUser);
        when(targetUnitAccountQueryService.getAccountName(authorityInfo.getAccountId())).thenReturn(accountInstallationName);
        when(operatorUserAcceptInvitationMapper.toOperatorUserWithAuthorityDTO(operatorUser, authorityInfo, accountInstallationName))
                .thenReturn(operatorUserAcceptInvitation);
        when(ccaOperatorUserAcceptInvitationMapper.toOperatorInvitedUserInfoDTO(operatorUserAcceptInvitation, authorityRoleCode, userInvitationStatus, ContactType.OPERATOR))
                .thenReturn(operatorInvitedUserInfoDTO);
        when(operatorRoleCodeAcceptInvitationServiceDelegator.acceptInvitation(operatorUserAcceptInvitation, authorityInfo.getCode()))
                .thenReturn(userInvitationStatus);
        when(ccaAuthorityDetailsRepository.findCcaAuthorityDetailsByAuthorityId(authorityInfo.getId()))
                .thenReturn(authorityDetails);

        service.acceptInvitation(invitationToken);

        verify(operatorUserTokenVerificationService, times(1))
                .verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(operatorUserAuthService, times(1)).getUserById(userId);
        verify(targetUnitAccountQueryService, times(1)).getAccountName(accountId);
        verify(operatorUserAcceptInvitationMapper, times(1)).
                toOperatorUserWithAuthorityDTO(operatorUser, authorityInfo, accountInstallationName);
        verify(operatorRoleCodeAcceptInvitationServiceDelegator, times(1))
                .acceptInvitation(operatorUserAcceptInvitation, authorityRoleCode);
        verify(ccaOperatorUserAcceptInvitationMapper, times(1))
                .toOperatorInvitedUserInfoDTO(operatorUserAcceptInvitation, authorityRoleCode, userInvitationStatus, ContactType.OPERATOR);
        verify(ccaAuthorityDetailsRepository, times(1))
                .findCcaAuthorityDetailsByAuthorityId(authorityInfo.getId());
        verify(operatorUserRegisterValidationService, times(1))
                .validateRegisterForAccount(authorityInfo.getUserId(), authorityInfo.getAccountId());
    }
}
