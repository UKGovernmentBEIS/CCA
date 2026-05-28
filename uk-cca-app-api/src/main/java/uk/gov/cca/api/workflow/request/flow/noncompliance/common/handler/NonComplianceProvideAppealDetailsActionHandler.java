package uk.gov.cca.api.workflow.request.flow.noncompliance.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceProvideAppealDetailsRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskAppealable;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceAppealDetailsService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceAppealDetailsSubmitValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NonComplianceProvideAppealDetailsActionHandler implements RequestTaskActionHandler<NonComplianceProvideAppealDetailsRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final NonComplianceAppealDetailsService nonComplianceAppealDetailsService;
    private final NonComplianceAppealDetailsSubmitValidator validator;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, NonComplianceProvideAppealDetailsRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NonComplianceRequestTaskAppealable requestTaskPayload =
                (NonComplianceRequestTaskAppealable) requestTask.getPayload();

        // Update Request Task payload
        nonComplianceAppealDetailsService.applyAppealAction(payload, requestTask);

        // Validate
        validator.validate(requestTaskPayload);

        // Update Request
        nonComplianceAppealDetailsService.submitAppealAction(requestTask);

        // Create request action
        Request request = requestTask.getRequest();
        nonComplianceAppealDetailsService.addAppealSubmittedAction(request);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.APPEAL_OUTCOME_REQUIRED));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS);
    }
}
