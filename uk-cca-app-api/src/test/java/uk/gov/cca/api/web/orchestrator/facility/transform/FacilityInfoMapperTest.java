package uk.gov.cca.api.web.orchestrator.facility.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationDetailsDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationSearchResultInfoDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FacilityInfoMapperTest {

    private final FacilityInfoMapper mapper = Mappers.getMapper(FacilityInfoMapper.class);

    @Test
    void toFacilityCertificationSearchResultInfo() {

        FacilitySearchResultInfoDTO facilitySearchResultInfoDTO =
                new FacilitySearchResultInfoDTO(1L, "facilityId", "siteName", null, FacilityDataStatus.LIVE);

        FacilityCertificationSearchResultInfoDTO facilityCertificationSearchResultInfoDTO =
                new FacilityCertificationSearchResultInfoDTO("facilityId", "siteName", null, FacilityDataStatus.LIVE, FacilityCertificationStatus.CERTIFIED);

        // invoke
        FacilityCertificationSearchResultInfoDTO result =
                mapper.toFacilityCertificationSearchResultInfo(facilitySearchResultInfoDTO, FacilityCertificationStatus.CERTIFIED);

        // verify
        assertThat(result).isEqualTo(facilityCertificationSearchResultInfoDTO);
    }

    @Test
    void toFacilityCertificationDetails() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        FacilityCertificationDetailsDTO certificationDetailsDTO = FacilityCertificationDetailsDTO.builder()
                .certificationPeriod(CertificationPeriodType.CP6)
                .status(FacilityCertificationStatus.CERTIFIED)
                .startDate(startDate)
                .certificationPeriodStartDate(startDate)
                .certificationPeriodEndDate(endDate)
                .build();

        CertificationPeriodDTO certificationPeriodDTO = CertificationPeriodDTO.builder()
                .id(1L)
                .certificationPeriodType(CertificationPeriodType.CP6)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        FacilityCertificationDTO facilityCertificationDTO = FacilityCertificationDTO.builder()
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(startDate)
                .build();

        // invoke
        FacilityCertificationDetailsDTO result =
                mapper.toFacilityCertificationDetails(facilityCertificationDTO, certificationPeriodDTO);

        // verify
        assertThat(result).isEqualTo(certificationDetailsDTO);
    }

    @Test
    void toFacilityInfoDTO() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        String facilityId = "facilityId";
        String siteName = "Facility Name";

        FacilityDataDetailsDTO facilityDataDetailsDTO = FacilityDataDetailsDTO.builder()
                .facilityId(facilityId)
                .status(FacilityDataStatus.LIVE)
                .siteName(siteName)
                .build();

        FacilityCertificationDetailsDTO certificationDetailsDTO = FacilityCertificationDetailsDTO.builder()
                .status(FacilityCertificationStatus.CERTIFIED)
                .certificationPeriod(CertificationPeriodType.CP6)
                .certificationPeriodStartDate(startDate)
                .certificationPeriodEndDate(endDate)
                .build();

        FacilityInfoDTO facilityInfoDTO = FacilityInfoDTO.builder()
                .facilityCertificationDetails(List.of(certificationDetailsDTO))
                .facilityId(facilityId)
                .status(FacilityDataStatus.LIVE)
                .siteName(siteName)
                .build();

        // invoke
        FacilityInfoDTO result = mapper.toFacilityInfoDTO(facilityDataDetailsDTO, List.of(certificationDetailsDTO));

        // verify
        assertThat(result).isEqualTo(facilityInfoDTO);
    }
}
