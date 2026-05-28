package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.CcaDecisionNotificationUsersValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonCompliancePenaltyOutcomeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceWithdrawNotice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;

import java.time.LocalDate;
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
class NonComplianceConclusionNotifyOperatorValidatorTest {

    @InjectMocks
    private NonComplianceConclusionNotifyOperatorValidator nonComplianceConclusionNotifyOperatorValidator;

    @Mock
    private NonComplianceConclusionSubmitValidator nonComplianceConclusionSubmitValidator;

    @Mock
    private DecisionNotificationValidator decisionNotificationValidator;

    @Test
    void validate_valid() {
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
        final UUID fileUuid = UUID.randomUUID();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");

        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .penaltyReissueNeeded(false)
                .build();

        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.WITHDRAW)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(NonComplianceWithdrawNotice.builder()
                        .file(UUID.randomUUID())
                        .comments("bla bla")
                        .build())
                .build();

        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator")).build();
        final NotifyOperatorForDecisionRequestTaskActionPayload requestTaskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .payloadType(CcaRequestTaskActionPayloadType.NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .build();

        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .sectionsCompleted(sectionsCompleted)
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

        when(nonComplianceConclusionSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.valid());
        when(decisionNotificationValidator.validate(requestTask, decisionNotification, appUser)).thenReturn(BusinessValidationResult.valid());

        // invoke
        nonComplianceConclusionNotifyOperatorValidator.validate(requestTask, requestTaskActionPayload, appUser);

        // verify
        verify(nonComplianceConclusionSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(decisionNotificationValidator, times(1)).validate(requestTask, decisionNotification, appUser);
    }

    @Test
    void validate_not_valid() {
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
        final UUID fileUuid = UUID.randomUUID();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .penaltyReissueNeeded(false)
                .build();
        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.WITHDRAW)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(NonComplianceWithdrawNotice.builder()
                        .file(UUID.randomUUID())
                        .comments("bla bla")
                        .build())
                .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator")).build();
        final NotifyOperatorForDecisionRequestTaskActionPayload requestTaskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .payloadType(CcaRequestTaskActionPayloadType.NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .build();

        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .sectionsCompleted(sectionsCompleted)
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

        when(nonComplianceConclusionSubmitValidator.validate(requestTaskPayload)).thenReturn(BusinessValidationResult.valid());
        when(decisionNotificationValidator.validate(requestTask, decisionNotification, appUser))
                .thenReturn(BusinessValidationResult.invalid(List.of(new DecisionNotificationViolation(CcaDecisionNotificationUsersValidator.class.getName(),
                        DecisionNotificationViolation.DecisionNotificationViolationMessage.INVALID_NOTIFICATION_USERS))));

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class,
                        () -> nonComplianceConclusionNotifyOperatorValidator.validate(requestTask, requestTaskActionPayload, appUser));

        // verify
        assertThat(CcaErrorCode.INVALID_NON_COMPLIANCE).isEqualTo(businessException.getErrorCode());
        verify(nonComplianceConclusionSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(decisionNotificationValidator, times(1)).validate(requestTask, decisionNotification, appUser);
    }
}