package uk.gov.cca.api.user.sectoruser.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.constants.CcaNotificationTemplateName;
import uk.gov.cca.api.token.CcaJwtTokenAction;
import uk.gov.cca.api.user.CcaNavigationOutcomes;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.netz.api.authorization.core.service.RoleService;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.user.NotificationTemplateConstants;
import uk.gov.netz.api.user.NotificationTemplateName;
import uk.gov.netz.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.netz.api.user.core.service.UserNotificationService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

@Log4j2
@Service
@RequiredArgsConstructor
public class SectorUserNotificationGateway {

    private final RoleService roleService;
    private final UserNotificationService userNotificationService;
    private final NotificationProperties notificationProperties;
    private final JwtProperties jwtProperties;
    private final WebAppProperties webAppProperties;
    private final NotificationEmailService notificationEmailService;

    /**
     * Sends an {@link CcaNotificationTemplateName#} email with receiver email param as recipient.
     *
     * @param sectorUserInvitationDTO the invited sector user to notify
     * @param sectorAssociationName   the account name that will be used to form the email body
     * @param authorityUuid           the uuid that will be used to form the token that will be send with the email body
     */
    public void notifyInvitedUser(SectorUserInvitationDTO sectorUserInvitationDTO, String sectorAssociationName, String authorityUuid) {

        RoleDTO roleDTO = roleService.getRoleByCode(sectorUserInvitationDTO.getRoleCode());
        long expirationInMinutes = jwtProperties.getClaim().getUserInvitationExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
                NotificationTemplateConstants.USER_ROLE_TYPE, roleDTO.getName(),
                CcaEmailNotificationTemplateConstants.SECTOR_ASSOCIATION_NAME, sectorAssociationName,
                NotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes,
                NotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink())
        );

        userNotificationService.notifyUserWithLink(
                UserNotificationWithRedirectionLinkInfo.builder()
                        .templateName(CcaNotificationTemplateName.INVITATION_TO_SECTOR_USER)
                        .userEmail(sectorUserInvitationDTO.getEmail())
                        .notificationParams(notificationParams)
                        .linkParamName(CcaEmailNotificationTemplateConstants.SECTOR_USER_INVITATION_CONFIRMATION_LINK)
                        .linkPath(CcaNavigationOutcomes.SECTOR_USER_REGISTRATION_INVITATION_ACCEPTED_URL)
                        .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                                .jwtTokenAction(CcaJwtTokenAction.SECTOR_USER_INVITATION)
                                .claimValue(authorityUuid)
                                .expirationInterval(expirationInMinutes)
                                .build()
                        )
                        .build()
        );
    }

    public void notifyInviteeAcceptedInvitation(UserInfoDTO invitee) {
        EmailData inviteeInfo = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                .templateName(NotificationTemplateName.INVITEE_INVITATION_ACCEPTED)
                .templateParams(Map.of(NotificationTemplateConstants.USER_ROLE_TYPE, "SectorUser",
                    NotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
                    NotificationTemplateConstants.HOME_URL, webAppProperties.getUrl()))
                .build())
            .build();

        notificationEmailService.notifyRecipient(inviteeInfo, invitee.getEmail());
    }

    public void notifyInviterAcceptedInvitation(UserInfoDTO invitee, UserInfoDTO inviter) {
        EmailData inviteeInfo = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                .templateName(NotificationTemplateName.INVITER_INVITATION_ACCEPTED)
                .templateParams(Map.of(
                    NotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME, inviter.getFirstName(),
                    NotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME, inviter.getLastName(),
                    NotificationTemplateConstants.USER_INVITEE_FNAME, invitee.getFirstName(),
                    NotificationTemplateConstants.USER_INVITEE_LNAME, invitee.getLastName(),
                    NotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()))
                .build())
            .build();

        notificationEmailService.notifyRecipient(inviteeInfo, inviter.getEmail());
    }

    public void notifyUsersUpdateStatus(List<NewUserActivated> activatedSectorUsers) {
        activatedSectorUsers.forEach(user -> {
            try {
                RoleDTO roleDTO = roleService.getRoleByCode(user.getRoleCode());
                userNotificationService.notifyUserAccountActivation(user.getUserId(), roleDTO.getName());
            } catch (Exception ex) {
                log.error("Exception during sending email for update sector user status:", ex);
            }
        });
    }
}
