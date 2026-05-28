package uk.gov.cca.api.workflow.request.flow.common.validation.performancedata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceDataCreateSchemeValidator {

    private final TargetPeriodService targetPeriodService;

    public boolean isAvailableForScheme(SchemeVersion scheme, LocalDate submissionDate) {
        List<TargetPeriodDetailsDTO> targetPeriods = targetPeriodService.getTargetPeriodDetailsBySchemeVersion(scheme);

        // Find the min start reporting date for TPs
        Optional<LocalDate> minStartReportingDate = targetPeriods.stream()
                .map(tp -> tp.getTargetPeriodYearsContainer().getTargetPeriodReportingStartDate())
                .min(LocalDate :: compareTo);

        // Find the max end reporting date for TPs if exist
        List<Optional<LocalDate>> maxEndReportingDates = targetPeriods.stream()
                .map(tp -> tp.getTargetPeriodYearsContainer().getTargetPeriodReportingEndDate()).toList();
        Optional<LocalDate> maxEndReportingDate = maxEndReportingDates.stream().anyMatch(Optional::isEmpty)
                ? Optional.empty()
                : maxEndReportingDates.stream().map(Optional::get).max(LocalDate :: compareTo);

        return minStartReportingDate.isPresent() && !submissionDate.isBefore(minStartReportingDate.get())
                && (maxEndReportingDate.isEmpty() || !submissionDate.isAfter(maxEndReportingDate.get()));
    }
}
