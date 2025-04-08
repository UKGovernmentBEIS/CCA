package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataSpreadsheetDoGenerateService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetGenerateHandler implements JavaDelegate {

	private final PerformanceDataSpreadsheetDoGenerateService performanceDataSpreadsheetDoGenerateService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		TargetUnitAccountReport accountReport = (TargetUnitAccountReport) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
		
		try {
			final FileInfoDTO reportFileInfo = performanceDataSpreadsheetDoGenerateService
					.doGenerate(requestId, accountReport.getAccountId());
			accountReport.setFileInfo(reportFileInfo);
		} catch (BpmnExecutionException e) {
			accountReport.getErrors().addAll(e.getErrors());
			throw new BpmnError("PerformanceDataSpreadsheetGenerateHandler", e);
		}
	}
}