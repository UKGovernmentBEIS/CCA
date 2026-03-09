package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationRegulatorLedSubmitRequestPeerReviewValidator {

    private final UnderlyingAgreementVariationRegulatorLedSubmitValidatorService underlyingAgreementVariationRegulatorLedSubmitValidatorService;
    private final CcaPeerReviewValidator peerReviewValidator;

    public void validate(final RequestTask requestTask,
                         final PeerReviewRequestTaskActionPayload payload,
                         final AppUser appUser) {

        // Validate regulator led submit
        List<BusinessValidationResult> validationResults = underlyingAgreementVariationRegulatorLedSubmitValidatorService
                .validateSubmit(requestTask);

        // Validate peer reviewer
        validationResults.add(peerReviewValidator
                .validate(requestTask, payload, appUser, CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
