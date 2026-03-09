package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

@Service
public class SectorMoaGenerateMarkAsCompletedHandlerFlowable implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		execution.setVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED, Boolean.TRUE);
		execution.setVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_ERRORS, List.of());
	}
}
