package uk.gov.cca.api.files.attachments.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CcaFileAttachmentServiceTest {

    @InjectMocks
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Mock
    private FileAttachmentRepository fileAttachmentRepository;

    @Test
    void createSystemFileAttachment() throws IOException {
        byte[] fileContent = "fileContent".getBytes();
        final FileDTO fileDTO = FileDTO.builder()
                .fileName("test")
                .fileContent(fileContent)
                .fileType("text/plain")
                .fileSize(fileContent.length)
                .build();
        final FileStatus status = FileStatus.SUBMITTED;
        final String createdBy = "createdBy";

        // Invoke
        String result = ccaFileAttachmentService.createSystemFileAttachment(fileDTO, status, createdBy);

        // Verify
        assertThat(result).isNotNull();
        verify(fileAttachmentRepository, times(1)).save(any());
    }
}
