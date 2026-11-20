package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataEntity;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.repository.EmissionsTradingSchemeDataEntityRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataQueryServiceTest {

    @InjectMocks
    private EmissionsTradingSchemeDataQueryService service;

    @Mock
    private EmissionsTradingSchemeDataEntityRepository emissionsTradingSchemeDataEntityRepository;

    @Test
    void getEmissionsTradingSchemeDataReportInfo() {

        final Year targetPeriodYear = Year.of(2026);
        final int reportVersion = 3;
        final LocalDateTime submissionDate = LocalDateTime.of(2027, 6, 15, 10, 30, 0);
        final Long facilityId = 42L;
        MeasurementType measurementType = MeasurementType.CARBON_KG;

        final EmissionsTradingSchemeDataEntity entity = EmissionsTradingSchemeDataEntity.builder()
                .id(1L)
                .facilityId(facilityId)
                .data(EmissionsTradingSchemeDataContainer.builder()
                        .measurementType(measurementType)
                        .uketsId("UK0012345")
                        .isUltraSmallEmitter(false)
                        .fuels(List.of(PerformanceDataFacilityFuel.builder()
                                .name(NATURAL_GAS.getDescription())
                                .fixedConversionFactorCode(NATURAL_GAS)
                                .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, measurementType))
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build()))
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(912.700))
                                .build())
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

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.of(entity));

        // invoke
        EmissionsTradingSchemeDataReportInfoDTO actual = service.getEmissionsTradingSchemeDataReportInfo(facilityId, targetPeriodYear);

        // verify
        assertThat(actual).isEqualTo(expected);
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getEmissionsTradingSchemeDataReportInfo_empty() {

        final Year targetPeriodYear = Year.of(2026);
        final Long facilityId = 42L;

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.empty());

        // invoke
        EmissionsTradingSchemeDataReportInfoDTO actual = service.getEmissionsTradingSchemeDataReportInfo(facilityId, targetPeriodYear);

        // verify
        assertThat(actual).isNull();
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getEmissionsTradingSchemeDataReportDetails() {

        final Year targetPeriodYear = Year.of(2026);
        final int reportVersion = 3;
        final LocalDateTime submissionDate = LocalDateTime.of(2027, 6, 15, 10, 30, 0);
        final Long facilityId = 42L;
        final MeasurementType measurementType = MeasurementType.CARBON_KG;
        final EmissionsTradingSchemeDataContainer dataContainer = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(measurementType)
                .uketsId("UK0012345")
                .isUltraSmallEmitter(false)
                .fuels(List.of(PerformanceDataFacilityFuel.builder()
                        .name(NATURAL_GAS.getDescription())
                        .fixedConversionFactorCode(NATURAL_GAS)
                        .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, measurementType))
                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                        .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                        .primaryEnergy(BigDecimal.valueOf(5000000))
                        .build()))
                .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                        .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                        .totalCo2Emissions(BigDecimal.valueOf(912.700))
                        .build())
                .build();

        final EmissionsTradingSchemeDataEntity entity = EmissionsTradingSchemeDataEntity.builder()
                .id(1L)
                .facilityId(facilityId)
                .data(dataContainer)
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(reportVersion)
                .submissionDate(submissionDate)
                .build();

        final EmissionsTradingSchemeDataReportDetailsDTO expected = EmissionsTradingSchemeDataReportDetailsDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .submissionDate(submissionDate)
                .data(dataContainer)
                .build();

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.of(entity));

        // invoke
        EmissionsTradingSchemeDataReportDetailsDTO actual = service.getEmissionsTradingSchemeDataReportDetails(facilityId, targetPeriodYear);

        // verify
        assertThat(actual).isEqualTo(expected);
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);

    }

    @Test
    void getEmissionsTradingSchemeDataReportDetails_throws_exception() {

        final Year targetPeriodYear = Year.of(2026);
        final Long facilityId = 42L;

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.empty());

        // invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.getEmissionsTradingSchemeDataReportDetails(facilityId, targetPeriodYear));

        // verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);

    }

}
