package uk.gov.cca.api.targetperiodreporting.facilitycertification.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;

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

}
