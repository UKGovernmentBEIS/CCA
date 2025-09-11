package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewNotifyOperatorValidatorTest {

    @InjectMocks
    private UnderlyingAgreementReviewNotifyOperatorValidator underlyingAgreementSubmitNotifyOperatorValidator;

    @Mock
    private DecisionNotificationValidator decisionNotificationValidator;

    @Mock
    private UnderlyingAgreementReviewValidatorService underlyingAgreementReviewValidatorService;

    @Mock
    private UnderlyingAgreementReviewDecisionDataValidator underlyingAgreementReviewDecisionDataValidator;

    @Test
    void validate() {
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();
        final AppUser appUser = AppUser.builder().userId("user").build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());

        when(underlyingAgreementReviewValidatorService.validateEditedUnderlyingAgreement(requestTask))
                .thenReturn(validationResults);
        when(underlyingAgreementReviewValidatorService.validateProposedUnderlyingAgreement(requestTask))
                .thenReturn(validationResults);
        when(underlyingAgreementReviewDecisionDataValidator.validateReviewDecisionData(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(decisionNotificationValidator.validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        underlyingAgreementSubmitNotifyOperatorValidator.validate(requestTask, payload, appUser);

        // Verify
        verify(underlyingAgreementReviewValidatorService, times(1))
                .validateEditedUnderlyingAgreement(requestTask);
        verify(underlyingAgreementReviewValidatorService, times(1))
                .validateProposedUnderlyingAgreement(requestTask);
        verify(underlyingAgreementReviewDecisionDataValidator, times(1))
                .validateReviewDecisionData(requestTask);
        verify(decisionNotificationValidator, times(1))
                .validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser);
    }

}
