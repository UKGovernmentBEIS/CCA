package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.service.EnforcementResponseNoticeSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceEnforcementResponseNoticeSaveActionHandlerTest {

    @InjectMocks
    private NonComplianceEnforcementResponseNoticeSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EnforcementResponseNoticeSubmitService enforcementResponseNoticeSubmitService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION;
        final NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload taskActionPayload =
                NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_PAYLOAD)
                        .build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(enforcementResponseNoticeSubmitService, times(1))
                .applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION);
    }
}
