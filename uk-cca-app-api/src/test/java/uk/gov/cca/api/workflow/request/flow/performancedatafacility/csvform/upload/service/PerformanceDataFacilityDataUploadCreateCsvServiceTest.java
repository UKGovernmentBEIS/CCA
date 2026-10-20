package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadResults;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.io.IOException;
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
class PerformanceDataFacilityDataUploadCreateCsvServiceTest {

    @InjectMocks
    private PerformanceDataFacilityDataUploadCreateCsvService performanceDataFacilityDataUploadCreateCsvService;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Test
    void createCsvFile() throws IOException {
        final String assignee = "assignee";
        PerformanceDataFacilityDataUploadSubmitRequestTaskPayload payload = PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                .csvRowErrors(List.of(PerformanceDataFacilityCsvErrorEntry.builder().filename("test.csv").message("error1 | error2").build()))
                .results(PerformanceDataFacilityUploadResults.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .assignee(assignee)
                .payload(payload)
                .build();
        final Map<Long, FacilityUploadReport> facilityReports = Map.of(1L, new FacilityUploadReport());

        final String fileCsv = UUID.randomUUID().toString();

        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.PENDING), eq(assignee)))
                .thenReturn(fileCsv);

        // Invoke
        performanceDataFacilityDataUploadCreateCsvService.createCsvFile(requestTask, facilityReports);

        // Verify
        assertThat(payload.getResults().getUploadSummaryFile()).isNotNull();
        assertThat(payload.getAttachments()).isNotEmpty();
        assertThat(payload.getErrorMessage()).isNull();
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.PENDING), eq(assignee));
    }

    @Test
    void createCsvFile_throw_exception() throws IOException {
        final String assignee = "assignee";
        PerformanceDataFacilityDataUploadSubmitRequestTaskPayload payload = PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                .csvRowErrors(List.of(PerformanceDataFacilityCsvErrorEntry.builder().filename("test.csv").message("error1 | error2").build()))
                .results(PerformanceDataFacilityUploadResults.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .assignee(assignee)
                .payload(payload)
                .build();
        final Map<Long, FacilityUploadReport > facilityReports = Map.of(1L, new FacilityUploadReport());

        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.PENDING), eq(assignee)))
                .thenThrow(new IOException("test"));

        // Invoke
        performanceDataFacilityDataUploadCreateCsvService.createCsvFile(requestTask, facilityReports);

        // Verify
        assertThat(payload.getResults().getUploadSummaryFile()).isNull();
        assertThat(payload.getAttachments()).isEmpty();
        assertThat(payload.getErrorMessage()).isEqualTo(PerformanceDataFacilityUploadErrorType.SUBMISSION_RESULTS_CSV_FAILED);
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.PENDING), eq(assignee));
    }
}
