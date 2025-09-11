package uk.gov.cca.api.underlyingagreement.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public abstract class UnderlyingAgreementSectionConstraintValidatorService<T extends UnderlyingAgreementSection> {

    private final DataValidator<T> validator;

    protected abstract List<UnderlyingAgreementViolation> validateSection(T section, UnderlyingAgreementContainer container, UnderlyingAgreementValidationContext underlyingAgreementValidationContext);

    protected abstract String getSectionName();

    public Optional<UnderlyingAgreementViolation> validate(T section) {
        return validator.validate(section).map(businessViolation -> new UnderlyingAgreementViolation(
                this.getSectionName(),
                UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA,
                businessViolation.getData()));
    }

    public List<UnderlyingAgreementViolation> validateEmptySection(T section) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        if(ObjectUtils.isEmpty(section)) {
            violations.add(new UnderlyingAgreementViolation(this.getSectionName(),
                    UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA));
        }

        return violations;
    }
}
