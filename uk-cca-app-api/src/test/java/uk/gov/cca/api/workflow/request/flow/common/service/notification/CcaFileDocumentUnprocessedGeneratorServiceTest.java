package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.documenttemplate.domain.dto.DocumentTemplateFileInfoDTO;
import uk.gov.netz.api.documenttemplate.service.DocumentTemplateFileService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

@ExtendWith(MockitoExtension.class)
class CcaFileDocumentUnprocessedGeneratorServiceTest {
    
    @InjectMocks
    private  CcaFileDocumentUnprocessedGeneratorService ccaFileDocumentUnprocessedGeneratorService;
    
    @Mock
    private FileDocumentStorageService fileDocumentStorageService;
    
    @Mock
    private DocumentTemplateFileService documentTemplateFileService;
    
    @Mock
    private FileInfoDTO fileInfoDTO;
    
    @Test
    void copyTemplateAndSaveFileDocument() {

        byte[] fileContent = "template content".getBytes(StandardCharsets.UTF_8);
        
        final String fileName = "Reclaim Form.docx";
        
        DocumentTemplateFileInfoDTO documentTemplate = DocumentTemplateFileInfoDTO.builder()
                .file(FileDTO.builder()
                        .fileContent(fileContent)
                        .build())
                .build();

        when(documentTemplateFileService
                .getFileDocumentTemplateFileInfoByTypeAndCompetentAuthority(
                        CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT, CompetentAuthorityEnum.ENGLAND))
                .thenReturn(documentTemplate);

        when(fileDocumentStorageService.createFileDocument(fileContent, fileName))
                .thenReturn(fileInfoDTO);

        // When
        FileInfoDTO result = ccaFileDocumentUnprocessedGeneratorService.copyTemplateAndSaveFileDocument(
                CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT, CompetentAuthorityEnum.ENGLAND, fileName);

        // Then
        assertThat(result).isSameAs(fileInfoDTO);

        verify(documentTemplateFileService)
                .getFileDocumentTemplateFileInfoByTypeAndCompetentAuthority(
                        CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT, CompetentAuthorityEnum.ENGLAND);

        verify(fileDocumentStorageService)
                .createFileDocument(fileContent, fileName);
    }

}
