package uk.gov.cca.api.workflow.bpmn.camunda.handler.facilityaudit;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.FacilityAuditCancelledService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class FacilityAuditCancelledHandler implements JavaDelegate {

    private final FacilityAuditCancelledService facilityAuditCancelledService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        facilityAuditCancelledService.cancel(requestId);
    }

}
