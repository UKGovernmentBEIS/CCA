package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetGenerateCompletedService {

	private final RequestService requestService;
	
	@Transactional
	public void completed(String requestId, Long accountId, TargetUnitAccountReport accountReport) {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceDataGenerateRequestPayload requestPayload =
				(PerformanceDataGenerateRequestPayload) request.getPayload();

		requestPayload.getAccountsReports().put(accountId, accountReport);
	}
}
