package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingTriggerHandler implements JavaDelegate {
	
	private final PerformanceAccountTemplateProcessingCreateRequestService service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);

		@SuppressWarnings("unchecked")
		final Map<Long, AccountUploadReport> accountsReports = (Map<Long, AccountUploadReport>) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ACCOUNT_REPORTS);

		service.createRequest(accountsReports.get(accountId), requestId, requestBusinessKey);
	}

}
