package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class FacilityCertificationRunInitiateCreateHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // TODO remove BPMN
    }
}
