package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormOutcome;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service.PerformanceDataFacilityDigitalFormSubmitService;
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
class PerformanceDataFacilityDigitalFormCancelActionHandlerTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormCancelActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CANCEL_APPLICATION;
        final AppUser appUser = AppUser.builder().build();
        final RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder().build();
        final String processTaskId = "processTaskId";

        final RequestTask requestTask = RequestTask.builder()
                .processTaskId(processTaskId)
                .payload(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder().build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(performanceDataFacilityDigitalFormSubmitService, times(1)).cancel(appUser, requestTask);
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(CcaBpmnProcessConstants.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_OUTCOME, PerformanceDataFacilityDigitalFormOutcome.CANCELLED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CANCEL_APPLICATION);
    }
}
