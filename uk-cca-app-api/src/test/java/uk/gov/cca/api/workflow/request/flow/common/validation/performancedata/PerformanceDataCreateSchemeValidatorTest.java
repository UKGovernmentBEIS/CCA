package uk.gov.cca.api.workflow.request.flow.common.validation.performancedata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataCreateSchemeValidatorTest {

    @InjectMocks
    private PerformanceDataCreateSchemeValidator validator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void isAvailableForScheme() {
        final SchemeVersion scheme = SchemeVersion.CCA_3;
        final LocalDate submissionDate = LocalDate.of(2022, 1, 1);

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersion(scheme))
                .thenReturn(createTargetPeriods(null));

        // Invoke
        boolean result = validator.isAvailableForScheme(scheme, submissionDate);

        // Verify
        assertThat(result).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsBySchemeVersion(scheme);
    }

    @Test
    void isAvailableForScheme_valid() {
        final SchemeVersion scheme = SchemeVersion.CCA_3;
        final LocalDate submissionDate = LocalDate.of(2024, 1, 1);

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersion(scheme))
                .thenReturn(createTargetPeriods(null));

        // Invoke
        boolean result = validator.isAvailableForScheme(scheme, submissionDate);

        // Verify
        assertThat(result).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsBySchemeVersion(scheme);
    }

    @Test
    void isAvailableForScheme_at_end_valid() {
        final SchemeVersion scheme = SchemeVersion.CCA_3;
        final LocalDate submissionDate = LocalDate.of(2025, 12, 31);

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersion(scheme))
                .thenReturn(createTargetPeriods(LocalDate.of(2025, 12, 31)));

        // Invoke
        boolean result = validator.isAvailableForScheme(scheme, submissionDate);

        // Verify
        assertThat(result).isTrue();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsBySchemeVersion(scheme);
    }

    @Test
    void isAvailableForScheme_before_start_not_valid() {
        final SchemeVersion scheme = SchemeVersion.CCA_3;
        final LocalDate submissionDate = LocalDate.of(2021, 1, 1);

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersion(scheme))
                .thenReturn(createTargetPeriods(null));

        // Invoke
        boolean result = validator.isAvailableForScheme(scheme, submissionDate);

        // Verify
        assertThat(result).isFalse();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsBySchemeVersion(scheme);
    }

    @Test
    void isAvailableForScheme_after_end_not_valid() {
        final SchemeVersion scheme = SchemeVersion.CCA_3;
        final LocalDate submissionDate = LocalDate.of(2026, 1, 1);

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersion(scheme))
                .thenReturn(createTargetPeriods(LocalDate.of(2025, 12, 31)));

        // Invoke
        boolean result = validator.isAvailableForScheme(scheme, submissionDate);

        // Verify
        assertThat(result).isFalse();
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsBySchemeVersion(scheme);
    }

    private List<TargetPeriodDetailsDTO> createTargetPeriods(LocalDate endDate) {
        return List.of(
                TargetPeriodDetailsDTO.builder()
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2021))
                                                .reportingStartDate(LocalDate.of(2022, 1, 1))
                                                .reportingEndDate(LocalDate.of(2022, 12, 31))
                                                .build(),
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2022))
                                                .reportingStartDate(LocalDate.of(2023, 1, 1))
                                                .reportingEndDate(endDate)
                                                .build()
                                ))
                                .build())
                        .build(),
                TargetPeriodDetailsDTO.builder()
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2023))
                                                .reportingStartDate(LocalDate.of(2024, 1, 1))
                                                .reportingEndDate(LocalDate.of(2024, 12, 31))
                                                .build(),
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2024))
                                                .reportingStartDate(LocalDate.of(2025, 1, 1))
                                                .reportingEndDate(endDate)
                                                .build()
                                ))
                                .build())
                        .build()
        );
    }
}
