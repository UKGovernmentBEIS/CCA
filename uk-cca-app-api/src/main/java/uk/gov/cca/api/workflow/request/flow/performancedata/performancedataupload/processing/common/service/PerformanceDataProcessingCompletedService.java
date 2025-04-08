package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.PerformanceDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PerformanceDataProcessingCompletedService {

private final RequestService requestService;
	
	@Transactional
	public void completed(String requestId, Long accountId, TargetUnitAccountUploadReport accountReport) {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceDataProcessingRequestPayload requestPayload =
				(PerformanceDataProcessingRequestPayload) request.getPayload();

		requestPayload.getAccountReports().put(accountId, accountReport);
	}
}
