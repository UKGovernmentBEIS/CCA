package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.service.PreAuditReviewSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreAuditReviewSubmitSaveActionHandlerTest {

    @InjectMocks
    private PreAuditReviewSubmitSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PreAuditReviewSubmitService preAuditReviewSubmitService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMIT_APPLICATION;
        final PreAuditReviewSubmitSaveRequestTaskActionPayload taskActionPayload =
                PreAuditReviewSubmitSaveRequestTaskActionPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(preAuditReviewSubmitService, times(1))
                .applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SAVE_APPLICATION);
    }
}
