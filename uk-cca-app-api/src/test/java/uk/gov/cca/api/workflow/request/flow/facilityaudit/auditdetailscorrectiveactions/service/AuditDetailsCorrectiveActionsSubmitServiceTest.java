package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsAndCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditTechnique;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.CorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuditDetailsCorrectiveActionsSubmitServiceTest {

    @InjectMocks
    private AuditDetailsCorrectiveActionsSubmitService auditDetailsCorrectiveActionsSubmitService;

    @Test
    void applySaveAction() {
        final UUID fileUuid = UUID.randomUUID();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
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
                                .title("title")
                                .deadline(LocalDate.of(2022, 3, 3))
                                .details("bla bla bla bla")
                                .build()))
                        .build())
                .build();

        final AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload taskActionPayload =
                AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload.builder()
                        .auditDetailsAndCorrectiveActions(auditDetailsAndCorrectiveActions)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.builder().build())
                .build();

        // Invoke
        auditDetailsCorrectiveActionsSubmitService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        AuditDetailsCorrectiveActionsSubmitRequestTaskPayload actual =
                (AuditDetailsCorrectiveActionsSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getAuditDetailsAndCorrectiveActions()).isEqualTo(auditDetailsAndCorrectiveActions);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void applySubmitAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final UUID fileUUID = UUID.randomUUID();
        final String filename = "testFile";
        Map<UUID, String> attachments = Map.of(fileUUID, filename);

        final AuditDetailsAndCorrectiveActions auditDetailsAndCorrectiveActions = AuditDetailsAndCorrectiveActions.builder()
                .auditDetails(AuditDetails.builder()
                        .auditTechnique(AuditTechnique.DESK_BASED_INTERVIEW)
                        .auditDate(LocalDate.of(2025, 2, 2))
                        .finalAuditReportDate(LocalDate.of(2025, 2, 2))
                        .comments("bla bla bla bla")
                        .auditDocuments(Set.of(fileUUID))
                        .build())
                .correctiveActions(CorrectiveActions.builder()
                        .hasActions(true)
                        .actions(Set.of(CorrectiveAction.builder()
                                .title("title")
                                .deadline(LocalDate.of(2022, 3, 3))
                                .details("bla bla bla bla")
                                .build()))
                        .build())
                .build();

        final AuditDetailsCorrectiveActionsSubmitRequestTaskPayload requestTaskPayload = AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.builder()
                .auditDetailsAndCorrectiveActions(auditDetailsAndCorrectiveActions)
                .sectionsCompleted(sectionsCompleted)
                .facilityAuditAttachments(attachments)
                .build();


        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(FacilityAuditRequestPayload.builder().build())
                        .build())
                .payload(requestTaskPayload)
                .build();

        // Invoke
        auditDetailsCorrectiveActionsSubmitService.applySubmitAction(requestTask);

        final Request request = requestTask.getRequest();
        FacilityAuditRequestPayload actual = (FacilityAuditRequestPayload) request.getPayload();

        assertThat(actual.getAuditDetailsAndCorrectiveActions()).isEqualTo(auditDetailsAndCorrectiveActions);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(actual.getAuditDetailsCorrectiveActionsAttachments()).isEqualTo(attachments);
    }
}
