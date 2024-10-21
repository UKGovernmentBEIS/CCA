package uk.gov.cca.api.workflow.request.application.userdisabled;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserStatusDisabledEvent;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.sectoruser.SectorUserRequestTaskAssignmentService;

@Component("workflowSectorAuthorityDisableEventListener")
public class SectorUserStatusDisabledEventListener {

    private final SectorUserRequestTaskAssignmentService sectorRequestTaskAssignmentService;

    public SectorUserStatusDisabledEventListener(final SectorUserRequestTaskAssignmentService sectorRequestTaskAssignmentService) {
        this.sectorRequestTaskAssignmentService = sectorRequestTaskAssignmentService;
    }

    @Order(2)
    @EventListener({SectorUserStatusDisabledEvent.class})
    public void onSectorUserDisabledEvent(SectorUserStatusDisabledEvent event) {
        this.sectorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(event.getUserId(), event.getSectorAssociationId());
    }
}
