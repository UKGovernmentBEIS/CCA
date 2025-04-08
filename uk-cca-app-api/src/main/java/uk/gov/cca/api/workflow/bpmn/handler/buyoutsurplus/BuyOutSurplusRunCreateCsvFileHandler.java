package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class BuyOutSurplusRunCreateCsvFileHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        // TODO
    }
}
