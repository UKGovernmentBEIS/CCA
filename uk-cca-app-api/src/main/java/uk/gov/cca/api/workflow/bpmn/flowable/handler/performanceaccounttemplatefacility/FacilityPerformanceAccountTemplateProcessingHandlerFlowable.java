package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.validation.FacilityPerformanceAccountTemplateDataViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingResults;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service.FacilityPerformanceAccountTemplateProcessingService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.transform.FacilityPerformanceAccountTemplateDataProcessingSubmittedMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateProcessingHandlerFlowable implements JavaDelegate {

    private final RequestService requestService;
    private final FacilityPerformanceAccountTemplateProcessingService facilityPerformanceAccountTemplateProcessingService;
    private static final FacilityPerformanceAccountTemplateDataProcessingSubmittedMapper FACILITY_PAT_SUBMITTED_MAPPER = Mappers
            .getMapper(FacilityPerformanceAccountTemplateDataProcessingSubmittedMapper.class);

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final FacilityPerformanceAccountTemplateUploadReport facilityUploadReport = (FacilityPerformanceAccountTemplateUploadReport) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        try {
            final Request request = requestService.findRequestById(requestId);
            final FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload =
                    (FacilityPerformanceAccountTemplateProcessingRequestPayload) request.getPayload();

            FacilityPerformanceAccountTemplateProcessingResults results =
                    facilityPerformanceAccountTemplateProcessingService.doProcess(requestPayload, facilityUploadReport);

            // Add timeline
            FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload actionPayload = FACILITY_PAT_SUBMITTED_MAPPER
                    .toFacilityPerformanceAccountTemplateDataFacilitySubmittedRequestActionPayload(requestPayload, results);
            requestService.addActionToRequest(request,
                    actionPayload,
                    CcaRequestActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED,
                    requestPayload.getSectorUserAssignee());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            facilityUploadReport.getErrors()
                    .add(FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.FACILITY_PROCESS_FAILED.getMessage());
        }
    }
}
