package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementActivationServiceTest {

	@InjectMocks
    private UnderlyingAgreementActivationService underlyingAgreementActivationService;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        UnderlyingAgreementActivationDetails details = UnderlyingAgreementActivationDetails.builder().comments("test").build();
        final UnderlyingAgreementActivationSaveRequestTaskActionPayload taskActionPayload =
        		UnderlyingAgreementActivationSaveRequestTaskActionPayload.builder()
        				.underlyingAgreementActivationDetails(details)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(UnderlyingAgreementActivationRequestTaskPayload.builder()
                        .build())
                .build();

        // Invoke
        underlyingAgreementActivationService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        UnderlyingAgreementActivationRequestTaskPayload actual =
                (UnderlyingAgreementActivationRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getUnderlyingAgreementActivationDetails()).isEqualTo(details);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void notifyOperator() {
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "file.png");
        final UnderlyingAgreementActivationDetails details = UnderlyingAgreementActivationDetails.builder()
                .evidenceFiles(Set.of(UUID.randomUUID()))
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(UnderlyingAgreementRequestPayload.builder().build())
                        .build())
                .payload(UnderlyingAgreementActivationRequestTaskPayload.builder()
                        .underlyingAgreementActivationDetails(details)
                        .underlyingAgreementActivationAttachments(attachments)
                        .build())
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();

        // Invoke
        underlyingAgreementActivationService.notifyOperator(requestTask, decisionNotification);

        // Verify
        final UnderlyingAgreementRequestPayload savePayload =
                (UnderlyingAgreementRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestTask.getRequest().getSubmissionDate()).isNotNull();
        assertThat(savePayload.getUnderlyingAgreementActivationDetails()).isEqualTo(details);
        assertThat(savePayload.getUnderlyingAgreementActivationAttachments()).isEqualTo(attachments);
        assertThat(savePayload.getDecisionNotification()).isEqualTo(decisionNotification);
    }
}
