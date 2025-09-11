package uk.gov.cca.api.underlyingagreement.validation;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITIES;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNIQUE_FACILITY_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;

@Service
public class UnderlyingAgreementFacilitiesContextValidatorService extends UnderlyingAgreementSectionConstraintValidatorService<Facility> implements UnderlyingAgreementSectionContextValidator {

	private final UnderlyingAgreementFacilityValidatorService underlyingAgreementFacilityValidatorService;

    public UnderlyingAgreementFacilitiesContextValidatorService(
			DataValidator<Facility> validator, UnderlyingAgreementFacilityValidatorService underlyingAgreementFacilityValidatorService) {
        super(validator);
        this.underlyingAgreementFacilityValidatorService = underlyingAgreementFacilityValidatorService;
    }

    @Override
    public BusinessValidationResult validate(final UnderlyingAgreementContainer container, UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {
    	Set<Facility> facilities = container.getUnderlyingAgreement().getFacilities();

        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        if (facilities.isEmpty()) {
        	violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_FACILITIES));
        }
        if(violations.isEmpty()){
			validateUniqueIds(facilities, violations);
        	facilities.forEach(section -> violations.addAll(this.validateSection(section, container, underlyingAgreementValidationContext)));
        }

        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }

    @Override
    protected List<UnderlyingAgreementViolation> validateSection(final Facility section, final UnderlyingAgreementContainer container, UnderlyingAgreementValidationContext underlyingAgreementValidationContext) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        // Validate data
        super.validate(section).ifPresent(violation -> {
			List<Object> list = new ArrayList<>(Arrays.asList(violation.getData()));
            list.add(section.getFacilityItem().getFacilityId());
            violation.setData(list.toArray());
            violations.add(violation);
		});

        // Validate business
        underlyingAgreementFacilityValidatorService.validate(section, container, underlyingAgreementValidationContext, violations);

        return violations;
    }

	@Override
    protected String getSectionName() {
        return Facility.class.getName();
    }

	private void validateUniqueIds(Set<Facility> facilities, List<UnderlyingAgreementViolation> violations) {
		Set<String> duplicated = facilities.stream()
				.collect(Collectors.groupingBy(facility -> facility.getFacilityItem().getFacilityId()))
				.entrySet().stream().filter(e -> e.getValue().size() > 1)
				.flatMap(e -> e.getValue().stream())
				.map(facility -> facility.getFacilityItem().getFacilityId())
				.collect(Collectors.toSet());

		if(!duplicated.isEmpty()) {
			violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_UNIQUE_FACILITY_ID, duplicated));
		}
	}
}
