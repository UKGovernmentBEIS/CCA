package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditOutcome;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityAuditCancelActionHandlerTest {

    @InjectMocks
    private FacilityAuditCancelActionHandler handler;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String requestTaskActionType = "FACILITY_AUDIT_CANCEL_APPLICATION";
        String processTaskId = UUID.randomUUID().toString();
        AppUser appUser = new AppUser();
        RequestTaskActionEmptyPayload payload = new RequestTaskActionEmptyPayload();
        Map<String, Object> variables =
                Map.of(CcaBpmnProcessConstants.FACILITY_AUDIT_OUTCOME, FacilityAuditOutcome.CANCELLED);

        RequestTask requestTask = new RequestTask();
        requestTask.setProcessTaskId(processTaskId);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.FACILITY_AUDIT_CANCEL_APPLICATION);
    }
}
