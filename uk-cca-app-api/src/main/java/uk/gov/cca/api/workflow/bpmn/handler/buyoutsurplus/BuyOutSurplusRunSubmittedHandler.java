package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service.BuyOutSurplusRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusRunSubmittedHandler implements JavaDelegate {

    private final BuyOutSurplusRunService buyOutSurplusRunService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        buyOutSurplusRunService.submit(requestId);
    }
}
