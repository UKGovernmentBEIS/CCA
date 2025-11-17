package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.validation.Cca2ExtensionNoticeViolation;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service.Cca2ExtensionNoticeAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingHandler implements JavaDelegate {

    private final Cca2ExtensionNoticeAccountProcessingService cca2ExtensionNoticeAccountProcessingService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Cca2ExtensionNoticeAccountState accountState = (Cca2ExtensionNoticeAccountState) execution
                .getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE);

        try {
            cca2ExtensionNoticeAccountProcessingService.doProcess(requestId, accountState);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String errorMessage = String.format("%s: %s",
                    Cca2ExtensionNoticeViolation.Cca2ExtensionNoticeViolationMessage.PROCESS_FAILED.getMessage(),
                    e.getMessage());
            accountState.getErrors().add(errorMessage);
        }
    }
}
