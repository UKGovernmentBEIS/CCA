package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceBuyOutSurplusDetailsDTO {
    private Long performanceDataId;
    private int reportVersion;
    private PerformanceDataSubmissionType submissionType;
    private MeasurementType energyCarbonUnit;
    private TargetPeriodResultType tpOutcome;
    private BigDecimal bankedSurplus;
    private BigDecimal surplusGained;
    private BigDecimal priBuyOutCarbon;
    private BigDecimal priBuyOutCost;
    private BigDecimal totalPriBuyOutCarbon;
}
