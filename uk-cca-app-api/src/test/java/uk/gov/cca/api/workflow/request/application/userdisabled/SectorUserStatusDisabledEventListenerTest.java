package uk.gov.cca.api.workflow.request.application.userdisabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserStatusDisabledEvent;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.sectoruser.SectorUserRequestTaskAssignmentService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SectorUserStatusDisabledEventListenerTest {

    @InjectMocks
    private SectorUserStatusDisabledEventListener listener;

    @Mock
    private SectorUserRequestTaskAssignmentService sectorRequestTaskAssignmentService;


    @Test
    void onSectorUserDisabledEvent() {
        String userId = "user";
        SectorUserStatusDisabledEvent event = SectorUserStatusDisabledEvent.builder().userId(userId).sectorAssociationId(1L).build();
        listener.onSectorUserDisabledEvent(event);
        verify(sectorRequestTaskAssignmentService, times(1)).assignTasksToSiteContactOrRelease(event.getUserId(), event.getSectorAssociationId());
    }

}
