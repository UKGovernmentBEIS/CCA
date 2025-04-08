package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingMarkAsFailedService {

	@Transactional
	public void markAsFailed(AccountUploadReport accountReport) {
		accountReport.setSucceeded(false);
	}
}
