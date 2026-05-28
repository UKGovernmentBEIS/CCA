package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonCompliancePenaltyOutcomeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceWithdrawNotice;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceConclusionCompleteValidatorTest {

    @InjectMocks
    private NonComplianceConclusionCompleteValidator nonComplianceConclusionCompleteValidator;

    @Mock
    private NonComplianceConclusionSubmitValidator nonComplianceConclusionSubmitValidator;

    @Test
    void validate_valid() {
        final UUID fileUuid = UUID.randomUUID();
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
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .sectionsCompleted(sectionsCompleted)
                .build();

        when(nonComplianceConclusionSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.valid());

        // invoke
        nonComplianceConclusionCompleteValidator.validate(requestTaskPayload);

        // verify
        verify(nonComplianceConclusionSubmitValidator, times(1)).validate(requestTaskPayload);
    }

    @Test
    void validate_not_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
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
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .sectionsCompleted(sectionsCompleted)
                .build();

        when(nonComplianceConclusionSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.valid());


        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class,
                        () -> nonComplianceConclusionCompleteValidator.validate(requestTaskPayload));

        // verify
        assertThat(CcaErrorCode.INVALID_NON_COMPLIANCE).isEqualTo(businessException.getErrorCode());
        verify(nonComplianceConclusionSubmitValidator, times(1)).validate(requestTaskPayload);
    }
}
