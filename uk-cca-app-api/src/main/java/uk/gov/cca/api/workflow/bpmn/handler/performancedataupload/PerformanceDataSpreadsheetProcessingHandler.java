package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service.PerformanceDataSpreadsheetProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetProcessingHandler implements JavaDelegate {

	private final PerformanceDataSpreadsheetProcessingService performanceDataSpreadsheetProcessingService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String errorMessage = PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.PROCESS_EXCEL_FAILED.getMessage();
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final TargetUnitAccountUploadReport accountReport = (TargetUnitAccountUploadReport) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);

		try {
			performanceDataSpreadsheetProcessingService.doProcess(requestId, accountReport);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			accountReport.getErrors().add(errorMessage);
		}
	}

}
