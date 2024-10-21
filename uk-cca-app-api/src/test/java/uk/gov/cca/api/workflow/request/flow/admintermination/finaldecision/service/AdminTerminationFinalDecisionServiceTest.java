package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalDecisionServiceTest {

    @InjectMocks
    private AdminTerminationFinalDecisionService adminTerminationFinalDecisionService;

    @Test
    void applySaveAction() {
        final AdminTerminationFinalDecisionReasonDetails reasonDetails =
                AdminTerminationFinalDecisionReasonDetails.builder()
                        .explanation("My explanation")
                        .finalDecisionType(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                        .relevantFiles(Set.of(UUID.randomUUID()))
                        .build();
        final Map<String, String> sectionsCompleted = Map.of("UUID", "filename");
        final AdminTerminationFinalDecisionSaveRequestTaskActionPayload taskActionPayload =
                AdminTerminationFinalDecisionSaveRequestTaskActionPayload.builder()
                        .adminTerminationFinalDecisionReasonDetails(reasonDetails)
                        .sectionsCompleted(sectionsCompleted)
                        .build();
        RequestTask requestTask = RequestTask.builder()
                .payload(AdminTerminationFinalDecisionRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_FINAL_DECISION_PAYLOAD)
                        .build())
                .build();

        final AdminTerminationFinalDecisionRequestTaskPayload expected =
                AdminTerminationFinalDecisionRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_FINAL_DECISION_PAYLOAD)
                        .adminTerminationFinalDecisionReasonDetails(reasonDetails)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        // Invoke
        adminTerminationFinalDecisionService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isEqualTo(expected);
    }

    @Test
    void notifyOperator() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final AdminTerminationFinalDecisionReasonDetails reasonDetails =
                AdminTerminationFinalDecisionReasonDetails.builder()
                        .finalDecisionType(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                        .explanation("explanation")
                        .build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "attachment");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");

        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(AdminTerminationRequestPayload.builder()
                                .payloadType(CcaRequestPayloadType.ADMIN_TERMINATION_REQUEST_PAYLOAD)
                                .build())
                        .build())
                .payload(AdminTerminationFinalDecisionRequestTaskPayload.builder()
                        .adminTerminationFinalDecisionReasonDetails(reasonDetails)
                        .adminTerminationAttachments(attachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .build();

        // Invoke
        adminTerminationFinalDecisionService.notifyOperator(requestTask, decisionNotification);

        // Verify
        AdminTerminationRequestPayload actual = (AdminTerminationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(actual.getAdminTerminationFinalDecisionReasonDetails()).isEqualTo(reasonDetails);
        assertThat(actual.getAdminTerminationFinalDecisionAttachments()).isEqualTo(attachments);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(actual.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestTask.getRequest().getSubmissionDate()).isNotNull();
    }
}
