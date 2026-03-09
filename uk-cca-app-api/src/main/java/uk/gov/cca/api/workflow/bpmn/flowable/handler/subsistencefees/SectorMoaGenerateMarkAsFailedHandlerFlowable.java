package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class SectorMoaGenerateMarkAsFailedHandlerFlowable implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		execution.setVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED, Boolean.FALSE);
		execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
	}
}
