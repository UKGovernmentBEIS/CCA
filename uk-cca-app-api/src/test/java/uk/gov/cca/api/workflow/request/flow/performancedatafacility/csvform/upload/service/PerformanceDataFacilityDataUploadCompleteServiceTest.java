package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUpload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadResults;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataUploadCompleteServiceTest {

    @InjectMocks
    private PerformanceDataFacilityDataUploadCompleteService performanceDataFacilityDataUploadCompleteService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PerformanceDataFacilityDataUploadCreateCsvService performanceDataFacilityDataUploadCreateCsvService;

    @Test
    void processCompleted() {
        final String requestId = "requestId";
        PerformanceDataFacilityDataUploadSubmitRequestTaskPayload payload = PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                .performanceDataUpload(PerformanceDataFacilityUpload.builder()
                        .files(Set.of(UUID.randomUUID(), UUID.randomUUID()))
                        .build())
                .csvRowErrors(List.of(PerformanceDataFacilityCsvErrorEntry.builder().filename("test.csv").message("error1 | error2").build()))
                .results(PerformanceDataFacilityUploadResults.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .request(Request.builder()
                        .metadata(PerformanceDataFacilityDataUploadRequestMetadata.builder()
                                .submittedDate(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .build())
                        .build())
                .payload(payload)
                .build();
        final Map<Long, FacilityUploadReport > facilityReports = Map.of(
                1L, FacilityUploadReport.builder().succeeded(true).build(),
                2L, FacilityUploadReport.builder().build()
        );

        when(requestTaskService.findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT, requestId))
                .thenReturn(requestTask);

        // Invoke
        performanceDataFacilityDataUploadCompleteService.processCompleted(requestId, facilityReports);

        // Verify
        assertThat(payload.getFacilityReports()).containsExactlyEntriesOf(facilityReports);
        assertThat(payload.getProcessingStatus()).isEqualTo(PerformanceDataFacilityDataUploadProcessingStatus.COMPLETED);
        assertThat(payload.getResults().getTotalFilesUploaded()).isEqualTo(2);
        assertThat(payload.getResults().getFacilitiesSucceeded()).isEqualTo(1);
        assertThat(payload.getResults().getFacilitiesFailed()).isEqualTo(2);
        assertThat(payload.getResults().getSubmittedDate()).isEqualTo(LocalDate.of(2020, 1, 1).atStartOfDay());
        verify(requestTaskService, times(1))
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT, requestId);
        verify(performanceDataFacilityDataUploadCreateCsvService, times(1))
                .createCsvFile(requestTask, facilityReports);
    }

    @Test
    void processMessageFailed() {
        final String requestId = "requestId";

        PerformanceDataFacilityDataUploadSubmitRequestTaskPayload payload = PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(payload).build();

        when(requestTaskService.findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT, requestId))
                .thenReturn(requestTask);

        // Invoke
        performanceDataFacilityDataUploadCompleteService.processMessageFailed(requestId);

        // Verify
        assertThat(payload.getProcessingStatus()).isEqualTo(PerformanceDataFacilityDataUploadProcessingStatus.COMPLETED);
        assertThat(payload.getErrorMessage()).isEqualTo(PerformanceDataFacilityUploadErrorType.MESSAGE_PROCESSING_FAILED);
        verify(requestTaskService, times(1))
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT, requestId);
    }
}
