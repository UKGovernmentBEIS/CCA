package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.cca2termination.run.service.Cca2TerminationRunRequestService;

@Service
@RequiredArgsConstructor
public class Cca2TerminationRunTerminateCca3MigrationAccountProcessingRequestsHandlerFlowable implements JavaDelegate {

private final Cca2TerminationRunRequestService service;
	
	@Override
    public void execute(DelegateExecution execution) {
		service.terminateCca3MigrationAccountProcessingRequests();
    }
}
