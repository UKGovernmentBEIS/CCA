package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormOutcome;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service.PerformanceDataFacilityDigitalFormSubmitService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation.PerformanceDataFacilityDigitalFormSubmitValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormSubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final PerformanceDataFacilityDigitalFormSubmitValidator performanceDataFacilityDigitalFormSubmitValidator;
    private final PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final LocalDateTime submissionDate = LocalDateTime.now();
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate if report or facility is expired
        boolean isExpired = performanceDataFacilityDigitalFormSubmitValidator.isReportSubmissionExpired(requestTask, submissionDate.toLocalDate());
        if(isExpired) {
            // Complete task as EXPIRED
            performanceDataFacilityDigitalFormSubmitService.markTaskAsExpired(requestTask, submissionDate);
            workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                    BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                    CcaBpmnProcessConstants.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_OUTCOME, PerformanceDataFacilityDigitalFormOutcome.EXPIRED));

            return requestTask.getPayload();
        }

        // Validate task
        performanceDataFacilityDigitalFormSubmitValidator.validate(requestTask);

        // Submit action
        performanceDataFacilityDigitalFormSubmitService.submit(appUser, requestTask, submissionDate);

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                CcaBpmnProcessConstants.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_OUTCOME, PerformanceDataFacilityDigitalFormOutcome.COMPLETED));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_APPLICATION);
    }
}
