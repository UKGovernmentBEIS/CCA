package uk.gov.cca.api.underlyingagreement.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;

class UnderlyingAgreementMapperTest {

	private final UnderlyingAgreementMapper mapper = Mappers.getMapper(UnderlyingAgreementMapper.class);

    @Test
    void toEmissionsMonitoringPlanUkEtsDTO() {

        Long accountId = 1L;
        Facility facility1 = Facility.builder().facilityItem(FacilityItem.builder().facilityId("id1").build()).build();
        Facility facility2 = Facility.builder().facilityItem(FacilityItem.builder().facilityId("id2").build()).build();
        final UnderlyingAgreementContainer unaContainer = UnderlyingAgreementContainer.builder()
        		.sectorMeasurementType(MeasurementType.CARBON_KG)
        		.underlyingAgreement(UnderlyingAgreement.builder()
        				.targetPeriod5Details(TargetPeriod5Details.builder()
        						.exist(Boolean.FALSE)
        						.build())
        				.facilities(Set.of(facility1, facility2))
        				.build())
        		.build();

        final UnderlyingAgreementEntity entity = UnderlyingAgreementEntity.builder()
                .accountId(accountId)
                .underlyingAgreementContainer(unaContainer)
                .build();
        entity.setFileDocumentUuid("uuid");
        UnderlyingAgreementDTO actual = mapper.toUnderlyingAgreementDTO(entity);

        final UnderlyingAgreementContainer actualContainer = actual.getUnderlyingAgreementContainer();

        assertThat(actual.getAccountId()).isEqualTo(accountId);
        assertThat(actual.getFileDocumentUuid()).isEqualTo("uuid");
        assertThat(actual.getConsolidationNumber()).isEqualTo(1);
        assertThat(actualContainer.getSectorMeasurementType()).isEqualTo(MeasurementType.CARBON_KG);
        assertThat(actualContainer.getUnderlyingAgreement().getTargetPeriod5Details()).isEqualTo(unaContainer.getUnderlyingAgreement().getTargetPeriod5Details());
        assertThat(actualContainer.getUnderlyingAgreement().getFacilities()).isEqualTo(unaContainer.getUnderlyingAgreement().getFacilities());
    }
}
