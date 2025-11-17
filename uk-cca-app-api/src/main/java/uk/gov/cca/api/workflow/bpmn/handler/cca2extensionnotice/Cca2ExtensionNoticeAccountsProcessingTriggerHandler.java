package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service.Cca2ExtensionNoticeAccountProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountsProcessingTriggerHandler implements JavaDelegate {

    private final Cca2ExtensionNoticeAccountProcessingCreateRequestService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);

        // Create request
        service.createRequest(accountId, requestId, requestBusinessKey);
    }
}
