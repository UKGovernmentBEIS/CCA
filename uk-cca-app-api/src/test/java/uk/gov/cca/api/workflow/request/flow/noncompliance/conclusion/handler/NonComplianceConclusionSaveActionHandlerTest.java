package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.service.NonComplianceConclusionSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceConclusionSaveActionHandlerTest {

    @InjectMocks
    private NonComplianceConclusionSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceConclusionSubmitService nonComplianceConclusionSubmitService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION;
        final NonComplianceConclusionSaveRequestTaskActionPayload taskActionPayload =
                NonComplianceConclusionSaveRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.NON_COMPLIANCE_CONCLUSION_SAVE_PAYLOAD)
                        .build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(nonComplianceConclusionSubmitService, times(1))
                .applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION);
    }
}
