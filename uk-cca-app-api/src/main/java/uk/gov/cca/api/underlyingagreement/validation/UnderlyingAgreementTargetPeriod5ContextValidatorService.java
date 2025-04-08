package uk.gov.cca.api.underlyingagreement.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGETS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UnderlyingAgreementTargetPeriod5ContextValidatorService extends UnderlyingAgreementSectionConstraintValidatorService<TargetPeriod5Details> implements UnderlyingAgreementSectionContextValidator {

    private final UnderlyingAgreementTargetPeriod6ContextValidatorService underlyingAgreementTargetPeriod6ContextValidatorService;

    public UnderlyingAgreementTargetPeriod5ContextValidatorService(DataValidator<TargetPeriod5Details> validator, UnderlyingAgreementTargetPeriod6ContextValidatorService underlyingAgreementTargetPeriod6ContextValidatorService) {
        super(validator);
        this.underlyingAgreementTargetPeriod6ContextValidatorService = underlyingAgreementTargetPeriod6ContextValidatorService;
    }

    @Override
    public BusinessValidationResult validate(UnderlyingAgreementContainer container) {
        TargetPeriod5Details section = container.getUnderlyingAgreement().getTargetPeriod5Details();

        List<UnderlyingAgreementViolation> violations = super.validateEmptySection(section);
        if(violations.isEmpty()){
            violations = this.validateSection(section, container);
            
            // Validate calculated target for ABSOLUTE and TP5
            validateTargetTP5(section, violations);
        }

        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }

    @Override
    protected List<UnderlyingAgreementViolation> validateSection(TargetPeriod5Details section, UnderlyingAgreementContainer container) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        // Invoke validation manually to reduce duplications from TargetPeriod6 validations
        if((Boolean.TRUE.equals(section.getExist()) && ObjectUtils.isEmpty(section.getDetails()))
                || (Boolean.FALSE.equals(section.getExist()) && !ObjectUtils.isEmpty(section.getDetails()))) {
            violations.add(new UnderlyingAgreementViolation(this.getSectionName(), UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_PERIOD));
        }

        if(Boolean.TRUE.equals(section.getExist()) && !ObjectUtils.isEmpty(section.getDetails())) {
            violations.addAll(underlyingAgreementTargetPeriod6ContextValidatorService.validateSection(section.getDetails(), container));
        }

        return violations;
    }

    @Override
    protected String getSectionName() {
        return TargetPeriod5Details.class.getName();
    }
    
    private void validateTargetTP5(TargetPeriod5Details section, List<UnderlyingAgreementViolation> violations) {
		if(!ObjectUtils.isEmpty(section.getDetails()) && 
				AgreementCompositionType.ABSOLUTE.equals(section.getDetails().getTargetComposition().getAgreementCompositionType()) &&
				UnderlyingAgreementValidationHelper.calculateTarget(
						section.getDetails().getBaselineData().getEnergy().multiply(BigDecimal.TWO), section.getDetails().getTargets().getImprovement())
				.compareTo(section.getDetails().getTargets().getTarget()) != 0) {
		    violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_TARGETS, section.getDetails().getTargets().getTarget()));
		}
	}
}
