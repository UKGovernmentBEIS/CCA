package uk.gov.cca.api.underlyingagreement.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDocumentDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementMapperTest {

    private final UnderlyingAgreementMapper mapper = Mappers.getMapper(UnderlyingAgreementMapper.class);

    @Test
    void toUnderlyingAgreementDTO() {

        Long accountId = 1L;
        Facility facility1 = Facility.builder().facilityItem(FacilityItem.builder().facilityId("id1").build()).build();
        Facility facility2 = Facility.builder().facilityItem(FacilityItem.builder().facilityId("id2").build()).build();
        final UnderlyingAgreementContainer unaContainer = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder().sectorMeasurementType(MeasurementType.CARBON_KG).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder()
                                .exist(Boolean.FALSE)
                                .build())
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        final UnderlyingAgreementDocument document = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_2);
        document.setFileDocumentUuid("uuid");
        final UnderlyingAgreementEntity entity = UnderlyingAgreementEntity.builder()
                .accountId(accountId)
                .underlyingAgreementContainer(unaContainer)
                .build();
        entity.addUnderlyingAgreementDocument(document);

        UnderlyingAgreementDTO actual = mapper.toUnderlyingAgreementDTO(entity);

        final UnderlyingAgreementContainer actualContainer = actual.getUnderlyingAgreementContainer();

        assertThat(actual.getAccountId()).isEqualTo(accountId);
        assertThat(actual.getUnderlyingAgreementDocuments().getFirst().getFileDocumentUuid()).isEqualTo("uuid");
        assertThat(actual.getUnderlyingAgreementDocuments().getFirst().getConsolidationNumber()).isEqualTo(1);
        assertThat(actualContainer.getSchemeDataMap().get(SchemeVersion.CCA_2).getSectorMeasurementType()).isEqualTo(MeasurementType.CARBON_KG);
        assertThat(actualContainer.getUnderlyingAgreement().getTargetPeriod5Details()).isEqualTo(unaContainer.getUnderlyingAgreement().getTargetPeriod5Details());
        assertThat(actualContainer.getUnderlyingAgreement().getFacilities()).isEqualTo(unaContainer.getUnderlyingAgreement().getFacilities());
    }
    
    @Test
    void toUnderlyingAgreementDocumentDetailsDTO() {

        final UnderlyingAgreementDocument document = UnderlyingAgreementDocument.createUnderlyingAgreementDocument(SchemeVersion.CCA_2);
        document.setFileDocumentUuid("uuid");
        document.setTerminatedDate(LocalDateTime.now().plusDays(1));
        final FileInfoDTO file = FileInfoDTO.builder().name("filename").uuid(null).build();

        UnderlyingAgreementDocumentDetailsDTO actual = mapper.toUnderlyingAgreementDocumentDetailsDTO(document, file);

        assertThat(actual.getActivationDate()).isNotNull();
        assertThat(actual.getTerminatedDate()).isNotNull();
        assertThat(actual.getFileDocument()).isEqualTo(file);
    }
}
