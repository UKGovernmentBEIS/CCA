package uk.gov.cca.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cca.api.user.NavigationParams;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.netz.api.common.config.AppProperties;
import uk.gov.cca.api.notification.mail.config.property.NotificationProperties;
import uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.domain.EmailData;
import uk.gov.cca.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.cca.api.notification.mail.service.NotificationEmailService;
import uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_FNAME;
import static uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_LNAME;
import static uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;
import static uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName.INVITATION_TO_EMITTER_CONTACT;
import static uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName.RESET_2FA_CONFIRMATION;
import static uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName.RESET_PASSWORD_CONFIRMATION;
import static uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName.USER_ACCOUNT_ACTIVATION;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserAuthService userAuthService;
    private final NotificationEmailService notificationEmailService;
    private final AppProperties appProperties;
    private final NotificationProperties notificationProperties;
    private final JwtTokenService jwtTokenService;

    /**
     * Sends email notification containing a redirection link to user.
     * @param notificationInfo {@link UserNotificationWithRedirectionLinkInfo}
     */
    public void notifyUserWithLink(UserNotificationWithRedirectionLinkInfo notificationInfo) {
        String redirectionLink = constructRedirectionLink(notificationInfo.getLinkPath(), notificationInfo.getTokenParams());

        Map<String, Object> notificationParameters = !ObjectUtils.isEmpty(notificationInfo.getNotificationParams()) ?
            notificationInfo.getNotificationParams() :
            new HashMap<>();

        notificationParameters.put(notificationInfo.getLinkParamName(), redirectionLink);

        notifyUser(notificationInfo.getUserEmail(), notificationInfo.getTemplateName(), notificationParameters);
    }

    public void notifyUserAccountActivation(String userId, String roleName) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);
        
        notifyUser(user.getEmail(), USER_ACCOUNT_ACTIVATION, Map.of(USER_ROLE_TYPE, roleName,
                EmailNotificationTemplateConstants.CCA_HELPDESK, notificationProperties.getEmail().getCcaHelpdesk(),
                EmailNotificationTemplateConstants.HOME_URL, appProperties.getWeb().getUrl()));
    }

    public void notifyEmitterContactAccountActivation(String userId, String installationName) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);
        
        notifyUser(user.getEmail(), INVITATION_TO_EMITTER_CONTACT, Map.of(APPLICANT_FNAME, user.getFirstName(),
                APPLICANT_LNAME, user.getLastName(),
                EmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                EmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()));
    }
    
    public void notifyUserPasswordReset(String userId) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);
        
        notifyUser(user.getEmail(), RESET_PASSWORD_CONFIRMATION, Map.of(
                EmailNotificationTemplateConstants.HOME_URL, appProperties.getWeb().getUrl(),
                EmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()));
    }
    
    public void notifyUserReset2Fa(String userId) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);
        
        notifyUser(user.getEmail(), RESET_2FA_CONFIRMATION, Map.of(
                EmailNotificationTemplateConstants.HOME_URL, appProperties.getWeb().getUrl(),
                EmailNotificationTemplateConstants.CCA_HELPDESK, notificationProperties.getEmail().getCcaHelpdesk()));
    }
    
    private void notifyUser(String email, NotificationTemplateName templateName, Map<String, Object> params) {
        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(templateName)
                        .templateParams(params)
                        .build())
                .build();

        notificationEmailService.notifyRecipient(emailInfo, email);
    }

    private String constructRedirectionLink(String path, UserNotificationWithRedirectionLinkInfo.TokenParams tokenParams) {
        String token = jwtTokenService
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval());

        return UriComponentsBuilder
            .fromHttpUrl(appProperties.getWeb().getUrl())
            .path("/")
            .path(path)
            .queryParam(NavigationParams.TOKEN, token)
            .build()
            .toUriString();
    }	
}
