package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementOutcome;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.service.UnderlyingAgreementSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementSubmitActionHandler 
		implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

	private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementSubmitService underlyingAgreementSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, String requestTaskActionType, AppUser appUser, 
    		RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

      // Submit underlying agreement
      underlyingAgreementSubmitService.submitUnderlyingAgreement(requestTask, appUser);
      		
      // Set request's submission date
      requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

      // Complete task
      workflowService.completeTask(requestTask.getProcessTaskId(), 
    		  Map.of(CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_OUTCOME, UnderlyingAgreementOutcome.SUBMITTED));
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_SUBMIT_APPLICATION);
    }
}
