package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportTypeDTO;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceDataService {

    private final TargetPeriodService targetPeriodService;

    public List<PerformanceDataReportTypeDTO> getAvailableTargetPeriodsForPerformanceDataReporting(SchemeVersion schemeVersion) {
        LocalDate currentDate = LocalDate.now();

        return targetPeriodService.getTargetPeriodDetailsBySchemeVersion(schemeVersion).stream()
                .filter(tp ->
                        !currentDate.isBefore(tp.getTargetPeriodYearsContainer().getTargetPeriodReportingStartDate()) &&
                                (tp.getTargetPeriodYearsContainer().getTargetPeriodReportingEndDate().isEmpty()
                                        || !currentDate.isAfter(tp.getTargetPeriodYearsContainer().getTargetPeriodReportingEndDate().get()))
                ).map(tp ->
                        PerformanceDataReportTypeDTO.builder()
                                .targetPeriodType(tp.getBusinessId())
                                .reportType(PerformanceDataFacilityUtil.getReportTypeBySubmissionDate(tp, currentDate).orElse(null))
                                .build()
                ).toList();
    }
}
