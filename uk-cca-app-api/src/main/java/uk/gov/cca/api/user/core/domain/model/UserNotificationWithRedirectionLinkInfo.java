package uk.gov.cca.api.user.core.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.netz.api.token.JwtTokenActionEnum;

import java.util.Map;

/**
 * Object containing information to send a notification with a redirection link to user.
 */
@Data
@Builder
public class UserNotificationWithRedirectionLinkInfo {

    private NotificationTemplateName templateName;
    private String userEmail;
    private Map<String, Object> notificationParams;
    private String linkParamName;
    private String linkPath;
    private TokenParams tokenParams;

    @Getter
    @EqualsAndHashCode
    @Builder
    public static class TokenParams {
        private final JwtTokenActionEnum jwtTokenAction;
        private final String claimValue;
        private final long expirationInterval;
    }
}
