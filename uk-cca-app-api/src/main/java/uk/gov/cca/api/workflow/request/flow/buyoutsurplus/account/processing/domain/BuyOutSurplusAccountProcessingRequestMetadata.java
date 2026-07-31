package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingBaseRequestMetadata;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusAccountProcessingRequestMetadata extends BuyOutSurplusAccountProcessingBaseRequestMetadata {
    
    private TargetPeriodType targetPeriodType;
    private Long performanceDataId;
    private Integer performanceDataReportVersion;
    private TargetPeriodResultType tpOutcome;
    private String transactionCode;
}
