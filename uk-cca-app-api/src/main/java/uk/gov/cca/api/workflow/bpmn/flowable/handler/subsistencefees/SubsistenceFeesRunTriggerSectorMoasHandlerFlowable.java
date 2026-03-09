package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service.SectorMoaCreateRequestService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunTriggerSectorMoasHandlerFlowable implements JavaDelegate {

	private final SectorMoaCreateRequestService sectorMoaCreateRequestService;
	private final SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
	private final SubsistenceFeesRunRequestService subsistenceFeesRunRequestService;
	private final RequestService requestService;
	
	@Override
	public void execute(DelegateExecution execution) {
		final Long sectorId = (Long) execution.getVariable(CcaBpmnProcessConstants.SECTOR_ID);
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);
		final Request request = requestService.findRequestById(requestId);
		final SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) request.getMetadata();
		// Check if sector is eligible for current run
		if (subsistenceFeesRunQueryService.isSectorEligibleForSubsistenceFeesRun(sectorId, metadata.getChargingYear())) {
			sectorMoaCreateRequestService.createRequest(sectorId, requestId, requestBusinessKey);
		} else {
			subsistenceFeesRunRequestService.updateRequestMetadata(requestId, sectorId);
		}
	}
}
