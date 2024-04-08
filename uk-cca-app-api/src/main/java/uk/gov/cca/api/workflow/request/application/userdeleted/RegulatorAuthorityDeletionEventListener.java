package uk.gov.cca.api.workflow.request.application.userdeleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator.RegulatorRequestTaskAssignmentService;

@RequiredArgsConstructor
@Component(value = "workflowRegulatorAuthorityDeletionEventListener")
public class RegulatorAuthorityDeletionEventListener {

    private final RegulatorRequestTaskAssignmentService regulatorRequestTaskAssignmentService;

    @Order(2)
    @EventListener(RegulatorAuthorityDeletionEvent.class)
    public void onRegulatorUserDeletedEvent(RegulatorAuthorityDeletionEvent event) {
        regulatorRequestTaskAssignmentService.assignTasksOfDeletedRegulatorToCaSiteContactOrRelease(event.getUserId());
    }
}
