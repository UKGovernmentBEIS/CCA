package uk.gov.cca.api.workflow.request.application.userdeleted;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserDeletionEvent;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.sectoruser.SectorUserRequestTaskAssignmentService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SectorUserDeletionEventListenerTest {

    @InjectMocks
    private SectorUserDeletionEventListener listener;

    @Mock
    private SectorUserRequestTaskAssignmentService sectorRequestTaskAssignmentService;


    @Test
    void onSectorUserDeletedEvent() {
        String userId = "user";

        SectorUserDeletionEvent event = SectorUserDeletionEvent.builder().userId(userId).sectorAssociationId(1L).build();

        listener.onSectorUserDeletedEvent(event);

        verify(sectorRequestTaskAssignmentService, times(1)).assignTasksToSiteContactOrRelease(userId, 1L);
    }


}
