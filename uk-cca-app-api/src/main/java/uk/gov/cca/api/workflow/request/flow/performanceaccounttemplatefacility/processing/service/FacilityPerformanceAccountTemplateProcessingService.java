package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingResults;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.Year;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateProcessingService {

    private final RequestService requestService;

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public FacilityPerformanceAccountTemplateProcessingResults doProcess(final FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload,
                                                                         FacilityPerformanceAccountTemplateUploadReport facilityUploadReport) throws BpmnExecutionException {
        try {
            final FacilityDTO facility = requestPayload.getFacility();
            final Year targetYear = requestPayload.getTargetYear();

            //TODO: Validate facility, etc

            return FacilityPerformanceAccountTemplateProcessingResults.builder()
                    .reportVersion(1)
                    .build();

        } catch (Exception e) {
            //TODO: enhance
            String message = e.getMessage();
            log.error(message, e);
            throw new BpmnExecutionException(message, List.of(message));
        }
    }

    @Transactional
    public void markAsCompleted(final String requestId, FacilityPerformanceAccountTemplateUploadReport facilityUploadReport) {
        final Request request = requestService.findRequestById(requestId);
        final FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload =
                (FacilityPerformanceAccountTemplateProcessingRequestPayload) request.getPayload();

        // Update processing
        final Request parentRequest = requestService.findRequestById(requestPayload.getParentRequestId());
        FacilityPerformanceAccountTemplateDataProcessingRequestPayload parentRequestPayload =
                (FacilityPerformanceAccountTemplateDataProcessingRequestPayload) parentRequest.getPayload();
        facilityUploadReport.setSucceeded(facilityUploadReport.getErrors().isEmpty());
        parentRequestPayload.getFacilityReports().put(facilityUploadReport.getFacilityId(), facilityUploadReport);
    }
}
