package uk.gov.cca.api.underlyingagreement.validation;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityValidationContext;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.config.UnderlyingAgreementConfig;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementFacilityValidatorService {

    private static final String SECTION_NAME = Facility.class.getName();

    private final FacilityDataQueryService facilityDataQueryService;
    private final UnderlyingAgreementConfig underlyingAgreementConfig;

    public void validate(final Facility facility, final UnderlyingAgreementContainer container, UnderlyingAgreementValidationContext underlyingAgreementValidationContext, List<UnderlyingAgreementViolation> violations) {
    	Map<String, FacilityValidationContext> facilityValidationContextMap = getFacilityValidationContextMap(facility);
    	
        validateStatus(facility, facilityValidationContextMap.get(facility.getFacilityItem().getFacilityId()), violations);
        validateCca3BaselineAndTargets(facility, container, violations);
        validateFacilityParticipatingSchemeVersions(facility, facilityValidationContextMap, underlyingAgreementValidationContext, violations);
    }
    
	private Map<String, FacilityValidationContext> getFacilityValidationContextMap(final Facility facility) {
		Set<String> facilityBusinessIds = new HashSet<>();

		facilityBusinessIds.add(facility.getFacilityItem().getFacilityId());
		if (facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId() != null) {
			facilityBusinessIds.add(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId());
		}

		return facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
	}

    private void validateStatus(final Facility facility, FacilityValidationContext facilityValidationContext, List<UnderlyingAgreementViolation> violations) {
        String facilityBusinessId = facility.getFacilityItem().getFacilityId();
        final boolean exists = facilityValidationContext != null;
        final boolean active = exists && facilityValidationContext.getClosedDate() == null;
        if ((facility.getStatus().equals(FacilityStatus.NEW) && exists)
                || (List.of(FacilityStatus.LIVE, FacilityStatus.EXCLUDED).contains(facility.getStatus()) && !active)) {
            violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_ID, facilityBusinessId));
        }
    }

	public void validateCca3BaselineAndTargets(final Facility facility, final UnderlyingAgreementContainer container,
                                               List<UnderlyingAgreementViolation> violations) {
        Optional.ofNullable(facility.getFacilityItem().getCca3BaselineAndTargets()).ifPresent(
                baselineAndTargets -> {
                    MeasurementType sectorMeasurementType = container.getSchemeDataMap().get(SchemeVersion.CCA_3)
                            .getSectorMeasurementType();

                    // Validate Target Composition
                    validateFacilityTargetComposition(baselineAndTargets.getTargetComposition(), sectorMeasurementType, violations);

                    // Validate Facility BaselineEnergyConsumption
                    validateFacilityBaselineEnergyConsumption(facility, violations);

                    // Validate Facility Targets
                    validateFacilityTargets(baselineAndTargets.getFacilityTargets(), SchemeVersion.CCA_3, violations);
                }
        );
    }

    private void validateFacilityTargetComposition(final FacilityTargetComposition targetComposition, MeasurementType sectorMeasurementType,
                                                   List<UnderlyingAgreementViolation> violations) {
        if (!ObjectUtils.isEmpty(targetComposition)) {
            // Validate measurement type with sector/subsector scheme
            Optional.ofNullable(targetComposition.getMeasurementType()).ifPresent(measurementType -> {
                if (!measurementType.getCategory().equals(sectorMeasurementType.getCategory())) {
                    violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_TARGET_UNIT_TYPE, measurementType));
                }
            });

            // Validate if NOVEM
            if (!AgreementCompositionType.NOVEM.equals(targetComposition.getAgreementCompositionType())) {
                violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_AGREEMENT_COMPOSITION_TYPE, targetComposition.getAgreementCompositionType()));
            }
        }
    }

    private void validateFacilityTargets(final FacilityTargets targets, SchemeVersion schemeVersion, final List<UnderlyingAgreementViolation> violations) {
        if (!ObjectUtils.isEmpty(targets)) {
            Set<TargetImprovementType> improvements = TargetImprovementType.getImprovementsBySchemeVersion(schemeVersion);
            Set<TargetImprovementType> diffs = SetUtils.disjunction(improvements, targets.getImprovements().keySet());

            if (!diffs.isEmpty()) {
                violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_TARGETS, diffs));
            }
        }
    }

    public void validateFacilityParticipatingSchemeVersions(final Facility facility, Map<String, FacilityValidationContext> facilityValidationContextMap, UnderlyingAgreementValidationContext underlyingAgreementValidationContext, List<UnderlyingAgreementViolation> violations) {
        final Set<SchemeVersion> schemeVersions = facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions();
        final ApplicationReasonType applicationReason = facility.getFacilityItem().getFacilityDetails().getApplicationReason();
        final FacilityStatus status = facility.getStatus();
        final LocalDate requestCreationDate = underlyingAgreementValidationContext.getRequestCreationDate().toLocalDate();

        // Facility scheme cannot be greater than current scheme
        validateFacilitySchemeAgainstCurrentScheme(underlyingAgreementValidationContext, violations, schemeVersions);
        
        validateFacilitySchemeForNewAgreement(underlyingAgreementValidationContext, violations, schemeVersions,
				applicationReason, status);

        validateFacilitySchemeForChangeOfOwnership(facility, facilityValidationContextMap, violations, schemeVersions,
				applicationReason, requestCreationDate);
    }
    
    private void validateFacilitySchemeAgainstCurrentScheme(
			UnderlyingAgreementValidationContext underlyingAgreementValidationContext,
			List<UnderlyingAgreementViolation> violations, final Set<SchemeVersion> schemeVersions) {
		schemeVersions.stream()
                .reduce((a, b) -> a.getVersion() > b.getVersion() ? a : b)
                .ifPresent(facilityScheme -> {
                    if (facilityScheme.getVersion() > underlyingAgreementValidationContext.getSchemeVersion().getVersion()) {
                        violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME, facilityScheme));
                    }
                });
	}
    
    private void validateFacilitySchemeForNewAgreement(
			UnderlyingAgreementValidationContext underlyingAgreementValidationContext,
			List<UnderlyingAgreementViolation> violations, final Set<SchemeVersion> schemeVersions,
			final ApplicationReasonType applicationReason, final FacilityStatus status) {
		if (FacilityStatus.NEW.equals(status) && ApplicationReasonType.NEW_AGREEMENT.equals(applicationReason)
                && !SetUtils.difference(schemeVersions, Set.of(underlyingAgreementValidationContext.getSchemeVersion())).isEmpty()) {
            violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME, schemeVersions));
        }
	}

	private void validateFacilitySchemeForChangeOfOwnership(final Facility facility,
			Map<String, FacilityValidationContext> facilityValidationContextMap,
			List<UnderlyingAgreementViolation> violations, final Set<SchemeVersion> schemeVersions,
			final ApplicationReasonType applicationReason, final LocalDate requestCreationDate) {
		if (ApplicationReasonType.CHANGE_OF_OWNERSHIP.equals(applicationReason)) {
            Optional.ofNullable(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
                    .ifPresent(previousFacilityId -> {
                    	
						Set<SchemeVersion> previousSchemeVersions = facilityValidationContextMap.get(previousFacilityId) == null
								? Collections.emptySet()
								: facilityValidationContextMap.get(previousFacilityId).getParticipatingSchemeVersions();

                        if (!requestCreationDate.isBefore(underlyingAgreementConfig.getSchemeParticipationFlagCutOffDate())) {
                            if (!previousSchemeVersions.equals(schemeVersions)) {
                                violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE, schemeVersions));
                            }
                        } else {
                            int minPreviousScheme = previousSchemeVersions.stream()
                                    .mapToInt(SchemeVersion::getVersion).min().orElse(0);
                            int minCurrentScheme = schemeVersions.stream()
                                    .mapToInt(SchemeVersion::getVersion).min().orElse(0);

                            if (minCurrentScheme < minPreviousScheme) {
                                violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE, schemeVersions));
                            }
                        }
                    });
        }
	}

    private void validateFacilityBaselineEnergyConsumption(final Facility facility, final List<UnderlyingAgreementViolation> violations) {
        // Validate that there is at least one non EXCLUDED product
        Optional.ofNullable(facility.getFacilityItem().getCca3BaselineAndTargets().getFacilityBaselineEnergyConsumption()).ifPresent(energyConsumption -> {
            List<ProductVariableEnergyConsumptionData> variableEnergyConsumptionDataByProduct = energyConsumption.getVariableEnergyConsumptionDataByProduct();
            if (!variableEnergyConsumptionDataByProduct.isEmpty()
                    && variableEnergyConsumptionDataByProduct.stream().allMatch(p -> p.getProductStatus() == ProductStatus.EXCLUDED)) {

                violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT));
            }

            if (facility.getStatus().equals(FacilityStatus.NEW) && !variableEnergyConsumptionDataByProduct.isEmpty()
                    && !variableEnergyConsumptionDataByProduct.stream().allMatch(p -> p.getProductStatus() == ProductStatus.NEW)) {
                violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT_STATUS));
            }

            // Validate product unique names
            Set<String> productNames = variableEnergyConsumptionDataByProduct.stream()
                    .map(ProductVariableEnergyConsumptionData::getProductName).collect(Collectors.toSet());
            if(variableEnergyConsumptionDataByProduct.size() != productNames.size()) {
                violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_UNIQUE_PRODUCT_NAME));
            }
        });
    }
}
