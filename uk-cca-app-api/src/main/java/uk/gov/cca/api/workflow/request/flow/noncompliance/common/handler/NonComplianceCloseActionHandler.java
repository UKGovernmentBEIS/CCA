package uk.gov.cca.api.workflow.request.flow.noncompliance.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskClosable;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceCloseService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceCloseApplicationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class NonComplianceCloseActionHandler implements RequestTaskActionHandler<NonComplianceCloseRequestTaskActionPayload> {

    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;
    private final NonComplianceCloseService nonComplianceCloseService;
    private final NonComplianceCloseApplicationValidator validator;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, NonComplianceCloseRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Update Request Task
        nonComplianceCloseService.applyCloseAction(payload, requestTask);

        // Validate data
        validator.validate((NonComplianceRequestTaskClosable) requestTask.getPayload());

        // Update Request
        nonComplianceCloseService.submitCloseAction(requestTask);

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.CLOSED));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION);
    }
}
