package uk.gov.cca.api.web.orchestrator.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.documenttemplate.domain.dto.DocumentTemplateInfoDTO;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import uk.gov.netz.api.notification.template.domain.dto.NotificationTemplateDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateViewDTO {
    
    @JsonUnwrapped
    private NotificationTemplateDTO notificationTemplate;
    
    @Builder.Default
    private Set<DocumentTemplateInfoDTO> documentTemplates = new HashSet<>();
    
}

