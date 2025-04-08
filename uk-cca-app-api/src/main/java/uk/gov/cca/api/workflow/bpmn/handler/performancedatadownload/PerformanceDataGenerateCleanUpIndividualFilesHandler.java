package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataGenerateCleanUpIndividualFilesService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataGenerateCleanUpIndividualFilesHandler implements JavaDelegate {

	private final PerformanceDataGenerateCleanUpIndividualFilesService performanceDataGenerateCleanUpIndividualFilesService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		performanceDataGenerateCleanUpIndividualFilesService.cleanupIndividualAccountReports(requestId);
	}
}
