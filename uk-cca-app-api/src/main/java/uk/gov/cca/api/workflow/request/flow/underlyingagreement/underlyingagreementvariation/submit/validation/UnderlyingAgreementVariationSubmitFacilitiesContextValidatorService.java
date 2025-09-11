package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITIES;

@Service
public class UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService {

    public BusinessValidationResult validate(final UnderlyingAgreementContainer container) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        final Set<Facility> facilities = container.getUnderlyingAgreement().getFacilities();

        final boolean hasAtLeastOneActiveFacility =
                facilities.stream().anyMatch(f -> !f.getStatus().equals(FacilityStatus.EXCLUDED));

        if (!hasAtLeastOneActiveFacility) {
            violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_FACILITIES));
        }

        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }

    private String getSectionName() {
        return Facility.class.getName();
    }
}
