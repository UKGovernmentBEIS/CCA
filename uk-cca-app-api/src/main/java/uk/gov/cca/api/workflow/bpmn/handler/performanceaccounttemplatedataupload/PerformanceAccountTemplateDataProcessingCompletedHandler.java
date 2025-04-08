package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateDataProcessingQueryService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataProcessingCompletedHandler implements JavaDelegate {

	private final PerformanceAccountTemplateDataProcessingQueryService performanceAccountTemplateDataProcessingQueryService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

		// Set final account reports
		final Map<Long, AccountUploadReport> accountReports = performanceAccountTemplateDataProcessingQueryService
				.getAccountReports(requestId);
		execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ACCOUNT_REPORTS,
				accountReports);
	}

}
