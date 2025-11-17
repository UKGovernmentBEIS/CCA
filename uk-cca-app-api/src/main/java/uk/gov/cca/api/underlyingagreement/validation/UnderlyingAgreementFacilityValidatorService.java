package uk.gov.cca.api.underlyingagreement.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
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
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_AGREEMENT_COMPOSITION_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_EVIDENCE_ATTACHMENT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT_STATUS;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_TARGETS;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_CALCULATOR_ATTACHMENT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_UNIT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNIQUE_PRODUCT_NAME;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementFacilityValidatorService {

    private static final String SECTION_NAME = Facility.class.getName();

    private final FileAttachmentService fileAttachmentService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final UnderlyingAgreementConfig underlyingAgreementConfig;

    public void validate(final Facility facility, final UnderlyingAgreementContainer container, UnderlyingAgreementValidationContext underlyingAgreementValidationContext, List<UnderlyingAgreementViolation> violations) {
        validateStatus(facility, violations);
        validateEvidenceFile(facility, violations);
        validateExistingFacilityIds(facility, violations);
        validateCca3BaselineAndTargets(facility, container, violations);
        validateFacilityParticipatingSchemeVersions(facility, underlyingAgreementValidationContext, violations);
    }

    private void validateStatus(final Facility facility, List<UnderlyingAgreementViolation> violations) {
        String facilityBusinessId = facility.getFacilityItem().getFacilityId();
        if ((facility.getStatus().equals(FacilityStatus.NEW) && facilityDataQueryService.isExistingFacilityBusinessId(facilityBusinessId))
                || (List.of(FacilityStatus.LIVE, FacilityStatus.EXCLUDED).contains(facility.getStatus())
                && !facilityDataQueryService.isActiveFacility(facilityBusinessId))) {
            violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_ID, facilityBusinessId));
        }
    }

    private void validateEvidenceFile(final Facility facility, List<UnderlyingAgreementViolation> violations) {
        // Evidence file should be XLSX or XLS
        Optional.ofNullable(facility.getFacilityItem().getApply70Rule().getEvidenceFile())
                .ifPresent(uuid -> {
                    FileDTO evidenceFile = fileAttachmentService.getFileDTO(uuid.toString());
                    if (!FileType.XLSX.getMimeTypes().contains(evidenceFile.getFileType())
                            && !FileType.XLS.getMimeTypes().contains(evidenceFile.getFileType())) {
                        violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_EVIDENCE_ATTACHMENT_TYPE));
                    }
                });
    }

    private void validateExistingFacilityIds(final Facility facility, List<UnderlyingAgreementViolation> violations) {
        // Validate previousFacilityId
        Optional.ofNullable(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
                .ifPresent(id -> {
                    if (FacilityStatus.NEW.equals(facility.getStatus()) && !facilityDataQueryService.isActiveFacility(id)) {
                        violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_PREVIOUS_FACILITY_ID, id));
                    }
                });
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

            // Calculator file should be XLSX or XLS
            Optional.ofNullable(targetComposition.getCalculatorFile())
                    .ifPresent(uuid -> {
                        FileDTO file = fileAttachmentService.getFileDTO(uuid.toString());
                        if (!FileType.XLSX.getMimeTypes().contains(file.getFileType())
                                && !FileType.XLS.getMimeTypes().contains(file.getFileType())) {
                            violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_TARGET_CALCULATOR_ATTACHMENT_TYPE));
                        }
                    });
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

    public void validateFacilityParticipatingSchemeVersions(final Facility facility, UnderlyingAgreementValidationContext underlyingAgreementValidationContext, List<UnderlyingAgreementViolation> violations) {
        final Set<SchemeVersion> schemeVersions = facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions();
        final ApplicationReasonType applicationReason = facility.getFacilityItem().getFacilityDetails().getApplicationReason();
        final FacilityStatus status = facility.getStatus();
        final LocalDate requestCreationDate = underlyingAgreementValidationContext.getRequestCreationDate().toLocalDate();

        // Facility scheme cannot be greater than current scheme
        schemeVersions.stream()
                .reduce((a, b) -> a.getVersion() > b.getVersion() ? a : b)
                .ifPresent(facilityScheme -> {
                    if (facilityScheme.getVersion() > underlyingAgreementValidationContext.getSchemeVersion().getVersion()) {
                        violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME, facilityScheme));
                    }
                });

        if (FacilityStatus.NEW.equals(status) && ApplicationReasonType.NEW_AGREEMENT.equals(applicationReason)
                && !SetUtils.difference(schemeVersions, Set.of(underlyingAgreementValidationContext.getSchemeVersion())).isEmpty()) {
            violations.add(new UnderlyingAgreementViolation(SECTION_NAME, INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME, schemeVersions));
        }

        if (ApplicationReasonType.CHANGE_OF_OWNERSHIP.equals(applicationReason)) {
            Optional.ofNullable(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
                    .ifPresent(previousFacilityId -> {
                        Set<SchemeVersion> previousSchemeVersions = facilityDataQueryService
                                .getParticipatingFacilitySchemeVersions(previousFacilityId);

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
