package uk.gov.cca.api.workflow.request.application.userdisabled;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator.CcaRegulatorRequestTaskAssignmentService;
import uk.gov.netz.api.authorization.regulator.event.RegulatorUserStatusDisabledEvent;

@RequiredArgsConstructor
@Component("ccaWorkflowRegulatorUserStatusDisabledEventListener")
public class RegulatorUserStatusDisabledEventListener {

    private final CcaRegulatorRequestTaskAssignmentService ccaRegulatorRequestTaskAssignmentService;

    @Order(2)
    @EventListener(RegulatorUserStatusDisabledEvent.class)
    public void onRegulatorUserStatusDisabledEvent(RegulatorUserStatusDisabledEvent event) {
        ccaRegulatorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(event.getUserId());
    }
}
