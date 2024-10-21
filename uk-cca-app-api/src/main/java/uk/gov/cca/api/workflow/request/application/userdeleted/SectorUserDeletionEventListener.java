package uk.gov.cca.api.workflow.request.application.userdeleted;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserDeletionEvent;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.sectoruser.SectorUserRequestTaskAssignmentService;

@Component("workflowSectorAuthorityDeletionEventListener")
public class SectorUserDeletionEventListener {

    private final SectorUserRequestTaskAssignmentService sectorRequestTaskAssignmentService;

    public SectorUserDeletionEventListener(final SectorUserRequestTaskAssignmentService sectorRequestTaskAssignmentService) {
        this.sectorRequestTaskAssignmentService = sectorRequestTaskAssignmentService;
    }

    @Order(2)
    @EventListener({SectorUserDeletionEvent.class})
    public void onSectorUserDeletedEvent(SectorUserDeletionEvent event) {
        this.sectorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(event.getUserId(), event.getSectorAssociationId());
    }
}