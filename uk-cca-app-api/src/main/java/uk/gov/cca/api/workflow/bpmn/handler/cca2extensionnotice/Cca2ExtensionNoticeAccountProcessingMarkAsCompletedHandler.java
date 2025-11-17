package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class Cca2ExtensionNoticeAccountProcessingMarkAsCompletedHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Cca2ExtensionNoticeAccountState accountState = (Cca2ExtensionNoticeAccountState) execution
                .getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE);

        if(!accountState.getErrors().isEmpty()) {
            // Set succeeded to false and close request
            accountState.setSucceeded(false);
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
        else {
            // No error occurred
            accountState.setSucceeded(true);
        }
    }
}
