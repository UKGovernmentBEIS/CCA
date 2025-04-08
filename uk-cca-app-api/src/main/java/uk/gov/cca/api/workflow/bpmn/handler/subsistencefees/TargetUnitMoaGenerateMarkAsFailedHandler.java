package uk.gov.cca.api.workflow.bpmn.handler.subsistencefees;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class TargetUnitMoaGenerateMarkAsFailedHandler implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.setVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_SUCCEEDED, Boolean.FALSE);
		execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
	}
}
