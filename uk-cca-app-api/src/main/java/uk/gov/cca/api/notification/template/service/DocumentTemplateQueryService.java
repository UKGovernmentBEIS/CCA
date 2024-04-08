package uk.gov.cca.api.notification.template.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.DocumentTemplateAuthorityInfoProvider;
import uk.gov.cca.api.notification.template.domain.DocumentTemplate;
import uk.gov.cca.api.notification.template.domain.dto.DocumentTemplateDTO;
import uk.gov.cca.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.cca.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.cca.api.notification.template.transform.DocumentTemplateMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentTemplateService;
import uk.gov.cca.api.notification.template.repository.DocumentTemplateRepository;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DocumentTemplateQueryService implements DocumentTemplateAuthorityInfoProvider {

    private final DocumentTemplateRepository documentTemplateRepository;
    private final FileDocumentTemplateService fileDocumentTemplateService;
    private final DocumentTemplateMapper documentTemplateMapper;
    
    DocumentTemplate getDocumentTemplateById(Long id) {
        return documentTemplateRepository.findById(id)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public TemplateSearchResults getDocumentTemplatesBySearchCriteria(DocumentTemplateSearchCriteria searchCriteria) {
        return documentTemplateRepository.findBySearchCriteria(searchCriteria);
    }

    @Transactional(readOnly = true)
    public DocumentTemplateDTO getDocumentTemplateDTOById(Long id) {
        DocumentTemplate documentTemplate = getDocumentTemplateById(id);
        FileInfoDTO fileInfoDTO = fileDocumentTemplateService.getFileInfoDocumentTemplateById(documentTemplate.getFileDocumentTemplateId());
        return documentTemplateMapper.toDocumentTemplateDTO(documentTemplate, fileInfoDTO);
    }

    @Override
    public CompetentAuthorityEnum getDocumentTemplateCaById(Long id) {
        return getDocumentTemplateById(id).getCompetentAuthority();
    }
}
