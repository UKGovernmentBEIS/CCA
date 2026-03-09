package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilityaudit;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.FacilityAuditCancelledService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class FacilityAuditCancelledHandlerFlowable implements JavaDelegate {

    private final FacilityAuditCancelledService facilityAuditCancelledService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        facilityAuditCancelledService.cancel(requestId);
    }

}
