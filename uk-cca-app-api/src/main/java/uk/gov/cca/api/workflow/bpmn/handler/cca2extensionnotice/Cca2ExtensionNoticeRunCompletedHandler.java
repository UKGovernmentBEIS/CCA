package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.run.service.Cca2ExtensionNoticeRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeRunCompletedHandler implements JavaDelegate {

    private final Cca2ExtensionNoticeRunService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.complete(requestId);
    }
}
