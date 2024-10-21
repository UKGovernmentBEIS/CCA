package uk.gov.cca.api.workflow.bpmn.handler.targetunitaccountcreation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountCreationRollbackService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountCreationRollbackActionHandler implements JavaDelegate {

    private final TargetUnitAccountCreationRollbackService targetUnitAccountCreationRollbackService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        targetUnitAccountCreationRollbackService.rollback(requestId);
    }
}
