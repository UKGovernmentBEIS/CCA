package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service.PerformanceDataFacilityDigitalFormSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormSaveActionHandlerTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION;
        final PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload taskActionPayload =
                PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(performanceDataFacilityDigitalFormSubmitService, times(1))
                .applySave(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION);
    }
}
