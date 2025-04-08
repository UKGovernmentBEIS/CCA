package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class PerformanceDataSpreadsheetGenerateMarkAsFailedHandler implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		TargetUnitAccountReport accountReport = (TargetUnitAccountReport) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);

		accountReport.setSucceeded(false);
		execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
	}
}