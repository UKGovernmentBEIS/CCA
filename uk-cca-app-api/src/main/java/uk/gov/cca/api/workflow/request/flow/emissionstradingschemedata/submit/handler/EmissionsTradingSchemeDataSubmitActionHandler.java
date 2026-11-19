package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataOutcome;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.service.EmissionsTradingSchemeDataSubmitService;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.validation.EmissionsTradingSchemeDataSubmitValidator;
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
public class EmissionsTradingSchemeDataSubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final EmissionsTradingSchemeDataSubmitValidator validator;
    private final EmissionsTradingSchemeDataSubmitService emissionsTradingSchemeDataSubmitService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final LocalDateTime submissionDate = LocalDateTime.now();
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate task
        validator.validate(requestTask);

        // Submit action
        emissionsTradingSchemeDataSubmitService.submit(appUser, requestTask, submissionDate);

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                CcaBpmnProcessConstants.EMISSIONS_TRADING_SCHEME_DATA_OUTCOME, EmissionsTradingSchemeDataOutcome.COMPLETED));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_APPLICATION);
    }
}
