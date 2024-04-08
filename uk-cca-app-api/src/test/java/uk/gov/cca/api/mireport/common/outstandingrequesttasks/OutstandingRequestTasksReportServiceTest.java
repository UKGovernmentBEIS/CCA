package uk.gov.cca.api.mireport.common.outstandingrequesttasks;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.mireport.common.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.application.taskview.RequestTaskViewService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OutstandingRequestTasksReportServiceTest {

    @InjectMocks
    private OutstandingRequestTasksReportService service;

    @Mock
    private RequestTaskViewService requestTaskViewService;

    @Test
    void getRequestTasks() {
        final String user = "user";
        RequestTaskType requestTaskType = mock(RequestTaskType.class, Answers.RETURNS_DEEP_STUBS);

        final AppUser appUser = AppUser.builder().userId(user).firstName("fn").lastName("ln").roleType(RoleType.REGULATOR).build();
        Set<RequestTaskType> expectedRequestTaskTypes = Set.of(requestTaskType);

        when(requestTaskViewService.getRequestTaskTypes(RoleType.REGULATOR)).thenReturn(expectedRequestTaskTypes);

        Set<RequestTaskType> actualRequestTasks =
            service.getRequestTaskTypesByRoleType(appUser.getRoleType());

        Assertions.assertThat(actualRequestTasks.size()).isEqualTo(expectedRequestTaskTypes.size());
        Assertions.assertThat(actualRequestTasks).containsAll(Set.of(requestTaskType));
    }
}
