package uk.gov.cca.api.underlyingagreement.validation;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnderlyingAgreementAuthorisationAndAdditionalEvidenceContextValidatorService extends UnderlyingAgreementSectionConstraintValidatorService<AuthorisationAndAdditionalEvidence> implements UnderlyingAgreementSectionContextValidator {

    public UnderlyingAgreementAuthorisationAndAdditionalEvidenceContextValidatorService(DataValidator<AuthorisationAndAdditionalEvidence> validator) {
        super(validator);
    }

    @Override
    protected List<UnderlyingAgreementViolation> validateSection(AuthorisationAndAdditionalEvidence section, UnderlyingAgreementContainer container) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        // Validate data
        super.validate(section).ifPresent(violations::add);
        return violations;
    }

    @Override
    protected String getSectionName() {
        return AuthorisationAndAdditionalEvidence.class.getName();
    }

    @Override
    public BusinessValidationResult validate(UnderlyingAgreementContainer container) {
        AuthorisationAndAdditionalEvidence section = container.getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence();

        List<UnderlyingAgreementViolation> violations = super.validateEmptySection(section);
        if (violations.isEmpty()) {
            violations = this.validateSection(section, container);
        }

        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }
}
