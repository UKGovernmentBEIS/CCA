package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation.FacilityPerformanceAccountTemplateDataUploadValidator;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadService service;

    @Mock
    private FacilityPerformanceAccountTemplateDataUploadValidator facilityPerformanceAccountTemplateDataUploadValidator;

    //TODO: enhance

    @Test
    void process() {
        final LocalDateTime submissionDate = LocalDateTime.now();
        final Year targetYear = Year.of(submissionDate.getYear() - 1);
        final FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = FacilityPerformanceAccountTemplateDataUpload.builder()
                .targetYear(targetYear)
                .files(Set.of(UUID.randomUUID()))
                .build();
        FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload taskActionPayload = FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.builder()
                .performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
                .build();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .build();
        final FacilityPerformanceAccountTemplateDataUploadRequestMetadata metadata = FacilityPerformanceAccountTemplateDataUploadRequestMetadata.builder()
                .targetYear(targetYear)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().metadata(metadata).build())
                .payload(requestTaskPayload)
                .build();

        // Invoke
        service.process(requestTask, taskActionPayload, submissionDate);

        // Verify
        assertThat(requestTaskPayload.getPerformanceAccountTemplateDataUpload()).isEqualTo(performanceAccountTemplateDataUpload);
        verify(facilityPerformanceAccountTemplateDataUploadValidator, times(1))
                .validate(eq(requestTaskPayload), any());
    }
}
