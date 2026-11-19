package uk.gov.cca.api.web.orchestrator.facility.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResultExtendedDTO;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FacilityInfoMapperTest {

    private final FacilityInfoMapper mapper = Mappers.getMapper(FacilityInfoMapper.class);

    @Test
    void toFacilityCertificationSearchResultInfo() {

        FacilitySearchResultInfoDTO facilitySearchResultInfoDTO =
                new FacilitySearchResultInfoDTO(1L, "facilityId", "siteName", null, FacilityDataStatus.LIVE);

        FacilitySearchResultExtendedDTO facilitySearchResultExtendedDTO =
                new FacilitySearchResultExtendedDTO(1L, "facilityId", "siteName", null, FacilityDataStatus.LIVE, FacilityCertificationStatus.CERTIFIED, null);

        // invoke
        FacilitySearchResultExtendedDTO result =
                mapper.toFacilityCertificationSearchResultInfo(facilitySearchResultInfoDTO, FacilityCertificationStatus.CERTIFIED);

        // verify
        assertThat(result).isEqualTo(facilitySearchResultExtendedDTO);
    }

    @Test
    void toFacilityInfoDTO() {
        String facilityBusinessId = "facilityBusinessId";
        String siteName = "Facility Name";

        FacilityDataDetailsDTO facilityDataDetailsDTO = FacilityDataDetailsDTO.builder()
                .facilityBusinessId(facilityBusinessId)
                .status(FacilityDataStatus.LIVE)
                .siteName(siteName)
                .build();

        SurplusGainedDTO surplusDetails = SurplusGainedDTO.builder()
                .surplusGained(BigDecimal.valueOf(256))
                .hasHistory(false)
                .schemeVersion(SchemeVersion.CCA_3)
                .build();

        FacilityInfoDTO facilityInfoDTO = FacilityInfoDTO.builder()
                .facilityBusinessId(facilityBusinessId)
                .status(FacilityDataStatus.LIVE)
                .siteName(siteName)
                .surplusDetails(surplusDetails)
                .build();

        // invoke
        FacilityInfoDTO result = mapper.toFacilityInfoDTO(facilityDataDetailsDTO, surplusDetails);

        // verify
        assertThat(result).isEqualTo(facilityInfoDTO);
    }
}
