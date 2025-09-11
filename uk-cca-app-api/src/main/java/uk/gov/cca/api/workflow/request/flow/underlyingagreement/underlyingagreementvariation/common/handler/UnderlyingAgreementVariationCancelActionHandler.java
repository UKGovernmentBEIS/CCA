package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewOutcome;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationOutcome;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCancelActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        final String roleType = appUser.getRoleType();
        Map<String, Object> bpmnProcessConstants = Stream.of(
                        new AbstractMap.SimpleEntry<>(CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_OUTCOME, UnderlyingAgreementVariationOutcome.CANCELLED),
                        new AbstractMap.SimpleEntry<>(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, roleType))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // If it is the review request task, then we need to set the reviewOutcome also
        if (CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW.equals(requestTask.getType().getCode())) {
            bpmnProcessConstants.put(BpmnProcessConstants.REVIEW_OUTCOME, CcaReviewOutcome.CANCELLED);
        }

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(), bpmnProcessConstants);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_CANCEL_APPLICATION);
    }
}
