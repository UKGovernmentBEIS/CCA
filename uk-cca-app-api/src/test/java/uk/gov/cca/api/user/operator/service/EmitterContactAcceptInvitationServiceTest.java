package uk.gov.cca.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.AuthorityConstants;
import uk.gov.cca.api.authorization.core.domain.Authority;
import uk.gov.cca.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.cca.api.user.operator.service.EmitterContactAcceptInvitationService;
import uk.gov.cca.api.user.operator.service.OperatorUserNotificationGateway;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.cca.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.user.operator.domain.OperatorUserAcceptInvitationDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmitterContactAcceptInvitationServiceTest {

    @InjectMocks
    private EmitterContactAcceptInvitationService emitterContactAcceptInvitationService;

    @Mock
    private OperatorAuthorityService operatorAuthorityService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Mock
    private UserAuthService userAuthService;

    @Test
    void acceptInvitation_user_authentication_status_deleted() {
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder()
            .userAuthenticationStatus(AuthenticationStatus.DELETED)
            .build();

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> emitterContactAcceptInvitationService.acceptInvitation(operatorUserAcceptInvitation));

        assertEquals(ErrorCode.USER_STATUS_DELETED, businessException.getErrorCode());

        verifyNoInteractions(operatorAuthorityService, operatorUserNotificationGateway, userAuthService);
    }

    @Test
    void acceptInvitation_user_authentication_status_pending() {
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder()
            .userAuthenticationStatus(AuthenticationStatus.PENDING)
            .build();

        UserInvitationStatus userInvitationStatus =
            emitterContactAcceptInvitationService.acceptInvitation(operatorUserAcceptInvitation);

        assertEquals(UserInvitationStatus.PENDING_USER_REGISTRATION_NO_PASSWORD, userInvitationStatus);

        verifyNoInteractions(operatorAuthorityService, operatorUserNotificationGateway, userAuthService);
    }

    @Test
    void acceptInvitation_user_authentication_status_registered() {
        Long authorityId = 1L;
        String inviterUserId = "inviterUserId";
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder()
            .userAuthenticationStatus(AuthenticationStatus.REGISTERED)
            .userAuthorityId(authorityId)
            .build();
        Authority authority = Authority.builder().createdBy(inviterUserId).build();
        UserInfoDTO inviterUser = UserInfoDTO.builder().build();

        when(operatorAuthorityService.acceptAuthority(authorityId)).thenReturn(authority);
        when(userAuthService.getUserByUserId(inviterUserId)).thenReturn(inviterUser);

        UserInvitationStatus userInvitationStatus = emitterContactAcceptInvitationService
                .acceptInvitation(operatorUserAcceptInvitation);

        assertEquals(UserInvitationStatus.ACCEPTED, userInvitationStatus);

        verify(operatorAuthorityService, times(1))
                .acceptAuthority(authorityId);
        verify(userAuthService, times(1))
                .getUserByUserId(inviterUserId);
        verify(operatorUserNotificationGateway, times(1))
            .notifyInviteeAcceptedInvitation(operatorUserAcceptInvitation);
        verify(operatorUserNotificationGateway, times(1))
                .notifyInviterAcceptedInvitation(operatorUserAcceptInvitation, inviterUser);
    }

    @Test
    void getRoleCodes() {
        assertThat(emitterContactAcceptInvitationService.getRoleCodes()).containsOnly(AuthorityConstants.EMITTER_CONTACT);
    }
}