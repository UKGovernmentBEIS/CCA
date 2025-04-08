package uk.gov.cca.api.workflow.request.core.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;

import static org.assertj.core.api.Assertions.assertThat;

public class SectorReferenceDataMapperTest {

    private final SectorReferenceDataMapper mapper = Mappers.getMapper(SectorReferenceDataMapper.class);

    @Test
    void toSectorAssociationDetails() {
        final String subsectorAssociationName = "SubSectorName";
        final MeasurementType measurementType = MeasurementType.CARBON_KG;
        final String throughputUnit = "tonne";


        final SectorAssociationMeasurementInfoDTO sectorAssociationMeasurementInfoDTO = SectorAssociationMeasurementInfoDTO
                .builder()
                .subsectorAssociationName(subsectorAssociationName)
                .measurementUnit(measurementType.getUnit())
                .throughputUnit(throughputUnit)
                .build();

        final SectorAssociationDetails result = mapper.toSectorAssociationDetails(sectorAssociationMeasurementInfoDTO);

        assertThat(result.getSubsectorAssociationName()).isEqualTo(subsectorAssociationName);
        assertThat(result.getMeasurementType()).isEqualTo(measurementType);
        assertThat(result.getThroughputUnit()).isEqualTo(throughputUnit);
    }

}
