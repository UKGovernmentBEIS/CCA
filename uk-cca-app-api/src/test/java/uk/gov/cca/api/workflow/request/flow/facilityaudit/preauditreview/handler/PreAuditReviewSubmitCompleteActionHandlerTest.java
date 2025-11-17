package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditReasonDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditDetermination;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.RequestedDocuments;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.service.PreAuditReviewSubmitService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.transform.PreAuditReviewSubmitMapper;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.validation.PreAuditReviewSubmitValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreAuditReviewSubmitCompleteActionHandlerTest {

    @InjectMocks
    private PreAuditReviewSubmitCompleteActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PreAuditReviewSubmitService preAuditReviewSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private PreAuditReviewSubmitValidator preAuditReviewSubmitValidator;

    private final PreAuditReviewSubmitMapper PRE_AUDIT_REVIEW_SUBMIT_MAPPER = Mappers.getMapper(PreAuditReviewSubmitMapper.class);

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final String requestTaskActionType = CcaRequestTaskActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMIT_APPLICATION;
        final AppUser appUser = AppUser.builder().build();
        final RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();

        final PreAuditReviewSubmitRequestTaskPayload requestTaskPayload = PreAuditReviewSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                .preAuditReviewDetails(PreAuditReviewDetails.builder()
                        .auditReasonDetails(AuditReasonDetails.builder()
                                .reasonsForAudit(List.of(FacilityAuditReasonType.NON_COMPLIANCE))
                                .comment("bla bla bla")
                                .build())
                        .requestedDocuments(RequestedDocuments.builder()
                                .auditMaterialReceivedDate(LocalDate.of(2024, 3, 15))
                                .annotatedSitePlansFile(UUID.randomUUID())
                                .build())
                        .auditDetermination(AuditDetermination.builder()
                                .reviewCompletionDate(LocalDate.of(2024, 3, 15))
                                .furtherAuditNeeded(true)
                                .reviewComments("test comments")
                                .build())
                        .build())
                .build();

        PreAuditReviewSubmittedRequestActionPayload actionPayload =
                PRE_AUDIT_REVIEW_SUBMIT_MAPPER.toPreAuditReviewSubmittedRequestActionPayload(requestTaskPayload);

        Request request = Request.builder().id(requestId).build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload = handler.process(requestTaskId, requestTaskActionType, appUser, taskActionEmptyPayload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(preAuditReviewSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(preAuditReviewSubmitService, times(1)).submitPreAuditReview(requestTask);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.FACILITY_AUDIT_OUTCOME, "",
                        CcaBpmnProcessConstants.IS_FURTHER_AUDIT_NEEDED, true));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMIT_APPLICATION);
    }
}
