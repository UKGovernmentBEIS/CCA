package uk.gov.cca.api.workflow.bpmn.handler.subsistencefees;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunGenerateReportService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunGenerateReportHandler implements JavaDelegate {

	private final SubsistenceFeesRunGenerateReportService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.generateReport((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
