package uk.gov.cca.api.notification.template.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.cca.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DocumentFileGeneratorService {
    
    private final DocumentTemplateFileService documentTemplateFileService;
    private final DocumentTemplateProcessService documentTemplateProcessService;
    private final FileDocumentService fileDocumentService;
    
    @Transactional
    public FileInfoDTO generateFileDocument(DocumentTemplateType type, TemplateParams templateParams, String fileNameToGenerate) {
        return generate(type, templateParams, fileNameToGenerate);
    }
    
    @Transactional
    public CompletableFuture<FileInfoDTO> generateFileDocumentAsync(DocumentTemplateType type, TemplateParams templateParams, String fileNameToGenerate) {
        return CompletableFuture.supplyAsync(() -> generate(type, templateParams, fileNameToGenerate));
    }
    
    private FileInfoDTO generate(DocumentTemplateType type, TemplateParams templateParams, String fileNameToGenerate) {
        //get file document template
        final FileDTO fileDocumentTemplate = documentTemplateFileService
            .getFileDocumentTemplateByTypeAndCompetentAuthority(
                type,
                templateParams.getCompetentAuthorityParams().getCompetentAuthority().getId()
            );

        //generate file from template
        final byte[] generatedFile;
        try {
            generatedFile = documentTemplateProcessService.generateFileDocumentFromTemplate(
                    fileDocumentTemplate, templateParams,fileNameToGenerate);
        } catch (DocumentTemplateProcessException e) {
            throw new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, fileDocumentTemplate.getFileName());
        }
        
        //persist file
        return fileDocumentService.createFileDocument(generatedFile, fileNameToGenerate);	
        
    }

}
