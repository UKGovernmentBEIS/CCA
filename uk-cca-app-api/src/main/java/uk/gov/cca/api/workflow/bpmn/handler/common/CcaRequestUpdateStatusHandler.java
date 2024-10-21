package uk.gov.cca.api.workflow.bpmn.handler.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class CcaRequestUpdateStatusHandler implements JavaDelegate {

    private final RequestService requestService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        String status = (String) execution.getVariable(BpmnProcessConstants.REQUEST_STATUS);
        try {
            requestService.updateRequestStatus(requestId, status);
        } catch (Exception e) {
            log.error(String.format("CcaRequestUpdateStatusHandler error for requestId %s", requestId), e);
            throw new BpmnError("CcaRequestUpdateStatusHandler", e);
        }
    }
}
