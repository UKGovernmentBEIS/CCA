package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

@Service
public class FacilityCertificationRunInitiateHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // TODO remove BPMN
        boolean canBeInitiated = false;
        execution.setVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_INITIATE_FLAG, canBeInitiated);
    }
}
