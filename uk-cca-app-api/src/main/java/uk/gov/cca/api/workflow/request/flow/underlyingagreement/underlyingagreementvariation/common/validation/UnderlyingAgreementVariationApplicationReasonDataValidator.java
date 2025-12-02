package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
public abstract class UnderlyingAgreementVariationApplicationReasonDataValidator<T extends UnderlyingAgreementVariationRequestTaskPayload> {

    public abstract UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(T taskPayload);

    public abstract String getPayloadType();

    public BusinessValidationResult validate(final T taskPayload) {
        List<UnderlyingAgreementVariationViolation> violations = new ArrayList<>();
        Set<Facility> originalFacilities = taskPayload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities();
        Map<String, Facility> facilityByBusinessId = this.getUnderlyingAgreementPayload(taskPayload).getUnderlyingAgreement().getFacilities()
        		.stream()
        		.collect(Collectors.toMap(f -> f.getFacilityItem().getFacilityId(), Function.identity()));

        originalFacilities
                .forEach(originalFacility -> {
                    boolean valid = validate(facilityByBusinessId, originalFacility);

                    if (!valid) {
                        violations.add(new UnderlyingAgreementVariationViolation(this.getPayloadType() + Facility.class.getName(),
                                UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_APPLICATION_REASON_SECTION,
                                originalFacility.getFacilityItem().getFacilityId()));
                    }
                });

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    private boolean validate(Map<String, Facility> facilityByBusinessId, Facility originalFacility) {
    	final String originalFacilityId = originalFacility.getFacilityItem().getFacilityId();
    	final Facility facilityFound = facilityByBusinessId.get(originalFacilityId);
    	
		if (facilityFound == null) {
			throw new BusinessException(RESOURCE_NOT_FOUND, originalFacilityId);
		}

        final FacilityDetails originalFacilityDetails = originalFacility.getFacilityItem().getFacilityDetails();
        final FacilityDetails facilityDetails = facilityFound.getFacilityItem().getFacilityDetails();

        final boolean sameApplicationReason = originalFacilityDetails.getApplicationReason()
                .equals(facilityDetails.getApplicationReason());

        if (!sameApplicationReason) return false;

        return ObjectUtils.nullSafeEquals(originalFacilityDetails.getPreviousFacilityId(), facilityDetails.getPreviousFacilityId());
    }
}
