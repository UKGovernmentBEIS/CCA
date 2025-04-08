package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingMarkAsFailedService;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingMarkAsFailedHandler implements JavaDelegate {

	private final PerformanceAccountTemplateProcessingMarkAsFailedService service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final AccountUploadReport accountReport = (AccountUploadReport) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT);

		service.markAsFailed(accountReport);
	}

}