package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service.PerformanceDataProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataProcessingTriggerHandler implements JavaDelegate {
	
	private final PerformanceDataProcessingCreateRequestService performanceDataProcessingCreateRequestService;

	@Override
	@SuppressWarnings("unchecked")
	public void execute(DelegateExecution execution) throws Exception {
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final Map<Long, TargetUnitAccountUploadReport> accountReports = (Map<Long, TargetUnitAccountUploadReport>) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS);
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);

		performanceDataProcessingCreateRequestService
				.createRequest(accountReports.get(accountId), requestId, requestBusinessKey);
	}
}
