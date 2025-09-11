package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationReviewDecisionDataValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewRequestPeerReviewValidator {

    private final UnderlyingAgreementVariationReviewValidatorService underlyingAgreementVariationReviewValidatorService;
    private final UnderlyingAgreementVariationReviewDecisionDataValidator underlyingAgreementVariationReviewDecisionDataValidator;
    private final CcaPeerReviewValidator peerReviewValidator;

    public void validate(final RequestTask requestTask,
                         final PeerReviewRequestTaskActionPayload payload,
                         final AppUser appUser) {

        final List<BusinessValidationResult> validationResults =
                new ArrayList<>(underlyingAgreementVariationReviewValidatorService.validateEditedUnderlyingAgreement(requestTask));

        validationResults.add(underlyingAgreementVariationReviewDecisionDataValidator.validateReviewDecisionData(requestTask));

        // Validate peer reviewer
        validationResults.add(peerReviewValidator
                .validate(requestTask, payload, appUser, CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION_REVIEW, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
