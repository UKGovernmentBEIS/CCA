package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.service.Cca2ExtensionNoticeCreateRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeRunInitiateCreateHandler implements JavaDelegate {

    private final Cca2ExtensionNoticeCreateRunService service;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception {
        List<String> providedAccounts = execution.getProcessInstance().hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)
                ? (List<String>) execution.getProcessInstance().getVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)
                : List.of();

        service.createRun(new HashSet<>(providedAccounts));
    }
}
