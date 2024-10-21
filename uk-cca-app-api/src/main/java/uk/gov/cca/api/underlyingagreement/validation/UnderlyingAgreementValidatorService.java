package uk.gov.cca.api.underlyingagreement.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementValidatorService {

    private final List<UnderlyingAgreementSectionContextValidator> underlyingAgreementSectionContextValidators;

    public void validate(UnderlyingAgreementContainer container) {
        List<BusinessValidationResult> validationResults = getValidationResults(container);
        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, ValidatorHelper.extractViolations(validationResults));
        }
    }

    public List<BusinessValidationResult> getValidationResults(UnderlyingAgreementContainer container) {
        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Perform validations
        underlyingAgreementSectionContextValidators.forEach(v -> validationResults.add(v.validate(container)));

        return validationResults;
    }
}
