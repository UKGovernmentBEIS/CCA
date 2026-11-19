package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadResults;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FacilityPerformanceAccountTemplateDataUploadCompletedMapperTest {

    private final FacilityPerformanceAccountTemplateDataUploadCompletedMapper mapper =
            Mappers.getMapper(FacilityPerformanceAccountTemplateDataUploadCompletedMapper.class);

    @Test
    void toFacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload() {
        final LocalDateTime submittedDate = LocalDateTime.now();
        final Year targetYear = Year.of(2016);
        final UUID uuid = UUID.randomUUID();

        final FacilityPerformanceAccountTemplateDataUpload upload = FacilityPerformanceAccountTemplateDataUpload.builder()
                .targetYear(targetYear)
                .build();
        final FacilityPerformanceAccountTemplateDataUploadResults results = FacilityPerformanceAccountTemplateDataUploadResults.builder()
                .totalFilesUploaded(1)
                .facilitiesFailed(0)
                .facilitiesSucceeded(1)
                .uploadSummaryFile(uuid)
                .submittedDate(submittedDate)
                .build();

        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .performanceAccountTemplateDataUpload(upload)
                .results(results)
                .processingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED)
                .uploadAttachments(Map.of(uuid, "fileName.csv"))
                .build();

        final FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload expected = FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_COMPLETED_PAYLOAD)
                .details(upload)
                .results(results)
                .uploadAttachments(Map.of(uuid, "fileName.csv"))
                .build();

        // invoke
        FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload actual =
                mapper.toFacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload(taskPayload);

        // verify
        assertThat(actual).isEqualTo(expected);
    }
}
