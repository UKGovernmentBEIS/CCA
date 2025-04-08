package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingException;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingHandler implements JavaDelegate {
	
	private final PerformanceAccountTemplateProcessingService service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final AccountUploadReport accountReport = (AccountUploadReport) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT);
		
		try {
			service.doProcess(requestId, accountReport);
		} catch (PerformanceAccountTemplateProcessingException e) {
			accountReport.getErrors().addAll(e.getErrors());
			throw new BpmnError("PerformanceAccountTemplateProcessingException", e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			accountReport.getErrors().add("Internal error occurred");
			throw new BpmnError("PerformanceAccountTemplateProcessingHandler", e);
		}
	}

}
