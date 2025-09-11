package uk.gov.cca.api.web.orchestrator.template.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import uk.gov.netz.api.documenttemplate.domain.dto.DocumentTemplateDTO;
import uk.gov.netz.api.documenttemplate.service.DocumentTemplateQueryService;
import uk.gov.netz.api.notification.template.domain.dto.NotificationTemplateInfoDTO;
import uk.gov.netz.api.notification.template.service.NotificationTemplateQueryService;
import uk.gov.cca.api.web.orchestrator.template.dto.DocumentTemplateViewDTO;

@Service
@AllArgsConstructor
public class DocumentTemplateQueryServiceOrchestrator {
    
    private final DocumentTemplateQueryService documentTemplateQueryService;
    private final NotificationTemplateQueryService notificationTemplateQueryService;
    
    @Transactional(readOnly = true)
    public DocumentTemplateViewDTO getDocumentTemplateDTOById(Long id) {
        final DocumentTemplateDTO documentTemplateDTO = documentTemplateQueryService
                .getDocumentTemplateDTOById(id);
        
        final NotificationTemplateInfoDTO notificationTemplate = Optional.ofNullable(documentTemplateDTO.getNotificationTemplateId())
                .map(notificationTemplateQueryService::getNotificationTemplateInfoDTOById)
                .orElse(null);
        
        return DocumentTemplateViewDTO.builder()
                .documentTemplate(documentTemplateDTO)
                .notificationTemplate(notificationTemplate)
                .build();
    }
}

