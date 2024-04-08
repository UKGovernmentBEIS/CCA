package uk.gov.cca.api.notification.template.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.cca.api.notification.template.service.DocumentTemplateFileService;
import uk.gov.cca.api.notification.template.service.DocumentTemplateProcessException;
import uk.gov.cca.api.notification.template.service.DocumentTemplateProcessService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.cca.api.notification.template.domain.enumeration.DocumentTemplateType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentFileGeneratorServiceTest {

    @InjectMocks
    private DocumentFileGeneratorService service;
    
    @Mock
    private DocumentTemplateFileService documentTemplateFileService;

    @Mock
    private DocumentTemplateProcessService documentTemplateProcessService;

    @Mock
    private FileDocumentService fileDocumentService;

    @Test
    void generateFileDocument() throws DocumentTemplateProcessException {
        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build();
        DocumentTemplateType type = DocumentTemplateType.IN_RFI;
        AccountTemplateParams accountParams = Mockito.mock(AccountTemplateParams.class);
        TemplateParams templateParams = TemplateParams.builder()
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(ca)
                        .build())
                .accountParams(accountParams)
                .build();
        String fileNameToGenerate = "generatedFileName";
        
        FileDTO fileDocumentTemplate = FileDTO.builder()
                .fileName("fileDocTemplate")
                .fileContent("some content".getBytes())
                .fileType("some type")
                .fileSize("some content".length())
                .build();

        when(documentTemplateFileService.getFileDocumentTemplateByTypeAndCompetentAuthority(type, ca.getId()))
            .thenReturn(fileDocumentTemplate);
        
        byte[] generatedFileBytes = "generated file content".getBytes();
        when(documentTemplateProcessService.generateFileDocumentFromTemplate(fileDocumentTemplate, templateParams, fileNameToGenerate))
            .thenReturn(generatedFileBytes);
        
        UUID uuid = UUID.randomUUID();
        FileInfoDTO perstistedGeneratedFileInfo = FileInfoDTO.builder().name(fileNameToGenerate).uuid(uuid.toString()).build();
        when(fileDocumentService.createFileDocument(generatedFileBytes, fileNameToGenerate))
            .thenReturn(perstistedGeneratedFileInfo);
        
        //invoke
        FileInfoDTO result = service.generateFileDocument(type, templateParams, fileNameToGenerate);
        
        //assert
        assertThat(result).isEqualTo(perstistedGeneratedFileInfo);
        verify(documentTemplateFileService, times(1))
            .getFileDocumentTemplateByTypeAndCompetentAuthority(type, ca.getId());
        verify(documentTemplateProcessService, times(1)).generateFileDocumentFromTemplate(fileDocumentTemplate, templateParams, fileNameToGenerate);
        verify(fileDocumentService, times(1)).createFileDocument(generatedFileBytes, fileNameToGenerate);
    }
    
    @Test
    void generateFileDocument_throws_business_exception_when_generate_file_fails() throws DocumentTemplateProcessException {
        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build();
        DocumentTemplateType type = DocumentTemplateType.IN_RFI;
        AccountTemplateParams accountParams = Mockito.mock(AccountTemplateParams.class);
        TemplateParams templateParams = TemplateParams.builder()
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(ca)
                        .build())
                .accountParams(accountParams)
                .build();
        String fileNameToGenerate = "generatedFileName";
        
        FileDTO fileDocumentTemplate = FileDTO.builder()
                .fileName("fileDocTemplate")
                .fileContent("some content".getBytes())
                .fileType("some type")
                .fileSize("some content".length())
                .build();

        when(documentTemplateFileService.getFileDocumentTemplateByTypeAndCompetentAuthority(type, ca.getId()))
            .thenReturn(fileDocumentTemplate);
        
        when(documentTemplateProcessService.generateFileDocumentFromTemplate(fileDocumentTemplate, templateParams, fileNameToGenerate))
            .thenThrow(new DocumentTemplateProcessException("process failed"));
        
        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> {
            service.generateFileDocument(type, templateParams, fileNameToGenerate);    
        });
        
        
        //assert
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);
        verify(documentTemplateFileService, times(1))
            .getFileDocumentTemplateByTypeAndCompetentAuthority(type, ca.getId());
        verify(documentTemplateProcessService, times(1)).generateFileDocumentFromTemplate(fileDocumentTemplate, templateParams, fileNameToGenerate);
        verifyNoInteractions(fileDocumentService);
    }

}
