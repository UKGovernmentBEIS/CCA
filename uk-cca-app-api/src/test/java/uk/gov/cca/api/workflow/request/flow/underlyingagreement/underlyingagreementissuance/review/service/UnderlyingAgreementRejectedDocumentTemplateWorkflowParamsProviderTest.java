package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_REJECTED;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementRejectedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    UnderlyingAgreementRejectedDocumentTemplateWorkflowParamsProvider paramsProvider;

    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Test
    void getContextActionType() {
        assertThat(paramsProvider.getContextActionType())
                .isEqualTo(UNDERLYING_AGREEMENT_REJECTED);
    }

    @Test
    void constructParams() {
        final Determination determination = Determination.builder()
                .reason("reason")
                .additionalInformation("additionalInformation")
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .determination(determination)
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));

        // Invoke
        Map<String, Object> actual = paramsProvider.constructParams(requestPayload);

        // Verify
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "reason", "reason"
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
    }
}
