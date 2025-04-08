package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class PerformanceDataProcessingMarkAsCompletedHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
