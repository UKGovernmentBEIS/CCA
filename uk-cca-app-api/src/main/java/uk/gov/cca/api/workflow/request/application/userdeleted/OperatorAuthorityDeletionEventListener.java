package uk.gov.cca.api.workflow.request.application.userdeleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.operator.OperatorRequestTaskAssignmentService;

@RequiredArgsConstructor
@Component(value =  "workflowOperatorAuthorityDeletionEventListener")
public class OperatorAuthorityDeletionEventListener {

    private final OperatorRequestTaskAssignmentService operatorRequestTaskAssignmentService;

    @Order(2)
    @EventListener(OperatorAuthorityDeletionEvent.class)
    public void onOperatorUserDeletionEvent(OperatorAuthorityDeletionEvent deletionEvent) {
        operatorRequestTaskAssignmentService
            .assignUserTasksToAccountPrimaryContactOrRelease(deletionEvent.getUserId(), deletionEvent.getAccountId());
    }
}
