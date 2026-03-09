package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationViolation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_FACILITY_CHARGE_START_DATE;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService extends UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService {

    public List<BusinessValidationResult> validate(final UnderlyingAgreementContainer container, Map<String, LocalDate> facilityChargeStartDateMap) {
        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate facilities at least one active
        validationResults.add(this.validate(container));

        // Validate new facilities for charge start date
        Set<String> facilityIds = container.getUnderlyingAgreement().getFacilities().stream()
                .filter(f -> f.getStatus().equals(FacilityStatus.NEW))
                .map(f -> f.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> diff = SetUtils.difference(facilityChargeStartDateMap.keySet(), facilityIds);

        BusinessValidationResult diffResult = diff.isEmpty()
                ? BusinessValidationResult.valid()
                : BusinessValidationResult.invalid(List.of(new UnderlyingAgreementVariationViolation(this.getSectionName(), INVALID_FACILITY_CHARGE_START_DATE, diff)));
        validationResults.add(diffResult);

        return validationResults;
    }
}
