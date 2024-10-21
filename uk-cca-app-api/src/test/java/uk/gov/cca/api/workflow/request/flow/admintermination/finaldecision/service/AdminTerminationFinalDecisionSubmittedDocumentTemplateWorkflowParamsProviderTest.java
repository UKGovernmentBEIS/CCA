package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalDecisionSubmittedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private AdminTerminationFinalDecisionSubmittedDocumentTemplateWorkflowParamsProvider paramsProvider;

    @Test
    void getContextActionType() {
        assertThat(paramsProvider.getContextActionType())
                .isEqualTo(CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_FINALISED);
    }

    @Test
    void constructParams() {
        final AdminTerminationReasonDetails reasonDetails = AdminTerminationReasonDetails.builder()
                .reason(AdminTerminationReason.DATA_NOT_PROVIDED)
                .build();
        final AdminTerminationRequestPayload requestPayload = AdminTerminationRequestPayload.builder()
                .underlyingAgreementVersion(1)
                .adminTerminationReasonDetails(reasonDetails)
                .build();

        final Map<String, Object> expected = Map.of(
                "reasonDetails", reasonDetails,
                "version", "v1"
        );

        // Invoke
        Map<String, Object> actual = paramsProvider.constructParams(requestPayload);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }
}
