package uk.gov.cca.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.notification.mail.config.property.NotificationProperties;
import uk.gov.cca.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.cca.api.user.NavigationOutcomes;
import uk.gov.cca.api.user.core.domain.dto.ResetPasswordDTO;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenActionEnum;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserResetPasswordService {

    private final UserNotificationService userNotificationService;
    private final UserAuthService userAuthService;
    private final JwtProperties jwtProperties;
    private final NotificationProperties notificationProperties;
    private final JwtTokenService jwtTokenService;

    public void sendResetPasswordEmail(String email) {
        long expirationInMinutes = jwtProperties.getClaim().getResetPasswordExpIntervalMinutes();
        Optional<UserInfoDTO> user = userAuthService.getUserByEmail(email);

        if (user.isPresent()) {
            Map<String, Object> notificationParams = new HashMap<>(Map.of(
                    EmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
                    EmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes
            ));

            userNotificationService.notifyUserWithLink(
                    UserNotificationWithRedirectionLinkInfo.builder()
                            .templateName(NotificationTemplateName.RESET_PASSWORD_REQUEST)
                            .userEmail(email)
                            .notificationParams(notificationParams)
                            .linkParamName(EmailNotificationTemplateConstants.RESET_PASSWORD_LINK)
                            .linkPath(NavigationOutcomes.RESET_PASSWORD_URL)
                            .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                                    .jwtTokenAction(JwtTokenActionEnum.RESET_PASSWORD)
                                    .claimValue(email)
                                    .expirationInterval(jwtProperties.getClaim().getResetPasswordExpIntervalMinutes())
                                    .build()
                            )
                            .build()
            );
        }
    }

    public String verifyToken(String token) {
        return jwtTokenService.resolveTokenActionClaim(token, JwtTokenActionEnum.RESET_PASSWORD);
    }

    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String email = verifyToken(resetPasswordDTO.getToken());
        userAuthService.resetPassword(
                email, resetPasswordDTO.getOtp(), resetPasswordDTO.getPassword());

        Optional<UserInfoDTO> user = userAuthService.getUserByEmail(email);
        if (user.isPresent()) {
            userNotificationService.notifyUserPasswordReset(user.get().getUserId());
        }
    }

}
