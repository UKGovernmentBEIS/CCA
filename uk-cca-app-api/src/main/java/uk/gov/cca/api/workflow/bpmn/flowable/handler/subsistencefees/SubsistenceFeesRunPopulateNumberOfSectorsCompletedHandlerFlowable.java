package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunPopulateNumberOfSectorsCompletedHandlerFlowable implements JavaDelegate {
	
	private final SubsistenceFeesRunRequestService service;

    @Override
    public void execute(DelegateExecution execution) {
    	final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
    	final long sectorsCompletedNumber = service.getNumberOfSectorsCompleted(requestId);
    	execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED, sectorsCompletedNumber);
    }
}
