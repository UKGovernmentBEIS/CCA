package uk.gov.cca.api.workflow.bpmn.camunda.handler.subsistencefees;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

@Service
public class SectorMoaGenerateMarkAsCompletedHandler implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.setVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED, Boolean.TRUE);
		execution.setVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_ERRORS, List.of());
	}
}
