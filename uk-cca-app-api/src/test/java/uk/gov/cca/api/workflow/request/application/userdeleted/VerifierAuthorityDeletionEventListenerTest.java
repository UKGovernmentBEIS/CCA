package uk.gov.cca.api.workflow.request.application.userdeleted;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.cca.api.workflow.request.application.userdeleted.VerifierAuthorityDeletionEventListener;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.verifier.VerifierRequestTaskAssignmentService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityDeletionEventListenerTest {

    @InjectMocks
    private VerifierAuthorityDeletionEventListener listener;

    @Mock
    private VerifierRequestTaskAssignmentService verifierRequestTaskAssignmentService;


    @Test
    void onVerifierUserDeletedEvent() {
        final String userId = "user";
        VerifierAuthorityDeletionEvent event = VerifierAuthorityDeletionEvent.builder().userId(userId).build();

        listener.onVerifierUserDeletedEvent(event);

        verify(verifierRequestTaskAssignmentService, times(1)).assignTasksOfDeletedVerifierToVbSiteContactOrRelease(userId);
    }
}
