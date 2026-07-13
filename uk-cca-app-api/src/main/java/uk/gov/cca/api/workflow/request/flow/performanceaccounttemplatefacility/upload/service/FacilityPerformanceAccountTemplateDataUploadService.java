package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadService {

    @Transactional
    public void process(RequestTask requestTask, FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload taskActionPayload,
                        LocalDateTime submissionDate) {
        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
        FacilityPerformanceAccountTemplateDataUploadRequestMetadata metadata =
                (FacilityPerformanceAccountTemplateDataUploadRequestMetadata) requestTask.getRequest().getMetadata();

        taskPayload.setPerformanceAccountTemplateDataUpload(taskActionPayload.getPerformanceAccountTemplateDataUpload());

        // TODO: Validate

        // TODO: Extract CSV data

        // TODO: Set csv extract outcome

        taskPayload.setProcessingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.IN_PROGRESS);

        // Set metadata
        metadata.setSubmittedDate(submissionDate);
    }
}
