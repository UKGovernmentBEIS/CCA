package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.review.DeterminationDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewNotifyOperatorValidator {

    private final DecisionNotificationValidator decisionNotificationValidator;
    private final UnderlyingAgreementReviewDecisionDataValidator underlyingAgreementReviewDecisionDataValidator;
    private final UnderlyingAgreementFacilityReviewDecisionDataValidator underlyingAgreementFacilityReviewDecisionDataValidator;
    private final DeterminationDataValidator determinationDataValidator;

    public List<BusinessValidationResult> validate(final RequestTask requestTask,
                                                   final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload,
                                                   final AppUser appUser) {

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        validationResults.add(decisionNotificationValidator
                .validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser));

        validationResults.add(decisionNotificationValidator
                .validateUnderlyingAgreementFiles(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments()));

        validationResults.add(underlyingAgreementReviewDecisionDataValidator
                .validateUnderlyingAgreementReviewDecisions(taskPayload));

        validationResults.add(underlyingAgreementFacilityReviewDecisionDataValidator
                .validateUnderlyingAgreementFacilityReviewDecisions(taskPayload));

        validationResults.add(determinationDataValidator
                .validateDetermination(taskPayload.getDetermination()));

        return validationResults;
    }

}
