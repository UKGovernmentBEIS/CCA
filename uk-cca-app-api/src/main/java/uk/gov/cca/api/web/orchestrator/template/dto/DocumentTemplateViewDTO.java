package uk.gov.cca.api.web.orchestrator.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.documenttemplate.domain.dto.DocumentTemplateDTO;
import uk.gov.netz.api.notification.template.domain.dto.NotificationTemplateInfoDTO;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTemplateViewDTO {
    
    @JsonUnwrapped
    private DocumentTemplateDTO documentTemplate;
    
    private NotificationTemplateInfoDTO notificationTemplate;
}

