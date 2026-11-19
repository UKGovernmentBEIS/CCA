package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataOutcome;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.service.EmissionsTradingSchemeDataSubmitService;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.validation.EmissionsTradingSchemeDataSubmitValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataSubmitActionHandlerTest {

    @InjectMocks
    private EmissionsTradingSchemeDataSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private EmissionsTradingSchemeDataSubmitValidator validator;

    @Mock
    private EmissionsTradingSchemeDataSubmitService emissionsTradingSchemeDataSubmitService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_APPLICATION;
        final RequestTaskActionEmptyPayload taskActionPayload = RequestTaskActionEmptyPayload.builder().build();

        final String processTaskId = "processTaskId";
        final String requestId = "requestId";
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(Request.builder().id(requestId).build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(validator, times(1)).validate(requestTask);
        verify(emissionsTradingSchemeDataSubmitService, times(1))
                .submit(eq(appUser), eq(requestTask), any());
        verify(workflowService, times(1)).completeTask(processTaskId, Map.of(
                BpmnProcessConstants.REQUEST_ID, requestId,
                CcaBpmnProcessConstants.EMISSIONS_TRADING_SCHEME_DATA_OUTCOME, EmissionsTradingSchemeDataOutcome.COMPLETED));
        verify(emissionsTradingSchemeDataSubmitService, times(1)).submit(eq(appUser), eq(requestTask), any());
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_APPLICATION);
    }
}
