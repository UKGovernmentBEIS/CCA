package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Year;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataEntity;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportInfoDTO;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataMapperTest {

    private final EmissionsTradingSchemeDataMapper mapper = Mappers.getMapper(EmissionsTradingSchemeDataMapper.class);

    @Test
    void toEmissionsTradingSchemeDataReportInfoDTO() {
        final Year targetPeriodYear = Year.of(2026);
        final int reportVersion = 3;
        final LocalDateTime submissionDate = LocalDateTime.of(2027, 6, 15, 10, 30, 0);

        final EmissionsTradingSchemeDataEntity entity = EmissionsTradingSchemeDataEntity.builder()
                .id(1L)
                .facilityId(42L)
                .data(EmissionsTradingSchemeDataContainer.builder()
                        .measurementType(MeasurementType.CARBON_KG)
                        .uketsId("UK0012345")
                        .isUltraSmallEmitter(false)
                        .build())
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(reportVersion)
                .submissionDate(submissionDate)
                .build();

        final EmissionsTradingSchemeDataReportInfoDTO expected = EmissionsTradingSchemeDataReportInfoDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(reportVersion)
                .submissionDate(submissionDate)
                .build();

        // invoke
        EmissionsTradingSchemeDataReportInfoDTO result = mapper.toEmissionsTradingSchemeDataReportInfoDTO(entity);

        // verify
        assertThat(result).isEqualTo(expected);
    }
}
