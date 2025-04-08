package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementVariationContainerDeepCloneMapperTest {

    private final UnderlyingAgreementVariationContainerDeepCloneMapper UNA_VARIATION_CONTAINER_DEEP_CLONE_MAPPER =
            Mappers.getMapper(UnderlyingAgreementVariationContainerDeepCloneMapper.class);

    @Test
    void request_toUnderlyingAgreementContainer() {
        UUID uuid = UUID.randomUUID();

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
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id3").build())
                .build();
        Facility facility4 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id4")
                        .facilityDetails(FacilityDetails.builder()
                                .facilityAddress(AccountAddressDTO.builder()
                                        .postcode("71409")
                                        .city("Heraklion")
                                        .country("Greece")
                                        .build())
                                .build())
                        .build())
                .build();

        UnderlyingAgreement originalUna = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .calculatorFile(uuid)
                                .build())
                        .build())
                .facilities(Set.of(facility2, facility4))
                .build();

        Facility updatedFacility4 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem.builder()
                        .facilityId("id4")
                        .facilityDetails(FacilityDetails.builder()
                                .facilityAddress(AccountAddressDTO.builder()
                                        .postcode("11524")
                                        .city("Athens")
                                        .country("Greece")
                                        .build())
                                .build())
                        .build())
                .build();

        final Map<String, String> sectionsCompleted = Map.of(
                "targetPeriod5Details", "COMPLETED",
                "underlyingAgreementTargetUnitDetails", "IN PROGRESS",
                "id1", "COMPLETED",
                "id2", "COMPLETED",
                "id3", "COMPLETED",
                "id4", "COMPLETED"
        );

        UnderlyingAgreement una = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .calculatorFile(uuid)
                                .build())
                        .build())
                .facilities(Set.of(facility1, facility2, facility3, updatedFacility4))
                .build();

        UnderlyingAgreement proposedUna = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .calculatorFile(uuid)
                                .build())
                        .build())
                .facilities(Set.of(facility1, facility2, facility4))
                .build();

        AccountReferenceData accountData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .throughputUnit("tonne")
                        .build())
                .build();

        UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(una)
                        .build())
                .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                        .underlyingAgreement(originalUna)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(proposedUna)
                        .build())
                .facilitiesReviewGroupDecisions(Map.of(
                        "id1", UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .facilityStatus(FacilityStatus.NEW)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "id2", UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED)
                                .facilityStatus(FacilityStatus.EXCLUDED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "id3", UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED)
                                .facilityStatus(FacilityStatus.NEW)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "id4", UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .facilityStatus(FacilityStatus.EXCLUDED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build()
                ))
                .sectionsCompleted(sectionsCompleted)
                .build();

        UnderlyingAgreementContainer unaContainer =
                UNA_VARIATION_CONTAINER_DEEP_CLONE_MAPPER.toUnderlyingAgreementContainer(payload, accountData);

        // check result
        assertThat(unaContainer).isEqualTo(UnderlyingAgreementContainer.builder()
                .underlyingAgreement(proposedUna)
                .sectorMeasurementType(MeasurementType.ENERGY_KWH)
                .sectorThroughputUnit("tonne")
                .build());

        // check deep clone
        facility1.setStatus(FacilityStatus.LIVE);
        Facility containerFacility = unaContainer.getUnderlyingAgreement().getFacilities().stream()
                .filter(f -> f.getFacilityItem().getFacilityId().equals(facility1.getFacilityItem().getFacilityId()))
                .findAny()
                .orElse(null);

        assertThat(containerFacility).isNotNull();
        assertThat(containerFacility.getStatus()).isNotEqualTo(facility1.getStatus());
    }
}