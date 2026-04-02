package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.CcaDecisionNotificationUsersValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NoticeOfIntent;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeOfIntentSubmitNotifyOperatorValidatorTest {

    @InjectMocks
    private NoticeOfIntentSubmitNotifyOperatorValidator noticeOfIntentSubmitNotifyOperatorValidator;

    @Mock
    private NoticeOfIntentSubmitValidator noticeOfIntentSubmitValidator;

    @Mock
    private DecisionNotificationValidator decisionNotificationValidator;

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

        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator")).build();
        final NotifyOperatorForDecisionRequestTaskActionPayload requestTaskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .payloadType(CcaRequestTaskActionPayloadType.NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
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

        when(noticeOfIntentSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.valid());
        when(decisionNotificationValidator.validate(requestTask, decisionNotification, appUser)).thenReturn(BusinessValidationResult.valid());

        // invoke
        noticeOfIntentSubmitNotifyOperatorValidator.validate(requestTask, requestTaskActionPayload, appUser);

        // verify
        verify(noticeOfIntentSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(decisionNotificationValidator, times(1)).validate(requestTask, decisionNotification, appUser);
    }

    @Test
    void validate_not_valid() {
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
        final UUID fileUuid = UUID.randomUUID();
        final NoticeOfIntent noticeOfIntent = NoticeOfIntent.builder()
                .noticeOfIntentFile(fileUuid)
                .comments("bla bla bla")
                .build();

        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator")).build();
        final NotifyOperatorForDecisionRequestTaskActionPayload requestTaskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .payloadType(CcaRequestTaskActionPayloadType.NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
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

        when(noticeOfIntentSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.valid());
        when(decisionNotificationValidator.validate(requestTask, decisionNotification, appUser))
                .thenReturn(BusinessValidationResult.invalid(List.of(new DecisionNotificationViolation(CcaDecisionNotificationUsersValidator.class.getName(),
                DecisionNotificationViolation.DecisionNotificationViolationMessage.INVALID_NOTIFICATION_USERS))));

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class,
                        () -> noticeOfIntentSubmitNotifyOperatorValidator.validate(requestTask, requestTaskActionPayload, appUser));

        // verify
        assertThat(CcaErrorCode.INVALID_NON_COMPLIANCE).isEqualTo(businessException.getErrorCode());
        verify(noticeOfIntentSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(decisionNotificationValidator, times(1)).validate(requestTask, decisionNotification, appUser);
    }
}
