package uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetPeriodInfoDTO {
    private Long id;
    private TargetPeriodType businessId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate buyOutStartDate;
    private LocalDate buyOutPrimaryPaymentDeadline;
    private BigDecimal buyOutCost;
    private LocalDate secondaryReportingStartDate;
    private SchemeVersion schemeVersion;
}
