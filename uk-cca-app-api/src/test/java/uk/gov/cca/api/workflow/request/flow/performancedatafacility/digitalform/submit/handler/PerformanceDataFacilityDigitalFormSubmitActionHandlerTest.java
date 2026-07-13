package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormOutcome;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service.PerformanceDataFacilityDigitalFormSubmitService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation.PerformanceDataFacilityDigitalFormSubmitValidator;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormSubmitActionHandlerTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PerformanceDataFacilityDigitalFormSubmitValidator performanceDataFacilityDigitalFormSubmitValidator;

    @Mock
    private PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_APPLICATION;
        final RequestTaskActionEmptyPayload taskActionPayload = RequestTaskActionEmptyPayload.builder().build();

        final String processTaskId = "processTaskId";
        final String requestId = "requestId";
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(Request.builder().id(requestId).build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(performanceDataFacilityDigitalFormSubmitValidator.isReportSubmissionExpired(eq(requestTask), any()))
                .thenReturn(false);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(performanceDataFacilityDigitalFormSubmitValidator, times(1))
                .isReportSubmissionExpired(eq(requestTask), any());
        verify(performanceDataFacilityDigitalFormSubmitValidator, times(1))
                .validate(requestTask);
        verify(performanceDataFacilityDigitalFormSubmitService, times(1))
                .submit(eq(appUser), eq(requestTask), any());
        verify(workflowService, times(1)).completeTask(processTaskId, Map.of(
                BpmnProcessConstants.REQUEST_ID, requestId,
                CcaBpmnProcessConstants.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_OUTCOME, PerformanceDataFacilityDigitalFormOutcome.COMPLETED));
        verifyNoMoreInteractions(performanceDataFacilityDigitalFormSubmitService);
    }

    @Test
    void process_expired() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_APPLICATION;
        final RequestTaskActionEmptyPayload taskActionPayload = RequestTaskActionEmptyPayload.builder().build();

        final String processTaskId = "processTaskId";
        final String requestId = "requestId";
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(Request.builder().id(requestId).build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(performanceDataFacilityDigitalFormSubmitValidator.isReportSubmissionExpired(eq(requestTask), any()))
                .thenReturn(true);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(performanceDataFacilityDigitalFormSubmitValidator, times(1))
                .isReportSubmissionExpired(eq(requestTask), any());
        verify(performanceDataFacilityDigitalFormSubmitService, times(1))
                .markTaskAsExpired(eq(appUser), eq(requestTask), any());
        verify(workflowService, times(1)).completeTask(processTaskId, Map.of(
                BpmnProcessConstants.REQUEST_ID, requestId,
                CcaBpmnProcessConstants.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_OUTCOME, PerformanceDataFacilityDigitalFormOutcome.EXPIRED));
        verifyNoMoreInteractions(performanceDataFacilityDigitalFormSubmitService);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_APPLICATION);
    }
}
