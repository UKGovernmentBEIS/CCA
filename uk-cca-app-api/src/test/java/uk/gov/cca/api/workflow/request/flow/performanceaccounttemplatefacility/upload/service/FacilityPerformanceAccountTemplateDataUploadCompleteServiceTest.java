package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadResults;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
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
class FacilityPerformanceAccountTemplateDataUploadCompleteServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadCompleteService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private FacilityPerformanceAccountTemplateDataUploadCreateCsvService createCsvService;

    @Test
    void processCompleted() {
        final String requestId = "requestId";
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload payload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .performanceAccountTemplateDataUpload(FacilityPerformanceAccountTemplateDataUpload.builder()
                        .files(Set.of(UUID.randomUUID(), UUID.randomUUID()))
                        .build())
                .csvRowErrors(List.of(FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                                .facilityBusinessId("facilityBusinessId1")
                                .filename("test.csv")
                                .message("error1")
                                .build(),
                        FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                                .facilityBusinessId("facilityBusinessId1")
                                .filename("test2.csv")
                                .message("error1")
                                .build(),
                        FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                                .facilityBusinessId(null)
                                .filename("test1.csv")
                                .message("error1")
                                .build(),
                        FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                                .facilityBusinessId(null)
                                .filename("test2.csv")
                                .message("error1")
                                .build()
                ))
                .results(FacilityPerformanceAccountTemplateDataUploadResults.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .request(Request.builder()
                        .metadata(FacilityPerformanceAccountTemplateDataUploadRequestMetadata.builder()
                                .submittedDate(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .build())
                        .build())
                .payload(payload)
                .build();
        final Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = Map.of(
                1L, FacilityPerformanceAccountTemplateUploadReport.builder().succeeded(true).build(),
                2L, FacilityPerformanceAccountTemplateUploadReport.builder().succeeded(false).build()
        );

        when(requestTaskService.findByTypeAndRequestId(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId))
                .thenReturn(requestTask);

        // Invoke
        service.processCompleted(requestId, facilityReports);

        // Verify
        assertThat(payload.getFacilityReports()).containsExactlyEntriesOf(facilityReports);
        assertThat(payload.getProcessingStatus()).isEqualTo(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
        assertThat(payload.getResults().getTotalFilesUploaded()).isEqualTo(2);
        assertThat(payload.getResults().getFacilitiesSucceeded()).isEqualTo(1);
        assertThat(payload.getResults().getFacilitiesFailed()).isEqualTo(4);
        assertThat(payload.getResults().getSubmittedDate()).isEqualTo(LocalDate.of(2020, 1, 1).atStartOfDay());
        verify(requestTaskService, times(1))
                .findByTypeAndRequestId(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId);
        verify(createCsvService, times(1))
                .createCsvFile(requestTask, facilityReports);
    }

    @Test
    void processMessageFailed() {
        final String requestId = "requestId";

        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload payload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(payload).build();

        when(requestTaskService.findByTypeAndRequestId(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId))
                .thenReturn(requestTask);

        // Invoke
        service.processMessageFailed(requestId);

        // Verify
        assertThat(payload.getProcessingStatus()).isEqualTo(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
        assertThat(payload.getErrorMessage()).isEqualTo(FacilityPerformanceAccountTemplateDataUploadErrorType.MESSAGE_PROCESSING_FAILED);
        verify(requestTaskService, times(1))
                .findByTypeAndRequestId(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId);
    }

}
