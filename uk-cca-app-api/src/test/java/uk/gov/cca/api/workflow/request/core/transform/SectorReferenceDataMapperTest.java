package uk.gov.cca.api.workflow.request.core.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoNameDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

class SectorReferenceDataMapperTest {

    private final SectorReferenceDataMapper mapper = Mappers.getMapper(SectorReferenceDataMapper.class);

    @Test
    void toSectorAssociationDetails() {
        final String subsectorAssociationName = "SubSectorName";
        final MeasurementType currentMeasurementType = MeasurementType.CARBON_KG;
        final String currentThroughputUnit = "tonne";
        final MeasurementType previousMeasurementType = MeasurementType.CARBON_TONNE;
        final String previousThroughputUnit = null;


        final SectorAssociationMeasurementInfoDTO sectorAssociationMeasurementInfoDTO = SectorAssociationMeasurementInfoDTO
                .builder()
                .subsectorAssociationName(subsectorAssociationName)
                .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
                		.sectorMeasurementType(previousMeasurementType)
                		.sectorThroughputUnit(previousThroughputUnit)
                		.build(), SchemeVersion.CCA_3, SchemeData.builder()
                		.sectorMeasurementType(currentMeasurementType)
                		.sectorThroughputUnit(currentThroughputUnit)
                		.build()))
                .build();

        final SectorAssociationDetails result = mapper.toSectorAssociationDetails(sectorAssociationMeasurementInfoDTO);

        assertThat(result.getSubsectorAssociationName()).isEqualTo(subsectorAssociationName);
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_3).getSectorMeasurementType()).isEqualTo(currentMeasurementType);
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_3).getSectorThroughputUnit()).isEqualTo(currentThroughputUnit);
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_2).getSectorMeasurementType()).isEqualTo(previousMeasurementType);
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_2).getSectorThroughputUnit()).isNull();
    }
    
    @Test
    void toSectorAssociationInfo() {
        final String acronym = "acronym";
        final String name = "name";
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;


        final SectorAssociationInfoNameDTO sectorAssociationInfoNameDTO = SectorAssociationInfoNameDTO
                .builder()
                .name(name)
                .acronym(acronym)
                .competentAuthority(ca)
                .build();
        
        final SectorAssociationInfo expected = SectorAssociationInfo.builder()
        		.name(name)
                .acronym(acronym)
                .competentAuthority(ca)
        		.build();

        final SectorAssociationInfo result = mapper.toSectorAssociationInfo(sectorAssociationInfoNameDTO);

        assertThat(result).isEqualTo(expected);

    }

}
