package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationViolation;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingValidatorTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingValidator validator;

    @Test
    void validate() {
        Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .facilityMigrationDataList(List.of(
                        Cca3FacilityMigrationData.builder()
                                .facilityBusinessId("facility1")
                                .facilityName("facility1Name")
                                .participatingInCca3Scheme(true)
                                .measurementType(MeasurementType.ENERGY_KWH)
                                .build(),
                        Cca3FacilityMigrationData.builder()
                                .facilityBusinessId("facility2")
                                .facilityName("facility2Name")
                                .participatingInCca3Scheme(true)
                                .measurementType(MeasurementType.ENERGY_MWH)
                                .build(),
                        Cca3FacilityMigrationData.builder()
                                .facilityBusinessId("facility3")
                                .facilityName("facility3Name")
                                .participatingInCca3Scheme(true)
                                .measurementType(MeasurementType.ENERGY_MWH)
                                .build()
                ))
                .errors(new ArrayList<>())
                .build();
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                    .accountReferenceData(AccountReferenceData.builder()
                            .sectorAssociationDetails(SectorAssociationDetails.builder()
                                    .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                                    .build())
                            .build()
                    )
                    .underlyingAgreement(UnderlyingAgreement.builder()
                            .facilities(Set.of(
                                    Facility.builder()
                                            .facilityItem(FacilityItem.builder()
                                                    .facilityId("facility1")
                                                    .facilityDetails(FacilityDetails.builder().name("facility1Name").build())
                                                    .build())
                                            .build(),
                                    Facility.builder()
                                            .facilityItem(FacilityItem.builder()
                                                    .facilityId("facility2")
                                                    .facilityDetails(FacilityDetails.builder().name("facility2Name").build())
                                                    .build())
                                            .build(),
                                    Facility.builder()
                                            .facilityItem(FacilityItem.builder()
                                                    .facilityId("facility3")
                                                    .facilityDetails(FacilityDetails.builder().name("facility3Name").build())
                                                    .build())
                                            .build()
                            ))
                            .build())
                    .build();

        // Invoke
        validator.validate(accountState, payload);

        // Verify
        assertThat(accountState.getErrors()).isEmpty();
    }

    @Test
    void validate_not_valid() {
        Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .facilityMigrationDataList(List.of(
                        Cca3FacilityMigrationData.builder()
                                .facilityBusinessId("facilityId")
                                .facilityName("facilityIdName")
                                .participatingInCca3Scheme(true)
                                .measurementType(MeasurementType.ENERGY_MWH)
                                .build(),
                        Cca3FacilityMigrationData.builder()
                                .facilityBusinessId("facility1")
                                .facilityName("facility1Name")
                                .participatingInCca3Scheme(true)
                                .measurementType(MeasurementType.CARBON_KG)
                                .build(),
                        Cca3FacilityMigrationData.builder()
                                .facilityBusinessId("facility3")
                                .facilityName("falseName")
                                .participatingInCca3Scheme(true)
                                .measurementType(MeasurementType.ENERGY_MWH)
                                .build()
                ))
                .errors(new ArrayList<>())
                .build();
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .accountReferenceData(AccountReferenceData.builder()
                                .sectorAssociationDetails(SectorAssociationDetails.builder()
                                        .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                                        .build())
                                .build())
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityId("facility1")
                                                        .facilityDetails(FacilityDetails.builder().name("facility1Name").build())
                                                        .build())
                                                .build(),
                                        Facility.builder()
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityId("facility2")
                                                        .facilityDetails(FacilityDetails.builder().name("facility2Name").build())
                                                        .build())
                                                .build(),
                                        Facility.builder()
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityId("facility3")
                                                        .facilityDetails(FacilityDetails.builder().name("facility3Name").build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build();

        // Invoke
        validator.validate(accountState, payload);

        // Verify
        assertThat(accountState.getErrors()).hasSize(3).containsExactlyInAnyOrder(
                Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_FACILITIES_NOT_VALID.getMessage() + ": [facility2, facilityId]",
                Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_FACILITY_NAME_NOT_VALID.getMessage() + ": facility3Name - falseName",
                Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_FACILITY_MEASUREMENT_TYPE_NOT_VALID.getMessage() + ": " + MeasurementType.CARBON_KG
        );
    }

    @Test
    void validate_not_all_included_not_valid() {
        Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .facilityMigrationDataList(List.of(
                        Cca3FacilityMigrationData.builder()
                                .facilityBusinessId("facility1")
                                .facilityName("facility1Name")
                                .participatingInCca3Scheme(true)
                                .measurementType(MeasurementType.ENERGY_KWH)
                                .build(),
                        Cca3FacilityMigrationData.builder()
                                .facilityBusinessId("facility3")
                                .facilityName("facility3Name")
                                .participatingInCca3Scheme(true)
                                .measurementType(MeasurementType.ENERGY_MWH)
                                .build()
                ))
                .errors(new ArrayList<>())
                .build();
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .accountReferenceData(AccountReferenceData.builder()
                                .sectorAssociationDetails(SectorAssociationDetails.builder()
                                        .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                                        .build())
                                .build())
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityId("facility1")
                                                        .facilityDetails(FacilityDetails.builder().name("facility1Name").build())
                                                        .build())
                                                .build(),
                                        Facility.builder()
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityId("facility2")
                                                        .facilityDetails(FacilityDetails.builder().name("facility2Name").build())
                                                        .build())
                                                .build(),
                                        Facility.builder()
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityId("facility3")
                                                        .facilityDetails(FacilityDetails.builder().name("facility3Name").build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build();

        // Invoke
        validator.validate(accountState, payload);

        // Verify
        assertThat(accountState.getErrors()).hasSize(1).containsExactlyInAnyOrder(
                Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_FACILITIES_NOT_VALID.getMessage() + ": [facility2]"
        );
    }
}
