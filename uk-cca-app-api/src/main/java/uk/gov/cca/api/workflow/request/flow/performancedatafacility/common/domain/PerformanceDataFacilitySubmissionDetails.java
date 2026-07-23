package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilitySubmissionDetails {

    @NotNull
    private TargetPeriodType targetPeriodType;

    @NotNull
    private PerformanceDataReportType reportType;

    @NotNull
    private Year targetPeriodYear;

    private PerformanceDataSubmissionType submissionType;

    @NotNull
    @Positive
    private Integer reportVersion;

    @NotNull
    private LocalDateTime creationDate;

    @NotNull
    private LocalDateTime submissionDate;
}
