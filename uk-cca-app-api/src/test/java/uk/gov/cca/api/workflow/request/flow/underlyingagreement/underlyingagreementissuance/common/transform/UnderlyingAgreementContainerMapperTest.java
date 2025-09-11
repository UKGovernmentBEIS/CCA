package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementContainerMapperTest {

    private final UnderlyingAgreementContainerMapper mapper = Mappers.getMapper(UnderlyingAgreementContainerMapper.class);

    @Test
    void toUnderlyingAgreementContainer() {
        UUID uuid = UUID.randomUUID();

        UnderlyingAgreement una = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .calculatorFile(uuid)
                                .build())
                        .build())
                .build();

        AccountReferenceData accountData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
                				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
                				.sectorThroughputUnit("tonne")
                				.build()))
                        .build())
                .build();

        UnderlyingAgreementSubmitRequestTaskPayload taskPayload = UnderlyingAgreementSubmitRequestTaskPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreement(una)
                        .build())
                .accountReferenceData(accountData)
                .build();


        UnderlyingAgreementContainer result = mapper.toUnderlyingAgreementContainer(taskPayload);

        assertThat(result).isEqualTo(UnderlyingAgreementContainer.builder()
                .underlyingAgreement(una)
                .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("tonne")
        				.build()))
                .build());
    }

    @Test
    void request_toUnderlyingAgreementContainer() {
        UUID uuid = UUID.randomUUID();

        Facility facility1 = Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder()
                        .facilityId("id1").build())
                .build();
        Facility facility2 = Facility.builder().status(FacilityStatus.NEW).facilityItem(FacilityItem.builder()
                        .facilityId("id2").build())
                .build();

        UnderlyingAgreement una = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .calculatorFile(uuid)
                                .build())
                        .build())
                .facilities(Set.of(facility1, facility2))
                .build();

        UnderlyingAgreement proposedUna = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .calculatorFile(uuid)
                                .build())
                        .build())
                .facilities(Set.of(facility1))
                .build();

        AccountReferenceData accountData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
                				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
                				.sectorThroughputUnit("tonne")
                				.build()))
                        .build())
                .build();

        UnderlyingAgreementRequestPayload payload = UnderlyingAgreementRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreement(una)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreement(proposedUna)
                        .build())
                .facilitiesReviewGroupDecisions(Map.of(
                        "id1", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "id2", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build()
                ))
                .build();

        UnderlyingAgreement unaWithoutRejectedFacilities = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                .calculatorFile(uuid)
                                .build())
                        .build())
                .facilities(Set.of(facility1))
                .build();


        UnderlyingAgreementContainer result = mapper.toUnderlyingAgreementContainer(payload, accountData);

        assertThat(result).isEqualTo(UnderlyingAgreementContainer.builder()
                .underlyingAgreement(unaWithoutRejectedFacilities)
                .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("tonne")
        				.build()))
                .build());
    }
}
