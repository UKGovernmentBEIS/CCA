package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonCompliancePenaltyOutcomeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceWithdrawNotice;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceConclusionSubmitServiceTest {

    @InjectMocks
    private NonComplianceConclusionSubmitService service;


    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.NONE)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(null)
                .build();
        final NonComplianceConclusionSaveRequestTaskActionPayload requestTaskActionPayload = NonComplianceConclusionSaveRequestTaskActionPayload.builder()
                .nonComplianceConclusion(nonComplianceConclusion)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(NonComplianceConclusionSubmitRequestTaskPayload.builder().build())
                .build();

        // invoke
        service.applySaveAction(requestTaskActionPayload, requestTask);

        // verify
        NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = (NonComplianceConclusionSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getNonComplianceConclusion()).isEqualTo(nonComplianceConclusion);
        assertThat(requestTaskPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void notifyOperator() {
        final String requestId = "requestId";
        final UUID fileUuid = UUID.randomUUID();
        final Map<UUID, String> nonComplianceAttachments = Map.of(fileUuid, "notice");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .build();
        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.WITHDRAW)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(NonComplianceWithdrawNotice.builder()
                        .file(fileUuid)
                        .comments("bla bla")
                        .build())
                .build();
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(nonComplianceAttachments)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(NonComplianceRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(requestTaskPayload)
                .build();

        // invoke
        service.notifyOperator(requestTask, decisionNotification);

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.isPenaltyReissueNeeded()).isFalse();
        assertThat(requestPayload.getNonComplianceConclusion()).isEqualTo(nonComplianceConclusion);
        assertThat(requestPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getNonComplianceAttachments()).isEqualTo(nonComplianceAttachments);
    }

    @Test
    void complete() {
        final String requestId = "requestId";
        final UUID fileUuid = UUID.randomUUID();
        final Map<UUID, String> nonComplianceAttachments = Map.of(fileUuid, "notice");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.REISSUE)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(null)
                .build();
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(nonComplianceAttachments)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(NonComplianceRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(requestTaskPayload)
                .build();

        final boolean isPenaltyReissueNeeded =
                requestTaskPayload.getNonComplianceConclusion().getDetails().getPenaltyOutcome().equals(NonCompliancePenaltyOutcomeType.REISSUE);

        // invoke
        service.complete(requestTask, isPenaltyReissueNeeded);

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.isPenaltyReissueNeeded()).isTrue();
        assertThat(requestPayload.getNonComplianceConclusion()).isEqualTo(nonComplianceConclusion);
        assertThat(requestPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(requestPayload.getNonComplianceAttachments()).isEqualTo(nonComplianceAttachments);
    }
}
