package uk.gov.cca.api.workflow.request.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.cca.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTaskServiceTest {

    @InjectMocks
    private RequestTaskService requestTaskService;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private AuthorizationRulesQueryService authorizationRulesQueryService;

    @Test
    void findTasksByRequestIdAndRoleType() {
        final String requestId = "1";
        Set<String> roleAllowedTaskTypes = Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW.toString());

        Request request = Request.builder().id(requestId).build();
        RequestTask regulatorRequestTask1 = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW)
            .build();
        List<RequestTask> requestTasks = List.of(regulatorRequestTask1);

        when(authorizationRulesQueryService
            .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR))
            .thenReturn(roleAllowedTaskTypes);
        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(requestTasks);

        List<RequestTask> requestTasksRetrieved = requestTaskService.findTasksByRequestIdAndRoleType(requestId, RoleType.REGULATOR);

        assertThat(requestTasksRetrieved).containsExactly(regulatorRequestTask1);

        verify(authorizationRulesQueryService, times(1)).
            findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR);
        verify(requestTaskRepository, times(1)).findByRequestId(requestId);
    }

    @Test
    void findTasksByRequestIdAndRoleType_no_tasks_found_for_request() {
        final String requestId = "1";
        Set<String> roleAllowedTaskTypes = Set.of("requestTaskType");

        when(authorizationRulesQueryService
            .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR))
            .thenReturn(roleAllowedTaskTypes);
        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(Collections.emptyList());

        List<RequestTask> requestTasksRetrieved = requestTaskService.findTasksByRequestIdAndRoleType(requestId, RoleType.REGULATOR);

        assertThat(requestTasksRetrieved).isEmpty();

        verify(authorizationRulesQueryService, times(1)).
            findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR);
        verify(requestTaskRepository, times(1)).findByRequestId(requestId);
    }

    @Test
    void findTasksByRequestIdAndRoleType_no_tasks_found_for_role_resource_sub_types() {
        final String requestId = "1";
        Set<String> roleAllowedTaskTypes = Set.of("requestTaskType");

        Request request = Request.builder().id(requestId).build();
        RequestTask regulatorRequestTask = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW)
            .build();
        RequestTask operatorRequestTask = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW)
            .build();
        List<RequestTask> requestTasks = List.of(regulatorRequestTask, operatorRequestTask);

        when(authorizationRulesQueryService
            .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR))
            .thenReturn(roleAllowedTaskTypes);
        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(requestTasks);

        List<RequestTask> requestTasksRetrieved = requestTaskService.findTasksByRequestIdAndRoleType(requestId, RoleType.REGULATOR);

        assertThat(requestTasksRetrieved).isEmpty();

        verify(authorizationRulesQueryService, times(1)).
            findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR);
        verify(requestTaskRepository, times(1)).findByRequestId(requestId);
    }

    @Test
    void findTasksByRequestIdAndRoleType_no_resource_sub_types_for_role() {
        final String requestId = "1";

        Request request = Request.builder().id(requestId).build();
        RequestTask regulatorRequestTask1 = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW)
            .build();
        RequestTask regulatorRequestTask2 = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW)
            .build();
        RequestTask operatorRequestTask = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.DUMMY_REQUEST_TASK_TYPE2)
            .build();
        List<RequestTask> requestTasks = List.of(regulatorRequestTask1, regulatorRequestTask2, operatorRequestTask);

        when(authorizationRulesQueryService
            .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR))
            .thenReturn(Collections.emptySet());
        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(requestTasks);

        List<RequestTask> requestTasksRetrieved = requestTaskService.findTasksByRequestIdAndRoleType(requestId, RoleType.REGULATOR);

        assertThat(requestTasksRetrieved).isEmpty();

        verify(authorizationRulesQueryService, times(1)).
            findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR);
        verify(requestTaskRepository, times(1)).findByRequestId(requestId);
    }
}
