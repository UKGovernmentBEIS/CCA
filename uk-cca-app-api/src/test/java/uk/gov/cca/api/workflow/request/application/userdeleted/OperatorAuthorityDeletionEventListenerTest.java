package uk.gov.cca.api.workflow.request.application.userdeleted;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.cca.api.workflow.request.application.userdeleted.OperatorAuthorityDeletionEventListener;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.operator.OperatorRequestTaskAssignmentService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private OperatorAuthorityDeletionEventListener operatorAuthorityDeletionEventListener;

    @Mock
    private OperatorRequestTaskAssignmentService operatorRequestTaskAssignmentService;

    @Test
    void onOperatorUserDeletionEvent() {
        String userId = "userId";
        Long accountId = 1L;
        OperatorAuthorityDeletionEvent deletionEvent = OperatorAuthorityDeletionEvent.builder()
            .accountId(accountId)
            .userId(userId)
            .build();

        operatorAuthorityDeletionEventListener.onOperatorUserDeletionEvent(deletionEvent);

        verify(operatorRequestTaskAssignmentService, times(1))
            .assignUserTasksToAccountPrimaryContactOrRelease(userId, accountId);
    }
}