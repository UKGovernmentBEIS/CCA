package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsAndCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditTechnique;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.CorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.service.AuditDetailsCorrectiveActionsSubmitService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.transform.AuditDetailsCorrectiveActionsSubmitMapper;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.validation.AuditDetailsCorrectiveActionsSubmitValidator;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditDetailsCorrectiveActionsSubmitCompleteActionHandlerTest {

    @InjectMocks
    private AuditDetailsCorrectiveActionsSubmitCompleteActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AuditDetailsCorrectiveActionsSubmitService auditDetailsCorrectiveActionsSubmitService;

    @Mock
    private AuditDetailsCorrectiveActionsSubmitValidator auditDetailsCorrectiveActionsSubmitValidator;

    private static final AuditDetailsCorrectiveActionsSubmitMapper AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_MAPPER = Mappers.getMapper(AuditDetailsCorrectiveActionsSubmitMapper.class);

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final String requestTaskActionType = CcaRequestTaskActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMIT_APPLICATION;
        final AppUser appUser = AppUser.builder().build();
        final RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();
        final UUID fileUuid = UUID.randomUUID();

        final AuditDetailsCorrectiveActionsSubmitRequestTaskPayload requestTaskPayload = AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.builder()
                .auditDetailsAndCorrectiveActions(AuditDetailsAndCorrectiveActions.builder()
                        .auditDetails(AuditDetails.builder()
                                .auditTechnique(AuditTechnique.DESK_BASED_INTERVIEW)
                                .auditDate(LocalDate.of(2025, 2, 2))
                                .finalAuditReportDate(LocalDate.of(2025, 2, 2))
                                .comments("bla bla bla bla")
                                .auditDocuments(Set.of(fileUuid))
                                .build())
                        .correctiveActions(CorrectiveActions.builder()
                                .hasActions(true)
                                .actions(Set.of(CorrectiveAction.builder()
                                        .title("title")
                                        .deadline(LocalDate.of(2022, 3, 3))
                                        .details("bla bla bla bla")
                                        .build()))
                                .build())
                        .build())
                .sectionsCompleted(Map.of("section1", "COMPLETED"))
                .facilityAuditAttachments(Map.of(fileUuid, "filename"))
                .build();

        AuditDetailsCorrectiveActionsSubmittedRequestActionPayload actionPayload =
                AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_MAPPER.toAuditDetailsCorrectiveActionsSubmittedRequestActionPayload(requestTaskPayload);

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
        verify(auditDetailsCorrectiveActionsSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(auditDetailsCorrectiveActionsSubmitService, times(1)).applySubmitAction(requestTask);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.FACILITY_AUDIT_OUTCOME, "",
                        CcaBpmnProcessConstants.IS_CORRECTIVE_ACTIONS_NEEDED, true));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_APPLICATION);
    }
}
