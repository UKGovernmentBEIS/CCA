package uk.gov.cca.api.workflow.request.flow.admintermination.submit.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@ExtendWith(MockitoExtension.class)
class AdminTerminationSubmitServiceTest {

    @InjectMocks
    private AdminTerminationSubmitService adminTerminationSubmitService;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final AdminTerminationSaveRequestTaskActionPayload taskActionPayload =
                AdminTerminationSaveRequestTaskActionPayload.builder()
                        .adminTerminationReasonDetails(AdminTerminationReasonDetails.builder().build())
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(AdminTerminationSubmitRequestTaskPayload.builder()
                        .build())
                .build();

        // Invoke
        adminTerminationSubmitService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        AdminTerminationSubmitRequestTaskPayload actual =
                (AdminTerminationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void notifyOperator() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final AdminTerminationReasonDetails adminTerminationReasonDetails = AdminTerminationReasonDetails.builder()
                .reason(AdminTerminationReason.DATA_NOT_PROVIDED)
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
                .payload(AdminTerminationSubmitRequestTaskPayload.builder()
                        .adminTerminationReasonDetails(adminTerminationReasonDetails)
                        .adminTerminationAttachments(attachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .build();

        // Invoke
        adminTerminationSubmitService.notifyOperator(requestTask, decisionNotification);

        // Verify
        AdminTerminationRequestPayload actual = (AdminTerminationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(actual.getAdminTerminationReasonDetails()).isEqualTo(adminTerminationReasonDetails);
        assertThat(actual.getAdminTerminationSubmitAttachments()).isEqualTo(attachments);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(actual.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(actual.getSubmitSubmissionDate()).isNotNull();
        assertThat(requestTask.getRequest().getSubmissionDate()).isNotNull();
    }
}
