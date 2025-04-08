package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingService {

	private final RequestService requestService;

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void doProcess(String requestId, AccountUploadReport accountReport)
			throws PerformanceAccountTemplateProcessingException {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceAccountTemplateProcessingRequestMetadata metadata = (PerformanceAccountTemplateProcessingRequestMetadata) request
				.getMetadata();
		
		// do processing

		// save business object

		// create request action history
		
		// mock processing and potential errors
//		if(accountReport.getAccountId() % 2 != 0) {
//			throw new PerformanceAccountTemplateProcessingException(List.of("some error"));
//		}
	}
}
