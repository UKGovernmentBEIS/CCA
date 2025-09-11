package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationOutcome;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.service.UnderlyingAgreementVariationSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementVariationSubmitService underlyingAgreementVariationSubmitService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Submit underlying agreement variation
         underlyingAgreementVariationSubmitService.submitUnderlyingAgreementVariation(requestTask, appUser);

        // Set request's submission date
        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_OUTCOME, UnderlyingAgreementVariationOutcome.SUBMITTED));
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION);
    }
}
