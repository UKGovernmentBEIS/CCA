package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataOutcome;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.service.EmissionsTradingSchemeDataSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataCancelActionHandlerTest {

    @InjectMocks
    private EmissionsTradingSchemeDataCancelActionHandler handler;
    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EmissionsTradingSchemeDataSubmitService emissionsTradingSchemeDataSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.EMISSIONS_TRADING_SCHEME_DATA_CANCEL_APPLICATION;
        final AppUser appUser = AppUser.builder().build();
        final RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder().build();
        final String processTaskId = "processTaskId";

        final RequestTask requestTask = RequestTask.builder()
                .processTaskId(processTaskId)
                .payload(EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder().build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(emissionsTradingSchemeDataSubmitService, times(1)).cancel(appUser, requestTask);
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(CcaBpmnProcessConstants.EMISSIONS_TRADING_SCHEME_DATA_OUTCOME, EmissionsTradingSchemeDataOutcome.CANCELLED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.EMISSIONS_TRADING_SCHEME_DATA_CANCEL_APPLICATION);
    }
}
