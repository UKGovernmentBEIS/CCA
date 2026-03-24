package uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetPeriodDetailsDTO {
    private Long id;
    private TargetPeriodType businessId;
    private String name;
    private TargetPeriodYearsContainer targetPeriodYearsContainer;
    private LocalDate buyOutStartDate;
    private LocalDate buyOutPrimaryPaymentDeadline;
    private LocalDate secondaryReportingStartDate;
}
