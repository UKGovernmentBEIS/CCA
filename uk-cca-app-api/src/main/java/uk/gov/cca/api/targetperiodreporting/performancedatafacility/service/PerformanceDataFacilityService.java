package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.PerformanceDataReportTypeDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityService {

    private final TargetPeriodService targetPeriodService;

    public List<PerformanceDataReportTypeDTO> getAvailableTargetPeriodsForPerformanceDataReporting(
            SchemeVersion schemeVersion) {

        LocalDate currentDate = LocalDate.now();

        return getTargetPeriods(schemeVersion)
                .filter(tp -> isTargetPeriodOpenForReporting(tp, currentDate))
                .map(tp -> PerformanceDataReportTypeDTO.builder()
                        .targetPeriodType(tp.getBusinessId())
                        .reportType(
                                PerformanceDataFacilityUtil
                                        .getReportTypeBySubmissionDate(tp, currentDate)
                                        .orElse(null))
                        .build())
                .toList();
    }

    public Set<Year> getAvailableTargetPeriodYears(SchemeVersion schemeVersion) {

        LocalDate currentDate = LocalDate.now();

        return getTargetPeriods(schemeVersion)
                .flatMap(tp -> tp.getTargetPeriodYearsContainer()
                        .getTargetPeriodYears()
                        .stream())
                .filter(year ->
                        !currentDate.isBefore(year.getReportingStartDate())
                        && (year.getReportingEndDate() == null
                            || !currentDate.isAfter(year.getReportingEndDate())))
                .map(TargetPeriodYear::getTargetYear)
                .collect(Collectors.toSet());
    }
    
    private boolean isTargetPeriodOpenForReporting(TargetPeriodDetailsDTO targetPeriod, LocalDate currentDate) {

    	return !currentDate.isBefore(
                targetPeriod.getTargetPeriodYearsContainer()
                        .getTargetPeriodReportingStartDate())
                && targetPeriod.getTargetPeriodYearsContainer()
                        .getTargetPeriodReportingEndDate()
                        .map(endDate -> !currentDate.isAfter(endDate))
                        .orElse(true);
    }
    
    private Stream<TargetPeriodDetailsDTO> getTargetPeriods(SchemeVersion schemeVersion) {
        return targetPeriodService.getTargetPeriodDetailsBySchemeVersion(schemeVersion)
                .stream();
    }
}
