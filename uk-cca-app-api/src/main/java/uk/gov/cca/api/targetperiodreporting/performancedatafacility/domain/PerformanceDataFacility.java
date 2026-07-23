package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PerformanceDataFacility {

    @Valid
    @NotNull
    private PerformanceDataFacilityContainer data;

    @NotNull
    private TargetPeriodType targetPeriodType;

    @NotNull
    private Year targetPeriodYear;

    @NotNull
    private Long facilityId;

    private PerformanceDataSubmissionType submissionType;
}
