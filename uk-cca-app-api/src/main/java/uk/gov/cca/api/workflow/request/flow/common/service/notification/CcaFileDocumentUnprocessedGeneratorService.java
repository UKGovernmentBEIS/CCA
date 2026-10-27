package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.documenttemplate.domain.dto.DocumentTemplateFileInfoDTO;
import uk.gov.netz.api.documenttemplate.service.DocumentTemplateFileService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

@Validated
@Service
@RequiredArgsConstructor
public class CcaFileDocumentUnprocessedGeneratorService {
    
    private final FileDocumentStorageService fileDocumentStorageService;
    private final DocumentTemplateFileService documentTemplateFileService;
    
    @Transactional
    public FileInfoDTO copyTemplateAndSaveFileDocument(
            @NotBlank String type,
            @NotNull CompetentAuthorityEnum competentAuthority,
            @NotBlank String fileNameToGenerate) {

        DocumentTemplateFileInfoDTO documentTemplate = findDocumentTemplateInfo(type, competentAuthority);

        return fileDocumentStorageService.createFileDocument(
                documentTemplate.getFile().getFileContent(),
                fileNameToGenerate);
    }
    
    private DocumentTemplateFileInfoDTO findDocumentTemplateInfo(String type, CompetentAuthorityEnum competentAuthority) {
        return documentTemplateFileService
                .getFileDocumentTemplateFileInfoByTypeAndCompetentAuthority(
                        type,
                        competentAuthority
                );
    }

}
