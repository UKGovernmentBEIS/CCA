package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsAndCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditTechnique;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.CorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateFacilityAuditTrackCorrectiveActionsExpirationRemindersServiceTest {

    @InjectMocks
    private CalculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService service;

    @Mock
    private RequestService requestService;

    @Test
    void calculateExpirationDate() {
        final String requestId = "1";
        final UUID fileUuid = UUID.randomUUID();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final String correctiveActionTitle1 = "Corrective Action 1";
        final String correctiveActionTitle2 = "Corrective Action 2";
        final LocalDate deadline1 = LocalDate.now().plusDays(1);
        final LocalDate deadline2 = deadline1.plusDays(1);

        final AuditDetailsAndCorrectiveActions auditDetailsAndCorrectiveActions = AuditDetailsAndCorrectiveActions.builder()
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
                                        .title(correctiveActionTitle1)
                                        .deadline(deadline1)
                                        .details("bla bla bla bla")
                                        .build(),
                                CorrectiveAction.builder()
                                        .title(correctiveActionTitle2)
                                        .deadline(deadline2)
                                        .details("bla bla bla bla")
                                        .build()))
                        .build())
                .build();

        final FacilityAuditRequestPayload requestPayload = FacilityAuditRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.FACILITY_AUDIT_REQUEST_PAYLOAD)
                .auditDetailsAndCorrectiveActions(auditDetailsAndCorrectiveActions)
                .build();

        final AuditTrackCorrectiveActions auditTrackCorrectiveActions = AuditTrackCorrectiveActions.builder()
                .correctiveActionResponses(Map.of())
                .build();
        final AuditTrackCorrectiveActionsRequestTaskPayload auditTrackCorrectiveActionsRequestTaskPayload = AuditTrackCorrectiveActionsRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD)
                .auditTrackCorrectiveActions(auditTrackCorrectiveActions)
                .respondedActions(Set.of())
                .sectionsCompleted(sectionsCompleted)
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .requestTasks(List.of(RequestTask.builder()
                        .type(RequestTaskType.builder().code(CcaRequestTaskType.AUDIT_TRACK_CORRECTIVE_ACTIONS).build())
                        .payload(auditTrackCorrectiveActionsRequestTaskPayload)
                        .build()))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // invoke
        Optional<LocalDate> result = service.calculateExpirationDate(requestId);

        // verify
        verify(requestService, times(1)).findRequestById(requestId);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(deadline1);
    }
}
