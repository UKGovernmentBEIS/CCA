package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataEntity;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS;

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

    @Test
    void toEmissionsTradingSchemeDataReportDetailsDTO() {

        final Year targetPeriodYear = Year.of(2026);
        final int reportVersion = 3;
        final LocalDateTime submissionDate = LocalDateTime.of(2027, 6, 15, 10, 30, 0);

        EmissionsTradingSchemeDataContainer dataContainer = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(MeasurementType.CARBON_KG)
                .uketsId("UK0012345")
                .isUltraSmallEmitter(false)
                .fuels(List.of(PerformanceDataFacilityFuel.builder()
                                .name(NATURAL_GAS.getDescription())
                                .fixedConversionFactorCode(NATURAL_GAS)
                                .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, MeasurementType.ENERGY_KWH))
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build(),
                        PerformanceDataFacilityFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryConversionFactor(BigDecimal.ONE)
                                .primaryEnergy(BigDecimal.valueOf(1500000))
                                .build()
                ))
                .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                        .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                        .totalCo2Emissions(BigDecimal.valueOf(2000000))
                        .build())
                .build();

        final EmissionsTradingSchemeDataEntity entity = EmissionsTradingSchemeDataEntity.builder()
                .id(1L)
                .facilityId(42L)
                .data(dataContainer)
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(reportVersion)
                .submissionDate(submissionDate)
                .build();

        EmissionsTradingSchemeDataReportDetailsDTO expected = EmissionsTradingSchemeDataReportDetailsDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .submissionDate(submissionDate)
                .data(dataContainer)
                .build();

        // invoke
        EmissionsTradingSchemeDataReportDetailsDTO actual = mapper.toEmissionsTradingSchemeDataReportDetailsDTO(entity);

        // verify
        assertThat(actual).isEqualTo(expected);
    }
}
