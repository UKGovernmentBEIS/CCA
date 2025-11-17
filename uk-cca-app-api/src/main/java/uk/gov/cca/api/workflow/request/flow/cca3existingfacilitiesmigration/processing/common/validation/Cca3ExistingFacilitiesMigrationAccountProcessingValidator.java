package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationViolation;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingValidator {

    public void validate(Cca3FacilityMigrationAccountState accountState,
                         final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload) {

        // Validate existence
        validateFacilityExistence(accountState, payload);

        // Validate facility names
        validateFacilityName(accountState, payload);

        // Validate sector measurement type
        validateSectorMeasurementType(accountState, payload);
    }

    private void validateFacilityExistence(Cca3FacilityMigrationAccountState accountState,
                                            final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload) {
        Set<String> persistedFacilities = payload.getUnderlyingAgreement().getFacilities().stream()
                .map(f -> f.getFacilityItem().getFacilityId())
                .collect(Collectors.toSet());
        Set<String> migratedFacilities = accountState.getFacilityMigrationDataList().stream()
                .map(Cca3FacilityMigrationData::getFacilityBusinessId)
                .collect(Collectors.toSet());

        Set<String> diff = SetUtils.disjunction(persistedFacilities, migratedFacilities);

        if(!diff.isEmpty()) {
            String errorMessage = String.format("%s: %s",
                    Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_FACILITIES_NOT_VALID.getMessage(),
                    diff);
            accountState.getErrors().add(errorMessage);
        }
    }

    private void validateFacilityName(Cca3FacilityMigrationAccountState accountState,
                                            final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload) {
        accountState.getFacilityMigrationDataList().forEach(facilityMigration ->
                payload.getUnderlyingAgreement().getFacilities().stream()
                        .filter(f -> f.getFacilityItem().getFacilityId().equals(facilityMigration.getFacilityBusinessId()))
                        .findFirst().ifPresent(f -> {
                            String facilityPersistedName = f.getFacilityItem().getFacilityDetails().getName();
                            if(!facilityPersistedName.equals(facilityMigration.getFacilityName())) {
                                String errorMessage = String.format("%s: %s - %s",
                                        Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_FACILITY_NAME_NOT_VALID.getMessage(),
                                        facilityPersistedName, facilityMigration.getFacilityName());
                                accountState.getErrors().add(errorMessage);
                            }
                        })
        );
    }

    private void validateSectorMeasurementType(Cca3FacilityMigrationAccountState accountState,
                                           final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload) {
        MeasurementType sectorMeasurementType = payload.getAccountReferenceData().getSectorAssociationDetails().getSchemeDataMap().get(SchemeVersion.CCA_3)
                .getSectorMeasurementType();
        accountState.getFacilityMigrationDataList().stream()
                .filter(Cca3FacilityMigrationData::getParticipatingInCca3Scheme)
                .filter(f-> !f.getMeasurementType().getCategory().equals(sectorMeasurementType.getCategory()))
                .forEach(f -> {
                    String errorMessage = String.format("%s: %s",
                            Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_FACILITY_MEASUREMENT_TYPE_NOT_VALID.getMessage(),
                            f.getMeasurementType());
                    accountState.getErrors().add(errorMessage);
                });
    }
}
