package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service.TargetUnitMoaGenerateService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class TargetUnitMoaGenerateHandlerFlowable implements JavaDelegate {

	private final TargetUnitMoaGenerateService targetUnitMoaGenerateService;
	
	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

		try {
			targetUnitMoaGenerateService.generateMoa(requestId);
		} catch (BpmnExecutionException e) {
			execution.setVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ERRORS, e.getErrors());
			log.error(String.format("TargetUnitMoaGenerateHandler error for requestId %s", requestId), e);
			throw new BpmnError("TargetUnitMoaGenerateHandler", e.getMessage());
		}
	}
}
