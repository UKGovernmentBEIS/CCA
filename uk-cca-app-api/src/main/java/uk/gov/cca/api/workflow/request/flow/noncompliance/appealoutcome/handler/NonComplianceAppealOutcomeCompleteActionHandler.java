package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.service.NonComplianceAppealOutcomeService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.transform.NonComplianceAppealOutcomeMapper;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.validation.NonComplianceAppealOutcomeSubmitValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NonComplianceAppealOutcomeCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final NonComplianceAppealOutcomeService nonComplianceAppealOutcomeService;
    private final NonComplianceAppealOutcomeSubmitValidator validator;

    private static final NonComplianceAppealOutcomeMapper NON_COMPLIANCE_APPEAL_OUTCOME_MAPPER = Mappers.getMapper(NonComplianceAppealOutcomeMapper.class);

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        //Validate
        validator.validate((NonComplianceAppealOutcomeSubmitRequestTaskPayload) requestTask.getPayload());

        // Update Request
        nonComplianceAppealOutcomeService.complete(requestTask);

        // Add submit action request
        addAppealOutcomeSubmittedAction(requestTask.getRequest());

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId()));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_APPEAL_OUTCOME_COMPLETE_APPLICATION);
    }

    private void addAppealOutcomeSubmittedAction(Request request) {
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        requestService.addActionToRequest(request,
                NON_COMPLIANCE_APPEAL_OUTCOME_MAPPER.toNonComplianceAppealOutcomeSubmittedRequestActionPayload(requestPayload),
                CcaRequestActionType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMITTED,
                request.getPayload().getRegulatorAssignee());
    }
}
