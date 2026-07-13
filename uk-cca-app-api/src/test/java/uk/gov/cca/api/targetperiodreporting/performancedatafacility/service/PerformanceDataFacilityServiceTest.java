package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.PerformanceDataReportTypeDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityServiceTest {

    @InjectMocks
    private PerformanceDataFacilityService service;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void getAvailableTargetPeriodsForPerformanceDataReporting() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP6)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2022))
                                                .reportingStartDate(LocalDate.of(2023, 1, 1))
                                                .reportingEndDate(LocalDate.of(2123, 1, 2))
                                                .build()
                                ))
                                .build()
                        )
                        .schemeVersion(schemeVersion)
                        .build(),
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2023))
                                                .reportingStartDate(LocalDate.of(2024, 1, 1))
                                                .reportingEndDate(LocalDate.of(2025, 1, 1))
                                                .build(),
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2024))
                                                .reportingStartDate(LocalDate.of(2025, 1, 1))
                                                .build()
                                ))
                                .build()
                        )
                        .schemeVersion(schemeVersion)
                        .build(),
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP8)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2123))
                                                .reportingStartDate(LocalDate.of(2124, 1, 1))
                                                .build()
                                ))
                                .build()
                        )
                        .schemeVersion(schemeVersion)
                        .build()
        );

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersion(schemeVersion)).thenReturn(targetPeriods);

        // Invoke
        List<PerformanceDataReportTypeDTO> result = service.getAvailableTargetPeriodsForPerformanceDataReporting(schemeVersion);

        // Verify
        assertThat(result).containsExactlyInAnyOrder(
                PerformanceDataReportTypeDTO.builder()
                        .targetPeriodType(TargetPeriodType.TP6)
                        .reportType(PerformanceDataReportType.FINAL)
                        .build(),
                PerformanceDataReportTypeDTO.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .build()
        );
        verify(targetPeriodService).getTargetPeriodDetailsBySchemeVersion(schemeVersion);
    }
    
    @Test
    void getAvailableTargetPeriodYears() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2026))
                                                .reportingStartDate(LocalDate.of(2025, 1, 1))
                                                .reportingEndDate(LocalDate.of(2125, 1, 2))
                                                .build()
                                ))
                                .build()
                        )
                        .schemeVersion(schemeVersion)
                        .build(),
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP8)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2027))
                                                .reportingStartDate(LocalDate.of(2026, 1, 1))
                                                .reportingEndDate(LocalDate.of(2126, 1, 1))
                                                .build(),
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2028))
                                                .reportingStartDate(LocalDate.of(2127, 1, 1))
                                                .build()
                                ))
                                .build()
                        )
                        .schemeVersion(schemeVersion)
                        .build()
        );

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersion(schemeVersion)).thenReturn(targetPeriods);

        // Invoke
        Set<Year> result = service.getAvailableTargetPeriodYears(schemeVersion);

        // Verify
        assertThat(result).containsExactlyInAnyOrder(Year.of(2026), Year.of(2027));
        verify(targetPeriodService).getTargetPeriodDetailsBySchemeVersion(schemeVersion);
    }
}
