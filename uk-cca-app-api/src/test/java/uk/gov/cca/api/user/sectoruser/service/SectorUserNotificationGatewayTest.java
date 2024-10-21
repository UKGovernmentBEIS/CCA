package uk.gov.cca.api.user.sectoruser.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.constants.CcaNotificationTemplateName;
import uk.gov.cca.api.token.CcaJwtTokenAction;
import uk.gov.cca.api.user.CcaNavigationOutcomes;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.netz.api.authorization.core.service.RoleService;
import uk.gov.netz.api.notification.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.netz.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.netz.api.user.core.service.UserNotificationService;


@ExtendWith(MockitoExtension.class)
class SectorUserNotificationGatewayTest {

    @InjectMocks
    private SectorUserNotificationGateway sectorUserNotificationGateway;

    @Mock
    private RoleService roleService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NotificationProperties notificationProperties;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private JwtProperties jwtProperties;

    @Test
    void notifyInvitedUser() {
        String receiverEmail = "receiverEmail";
        String roleCode = "roleCode";
        String sectorAssociationName = "sectorAssociationName";
        String authorityUuid = "authorityUuid";
        String roleName = "roleName";
        String helpdesk = "helpdesk";
        RoleDTO roleDTO = RoleDTO.builder().code(roleCode).name(roleName).build();

        final SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(receiverEmail, roleCode);

        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 60L;

        when(roleService.getRoleByCode(roleCode)).thenReturn(roleDTO);
        when(jwtProperties.getClaim()).thenReturn(claim);
        when(notificationProperties.getEmail().getContactUsLink()).thenReturn(helpdesk);

        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        sectorUserNotificationGateway.notifyInvitedUser(sectorUserInvitationDTO, sectorAssociationName, authorityUuid);

        verify(roleService , times(1)).getRoleByCode(roleCode);

        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
                ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUserWithLink(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(CcaNotificationTemplateName.INVITATION_TO_SECTOR_USER);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(sectorUserInvitationDTO.getEmail());
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(CcaEmailNotificationTemplateConstants.SECTOR_USER_INVITATION_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(CcaNavigationOutcomes.SECTOR_USER_REGISTRATION_INVITATION_ACCEPTED_URL);
        assertThat(notificationInfo.getNotificationParams()).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        USER_ROLE_TYPE, roleDTO.getName(),
                        CcaEmailNotificationTemplateConstants.SECTOR_ASSOCIATION_NAME, sectorAssociationName,
                        EmailNotificationTemplateConstants.EXPIRATION_MINUTES, 60L,
                        EmailNotificationTemplateConstants.CONTACT_REGULATOR, helpdesk
                ));
        assertThat(notificationInfo.getTokenParams())
                .isEqualTo(expectedInvitationLinkTokenParams(CcaJwtTokenAction.SECTOR_USER_INVITATION, authorityUuid, expirationInterval));

    }

    private SectorUserInvitationDTO createSectorUserInvitationDTO(String email, String roleCode) {
        return SectorUserInvitationDTO.builder()
                .email(email)
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .roleCode(roleCode)
                .build();
    }

    private UserNotificationWithRedirectionLinkInfo.TokenParams expectedInvitationLinkTokenParams(JwtTokenAction jwtTokenAction,
                                                                                                  String claimValue, long expirationInterval) {
        return UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                .jwtTokenAction(jwtTokenAction)
                .claimValue(claimValue)
                .expirationInterval(expirationInterval)
                .build();
    }
}
