package uk.gov.cca.api.workflow.request.application.userdeleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator.CcaRegulatorRequestTaskAssignmentService;
import uk.gov.netz.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;

@RequiredArgsConstructor
@Component("ccaWorkflowRegulatorAuthorityDeletionEventListener")
public class RegulatorAuthorityDeletionEventListener {

    private final CcaRegulatorRequestTaskAssignmentService ccaRegulatorRequestTaskAssignmentService;

    @Order(2)
    @EventListener(RegulatorAuthorityDeletionEvent.class)
    public void onRegulatorUserDeletedEvent(RegulatorAuthorityDeletionEvent event) {
        ccaRegulatorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(event.getUserId());
    }
}
