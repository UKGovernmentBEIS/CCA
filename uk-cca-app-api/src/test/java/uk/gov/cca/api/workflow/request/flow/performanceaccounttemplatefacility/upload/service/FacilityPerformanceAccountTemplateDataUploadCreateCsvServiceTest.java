package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadResults;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadCreateCsvServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadCreateCsvService service;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Test
    void createCsvFile() {
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload payload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .csvRowErrors(List.of(FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder().filename("test.csv").message("error1 | error2").build()))
                .results(FacilityPerformanceAccountTemplateDataUploadResults.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .assignee("assignee")
                .payload(payload)
                .build();
        final Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = Map.of(1L, new FacilityPerformanceAccountTemplateUploadReport());

        final String fileCsv = UUID.randomUUID().toString();

        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.PENDING)))
                .thenReturn(fileCsv);

        // Invoke
        service.createCsvFile(requestTask, facilityReports);

        // Verify
        assertThat(payload.getResults().getUploadSummaryFile()).isNotNull();
        assertThat(payload.getAttachments()).isNotEmpty();
        assertThat(payload.getErrorMessage()).isNull();
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.PENDING));
    }

    @Test
    void createCsvFile_throw_exception() {
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload payload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .csvRowErrors(List.of(FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder().filename("test.csv").message("error1 | error2").build()))
                .results(FacilityPerformanceAccountTemplateDataUploadResults.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .assignee("assignee")
                .payload(payload)
                .build();
        final Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = Map.of(1L, new FacilityPerformanceAccountTemplateUploadReport());

        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.PENDING)))
                .thenThrow(new NullPointerException("test"));

        // Invoke
        service.createCsvFile(requestTask, facilityReports);

        // Verify
        assertThat(payload.getResults().getUploadSummaryFile()).isNull();
        assertThat(payload.getAttachments()).isEmpty();
        assertThat(payload.getErrorMessage()).isEqualTo(FacilityPerformanceAccountTemplateDataUploadErrorType.SUBMISSION_RESULTS_CSV_FAILED);
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.PENDING));
    }
}
