package uk.gov.cca.api.targetperiodreporting.facilitycertification.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class FacilityCertificationMapperTest {

    private final FacilityCertificationMapper mapper = Mappers.getMapper(FacilityCertificationMapper.class);

    @Test
    void toFacilityCertificationDto() {
        FacilityCertificationDTO facilityCertificationDTO = FacilityCertificationDTO.builder()
                .facilityId(2L)
                .certificationPeriodId(1L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2025, 5, 5))
                .build();

        FacilityCertification facilityCertification = FacilityCertification.builder()
                .facilityId(2L)
                .certificationPeriodId(1L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2025, 5, 5))
                .build();

        // invoke
        FacilityCertificationDTO result = mapper.toFacilityCertificationDto(facilityCertification);

        // verify
        assertThat(result).isEqualTo(facilityCertificationDTO);
    }

    @Test
    void toFacilityCertification() {

        FacilityCertificationDTO facilityCertificationDTO = FacilityCertificationDTO.builder()
                .facilityId(2L)
                .certificationPeriodId(1L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2025, 5, 5))
                .build();

        FacilityCertification facilityCertification = FacilityCertification.builder()
                .facilityId(2L)
                .certificationPeriodId(1L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2025, 5, 5))
                .build();

        // invoke
        FacilityCertification result = mapper.toFacilityCertification(facilityCertificationDTO);

        // verify
        assertThat(result).isEqualTo(facilityCertification);
    }

    @Test
    void toFacilityCertificationStatusDTO() {
        final LocalDate startDate = LocalDate.of(2025, 1, 1);
        final LocalDate endDate = LocalDate.of(2025, 12, 31);
        final CertificationPeriodDTO certificationPeriodDTO = CertificationPeriodDTO.builder()
                .id(1L)
                .schemeVersion(SchemeVersion.CCA_2)
                .certificationPeriodType(CertificationPeriodType.CP6)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        final FacilityCertificationDTO facilityCertificationDTO = FacilityCertificationDTO.builder()
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(startDate)
                .build();

        final FacilityCertificationStatusDTO certificationDetailsDTO = FacilityCertificationStatusDTO.builder()
                .certificationPeriodId(1L)
                .schemeVersion(SchemeVersion.CCA_2)
                .certificationPeriod(CertificationPeriodType.CP6)
                .status(FacilityCertificationStatus.CERTIFIED)
                .startDate(startDate)
                .certificationPeriodStartDate(startDate)
                .certificationPeriodEndDate(endDate)
                .build();

        // Invoke
        FacilityCertificationStatusDTO result = mapper.toFacilityCertificationStatusDTO(facilityCertificationDTO, certificationPeriodDTO);

        // Verify
        assertThat(result).isEqualTo(certificationDetailsDTO);
    }
}
