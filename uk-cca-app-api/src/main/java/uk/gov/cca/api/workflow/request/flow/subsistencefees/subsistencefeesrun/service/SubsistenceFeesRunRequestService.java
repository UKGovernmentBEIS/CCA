package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunRequestService {
	
	private final RequestService requestService;
	
	@Transactional
	public void updateRequestMetadata(String requestId, Long sectorId) {
		final Request request = requestService.findRequestByIdForUpdate(requestId);
		final SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) request.getMetadata();
		
		// Increment skipped sectors count and remove report
		metadata.incrementSkippedSectors();
		metadata.getSectorsReports().remove(sectorId);
	}

	public long getNumberOfSectorsCompleted(String requestId) {
		final Request batchRequest = requestService.findRequestById(requestId);
		final SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) batchRequest.getMetadata();
		return metadata.getSectorsReports()
				.values().stream()
				.filter(report -> report.getSucceeded() != null)
				.count()
				+ metadata.getSkippedSectors();
	}
	
	public long getNumberOfAccountsCompleted(String requestId) {
		final Request batchRequest = requestService.findRequestById(requestId);
		final SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) batchRequest.getMetadata();
		return metadata.getAccountsReports()
				.values().stream()
				.filter(report -> report.getSucceeded() != null)
				.count();
	}
}
