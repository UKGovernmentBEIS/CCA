package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormOutcome;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service.PerformanceDataFacilityDigitalFormSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormCancelActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Cancel with timeline
        performanceDataFacilityDigitalFormSubmitService.cancel(appUser, requestTask);

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(CcaBpmnProcessConstants.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_OUTCOME, PerformanceDataFacilityDigitalFormOutcome.CANCELLED));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CANCEL_APPLICATION);
    }
}
