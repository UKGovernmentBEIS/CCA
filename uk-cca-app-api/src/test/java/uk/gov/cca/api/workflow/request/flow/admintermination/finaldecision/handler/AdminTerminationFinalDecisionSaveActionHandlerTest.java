package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service.AdminTerminationFinalDecisionService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalDecisionSaveActionHandlerTest {

    @InjectMocks
    private AdminTerminationFinalDecisionSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AdminTerminationFinalDecisionService adminTerminationFinalDecisionService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser appUser = AppUser.builder().build();
        final AdminTerminationFinalDecisionSaveRequestTaskActionPayload payload =
                AdminTerminationFinalDecisionSaveRequestTaskActionPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().id(1L).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, CcaRequestTaskActionType.ADMIN_TERMINATION_FINAL_DECISION_SAVE_APPLICATION, appUser, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(adminTerminationFinalDecisionService, times(1)).applySaveAction(payload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.ADMIN_TERMINATION_FINAL_DECISION_SAVE_APPLICATION);
    }
}
