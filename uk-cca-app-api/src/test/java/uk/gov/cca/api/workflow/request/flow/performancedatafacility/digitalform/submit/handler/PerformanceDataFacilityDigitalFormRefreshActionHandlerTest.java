package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service.PerformanceDataFacilityDigitalFormSubmitService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation.PerformanceDataFacilityDigitalFormRefreshValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormRefreshActionHandlerTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormRefreshActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;

    @Mock
    private PerformanceDataFacilityDigitalFormRefreshValidator performanceDataFacilityDigitalFormRefreshValidator;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REFRESH_APPLICATION;
        final RequestTaskActionEmptyPayload taskActionPayload = RequestTaskActionEmptyPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(performanceDataFacilityDigitalFormRefreshValidator, times(1)).validate(requestTask);
        verify(performanceDataFacilityDigitalFormSubmitService, times(1)).refreshBaselineData(requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REFRESH_APPLICATION);
    }
}
