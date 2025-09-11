package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationOutcome;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationCancelActionHandlerTest {

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private UnderlyingAgreementVariationCancelActionHandler handler;

    @Test
    void testProcess() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        String requestTaskActionType = "actionType";
        String roleType = SECTOR_USER;
        AppUser appUser = AppUser.builder().roleType(roleType).build();
        RequestTaskActionEmptyPayload payload = new RequestTaskActionEmptyPayload();

        RequestTask requestTask = RequestTask.builder()
                .processTaskId(processTaskId)
                .type(RequestTaskType.builder().code(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT).build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestTaskService, times(1))
                .findTaskById(requestTaskId);
        verify(workflowService, times(1))
                .completeTask(processTaskId, Map.of(CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_OUTCOME, UnderlyingAgreementVariationOutcome.CANCELLED,
                        BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, roleType));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_CANCEL_APPLICATION);
    }
}
