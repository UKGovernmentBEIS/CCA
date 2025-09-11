package uk.gov.cca.api.workflow.request.application.userdeleted;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator.CcaRegulatorRequestTaskAssignmentService;
import uk.gov.netz.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private RegulatorAuthorityDeletionEventListener listener;

    @Mock
    private CcaRegulatorRequestTaskAssignmentService ccaRegulatorRequestTaskAssignmentService;

    @Test
    void onSectorUserDisabledEvent() {
        String userId = "user";
        RegulatorAuthorityDeletionEvent event = RegulatorAuthorityDeletionEvent.builder().userId(userId).build();
        listener.onRegulatorUserDeletedEvent(event);
        verify(ccaRegulatorRequestTaskAssignmentService, times(1)).assignTasksToSiteContactOrRelease(event.getUserId());
    }

}
