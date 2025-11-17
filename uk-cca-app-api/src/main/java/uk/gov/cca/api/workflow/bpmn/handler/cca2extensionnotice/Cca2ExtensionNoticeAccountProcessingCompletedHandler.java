package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.run.service.Cca2ExtensionNoticeRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingCompletedHandler implements JavaDelegate {

    private final Cca2ExtensionNoticeRunService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final Cca2ExtensionNoticeAccountState accountState = (Cca2ExtensionNoticeAccountState) execution
                .getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE);

        service.accountProcessingCompleted(requestId, accountId, accountState);

        // Increment completed number var
        final Integer numberOfAccountsCompleted = (Integer) execution
                .getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED);
        execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
    }
}
