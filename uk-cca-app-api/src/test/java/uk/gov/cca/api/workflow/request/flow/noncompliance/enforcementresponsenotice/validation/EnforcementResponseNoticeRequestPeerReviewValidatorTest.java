package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseJustification;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnforcementResponseNoticeRequestPeerReviewValidatorTest {

    @InjectMocks
    private EnforcementResponseNoticeRequestPeerReviewValidator enforcementResponseNoticeRequestPeerReviewValidator;

    @Mock
    private EnforcementResponseNoticeSubmitValidator enforcementResponseNoticeSubmitValidator;

    @Mock
    private CcaPeerReviewValidator peerReviewValidator;

    @Test
    void validate_valid() {
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
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
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .penaltyReissueNeeded(false)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();
        final PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer("peerReviewer")
                .build();

        when(enforcementResponseNoticeSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.valid());
        when(peerReviewValidator.validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW))
                .thenReturn(BusinessValidationResult.valid());

        // invoke
        enforcementResponseNoticeRequestPeerReviewValidator.validate(requestTask, payload, appUser);

        // verify
        verify(enforcementResponseNoticeSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(peerReviewValidator, times(1)).validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW);
    }

    @Test
    void validate_not_valid() {
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
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
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .penaltyReissueNeeded(false)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();
        final PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer("peerReviewer")
                .build();

        when(enforcementResponseNoticeSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.invalid(List.of(new NonComplianceViolation(NonComplianceCloseJustification.class.getName(),
                NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_DATA))));
        when(peerReviewValidator.validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW))
                .thenReturn(BusinessValidationResult.valid());

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class,
                        () -> enforcementResponseNoticeRequestPeerReviewValidator.validate(requestTask, payload, appUser));

        // verify
        assertThat(CcaErrorCode.INVALID_NON_COMPLIANCE).isEqualTo(businessException.getErrorCode());
        verify(enforcementResponseNoticeSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(peerReviewValidator, times(1)).validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW);
    }
}
