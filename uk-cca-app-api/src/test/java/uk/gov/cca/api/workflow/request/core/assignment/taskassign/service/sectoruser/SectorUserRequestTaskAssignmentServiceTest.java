package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.sectoruser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class SectorUserRequestTaskAssignmentServiceTest {

    @InjectMocks
    private SectorUserRequestTaskAssignmentService sectorRequestTaskAssignmentService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private SectorUserRequestTaskDefaultAssignmentService sectorUserRequestTaskDefaultAssignmentService;

    private List<RequestTask> requestTasks;
    private List<Long> accountsBySectorId;

    @Test
    void assignTask() throws BusinessCheckedException {
        String userId = "userId";
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .type(RequestTaskType.builder().code("requestTaskTypeCode").build())
                .build();

        sectorRequestTaskAssignmentService.assignTask(requestTask, userId);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, userId);
    }

    @Test
    void assignTask_exception_on_assignment() throws BusinessCheckedException {
        String userId = "userId";
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .type(RequestTaskType.builder().code("requestTaskTypeCode").build())
                .build();

        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, userId);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> sectorRequestTaskAssignmentService.assignTask(requestTask, userId));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, userId);
        verifyNoMoreInteractions(requestTaskAssignmentService);
    }

    @Test
    void getRoleType() {
        assertEquals(SECTOR_USER, sectorRequestTaskAssignmentService.getRoleType());
    }

    @Test
    void testAssignTasksToSiteContactOrRelease_whenTasksExistAndFilteredTasksNotEmpty() {

        // Mock Request objects
        Request request1 = mock(Request.class);
        when(request1.getAccountId()).thenReturn(1L);

        Request request2 = mock(Request.class);
        when(request2.getAccountId()).thenReturn(2L);

        // Mock RequestTask objects
        RequestTask task1 = mock(RequestTask.class);
        when(task1.getRequest()).thenReturn(request1);

        RequestTask task2 = mock(RequestTask.class);
        when(task2.getRequest()).thenReturn(request2);

        requestTasks = Arrays.asList(task1, task2);
        accountsBySectorId = Arrays.asList(1L, 2L, 3L);

        String userDeleted = "user";
        Long sectorAssociationId = 1L;

        // Mock the dependencies
        when(requestTaskRepository.findByAssignee(userDeleted)).thenReturn(requestTasks);
        when(targetUnitAccountQueryService.getAllTargetUnitAccountIdsBySectorAssociationId(sectorAssociationId)).thenReturn(accountsBySectorId);

        // Call the method
        sectorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(userDeleted, sectorAssociationId);

        // Verify interactions
        verify(sectorUserRequestTaskDefaultAssignmentService, times(2)).assignTaskToSiteContactOrReleaseRequest(any(RequestTask.class));
    }

    @Test
    void testAssignTasksToSiteContactOrRelease_whenTasksExistButFilteredTasksEmpty() {
        // Mock Request objects
        Request request1 = mock(Request.class);
        when(request1.getAccountId()).thenReturn(1L);

        Request request2 = mock(Request.class);
        when(request2.getAccountId()).thenReturn(2L);

        // Mock RequestTask objects
        RequestTask task1 = mock(RequestTask.class);
        when(task1.getRequest()).thenReturn(request1);

        RequestTask task2 = mock(RequestTask.class);
        when(task2.getRequest()).thenReturn(request2);

        requestTasks = Arrays.asList(task1, task2);
        accountsBySectorId = Arrays.asList(1L, 2L, 3L);

        String userDeleted = "user";
        Long sectorAssociationId = 1L;

        // Mock the dependencies to return no matching account IDs
        when(requestTaskRepository.findByAssignee(userDeleted)).thenReturn(requestTasks);
        when(targetUnitAccountQueryService.getAllTargetUnitAccountIdsBySectorAssociationId(sectorAssociationId)).thenReturn(Collections.emptyList());

        // Call the method
        sectorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(userDeleted, sectorAssociationId);

        // Verify no interactions with sectorUserRequestTaskDefaultAssignmentService
        verify(sectorUserRequestTaskDefaultAssignmentService, never()).assignTaskToSiteContactOrReleaseRequest(any(RequestTask.class));
    }
}
