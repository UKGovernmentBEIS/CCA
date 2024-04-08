package uk.gov.cca.api.workflow.request.application.userdeleted;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.cca.api.workflow.request.application.userdeleted.RegulatorAuthorityDeletionEventListener;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator.RegulatorRequestTaskAssignmentService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private RegulatorAuthorityDeletionEventListener listener;

    @Mock
    private RegulatorRequestTaskAssignmentService regulatorRequestTaskAssignmentService;


    @Test
    void onRegulatorUserDeletedEvent() {
        final String userId = "user";
        RegulatorAuthorityDeletionEvent event = RegulatorAuthorityDeletionEvent.builder().userId(userId).build();

        listener.onRegulatorUserDeletedEvent(event);

        verify(regulatorRequestTaskAssignmentService, times(1)).assignTasksOfDeletedRegulatorToCaSiteContactOrRelease(userId);
    }
}
