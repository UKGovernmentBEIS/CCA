package uk.gov.cca.api.workflow.request.application.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.netz.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.user.application.UserServiceDelegator;
import uk.gov.netz.api.user.core.domain.dto.UserDTO;
import uk.gov.netz.api.workflow.request.application.taskview.RequestTaskItemDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionType;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaRequestTaskViewServiceTest {

    @InjectMocks
    private CcaRequestTaskViewService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UserServiceDelegator userServiceDelegator;

    @Mock
    private RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

    @Test
    void getTaskItemInfoSectorUser() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(CcaRoleTypeConstants.SECTOR_USER).build();
        final Long requestTaskId = 1L;
        final String requestTypeCode = "requestTypeCode";
        final String requestTaskTypeCode = "requestTaskTypeCode";
        List<String> allowedRequestTaskActions = List.of("action1", "action2");

        final Request request = createRequest("1", ca, accountId, requestTypeCode);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, appUser.getUserId(),
                "proceTaskId", requestTaskTypeCode, allowedRequestTaskActions);

        final UserDTO requestTaskAssigneeUser = SectorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userServiceDelegator.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria))
                .thenReturn(true);
        when(requestTaskAuthorizationResourceService.hasUserExecuteScopeOnRequestTaskType(appUser, requestTaskTypeCode, resourceCriteria))
                .thenReturn(true);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, appUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskTypeCode);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).containsExactlyInAnyOrderElementsOf(allowedRequestTaskActions);
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestTask().getAssigneeUserId()).isEqualTo(user);
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userServiceDelegator, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
                .hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria);
        verify(requestTaskAuthorizationResourceService, times(1))
                .hasUserExecuteScopeOnRequestTaskType(appUser, requestTaskTypeCode, resourceCriteria);
        verifyNoMoreInteractions(requestTaskAuthorizationResourceService);
    }

    @Test
    void getTaskItemInfoSectorUser_assignee_user_has_not_execute_scope_on_request_tasks() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(CcaRoleTypeConstants.SECTOR_USER).build();
        final Long requestTaskId = 1L;
        final String requestTypeCode = "requestTypeCode";
        final String requestTaskTypeCode = "requestTaskTypeCode";

        final Request request = createRequest("1", ca, accountId, requestTypeCode);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, user,
                "proceTaskId", requestTaskTypeCode, Collections.emptyList());

        final UserDTO requestTaskAssigneeUser = SectorUserDTO.builder().firstName("fn").lastName("ln").build();

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(userServiceDelegator.getUserById(requestTask.getAssignee())).thenReturn(requestTaskAssigneeUser);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria))
                .thenReturn(false);
        when(requestTaskAuthorizationResourceService.hasUserExecuteScopeOnRequestTaskType(appUser, requestTaskTypeCode, resourceCriteria))
                .thenReturn(false);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, appUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskTypeCode);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEmpty();
        assertThat(result.isUserAssignCapable()).isFalse();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(userServiceDelegator, times(1)).getUserById(requestTask.getAssignee());
        verify(requestTaskAuthorizationResourceService, times(1))
                .hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria);
        verify(requestTaskAuthorizationResourceService, times(1))
                .hasUserExecuteScopeOnRequestTaskType(appUser, requestTaskTypeCode, resourceCriteria);
    }

    @Test
    void getTaskItemInfoSectorUser_request_task_assignee_is_null() {
        final String user = "user";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(CcaRoleTypeConstants.SECTOR_USER).build();
        final Long requestTaskId = 1L;
        final String requestTypeCode = "requestTypeCode";
        final String requestTaskTypeCode = "requestTaskTypeCode";

        final Request request = createRequest("1", ca, accountId, requestTypeCode);
        final RequestTask requestTask = createRequestTask(requestTaskId, request, null,
                "proceTaskId", requestTaskTypeCode, Collections.emptyList());

        ResourceCriteria resourceCriteria = ResourceCriteria.builder().accountId(accountId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria))
                .thenReturn(true);

        //invoke
        RequestTaskItemDTO result = service.getTaskItemInfo(requestTaskId, appUser);

        assertThat(result.getRequestTask().getType()).isEqualTo(requestTaskTypeCode);
        assertThat(result.getRequestTask().getDaysRemaining()).isEqualTo(14);
        assertThat(result.getAllowedRequestTaskActions()).isEmpty();
        assertThat(result.isUserAssignCapable()).isTrue();
        assertThat(result.getRequestInfo().getCompetentAuthority()).isEqualTo(ca);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestTaskAuthorizationResourceService, times(1))
                .hasUserAssignScopeOnRequestTasks(appUser, resourceCriteria);
        verify(requestTaskAuthorizationResourceService, never())
                .hasUserExecuteScopeOnRequestTaskType(any(), anyString(), any());
    }

    private Request createRequest(String requestId, CompetentAuthorityEnum ca,
                                  Long accountId, String requestTypeCode) {
        return Request.builder()
                .id(requestId)
                .type(RequestType.builder().code(requestTypeCode).build())
                .competentAuthority(ca)
                .status("inprogress")
                .processInstanceId("procInst")
                .accountId(accountId)
                .creationDate(LocalDateTime.now())
                .build();
    }

    private RequestTask createRequestTask(Long requestTaskId, Request request, String assignee, String processTaskId,
                                          String requestTaskTypeCode,  List<String> allowedRequestTaskActions) {
        return RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .processTaskId(processTaskId)
                .type(RequestTaskType.builder().code(requestTaskTypeCode).actionTypes(
                        allowedRequestTaskActions.stream().map(action -> RequestTaskActionType.builder().code(action).build()).collect(Collectors.toSet())
                ).build())
                .assignee(assignee)
                .dueDate(LocalDate.now().plusDays(14))
                .build();
    }
}
