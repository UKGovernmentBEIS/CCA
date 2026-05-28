package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestCustomContext;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.service.NoticeOfIntentSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.validation.NoticeOfIntentSubmitRequestPeerReviewValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class NonComplianceNoticeOfIntentSubmitRequestPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final NoticeOfIntentSubmitService noticeOfIntentSubmitService;
    private final NoticeOfIntentSubmitRequestPeerReviewValidator validator;
    private final RequestService requestService;

    @Override
    public RequestTaskPayload process(Long requestTaskId,
                                      String requestTaskActionType,
                                      AppUser appUser,
                                      PeerReviewRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();

        // validate
        validator.validate(requestTask, actionPayload, appUser);

        final String regulatorReviewer = appUser.getUserId();
        final String peerReviewer = actionPayload.getPeerReviewer();
        noticeOfIntentSubmitService.requestPeerReview(requestTask, peerReviewer, regulatorReviewer);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED,
                regulatorReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.PEER_REVIEW_REQUIRED,
                        BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, CcaRequestCustomContext.NON_COMPLIANCE_NOTICE_OF_INTENT));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW);
    }
}
