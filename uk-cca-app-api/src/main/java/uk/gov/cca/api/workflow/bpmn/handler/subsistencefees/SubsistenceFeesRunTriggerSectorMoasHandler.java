package uk.gov.cca.api.workflow.bpmn.handler.subsistencefees;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service.SectorMoaCreateRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunTriggerSectorMoasHandler implements JavaDelegate {

	private final SectorMoaCreateRequestService sectorMoaCreateRequestService;
	private final SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
	private final RequestService requestService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Long sectorId = (Long) execution.getVariable(CcaBpmnProcessConstants.SECTOR_ID);
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);
		final Request request = requestService.findRequestById(requestId);
		final SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) request.getMetadata();
		// Check if sector is eligible for current run
		if (subsistenceFeesRunQueryService.isSectorEligibleForSubsistenceFeesRun(sectorId, metadata.getChargingYear())) {
			sectorMoaCreateRequestService.createRequest(sectorId, requestId, requestBusinessKey);
		} else {
			// Increase number of sectors completed
			final Integer numberOfSectorsCompleted = (Integer) execution.getVariable(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED);
			execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED, numberOfSectorsCompleted + 1);
			// Remove report for this sector
			metadata.getSectorsReports().remove(sectorId);
		}
	}
}
