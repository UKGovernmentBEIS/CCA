package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.AccountCaSiteContactService;
import uk.gov.cca.api.authorization.core.service.UserRoleTypeService;
import uk.gov.cca.api.workflow.request.TestRequestPayload;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.cca.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.domain.RoleType.REGULATOR;

@ExtendWith(MockitoExtension.class)
class RegulatorRequestTaskDefaultAssignmentServiceTest {

    @InjectMocks
    private RegulatorRequestTaskDefaultAssignmentService regulatorRequestTaskDefaultAssignmentService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private AccountCaSiteContactService accountCaSiteContactService;

    @Mock
    private RequestReleaseService requestReleaseService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestSupportingRegulator = "requestSupportingRegulator";
        Request request = Request.builder()
            .accountId(1L)
            .payload(TestRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .supportingRegulator(requestSupportingRegulator)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorAssignee)).thenReturn(true);

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorAssignee);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, requestRegulatorAssignee);
        verifyNoInteractions(accountCaSiteContactService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task_assignment_throws_exception() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestSupportingRegulator = "requestSupportingRegulator";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(TestRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .supportingRegulator(requestSupportingRegulator)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorAssignee)).thenReturn(true);
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, requestRegulatorAssignee);
        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorAssignee);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, requestRegulatorAssignee);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task_regulator_assignee_not_exists() throws BusinessCheckedException {
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestSupportingRegulator = "requestSupportingRegulator";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(TestRequestPayload.builder()
                .regulatorReviewer(requestRegulatorReviewer)
                .supportingRegulator(requestSupportingRegulator)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW).build();

        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(userRoleTypeService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task_regulator_assignee_not_reviewer() throws BusinessCheckedException {
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        String requestSupportingRegulator = "requestSupportingRegulator";
        String caSiteContact = "caSiteContact";
        Request request = Request.builder()
            .accountId(1L)
            .payload(TestRequestPayload.builder()
                .regulatorAssignee(requestRegulatorAssignee)
                .regulatorReviewer(requestRegulatorReviewer)
                .supportingRegulator(requestSupportingRegulator)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW).build();

        when(userRoleTypeService.isUserRegulator(requestRegulatorAssignee)).thenReturn(false);
        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.of(caSiteContact));

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserRegulator(requestRegulatorAssignee);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, caSiteContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_non_peer_review_task_nor_regulator_assignee_neither_site_contact_exist() {
        String requestSupportingRegulator = "requestSupportingRegulator";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        Request request = Request.builder()
            .accountId(1L)
            .payload(TestRequestPayload.builder()
                .supportingRegulator(requestSupportingRegulator)
                .regulatorReviewer(requestRegulatorReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW).build();

        when(accountCaSiteContactService.findCASiteContactByAccount(request.getAccountId())).thenReturn(Optional.empty());

        regulatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(request.getAccountId());
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);
        verifyNoInteractions(userRoleTypeService, requestTaskAssignmentService);
    }

    @Test
    void getRoleType() {
        assertEquals(REGULATOR, regulatorRequestTaskDefaultAssignmentService.getRoleType());
    }
}