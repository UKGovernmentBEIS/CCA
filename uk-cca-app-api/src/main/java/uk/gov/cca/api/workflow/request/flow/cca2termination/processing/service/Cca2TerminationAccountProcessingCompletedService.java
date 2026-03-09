package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class Cca2TerminationAccountProcessingCompletedService {

	private final RequestService requestService;
	
	@Transactional
	public void completed(
			String parentRequestId, String requestId, Long accountId, Cca2TerminationAccountState accountState) {
		// Lock parent request
		final Request request = requestService.findRequestByIdForUpdate(parentRequestId);

		// Update account state in parent request metadata
		Cca2TerminationRunRequestMetadata metadata = (Cca2TerminationRunRequestMetadata) request.getMetadata();
		metadata.getCca2TerminationAccountStates().put(accountId, accountState);
		
	}
}
