package uk.gov.cca.api.targetperiodreporting.targetperiod.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetPeriodYear {

    @NotNull
    private Year targetYear;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private LocalDate reportingStartDate;

    private LocalDate reportingEndDate;
}
