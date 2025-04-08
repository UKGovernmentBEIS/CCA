package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadAttachReportPackageService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadAttachReportPackageHandlerTest {

    @InjectMocks
    private PerformanceDataUploadAttachReportPackageHandler handler;

    @Mock
    private PerformanceDataUploadAttachReportPackageService performanceDataUploadAttachReportPackageService;

    @Test
    void uploadAttachment() {
        final long requestTaskId = 1L;
        final String attachmentUuid = UUID.randomUUID().toString();
        final String filename = "filename";

        // Invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        verify(performanceDataUploadAttachReportPackageService, times(1))
                .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertThat(handler.getType())
                .isEqualTo(CcaRequestTaskActionType.PERFORMANCE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE);
    }
}
