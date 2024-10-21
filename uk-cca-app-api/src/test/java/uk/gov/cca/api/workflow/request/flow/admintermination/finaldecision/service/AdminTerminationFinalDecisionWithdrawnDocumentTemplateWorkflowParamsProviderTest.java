package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalDecisionWithdrawnDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private AdminTerminationFinalDecisionWithdrawnDocumentTemplateWorkflowParamsProvider paramsProvider;

    @Test
    void getContextActionType() {
        assertThat(paramsProvider.getContextActionType())
                .isEqualTo(CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_WITHDRAWN);
    }

    @Test
    void constructParams() {
        final LocalDateTime submissionDateTime = LocalDateTime.now();
        final AdminTerminationFinalDecisionReasonDetails reasonDetails = AdminTerminationFinalDecisionReasonDetails.builder()
                .explanation("Explanation")
                .build();
        final AdminTerminationRequestPayload requestPayload = AdminTerminationRequestPayload.builder()
                .submitSubmissionDate(submissionDateTime)
                .adminTerminationFinalDecisionReasonDetails(reasonDetails)
                .build();

        final Map<String, Object> expected = Map.of(
                "explanation", "Explanation",
                "submissionDate", submissionDateTime
        );

        // Invoke
        Map<String, Object> actual = paramsProvider.constructParams(requestPayload);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }
}
