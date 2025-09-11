package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PerformanceDataDetailsInfoDTO {
    private String accountBusinessId;
    private String operatorName;
    private TargetPeriodType targetPeriodType;
    private TargetPeriodResultType targetPeriodResultType;
    private int reportVersion;
    private PerformanceDataSubmissionType submissionType;
    private BigDecimal priBuyOutCarbon;
}
