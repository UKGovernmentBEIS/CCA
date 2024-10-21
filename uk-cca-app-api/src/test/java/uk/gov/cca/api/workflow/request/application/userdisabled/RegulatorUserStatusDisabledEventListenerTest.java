package uk.gov.cca.api.workflow.request.application.userdisabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator.CcaRegulatorRequestTaskAssignmentService;
import uk.gov.netz.api.authorization.regulator.event.RegulatorUserStatusDisabledEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegulatorUserStatusDisabledEventListenerTest {

    @InjectMocks
    private RegulatorUserStatusDisabledEventListener listener;

    @Mock
    private CcaRegulatorRequestTaskAssignmentService ccaRegulatorRequestTaskAssignmentService;

    @Test
    void onRegulatorUserStatusDisabledEvent() {
        String userId = "user";
        RegulatorUserStatusDisabledEvent event = new RegulatorUserStatusDisabledEvent(userId);
        listener.onRegulatorUserStatusDisabledEvent(event);
        verify(ccaRegulatorRequestTaskAssignmentService, times(1)).assignTasksToSiteContactOrRelease(event.getUserId());
    }

}
