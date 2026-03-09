package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaCompletedService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SectorMoaCompletedHandlerFlowable implements JavaDelegate {

	private final RequestService requestService;
	private final MoaCompletedService moaCompletedService;
	
	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Request request = requestService.findRequestById(requestId);
		final SectorMoaRequestMetadata metadata = (SectorMoaRequestMetadata) request.getMetadata();
		final Long sectorId = (Long) execution.getVariable(CcaBpmnProcessConstants.SECTOR_ID);
		final Boolean sectorMoaSucceeded = (Boolean) execution.getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED);
		final List<String> sectorMoaErrors = (List<String>) execution.getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_ERRORS);
		
		moaCompletedService.completed(metadata.getParentRequestId(), sectorId, MoaType.SECTOR_MOA, requestId, sectorMoaSucceeded, sectorMoaErrors, true);
	}
}
