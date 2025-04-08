package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingCompletedService {

	private final RequestService requestService;

	@Transactional
	public void completed(String requestId, Long accountId, AccountUploadReport accountReport) {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceAccountTemplateDataProcessingRequestPayload requestPayload = (PerformanceAccountTemplateDataProcessingRequestPayload) request
				.getPayload();

		requestPayload.getAccountFileReports().put(accountId, accountReport);
	}
}
