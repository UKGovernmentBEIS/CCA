package uk.gov.cca.api.workflow.request.application.userdeleted;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.regulator.event.RegulatorUserStatusDisabledEvent;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator.RegulatorRequestTaskAssignmentService;

@ExtendWith(MockitoExtension.class)
class RegulatorUserStatusDisabledEventListenerTest {

    @InjectMocks
    private RegulatorUserStatusDisabledEventListener listener;

    @Mock
    private RegulatorRequestTaskAssignmentService regulatorRequestTaskAssignmentService;

    @Test
    void onRegulatorUserDeletedEvent() {
        final String userId = "user";
        RegulatorUserStatusDisabledEvent event = new RegulatorUserStatusDisabledEvent(userId);

        listener.onRegulatorUserDeletedEvent(event);

        verify(regulatorRequestTaskAssignmentService, times(1)).assignTasksOfDeletedRegulatorToCaSiteContactOrRelease(userId);
    }
}
