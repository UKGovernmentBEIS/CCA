package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataProcessingQueryService {

private final RequestService requestService;
	
	@Transactional
	public Map<Long, AccountUploadReport> getAccountReports(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceAccountTemplateDataProcessingRequestPayload requestPayload = (PerformanceAccountTemplateDataProcessingRequestPayload) request
				.getPayload();
		
		return requestPayload.getAccountFileReports();
	}
	
}
