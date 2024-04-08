package uk.gov.cca.api.workflow.request.application.userdeleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.verifier.VerifierRequestTaskAssignmentService;

@RequiredArgsConstructor
@Component(value = "workflowVerifierAuthorityDeletionEventListener")
public class VerifierAuthorityDeletionEventListener {

    private final VerifierRequestTaskAssignmentService verifierRequestTaskAssignmentService;

    @Order(2)
    @EventListener(VerifierAuthorityDeletionEvent.class)
    public void onVerifierUserDeletedEvent(VerifierAuthorityDeletionEvent event) {
        verifierRequestTaskAssignmentService.assignTasksOfDeletedVerifierToVbSiteContactOrRelease(event.getUserId());
    }

}
