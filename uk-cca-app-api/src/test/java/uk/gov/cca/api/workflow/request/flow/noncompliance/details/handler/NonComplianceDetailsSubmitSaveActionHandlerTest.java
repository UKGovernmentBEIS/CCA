package uk.gov.cca.api.workflow.request.flow.noncompliance.details.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.service.NonComplianceDetailsSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceDetailsSubmitSaveActionHandlerTest {

    @InjectMocks
    private NonComplianceDetailsSubmitSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceDetailsSubmitService nonComplianceDetailsSubmitService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.NON_COMPLIANCE_DETAILS_SAVE_APPLICATION;
        final NonComplianceDetailsSubmitSaveRequestTaskActionPayload taskActionPayload =
                NonComplianceDetailsSubmitSaveRequestTaskActionPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(nonComplianceDetailsSubmitService, times(1))
                .applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_DETAILS_SAVE_APPLICATION);
    }
}
