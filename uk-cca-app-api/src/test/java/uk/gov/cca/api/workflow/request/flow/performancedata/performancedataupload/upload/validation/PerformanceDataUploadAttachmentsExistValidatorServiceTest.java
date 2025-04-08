package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUpload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadAttachmentsExistValidatorServiceTest {

    @InjectMocks
    private PerformanceDataUploadAttachmentsExistValidatorService validatorService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void validate() {
        final UUID zipFile = UUID.randomUUID();
        final Set<String> files = Set.of(zipFile.toString());
        final PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .performanceDataUpload(PerformanceDataUpload.builder()
                                .reportPackages(Set.of(zipFile))
                                .build())
                        .performanceDataUploadAttachments(Map.of(zipFile, "File"))
                        .build();

        when(fileAttachmentService.fileAttachmentsExist(files))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = validatorService.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(fileAttachmentService, times(1)).fileAttachmentsExist(files);
    }

    @Test
    void validate_file_not_exists() {
        final UUID zipFile = UUID.randomUUID();
        final Set<String> files = Set.of(zipFile.toString());
        final PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .performanceDataUpload(PerformanceDataUpload.builder()
                                .reportPackages(Set.of(zipFile))
                                .build())
                        .performanceDataUploadAttachments(Map.of(zipFile, "File"))
                        .build();

        when(fileAttachmentService.fileAttachmentsExist(files))
                .thenReturn(false);

        // Invoke
        BusinessValidationResult result = validatorService.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataUploadViolation>) result.getViolations()).extracting(PerformanceDataUploadViolation::getMessage)
                .containsOnly(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.ATTACHMENT_NOT_FOUND.getMessage());
        verify(fileAttachmentService, times(1)).fileAttachmentsExist(files);
    }

    @Test
    void validate_file_not_in_attachments() {
        final UUID zipFile = UUID.randomUUID();
        final PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .performanceDataUpload(PerformanceDataUpload.builder()
                                .reportPackages(Set.of(zipFile))
                                .build())
                        .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<PerformanceDataUploadViolation>) result.getViolations()).extracting(PerformanceDataUploadViolation::getMessage)
                .containsOnly(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.ATTACHMENT_NOT_FOUND.getMessage());
        verifyNoInteractions(fileAttachmentService);
    }
}
