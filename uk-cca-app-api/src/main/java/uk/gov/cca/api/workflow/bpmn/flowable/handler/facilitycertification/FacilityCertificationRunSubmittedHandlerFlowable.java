package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.facilitycertification.run.service.FacilityCertificationRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunSubmittedHandlerFlowable implements JavaDelegate {

    private final FacilityCertificationRunService facilityCertificationRunService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        facilityCertificationRunService.submit(requestId);
    }
}
