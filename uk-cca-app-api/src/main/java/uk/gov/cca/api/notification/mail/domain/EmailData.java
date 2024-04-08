package uk.gov.cca.api.notification.mail.domain;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class EmailData {
    
    private EmailNotificationTemplateData notificationTemplateData;
    
    @Builder.Default
    private Map<String, byte[]> attachments = new HashMap<>();
    
}
