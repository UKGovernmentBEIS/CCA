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

    public void validate(UnderlyingAgreementContainer container, UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {
        List<BusinessValidationResult> validationResults = getValidationResults(container, underlyingAgreementValidationContext);
        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, ValidatorHelper.extractViolations(validationResults));
        }
    }

    public List<BusinessValidationResult> getValidationResults(UnderlyingAgreementContainer container, UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {

        return invokeContextValidators(underlyingAgreementSectionContextValidators, container, underlyingAgreementValidationContext);
    }
    
    public List<BusinessValidationResult> getValidationResultsExceptFacilities(UnderlyingAgreementContainer container, UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {

    	// Remove facilities validator
    	List<UnderlyingAgreementSectionContextValidator> validatorsWithoutFacilities =
    			underlyingAgreementSectionContextValidators.stream()
    			.filter(validator -> !(validator instanceof UnderlyingAgreementFacilitiesContextValidatorService))
    			.toList();
    	
        return invokeContextValidators(validatorsWithoutFacilities, container, underlyingAgreementValidationContext);
    }
    
    private List<BusinessValidationResult> invokeContextValidators(
    		List<UnderlyingAgreementSectionContextValidator> underlyingAgreementSectionContextValidators,
    		UnderlyingAgreementContainer container,
            UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {
    	
        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Perform validations
        underlyingAgreementSectionContextValidators.forEach(v -> validationResults.add(v.validate(container, underlyingAgreementValidationContext)));

        return validationResults;
    }
}
