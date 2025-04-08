package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.PerformanceDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataProcessingService {

	private final RequestService requestService;
	
	@Transactional
	public Map<Long, TargetUnitAccountUploadReport> getAccountReports(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceDataProcessingRequestPayload requestPayload = (PerformanceDataProcessingRequestPayload) request
				.getPayload();
		
		return requestPayload.getAccountReports();
	}
}
