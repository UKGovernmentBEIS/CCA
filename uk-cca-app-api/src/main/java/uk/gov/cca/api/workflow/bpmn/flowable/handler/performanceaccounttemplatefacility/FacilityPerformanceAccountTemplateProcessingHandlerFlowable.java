package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingResults;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service.FacilityPerformanceAccountTemplateProcessingService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateProcessingHandlerFlowable implements JavaDelegate {

    private final RequestService requestService;
    private final FacilityPerformanceAccountTemplateProcessingService facilityPerformanceAccountTemplateProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final FacilityPerformanceAccountTemplateUploadReport facilityUploadReport = (FacilityPerformanceAccountTemplateUploadReport) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        try {
            final Request request = requestService.findRequestById(requestId);
            final FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload =
                    (FacilityPerformanceAccountTemplateProcessingRequestPayload) request.getPayload();
            FacilityPerformanceAccountTemplateProcessingRequestMetadata metadata =
                    (FacilityPerformanceAccountTemplateProcessingRequestMetadata) request.getMetadata();

            // TODO implement locking before saving the BO
            FacilityPerformanceAccountTemplateProcessingResults results = facilityPerformanceAccountTemplateProcessingService
                    .doProcess(requestPayload, facilityUploadReport);


            if (facilityUploadReport.getErrors().isEmpty()) {
                // Set metadata
                metadata.setReportVersion(results.getReportVersion());

                //TODO: Add timeline
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //TODO: Validation Error Messages
            facilityUploadReport.getErrors()
                    .add("Facility process failed");
        }
    }
}
