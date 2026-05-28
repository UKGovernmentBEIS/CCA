package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;

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
class EnforcementResponseNoticeSubmitValidatorTest {

    @InjectMocks
    private EnforcementResponseNoticeSubmitValidator enforcementResponseNoticeSubmitValidator;

    @Mock
    private DataValidator<NonComplianceEnforcementResponseNotice> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(fileUuid)
                .comments("bla bla bla")
                .build();
        final NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload requestTaskPayload = NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD)
                .enforcementResponseNotice(enforcementResponseNotice)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .build();

        when(dataValidator.validate(enforcementResponseNotice)).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(fileUuid), Set.of(fileUuid))).thenReturn(true);
        // invoke
        enforcementResponseNoticeSubmitValidator.validate(requestTaskPayload);

        // verify
        verify(dataValidator, times(1)).validate(enforcementResponseNotice);
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(fileUuid), Set.of(fileUuid));
    }

    @Test
    void validate_not_valid() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .penaltyReissueNeeded(false)
                .build();
        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(null)
                .comments("bla bla bla")
                .build();
        final NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload requestTaskPayload = NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD)
                .enforcementResponseNotice(enforcementResponseNotice)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .build();


        when(dataValidator.validate(enforcementResponseNotice)).thenReturn(Optional.of(new NonComplianceViolation(NonComplianceEnforcementResponseNotice.class.getName(),
                NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_DATA)));

        // invoke
        BusinessValidationResult result = enforcementResponseNoticeSubmitValidator.validate(requestTaskPayload);

        // verify
        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(enforcementResponseNotice);
        verify(fileAttachmentsExistenceValidator, never()).valid(Set.of(fileUuid), Set.of(fileUuid));
    }
}
