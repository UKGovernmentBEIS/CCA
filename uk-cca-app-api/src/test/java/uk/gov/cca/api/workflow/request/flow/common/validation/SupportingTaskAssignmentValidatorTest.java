package uk.gov.cca.api.workflow.request.flow.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.flow.common.validation.SupportingTaskAssignmentValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentValidationService;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportingTaskAssignmentValidatorTest {

    @InjectMocks
    private SupportingTaskAssignmentValidator supportingTaskAssignmentValidator;

    @Mock
    private RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    @Test
    void validate() {
        AppUser appUser = AppUser.builder().userId("userId").build();
        String peerReviewer = "peerReviewer";
        RequestTask requestTask = RequestTask.builder().build();
        RequestTaskType requestTaskType = mock(RequestTaskType.class);

        when(requestTaskAssignmentValidationService.
            hasUserPermissionsToBeAssignedToTaskType(requestTask, requestTaskType, peerReviewer, appUser))
            .thenReturn(true);

        supportingTaskAssignmentValidator.validate(requestTask, requestTaskType, peerReviewer, appUser);
    }

    @Test
    void validate_assignment_not_allowed() {
        AppUser appUser = AppUser.builder().userId("userId").build();
        String peerReviewer = "peerReviewer";
        RequestTask requestTask = RequestTask.builder().build();
        RequestTaskType requestTaskType =mock(RequestTaskType.class);

        when(requestTaskAssignmentValidationService.
            hasUserPermissionsToBeAssignedToTaskType(requestTask, requestTaskType, peerReviewer, appUser))
            .thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> supportingTaskAssignmentValidator.validate(requestTask, requestTaskType, peerReviewer, appUser));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());
    }
}