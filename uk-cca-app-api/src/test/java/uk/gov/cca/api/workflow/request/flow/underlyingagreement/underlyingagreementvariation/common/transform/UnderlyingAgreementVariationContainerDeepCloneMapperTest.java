package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementVariationContainerDeepCloneMapperTest {

    private static final UnderlyingAgreementVariationContainerDeepCloneMapper UNA_VARIATION_CONTAINER_DEEP_CLONE_MAPPER =
            Mappers.getMapper(UnderlyingAgreementVariationContainerDeepCloneMapper.class);

    @Test
    void toUnderlyingAgreementContainer() {
        Facility facility1 = Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id1").build())
                .build();
        Facility facility2 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id2").build())
                .build();
        Facility facility3 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id3")
                        .build())
                .build();
        Facility facility4 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id3")
                        .facilityDetails(FacilityDetails.builder()
                                .facilityAddress(FacilityAddressDTO.builder()
                                        .postcode("71409")
                                        .city("Heraklion")
                                        .country("Greece")
                                        .build())
                                .build())
                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                        .variableEnergyConsumptionDataByProduct(List.of(
                                                ProductVariableEnergyConsumptionData.builder().productName("p1").productStatus(ProductStatus.NEW).build(),
                                                ProductVariableEnergyConsumptionData.builder().productName("p2").productStatus(ProductStatus.EXCLUDED).build(),
                                                ProductVariableEnergyConsumptionData.builder().productName("p3").productStatus(ProductStatus.LIVE).build()
                                        ))
                                        .build())
                                .build())
                        .build())
                .build();
        UnderlyingAgreement originalUna = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .build())
                        .build())
                .facilities(Set.of(facility2, facility3, facility4))
                .build();

        Facility updatedFacility3 = Facility.builder()
                .status(FacilityStatus.EXCLUDED)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id3")
                        .build())
                .build();
        Facility updatedFacility4 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id4")
                        .facilityDetails(FacilityDetails.builder()
                                .facilityAddress(FacilityAddressDTO.builder()
                                        .postcode("11524")
                                        .city("Athens")
                                        .country("Greece")
                                        .build())
                                .build())
                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                        .variableEnergyConsumptionDataByProduct(List.of(
                                                ProductVariableEnergyConsumptionData.builder().productName("p1").productStatus(ProductStatus.NEW).build(),
                                                ProductVariableEnergyConsumptionData.builder().productName("p3").productStatus(ProductStatus.LIVE).build()
                                        ))
                                        .build())
                                .build())
                        .build())
                .build();
        Facility excludedFacility5 = Facility.builder()
                .status(FacilityStatus.EXCLUDED)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id5")
                        .build())
                .build();
        UnderlyingAgreement proposedUna = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .build())
                        .build())
                .facilities(Set.of(facility1, facility2, updatedFacility3, updatedFacility4))
                .build();

        AccountReferenceData accountData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
                				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
                				.sectorThroughputUnit("tonne")
                				.build()))
                        .build())
                .build();

        UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                        .underlyingAgreement(originalUna)
                        .excludedFacilities(Set.of(excludedFacility5))
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(proposedUna)
                        .build())
                .build();

        // Invoke
        UnderlyingAgreementContainer unaContainer =
                UNA_VARIATION_CONTAINER_DEEP_CLONE_MAPPER.toUnderlyingAgreementContainer(payload, accountData);

        // Verify
        assertThat(unaContainer.getSchemeDataMap()).containsExactlyInAnyOrderEntriesOf(Map.of(
                SchemeVersion.CCA_2, SchemeData.builder()
                        .sectorMeasurementType(MeasurementType.ENERGY_KWH)
                        .sectorThroughputUnit("tonne")
                        .build()));
        assertThat(unaContainer.getUnderlyingAgreement().getTargetPeriod5Details())
                .isEqualTo(proposedUna.getTargetPeriod5Details());
        assertThat(unaContainer.getUnderlyingAgreement().getTargetPeriod6Details())
                .isEqualTo(proposedUna.getTargetPeriod6Details());
        assertThat(unaContainer.getUnderlyingAgreement().getFacilities())
                .containsExactlyInAnyOrder(facility1, facility2, updatedFacility4);
        assertThat(unaContainer.getExcludedFacilities())
                .containsExactlyInAnyOrder(excludedFacility5, updatedFacility3);
    }
}