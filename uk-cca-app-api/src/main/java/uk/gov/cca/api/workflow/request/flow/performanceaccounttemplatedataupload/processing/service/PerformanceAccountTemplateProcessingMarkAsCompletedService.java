package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;

@Service
public class PerformanceAccountTemplateProcessingMarkAsCompletedService {
	
	@Transactional
	public void markAsCompleted(AccountUploadReport accountReport) {
		accountReport.setSucceeded(true);
	}

}
