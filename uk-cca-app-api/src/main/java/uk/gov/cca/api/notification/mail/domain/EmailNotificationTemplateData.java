package uk.gov.cca.api.notification.mail.domain;

import lombok.Builder;
import lombok.Data;
import uk.gov.cca.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class EmailNotificationTemplateData {

    private CompetentAuthorityEnum competentAuthority;
    
    private NotificationTemplateName templateName;

    @Builder.Default
    private Map<String, Object> templateParams = new HashMap<>();
    
}
