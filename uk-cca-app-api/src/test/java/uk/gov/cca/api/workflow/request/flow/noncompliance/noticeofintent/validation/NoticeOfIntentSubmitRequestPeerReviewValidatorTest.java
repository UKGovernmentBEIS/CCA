package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseJustification;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NoticeOfIntent;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
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
class NoticeOfIntentSubmitRequestPeerReviewValidatorTest {

    @InjectMocks
    private NoticeOfIntentSubmitRequestPeerReviewValidator noticeOfIntentSubmitRequestPeerReviewValidator;

    @Mock
    private NoticeOfIntentSubmitValidator noticeOfIntentSubmitValidator;

    @Mock
    private CcaPeerReviewValidator peerReviewValidator;

    @Test
    void validate_valid() {
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
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
        final Request request = Request.builder().id(requestId).build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();
        final PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer("peerReviewer")
                .build();

        when(noticeOfIntentSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.valid());
        when(peerReviewValidator.validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW))
                .thenReturn(BusinessValidationResult.valid());

        // invoke
        noticeOfIntentSubmitRequestPeerReviewValidator.validate(requestTask, payload, appUser);

        // verify
        verify(noticeOfIntentSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(peerReviewValidator, times(1)).validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW);
    }

    @Test
    void validate_not_valid() {
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
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
        final Request request = Request.builder().id(requestId).build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();
        final PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer("peerReviewer")
                .build();

        when(noticeOfIntentSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.invalid(List.of(new NonComplianceViolation(NonComplianceCloseJustification.class.getName(),
                NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_NOTICE_OF_INTENT_DATA))));
        when(peerReviewValidator.validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW))
                .thenReturn(BusinessValidationResult.valid());

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class,
                        () -> noticeOfIntentSubmitRequestPeerReviewValidator.validate(requestTask, payload, appUser));

        // verify
        assertThat(CcaErrorCode.INVALID_NON_COMPLIANCE).isEqualTo(businessException.getErrorCode());
        verify(noticeOfIntentSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(peerReviewValidator, times(1)).validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW);
    }
}
