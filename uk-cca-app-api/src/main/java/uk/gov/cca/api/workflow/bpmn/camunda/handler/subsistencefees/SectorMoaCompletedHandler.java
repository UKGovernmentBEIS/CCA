package uk.gov.cca.api.workflow.bpmn.camunda.handler.subsistencefees;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SectorMoaCompletedHandler implements JavaDelegate {

	private final MoaCompletedService moaCompletedService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String sectorMoaRequestId = (String) execution.getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_ID);
		final Long sectorId = (Long) execution.getVariable(CcaBpmnProcessConstants.SECTOR_ID);
		final Boolean sectorMoaSucceeded = (Boolean) execution.getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED);
		final List<String> sectorMoaErrors = (List<String>) execution.getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_ERRORS);
		
		moaCompletedService.completed(requestId, sectorId, MoaType.SECTOR_MOA, sectorMoaRequestId, sectorMoaSucceeded, sectorMoaErrors, false);
		
		// Increment completed number var
		final Integer numberOfSectorsCompleted = (Integer) execution.getVariable(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED);
		execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED, numberOfSectorsCompleted + 1);
	}
}
