package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementVariationActivationServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationActivationService underlyingAgreementVariationActivationService;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        UnderlyingAgreementActivationDetails details = UnderlyingAgreementActivationDetails.builder().comments("test").build();
        final UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload taskActionPayload =
                UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload.builder()
                        .underlyingAgreementActivationDetails(details)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(UnderlyingAgreementVariationActivationRequestTaskPayload.builder()
                        .build())
                .build();

        // Invoke
        underlyingAgreementVariationActivationService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        UnderlyingAgreementVariationActivationRequestTaskPayload actual =
                (UnderlyingAgreementVariationActivationRequestTaskPayload) requestTask.getPayload();
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
                        .payload(UnderlyingAgreementVariationRequestPayload.builder().build())
                        .build())
                .payload(UnderlyingAgreementVariationActivationRequestTaskPayload.builder()
                        .underlyingAgreementActivationDetails(details)
                        .underlyingAgreementActivationAttachments(attachments)
                        .build())
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();

        // Invoke
        underlyingAgreementVariationActivationService.notifyOperator(requestTask, decisionNotification);

        // Verify
        final UnderlyingAgreementVariationRequestPayload savePayload =
                (UnderlyingAgreementVariationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestTask.getRequest().getSubmissionDate()).isNotNull();
        assertThat(savePayload.getUnderlyingAgreementActivationDetails()).isEqualTo(details);
        assertThat(savePayload.getUnderlyingAgreementActivationAttachments()).isEqualTo(attachments);
        assertThat(savePayload.getDecisionNotification()).isEqualTo(decisionNotification);
    }
}
