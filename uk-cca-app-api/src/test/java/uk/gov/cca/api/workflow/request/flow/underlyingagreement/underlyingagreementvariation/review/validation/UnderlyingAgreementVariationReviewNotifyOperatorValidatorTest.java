package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

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
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationReviewDecisionDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewNotifyOperatorValidatorTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewNotifyOperatorValidator underlyingAgreementVariationReviewNotifyOperatorValidator;

    @Mock
    private DecisionNotificationValidator decisionNotificationValidator;

    @Mock
    private UnderlyingAgreementVariationReviewDecisionDataValidator underlyingAgreementVariationReviewDecisionDataValidator;

    @Mock
    private UnderlyingAgreementVariationReviewValidatorService underlyingAgreementVariationReviewValidatorService;


    @Test
    void validate() {
        final VariationDetermination determination = VariationDetermination.builder()
                .determination(Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build())
                .build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
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

        when(decisionNotificationValidator.validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationReviewDecisionDataValidator.validateReviewDecisionData(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationReviewValidatorService.validateEditedUnderlyingAgreement(requestTask))
                .thenReturn(List.of(BusinessValidationResult.valid()));
        when(underlyingAgreementVariationReviewValidatorService.validateProposedUnderlyingAgreement(requestTask))
                .thenReturn(List.of(BusinessValidationResult.valid()));

        // Invoke
        underlyingAgreementVariationReviewNotifyOperatorValidator.validate(requestTask, payload, appUser);

        // Verify
        verify(decisionNotificationValidator, times(1))
                .validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser);
        verify(underlyingAgreementVariationReviewDecisionDataValidator, times(1))
                .validateReviewDecisionData(requestTask);
        verify(underlyingAgreementVariationReviewValidatorService, times(1))
                .validateEditedUnderlyingAgreement(requestTask);
        verify(underlyingAgreementVariationReviewValidatorService, times(1))
                .validateProposedUnderlyingAgreement(requestTask);
    }


}
