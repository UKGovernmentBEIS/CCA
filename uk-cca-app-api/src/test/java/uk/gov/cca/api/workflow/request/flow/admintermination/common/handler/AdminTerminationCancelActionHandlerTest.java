package uk.gov.cca.api.workflow.request.flow.admintermination.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationOutcome;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTerminationCancelActionHandlerTest {

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private AdminTerminationCancelActionHandler handler;

    @Test
    void testProcess() {
        Long requestTaskId = 1L;
        String requestTaskActionType = "ADMIN_TERMINATION_CANCEL_APPLICATION";
        String processTaskId = UUID.randomUUID().toString();
        AppUser appUser = new AppUser();
        RequestTaskActionEmptyPayload payload = new RequestTaskActionEmptyPayload();
        Map<String, Object> variables = Map.of(CcaBpmnProcessConstants.ADMIN_TERMINATION_OUTCOME,
                AdminTerminationOutcome.CANCELLED);

        RequestTask requestTask = new RequestTask();
        requestTask.setProcessTaskId(processTaskId);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }
}