package uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceAppealDetailsSubmitValidatorTest {

    @InjectMocks
    private NonComplianceAppealDetailsSubmitValidator validator;

    @Mock
    private DataValidator<NonComplianceAppealDetails> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealDetails appealDetails = NonComplianceAppealDetails.builder()
                .files(Set.of(fileUuid))
                .registrationDate(LocalDate.now().minusDays(1))
                .comments("bla bla bla")
                .build();
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .appealDetails(appealDetails)
                .nonComplianceAttachments(Map.of(fileUuid, "attachmentName"))
                .build();

        when(dataValidator.validate(appealDetails)).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);

        // invoke
        validator.validate(requestTaskPayload);

        // verify
        verify(dataValidator, times(1)).validate(appealDetails);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
    }

    @Test
    void validate_not_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealDetails appealDetails = NonComplianceAppealDetails.builder()
                .files(Set.of(fileUuid))
                .registrationDate(null)
                .comments("bla bla bla")
                .build();
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .appealDetails(appealDetails)
                .nonComplianceAttachments(Map.of(fileUuid, "attachmentName"))
                .build();

        when(dataValidator.validate(appealDetails)).thenReturn(Optional.of(new NonComplianceViolation(NonComplianceAppealDetails.class.getName(),
                NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_APPEAL_DETAILS)));
        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class,
                        () -> validator.validate(requestTaskPayload));

        // verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_NON_COMPLIANCE);
        verify(dataValidator, times(1)).validate(appealDetails);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
    }
}
