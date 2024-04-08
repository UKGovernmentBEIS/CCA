package uk.gov.cca.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.user.NavigationOutcomes;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.cca.api.user.core.service.UserNotificationService;
import uk.gov.cca.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.netz.api.common.config.AppProperties;
import uk.gov.cca.api.notification.mail.config.property.NotificationProperties;
import uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.domain.EmailData;
import uk.gov.cca.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.cca.api.notification.mail.service.NotificationEmailService;
import uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenActionEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME;
import static uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME;
import static uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_INVITEE_FNAME;
import static uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_INVITEE_LNAME;
import static uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;
import static uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName.INVITEE_INVITATION_ACCEPTED;
import static uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName.INVITER_INVITATION_ACCEPTED;

@Log4j2
@Service
@RequiredArgsConstructor
public class RegulatorUserNotificationGateway {

    private final UserNotificationService userNotificationService;
    private final NotificationEmailService notificationEmailService;
    private final JwtProperties jwtProperties;
    private final NotificationProperties notificationProperties;
    private final AppProperties appProperties;

    public void notifyInvitedUser(RegulatorInvitedUserDetailsDTO invitedUserDetails, String authorityUuid) {
        long expirationInMinutes = jwtProperties.getClaim().getUserInvitationExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
                EmailNotificationTemplateConstants.CCA_HELPDESK, notificationProperties.getEmail().getCcaHelpdesk())
        );

        userNotificationService.notifyUserWithLink(
                UserNotificationWithRedirectionLinkInfo.builder()
                        .templateName(NotificationTemplateName.INVITATION_TO_REGULATOR_ACCOUNT)
                        .userEmail(invitedUserDetails.getEmail())
                        .notificationParams(notificationParams)
                        .linkParamName(EmailNotificationTemplateConstants.REGULATOR_INVITATION_CONFIRMATION_LINK)
                        .linkPath(NavigationOutcomes.REGULATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
                        .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                                .jwtTokenAction(JwtTokenActionEnum.REGULATOR_INVITATION)
                                .claimValue(authorityUuid)
                                .expirationInterval(expirationInMinutes)
                                .build()
                        )
                        .build()
        );
    }

    public void notifyInviteeAcceptedInvitation(final String email) {

        final EmailData inviteeInfo = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(INVITEE_INVITATION_ACCEPTED)
                    .templateParams(Map.of(
                        EmailNotificationTemplateConstants.CCA_HELPDESK, notificationProperties.getEmail().getCcaHelpdesk(),
                        EmailNotificationTemplateConstants.HOME_URL, appProperties.getWeb().getUrl()))
                    .build())
            .build();

        notificationEmailService.notifyRecipient(inviteeInfo, email);
    }

    public void notifyInviterAcceptedInvitation(UserInfoDTO invitee, UserInfoDTO inviter) {

        final EmailData inviteeInfo = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(INVITER_INVITATION_ACCEPTED)
                    .templateParams(Map.of(USER_ACCOUNT_CREATED_USER_FNAME, inviter.getFirstName(),
                            USER_ACCOUNT_CREATED_USER_LNAME, inviter.getLastName(),
                            USER_INVITEE_FNAME, invitee.getFirstName(),
                            USER_INVITEE_LNAME, invitee.getLastName(),
                            EmailNotificationTemplateConstants.CCA_HELPDESK, notificationProperties.getEmail().getCcaHelpdesk()))
                    .build())
            .build();

        notificationEmailService.notifyRecipient(inviteeInfo, inviter.getEmail());
    }

    public void sendUpdateNotifications(final List<String> activatedRegulators) {

        // send notifications for accounts that have been activated
        activatedRegulators
            .forEach(userId -> {
                try {
                    userNotificationService.notifyUserAccountActivation(userId, "Regulator");
                } catch (Exception ex) {
                    log.error("Exception during sending email for regulator activation:", ex);
                }
            });
    }
}
