package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealTribunalDecision;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceAppealOutcomeSubmitValidatorTest {

    @InjectMocks
    private NonComplianceAppealOutcomeSubmitValidator validator;

    @Mock
    private DataValidator<NonComplianceAppealOutcomeDetails> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealOutcomeDetails appealOutcome = NonComplianceAppealOutcomeDetails.builder()
                .file(fileUuid)
                .appealOutcomeDate(LocalDate.now())
                .tribunalDecision(NonComplianceAppealTribunalDecision.APPEAL_ALLOWED)
                .comments("bla bla")
                .build();
        final NonComplianceAppealOutcomeSubmitRequestTaskPayload requestTaskPayload = NonComplianceAppealOutcomeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMIT_PAYLOAD)
                .appealOutcome(appealOutcome)
                .nonComplianceAttachments(Map.of(fileUuid, "file"))
                .build();

        when(dataValidator.validate(appealOutcome)).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);
        // invoke
        validator.validate(requestTaskPayload);

        // verify
        verify(dataValidator, times(1)).validate(appealOutcome);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
    }

    @Test
    void validate_not_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealOutcomeDetails appealOutcome = NonComplianceAppealOutcomeDetails.builder()
                .file(fileUuid)
                .appealOutcomeDate(LocalDate.now())
                .tribunalDecision(NonComplianceAppealTribunalDecision.APPEAL_ALLOWED)
                .comments("bla bla")
                .build();

        final NonComplianceAppealOutcomeSubmitRequestTaskPayload requestTaskPayload = NonComplianceAppealOutcomeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMIT_PAYLOAD)
                .appealOutcome(appealOutcome)
                .nonComplianceAttachments(Map.of(fileUuid, "file"))
                .build();

        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);
        when(dataValidator.validate(appealOutcome))
                .thenReturn(Optional.of(new NonComplianceViolation(NonComplianceAppealOutcomeDetails.class.getName(),
                        NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_APPEAL_OUTCOME_DETAILS)));

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class,
                        () -> validator.validate(requestTaskPayload));

        // verify
        assertThat(CcaErrorCode.INVALID_NON_COMPLIANCE).isEqualTo(businessException.getErrorCode());
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
        verify(dataValidator, times(1)).validate(appealOutcome);
    }
}
