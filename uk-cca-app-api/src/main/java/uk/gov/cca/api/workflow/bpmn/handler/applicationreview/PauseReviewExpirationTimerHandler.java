package uk.gov.cca.api.workflow.bpmn.handler.applicationreview;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestExpirationType;

@Service
@RequiredArgsConstructor
public class PauseReviewExpirationTimerHandler implements JavaDelegate {

    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        requestTaskTimeManagementService.pauseTasks((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID),
                RequestExpirationType.APPLICATION_REVIEW);
    }
}
