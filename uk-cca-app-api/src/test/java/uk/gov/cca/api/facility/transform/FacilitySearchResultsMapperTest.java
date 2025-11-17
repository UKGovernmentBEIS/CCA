package uk.gov.cca.api.facility.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;

import static org.assertj.core.api.Assertions.assertThat;

class FacilitySearchResultsMapperTest {

    private final FacilitySearchResultsMapper mapper = Mappers.getMapper(FacilitySearchResultsMapper.class);


    @Test
    void toFacilitySearchResultInfo() {
        final String facilityBusinessId = "SA-F00001";
        final String siteName = "site1";

        final FacilityData facilityData = FacilityData.builder()
                .id(1L)
                .facilityBusinessId(facilityBusinessId)
                .siteName(siteName)
                .build();

        // invoke
        FacilitySearchResultInfoDTO resultInfo = mapper.toFacilitySearchResultInfo(facilityData);

        final FacilitySearchResultInfoDTO facilitySearchResultInfoDTO =
                new FacilitySearchResultInfoDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE);

        assertThat(resultInfo).isEqualTo(facilitySearchResultInfoDTO);

    }
}
