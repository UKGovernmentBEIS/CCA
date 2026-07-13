package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service.PerformanceDataFacilityDataUploadAttachmentService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataUploadAttachReportHandlerTest {

    @InjectMocks
    private PerformanceDataFacilityDataUploadAttachReportHandler handler;

    @Mock
    private PerformanceDataFacilityDataUploadAttachmentService performanceDataUploadAttachReportPackageService;

    @Test
    void uploadAttachment() {
        final long requestTaskId = 1L;
        final String attachmentUuid = "uuid";
        final String filename = "test.png";

        // Invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        // Verify
        verify(performanceDataUploadAttachReportPackageService, times(1))
                .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getType() {
        assertThat(handler.getType())
                .isEqualTo(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_ATTACH_REPORT);
    }
}
