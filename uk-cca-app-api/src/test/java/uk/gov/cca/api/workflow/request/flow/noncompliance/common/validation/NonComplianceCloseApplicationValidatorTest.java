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
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseJustification;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;

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
class NonComplianceCloseApplicationValidatorTest {

    @InjectMocks
    private NonComplianceCloseApplicationValidator nonComplianceCloseApplicationValidator;

    @Mock
    private DataValidator<NonComplianceCloseJustification> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceCloseJustification closeJustification = NonComplianceCloseJustification.builder()
                .reason("reason")
                .files(Set.of(fileUuid))
                .build();
        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                .closeJustification(closeJustification)
                .nonComplianceAttachments(Map.of(fileUuid, "attachmentName"))
                .build();


        when(dataValidator.validate(closeJustification)).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);

        // invoke
        nonComplianceCloseApplicationValidator.validate(requestTaskPayload);

        // verify
        verify(dataValidator, times(1)).validate(closeJustification);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
    }

    @Test
    void validate_not_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceCloseJustification closeJustification = NonComplianceCloseJustification.builder()
                .reason(null)
                .files(Set.of(fileUuid))
                .build();
        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                .closeJustification(closeJustification)
                .nonComplianceAttachments(Map.of(fileUuid, "attachmentName"))
                .build();


        when(dataValidator.validate(closeJustification)).thenReturn(Optional.of(new NonComplianceViolation(NonComplianceCloseJustification.class.getName(),
                NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_CLOSURE_DATA)));
        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class,
                        () -> nonComplianceCloseApplicationValidator.validate(requestTaskPayload));

        // verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_NON_COMPLIANCE);
        verify(dataValidator, times(1)).validate(closeJustification);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
    }
}
