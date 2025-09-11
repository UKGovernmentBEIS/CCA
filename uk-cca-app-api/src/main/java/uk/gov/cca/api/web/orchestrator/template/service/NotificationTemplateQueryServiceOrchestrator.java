package uk.gov.cca.api.web.orchestrator.template.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import uk.gov.netz.api.documenttemplate.domain.dto.DocumentTemplateInfoDTO;
import uk.gov.netz.api.documenttemplate.service.DocumentTemplateQueryService;
import uk.gov.netz.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.netz.api.notification.template.service.NotificationTemplateQueryService;
import uk.gov.cca.api.web.orchestrator.template.dto.NotificationTemplateViewDTO;

@Service
@AllArgsConstructor
public class NotificationTemplateQueryServiceOrchestrator {
    
    private final NotificationTemplateQueryService notificationTemplateQueryService;
    private final DocumentTemplateQueryService documentTemplateQueryService;
    
    @Transactional(readOnly = true)
    public NotificationTemplateViewDTO getManagedNotificationTemplateById(Long id) {
        final NotificationTemplateDTO notificationTemplateDTO = notificationTemplateQueryService
                .getManagedNotificationTemplateById(id);
        
        final Set<DocumentTemplateInfoDTO> documentTemplates = documentTemplateQueryService.getAllByNotificationTemplateId(id);
        
        return NotificationTemplateViewDTO.builder()
                .notificationTemplate(notificationTemplateDTO)
                .documentTemplates(documentTemplates)
                .build();
    }
    
}
