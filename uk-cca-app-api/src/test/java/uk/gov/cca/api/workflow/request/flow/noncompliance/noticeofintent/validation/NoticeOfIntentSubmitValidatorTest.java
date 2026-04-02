package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.validation;

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
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NoticeOfIntent;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;

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
class NoticeOfIntentSubmitValidatorTest {

    @InjectMocks
    private NoticeOfIntentSubmitValidator noticeOfIntentSubmitValidator;

    @Mock
    private DataValidator<NoticeOfIntent> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NoticeOfIntent noticeOfIntent = NoticeOfIntent.builder()
                .noticeOfIntentFile(fileUuid)
                .comments("bla bla bla")
                .build();
        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                .noticeOfIntent(noticeOfIntent)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .build();

        when(dataValidator.validate(noticeOfIntent)).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);
        // invoke
        noticeOfIntentSubmitValidator.validate(requestTaskPayload);

        // verify
        verify(dataValidator, times(1)).validate(noticeOfIntent);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
    }

    @Test
    void validate_not_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NoticeOfIntent noticeOfIntent = NoticeOfIntent.builder()
                .noticeOfIntentFile(null)
                .comments("bla bla bla")
                .build();
        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                .noticeOfIntent(noticeOfIntent)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .build();

        when(dataValidator.validate(noticeOfIntent)).thenReturn(Optional.of(new NonComplianceViolation(NoticeOfIntent.class.getName(),
                NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_NOTICE_OF_INTENT_DATA)));

        // invoke
        BusinessValidationResult result = noticeOfIntentSubmitValidator.validate(requestTaskPayload);

        // verify
        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(noticeOfIntent);
        verify(fileAttachmentsExistenceValidator, never()).valid(Set.of(fileUuid), Set.of(fileUuid));
    }
}
