package uk.gov.cca.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.AuthorityStatus;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.cca.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.cca.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.cca.api.user.regulator.service.RegulatorUserInvitationService;
import uk.gov.cca.api.user.regulator.service.RegulatorUserNotificationGateway;
import uk.gov.cca.api.user.regulator.service.RegulatorUserTokenVerificationService;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.cca.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.cca.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.cca.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.cca.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.cca.api.user.regulator.domain.RegulatorUserDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.netz.api.common.exception.ErrorCode.USER_INVALID_STATUS;

@ExtendWith(MockitoExtension.class)
class RegulatorUserInvitationServiceTest {

    @InjectMocks
    private RegulatorUserInvitationService service;

    @Mock
    private RegulatorAuthorityService regulatorAuthorityService;

    @Mock
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;

    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;

    @Mock
    private RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;

    @Test
    void inviteRegulatorUser() {
        String userId = "userId";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        AppUser authUser = AppUser.builder()
            .userId("authUser")
            .roleType(RoleType.REGULATOR)
            .authorities(List.of(AppAuthority.builder().competentAuthority(ca).build()))
            .build();

        String authorityUuid = "uuid";
        RegulatorInvitedUserDTO regulatorInvitedUser = createInvitedUser();
        
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();

        when(regulatorUserAuthService.registerRegulatorInvitedUser(regulatorInvitedUser.getUserDetails(), signature))
            .thenReturn(userId);
        when(regulatorAuthorityService
            .createRegulatorAuthorityPermissions(authUser, userId, ca, List.of(Permission.PERM_CA_USERS_EDIT)))
            .thenReturn(authorityUuid);

        //invoke
        service.inviteRegulatorUser(regulatorInvitedUser,signature,  authUser);

        //verify
        verify(regulatorUserAuthService, times(1))
            .registerRegulatorInvitedUser(regulatorInvitedUser.getUserDetails(), signature);
        verify(regulatorAuthorityService, times(1))
            .createRegulatorAuthorityPermissions(authUser, userId, ca, List.of(Permission.PERM_CA_USERS_EDIT));
        verify(regulatorUserNotificationGateway, times(1))
            .notifyInvitedUser(regulatorInvitedUser.getUserDetails(), authorityUuid);
    }

    @Test
    void acceptInvitation() {
        String invitationToken = "invitationToken";
        String userEmail = "userEmail";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        RegulatorUserDTO regulatorUser = RegulatorUserDTO.builder()
            .email(userEmail)
            .status(AuthenticationStatus.PENDING)
            .build();

        InvitedUserInfoDTO expectedInvitedUserInfo = InvitedUserInfoDTO.builder().email(userEmail).build();

        when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId())).thenReturn(regulatorUser);

        InvitedUserInfoDTO actualInvitedUserInfo = service.acceptInvitation(invitationToken);

        assertEquals(expectedInvitedUserInfo, actualInvitedUserInfo);

        verify(regulatorUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(authorityInfo.getUserId());
    }

    @Test
    void checkRegulatorUserRegistrationEligibility_when_user_authentication_status_not_pending() {
        String invitationToken = "invitationToken";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        RegulatorUserDTO regulatorUser = RegulatorUserDTO.builder().status(AuthenticationStatus.REGISTERED).build();

        when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId())).thenReturn(regulatorUser);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
            service.acceptInvitation(invitationToken));

        assertThat(businessException.getErrorCode()).isEqualTo(USER_INVALID_STATUS);

        verify(regulatorUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(authorityInfo.getUserId());
    }


    private RegulatorInvitedUserDTO createInvitedUser() {
        RegulatorInvitedUserDTO invitedUser =
            RegulatorInvitedUserDTO.builder()
                .userDetails(RegulatorInvitedUserDetailsDTO.builder()
                    .firstName("fn")
                    .lastName("ln")
                    .email("em@em.gr")
                    .jobTitle("title")
                    .phoneNumber("210000")
                    .build())
                .permissions(Map.of(MANAGE_USERS_AND_CONTACTS, EXECUTE))
                .build();
        return invitedUser;
    }

}
