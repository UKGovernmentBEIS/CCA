package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonCompliancePenaltyOutcomeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceWithdrawNotice;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceConclusionSubmitValidatorTest {

    @InjectMocks
    private NonComplianceConclusionSubmitValidator nonComplianceConclusionSubmitValidator;

    @Mock
    private DataValidator<NonComplianceConclusion> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate_valid() {
        final UUID fileUuid = UUID.randomUUID();
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
                .build();

        when(dataValidator.validate(nonComplianceConclusion)).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);
        // invoke
        nonComplianceConclusionSubmitValidator.validate(requestTaskPayload);

        // verify
        verify(dataValidator, times(1)).validate(nonComplianceConclusion);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
    }

    @Test
    void validate_not_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.NONE)
                        .penaltyPaid(true)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(null)
                .build();
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .build();


        when(dataValidator.validate(nonComplianceConclusion)).thenReturn(Optional.of(new NonComplianceViolation(NonComplianceConclusion.class.getName(),
                NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_CONCLUSION_DATA)));

        // invoke
        BusinessValidationResult result = nonComplianceConclusionSubmitValidator.validate(requestTaskPayload);

        // verify
        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(nonComplianceConclusion);
        verify(fileAttachmentsExistenceValidator, never()).valid(Set.of(fileUuid), Set.of(fileUuid));
    }
}